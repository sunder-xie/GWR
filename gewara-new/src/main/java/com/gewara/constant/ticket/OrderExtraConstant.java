package com.gewara.constant.ticket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.lang.StringUtils;

public abstract class OrderExtraConstant {
	public static final String EXPRESS_YUNDA = "YUNDA";		//�ϴ���
	public static final String EXPRESS_SF = "SF";			//˳����
	
	public static final List<String> EXPRESS_TYPE_LIST = Arrays.asList(EXPRESS_YUNDA, EXPRESS_SF);
	public static final Map<String,String> EXPRESS_TYPE_TEXT_MAP = new HashMap<String, String>();
	static{
		EXPRESS_TYPE_TEXT_MAP.put(EXPRESS_YUNDA, "�ϴ���");
		EXPRESS_TYPE_TEXT_MAP.put(EXPRESS_SF, "˳����");
	}
	
	public static String getExpressTypeText(String expresstype){
		String tmp = EXPRESS_TYPE_TEXT_MAP.get(expresstype);
		if(StringUtils.isNotBlank(tmp)) return tmp;
		return "δ֪";
	}
	
	public static final String EXPRESS_STATUS_NEW = "new";				//������
	public static final String EXPRESS_STATUS_PRINT = "print";          //��ɴ�ӡ�����
	public static final String EXPRESS_STATUS_ALLOCATION = "allocation";//������
	public static final String EXPRESS_STATUS_TRANSIT = "transit";		//������
	public static final String EXPRESS_STATUS_SIGNFAIL = "signfail";	//ǩ��ʧ��
	public static final String EXPRESS_STATUS_SIGNED = "signed";		//ǩ�ճɹ�
	
	public static final Map<String,String> EXPRESS_DEALSTATUS_STEP;		//������̲���
	public static final Map<String,String> EXPRESS_STATUS_TEXT;			//״̬˵��
	static{
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put(EXPRESS_STATUS_NEW, "������");
		tmp.put(EXPRESS_STATUS_PRINT, "��ɴ�ӡ�����");
		tmp.put(EXPRESS_STATUS_ALLOCATION, "������");
		tmp.put(EXPRESS_STATUS_TRANSIT, "������");
		tmp.put(EXPRESS_STATUS_SIGNFAIL, "ǩ��ʧ��");
		tmp.put(EXPRESS_STATUS_SIGNED, "ǩ�ճɹ�");
		EXPRESS_STATUS_TEXT = UnmodifiableMap.decorate(tmp);
		Map<String, String> tmpStep = new HashMap<String, String>();
		tmpStep.put(EXPRESS_STATUS_PRINT, EXPRESS_STATUS_NEW);
		tmpStep.put(EXPRESS_STATUS_ALLOCATION, EXPRESS_STATUS_PRINT);
		tmpStep.put(EXPRESS_STATUS_TRANSIT, EXPRESS_STATUS_ALLOCATION);
		tmpStep.put(EXPRESS_STATUS_SIGNFAIL, EXPRESS_STATUS_TRANSIT);
		tmpStep.put(EXPRESS_STATUS_SIGNED, EXPRESS_STATUS_TRANSIT);
		EXPRESS_DEALSTATUS_STEP = UnmodifiableMap.decorate(tmpStep);
	}
	
	public static final String DEAL_TYPE_FRONT = "front";				//��������:ǰ��
	public static final String DEAL_TYPE_BACKEND = "backend";			//��������:���
	
	public static final String INVOICE_N = "N";							//�ɿ���Ʊ
	public static final String INVOICE_F = "F";							//���ܿ���Ʊ
	public static final String INVOICE_Y = "Y";							//�ѿ���Ʊ
	
	public static final String PRETYPE_ENTRUST = "E"; 					//ί�д���
	public static final String PRETYPE_MANAGE = "M";					//������Ӫ
	
}
