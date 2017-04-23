/**
 * 
 */
package com.gewara.service.gewapay;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.command.SearchRefundCommand;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.pay.AccountRefund;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.GymOrder;
import com.gewara.model.pay.OrderRefund;
import com.gewara.model.pay.PubSaleOrder;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.support.ErrorCode;

public interface RefundService{
	/**
	 * ͳ�Ƹ��û��Ĳ�ͬ��Ʊԭ��Ĵ���
	 * @param command
	 * @return
	 */
	List<Map> getRefundReason(SearchRefundCommand command);
	List<OrderRefund> getOrderRefundList(SearchRefundCommand command, String order, int from, int maxnum);
	/**
	 * ��ѯ�¾ɽ���۲�ͬ���˿��
	 * @param command
	 * @param order
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<OrderRefund> getDifferentSettleOrderRefundList(SearchRefundCommand command, String order, int from, int maxnum);
	Integer getOrderRefundCount(SearchRefundCommand command);
	/**
	 * ����δ����ȫ���˿�
	 * @param order
	 * @param opi
	 * @param userid
	 * @param status
	 * @return
	 */
	ErrorCode<OrderRefund> getCurFullTicketOrderRefund(TicketOrder order, OpenPlayItem opi, Long userid, String status);
	/**
	 * �������ȫ���˿�
	 * @param order
	 * @param opi
	 * @param userid
	 * @param status
	 * @return
	 */
	ErrorCode<OrderRefund> getExpFullTicketOrderRefund(TicketOrder order, OpenPlayItem opi, Long userid, String status);
	/**
	 * �������˿����Ҫ���ڳɹ�״̬
	 * @param order
	 * @param opi
	 * @param userid
	 * @param status
	 * @return
	 */
	ErrorCode<OrderRefund> getSupplementTicketOrderRefund(TicketOrder order, OpenPlayItem opi, Long userid, String status);
	/**
	 * ���벿���˿�
	 * @param order
	 * @param opi
	 * @param userid
	 * @param status
	 * @return
	 */
	ErrorCode<OrderRefund> getPartTicketOrderRefund(TicketOrder order, OpenPlayItem opi, Long userid, String status);
	
	//��������ȫ���˿�
	ErrorCode<OrderRefund> getFullDramaOrderRefund(DramaOrder order, OpenDramaItem odi, Long userid, String status);
	//�����������˿����Ҫ���ڳɹ�״̬
	ErrorCode<OrderRefund> getSupplementDramaOrderRefund(DramaOrder order, OpenDramaItem odi, Long userid, String status);
	//�������벿���˿�
	//ErrorCode<OrderRefund> getPartDramaOrderRefund(DramaOrder order, OpenDramaItem odi, Long userid, String status);
	//�˶�����ȫ���˿�
	ErrorCode<OrderRefund> getFullSportOrderRefund(SportOrder order, OpenTimeTable ott, Long userid, String status);
	//�˶��������˿����Ҫ���ڳɹ�״̬
	ErrorCode<OrderRefund> getSupplementSportOrderRefund(SportOrder order, OpenTimeTable ott, Long userid, String status);
	//�˶����벿���˿�
	//ErrorCode<OrderRefund> getPartSportOrderRefund(SportOrder order, OpenTimeTable ott, Long userid, String status);
	
	
	ErrorCode<OrderRefund> getDramaOrderRefund(DramaOrder order, Long userid, String status);
	ErrorCode<OrderRefund> getSportOrderRefund(SportOrder order, Long userid, String status);
	ErrorCode<OrderRefund> getGymOrderRefund(GymOrder order, Long userid, String status);
	ErrorCode<OrderRefund> getGoodsOrderRefund(GoodsOrder order, Long userid, String status);
	ErrorCode<OrderRefund> getPubSaleOrderRefund(PubSaleOrder order, Long userid, String status);
	/**
	 * �˿������ύ����
	 * @param refund
	 * @return
	 */
	ErrorCode<AccountRefund> submit2Financial(OrderRefund refund);
	List<OrderRefund> getSettleRefundList(String ordertype, Timestamp timefrom, Timestamp timeto, Long placeid);
}
