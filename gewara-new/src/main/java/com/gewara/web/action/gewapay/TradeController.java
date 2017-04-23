package com.gewara.web.action.gewapay;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.Config;
import com.gewara.bank.BankConstant;
import com.gewara.constant.AdminCityContant;
import com.gewara.constant.ChargeConstant;
import com.gewara.constant.GoodsConstant;
import com.gewara.constant.MemberCardConstant;
import com.gewara.constant.PayConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.Status;
import com.gewara.constant.TagConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OpiConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.helper.discount.DramaSpecialDiscountHelper;
import com.gewara.helper.discount.GoodsSpecialDiscountHelper;
import com.gewara.helper.discount.GymSpecialDiscountHelper;
import com.gewara.helper.discount.MovieSpecialDiscountHelper;
import com.gewara.helper.discount.SpecialDiscountHelper;
import com.gewara.helper.discount.SportSpecialDiscountHelper;
import com.gewara.helper.order.ElecCardContainer;
import com.gewara.helper.order.GewaOrderHelper;
import com.gewara.helper.order.OrderOther;
import com.gewara.model.agency.Agency;
import com.gewara.model.agency.TrainingGoods;
import com.gewara.model.bbs.Diary;
import com.gewara.model.bbs.DiaryBase;
import com.gewara.model.bbs.DiaryHist;
import com.gewara.model.drama.Drama;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.drama.SellDramaSeat;
import com.gewara.model.drama.Theatre;
import com.gewara.model.drama.TheatreField;
import com.gewara.model.drama.TheatreProfile;
import com.gewara.model.goods.ActivityGoods;
import com.gewara.model.goods.BaseGoods;
import com.gewara.model.goods.Goods;
import com.gewara.model.goods.GoodsDisQuantity;
import com.gewara.model.goods.GoodsGift;
import com.gewara.model.goods.GoodsPrice;
import com.gewara.model.goods.SportGoods;
import com.gewara.model.goods.TicketGoods;
import com.gewara.model.movie.Cinema;
import com.gewara.model.movie.CinemaProfile;
import com.gewara.model.movie.CinemaRoom;
import com.gewara.model.movie.Movie;
import com.gewara.model.pay.BuyItem;
import com.gewara.model.pay.Charge;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.GymOrder;
import com.gewara.model.pay.MemberAccount;
import com.gewara.model.pay.MemberCardOrder;
import com.gewara.model.pay.OrderAddress;
import com.gewara.model.pay.PayBank;
import com.gewara.model.pay.PubSale;
import com.gewara.model.pay.PubSaleOrder;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.sport.MemberCardInfo;
import com.gewara.model.sport.MemberCardType;
import com.gewara.model.sport.OpenTimeItem;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.sport.SellTimeTable;
import com.gewara.model.sport.Sport;
import com.gewara.model.sport.SportItem;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.SellSeat;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.model.user.MemberUsefulAddress;
import com.gewara.pay.PayUtil;
import com.gewara.pay.PayValidHelper;
import com.gewara.service.OpenGymService;
import com.gewara.service.PlaceService;
import com.gewara.service.drama.DramaOrderService;
import com.gewara.service.gewapay.ElecCardService;
import com.gewara.service.order.GoodsOrderService;
import com.gewara.service.order.GoodsService;
import com.gewara.service.order.PubSaleService;
import com.gewara.service.sport.MemberCardService;
import com.gewara.service.sport.SportOrderService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.activity.SynchActivityService;
import com.gewara.untrans.drama.TheatreOrderService;
import com.gewara.untrans.gym.SynchGymService;
import com.gewara.untrans.order.impl.SpdiscountService;
import com.gewara.untrans.sport.SportUntransService;
import com.gewara.util.BeanUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.xmlbind.activity.RemoteActivity;
import com.gewara.xmlbind.gym.CardItem;
import com.gewara.xmlbind.gym.RemoteGym;
import com.gewara.xmlbind.gym.RemoteSpecialCourse;

@Controller
public class TradeController extends BasePayController {
	@Autowired@Qualifier("goodsOrderService")
	private GoodsOrderService goodsOrderService;
	@Autowired@Qualifier("sportOrderService")
	private SportOrderService sportOrderService;
	@Autowired@Qualifier("elecCardService")
	private ElecCardService elecCardService;
	@Autowired@Qualifier("goodsService")
	private GoodsService goodsService;
	@Autowired@Qualifier("theatreOrderService")
	private TheatreOrderService theatreOrderService;
	@Autowired@Qualifier("pubSaleService")
	private PubSaleService pubSaleService;
	@Autowired@Qualifier("spdiscountService")
	private SpdiscountService spdiscountService;
	@Autowired@Qualifier("openGymService")
	private OpenGymService openGymService;
	@Autowired@Qualifier("synchActivityService")
	private SynchActivityService synchActivityService;
	@Autowired@Qualifier("dramaOrderService")
	private DramaOrderService dramaOrderService;
	@Autowired@Qualifier("synchGymService")
	private SynchGymService synchGymService;
	@Autowired@Qualifier("memberCardService")
	private MemberCardService memberCardService;
	@Autowired@Qualifier("placeService")
	private PlaceService placeService;
	@Autowired@Qualifier("sportUntransService")
	private SportUntransService sportUntransService;
	
	private static final Map<String, String> CancelOrderURLMap = new HashMap<String, String>();
	static{
		CancelOrderURLMap.put("normal", "/home/myOrderManage.xhtml");
		CancelOrderURLMap.put("userindex", "/home/sns/personIndex.xhtml");
	}
	
