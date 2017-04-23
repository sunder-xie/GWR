package com.gewara.untrans;

import java.util.Map;

public interface MemberCountService {
	int getFansCountByMemberId(Long memberid);

	/**
	 * ��ȡ�û��ۺ����ݣ���
	 * 
	 * @param memberid
	 * @return
	 */
	Map getMemberCount(Long memberid);

	/**
	 * 
	 * @param id
	 *            �û�ID
	 * @param key
	 *            �ۺ�����KEY
	 * @param value
	 *            �ۺ�����ֵ
	 * @param isAdd
	 *            isAdd = true Ϊ���� value, ��֮��ȥ value, ���С��0��Ϊ0;
	 */
	void updateMemberCount(Long memberid, String key, int value, boolean isAdd);

	/**
	 * �û���Ϊͳ��
	 * 
	 * @param memberid
	 * @param key
	 * @param value
	 * @param isReplace
	 */
	void saveMemberCount(Long memberid, String key, String value, boolean isReplace);

	/**
	 * @param memberid
	 * @param key
	 * @param value
	 * @param isReplace
	 * @return
	 */
	void saveMemberCount(Long memberid, String key, String value, Integer maxnum, boolean isReplace);

	/**
	 * ��ȡ�û��ۺ�����
	 * @param memberid
	 * @return
	 */
	Map getMemberInfoStats(Long memberid);
	/**
	 * �����û������һ���µ�����
	 * @param mobile
	 * @param tradeNo
	 * @param orderType
	 * @param time
	 */
	void saveMobileLastTicket(String mobile,String tradeNo,String orderType,String time);
	/**
	 * ��ȡָ���ֻ��ŵ����һ�ζ���
	 * @param mobile
	 * @return
	 */
	String getMobileLastTrade(String mobile);
	/**
	 * �����û��µĵ�һ�ʶ���
	 * @param memberid
	 * @param tradeNO
	 * @param orderType
	 */
	void saveMbrFirstTicket(Long memberid, String tradeNo, String orderType);
}
