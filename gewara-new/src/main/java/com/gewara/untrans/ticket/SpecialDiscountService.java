package com.gewara.untrans.ticket;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.gewara.helper.discount.SportSpecialDiscountHelper.OrderCallback;
import com.gewara.helper.order.OrderContainer;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.TicketOrder;
import com.gewara.support.ErrorCode;

/**
 * ������¼����ֹ�������
 * 
 * @author gebiao(ge.biao@gewara.com)
 * @since Feb 21, 2013 8:53:47 PM
 */
public interface SpecialDiscountService {
	/**
	 * @param ordertype
	 * @param orderid
	 * @param spid
	 * @return
	 */
	ErrorCode<OrderContainer> useSpecialDiscount(String ordertype, Long orderid, SpecialDiscount sd, String clientIP);
	ErrorCode<OrderContainer> useSpecialDiscountBySpCodePass(String ordertype, Long orderid, String clientIP, Long memberid, String spcodePass);
	ErrorCode<OrderContainer> useSpecialDiscountBySpCodeId(String ordertype, Long orderid, String clientIP, Long memberid, Long spcodeId);
	ErrorCode<OrderContainer> useSpecialDiscount(String ordertype, Long orderid, SpecialDiscount sd, OrderCallback callback, String clientIP);

	void updateSpdiscountPaidCount(GewaOrder order, SpecialDiscount sd) throws Throwable;

	/**
	 * �ؼۻ����չʾ��̨
	 * 
	 * @param discountIdList
	 *            �ؼۻID
	 * @param fromTime
	 *            ��ѯ��ʼʱ��
	 * @param endTime
	 *            ��ѯ����ʱ��
	 * @return
	 */
	<T extends GewaOrder> List<T> getOrderListByDiscountIds(Class<T> clazz, List<Long> discountIdList, Date fromTime, Date endTime, int from, int maxnum);
	<T extends GewaOrder> Integer getOrderCountByDiscountIds(Class<T> clazz, List<Long> discountIdList, Date fromTime, Date endTime);
	
	List<TicketOrder> specialDiscountOrderList(Timestamp starttime, Timestamp endtime, Long sid, String citycode, String status);

}
