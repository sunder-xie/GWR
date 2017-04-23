package com.gewara.service.bbs;

import java.util.List;

import com.gewara.model.bbs.qa.GewaAnswer;
import com.gewara.model.bbs.qa.GewaQuestion;

public interface UserQuestionService {
	/**
	 * ��ѯ��ǰ�û������֪��
	 */
	List<GewaQuestion> getQuestionByMemberid(Long memberid, int from, int maxnum);
	
	/**
	 * ��ѯ��ǰ�û������֪����Ϣ����
	 */
	Integer getQuestionCountByMemberid(Long memberid);
	
	/**
	 * ��ѯ��ǰ�û��ظ���֪��
	 * page ��ǰҳ��
	 */
	List<GewaQuestion> getAnswerByMemberid(Long memberid, int from, int maxnum);
	
	/**
	 * ��ѯ��ǰ�û��ظ���֪����Ϣ����
	 */
	Integer getAnswerCountByMemberid(Long memberid);
	
	/**
	 * ����questionid��ѯgewaAnswer
	 * @return
	 */
	GewaAnswer getGewaAnswerByAnswerid(Long questionid, Long memberid);
}
