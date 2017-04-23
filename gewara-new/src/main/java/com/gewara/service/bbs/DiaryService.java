package com.gewara.service.bbs;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.model.bbs.Diary;
import com.gewara.model.bbs.DiaryBase;
import com.gewara.model.bbs.DiaryComment;
import com.gewara.model.bbs.VoteChoose;
import com.gewara.model.bbs.VoteOption;

/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public interface DiaryService {
	/**
	 * ���۵�����
	 * @param type
	 * @param tag
	 * @param relatedid
	 * @return
	 */
	DiaryBase getDiaryBase(Long id);
	<T extends DiaryBase> Integer getDiaryCount(Class<T> clazz, String citycode, String type, String tag, Long relatedid);
	<T extends DiaryBase> Integer getDiaryCountByKey(Class<T> clazz, String citycode, String type, String tag, Long relatedid, String key, Timestamp startTime, Timestamp endTime);
	<T extends DiaryBase> List<T> getDiaryList(Class<T> clazz, String citycode, String type, String tag, Long relatedid, int start, int maxnum);
	/**
	 * �������ֶ�
	 */
	<T extends DiaryBase> List<T> getDiaryList(Class<T> clazz, String citycode, String type, String tag, Long relatedid, int start, int maxnum, String order);
	/**
	 * ��ѯһ��ʱ���ڵ�������Ϣ
	 */
	<T extends DiaryBase> List<T> getDiaryListByOrder(Class<T> clazz, String citycode, String type, String tag, Long relatedid, Timestamp startTime,Timestamp endTime, String order, boolean asc, int start, int maxnum);
	/**
	 * �������ֶ�
	 */
	<T extends DiaryBase> List<T> getDiaryListByKey(Class<T> clazz, String citycode, String type, String tag, Long relatedid, int start, int maxnum, String key, Timestamp startTime,Timestamp endTime);
	/**
	 * ��Flag�ֶ� 
	 */
	<T extends DiaryBase> List<T> getDiaryListByFlag(Class<T> clazz, String citycode, String type, String tag, String flag, int from, int maxnum);
	/**
	 * @param type
	 * @param tag
	 * @return �ö���Diary
	 */
	List<Diary> getTopDiaryList(String citycode, String type, String tag, boolean isCache);
	/**
	 * ��ȡ24Сʱ�������ӣ�����24Сʱ�ڻظ�������
	 * @param tag
	 * @return
	 */
	List<Map> getOneDayHotDiaryList(String citycode, String tag);
	/**
	 * @param type
	 * @param tag
	 * @param memberid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	<T extends DiaryBase> List<T> getDiaryListByMemberid(Class<T> clazz, String type, String tag, Long memberid, int from, int maxnum);
	/**
	 * ĳ���û�����������
	 * @param type
	 * @param tag
	 * @param mid
	 * @return
	 */
	<T extends DiaryBase> Integer getDiaryCountByMemberid(Class<T> clazz, String type, String tag, Long memberId);
	/**
	 * �õ�ĳ��Diary�Ļظ�
	 * @param diaryId
	 * @return
	 */
	List<DiaryComment> getDiaryCommentList(Long diaryId, int from, int maxnum); 
	List<DiaryComment> getDiaryCommentList(Long diaryId); 
	/**
	 * @param type
	 * @param category
	 * @param categoryid
	 * @param memberid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Diary> getFriendDiaryList(String type, String category, Long categoryid, Long memberid, int from, int maxnum);
	/**
	 * ����ͶƱ��id������ѡ��
	 * @param vid
	 * @return
	 */
	List<VoteOption> getVoteOptionByVoteid(Long vid);
	/**
	 * ͶƱ������
	 * @param did
	 * @return
	 */
	Integer getVotecount(Long did);
	/**
	 * �Ҷ�ĳ�����ӵ�ͶƱ����
	 * @return
	 */
	List<VoteChoose> getVoteChooseByDiaryidAndMemberid(Long did,Long mid);
	Integer getDiaryCommentCount(String tag, Long diaryId);
	/**
	 * �Ƿ��Ѿ�ͶƱ(׼�Զ������)
	 * @param memberid
	 * @return
	 */
	boolean isMemberVoted(Long memberid, Long diaryid);
	
	//���һ������Ӱ��
	List<Diary> getHotCommentDiary(String citycode, String type, String tag, Long relateid, int from,int maxnum);
	/**
	 * ��ѯȦ�����Ż���
	 */
	<T extends DiaryBase> List<T> getHotCommuDiary(Class<T> clazz, String citycode, boolean isCommu,String type,int from,int maxnum);
	
	/**
	 * ����status��ѯ����
	 * @param status
	 * @return
	 */
	<T extends DiaryBase> List<T> getDiaryListByStatus(Class<T> clazz, String keyname, String status, Date fromDate, Date endDate, int from, int maxnum);
	<T extends DiaryBase> Integer getDiaryCountByStatus(Class<T> clazz, String keyname, String status, Date fromDate, Date endDate);
	/**
	 * ����status��ѯ��������
	 * @param status
	 * @return
	 */
	List<DiaryComment> getDiaryCommentListByStatus(String keyname, String status, Date fromDate, Date endDate, int from, int maxnum);
	Integer getDiaryCommentCountByStatus(String keyname, String status, Date fromDate, Date endDate);
	<T extends DiaryBase> List<T> getRepliedDiaryList(Class<T> clazz, Long memberid, int from, int maxnum);
	<T extends DiaryBase> Integer getRepliedDiaryCount(Class<T> clazz, Long memberid);

	/**
	 * ��ѯ��Ӱ�����硢�˶���Ŀ�ĵ����б�
	 * @param keyname
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Map> getMDSDiaryListByKeyname(String citycode, String keyname, String tag, String name, int from, int maxnum);
	Integer getMDSDiaryCountByKeyname(String citycode, String keyname, String tag, String name);
	/**
	 * ��ҳ��ѯ�ݳ������б�����
	 * @param citycode
	 * @param key
	 * @param starttime
	 * @param endtime
	 * @param order
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Diary> getDiaryBySearchkeyAndOrder(String citycode,String key,Timestamp starttime, Timestamp endtime, String order,int from,int maxnum);
}
