package com.gewara.service.order;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.command.GoodsCommand;
import com.gewara.command.SearchOrderCommand;
import com.gewara.helper.discount.SportSpecialDiscountHelper.OrderCallback;
import com.gewara.helper.order.GoodsOrderContainer;
import com.gewara.helper.order.OrderContainer;
import com.gewara.model.api.ApiUser;
import com.gewara.model.goods.ActivityGoods;
import com.gewara.model.goods.BaseGoods;
import com.gewara.model.goods.Goods;
import com.gewara.model.goods.GoodsGift;
import com.gewara.model.goods.SportGoods;
import com.gewara.model.goods.TicketGoods;
import com.gewara.model.pay.BuyItem;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.OrderNote;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.user.Member;
import com.gewara.service.OrderException;
import com.gewara.support.ErrorCode;

public interface GoodsOrderService extends GewaOrderService{
	/**
	 * ��������
	 * @param goods
	 * @param member
	 * @param quantity ��������
	 * @return
	 */
	GoodsOrder addGoodsOrder(Goods goods, Member member, String mobile, int quantity, String address) throws OrderException;
	/**
	 * ����Ʊȯ�������г����붩��
	 * @param goods
	 * @param member
	 * @param mobile
	 * @param quantity
	 * @param address
	 * @return
	 * @throws OrderException
	 */
	GoodsOrder addCardDelayOrder(Goods goods, Member member, String mobile,ElecCard card) throws OrderException;
	GoodsOrder addGoodsOrder(Goods goods, Member member, String mobile, int quantity, String address, ApiUser apiuser) throws OrderException;
	ErrorCode<GoodsOrder> addSportGoodsOrder(SportGoods goods, Member member, String mobile, int quantity, String address);
	ErrorCode<GoodsOrder> addActivityGoodsOrder(ActivityGoods goods, Member member, ApiUser partner, String mobile, int quantity, String realname, String address, Timestamp jointime);
	ErrorCode<GoodsOrder> addTicketGoodsOrder(TicketGoods goods, Member member, String mobile, Integer quantity, Long disid, Long priceid, ApiUser partner, String ukey);
	ErrorCode<GoodsOrder> addTicketGoodsOrder(List<GoodsCommand> commandList, Member member, String mobile, ApiUser partner, String ukey);
	//�˶�������Ӷ���
	ErrorCode<GoodsOrder> addTrainingGoodsOrder(Long goodsId, Long gspId, Integer quantity, String mobile, String infoList,  Member member, ApiUser partner, String ukey);
	/**
	 * ������Ʒ������״̬��ѯ�����ܵĹ�������
	 * @param goodsId
	 * @param status
	 * @return
	 */
	Integer getGoodsOrderQuantity(Long goodsId, String status);
	/**
	 * ȡ��δ����Ķ���
	 * @param mpid
	 * @param id
	 * @return
	 */
	boolean cancelUnpaidOrderList(List<GoodsOrder> orderList);
	ErrorCode cancelGoodsOrder(String tradeNo, Member member);
	ErrorCode cancelGoodsOrder(GoodsOrder order, Member member);
	GoodsOrder getLastPaidFailureOrder(Long memberid, String ukey);
	/**
	 * ȷ��GoodsOrder
	 * @param order
	 */
	ErrorCode<String> processGoodsOrder(GoodsOrder order);
	ErrorCode usePoint(Long orderId, Long memberId, int usePoint);
	
	/**
	 * ��ѯ����
	 * @param relatedid
	 * @param status
	 * @param tradeNo
	 * @param mobile
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	List<GoodsOrder> getGoodsOrderList(Long relatedid, String status, String tradeNo, String mobile, Timestamp starttime, Timestamp endtime);
	List<GoodsOrder> getGoodsOrderList(Long relatedid, Long placeid, String status, String tradeNo, String mobile, Timestamp starttime, Timestamp endtime);
	/**
	 * �����û�id�����ֻ��Ų�ѯ�ɹ�����������
	 * @param memberid
	 * @param mobile
	 * @return
	 */
	Integer getGewaorderCountByMobile(Long memberid, String mobile, String ordertype);
	List<GoodsOrder> getGoodsOrderList(Long goodsId, Long memberId,
			String status, boolean like, boolean order, int maxnum);
	GoodsGift getBindGoodsGift(OpenPlayItem opi, Long partnerid);
	List<GoodsGift> getBindGoodsGift(Long cinemaid, Long partnerid);
	GoodsGift getBindGoodsGift(List<GoodsGift> goodsGiftList, OpenPlayItem opi, Long partnerid);
	boolean isValidGoodsGift(OpenPlayItem opi, GoodsGift gift, Long partnerid);
	/**
	 * //�����������
	 * @param goods
	 * @return
	 */
	boolean isOverQuantity(BaseGoods goods);
	ErrorCode<OrderContainer> useSpecialDiscount(Long orderId, SpecialDiscount sd, OrderCallback callback);
	ErrorCode<GoodsOrderContainer> useElecCard(Long orderId, ElecCard card, Long memberid);
	Map<String, String> getOtherInfoMap(List<BaseGoods> goodsList);
	/**
	 * ϵͳ��Ӷ���(�û��������ײ�)
	 * @param torder
	 * @return
	 */
	ErrorCode<GoodsOrder> addGoodsOrderByBuyItem(TicketOrder torder);
	
	OrderContainer processOrderPay(GewaOrder order) throws OrderException;
	/**
	 * ��ѯ����
	 * @param order
	 * @return
	 */
	ErrorCode<BaseGoods> checkOrder(GoodsOrder order);
	String getExpressid(GoodsOrder order);
	/**
	 * ��ȡһ���������Ҫ�Ķ���
	 * @param order
	 * @param orderNote
	 * @param goods
	 * @param buyItemList
	 * @return
	 */
	Map<String, String> getDownOrderMap(GewaOrder order, OrderNote orderNote, BaseGoods goods, List<BuyItem> buyItemList);
	<T extends BaseGoods> List<GoodsOrder> getGoodsOrderList(Class<T> clazz, SearchOrderCommand soc);
	
	void updateGoodsprice(GoodsOrder order);
}
