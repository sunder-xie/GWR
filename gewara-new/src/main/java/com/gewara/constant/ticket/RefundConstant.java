package com.gewara.constant.ticket;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;

public class RefundConstant {
	//public static final String STATUS_PREPAIR1 = "prepare"; 	//Ԥ����
	public static final String STATUS_APPLY = "apply"; 		//����
	public static final String STATUS_ACCEPT = "accept"; 	//����
	public static final String STATUS_REJECT = "reject"; 	//�ܾ��˿�
	public static final String STATUS_SUCCESS = "success";	//�˿�ɹ�
	public static final String STATUS_FINISHED = "finish";	//�������
	//public static final String STATUS_CANCEL = "cancel";	//ȡ���˿�(prepair-->cancel)
	
	//��Ҫִ�еĲ���
	public static final String OP_CANCEL_TICKET = "cancelTicket";	//��Ʊ
	public static final String OP_ADJUST_SETTLE = "adjustSettle";	//�������
	public static final String OP_COMPENSATE = "compensate";		//�����û�����
	public static final String OP_RET2PARTNER = "ret2Partner";		//�����̼��˿�
	
	public static final String OP_RESULT_CANCEL_SUCCESS = "cancelSuccess"; //��Ʊ�ɹ�
	public static final String OP_RESULT_CANCEL_FAILURE = "cancelFailure"; //��Ʊʧ��
	
	//�˿����� all ȫ���˿part �����˿supplement ����
	public static final String REFUNDTYPE_FULL = "full";			//ȫ���˿�
	public static final String REFUNDTYPE_PART = "part";			//�����˿�
	public static final String REFUNDTYPE_SUPPLEMENT = "supplement";//�������
	
	//�˿�ԭ��
	public static final String REASON_UNKNOWN = "unknown";			//δ֪
	public static final String REASON_USER = "user";				//�û��˿�
	public static final String REASON_GEWA = "gewa";				//Gewa�˿�
	public static final String REASON_MERCHANT = "merchant";		//�̼ң�ӰԺ���˿�
	public static final String REASON_PRICE = "price";				//�۸����
	
	//�˻��˿� Y����Ҫ��N������Ҫ��O��δ֪, �μ�����(Other)��S: ���ύ����(Submit) R�������Ѿ�����(Refund)��F�����񷵻�����(Failure)
	public static final String RETBACK_Y = "Y";
	public static final String RETBACK_N = "N";
	public static final String RETBACK_OTHER = "O";
	public static final String RETBACK_SUBMIT = "S";
	public static final String RETBACK_REFUND = "R";
	public static final String RETBACK_FAILURE = "F";
	
	//������
	public static final String REFUND_MANAGE_DEAL = "manageDeal";
	public static final String REFUND_FINANCE_DEAL = "financeDeal";
	public static final String REFUND_FINANCE_STATUS = "status";
	public static final String REFUND_FINANCE_RESON = "reson";

	
	public static final Map<String, String> textMap;
	public static final Map<String, String> refundTypeMap;
	public static final Map<String, String> reasonTypeMap;
	public static final Map<String, String> retbackMap;
	static{
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put(STATUS_APPLY, "������");
		tmp.put(STATUS_REJECT, "�������˿�");
		tmp.put(STATUS_SUCCESS, "�˿�ɹ�");
		textMap = UnmodifiableMap.decorate(tmp);
		
		Map<String, String> tmpRefund = new HashMap<String, String>();
		tmpRefund.put(REFUNDTYPE_FULL, "ȫ���˿�");
		tmpRefund.put(REFUNDTYPE_PART, "�����˿�");
		tmpRefund.put(REFUNDTYPE_SUPPLEMENT, "�������");
		refundTypeMap = UnmodifiableMap.decorate(tmpRefund);
		
		Map<String, String> tmpReason = new HashMap<String, String>();
		tmpReason.put(REASON_UNKNOWN, "δ֪");
		tmpReason.put(REASON_USER, "�û��˿�");
		tmpReason.put(REASON_GEWA, "Gewa�˿�");
		tmpReason.put(REASON_MERCHANT, "�̼ң�ӰԺ���˿�");
		tmpReason.put(REASON_PRICE, "�۸����");
		reasonTypeMap = UnmodifiableMap.decorate(tmpReason);
		
		Map<String, String> tmpRetack = new HashMap<String, String>();
		tmpRetack.put(RETBACK_Y, "��Ҫ");
		tmpRetack.put(RETBACK_N, "����Ҫ");
		tmpRetack.put(RETBACK_OTHER, "�μ�����");
		tmpRetack.put(RETBACK_SUBMIT, "���ύ����");
		tmpRetack.put(RETBACK_REFUND, "����ɹ�");
		tmpRetack.put(RETBACK_FAILURE, "����ʧ��");
		retbackMap = UnmodifiableMap.decorate(tmpRetack);
	}
}
