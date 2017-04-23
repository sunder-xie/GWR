package com.gewara.service.bbs;

import java.sql.Timestamp;
import java.util.List;

import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.user.SysMessageAction;
import com.gewara.model.user.UserMessageAction;

public interface UserMessageService {
	Integer getReceiveUserMessageCountByMemberid(Long memberid, Integer isread);
	List<UserMessageAction> getReceiveUserMessageListByMemberid(Long memberid, Integer isread, int from, int maxnum);
	Integer getSendUserMessageCountByMemberid(Long memberid);
	List<UserMessageAction> getSendUserMessageListByMemberid(Long memberid, int from, int maxnum);
	Integer getSysMsgCountByMemberid(Long memberid, String status);
	List<SysMessageAction> getSysMsgListByMemberid(Long memberid, String status, int from, int maxnum);
	UserMessageAction getUserMessageActionByUserMessageid(Long mid);
	List<UserMessageAction> getUserMessageListByGroupid(Long groupid);
	Integer getUMACountByMemberid(Long memberid);
	List<UserMessageAction> getUMAListByMemberid(Long memberid, Integer first, int maxnum);
	boolean isSendMsg(Long memberid);
	void initSysMsgList(List<SysMessageAction> sysMsgList);
	/**
	 * �û����û�֮���˽��
	 * @param frommemberid
	 * @param tomemberid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<UserMessageAction> getUserMessageActionByFromidToid(Long frommemberid, Long tomemberid, Timestamp addtime, int from, int maxnum);
	/**
	 * �ҵ�ȫ��˽��
	 * @param memberid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<UserMessageAction> getUserMessageActionByMemberid(Long memberid, int from, int maxnum);
	
	/**
	 *  �����û�Id, ״̬ ��ҳ��ѯ
	 * @param memberId
	 * @param status
	 * @param first
	 * @param max
	 * @return List<UserMessageAction>
	 */
	Integer countMessagesByMemIdAndStatus(Long memberId, String status);
	List<UserMessageAction> getMessagesByMemIdAndStatus(Long memberId, String status, int isread, int from, int maxnum);
	/**
	 *  �����б�(������¼)
	 */
	UserMessageAction getPublicNotice();
	
	/**
	 * �Ƿ��Ѿ��������롢����
	 * @param commuid
	 * @param memberid
	 * @return
	 */
	boolean isExistSysMessageAction(Long commuid, Long memberid, String action, boolean flag);
	/**
	 * ����commuid,memberid,action,status��ѯ�Ǵ�������
	 * @param commuid
	 * @param memberid
	 * @param action
	 * @param flag
	 * @return
	 */
	SysMessageAction getSysMessageAction(Long commuid, Long memberid, String action, boolean flag);
	Integer getCountMessageByMessageActionId(Long id);
	
	/**
	 * ��ѯ�ռ���δ����Ϣ����
	 */
	Integer getNotReadMessage(Long memberid,Integer isRead);
	
	/**
	 * ��ѯϵͳ��Ϣδ������
	 */
	Integer getNotReadSysMessage(Long memberid,Long isRead);
	List<SysMessageAction> getNotReadSysMessageList(Long memberid,Long isRead);
	/**
	 * ����actionid,action,status��ѯϵͳ��Ϣ�б�
	 * @param actionid
	 * @param action
	 * @param stauts
	 * @return
	 */
	List<SysMessageAction> getSysMessageActionListByActionidAndActionAndStatus(Long actionid, String action, String stauts, int from, int maxnum);
	
	/**
	 *  20101109 ��ӷ���ϵͳ��Ϣģ��
	 */
	void sendSiteMSG(Long tomemberid, String action, Long actionid, String body);
	
	void addMsgAction(TicketOrder order, OpenPlayItem opi);
}
