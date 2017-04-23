package com.gewara.constant.app;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;

/**
 * Ӧ�������ֵ�
 * 
 * @author taiqichao
 * 
 */
public class AppType {

	public static final String APP_TYPE_CINAME = "cinema";
	public static final String APP_TYPE_SPORT = "sport";
	public static final String APP_TYPE_BAR = "bar";
	public static final String APP_TYPE_CINAME_CMCC="cinema_cmcc";

	public static final Map<String, String> APPTYPE_MAP;

	static {
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put(APP_TYPE_CINAME, "��Ӱ");
		tmp.put(APP_TYPE_BAR, "�ư�");
		tmp.put(APP_TYPE_SPORT, "�˶�");
		tmp.put(APP_TYPE_CINAME_CMCC, "�ƶ�android�ͻ���");
		APPTYPE_MAP = UnmodifiableMap.decorate(tmp);
	}
}
