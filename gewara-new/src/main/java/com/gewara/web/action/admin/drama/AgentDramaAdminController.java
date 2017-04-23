package com.gewara.web.action.admin.drama;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.Config;
import com.gewara.command.TheatrePriceCommand;
import com.gewara.constant.AdminCityContant;
import com.gewara.constant.ChargeConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.Status;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.OrderExtraConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.helper.GoodsPriceHelper;
import com.gewara.helper.order.OrderOther;
import com.gewara.model.acl.User;
import com.gewara.model.api.ApiUser;
import com.gewara.model.drama.Drama;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.drama.TheatreProfile;
import com.gewara.model.drama.TheatreSeatArea;
import com.gewara.model.express.ExpressConfig;
import com.gewara.model.pay.BuyItem;
import com.gewara.model.pay.Charge;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.MemberAccount;
import com.gewara.model.pay.OrderAddress;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberUsefulAddress;
import com.gewara.pay.PayUtil;
import com.gewara.pay.PayValidHelper;
import com.gewara.service.MessageService;
import com.gewara.service.drama.DramaOrderService;
import com.gewara.service.drama.DramaPlayItemService;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.service.ticket.TicketOrderService;
import com.gewara.support.ErrorCode;
import com.gewara.support.VelocityTemplate;
import com.gewara.untrans.UntransService;
import com.gewara.untrans.drama.TheatreOrderService;
import com.gewara.untrans.drama.impl.DramaControllerService;
import com.gewara.untrans.monitor.OrderMonitorService;
import com.gewara.util.BeanUtil;
import com.gewara.util.ChangeEntry;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.admin.BaseAdminController;

@Controller
public class AgentDramaAdminController extends BaseAdminController {

	@Autowired@Qualifier("dramaControllerService")
	private DramaControllerService dramaControllerService;
	
	@Autowired@Qualifier("dramaPlayItemService")
	private DramaPlayItemService dramaPlayItemService;
	
	@Autowired@Qualifier("velocityTemplate")
	private VelocityTemplate velocityTemplate;
	
	@Autowired@Qualifier("dramaOrderService")
	private DramaOrderService dramaOrderService;
	
	@Autowired@Qualifier("theatreOrderService")
	private TheatreOrderService theatreOrderService;
	
	@Autowired@Qualifier("ticketOrderService")
	private TicketOrderService ticketOrderService;
	
	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;
	
	@Autowired@Qualifier("orderMonitorService")
	private OrderMonitorService orderMonitorService;
	
	@Autowired@Qualifier("config")
	private Config config;
	
	@Autowired@Qualifier("messageService")
	private MessageService messageService;
	
	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	
	@RequestMapping("/admin/drama/agent/dramamobile.xhtml")
	public String dramaMobile(){
		return "admin/order/mobileDramaOrder.vm";
	}
	
	@RequestMapping("/admin/drama/agent/dramaDetail.xhtml")
	public String dramaDetail(String mobile, ModelMap model, HttpServletRequest request) {
		if(StringUtils.isBlank(mobile)){
			return showMessageAndReturn(model, request, "�ֻ����벻��Ϊ�ջ����");
		}
		model.put("mobile", mobile);
		return "admin/drama/ticket/w_dramaDetail.vm";
	}
	
	@RequestMapping("/admin/drama/agent/matchMember.xhtml")
	public String matchMember(String mobile, ModelMap model) {
		Member member = memberService.getMemberByMobile(mobile);
		model.put("member", member);
		model.put("mobile", mobile);
		model.put("isMobile", ValidateUtil.isMobile(mobile));
		return "admin/drama/ticket/ajax/matchMember.vm";
	}
	
	@RequestMapping("/admin/drama/agent/getDramaList.xhtml")
	public String getDramaList(String dramanameOrId, ModelMap model) {
		if (StringUtils.isBlank(dramanameOrId)) {
			return showJsonError(model, "��ѯ��������Ϊ��!");
		}
		Drama drama = null;
		List<Drama> dramaList = new ArrayList<Drama>();
		try{
			Long dramaid = Long.parseLong(dramanameOrId);
			drama = daoService.getObject(Drama.class, dramaid);
		}catch(Exception e){}
		if(drama==null){
			dramaList = dramaPlayItemService.getDramaListByName(dramanameOrId);
		}else{
			dramaList.add(drama);
		}
		model.put("dramaList", dramaList);
		return "admin/drama/ticket/ajax/w_dramaList.vm";
	}
	
