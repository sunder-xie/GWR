package com.gewara.service.ticket;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.helper.order.OrderContainer;
import com.gewara.helper.order.TicketOrderContainer;
import com.gewara.model.api.ApiUser;
import com.gewara.model.goods.GoodsGift;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.OpenSeat;
import com.gewara.model.ticket.SellSeat;
import com.gewara.model.user.Member;
import com.gewara.service.OrderException;
import com.gewara.service.order.GewaOrderService;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.ticket.WdOrder;

/**
 * �µ������������
 * @author gebiao(ge.biao@gewara.com)
 * @since Feb 1, 2013 7:40:50 PM
 */
public interface TicketOrderService extends GewaOrderService {
	int MAXSEAT_PER_ORDER = 5;

	OpenSeat getOpenSeatByLoc(Long mpid, String seatline, String seatrank);
	ErrorCode checkOrderSeat(TicketOrder order, List<SellSeat> seatList);
	ErrorCode validLoveSeat(List<OpenSeat> oseatList);
	/**
	 * �����λ�Ƿ�������
	 * @param oseatList
	 * @param oseatList
	 * @param sellSeatMap
	 * @param hfhLockList
	 * @return
	 */
	ErrorCode validateSeatLock(List<OpenSeat> oseatList, Map<Long/*id*/, SellSeat> sellSeatMap, List<String> hfhLockList);
	
	/**
	 * ����SellSeat
	 * @param oseatList
	 * @return
	 */
	List<SellSeat> createSellSeat(List<OpenSeat> oseatList);
	/**
	 * ȡ������ʧ�ܶ���
	 * @param tradeNo
	 * @param memberid
	 * @param status
	 * @param reason
	 * @return
	 */
	void cancelLockFailureOrder(TicketOrder order);
	/**
	 * @param tradeNo
	 * @param memberid
	 * @param status
	 * @param reason
	 * @return
	 */
	TicketOrder cancelTicketOrder2(String tradeNo, Long memberid, String status, String reason);
	/**
	 * ĳ�������һ��δ֧���Ķ���
	 * @param ukey
	 * @param mpid
	 * @return
	 */
	TicketOrder getLastUnpaidTicketOrder(Long memberid, String ukey, long mpid);
	/**
	 * ������֧����ȷ��֧�����Ķ������������нӿڻص�����������ȷ�ϣ�
	 * paid_failure ----> paid_unfixed
	 * @param order
	 * @param opi
	 * @param seatList
	 * @return OrderContainer(discountList,spcounter,order,
	 * @throws OrderException
	 */
	OrderContainer processOrderPay(TicketOrder order, OpenPlayItem opi, List<SellSeat> seatList) throws OrderException;
	List<SellSeat> getOrderSeatList(Long orderid);
	
	ErrorCode<GoodsGift> addOrderGoodsGift(TicketOrder order, OpenPlayItem opi, Long goodsid, Integer quantity);
	/**
	 * ��ͣ��Ʊ
	 * @param opi
	 * @return
	 */
	ErrorCode checkPauseBooking(OpenPlayItem opi);
	void processSuccess(TicketOrder torder, List<SellSeat> seatList, Timestamp curtime);
	/**
	 * ���ɹ�Ʊ����
	 * @param opi
	 * @param seatidList
	 * @param memberid
	 * @param membername
	 * @param mobile
	 * @param point
	 * @param randomNum
	 * @param hfhLockList
	 * @param partnerid
	 * @return TicketOrderContainer(order,opi,seatList,oseatList,bindGift)
	 * @throws OrderException
	 */
	TicketOrderContainer addTicketOrder(OpenPlayItem opi, List<Long> seatidList, Long memberid, String membername, String mobile, Integer point, String randomNum, List<String> hfhLockList) throws OrderException;
	/**
	 * @param opi
	 * @param seatLabel
	 * @param member
	 * @param partner
	 * @param mobile
	 * @param checkpass
	 * @param hfhLockList
	 * @return
	 * @throws OrderException
	 */
	TicketOrderContainer addTicketOrder(OpenPlayItem opi, String seatLabel, Member member, ApiUser partner, String mobile, String randomNum, List<String> hfhLockList) throws OrderException;
	/**
	 * @param opi
	 * @param seatLabel
	 * @param member
	 * @param partner
	 * @param mobile
	 * @param checkpass
	 * @param hfhLockList
	 * @return
	 * @throws OrderException
	 */
	TicketOrderContainer addTicketOrder(OpenPlayItem opi, List<Long> seatidList, Member member, ApiUser partner, String mobile, String randomNum, List<String> hfhLockList) throws OrderException;
	/**
	 * ���Ӷ���
	 * @param mpid
	 * @param seatidList
	 * @param apiUser
	 * @param mobile
	 * @param checkpass
	 * @return
	 * @throws OrderException
	 */
	TicketOrderContainer addPartnerTicketOrder(OpenPlayItem opi, List<Long> seatidList, ApiUser apiUser, 
			String mobile, String checkpass, String ukey, String userid, String paymethod, String paybank, List<String> hfhLockList) throws OrderException;
	TicketOrderContainer addPartnerTicketOrder(OpenPlayItem opi, String seatLabel, ApiUser apiUser, 
			String mobile, String checkpass, String ukey, String userid, String paymethod, String paybank, List<String> hfhLockList) throws OrderException;
	/**
	 * ���������û�����
	 * @param parentid
	 * @param ukey
	 * @param status
	 * @return
	 */
	List<TicketOrder> getTicketOrderListByUkey(Long parentid, String ukey, String status);
	/**
	 * �������͵Ķ���
	 * @param order
	 */
	ErrorCode<GoodsOrder> addBindGoodsOrder(TicketOrder torder, String randomNum);
	/**
	 * ��֤��λ�Ϸ���
	 * @param mpid
	 * @param seatLabel
	 * @param hfhLockList
	 * @return
	 */
	ErrorCode<String> isValidateSeatPosition(OpenPlayItem opi, String seatLabel, List<String> hfhLockList);
	String getOrderHis(String mobile);
	//void setOrderDescription(TicketOrder order, Collection<SellSeat> seatList, OpenPlayItem opi);
	/**
	 * �����û����һ�ʶ�����
	 * 1���д����������򷵻ش�����Ϣ
	 * 2����δ������Զ�ȡ��
	 * @param memberid
	 * @return
	 */
	ErrorCode processLastOrder(Long memberid, String ukey, String msg);
	
	/**
	 * ��ﶩ���͸����������Ա�
	 * @param date
	 * @return ���ظ�����ϵͳ�еĶ����������ϵͳ��δͬ����
	 */
	List<TicketOrder> wdOrderContrast(Date date,List<WdOrder> wdOrderList);
	/**
	 * ��ȡȫ�ֶ�����
	 * @return
	 */
	String getTicketTradeNo();
	ErrorCode<TicketOrder> removeBuyItem(Long memberid, Long itemid);
}
