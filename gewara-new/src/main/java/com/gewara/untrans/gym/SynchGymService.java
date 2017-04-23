package com.gewara.untrans.gym;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.gewara.model.pay.GymOrder;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.common.BrandnameCount;
import com.gewara.xmlbind.gym.BookingRecord;
import com.gewara.xmlbind.gym.CardItem;
import com.gewara.xmlbind.gym.RemoteCoach;
import com.gewara.xmlbind.gym.RemoteCourse;
import com.gewara.xmlbind.gym.RemoteGym;
import com.gewara.xmlbind.gym.RemoteSpecialCourse;

public interface SynchGymService {
	/**
	 * ������Ż�ȡ������ϸ
	 * @param cid			�������
	 * @param cache	
	 * @return	
	 */
	ErrorCode<CardItem> getGymCardItem(Serializable cid, boolean cache);

	/**
	 * ������Ż�ȡ��������ϸ
	 * @param cid			�������
	 * @param speciallist	��ѡ�γ̱�ż��ϣ���,����
	 * @return
	 */
	ErrorCode<CardItem> showBuyCardItem(Serializable cid, String speciallist, boolean cache);

	/**
	 * ͨ��ID��ȡ��������Ϣ
	 * @param gymId		����ID
	 * @param cache		
	 * @return
	 */
	ErrorCode<RemoteGym> getRemoteGym(Serializable gymId, boolean cache);

	/**
	 * ȷ�϶�����Ϣ
	 * @param order
	 * @return
	 */
	ErrorCode<String> lockCard(GymOrder order);
	
	/**
	 * ͨ������ID���ϻ�ȡ������Ϣ
	 * @param idList	����ID������Ϣ
	 * @return
	 */
	ErrorCode<List<RemoteGym>> getRemoteGymIdList(List<Long> idList);
	
	/**
	 * ͨ������ID��ѯ������Ϣ
	 * @param coachId	����ID
	 * @return
	 */
	ErrorCode<List<RemoteGym>> getGymListByCoachId(Long coachId);
	
	/**
	 * ͨ������ID��ѯ������Ϣ
	 * @param coachId	����ID
	 * @return
	 */
	ErrorCode<List<RemoteGym>> getGymListByCoachId(Long coachId, String order, boolean asc, int from, int maxnum);
	
	/**
	 * ͨ������ID��ѯ��������
	 * @param coachId	����ID
	 * @return
	 */
	ErrorCode<Integer> getGymCountByCoachId(Long coachId);
	
	/**
	 * ͨ����ĿID��ѯ������Ϣ
	 * @param courseId	�γ���ĿID
	 * @return
	 */
	ErrorCode<List<RemoteGym>> getGymListByCourseId(Long courseId);
	
	/**
	 * ͨ����ĿID��ѯ������Ϣ
	 * @param courseId	�γ���ĿID
	 * @return
	 */
	ErrorCode<List<RemoteGym>> getGymListByCourseId(Long courseId, String order, boolean asc, int from, int maxnum);
	
	/**
	 * ͨ����ĿID��ѯ��������
	 * @param courseId	��ĿID
	 * @return
	 */
	ErrorCode<Integer> getGymCountByCourseId(Long courseId);
	
	/**
	 * ���ݳ��С�������Ȧ�ȱ��룬��ѯ������Ϣ
	 * @param citycode			���б���
	 * @param countycode		�������
	 * @param indexareacode		��Ȧ����
	 * @param order				�����ֶ�
	 * @param from				�ӵڼ��п�ʼ
	 * @param maxnum			����ѯֵ
	 * @return
	 */
	ErrorCode<List<RemoteGym>> getGymList(String citycode, String countycode, String indexareacode, String order, boolean asc, int from, int maxnum);
	/**
	 * ���ݳ��С�������Ȧ�ȱ��룬��ѯ��������
	 * @param citycode			���б���
	 * @param countycode		�������
	 * @param indexareacode		��Ȧ����
	 * @return
	 */
	ErrorCode<Integer> getGymCount(String citycode, String countycode, String indexareacode);
	/**
	 * ͨ����ĿID��ѯ��Ŀ��Ϣ
	 * @param courseId		��ĿID
	 * @param cache		
	 * @return
	 */
	ErrorCode<RemoteCourse> getRemoteCourse(Serializable courseId, boolean cache);
	
	/**
	 * ͨ����ĿID���ϻ�ȡ��Ŀ��Ϣ
	 * @param idList	��ĿID������Ϣ
	 * @return
	 */
	ErrorCode<List<RemoteCourse>> getRemoteCourseIdList(List<Long> idList);
	
