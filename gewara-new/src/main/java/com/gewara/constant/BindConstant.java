package com.gewara.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;

public class BindConstant {
	public static final String KEY_BINDTIME = "bindTime";	//���ֻ�ʱ�䣬�ֻ�ע��ֱ����ע��ʱ��

	public static final String TAG_REGISTERCODE = "registercode"; 	//��ȡ�ֻ�ע�ᡢ���ٵ�¼��̬��
	//public static final String TAG_DYNAMICCODE = "dynamiccode"; 	//��TAG_REGISTERCODE�ϲ�
	
	public static final String TAG_BINDMOBILE = "bindMobile"; 		//�ֻ����
	
	public static final String TAG_ACCOUNT_BACKPASS = "account_backpass"; //�ֻ��һ�֧������
	public static final String TAG_DYNAMICCODE_CARD = "dynamiccode_card"; //����Ʊȯ���ֻ���̬��
	public static final String TAG_BACKPASS = "backpass"; 		//�ֻ��һ�����
	public static final String TAG_MODIFYPASS = "modifypass"; 	//�޸�����

	public static final String TAG_SETPAYPASS = "setpaypass"; 	//����֧������
	public static final String TAG_MDYPAYPASS = "mdypaypass"; 	//�޸�֧������
	public static final String TAG_CHGBINDMOBILE = "chgbindMobile"; //�޸��ֻ���
	public static final String TAG_DRAWMOBILE = "drawMobile"; 		//�齱�ֻ���֤
	public static final String TAG_CCBANKCODE = "ccbankcode";	//�������ж�̬��
	public static final String TAG_GETPAYPASS = "getpaypass"; 	//�һ�֧������ TODO:��TAG_ACCOUNT_BACKPASS�����𣿣�
	public static final String TAG_VDEMAIL_BY_UPDATEPWD = "vdemailbyuppwd"; //�޸�����ǰ�����䰲ȫ��֤

	
	//TODO:��֯ר�ų�����������ݿ�
	public static final List<String> VALID_TAG_LIST = Arrays.asList(
			TAG_REGISTERCODE,
			TAG_BINDMOBILE,
			TAG_ACCOUNT_BACKPASS,
			TAG_DYNAMICCODE_CARD,
			TAG_BACKPASS,
			TAG_MODIFYPASS,
			TAG_SETPAYPASS,
			TAG_MDYPAYPASS,
			TAG_CHGBINDMOBILE,
			TAG_DRAWMOBILE,
			TAG_CCBANKCODE,
			TAG_GETPAYPASS,
			TAG_VDEMAIL_BY_UPDATEPWD
		);

	//Ĭ�϶���
	public static final String DEFAULT_TEMPLATE = "checkpass��̬�룬ʹ�ú�30���ӹ�����Ч���Ǳ��˻���Ȩ������Ϊȷ���˻���ȫ�����µ�4000406506";
	public static final String ADMIN_MOBILE_TEMPLATE = "checkpass��лʹ�ø������绰��Ʊ���񣬴���֤������У���û���Ϣ�Ƿ�Ϸ�����Ǳ��˲�������Դ���Ϣ����ϵ�������ͷ���ѯ��4000-406-506";
	public static final int VALID_MIN = 30;			//��Чʱ��(MINUTE)
	public static final int MAX_CHECKNUM = 5;
	public static final int MAX_SENDNUM = 99999;
	
	private static final Map<String, Integer> SENDNUM_MAP;
	private static final Map<String, Integer> MAXCHECK_MAP;
	private static final Map<String, String> TEMPLATE_MAP;
	//Ĭ�����������
	
	static{
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put(TAG_REGISTERCODE, "checkpassע�ᶯ̬�룬ʹ�ú�30���ӹ�����Ч���Ǳ��˻���Ȩ������Ϊȷ���˻���ȫ�����µ�4000406506");
		tmp.put(TAG_CCBANKCODE, "checkpass֧����̬�룬ʹ�ú�30���ӹ�����Ч���Ǳ��˻���Ȩ������Ϊȷ���˻���ȫ�����µ�4000406506");
		TEMPLATE_MAP = UnmodifiableMap.decorate(tmp);
		
		Map<String, Integer> tmp2 = new HashMap<String, Integer>();
		tmp2.put(TAG_REGISTERCODE, 20);		//ע����һ�ֻ�ֻ����20��
		tmp2.put(TAG_MODIFYPASS, 20);
		tmp2.put(TAG_BINDMOBILE, 20);
		SENDNUM_MAP = UnmodifiableMap.decorate(tmp2);
		
		tmp2 = new HashMap<String, Integer>();
		tmp2.put(TAG_MODIFYPASS, 8);
		tmp2.put(TAG_SETPAYPASS, 8);
		MAXCHECK_MAP = UnmodifiableMap.decorate(tmp2);
	}

	public static String getMsgTemplate(String tag) {
		if(TEMPLATE_MAP.containsKey(tag)) return TEMPLATE_MAP.get(tag);
		return DEFAULT_TEMPLATE;
	}
	public static int getMaxSendnum(String tag){
		if(SENDNUM_MAP.containsKey(tag)) return SENDNUM_MAP.get(tag);
		return MAX_SENDNUM;
	}
	public static int getMaxCheck(String tag){
		if(MAXCHECK_MAP.containsKey(tag)) return MAXCHECK_MAP.get(tag);
		return MAX_CHECKNUM;
	}

}
