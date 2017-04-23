package com.gewara.web.action.api2mobile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.Config;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.PayConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.app.AppConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.helper.GewaAppHelper;
import com.gewara.helper.discount.MovieSpecialDiscountHelper;
import com.gewara.helper.discount.SpecialDiscountHelper;
import com.gewara.helper.discount.SportSpecialDiscountHelper;
import com.gewara.helper.order.OrderContainer;
import com.gewara.helper.order.TicketOrderContainer;
import com.gewara.model.api.ApiUser;
import com.gewara.model.api.ApiUserExtra;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.sport.OpenTimeItem;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.SellSeat;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.pay.PartnerPayUtil;
import com.gewara.pay.PayValidHelper;
import com.gewara.service.gewapay.ElecCardService;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.service.sport.SportOrderService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.order.impl.SpdiscountService;
import com.gewara.untrans.ticket.SpecialDiscountService;
import com.gewara.util.BeanUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.api.ApiAuth;
import com.gewara.web.action.api.BaseApiController;
import com.gewara.web.filter.NewApiAuthenticationFilter;

/**
 * @description �ֻ��ͻ���API�ӿ� ������������·APi��Ʊȯ֧�����API
 * @author shusong.liu
 *
 */
@Controller
public class Api2MobilePartController extends BaseApiController {
	@Autowired@Qualifier("specialDiscountService")
	private SpecialDiscountService specialDiscountService;
	@Autowired@Qualifier("elecCardService")
	private ElecCardService elecCardService;
	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;
	@Autowired@Qualifier("sportOrderService")
	private SportOrderService sportOrderService;
	public void setSportOrderService(SportOrderService sportOrderService) {
		this.sportOrderService = sportOrderService;
	}
	@Autowired@Qualifier("spdiscountService")
	private SpdiscountService spdiscountService;
	public void setSpdiscountService(SpdiscountService spdiscountService) {
		this.spdiscountService = spdiscountService;
	}
	
