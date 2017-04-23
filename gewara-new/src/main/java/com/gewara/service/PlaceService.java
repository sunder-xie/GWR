package com.gewara.service;

import java.util.List;
import java.util.Map;

import com.gewara.model.common.BaseInfo;
import com.gewara.model.common.City;
import com.gewara.model.common.County;
import com.gewara.model.common.Indexarea;
import com.gewara.model.common.Line2Station;
import com.gewara.model.common.Province;
import com.gewara.model.common.Subwayline;
import com.gewara.model.common.Subwaystation;

/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public interface PlaceService{
	List<Subwayline> getSubwaylinesByCityCode(String citycode);
	List<Subwaystation> getSubwaystationsByCityCode(String citycode);
	List<Subwaystation> getSubwaystationsByLineId(Long lineId);
	Integer getSubwaystationCount(String stationname);
	List<Subwaystation> getSubwaystationList(String stationname, int from, int maxnum);
	Subwaystation getSubwaystation(String stationname);
	List<Line2Station> getLine2StationByLineId(Long lineId);
	Subwayline getSubwaylineByCitycodeAndName(String citycode, String linename);
	List<Province> getAllProvinces();
	/**
	 * @return Map<CountyCode, CountyName>
	 */
	Map<String,String> getCountyPairByCityCode(String cityCode);
	
	List<City> getCityByProvinceCode(String provinceCode);
	List<County> getCountyByCityCode(String cityCode);
	List<Indexarea> getIndexareaByCountyCode(String countyCode);
	<S extends BaseInfo> List<S> getPlaceList(String citycode, Class<S> clazz, String orderField, boolean asc, int from, int maxnum);
	<T extends BaseInfo> T getZbPlace(Class<T> clazz, String countycode, String indexareacode);
	
	<T extends BaseInfo> List<T> getPlaceListByIndexareaCode(Class<T> clazz, String indexareacode, String orderField, boolean asc);
	<T extends BaseInfo> List<T> getPlaceListByIndexareaCode(Class<T> clazz, String indexareacode, String orderField, boolean asc, int from, int maxrow);
	<T extends BaseInfo> List<T> getPlaceListByCountyCode(Class<T> clazz, String countycode, String orderField, boolean asc);
	<T extends BaseInfo> List<T> getPlaceListByCountyCode(Class<T> clazz, String countycode, String orderField, boolean asc, int from, int maxrow);

	<T extends BaseInfo> Integer getPlaceCount(Class<T> clazz, String citycode);
	<T extends BaseInfo> Integer getPlaceCountByCountyCode(Class<T> clazz, String countycode);
	<T extends BaseInfo> Integer getPlaceCountByIndexareaCode(Class<T> clazz, String indexareacode);
	
	<T extends BaseInfo> void updateHotValue(Class<T> clazz, Long placeId, Integer hotvalue);
	<T extends BaseInfo> List<T> getPlaceListByHotvalue(String citycode, Class<T> clazz, int from,int maxnum);
	<T extends BaseInfo> List<T> getPlaceListByHotvalue(String citycode, Class<T> clazz, int hotvalue, int from,int maxnum);
	
	<T extends BaseInfo> List<T> searchPlaceByName(String citycode, Class<T> clazz, String name);
	<T extends BaseInfo> T getPlaceByName(String citycode, Class<T> clazz, String name);
	<T extends BaseInfo> List<Map> getPlaceCountyCountMap(Class<T> clazz, String citycode);
	<T extends BaseInfo> List<Map> getPlaceIndexareaCountMap(Class<T> clazz, String countycode);
	/**
	 * ����tag��countycode��indexarea��ȡ�����б�
	 * @param tag
	 * @param countycode
	 * @param indexareacode
	 * @return
	 */
	List<BaseInfo> getPlaceListByTag(String tag, String countycode, String indexareacode);
	List<BaseInfo> getPlaceListByTag(String tag, String countycode, String indexareacode, int from, int maxnum);
	
	List<Map<String, Object>> getPlaceGroupMapByCitySubwayline(String citycode, String tag);
	String getIndexareaname(String  indexareacode);
	List<Indexarea> getUsefulIndexareaList(String countycode,String tag);
	Map<String, String> getSubwaylineMap(String citycode);
	
	/**
	 *  ȡ�����ڳ���, ������������
	 * */
	public String getLocationPair(Long memberid, String joinChar);
	
	/**
	 *  ��ʶ + code, ����Map<String, String>
	 *  eg. getPlaceMapBycode("citycode", "310000") => "�Ϻ�"
	 */
	String getPlaceNameBycode(String tag, String code);
	List<Map> getSubwaystationList(String citycode, String tag, Long lineid);
	String getCountyname(String countycode);
	/**
	 * ͨ�������������������ƣ���ƣ����б���
	 * @param county		�������
	 */
	void updateCounty(County county);
	
	/**
	 * ͨ����·ID������·���ƣ�˵�������б���
	 * @param lid			��·ID
	 * @param citycode		���б���
	 * @param linename		��·����
	 * @param remark		��·˵��
	 */
	void updateSubwayline(Long lid, String citycode, String linename, String remark);
	
	/**
	 * ͨ��վ��ID����վ������
	 * @param sid				վ��ID
	 * @param stationname		վ������
	 */
	void updateSubwaystation(Long sid, String stationname);
	/**
	 * ͨ����·��վ�����ID������·ID��վ��ID
	 * @param recordid		��·��վ�����ID
	 * @param lid			��·ID
	 * @param sid			վ��ID
	 */
	void updateLine2Station(Long recordid, Long lid, Long sid);
	
	/**
	 * ͨ����·��վ�����ID���������ֶ�
	 * @param recordid			��·��վ�����ID
	 * @param stationorder		�����ֶ�
	 */
	void updateLine2StationOrder(Long recordid, Integer stationorder);
	/**
	 * ������·վ��� ��ĩ�೵ʱ��
	 * @param otherinfo
	 * @param lineId
	 * @param stationId
	 */
	void updateLine2StationOtherinfo(Long recordid, String otherinfo);
	Line2Station getLine2StationListByLineIdAndStationId(Long lineId, Long stationId);
}
