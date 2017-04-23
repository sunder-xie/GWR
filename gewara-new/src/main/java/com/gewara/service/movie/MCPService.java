package com.gewara.service.movie;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.command.SearchCinemaCommand;
import com.gewara.model.movie.Cinema;
import com.gewara.model.movie.CinemaRoom;
import com.gewara.model.movie.GrabTicketMpi;
import com.gewara.model.movie.Movie;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.model.movie.MultiPlay;

/**
 * Movie,Cinema,PlayTime Service
 * 
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public interface MCPService {
	/**
	 * ���ݵ�Ӱ�����Ʋ���ӰԺ
	 * @param movieName
	 * @return ���������ĵ�һ����Ӱ
	 */
	Movie getMovieByName(String movieName);
	Integer getMovieCountByName(Long movieid,String movieName);
	/**
	 * ���ݵ�Ӱ����ģ����ѯ��Ӱ
	 * @param moviename
	 * @return ģ����ѯ�ĵ�Ӱ�б�
	 */
	List<Movie> searchMovieByName(String moviename);
	boolean updateMovieHotValue(Long movieId, Integer value);
	boolean updateCinemaHotValue(Long movieId, Integer value);
	/**
	 * @param orderField :�����ֶ�,Ϊ���������ų̶�����
	 * @return ��ǰ���ڷ�ӳ�ĵ�Ӱ�б�
	 */
	List<Movie> getCurMovieList(String citycode);
	List<Movie> getCurMovieList();
	/**
	 * @param from
	 * @param maxnum
	 * @return
	 * ������д���
	 */
	List<Movie> getCurMovieListByMpiCount(String citycode, int from, int maxnum);
	/**
	 * ĳӰԺĳ���ӰƬ
	 * @param date
	 * @return
	 */
	List<Movie> getCurMovieListByCinemaIdAndDate(Long cinemaId, Date date);

	/**
	 * @return ��ǰ���ڷ�ӳ�ĵ�Ӱ����
	 */
	Integer getCurMovieCount(String citycode);

	/**
	 * ��ȡ������ӳ��ӰƬ
	 * @return
	 */
	List<Movie> getFutureMovieList(int from, int maxnum,String order);
	/**
	 * @param endDate ��ֹ����
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Movie> getFutureMovieList(Date endDate,int from, int maxnum);
	/**
	 * @param movieId
	 * @param playdate
	 * @param from
	 * @param maxnum
	 * @return ��ӳ�õ�Ӱ��ӰԺ���������ų̶�����
	 */
	List<Long> getPlayCinemaIdList(String citycode, Long movieId, Date playdate);
	List<Long> getPlayCinemaIdListByCountycode(String countycode, Long movieid, Date playdate);
	List<Cinema> getPlayCinemaList(String citycode, Long movieId, Date playdate, int from, int maxnum);
	List<Cinema> getPlayCinemaListByCountycode(String countycode, Long movieid, Date playdate) ;
	/**
	 * @param citycode ���д���
	 * @param movieid	��ӰID
	 * @param playdate ����
	 * @return ��ѯ��ǰ���ڵ�Ӱ����Ƭ��ӰԺID
	 */
	List<Long> getCurCinemaIdList(String citycode, Long movieid, Date playdate);
	/**
	 * @param movieId
	 * @param date
	 * @return ��ӳ�õ�Ӱ��ӰԺ����
	 */
	Integer getPlayCinemaCount(String citycode, Long movieId);
	/**
	 * �۸õ�ӰƱ��ӰԺ����
	 * @param movieid
	 * @return
	 */
	Integer getOrderPlayCinemaCount(String citycode, Long movieid);
	/**
	 * @param cinemaId
	 * @return ��ǰ��ӰԺ�ķ�ӳӰƬ�б�
	 */
	List<Movie> getCurMovieListByCinemaId(Long cinemaId);
	/**
	 * @param movieId
	 * @return ĳ��Ӱ��ǰ����Ƭ����
	 */
	List<Date> getCurMoviePlayDate(String citycode, Long movieId);
	List<Date> getCurMoviePlayDate2(String citycode, Long movieId);
	/**
	 * @param cinemaId
	 * @return ӰԺ��ǰ����Ƭ����
	 */
	List<Date> getCurCinemaPlayDate(Long cinemaId);
	/**
	 * @param cinemaId
	 * @param date
	 * @return ��ӰԺĳ�����Ƭ����
	 */
	Integer getCinemaMpiCountByDate(Long cinemaId, Date date);
	/**
	 * @param cinemaId
	 * @param date
	 * @return ��ӰԺ��ǰĳ�����Ƭ
	 */
	List<MoviePlayItem> getCinemaCurMpiListByDate(Long cinemaId, Date date);

	/**
	 * ��ȡĳӰ��ĳ�����Ƭ
	 * @param roomId
	 * @return
	 */
	List<MoviePlayItem> getCinemaCurMpiListByRoomIdAndDate(Long roomId, Date playdate);
	Integer getMovieCurMpiCount(String citycode, Long movieId);
	Integer getMovieCurMpiCountByPlaydate(String citycode, Long movieId, Date playdate);
	/**
	 * ��ȡ��ӰԺ�ĵ�ǰ��Ƭ
	 * @param cinemaId
	 * @return ��ӰԺ�ĵ�ǰ������Ƭ
	 */
	List<MoviePlayItem> getCinemaCurMpiList(Long cinemaId);
	List<MoviePlayItem> getCinemaMpiList(Long cinemaId,Long movieId, Date fyrq);
	List<MoviePlayItem> getCinemaMpiList(Long cinemaId, Date fyrq);

	int getCinemaMpiCount(Long cinemaId,Long movieid, Date fyrq);
	/**
	 * @param cinemaId
	 * @param movieId
	 * @param playdate
	 * @return ĳӰԺĳӰƬĳ��ĵ�ǰ��Ƭ
	 */
	List<MoviePlayItem> getCurMpiList(Long cinemaId, Long movieId, Date playdate);
	/**
	 * �õ���ӳӰƬ��id�б�(������Ƭ������)
	 * @return
	 */
	List<Long> getCurMovieIdList(String citycode);
	/**
	 * �õ���ӳӰƬ��id�б�(������Ƭ������)
	 * @return
	 */
	List<Long> getCurMovieIdList(String citycode,Date playdate);
	/**
	 * ���ݵ�Ӱ��������
	 * @param keyname
	 * @return
	 */
	List<Long> getMovieIdByMoviename(String keyname);
	/**
	 * ����ӰԺ��Ӱ�����ƻ�ȡӰ��ID
	 * @param cinemaId
	 * @param playroom
	 * @return
	 */
	CinemaRoom getRoomByRoomname(Long cinemaId, String playroom);
	CinemaRoom getRoomByRoomnum(Long cinemaId, String playroom);
	List<Map> getGroupMoviePlayItemByCinema_Time(String citycode,Long mid,Date d,String playtime,String time1,String time2);
	void sortMoviesByMpiCount(String citycode, List<Movie> movieList);
	void sortTodayMoviesByMpiCount(String citycode,List<Movie> movieList);
	/**
	 * ����Ӱ������ӳ���ڡ���ӳʱ���ҵ�Ψһ��Ƭ
	 * @param roomId
	 * @param movieId
	 * @param date
	 * @param playtime
	 * @return
	 */
	MoviePlayItem getUniqueMpi(String opentype, Long cinemaid, Long roomId, Date playdate, String playtime);
	/**
	 * ��������Ƭ
	 * @param cinemaid
	 * @param movieid
	 * @param playdate
	 * @param playtime
	 * @return
	 */
	MoviePlayItem getUniqueMpi2(Long cinemaid, Long movieid, Date playdate, String playtime);
	GrabTicketMpi getGrabTicketMpiListByMpid(Long mpid);
	/**
	 * ��ѯӰԺ����
	 */
	Integer getTicketCinemaCount(String citycode, String countycode,String indexareacode, String cname);
	Integer getCinemaCount(String citycode, String countycode, String indexareacode, String cname, boolean booking);
	
	/**
	 * ���ݾ�γ�ȷ�Χ��ѯ����ӰԺ
	 * @param maxLd
	 * @param minLd
	 * @param maxLa
	 * @param minLa
	 * @param movieid ����Ƭ��
	 * @return
	 */
	List<Cinema> getNearCinemaList(double pointxx, double pointyy, int distant, Long movieid, String citycode, Date playdate);
	List<Cinema> getCinemaListByNearOrder(double ld/*pointx*/, double la, List<Cinema> cinemaList, int distance, boolean validDistance);
	/**
	 * ��ѯӰԺ�б�
	 * @param countycode
	 * @param indexareacode
	 * @param movieid����ӳ��
	 * @return
	 */
	List<Cinema> getCinemaListByIndexareaCodeCountycodeMovie(String countycode, String indexareacode, Long movieid);
	/**
	 * ��ȡ���п��Ŷ�Ʊ��ӰԺ
	 * @param citycode ����á�,���ָ���AdminCityContant.CITYCODE_ALL��ʾ���г���
	 * @param countycode
	 * @return
	 */
	List<Long> getBookingCinemaIdList(String citycode, String countycode);
	/**
	 * ������ƱӰԺ�б�
	 * @return
	 */
	List<Cinema> getBookingCinemaList(String citycode);
	
	/**
	 * ��ѯ����ӰԺ
	 */
	List<Cinema> getCinemaListByCitycode(String citycode, int from, int maxnum);
	/**
	 * ��ѯ����ӰԺ������
	 */
	Integer getCinemaCountByCitycode(String citycode);
	/**
	 * ��ȡ���Ŷ�Ʊ��ӰƬ������Ƭ��������
	 * @param citycode
	 * @return
	 */
	List<Movie> getOpenMovieList(String citycode);
	List<Long> getSpecialActivityOpiMovieList(Long aid, Date playdate, String citycode);
	/**
	 * ��ȡӰԺ��ǰ��ӳӰƬ
	 * @param cinemaid
	 * @return
	 */
	List<MultiPlay> getCurMultyPlayList(Long cinemaid);
	
	/**
	 *  @function  �ɶ�Ʊ + ����
	 * 	@author bob.hu
	 *	@date	2011-07-06 18:11:18
	 */
	List<Cinema> getHotBookingCinames(String citycode, int from, int maxnum);
	List<Cinema> getCinemaList(String citycode,String countycode, int from,int maxnum,Long movieid, boolean onlyBooking, boolean hasPlay);
	
	List<Cinema> getCinemaListBySearchCmd(SearchCinemaCommand cmd, String citycode, int from, int maxnum);
	List<Long> getCinemaIdListBySearchCmd(SearchCinemaCommand cmd, String citycode);
	/**
	 * ��������Ʒ��ӰԺID�б�������������
	 * @param citycode
	 * @return
	 */
	List<Long> getCinemaIdListByGoods(String citycode);
	//DetachedCriteria getCinemaQuery(SearchCinemaCommand cmd, String citycode);
	List<Movie> getReleaseMovieList(Date date);
	
	List<Map<String, Object>> getMovieProjectionCount(Date date, String status);
	/**
	 * ��ȡ��ӰԺĳ���ӳ�ĵ�Ӱ����
	 * @param cinemaId
	 * @param date
	 * @return
	 */
	Integer getCinemaMovieCountByDate(Long cinemaId, Date date);
	List<Map<String, Object>> getReleaseDateMovieCount();
	MoviePlayItem getMpiBySeqNo(String seqNo);
	List<Movie> getHotPlayMovieList(String citycode);
	
	/**
	 * ��ɫͳ��ӰԺ
	 * @return
	 */
	Map<String,Integer> getFeatureCinema(String citycode,String... feature);
	/**
	 * ��ɫӰ����ӰԺͳ��
	 * IMAX��10��   4D 11�� ������
	 * @param citycode
	 * @return
	 */
	Map<String,Integer> getRoomFeatureCinema(String citycode);
	/**
	 * ĳ��ӰԺ����ɫӰ������
	 * @param cinemaId
	 * @return
	 */
	List<String> getCharacteristicCinemaRoomByCinema(long cinemaId);
	/**
	 * ������ɫӰ�����Ͳ�ѯ��ɫӰ��ӰԺid�б�
	 * @param cType
	 * @param citycode
	 * @return
	 */
	List<Long> getCinemaIdListByRoomCharacteristic(String cType,String citycode);
	
	List<Long> getRoomIdListByCinemaAndCtype(long cinemaId,String ctype);
	
	Integer getPlayCinemaCountByPlayDate(String citycode, Long movieId, Date playdate);
	Date getMinPlaydateByMovieid(Long movieid);
	Integer getMovieCurMpiCount(String citycode, Long movieId, Date startdate, Date enddate);
	//���ݵ�Ӱ�õ�����Ӱ������
	List<CinemaRoom> getCurCinemaRoomByMovieId(String citycode, Long movieid, Date playdate);
	//����ӰԺ�õ���ǰ�г��ε�Ӱ��ID
	List<Long> getRoomIdListByOpi(Long cinemaid);
}
