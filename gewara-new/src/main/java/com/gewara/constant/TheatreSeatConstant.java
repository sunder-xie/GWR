package com.gewara.constant;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheatreSeatConstant implements Serializable {
	private static final long serialVersionUID = -3625538381513198225L;
	public static final String STATUS_NEW = "A";//����λ
	public static final String STATUS_SELLING = "W";//�۳�δ����
	public static final String STATUS_SOLD = "S";//�۳�
	
	public static final String STATUS_LOCKB 	= "B";//ӰԺ�۳�����(�Լ�����)
	public static final String STATUS_LOCKC 	= "C";//������λ����
	public static final String STATUS_LOCKD 	= "D";//��Ʊ����
	
	public static final String SEATMAP_KEY = "ODI_AREA_SEATMAP_";
	public static final String SEATMAP_UPDATE = "ODI_AREA_SEATMAP_UPDATE_";

	private static final Map<String, String> statusTextMap = new HashMap<String, String>();
	public static final List<String> STATUS_LOCK_LIST = Arrays.asList(STATUS_LOCKB, STATUS_LOCKC, STATUS_LOCKD);
	static{
		statusTextMap.put("B", "�����۳�����");
		statusTextMap.put("C", "������λ����");
		statusTextMap.put("D", "��Ʊ����");
	}

}
