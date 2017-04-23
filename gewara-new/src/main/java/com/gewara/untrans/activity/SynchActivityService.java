package com.gewara.untrans.activity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.gewara.model.user.Member;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.activity.CategoryCount;
import com.gewara.xmlbind.activity.CountyCount;
import com.gewara.xmlbind.activity.RemoteActivity;
import com.gewara.xmlbind.activity.RemoteActivityMpi;
import com.gewara.xmlbind.activity.RemoteApplyjoin;
import com.gewara.xmlbind.activity.RemoteTreasure;

public interface SynchActivityService {
	/**
	 * ��ȡ�����Ϣ
	 * @param activityId
	 * @return
	 */
	ErrorCode<RemoteActivity> getRemoteActivity(Serializable activityId);
	/**
	 * ��ȡ�û�����Ļ
	 * @param memberid
	 * @param tag
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getRemoteActivityByMemberid(Long memberid, String tag, int from, int maxnum);
	/**
	 * ��ȡ�û��ղصĻ
	 * @param memberid
	 * @param tag
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getMemberCollActivityList(Long memberid, String tag, String order, String asc, int from, int maxnum);
	/**
	 * ���ݻid���ϻ�ȡ��б�
	 * @param idList
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getRemoteActivityListByIds(List<Long> idList);
	/**
	 * ����flag��ѯ
	 * @param citycode
	 * @param tag
	 * @param flag
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getActivityListByFlag(String citycode, String tag, Integer timetype, String flag, int from, int maxnum);
	/**
	 * �û��Ļ�б�
	 * @param memberid
	 * @param citycode
	 * @param timetype
	 * @param tag
	 * @param relatedid
	 * @return
	 */
	
	ErrorCode<List<RemoteActivity>> getMemberActivityListByMemberid(Long memberid, String citycode, int timetype, String tag, Long relatedid, int from, int maxnum);
	/**
	 * �û��μӵĻ�б�
	 * @param memberid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getMemberJoinActivityList(Long memberid, int from, int maxnum);
	/**
	 * ����״̬��ѯ�û���Ϣ
	 * @param citycode
	 * @param status
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getActivityListByStatus(String citycode, String status, int from, int maxnum);
	/**
	 * ����tag��ѯ�
	 * @param citycode
	 * @param timeType
	 * @param tag
	 * @param countycode
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getActivityListByTag(String citycode, String atype, String flag, 
			String tag, Long relatedid,
			String category, Long categoryid, Timestamp starttime, Timestamp endtime, String order, String asc,
			Integer from, Integer maxnum);
	/**
	 * �û��ղصĹ����Ļ
	 * @param memberid
	 * @param tag
	 * @param relatedidList
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getMemberActivityList(Long memberid, String tag, List<Long> relatedidList, int from, int maxnum);
	/**
	 * �������Ƽ��Ļ
	 * @param idList
	 * @param isClose
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getGewaCommendActivityList(List<Long> idList, boolean isClose);
	/**
	 * ���û����ѷ���Ļ�б�
	 * @param tag			
	 * @param idList		����ID����
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getFriendActivityList(String tag, List<Long> idList, int from, int maxnum);

	/**
	 * �ö��Ļ�б�
	 * @param citycode
	 * @param atype
	 * @param tag
	 * @param relatedid
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getTopActivityList(String citycode, final String atype, String tag, Long relatedid);

	/**
	 * �ղػ
	 * @param memberid
	 * @param activityId
	 * @return
	 */
	ErrorCode<String> collectActivity(Long memberid, Long activityId);
	/**
	 * ��ȡ�û��μӵĻ��Ϣ
	 * @param memberid
	 * @param activityid
	 * @return
	 */
	ErrorCode<RemoteApplyjoin> getApplyJoin(Long memberid, Long activityid);
	
	/**
	 * ��ȡ�û���μӵĻ��Ϣ
	 * @param memberids
	 * @param activityid
	 * @return
	 */
	ErrorCode<List<RemoteApplyjoin>> getApplyJoinByMemberids(List<Long> memberids, Long activityid);
	/**
	 * ��ȡ��μӵ���Ϣ
	 * @param activityid
	 * @return
	 */
	ErrorCode<List<RemoteApplyjoin>> getApplyJoinListByActivityid(Long activityid);
	
	/**
	 * ��ȡ�û��μӵĻ��Ϣ
	 * @param memberid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteApplyjoin>> getApplyJoinListByMemberid(Long memberid, int from, int maxnum);
	/**
	 * ���ӻ�������
	 * @param activityid
	 * @return
	 */
	ErrorCode addClickedtimes(Long activityid);
	/**
	 * ����relateid���ϻ�ȡ��б�
	 * @param citycode
	 * @param atype
	 * @param tag
	 * @param relatedidList
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getActivityListByRelatedidList(String citycode, String atype, String tag, 
			List<Long> relatedidList, int from, int maxnum);
	/**
	 * �����������
	 * @param tag
	 * @param date
	 * @param citycode
	 * @return
	 */
	ErrorCode<List<CountyCount>> getGroupActivityByTag(String tag, Date date, String citycode);
	
