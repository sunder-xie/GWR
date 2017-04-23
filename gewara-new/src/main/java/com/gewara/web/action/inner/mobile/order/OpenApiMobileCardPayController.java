package com.gewara.web.action.inner.mobile.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.helper.GewaAppHelper;
import com.gewara.helper.api.GewaApiOrderHelper;
import com.gewara.helper.order.GoodsOrderContainer;
import com.gewara.helper.order.OrderContainer;
import com.gewara.helper.order.SportOrderContainer;
import com.gewara.helper.order.TicketOrderContainer;
import com.gewara.model.api.ApiUserExtra;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.goods.BaseGoods;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.SpCode;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.model.user.OpenMember;
import com.gewara.pay.PayValidHelper;
import com.gewara.service.drama.DramaOrderService;
import com.gewara.service.gewapay.ElecCardService;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.service.sport.SportOrderService;
import com.gewara.service.ticket.TicketDiscountService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.ticket.OrderProcessService;
import com.gewara.untrans.ticket.SpecialDiscountService;
import com.gewara.util.BeanUtil;
import com.gewara.util.VmUtils;
import com.gewara.web.action.inner.OpenApiAuth;
import com.gewara.web.action.inner.mobile.BaseOpenApiMobileController;
import com.gewara.web.filter.OpenApiMobileAuthenticationFilter;
@Controller
public class OpenApiMobileCardPayController extends BaseOpenApiMobileController{
	@Autowired@Qualifier("elecCardService")
	private ElecCardService elecCardService;
	@Autowired@Qualifier("ticketDiscountService")
	private TicketDiscountService ticketDiscountService;
	@Autowired@Qualifier("dramaOrderService")
	private DramaOrderService dramaOrderService;
	@Autowired@Qualifier("sportOrderService")
	private SportOrderService sportOrderService;
	@Autowired@Qualifier("orderProcessService")
	private OrderProcessService orderProcessService;
	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;
	@Autowired@Qualifier("specialDiscountService")
	private SpecialDiscountService specialDiscountService;
	private static final String PAY_METHOD_CARD = "card";//Ʊȯ֧��
	/**
	 * ��ǰ�û��ɽ���֧��Ʊȯ���б�(�û�������֧����Ʊȯ�б�API)
	 */
	@RequestMapping("/openapi/mobile/order/memberPayCardList.xhtml")
	public String memberPayCardList(String tradeNo,  ModelMap model){
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		Member member = OpenApiMobileAuthenticationFilter.getOpenApiAuth().getMember();
		if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
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
		}else if(order instanceof DramaOrder){
			OpenDramaItem item = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", ((DramaOrder)order).getDpid());
			if(item.isOpenCardPay()){
				List<ElecCard> cardList = elecCardService.getAvailableCardList((DramaOrder)order, discountList, item, member.getId()).getAvaliableList();
				filterCardList(cardList);
				model.put("cardList", cardList);
			}else{
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��ǰ���β�֧��Ʊȯ֧��������ѡ������֧����ʽ��");
			}
		}else if(order instanceof GoodsOrder){
			BaseGoods goods = daoService.getObject(BaseGoods.class, ((GoodsOrder)order).getGoodsid());
			if(goods.isOpenCardPay()){
				List<ElecCard> cardList = elecCardService.getAvailableCardList((GoodsOrder)order, discountList, goods, member.getId()).getAvaliableList();
				filterCardList(cardList);
				model.put("cardList", cardList);
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
		return getXmlView(model, "inner/mobile/memberPayCardList.vm");
	}
	
	/**
	 * ʹ��Ʊȯ����֧����ֵ(Ʊȯ֧����ʽAPI)
	 */
	@RequestMapping("/openapi/mobile/order/useCard.xhtml")
	public String useCard(String tradeNo,String cardNo,String cardPass, ModelMap model){
		if((StringUtils.isBlank(cardNo) && StringUtils.isBlank(cardPass)) || 
				(StringUtils.isNotBlank(cardNo) && StringUtils.isNotBlank(cardPass))){ 
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		}
		cardNo = StringUtils.isBlank(cardNo)?"":cardNo;
		cardPass = StringUtils.isBlank(cardPass)?"":cardPass;
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		OpenApiAuth auth = OpenApiMobileAuthenticationFilter.getOpenApiAuth();
		Member member = auth.getMember();
		if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		ElecCard card = null;
		if(StringUtils.isBlank(cardPass)){//ֱ��ʹ���Ѱ󶨵�Ʊȯ������֧��
			card = elecCardService.getMemberElecCardByNo(member.getId(), cardNo);
		}else{//����Ʊȯ���룬���а󶨣�����֧��
			if(StringUtils.startsWith(cardPass, SpCode.PASSPRE)){//�ؼ�ȯ
				ErrorCode<? extends OrderContainer> useResult = specialDiscountService.useSpecialDiscountBySpCodePass(order.getOrdertype(), order.getId(), auth.getRemoteIp(), member.getId(), cardPass);
				if(useResult.isSuccess()) {
					model.put("bindCardGroup", getSpcodeDiscountMap(useResult.getRetval()));
					return getXmlView(model, "inner/mobile/useCard.vm");
				}else{
					return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, useResult.getMsg()+",������������ϵ�ͷ���4000-406-506");
				}
			}
			card = elecCardService.getElecCardByPass(StringUtils.upperCase(cardPass));
		}
		if(card == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�����벻���ڣ����������룡");
		ErrorCode code = useElecCard(order, card, member.getId());
		if(code.isSuccess()){
			model.put("bindCardGroup", code.getRetval());
			return getXmlView(model, "inner/mobile/useCard.vm");
		}
		return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, code.getMsg());
	}
	private Map getSpcodeDiscountMap(OrderContainer con){
		Map map = new HashMap<String, String>();
		Discount discount =  con.getCurUsedDiscount();
		GewaOrder order = con.getOrder();
		map.put("discountId", discount.getId());
		map.put("cardno",  discount.getId());
		map.put("usage", "�����ֽ�" + discount.getAmount() + "Ԫ");
		map.put("cardtype", "C");
		map.put("cardDiscountAmount", discount.getAmount());
		map.put("due", order.getDue());
		map.put("totalDiscount", order.getDiscount());
		map.put("totalAmount", order.getTotalAmount());
		return map;
	}
	@RequestMapping("/openapi/mobile/order/useSpcode.xhtml")
	public String useSpcode(String tradeNo, String cardPass, ModelMap model){
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		SpCode spcode = ticketDiscountService.getSpCodeByPass(cardPass);
		OpenApiAuth auth = OpenApiMobileAuthenticationFilter.getOpenApiAuth();
		Member member = auth.getMember();
		if(spcode!=null){
			ErrorCode<? extends OrderContainer> useResult = specialDiscountService.useSpecialDiscountBySpCodePass(order.getOrdertype(), order.getId(), auth.getRemoteIp(), member.getId(), cardPass);
			if(useResult.isSuccess()) {
				model.put("container", useResult.getRetval());
				return getXmlView(model, "inner/mobile/useSpcode.vm");
			}else{
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, useResult.getMsg()+",������������ϵ�ͷ���4000-406-506");
			}
		}else{
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,  "���ĵ����벻���ڻ�ʱ,������������ϵ�ͷ���4000-406-506");
		}
	}
	
	@RequestMapping("/openapi/mobile/member/getSpCodeList.xhtml")
	public String getSpCodeList(Long spid, int from, int maxnum, ModelMap model, HttpServletRequest request){
		OpenApiAuth auth = OpenApiMobileAuthenticationFilter.getOpenApiAuth();
		Member member = auth.getMember();
		List<SpCode> spCodeList = ticketDiscountService.getSpCodeList(member.getId(), spid, from, maxnum);
		List<Map<String, Object>> resMapList = new ArrayList<Map<String, Object>>();
		for(SpCode code : spCodeList){
			if(code.getUsedcount()==0){
				SpecialDiscount sp = daoService.getObject(SpecialDiscount.class, code.getSdid());
				Map<String, Object> resMap = GewaApiOrderHelper.getSpCodeMap(code);
				resMap.put("remark", sp.getRemark());
				resMap.put("bankname", sp.getBankname());
				resMap.put("enableRemark", sp.getEnableRemark());
				resMap.put("description", sp.getDescription());
				resMap.put("timefrom", sp.getTimefrom());
				resMap.put("timeto", sp.getTimeto());
				resMapList.add(resMap);
			}
		}
		return getOpenApiXmlList(resMapList, "spcodeList,spcode", model, request);
	}
	private ErrorCode<Map> useElecCard(GewaOrder order, ElecCard card, Long memberid){
		Long discountId = null;
		GewaOrder gOrder = null;
		if(order instanceof TicketOrder){
			ErrorCode<TicketOrderContainer> code = ticketDiscountService.useElecCardByTradeNo(order.getTradeNo(), card, memberid);
			if(!code.isSuccess()) {
				return ErrorCode.getFailure(code.getMsg()+"������������ϵ�ͷ���");
			}
			discountId = code.getRetval().getCurUsedDiscount().getId();
			gOrder = code.getRetval().getTicketOrder();
		}else if(order instanceof TicketOrder){
			ErrorCode<SportOrderContainer> code = sportOrderService.useElecCard(order.getId(), card, memberid);
			if(!code.isSuccess()) {
				return ErrorCode.getFailure(code.getMsg()+"������������ϵ�ͷ���");
			}
			discountId = code.getRetval().getCurUsedDiscount().getId();
			gOrder = code.getRetval().getOrder();
		}else if(order instanceof DramaOrder){
			ErrorCode<DramaOrder> code = dramaOrderService.useElecCard((DramaOrder)order, card, memberid);
			if(!code.isSuccess()) {
				return ErrorCode.getFailure(code.getMsg()+"������������ϵ�ͷ���");
			}
			List<Discount> discountList = paymentService.getOrderDiscountList(order);
			discountId = discountList.get(0).getId();
			gOrder = code.getRetval();
		}else if(order instanceof GoodsOrder){
			ErrorCode<GoodsOrderContainer> code = goodsOrderService.useElecCard(order.getId(), card, memberid);
			if(!code.isSuccess()) {
				return ErrorCode.getFailure(code.getMsg()+"������������ϵ�ͷ���");
			}
			discountId = code.getRetval().getCurUsedDiscount().getId();
			gOrder = code.getRetval().getOrder();
		}else {
			return ErrorCode.getFailure("�ö������Ͳ�֧��ȯ�Ż�");
		}
		Map jsonMap = new HashMap<String, String>();
		jsonMap.put("discountId", discountId);
		jsonMap.put("cardno", card.getCardno());
		jsonMap.put("usage", card.gainUsage());
		jsonMap.put("cardtype", card.getEbatch().getCardtype());
		jsonMap.put("cardDiscountAmount", card.getEbatch().getAmount());
		jsonMap.put("due", gOrder.getDue());
		jsonMap.put("totalDiscount", gOrder.getDiscount());
		jsonMap.put("totalAmount", gOrder.getTotalAmount());
		return ErrorCode.getSuccessReturn(jsonMap);
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
	 * ȡ�� ֧������ʱ����ѡ�����֧����Ʊȯ(ȡ����ʹ��ƱȯAPI)
	 */
	@RequestMapping("/openapi/mobile/order/cancelPayCard.xhtml")
	public String cancelPayCard(String tradeNo,Long discountId,ModelMap model){
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(discountId == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		Member member = OpenApiMobileAuthenticationFilter.getOpenApiAuth().getMember();
		if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		Discount discount = daoService.getObject(Discount.class, discountId);
		if(discount == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		ErrorCode<GewaOrder> code = ticketOrderService.removeDiscount(order, discountId);
		if(code.isSuccess()) {
			ElecCard card = daoService.getObject(ElecCard.class, discount.getRelatedid());
			GewaOrder ticketOrder = code.getRetval();
			model.put("order", ticketOrder);
			model.put("card", card);
			return getXmlView(model, "inner/mobile/cancelPayCard.vm");
		}
		return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, code.getMsg());
	}
	
	/**
	 * ѡ��Ʊȯ֧����ȷ��֧��(ʹ��Ʊȯ֧��API)
	 * @param version 
	 */
	@RequestMapping("/openapi/mobile/order/confirmCardPay.xhtml")
	public String confirmCardPay(String tradeNo,ModelMap model,String appVersion){
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		Member member = OpenApiMobileAuthenticationFilter.getOpenApiAuth().getMember();
		if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		if((!StringUtils.equals(PaymethodConstant.PAYMETHOD_GEWAPAY, order.getPaymethod()))){ 
			ErrorCode code = paymentService.isAllowChangePaymethod(order, PaymethodConstant.PAYMETHOD_GEWAPAY, null);
			if(!code.isSuccess()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, code.getMsg());
		}
		order.setPaymethod(PaymethodConstant.PAYMETHOD_GEWAPAY);
		order.setStatus(OrderConstant.STATUS_NEW_CONFIRM);
		daoService.saveObject(order);
		orderMonitorService.addOrderChangeLog(order.getTradeNo(), "APIȥ֧��", order, PaymethodConstant.PAYMETHOD_GEWAPAY + ",host=" + Config.getServerIp());
		if(order.getDue() > 0){ //ʹ��Ʊȯ֧���󣬻��в���Ӧ����δ����ѡ������֧����ʽ����֧��
			List<String> limitPayList = paymentService.getLimitPayList();
			List<Discount> discountList = paymentService.getOrderDiscountList(order);
			model.put("discountList", discountList);
			Map<String, String> orderOtherinfo = VmUtils.readJsonToMap(order.getOtherinfo());
			model.put("orderOtherinfo", orderOtherinfo);
			String bindpay = paymentService.getBindPay(discountList, orderOtherinfo, order );
			PayValidHelper valHelp = new PayValidHelper();
			if(StringUtils.isNotBlank(bindpay)){
				valHelp = new PayValidHelper(bindpay);
				String[] bindpayArr = StringUtils.split(bindpay, ",");
				for(String t : bindpayArr){
					limitPayList.remove(t);
				}
			}
			valHelp.setLimitPay(limitPayList);
			model.put("valHelp", valHelp);
			String orderPaymethod = order.getPaymethod();
			if(StringUtils.isNotBlank(order.getPaybank())) orderPaymethod = orderPaymethod + ":" + order.getPaybank();
			OpenApiAuth auth = OpenApiMobileAuthenticationFilter.getOpenApiAuth();
			ApiUserExtra extra = auth.getUserExtra();
			MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
			String omCategory = null;
			ErrorCode<OpenMember> omCode = getOpenMember(memberInfo);
			if(omCode.isSuccess()){
				omCategory = omCode.getRetval().getCategory();
			}
			model.put("payMethodMap", GewaAppHelper.getFilterMap(extra, discountList, omCategory, appVersion));
			getOrderCommon(model, order,false);
			model.put("alimember", isAliMember(memberInfo));
			return getXmlView(model, "inner/mobile/showPayMethodList.vm");
		}else {  //ȫ��ʹ��Ʊȯ֧����ȷ�϶���
			ErrorCode code = orderProcessService.gewaPayOrderAtServer(order.getId(), member.getId(), null, true);
			if(code.isSuccess()){
				getOrderCommon(model, order, true);
				return getXmlView(model, "inner/mobile/confirmBalancePay.vm");//ʹ��Ʊȯ֧���ɹ���
			}else{
				return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR,code.getMsg());
			}
		}
	}
}
