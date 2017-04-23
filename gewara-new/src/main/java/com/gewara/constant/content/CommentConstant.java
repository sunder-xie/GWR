package com.gewara.constant.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public abstract class CommentConstant {

	public static final String WALA_EXP = "\\[(0[1-9]|0[1-4][0-9]|05[0-5])\\]"; 
	public static final Map<String, String> EXPMAP = new HashMap<String, String>();
	static{
		EXPMAP.put("[01]", "[����]");	EXPMAP.put("[02]", "[�ɰ�]");	EXPMAP.put("[03]", "[�Ǻ�]");	EXPMAP.put("[04]", "[����]");	EXPMAP.put("[05]", "[����]");
		EXPMAP.put("[06]", "[����]");	EXPMAP.put("[07]", "[Ү]");	EXPMAP.put("[08]", "[��������]");	EXPMAP.put("[09]", "[����]");	EXPMAP.put("[010]", "[����]");
		EXPMAP.put("[011]", "[��]"); EXPMAP.put("[012]", "[��]"); EXPMAP.put("[013]", "[��]"); EXPMAP.put("[014]", "[����]"); EXPMAP.put("[015]", "[����]");
		EXPMAP.put("[016]", "[�Ծ�]"); EXPMAP.put("[017]", "[��]"); EXPMAP.put("[018]", "[����]"); EXPMAP.put("[019]", "[��]"); EXPMAP.put("[020]", "[��]");
		EXPMAP.put("[021]", "[����]"); EXPMAP.put("[022]", "[�ʼ�]"); EXPMAP.put("[023]", "[�绰]"); EXPMAP.put("[024]", "[�ɱ�]"); EXPMAP.put("[025]", "[��л]"); 
		EXPMAP.put("[026]", "[����]");	EXPMAP.put("[027]", "[good]");	EXPMAP.put("[028]", "[����]");	EXPMAP.put("[029]", "[����]");	EXPMAP.put("[030]", "[����]");
		EXPMAP.put("[031]", "[���]");	EXPMAP.put("[032]", "[��]");	EXPMAP.put("[033]", "[��з]");	EXPMAP.put("[034]", "[��]");	EXPMAP.put("[035]", "[����]");
		EXPMAP.put("[036]", "[��]"); EXPMAP.put("[037]", "[����]"); EXPMAP.put("[038]", "[����]"); EXPMAP.put("[039]", "[����]"); EXPMAP.put("[040]", "[����]");
		EXPMAP.put("[041]", "[��]"); EXPMAP.put("[042]", "[ŭ]"); EXPMAP.put("[043]", "[˥]"); EXPMAP.put("[044]", "[ʧ��]"); EXPMAP.put("[045]", "[����]");
		EXPMAP.put("[046]", "[˯��]"); EXPMAP.put("[047]", "[̫��]"); EXPMAP.put("[048]", "[����]"); EXPMAP.put("[049]", "[��]"); EXPMAP.put("[050]", "[��]");
		EXPMAP.put("[051]", "[ҩ]"); EXPMAP.put("[052]", "[����]"); EXPMAP.put("[053]", "[��]"); EXPMAP.put("[054]", "[ץ��]"); EXPMAP.put("[055]", "[����]");
	}
	
	public static String getExpMap(String key){
		if(StringUtils.isBlank(key)) return "";
		String str = EXPMAP.get(key);
		if(str == null) return key;
		return str;
	}
}
