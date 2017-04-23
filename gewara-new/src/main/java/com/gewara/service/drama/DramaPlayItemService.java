package com.gewara.service.drama;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.model.drama.DisQuantity;
import com.gewara.model.drama.Drama;
import com.gewara.model.drama.DramaPlayItem;
import com.gewara.model.drama.Theatre;
import com.gewara.model.drama.TheatreField;
import com.gewara.model.drama.TheatreRoom;
import com.gewara.model.drama.TheatreSeatPrice;
import com.gewara.support.ErrorCode;

public interface DramaPlayItemService{
	
	Drama getDramaByName(String dramaName);
	
	List<Drama> getDramaListByName(String dramaName);
	/**
	 * ���泡��
	 * @param item
	 * @return
	 */
	ErrorCode saveDramaPlayItem(DramaPlayItem item, Long userid);
	/**
	 * ɾ������
	 * @param item
	 * @return
	 */
	ErrorCode removieDramaPlayItem(Long id);
	/**
	 * ������Ƭ
	 * @param item
	 * @param playdate
	 * @return
	 */
	ErrorCode saveDramaPlayItem(DramaPlayItem item, String playdates, String rooms) throws Exception;
	/**
	 * ĳ������Ժ�ӿ�ʼ����
	 * @param theatreid
	 * @param starttime
	 * @return ��ȡ�������ڶ�Ӧ������
	 */
	List<Map> getDateCount(Long theatreid, Timestamp starttime);
	/**
	 * ��ȡ�����б�
	 * @param theatreid
	 * @param dramaid
	 * @param playstart
	 * @param playend
	 * @return
	 */
	List<DramaPlayItem> getDramaPlayItemList(String citycode, Long theatreid, Long dramaid, Timestamp playstart, Timestamp playend, Boolean isPartner);
	List<DramaPlayItem> getDramaPlayItemList(String citycode, Long theatreid, Long dramaid, Long starid, Timestamp playstart, Timestamp playend, Boolean isPartner);
	List<DramaPlayItem> getDramaPlayItemList(String citycode, Long theatreid, Long dramaid, Timestamp playstart, Timestamp playend, Boolean isPartner, Boolean isValidEndtime);
	List<DramaPlayItem> getDramaPlayItemList(String citycode, Long theatreid, Long dramaid, Long starid, Timestamp playstart, Timestamp playend, Boolean isPartner, Boolean isValidEndtime);
	List<DramaPlayItem> getUnOpenDramaPlayItemList(String citycode, Long theatreid, Long starid, Timestamp playstart, Timestamp playend, int maxnum);
	List<DramaPlayItem> getUnOpenDramaPlayItemList(String citycode, Long theatreid, Long dramaid, Long starid, Timestamp playstart, Timestamp playend, int maxnum);
	void initDramaPlayItem(DramaPlayItem item);
	void initDramaPlayItemList(List<DramaPlayItem> itemList);
	
	/**
	 * �õ���ӳ���б�
	 * @param theatreid
	 * @return
	 */
	List<TheatreRoom> getRoomList(Long theatreid);
	TheatreField getTheatreFieldByName(Long theatreid, String name);
	TheatreRoom getTheatreRoomByNum(Long theatreid, String fieldnum, String roomnum);
	List<TheatreSeatPrice> getTspList(Long dpid);
	List<TheatreSeatPrice> getTspList(Long dpid, Long areaid);
	TheatreSeatPrice getTsp(Long dpid, Long areaid, Integer price, String seattype);
	/**
	 * �õ��۸��б�
	 * @param theatreid
	 * @param dramaid
	 * @param starttime
	 * @param endtime
	 * @param isBooking
	 * @return
	 */
	List<Integer> getPriceList(Long theatreid, Long dramaid, Timestamp starttime, Timestamp endtime, boolean isBooking);
	/**
	 * ���ݻ���ľ�Ժ
	 * @param dramaid
	 * @param isBooking
	 * @return
	 */
	List<Theatre> getTheatreList(String citycode, Long dramaid, boolean isBooking, int maxnum);
	/**
	 * �����Ƿ��ܹ�Ʊ
	 * @param dramaid
	 * @return
	 */
	boolean isBookingByDramaId(Long dramaid);
	/**
	 * ��ѯ�г��ξ�ԺidList
	 * @return
	 */
	List<Long> getTheatreidList(String citycode, Long dramaid, boolean isBooking);
	List<Long> getTheatreidList(String citycode, Long dramaid, String opentype, boolean isBooking);
	List<Long> getTheatreFieldIdList(String citycode, Long dramaid, boolean isBooking);
	List<Long> getCurBookingTheatreList(String citycode, String countycode);
	
	List<Long> getDramaStarDramaIdList(String citycode, Long dramaStarId);
	
	/**
	 * ��ѯ�����ݳ��ľ�Ŀ���ļ��Ҿ�Ժ
	 */
	List<Theatre> getTheatreidByDramaid(String citycode, int from, int maxnum);
	/**
	 * ��ѯһ������ĳһʱ�����ݳ���������
	 */
	Integer getDramaCount(Long dramaid, Timestamp playtime);
	List<Date> getDramaPlayDateList(Long dramaid,Timestamp starttime, Timestamp endtime, Boolean isPartner);
	List<Date> getDramaPlayMonthDateList(Long dramaid, Boolean isPartner);
	Integer getDramaOpenCount();
	/**
	 * ĳ���۸���Ż�
	 * @param tspid
	 * @return
	 */
	List<DisQuantity> getDisQuantityList(Long tspid);
	/**
	 * ���ε��Ż��ۿ�
	 * @param dpid
	 * @return
	 */
	List<DisQuantity> getDisQuantityListByDpid(Long dpid);
	
	/**
	 * ָ������Ŀ��ų���
	 * @param dramaid
	 * @return
	 */
	List<Long> getDramaPlayIdList(List dramaid);
	
	DramaPlayItem getUniqueDpi(Long theatreid, Long dramaid, Long roomid, Timestamp playtime);
	
	List<Long> getCurDramaidList(String citycode);
	
	DramaPlayItem getDpiBySeqno(String seller, String sellerseq);
	/**
	 * ˢ����Ŀ�۸�
	 * @param dramaid
	 */
	void refreshDramaPrice(Long dramaid);
}
