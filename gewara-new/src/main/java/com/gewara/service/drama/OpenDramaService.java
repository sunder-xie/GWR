package com.gewara.service.drama;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.gewara.model.drama.Drama;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.drama.OpenTheatreSeat;
import com.gewara.model.drama.TheatreRoom;
import com.gewara.model.drama.TheatreRoomSeat;
import com.gewara.model.drama.TheatreSeatArea;
import com.gewara.model.drama.TheatreSeatPrice;
import com.gewara.support.ErrorCode;


public interface OpenDramaService {
	List<TheatreRoomSeat> getSeatList(Long roomId);
	String getTheatreRoomSeatMapStr(TheatreRoom room);
	String getTheatreSeatAreaMapStr(TheatreSeatArea seatArea);
	boolean addRowSeat(Long roomId);
	boolean addRankSeat(Long roomId);
	boolean deleteRowSeat(Long roomId);
	boolean deleteRankSeat(Long roomId);
	TheatreRoomSeat getRoomSeatByLocation(Long roomid, int line, int rank);
	boolean updateSeatLine(Long roomid, int lineno, String newline);
	boolean updateSeatRank(Long roomid, int rank, String newrank);
	
	List<OpenTheatreSeat> getOpenTheatreSeatListByDpid(Long dpid, Long areaid);
	ErrorCode addPriceSeat(Long dpid, Long areaid, String seattype, Long seatid, Long userid);
	ErrorCode batchAddPriceSeat(Long dpid, Long areaid, String seattype, String rankno, String lineno, Long userid);
	ErrorCode removePriceSeat(Long dpid, Long areaid, Long seatid, Long userid);
	TheatreSeatPrice getTheatreSeatPriceBySeatType(Long itemid, Long areaid, String seattype);
	ErrorCode lockSeat(Long seatId, String locktype, String lockreason, Long userid);
	ErrorCode unLockSeat(Long seatId, Long userid);
	ErrorCode batchLockSeatBySeatPrice(Long itemid, Long areaid, TheatreSeatPrice seatprice, String locktype, Long userid);
	ErrorCode batchUnLockSeatBySeatPrice(Long itemid, Long areaid, TheatreSeatPrice seatprice, Long userid);
	ErrorCode batchUnLockSeat(Long itemid, Long areaid, String lockline, String lockrank, Long userid);
	ErrorCode batchLockSeat(Long itemid, Long areaid, String locktype, String lockreason, String lockline, String lockrank, Long userid);
	/**
	 * ����������λ
	 * @param roomid
	 * @param linelist �к�
	 */
	ErrorCode batchAddBaseSeat(Long roomid, String linelist, String ranklist, Long userid);
	ErrorCode batchDelBaseSeat(Long roomid, String linelist, String ranklist, Long userid);
	ErrorCode updateSeatInitStatus(Long seatid, String initstatus);
	ErrorCode updateSeatLoveInd(Long seatid, String loveInd);
	
	/**
	 * ================ǰ̨����==========================
	 */
	List<OpenDramaItem> getOdiList(String citycode, Long theatreid, Long dramaid, Timestamp from, Timestamp to, boolean open);
	List<Drama> getCurTheatreDrama(Long tid, int from, int maxnum);
	List<Long> getCurDramaidList(Long theatreid);
	/**
	 * ��ȡ��ǰ�ɹ�Ʊ����
	 * @return
	 */
	List<String> getCitycodeList();
	
	/**
	 * ��ѯ��ǰ������ӳ�Ļ���
	 */
	List<Drama> getCurPlayDrama(String citycode, int from,int maxnum);
	List<Long> getCurDramaidList(String citycode);
	List<Long> getCurDramaidList(String citycode, String opentype);
	
	/**
	 *  ��������ID ��ѯ������ӳ���� / ��ʷ����
	 */
	List<Drama> getDramaByStarid(Long starid, int from, int maxnum);
	List<Drama> getPlayDramaByStarid(Long starid);	// ����������ӳ
	List<Drama> getPlayDramaByStarid(Long starid, boolean iscurrent, int from, int maxnum);
	Integer getDramaOrderCount(Long itemid);
	ErrorCode removeOpenDramaItem(Long id);
	/**
	 * �Ƿ�֧��Ԥ��
	 * @param theatreid
	 * @param dramaid
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	boolean isSupportBooking(String citycode, Long theatreid, Long dramaid, Timestamp starttime, Timestamp endtime);
	List<Date> getDramaOpenDateList(Long dramaid, Timestamp startime, Timestamp endtime, boolean isPartner);
	
	ErrorCode<OpenDramaItem> updateStatus(OpenDramaItem odi, String status, Long userid);
	ErrorCode<OpenDramaItem> updatePartner(OpenDramaItem odi, String partner, Long userid);
	
	/**
	 * ����theatreid��ѯĳ���͹�Ʊ��������
	 * @param theatreid
	 * @param opentype
	 * @return
	 */
	Integer getOdiCountByTheatreid(Long theatreid, Long dramaid, String opentype);
	List<Drama> getCurPlayDramaByType(String citycode, String dramatype, int from, int maxnum);
	
	List<Long> getOpenDramaItemId(List dramaid, Timestamp lasttime);
	List<Long> getSeatidListBySeatLabel(Long areaid, String seatLabel);
	ErrorCode<String> validOpenDramaItem(OpenDramaItem odi);
	ErrorCode<String> openSeat(Long userid, OpenDramaItem odi, TheatreSeatArea seatArea, TheatreRoom room);
	ErrorCode updateTheatreSeatPrice(TheatreSeatPrice seatprice, String status, Timestamp updatetime, Long userid);
}