	/**
	 * �û��Ļ����
	 * @param memberid
	 * @param citycode
	 * @param timetype
	 * @param tag
	 * @param relatedid
	 * @return
	 */
	
	ErrorCode<Integer> getMemberActivityCount(Long memberid, String citycode, int timetype, String tag, Long relatedid);
	
	
	/**
	 * ��ѯ��ǰ�����
	 * @param citycode
	 * @param atype
	 * @param timetype
	 * @param tag
	 * @param relatedid
	 * @return
	 */
	ErrorCode<Integer> getActivityCount(String citycode, String atype, int timetype, String tag, Long relatedid);
	
	
	/**
	 * ���ݹ�����Ķ����ѯ���������
	 * @param citycode
	 * @param tag
	 * @param relatedid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteApplyjoin>> getApplyJoinList(String citycode, String tag, Long relatedid, int from, int maxnum);
	
	/**
	 * ��ȡ��Ĳ�����Ϣ
	 * @param activityid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteApplyjoin>> getApplyJoinListByActivityid(Long activityid, int from, int maxnum);
	/**
	 * ��ȡĳ�����μӻ��Ϣ
	 * @param citycode
	 * @param tag
	 * @return
	 */
	ErrorCode<Integer> getApplyJoinCountByTag(String citycode, String tag);
	/**
	 * �û��μӵĻ����
	 * @param memberid
	 * @return
	 */
	ErrorCode<Integer> getMemberJoinActivityCount(Long memberid);
	/**
	 * ���û����ѷ���Ļ����
	 * @param tag			
	 * @param idList		����ID����
	 * @return
	 */
	ErrorCode<Integer> getFriendActivityCount(String tag, List<Long> idList);
	
	
	/**
	 * �μӻ
	 * @param memberid
	 * @param activityid
	 * @param sex
	 * @param realname
	 * @param mobile
	 * @param joinnum
	 * @param joinDate
	 * @param walaAddress
	 * @return
	 */
	ErrorCode<RemoteActivity> joinActivity(Long memberid, Long activityid, String sex, String realname, String mobile, Integer joinnum, Date joinDate, String walaAddress);
	/**
	 * ȡ���μӻ
	 * @param activityid
	 * @param sex
	 * @param realname
	 * @param mobile
	 * @param joinnum
	 * @param joinDate
	 * @param address
	 * @return
	 */
	ErrorCode<RemoteActivity> cancelActivity(Long activityid, Long memberid);
	
