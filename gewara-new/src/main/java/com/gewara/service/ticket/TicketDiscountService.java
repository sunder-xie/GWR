package com.gewara.service.ticket;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import com.gewara.helper.discount.SportSpecialDiscountHelper.OrderCallback;
import com.gewara.helper.order.OrderContainer;
import com.gewara.helper.order.TicketOrderContainer;
import com.gewara.model.draw.Prize;
import com.gewara.model.draw.WinnerInfo;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.SpCode;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.user.Member;
import com.gewara.model.user.TempMember;
import com.gewara.service.OrderException;
import com.gewara.support.ErrorCode;

/**
 * �����Ż����
 * @author gebiao(ge.biao@gewara.com)
 * @since Feb 1, 2013 7:41:18 PM
 */
public interface TicketDiscountService {
	/**
	 * ʹ�û���
	 * @param orderId
	 * @param member
	 * @param usePoint
	 * @return
	 */
	ErrorCode usePoint(Long orderId, Long memberId, int usePoint);
	/**
	 * ���������Ż�
	 * @param order
	 * @param spdiscountFlag
	 * @return
	 */
	/**
	 * @param order
	 * @param sd
	 * @param jsonValue 
	 * @param jsonKey 
	 * @return
	 * @throws Exception
	 */
	ErrorCode<OrderContainer> useSpecialDiscount(Long orderid, SpecialDiscount sd, OrderCallback callback) throws OrderException;
	/**
	 * @param orderId
	 * @param card
	 * @param memberid
	 * @return TicketOrderContainer(order, opi, seatList, curDiscount, discountList)
	 */
	ErrorCode<TicketOrderContainer> useElecCard(Long orderId, ElecCard card, Long memberid);
	/**
	 * @param orderId
	 * @param card
	 * @param memberid
	 * @return TicketOrderContainer(order, opi, seatList, curDiscount, discountList)
	 */
	ErrorCode<TicketOrderContainer> useElecCardByTradeNo(String tradeno, ElecCard card, Long memberid);
	ErrorCode<String> randomSendPrize(Prize prize, WinnerInfo winner);
	/**
	 * �����ȡһ��ȯ
	 * @param member
	 * @param spid
	 * @return
	 */
	ErrorCode<String> randomBindCard(Member member, Long spid);
	/**
	 * ���ɶ�̬��
	 * @param sd
	 * @param maxnum
	 * @return
	 */
	List<SpCode> genSpCode(SpecialDiscount sd, int maxnum);
	/**
	 * ��ȡ��̬��
	 * @param pass
	 * @return
	 */
	SpCode getSpCodeByPass(String pass);
	
	List<SpCode> getSpCodeList(Long memberid, Long spid, int fromnum, int maxnum);
	Integer getSpCodeCountByMemberid(Long memberid);
	/**
	 * ��ȡͳ������
	 * @param sdid
	 * @return
	 */
	Map getSpCodeCountStats(Long sdid);
	void exportSpCodePassBySd(SpecialDiscount sd, Writer writer, Long userid) throws IOException;
	
	/**
	 * �������������
	 * FIXME:��ʱʹ�ã�����ɾ��
	 * @param tmid
	 * @return
	 */
	ErrorCode<Member> processBaiduPaySuccess(TempMember tm, Long spid);
}
