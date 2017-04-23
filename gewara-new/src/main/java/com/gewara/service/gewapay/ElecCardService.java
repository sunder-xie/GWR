package com.gewara.service.gewapay;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gewara.helper.order.ElecCardContainer;
import com.gewara.model.acl.User;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.draw.Prize;
import com.gewara.model.draw.WinnerInfo;
import com.gewara.model.goods.BaseGoods;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.ElecCardBatch;
import com.gewara.model.pay.ElecCardExtra;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.GymOrder;
import com.gewara.model.pay.MemberAccount;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.user.Member;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.gym.CardItem;

public interface ElecCardService{
	/**
	 * ��ȡ��������
	 * @param status 
	 * @param applytype 
	 * @param applydep 
	 * @return
	 */
	List<ElecCardExtra> getTopCardExtraList(String status, String applydept, String applytype);
	List<ElecCardExtra> getSubCardExtraList(String status, String applydept, String applytype, Long adduserid, Timestamp addfrom, Timestamp addto);
	/**
	 * ���ɵ��ӿ�
	 * @param bid
	 * @param num
	 * @return
	 */
	ErrorCode<ElecCardExtra> genElecCard(Long bid, int num, Long userid);
	/**
	 * Ԥ����������������
	 * @param bid
	 * @return
	 */
	ErrorCode<ElecCardBatch> preSellBatch(Long bid, Long userid);
	/**
	 * ��ȡ��������
	 * @param bid
	 * @return
	 */
	List<ElecCard> getElecCardByBatchId(Long bid, String status, boolean mobile, int from, int maxrows);
	/**
	 * ����ĳ�ſ���ֻ�������߲��ܷ����Լ������Ŀ�
	 * @param cardId
	 * @param userId
	 * @return
	 */
	ErrorCode discardElecCard(Long cardId, Long userid);
	/**
	 * ����ĳ�ſ���ֻ�������߲��ܶ����Լ������Ŀ�
	 * @param cardId �ſ�ID
	 * @param userid ������ԱID
	 * @return �ɹ������� "success",��֮���ش�����Ϣ�� 
	 */
	ErrorCode lockElecCard(Long cardId, Long userid);
	/**
	 * �����ⶳ����
	 * @param batchid
	 * @param cardFrom
	 * @param cardTo
	 * @param userid
	 * @return
	 */
	ErrorCode batchLockElecCard(Long batchid, String cardFrom, String cardTo, Long userid);
	/**
	 * �������Ῠ��,�����û�ID�����κ�
	 * @param batchid ���κ�
	 * @param memberid �û�ID
	 * @param userid �û�ID
	 * @return �ɹ������� "success",��֮���ش�����Ϣ�� 
	 */
	ErrorCode<String> batchLockElecCard(Long batchid, Long memberid, Long userid);
	/**
	 * �ⶳĳ�ſ���ֻ�������߲��ܽⶳ�Լ�����Ŀ�
	 * @param cardId �ſ�ID
	 * @param userid ������ԱID
	 * @return �ɹ������� "success",��֮���ش�����Ϣ�� 
	 */
	ErrorCode unlockcardElecCard(Long cardId, Long userid);
	/**
	 * 
	 * @param batchid 
	 * @param cardFrom
	 * @param cardTo
	 * @param userid
	 * @return
	 */
	ErrorCode batchUnLockElecCard(Long batchid, String cardFrom, String cardTo, Long userid);
	/**
	 * �����ⶳ����,�����û�ID�����κ�
	 * @param batchid ���κ�
	 * @param memberid �û�ID
	 * @param userid �û�ID
	 * @return �ɹ������� "success",��֮���ش�����Ϣ�� 
	 */
	ErrorCode<String> batchUnLockElecCard(Long batchid, Long memberid, Long userid);
	/**
	 * �����˻���������
	 * @param cardId
	 * @return
	 */
	ErrorCode<String> returnElecCard(Long cardId, Long userid);
	ErrorCode<String> returnElecCard(Long batchid, String cardFrom, String cardTo, Long userid);
	/**
	 * ���۳���Ϊ����״̬
	 * @param cardId
	 * @param userid
	 * @return
	 */
	ErrorCode<String> unsellElecCard(Long cardId, Long userid);
	ErrorCode<String> unsellElecCard(Long batchid, String cardFrom, String cardTo, Long userid);
	/**
	 * ���ϼ����м���һ��������
	 * @param bid
	 * @param cardFrom
	 * @param cardTo
	 * @return
	 */
	ErrorCode<String> addCardFromParent(Long bid, String cardFrom, String cardTo, Long userid);
	/**
	 * ��ȡ����
	 * @param bid
	 * @param cardFrom
	 * @param cardTo
	 * @param status
	 * @return
	 */
	List<ElecCard> getCardList(Long bid, String cardFrom, String cardTo, String status);
	/**
	 * �۳�ĳ����
	 * @param batchId
	 * @param userId
	 * @return
	 */
	ErrorCode<String> soldElecBatch(Long batchId, Long userid);
	/**
	 * �����û��뿨
	 * @param member
	 * @param cardpass
	 * @return
	 */
	ErrorCode<String> registerCard(Member member, String cardpass, String ip);
	ErrorCode<ElecCard> chargeByCard(Member member, MemberAccount account, String cardpass, String ip);
	/**
	 * ���ݿ��Ż������ҿ�
	 * @param cardno
	 * @return
	 */
	ElecCard getElecCardByPass(String cardpass);
	ElecCard getMemberElecCardByNo(Long memberid, String cardno);
	ElecCard getHistElecCardByPass(String cardPass);
	ElecCard getHistElecCardByNo(String cardno);
	/**
	 * ��ȡ��������
	 * @param bid
	 * @return
	 */
	int getElecCardCountByBatchId(Long bid, String status);
	/**
	 * ���俨�Ÿ��ֻ�
	 * @param bid ����
	 * @param mobiles �ֻ��ţ����ŷָ�
	 * @param num ÿ���ֻ���������
	 * @param allowDup �Ƿ������غ�
	 * @return
	 */
	ErrorCode<String> assignMobile(Long batchid, List<String> mobileList, Integer num, String cardFrom, String cardTo);
	/**
	 * ȡ�����ŷ���
	 * @param batchid
	 * @param cardFrom
	 * @param cardTo
	 * @return
	 */
	ErrorCode<String> unassignMobile(Long batchid, String cardFrom, String cardTo);
	/**
	 * ȡ���󶨵��˻�
	 * @param bid
	 * @param cardFrom
	 * @param cardTo
	 * @return
	 */
	ErrorCode<String> unbindMember(Long bid, String flag, String cardFrom, String cardTo);
	/**
	 * �󶨵��˻�
	 * @param bid
	 * @param mobileList
	 * @param num
	 * @param cardFrom
	 * @param cardTo
	 * @return
	 */
	ErrorCode<String> bind2Member(Long bid, String flag, List<Long> memberidList, Integer num, String cardFrom, String cardTo);
	/**
	 * �˶������õĿ�
	 * @param order
	 * @param opi
	 * @param memberid
	 * @return
	 */
	ElecCardContainer getAvailableCardList(TicketOrder order, List<Discount> discountList, OpenPlayItem opi, Long memberid);
	ElecCardContainer getAvailableCardList(DramaOrder order, List<Discount> discountList, OpenDramaItem item, Long memberid);
	ElecCardContainer getAvailableCardList(SportOrder order, List<Discount> discountList, OpenTimeTable table, Long memberid);
	ElecCardContainer getAvailableCardList(GoodsOrder order, List<Discount> discountList, BaseGoods goods, Long memberid);
	ElecCardContainer getAvailableCardList(GymOrder order, List<Discount> discountList, CardItem item, Long memberid);
	/**
	 *  ��ѯ��ǰ�û���Ʊȯ
	 */
	List<ElecCard> getCardListByMemberid(Long memberid, String tag, int from, int maxnum);
	Integer getCardCountByMemberid(Long memberid, String tag);

