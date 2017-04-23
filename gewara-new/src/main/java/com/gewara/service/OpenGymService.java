package com.gewara.service;

import java.util.Date;
import java.util.List;

import com.gewara.command.SearchOrderCommand;
import com.gewara.helper.discount.SportSpecialDiscountHelper.OrderCallback;
import com.gewara.helper.order.OrderContainer;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.GymOrder;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.user.Member;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.gym.CardItem;
public interface OpenGymService {
	
	/**
	 * 
	 * @param gymCardItem
	 * @param quantity
	 * @param mobile
	 * @param speciallist
	 * @param startdate
	 * @param member
	 * @return
	 */
	ErrorCode<GymOrder> addGymOrder(CardItem cardItem, Integer quantity, String mobile, String speciallist, Date startdate, Member member, String origin);

	/**
	 * 
	 * @param memberid
	 * @param ukey
	 * @return
	 */
	GymOrder getLastPaidFailureOrder(Long memberid, String ukey);
	
	/**
	 * 
	 * @param memberid
	 * @param ukey
	 * @param gci
	 * @return
	 */
	GymOrder getLastUnpaidGymOrder(Long memberid, String ukey, Long gci);
	
	
	OrderContainer processOrderPay(GymOrder order) throws OrderException;
	ErrorCode processGymOrder(GymOrder order);
	/**
	 * 
	 * @param order
	 * @return
	 */
	ErrorCode<GymOrder> checkOrderCard(GymOrder order);

	
	/**
	 * ͨ�������š��û�IDȡ������
	 * @param tradeNo
	 * @param memberid
	 * @param reason
	 * @return
	 */
	ErrorCode cancelGymOrder(String tradeNo, Long memberid, String status, String reason);
	
	
	/**
	 * ʹ�û�������
	 * @param orderId		����ID
	 * @param item			��������
	 * @param memberId		�û�ID
	 * @param usePoint		������
	 * @return
	 */
	ErrorCode usePoint(Long orderId, CardItem item, Long memberId, int usePoint);
	
	/**
	 * ʹ�öһ�ȯ����
	 * @param orderId		����ID
	 * @param item			��������	
	 * @param card			�һ�ȯ����
	 * @param memberid		�û�ID
	 * @return
	 */
	ErrorCode<GymOrder> useElecCard(Long orderId, CardItem item, ElecCard card, Long memberid);
	
	/**
	 * ʹ���ؼۻ
	 * @param orderId		��������
	 * @param jsonValue 
	 * @param jsonKey 
	 * @param sd			�ؼۻ����
	 * @return
	 */
	ErrorCode<OrderContainer> useSpecialDiscount(Long orderId, SpecialDiscount sd, OrderCallback callback);
	
	/**
	 * ��ѯ������Ϣ
	 * @param soc		
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<GymOrder> getGymOrderList(SearchOrderCommand soc, int from, int maxnum);
}
