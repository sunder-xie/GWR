package com.gewara.untrans.ticket;

import java.util.Map;

import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.ConfigTrigger;

/**
 * ��Ʊ�Ŷӻ���
 * 1���̼���������
 * 2��ÿ���û�����
 * 3��ÿ��IP����
 * 4�������û��ܿ���
 * @author acerge(acerge@163.com)
 * @since 9:29:14 AM Oct 26, 2011
 */
public interface TicketQueueService extends ConfigTrigger{
	ErrorCode isMemberAllowed(Long memberid, Long cinemaid, String ip);
	ErrorCode isPartnerAllowed(Long partnerid, Long cinemaid);
	Map getStatistics();
	void clearData();
}
