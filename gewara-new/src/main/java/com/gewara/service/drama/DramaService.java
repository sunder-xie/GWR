package com.gewara.service.drama;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.model.drama.Drama;
import com.gewara.model.drama.OpenDramaItem;

public interface DramaService {

	/**
	 * ���Ż���
	 */
	List<Drama> getHotDrama(String citycode, String order,int from,int maxnum);
	/**
	 * ���ھ��еĻ���
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Long> getNowDramaList(String citycode, int from, int maxnum);
	
	/**
	 * ��ѯ���ھ��л��������
	 * @return
	 */
	Integer getNowDramaCount(String citycode);
	/**
	 * ����dramaname��ѯ������Ϣ
	 */
	List<Drama> getDramaListByName(String citycode, String name,int from,int maxnum);
	
	/**
	 * ����dramaid��ѯOpenDramaItem����
	 * @param dramaid
	 * @return
	 */
	List<OpenDramaItem> getOpenDramaItemListBydramaid(String citycode, Long dramaid);
	List<OpenDramaItem> getOpenDramaItemListBydramaid(String citycode, Long dramaid, Long theatreid, Boolean isOpenPartner);
	/**
	 * �������ݵĻ���
	 * @param fromDate
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Drama> getCurDramaList(String citycode, Date fromDate, String order, int from, int maxnum);
	List<Drama> getCurDramaList(Long theatreid, String order, int from, int maxnum);
	List<Drama> getCurPlayDramaList(Long theatreid, int from, int maxnum);
	/**
	 *������������
	 * @param theatreid
	 * @return
	 */
	Integer getCurPlayDramaCount(Long theatreid);

	/**
	 * ��������
	 * @param fromDate
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Drama> getFutureDramaList(String citycode, Date fromDate, int from, int maxnum);
	
	List<Drama> getCurDramaList(String citycode, String orderField);
	
	Map<String/*yyyy-MM-dd*/,Integer> getMonthDramaCountGroupPlaydate(String citycode, Date playdate);
	
	List<Drama> getCurDramaByDate(Timestamp curtime, String citycode, String order, int from, int maxnum);
	/**
	 * @param citycode ���д���
	 * @param fyrq 1.������Ʊ��2.�������ݣ�6.�������ݣ�8.��ǰ��Ŀ7.�������͵ľ�Ŀ
	 * @param type �������ͣ�ϲ�硢����ȣ�
	 * @param order �����ֶ�
	 * @param dramatype ��Ŀ���ͣ�drama���硢other�����ݳ���
	 * @param searchkey �����ؼ���
	 * @return
	 */
	List<Drama> getDramaList(String citycode, String fyrq, String type, String order, String dramatype, String searchkey,int from,int maxnum);
	Integer getDramaListCount(String citycode, String fyrq, String type, String order, String dramatype, String searchkey);

	/**
	 * ����BaseDramaController
	 * @param citycode
	 * @param dramaid
	 * @param theatreid
	 * @param itemid
	 * @param isPartner
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	List<Drama> getDramaListByTroupeCompany(String citycode, Timestamp lasttime, String troupecompany);
	
	/**
	 *	��̬��ѯ�ݳ����� 
	 */
	List<String> getDramaTypeList(String citycode);
	
	/**
	 * ��ȡ��ӳ��ʼ������30���ڵ�������Ʊ�Ĺ�ע�������ľ�Ŀ
	 * @param citycode
	 * @param isPartner
	 * @param maxnum
	 * @return
	 */
	public List<Drama> getDramaListByMonthOpenDramaItem(String citycode, boolean isPartner, int maxnum);
	/**
	 * ���������Ʊ�ľ�Ŀ,������Ʊ���ο���ʱ��ȡ�������
	 * @param citycode
	 * @param max
	 * @return
	 */
	public List<Drama> getDramaListLastOpenTime(String citycode, int from, int maxnum);
	
	Date getDramaMinMonthDate(String citycode);
	
	Map<String, Integer> getMonthDramaCount(String citycode, Date playdate);
	
	List<Drama> getCurDramaByDate(String citycode, Date playdate, String order, int from, int maxnum);
	
	/**
	 * ��ȡ�����б�ҳ�ľ�Ŀ
	 * @param citycode
	 * @param searchKey
	 * @param fromDate
	 * @param from
	 * @param maxnum
	 * @return
	 */
	public List<Drama> getDramaListByName(String citycode,String searchKey,Timestamp fromDate, String orderField, boolean asc, int from, int maxnum);
	public Integer getDramaCountByName(String citycode,String searchKey,Timestamp fromDate);
	
	List<OpenDramaItem> getBookingOdiList(String citycode, Date playdate, Long dramaid, Long theatreid);
}
