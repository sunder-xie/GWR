package com.gewara.untrans.ticket.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;

import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.ticket.OpiConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.jms.JmsConstant;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.MemberAccount;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.OpenSeat;
import com.gewara.model.ticket.SellSeat;
import com.gewara.service.OrderException;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.AbstractOrderProcessService;
import com.gewara.untrans.ticket.OrderProcessService;
import com.gewara.util.StringUtil;
import com.gewara.xmlbind.ticket.TicketRemoteOrder;

@Service("orderProcessService")
public class ProcessOrderServiceImpl extends AbstractOrderProcessService implements OrderProcessService {
	@Override
	public ErrorCode<GewaOrder> gewaPayOrderAtServer(Long orderId, Long memberid, String checkvalue, boolean ignoreCheck) {
		boolean allow = operationService.updateOperation("pay" + orderId, 30);
		if(!allow) {
			return ErrorCode.getFailure("ϵͳ���ڴ������15-20���ˢ�£�");
		}
		final GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(memberid))	return ErrorCode.getFailure("����֧�����˶�����");
		final String from;
		List<String> paymethodList = Arrays.asList(PaymethodConstant.PAYMETHOD_GEWAPAY, PaymethodConstant.PAYMETHOD_ELECARDPAY);
		if(!paymethodList.contains(order.getPaymethod())){
			return ErrorCode.getFailure("֧����ʽ����");
		}
		if (order.isZeroPay()) {
			from = "Zero";
			try {
				ticketOrderService.zeropayGewaOrder(order);
			} catch (StaleObjectStateException e) {
				dbLogger.warn(StringUtil.getExceptionTrace(e, 4));
				return ErrorCode.getFailure("�����д���");
			} catch (Exception e) {
				dbLogger.warn(StringUtil.getExceptionTrace(e, 4));
				return ErrorCode.getFailure(e.getMessage());
			}
		} else {
			MemberAccount account = daoService.getObjectByUkey(MemberAccount.class, "memberid",	order.getMemberid(), false);
			if (account == null) {
				return ErrorCode.getFailure("����û�н����˻���");
			}
			if(!ignoreCheck){
				String prikey = paymentService.getGewaPayPrikey();
				String str = account.getPassword()+account.getMemberid() + orderId + prikey;
				String checkvalue2 = StringUtil.md5(str, "UTF-8");
				if(!StringUtils.equals(checkvalue2, checkvalue)){
					return ErrorCode.getFailure("У�����");
				}
			}
			try {
				paymentService.gewaPayOrder(order, order.getMemberid());
				from = "GewaPay";
			} catch (OrderException e) {
				dbLogger.error("֧����������" + order.getId() + e.getMessage());
				return ErrorCode.getFailure(e.getMessage());
			} catch (Exception e) {
				dbLogger.error("֧����������", e);
				return ErrorCode.getFailure("֧����������");
			}
		}
		jmsService.sendMsgToDst(JmsConstant.QUEUE_PAY, JmsConstant.TAG_ORDER, "tradeNo,from", order.getTradeNo(), from);
		return ErrorCode.getSuccessReturn(order);
	}
	
	@Override
	public ErrorCode<GewaOrder> memberCardPayOrderAtServer(Long orderId, Long memberid) {
		boolean allow = operationService.updateOperation("pay" + orderId, 30);
		if(!allow) {
			return ErrorCode.getFailure("ϵͳ���ڴ������15-20���ˢ�£�");
		}
		final GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(memberid))	return ErrorCode.getFailure("����֧�����˶�����");
		final String from = "memberCardPay";
		try {
			memberCardService.memberCardPayOrder(order, memberid);
		} catch (OrderException e) {
			dbLogger.error("֧����������" + order.getId() + e.getMessage());
			return ErrorCode.getFailure(e.getMessage());
		} catch (Exception e) {
			dbLogger.error("֧����������", e);
			return ErrorCode.getFailure("֧����������");
		}
		jmsService.sendMsgToDst(JmsConstant.QUEUE_PAY, JmsConstant.TAG_ORDER, "tradeNo,from", order.getTradeNo(), from);
		return ErrorCode.getSuccessReturn(order);
	}
	
	@Override
	public ErrorCode processOrder(GewaOrder order, String from, String retry) {
		dbLogger.warn("���������" + order.getTradeNo() + "," + from + "," + retry);
		String processKey = PROCESS_ORDER + order.getId();
		boolean allow = operationService.updateOperation(processKey, PROCESS_INTERVAL);
		if (!allow) return ErrorCode.getFailure("����(ϵͳ)���ڴ�����ȴ�1-2���ӣ�");
		try{
			return processOrderInternal(order, from, retry);
		}finally{
			operationService.resetOperation(processKey,	PROCESS_INTERVAL);
		}
	}
	@Override
	public ErrorCode reconfirmOrder(TicketOrder order, Long userid,	boolean isAuto, boolean reChange) {
		String processKey = PROCESS_ORDER + order.getId();
		boolean allow = operationService.updateOperation(processKey, PROCESS_INTERVAL);
		if (!allow)	return ErrorCode.getFailure("����(ϵͳ)���ڴ�����ȴ�1-2���ӣ�");
		OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", order.getMpid(), true);
		try {
			if (!opi.hasGewara()) {
				ErrorCode<TicketRemoteOrder> remoteOrder = ticketOperationService.checkRemoteOrder(order);
				if (!remoteOrder.isSuccess()) {
					return ErrorCode.getFailure(remoteOrder.getMsg());
				}
				if (remoteOrder.getRetval().hasFixed()) {// �Ѿ��ɹ�
					if (order.isPaidFailure() || order.isPaidUnfix()) {
						ErrorCode result = processOrderInternal(order, "����ȷ��", null);
						if (result.isSuccess()) {
							dbLogger.warn(userid + "ת������״̬Ϊ���׳ɹ���"	+ order.getTradeNo());
							return ErrorCode.getSuccess("���ɹ�,ת���ɹ���");
						} else {
							return ErrorCode.getSuccess("���ɹ�,ת��ʧ�ܣ�" + result);
						}
					}
				}
			}
			if (!order.getStatus().equals(OrderConstant.STATUS_PAID_UNFIX)) {
				return ErrorCode.getFailure("ֻ�С���λ�����������ſ�����ȷ�ϣ�");
			}
			//��������⴦��
			if(StringUtils.equals(OpiConstant.OPEN_WD, order.getCategory())){
				return ErrorCode.getFailure("���ӰԺ�ݲ�֧�֣�");
			}
			List<SellSeat> oldSeatList = ticketOrderService.getOrderSeatList(order.getId());
			List<OpenSeat> seatList = ticketProcessService.getOriginalSeat(order,	oldSeatList);
			TicketOrder newOrder = ticketProcessService.changeSeat(opi, seatList,	order, reChange);
			
			ErrorCode code = confirmSuccessInternal(newOrder, opi, userid, isAuto);
			return code;
			// FIXME:����Exception
		} catch (OrderException e){
			dbLogger.warn(StringUtil.getExceptionTrace(e, 10));
			return ErrorCode.getFailure(e.getMsg());
		} catch (Exception e) {
			dbLogger.warn("", e);
			return ErrorCode.getFailure(e.getClass() + " Exception: " + e.getMessage());
		} finally{
			operationService.resetOperation(processKey, PROCESS_INTERVAL);
		}
	}
	@Override
	public ErrorCode confirmSuccess(TicketOrder order, OpenPlayItem opi, Long userid, boolean isAuto) {
		String processKey = PROCESS_ORDER + order.getId();
		boolean allow = operationService.updateOperation(processKey, PROCESS_INTERVAL);
		if (!allow)	return ErrorCode.getFailure("����(ϵͳ)���ڴ�����ȴ�1-2���ӣ�");
		try{
			return confirmSuccessInternal(order, opi, userid, isAuto);
		}finally{
			operationService.resetOperation(processKey, PROCESS_INTERVAL);
		}
	}
	private ErrorCode confirmSuccessInternal(TicketOrder order, OpenPlayItem opi, Long userid, boolean isAuto) {
		try {
			TicketRemoteOrder remoteOrder = null;
			if (!opi.hasGewara()) {
				ErrorCode<TicketRemoteOrder> checkResult = ticketOperationService.checkRemoteOrder(order);
				if (!checkResult.isSuccess()) {
					return ErrorCode.getFailure(checkResult.getMsg());
				}
				remoteOrder = checkResult.getRetval();
			}
			if(order.isPaidUnfix()){//��λ������
				if (remoteOrder != null && !remoteOrder.hasFixed() /*&& !remoteOrder.hasOrderType(OpiConstant.OPEN_MTX)*/ && order.needChangeSeat())
					return ErrorCode.getFailure("ֻ�л�����λ�Ĳ���ȷ�ϳɹ���");
			}
			String username = "�Զ�";
			if (!isAuto) {
				if (userid == null) return ErrorCode.getFailure("���ȵ�¼��");
				username = "" + userid;
			}
			if (!order.isPaidFailure() && !order.isPaidUnfix())
				return ErrorCode.getFailure("״̬�д���");
			ErrorCode result = processOrderInternal(order, "����ȷ��", null);
			if (result.isSuccess()) {
				dbLogger.warn(username + "ת������״̬Ϊ���׳ɹ���" + order.getTradeNo());
				return ErrorCode.SUCCESS;
			} else {
				return result;
			}
		} catch (Exception e) {
			dbLogger.warn("", e);
			return ErrorCode.getFailure(e.getClass() + " Exception: " + e.getMessage());
		}
	}
}