	/**
	 * ȡ���ղ�
	 * @param activityid
	 * @param memberid
	 * @return
	 */
	ErrorCode<String> cancelCollection(Long activityid, Long memberid);
	/**
	 * ����ʱ�����Ͳ�ѯ��б�
	 * @param citycode
	 * @param atype
	 * @param timetype
	 * @param flag
	 * @param tag
	 * @param relatedid
	 * @param category
	 * @param categoryid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getActivityListByTimetype(String citycode, String atype, Integer timetype, String flag, String tag,
			Long relatedid, String category, Long categoryid, Integer from, Integer maxnum);
	ErrorCode<List<RemoteActivity>> getActivityList(String citycode, String atype, Integer timetype, String tag,
			Long relatedid, String category, Long categoryid, Integer from, Integer maxnum);
	ErrorCode<List<RemoteActivity>> getActivityListByOrder(String citycode, String atype, Integer timetype, String tag,
			Long relatedid, String category, Long categoryid, String order, Integer from, Integer maxnum);
	ErrorCode<List<RemoteActivity>> getActivityListByTime(String citycode, String atype, Timestamp starttime, Timestamp endtime, String flag,
			String tag, Long relatedid, String category, Long categoryid, String order, String asc, Integer from, Integer maxnum);
	ErrorCode<Integer> getActivityCountByTime(String citycode, String atype, Timestamp starttime, Timestamp endtime, String flag, String tag,
			Long relatedid, String category, Long categoryid);
	ErrorCode<Integer> getCurrActivityCount(String citycode, String atype, String flag, String tag, Long relatedid,
			String category, Long categoryid, Timestamp starttime, Timestamp endtime);
	ErrorCode<List<RemoteTreasure>> getTreasureList(Long activityid, String asc, int from, int maxnum);
	ErrorCode<RemoteActivity> addActivity(Member member, String citycode, Long activityid, String tag, Long relatedid, String category, Long categoryid,
			String title, Integer price, Timestamp starttime, Timestamp endtime, String contentdetail, String address);
	/**
	 * �Ƽ��
	 * @param citycode
	 * @param signname
	 * @param tag
	 * @param isClose
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<RemoteActivity> getGewaCommendActivityList(String citycode, String signname, String tag, int from, int maxnum);
	/**
	 * ��ȡͳ�Ƶ�����
	 * @return
	 */
	ErrorCode<List<CategoryCount>> getCategoryCountList();
	/**
	 * ��ȡ�ID
	 * @param begintime
	 * @param endtime
	 * @return
	 */
	List<String> getActivityIdList(Timestamp begintime, Timestamp endtime);
	/**
	 * ��ȡһ��ʱ���ڲμӻ������
	 * @param begintime
	 * @param endtime
	 * @return
	 */
	Integer getJoinCountByAddtime(Timestamp begintime, Timestamp endtime);
	/**
	 * ��ȡ����top���û�
	 * @param citycode
	 * @param tag
	 * @param maxnum
	 * @return
	 */
	List<Long> getTopAddMemberidList(String citycode, String tag, int maxnum);
	/**
	 * �õ���ǰ�Ļ
	 * @param citycode
	 * @param atype
	 * @param tag
	 * @param relatedidList
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getCurrActivityListByRelatedidList(String citycode, String atype, String tag, List<Long> relatedidList, int from,
			int maxnum);
	ErrorCode<List<RemoteActivity>> getCurrActivityList(String citycode, String atype, String tag, Long relatedid, String category, Long categoryid,
			Timestamp starttime, Timestamp endtime, int from, int maxnum);
	/**
	 * ��Ļ
	 * @param citycode
	 * @param tag
	 * @param relatedid
	 * @param cateogory
	 * @param categoryid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getCommendActivityList(String citycode, String tag, Long relatedid, String cateogory, Long categoryid, int from,
			int maxnum);
	/**
	 * ���������Ƚ϶�Ļ
	 * @param citycode
	 * @param tag
	 * @param relatedid
	 * @param cateogory
	 * @param categoryid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getHotActivityList(String citycode, String tag, Long relatedid, String cateogory, Long categoryid, int from,
			int maxnum);
	/**
	 * �Ƽ��Ļ
	 * @param citycode
	 * @param signname
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getActivityListBySignname(String citycode, String signname, int from, int maxnum);
	/**
	 * ����ʱ�����Ͳ�ѯ
	 * @param citycode
	 * @param datetype
	 * @param tag
	 * @param relatedid
	 * @param category
	 * @param categoryid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getActivityListByDatetype(String citycode, String atype, String datetype,  String isFee, String tag, Long relatedid, String category,
			Long categoryid, int from, int maxnum);
	/**
	 * ��ȡ��г���id�Ľӿ�
	 * @param activityid
	 * @return
	 */
	List<String> getActivityMpidList(Long activityid);
	ErrorCode<List<RemoteActivity>> getActivityListByMemberidList(String citycode, String atype, String datetype, List<Long> memberidList, String isFee, String tag, Long relatedid, String category,
			Long categoryid, int from, int maxnum);
	
	/**
	 * �û�������Ľ��
	 * @param activityid
	 * @param memberid
	 * @return
	 */
	List<String> memberOperActivityResult(Long activityid, Long memberid);
	/**
	 * ��ĳ�����Ϣ�б�
	 * @param activityid
	 * @return
	 */
	ErrorCode<List<RemoteActivityMpi>> getRemoteActiviyMpiList(Long activityid);
	/**
	 * �������ͣ��������ͣ�ʱ�����ͣ���ѯ����ͻ��ҳ����һ��
	 * @param citycode
	 * @param countycode
	 * @param atype
	 * @param datetype
	 * @param timetype
	 * @param tag
	 * @param relatedid
	 * @param category
	 * @param categoryid
	 * @param isFee
	 * @param order
	 * @param from
	 * @param maxnum
	 * @return
	 */
	ErrorCode<List<RemoteActivity>> getActivityListByType(String citycode, String countycode, String atype, String datetype, Integer timetype,
			String tag, Long relatedid, String category, Long categoryid, String isFee, String order, Integer from, Integer maxnum);
	//�����޸��ֻ���ʱ֪ͨactivity�ﶩ���޸��ֻ���
	void updateActiviyOrderMobile(String tradeNo, String mobile);
	//�����˿�֪ͨactivity�޸Ķ���״̬
	ErrorCode<String> activityOrderReturn(String tradeNo);
	//����tag�õ�������ӰԺ��
	List<Long> getActivityRelatedidByTag(String citycode, Integer timetype, String tag);
}
