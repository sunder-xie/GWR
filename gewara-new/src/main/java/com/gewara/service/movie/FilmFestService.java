package com.gewara.service.movie;

import java.util.Date;
import java.util.List;

import com.gewara.model.common.County;
import com.gewara.model.content.News;
import com.gewara.model.movie.Movie;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.model.movie.SpecialActivity;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.untrans.monitor.ConfigTrigger;

public interface FilmFestService extends ConfigTrigger{
	//��ѯ��Ӱ�ڻ��Ϣ
	SpecialActivity getSpecialActivity(String tag);
	/**
	 * 2011 ��Ӱ��
	 * ��ȡ��չӰƬ��Ϣ
	 */
	List<Long> getFilmFestMovieIdList(String flag, String specialFlag, String specialValue, int from,int maxnum);
	/**
	 * 2012 ��Ӱ��
	 * �ֻ�api��ȡ��չӰƬ��Ϣ
	 * @param flag
	 * @param specialFlag
	 * @param specialValue
	 * @param order
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Movie> getFilmFestMovie(String flag, String specialFlag, String specialValue,int from,int maxnum);
	
	List<Long> getSpecialActivityMovieIds(String flag);
	
	Integer getFilmFestMovieCount(String flag,String specialFlag,String specialValue);
	
	/**
	 * 2011 ��Ӱ��
	 * ��ȡӰԺ������Ϣ
	 */
	List<County> getCinemaCountyList(String flag);
	
	List<Long> getFilmFestCinema(String flag, String citycode, String countycode);
	
	/**
	 * 2011 ��Ӱ��
	 * ��չӰƬ��ѯ(web)
	 */
	List<Long> getJoinMovieIdList(Long batch,String citycode,Date curDate,String type,String flag,String state,String moviename,Long cinemaid, String festtype, String order, int from, int maxnum);
	Integer getJoinMovieCount(Long batch,String citycode,Date curDate,String type,String flag,String state,String moviename,Long cinemaid, String festtype);
	
	//��ǰ��Ӱ��λ������
	Integer getCurMovieSeatSum(Long movieid, Long batch);
	List<MoviePlayItem> getMoviePlayItemList(String citycode, Long movieId, Long cinemaId, Date playDate, Long batch, String order, int from, int maxnum);
	List<Long> getMoviePlayItemIdList(String citycode, Long movieId, Long cinemaId, Date playDate, boolean  isGeHour, Long batch, String order, int from, int maxnum);
	Integer getMoviePlayItemCount(String citycode, Long movieId, Long cinemaId, Date playDate, boolean  isGeHour, Long batch);
	List<MoviePlayItem> getMaybeMoviePlayItemList(List<Long> cinemaIdList, List<Long> movieIdList, Date playDate, Long batch);
	/**
	 * �õ���Ӱ��ӰԺ������Ƭ�ĵ�Ӱ 
	 */
	List<Movie> getCurMovieListByCinemaIdAndDate(Long cinemaId, Date date, Long batch);
	/**
	 * �õ���Ӱ��ӰԺ������Ƭ�ĵ�Ӱ 
	 */
	List<MoviePlayItem> getCinemaCurMpiListByRoomIdAndDate(Long roomId, Date playdate, Long batch);
	
	List<Long> getMPIMovieIds(String citycode, long batch);
	/**
	 * ��Ӱ�ڲ�ѯ������Ϣ
	 */
	List<News> getFilmFestNewsList(String citycode,String flag,String[] newstype,Date releasedate,int from,int maxnum);
	Integer getFilmFestNewsCount(String citycode,String flag,String[] newstype,Date releasedate);
	List<String> getFilmFestNewsDateList(String citycode,String flag);
	boolean isFilmMoviePlayItem(Long batchid);
	String getCachePre();
	/**
	 * ��ʾ����ʾ
	 * */
	void updateMpiOtherinfo(String[] idList, String unopengewa, String unshowgewa);
	
	void copyOpiRemark(List<OpenPlayItem> opiList, List<String> msgList);
}
