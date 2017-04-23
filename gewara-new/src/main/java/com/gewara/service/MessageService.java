package com.gewara.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.GymOrder;
import com.gewara.model.pay.OrderNote;
import com.gewara.model.pay.PubSaleOrder;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.SellSeat;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.ConfigTrigger;

public interface MessageService extends ConfigTrigger{
	/**
	 * ����Ҫ���͵���Ϣ��¼�������������Ҫ�������͵ģ��򷵻�
	 * @param order
	 * @return
	 */
	ErrorCode<List<SMSRecord>> addMessage(GewaOrder order);
	void addCommentMsg(TicketOrder order, OpenPlayItem opi);
	void addDramaMsg(DramaOrder order, OpenDramaItem odi);
	void addSportMsg(SportOrder order);
	String getCheckpassTemplate(OpenPlayItem opi);
	ErrorCode<String> getCheckpassMsg(String msgTemplate, TicketOrder order, List<SellSeat> seatList, OpenPlayItem opi);
	SMSRecord getSMSRecordByUkey(String tradeNo, String contact, String smstype);
	SMSRecord addManualMsg(Long relatedid, String mobile, String msg, String ukey);
	
	SMSRecord addOrderNoteSms(DramaOrder order, OrderNote orderNote, Timestamp cur);
	SMSRecord addOrderNoteSms(GoodsOrder order, OrderNote orderNote, Timestamp cur);
	SMSRecord addTrainingOrderSms(GoodsOrder order, OrderNote orderNote, Timestamp cur);
	/**
	 * ����һ�ζ�û�з��͵ļ�¼
	 * @param maxnum
	 * @return
	 */
	List<SMSRecord> getUnSendMessageList(int maxnum);
	List<SMSRecord> getFailureSMSList();
	SMSRecord getSMSRecordByOttidAndMobile(Long ottid,String mobile);
	/**
	 * ���Ҷ�Ϣδ���뷢�Ͷ��еĶ���
	 * @param ordertype
	 * @return
	 */
	List<GewaOrder> getUnSendOrderList();
	/**
	 * ���Ҷ���δ���뷢�Ͷ��еĻ��綩��
	 * @return
	 */
	List<DramaOrder> getUnSendDramaOrderList();
	/**
	 * ���Ҷ���δ���뷢�Ͷ��е��˶�����
	 * @return
	 */
	List<SportOrder> getUnSendSportOrderList();
	/**
	 * ���Ҷ���δ���뷢�Ͷ��еĽ�����
	 * @return
	 */
	List<GymOrder> getUnSendGymOrderList();
	void addUnSendMessage(GewaOrder order);
	String getMobileList(String type, List<Long> cinemaidList, Long movieid, Long relatedid, Timestamp fromtime, Timestamp totime);
	/**
	 * ���ݶ����ţ�smstype��ѯȡƱ����
	 */
	void removeSMSRecordByTradeNo(String tradeNo, String smstype);
	/**
	 * ����ǩ����Ʒ�Ķ���
	 * @param porder
	 * @param company
	 * @param sno
	 * @return
	 */
	ErrorCode<SMSRecord> addPostPubSaleMessage(PubSaleOrder porder, String company,
			String sno);
	/**
	 * �õ����ŷ���ȥ��
	 * @return
	 */
	String getSmsChannel(String mobile, String smstype);
	String getSmsChannel(String mobile, String smstype, Map<String, String> map);
	/**
	 * �����˶�������Ϣ
	 * @param order
	 * @return
	 */
	ErrorCode<List<SMSRecord>> addSportOrderMessage(GewaOrder order);
	
	/**
	 * ����ʱ�估����ID��ѯ��������
	 */
	int querySmsRecord(String tradeNo, String tag, Timestamp starttime, Timestamp endtime, Long relatedid, Long memberid);
	String getOrderPassword(TicketOrder ticketOrder, List<SellSeat> seatList);
	ErrorCode<List<SMSRecord>> addMemberCardOrderMsg(GewaOrder order);
	/**
	 * ȡ���������ζ�ʱ��Ϣ
	 * @param ticketOrder�����б�
	 */
	void updateSMSRecordStatus(List<String> orderList);

}
