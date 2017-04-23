package com.gewara.service.bbs;

import java.util.List;
import java.util.Map;

import com.gewara.model.bbs.Moderator;


public interface ModeratorService {
	/**
	 * ��ѯ���������б�
	 */
	List<Moderator> getModeratorList(String type, List showaddress, String mstatus, int from,int maxnum);
	Integer getModeratorCount(String type, Long memberid);
	
	/**
	 * ��ѯ������Ϣ
	 */
	List<Moderator> getModeratorByType(Integer showAddress,String type);
	List<Moderator> getModeratorByType(Integer showAddress,String type, int from, int maxnum, boolean isRule);
	
	/**
	 * ��ѯ�û���ע�Ļ�����Ϣ(����)
	 */
	List<Moderator> getModeratorList(Long memberid,String type,int from ,int maxnum);
	/**
	 *  @function �ӻ�����ȡ������(ͬʱ�������ݿ�)
	 * 	@author bob.hu
	 *	@date	2011-12-02 17:46:23
	 */
	List<Map> updateHotModeratorFromCache(int from,int max);
	
}
