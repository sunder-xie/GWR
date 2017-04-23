/**
 * 
 */
package com.gewara.web.action.gewapay;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.Config;
import com.gewara.constant.ChargeConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OrderProcessConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.helper.order.GewaOrderHelper;
import com.gewara.model.pay.Charge;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.sport.OpenTimeSale;
import com.gewara.model.sport.SellDeposit;
import com.gewara.model.sport.Sport;
import com.gewara.model.sport.SportItem;
import com.gewara.model.user.Member;
import com.gewara.pay.NewPayUtil;
import com.gewara.pay.PayUtil;
import com.gewara.service.MessageService;
import com.gewara.service.pay.GatewayService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.untrans.ticket.OrderProcessService;
import com.gewara.util.CAUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.HttpResult;
import com.gewara.util.HttpUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;

@Controller
public class NewPayController extends BasePayController{
	@Autowired@Qualifier("orderProcessService")
	private OrderProcessService orderProcessService;
	public void setProcessOrderService(OrderProcessService orderProcessService) {
		this.orderProcessService = orderProcessService;
	}
	@Autowired@Qualifier("messageService")
	private MessageService messageService;
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}
	
	@Autowired@Qualifier("gatewayService")
	private GatewayService gatewayService;
	
	@RequestMapping("/newpay/return.xhtml")
	public String result(String returnParams, String sign,HttpServletRequest request, ModelMap model) throws Exception{
		if(StringUtils.isBlank(returnParams)) return showJsonError(model, "��������");
		String params = WebUtils.getParamStr(request, true);
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "return:" + params);
		String ret = new String(Base64.decodeBase64(returnParams.getBytes()), "utf-8");
		if(CAUtil.doCheck(ret, sign, NewPayUtil.getGewapubkey(), "SHA1WithRSA")) {
			Map<String, String> returnMap = VmUtils.readJsonToMap(ret);
			String merOrderNo = returnMap.get("merOrderno");
			String tradeNo = merOrderNo;
			String paymethod = returnMap.get("paymethod");
			String payseqno = returnMap.get("payseqno");
			
			String gatewayCode = returnMap.get("gatewayCode"); 
			String merchantCode = returnMap.get("merchantCode"); 
			
			orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_RETURN, paymethod, ret + ",host=" + Config.getServerIp());
			
			String qryRes = qryOrderStatus(merOrderNo);
			if(!StringUtils.containsIgnoreCase(qryRes, "paid")) {
				dbLogger.warn("��������ʧ�ܣ�" + qryRes);
				return forwardMessage(model, "��������ʧ�ܣ�");
			}
			String paybank = returnMap.get("paybank");
			Integer paidAmount = Integer.valueOf(returnMap.get("paidAmount"));
			Timestamp paidtime = new Timestamp(DateUtil.parseDate(returnMap.get("paidtime"), "yyyyMMddHHmmss").getTime()); 
			model.put("tradeNo", merOrderNo);
			String paytxt = PaymethodConstant.getPayTextMap().get(paymethod);
			int fee = paidAmount/100;
			model.put("fee", fee);
			if(PayUtil.isChargeTrade(tradeNo)) {
				Charge charge = null;
				ErrorCode<Charge> code = null;
				try{
					code = paymentService.bankPayCharge(tradeNo, true, payseqno, fee, paymethod, paybank,gatewayCode,merchantCode);
					charge = code.getRetval();
					if(code.isSuccess()){
						processCharge(tradeNo, paytxt);
					}
				}catch(Exception e){
					dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "��ֵ����ʧ�ܣ����촦�������ţ�" +  tradeNo, e);
					monitorService.saveSysWarn("��ֵ�������ʧ�ܣ����촦��", "�����ţ�" +  tradeNo, RoleTag.dingpiao);
				}
				if(charge==null) {
					charge = daoService.getObjectByUkey(Charge.class, "tradeNo", tradeNo, false);
				}
				if(ChargeConstant.isBankPay(charge.getChargetype())){
					GewaOrder order = daoService.getObject(GewaOrder.class, charge.getOutorderid());
					if(code !=null && code.isSuccess()){
						try{
							orderProcessService.gewaPayOrderAtServer(order.getId(), order.getMemberid(), null, true);
						}catch(Exception e){
							dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "�����������ʧ�ܣ����촦�������ţ�" +  tradeNo, e);
							monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  tradeNo, RoleTag.dingpiao);
						}
					}
					model.put("tradeNo", order.getTradeNo());
					return "redirect:/gewapay/orderResult.xhtml";
				}
				return "gewapay/new_chargeReturn.vm";
			}else {
				GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", merOrderNo, false);
				if(order==null) return forwardMessage(model, "���������ڣ�");
				if(StringUtils.isBlank(paybank)) paybank = order.getPaybank();
				try{
					ErrorCode<GewaOrder> result = paymentService.netPayOrder(merOrderNo, payseqno, fee, paymethod, paybank, paytxt, paidtime,gatewayCode,merchantCode);
					if(result.isSuccess()) processPay(merOrderNo, paytxt);
				}catch(HibernateOptimisticLockingFailureException e){
					//��̨�����
					dbLogger.warn(StringUtil.getExceptionTrace(e, 5));
				}
				
				if(order.sureOutPartner()) {
					return outOrderResult(order);
				}
				if(PaymethodConstant.MOBILE_PAYMETHOD_LIST.contains(order.getPaymethod())){
					return wapOrderResult(order, model);
				}
				return "redirect:/gewapay/orderResult.xhtml";
			}
		}else {
			return forwardMessage(model, "У�����");
		}
	}
	
	private void sendPaySusMSG(String tradeNo,int fee){
		Charge charge = daoService.getObjectByUkey(Charge.class, "tradeNo", tradeNo, false);
		Member member = daoService.getObject(Member.class, charge.getMemberid());
		if(charge!=null&&member.isBindMobile()){
			ErrorCode<Double> code = paymentService.checkAddpoint(charge);
			String sendMsg = "";
			int point = 0;
			if(code.isSuccess()){
				point = Long.valueOf(Math.round(fee*code.getRetval())).intValue();
			}
			if(charge.hasChargeto(ChargeConstant.WABIPAY)){
				if(point>0){
					sendMsg = "���ѳɹ���ֵ"+ fee +"�߱ң�1�߱�=1Ԫ�������"+point+"���֣��߱ҿ��������ڸ�����ƽ̨����ϵͳ��������������ϵ�ͷ���4000-406-506";
				}else{
					sendMsg = "���ѳɹ���ֵ"+ fee +"�߱ң�1�߱�=1Ԫ�������������ڸ�����ƽ̨���ѣ�������������ϵ�ͷ���4000-406-506";
				}
			}else if(charge.hasChargeto(ChargeConstant.BANKPAY)){
				sendMsg = "���ѳɹ���ֵ"+ fee +"Ԫ���˻����ת�߱ң�1�߱�=1Ԫ��������������ϵ�ͷ���4000-406-506";
			}else if(charge.hasChargeto(ChargeConstant.DEPOSITPAY)){
				SellDeposit sellGuarantee = daoService.getObjectByUkey(SellDeposit.class, "chargeid", charge.getId());
				OpenTimeSale ots = daoService.getObject(OpenTimeSale.class, sellGuarantee.getOtsid());
				Sport sport = daoService.getObject(Sport.class, ots.getSportid());
				SportItem item = daoService.getObject(SportItem.class, ots.getItemid());
				sendMsg = "���ѳɹ�֧��"+ sport.getRealBriefname() + item.getName() + DateUtil.format(ots.getPlaydate(), "M��d��") + "("+DateUtil.getCnWeek(ots.getPlaydate())+")"
					+ ots.getStarttime() +"-"+ ots.getEndtime() + "���ı�֤��"+fee+"Ԫ�����������Լ����Դ˳��μ�������ֱ���ó��ξ��۽�����������ʤ����������Ҫ֧��ʣ�����ſɻ�ó��Σ�ף���ɹ����ۡ�";
			}
			if(StringUtils.isNotBlank(sendMsg)){
				messageService.addManualMsg(member.getId(), member.getMobile(), sendMsg, null);
			}
		}
	}
	
	@RequestMapping("/newpay/notify.xhtml")
	@ResponseBody
	public String cmbPayNotify(String returnParams, String sign, HttpServletRequest request){
		String params = WebUtils.getParamStr(request, true);
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "notify:" + params);
		try {
			String ret = new String(Base64.decodeBase64(returnParams.getBytes()), "utf-8");
			if(CAUtil.doCheck(ret, sign, NewPayUtil.getGewapubkey(), "SHA1WithRSA")) {
				Map<String, String> returnMap = VmUtils.readJsonToMap(ret);
				String merOrderNo = returnMap.get("merOrderno");
				String tradeNo = merOrderNo;
				String paymethod = returnMap.get("paymethod");
				String payseqno = returnMap.get("payseqno");
				String gatewayCode = returnMap.get("gatewayCode"); 
				String merchantCode = returnMap.get("merchantCode");  
				orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_NOTIFY, paymethod, ret + ",host=" + Config.getServerIp());
				String qryRes = qryOrderStatus(merOrderNo);
				if(!StringUtils.containsIgnoreCase(qryRes, "paid")) {
					dbLogger.warn("��������ʧ�ܣ�" + qryRes);
					return "qry order status:" + qryRes;
				}
				String paybank = returnMap.get("paybank");
				String paytxt = PaymethodConstant.getPayTextMap().get(paymethod);
				Integer paidAmount = Integer.valueOf(returnMap.get("paidAmount"));
				int fee = paidAmount/100;
				if(PayUtil.isChargeTrade(tradeNo)) {
					ErrorCode<Charge> code = paymentService.bankPayCharge(tradeNo, true, payseqno, fee, paymethod, paybank,gatewayCode,merchantCode);
					if(code.isSuccess()) {
						processCharge(tradeNo, paytxt);
						Charge charge = code.getRetval();
						if(ChargeConstant.isBankPay(charge.getChargetype())){
							GewaOrder order = daoService.getObject(GewaOrder.class, charge.getOutorderid());
							try{
								orderProcessService.gewaPayOrderAtServer(order.getId(), order.getMemberid(), null, true);
							}catch(Exception e){
								dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "�����������ʧ�ܣ����촦�������ţ�" +  tradeNo, e);
								monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  tradeNo, RoleTag.dingpiao);
							}
						}else{
							if(code.isSuccess()){
								//��Ӷ��ų�ֵ�ɹ�����
								sendPaySusMSG(tradeNo,fee);
							}
						}
					}
				}else {
					GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", merOrderNo, false);
					if(order==null) return "order not exists";
					Timestamp paidtime = new Timestamp(DateUtil.parseDate(returnMap.get("paidtime"), "yyyyMMddHHmmss").getTime()); 
					if(StringUtils.isBlank(paybank)) paybank = order.getPaybank();
					if(!order.isNetPaid()){
						try{
							ErrorCode<GewaOrder> result = paymentService.netPayOrder(merOrderNo, payseqno, paidAmount/100, paymethod, paybank, paytxt, paidtime,gatewayCode,merchantCode);
							if(result.isSuccess()) processPay(merOrderNo, paytxt);
						}catch(HibernateOptimisticLockingFailureException e){
							dbLogger.warn(StringUtil.getExceptionTrace(e, 5));
						}
					}else{
						dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "�ظ�����"+paytxt+"��������:" + payseqno);
					}
				}
			}else {
				return "check error";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "success";
	}
	@RequestMapping("/newpay/qryOrderStatus.xhtml")
	@ResponseBody
	public String qryOrderStatus(String tradeno){
		Map<String, String> paramMap = new LinkedHashMap<String, String>();
		paramMap.put("merid", NewPayUtil.getMerid());
		paramMap.put("orderno", tradeno);
		
		String paymethod = null;
		if(PayUtil.isChargeTrade(tradeno)){
			Charge charge = daoService.getObjectByUkey(Charge.class, "tradeNo", tradeno, false);
			if(charge == null) return "not exist";
			paymethod = charge.getPaymethod();
		}else{
			GewaOrder gewaOrder = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeno, false);
			if(gewaOrder == null) return "not exist";
			paymethod = gewaOrder.getPaymethod();
		}
		
		String qryurl = NewPayUtil.getQryurl();
		if(gatewayService.isSwitch(paymethod)){
			qryurl = NewPayUtil.getNewQryurl();
		}
		
		HttpResult code = HttpUtils.postUrlAsString(qryurl, paramMap);
		if(code.isSuccess()) return code.getResponse();
		return code.getMsg();
	}
	
	private String outOrderResult(GewaOrder order){
		if(order.getPartnerid().equals(PartnerConstant.PARTNER_UNION)){ //����
			return "redirect:/partner/chinapay/orderResult.xhtml";
		}
		return "redirect:/partner/orderResult.xhtml";
	}
	
	private String wapOrderResult(GewaOrder order, ModelMap model) {
		boolean success = false;
		if(order.isAllPaid()) success = true;
		Map submitMap = new HashMap();
		submitMap.put("from", "app");
		submitMap.put("ordertitle", order.getOrdertitle());
		submitMap.put("memberid", order.getMemberid());
		submitMap.put("partnerid", order.getPartnerid());
		submitMap.put("tradeNo", order.getTradeNo());
		submitMap.put("ordertype", order.getOrdertype());
		submitMap.put("ukey", GewaOrderHelper.getPartnerUkey(order));
		submitMap.put("description", order.getDescription2());
		submitMap.put("success", success+"");
		model.put("submitParams", submitMap);
		//��ת��wap��Ŀ����wap��Ŀ���ٸ��������ת�������̡�WAP��android�ȵ�
		String payResultUrl = config.getPageMap().get("absWap") + "/payResult.xhtml";
		model.put("method", "post");
		model.put("submitUrl", payResultUrl);
		return "tempSubmitForm.vm";
	}
}
