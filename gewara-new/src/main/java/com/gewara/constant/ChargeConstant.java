package com.gewara.constant;

import org.apache.commons.lang.StringUtils;

public abstract class ChargeConstant {
	public static final String TYPE_CHARGE = "charge";			//��ֵ
	public static final String TYPE_ORDER = "order";			//����֧����ת
	
	public static final String BANKPAY = "bank";			//�˻�
	public static final String WABIPAY = "wabi";			//�߱�
	public static final String DEPOSITPAY = "deposit";			//��֤��
	
	public static final String KEY_CHARGE_VALIDTIME_ = "CHARGE_VALIDTIME_";
	
	public static boolean isBankPay(String chargetype){
		return StringUtils.equals(chargetype, TYPE_ORDER);
	}
}
