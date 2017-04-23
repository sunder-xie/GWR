package com.gewara.service.ticket;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.helper.ticket.SeatStatusUtil;
import com.gewara.model.api.ApiUser;
import com.gewara.model.movie.Cinema;
import com.gewara.model.movie.CinemaRoom;
import com.gewara.model.movie.Movie;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.model.movie.MoviePrice;
import com.gewara.model.movie.RoomSeat;
import com.gewara.model.ticket.AutoSetter;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.OpenSeat;
import com.gewara.model.ticket.SellSeat;
import com.gewara.model.user.Member;
import com.gewara.support.ErrorCode;

public interface OpenPlayService {
	/**
	 * ��ȡӰ����λ��
	 * @param roomId
	 * @return
	 */
	List<RoomSeat> getSeatListByRoomId(Long roomId);
	/**
	 * ����һ����λ
	 * @param roomId
	 * @return
	 */
	boolean addRowSeat(Long roomId);
	boolean addRankSeat(Long roomId);
	boolean deleteRowSeat(Long roomId);
	boolean deleteRankSeat(Long roomId);
	/**
	 * �������������λ
	 * @param roomid
	 * @param line
	 * @param rank
	 * @return
	 */
	RoomSeat getRoomSeatByLocation(Long roomid, int line, int rank);
	List<List<String>> getRoomSeatNavigation(CinemaRoom room,String orderSeat);
	/**
	 * �����б��
	 * @param roomid
	 * @param lineno
	 * @param newline
	 * @return
	 */
	boolean updateSeatLine(Long roomid, int lineno, String newline);
	/**
	 * �ֹ�������λ
	 * @param mpid
	 * @param seatId
	 * @param locktype:��������
	 * @param lockreason����������
	 * @return
	 */
	ErrorCode<OpenSeat> lockSeat(Long mpid, Long seatId, String locktype, String lockreason);
	/**
	 * ��ȡĳӰ����λ����
	 * @param roomid
	 * @return
	 */
	Integer getSeatCountByRoomId(Long roomid);
	/**
	 * @param cinemaId
	 * @param movieId
	 * @param timeFrom
	 * @param timeTo
	 * @param open ֻ��ѯ�ܹ�Ʊ�ĳ���
	 * @return
	 */
	List<OpenPlayItem> getOpiList(String citycode, Long cinemaId, Long movieId, Timestamp timeFrom, Timestamp timeTo, boolean open);
	List<OpenPlayItem> getOpiList(String citycode, Long cinemaId, Long movieId, Timestamp timeFrom, Timestamp timeTo, boolean open, int maxnum);
	/**
	 * ��ȡĳ���ε���λ
	 * @param mpid
	 * @return
	 */
	List<OpenSeat> getOpenSeatList(Long mpid);
	List<OpenSeat> refreshOpenSeatList(Long mpid);
	/**
	 * ��ȡĳ��������������λ
	 * @param mpid
	 * @return
	 */
	List<SellSeat> getSellSeatListByMpid(Long mpid);
	/**
	 * ��ȡ��ͬ���ĳ��Σ�ӰԺ�Ѹ��ĵģ���ע�⣺language��edition������
	 * @param cinemaid
	 * @return List(opid)
	 */
	List<Long> getUnsynchPlayItem(Long cinemaid);
	/**
	 * ����Ԥ����ӰԺID
	 * @param movieid Ϊ���򷵻����п���Ԥ����ӰԺ
	 * @return
	 */
	List<Long> getOpiCinemaidList(String citycode, Long movieid);
	/**
	 * ����Ԥ����ƬID
	 * @param cinemaid Ϊ���򷵻����п���Ԥ����ӰƬ
	 * @return
	 */
	List<Long> getOpiMovieidList(String citycode, Long cinemaid);
	List<Cinema> getOpenCinemaList(String citycode, Long movieId, Date playdate, int from, int maxnum);
	/**
	 * ��ȡ���Թ�Ʊ������
	 * @param citycode
	 * @param movieid
	 * @return List<Date>
	 */
	List<Date> getMovieOpenDateList(String citycode, Long movieid);
	List<Date> getMovieOpenDateListByCounycode(String counycode, Long movieid);
	/**
	 * ��ȡ���Թ�Ʊ������
	 * @param cinemaid
	 * @return List<Date)>
	 */
	List<Date> getCinemaAndMovieOpenDateList(Long cinemaid, Long movieid);
	/**
	 * ��ѯ������Ƭ����
	 */
	List<Date> getMoviePlayItemDateList(String citycode, int from, int maxnum);
	/**
	 * ��ȡӰ����λͼ���ַ������������á�@@���ָ������á�,���ָ�
	 * @param room
	 * @return
	 */
	String getRoomSeatMapStr(CinemaRoom room);
	/**
	 * ��ȡ����޼�
	 * @param movieid
	 * @param citycode
	 * @return
	 */
	MoviePrice getMoviePrice(Long movieid, String citycode);
	/**
	 * ͨ��ӰԺID��ʱ���ȡ������Ƭ�ĵ�Ӱ����������Ƭ��������
	 * @param cinemaId
	 * @param playDate
	 * @return
	 */
	List<Movie> getMovieListByCinemaIdAndPlaydate(Long cinemaId, Date playDate, int from, int maxnum);
	
