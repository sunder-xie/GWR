package com.gewara.untrans.order.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gewara.Config;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.SmsConstant;
import com.gewara.constant.Status;
import com.gewara.constant.sys.JsonDataKey;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OpiConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.RefundConstant;
import com.gewara.model.acl.User;
import com.gewara.model.common.JsonData;
import com.gewara.model.partner.CallbackOrder;
import com.gewara.model.pay.AccountRefund;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.OrderRefund;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.pay.PayUtil;
import com.gewara.service.DaoService;
import com.gewara.service.MessageService;
import com.gewara.service.gewapay.AccountRefundService;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.service.gewapay.RefundOrderService;
import com.gewara.service.gewapay.RefundService;
import com.gewara.service.partner.PartnerSynchService;
import com.gewara.service.ticket.TicketProcessService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.MailService;
import com.gewara.untrans.UntransService;
import com.gewara.untrans.monitor.MonitorService;
import com.gewara.untrans.order.RefundOperationService;
import com.gewara.untrans.ticket.TicketOperationService;
import com.gewara.util.ChangeEntry;
import com.gewara.util.GewaLogger;
import com.gewara.util.JsonUtils;
import com.gewara.util.LoggerUtils;

@Service("refundOperationService")
public class RefundOperationServiceImpl implements RefundOperationService {

	private GewaLogger dbLogger = LoggerUtils.getLogger(getClass(), Config.getServerIp(), Config.SYSTEMID);
	
	@Autowired@Qualifier("daoService")
	private DaoService daoService;
	
	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;
	
	@Autowired@Qualifier("refundService")
	private RefundService refundService;
	
	@Autowired@Qualifier("partnerSynchService")
	private PartnerSynchService partnerSynchService;
	
	@Autowired@Qualifier("ticketProcessService")
	private TicketProcessService ticketProcessService;
	
	@Autowired@Qualifier("monitorService")
	private MonitorService monitorService;
	
	@Autowired@Qualifier("mailService")
	private MailService mailService;
	
	@Autowired@Qualifier("ticketOperationService")
	private TicketOperationService ticketOperationService;
	
	@Autowired@Qualifier("accountRefundService")
	private AccountRefundService accountRefundService;
	
	@Autowired@Qualifier("refundOrderService")
	private RefundOrderService refundOrderService;
	
	@Autowired@Qualifier("messageService")
	private MessageService messageService;
	
	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	
	
