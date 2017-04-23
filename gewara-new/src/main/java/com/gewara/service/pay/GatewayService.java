package com.gewara.service.pay;

import java.util.List;

import com.gewara.api.pay.domain.Gateway;
import com.gewara.model.pay.PayMerchant;
import com.gewara.support.ErrorCode;

public interface GatewayService {

	/**
	 * ͬ������֧�����ء��̻��š����е���Ϣ
	 * 
	 * @param response
	 *
	 * @author leo.li
	 * Modify Time Oct 17, 2013 4:22:29 PM
	 */
	public void synAllGateway(List<Gateway> gatewayList);

	/**
	 * �жϸ������Ƿ����л����µĽӿ�
	 * 
	 * @param gatewayCode ֧�����ش���
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Oct 18, 2013 4:43:57 PM
	 */
	public boolean isSwitch(String gatewayCode);
	

	/**
	 * �����̻���Ϣ��ֻ����ݳ���·�ɡ������Ĭ��
	 * 
	 * @param cityCode
	 * @param gatewayCode
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Dec 3, 2013 9:58:51 PM
	 */
	public ErrorCode<PayMerchant> findMerchant(String cityCode, String gatewayCode);
	
	
	/**
	 * �����̻���Ϣ�����Ը��ݳ���·�ɡ�ָ���̻���ʶ��Ĭ�ϲ���
	 * 
	 * @param cityCode      ����
	 * @param gatewayCode   ����
	 * @param merchantCode 	�̻���ʶ��ָ���̻���ʶʱ�õ������ʴ���ũ�У�
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Oct 18, 2013 6:24:52 PM
	 */
	public ErrorCode<PayMerchant> findMerchant(String cityCode,String gatewayCode,String merchantCode);

	/**
	 * 
	 * ��ȡ�̻���ʶ
	 * �̻��Ȱ�id����С��������
	 * �������Ĭ�ϣ�ȡ��һ������Ĭ�ϵ��̻�������
	 * ���û����Ĭ�ϣ�ֱ��ȡ��һ��������
	 * 
	 * @param gatewayCode
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Oct 29, 2013 2:11:52 PM
	 */
	public ErrorCode<PayMerchant> findDefaultMerchant(String gatewayCode);
}
