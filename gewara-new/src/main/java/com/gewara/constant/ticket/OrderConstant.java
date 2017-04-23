package com.gewara.constant.ticket;

import java.util.HashMap;
import java.util.Map;

public class OrderConstant {
	public static final String CHANGEHIS_KEY_CHANGESEAT = "changeSeat";	//������λ
	public static final String CHANGEHIS_KEY_SUCCESSCHANGE = "successChange";//�ɹ�����������λ
	public static final String CHANGEHIS_KEY_MPITO = "mpito";				//�������ε�
	public static final String CHANGEHIS_KEY_MPIFROM = "mpifrom";			//�ӳ�����
	public static final String CHANGEHIS_KEY_RECONFIRMS = "reconfirms";	//����ȷ�϶�������
	public static final String CHANGEHIS_KEY_BUYTIMES = "buytimes";		//���ֻ���ʷ��������
	public static final String CHANGEHIS_KEY_PROCESSTIMES = "processtimes";	//�ö����������
	
	public static final String OTHERKEY_PROCESSERROR = "processError";//
	public static final String OTHERKEY_CREDENTIALSID = "credentialsId";		//�̼���֤ID
	public static final String OTHERKEY_BINDMEMBER = "bindMember";				//�绰�µ����û�
	public static final String OTHERKEY_TELEPHONE = "telephone";				//�绰�µ������绰
	public static final String OTHERKEY_BINDMOBILE = "bindMobile";				//�绰�µ����ֻ�
	public static final String OTHERKEY_CREATEMEMBER = "createMember";			//�绰�µ��Ƿ񴴽�
	public static final String OTHERKEY_DELAY_CARDNO = "delayCardNo";      //����Ʊȯ�г�����Ʊȯ��
	
	public static final String OTHERKEY_GREETINGS = "greetings";				//���Ի�Ʊ��
	
	public static final String STATUS_NEW = "new";						//�¶�������λ������
	public static final String STATUS_NEW_UNLOCK = "new_unlock";		//�¶�����δ��������ʱ״̬��
	public static final String STATUS_NEW_CONFIRM = "new_confirm";		//�¶�����ȷ��ȥ����
	public static final String STATUS_PAID = "paid";					//��������δ���ǳɽ���
	public static final String STATUS_PAID_FAILURE = "paid_failure";	//���������д���
	public static final String STATUS_PAID_UNFIX = "paid_failure_unfix";//����������λδȷ��
	public static final String STATUS_PAID_SUCCESS = "paid_success";	//����󣬶����ɽ�
	public static final String STATUS_PAID_RETURN = "paid_return";		//��������ȡ���˿���
	public static final String STATUS_EMAIL_ID = "email_id";				//�ʼ�ID

	public static final String STATUS_CANCEL = "cancel";					//����ȡ����
	public static final String STATUS_SYS_CANCEL = "cancel_sys";		//ϵͳȡ��
	public static final String STATUS_SYS_CHANGE_CANCEL = "cancel_sys_change";	//�ɹ�����ϵͳ����ȡ��
	public static final String STATUS_REPEAT = "cancel_repeat";			//�ظ�����
	public static final String STATUS_USER_CANCEL = "cancel_user";		//�û�ȡ����
	public static final String STATUS_TIMEOUT = "cancel_timeout";		//��ʱȡ��
	public static final Map<String, String> statusMap = new HashMap<String, String>();
	
	public static final String UNIQUE_BY_MEMBERID = "memberid";			//��������Ψһ�Ա�ʶ��ʹ���û�Ψһ��
	public static final String UNIQUE_BY_MOBILE = "mobile";				//��������Ψһ�Ա�ʶ��ʹ���ֻ�
	public static final String UNIQUE_BY_MEMBER_AND_MOBILE = "all";	//��������Ψһ�Ա�ʶ��ʹ���ֻ�+�˺�
	public static final String UNIQUE_BY_PARTNERNAME = "partnername";	//��������Ψһ�Ա�ʶ��ʹ���ֻ�+�˺�
	
	public static final String ORDER_EXPRESSNO = "expressNo";			//��ݶ�����
	public static final String ORDER_EXPRESSMode = "expressMode";			//��ݶ�����
	public static final String SYSBANK_BUY = "buy";
	public static final String SYSBANK_GIFT = "gift";
	
	//��ѵ�̻�ȷ�϶���
	public static final String TRAINING_ORDER_IS_SURE = "isSure";
	
