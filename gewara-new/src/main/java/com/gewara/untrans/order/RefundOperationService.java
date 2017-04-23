package com.gewara.untrans.order;

import com.gewara.model.acl.User;
import com.gewara.model.pay.AccountRefund;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.OrderRefund;
import com.gewara.model.pay.SMSRecord;
import com.gewara.support.ErrorCode;

public interface RefundOperationService {

	/**
	 * ȷ�϶�����Ʊ
	 * @param rid
	 * @param user
	 * @return
	 */
	ErrorCode confirmRefund(OrderRefund refund, Long userid, String username);
	
	/**
	 * ȷ�϶�����Ʊ
	 * @param rid
	 * @param user
	 * @return
	 */
	ErrorCode confirmRefund(OrderRefund refund, GewaOrder order, Long userid, String username);
	
	/**
	 * ȡ���˿�����ż��ʼ�����
	 * @param order
	 */
	void refundOtherOperation(GewaOrder order);
	
	/**
	 * ��Ӱ����ȷ����Ʊ
	 * @param refund
	 * @param force
	 * @param user
	 * @return
	 */
	ErrorCode cancelTicket(OrderRefund refund, boolean force, User user);
	
	/**
	 * �˿���ж���
	 * @param userid
	 * @param accountRefund
	 * @return
	 */
	SMSRecord bankTemplateMsg(Long userid, AccountRefund accountRefund);

}