	/**
	 * ��ѯ�γ���Ϣ��Ĭ�ϵ������������
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteCourse>> getHotCourseList(int from, int maxnum);
	/**
	 * ��ѯ�γ���Ϣ
	 * @param order				�����ֶ�
	 * @param asc				�Ƿ�����
	 * @param from				
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteCourse>> getCourseListByOrder(String order, boolean asc, int from, int maxnum);
	
	/**
	 * ��ѯ�γ�����
	 * @return
	 */
	ErrorCode<Integer> getCourseCount();
	
	/**
	 * ��ȡ����Ŀ��Ϣ
	 * @param courseId 	��ĿID
	 * @return
	 */
	ErrorCode<List<RemoteCourse>> getSubCourseListById(Serializable courseId);
	
	/**
	 * ͨ���γ���Ϣ��ѯ�γ���Ϣ
	 * @param specialCourseId
	 * @param cache
	 * @return
	 */
	ErrorCode<RemoteSpecialCourse> getSpecialCourse(Serializable specialCourseId, boolean cache);
	
	/**
	 * ͨ���γ�ID��Ϣ��ѯ�γ�����
	 * @param idList		�γ�ID������Ϣ
	 * @return
	 */
	ErrorCode<List<RemoteSpecialCourse>> getSpecialCourseIdList(List<Long> idList);
	
	/**
	 * ͨ������ID��ѯ�γ���Ϣ��Ĭ�ϵ��������
	 * @param gymId
	 * @return
	 */
	ErrorCode<List<RemoteSpecialCourse>> getSpecialCourseListByGymId(Long gymId);
	
	/**
	 * ͨ������ID��ѯ�γ���Ϣ
	 * @param gymId			����ID
	 * @param order			�����ֶ�
	 * @param asc			�Ƿ�����
	 * @param from			
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteSpecialCourse>> getSpecialCourseListByGymId(Long gymId, String order, boolean asc, int from, int maxnum);
	
	/**
	 * ͨ������ID��ѯ�γ�����
	 * @param gymId			����ID
	 * @return
	 */
	ErrorCode<Integer> getSpecialCourseCountByGymId(Long gymId);
	
	/**
	 * ͨ������ID��ѯ������Ϣ
	 * @param coachId		����ID
	 * @param cache		
	 * @return
	 */
	ErrorCode<RemoteCoach> getRemoteCoach(Serializable coachId, boolean cache);
	
	/**
	 * ͨ������ID���ϲ�ѯ������Ϣ
	 * @param idList		����ID
	 * @return
	 */
	ErrorCode<List<RemoteCoach>> getRemoteCoachIdList(List<Long> idList);
	/**
	 * ͨ������ID��ѯ������Ϣ
	 * @param gymId			����ID		
	 * @return
	 */
	ErrorCode<List<RemoteCoach>> getCoachListByGymId(Long gymId);
	
	/**
	 * ͨ������ID��ѯ������Ϣ
	 * @param gymId				����ID
	 * @param order				�����ֶ�
	 * @param asc				�Ƿ�����
	 * @param from				�ڼ������ݿ�ʼ
	 * @param maxnum			��ѯ�������ֵ
	 * @return
	 */
	ErrorCode<List<RemoteCoach>> getCoachListByGymId(Long gymId, String order, boolean asc, int from, int maxnum);
	
	/**
	 * ͨ������ID��ѯ��������
	 * @param gymId				����ID
	 * @return
	 */
	ErrorCode<Integer> getCoachCountByGymId(Long gymId);
	
	/**
	 * ����Ʒ�����Ʒ��飬��ѯƷ������
	 * @param citycode			���б���
	 * @param countycode		�������
	 * @param indexareacode		��Ȧ����
	 * @return
	 */
	ErrorCode<List<BrandnameCount>> getGroupGymByBrand(String citycode, String countycode, String indexareacode);
	
	/**
	 * ͨ������ID��ѯ��Ŀ��Ϣ
	 * @param coachId			����ID
	 * @return
	 */
	ErrorCode<List<RemoteCourse>> getCourseListByCoachId(Long coachId);
	
	/**
	 * ͨ������ID��ѯ��Ŀ��Ϣ
	 * @param coachId			����ID
	 * @param order				�����ֶ�
	 * @param asc				�Ƿ�����
	 * @param from				�ڼ������ݿ�ʼ
	 * @param maxnum			��ѯ�������ֵ
	 * @return
	 */
	ErrorCode<List<RemoteCourse>> getCourseListByCoachId(Long coachId, String order, boolean asc, int from, int maxnum);
	
