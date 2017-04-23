package com.gewara.constant;

import java.util.Arrays;
import java.util.List;

public class MemberConstant {
	// ע����Դ
	public static final String REGISTER_EMAIL = "email"; //����ע��
	public static final String REGISTER_MOBLIE = "mobile"; // �ֻ�ע��
	public static final String REGISTER_CODE = "code"; //��̬��
	public static final String REGISTER_APP = "app"; //������¼
	
	// ��ҳȨ��
	public static final String RIGHTS_INDEX_PUBLIC = "index_public";
	public static final String RIGHTS_INDEX_FRIEND = "index_friend";
	public static final String RIGHTS_INDEX_PRIVATE = "index_private";
	// ���Ȩ��
	public static final String RIGHTS_ALBUM_PUBLIC = "album_public";
	public static final String RIGHTS_ALBUM_FRIEND = "album_friend";
	public static final String RIGHTS_ALBUM_PRIVATE = "album_private";
	// ����Ȩ��
	public static final String RIGHTS_FRIEND_PUBLIC = "friend_public";
	public static final String RIGHTS_FRIEND_FRIEND = "friend_friend";
	public static final String RIGHTS_FRIEND_PRIVATE = "friend_private";
	// Ȧ��Ȩ��
	public static final String RIGHTS_COMMU_PUBLIC = "commu_public";
	public static final String RIGHTS_COMMU_FRIEND = "commu_friend";
	public static final String RIGHTS_COMMU_PRIVATE = "commu_private";
	// ����Ȩ��
	public static final String RIGHTS_TOPIC_PUBLIC = "topic_public";
	public static final String RIGHTS_TOPIC_FRIEND = "topic_friend";
	public static final String RIGHTS_TOPIC_PRIVATE = "topic_private";
	
	// �Ȩ��
	public static final String RIGHTS_ACTIVITY_PUBLIC = "activity_public";
	public static final String RIGHTS_ACTIVITY_FRIEND = "activity_friend";
	public static final String RIGHTS_ACTIVITY_PRIVATE = "activity_private";
	
	// ֪��Ȩ��
	public static final String RIGHTS_QA_PUBLIC = "qa_public";
	public static final String RIGHTS_QA_FRIEND = "qa_friend";
	public static final String RIGHTS_QA_PRIVATE = "qa_private";
	
	//����Ȩ��
	public static final String RIGHTS_AGENDA_PUBLIC = "agenda_public";
	public static final String RIGHTS_AGENDA_FRIEND = "agenda_friend";
	public static final String RIGHTS_AGENDA_PRIVATE = "agenda_private";
	
	//��������

	public static final String NEWTASK = "newtask";
	//����
	public static final String TASK_CONFIRMREG = "confirmreg";		//ע�������ȷ��
	public static final String TASK_SENDWALA = "sendwala";			//����һ������
	public static final String TASK_JOINCOMMU = "joincommu";		//����һ��Ȧ��
	public static final String TASK_FIVEFRIEND= "fivefriend";		//�Ѿ���5λ����
	public static final String TASK_UPDATE_HEAD_PIC= "headpic"; 	//����ͷ��
	public static final String TASK_BINDMOBILE = "bindmobile";		//���ֻ�

	public static final String TASK_BUYED_TICKET = "buyticket"; 	//�ɹ������ӰƱ
	public static final String TASK_MOVIE_COMMENT = "moviecomment";	//���һ��Ӱ��
	
	//public static final String TASK_FINISHED = "finished";//��ɲ���ȡ
	public static final List<String> TASK_LIST = Arrays.asList(
			TASK_UPDATE_HEAD_PIC, TASK_BUYED_TICKET, TASK_MOVIE_COMMENT,
			TASK_BINDMOBILE, TASK_CONFIRMREG, TASK_FIVEFRIEND, TASK_SENDWALA, TASK_JOINCOMMU);
	
