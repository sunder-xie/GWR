package com.gewara.service.order;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.command.OrderParamsCommand;
import com.gewara.command.SearchOrderCommand;
import com.gewara.model.api.CooperUser;
import com.gewara.model.goods.BaseGoods;
import com.gewara.model.movie.Cinema;
import com.gewara.model.movie.Movie;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.TicketOrder;

public interface OrderQueryService{
	/**
	 *    @function �����û�ID + ʱ���޶� ��ѯ������¼, ������״̬, ����ҳ 
	 * 	@author bob.hu
	 *		@date	2011-04-26 11:04:04
	 */
	List<GewaOrder> getOrderListByMemberId(Long memberid, Integer days, int from, int maxnum);
	Integer getOrderCountByMemberId(Long memberid, Integer days);
	/**
	 * ��ȡ��ǰ������������
	 * @return
	 */
	int getPaidFailureOrderCount();
	String getMemberOrderHis(Long memberid);
	/**
	 * ��λ��������
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<TicketOrder> getPaidUnfixOrderList(int from, int maxnum);
	/**
	 * ��ѯ���ε����ж�����
	 * @param mpid
	 * @param status
	 * @return
	 */
	List<String> getTradeNoListByMpid(String orderType, Long mpid, String status);
	/**
	 * ĳ�û��ĵ�ǰ��Ч����(ȥ���ظ���)
	 * @param memberId
	 * @return
	 */
	<T extends GewaOrder> List<T> getOrderListByMemberId(Class<T> clazz, Long memberId, String status, int days, int from, int maxnum);
	/**
	 * �����û����δ֧���Ķ���
	 * @param memberid
	 * @return
	 */
	<T extends GewaOrder> T getLastUnpaidOrder(Long memberid);

	/**
	 * ��������
	 * @param soc
	 * @return
	 */
	List<TicketOrder> getTicketOrderList(SearchOrderCommand soc);
	
	/**
	 * ��ѯ���εĶ���
	 * @param mpid
	 * @param status
	 * @return
	 */
	List<TicketOrder> getTicketOrderListByMpid(Long mpid, String status);
	/**
	 * ��ȡĳ���εĶ�������
	 * @param mpid
	 * @return
	 */
	Integer getTicketOrderCountByMpid(Long mpid);
	/**
	 * ��ȡ�û�����Ʊ��ӰƬ
	 * @param memberid
	 * @param maxnum
	 * @return
	 */
	List<Movie> getMemberOrderMovieList(Long memberid,int maxnum);
	/**
	 * ��ȡ�û�����Ʊ��ӰԺ
	 * @param memberid
	 * @param maxnum
	 * @return
	 */
	List<Cinema> getMemberOrderCinemaList( Long memberid, int maxnum);
	Integer getMemberOrderCinemaCount(Long memberid);
	/**
	 * 
	 * @param memberid
	 * @param relatedid
	 */
	Integer getMemberOrderCountByMemberid(Long memberid, Long relatedid);
	Integer getMemberOrderCountByMemberid(Long memberid, Long relatedid, Timestamp fromtime, Timestamp totime, String citycode, String pricategory);
	
	List<GewaOrder> getPreferentialOrder(Long memberid,List<Long> spIdList);
	/**
	 * ���Ҷ���
	 * @param user
	 * @param soc
	 * @return
	 */
	List<TicketOrder> getTicketOrderList(CooperUser user, SearchOrderCommand soc);
	List<Map> getTicketOrderListByDate(CooperUser user, SearchOrderCommand soc);
	List<GewaOrder> getOrderOriginListByDate(CooperUser user,SearchOrderCommand soc);
	List<GewaOrder> getOrderAppsourceListByDate(CooperUser partner, Timestamp dateFrom, Timestamp dateTo, String appsource);
	
	/**
	 * ��ѯ��Ӱ��������
	 * @param memberid
	 * @param fromtime
	 * @param totime
	 * @param citycode
	 * @param status
	 */
	Integer getMemberTicketCountByMemberid(Long memberid, Timestamp fromtime, Timestamp totime, String status, String citycode);
	
	/**
	 * ��ѯ���Żݵ��˶���������		(û��ʹ�û��֡���ֵ������ȯ���Żݻ)
	 * @param memberid
	 * @param fromtime
	 * @param totime
	 * @param status
	 */
	Integer getNoPreferentialSportOrderCount(Long memberid, Timestamp fromtime, Timestamp totime, String status);
	
	List<GewaOrder> getTicketOrderListByPayMethod(Timestamp starttime,Timestamp endtime,String paymethod,String tradeNo);
	/**
	 * ��ȡָ��ӰƬ+ָ����Ա����>��֧��������
	 * @param memberids
	 * @param movieid
	 * @return
	 */
	Integer getMUOrderCountByMbrids(List<Long> memberids, Long movieid, Timestamp fromtime, Timestamp totime);

	/**
	 * ��ȡ������ʱʱ��
	 * @param tradeNo
	 * @return
	 */
	Long getOrderValidTime(String tradeNo);
	Long getOrderValidTimeById(Long orderid);
	/**
	 * ��ѯָ��ӰԺ��������
	 * @param cinemaId
	 * @param fromtime
	 * @param totime
	 * @param status
	 * @return
	 */
	Integer getTicketOrderCountByCinema(long cinemaId, Timestamp fromtime, Timestamp totime, String status);
	
	List<GewaOrder> getOrderList(OrderParamsCommand command, int from, int maxnum);
	Integer getOrderCount(OrderParamsCommand command);
	<T extends GewaOrder> List<T> getOrderList(Class<T> clazz, OrderParamsCommand command);
	<T extends GewaOrder> List<T> getOrderList(Class<T> clazz, OrderParamsCommand command, int from, int maxnum);
	<T extends GewaOrder> Integer getOrderCount(Class<T> clazz, OrderParamsCommand command);
	
	/**
	 * ��ѯ��Ʒ����
	 * @param clazz
	 * @param command
	 * @param from
	 * @param maxnum
	 * @return
	 */
	<T extends BaseGoods> List<GoodsOrder> getGoodsOrderList(Class<T> clazz, OrderParamsCommand command, int from, int maxnum);
	
	/**
	 * ��ѯӰƱ����
	 * @param clazz
	 * @param command
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<TicketOrder> getTicketOrderList(OrderParamsCommand command, String place, String item, int from, int maxnum);
	/**
	 * ��ȡ�û��ĵ�һ������
	 * @param memberid
	 * @return
	 */
	TicketOrder getFirstTicketOrder(long memberid);

}
