package com.gewara.untrans;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.model.BaseObject;
import com.gewara.model.bbs.Correction;
import com.gewara.model.bbs.commu.CommuTopic;
import com.gewara.model.common.BaseInfo;
import com.gewara.model.common.DataDictionary;
import com.gewara.model.common.Place;
import com.gewara.model.common.RelateToCity;
import com.gewara.model.common.Relationship;
import com.gewara.model.content.Bulletin;
import com.gewara.model.content.DiscountInfo;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.content.HeadInfo;
import com.gewara.model.content.Link;
import com.gewara.model.content.PhoneAdvertisement;
import com.gewara.model.movie.GrabTicketSubject;
import com.gewara.model.movie.TempMovie;
import com.gewara.util.RelatedHelper;

/**
 * Service for News��DiscountInfo��Bulletin
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public interface CommonService {
	//1. ����
	List<Bulletin> getCurrentBulletinsByRelatedidAndHotvalue(String citycode, Long relatedid, Integer hotvalue);
	List<Bulletin> getBulletinListByTag(String citycode, String tag);
	List<Bulletin> getBulletinListByTagAndTypeAndRelatedid(String citycode, String tag,String type,boolean isCommend,Long relatedid);
	//2. �ۿ�
	List<DiscountInfo> getCurrentDiscountInfoByRelatedid(String tag,Long relatedid);
	List<DiscountInfo> getDiscountInfoByRelatedidAndTag(Long relatedid, String tag);
	/**
	 * ��sportid��ѯ�Ż���Ϣ����
	 * */
	Integer getDiscountInfoCount(Long sportid,String tag);
	void updateBulletinHotValue(Long id,Integer value);
	List<Bulletin> getBulletinListByHotvalue(String citycode, String tag, Integer hotvalue);
	
	List<Correction> getCorrectionList(String status,Timestamp starttime,Timestamp endtime,int from,int maxnum);
	Integer getCorrectionCount(String status,Timestamp starttime,Timestamp endtime);
	List<TempMovie> getTempMovieList(String tag,String status,Timestamp starttime,Timestamp endtime,String type,Integer point,int from,int maxnum);
	Integer getTempMovieCount(String tag,String status,Timestamp starttime,Timestamp endtime);
	List<Place> getPlaceList(String status,Timestamp starttime,Timestamp endtime,int from,int maxnum);
	Integer getPlaceCount(String status,Timestamp starttime,Timestamp endtime);
	/**
	 * ����tag��relatedid���³����Żݾ���
	 * @param tag
	 * @param relatedid
	 */
	void updateCoupon(String citycode, String tag, Long relatedid);
	
	List<Link> getLinkListByType(String type);
	
	void initGewaCommendList(String group, RelatedHelper rh, List<GewaCommend> gcList);
	GewaCommend getGewaCommendByRelatedid(String signname, Long relateid);
	List<GewaCommend> getGewaCommendListByParentid(String signname, Long parentid,boolean isAll);
	Integer getGewaCommendCount(String citycode, String signname, Long parentid, String tag, boolean isGtZero);
	Integer getGewaCommendCount(String citycode, List<String> signNameList, Long parentid, String tag, boolean isGtZero);
	
	List<GewaCommend> getGewaCommendList(Long parentid,  String signname, List tag, boolean isGtZero, int first, int maxnum);
	List<GewaCommend> getGewaCommendList(String citycode, String signname, Long parentid,String tag, boolean isGtZero, int first, int maxnum);
	List<GewaCommend> getGewaCommendList(String citycode, String signname, Long parentid, String tag, boolean isGtZero, String order, boolean asc, int from, int maxnum);
	List<GewaCommend> getGewaCommendList(String citycode,String countycode, String signname, Long parentid, String tag, boolean isGtZero, boolean isActivity, int first, int maxnum);
	List<GewaCommend> getGewaCommendList(String citycode, String signname, Long parentid, boolean isGtZero, boolean isdesc,int from, int maxnum);
	List<GewaCommend> getGewaCommendList(String citycode, List<String> signNameList, Long parentid, boolean isGtZero, boolean isdesc, boolean isActivity,int from, int maxnum);
	List<GewaCommend> getGewaCommendList(String citycode, String countycode, String signname, Long parentid, String tag, boolean isGtZero,boolean isActivity,boolean isStarttime, int first, int maxnum);
	/**
	 * ��ѯ��վͷ����Ϣ�б�
	 */
	List<HeadInfo> getHeadInfoList(String board, String citycode, int from,int maxNum);
	Integer getHeadInfoCount(String board);
	/**
	 * ��ѯȦ�ӻ������б�
	 */
	List<CommuTopic> getCommuTopicList(Long commuid,int from,int maxnum);
	/**
	 * ��ѯȦ�ӻ���������
	 * @param commuid
	 * @return
	 */
	Integer getCommuTopicCount(Long commuid);
	
	/**
	 * ����relatedid, tag,��ѯ�Ƽ���������
	 * @param relatedid
	 * @param tag
	 * @param signname
	 * @param isGtZero
	 * @return
	 */
	List<GewaCommend> getGewaCommendListByid(Long relatedid, String tag, String signname, boolean isGtZero);
	
	/****
	 * 
	 * ���� / � ���, �����Ӧ�Ĺ�����������. 
	 */
	<T extends BaseObject> Map<Long, String> initRelateCityName(List<T> list);
	<T extends BaseObject> Map<Long, List<Map>>  initRelateToCityName(List<T> list, String tag);
	
	/***
	 *  ��վ���ݷ��� - 
	 * */
	List<GewaCommend> getCommendListByRelatedid(Long relatedid, String signname, String tag);
	
	Integer getRelationshipCount(String category,  Long relatedid1, String tag, Long relatedid2, Timestamp validtime);
	List<Relationship> getRelationshipList(String category,  Long relatedid1, String tag, Long relatedid2, Timestamp validtime, int from, int maxnum);
	Relationship getRelationship(String category, String tag, Long relatedid2, Timestamp validtime);
	List<RelateToCity> getRelateToCity(String tag, Long relatedid, String citycode, String flag);
	List<GewaCommend> getGewaCommendListByRelatedid(String citycode, String signname, Long relatedid, String tag, boolean isGtZero, int first, int maxnum);
	
	List<GrabTicketSubject> getGrabTicketSubjectList(String citycode, String tag, int from, int maxnum);
	/**
	 * key=relatedid+tag value=activityCount
	 */
	Map<String, Integer> getActivityCount();
	/**
	 * key=relatedid+tag value=newsCount
	 */
	Map<String, Integer> getNewsCount();
	/**
	 * key=relatedid+tag value=pictureCount
	 */
	Map<String, Integer> getPictureCount();
	/**
	 * key=relatedid+tag value=videoCount
	 */
	Map<String, Integer> getVideoCount();
	/**
	 * key=relatedid+tag value=commentCount
	 */
	Map<String, Integer> getCommentCount();
	/**
	 * key=relatedid+tag value=diaryCount
	 */
	Map<String, Integer> getDiaryCount();
	/**
	 * key=relatedid+tag value=commentCount
	 */
	Map<String, Integer> getCommuCount();
	/**
	 * key=sportitemid  value=sportCount
	 */
	Map<String, Integer> getSportItemSportCount();
	
	/**
	 * ��ȡδɾ���Ĺ����Ϣ
	 */
	List<PhoneAdvertisement> getNewPhoneAdvertisementList(String status);
	
	/**
	 * ��ȡ��ǰ�����û���������
	 * @return map()
	 */
	Map getCurIndexDataSheet();
	/**
	 * ͨ���������Ʋ�ѯ�����ֵ���������objectNameΪ�ջ���ַ��������ѯ����
	 * @param objectName ��������
	 * @return �����ֵ����� ���ѯ������Ϊ 0
	 */
	Integer getDataDictionaryCount(String objectName);
	/**
	 *	ͨ���������Ʋ�ѯ�����ֵ䣬��objectNameΪ�ջ���ַ��������ѯ����
	 * @param objectName ��������
	 * @param from ��ʼ��ѯ��
	 * @param maxnum ��ѯ����
	 * @return �����ֵ伯�� ���ѯ������ ����sizeΪ0 
	 */
	List<DataDictionary> getDataDictionaryList(String objectName, int from, int maxnum);
	
	<T extends BaseInfo> List<T> getBaiDuNearPlaceObjectList(Class<T> clazz, String citycode, String countycode, String bpointx, String bpointy, double spaceRound);
}