	@RequestMapping("/gewapay/order.xhtml")
	public String modifyOrder(@RequestParam("orderId")long orderId, ModelMap model) {
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) return show404(model, "�ö��������ڻ�ɾ����");
		Member member = getLogonMember();
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "�����޸���֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("logonMember", member);
		model.put("order", order);
		MemberInfo info = daoService.getObject(MemberInfo.class, member.getId());
		Integer point = info.getPointvalue();
		model.put("memberpoint", point);
		//TODO:�Ż���ֻ����Ҫ�Ų�ѯ
		OrderAddress orderAddress = daoService.getObject(OrderAddress.class, order.getTradeNo());
		model.put("orderAddress", orderAddress);
		if(order instanceof TicketOrder){
			if(((TicketOrder) order).hasUnlock())  {
				return show404(model, "������λδ�������޷�֧����");
			}
			putTicketOrderData((TicketOrder)order, info, model);
			return "gewapay/ticket/wide_showOrder.vm";
		}else if(order instanceof GoodsOrder){
			return putGoodsOrderData((GoodsOrder)order, model);
		}else if(order instanceof SportOrder) {
			putSportOrderData((SportOrder) order, model);
			return "gewapay/sport/wide_showOrder.vm";
		}else if(order instanceof DramaOrder){
			DramaOrder dramaOrder = (DramaOrder)order;
			OpenDramaItem item = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", dramaOrder.getDpid(), false);
			putDramaOrderData(dramaOrder, item, model);
			return "gewapay/drama/wide_showOrder.vm";
		}else if(order instanceof PubSaleOrder){
			PubSaleOrder porder = (PubSaleOrder)order;
			PubSale sale = daoService.getObject(PubSale.class, porder.getPubid());
			model.put("sale", sale);
			return "exchange/pubsale/pubsaleOrder.vm";
		}else if(order instanceof GymOrder){
			GymOrder gymOrder = (GymOrder)order;
			putRemoteGymOrderData(gymOrder, model);
			return "gewapay/gym/showGymOrder.vm";
		}else if(order instanceof MemberCardOrder){
			MemberCardOrder memberCardOrder = (MemberCardOrder)order;
			putMemberCardOrderData(memberCardOrder, model);
			return "gewapay/sport/vip/wide_showOrder.vm";
		}else{
			//�����ܷ�������
			return showError(model, "�������ʹ��󣡣�");
		}
	}

	//��ӰƱ��Ϣ
	private void putTicketOrderData(TicketOrder order, MemberInfo memberInfo, ModelMap model){
		OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", order.getMpid(), true);
		model.put("movie", daoService.getObject(Movie.class,opi.getMovieid()));
		model.put("cinema", daoService.getObject(Cinema.class,opi.getCinemaid()));
		model.put("opi", opi);
		model.put("room", daoService.getObject(CinemaRoom.class, opi.getRoomid()));
		model.put("GewaOrderHelper", new GewaOrderHelper());
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		CinemaProfile cp = daoService.getObject(CinemaProfile.class, opi.getCinemaid());
		if(cp.getTopicid()!=null) {
			model.put("takeTopicId", cp.getTopicid());
		}
		if (opi.isOpenCardPay()) {
			//TODO:�Ż����첽�����ҵ�ȯ
			ElecCardContainer container = elecCardService.getAvailableCardList(order, discountList, opi, memberInfo.getId());
			model.put("cardList", container.getAvaliableList());
		}
		model.putAll(getGoodsGiftData(order, opi));
		model.put("profile", cp);
		Map<String, String> otherInfo = VmUtils.readJsonToMap(opi.getOtherinfo());
		model.put("opiOtherinfo", otherInfo);
		if(StringUtils.equals(otherInfo.get("address"), Status.Y)){
			List<MemberUsefulAddress> addressList = memberService.getMemberUsefulAddressByMeberid(order.getMemberid(), 0, 2);
			if(!addressList.isEmpty()){
				model.put("usefulAddress", addressList.get(0));
			}
			model.put("addressList", addressList);
		}
		if(cp.isBuyItem(opi.getPlaytime())){
			if(order.getItemfee() > 0) {
				Map<Long, Goods> goodsMap = new HashMap();
				List<BuyItem> itemList = daoService.getObjectListByField(BuyItem.class, "orderid", order.getId());
				for(BuyItem item : itemList){
					GoodsGift gift = daoService.getObjectByUkey(GoodsGift.class, "goodsid", item.getRelatedid(), true);
					if(gift==null) goodsMap.put(item.getId(), daoService.getObject(Goods.class, item.getRelatedid()));
				}
				model.put("goodsMap", goodsMap);
				model.put("itemList", itemList);
			}
			String mealoption = otherInfo.get(OpiConstant.MEALOPTION);
			if(!StringUtils.equals(mealoption, "notuse")){
				List<Goods> goodsList = goodsService.getGoodsList(Goods.class, GoodsConstant.GOODS_TAG_BMH, order.getCinemaid(), true, true, true, "goodssort", true, false);
				model.put("goodsList", goodsList);
			}
		}
		Map<String, String> orderOtherinfo = VmUtils.readJsonToMap(order.getOtherinfo());
		model.put("orderOtherinfo", orderOtherinfo);
		List<String> limitPayList = paymentService.getLimitPayList();
		PayValidHelper valHelp = new PayValidHelper(otherInfo);
		valHelp.setLimitPay(limitPayList);
		List<SellSeat> seatList = ticketOrderService.getOrderSeatList(order.getId());
		SpecialDiscountHelper sdh = new MovieSpecialDiscountHelper(opi, order, seatList, discountList);
		boolean openSpdiscount = StringUtils.contains(opi.getElecard(), PayConstant.CARDTYPE_PARTNER);
		Map discountData = spdiscountService.getSpecialDiscountData(sdh, valHelp, order, openSpdiscount, opi.getSpflag(), 
				discountList, SpecialDiscount.OPENTYPE_GEWA, PayConstant.APPLY_TAG_MOVIE);
		
		model.putAll(discountData);
	}
	//��Ʒ��Ϣ
	private String putGoodsOrderData(GoodsOrder order, ModelMap model){
		BaseGoods baseGoods = daoService.getObject(BaseGoods.class, order.getGoodsid());
		String view = "";
		List<BaseGoods> goodsList = new ArrayList<BaseGoods>();
		List<BuyItem> itemList = new ArrayList<BuyItem>();
		Map<String, String> otherInfoMap = JsonUtils.readJsonToMap(baseGoods.getOtherinfo());
		if(StringUtils.isNotBlank(baseGoods.getExpressid()) || StringUtils.equals(baseGoods.getDeliver(), "address")){
			List<MemberUsefulAddress> addressList = memberService.getMemberUsefulAddressByMeberid(order.getMemberid(), 0, 2);
			if(!addressList.isEmpty()){
				model.put("usefulAddress", addressList.get(0));
			}
			model.put("addressList", addressList);
		}
		String applay_tag  = PayConstant.APPLY_TAG_GOODS;
		if(baseGoods instanceof Goods || baseGoods instanceof ActivityGoods){
			setGoodsOrderInfo(baseGoods, model);
			view = "gewapay/goods/goodsOrder.vm";
			if(baseGoods instanceof Goods && baseGoods.isPointType()) {
				view = "gewapay/goods/pointOrder.vm";
			}else if(baseGoods instanceof ActivityGoods){
				ErrorCode<RemoteActivity> code = synchActivityService.getRemoteActivity(baseGoods.getRelatedid());
				if(code.isSuccess()) model.put("activity", code.getRetval());
				view = "gewapay/goods/activityShowOrder.vm";
			}
			goodsList.add(baseGoods);
		}else if(baseGoods instanceof SportGoods){
			SportGoods sportGoods = (SportGoods)baseGoods;
			model.put("goods", sportGoods);
			Sport sport = daoService.getObject(Sport.class, sportGoods.getRelatedid());
			model.put("sport", sport);
			SportItem sportItem = daoService.getObject(SportItem.class, sportGoods.getItemid());
			model.put("item", sportItem);
			model.put("goodstype", "sportgoods");
			view = "gewapay/sport/goodsOrder.vm";
			goodsList.add(baseGoods);
		}else if(baseGoods instanceof TicketGoods){
			TicketGoods ticketGoods = (TicketGoods) baseGoods;
			model.put("goods", ticketGoods);
			Object relate = relateService.getRelatedObject(ticketGoods.getTag(), ticketGoods.getRelatedid());
			model.put("relate", relate);
			Object category = relateService.getRelatedObject(ticketGoods.getCategory(), ticketGoods.getCategoryid());
			model.put("category", category);
			view = "gewapay/goods/ticketGoodsShowOrder.vm";
			itemList = daoService.getObjectListByField(BuyItem.class, "orderid", order.getId());
			model.put("itemList", itemList);
			Map<Long, BaseGoods> goodsMap = new HashMap<Long, BaseGoods>();
			Map<Long, GoodsPrice> priceMap = new HashMap<Long, GoodsPrice>();
			Map<Long, GoodsDisQuantity> disMap = new HashMap<Long, GoodsDisQuantity>();
			model.put("goodsMap", goodsMap);
			model.put("priceMap", priceMap);
			model.put("disMap", disMap);
			boolean openCardPay = false,openPointPay = false,isExpress = false;
			int maxpoint = 0, minpoint = 0;
			for (BuyItem item : itemList) {
				BaseGoods goods = goodsMap.get(item.getRelatedid());
				if(goods == null){
					goods = daoService.getObject(BaseGoods.class, item.getRelatedid());
					if(StringUtils.isNotBlank(goods.getExpressid())){
						isExpress = true;
					}
					if(goods.isOpenCardPay()) openCardPay = true;
					if(goods.isOpenPointPay()){
						openPointPay = true;
						maxpoint = Math.max(maxpoint, goods.getMaxpoint());
						minpoint = Math.max(minpoint, goods.getMinpoint());
					}
					goodsMap.put(item.getRelatedid(), goods);
					goodsList.add(goods);
					otherInfoMap.putAll(JsonUtils.readJsonToMap(goods.getOtherinfo()));
				}
				GoodsPrice price = priceMap.get(item.getSmallitemid());
				if(price == null){
					price = daoService.getObject(GoodsPrice.class, item.getSmallitemid());
					priceMap.put(item.getSmallitemid(), price);
				}
				if(item.getDisid() != null){
					GoodsDisQuantity dis = disMap.get(item.getDisid());
					if(dis == null){
						dis = daoService.getObject(GoodsDisQuantity.class, item.getDisid());
						disMap.put(item.getDisid(), dis);
					}
				}
			}
			model.put("openCardPay", openCardPay);
			model.put("openPointPay", openPointPay);
			model.put("isExpress", isExpress);
			model.put("maxpoint", maxpoint);
			model.put("minpoint", minpoint);
			applay_tag = ticketGoods.getCategory();
		}else if(baseGoods instanceof TrainingGoods){
			TrainingGoods goods = (TrainingGoods) baseGoods;
			Agency agency = daoService.getObject(Agency.class, goods.getRelatedid());
			boolean openCardPay = false,openPointPay = false;
			int maxpoint = 0, minpoint = 0;
			if(goods.isOpenCardPay()) openCardPay = true;
			if(goods.isOpenPointPay()){
				openPointPay = true;
				maxpoint = Math.max(maxpoint, goods.getMaxpoint());
				minpoint = Math.max(minpoint, goods.getMinpoint());
			}
			model.put("openPointPay", openPointPay);
			model.put("openCardPay", openCardPay);
			model.put("maxpoint", maxpoint);
			model.put("minpoint", minpoint);
			model.put("agency", agency);
			model.put("goods", goods);
			view = "gewapay/sport/agency/wide_showOrder.vm";
			//�����˶�
			applay_tag = TagConstant.TAG_SPORT;
		}
		String spflags = baseGoods.getSpflag();
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		SpecialDiscountHelper sdh = new GoodsSpecialDiscountHelper(order, goodsList, itemList);
		PayValidHelper valHelp = new PayValidHelper(otherInfoMap);
		Map discountData = spdiscountService.getSpecialDiscountData(sdh, valHelp, order, true, spflags, 
				discountList, SpecialDiscount.OPENTYPE_GEWA, applay_tag);
		model.putAll(discountData);
		return view;
	}
	private void putDramaOrderData(DramaOrder order, OpenDramaItem item, ModelMap model) {
		Member member = daoService.getObject(Member.class, order.getMemberid());
		Theatre theatre = daoService.getObject(Theatre.class, order.getTheatreid());
		TheatreProfile profile = daoService.getObject(TheatreProfile.class, theatre.getId());
		Drama drama = daoService.getObject(Drama.class, order.getDramaid());
		if(profile!=null && profile.getTopicid()!=null){
			DiaryBase diary = daoService.getObject(Diary.class, profile.getTopicid());
			if(diary == null) diary = daoService.getObject(DiaryHist.class, profile.getTopicid());
			model.put("topic", diary);
		}
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		if (item.isOpenCardPay()) {
			ElecCardContainer container = elecCardService.getAvailableCardList(order, discountList, item, member.getId());
			model.put("cardList", container.getAvaliableList());
		}
		TheatreField section = daoService.getObject(TheatreField.class, item.getRoomid());
		model.put("item", item);
		model.put("drama", drama);
		model.put("section", section);
		model.put("theatre", theatre);
		model.put("profile", profile);
		List<MemberUsefulAddress> addressList = memberService.getMemberUsefulAddressByMeberid(order.getMemberid(), 0, 2);
		if(!addressList.isEmpty()){
			model.put("usefulAddress", addressList.get(0));
		}
		model.put("addressList", addressList);
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		model.put("memberInfo", memberInfo);
		Map<String, String> orderOtherinfo = VmUtils.readJsonToMap(order.getOtherinfo());
		model.put("orderOtherinfo", orderOtherinfo);
		List<SellDramaSeat> seatList = null;
		if(item.isOpenseat()) seatList = dramaOrderService.getDramaOrderSeatList(order.getId());
		List<String> limitPayList = paymentService.getLimitPayList();
		List<BuyItem> buyList = daoService.getObjectListByField(BuyItem.class, "orderid", order.getId());
		List<OpenDramaItem> itemList = dramaOrderService.getOpenDramaItemList(item, buyList);
		model.put("buyList", buyList);
		Map<Long, OpenDramaItem> odiMap = BeanUtil.beanListToMap(itemList, "dpid");
		model.put("odiMap", odiMap);
		OrderOther orderOther = theatreOrderService.getDramaOrderOtherData(order, buyList, odiMap, model);
		Map<String, String> otherInfo = dramaOrderService.getOtherInfoMap(itemList);
		PayValidHelper valHelp = new PayValidHelper(otherInfo);
		valHelp.setLimitPay(limitPayList);
		SpecialDiscountHelper sdh = new DramaSpecialDiscountHelper(order, itemList, buyList, discountList, seatList);
		boolean openSpdiscount = StringUtils.contains(orderOther.getElecard(), PayConstant.CARDTYPE_PARTNER);
		Map discountData = spdiscountService.getSpecialDiscountData(sdh, valHelp, order, openSpdiscount, item.getSpflag(), 
				discountList, SpecialDiscount.OPENTYPE_GEWA, PayConstant.APPLY_TAG_DRAMA);
		model.putAll(discountData);
	}
	//�˶���Ϣ
	private void putSportOrderData(SportOrder order, ModelMap model){
		OpenTimeTable ott = daoService.getObject(OpenTimeTable.class,  order.getOttid());
		Sport sport = daoService.getObject(Sport.class, order.getSportid());
		SportItem item = daoService.getObject(SportItem.class, order.getItemid());
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		if (ott.isOpenCardPay()) {
			ElecCardContainer container = elecCardService.getAvailableCardList(order, discountList, ott, order.getMemberid());
			model.put("cardList", container.getAvaliableList());
		}
		model.put("sport", sport);
		model.put("item", item);
		model.put("ott", ott);
		List<OpenTimeItem> otiList = sportOrderService.getMyOtiList(order.getId());
		SpecialDiscountHelper sdh = new SportSpecialDiscountHelper(order, ott, discountList, otiList);
		boolean openSpdiscount = StringUtils.contains(ott.getElecard(), PayConstant.CARDTYPE_PARTNER);
		Map<String, String> otherInfo = VmUtils.readJsonToMap(ott.getOtherinfo());
		model.put("opiOtherinfo", otherInfo);
		List<String> limitPayList = paymentService.getLimitPayList();
		PayValidHelper valHelp = new PayValidHelper(otherInfo);
		valHelp.setLimitPay(limitPayList);
		Map discountData = spdiscountService.getSpecialDiscountData(sdh, valHelp, order, openSpdiscount, ott.getSpflag(), 
				discountList, SpecialDiscount.OPENTYPE_GEWA, PayConstant.APPLY_TAG_SPORT);
		
		model.putAll(discountData);
		Map<String, String> orderOtherinfo = VmUtils.readJsonToMap(order.getOtherinfo());
		model.put("orderOtherinfo", orderOtherinfo);
		model.put("subwaylineMap", placeService.getSubwaylineMap(sport.getCitycode()));
		if(order.getCardid()!=null){
			MemberCardInfo memberCard = daoService.getObject(MemberCardInfo.class, order.getCardid());
			MemberCardType mct = daoService.getObject(MemberCardType.class, memberCard.getTypeid());
			Integer cardDue = order.getDue();
			if(mct.hasAmountCard() && mct.getDiscount()!=null){
				cardDue = Math.round(cardDue*mct.getDiscount()/100f);
			}
			model.put("memberCard", memberCard);
			model.put("mct", mct);
			model.put("cardDue", cardDue);
		}
	}
	
	private void putRemoteGymOrderData(GymOrder order, ModelMap model){
		String speciallist = JsonUtils.getJsonValueByKey(order.getOtherinfo(), GymOrder.KEY_SPECAILID);
		ErrorCode<CardItem> code = synchGymService.showBuyCardItem(order.getGci(), speciallist, true);
		if(!code.isSuccess()){
			dbLogger.warn(code.getMsg());
			return;
		}
		CardItem gymCardItem = code.getRetval();
		model.put("gymCardItem", gymCardItem);
		RemoteGym gym = gymCardItem.getGym();
		model.put("gym", gym);
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		if (gymCardItem.isOpenCardPay()) {
			ElecCardContainer container = elecCardService.getAvailableCardList(order, discountList, gymCardItem, order.getMemberid());
			model.put("cardList", container.getAvaliableList());
		}
		List<RemoteSpecialCourse> specialList = gymCardItem.getSpecialCourseList();
		model.put("specialList", specialList);
		List<MemberUsefulAddress> addressList = memberService.getMemberUsefulAddressByMeberid(order.getMemberid(), 0, 2);
		if(!addressList.isEmpty()){
			model.put("usefulAddress", addressList.get(0));
		}
		SpecialDiscountHelper sdh = new GymSpecialDiscountHelper(order, gymCardItem, discountList);
		boolean openSpdiscount = StringUtils.contains(gymCardItem.getElecard(), PayConstant.CARDTYPE_PARTNER);
		Map<String, String> otherInfo = VmUtils.readJsonToMap(gymCardItem.getOtherinfo());
		model.put("opiOtherinfo", otherInfo);
		List<String> limitPayList = paymentService.getLimitPayList();
		PayValidHelper valHelp = new PayValidHelper(otherInfo);
		valHelp.setLimitPay(limitPayList);
		Map discountData = spdiscountService.getSpecialDiscountData(sdh, valHelp, order, openSpdiscount, gymCardItem.getSpflag(), 
				discountList, SpecialDiscount.OPENTYPE_GEWA, PayConstant.APPLY_TAG_GYM);
		model.putAll(discountData);
		Map<String, String> orderOtherinfo = VmUtils.readJsonToMap(order.getOtherinfo());
		model.put("orderOtherinfo", orderOtherinfo);
		model.put("addressList", addressList);
	}
	private void putMemberCardOrderData(MemberCardOrder order, ModelMap model){
		MemberCardType mct = daoService.getObject(MemberCardType.class, order.getMctid());
		Sport sport = daoService.getObject(Sport.class, order.getPlaceid());
		model.put("sport", sport);
		model.put("fitItem", memberCardService.getFitItem(mct.getFitItem()));
		model.put("order", order);
		model.put("mct", mct);
		Map<String, String> subwaylineMap = placeService.getSubwaylineMap(sport.getCitycode());
		model.put("subwaylineMap", subwaylineMap);
	}
	@RequestMapping("/gewapay/confirmOrder.xhtml")
	public String confirmOrder(@RequestParam("orderId")long orderId, ModelMap model) {
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		ErrorCode code = addConfirmOrderData(order, model);
		if(!code.isSuccess()) {
			return alertMessage(model, code.getMsg(), "gewapay/order.xhtml?orderId=" + orderId);
		}
		model.put("creditMap", BankConstant.getAlipayKjCreditMap());
		List<PayBank> confPayList = paymentService.getPayBankList(PayBank.TYPE_PC);
		model.put("confPayList", confPayList);
		if(order instanceof DramaOrder){
			return "gewapay/drama/wide_confirmOrder.vm";
		}else if(order instanceof SportOrder) {
			return "gewapay/sport/wide_confirmOrder.vm";
		}else if(order instanceof TicketOrder) {
			return "gewapay/ticket/wide_confirmOrder.vm";
		}else if(order instanceof GymOrder){
				return "gewapay/gym/confirmGymOrder.vm";
		}else if(order instanceof PubSaleOrder){
			return "gewapay/pubsaleConfirm.vm";
		}else if(order instanceof GoodsOrder){
			GoodsOrder goodsorder = (GoodsOrder)order;
			BaseGoods goods = daoService.getObject(BaseGoods.class, goodsorder.getGoodsid());
			model.put("goods", goods);
			if(goods instanceof SportGoods){
				SportGoods sportGoods = (SportGoods)goods;
				Sport sport = daoService.getObject(Sport.class, sportGoods.getRelatedid());
				SportItem item = daoService.getObject(SportItem.class, sportGoods.getItemid());
				model.put("sport", sport);
				model.put("item", item);
				model.put("order", goodsorder);
				model.put("goods", sportGoods);
				return "gewapay/sport/confirmOrder.vm";
			}else if(goods instanceof ActivityGoods){
				ErrorCode<RemoteActivity> code2 = synchActivityService.getRemoteActivity(goods.getRelatedid());
				if(code2.isSuccess()) model.put("activity", code2.getRetval());
				return "gewapay/goods/activityConfirmOrder.vm";
			}else if(goods instanceof TicketGoods){
				TicketGoods ticketGoods = (TicketGoods) goods;
				Object relate = relateService.getRelatedObject(ticketGoods.getTag(), ticketGoods.getRelatedid());
				model.put("relate", relate);
				Object category = relateService.getRelatedObject(ticketGoods.getCategory(), ticketGoods.getCategoryid());
				model.put("category", category);
				return "gewapay/goods/ticketGoodsConfirmOrder.vm";
			}else if(goods instanceof TrainingGoods){
				TrainingGoods trainingGoods = (TrainingGoods) goods;
				Agency agency = daoService.getObject(Agency.class, trainingGoods.getRelatedid());
				model.put("agency", agency);
				model.put("goods", goods);
				return "gewapay/sport/agency/wide_confirmOrder.vm";
			}
			return "gewapay/goods/confirmOrder.vm";
		}else if(order instanceof MemberCardOrder){
			MemberCardOrder memberCardOrder = (MemberCardOrder)order;
			putMemberCardOrderData(memberCardOrder, model);
			return "gewapay/sport/vip/wide_confirmOrder.vm";
		}else{
			//�����ܷ�������
			return showError(model, "�������ʹ��󣡣�");
		}
	}
	private ErrorCode addConfirmOrderData(GewaOrder order, ModelMap model){
		Member member = getLogonMember();
		if (!order.getMemberid().equals(member.getId())) return ErrorCode.getFailure("�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return ErrorCode.getFailure("�����޸���֧�����ѣ���ʱ��ȡ���Ķ�����");
		MemberAccount account = daoService.getObjectByUkey(MemberAccount.class, "memberid", member.getId(), false);
		if (account != null)	model.put("account", account);
		model.put("order", order);
		int otherfee = ticketOrderService.getUmpayfee(order);
		model.put("otherfee", otherfee);
		model.put("telecomOtherFee", (int)(Math.rint(order.getDue() * 1.4) - order.getDue()));
		PayValidHelper valHelp = new PayValidHelper();
		List<String> limitPayList = paymentService.getLimitPayList();
		if(order instanceof TicketOrder) {
			TicketOrder tOrder = (TicketOrder) order;
			OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", tOrder.getMpid(), true);
			valHelp = new PayValidHelper(VmUtils.readJsonToMap(opi.getOtherinfo()));
			Cinema cinema = daoService.getObject(Cinema.class, opi.getCinemaid());
			CinemaProfile cinemaProfile = daoService.getObject(CinemaProfile.class, opi.getCinemaid());
			Movie movie = daoService.getObject(Movie.class, opi.getMovieid());
			List<BuyItem> itemList = daoService.getObjectListByField(BuyItem.class, "orderid", order.getId());
			model.put("cinema", cinema);
			model.put("cinemaProfile", cinemaProfile);
			model.put("movie", movie);
			model.put("opi", opi);
			model.put("itemList", itemList);
			model.put("GewaOrderHelper", new GewaOrderHelper());
			model.put("room", daoService.getObject(CinemaRoom.class, opi.getRoomid()));
		}else if(order instanceof DramaOrder){
			DramaOrder dOrder = (DramaOrder) order;
			OpenDramaItem item = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", dOrder.getDpid(), true);
			Map<String, String> otherinfoMap = new HashMap<String, String>();
			if(item.isOpenseat()){
				otherinfoMap = VmUtils.readJsonToMap(item.getOtherinfo());
			}else{
				List<BuyItem> buyList = daoService.getObjectListByField(BuyItem.class, "orderid", order.getId());
				List<OpenDramaItem> itemList = dramaOrderService.getOpenDramaItemList(item, buyList);
				Map<Long, OpenDramaItem> odiMap = BeanUtil.beanListToMap(itemList, "dpid");
				model.put("odiMap", odiMap);
				model.put("buyList", buyList);
				theatreOrderService.getDramaOrderOtherData(dOrder, buyList, odiMap, model);
				otherinfoMap = dramaOrderService.getOtherInfoMap(itemList);
			}
			valHelp = new PayValidHelper(otherinfoMap);
			Theatre theatre = daoService.getObject(Theatre.class, item.getTheatreid());
			Drama drama = daoService.getObject(Drama.class, dOrder.getDramaid());
			model.put("drama", drama);
			model.put("theatre", theatre);
			model.put("item", item);
		}else if(order instanceof GoodsOrder){
			GoodsOrder gOrder = (GoodsOrder) order;
			BaseGoods goods = daoService.getObject(BaseGoods.class, gOrder.getGoodsid());
			List<BuyItem> itemList = daoService.getObjectListByField(BuyItem.class, "orderid", order.getId());
			List<Long> idList = BeanUtil.getBeanPropertyList(itemList, "relatedid", true);
			List<BaseGoods> goodsList = daoService.getObjectList(BaseGoods.class, idList);
			if(!goodsList.contains(goods)){
				goodsList.add(goods);
			}
			Map<String, String>	otherInfoMap = goodsOrderService.getOtherInfoMap(goodsList);
			valHelp = new PayValidHelper(otherInfoMap);
		}else if(order instanceof SportOrder){
			SportOrder sorder = (SportOrder) order;
			Sport sport = daoService.getObject(Sport.class, sorder.getSportid());
			OpenTimeTable ott = daoService.getObject(OpenTimeTable.class,  sorder.getOttid());
			SportItem item = daoService.getObject(SportItem.class, sorder.getItemid());
			model.put("sport", sport);
			model.put("item", item);
			model.put("ott", ott);
			if(sorder.hasMemberCardPay()){
				MemberCardInfo memberCard = daoService.getObject(MemberCardInfo.class, sorder.getCardid());
				MemberCardType mct = daoService.getObject(MemberCardType.class, memberCard.getTypeid());
				model.put("memberCard", memberCard);
				model.put("mct", mct);
				Integer cardDue = order.getDue();
				if(mct.hasAmountCard() && mct.getDiscount()!=null){
					cardDue = Math.round(cardDue*mct.getDiscount()/100f);
				}
				model.put("cardDue", cardDue);
			}
			valHelp = new PayValidHelper(VmUtils.readJsonToMap(ott.getOtherinfo()));
		}else if(order instanceof GymOrder){
			GymOrder gymOrder = (GymOrder) order;
			Map<String, String> otherInfoMap = new HashMap<String, String>();
			String speciallist = JsonUtils.getJsonValueByKey(gymOrder.getOtherinfo(), GymOrder.KEY_SPECAILID);
			ErrorCode<CardItem> code = synchGymService.showBuyCardItem(gymOrder.getGci(), speciallist, true);
			if(code.isSuccess()){
				CardItem gymCardItem = code.getRetval();
				model.put("gymCardItem", gymCardItem);
				model.put("specialList", gymCardItem.getSpecialCourseList());
				model.put("gym", gymCardItem.getGym());
				otherInfoMap = JsonUtils.readJsonToMap(gymCardItem.getOtherinfo());
			}
			valHelp = new PayValidHelper(otherInfoMap);
		}else if(order instanceof MemberCardOrder){
			MemberCardOrder memberCardOrder = (MemberCardOrder)order;
			MemberCardType mct = daoService.getObject(MemberCardType.class, memberCardOrder.getMctid());
			Sport sport = daoService.getObject(Sport.class, memberCardOrder.getPlaceid());
			model.put("sport", sport);
			model.put("fitItem", memberCardService.getFitItem(mct.getFitItem()));
			valHelp = new PayValidHelper(VmUtils.readJsonToMap(mct.getOtherinfo()));
		}
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		model.put("discountList", discountList);
		Map<String, String> orderOtherinfo = VmUtils.readJsonToMap(order.getOtherinfo());
		model.put("orderOtherinfo", orderOtherinfo);
		String bindpay = paymentService.getBindPay(discountList, orderOtherinfo, order);
		if(StringUtils.isNotBlank(bindpay)){
			if(StringUtils.equals(order.getPaymethod(), PaymethodConstant.PAYMETHOD_ELECARDPAY)){//����ȯ֧��
				if(StringUtils.isNotBlank(orderOtherinfo.get(PayConstant.KEY_CARDBINDPAY)) && order.getDiscount() >0
						|| StringUtils.isNotBlank(orderOtherinfo.get(PayConstant.KEY_CHANGECOST))){
					if(order.getDue() > 0) {
						//BȯҪ�����ʹ�õ�����Ҫ����λ����һ��
						if(discountList.size() < order.getQuantity()){
							return ErrorCode.getFailure("�˶���ֻ��ʹ��ȯ֧����");
						}
					}
				}
			}else{
				valHelp = new PayValidHelper(bindpay);
				String[] bindpayArr = StringUtils.split(bindpay, ",");
				for(String t : bindpayArr){
					limitPayList.remove(t);
				}
			}
		}
		valHelp.setLimitPay(limitPayList);
		model.put("valHelp", valHelp);
		return ErrorCode.SUCCESS;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/gewapay/saveOrder.xhtml")
	public String saveOrder(@RequestParam("orderId")long orderId, String paymethod, String chargeMethod, String checkGewaPay,
			String paypass, String smspass, ModelMap model, HttpServletRequest request) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return showJsonError(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return showJsonError(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		if(StringUtils.isBlank(paymethod)) return showJsonError(model, "��ѡ��֧����ʽ��");
		String fullPaymethod = paymethod;
		String[] pay = StringUtils.split(paymethod, ":");
		paymethod = pay[0];
		String paybank = pay.length>1?pay[1]:null;
		String payinfo = null;
		try {
			if (order instanceof TicketOrder) {
				TicketOrder torder = (TicketOrder)order;
				OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", torder.getMpid(), true);
				if(order.getDue()>0) {
					payinfo = opi.getOtherinfo();
				}
				Map<String, String> otherinfo = VmUtils.readJsonToMap(opi.getOtherinfo());
				if (otherinfo.get(OpiConstant.ADDRESS) != null) {
					if (StringUtils.equals(otherinfo.get(OpiConstant.ADDRESS), Status.Y)) {
						OrderAddress orderAddress = daoService.getObjectByUkey(OrderAddress.class, "tradeno", order.getTradeNo());
						if (orderAddress == null) {
							return showJsonError(model, "����д��ݵ�ַ��");
						}
					}
				}
				List<SellSeat> seatList = ticketOrderService.getOrderSeatList(order.getId());
				ErrorCode code = ticketOrderService.checkOrderSeat(torder, seatList);
				if (!code.isSuccess()) {
					dbLogger.error("�����д�" + order.getTradeNo() + code.getMsg());
					return showJsonError(model, code.getMsg());
				}
			}else if(order instanceof SportOrder){
				SportOrder sorder = (SportOrder)order;
				OpenTimeTable ott = daoService.getObject(OpenTimeTable.class, sorder.getOttid());
				if(ott.hasPeriod() || ott.hasInning()){
					SellTimeTable stt = daoService.getObject(SellTimeTable.class, sorder.getId());
					OpenTimeItem item = daoService.getObject(OpenTimeItem.class, stt.getOtiid());
					int count = sportOrderService.getSellTimeTableCount(stt.getOtiid(), stt.getStarttime());
					if(sorder.getQuantity()+ count > item.getQuantity())
						return showJsonError(model, "��Ԥ������������");
				}else{
					List<OpenTimeItem> otiList = sportOrderService.getMyOtiList(sorder.getId());
					ErrorCode code = sportOrderService.checkOrderField(sorder, otiList);
					if(!code.isSuccess()){
						dbLogger.error("�˶������д�" + order.getTradeNo() + code.getMsg());
						return showJsonError(model, code.getMsg());
					}
					if(sorder.hasMemberCardPay()){
						if(StringUtils.isBlank(smspass)) {
							return showJsonError(model, "�����������֤��");
						}else {
							String otherinfo = JsonUtils.addJsonKeyValue(order.getOtherinfo(), MemberCardConstant.SMSPASS, smspass);
							order.setOtherinfo(otherinfo);
						}
					}
					
				}
			}else if(order instanceof DramaOrder){
				DramaOrder dorder = (DramaOrder)order;
				ErrorCode code = dramaOrderService.checkOrderSeat(dorder, model);
				if(!code.isSuccess()){
					dbLogger.error("���綩���д�" + order.getTradeNo() + code.getMsg());
					return showJsonError(model, code.getMsg());
				}
			}else if(order instanceof GymOrder){
				GymOrder gymOrder = (GymOrder)order;
				if(StringUtils.isNotBlank(JsonUtils.getJsonValueByKey(gymOrder.getOtherinfo(), GymOrder.KEY_REMOTE_GYM))){
					ErrorCode<String> code = synchGymService.lockCard(gymOrder);
					if(!code.isSuccess()) return showJsonError(model, code.getMsg());
				}
				ErrorCode code = openGymService.checkOrderCard(gymOrder);
				if(!code.isSuccess()){
					dbLogger.error("�������д�" + order.getTradeNo() + code.getMsg());
					return showJsonError(model, code.getMsg());
				}
			}else if(order instanceof GoodsOrder){
				GoodsOrder gorder = (GoodsOrder)order;
				ErrorCode<BaseGoods> code = goodsOrderService.checkOrder(gorder);
				if(!code.isSuccess()) return showJsonError(model, code.getMsg());
				BaseGoods goods = code.getRetval();
				if(order.getDue()>0) {
					payinfo = goods.getOtherinfo();
				}
			}
			
		} catch (Exception e) {
			dbLogger.warn("ȷ�϶�������" + order.getTradeNo(), e);
			return showJsonError(model, e.getMessage());
		}
		if(order.getDue() > 0){
			Map otherinfoMap = JsonUtils.readJsonToMap(payinfo);
			List<Discount> discountList = paymentService.getOrderDiscountList(order);
			Map<String, String> orderOtherinfo = VmUtils.readJsonToMap(order.getOtherinfo());
			List<String> limitPayList = paymentService.getLimitPayList();
			String bindpay = paymentService.getBindPay(discountList, orderOtherinfo, order);
			if(StringUtils.isNotBlank(bindpay)) {
				String[] bindpayArr = StringUtils.split(bindpay, ",");
				for(String t : bindpayArr){
					limitPayList.remove(t);
				}				
			}			
			PayValidHelper pvh = new PayValidHelper(otherinfoMap);
			pvh.setLimitPay(limitPayList);
			if(!pvh.supportPaymethod(fullPaymethod)){
				return showJsonError(model, "�����֧�ָ�֧����ʽ��");
			}
		}
		//��ȷ�ϵĶ������ٴ��޸ģ�����Ƿ��ܸ���֧����ʽ
		if((!StringUtils.equals(paymethod, order.getPaymethod()) || !StringUtils.equals(paybank, order.getPaybank()))){ 
			ErrorCode code = paymentService.isAllowChangePaymethod(order, paymethod, paybank);
			if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		}
		order.setPaymethod(paymethod);
		if (order.isZeroPay()) {
			order.setPaymethod(PaymethodConstant.PAYMETHOD_GEWAPAY);
			order.setPaybank(null);
		}else	{
			order.setPaybank(paybank);
		}
		order.setStatus(OrderConstant.STATUS_NEW_CONFIRM);
		Timestamp curtime = new Timestamp(System.currentTimeMillis());
		order.setUpdatetime(curtime);
		order.setModifytime(curtime);
		if(StringUtils.startsWith(paymethod, PaymethodConstant.PAYMETHOD_UMPAY)){
			ErrorCode<Integer> code = ticketOrderService.computeUmpayfee(order);
			if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		}else if(StringUtils.equals(paymethod, PaymethodConstant.PAYMETHOD_TELECOM) || StringUtils.equals(paymethod, PaymethodConstant.PAYMETHOD_MOBILE_TELECOM)){
			if(!StringUtils.equals(order.getCitycode(), AdminCityContant.CITYCODE_SH)) {
				return showJsonError(model, "��֧���Ϻ������ֻ���̻�");
			}
		}
		daoService.saveObject(order);
		Map jsonReturn = new HashMap();
		jsonReturn.put("orderId", "" + order.getId());
		if(PaymethodConstant.PAYMETHOD_GEWAPAY.equals(order.getPaymethod()) && !order.isZeroPay()){
			if(!PaymethodConstant.isValidPayMethod(PaymethodConstant.PAYMETHOD_GEWAPAY)) return showJsonError(model, "֧����ʽ��֧�֣�");
			if(StringUtils.isBlank(paypass)) return showJsonError(model, "֧�����벻��Ϊ�գ�");
			MemberAccount account = daoService.getObjectByUkey(MemberAccount.class, "memberid", member.getId(), false);
			if(account == null || account.isNopassword()) return showJsonError(model, "�˻�Ϊ�ջ�������ڼ򵥣�");
			if(!account.hasRights()){
				return showJsonError(model, "����˻��ݱ����ã��������������ϵ�ͷ�");
			}
			if(!PayUtil.passEquals(paypass, account.getPassword())) return showJsonError(model, "֧�����벻��ȷ��");
			if(account.getBanlance()>0 && StringUtils.isBlank(checkGewaPay)) return showJsonError(model, "�빴ѡ���֧����");
			int banlance = account.getBanlance(), bankcharge = account.getBankcharge(), othercharge = account.getOthercharge(), depositcharge= account.getDepositcharge();
			Long memberid = account.getMemberid();
			if(banlance==0) return showJsonError(model, "�˻����Ϊ0������֧����");
			if(banlance!=(bankcharge + othercharge+depositcharge)){ 
				dbLogger.warn("�����쳣��memberid:" + memberid + ", banlance:" + banlance + ", bankcharge:" + bankcharge + ", othercharge:" + othercharge);
				return showJsonError(model, "�˻�����쳣������ϵ�ͷ���");
			}
			int due = order.getDue();
			if(account.getBanlance()-account.getDepositcharge()< due){
				if(StringUtils.isBlank(chargeMethod)) return showJsonError(model, "��ѡ��������֧����֧��");
				List<String> chargeMethodList = Arrays.asList(PaymethodConstant.PAYMETHOD_ALIPAY, PaymethodConstant.PAYMETHOD_PNRPAY);
				String[] mainMethod = StringUtils.split(chargeMethod, ":");
				if(!chargeMethodList.contains(mainMethod[0])) return showJsonError(model, "��ֵ��ʽ����");
				Charge charge = addCharge(member, account, order, mainMethod);
				String redirectUrl = paymentService.getChargePayUrl(charge, WebUtils.getRemoteIp(request));
				jsonReturn.put("url", redirectUrl);
				return showJsonSuccess(model, jsonReturn);
			}
		}
		if(PaymethodConstant.isValidPayMethod(order.getPaymethod())) {
			jsonReturn.put("url", paymentService.getOrderPayUrl2(order));
			jsonReturn.put("pay", order.getPaymethod());
			if(PaymethodConstant.PAYMETHOD_UNIONPAYFAST.equals(order.getPaymethod()) 
					|| PaymethodConstant.PAYMETHOD_UNIONPAYFAST_ACTIVITY_JS.equals(order.getPaymethod()) 
					|| PaymethodConstant.PAYMETHOD_UNIONPAYFAST_ACTIVITY_BJ.equals(order.getPaymethod())
					|| PaymethodConstant.PAYMETHOD_UNIONPAYFAST_ACTIVITY_GZ.equals(order.getPaymethod())
					|| PaymethodConstant.PAYMETHOD_UNIONPAYFAST_ACTIVITY_SZ.equals(order.getPaymethod())
					|| PaymethodConstant.PAYMETHOD_UNIONPAYFAST_ACTIVITY_ZJ.equals(order.getPaymethod())){
				jsonReturn.put("url", config.getBasePath() + "gewapay/unionPayFast.xhtml?orderId=" + order.getId() + "&checkpass=" + StringUtil.md5(order.getId() + "&paymethod=" + order.getPaymethod()));
			}else if(PaymethodConstant.PAYMETHOD_BOCAGRMTPAY.equals(order.getPaymethod())){
				jsonReturn.put("url", config.getBasePath() + "gewapay/agrmt/cashier.xhtml?orderId=" + order.getId() + "&checkpass=" + StringUtil.md5(order.getId() + "&paymethod=" + order.getPaymethod()));
			}
		} else {
			return showJsonError(model, "֧����ʽ�д���");
		}
		orderMonitorService.addOrderChangeLog(order.getTradeNo(), "ȥ֧��", order, jsonReturn.toString() + ",host=" + Config.getServerIp());
		
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "[�û�֧������]�û�:"+member.getId()+"������:"+order.getTradeNo()+",֧����ʽ:"+paymethod+"[IP:]"+WebUtils.getRemoteIp(request));
		return showJsonSuccess(model, jsonReturn);
	}
	@RequestMapping("/gewapay/cancelOrder.xhtml")
	public String cancleOrder(String tradeNo, String source, ModelMap model) {
		String status = OrderConstant.STATUS_USER_CANCEL;	// ����:�û�ȡ��
		Member member = getLogonMember();
		try{
			if(PayUtil.isTicketTrade(tradeNo)){
				ticketOrderService.cancelTicketOrder2(tradeNo, member.getId(), status, "�û�ȡ��");
			}else if(PayUtil.isGoodsTrade(tradeNo)){
				goodsOrderService.cancelGoodsOrder(tradeNo, member);
			}else if(PayUtil.isSportTrade(tradeNo)){
				SportOrder order = daoService.getObjectByUkey(SportOrder.class, "tradeNo", tradeNo, false);
				sportUntransService.cancelSportOrder(order, member.getId(), "�û�ȡ��");
			}else if(PayUtil.isDramaOrder(tradeNo)){
				theatreOrderService.cancelDramaOrder(tradeNo, member.getId()+"", "�û�ȡ��");
			}else if(PayUtil.isPubSaleOrder(tradeNo)){
				pubSaleService.cancelPubSaleOrder(tradeNo, member.getId(), "�û�ȡ��");
			}else if(PayUtil.isGymTrade(tradeNo)){
				openGymService.cancelGymOrder(tradeNo, member.getId(), status, "�û�ȡ��");
			}
		}catch(HibernateOptimisticLockingFailureException e){
			dbLogger.warn(StringUtil.getExceptionTrace(e, 10));
			return show404(model, "����ȡ�������ظ�ȡ����");
		}catch(Exception e){
			dbLogger.warn(StringUtil.getExceptionTrace(e, 10));
			return show404(model, "����ȡ��ʧ�ܣ����Ժ����ԣ�");
		}
		
		if(StringUtils.isBlank(source)){
			source = "normal";
		}else{
			source = "userindex";
		}
		return showRedirect(CancelOrderURLMap.get(source), model);
	}
	private Charge addCharge(Member member, MemberAccount account, GewaOrder order, String[] mainMethod) {
		String chargeno = PayUtil.FLAG_CHARGE + order.getTradeNo().substring(1);
		int totalfee = 0;
		Charge charge = daoService.getObjectByUkey(Charge.class, "tradeNo", chargeno, false);
		if(charge==null){
			charge = new Charge(chargeno, ChargeConstant.WABIPAY);
			charge.setMemberid(member.getId());
			charge.setMembername(member.getNickname());
			charge.setOutorderid(order.getId());
			charge.setValidtime(order.getValidtime());
		}
		charge.setPaymethod(mainMethod[0]);
		if(mainMethod.length==2) charge.setPaybank(mainMethod[1]);
		totalfee = order.getDue() - account.getBanlance();
		charge.setTotalfee(totalfee);
		charge.setChargetype(ChargeConstant.TYPE_ORDER);
		daoService.saveObject(charge);
		return charge;
	}
	@RequestMapping("/checkOrderPay.xhtml")
	public String checkOrderPay(String tradeNo, ModelMap model) {
		TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeNo, false);
		if(order.isNetPaid()) return showJsonSuccess(model);
		else return showJsonError(model, "�Բ�������û��֧��!");
	}

	private void setGoodsOrderInfo(BaseGoods baseGoods, ModelMap model){
		if(baseGoods instanceof Goods){
			Goods goods = (Goods) baseGoods;
			if(goods!=null){
				ErrorCode<RemoteActivity> code = synchActivityService.getRemoteActivity(goods.getRelatedid());
				if(code.isSuccess())  model.put("activity", code.getRetval());
			}
		}else if(baseGoods instanceof ActivityGoods){
			ErrorCode<RemoteActivity> code = synchActivityService.getRemoteActivity(baseGoods.getRelatedid());
			if(code.isSuccess()) model.put("activity", code.getRetval());
		}
		model.put("goods", baseGoods);
		if(StringUtils.isBlank(baseGoods.getSpflag())){
			SpecialDiscount sp = daoService.getObjectByUkey(SpecialDiscount.class, "flag", baseGoods.getSpflag(), true);
			if(sp!=null) model.put("sp", sp);
		}
	}
	private Map getGoodsGiftData(TicketOrder order, OpenPlayItem opi){
		Map model = new HashMap();
		GoodsGift goodsGift = goodsOrderService.getBindGoodsGift(opi, PartnerConstant.GEWA_SELF);
		if(goodsGift!=null) {
			model.put("goodsGift", goodsGift);
			model.put("goods", daoService.getObject(Goods.class, goodsGift.getGoodsid()));
		}else {
			//TODO:���´������BuyItem
			BuyItem item = daoService.getObjectByUkey(BuyItem.class, "orderid", order.getId(), true);
			if(item != null) {
				model.put("item", item);
				List<Goods> goodsList = goodsService.getCurGoodsList(Goods.class, GoodsConstant.GOODS_TAG_BMH, opi.getCinemaid(), 0 ,1);
				if(goodsList.size() > 0) model.put("goods", goodsList.get(0));
			}
		}
		return model;
	}
}