	@Override
	public ErrorCode confirmRefund(OrderRefund refund, Long userid, String username){
		if(refund == null){
			return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "��Ʊ���������ڣ�");
		}
		//δ���ڶ����˿�
		if(!StringUtils.equals(refund.getStatus(), RefundConstant.STATUS_ACCEPT)){
			return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "���Ƚ����˿��");
		}
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", refund.getTradeno(), false);
		return confirmRefund(refund, order, userid, username);
	}
	
	@Override
	public ErrorCode confirmRefund(OrderRefund refund, GewaOrder order, Long userid, String username){
		if(refund == null){
			return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "��Ʊ���������ڣ�");
		}
		ErrorCode result = null;
		if(order==null) return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "���������ڣ�");
		ChangeEntry entry = new ChangeEntry(refund);
		if(refund.getRefundtype().equals(RefundConstant.REFUNDTYPE_SUPPLEMENT)){//�����˿�
			result = paymentService.refundSupplementOrder(refund, order, userid);
		}else if(refund.getRefundtype().equals(RefundConstant.REFUNDTYPE_PART)){//�����˿�
			result = ticketProcessService.refundPartExpiredOrder(refund, (TicketOrder)order, userid);
		}else{//ȫ���˿�
			if(PayUtil.isTicketTrade(refund.getTradeno())){
				OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", ((TicketOrder) order).getMpid(), true);
				if(opi.isExpired()){//���ڶ���
					result = ticketProcessService.refundFullExpiredOrder(refund, opi, (TicketOrder) order, userid);
					//TODO:���ڶ����̼һص�����
				}else{
					result = ticketProcessService.refundFullCurOrder(refund, opi, (TicketOrder) order, userid);
					if(result.isSuccess()){
						refundOtherOperation(order);
						if(result.getRetval()!=null) {
							//�̼һص�
							partnerSynchService.pushCallbackOrder((CallbackOrder) result.getRetval());
						}
					}
				}
			}else{
				//TODO:���ֹ�����ǹ���
				result = otherOrderRefund(order, refund, userid);
			}
			
		}
		
		dbLogger.warn("�˿�����" + result.isSuccess() + ":" + refund.getTradeno() + ":" + result.getMsg());
		if(result.isSuccess()){
			final String retback = refund.getRetback();
			if(StringUtils.equals(retback, RefundConstant.RETBACK_N)){
				SMSRecord sms = accountTemplateMsg(userid, refund);
				if(sms != null){
					untransService.sendMsgAtServer(sms, false);
				}
			}
			result = refundService.submit2Financial(refund);//��һ���ύ�����˿�
			if(result.isSuccess()){
				AccountRefund accountRefund = (AccountRefund) result.getRetval();
				accountRefundService.deductAccountRefund(accountRefund, userid);
			}
			monitorService.saveChangeLog(userid, OrderRefund.class, refund.getTradeno(), entry.getChangeMap(refund));
			saveOperator(refund, userid, username, RefundConstant.REFUND_MANAGE_DEAL);
			return ErrorCode.getSuccess("�˿�ɹ���" + (result.isSuccess()?"":result.getMsg()));
		}else{
			monitorService.saveChangeLog(userid, OrderRefund.class, refund.getTradeno(), entry.getChangeMap(refund));
			return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "�˿�ʧ�ܣ�" + result.getMsg());
		}
	}
	
	private SMSRecord accountTemplateMsg(Long userid, OrderRefund refund){
		String notifymsg = "";
		JsonData template = daoService.getObject(JsonData.class, JsonDataKey.KEY_REFUNDACCOUNT);
		if(template != null){
			Map<String, String> dataMap = JsonUtils.readJsonToMap(template.getData());
			notifymsg = dataMap.get("notifymsg");
		}
		if(StringUtils.isBlank(notifymsg)){
			dbLogger.warn("accountTemplateMsg: notifymsg is null or \"\" !");
			return null;
		}
		notifymsg = StringUtils.replace(notifymsg, "sorder", StringUtils.right(refund.getTradeno(), 6));
		notifymsg = StringUtils.replace(notifymsg, "amount", refund.getGewaRetAmount()+"");
		SMSRecord sms = messageService.addManualMsg(userid, refund.getMobile(), notifymsg, refund.getTradeno() + "refund");
		dbLogger.warn("accountTemplateMsg: notifymsg tradeno," + refund.getTradeno() + ", sms:" + (sms != null));
		return sms;
	}
	
	@Override
	public SMSRecord bankTemplateMsg(Long userid, AccountRefund accountRefund){
		String notifymsg = "";
		JsonData template = daoService.getObject(JsonData.class, JsonDataKey.KEY_REFUNDBANK);
		if(template != null){
			Map<String, String> dataMap = JsonUtils.readJsonToMap(template.getData());
			notifymsg = dataMap.get("notifymsg");
		}
		if(StringUtils.isBlank(notifymsg)){
			dbLogger.warn("bankTemplateMsg: notifymsg is null or \"\" !");
			return null;
		}
		notifymsg = StringUtils.replace(notifymsg, "sorder", StringUtils.right(accountRefund.getTradeno(), 6));
		notifymsg = StringUtils.replace(notifymsg, "amount", accountRefund.getAmount()+"");
		SMSRecord sms = messageService.addManualMsg(userid, accountRefund.getMobile(), notifymsg, accountRefund.getTradeno() + "bank");
		dbLogger.warn("bankTemplateMsg: notifymsg tradeno," + accountRefund.getTradeno() + ", sms:" + (sms != null));
		return sms;
	}
	
	private void saveOperator(OrderRefund refund, Long userid, String username, String key){
		refund.setOtherinfo(JsonUtils.addJsonKeyValue(refund.getOtherinfo(), key, userid +","+ username));
		daoService.saveObject(refund);
	}
	
	private ErrorCode otherOrderRefund(GewaOrder order, OrderRefund refund, Long userid){
		ErrorCode code = refundOrderService.refund(order, refund, userid);
		if(code.isSuccess()){
			refundOtherOperation(order);
		}
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, userid + "�����˿�ɹ������" + code.isSuccess() + code.getMsg());
		return code;
	}
	
	@Override
	public void refundOtherOperation(GewaOrder order){
		if(!StringUtils.equals(order.getStatus(), OrderConstant.STATUS_PAID_RETURN)){
			return;
		}
		//ȡ�������źͷ��ʼ�
		List<SMSRecord> smsList = daoService.getObjectListByField(SMSRecord.class, "tradeNo", order.getTradeNo());
		List<SMSRecord> delSmsList = new ArrayList<SMSRecord>();
		for (SMSRecord smsRecord : smsList) {
			if(!StringUtils.contains(smsRecord.getStatus(), Status.Y)){
				smsRecord.setStatus( SmsConstant.STATUS_D + smsRecord.getStatus());
				delSmsList.add(smsRecord);
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "�����˿�ɹ���ȡ������ID��"+smsRecord.getId()+"  �����ţ�"+smsRecord.getTradeNo());
			}
		}
		daoService.saveObjectList(delSmsList);
		//ȡ���ʼ�
		Map otherInfoMap = JsonUtils.readJsonToMap(order.getOtherinfo());
		if(otherInfoMap.get(OrderConstant.STATUS_EMAIL_ID) != null){
			mailService.cancelSendEmailApi(otherInfoMap.get(OrderConstant.STATUS_EMAIL_ID) + "");
		}
	}
	
	@Override
	public ErrorCode cancelTicket(OrderRefund refund, boolean force, User user){
		
		if(!StringUtils.equals(refund.getStatus(), RefundConstant.STATUS_ACCEPT)){
			return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "���Ƚ����˿��");
		}
		if(PayUtil.isTicketTrade(refund.getTradeno())){
			TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", refund.getTradeno(), false);
			if(order==null) return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "���������ڣ�");
			OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", order.getMpid(), true);
			if(!opi.hasGewara()){
				if(opi.isExpired()) {
					return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "���ڳ��β�����Ʊ����");
				}
				//���
				ErrorCode result = ticketOperationService.cancelRemoteTicket(opi, order, user.getId());
				if(!result.isSuccess() && !force) {
					return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR,  OpiConstant.getParnterText(opi.getOpentype()) + "��Ʊʧ�ܣ�ʧ��ԭ��" + result.getMsg());
				}
				ChangeEntry entry = new ChangeEntry(refund);
				if(force) {//ǿ���˿��
					result = ticketProcessService.forceHfhRefund(refund, opi, order, user.getId());
				}else {
					result = ticketProcessService.hfhRefund(refund, opi, order, user.getId());
				}
				if(result.isSuccess()){
					refundOtherOperation(order);//ȡ������
					if(result.getRetval()!=null) {
						//�̼һص�
						partnerSynchService.pushCallbackOrder((CallbackOrder) result.getRetval());
					}
					final String retback = refund.getRetback();
					if(StringUtils.equals(retback, RefundConstant.RETBACK_N)){
						SMSRecord sms = accountTemplateMsg(user.getId(), refund);
						if(sms != null){
							untransService.sendMsgAtServer(sms, false);
						}
					}
					result = refundService.submit2Financial(refund);//��һ���ύ�����˿�
					if(result.isSuccess()){//ֱ����һ�οۿ�!
						AccountRefund accountRefund = (AccountRefund) result.getRetval();
						accountRefundService.deductAccountRefund(accountRefund, user.getId());
					}
					monitorService.saveChangeLog(user.getId(), OrderRefund.class, refund.getTradeno(), entry.getChangeMap(refund));
					saveOperator(refund, user.getId(), user.getUsername(), RefundConstant.REFUND_MANAGE_DEAL);
					return ErrorCode.getSuccess("��Ʊ�ɹ����˿�ɹ���" + (result.isSuccess()?"":result.getMsg()));
				}else{
					monitorService.saveChangeLog(user.getId(), OrderRefund.class, refund.getTradeno(), entry.getChangeMap(refund));
					saveOperator(refund, user.getId(), user.getUsername(), RefundConstant.REFUND_MANAGE_DEAL);
					return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "��Ʊ�ɹ����˿�ʧ�ܣ�" + result.getMsg());
				}
			}else{
				return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "�ǵ��������Σ�������Ʊ��");
			}
		}else{
			return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "�ǵ�Ӱ����������Ʊ���ܣ�");
		}
	}
}
