package com.gewara.service.bbs;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.user.Agenda;
import com.gewara.model.user.Member;
import com.gewara.xmlbind.activity.RemoteActivity;

public interface AgendaService {
	/**
	 * ɾ����������Ա�б�
	 * @param activityid
	 * @param member
	 */
	void deleteActivityAgenda(Long activityid, Member member);

	List<Agenda> getAgendaListByDate(Long memberid, Date date, boolean flag ,boolean isNewTime,int from,int maxnum);
	Integer getAgendaCountByDate(Long memberid, Date date);

	/**
	 * ��̨��ѯ��ʾ�����б���ص�������Ϣ
	 * @param status
	 * @param from
	 * @param maxnum
	 * @param fromDate
	 * @param toDate
	 * @param keyName
	 * @return
	 */
	List<Agenda> getAgendaList(String status, int from, int maxnum, Date fromDate, Date toDate, String keyName);
	Integer getAgendaListCount(String status, Date fromDate, Date toDate, String keyName);
	List<SMSRecord> getFriendListFromSMS(Long recordid);
	Agenda getAgendaByAction(Long actionid, String action, Long memberid);
	
	//�������ڲ�ѯ����
	List<Agenda> getAgendaListByDate(Long memberid, Date startDate, Date endDate);
	//��ӻ����
	Agenda addActivityAgenda(RemoteActivity activity, Member member);
	//�������
	Agenda addAgenda(String title, Long memberid, String membername, Date startdate, String starttime, String content, String tag, Long relatedid,
			String category, Long categoryid, Timestamp addtime, String action, Long actionid, Date enddate, String endtime, String address, String otherinfo);
	void addOrderAgenda(TicketOrder order, OpenPlayItem opi);
	void addOrderAgenda(SportOrder order, OpenTimeTable ott);
	void addOrderAgenda(DramaOrder order, OpenDramaItem odt);
	/**
	 * ��ȡ��������б�
	 * @param actionid
	 * @return
	 */
	List<Agenda> getAgendaList(Long actionid);
}
