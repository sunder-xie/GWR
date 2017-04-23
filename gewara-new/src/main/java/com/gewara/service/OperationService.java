package com.gewara.service;

import java.sql.Timestamp;

import com.gewara.model.common.LastOperation;
import com.gewara.support.ErrorCode;

/**
 * �û����Ʋ����ӿ�
 * @author acerge(acerge@163.com)
 * @since 7:06:35 PM Sep 7, 2010
 */
public interface OperationService {
	int ONE_HOUR = 60*60;
	int HALF_HOUR = 60 * 30;
	int ONE_MINUTE = 60;
	int HALF_MINUTE = 30;
	int ONE_DAY = ONE_HOUR * 24;//seconds
	int HALF_DAY = ONE_HOUR * 12;
	int ONE_WEEK = ONE_DAY * 7;
	String TAG_ADDCONTENT = "addContent"; 	//�û��������ӡ�֪�������
	String TAG_REPLYCONTENT = "replyContent"; 	//�ظ�����
	String TAG_REPLY = "reply";				//�û��ظ����ӡ�֪�������
	String TAG_SENDEAIL = "sendEail"; 		//�û����ʼ�
	String TAG_ADVISE = "advise"; 			//Ͷ�߽���
	String TAG_TREASURE_ADD = "treasure_add";  //��ӹ�ע
	String TAG_TREASURE_CANCEL = "treasure_cancel"; //ȡ����ע
	String TAG_ATTACHMOVIE="attachmovie";//��Ӿ���
	String TAG_SENDTICKETPWD = "sendTicketPWD";	// ����ȡƱ����
	String TAG_MEMBERMARK = "member_mark"; //�û�����
	String TAG_SUBJECTAGENDA = "subject_agenda"; //5Ԫ��Ʊ��������
	
	String TAG_MOBILE_SMS_INVITE="mobile_sms_invite";//�ֻ���������
	String TAG_MOBILE_REG="mobile_reg_limits";//�ֻ�ע��
	
	/**
	 * ���24Сʱ����һ��
	 * @param opkey
	 * @return
	 */
	boolean updateOperationOneDay(String opkey, boolean update);
	/**
	 * ÿ�β���������allowIntervalSecond
	 * @param opkey
	 * @param allowIntervalSecond ʱ�������룩
	 * @return
	 */
	boolean isAllowOperation(String opkey, int allowIntervalSecond);
	boolean updateOperation(String opkey, int allowIntervalSecond);
	boolean updateOperation(String opkey, int allowIntervalSecond, String secondkey);
	/**
	 * scopeSecond���ʱ���ڣ��룩����������allowNum��
	 * @param opkey
	 * @param scopeSecond ʱ�䷶Χ���룩
	 * @return
	 */
	boolean isAllowOperation(String opkey, int scopeSecond, int allowNum);
	boolean updateOperation(String opkey, int scopeSecond, int allowNum);
	boolean updateOperation(String opkey, int scopeSecond, int allowNum, String secondkey);
	/**
	 * ÿ�β���������allowIntervalSecond��scopeSecond���ʱ���ڣ��룩����������allowNum��
	 * implied condition: allowIntervalSecond * allowNum < scopeSecond
	 * @param opkey
	 * @param allowIntervalSecond ʱ�������룩
	 * @param scopeSecond ʱ�䷶Χ���룩
	 * @param allowNum
	 * @return
	 */
	boolean isAllowOperation(String opkey, int allowIntervalSecond, int scopeSecond, int allowNum);
	boolean updateOperation(String opkey, int allowIntervalSecond, int scopeSecond, int allowNum);
	boolean updateOperation(String opkey, int allowIntervalSecond, int scopeSecond, int allowNum, String secondkey);
	/**
	 * ����ʱ����
	 * @param opkey
	 * @param secondNum
	 */
	void resetOperation(String opkey, int secondNum);
	/**
	 * ���Key��������Ĵ�����ֻ���ڶ�ʱ��ļ��Ҿ������
	 * �����ؼۻʱ��key=�����ip + �����Ż�id
	 * @param key 
	 * @param limitedCount ���Ƶ��������
	 * @return
	 */
	ErrorCode<String> checkLimitInCache(String key, int limitedCount);
	ErrorCode<String> updateLoginLimitInCache(String key, int maxnum);
	ErrorCode<String> checkLoginLimitNum(String key, int maxnum);
	LastOperation updateLastOperation(String lastkey, String lastvalue, Timestamp lasttime, Timestamp validtime, String tag);
}
