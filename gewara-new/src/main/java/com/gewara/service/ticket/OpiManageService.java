package com.gewara.service.ticket;

import java.sql.Timestamp;
import java.util.List;

import com.gewara.json.TempRoomSeat;
import com.gewara.model.acl.User;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.model.ticket.AutoSetter;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.OpenSeat;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.ticket.MpiSeat;

public interface OpiManageService {
	/**
	 * ��������е���λ
	 * @param mpid
	 * @param seatid
	 */
	ErrorCode removePriceSeat(Long mpid, Long seatid);
	/**
	 * ����λ���뵽ĳ��������
	 * @param mpid
	 * @param seattype
	 * @param seatid
	 */
	ErrorCode<String> addPriceSeat(Long mpid, String seattype, Long seatid);
	/**
	 * ���ŵ�һ�������ó�����Ϣ
	 * @param mpid
	 * @param opentype �������ͣ�gewa�����
	 * @return
	 */
	ErrorCode<OpenPlayItem> openMpi(MoviePlayItem mpi, Long userid, String opentype, TempRoomSeat tempRoomSeat);
	/**
	 * ���Ŷ�Ʊ����
	 * @param mpi
	 * @param userid
	 * @param mpiSeatList
	 * @return
	 */
	ErrorCode<OpenPlayItem> openPnxMpi(MoviePlayItem mpi, Long userid, List<MpiSeat> mpiSeatList);
	/**
	 * ������ﳡ�Σ����������λ
	 * @param mpi
	 * @param userid
	 * @return
	 */
	ErrorCode<OpenPlayItem> openWdMpi(MoviePlayItem mpi, Long userid);
	
	/**
	 * ����ˢ����λ��Ϣ
	 * @param opi
	 * @param userid
	 * @param mpiSeatList
	 * @param refresh
	 * @return
	 */
	ErrorCode<OpenPlayItem> refreshOpiSeat(Long mpid, Long userid, List<MpiSeat> mpiSeatList);
	
	/**
	 * �ı�״̬
	 * @param mpid
	 * @param status
	 * @param userid 
	 * @return
	 */
	ErrorCode<OpenPlayItem> changeMpiStatus(Long mpid, String status, Long userid);
	/**
	 * ����ͳһ�ۼ�
	 * @param mpid
	 * @param gewaprice
	 * @return
	 */
	ErrorCode<OpenPlayItem> updateGewaPrice(Long mpid, Integer gewaprice, User user);
	/**
	 * ���ĳɱ���
	 * @param mpid
	 * @param costprice
	 * @return
	 */
	ErrorCode updateCostPrice(Long mpid, Integer costprice, User user);
	ErrorCode<OpenPlayItem> updateOpenPlayItem(Long opid);
	/**
	 * �޸Ŀ��ÿ�
	 * @param mpid
	 * @param cardtype
	 * @return
	 */
	ErrorCode<String> updateElecard(Long mpid, String cardtype, User user);
	/**
	 * ����������λ
	 * @param mpid
	 * @param locktype
	 * @param lockReason
	 * @param lockline
	 * @param lockrank
	 * @return
	 */
	ErrorCode batchLockSeat(Long mpid, String locktype, String lockReason, String lockline, String lockrank);
	/**
	 * ����������λ
	 * @param mpid
	 * @param lockline
	 * @param lockrank
	 * @return
	 */
	ErrorCode batchUnLockSeat(Long mpid, String lockline, String lockrank);
	/**
	 * �ֹ��ͷ���λ
	 * @param mpid
	 * @param seatId
	 * @return
	 */
	ErrorCode<OpenSeat> releaseSeat(Long mpid, Long seatId);
	/**
	 * ����ĳ���Σ��������޶����ĳ���
	 * @param mpid
	 * @return
	 */
	ErrorCode<OpenPlayItem> discardOpiByMpid(Long mpid, Long userid);
	/**
	 * ������λ��ʼ״̬
	 * @param seatid
	 * @param initstatus
	 * @return
	 */
	ErrorCode updateSeatInitStatus(Long seatid, String initstatus);
	/**
	 * ������λͼʵʱ��������ͳ��
	 * @param opid
	 * @return
	 */
	void updateOpiStats(Long opid, List<String> hfhLockList, boolean isFinished);
	
	/**
	 * ����������
	 * @param seatid
	 * @param loveInd
	 * @return
	 */
	ErrorCode<Long> updateSeatLoveInd(Long seatid, String loveInd);
	ErrorCode<String> batchAddPriceSeat(Long mpid, String seattype, String rows, String ranks);
	ErrorCode<String> batchRemovePriceSeat(Long mpid, String seattype, String rows, String ranks);
	/**
	 * ��һ�ο��ų��Σ���¼�桢�����ԡ��۸�ԭʼ��Ϣ�����ں���ͬ����Ƭ���Ա�
	 * @param mpi
	 */
	void updateOriginInfo(MoviePlayItem mpi);
	/**
	 * �Զ����ų���
	 * @param mpid
	 * @param setter
	 * @param tempRoomSeat
	 * @param userid
	 * @return
	 */
	ErrorCode<OpenPlayItem> autoOpenMpi(MoviePlayItem mpi, AutoSetter setter, TempRoomSeat tempRoomSeat, Long userid);
	/**
	 * �ӷ����������ؽ�MoviePlayItem
	 * ͬ������ʱ��MoviePlayItemɾ����ӰԺɾ����Ƭ������OpenPlayItem����������ӰԺ�ָֻ���Ƭ����
	 * @param opi
	 * @return
	 */
	boolean restoreMpiFromHisData(String seqNo) ;
	void saveMpiToHisData(MoviePlayItem mpi);
	/**
	 * ����������λ
	 * @param opi
	 * @param tempRoomSeat
	 * @return
	 */
	ErrorCode batchInsertOpenSeat(OpenPlayItem opi, TempRoomSeat tempRoomSeat);
	/**
	 * ��������������λ����ʱ�������λ��������֧��������ͳ���������⣩
	 * @return
	 */
	int verifyOpiSeatLock(Long mpid);
	void updateLocknum(Long mpid, int total);
	/**
	 * @param mpid
	 * @param fee
	 * @param userid 
	 * @return
	 */
	ErrorCode<OpenPlayItem> updateOpiFee(Long mpid, Integer fee, Long userid);
	ErrorCode<OpenPlayItem> changePartnerStatus(Long mpid, String status);
	ErrorCode<OpenPlayItem> changeOpentime(Long mpid, Timestamp opentime, Long userid);
	ErrorCode<OpenPlayItem> changeClosetime(Long mpid, Timestamp closetime, Long userid);
	/**
	 * ���Ļ�������
	 * @param opid
	 * @param minpoint
	 * @param maxpoint
	 * @param id
	 * @return
	 */
	ErrorCode<OpenPlayItem> updatePointLimit(Long opid, Integer minpoint, Integer maxpoint, Long userid);
	ErrorCode<OpenPlayItem> updateGivepoint(Long opid, Integer point, Long userid);
	ErrorCode<OpenPlayItem> updateSpflag(Long opid, String spflag, Long userid);
	ErrorCode<OpenPlayItem> updateOpiRemark(Long opid, String remark, Long userid);
	void updateOpiStatsByMtx(Long opid, int sellNum, boolean isFinished);
}
