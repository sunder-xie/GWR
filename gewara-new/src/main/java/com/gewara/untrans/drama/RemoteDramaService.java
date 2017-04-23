package com.gewara.untrans.drama;

import com.gewara.api.gpticket.vo.ticket.DramaRemoteOrderVo;
import com.gewara.support.ErrorCode;

public interface RemoteDramaService {
	
	ErrorCode<String> getRemoteLockSeat(String areaseqno);
	
	ErrorCode<String> getRemoteLockPrice(String areaseqno);

	ErrorCode<DramaRemoteOrderVo> backOrder(Long orderid, String description);

	/**
	 *	����Զ�̶���
	 * @param seqno
	 * @param orderid
	 * @param mobile
	 * @param areaseqno
	 * @param opentype
	 * @param seatLabel
	 * @return
	 */
	ErrorCode<DramaRemoteOrderVo> newCreateOrder(String seqno, Long orderid, String mobile, String areaseqno, String opentype, String seatLabel);
	
	/**
	 * ����������������λ
	 * @param seqno
	 * @param orderid
	 * @param mobile
	 * @param areaseqno
	 * @param seatLabel
	 * @return
	 */
	ErrorCode<DramaRemoteOrderVo> newLockSeat(String seqno, Long orderid, String mobile, String areaseqno, String seatLabel);
	
	/**
	 * �����������۸�����
	 * @param seqno
	 * @param orderid
	 * @param mobile
	 * @param areaseqno
	 * @param seatLabel
	 * @return
	 */
	ErrorCode<DramaRemoteOrderVo> newLockPrice(String seqno, Long orderid, String mobile, String areaseqno, String seatLabel);
	
	/**
	 * ȷ�϶������ɹ���Ʊ
	 * @param seqno
	 * @param orderid
	 * @param mobile
	 * @param areaseqno
	 * @param opentype
	 * @param seatLabel
	 * @return
	 */
	ErrorCode<DramaRemoteOrderVo> newFixOrder(String seqno, Long orderid, String mobile, String areaseqno, String opentype, String seatLabel, String greetings);
	
	/**
	 * ȡ������
	 * @param orderid
	 * @return
	 */
	ErrorCode newUnRemoteOrder(Long orderid);
	
	ErrorCode<DramaRemoteOrderVo> qryOrder(Long orderid, boolean forceRefresh);
	
	ErrorCode<DramaRemoteOrderVo> checkOrder(Long orderid, boolean forceRefresh);

	ErrorCode<String> qryTicketPrice(String seqno);

	ErrorCode<String> qryOrderPrintInfo(Long orderid);
}
