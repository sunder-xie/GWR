/**
 * 
 */
package com.gewara.service.member;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.model.bbs.Diary;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.user.Member;
import com.gewara.model.user.Point;
import com.gewara.model.user.PointHist;
import com.gewara.support.ErrorCode;

/**
 * @author hxs(ncng_2006@hotmail.com)
 * @since Feb 2, 2010 10:10:58 AM
 */
public interface PointService {
	
	/**
	 * ����memberid��ѯ�����б�
	 * @param memberid
	 * @return
	 */
	List<Point> getPointListByMemberid(Long memberid, String tag, Timestamp addtime, Timestamp endtime, String order, int from, int maxnum);
	Integer getPointCountByMemberid(Long memberid, String tag, Timestamp addtime, Timestamp endtime);
	List<Point> getPointListByMemberidAndPointValue(Long memberid, String tag, int pointvalue, Timestamp addtime, Timestamp endtime, String order, int from, int maxnum);
	Integer getPointCountByMemberidAndPointValue(Long memberid, String tag, int pointvalue, Timestamp addtime, Timestamp endtime);
	
	/**
	 * ��ӻ�����Ϣ
	 */
	Point addPointInfo(Long memberid, Integer pointvalue, String reason, String tag);
	Point addPointInfo(Long memberid, Integer pointvalue, String reason, String tag, Long tagid, Long adminid);
	Point addPointInfo(Long memberid, Integer pointvalue, String reason, String tag, Long tagid, Long adminid, Timestamp addtime);
	Point addPointInfo(Long memberid, Integer pointvalue, String reason, String tag, Long tagid, Long adminid, Timestamp addtime, String uniquetag, String statflag);
	/**
	 * �Ƿ��Ѿ���ȡ������
	 * @return
	 */
	boolean isGetLoginPoint(Long memberId, String date);
	
	/**
	 * ��̨���ֲ�ѯ
	 * @param startTime
	 * @param endTime
	 * @param tag
	 * @param valueStart
	 * @param valueEnd
	 * @param pageNo
	 * @param maxNum
	 * @return List<Map(memberid, gainpoint, paypoint)> 
	 */
	List<Map> getPointVariableList(Timestamp startTime, Timestamp endTime, String tag, int valueStart, int valueEnd, int from, int maxnum);
	/**
	 * @param startTime
	 * @param endTime
	 * @param tag
	 * @param valueStart
	 * @param valueEnd
	 * @return Map(memberid, gainpoint, paypoint)
	 */
	Map getPointVariableMap(Timestamp startTime, Timestamp endTime, String tag, int valueStart, int valueEnd);
	//ǰ̨��ѯ��������
	List<Map> getTopPointByDateMap(Timestamp startTime, Timestamp endTime, List<String> tagList, int from,int maxNum);
	/**
	 * ��̨���ֲ�ѯ����
	 */
	Integer getPointVariableCount(Timestamp startTime, Timestamp endTime, String tag, int valueStart, int valueEnd);
	/**
	 * ��ѯ��ǰϵͳ�ܻ���
	 */
	Integer getSumPoint();
	
	/**
	 * ��ѯ����tag
	 */
	List getPointTagList();
	
	/**
	 * ����id��ѯ������ȡ�����ĵĻ�����Ϣ
	 */
	List<Point> getPointListByIdAndType(Long memberid, Timestamp startTime, Timestamp endTime, String tag, String type, int from, int maxnum);
	
	/**
	 * ����id��ѯ������ȡ�����ĵĻ�����Ϣ����
	 */
	Integer getPointByIdAndTypeCount(Long memberid, Timestamp startTime, Timestamp endTime, String tag, String type);
	/**
	 * ÿ�ռӻ���
	 * @param member
	 * @return
	 */
	ErrorCode<Point> addLoginPoint(Member member, String type, Timestamp cur);
	ErrorCode<Map> addLoginPointInFestival(Member member);

	ErrorCode<Point> addOrderPoint(GewaOrder order);
	
	List getPointExpendDetail(String startTime, String endTime, String tag);
	
	/**
	 * ��ѯ�������ڹ�Ӱ�û�������Ӱ��
	 */
	List<Diary> getDiaryList(String tag,String pointTag,int from,int maxnum);
	Integer getDiaryCount(String tag,String pointTag);
	/**
	 * ��ѯ��ǰϵͳ���û��Ļ����Ƿ����쳣
	 */
	boolean isLoginPointRewards(Long memberid, Timestamp cur);
	Integer getPointRewardsDay(Long memberid, Timestamp cur);
	/**
	 ����tag,tagid,memberid��ѯ���ּ�¼*/
	Point getPointByMemberiAndTagid(Long memberid, String tag, Long tagid);
	
	/**
	 *  ���� memberid��ѯ uniquetag��ʶ
	 * */
	int countUniquetagByCombine(String combUniquetag);
	
	/**
	 *  �����ȡ������û�
	 * */
	List<Map> getRecentlyGetPointList(int maxnum);
	/**
	 * ��ȡ��ȡ�������5���û� 
	 */
	List<Map> getLuckGetPointList(int maxnum);
	/**
	 *  ���������Ʊ�ͻ���
	 * */
	void addPointToInvite(Long memberid, Integer point);
	
	/**
	 * ��ӻ�����Ϣ������ͳ�Ʊ�
	 */
	void addPointStats(Timestamp curTimestamp);
	/**
	 * �û��û���֧���ļ�¼
	 * @param tradeNo
	 * @return
	 */
	List<Point> getPointListByTradeNo(String tradeNo);
	//�û������������ӻ���
	ErrorCode<Point> addNewTaskPoint(Long memberid, String tag);
	ErrorCode<String> validUsePoint(Long memberid);
	/**
	 * ��֤΢���˻���ȡ����
	 * @param member
	 * @return
	 */
	ErrorCode validWeiboPoint(Member member, boolean isApp);
	ErrorCode validWeiboPoint(Member member);
	/**
	 * ��ѯ������ʷ���¼
	 * @param memberid
	 * @param tag
	 * @param pointvalue
	 * @param addtime
	 * @param endtime
	 * @param order
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<PointHist> getPointHistListByMemberidAndPointValue(Long memberid, String tag, int pointvalue, Timestamp addtime, Timestamp endtime,String order, int from, int maxnum);
	/**
	 * ��ѯ������ʷ���¼��
	 * @param memberid
	 * @param tag
	 * @param pointvalue
	 * @param addtime
	 * @param endtime
	 * @return
	 */
	Integer getPointHistCountByCondition(Long memberid, String tag, int pointvalue, Timestamp addtime, Timestamp endtime);
}