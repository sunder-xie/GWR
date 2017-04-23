package com.gewara.web.action.ajax;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.SmsConstant;
import com.gewara.constant.TagConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.user.Member;
import com.gewara.service.MessageService;
import com.gewara.service.OperationService;
import com.gewara.service.bbs.BlogService;
import com.gewara.untrans.UntransService;
import com.gewara.untrans.impl.ControllerService;
import com.gewara.util.DateUtil;
import com.gewara.util.StringUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;

@Controller
public class OrderMessageController extends AnnotationController{

	@Autowired@Qualifier("messageService")
	private MessageService messageService;
	
	@Autowired@Qualifier("blogService")
	private BlogService blogService;
	
	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	
	@Autowired@Qualifier("controllerService")
	private ControllerService controllerService;
	
	@Autowired@Qualifier("operationService")
	private OperationService operationService;

	/**
	 * @param sessid
	 * @param request
	 * @param orderId
	 * @param mobile
	 * @param msgContent
	 * @param model
	 * @param captchaId
	 * @param captcha
	 * @return
	 */
	@RequestMapping("/ajax/trade/orderResultSendMsg.xhtml")
	public String orderResultSendMsg(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, String mobile, String msgContent, ModelMap model, String captchaId, String captcha){
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		TicketOrder order = daoService.getObject(TicketOrder.class, orderId);
		if(order == null) return showJsonError(model, "�ö��������ڻ�ɾ����");
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�ö��������˵Ķ�����");
		if ( order.isCancel()) return showJsonError(model, "���ܶ��ѣ���ʱ��ȡ���Ķ������Ͷ���������ѣ�");
		if (!StringUtils.equals(order.getStatus(), OrderConstant.STATUS_PAID_SUCCESS)) return showJsonError(model, "�ö������ǳɹ�������");
		String[] moblies = StringUtils.split(mobile,",");
		if(moblies.length > 2) return showJsonError(model, "�������������಻�ܳ���2��");
		String opkey = member.getId()+"_" + order.getCinemaid();
		if(!operationService.isAllowOperation(opkey, OperationService.ONE_DAY)){
			return showJsonError(model, "ÿ�ʶ���ֻ������1�Σ�");
		}
		Timestamp curtime = DateUtil.getCurFullTimestamp();
		String tradeNo =  String.valueOf(order.getCinemaid());
		String smsTradeNo = orderId + "movie" + tradeNo;
		if(order.getPaidtime().before(DateUtil.addDay(curtime, -1))) return showJsonError(model, "����ʱ���ѳ�ʱ��");
		
		int count = messageService.querySmsRecord(smsTradeNo, TagConstant.TAG_MOVIEORDER, null, null, orderId, member.getId());
		if((count + moblies.length) > 2) return showJsonError(model, "ÿ�ʶ�����������2λ����");
		if(StringUtils.isBlank(msgContent)) return showJsonError(model, "���ŵ����ݲ���Ϊ�գ�");
		if(VmUtils.getByteLength(msgContent) > 128) return showJsonError(model, "�������ݳ��Ȳ��ܳ���64���֣����޸ģ�");
		boolean isSendMsg = StringUtils.isNotBlank(blogService.filterAllKey(msgContent));
		for(int i = 0;i < moblies.length;i++){
			if(moblies[i].length() > 1 && ValidateUtil.isMobile(moblies[i])){
				SMSRecord sms = new SMSRecord(moblies[i]);
				if(isSendMsg){
					sms.setStatus(SmsConstant.STATUS_FILTER);
				}
				sms.setTradeNo(smsTradeNo);
				sms.setContent(msgContent);
				sms.setSendtime(curtime);
				sms.setSmstype(SmsConstant.SMSTYPE_MANUAL);
				sms.setValidtime(DateUtil.getLastTimeOfDay(curtime));
				sms.setTag(TagConstant.TAG_MOVIEORDER);
				sms.setMemberid(member.getId());
				sms.setRelatedid(orderId);
				sms = untransService.addMessage(sms);
				if(sms!=null) untransService.addMessage(sms);
			}
		}
		operationService.updateOperation(opkey, OperationService.ONE_DAY);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/ajax/trade/acOrderResultSendMsg.xhtml")
	public String acOrderResultSendMsg(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, String mobile, String msgContent, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GoodsOrder order = daoService.getObject(GoodsOrder.class, orderId);
		if(order == null) return showJsonError(model, "�ö��������ڻ�ɾ����");
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�ö��������˵Ķ�����");
		if ( order.isCancel()) return showJsonError(model, "���ܶ��ѣ���ʱ��ȡ���Ķ������Ͷ���������ѣ�");
		if (!StringUtils.equals(order.getStatus(), OrderConstant.STATUS_PAID_SUCCESS)) return showJsonError(model, "�ö������ǳɹ�������");
		String[] moblies = StringUtils.split(mobile,",");
		if(moblies.length > 5) return showJsonError(model, "�������������಻�ܳ���5��");
		String opkey = member.getId()+"_" + order.getGoodsid();
		if(!operationService.updateOperation(opkey, 10)) return showJsonError(model, "���������Ƶ�������Ժ����ԣ�");
		Timestamp curtime = DateUtil.getCurFullTimestamp();
		String tradeNo =  String.valueOf(order.getGoodsid());
		String smsTradeNo = orderId + "activity" + tradeNo;
		if(order.getPaidtime().before(DateUtil.addDay(curtime, -1))) return showJsonError(model, "����ʱ���ѳ�ʱ��");
		int count = messageService.querySmsRecord(smsTradeNo, TagConstant.TAG_ACTIVITYORDER, null, null, orderId, member.getId());
		if((count + moblies.length) > 5) return showJsonError(model, "ÿ�ʶ�����������5λ����");
		if(StringUtils.isBlank(msgContent)) return showJsonError(model, "���ŵ����ݲ���Ϊ�գ�");
		if(VmUtils.getByteLength(msgContent) > 128) return showJsonError(model, "�������ݳ��Ȳ��ܳ���64���֣����޸ģ�");
		boolean isSendMsg = StringUtils.isNotBlank(blogService.filterAllKey(msgContent));
		for(int i = 0;i < moblies.length;i++){
			if(moblies[i].length() > 1 && ValidateUtil.isMobile(moblies[i])){
				SMSRecord sms = new SMSRecord(moblies[i]);
				if(isSendMsg){
					sms.setStatus(SmsConstant.STATUS_FILTER);
				}
				sms.setTradeNo(smsTradeNo);
				sms.setContent(msgContent);
				sms.setSendtime(curtime);
				sms.setSmstype(SmsConstant.SMSTYPE_MANUAL);
				sms.setValidtime(DateUtil.getLastTimeOfDay(curtime));
				sms.setTag(TagConstant.TAG_ACTIVITYORDER);
				sms.setMemberid(member.getId());
				sms.setRelatedid(orderId);
				sms = untransService.addMessage(sms);
				if(sms!=null) {
					untransService.sendMsgAtServer(sms, true);
				}
			}
		}
		return showJsonSuccess(model);
	}

	
	@RequestMapping("/drama/ajax/dramaSendMsg.xhtml")
	public String dramaSendMsg(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, String mobile, String msgContent,ModelMap model, String captchaId, String captcha){
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		DramaOrder order = daoService.getObject(DramaOrder.class, orderId);
		if(order == null) return showJsonError(model, "�ö��������ڻ�ɾ����");
		if(!member.getId().equals(order.getMemberid())) return show404(model, "�ö��������˵Ķ�����");
		if (order.isCancel()) return showJsonError(model, "���ܶ��ѣ���ʱ��ȡ���Ķ������Ͷ���������ѣ�");
		if(!StringUtils.equals(order.getStatus(), OrderConstant.STATUS_PAID_SUCCESS))  return showJsonError(model, "�ǳɹ�֧���Ķ��������ܷ�����");
		String[] moblies = StringUtils.split(mobile,",");
		if(moblies.length > 2) return showJsonError(model, "�����������ÿ�β��ܳ���2��");
		Timestamp curtime = DateUtil.getCurFullTimestamp();
		if(order.getPaidtime().before(DateUtil.addDay(curtime, -3))) return showJsonError(model, "����ʱ���ѳ�ʱ��");
		String opkey = member.getId()+"_" + order.getDramaid();
		if(!operationService.isAllowOperation(opkey, OperationService.ONE_DAY)){
			return showJsonError(model, "ÿ�ʶ���ֻ������1�Σ�");
		}
		String tradeNo =  String.valueOf(order.getDramaid());
		String smsTradeNo = orderId + "drama" + tradeNo;
		int count = messageService.querySmsRecord(smsTradeNo, TagConstant.TAG_DRAMAORDER, null, null, orderId, member.getId());
		if((count + moblies.length) > 2) return showJsonError(model, "ÿ�ʶ�����������2λ����");
		if(StringUtils.isBlank(msgContent)) return showJsonError(model, "���ŵ����ݲ���Ϊ�գ�");
		if(!member.isBindMobile()) return showJsonError(model, "����ֻ������ԣ�");
		if(VmUtils.getByteLength(msgContent) > 128) return showJsonError(model, "�������ݳ��Ȳ��ܳ���64���֣����޸ģ�");
		boolean isSendMsg = StringUtils.isNotBlank(blogService.filterAllKey(msgContent));
		for(int i = 0;i < moblies.length;i++){
			if(moblies[i].length() > 1 && ValidateUtil.isMobile(moblies[i])){
				SMSRecord sms = new SMSRecord(moblies[i]);
				if(isSendMsg){
					sms.setStatus(SmsConstant.STATUS_FILTER);
				}
				sms.setTradeNo(smsTradeNo+StringUtil.getRandomString(5));
				sms.setContent(msgContent);
				sms.setSendtime(curtime);
				sms.setSmstype(SmsConstant.SMSTYPE_MANUAL);
				sms.setValidtime(DateUtil.getLastTimeOfDay(curtime));
				sms.setTag(TagConstant.TAG_DRAMAORDER);
				sms.setMemberid(member.getId());
				sms.setRelatedid(orderId);
				sms = untransService.addMessage(sms);
				if(sms!=null){
					untransService.sendMsgAtServer(sms, true);
				}
			}
		}
		operationService.updateOperation(opkey, OperationService.ONE_DAY);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/sport/ajax/newsportSendMsg.xhtml")
	public String newsportSendMsg(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, String mobile, String msgContent,
			ModelMap model, String captchaId, String captcha){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		Map jsonMap = new HashMap<String, String>();
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) {
			jsonMap.put("msg", "��֤�����");
			jsonMap.put("refreshCaptcha", true);
			return showJsonError(model, jsonMap);
		}
		SportOrder order = daoService.getObject(SportOrder.class, orderId);
		if(order == null) return showJsonError(model, "�ö��������ڻ�ɾ����");
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�ö��������˵Ķ�����");
		if ( order.isCancel()) return showJsonError(model, "���ܶ��ѣ���ʱ��ȡ���Ķ������Ͷ���������ѣ�");
		if(!StringUtils.equals(order.getStatus(), OrderConstant.STATUS_PAID_SUCCESS)) return showJsonError(model, "�ǳɹ�֧���Ķ��������ܷ�����");
		mobile = StringUtils.substring(mobile, 1);
		String[] moblies = StringUtils.split(mobile,",");
		if(moblies.length > 2) return showJsonError(model, "�����������ÿ�β��ܳ���2��");
		String opkey = member.getId()+"_" + order.getSportid();
		if(!operationService.isAllowOperation(opkey, OperationService.ONE_DAY)){
			return showJsonError(model, "ÿ�ʶ���ֻ������1�Σ�");
		}
		Timestamp curtime = DateUtil.getCurFullTimestamp();
		if(order.getPaidtime().before(DateUtil.addDay(curtime, -1))) return showJsonError(model, "����ʱ���ѳ�ʱ��");
		String tradeNo =  String.valueOf(order.getSportid());
		String smsTradeNo = orderId + "sport" + tradeNo;
		int count = messageService.querySmsRecord(smsTradeNo, TagConstant.TAG_SPORTORDER, null, null, orderId, member.getId());
		if((count + moblies.length) > 2) return showJsonError(model, "ÿ�ʶ�����������2λ����");
		if(StringUtils.isBlank(msgContent)) return showJsonError(model, "���ŵ����ݲ���Ϊ�գ�");
		if(!member.isBindMobile()) return showJsonError(model, "����ֻ������ԣ�");
		if(msgContent.length() > 60) return showJsonError(model, "�������ݳ��Ȳ��ܳ���60���֣����޸ģ�");
		boolean isSendMsg = StringUtils.isNotBlank(blogService.filterAllKey(msgContent));
		for(int i = 0;i < moblies.length;i++){
			if(ValidateUtil.isMobile(moblies[i])){
				SMSRecord sms = new SMSRecord(moblies[i]);
				if(isSendMsg){
					sms.setStatus(SmsConstant.STATUS_FILTER);
				}
				sms.setTradeNo(smsTradeNo+StringUtil.getRandomString(5));
				sms.setContent(msgContent);
				sms.setSendtime(curtime);
				sms.setSmstype(SmsConstant.SMSTYPE_MANUAL);
				sms.setValidtime(DateUtil.getLastTimeOfDay(curtime));
				sms.setTag(TagConstant.TAG_SPORTORDER);
				sms.setMemberid(member.getId());
				sms.setRelatedid(orderId);
				sms = untransService.addMessage(sms);
				if(sms!=null){
					untransService.sendMsgAtServer(sms, true);
				}
			}
		}
		operationService.updateOperation(opkey, OperationService.ONE_DAY);
		return showJsonSuccess(model);
	}

}
