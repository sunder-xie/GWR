package com.gewara.service.ticket;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gewara.model.movie.CityPrice;
import com.gewara.model.movie.MoviePrice;
import com.gewara.model.movie.MovieTierPrice;
import com.gewara.support.ErrorCode;

public interface MoviePriceService {
	List<String> PRICE_TIER = Arrays.asList("A", "B", "C", "D", "E", "F");
	/**
	 * ͨ����ӰID��ѯ�۸��������
	 * @param movieid ��ӰID
	 * @return ��Ӱ�������ļ۸�
	 */
	List<MovieTierPrice> getMovieTierPriceList(Long movieid);
	/**
	 * ͨ����ӰID���ϲ�ѯ�۸��������
	 * @param movieid ��ӰID����
	 * @return ��Ӱ�������ļ۸�
	 */
	List<MovieTierPrice> getMovieTierPriceList(Long... movieid);
	
	/**
	 * ͨ����ӰID������ѯ�۸�
	 * @param movieid	��ӰID
	 * @param type	�۸����
	 * @return	��Ӱ���ļ۸�
	 */
	MovieTierPrice getMovieTierPrice(Long movieid, String type);
	
	/**
	 * ͨ����ӰID��۸�������ӻ��޸ļ۸�
	 * @param movieid	��ӰID
	 * @param type	�۸����
	 * @param price �۸�
	 * @return ��Ӱ���ļ۸� ���籨�����ش�����Ϣ��
	 */
	ErrorCode<MovieTierPrice> saveOrUpdateMovieTierPrice(Long movieid, String type, Integer price, Integer edition3D,Integer editionJumu,
			Integer editionIMAX,Integer rangeEdition3D,Integer rangePrice,Integer rangeEditionJumu,Integer rangeEditionIMAX,
			Timestamp startTime,Timestamp endTime);
	
	/**
	 *	���ݳ��л�ȡ��ǰ�۸�ͬ�ĵ�Ӱ��ͼ۸�
	 *	@param ���д���
	 * @return ��Ӱ��ͼ۸�
	 */
	List<MoviePrice> getDiffMoviePriceList(String citycode);
	CityPrice getCityPrice(Long relatedid, String citycode, String tag);
	/**
	 * �õ�PlacePrice�����ݼ۸�
	 * @param tag
	 * @param relatedid
	 * @param category
	 * @param categoryid
	 * @return
	 */
	Map getPlacePriceFromCache(String tag, Long relatedid, String category, Long categoryid);
	/**
	 * @param tag
	 * @param relatedid
	 * @return minprice, maxprice
	 */
	Map getMinMaxPlacePrice(String tag, Long relatedid);
	/**
	 * ���³�������
	 * @param tag
	 * @param relatedid
	 * @param category
	 * @param categoryid
	 * @param avgprice
	 * @param minprice
	 * @param maxprice
	 */
	void saveOrUpdatePlacePrice(String tag, Long relatedid, String category, Long categoryid, Integer avgprice, Integer minprice, Integer maxprice);
	/**
	 * ͨ����γ�Ȼ�ȡһ����Χ�ڵĳ�������
	 * @param clazz ���ݶ���
	 * @param citycode ���д���
	 * @param countycode �����������
	 * @param bpointx ����
	 * @param bpointy γ��
	 * @param spaceRound ��Χ
	 * @return �������ݼ���
	 */
	
}
