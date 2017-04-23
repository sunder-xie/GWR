package com.gewara.constant;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.lang.StringUtils;

public class DramaConstant {
	public static final String TYPE_DRAMA = "drama";
	public static final String TYPE_CONCERT = "concert";				//�ݳ���
	public static final String TYPE_MUSICALE = "musicale";				//���ֻ�
	public static final String TYPE_MUSICALPLAY = "musicalplay";		//���־�
	public static final String TYPE_DANCE = "dance";					//�赸
	public static final String TYPE_ACROBATICS = "acrobatics";			//�����Ӽ�
	public static final String TYPE_RACE = "race";						//��������
	public static final String TYPE_SHOW = "show";						//չ��
	public static final String TYPE_OTHER = "other";					//����
	
	public static final String PRETYPE_ENTRUST = "E"; 					//ί�д���
	public static final String PRETYPE_MANAGE = "M";					//������Ӫ
	
	//���硢�ݳ��ᡢ���־硢�赸��չ�ᡢ�����Ӽ����������¡���ͯ���ӡ�������Ʊ	
	public static final String TYPE_CHILDREN = "children"; 				//��ͯ����
	public static final String TYPE_ATTRACTICKET = "attracticket"; 		//������Ʊ
	public static final Map<String, String> dramaTypeMap;
	public static final Map<String, String> dramaSaleCycleMap;
	public static final Map<String, String> pretypeMap;
	static{
		Map<String, String> tmpMap = new LinkedHashMap<String, String>();
		tmpMap.put(TYPE_DRAMA, "����");
		tmpMap.put(TYPE_CONCERT, "�ݳ���");
		tmpMap.put(TYPE_MUSICALE, "���ֻ�");
		tmpMap.put(TYPE_MUSICALPLAY, "���־�");
		tmpMap.put(TYPE_DANCE, "�赸");
		tmpMap.put(TYPE_ACROBATICS, "�����Ӽ�");
		tmpMap.put(TYPE_RACE, "��������");
		tmpMap.put(TYPE_SHOW, "չ��");
		tmpMap.put(TYPE_CHILDREN, "��ͯ����");
		tmpMap.put(TYPE_ATTRACTICKET, "������Ʊ");
		tmpMap.put(TYPE_OTHER, "����");
		dramaTypeMap = UnmodifiableMap.decorate(tmpMap);
		Map<String, String> cycleTmpMap = new LinkedHashMap<String, String>();
		cycleTmpMap.put("1", "��һ����");
		cycleTmpMap.put("2", "�ڶ�����");
		cycleTmpMap.put("3", "��������");
		cycleTmpMap.put("4", "��������");
		cycleTmpMap.put("5", "��������");
		dramaSaleCycleMap = UnmodifiableMap.decorate(cycleTmpMap);
		Map<String, String> tmpPretypeMap = new LinkedHashMap<String, String>();
		tmpPretypeMap.put(PRETYPE_MANAGE, "������");
		tmpPretypeMap.put(PRETYPE_ENTRUST, "������");
		pretypeMap = UnmodifiableMap.decorate(tmpPretypeMap);
	}
	
	public static String getDramaTypeText(String type){
		String tmp = dramaTypeMap.get(type);
		if(StringUtils.isBlank(tmp)) return "";
		return tmp;
	}
}
