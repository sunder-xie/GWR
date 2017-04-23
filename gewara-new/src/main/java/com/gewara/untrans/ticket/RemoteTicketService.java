package com.gewara.untrans.ticket;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.gewara.model.movie.Cinema;
import com.gewara.model.movie.CinemaRoom;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.ticket.MpiSeat;
import com.gewara.xmlbind.ticket.SynchPlayItem;
import com.gewara.xmlbind.ticket.TicketRemoteOrder;
import com.gewara.xmlbind.ticket.TicketRoom;
import com.gewara.xmlbind.ticket.TicketRoomSeatList;
import com.gewara.xmlbind.ticket.WdOrder;
import com.gewara.xmlbind.ticket.WdParam;

public interface RemoteTicketService {
	/**
	 * �ύ������������λ
	 * @param orderid	��������Ϣ
	 * @param seqno		��Ƭ���
	 * @param mobile	�ֻ���
	 * @param seatList	��λ��Ϣ
	 * @param priceList	��λ�ŵĳɱ�����Ϣ
	 * @param playtime HHmm����ӳʱ�䣬������֤
	 * @return
	 */
	ErrorCode<TicketRemoteOrder> remoteLockSeat(TicketOrder order, String seqno, String mobile, List<String> seatList, List<Integer> priceList, String playtime);
	/**
	 * ����Զ�̶���
	 * @param orderid
	 * @param seqno
	 * @param mobile
	 * @param seatList
	 * @param priceList
	 * @return
	 */
	ErrorCode<TicketRemoteOrder> createRemoteOrder(TicketOrder order, String seqno, String mobile, List<String> seatList);
	/**
	 * ȡ��������Ϣ
	 * @param orderid	��������Ϣ
	 * @return
	 */
	ErrorCode<TicketRemoteOrder> remoteUnLockSeat(TicketOrder order);
	
	/**
	 * ȷ�϶�����Ϣ
	 * @param orderid	��������Ϣ
	 * @param seqno		��Ƭ���
	 * @param mobile	�ֻ���
	 * @param seatList	��λ��Ϣ
	 * @param priceList	��λ�ŵĳɱ�����Ϣ
	 * @param playtime HHmm����ӳʱ�䣬������֤
	 * @return
	 */
	ErrorCode<TicketRemoteOrder> remoteFixOrder(TicketOrder order, String seqno, String mobile, int unitPrice, List<String> seatList, List<Integer> priceList, String playtime);
	
	/**
	 * ��Ʊ
	 * @param orderid
	 * @param description
	 * @return
	 */
	ErrorCode<TicketRemoteOrder> remoteCancelOrder(TicketOrder order, String description);
	
	/**
	 * ��ѯ����״̬
	 * @param orderid	��������Ϣ
	 * @param forceRefresh 
	 * @return
	 */
	ErrorCode<TicketRemoteOrder> getRemoteOrder(TicketOrder order, boolean forceRefresh);
	
	/**
	 * ���Զ��״̬�������ȷ�϶������򱣴�״̬
	 * @param orderid
	 * @param forceRefresh
	 * @return
	 */
	ErrorCode<TicketRemoteOrder> checkRemoteOrder(TicketOrder order);
	/**
	 * ��ȡ��Ƭ��������λ��Ϣ
	 * @param seqno			Զ������Ƭ���
	 * @return
	 */
	ErrorCode<String> getRemoteLockSeat(OpenPlayItem opi);
	
	/**
	 * ��ȡ��Ƭ������������λ��Ϣ
	 * @param seqno		Զ������Ƭ���
	 * @return
	 */
	ErrorCode<String> getLockSeatListFromCache(OpenPlayItem opi);
	
	/**
	 * ��ȡ��Ƭ��Ϣ
	 * @param updatetime	����ʱ��
	 * @return
	 */
	ErrorCode<List<SynchPlayItem>> getRemotePlayItemListByUpdatetime(Timestamp updatetime, Long cinemaid);
	
	/**
	 * ��ȡӰԺ��ǰ������Ƭ��Ϣ
	 * @param cinema		ӰԺ��Ϣ
	 * @param playdate		����
	 * @return
	 */
	ErrorCode<List<SynchPlayItem>> getRemotePlayItemList(Cinema cinema, Date playdate);
	
	/**
	 * ��ȡӰ����Ϣ
	 * @param cinema	ӰԺ��Ϣ
	 * @return
	 */
	ErrorCode<List<TicketRoom>> getRemoteRoomList(Cinema cinema);
	
	/**
	 * ��ȡӰ����λͼ
	 * @param room	Ӱ����Ϣ
	 * @return
	 */
	ErrorCode<TicketRoomSeatList> getRemoteRoomSeatList(CinemaRoom room);
	
	
	/**
	 * ����ӰԺ���ν����
	 * @param seqno			���α��
	 * @param costprice		�����
	 * @return	
	 */
	ErrorCode<Integer> featurePrice(String seqno, Integer costprice);
	
	ErrorCode<TicketRemoteOrder> backRemoteOrder(Long orderid);

	List<Integer> getPriceList(TicketOrder order, int size);
	/**
	 * ͳ�Ƴ�����������
	 * @param seqno
	 * @return
	 */
	ErrorCode<Integer> getPlanSiteStatistc(String seqno);
	/**
	 * ��ѯ�򴴽���ﶩ��
	 * @return
	 */
	ErrorCode<TicketRemoteOrder> getWdRemoteOrder(String wdOrderId, String seqNo, Long cinemaId);
	ErrorCode<TicketRemoteOrder> createWdRemoteOrder(String seqno, String wdOrderId, Long orderid, Long cinemaId);
	/**
	 * ����wanda���������Ƕ���ϵͳ��δ���ɣ�
	 * @param orderId
	 */
	boolean unlockWandaOrder(String orderId, Long cinemaId);
	ErrorCode<String> getWdUserId(String memberUkey);
	ErrorCode<WdParam> getWdParam(String memberUkey, String seqno);
	/**
	 * ��ȡ������λ
	 * @param seqno
	 * @return
	 */
	ErrorCode<List<MpiSeat>> getMpiSeat(MoviePlayItem mpi);
	
	ErrorCode<List<WdOrder>> getWDOrderList(Date addDate);
	
	/**
	 * ��ȡ�������۳�����
	 * @param mpi
	 * @return
	 */
	ErrorCode<Integer> getRemoteMtxSellNum(MoviePlayItem mpi);
}
