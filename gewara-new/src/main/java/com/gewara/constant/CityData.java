package com.gewara.constant;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.lang.StringUtils;

public class CityData {
	
	//public static final String key = "ABQIAAAAYudc4zhQsoXevWVqIbF59xQ1ErC534tTScgxL7Pp5vQuB85ZrBSUhrFItaXNbeNJBRlot8loFR4jjg";
	//private CityData(){}
	private static final Map<String, String> proMap;
	static{
		Map<String, String> tmpMap = new LinkedHashMap<String, String>();
		tmpMap.put("0000", "ֱϽ��");
		tmpMap.put("44", "�㶫ʡ");
		tmpMap.put("32", "����ʡ");
		tmpMap.put("33", "�㽭ʡ");
		tmpMap.put("42", "����ʡ");
		tmpMap.put("51", "�Ĵ�ʡ");
		proMap = UnmodifiableMap.decorate(tmpMap);
	}
	public static Map<String, String> getOtherCityNames(){
		Map<String, String> others = AdminCityContant.getCitycode2CitynameMap();
		others.remove(AdminCityContant.CITYCODE_SH);
		return others;
	}
	public static Map<String, Map<String,String>> getCityNameMap(String citycodes){
		String[] codeArr = StringUtils.split(citycodes, ",");
		Map<String,Map<String,String>> cityMap = new LinkedHashMap<String,Map<String,String>>(); 
		if(codeArr == null || codeArr.length == 0)return cityMap;
		String cityName = null;
		Map<String,String> subCityMap = null;
		for (String citycode : codeArr) {
			if(citycode.indexOf("0000") != -1){
				cityName = proMap.get("0000");
			}else{
				cityName = proMap.get(citycode.substring(0, 2));
			}
			subCityMap = cityMap.get(cityName);
			if(subCityMap == null){
				subCityMap = new LinkedHashMap<String,String>();
				cityMap.put(cityName, subCityMap);
			}
			subCityMap.put(AdminCityContant.getCitycode2PinyinMap().get(citycode), AdminCityContant.allcityMap.get(citycode));
		}
		return cityMap;
	}
	/*public static String getKey() {
		return key;
	}*/
	
	//��ͼ��ʹ��
	public static String[] getCenter(String citycode){
		Map<String, String[]> pointMap = AdminCityContant.getCitycodeBPointMap();
		String[] str = pointMap.get(citycode);
		if(str==null) return pointMap.get("310000");
		return str;
	}
}
