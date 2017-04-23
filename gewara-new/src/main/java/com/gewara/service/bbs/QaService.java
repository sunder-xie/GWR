package com.gewara.service.bbs;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.model.bbs.qa.GewaAnswer;
import com.gewara.model.bbs.qa.GewaQaExpert;
import com.gewara.model.bbs.qa.GewaQaPoint;
import com.gewara.model.bbs.qa.GewaQuestion;
import com.gewara.model.user.Member;

public interface QaService {
	List<GewaQuestion> getQuestionListByQuestionstatus(String citycode, String questionstatus, String order, int from, int maxnum);

	Integer getQuestionCountByQuestionstatus(String citycode, String questionstatus);

	List<GewaQuestion> getQuestionListByStatus(String status, Date fromDate, Date endDate, int from, int maxnum);

	Integer getQuestionCountByStatus(String status, Date fromDate, Date endDate);

	List<GewaQuestion> getQuestionListByHotvalue(String citycode, int hotvalue, int from, int maxnum);

	List<GewaAnswer> getAnswerListByQuestionid(Long questionid);

	Integer getAnswerCount(Long questionid);

	List<GewaAnswer> getAnswerListByQuestionId(int start, int maxnum, Long questionid);
	
	List<GewaAnswer> getAnswerListByQuestionAndMemId(int start, int maxnum, Long questionid,Long memberId);

	/**
	 * ���������ѯ�ظ� status=Y_NEW
	 */
	Integer getAnswerCountByQuestionId(Long questionid);

	/**
	 * Ta�ش�����������
	 * 
	 * @param mid
	 * @return
	 */
	Integer getAnswerCountByMemberid(Long mid);

	/**
	 * Ta�ش�����ⱻ���ɵ�����
	 * 
	 * @param mid
	 * @return
	 */
	Integer getBestAnswerCountByMemberid(Long mid);

	/**
	 * �û��Ƿ�����������
	 * 
	 * @param memberid
	 * @param maxdays
	 * @return
	 */
	boolean isQuestion(Long memberid, Integer maxdays);

	/**
	 * �û��Ƿ��Ѿ��ش����������
	 * 
	 * @param qid
	 * @param mid
	 * @return
	 */
	boolean isAnswerQuestion(Long qid, Long mid);

	/**
	 * �������Ѵ�
	 * 
	 * @param qid
	 * @return
	 */
	GewaAnswer getBestAnswerByQuestionid(Long qid);

	/**
	 * �û��Ƿ���ר��
	 * 
	 * @param mid
	 * @return
	 */
	GewaQaExpert getQaExpertByMemberid(Long mid);

	/**
	 * ����������ȶ�
	 * 
	 * @param id
	 * @param hotvalue
	 * @return 2009-10-29
	 */
	boolean updateQAHotValue(Long id, Integer hotvalue);

	/**
	 * �޸�ר����Ϣ�ȶ�
	 * 
	 * @param id
	 * @param hotvalue
	 * @return 2009-10-30
	 */
	boolean updateQAExpertHotValue(Long id, Integer hotvalue);

	/**
	 * ��ѯר����Ϣ����
	 * 
	 * @return 2009-10-29
	 */

	Integer getQAExpertCount();

	/**
	 * ��ѯר����Ϣ
	 * 
	 * @return 2009-10-29
	 */
	List<GewaQaExpert> getQaExpertList();

	/**
	 * ��������
	 * 
	 * @param tag
	 * @param relatedid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<GewaQuestion> getQuestionByTagAndRelatedid(String citycode, String tag, Long relatedid, int from, int maxnum);

	/**
	 * ��������
	 * 
	 * @param category
	 * @param categoryid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<GewaQuestion> getQuestionByCategoryAndCategoryid(String citycode, String category, Long categoryid, int from, int maxnum);
	
	List<GewaQuestion> getQuestionByCategoryAndCategoryid(String citycode, String category, Long categoryid, boolean status, String questionstatus, int from, int maxnum);

	Integer getQuestionCountByCategoryAndCid(String citycode, String category, Long categoryid);

	/**
	 * �û���Ѵ𰸶�������
	 * 
	 * @param from
	 * @param maxnum
	 * @return
	 */
	Map<Member, Integer> getTopMemberListByBestAnswer(int from, int maxnum);

	Integer getTopMemberCountByBestAnswer();

	/**
	 * ��ѯר����Ϣ���״̬
	 * 
	 * @return 2009-10-30
	 */
	GewaQaExpert getQaExpertStatusById(Long id);

	List<GewaAnswer> getAnswerByMemberId(Long id);

	/**
	 * �û��ش������������
	 * 
	 * @param from
	 * @param maxnum
	 * @return
	 */
	Map<Member, Integer> getTopMemberListByAnswer(int from, int maxnum);

	Integer getTopMemberCountByAnswer();

	/**
	 * �û�����ֵ��������
	 * 
	 * @return
	 */
	List<Map> getTopMemberListByPoint(int from, int maxnum);

	Integer getTopMemberCountByPoint();

	// List<Long> getQuesionidListByMemberid(Long memberid);

	List<GewaQuestion> getQuestionListByMemberid(Long memberid, int from, int maxnum);

	Map<Map<Object, String>, Integer> getQuestionListByTagGroup(String tag, int from, int maxnum);

	Map<Map<Object, String>, Integer> getQuestionListByCategoryGroup(String category, int from, int maxnum);

	List<GewaQaExpert> getCommendExpertList(Integer hotvalue, int from, int maxnum);

	GewaQaPoint getGewaQaPointByQuestionidAndTag(Long qid, String tag);

	/**
	 * �û��ۼƵ��ʴ���ֵ
	 * 
	 * @param mid
	 * @return
	 */
	Integer getPointByMemberid(Long mid);

	List<GewaQuestion> getQuestionByQsAndTagList(String citycode, String qs, String tag, String order, int maxnum);

	List<GewaQuestion> getQuestionListByQsAndTagAndRelatedid(String tag, Long relatedid, String qs, String order, int maxnum);

	/**
	 * ��ѯ����������
	 * 
	 * @param citycode
	 *            ���д���
	 * @param tag
	 *            ��������
	 * @param relatedid
	 *            ��������id
	 * @param status
	 *            ����״̬,��ѡֵ:N(�����),Y(�ѽ��),Z(����),noproper(�������)
	 * @return
	 */
	int getQuestionCount(String citycode, String tag, Long relatedid, String status);

	/**
	 * ��ҳ��ѯ����
	 * 
	 * @param citycode
	 *            ���д���
	 * @param tag
	 *            ��������
	 * @param relatedid
	 *            ��������id
	 * @param status
	 *            ����״̬,��ѡֵ:N(�����),Y(�ѽ��),Z(����),noproper(�������)
	 * @param order
	 *            ����
	 * @param from
	 *            ҳ��
	 * @param maxnum
	 *            �������
	 * @return
	 */
	List<GewaQuestion> getQuestionList(String citycode, String tag, Long relatedid, String status, String order, int from, int maxnum);
	/**
	 * ����hotvalue��ѯ֪������
	 */
	Integer getQuestionCountByHotvalue(String citycode, Integer hotvalue);
	
	/**
	 * ��ȡ�ݳ�������Ա�ظ���memberid;
	 * @return
	 */
	Long getGewaraAnswerByMemberid();
}
