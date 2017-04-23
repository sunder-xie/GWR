package com.gewara.untrans.activity;

/**
 * ��ำ齱����ݹ���
 * @author zhaorq
 *
 */
public interface TenWowActivityService {

	/**
	 * ��֤���Ƿ����
	 * @param authenticode
	 * @return
	 */
	boolean isAuthenticode(String authenticode);
	
	/**
	 * ʹ����֤��齱
	 * @param authenticode
	 * @param memberid
	 * @param memberCode
	 * @param memberName
	 * @return
	 */
	boolean useAuthenticode(String authenticode,String memberid,String memberCode,String memberName);
	
	/**
	 * ������֤������
	 * @param fileName
	 * @return
	 */
	boolean loadTinWowTxt(String fileName,boolean isLoad);
}
