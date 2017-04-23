package com.gewara.web.action.inner.partner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.Config;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.PayConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.helper.GewaAppHelper;
import com.gewara.helper.api.GewaApiOrderHelper;
import com.gewara.helper.discount.MovieSpecialDiscountHelper;
import com.gewara.helper.discount.SpecialDiscountHelper;
import com.gewara.helper.order.OrderContainer;
import com.gewara.helper.order.TicketOrderContainer;
import com.gewara.model.api.ApiUser;
import com.gewara.model.api.ApiUserExtra;
import com.gewara.model.movie.Cinema;
import com.gewara.model.partner.CallbackOrder;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.SellSeat;
import com.gewara.model.user.OpenMember;
import com.gewara.pay.PartnerPayUtil;
import com.gewara.pay.PayValidHelper;
import com.gewara.service.OperationService;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.service.order.OrderQueryService;
import com.gewara.service.partner.PartnerSynchService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.order.impl.SpdiscountService;
import com.gewara.untrans.ticket.SpecialDiscountService;
import com.gewara.util.DateUtil;
import com.gewara.util.PKCoderUtil;
import com.gewara.util.VmUtils;
import com.gewara.web.action.api.ApiAuth;
import com.gewara.web.filter.OpenApiPartnerAuthenticationFilter;
@Controller
public class OpenApiPartnerOrderController extends BaseOpenApiPartnerController{
	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;
	
	@Autowired@Qualifier("spdiscountService")
	private SpdiscountService spdiscountService;
	
	@Autowired@Qualifier("specialDiscountService")
	private SpecialDiscountService specialDiscountService;
	
	@Autowired@Qualifier("partnerSynchService")
	private PartnerSynchService partnerSynchService;
	
	@Autowired@Qualifier("orderQueryService")
	private OrderQueryService orderQueryService;
	
