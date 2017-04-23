package com.gewara.constant;

import java.util.HashMap;
import java.util.Map;

public class MemberCardConstant {
	public static final Map<String, String> cardtypeMap = new HashMap<String, String>();
	public static final Map<String, String> cardstatusMap = new HashMap<String, String>();
	public static final String CARD_TYPE_NUMBER = "1";
	public static final String CARD_TYPE_AMOUNT = "2";
	public static final String CARD_STATUS_Y = "1";
	public static final String CARD_STATUS_S = "2";
	public static final String CARD_STATUS_O = "3";
	
	public static final String CHECKPAS_TYPE_CPAY = "cpay";
	public static final String CHECKPAS_TYPE_MCARD = "mcard";
	public static final String SMSPASS = "smspass";
	
	public static final String CUS_TRADENO = "cusTradeNo";
	
	public static final String VIPCARD = "����";
	
	//public static final String VIPCARD = "����";
	public static final String KEY_MEMBERCARDMSG = "memberCardmsg";
	
	public static final Integer MIN_OVERNUM = 5;
	
	static{
		cardtypeMap.put(CARD_TYPE_NUMBER, "�ο�");
		cardtypeMap.put(CARD_TYPE_AMOUNT, "��");
		
		cardstatusMap.put(CARD_STATUS_Y, "����");
		cardstatusMap.put(CARD_STATUS_S, "ͣ��");
		cardstatusMap.put(CARD_STATUS_O, "����");
	}
}