	@RequestMapping("/admin/drama/agent/getItemList.xhtml")
	public String getDramaPriceList(Long dramaid, Long fieldid, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		final boolean cache = false;
		ErrorCode<Map> code = dramaControllerService.getItemList(dramaid, fieldid, request, response, cache);
		if(!code.isSuccess()){
			return showJsonError(model, code.getMsg());
		}
		Map jsonMap = code.getRetval();
		model.putAll(jsonMap);
		final String viewPage = "admin/drama/ticket/ajax/dramaPlayItem.vm";
		return viewPage;
	}
	
	@RequestMapping("/admin/drama/agent/getDramaPrice.xhtml")
	public String getDramaPrice(@RequestParam("itemid")Long itemid, ModelMap model){
		OpenDramaItem odi = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", itemid, false);
		ErrorCode<Map> code = dramaControllerService.getPriceList(odi);
		if(!code.isSuccess()){
			return showJsonError(model, code.getMsg());
		}
		model.putAll(code.getRetval());
		if(odi.isOpenseat()){
			return "admin/drama/ticket/seatPrice.vm";
		}
		return "admin/drama/ticket/choosePrice.vm";
	}
	
	@RequestMapping("/admin/drama/agent/chooseSeat.shtml")
	public String chooseSeat(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("itemid")Long itemid, Long areaid, String mobile, ModelMap model){
		OpenDramaItem odi = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", itemid, false);
		ErrorCode code = dramaControllerService.addSeatData(model, odi, areaid, response, request, null);
		model.put("mobile", mobile);
		model.put("dramaId", odi.getDramaid());
		if(!code.isSuccess()) return showMessageAndReturn(model, request, code.getMsg());
		return "admin/drama/ticket/w_chooseSeat.vm";
	}
	
