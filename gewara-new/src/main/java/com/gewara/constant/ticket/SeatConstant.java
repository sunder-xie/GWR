package com.gewara.constant.ticket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SeatConstant {
	public static final String STATUS_NEW = "A";//����λ
	public static final String STATUS_SELLING = "W";//�۳�δ����
	public static final String STATUS_SOLD = "S";//�۳�
	
	public static final String STATUS_LOCKB = "B";//ӰԺ�۳�����
	public static final String STATUS_LOCKC = "C";//������λ����
	public static final String STATUS_LOCKD = "D";//��Ʊ����
	
	public static final String SEAT_TYPE_A = "A";//ͨ����λ
	public static final String SEAT_TYPE_B = "B";
	public static final String SEAT_TYPE_C = "C";
	public static final String SEAT_TYPE_D = "D";
	private static final Map<String, String> statusTextMap = new HashMap<String, String>();
	static{
		statusTextMap.put("B", "ӰԺ�۳�����");
		statusTextMap.put("C", "������λ����");
		statusTextMap.put("D", "��Ʊ����");
	}
	public static final List<String> STATUS_LOCK_LIST = Arrays.asList("B","C","D");

}
