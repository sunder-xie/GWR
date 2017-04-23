package com.gewara.service.ticket;

import java.util.List;

import com.gewara.helper.ticket.UpdateMpiContainer;
import com.gewara.model.movie.Cinema;
import com.gewara.model.movie.CinemaRoom;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.ticket.SynchPlayItem;

/**
 * ��Ticket��Ŀ������ͬ��
 * @author gebiao(ge.biao@gewara.com)
 * @since Apr 15, 2013 4:27:41 PM
 */
public interface TicketSynchService {
	/**
	 * @param synchPlayItem
	 * @param mpi
	 * @param cinema
	 * @param room
	 * @param msgList
	 */
	void updateSynchPlayItem(UpdateMpiContainer container, SynchPlayItem synchPlayItem, MoviePlayItem mpi, Cinema cinema, CinemaRoom room, List<String> msgList);
	
	/**
	 * ����ӰԺӰ����Ϣ
	 * @param cinemaid		ӰԺID���
	 * @param userid		����ԱID
	 * @return
	 */
	ErrorCode updateCinemaRoom(Long cinemaid, Long userid);
	
	/**
	 * ����Ӱ����λ��Ϣ
	 * @param room			Ӱ����Ϣ
	 * @param forceUpdate	�Ƿ�ǿ�Ƹ���
	 * @return
	 */
	List<String> updateRoomSeatList(CinemaRoom room, boolean forceUpdate);
	void updateOpenPlayItem(List<String> msgList);
	void updateOpenPlayItem(Long cinemaid, List<String> msgList);

	void updateSpiderPlayItem(SynchPlayItem synchPlayItem, MoviePlayItem mpi,
			Cinema cinema, List<String> msgList);
}
