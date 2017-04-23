package com.gewara.service.member;

import com.gewara.model.pay.SMSRecord;
import com.gewara.support.ErrorCode;

public interface BindMobileService {
	/**
	 * ����ˢ����֤��
	 * @param tag
	 * @param mobile
	 * @return
	 */
	ErrorCode<SMSRecord> refreshBindMobile(String tag, String mobile, String ip);
	ErrorCode<SMSRecord> refreshBindMobile(String tag, String mobile, String ip, String msgTemplate);
	/**
	 * ��̨������Աʹ�÷�����
	 * @param tag
	 * @param mobile
	 * @param ip
	 * @param msgTemplate
	 * @return
	 */
	ErrorCode<SMSRecord> refreshNoSecurityBindMobile(String tag, String mobile, String ip, String msgTemplate);
	/**
	 * �����ֻ���̬�룬��IP���ƣ������̺�̨ר�á�
	 * @param tag
	 * @param mobile
	 * @param ip
	 * @param msgTemplate
	 * @return
	 */
	ErrorCode<SMSRecord> refreshBMByAdmin(String tag, String mobile, String ip, String msgTemplate);
	
	/**
	 * ���Գɹ������ֱ������
	 * @param tag
	 * @param mobile
	 * @param checkpass
	 * @return ERRORCODE�����֣������Ի�ʧЧ��ǰ̨����
	 */
	ErrorCode checkBindMobile(String tag, String mobile, String checkpass);
	/**
	 * Ԥ�ȼ�⣬������1
	 * @param tag
	 * @param mobile
	 * @param checkpass
	 * @return
	 */
	ErrorCode preCheckBindMobile(String tag, String mobile, String checkpass);

	boolean getAndUpdateToken(String type, String ip, int checkcount);
	boolean isNeedToken(String type, String ip, int checkcount);
}
