package com.gewara.service.drama;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.model.drama.DramaStar;


/**
 *    @function ��������Service 
 * 	@author bob.hu
 *		@date	2010-12-03 10:39:50
 */
public interface DramaStarService {

	/**
	 *  �����б� - ��ʱ������
	 *  order - �������ֶ�����
	 *  property - ����Ʒ�������ѯ, Ŀǰ��null
	 *  type - ����/����
	 */
	List<DramaStar> getStarList(String order, String property, String startype, int from, int maxnum);
	List<DramaStar> getDramaStarList(String order, String startype, String searchkey, String troupecompany, int from, int maxnum, String...notNullPropertys);
	/**
	 * ͨ��troup ��������������
	 * @param startype
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<DramaStar> getDramaStarListGroupTroupe(String startype, String searchKey, int from, int maxnum);
	
	Integer getStarCount(String startype,String searchkey, String...notNullPropertys);
	
	/**
	 * ����state + name ����
	 * state: ����
	 * name: ������(֧��like)
	 */
	List<DramaStar> getStarListByStateAndName(String state, String name, String type, int from, int maxnum);
	Integer getStarCountByStateAndName(String state, String name, String type);
	
	/**
	 *  ��������ID(����ID)ƥ�����/����
	 */
	List<DramaStar> getStarListByTroupid(Long starid, String orderField, boolean asc, int from, int maxnum);
	Integer getStarCountByTroupid(Long starid);
	
	/**
	 *  ȡ��ĳ���ǵ� �ݳ���/������/ͼƬ��/��Ƶ��
	 *  params: List<DramaStar>
	 *  return: Map<Long, Map<String, Integer>>
	 *  page:	eg.	dataMap.get(starid).get("newCount")
	 */
	Map getFavStarListProperty(String citycode, List<DramaStar> starlist);
	
	
	
	/*****
	 *  ��̨��ѯ - �������� + name ģ��ƥ��
	 * */
	List<DramaStar> getStarListByName(String type, String name, String startype, int from, int maxnum);
	DramaStar getDramaStarByName(String name, String startype);
	int getStarCountByName(String type, String name, String startype);
	
	/**
	 * ��ѯı�����������
	 * Long troupe ����ID
	 * starType
	 */
	public List<DramaStar> getDramaStarListByTroupe(String type, Long troupe, String starType, int from, int maxnum, String...notNullPropertys);
	/**
	 * ��ѯ���¼������ǣ���ͬ����
	 * Timestamp lasttime �ϴ�ͬ��ʱ��
	 */
	List<DramaStar> getSynchStarList(Timestamp lasttime);
}
