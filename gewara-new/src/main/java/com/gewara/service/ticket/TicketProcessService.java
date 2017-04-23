package com.gewara.service.ticket;

import java.sql.Timestamp;
import java.util.List;

import com.gewara.model.partner.CallbackOrder;
import com.gewara.model.pay.OrderRefund;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.OpenSeat;
import com.gewara.model.ticket.SellSeat;
import com.gewara.service.OrderException;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.ticket.TicketRemoteOrder;

/**
 * ������ĺ����������
 * @author gebiao(ge.biao@gewara.com)
 * @since Feb 1, 2013 5:15:27 PM
 */
public interface TicketProcessService {
	/**
	 * �����˿ֻ�д������������˿�
	 * @param order
	 * @param seatList
	 * @param discountList
	 * @param refund
	 * @param userid
	 * @return
	 */
	ErrorCode<CallbackOrder> refundFullCurOrder(OrderRefund refund, OpenPlayItem opi, TicketOrder order, Long userid);
	/**
	 * ���ڶ����˿�
	 * @param refund
	 * @param opi
	 * @param order
	 * @param userid 
	 * @return
	 */
	ErrorCode<CallbackOrder> refundFullExpiredOrder(OrderRefund refund, OpenPlayItem opi, TicketOrder order, Long userid);
	/**
	 * �����˿����ȯ����ȡ���ۿ�
	 * @param order
	 * @return
	 */
	ErrorCode refundPartExpiredOrder(OrderRefund refund, TicketOrder order, Long userid);
	/**
	 * �������˿�
	 * @param order
	 * @param seatList
	 * @param discountList
	 * @param refund
	 * @param userid
	 * @return
	 */
	ErrorCode<CallbackOrder> hfhRefund(OrderRefund refund, OpenPlayItem opi, TicketOrder order, Long userid);
	ErrorCode<CallbackOrder> forceHfhRefund(OrderRefund refund, OpenPlayItem opi, TicketOrder order, Long userid);
	void updateBuytimes(Long memberid, String mobile, String orderType, Timestamp addtime);
	/**
	 * ͬ���ɱ��۸�
	 * @param order
	 * @param userid
	 * @return
	 */
	ErrorCode synchCostprice(TicketOrder order, Long userid);
	/**
	 * ���Ķ�������λ
	 * 1��ֻ��״̬Ϊpaid_failure�Ķ������ܸ�����λ
	 * 2������λ�ļ۸���������λ�ļ۸񱣳�һ��
	 * 3��ͬ�������Ż�ȯ
	 * 4��ͬ����������λ��״̬��������˶���ռ�õĻ���
	 * @param orderid
	 * @param newseat ��ʽΪ 5:03,5:04,6:3,6:4
	 * @return
	 * @throws OrderException 
	 */
	List<OpenSeat> getNewSeatList(TicketOrder order, OpenPlayItem opi, List<SellSeat> seatList, String newseat) throws OrderException;
	List<OpenSeat> getOriginalSeat(TicketOrder order, List<SellSeat> seatList) throws OrderException;
	TicketOrder changeSeat(OpenPlayItem opi, List<OpenSeat> oseatList, TicketOrder oldOrder, boolean reChange) throws OrderException;
	TicketOrder wdChangeSeat(OpenPlayItem opi, String oldTradeNo, TicketRemoteOrder wdOrder, String wdOrderId, String randomNum) throws OrderException;
}
