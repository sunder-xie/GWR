package com.gewara.constant;

public abstract class SysAction {
	public static final String APPLY_TYPE_SELFSERVICE="selfservice";//�û��Լ�
	public static final String APPLY_TYPE_CUSTOMSERVICE="customservice";//�ͷ���̨����
	
	public static final String STATUS_APPLY = "apply";
	public static final String STATUS_AGREE = "agree";
	public static final String STATUS_REFUSE = "refuse";
	public static final String STATUS_RESULT = "result";	//ϵͳ��Ϣר��
	
	
	public static final String ACTION_APPLY_FRIEND_ADD = "apply_friend_add"; //�Ӻ���
	public static final String ACTION_APPLY_COMMU_INVITE = "apply_commu_invite";//Ȧ������
	public static final String ACTION_APPLY_C_ADMIN = "apply_c_admin";//�õ�-->Ȧ��������Ա
	public static final String ACTION_APPLY_COMMU_JOIN = "apply_commu_join";//�������Ȧ��
	public static final String ACTION_TICKET_SUCCESS = "ticket_sucess";//�ɹ���Ʊ
	public static final String ACTION_GETPOINT = "getpoint";//��ȡ����
	
	public static final String ACTION_COMMU_APPLYCERTIFICATION = "commu_apply"; 	// Ȧ��������֤
	public static final String ACTION_FRIEND_BIRTHDAY = "friend_birthday";			// ��������ף��
	
	public static final String ACTION_QUESTION_NEW_ANSWER = "question_new_answer"; 	//�������»ش�
	public static final String ACTION_ANSWER_IS_BEST = "answer_is_best";			//��ѻش�

	public static final String ACTION_ACTIVITY_JOIN = "activity_join";				//�μӻ
}