	List<ElecCardBatch> getSubBatchListByMerchantid(Long merchantid);
	ErrorCode addCardFromParent(ElecCardBatch batch, int addnum, Long userid);
	/**
	 * ����������
	 * @param batch
	 * @return
	 */
	void addCardBatch(ElecCardBatch batch, Long userid);
	/**
	 * ���¿�������Ϣ
	 * @param bid
	 * @return
	 */
	void updateBatchExtra(ElecCardExtra extra);
	List<ElecCardExtra> getSubCardExtraListByStatus(String status);
	List<ElecCardExtra> getSubCardExtraListByIssuerId(Long issuserid);
	List<ElecCardExtra> getAllSubCardExtraList();
	/**
	 * ��������
	 * @param bid
	 * @return
	 */
	ErrorCode frozenBatch(Long bid);
	
	
	/**
	 * ����ӰԺid�õ����������ӰԺ�ĵ���ȯͳ����Ϣ
	 * @param cinemaId
	 * @return
	 */
	Map<String,Object> getTopCardExtraListByValidcinema(Long cinemaId);
	
	/**
	 * ��鶯̬�벢����Ʊȯ
	 * @param member
	 * @param cardPass 
	 * @param checkpass
	 * @param checkcount
	 * @return
	 */
	ErrorCode<ElecCard> checkActivationCard(Member member, String cardPass, String checkpass);
	/**
	 * ��ȡȯ����
	 * @param elecCard
	 * @return
	 */
	String getElecCardpass(ElecCard elecCard);
	/**
	 * ��ȡ������ε��û��б�
	 * @return
	 */
	List<User> getAddBatchUserList();
	/**
	 * �����ε�ȯ���Ƿ��б�ʹ�ù���
	 * @param batchId
	 * @return
	 */
	boolean hasUsed(ElecCardBatch batch);
	List<String> batchDiscard(Long userId, String[] cardPassOrNoList, boolean byCardno);
	/**
	 * ֻ�Ǻ�̨������ѯ
	 * @param cardno
	 * @return
	 */
	ElecCard queryCardByNo(String cardno);
	
	ErrorCode batchAddAmountWithElecCard(Set<Long> mems, Set<String> cards, Long operatorId, String ip);
	ErrorCode randomSendPrize(Prize prize, WinnerInfo winner);
	int upgradeElecCard();
}
