package com.gewara.service.api;

import java.util.List;

import com.gewara.model.api.ApiUser;
import com.gewara.model.api.ApiUserExtra;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.content.PhoneAdvertisement;


public interface ApiMobileService {
	/**
	 * ��ȡ�ͻ�����ҳ�����Ϣ
	 */
	GewaCommend getPhoneIndexAdvertInfo(String citycode,String tag);
	/**
	 * ��ѯ����б�
	 * @param apptype ��Ʒ
	 * @param osType ϵͳ����
	 * @param citycode ���д���
	 * @param advtype �������
	 * @param from ��ʼ����
	 * @param maxnum ����
	 * @return
	 */
	List<PhoneAdvertisement> getPhoneAdvertList(String apptype, String osType,String citycode,String advtype,int from, int maxnum);
	//List<Long> getGewaParnteridList();
	boolean isGewaPartner(Long partnerid);
	void initApiUserList();
	ApiUserExtra getApiUserExtraById(Long id);
	ApiUser getApiUserByAppkey(String appkey);
}