	//�̼��û��¶���
	@RequestMapping("/openapi/partner/addTicketOrder.xhtml")
	public String addOrder(Long mpid, String mobile, String seatLabel, String ukey, String language, String edition, ModelMap model, HttpServletRequest request) {
		ApiUser partner = OpenApiPartnerAuthenticationFilter.getApiAuth().getApiUser();
		OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", mpid, true);
		if(opi==null) return getErrorXmlView(model, ApiConstant.CODE_OPI_NOT_EXISTS, "���β����ڣ�");
		if(StringUtils.isNotBlank(language)){
			if(!StringUtils.equalsIgnoreCase(language, opi.getLanguage())){
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���γ��������Ѹ��ģ����������س�����Ϣ");
			}
		}
		if(StringUtils.isNotBlank(edition)){
			if(!StringUtils.equalsIgnoreCase(edition, opi.getEdition())){
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���γ��ΰ汾�Ѹ��ģ����������س�����Ϣ");
			}
		}
		
		ErrorCode code = addOrder(partner, null, StringUtils.isBlank(ukey)? StringUtils.reverse(partner.getId()+mobile):ukey, opi, mobile, seatLabel, model);
		if(!code.isSuccess()) return getErrorXmlView(model, code.getErrcode(), code.getMsg());
		TicketOrder order = (TicketOrder)model.get("order");
		Map<String, Object> resMap = GewaApiOrderHelper.getTicketOrderMap(order);
		Cinema cinema = daoService.getObject(Cinema.class, order.getCinemaid());
		resMap.put("cinemaname", cinema.getName());
		resMap.put("cityname", cinema.getCityname());
		putDetail(resMap, model, request);
		model.put("root", "ticketOrder");
		return getOpenApiXmlDetail(model);
	}
	//������ϸ��Ϣ��ѯ
	@RequestMapping("/openapi/partner/ticketOrderDetail.xhtml")
	public String orderDetail(String tradeno, ModelMap model, HttpServletRequest request) {
		ApiUser partner = OpenApiPartnerAuthenticationFilter.getApiAuth().getApiUser();
		TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeno, false);
		if(order==null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "����������!");
		if(!order.getPartnerid().equals(partner.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�ѯ�����̼Ҷ���!");
		Map<String, Object> resMap = GewaApiOrderHelper.getTicketOrderMap(order);
		Cinema cinema = daoService.getObject(Cinema.class, order.getCinemaid());
		resMap.put("cinemaname", cinema.getName());
		resMap.put("cityname", cinema.getCityname());
		putDetail(resMap, model, request);
		model.put("root", "ticketOrder");
		return getOpenApiXmlDetail(model);
	}
	//����������Ϣ��ѯ
	@RequestMapping("/openapi/partner/ticketOrderOtherInfo.xhtml")
	public String orderStatus(String tradeno, ModelMap model) {
		ApiUser partner = OpenApiPartnerAuthenticationFilter.getApiAuth().getApiUser();
		TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeno, false);
		if(order==null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "����������!");
		if(!order.getPartnerid().equals(partner.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�ѯ�����̼Ҷ���!");
		OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", order.getMpid(), true);
		model.put("order", order);
		model.put("orderStatus", ApiConstant.getMappedOrderStatus(order.getFullStatus()));
		if(order.isPaidSuccess() && StringUtils.isNotBlank(partner.getSecretKey())){//�ɹ�����
			String msgTemplate = messageService.getCheckpassTemplate(opi);
			String checkpass = msgTemplate.indexOf("hfhpass") >= 0 ? order.getHfhpass(): order.getCheckpass();
			List<SellSeat> seatList = ticketOrderService.getOrderSeatList(order.getId());
			String remark = messageService.getCheckpassMsg(msgTemplate, order, seatList, opi).getRetval();
			String deskey = partner.getSecretKey();
			model.put("encCheckpass", PKCoderUtil.encryptWithThiDES(deskey, checkpass, "utf-8"));
			model.put("encMobile", PKCoderUtil.encryptWithThiDES(deskey, order.getMobile(), "utf-8"));
			model.put("encRemark", PKCoderUtil.encryptWithThiDES(deskey, remark, "utf-8"));
		}
		model.put("orderStatus", ApiConstant.getMappedOrderStatus(order.getFullStatus()));
		return getXmlView(model, "inner/partner/ticketOrderOtherInfo.vm");
	}
	
	//�������·���
	@RequestMapping("/openapi/partner/reSendSms.xhtml")
	public String smsResend(String tradeno, ModelMap model) {
		ApiUser partner = OpenApiPartnerAuthenticationFilter.getApiAuth().getApiUser();
		TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeno, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��ѯ��Ϣ�����ڣ�");
		if(!order.getPartnerid().equals(partner.getId())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�ѯ�����̼Ҷ���!");
		if(!StringUtils.equals(order.getStatus(), OrderConstant.STATUS_PAID_SUCCESS)){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�ǳɹ��Ķ������ܷ�����Ϣ");
		}
		if(!order.isPaidSuccess()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "δ�ɹ��Ķ�����");
		String opkey = OperationService.TAG_SENDTICKETPWD + partner.getId() + order.getId();
		if(!operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 3, 3)){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,  "ͬһ�������ֻ�ܷ���3�Σ�");
		}
		OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", order.getMpid());
		if(DateUtil.addHour(opi.getPlaytime(), 1).before(DateUtil.getCurFullTimestamp())){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,  "�����ѹ���");
		}
		untransService.reSendOrderMsg(order);
		operationService.updateOperation(opkey, OperationService.ONE_DAY * 3, 3);
		return getSuccessXmlView(model);
	}
	/**
	 * ��������Ч�� 
	 */
	@RequestMapping("/openapi/partner/getOrderValidTime.xhtml")
	public String getTicketHelp(String tradeNo, ModelMap model){
		Long valid = orderQueryService.getOrderValidTime(tradeNo);
		if(valid==null){
			return getErrorXmlView(model,ApiConstant.CODE_SIGN_ERROR,"��Чʱ���ѹ�");
		}
		Long cur = System.currentTimeMillis();
		Long remain = valid - cur;
		model.put("remain", remain);
		return getSingleResultXmlView(model, remain);
	}
	/**
	 * ������������¼�û�����
	 * @param userid
	 * @param from
	 * @param maxnum
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/openapi/partner/qryOpenMemberOrderList.xhtml")
	public String qryOpemMemberOrderList(String userid, Integer from, Integer maxnum, ModelMap model, HttpServletRequest request){
		ApiUser partner = OpenApiPartnerAuthenticationFilter.getApiAuth().getApiUser();
		OpenMember om = memberService.getOpenMemberByLoginname(partner.getPartnerkey(), userid);
		if(om==null){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,  "�û�������");
		}
		String qry = "from TicketOrder t where t.memberid=? and t.addtime>? order by t.addtime desc";
		if(maxnum==null) maxnum = 20;
		if(maxnum > 20) maxnum = 20;
		Timestamp curtime = DateUtil.getCurFullTimestamp();
		List<TicketOrder> orderList = daoService.queryByRowsRange(qry, from, maxnum, om.getMemberid(), DateUtil.addDay(curtime, -180));
		List<Map<String, Object>> resMapList = new ArrayList<Map<String, Object>>();
		for(TicketOrder order : orderList){
			Map<String, Object> resMap = GewaApiOrderHelper.getTicketOrderMap(order);
			Cinema cinema = daoService.getObject(Cinema.class, order.getCinemaid());
			resMap.put("cinemaname", cinema.getName());
			resMap.put("cityname", cinema.getCityname());
			resMapList.add(resMap);
		}
		return getOpenApiXmlList(resMapList, "orderList,order", model, request);
	}
	
	/**
	 * ֧��ʱ����ȡ�����Ż���Ϣ(����Żݻ�б�API)
	 */
	@RequestMapping("/openapi/partner/order/getDiscountList.xhtml")
	public String getDiscountList(String tradeNo,  String ukey, ModelMap model){
		if(StringUtils.isBlank(tradeNo)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		ApiAuth auth = OpenApiPartnerAuthenticationFilter.getApiAuth();
		ApiUser partner = auth.getApiUser();
		String opentype = SpecialDiscount.OPENTYPE_PARTNER;
		model.put("hasMobile", true);
		if (!partner.getId().equals(order.getPartnerid())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������������̵Ķ�����");
		if(!StringUtils.equals(order.getUkey(), ukey)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		model.put("order", order);
		model.put("cancelable", order.getStatus().equals(OrderConstant.STATUS_NEW) ? "1" : "0");
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
		List<SpecialDiscount> oldspList = (List<SpecialDiscount>)model.get("spdiscountList");
		ApiUserExtra extra = auth.getUserExtra();
		model.put("spdiscountList", GewaAppHelper.getDiscountList(oldspList, extra, getOpenMember(null)));
		return getXmlView(model, "inner/mobile/discountList.vm");
	}
	
	
	/**
	 * ȡ���Żݻ(ȡ���Ż�API)
	 */
	@RequestMapping("/openapi/partner/order/cancelDiscount.xhtml")
	public String cancelDisCount(String tradeNo,Long discountId, String ukey, ModelMap model){
		if(StringUtils.isBlank(tradeNo)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class,"tradeNo",tradeNo, false);
		ApiUser partner = OpenApiPartnerAuthenticationFilter.getApiAuth().getApiUser();
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		if (!partner.getId().equals(order.getPartnerid())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������������̵Ķ�����");
		if(!StringUtils.equals(order.getUkey(), ukey)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		ErrorCode<GewaOrder> code = ticketOrderService.removeDiscount(order, discountId);
		if(!code.isSuccess()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, code.getMsg());
		return getSuccessXmlView(model);
	}
	
	
	/**
	 * ֧����ʽչʾ(�ֻ��ͻ���֧����ʽ�б�API)
	 */
	@RequestMapping("/openapi/partner/order/showPayMethodList.xhtml")
	public String showPayMethodList(String tradeNo,Long discountId, String ukey, String appVersion, ModelMap model){
		if(StringUtils.isBlank(tradeNo)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class,"tradeNo",tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		ApiAuth auth = OpenApiPartnerAuthenticationFilter.getApiAuth();
		ApiUser partner = auth.getApiUser();
		String omCategory = null;
		if (!partner.getId().equals(order.getPartnerid())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������������̵Ķ�����");
		if(!StringUtils.equals(order.getUkey(), ukey)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
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
			String ip = partner.getPartnerip();
			ErrorCode<OrderContainer> discount = null;
				discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, order.getId(), sd, ip);
				if(discount.isSuccess()){
					OpenPlayItem opi = ((TicketOrderContainer)discount.getRetval()).getOpi();
					valHelp = new PayValidHelper(VmUtils.readJsonToMap(opi.getOtherinfo()));
				}
			if(discount!=null){
				if(!discount.isSuccess()) {
					return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, discount.getMsg());
				}
				order = discount.getRetval().getOrder();
			}
		}else {
			TicketOrder torder = (TicketOrder)order;
			OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", torder.getMpid(), false);
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
		if(StringUtils.isNotBlank(order.getPaybank())){ 
			orderPaymethod = orderPaymethod + ":" + order.getPaybank();
		}
		ApiUserExtra extra = auth.getUserExtra();
		Map<String, String> payMap = GewaAppHelper.getFilterMap(extra, discountList, omCategory, appVersion);
		model.put("payMethodMap", payMap);
		model.put("tradeno", order.getTradeNo());
		model.put("discountAmount", order.getDiscount());
		model.put("totalAmount", order.getTotalAmount());
		model.put("due", order.getDue());
		if(discountList.size()>0){
			model.put("relatedId", discountList.get(0).getRelatedid());
		}
		model.put("extra", extra);
		return getXmlView(model, "inner/mobile/showPayMethodList.vm");
	}
	
	private static final String METHOD_GET = "get";
	/**
	 * ѡ��֧����ʽ�����ض�Ӧ֧����ת���ӻ�����Ϣ(�ֻ��ͻ���֧����תAPI)
	 */
	@RequestMapping("/openapi/partner/order/selectPayMethod.xhtml")
	public String selectPayMethod(String tradeNo, String ukey, String payMethod, String apptype, String appVersion, 
			HttpServletRequest request, ModelMap model){
		ApiUser partner = OpenApiPartnerAuthenticationFilter.getApiAuth().getApiUser();
		if(StringUtils.isBlank(tradeNo)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if (!partner.getId().equals(order.getPartnerid())) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������������̵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		if(!StringUtils.equals(order.getUkey(), ukey)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ������˶�����");
		Map paramsData = new LinkedHashMap();
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
			String otherinfo = GewaAppHelper.getOrderOther(mainPaymethod, order, getOpenMember(null));
			order.setOtherinfo(otherinfo);
			order.setPaybank(paybank);
			order.setPaymethod(payMethod);
			order.setStatus(OrderConstant.STATUS_NEW_CONFIRM);
			orderMonitorService.addOrderChangeLog(order.getTradeNo(), "APIȥ֧��", order, payMethod + ",host=" + Config.getServerIp());
			daoService.saveObject(order);
			String version = GewaAppHelper.getChinaSmartPayVersion(order, apptype, order.getPaymethod(), appVersion);
			paymentService.usePayServer(order, partner.getPartnerip(), paramsData, version, request, model);
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
		CallbackOrder corder = partnerSynchService.addCallbackOrder(order, PayConstant.PUSH_FLAG_NEW, false);
		if(corder!=null) partnerSynchService.pushCallbackOrder(corder);
		return getXmlView(model, "inner/mobile/selectPayMethod.vm");
	}
}
