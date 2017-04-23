package com.gewara.service.bbs;

import java.util.List;

import com.gewara.model.bbs.CustomerAnswer;
import com.gewara.model.bbs.CustomerQuestion;

/**
 *    @function �û������ռ�Service
 * 	@author bob.hu
 *		@date	2011-03-11 14:32:11
 */
public interface CustomerQuestionService {
	List<CustomerQuestion> getQuestionsBykey(String citycode, String tag, String searchkey, String status, int from, int maxnum);
	Integer getQuestionCountBykey(String citycode, String tag, String searchkey, String status);
	
	/**
	 *  ���� qid, ��ѯĳ�û������Ӧ�Ļظ�
	 * */
	List<CustomerAnswer> getAnswersByQid(Long qid, int from, int maxnum);
	Integer getAnswerCountByQid(Long qid);
	List<CustomerQuestion> getCustomerQList(Long memberid, String citycode, String tag, int from, int maxnum);
	Integer getCustometQCount(Long memberid, String citycode, String tag);
	
	CustomerQuestion addCustomerQuestion(String citycode, Long memberid, String email, String tag, String body, String type);
}