	/**
	 * ͨ������ID��ѯ��Ŀ����
	 * @param coachId			����ID
	 * @return
	 */
	ErrorCode<Integer> getCourseCountByCoachId(Long coachId);
	
	/**
	 * ͨ������ID�������͡��۸������ѯ��������
	 * @param gymId				����ID
	 * @param itemType			������
	 * @param minprice			��С�۸�
	 * @param maxprice			���۸�
	 * @param order				�����ֶ�
	 * @param asc				�Ƿ�����
	 * @param from				�ڼ������ݿ�ʼ
	 * @param maxnum			��ѯ�������ֵ
	 * @return
	 */
	ErrorCode<List<CardItem>> getValidGymCardListByGymId(Long gymId, String itemType, Integer minprice, Integer maxprice, String order, boolean asc, int from, int maxnum);
	
	/**
	 * ͨ������ID�������͡��۸������ѯ��������
	 * @param gymId				����ID
	 * @param itemType			������
	 * @param minprice			��С�۸�
	 * @param maxprice			���۸�
	 * @return
	 */
	ErrorCode<Integer> getValidGymCardCountByGymId(Long gymId, String itemType, Integer minprice, Integer maxprice);
	
	/**
	 * ͨ��ԤԼID��ѯԤԼ��Ϣ
	 * @param recordId			ԤԼID
	 * @param cache			�Ƿ񻺴�
	 * @return
	 */
	ErrorCode<BookingRecord> getCourseBooking(Serializable recordId, boolean cache);
	
	/**
	 * ͨ��ԤԼΨһ��������������ֵ��ѯԤԼ��Ϣ
	 * @param keyName			��������
	 * @param keyValue			����ֵ
	 * @param cache			�Ƿ񻺴�
	 * @return
	 */
	ErrorCode<BookingRecord> getCourseBookingByUkey(String keyName, String keyValue, boolean cache);
	
	/**
	 * ͨ������ID��ѯԤԼ�γ̵���Ϣ
	 * @param gymId				����ID
	 * @param starttime			��ʼʱ���
	 * @param endtime			����ʱ���
	 * @param order				�����ֶ�
	 * @param asc				�Ƿ�����
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<BookingRecord>> getCourseBookingListByGymId(Long gymId, Timestamp starttime, Timestamp endtime, String order, boolean asc, int from, int maxnum);
	
	/**
	 * ͨ������ID��ѯԤԼ�γ̵�����
	 * @param gymId				����ID
	 * @param starttime			��ʼʱ���
	 * @param endtime			����ʱ���
	 * @return
	 */
	ErrorCode<Integer> getCourseBookingCountByGymId(Long gymId, Timestamp starttime, Timestamp endtime);
	
	/**
	 * ͨ����ĿID��������ֵ
	 * @param courseId			��ĿID
	 * @param fieldName			��������
	 * @param fieldValue		����ֵ
	 * @param isCover			�Ƿ񸲸�(���Integer���ͣ�false�ǼӼ�)
	 * @return
	 */
	ErrorCode<String> updateCourseByField(Serializable courseId, String fieldName, Serializable fieldValue, boolean isCover);
	
	
	/**
	 * ͨ������ID��������ֵ
	 * @param coachId			����ID
	 * @param fieldName			��������
	 * @param fieldValue		����ֵ
	 * @param isCover			�Ƿ񸲸�(���Integer���ͣ�false�ǼӼ�)
	 * @return
	 */
	ErrorCode<String> updateCoachByField(Serializable coachId, String fieldName, Serializable fieldValue, boolean isCover);

	/**
	 * ͨ������ID��������ֵ
	 * @param coachId			����ID
	 * @param fieldName			��������
	 * @param fieldValue		����ֵ
	 * @param isCover			�Ƿ񸲸�(���Integer���ͣ�false�ǼӼ�)
	 * @return
	 */
	ErrorCode<String> updateGymByField(Serializable gymId, String fieldName, Serializable fieldValue, boolean isCover);
	
	/**
	 * ͨ������������ID��������ֵ
	 * @param tag				����
	 * @param relatedid			����ID
	 * @param fieldName			��������
	 * @param fieldValue		����ֵ
	 * @param isCover			�Ƿ񸲸�(���Integer���ͣ�false�ǼӼ�)
	 * @return
	 */
	ErrorCode<String> updateRelatedByField(String tag, Serializable relatedid, String fieldName, Serializable fieldValue, boolean isCover);
}
