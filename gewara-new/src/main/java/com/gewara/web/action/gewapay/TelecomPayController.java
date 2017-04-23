package com.gewara.web.action.gewapay;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.Config;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OrderProcessConstant;
import com.gewara.model.pay.Charge;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.pay.PayUtil;
import com.gewara.pay.TelecomPayUtil;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.util.StringUtil;
import com.gewara.util.WebUtils;

@Controller
public class TelecomPayController extends BasePayController{
	
	@RequestMapping("/pay/telecomReturn.xhtml")
	public String telecomReturn(String objid, String money, String transid,Long username,String msg,String sign,ModelMap model, HttpServletRequest request){
		String params = WebUtils.getParamStr(request, true);
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, params);
		String signStr = StringUtil.md5("objid="+TelecomPayUtil.OBJID+"&money="+(money == null ? "":money)+"&transid="+transid+"&username="+username+"&msg="+msg+"&key="+TelecomPayUtil.KEY);
		if(StringUtils.equals(sign, signStr)){
			String tradeNo = transid.replaceFirst(TelecomPayUtil.OBJID, "");
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + tradeNo);
			orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_RETURN, PaymethodConstant.PAYMETHOD_TELECOM, params + ",host=" + Config.getServerIp());
			//����У��
			if(StringUtils.equals("2001", msg) && StringUtils.equals(TelecomPayUtil.OBJID, objid) ){
				model.put("tradeNo", tradeNo);
				if(PayUtil.isChargeTrade(tradeNo)) {//��ֵ
					Charge charge = daoService.getObjectByUkey(Charge.class, "tradeNo", tradeNo, false);
					int fee = charge.getFee();
					if(!TelecomPayUtil.getQueryOrderResult(fee + "",transid,TelecomPayUtil.OBJID,TelecomPayUtil.KEY,TelecomPayUtil.QUERYURL).startsWith("8001")){
						return showError(model, "������֤ʧ�ܣ�");
					}
					model.put("fee", fee);
					return "gewapay/new_chargeReturn.vm";
				}
			}
		}
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "telecomǩ��ʧ�ܣ�" + params);
		return showError(model, "������֤ʧ�ܣ�");
	}
	//�첽���ã��ı����ݿ�״̬
	@RequestMapping("/pay/telecomNotify.xhtml")
	@ResponseBody
	public String telecomReNotify(HttpServletRequest request, String payphone, String transid, 
			String objid, String money, String sign, String msg, ModelMap model) {
		String params = WebUtils.getParamStr(request, true);
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, params);
		String signStr = TelecomPayUtil.sign(request.getParameterMap());
		if(StringUtils.equals(sign, signStr)){
			//8001:02165755917
			if(!StringUtils.equals("8001:" + payphone,TelecomPayUtil.getQueryOrderResult(money, transid,TelecomPayUtil.OBJID,TelecomPayUtil.KEY,TelecomPayUtil.QUERYURL))){
				return "Err";
			}
			String tradeNo = StringUtils.substring(transid, TelecomPayUtil.OBJID.length());
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + tradeNo);
			orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_NOTIFY, PaymethodConstant.PAYMETHOD_TELECOM, params + ",host=" + Config.getServerIp());
			//����У��
			if(StringUtils.equals("8001", msg) && StringUtils.equals(TelecomPayUtil.OBJID, objid) ){
				int fee = new Float(money).intValue();
				model.put("tradeNo", tradeNo);
				model.put("fee", ""+fee);
				if(PayUtil.isChargeTrade(tradeNo)) {//��ֵ
					try{
						ErrorCode<Charge> result = paymentService.bankPayCharge(tradeNo, false, "", fee, PaymethodConstant.PAYMETHOD_TELECOM, "bk", PaymethodConstant.PAYMETHOD_TELECOM,"tel");

						if(result.isSuccess()){
							processCharge(tradeNo, "telecom");
						}
					}catch(Exception e){
						dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "�����������ʧ�ܣ����촦�������ţ�" +  tradeNo, e);
						monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  tradeNo, RoleTag.dingpiao);
						return "Err";
					}
					return "OK";
				}
			}
		}
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "telecomǩ��ʧ�ܣ�" + params);
		return "Err";
	}
	
	
	//�첽���ã��ı����ݿ�״̬   �µ�֧��
	@RequestMapping("/pay/payTelecomNotify.xhtml")
	@ResponseBody
	public String telecomPayNotify(HttpServletRequest request, String payphone, String transid, 
			String objid, String money, String sign, String msg) {
		String params = WebUtils.getParamStr(request, true);
		String remoteIp = WebUtils.getRemoteIp(request);
		String headers = WebUtils.getHeaderStr(request);
		dbLogger.warn("����֧����������params:" + params);
		dbLogger.warn("����֧����������Header:" + headers + ", IP:" + remoteIp);
		String signStr = TelecomPayUtil.paySign(request.getParameterMap());
		if(StringUtils.equals(sign, signStr)){
			//8001:02165755917
			if(!StringUtils.equals("8001:" + payphone,TelecomPayUtil.getQueryOrderResult(money, transid,TelecomPayUtil.PAY_OBJID,TelecomPayUtil.PAY_KEY,TelecomPayUtil.PAY_QUERYURL))){
				return "Err";
			}
			String tradeNo = StringUtils.substring(transid, TelecomPayUtil.PAY_OBJID.length());
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + tradeNo);
			orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_NOTIFY, PaymethodConstant.PAYMETHOD_TELECOM, params + ",host=" + Config.getServerIp());
			//����У��
			if(StringUtils.equals("8001", msg) && StringUtils.equals(TelecomPayUtil.PAY_OBJID, objid) ){
				int fee = new Float(money).intValue();
				try{
					ErrorCode<GewaOrder> result = paymentService.netPayOrder(tradeNo, transid, fee, PaymethodConstant.PAYMETHOD_TELECOM, "bk", "���Ż���֧��");
					if(result.isSuccess()) {
						processPay(tradeNo, "���Ż���֧��");
					}
				}catch(Exception e){
					dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "", e);
					monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  tradeNo, RoleTag.dingpiao);
					return "Err";
				}
				return "OK";
			}
		}
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "telecomǩ��ʧ�ܣ�" + params);
		return "Err";
	}

	@RequestMapping("/pay/payTelecomReturn.xhtml")
	public String telecomPayReturn(String objid, String money, String transid,long username,String msg,String sign,ModelMap model, HttpServletRequest request){
		String params = WebUtils.getParamStr(request, true);
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, params);
		String signStr = StringUtil.md5("objid="+TelecomPayUtil.PAY_OBJID+"&money="+(money == null ? "":money)+"&transid="+transid+"&username="+username+"&msg="+msg+"&key="+TelecomPayUtil.PAY_KEY);
		if(StringUtils.equals(sign, signStr)){
			String tradeNo = transid.replaceFirst(TelecomPayUtil.PAY_OBJID, "");
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + tradeNo);
			orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_RETURN, PaymethodConstant.PAYMETHOD_TELECOM, params + ",host=" + Config.getServerIp());
			//����У��
			if(StringUtils.equals("2001", msg) && StringUtils.equals(TelecomPayUtil.PAY_OBJID, objid) ){
				model.put("tradeNo", tradeNo);
				TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeNo, false);
				if(order!=null) {//��ֵ
					int fee = order.getDue();
					if(!TelecomPayUtil.getQueryOrderResult(fee + "",transid,TelecomPayUtil.PAY_OBJID,TelecomPayUtil.PAY_KEY,TelecomPayUtil.PAY_QUERYURL).startsWith("8001")){
						return showError(model, "������֤ʧ�ܣ�");
					}
					model.put("fee", fee);
					return showRedirect(config.getAbsPath() + "/gewapay/orderResult.xhtml", model);
				}else{
					return showError(model, "���������ڣ�");
				}
			}
		}
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "telecomǩ��ʧ�ܣ�" + params);
		return showError(model, "������֤ʧ�ܣ�");
	}
	
	@RequestMapping("/pay/payTelecomMobileReturn.xhtml")
	public String payTelecomMobileReturn(String TransactionID,String Result,String UserAccount,String ErrorDescription,String Sign,ModelMap model, HttpServletRequest request){
		String params = WebUtils.getParamStr(request, true);
		String remoteIp = WebUtils.getRemoteIp(request);
		String headers = WebUtils.getHeaderStr(request);
		dbLogger.warn("���������ֻ�֧����������params:" + params);
		dbLogger.warn("���������ֻ�֧����������Header:" + headers + ", IP:" + remoteIp);
		if(!"0000".equals(Result)){
			return showError(model, ErrorDescription);
		}
		String sign = StringUtil.md5("WebId=" + TelecomPayUtil.WEB_ID + "&TransactionID=" + TransactionID + "&Result=" + Result + "&UserAccount=" + UserAccount + "&key=" + TelecomPayUtil.PAY_MOBILE_KEY);
		if(sign.equals(Sign)){
			String tradeNo = TransactionID.substring(TelecomPayUtil.WEB_ID.length());
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + tradeNo);
			orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_RETURN, PaymethodConstant.PAYMETHOD_MOBILE_TELECOM, params + ",host=" + Config.getServerIp());
			model.put("tradeNo", tradeNo);
			TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeNo, false);
			if(order == null){
				return showError(model, "���������ڣ�");
			}
			if(TelecomPayUtil.getTelecomMobilePayQueryOrderResult(order.getDue() + "", TransactionID).startsWith("0000:")){
				try{
					ErrorCode<GewaOrder> result = paymentService.netPayOrder(tradeNo, TransactionID, order.getDue(), PaymethodConstant.PAYMETHOD_MOBILE_TELECOM, "bk", "���Ż���֧��");
					if(result.isSuccess()) {
						processPay(tradeNo, "���Ż���֧��");
					}
					return showRedirect(config.getAbsPath() + "/gewapay/orderResult.xhtml", model);
				}catch(Exception e){
					dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "", e);
					monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  tradeNo, RoleTag.dingpiao);
					return showError(model, "���������ڣ�");
				}
			}else{
				return showError(model, "�Ƿ�֧����Դ��");
			}
		}
		return showError(model, "ǩ������");
	}
}
