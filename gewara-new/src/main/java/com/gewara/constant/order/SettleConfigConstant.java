package com.gewara.constant.order;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class SettleConfigConstant implements Serializable {
	private static final long serialVersionUID = 1607327403420971718L;

	public static final String DISCOUNT_TYPE_PERCENT = "percent";			//����ٷֱ�
	public static final String DISCOUNT_TYPE_UPRICE = "uprice";			//��Ʒ�ۿ�
	
	public static final Map<String, String> DISCOUNT_TYPEMAP = new HashMap<String,String>();
	static{
		DISCOUNT_TYPEMAP.put(DISCOUNT_TYPE_PERCENT, "������(��)");
		DISCOUNT_TYPEMAP.put(DISCOUNT_TYPE_UPRICE, "������(��)");
	}
}