	private Discount getDiscountByTag(List<Discount> discountList, String tag){
		for(Discount discount : discountList){
			if(StringUtils.equals(discount.getTag(), tag)){
				return discount;
			}
		}
		return null;
	}
	/**
	 * ֧��ʱ����ȡ�����Ż���Ϣ(����Żݻ�б�API)
	 */
	@RequestMapping("/api2/mobile/getDiscountList.xhtml")
	public String getDiscountList(String tradeNo, String memberEncode, String ukey, String apptype, String appVersion, ModelMap model){
		if(StringUtils.isBlank(tradeNo)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		ApiAuth auth = NewApiAuthenticationFilter.getApiAuth();
		ApiUser partner = auth.getApiUser();
		String opentype = "";
		Member member = null;
		boolean supportPoint = false, supportCard = false;
		if(apiMobileService.isGewaPartner(partner.getId())){
			opentype = SpecialDiscount.OPENTYPE_WAP;
			member = memberService.getMemberByEncode(memberEncode);
			if(member == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û������ڣ�");
			if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
			model.put("hasMobile", member.isBindMobile()); ////��ֹ��ţ
			if(partner.getId().equals(PartnerConstant.GEWA_HTC)) model.put("hasMobile", true); //HTC
			supportPoint = GewaAppHelper.isSupportPoint(order, apptype, appVersion);
		}else {
			opentype = SpecialDiscount.OPENTYPE_PARTNER;
			model.put("hasMobile", true);
			if(!StringUtils.equals(order.getUkey(), ukey)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		}
		
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		model.put("order", order);
		model.put("cancelable", order.getStatus().equals(OrderConstant.STATUS_NEW));
		Integer minpoint = 500, maxpoint = 10000;
		if(order instanceof TicketOrder){
			OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", ((TicketOrder)order).getMpid(), true);
			if(StringUtils.contains(opi.getElecard(), PayConstant.CARDTYPE_PARTNER)){
				List<SellSeat> seatList = ticketOrderService.getOrderSeatList(order.getId());
				List<String> limitPayList = paymentService.getLimitPayList();
				Map<String, String> otherInfo = VmUtils.readJsonToMap(opi.getOtherinfo());
				PayValidHelper valHelp = new PayValidHelper(otherInfo);
				valHelp.setLimitPay(limitPayList);
				SpecialDiscountHelper sdh = new MovieSpecialDiscountHelper(opi, (TicketOrder)order, seatList, discountList);
				boolean openSpdiscount = StringUtils.contains(opi.getElecard(), PayConstant.CARDTYPE_PARTNER);
				Map discountData = spdiscountService.getSpecialDiscountData(sdh, valHelp, order, openSpdiscount, opi.getSpflag(), discountList, opentype, PayConstant.APPLY_TAG_MOVIE);
				model.putAll(discountData);
			}
			if(supportPoint){
				if(opi.isOpenPointPay()){ 
					minpoint = opi.getMinpoint();
					maxpoint = opi.getMaxpoint();
				}else {
					supportPoint = false;
				}
				if(opi.isOpenCardPay()) supportCard = true;
			}
		}else if(order instanceof SportOrder){
			SportOrder sorder = (SportOrder) order;
			OpenTimeTable ott = daoService.getObject(OpenTimeTable.class,  sorder.getOttid());
			List<OpenTimeItem> otiList = sportOrderService.getMyOtiList(order.getId());
			SpecialDiscountHelper sdh = new SportSpecialDiscountHelper(sorder, ott, discountList, otiList);
			Map<String, String> otherInfo = VmUtils.readJsonToMap(ott.getOtherinfo());
			model.put("opiOtherinfo", otherInfo);
			List<String> limitPayList = paymentService.getLimitPayList();
			PayValidHelper valHelp = new PayValidHelper(otherInfo);
			valHelp.setLimitPay(limitPayList);
			boolean openSpdiscount = StringUtils.contains(ott.getElecard(), PayConstant.CARDTYPE_PARTNER);
			Map discountData = spdiscountService.getSpecialDiscountData(sdh, valHelp, order, openSpdiscount, ott.getSpflag(), 
					discountList, opentype, PayConstant.APPLY_TAG_SPORT);
			
			model.putAll(discountData);
			Map<String, String> orderOtherinfo = VmUtils.readJsonToMap(order.getOtherinfo());
			model.put("orderOtherinfo", orderOtherinfo);
			if(supportPoint){
				if(ott.isOpenPointPay()) { 
					minpoint = ott.getMinpoint();
					maxpoint = ott.getMaxpoint();
				}else {
					supportPoint = false;
				}
				if(ott.isOpenCardPay()) supportCard = true;
			}
		}
		if(supportPoint){
			MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
			if(memberInfo.getPointvalue()<minpoint){
				model.put("lessPoint", true);
			}
		}
		model.put("minpoint", minpoint);
		model.put("maxpoint", maxpoint);
		model.put("supportCard", supportCard);
		model.put("supportPoint", supportPoint);
		model.put("pointDiscount", getDiscountByTag(discountList, PayConstant.DISCOUNT_TAG_POINT));
		List<SpecialDiscount> oldspList = (List<SpecialDiscount>)model.get("spdiscountList");
		if(oldspList!=null){
			List<SpecialDiscount> spList = new ArrayList<SpecialDiscount>();
			ApiUserExtra extra =  auth.getUserExtra();
			if(StringUtils.isNotBlank(extra.getAllPaymethod())){
				String[] pmList = StringUtils.split(extra.getAllPaymethod(), ",");
				for(String pm : pmList){
					for(SpecialDiscount oldsp : oldspList){
						if(StringUtils.isNotBlank(oldsp.getPaymethod())){
							List<String> tmppmList = Arrays.asList(StringUtils.split(oldsp.getPaymethod(), ","));
							if(!spList.contains(oldsp) && tmppmList.contains(pm)){
								spList.add(oldsp);
							}
						}else {
							if(!spList.contains(oldsp)) spList.add(oldsp);
						}
					}
				}
			}
			model.put("spdiscountList", spList);
		}
		return getXmlView(model, "api2/mobile/discountList.vm");
	}
	
	/**
	 * ֧����ʽչʾ(�ֻ��ͻ���֧����ʽ�б�API)
	 */
	@RequestMapping("/api2/mobile/showPayMethodList.xhtml")
	public String showPayMethodList(HttpServletRequest request, String tradeNo,String memberEncode,Long discountId, String ukey, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		if(StringUtils.isBlank(tradeNo)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		//TODO:ʹ��SpecialDiscount2
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class,"tradeNo",tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		ApiAuth auth = NewApiAuthenticationFilter.getApiAuth();
		if(apiMobileService.isGewaPartner(auth.getApiUser().getId())){
			Member member = memberService.getMemberByEncode(memberEncode);
			if(member == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û������ڣ�");
			if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		}else {
			if(!StringUtils.equals(order.getUkey(), ukey)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		}
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		SpecialDiscount sd = null;
		if(discountId != null){
			sd = daoService.getObject(SpecialDiscount.class, discountId);
			if(!SpecialDiscount.OPENTYPE_WAP.equals(sd.getOpentype()) 
					&& !SpecialDiscount.OPENTYPE_PARTNER.equals(sd.getOpentype())
					&& !SpecialDiscount.OPENTYPE_SPECIAL.equals(sd.getOpentype())){
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "ѡ����Żݷ�ʽ��ǰ��������ʹ�ã�");
			}
		}
		PayValidHelper valHelp = new PayValidHelper();
		if(sd!=null){
			ErrorCode<OrderContainer> discount = null;
			if(order instanceof TicketOrder){
				discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, order.getId(), sd, ip);
				if(discount.isSuccess()){
					OpenPlayItem opi = ((TicketOrderContainer)discount.getRetval()).getOpi();
					valHelp = new PayValidHelper(VmUtils.readJsonToMap(opi.getOtherinfo()));
				}
			}else if(order instanceof SportOrder){
				discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_SPORT, order.getId(), sd, ip);
			}else if(order instanceof DramaOrder){
				discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_DRAMA, order.getId(), sd, ip);
			}
			if(discount!=null){
				if(!discount.isSuccess()) {
					return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, discount.getMsg());
				}
				order = discount.getRetval().getOrder();
			}
		}else if(order instanceof TicketOrder){
			OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", ((TicketOrder)order).getMpid(), true);
			valHelp = new PayValidHelper(VmUtils.readJsonToMap(opi.getOtherinfo()));
		}
		
		Map<String, String> orderOtherinfo = VmUtils.readJsonToMap(order.getOtherinfo());
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		List<String> limitPayList = paymentService.getLimitPayList();
		String bindpay = paymentService.getBindPay(discountList, orderOtherinfo, order);
		if(StringUtils.isNotBlank(bindpay)){
			valHelp = new PayValidHelper(bindpay);
			model.put("flag", "true");
			String[] bindpayArr = StringUtils.split(bindpay, ",");
			for(String t : bindpayArr){
				limitPayList.remove(t);
			}
		}
		valHelp.setLimitPay(limitPayList);
		model.put("valHelp", valHelp);
		String orderPaymethod = order.getPaymethod();
		if(StringUtils.isNotBlank(order.getPaybank())) orderPaymethod = orderPaymethod + ":" + order.getPaybank();
		ApiUserExtra extra = auth.getUserExtra();
		model.put("payMethodMap", GewaAppHelper.getFilterMap(extra, discountList, AppConstant.MOVIE_APPVERSION_3_2_0));
		model.put("tradeno", order.getTradeNo());
		model.put("discountAmount", order.getDiscount());
		model.put("totalAmount", order.getTotalAmount());
		model.put("due", order.getDue());
		return getXmlView(model, "api2/pay/showPayMethodList.vm");
	}
	
	/**
	 * ȡ���Żݻ(ȡ���Ż�API)
	 */
	@RequestMapping("/api2/mobile/cancelDiscount.xhtml")
	public String cancelDisCount(String tradeNo,String memberEncode,Long discountId, String ukey, ModelMap model){
		if(StringUtils.isBlank(tradeNo)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class,"tradeNo",tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		ApiAuth auth = NewApiAuthenticationFilter.getApiAuth();
		if(apiMobileService.isGewaPartner(auth.getApiUser().getId())){
			Member member = memberService.getMemberByEncode(memberEncode);
			if(member == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û������ڣ�");
			if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		}else {
			if(!StringUtils.equals(order.getUkey(), ukey)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		}
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		ErrorCode<GewaOrder> code = ticketOrderService.removeDiscount(order, discountId);
		if(!code.isSuccess()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, code.getMsg());
		return getXmlView(model, "api/mobile/result.vm");
	}
	
	/**
	 * ѡ��֧����ʽ�����ض�Ӧ֧����ת���ӻ�����Ϣ(�ֻ��ͻ���֧����תAPI)
	 */
	@RequestMapping("/api2/mobile/selectPayMethod.xhtml")
	public String selectPayMethod(String tradeNo,String memberEncode, String ukey, String payMethod, String apptype, String appVersion, HttpServletRequest request, ModelMap model){
		ApiAuth auth = NewApiAuthenticationFilter.getApiAuth();
		if(StringUtils.isBlank(tradeNo)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if(apiMobileService.isGewaPartner(auth.getApiUser().getId())){
			Member member = memberService.getMemberByEncode(memberEncode);
			if(member == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û������ڣ�");
			if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		}else {
			if(!StringUtils.equals(order.getUkey(), ukey)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		}
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		ApiUser partner = auth.getApiUser();
		Map paramsData = new LinkedHashMap();
		String ip = WebUtils.getRemoteIp(request);
		String[] paypair = StringUtils.split(payMethod, ":");
		String method = METHOD_GET;
		if(!PaymethodConstant.isValidPayMethod(paypair[0])) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "֧����ʽ����");
		String mainPaymethod = paypair[0];
		String paybank = paypair.length>1?paypair[1]:"";
		if((!StringUtils.equals(mainPaymethod, order.getPaymethod()) || !StringUtils.equals(paybank, order.getPaybank()))){ 
			ErrorCode code = paymentService.isAllowChangePaymethod(order, mainPaymethod, paybank);
			if(!code.isSuccess()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, code.getMsg());
		}
		List<String> payServerList = paymentService.getPayserverMethodList();
		boolean isRemoveParams = true;
		if(payServerList.contains(mainPaymethod)){
			order.setPaybank(paybank);
			order.setPaymethod(payMethod);
			order.setStatus(OrderConstant.STATUS_NEW_CONFIRM);
			orderMonitorService.addOrderChangeLog(order.getTradeNo(), "APIȥ֧��", order, payMethod + ",host=" + Config.getServerIp());
			daoService.saveObject(order);
			String version = GewaAppHelper.getChinaSmartPayVersion(order, apptype, order.getPaymethod(), appVersion);
			paymentService.usePayServer(order, ip, paramsData, version, request, model);
			isRemoveParams = false;
		}else {
			if(StringUtils.equals(mainPaymethod, PaymethodConstant.PAYMETHOD_PARTNERPAY)){
				paramsData = PartnerPayUtil.getNetPayParams(order, partner);
				order.setPaymethod(payMethod);
				model.put("payUrl", paramsData.remove("payurl")+"");
			}else{
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "֧����ʽ����");
			}
		}
		if(isRemoveParams) model.put("method", method);
		model.put("paramsData", paramsData);
		model.put("tradeno", order.getTradeNo());
		model.put("discountAmount", order.getDiscount());
		model.put("totalAmount", order.getTotalAmount());
		model.put("due", order.getDue());
		order.setPaybank(paybank);
		order.setStatus(OrderConstant.STATUS_NEW_CONFIRM);
		orderMonitorService.addOrderChangeLog(order.getTradeNo(), "APIȥ֧��", order, payMethod + ",host=" + Config.getServerIp());
		daoService.saveObject(order);
		return getXmlView(model, "api/mobile/selectPayMethod.vm");
	}
	
	private static final String METHOD_GET = "get";
	private static final String PAY_METHOD_CARD = "card";//Ʊȯ֧��
	/**
	 * ��ǰ�û��ɽ���֧��Ʊȯ���б�(�û�������֧����Ʊȯ�б�API)
	 */
	@RequestMapping("/api2/mobile/memberPayCardList.xhtml")
	public String memberPayCardList(String tradeNo, String memberEncode, ModelMap model){
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		Member member = memberService.getMemberByEncode(memberEncode);
		if(member == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û������ڣ�");
		updateOrderMemberInfo(order, member);
		if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		order.setPaymethod(PAY_METHOD_CARD);
		daoService.saveObject(order);
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		if(order instanceof TicketOrder){
			OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", ((TicketOrder)order).getMpid(), true);
			if (opi.isOpenCardPay()) {
				List<ElecCard> cardList = elecCardService.getAvailableCardList((TicketOrder)order, discountList, opi, member.getId()).getAvaliableList();
				filterCardList(cardList);
				model.put("cardList", cardList);
			}else{
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��ǰ���β�֧��Ʊȯ֧��������ѡ������֧����ʽ��");
			}
		}else if(order instanceof SportOrder){
			OpenTimeTable ott = daoService.getObject(OpenTimeTable.class, ((SportOrder)order).getOttid());
			if(ott.isOpenCardPay()){
				List<ElecCard> cardList = elecCardService.getAvailableCardList((SportOrder)order, discountList, ott, member.getId()).getAvaliableList();
				filterCardList(cardList);
				model.put("cardList", cardList);
			}else{
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��ǰ���β�֧��Ʊȯ֧��������ѡ������֧����ʽ��");
			}
		}
		if(discountList.size() > 0){
			Map<Long, ElecCard> cardMap = new HashMap<Long, ElecCard>();//�Ѱ󶨵�Ʊȯ��Ϣ
			List<Discount> cardDiscountList = new ArrayList<Discount>();//�Żݷ�ʽ
			Map<Long, Integer> discountAmountMap = new HashMap<Long, Integer>();
			for(Discount discount: discountList){
				if(StringUtils.equals(PayConstant.DISCOUNT_TAG_ECARD, discount.getTag())){
					ElecCard elecCard = daoService.getObject(ElecCard.class, discount.getRelatedid());
					if(elecCard != null){
						cardMap.put(discount.getId(), elecCard);
						cardDiscountList.add(discount);
						discountAmountMap.put(discount.getId(), discount.getAmount());
					}
				}
			}
			if(!cardDiscountList.isEmpty()) model.put("cardDiscountIdList", BeanUtil.getBeanPropertyList(cardDiscountList, Long.class, "id",true));
			model.put("cardMap", cardMap);
			model.put("discountAmountMap", discountAmountMap);
		}
		return getXmlView(model, "api2/mobile/memberPayCardList.vm");
	}
	private void filterCardList(List<ElecCard> cardList){
		if(cardList==null) return;
		List<ElecCard> tmpList = new ArrayList<ElecCard>();
		Map<String, String> mobilePayMap = GewaAppHelper.textMap;
		Set<String> mPay = mobilePayMap.keySet();
		for(ElecCard card : cardList){
			String bindpay = card.getEbatch().getBindpay();
			if(StringUtils.isNotBlank(bindpay)){
				List<String> pList = Arrays.asList(bindpay.split(","));
				boolean res = CollectionUtils.containsAny(mPay, pList); //�Ƿ��н���
				if(!res){
					tmpList.add(card);
				}
			}
		}
		cardList.removeAll(tmpList);
	}
	/**
	 * ʹ��Ʊȯ����֧����ֵ(Ʊȯ֧����ʽAPI)
	 */
	@RequestMapping("/api2/mobile/useCard.xhtml")
	public String useCard(ModelMap model){
		return notSupport(model);
	}
	/**
	 * ʹ��Ʊȯ����֧����ֵ(Ʊȯ֧����ʽAPI)
	 */
	@RequestMapping("/api2/mobile/usePoint.xhtml")
	public String usePoint(ModelMap model){
		return notSupport(model);
	}
	
	/**
	 * ȡ�� ֧������ʱ����ѡ�����֧����Ʊȯ(ȡ����ʹ��ƱȯAPI)
	 */
	@RequestMapping("/api2/mobile/cancelPayCard.xhtml")
	public String cancelPayCard(String tradeNo,Long discountId,String memberEncode,ModelMap model){
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(discountId == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		Member member = memberService.getMemberByEncode(memberEncode);
		if(member == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û������ڣ�");
		if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		Discount discount = daoService.getObject(Discount.class, discountId);
		if(discount == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		ElecCard card = daoService.getObject(ElecCard.class, discount.getRelatedid());
		ErrorCode<GewaOrder> code = ticketOrderService.removeDiscount(order, discountId);
		if(code.isSuccess()) {
			GewaOrder ticketOrder = code.getRetval();
			model.put("order", ticketOrder);
			model.put("card", card);
			return getXmlView(model, "api/mobile/cancelPayCard.vm");
		}
		return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, code.getMsg());
	}
	
	/**
	 * ѡ��Ʊȯ֧����ȷ��֧��(ʹ��Ʊȯ֧��API)
	 * @param version 
	 */
	@RequestMapping("/api2/mobile/confirmCardPay.xhtml")
	public String confirmCardPay(ModelMap model){
		return notSupport(model);
	}
	private void updateOrderMemberInfo(GewaOrder order,Member member){
		if(PartnerConstant.IPHONE.equals(order.getMemberid()) || PartnerConstant.ANDROID.equals(order.getMemberid())){//����ֻ��ͻ���δ��¼�û��µ�
			order.setMemberid(member.getId());
			order.setMembername(member.getNickname());
			daoService.saveObject(order);
		}
	}
	
	/**
	 * ���֧��(�û����֧����ʽAPI)
	 */
	@RequestMapping("/api2/mobile/confirmBalancePay.xhtml")
	public String confirmBalancePay(ModelMap model){
		return notSupport(model);
	}
	@RequestMapping("/api2/mobile/showWbPaymethod.xhtml")
	public String balancePay(ModelMap model){
		return notSupport(model);
	}
	@RequestMapping("/api2/mobile/selectWbPaymethod.xhtml")
	public String selectWbPaymethod(ModelMap model){
		return notSupport(model);
	}
}
