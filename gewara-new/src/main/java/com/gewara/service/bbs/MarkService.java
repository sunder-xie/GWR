package com.gewara.service.bbs;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.json.bbs.MarkCountData;
import com.gewara.model.bbs.MarkCount;
import com.gewara.model.bbs.MemberMark;
import com.gewara.model.user.Member;

public interface MarkService {
	/**
	 * ĳ�����־ۺ�����
	 * @param tag
	 * @return
	 */
	Map getMarkdata(String tag);
	MarkCountData getMarkCountByTagRelatedid(String tag, Long relatedid);
	List<Map> getMarkRelatedidByAddtime(String tag, Timestamp starttime, Timestamp endtime);
	Timestamp updateMarkCount(String tag);
	Integer getMaxMarktimes(String tag, Timestamp starttime);
	void updateAvgMarkTimes(String tag,  Timestamp starttime, Timestamp endtime);
	/**
	 * ��ȡĳ���û���ĳ������ĳ������һ������ֵ
	 * @param tag
	 * @param relatedid
	 * @param markname
	 * @param memberId
	 * @return
	 */
	MemberMark getLastMemberMark(String tag, Long relatedid, String markname, Long memberId);
	MemberMark getCurMemberMark(String tag, Long relatedid, String markname, Long memberId);
	List<MemberMark> getMarkList(String tag, Long relatedid, String markname, int maxnum);
	List<MemberMark> getMarkList(String tag, Long relatedid, String markname, String flag);
	/**
	 * �������޸��û���ĳ������ĳ�������ֵ
	 * @param newmark
	 * @param newflag
	 * @param oldmark
	 * @param oldflag
	 */
	MemberMark saveOrUpdateMemberMark(String tag, Long relatedid, String markname, Integer markvalue, Member member);
	boolean saveOrUpdateMemberMarkMap(String tag, Long relatedid, Member member, Map<String, Integer> memberMarkMap);
	/**
	 * ��ȡ������ϸ������磺(markvalue:5, markcount:10) ��ʾ����������10�� 
	 * @param tag
	 * @param relatedid
	 * @param markname ������Ŀ����
	 * @return 
	 */
	List<Map> getMarkDetail(String tag, Long relatedid, String markname);
	Integer getMarkValueCount(String tag, Long relatedid, String markname,int fromValue, int maxValue);
	Map getPercentCount(String tag, Long relatedid);
	Map getGradeCount(String tag, Long relatedid);
	List<MarkCount> getMarkCountListByTag(String tag);
	/**
	 * ���ַּ�ͳ��
	 * ���磺
	 * 6--7 60% ���й�Ʊ�û�ƾռ20% �ǹ�Ʊռ80%
	 * 1--3 60% ���й�Ʊ�û�ƾռ20% �ǹ�Ʊռ10%
	 * @param tag
	 * @param relatedid
	 * @return
	 */
	List<Map> getGradeDetail(String tag, Long relatedid);
}	

