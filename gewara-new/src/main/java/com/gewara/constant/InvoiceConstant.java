package com.gewara.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceConstant {
	public static final String STATUS_OPEN = "Y"; //�ѿ�
	public static final String STATUS_UNOPEN = "N"; //δ��

	public static final String STATUS_OPENED = "Y_OPEN"; //�ѿ�
	public static final String STATUS_UNPOST = "Y_NOTPOST";//δ�ʼ�
	public static final String STATUS_POST_EXPRESS = "Y_EXP"; //���
	public static final String STATUS_POST_COMMON = "Y_POST";//ƽ�� 
	public static final String STATUS_UNOPENED = "N_NOTOPEN";//δ��
	public static final String STATUS_APPLY = "N_APPLY";//����
	public static final String STATUS_TRASH="N_TRASH";//��Ʊ��������������
	public static final String STATUS_APPLY_AGAIN="N_APPLYAGAIN";//���벹��
	public static final String STATUS_OPEN_AGAIN="Y_AGAIN";//�Ѳ���
	//��Ʊ�����б�(�����롢���ʼġ�δ�ʼġ��ѿ�ݡ�δ���������²������롢�ѿ����Ѳ���)
	public static final List<String> statusList= Arrays.asList(
					STATUS_OPENED, STATUS_UNPOST, STATUS_POST_EXPRESS,
					STATUS_POST_COMMON,STATUS_UNOPENED, STATUS_APPLY, 
					STATUS_APPLY_AGAIN, STATUS_OPEN_AGAIN);
	public static final Map<String, String> STATUSDESC_MAP = new HashMap<String, String>();
	static{
		STATUSDESC_MAP.put(STATUS_APPLY, "������");
		STATUSDESC_MAP.put(STATUS_OPENED, "��Ʊ�ѿ�");
		STATUSDESC_MAP.put(STATUS_UNPOST, "δ�ʼ�");
		STATUSDESC_MAP.put(STATUS_POST_EXPRESS, "���");
		STATUSDESC_MAP.put(STATUS_POST_COMMON, "ƽ��");
		STATUSDESC_MAP.put(STATUS_UNOPENED, "δ��");
		STATUSDESC_MAP.put(STATUS_OPEN, "�ѿ�");
		STATUSDESC_MAP.put(STATUS_UNOPEN, "δ��");
		STATUSDESC_MAP.put(STATUS_APPLY, "����");
		STATUSDESC_MAP.put(STATUS_TRASH, "������Ʊ");
		STATUSDESC_MAP.put(STATUS_APPLY_AGAIN, "���벹��");
		STATUSDESC_MAP.put(STATUS_OPEN_AGAIN, "�Ѳ���");
	}
	
}
