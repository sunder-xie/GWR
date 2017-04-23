package com.gewara.untrans.ticket;

import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.support.ErrorCode;


public interface OrderProcessService {
	/**
	 * ����֧����ɵĶ�������������paid_failure*---->paid_successת��
	 * @param order
	 * @param from
	 * @param retry
	 * @return
	 */
	ErrorCode processOrder(GewaOrder order, String from, String retry);
	/**
	 * ����ȷ�϶���
	 * @param order
	 * @param id
	 * @param isAuto
	 * @param reChange
	 * @return
	 */
	ErrorCode reconfirmOrder(TicketOrder order, Long id, boolean isAuto, boolean reChange);
	/**
	 * ȷ�϶���
	 * @param order
	 * @param opi
	 * @param userid
	 * @param isAuto
	 * @return
	 */
	ErrorCode confirmSuccess(TicketOrder order, OpenPlayItem opi, Long userid, boolean isAuto);
	/**
	 * ֧������
	 * @param orderId
	 * @param memberid
	 * @return
	 */
	ErrorCode<GewaOrder> gewaPayOrderAtServer(Long orderId, Long memberid, String checkvalue, boolean ignoreCheck);
	/**
	 * ��Ա��֧��
	 * @param orderId
	 * @param memberid
	 * @return
	 */
	ErrorCode<GewaOrder> memberCardPayOrderAtServer(Long orderId, Long memberid);
}
