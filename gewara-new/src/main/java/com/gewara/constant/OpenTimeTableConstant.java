package com.gewara.constant;

import java.util.ArrayList;
import java.util.List;
public abstract class OpenTimeTableConstant {
	public static final int MAX_MINUTS_TICKETS = 15;	//���������ʱ�䣨���ӣ�
	public static final int MAX_HOUR_TICKETS = 12;		//���������ʱ�䣨Сʱ��
	
	public static final String STATUS_BOOK = "Y"; 								// ����Ԥ��
	public static final String STATUS_NOBOOK = "N"; 							// ������Ԥ��
	public static final String STATUS_DISCARD = "D"; 							// ����
	public static final String VERSION_V2 = "2.0";
	
	public static final String OPEN_TYPE_PERIOD = "period";						//ʱ��
	public static final String OPEN_TYPE_FIELD = "field";						//����
	public static final String OPEN_TYPE_INNING = "inning"; 					//����
	
	public static final String UNIT_TYPE_WHOLE = "whole";						//����ʱ��
	public static final String UNIT_TYPE_TIME = "time";							//��λʱ��
	
	public static final String SALE_STATUS_LOCK = "lock";						//����
	public static final String SALE_STATUS_UNLOCK = "unlock";					//δ����
	public static final String SALE_STATUS_SUCCESS = "success";					//���۳ɹ�
	public static final String SALE_STATUS_SUCCESS_PAID = "success_paid";		//����֧���ɹ�
	
	public static final String ITEM_TYPE_COM = "0";			//��ͨ����
	public static final String ITEM_TYPE_VIP = "1";			//��Ա����
	public static final String ITEM_TYPE_VIE = "2";			//���ĳ���
	
	public static final String KEY_OPENTIMESALE_OPENTIME_ = "OPENTIMESALE_OPENTIME_";
	public static final String KEY_OPENTIMESALE_CLOSETIME_ = "OPENTIMESALE_CLOSETIME_";
	
	public static final String GUARANTEE_UNPAY = "unpay";		//δ����֤��
	
	public static List<String> opentypeList;
	static{
		opentypeList = new ArrayList<String>();
		opentypeList.add(OPEN_TYPE_FIELD);
		opentypeList.add(OPEN_TYPE_PERIOD);
		opentypeList.add(OPEN_TYPE_INNING);
	}
	public static List<String> getOpentypeList() {
		return opentypeList;
	}
	public boolean isValidOpentype(String opentype){
		return opentypeList.contains(opentype);
	}
}
