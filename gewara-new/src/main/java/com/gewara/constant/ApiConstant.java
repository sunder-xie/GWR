package com.gewara.constant;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;

import com.gewara.constant.ticket.OrderConstant;

/**
 * API��Ʊϵͳ���õ��ĳ���
 * @author acerge(acerge@163.com)
 * @since 12:27:06 PM Apr 22, 2011
 * 0000	�ɹ�
 * 1001  ���β�����
 * 1002  ���ε�ǰδ���Ŷ�Ʊ��1���ر� 2��ֻ��10��00��18��00���� 3��ӰԺ�رն��ⶩƱ ��
 * 1003  ӰԺ������Ϣ��ͬ������Ҫ�ȴ�Gewaraϵͳ����ӰԺ��Ϣ
 *
 * 2001  �޷�����ӰԺ
 * 2002  ��λλ�ô��󣺲�����������ѡ��������
 * 2003  ��λ��ռ��
 * 2004  ��λ�������ƴ��󣺳���5����ĳЩ������ֻ�����������ĸ���
 * 2005  ��λ�������ƴ����л������Ƶ�
 * 2006  Υ�������������
 * 2098  ���ڸ���ӰԺ����
 * 2099  ��λ����δ֪����
 * 4001  �̼Ҳ�����  
 * 4002  �̼���Ȩ��  
 * 4003  У�����  
 * 4004  ��������ȱ�ٲ������ʽ����ȷ  
 * 4005  ���ݴ������ѯ�����ݲ����ڵ�
 * 4006  ֧������  
 *	
 * 5000	�û�������
 * 5001	�û�δ��¼
 * 5002	�û���Ȩ��
 * 5003	�����ظ�����
 * 
 * 9999  δ֪��������δ����Ĵ���
 */
public abstract class ApiConstant {
	public static final String CODE_SUCCESS = "0000";				//
	public static final String CODE_OPI_NOT_EXISTS = "1001";
	public static final String CODE_OPI_CLOSED = "1002";
	public static final String CODE_OPI_UNSYNCH = "1003";
	
	public static final String CODE_CONNECTION_ERROR = "2001";
	public static final String CODE_SEAT_POS_ERROR = "2002";
	public static final String CODE_SEAT_OCCUPIED = "2003";
	public static final String CODE_SEAT_NUM_ERROR = "2004";
	public static final String CODE_SEAT_LIMITED = "2005";
	public static final String CODE_SEAT_BREAK_RULE = "2006";
	public static final String CODE_SEAT_LOCK_ERROR_CINEMA = "2010";//������λ����
	public static final String CODE_CCTO_ERROR = "2011";			//��������ӰԺ���Ӳ�����
	public static final String CODE_TC_ERROR = "2012";				//����������
	public static final String CODE_SEAT_RELEASED = "2013";			//��λ�Ѿ��ͷŻ򲻴���
	public static final String CODE_SYNCH_DATA = "2098";			//���ڸ���ӰԺ����
	public static final String CODE_SEAT_LOCK_ERROR = "2099";
	
	public static final String CODE_PARTNER_NOT_EXISTS = "4001";
	public static final String CODE_PARTNER_NORIGHTS = "4002";
	public static final String CODE_SIGN_ERROR = "4003";
	public static final String CODE_PARAM_ERROR = "4004";
	public static final String CODE_DATA_ERROR = "4005";
	public static final String CODE_PAY_ERROR = "4006";
	
	public static final String CODE_WEIBO_EXPRIES = "4100"; //΢������
	public static final String CODE_UNBIND_MOBILE = "4101"; //û�а��ֻ�
	
	public static final String CODE_MEMBER_NOT_EXISTS = "5000";
	public static final String CODE_NOTLOGIN = "5001";
	public static final String CODE_USER_NORIGHTS = "5002";
	public static final String CODE_REPEAT_OPERATION = "5003";
	public static final String CODE_NOT_EXISTS = "5004";
	
	public static final String CODE_PAYPASS_ERROR = "6001";	//֧��������ڼ�
	
	public static final String CODE_UNKNOWN_ERROR = "9999";
	
	public static final Map<String, String> ORDER_STATUS_MAP;
	static{
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put(OrderConstant.STATUS_NEW, "new");
		tmp.put(OrderConstant.STATUS_NEW_UNLOCK, "new");
		tmp.put(OrderConstant.STATUS_NEW_CONFIRM, "new");
		tmp.put(OrderConstant.STATUS_PAID, "paid");
		tmp.put(OrderConstant.STATUS_PAID_FAILURE, "paid");
		tmp.put(OrderConstant.STATUS_PAID_UNFIX, "paid");
		tmp.put(OrderConstant.STATUS_PAID_SUCCESS, "success");
		tmp.put(OrderConstant.STATUS_PAID_RETURN, "refund");
		tmp.put(OrderConstant.STATUS_CANCEL, "cancel");
		tmp.put(OrderConstant.STATUS_SYS_CANCEL,"cancel");
		tmp.put(OrderConstant.STATUS_REPEAT,"repeat");
		tmp.put(OrderConstant.STATUS_USER_CANCEL,"cancel");
		tmp.put(OrderConstant.STATUS_TIMEOUT, "cancel");
		ORDER_STATUS_MAP = UnmodifiableMap.decorate(tmp);
	}
	public static Map<String, String> getOrderStatusMap(){
		return ORDER_STATUS_MAP;
	}
	public static String getMappedOrderStatus(String status){
		return ORDER_STATUS_MAP.get(status);
	}
}