	/**
	 * ͨ��ӰԺID�͵�ӰID��ѯ��ǰӰԺ��ӰƬ����Ƭ���ڼ�����
	 */
	List<Date> getMovieOfCinemaOpenDateList(Long cinemaid,Long movieid);
	
	List<Long> getHotPlayMovieIdList(String cityCode,String order);
	String[] getOpiSeatMap(Long mpid);
	/**
	 * ��ȡĳӰԺ��ǰ����������
	 * @param cinemaid
	 * @return
	 */
	List<AutoSetter> getValidSetterList(Long cinemaid,String status);
	/**
	 * ��ȡ��Ч�Ĵ���˵�������
	 * @return
	 */
	List<AutoSetter> getCheckSetterList();
	/**
	 * ��ȡʣ����λ��С��seatnum�ĳ���
	 * @param seatnum
	 * @return
	 */
	List<OpenPlayItem> getIntensiveOpiList(int seatnum);
	/**
	 * ��ѯ��������
	 * @param citycode
	 * @param cinemaId
	 * @param movieId
	 * @param from
	 * @param to
	 * @param open
	 * @return
	 */
	int getOpiCount(String citycode, Long cinemaId, Long movieId, Timestamp from, Timestamp to, boolean open);
	
	List<OpenPlayItem> getDisabledOpiLlist(String citycode, Long cinemaId, int from, int maxnum);
	int getDisabledOpiCount(String citycode, Long cinemaId);
	List<Long> getDisabledCinemaIdList(String citycode);
	void clearOpenSeatCache(Long mpid);
	/**
	 * ������λԤ��ͼ
	 * @param mpid
	 * @param room
	 * @param openSeatList
	 * @param hfhLockList
	 * @param seatStatusUtil
	 */
	void updateOpiSeatMap(Long mpid, CinemaRoom room, List<OpenSeat> openSeatList, List<String> lockList, SeatStatusUtil seatStatusUtil);
	void updateOpiSeatMap4Wd(Long mpid, CinemaRoom room, List<RoomSeat> seatList, List<String> lockList, SeatStatusUtil seatStatusUtil);
	/**
	 * ��ѯӰԺ��Ƭ��
	 * @param cinemaid
	 * @return List<Map(playdate,opicount)
	 */
	Map getOpiCountMap(Long cinemaid);
	List<MoviePlayItem> synchUnOpenMpi();
	
	/**
	 * �Ϻ���Ӱ�������˻�ȡ��Ƭ����
	 * @param citycode
	 * @param cinemaIds
	 * @param movieIds
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<OpenPlayItem> getArtFilmAllianceOpi(String citycode, String cinemaIds, String movieIds, int from, int maxnum);
	ErrorCode<String> validOpiStatusByPartner(OpenPlayItem opi, ApiUser partner, Member member);
	
	List<CinemaRoom> updateRoomList(String citycode, Date updatedate);
	List<OpenPlayItem> getOpiListByRoomId(Long roomid, int from, int max);
}
