/**
 * 
 */
package com.gewara.service.sport;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.model.acl.User;
import com.gewara.model.sport.OpenTimeItem;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.sport.ProgramItemTime;
import com.gewara.model.sport.Sport2Item;
import com.gewara.model.sport.SportProfile;
import com.gewara.support.ErrorCode;

public interface OpenTimeTableService {
	Integer getOpenTimeTableCount(Long sportid, Long itemid, Date startdate, Date enddate, String openType);
	List<OpenTimeTable> getOpenTimeTableList(Long sportid, Long itemid, Date startdate, Date enddate, String openType);
	List<OpenTimeTable> getOpenTimeTableList(Long sportid, Long itemid, Date startdate, Date enddate, String openType, boolean isBooking, int from, int maxnum) ;
	List<OpenTimeTable> getOpenTimeTableList(Long sportid, Long itemid, Date playdate, String openType);
	List<OpenTimeTable> getOpenTimeTableList(Long sportid, Long itemid, Date playdate, String openType, int from, int maxnum);
	
	/**
	 * ĳ���˶���Ŀĳʱ���ڿ���Ԥ���ĳ��ݺͳ�����
	 * @param itemid
	 * @param startPlaydate
	 * @param endPlaydate
	 * @return
	 */
	List<Map<Long,Long>> getOpenTimeTableSportList(String citycode,Long itemid, Date startPlaydate,Date endPlaydate);
	/**
	 * ���μ۸�
	 * @param ottid
	 * @return
	 */
	List<Integer> getTimeItemPrice(Long ottid);
	Integer getbookingSportCount(Date startdate, String citycode) ;
	List<Map> getOpenTimeCountByItemid(Long itemid, Long... sportidList);
	List<Map> getOpenTimeCountByItemid(Long itemid, Date date, Long... sportIdArray);
	//���������õ���������
	List<String> getOpenTimeTableOpenTypeList(Long sportid, Long itemid, Date startdate, Date enddate, boolean isBooking);
	
	ErrorCode<ProgramItemTime> saveOrUpdateProgramItem(Long id, Long sportid, Long itemid, Integer week, String fieldids, Map dataMap, User user, String citycode);
	ErrorCode<String> programItemTime(ProgramItemTime programItemTime, int week);
	ErrorCode<String> batchProgramItemTime();
	ErrorCode<String> batchProgramItemTime(Sport2Item sport2Item);
	void clearOttPreferential(OpenTimeTable ott, SportProfile sp);
	List<Long> getCurOttSportIdList(Long itemid, String citycode);
	/**
	 * ���ݳ��λ�ȡ����ͼ
	 * @param ottid
	 * @return
	 */
	List<OpenTimeItem> getOpenItemList(Long ottid);
	/**
	 * �����˶�Ԥ�����ڱ�
	 * @param ottid
	 * @return
	 */
	List<OpenTimeTable> getOttList(Long sportid, Long itemid, String playdate);
	/**
	 * �����˶����������������ṩ��������
	 * @param ott	��������
	 */
	void updateOpenTimeTable(OpenTimeTable ott);
	
	/**
	 * �����˶�����ͳ������
	 * @param ott	��������
	 */
	void updateOttOtherData(OpenTimeTable ott);
	
	void refreshOpenTimeSale(List<OpenTimeItem> itemList, Integer dupprice, List<String> msgList);
}
