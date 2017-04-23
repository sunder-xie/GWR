package com.gewara.service.gewapay;

import java.util.List;
import java.util.Map;

import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.ConfigTrigger;

/**
 * ��ţ�������
 * <p>��ţ������ʹ�������Ż�
 * @author user
 *
 */
public interface ScalperService extends ConfigTrigger {

	/**
	 * �Ƿ����ƻ�ţʹ�������Żݻ
	 * <p>1���û�id�Ƿ��ǻ�ţ
	 * <p>1���ж��ֻ����Ƿ��ǻ�ţʹ�õ��ֻ���
	 * <p>2���жϸû�ţ�Ƿ�ʹ�ù��������Ż�
	 * @param memberId �û�id
	 * @param phone �û��󶨵��ֻ���
	 * @param specialDiscountId �����Ż�id
	 * @return
	 */
	ErrorCode<String> checkScalperLimited(Long memberId, String phone, Long specialDiscountId);
	/**
	 * @param memberId
	 * @param phone
	 * @param absolute
	 * @return
	 */
	boolean isScalper(Long memberId, String phone);
	
	/**
	 * ����ip��ȡ���ӻ�ţ�嵥
	 * @param hours ����Сʱ�ڵ�ע���û�
	 * @param count һ��ip����ע���û�����Ĭ��5
	 * @return
	 */
	Map<String,List<Map>> getSuspectScalperByIp(int hours, int count);
	
}
