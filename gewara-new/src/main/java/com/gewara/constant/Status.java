package com.gewara.constant;

import org.apache.commons.lang.StringUtils;

public abstract class Status {

	public static final String Y = "Y";						//������ʾ
	public static final String Y_NEW = "Y_NEW";				//�����ӡ��ʴ𡢻ظ�...
	public static final String Y_LOCK = "Y_LOCK";			//��
	public static final String Y_DOWN = "Y_DOWN";			//�³�����
	public static final String Y_LOCK_DOWN = "Y_LD";
	
	public static final String DEL = "D";					//ɾ��
	public static final String N = "N";						//���ⲻ��ʾ
	public static final String N_DELETE = "N_DELETE";		//��ɾ��
	public static final String N_FILTER = "N_FILTER";		//���ؼ��ֹ���
	public static final String N_ACCUSE = "N_ACCUSE";		//�ٱ���ʵ
	public static final String N_NIGHT = "N_NIGHT";			//ҹ�䷢��
	
	public static final String N_ERROR = "N_ERR";			//���ִ���
	
	public static final String Y_STOP = "Y_STOP";			//�ֹͣ����
	public static final String Y_PROCESS = "Y_PROCESS";		//����Ա���
	public static final String Y_TREAT = "Y_TREAT";			//����ڴ���
	
	//20110822
	public static boolean isHidden(String status){
		return StringUtils.isNotBlank(status) && status.startsWith(N);
	}
}