	//���̼ҽ���״̬
	public static final String SETTLE_NONE = "O";	//δ֪
	public static final String SETTLE_N = "N";		//������
	public static final String SETTLE_Y = "Y";		//����
	
	public static final String DISCOUNT_STATUS_Y = "Y";		//��ʹ�óɹ�
	public static final String DISCOUNT_STATUS_N = "N";		//δʹ�óɹ�

	//��������
	public static final String ORDER_TYPE_TICKET = "ticket";		//��ӰƱ
	public static final String ORDER_TYPE_GOODS = "goods";			//��Ʒ
	public static final String ORDER_TYPE_SPORT = "sport";			//�˶�
	public static final String ORDER_TYPE_DRAMA = "drama";			//����
	public static final String ORDER_TYPE_GYM = "gym";				//����
	public static final String ORDER_TYPE_PUBSALE = "pubsale";		//����
	public static final String ORDER_TYPE_GUARANTEE = "guarantee";	//��֤��
	public static final String ORDER_TYPE_MEMBERCARD = "membercard";//��Ա��
	
	//����ģ��
	public static final String ORDER_PRICATEGORY_MOVIE = "movie";		//��Ӱģ��	
	public static final String ORDER_PRICATEGORY_DRAMA = "drama";		//�ݳ�ģ��	
	public static final String ORDER_PRICATEGORY_SPORT = "sport";		//�˶�ģ��	
	public static final String ORDER_PRICATEGORY_GYM = "gym";			//����ģ��	
	public static final String ORDER_PRICATEGORY_BAR = "bar";			//�ư�ģ��	
	public static final String ORDER_PRICATEGORY_ACTIVITY = "activity";	//�ģ��	
	public static final String ORDER_PRICATEGORY_PUBSALE = "pubsale";	//����ģ��	
	public static final String ORDER_PRICATEGORY_POINT = "point";		//���ֶԶ�ģ��	
	public static final String ORDER_PRICATEGORY_GROUPON = "groupon";	//�Ź�ģ��	
	
	//Զ�̶���״̬
	public static final String REMOTE_STATUS_NEW = "N";		//�¶���
	public static final String REMOTE_STATUS_LOCK = "0";		//��λ����
	public static final String REMOTE_STATUS_FIXED = "1";		//�����ɹ�
	public static final String REMOTE_STATUS_UNLOCK = "2";		//��λ����
	public static final String REMOTE_STATUS_ERROR = "X";		//����
	public static final String REMOTE_STATUS_CANCEL = "C";		//��Ʊ 

	public static final String CHECKMARK_N = "N";		//δ���
	public static final String CHECKMARK_Y = "Y";		//���
	
	//���������������õ�ԭ��
	public static final String OTHERFEE_REASON_UMPAY = "umPay";						//����֧��������
	public static final String OTHERFEE_REASON_UMPAY_SH = "umPay_sh";				//����֧��������
	public static final String OTHERFEE_REASON_EXPRESS = "express";					//��ݷ���
	
	public static final String ORDER_TAKETIME = "taketime";							//ȡƱʱ��
	
	public static final Long CARD_DELAY_GOODSID = 152385164L;//������Ʒid    152385164
	static{
		statusMap.put(STATUS_CANCEL, "������ȡ��");
		statusMap.put(STATUS_REPEAT, "�ظ�����");
		statusMap.put(STATUS_USER_CANCEL, "�û�ȡ��");
		statusMap.put(STATUS_SYS_CANCEL, "ϵͳȡ��");
		statusMap.put(STATUS_SYS_CHANGE_CANCEL, "ϵͳ����ȡ��");
		statusMap.put(STATUS_NEW, "�¶���");
		statusMap.put(STATUS_NEW_CONFIRM, "�ȴ�����");
		statusMap.put(STATUS_NEW_UNLOCK, "��ʱ����");
		statusMap.put(STATUS_PAID, "����ɹ�");
		statusMap.put(STATUS_PAID_SUCCESS, "���׳ɹ�");
		statusMap.put(STATUS_PAID_FAILURE, "����������");
		statusMap.put(STATUS_PAID_UNFIX, "��λ������");
		statusMap.put(STATUS_PAID_RETURN, "�˿�ȡ��");
		statusMap.put(STATUS_TIMEOUT, "��ʱȡ��");
	}
}
