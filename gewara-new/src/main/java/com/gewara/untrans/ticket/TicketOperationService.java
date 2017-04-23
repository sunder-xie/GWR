package com.gewara.untrans.ticket;

import java.util.Date;
import java.util.List;

import com.gewara.helper.ticket.UpdateMpiContainer;
import com.gewara.model.acl.User;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.SellSeat;
import com.gewara.service.OrderException;
import com.gewara.support.ErrorCode;
import com.gewara.util.HttpResultCallback;
import com.gewara.xmlbind.ticket.TicketRemoteOrder;

public interface TicketOperationService {
	ErrorCode<List<String>> updateRemoteLockSeat(OpenPlayItem opi);
	/**
	 * ��ȡ������ĳ������������λ
	 * @param opi
	 * @param expireSeconds ��ʱ����
	 * @param room
	 * @param force ǿ��ˢ��
	 * @return
	 */
	ErrorCode<List<String>> updateRemoteLockSeat(OpenPlayItem opi, int expireSeconds, boolean force);
	ErrorCode<List<String>> getCachedRemoteLockSeat(OpenPlayItem opi, int expireSeconds);
	void asynchUpdateSeatLock(OpenPlayItem opi, boolean asynch, HttpResultCallback asynchCallback);
	ErrorCode<List<String>> updateRemoteLockSeatFromCache(OpenPlayItem opi);
	/**
	 * �첽������λͼ��ǰ�˼�APIʹ��
	 * @param opi
	 * @return
	 */
	ErrorCode<List<String>> updateLockSeatListAsynch(OpenPlayItem opi);
	ErrorCode lockRemoteSeat(OpenPlayItem opi, TicketOrder order, List<SellSeat> seatList);
	/**
	 * ����Զ�̶���
	 * @param opi
	 * @param order
	 * @param seatList
	 * @return
	 */
	ErrorCode createRemoteOrder(OpenPlayItem opi, TicketOrder order, List<SellSeat> seatList);
	ErrorCode cancelRemoteTicket(OpenPlayItem opi, TicketOrder order, Long userid);
	
	ErrorCode releasePaidFailureOrderSeat(OpenPlayItem opi, TicketOrder order, List<SellSeat> seatList);
	ErrorCode<Boolean> unlockRemoteSeat(TicketOrder order, List<SellSeat> seatList);
	ErrorCode<TicketRemoteOrder> setAndFixRemoteOrder(OpenPlayItem opi, TicketOrder order, List<SellSeat> seatList) throws OrderException;

	//void removeLockSeatFromQryResponse(Long mpid, List<SellSeat> seatList);
	/**
	 * �ֲ�����������λ��QryResponse��
	 * @param order
	 * @param seatList
	 */
	
	void addLockSeatToQryResponse(Long mpid, List<SellSeat> seatList);
	/**
	 * ֻ�ǲ�ѯ����������������
	 * @param order
	 * @return
	 */
	ErrorCode<TicketRemoteOrder> getRemoteOrder(TicketOrder order, boolean forceRefresh);
	/**
	 * ��鶩��״̬��״̬�б仯�ᱣ�����״̬
	 * @param order
	 * @param forceRefresh
	 * @return
	 */
	ErrorCode<TicketRemoteOrder> checkRemoteOrder(TicketOrder order);
	
	/**
	 * ���óɱ���
	 * @param mpi
	 * @param costprice
	 * @return
	 */
	ErrorCode updateCostPrice(String seqNo, Integer costprice);
	
	/**
	 * ȷ��Զ����Ʊ
	 * @param user
	 * @param order
	 * @param opi
	 * @return
	 */
	ErrorCode backRemoteOrder(User user, TicketOrder order, OpenPlayItem opi);
	
	/**
	 * ͨ������ʱ���ȡ��Ƭ
	 * @param cinemaid
	 * @param msgList
	 * @param notUpdateWithMin �������ڸ��¹��������£�
	 */
	ErrorCode updateMoviePlayItem(UpdateMpiContainer container, Long cinemaid, List<String> msgList, int notUpdateWithMin);
	
	/**
	 * ����ץȡ��������Ƭ
	 * @param cinemaid
	 * @param playdate
	 * @param msgList
	 */
	void updateMoviePlayItem(Long cinemaid, Date playdate, List<String> msgList);
	
	/**
	 * Ԥ�����ȵ㳡����λ
	 */
	void preloadHotspotPmiCache();
}