	//~~~~~~~~~~~~������¼~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final String TAG_SOURCE = "bindstatus"; //���Զ�ע���û���otherinfo��ʶ
	public static final String TAG_MOBILE_BINDTIME = "mblbindtime";
	public static final String TAG_EMAIL_BINDTIME = "emlbindtime";
	public static final String TAG_DANGER = "danger";
	public static final String SOURCE_ALIPAY = "alipay";	//֧����
	public static final String SOURCE_SDO = "sdo";
	public static final String SOURCE_SINA = "sina";
	public static final String SOURCE_QQ ="qq";
	public static final String SOURCE_TENCENT = "tencent"; //��ѶQQ
	public static final String SOURCE_RENREN="renren";
	public static final String SOURCE_NETEASE="netease";//����
	public static final String SOURCE_KAIXIN="kaixin";//����
	public static final String SOURCE_MSN="msn";//msn
	public static final String SOURCE_DOUBAN = "douban"; //����
	public static final String SOURCE_CHINAPAY = "chinapay"; //����
	public static final String SOURCE_139EMAIL = "139email"; //139����
	public static final String SOURCE_DYNCODE = "dyncode"; //�ֻ���̬���¼
	public static final String SOURCE_TAOBAO = "taobao";
	
	public static final String SOURCE_WEIXIN = "weixin";
	
	
	public static final String SHORT_ALIPAY = "a";
	public static final String SHORT_SDO = "s";
	public static final String SHORT_SINA = "S";
	public static final String SHORT_QQ ="q";
	public static final String SHORT_TENCENT = "t";
	public static final String SHORT_RENREN="r";
	public static final String SHORT_NETEASE="n";//����
	public static final String SHORT_KAIXIN="k";//����
	public static final String SHORT_MSN= "m";//msn
	public static final String SHORT_DOUBAN = "d";
	public static final String SHORT_CHINAPAY = "c";
	public static final String SHORT_139EMAIL = "e";
	
	public static final String CATEGORY_ALIKUAIJIE = "alikuaijie";
	public static final String CATEGORY_ALIWALLET = "aliwallet";
	
	//~~~~~~~~~~~~~~~~~~~~�û���Ϊ��¼~~~~~~~~~~~~~~~
	//public static final String ACTION_RELEASECARD = "releaseCard";//���
	public static final String ACTION_REGCARD = "regcard";		//�󶨿�
	public static final String ACTION_MODPWD = "modpwd";		//�޸�����
	public static final String ACTION_NEWTASK = "newtask";		//�����������
	public static final String ACTION_SETPAYPWD = "setpaypwd";	//����֧������
	public static final String ACTION_GETPAYPWD = "getpaypwd";	//�һ�֧������
	public static final String ACTION_MDYPAYPWD = "mdypaypwd";	//�޸�֧������
	public static final String ACTION_MODEMAIL = "modemail";	//�޸�����
	public static final String ACTION_BINDMOBILE = "bindmobile";//���ֻ�
	public static final String ACTION_VDDRAWMOBILE = "validdrawmobile";//��֤�齱�ֻ�
	public static final String ACTION_CHGBINDMOBILE = "chgbindmobile";//�޸İ��ֻ�
	public static final String ACTION_RELIEVEMOBILE = "relievemobile";//����ֻ�
	public static final String ACTION_TOWABI = "towabi";		//�˻����תwabi
	public static final String ACTION_DROPMESS = "drpmessage";		//ɾ��˽��
	/*
	public static final String ACTION_LOGIN = "login";
	
	public static final String ACTION_ORDER = "changeorder";*/

	//public static final String 
	
	public static final String OM_MOBILE = "mobile";			//�������ֻ���
	public static final String OPENMEMBER = "openMember";		//��¼�ǵ������û���½
	public static final String OMSOURCE = "omsource";			//��������¼�û���Դ
	
	public static final String ALIWALLET_SHORTTOKEN = "aliwallet_shorttoken";	//֧����Ǯ����token
	public static final String ALIWALLET_LONGTOKEN = "aliwallet_longtoken";		//֧����Ǯ����token
	public static final String ALIWALLET_EXPIRESIN = "aliwallet_expiresIn";		//֧����Ǯ����token
	public static final String ALIWALLET_REEXPIRESIN = "aliwallet_reExpiresIn";		//֧����Ǯ����token
	public static final String ALIWALLET_SHORTVALIDTIME = "aliwallet_shortvalidtime";	//֧����Ǯ����token
	public static final String ALIWALLET_LONGVALIDTIME = "aliwallet_longvalidtime";
	public static final String ALIWALLET_EXTERN_TOKEN = "extern_token";
	
	public static final String BINDMOBILE_STATUS_Y = "Y";	//��
	public static final String BINDMOBILE_STATUS_N = "N";	//δ��
	public static final String BINDMOBILE_STATUS_YS = "Y_S";	//�ֻ���ͨ����֤��
	public static final String BINDMOBILE_STATUS_X = "X";	//δ֪
}
