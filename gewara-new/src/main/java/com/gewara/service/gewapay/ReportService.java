package com.gewara.service.gewapay;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.model.movie.Cinema;
import com.gewara.model.pay.CinemaSettle;
import com.gewara.model.pay.GoodsOrder;

public interface ReportService{
	//���ݷ�ӳʱ���ѯ
	List<Map> getTicketOrderDataByPlaytime(Long cinemaId,Long movieId, Timestamp timefrom, Timestamp timeto, String opentype);
	//�����µ�ʱ���ѯ
	List<Map> getTicketOrderDataByAddtime(Long cinemaId,Long movieId, Timestamp timefrom, Timestamp timeto, String opentype);
	List<CinemaSettle> getLastSettleList();
	List<Cinema> getBookingCinemaList();
	CinemaSettle getLastSettle(Long cinemaid);
	/**
	 * @param cinemaid
	 * @param timefrom
	 * @param timeto
	 * @param lasttime
	 * @param curtime
	 * @return Map(lastRefundList,curRefundList,orderMap,curRefundQuantity,lastRefundQuantity);
	 */
	Map getRefundData(Long cinemaid, Timestamp timefrom/*���ν�������*/, Timestamp timeto, Timestamp lasttime/*�ϴν���ʱ��*/, Timestamp curtime/*���ν���ʱ��*/);
	/**
	 * @param cinemaId
	 * @param timefrom
	 * @param timeto
	 * @param movieid
	 * @param opentype
	 * @return Map(cinemaid, totalcount, totalcost, totalquantity,mpicount)
	 */
	Map getCinemaSummaryByPlaytime(Long cinemaId, Timestamp timefrom, Timestamp timeto, Long movieid, String opentype);
	Map getCinemaSummaryByAddtime(Long cinemaId, Timestamp timefrom, Timestamp timeto, Long movieid, String opentype);
	List<Map> getCinemaSummaryByPlaytime(List<Long> cinemaIds, Timestamp timefrom, Timestamp timeto, Long movieid, String opentype);
	List<Map> getCinemaSummaryByAddtime(List<Long> cinemaIds, Timestamp timefrom, Timestamp timeto, Long movieid, String opentype);
	/**
	 * ��������ʱ��ͳ�ƣ�δ���ѵĲ�����
	 * @param cinemaId
	 * @param timefrom
	 * @param timeto
	 * @return
	 */
	List<GoodsOrder> getCinemaGoodsOrderByTaketime(Long cinemaId, Timestamp timefrom, Timestamp timeto);
	/**
	 * �����µ�ʱ��ͳ��
	 * @param cinemaId
	 * @param timefrom
	 * @param timeto
	 * @param isTake //true Ϊ��ֻȡ��Ʊ���ײ�
	 * @return
	 */
	List<GoodsOrder> getCinemaGoodsOrderByAddtime(Long cinemaId, Timestamp timefrom, Timestamp timeto,boolean isTake);
	/**
	 * �����µ�ʱ��ͳ��
	 * @param cinemaid
	 * @param timefrom
	 * @param timeto
	 * @param isTake //true Ϊ��ֻȡ��Ʊ���ײ�
	 * @return Map(cinemaid, totalcount, totalcost, totalquantity)
	 */
	Map getGoodsSummaryByAddtime(Long cinemaid, Timestamp timefrom, Timestamp timeto,boolean isTake);
	/**
	 * ��������ʱ��ͳ�ƣ�δ���ѵĲ�����
	 * @param cinemaid
	 * @param timefrom
	 * @param timeto
	 * @return
	 */
	Map getGoodsSummaryByTaketime(Long cinemaid, Timestamp timefrom, Timestamp timeto);
	
	Map getRefundOrderData(Long cinemaId,Long movieId, Timestamp timefrom, Timestamp timeto, String timeType);
}
