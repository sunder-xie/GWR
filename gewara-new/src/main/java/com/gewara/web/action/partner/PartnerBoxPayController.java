package com.gewara.web.action.partner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.Config;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.sys.CacheConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OrderProcessConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.jms.JmsConstant;
import com.gewara.model.api.ApiUser;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.user.Member;
import com.gewara.model.user.OpenMember;
import com.gewara.pay.PartnerPayUtil;
import com.gewara.pay.PayBoxPayUtil;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CacheService;
import com.gewara.untrans.JmsService;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.util.ApiUtils;
import com.gewara.util.DateUtil;
import com.gewara.util.StringUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.api.ApiAuth;
import com.gewara.web.action.api.BaseApiController;
import com.gewara.xmlbind.partner.IBoxPay;
import com.gewara.xmlbind.partner.IBoxPayResult;
import com.gewara.xmlbind.partner.PartnerBoxPayUser;

@Controller
public class PartnerBoxPayController extends BaseApiController{
	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;
	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@Autowired@Qualifier("jmsService")
	private JmsService jmsService;
	public void setJmsService(JmsService jmsService) {
		this.jmsService = jmsService;
	}
	@Autowired@Qualifier("cacheService")
	private CacheService cacheService;
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	private ApiUser getBoxPay(){
		return daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_BOX_PAY);
	}
	
	@RequestMapping("/api/partner/boxpay/toCheckLogin.xhtml")
	public String boxLoginBind(String key,String encryptCode,String token,String iboxUserId,ModelMap model,HttpServletRequest request,HttpServletResponse response){
		ApiAuth apiAuth = this.check(key, encryptCode, request);
		if(!apiAuth.isChecked()){
			return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		}
		ApiUser partner = this.getBoxPay();
		String checkUserResult = PayBoxPayUtil.getCheckBoxLogin(partner,token,iboxUserId);
		if("fail".equals(checkUserResult)){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û���¼У��δͨ��");
		}
		BeanReader beanReader = ApiUtils.getBeanReader("iboxpay",IBoxPay.class);
		IBoxPay boxpay = (IBoxPay)ApiUtils.xml2Object(beanReader, checkUserResult);
		IBoxPayResult result = boxpay.getResult();
		if(result == null || !"0".equals(boxpay.getResult().getRespCode())){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û���¼У��δͨ��");
		}
		PartnerBoxPayUser user = result.getResponse();
		if(!this.checkSign(user.getSignMsg(), new String[]{"iboxUserId","parterId","partnerUserId","result","signType","token"}, 
				user.getIboxUserId(),user.getParterId(),user.getPartnerUserId(),user.getResult(),user.getSignType(),user.getToken())){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û���¼У��δͨ��");
		}
		OpenMember openMember = memberService.getOpenMemberByLoginname("boxpay", user.getIboxUserId());
		if(openMember == null){
			openMember = memberService.createOpenMember(WebUtils.getAndSetDefault(request, response), "boxpay","b", user.getIboxUserId(), WebUtils.getRemoteIp(request));
		}
		Member member = daoService.getObject(Member.class, openMember.getMemberid());
		model.put("member", member);
		model.put("boxUserId", iboxUserId);
		model.put("token", token);
		if(StringUtils.isBlank(user.getPartnerUserId())){
			String bindResult = PayBoxPayUtil.bindUser(token, member.getId() + "", user.getIboxUserId(),partner);
			boxpay = (IBoxPay)ApiUtils.xml2Object(beanReader, bindResult);
			result = boxpay.getResult();
			if(result == null || !"0".equals(result.getRespCode())){
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, result.getErrorDesc());
			}
			user = result.getResponse();
			if(!this.checkSign(user.getSignMsg(), new String[]{"iboxUserId","parterId","partnerUserId","result","signType","token"}, 
					user.getIboxUserId(),user.getParterId(),user.getPartnerUserId(),user.getResult(),user.getSignType(),user.getToken())){
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û���box�󶨳���");
			}
		}
		ErrorCode<String> encodeResult = memberService.getAndSetMemberEncode(member);
		model.put("memberEncode", encodeResult.getRetval());
		return this.getXmlView(model, "/api/box/member.vm");
	}
	@RequestMapping("/api/partner/boxpay/addOrderToBoxPay.xhtml")
	public String addOrderToBoxPay(String key,String encryptCode,String orderId,String memberEncode,ModelMap model,HttpServletRequest request){
		ApiAuth apiAuth = this.check(key, encryptCode, request);
		if(!apiAuth.isChecked()){
			return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		}
		if(StringUtils.isBlank(memberEncode)){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "memberEncode ����Ϊ��");
		}
		Member member =  memberService.getMemberByEncode(memberEncode);
		if(member == null){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û�Ϊ��");
		}
		OpenMember openMember = memberService.getOpenMemberByMemberid("boxpay", member.getId());
		if(openMember == null){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�˺����û���δ�ڸ�����ƽ̨��¼");
		}
		TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", orderId, true);
		if(order == null){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "����������");
		}
		if(!order.getMemberid().equals(member.getId())){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,"�����޸����˵Ķ�����");
		}
		if(order.isAllPaid() || order.isCancel()) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�����޸���֧�����ѣ���ʱ��ȡ���Ķ�����");
		}
		String result = PayBoxPayUtil.saveOrder(order, this.getBoxPay(),openMember.getLoginname());
		if("fail".equals(result)){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��Ӷ�����boxPayʧ��");
		}
		BeanReader beanReader = ApiUtils.getBeanReader("iboxpay",IBoxPay.class);
		IBoxPay boxpay = (IBoxPay)ApiUtils.xml2Object(beanReader, result);
		IBoxPayResult payResult = boxpay.getResult();
		if(payResult == null || !"0".equals(payResult.getRespCode())){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��Ӷ�����boxPayʧ��");
		}
		PartnerBoxPayUser boxPayResponse = payResult.getResponse();
		if(!this.checkSign(boxPayResponse.getSignMsg(), new String[]{"bizType","callbackUrl","createTime","cutOffTime","iboxUserId","orderAmount","orderNo","orderSerial","orderTime","parterId","signType"}, 
				boxPayResponse.getBizType(),boxPayResponse.getCallbackUrl(),boxPayResponse.getCreateTime(),boxPayResponse.getCutOffTime(),boxPayResponse.getIboxUserId(),boxPayResponse.getOrderAmount(),
				boxPayResponse.getOrderNo(),boxPayResponse.getOrderSerial(),boxPayResponse.getOrderTime(),boxPayResponse.getParterId(),boxPayResponse.getSignType())){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��Ӷ�����boxPayʧ��");
		}
		model.put("orderSerial", boxPayResponse.getOrderSerial());
		return this.getXmlView(model, "/api/box/result.vm");
	}
	
	/**
	 * @param  payStatus	��Ӧ��	String(10)	Y	0 �ɹ������� ʧ��
	 * @param  errorDesc	��������	String(100)	N	ʧ��ʱ����
		�ɹ�ʱ������
		�ɹ���Ӧ����
	 * @param 	parterId	������ID	String(100)	Y	����ID
	 * @param 	bizType	ҵ������	Number(2)	Y	
	 * @param 	orderNo	������ˮ��	String(50)	Y	�̻�������ˮ��
	 * @param 	orderTime	����ʱ��	String(15)	Y	�̻�����ʱ��:��ʽ
	  	YYYY-MM-DD hh:mm:ss
	 * @param 	orderAmount	�������	Number(15)	Y	�̻��������
		��ȷ����
	 * @param 	tradeNo	������ˮ��	String(20)	Y	����֧����ˮ��
	 * @param 	tradeAmount	���׽��	Number(15)	Y	����֧�����׽��
		��ȷ����
	 * @param 	tradeTime	����ʱ��	String(15)	Y	����ʱ�䣺��ʽ
		YYYY-MM-DD hh:mm:ss
	 * @param 	signType	ǩ������	Number(1)	Y	1,MD5  2 ,RSA
	 * @param signMsg	ǩ��	String(300)	Y	
	 */
	// �ı����ݿ�״̬
	@RequestMapping("/partner/pay/boxPayNotify.xhtml")
	@ResponseBody
	public String boxPayNotify(HttpServletRequest request, String payStatus,String errorDesc,String parterId,String bizType,
			String orderNo,String orderTime,String orderAmount,String tradeNo,String tradeAmount,String tradeTime,String signType,
			String signMsg,String orderSerial,String sysRefNo) {
		String params = WebUtils.getParamStr(request, true);
		String headers = WebUtils.getHeaderStr(request);
		dbLogger.error("�̼Ҷ�������Param:" + params);
		dbLogger.error("�̼Ҷ�������Header:" + headers);
		if(!"0".equals(payStatus)){
			return orderNo + "|" + tradeNo + "|" + errorDesc;
		}
		ApiUser partner = this.getBoxPay();
		if (partner == null) {
			dbLogger.error("�̼Ҳ�����");
			monitorService.saveSysWarn("�̼�API�����̼Ҳ�����,params:",  params + "\nheader:" + headers, RoleTag.jishu);
			return orderNo + "|" + tradeNo + "|partner not exists";
		}
		if(!partner.isRole(ApiUser.ROLE_PAYORDER)){
			dbLogger.error("��֧���̼�֧��");
			monitorService.saveSysWarn("�̼�API���󣺲�֧���̼�֧��,params:",  params + "\nheader:" + headers, RoleTag.jishu);
			return orderNo + "|" + tradeNo + "|pay not supported";
		}
		boolean valid = checkSign(signMsg,new String[]{"bizType","orderAmount","orderNo","orderSerial","orderTime","parterId","payStatus","signType","sysRefNo","tradeAmount","tradeNo","tradeTime"}, 
				bizType,orderAmount,orderNo,orderSerial,orderTime,parterId,payStatus,signType,sysRefNo,tradeAmount,tradeNo,tradeTime);
		if(valid && queryOrderValid(orderNo,orderSerial,partner)) {
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + orderNo);
			orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_NOTIFY, PaymethodConstant.PAYMETHOD_PARTNERPAY, params + ",host=" + Config.getServerIp());
			String remoteIp = WebUtils.getRemoteIp(request);
			if(!PartnerPayUtil.isValidIp(remoteIp, partner)){//�Ƿ�IP���ã�����
				dbLogger.error("�̼Ҹ���Ƿ�IP����");
				monitorService.saveSysWarn("�̼�API�����̼Ҹ���Ƿ�IP����,params:", params + "\nheader:" + headers, RoleTag.jishu);
				dbLogger.error("�̼Ҹ���Ƿ�IP���ã�" + remoteIp);
			}
			 //�����ڲ�ͬ״̬�»�ȡ������Ϣ�������̼����ݿ�ʹ����ͬ�� 
			int fee = new Double(tradeAmount).intValue()/100;
			try{
				ErrorCode<GewaOrder> result = paymentService.netPayOrder(orderNo, tradeNo, fee, PaymethodConstant.PAYMETHOD_PARTNERPAY, "bk", partner.getBriefname());
				if(result.isSuccess()) processPay(orderNo, partner.getBriefname());
				TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", orderNo, false);
				if (order == null) {
					monitorService.saveSysWarn("�̼�API�����̼Ҷ���������,params:", params + "\nheader:" + headers, RoleTag.jishu);
					return orderNo + "|" + tradeNo + "|order not exists";
				}
			}catch(Exception e){
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "", e);
				monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  orderNo, RoleTag.dingpiao);
			}
			return "success";
		}else{
			dbLogger.error("�Ƿ��Ķ�����Ϣ");
			monitorService.saveSysWarn("�̼�API���󣺷Ƿ��Ķ�����Ϣ,params:", params + "\nheader:" + headers, RoleTag.jishu);
			return orderNo + "|" + tradeNo + "|" + valid;
		}
	}
	
	private boolean queryOrderValid(String orderNo,String orderSerial,ApiUser partner){
		String result = PayBoxPayUtil.queryOrder(orderNo, orderSerial, partner);
		if("fail".equals(result)){
			dbLogger.error("����boxpay������ѯδ�ҵ�����");
			return false;
		}
		BeanReader beanReader = ApiUtils.getBeanReader("iboxpay",IBoxPay.class);
		IBoxPay boxpay = (IBoxPay)ApiUtils.xml2Object(beanReader, result);
		IBoxPayResult payResult = boxpay.getResult();
		if(payResult == null || !"0".equals(payResult.getRespCode())){
			dbLogger.error("����boxpay������ѯδ�ҵ�����");
			return false;
		}
		PartnerBoxPayUser boxPayResponse = payResult.getResponse();
		if(!"Y".equals(boxPayResponse.getOrderStatus())){
			dbLogger.error("������boxpay�̻���δ֧��");
			return false;
		}
		if(!this.checkSign(boxPayResponse.getSignMsg(), new String[]{"bizType","callbackUrl","createTime","orderAmount","orderNo","orderSerial","orderStatus","orderTime","parterId","payTime","signType","sysRefNo"}, 
				boxPayResponse.getBizType(),boxPayResponse.getCallbackUrl(),boxPayResponse.getCreateTime(),boxPayResponse.getOrderAmount(),
				boxPayResponse.getOrderNo(),boxPayResponse.getOrderSerial(),boxPayResponse.getOrderStatus(),boxPayResponse.getOrderTime(),
				boxPayResponse.getParterId(),boxPayResponse.getPayTime(),boxPayResponse.getSignType(),boxPayResponse.getSysRefNo())){
			dbLogger.error("boxpay�̻��Ƿ�ǩ��");
			return false;
		}
		return true;
	}
	
	protected void processPay(String tradeNo, String from){
		String key = "processOrder" + tradeNo;
		Long last = (Long) cacheService.get(CacheConstant.REGION_TENMIN, key);
		Long cur = System.currentTimeMillis();
		cacheService.set(CacheConstant.REGION_TENMIN, key, cur);
		if(last != null && last + DateUtil.m_minute * 5 > cur) {//5������ֻ����һ��
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "���Զ���������ã�" + key);
			return;
		}
		jmsService.sendMsgToDst(JmsConstant.QUEUE_PAY, JmsConstant.TAG_ORDER, "tradeNo,from", tradeNo, from);
	}
	
	private boolean checkSign(String signMsg,String[] keys,String ...strs){
		StringBuilder sb = new StringBuilder();
		if(strs != null){
			if(keys.length == strs.length){
				int i = 0;
				for(String key : keys){
					if(StringUtils.isNotBlank(strs[i])){
						sb.append(key).append("=").append(strs[i]).append("&");
					}
					i++;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
		sb.append("key=").append(PayBoxPayUtil.getBoxKey());
		String vilaSign = sb.toString();
		this.dbLogger.warn(vilaSign );
		try {
			if(signMsg.equals(StringUtil.md5(URLEncoder.encode(vilaSign, "UTF-8")).toUpperCase())){
				return true;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
}
