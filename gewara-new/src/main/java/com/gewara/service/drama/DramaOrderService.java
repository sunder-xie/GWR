/**
 * 
 */
package com.gewara.service.drama;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.ui.ModelMap;

import com.gewara.command.SearchOrderCommand;
import com.gewara.command.TheatrePriceCommand;
import com.gewara.helper.discount.SportSpecialDiscountHelper.OrderCallback;
import com.gewara.helper.order.OrderContainer;
import com.gewara.model.acl.User;
import com.gewara.model.api.ApiUser;
import com.gewara.model.drama.DisQuantity;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.drama.OpenTheatreSeat;
import com.gewara.model.drama.SellDramaSeat;
import com.gewara.model.drama.TheatreSeatArea;
import com.gewara.model.drama.TheatreSeatPrice;
import com.gewara.model.pay.BuyItem;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.user.Member;
import com.gewara.service.OrderException;
import com.gewara.service.order.GewaOrderService;
import com.gewara.support.ErrorCode;

public interface DramaOrderService extends GewaOrderService {
	/**
	 * ���Ӷ���
	 * @param member
	 * @param item
	 * @param seatidList
	 * @param mobile
	 * @param randomNum
	 * @return
	 * @throws OrderException
	 */
	ErrorCode<DramaOrder> addDramaOrder(OpenDramaItem odi, TheatreSeatArea seatArea, List<Long> seatidList, Long disid, String mobile, Member member, String ukey, List<String> remoteLockList) throws OrderException;
	ErrorCode<DramaOrder> addDramaOrder(OpenDramaItem odi, TheatreSeatArea seatArea, String seatLabel, Long disid, String mobile, Member member, ApiUser partner, String ukey, List<String> remoteLockList) throws OrderException;
	ErrorCode<DramaOrder> addDramaOrder(OpenDramaItem odi, Member member, String mobile, Integer quantity, Long disid, Long priceid, ApiUser partner, String ukey);
	ErrorCode<DramaOrder> addDramaOrder(List<TheatrePriceCommand> commandList, Member member, String mobile, ApiUser partner, String ukey);
	
	Map<String, String> getOtherInfoMap(List<OpenDramaItem> itemList);
	
	DramaOrder getLastUnpaidDramaOrder(Long memberid, String ukey, Long itemid);
	ErrorCode checkOrderSeat(DramaOrder order, ModelMap model);
	OrderContainer processOrderPay(DramaOrder order) throws OrderException;
	ErrorCode processDramaOrder(DramaOrder order, OpenDramaItem odi);
	List<GewaOrder> getAllDramaOrderList(SearchOrderCommand soc);
	List<DramaOrder> getDramaOrderList(SearchOrderCommand soc);
	List<SellDramaSeat> getSellDramaSeatList(Long dpid, Long areaid);
	/**
	 * ��֤��λ
	 * @param seatList
	 * @return
	 */
	ErrorCode validateSeatLock(List<OpenTheatreSeat> seatList, Map<Long, SellDramaSeat> sellSeatMap, List<String> remoteLockList);
	
	ErrorCode validLoveSeat(List<OpenTheatreSeat> seatList);
	
	List<SellDramaSeat> getDramaOrderSeatList(Long orderId);
	/**
	 * @param orderId
	 * @param card
	 * @param memberid
	 * @return
	 */
	ErrorCode<DramaOrder> useElecCard(DramaOrder order, ElecCard card, Long memberid);
	/**
	 * ����theatreid��ѯ��Ʊ�û�
	 * @param theatreid
	 * @param from, maxnum
	 * @return
	 */
	List<DramaOrder> getDramaOrderByTheatreid(Long theatreid, int from, int maxnum);
	/**
	 * @param jsonValue 
	 * @param jsonKey 
	 * @param order
	 * @param sd
	 * @return
	 */
	ErrorCode<OrderContainer> useSpecialDiscount(Long orderid, SpecialDiscount sd, OrderCallback callback);
	/**
	 * ʹ�û����Ż�
	 * @param orderId
	 * @param memberId
	 * @param usePoint
	 * @return
	 */
	ErrorCode usePoint(Long orderId, Long memberId, int usePoint);
	/**
	 * ����λ���õ��µ���λ
	 * @param order
	 * @param seatList
	 * @param newseat
	 * @param validprice
	 * @return
	 * @throws OrderException
	 */
	List<OpenTheatreSeat> getNewSeatList(DramaOrder order,
			List<SellDramaSeat> seatList, String newseat, boolean validprice)
			throws OrderException;
	
	ErrorCode<DramaOrder> processLastOrder(Long memberid, String ukey);
	List<OpenDramaItem> getOpenDramaItemList(OpenDramaItem odi, List<BuyItem> buyList);
	void cancelDramaOrder(DramaOrder order, String ukey, String reason);
	void cancelDramaOrder(String tradeNo, String ukey, String reason);
	
	void getPriceObjectList(DramaOrder dorder, OpenDramaItem odi, Map<OpenDramaItem, Integer> odiMap, Map<TheatreSeatPrice, Integer> priceQuantityMap, Map<Long, Map<DisQuantity, Integer>> priceDisMap);
	
	/**
	 * ��ȡ����ȡƱ��ʽ
	 * @param odi
	 * @param cur
	 * @return
	 */
	String getTakemethodByOdi(DramaOrder order, OpenDramaItem odi);
	String getTakemethodByOdi(Timestamp addtime, OpenDramaItem odi);
	
	/**
	 * ���¶�����չ��ݵ���Ϣ
	 * @param orderId
	 * @param expressId
	 * @param expressStatus
	 * @param memberId
	 */
	ErrorCode updateOrderExpress(String tradeNo, String expressNo, String expressStatus, User user, String dealType);
	
	/**
	 * ������ݵ�������״̬
	 * @param expressId
	 * @param expressStatus
	 * @param memberId
	 */
	ErrorCode<List<GewaOrder>> checkAndUpdateExpress(String expressNo, User user, String dealType);
}
