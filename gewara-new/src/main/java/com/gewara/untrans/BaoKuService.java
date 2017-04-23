package com.gewara.untrans;

/**
 * �������ݹ���
 * 
 * @author zhaorq
 * 
 */
public interface BaoKuService {

	/**
	 * ��֤�ʼ��������Ƿ����Σ��
	 * 
	 * @param authenticode
	 * @return
	 */
	boolean isDanger(String email, String pwd);

	/**
	 * ���뱩������
	 * 
	 * @param fileName
	 * @return
	 */
	boolean loadBaoKuTxt(String fileName, boolean isLoad);
	
	/**
	 * ����Ŀ¼�µ������ļ�����Ҳ�Ҫʹ�ã�
	 * @param isLoad
	 */
	void loadBaoKuFiles(boolean isLoad);

	void scanLoadBaoKu();
	
	void scanMemberBaoKu();
	
	void loadGewaraBaoKu();

	/**
	 * ��֤�ʼ��������Ƿ����Σ��������md5��
	 * @param email
	 * @param pwd
	 * @return
	 */
	boolean isDangerMD5(String email, String pwdMD5);
}
