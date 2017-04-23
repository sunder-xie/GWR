package com.gewara.constant;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.lang.StringUtils;

import com.gewara.model.pay.SMSRecordBase;

/**
 * ������س���
 * @author acerge(acerge@163.com)
 * @since 3:03:01 PM Oct 10, 2011
 */
public class SmsConstant {
	//����״̬
	public static final String STATUS_Y = "Y"; 							//�ѷ���
	public static final String STATUS_N = "N"; 							//δ����
	public static final String STATUS_Y_TRANS = "Y_TRANS";				//�Ѵ��ݸ����ͷ�
	public static final String STATUS_Y_IGNORE = "Y_IGNORE";			//������Ҫ�󲻷���
	public static final String STATUS_Y_LARGE = "Y_LARGE";				//���ݳ�������
	public static final String STATUS_N_SENDERROR = "N_SEND_ERR";		//�ͻ���δ���ͳ�ȥ
	
	public static final String STATUS_N_ERROR = "N_ERR";				//���ط��ͷ���ʧ�ܣ������ǹؼ��ֹ��˻�����
	public static final String STATUS_FILTER = "FILTER";				//���״̬
	public static final String STATUS_D = "D"; 							//����
	public static final String STATUS_PROCESS = "P";					//�Ѵ���

	//��������:���ȼ������͵���
	public static final String SMSTYPE_NOW = "now";						//����
	public static final String SMSTYPE_DYNCODE = "dyncode";				//�ֻ���̬��
	public static final String SMSTYPE_MANUAL = "manu";					//�ֹ�
	public static final String SMSTYPE_ACTIVITY = "activity";			//��ֻ�����
	public static final String SMSTYPE_ECARD = "ec";					//���ӿ�
	public static final String SMSTYPE_3H = "3h";						//��ǰ3Сʱ
	public static final String SMSTYPE_10M = "10m";						//��Ӱ��10����
	public static final String SMSTYPE_CO = "co";						//�Ż�ȯ��������
	public static final String SMSTYPE_FB = "fb";						//�˿�����
	public static final String SMSTYPE_INVOICE = "invoice";				//��Ʊ�ʼķ��Ͷ���
	public static final String SMSTYPE_NOW_API = "now_api";				//API��������
	
	public static final Map<String, String> typeMap;
	static {
		Map<String, String> tmp = new LinkedHashMap<String, String>();
		tmp.put(SMSTYPE_NOW, "����");
		tmp.put(SMSTYPE_3H, "��ǰ3Сʱ");
		tmp.put(SMSTYPE_MANUAL, "�ֹ�");
		tmp.put(SMSTYPE_ACTIVITY, "�");
		tmp.put(SMSTYPE_ECARD, "���ӿ�");
		tmp.put(SMSTYPE_10M, "��Ӱ��10����");
		tmp.put(SMSTYPE_CO, "�Ż�ȯ��������");
		tmp.put(SMSTYPE_FB, "�˿�����");
		tmp.put(SMSTYPE_INVOICE, "��Ʊ�ʼ�֪ͨ");
		tmp.put(SMSTYPE_DYNCODE, "���ද̬��");
		typeMap = UnmodifiableMap.decorate(tmp);
	}
	public static String filterContent(SMSRecordBase sms){
		String content = sms.getContent();
		if(StringUtils.equals(sms.getSmstype(), SMSTYPE_ECARD)){
			return content.replaceAll("[a-zA-Z1-9]{12}", "********");
		}
		return content.replaceAll("\\d{5,9}", "*****");
	}

}
