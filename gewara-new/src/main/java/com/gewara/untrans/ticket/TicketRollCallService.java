package com.gewara.untrans.ticket;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.query.Query;

import com.gewara.json.TicketRollCallMember;
import com.gewara.support.ErrorCode;


public interface TicketRollCallService {
	/**
	 * ��¼ÿ�칺Ʊ�û���Ϣ, ��Ʊ��������С�ڵ���checkCount,����checkCount�����������
	 * @param memberid	�û�ID
	 * @param mobile	�ֻ���	
	 * @param tag	�������ͣ�����tag = 'cinema',�� relatedid ΪӰԺID��	
	 * @param relatedid	��������ID
	 * @param quantity ��Ʊ��
	 * @param checkCount ���ƹ�Ʊ��
	 * @return 
	 */
	ErrorCode<String> saveOrUpdateTicketRollCall(Long memberid, String mobile, String tag, Long relatedid, int quantity, int checkCount);
	
	/**
	 * �û����ֻ��������б�������ѯ
	 * @param status	״̬	
	 * @param startDate	��ʼʱ���
	 * @param endDate ����ʱ���
	 * @param mobiles	�û�ID���ֻ�������
	 * @return 
	 */
	Integer getTicketRollCallMemberCount(String status, Date startDate, Date endDate, String... mobiles);
	
	/**
	 * �û����ֻ��������б��ѯ
	 * @param status	״̬ (status ='D' Ϊ������	, status = 'Y' Ϊ������)
	 * @param startDate	��ʼʱ���
	 * @param endDate ����ʱ��� 
	 * @param mobiles	�û�ID���ֻ�������
	 * @return 
	 */
	List<TicketRollCallMember>  getTicketRollCallMemberList(String status, Date startDate, Date endDate, int from, int maxnum, String... mobiles);
	/**
	 * 
	 * @param memberid �û�ID
	 * @prama mobile �ֻ���
	 * @return 
	 */
	boolean isTicketRollCallMember(Long memberid, String mobile);
	
	/**
	 * ͨ�����͡�����ID��ʱ��Ρ��ֻ����û�ID���ϻ��TicketRollCall �����ѯ
	 * @param tag	�������ͣ��磺cinema �ȣ���Ϊ�գ�
	 * @param relatedid	��������ID ����Ϊ�գ�
	 * @param startDate	��ʼʱ��	����Ϊ�գ�
	 * @param endDate	����ʱ��	 ����Ϊ�գ�
	 * @param mobiles	�ֻ����û�ID���� 
	 * @return	TicketRollCall �����ѯ
	 */
	Query getTicketRollCallQuery(String tag, Long relatedid, Date startDate, Date endDate, String... mobiles);

	/**
	 * �Ƴ���ţ����
	 * @param id
	 * @return
	 */
	boolean removeRollCallMember(String id);

	/**
	 * ���ӻ�ţ����
	 * @param mobile
	 * @param status
	 * @param reason
	 * @param userid
	 * @return
	 */
	ErrorCode addTicketRollMember(String mobile, String status, String reason, Long userid);
}
