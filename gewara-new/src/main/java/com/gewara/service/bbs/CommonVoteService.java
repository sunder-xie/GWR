package com.gewara.service.bbs;

import java.util.List;
import java.util.Map;



/**
 * 2012-12-10 ����7:18:14
 * @author bob
 * @desc Mongoר��ͶƱͨ��
 */
public interface CommonVoteService {
	
	/**
	 *  del
	 * */
	void delVote(String id);
	
	/**
	 * 2012-12-10 ����7:19:27
	 * @author bob
	 * @desc tag + memberid + itemid = Ψһȷ��һ��ͶƱ��¼
	 */
	Map<String, Object> getSingleVote(String tag, Long memberid, String itemid);
	void addVoteMap(String tag, String itemid, Long memberid, String flag);
	
	/**
	 * 2012-12-10 ����7:20:53
	 * @author bob
	 * @desc tag + itemidList = ����ͶƱ��������Ŀ 
	 */
	List<Map> getItemVoteList(String flag);
	
	/**
	 * 2012-12-11 ����11:07:11
	 * @author bob
	 * @desc (���)��ĳitem����ͶƱ��
	 * flag = ��ǰtag+"virtual"
	 */
	void addCommonVote(String flag, String itemid, Integer support);
	/**
	 * 2012-12-11 ����1:39:54
	 * @author bob
	 * @desc ���ҵ�ǰ��Ŀ��ͶƱֵ 
	 */
	Integer getSupportCount(String flag, String itemid);
	
	//��ȡͶƱ�û�
	List<Map> getVoteInfo(String tag, int from, int maxnum);
	int getVoteInfoCount(String tag);
}
