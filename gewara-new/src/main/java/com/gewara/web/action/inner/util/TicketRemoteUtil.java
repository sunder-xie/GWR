package com.gewara.web.action.inner.util;

import java.sql.Timestamp;
import java.util.Date;

import com.gewara.model.movie.CinemaRoom;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.util.DateUtil;
import com.gewara.xmlbind.ticket.SynchPlayItem;
import com.gewara.xmlbind.ticket.TicketRoom;

public abstract class TicketRemoteUtil {

	public static final String asynchLockOrderUrl = "asynch/getRemoteLockSeat";			//�첽��ȡ������λ
	
	public static final String lockOrderUrl = "/inner/lockSeat.xhtml";					//�µ�����
	public static final String unLockSeatUrl = "/inner/releaseSeat.xhtml";				//������λ
	public static final String fixOrderUrl = "/inner/confirmOrder.xhtml";				//ȷ�϶���
	public static final String cancelOrderUrl = "/inner/cancelOrder.xhtml";				//��Ʊ
	public static final String lockSeatUrl = "/inner/getLockSeatList.xhtml";			//����������λ
	public static final String cacheSeatUrl = "/inner/getLockSeatListFromCache.xhtml";	//���λ���������λ
	public static final String playItemUrl = "/inner/getPlayItem.xhtml";				//
	public static final String cinemaPlayItemUrl = "/inner/getCinemaPlayItem.xhtml";	//
	public static final String roomUrl = "/inner/getCinemaRoom.xhtml";					//
	public static final String roomSeatUrl = "/inner/getRoomSeat.xhtml";				//
	public static final String getRemoteOrderUrl = "/inner/getRemoteOrder.xhtml";		//��ѯ����
	public static final String checkOrderUrl = "/inner/checkRemoteOrder.xhtml";			//��鶩��
	public static final String featurePriceUrl = "/inner/featurePrice.xhtml";			//���óɱ���
	public static final String createRemoteOrderUrl = "/inner/createRemoteOrder.xhtml";	//��������
	public static final String backRemoteOrderUrl = "/inner/backRemoteOrder.xhtml";		//ӰԺ��Ʊ
	public static final String planSiteStatistcUrl = "/inner/getPlanSiteStatistc.xhtml";
	public static final String getMpiSeatUrl = "/inner/getMpiSeat.xhtml";
	public static final String getMtxSellNumUrl = "/inner/getMtxSellNum.xhtml";
	
	//������~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final String getWdRemoteOrderUrl = "/inner/getWdRemoteOrder.xhtml";					//���ӰԺ����
	public static final String createWdRemoteOrderUrl = "/inner/createWdRemoteOrder.xhtml";	//���ӰԺ����
	public static final String unlockWdOrderUrl = "/inner/unlockWdOrder.xhtml";
	public static final String getWdMemberUrl = "/inner/getWdMember.xhtml";
	public static final String getWdWapParamUrl = "/inner/getWdParam.xhtml";
	public static final String getWdOrderList = "/inner/getWdOrderList.xhtml";//adddate
	
	public static void copyMoviePlayItem(MoviePlayItem mpi, SynchPlayItem playItem){
		mpi.setPrice(playItem.getPrice());
		mpi.setEdition(playItem.getEdition());
		mpi.setLanguage(playItem.getLanguage());
		mpi.setMovieid(playItem.getMovieid());
		mpi.setCinemaid(playItem.getCinemaid());
		mpi.setLowest(playItem.getLowest());
		mpi.setRoomnum(playItem.getRoomnum());
		Timestamp playtime = playItem.getPlaytime();
		Date playdate = DateUtil.getBeginningTimeOfDay(new Date(playtime.getTime()));
		mpi.setPlaydate(playdate);
		mpi.setPlaytime(DateUtil.format(playtime, "HH:mm"));
	}
	
	public static void copyCinemaRoom(CinemaRoom room, TicketRoom ticketRoom){
		room.setLinenum(ticketRoom.getLinenum());
		room.setRanknum(ticketRoom.getRanknum());
		room.setSeatnum(ticketRoom.getSeatnum());
		room.setRoomtype(ticketRoom.getRoomtype());
	}
}
