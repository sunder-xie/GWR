package com.gewara.constant.order;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.lang.StringUtils;

public abstract class ElecCardConstant implements Serializable {

	private static final long serialVersionUID = 8398429592873502608L;
	
	//�������ԣ���ʷ������ʹ��
	public static final String DATA_HIS = "data_his";
	public static final String DATA_NOW = "data_now";
	public static final String DATA_ALL = "data";
	
	public static final String STATUS_NEW = "N";	//����
	public static final String STATUS_SOLD = "Y";	//�۳�
	public static final String STATUS_DISCARD = "D";//����
	public static final String STATUS_USED = "U";   //ʹ�ù�
	public static final String STATUS_LOCK = "L";   //����ʹ��, Y------>L

	
	
	public static final String EDITION_ALL = "ALL";		//���а汾����
	public static final String EDITION_3D = "3D";		//3D����
	public static final String EDITION_2D = "2D";		//3D����
	private static final Map<String, String> imaxMap;
	private static final Map<String, String> normalMap;
	public static final Map<String, String> CARDTYPEMAP = new HashMap<String, String>();
	static{
		Map<String, String> imaxTmp = new HashMap<String, String>(3);
		imaxTmp.put("A", "IMAXȯ");
		imaxTmp.put("B", "��ȯ");
		imaxTmp.put("D", "�ֿ�ȯ");
		imaxMap = UnmodifiableMap.decorate(imaxTmp);
		Map<String, String> normalTmp = new HashMap<String, String>(3);
		normalTmp.put("A", "��ȯ����ȯ");
		normalTmp.put("B", "��ȯ");
		normalTmp.put("D", "�ֿ�ȯ");
		normalMap = UnmodifiableMap.decorate(normalTmp);
		
		CARDTYPEMAP.put("A", "�һ�ȯ");
		CARDTYPEMAP.put("B", "����ȯ");
		CARDTYPEMAP.put("C", "�Ż�ȯ");
		CARDTYPEMAP.put("D", "�Ż�ȯ");
		CARDTYPEMAP.put("E", "��ֵȯ");
	}
	
	public static Map<String, String> getNormalMap(){
		return normalMap;
	}
	
	public static Map<String, String> getImaxMap(){
		return imaxMap;
	}
	
	public static String getCardtype(String cardtype) {
		if(StringUtils.isBlank(cardtype)) return "";
		return CARDTYPEMAP.get(cardtype);
	}

}