	@RequestMapping("/admin/drama/agent/setPrice.xhtml")
	public String setPrice(String pricelist, ModelMap model){
		if(!GoodsPriceHelper.isValidData(pricelist)) return showJsonError(model, "���λ�۸����");
		List<TheatrePriceCommand> commandList = new ArrayList<TheatrePriceCommand>();
		try{
			commandList = JsonUtils.readJsonToObjectList(TheatrePriceCommand.class, pricelist);
		}catch (Exception e) {
			return showJsonError(model, "���λ�۸����");
		}
		ErrorCode<Map> code = theatreOrderService.getTheatreSeatPriceInfo(commandList);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/admin/drama/agent/choosePrice.xhtml")
	public String choosePrice(String pricelist, String mobile, HttpServletRequest request, ModelMap model){
		if(!GoodsPriceHelper.isValidData(pricelist)) return showMessageAndReturn(model, request, "���λ�۸����");
		List<TheatrePriceCommand> commandList = new ArrayList<TheatrePriceCommand>();
		try{
			commandList = JsonUtils.readJsonToObjectList(TheatrePriceCommand.class, pricelist);
		}catch (Exception e) {
			return showMessageAndReturn(model, request, "���λ�۸����");
		}
		ErrorCode<Map> code = theatreOrderService.getTheatreSeatPriceInfo(commandList);
		if(!code.isSuccess()) return showMessageAndReturn(model, request, code.getMsg());
		model.putAll(code.getRetval());
		model.put("commandJson", JsonUtils.writeObjectToJson(commandList));
		model.put("mobile", mobile);
		return "admin/drama/ticket/w_chooseConfirmOrder.vm";
	}
	
	@RequestMapping("/admin/drama/agent/getSeatPage.xhtml")
	public String getSeatPage(Long areaid, ModelMap model){
		TheatreSeatArea seatArea = daoService.getObject(TheatreSeatArea.class, areaid);
		ErrorCode<Map> code = dramaControllerService.getSeatPage(seatArea);
		if(!code.isSuccess()){
			return showJsonError(model, code.getMsg());
		}
		Map model2 = code.getRetval();
		Map jsonMap = new HashMap();
		final String template = "admin/drama/wide_seatPage.vm";
		String seatPage = velocityTemplate.parseTemplate(template, model2);
		jsonMap.put("seatPage", seatPage);
		return showJsonSuccess(model, jsonMap);
	}
	
	@RequestMapping("/admin/drama/agent/mobileOrderBySeat.xhtml")
	public String mobileOrderBySeat(Long itemid, Long areaid, String mobile, String seatid, Long disid, String bind, String telephone, String bindmobile, String checkpass, ModelMap model){
		User user = getLogonUser();
		OpenDramaItem odi = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", itemid, false);
		TheatreSeatArea seatArea = daoService.getObject(TheatreSeatArea.class, areaid);
		if(seatArea == null) return showJsonError(model, "�����������ݲ����ڣ�");
		List<Long> seatidList = BeanUtil.getIdList(seatid, ",");
		if(!ValidateUtil.isMobile(mobile)) return showJsonError(model,"�ֻ����д���");
		if(seatidList.size()==0) return showJsonError(model, "��ѡ����λ��");
		ApiUser partner = daoService.getObject(ApiUser.class, PartnerConstant.GEWA_DRAMA_ADMIN_MOBILE);
		ErrorCode<DramaOrder> code = theatreOrderService.addDramaOrder(odi, seatArea, seatidList, disid, mobile, partner, user, telephone, Boolean.parseBoolean(bind), bindmobile, checkpass);
		if(!code.isSuccess()){
			return showJsonError(model, code.getMsg());
		}
		DramaOrder order = code.getRetval();
		return showJsonSuccess(model, order.getId() + "");
	}
	
	@RequestMapping("/admin/drama/agent/offlineOrderBySeat.xhtml")
	public String offlineOrderBySeat(Long itemid, Long areaid, String mobile, String seatid, Long disid, String bind, String telephone, String bindmobile, String checkpass, ModelMap model){
		User user = getLogonUser();
		OpenDramaItem odi = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", itemid, false);
		TheatreSeatArea seatArea = daoService.getObject(TheatreSeatArea.class, areaid);
		if(seatArea == null) return showJsonError(model, "�����������ݲ����ڣ�");
		List<Long> seatidList = BeanUtil.getIdList(seatid, ",");
		if(!ValidateUtil.isMobile(mobile)) return showJsonError(model,"�ֻ����д���");
		if(seatidList.size()==0) return showJsonError(model, "��ѡ����λ��");
		ApiUser partner = daoService.getObject(ApiUser.class, PartnerConstant.GEWA_DRAMA_ADMIN_OFFLINE);
		ErrorCode<DramaOrder> code = theatreOrderService.addDramaOrder(odi, seatArea, seatidList, disid, mobile, partner, user, telephone, Boolean.parseBoolean(bind), bindmobile, checkpass);
		if(!code.isSuccess()){
			return showJsonError(model, code.getMsg());
		}
		DramaOrder order = code.getRetval();
		return showJsonSuccess(model, order.getId() + "");
	}
	
	@RequestMapping("/admin/drama/agent/mobileOrderByPrice.xhtml")
	public String mobileOrderByPrice(String pricelist, String mobile, String origin, String bind, String telephone, String bindmobile, String checkpass, ModelMap model){
		User user = getLogonUser();
		ApiUser partner = daoService.getObject(ApiUser.class, PartnerConstant.GEWA_DRAMA_ADMIN_MOBILE);
		ErrorCode<DramaOrder> code = theatreOrderService.addDramaOrder(pricelist, mobile, partner, user, telephone, Boolean.parseBoolean(bind), bindmobile, checkpass);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		if(StringUtils.isNotBlank(origin)){
			ticketOrderService.addOrderOrigin(code.getRetval(), origin);
		}
		return showJsonSuccess(model, code.getRetval().getId() + "");
	}
	
	@RequestMapping("/admin/drama/agent/offlineOrderByPrice.xhtml")
	public String offlineOrderByPrice(String pricelist, String mobile, String origin, String bind, String telephone, String bindmobile, String checkpass, ModelMap model){
		User user = getLogonUser();
		ApiUser partner = daoService.getObject(ApiUser.class, PartnerConstant.GEWA_DRAMA_ADMIN_OFFLINE);
		ErrorCode<DramaOrder> code = theatreOrderService.addDramaOrder(pricelist, mobile, partner, user, telephone, Boolean.parseBoolean(bind), bindmobile, checkpass);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		if(StringUtils.isNotBlank(origin)){
			ticketOrderService.addOrderOrigin(code.getRetval(), origin);
		}
		return showJsonSuccess(model, code.getRetval().getId() + "");
	}
	
	@RequestMapping("/admin/drama/agent/saveOrderDis.xhtml")
	public String saveOrderInfo(Long orderId, Long addressRadio, String selectTicket, /*Integer amount, Integer gdiscount, Integer pdiscount, */ModelMap model){
		DramaOrder order = daoService.getObject(DramaOrder.class, orderId);
		if(order == null) return showJsonError(model, "��������!");
		if (order.isAllPaid() || order.isCancel()) return showJsonError(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		ChangeEntry changeEntry = new ChangeEntry(order);
		OpenDramaItem item = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", order.getDpid(), false);
		List<BuyItem> buyList = daoService.getObjectListByField(BuyItem.class, "orderid", order.getId());
		List<OpenDramaItem> itemList = dramaOrderService.getOpenDramaItemList(item, buyList);
		Map<Long, OpenDramaItem> odiMap = BeanUtil.beanListToMap(itemList, "dpid");	
		OrderOther orderOther = theatreOrderService.getDramaOrderOtherData(order, buyList, odiMap, model);
		if(!StringUtils.contains(orderOther.getTakemethod(), selectTicket)) return showJsonError(model, "��ѡ��ȡƱ��ʽ��");
		if(StringUtils.equals(selectTicket, TheatreProfile.TAKEMETHOD_E)){
			MemberUsefulAddress memberUsefulAddress = daoService.getObject(MemberUsefulAddress.class, addressRadio);
			if(memberUsefulAddress == null) return showJsonError(model, "����д��ݵ�ַ��");
			ExpressConfig expressConfig = daoService.getObject(ExpressConfig.class, orderOther.getExpressid());
			ErrorCode<OrderAddress> code = ticketOrderService.createOrderAddress(order, memberUsefulAddress, expressConfig);
			if(!code.isSuccess()){
				return showJsonError(model, code.getMsg());
			}
			ErrorCode<Integer> code2 = ticketOrderService.computeExpressFee(order, expressConfig, code.getRetval().getProvincecode());
			if(!code2.isSuccess()){
				return showJsonError(model, code2.getMsg());
			}
		}else if(orderOther.hasTakemethod(TheatreProfile.TAKEMETHOD_E, TheatreProfile.TAKEMETHOD_A)){
			ErrorCode<Integer> code2 = ticketOrderService.clearExpressFee(order);
			if(!code2.isSuccess()){
				return showJsonError(model, code2.getMsg());
			}
		}
		monitorService.saveChangeLog(getLogonUser().getId(), DramaOrder.class, order.getId(), changeEntry.getChangeMap(order));
		return showJsonSuccess(model);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/admin/drama/agent/saveOrder.xhtml")
	public String saveOrder(@RequestParam("orderId")long orderId, String paymethod, String chargeMethod, String checkGewaPay,
			String paypass, ModelMap model, HttpServletRequest request) {
		DramaOrder order = daoService.getObject(DramaOrder.class, orderId);
		if (order.isAllPaid() || order.isCancel()) return showJsonError(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		final boolean offlineFlag = PartnerConstant.GEWA_DRAMA_ADMIN_OFFLINE.equals(order.getPartnerid());
		String payinfo = null, fullPaymethod = null, paybank = null;
		if(!offlineFlag){
			if(StringUtils.isBlank(paymethod)) return showJsonError(model, "��ѡ��֧����ʽ��");
			fullPaymethod = paymethod;
			String[] pay = StringUtils.split(paymethod, ":");
			paymethod = pay[0];
			paybank = pay.length>1?pay[1]:null;
		}
		DramaOrder dorder = order;
		ErrorCode code = dramaOrderService.checkOrderSeat(dorder, model);
		if(!code.isSuccess()){
			dbLogger.error("���綩���д�" + order.getTradeNo() + code.getMsg());
			return showJsonError(model, code.getMsg());
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
			if(!offlineFlag && !pvh.supportPaymethod(fullPaymethod)){
				return showJsonError(model, "�����֧�ָ�֧����ʽ��");
			}
		}
		//��ȷ�ϵĶ������ٴ��޸ģ�����Ƿ��ܸ���֧����ʽ
		if(!offlineFlag && ((!StringUtils.equals(paymethod, order.getPaymethod()) || !StringUtils.equals(paybank, order.getPaybank())))){ 
			ErrorCode code1 = paymentService.isAllowChangePaymethod(order, paymethod, paybank);
			if(!code1.isSuccess()) return showJsonError(model, code1.getMsg());
		}
		order.setPaymethod(paymethod);
		if (order.isZeroPay()) {
			if(!offlineFlag){
				order.setPaymethod(PaymethodConstant.PAYMETHOD_GEWAPAY);
			}
			order.setPaybank(null);
		}else	{
			order.setPaybank(paybank);
		}
		order.setStatus(OrderConstant.STATUS_NEW_CONFIRM);
		Timestamp curtime = new Timestamp(System.currentTimeMillis());
		order.setUpdatetime(curtime);
		order.setModifytime(curtime);
		if(StringUtils.startsWith(paymethod, PaymethodConstant.PAYMETHOD_UMPAY)){
			ErrorCode<Integer> code1 = ticketOrderService.computeUmpayfee(order);
			if(!code1.isSuccess()) return showJsonError(model, code1.getMsg());
		}else if(StringUtils.equals(paymethod, PaymethodConstant.PAYMETHOD_TELECOM) || StringUtils.equals(paymethod, PaymethodConstant.PAYMETHOD_MOBILE_TELECOM)){
			if(!StringUtils.equals(order.getCitycode(), AdminCityContant.CITYCODE_SH)) {
				return showJsonError(model, "��֧���Ϻ������ֻ���̻�");
			}
		}
		daoService.saveObject(order);
		Map jsonReturn = new HashMap();
		jsonReturn.put("orderId", "" + order.getId());
		if(PaymethodConstant.PAYMETHOD_GEWAPAY.equals(order.getPaymethod()) && !order.isZeroPay()){
			Member member = daoService.getObject(Member.class, order.getMemberid());
			if(member == null) return showJsonError(model, "�û������ڣ�");
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
		
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "[�û�֧������]�û�:"+ order.getMemberid() +"������:"+order.getTradeNo()+",֧����ʽ:"+paymethod+"[IP:]"+WebUtils.getRemoteIp(request));
		return showJsonSuccess(model, jsonReturn);
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
	
	@RequestMapping(value = "/admin/drama/agent/saveExpressOrder.xhtml")
	public String saveExpressOrder(String orderExpressIdJson, ModelMap model) {
		Map<String, String> orderExpressMap = JsonUtils.readJsonToObject(Map.class, orderExpressIdJson);
		if(CollectionUtils.isEmpty(orderExpressMap)) {
			return showJsonError(model, "������ݲ���Ϊ��!");
		}
		User user = getLogonUser();
		for (String tradeNo : orderExpressMap.keySet()) {
			String expressNo = orderExpressMap.get(tradeNo);
			ErrorCode code = dramaOrderService.updateOrderExpress(tradeNo, expressNo, OrderExtraConstant.EXPRESS_STATUS_ALLOCATION, user, OrderExtraConstant.DEAL_TYPE_BACKEND);
			if (!code.isSuccess()) {
				return showJsonError(model, code.getMsg());
			}
		}
		return showJsonSuccess(model, "����ɹ�!");
	}
	
	@RequestMapping(value = "/admin/drama/agent/checkAndUpdateExpress.xhtml")
	public String checkAndUpdateExpress(String expressNo, ModelMap model) {
		if (StringUtils.isBlank(expressNo)) {
			return showJsonError(model, "��ݵ��Ų���Ϊ��!");
		}
		User user = getLogonUser();
		ErrorCode<List<GewaOrder>> code = dramaOrderService.checkAndUpdateExpress(expressNo, user, OrderExtraConstant.DEAL_TYPE_BACKEND);
		if (!code.isSuccess()) {
			return showJsonError(model, code.getMsg());
		}
		List<GewaOrder> orderList = code.getRetval();
		for (GewaOrder order : orderList) {
			//�ǿ�ݶ���ֱ������
			if(!StringUtils.equals(order.getExpress(), Status.Y)){
				continue;
			}
			String content = "����Ҹ����������������̰ͼ���������㣻������ԣ������ò��������㣬����Ī��������룬ֻ�����" + order.getOrdertitle() + "�����ˣ�Բͨ" + order.getTradeNo() + "��ע�����Ŷ��";
			SMSRecord sms = messageService.addManualMsg(order.getMemberid(), order.getMobile(), content, order.getTradeNo() + "express");
			if(sms != null){
				untransService.sendMsgAtServer(sms, true);
			}
		}
		return showJsonSuccess(model, "״̬���³ɹ�!");
	}
	
	@RequestMapping(value = "/admin/drama/agent/expressList.xhtml")
	public String expressList() {
		return "admin/order/expressList.vm";
	}
	
	@RequestMapping("/admin/drama/agent/saveGreetings.xhtml")
	public String saveGreetings(Long orderId, String greeting, ModelMap model){
		DramaOrder order = daoService.getObject(DramaOrder.class, orderId);
		if(order == null) return showJsonError(model, "��������");
		ErrorCode code = theatreOrderService.updateOtherInfo(order, greeting, getLogonUser().getId(), model);
		if(!code.isSuccess()){
			return showJsonError(model, code.getMsg());
		}
		return showJsonSuccess(model);
	}
}
