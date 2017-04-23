package com.gewara.service.bbs;

import java.util.List;
import java.util.Map;

import com.gewara.model.bbs.commu.CommuManage;

public interface CommuAdminService {

	/**
	 * ����Ȧ�����ƣ�Ȧ������
	 */
	Map getCommuInfoList(Long commuid,String commname,String nickname,String status,int from, int maxnum);
	
	/**
	 * ����״̬
	 * @param commuid
	 * @param status
	 */
	void updateCommuStatus(Long commuid,String status);
	
	/**
	 * ����Ȧ�����ƣ�Ȧ�����ƣ�Ȧ�ӱ�Ų�ѯȦ������
	 * @param commuid
	 * @param commname
	 * @param nickname
	 * @param status
	 * @return
	 */
	Integer getCommuInfoCount(Long commuid,String commname,String nickname,String status);
	
	/**
	 *  Ȧ����֤���� �б�.
	 */
	List<CommuManage> getCommuManageListByStatus(String status, int from, int maxnum);
	Integer getCommuManageCount(String status);
}
