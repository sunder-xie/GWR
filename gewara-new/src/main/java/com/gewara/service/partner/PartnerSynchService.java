package com.gewara.service.partner;

import java.util.List;

import com.gewara.model.partner.CallbackOrder;
import com.gewara.model.pay.GewaOrder;
import com.gewara.support.ErrorCode;

/**
 * ������鶩��ͬ��
 * @author acerge(acerge@163.com)
 * @since 6:03:24 PM May 19, 2010
 */
public interface PartnerSynchService {
	/**
	 * ���뵽�ش����У���ִ��һ������
	 * @param order
	 * @param pushflag: ���ͱ��
	 * @param renew ����״̬�ı䣬����֮ǰ�Ƿ��ͣ��������´���
	 */
	CallbackOrder addCallbackOrder(GewaOrder order, String pushflag, boolean renew);
	/**
	 * ���Ͷ�����Ϣ��������
	 * @param order
	 * @return
	 */
	ErrorCode pushCallbackOrder(CallbackOrder order);
	String writeChinapayTransFile();
	List<CallbackOrder> getCallbackOrderList(int maxtimes);
	ErrorCode pushCallbackOrder(String tradeNo, String pushflag);
}
