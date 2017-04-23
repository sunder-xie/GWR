package com.gewara.untrans;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.SMSRecord;
import com.gewara.support.ErrorCode;

public interface UntransService {
	/**
	 * @param order
	 */
	void saveSeeCount(GewaOrder order, Timestamp playtime);
	/**
	 * @param tag
	 * @param relatedid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Map> getPayMemberListByTagAndId(String tag, Long relatedid, int from, int maxnum);
	
	int countPayMemberListByTagAndId(String tag, Long id); 
	/**
	 * 
	 * @param memberid
	 * @param tag
	 * @param id
	 * @return
	 */
	boolean isPlayMemberByTagAndId(Long memberid, String tag, Long id);
	boolean isPlayMemberByTagAndId(Long memberid, String tradeno, String tag, Long id);
	boolean isPlayMemberByTagAndId(Long memberid, String tradeno, String tag, Long id, Date startDate, Date endDate);
	
	/**
	 * ͨ��ͳһ��ڷ��Ͷ���
	 * @param smsid
	 * @return
	 */
	ErrorCode sendMsgAtServer(SMSRecord sms, boolean resend);
	ErrorCode sendMsgAtServer(SMSRecord sms, String channel, boolean resend);
	SMSRecord addMessage(SMSRecord record);

	/**
	 * @param order
	 * @return
	 */
	ErrorCode reSendOrderMsg(GewaOrder order);
	/**
	 * ���ܳ����ظ������
	 * @param order
	 * @param paymethod
	 * @param alipaid
	 */
	void saveOrderWarn(GewaOrder order, String paymethod, Integer alipaid);
	/**
	 * membercache �и���keyȡ��ҳ����Ӱ��ҳ�����õĻ���ؼ�����
	 * @param key
	 * @return
	 */
	Integer getIndexKeyNumber(String key,String citycode);
	void addTeleSms(String mobile, String content);

}
