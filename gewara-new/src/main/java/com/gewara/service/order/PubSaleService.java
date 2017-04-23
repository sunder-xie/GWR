package com.gewara.service.order;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.model.pay.PubMember;
import com.gewara.model.pay.PubSale;
import com.gewara.model.pay.PubSaleOrder;
import com.gewara.model.user.Member;
import com.gewara.support.ErrorCode;

public interface PubSaleService {
	ErrorCode<Map> joinPubSale(Long sid, Member member, Integer pice, Timestamp addtime);
	Long stopPubSale(PubSale sale);
	void cancelPubSaleOrder(String order, Long memberid, String reason);
	void cancelPubSaleOrder(PubSaleOrder order, Long memberid, String reason);
	void processPubSaleOrder(PubSaleOrder order);
	
	Integer getPubSaleCount(String name, String status);
	// ������� orderby (endtime)
	List<PubSale> getPubSaleList(String name, String status, int begin, int maxnum);
	List<PubMember> getPubMemberList(Long sid, Long memberid);
	List<PubMember> getPubMemberList(Long sid, Long memberid, int from, int maxnum);
	/**
	 * ����addtime֮��μӵ�����
	 * @param sid
	 * @param addtime
	 * @return
	 */
	Integer getPubMemberCount(Long sid, Timestamp addtime);
	//���ĳɹ�����
	Integer getMemberPubSaleOrderCountByMemberid(Long memberid, Long relatedid, Timestamp fromtime, Timestamp totime,	String citycode);
}
