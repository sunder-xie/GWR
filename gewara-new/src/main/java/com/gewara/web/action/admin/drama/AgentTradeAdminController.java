package com.gewara.web.action.admin.drama;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.PayConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.model.drama.Drama;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.drama.Theatre;
import com.gewara.model.pay.BuyItem;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.OrderAddress;
import com.gewara.model.user.MemberUsefulAddress;
import com.gewara.pay.PayValidHelper;
import com.gewara.service.drama.DramaOrderService;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.service.ticket.TicketOrderService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.drama.TheatreOrderService;
import com.gewara.untrans.drama.impl.DramaControllerService;
import com.gewara.util.BeanUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.admin.BaseAdminController;

@Controller
public class AgentTradeAdminController extends BaseAdminController {

	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;
	
	@Autowired@Qualifier("theatreOrderService")
	private TheatreOrderService theatreOrderService;
	
	@Autowired@Qualifier("ticketOrderService")
	private TicketOrderService ticketOrderService;
	
	@Autowired@Qualifier("dramaOrderService")
	private DramaOrderService dramaOrderService;
	
	@Autowired@Qualifier("dramaControllerService")
	private DramaControllerService dramaControllerService;
	
	@RequestMapping("/admin/drama/agent/showOrder.xhtml")
	public String showOrder(Long orderId, String mobile, HttpServletRequest request, ModelMap model){
		DramaOrder order = daoService.getObject(DramaOrder.class, orderId);
		if(order == null) return showMessageAndReturn(model, request, "���������ڻ�ɾ����");
		if(order.isCancel()) return show404(model, "�����޸���֧�����ѣ���ʱ��ȡ���Ķ�����");
		if (order.isAllPaid()){
			model.put("tradeNo", order.getTradeNo());
			return showRedirect("/partner/orderResult.xhtml", model);
		}
		OpenDramaItem item = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", order.getDpid(), true);
		dramaControllerService.putDramaOrderData(order, item, model);
		model.put("showSearch", "0");
		model.put("mobile", mobile);
		model.put("dramaId", item.getDramaid());
		return "admin/drama/ticket/w_showOrder.vm";
	}

	@RequestMapping("/admin/drama/agent/confirmOrder.xhtml")
	public String confirmOrder(Long orderId, HttpServletRequest request, ModelMap model){
		DramaOrder order = daoService.getObject(DramaOrder.class, orderId);
		if(order == null) return showMessageAndReturn(model, request, "���������ڻ�ɾ����");
		if(order.isCancel()) return show404(model, "�����޸���֧�����ѣ���ʱ��ȡ���Ķ�����");
		if (order.isAllPaid()){
			model.put("tradeNo", order.getTradeNo());
			return showRedirect("/partner/orderResult.xhtml", model);
		}
		model.put("order", order);
		int otherfee = ticketOrderService.getUmpayfee(order);
		model.put("otherfee", otherfee);
		model.put("telecomOtherFee", (int)(Math.rint(order.getDue() * 1.4) - order.getDue()));
		OrderAddress address = daoService.getObject(OrderAddress.class, order.getTradeNo());
		model.put("address", address);
		PayValidHelper valHelp = new PayValidHelper();
		List<String> limitPayList = paymentService.getLimitPayList();
		OpenDramaItem item = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", order.getDpid(), true);
		DramaOrder dOrder = order;
		Map<String, String> otherinfoMap = new HashMap<String, String>();
		if(item.isOpenseat()){
			otherinfoMap = VmUtils.readJsonToMap(item.getOtherinfo());
		}else{
			List<BuyItem> buyList = daoService.getObjectListByField(BuyItem.class, "orderid", dOrder.getId());
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
							return showMessageAndReturn(model, request, "�˶���ֻ��ʹ��ȯ֧����");
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
		return "admin/drama/ticket/w_confirmOrder.vm";
	}
	
	@RequestMapping("/admin/drama/agent/saveAddress.xhtml")
	public String saveOrUpdateAddress(Long orderid, Long addressid, String realname, String mobile, String address, 
			String liveprovince, String liveprovinceName, String livecity, String livecityName, String livecounty, String livecountyName, HttpServletRequest request, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		GewaOrder order = daoService.getObject(GewaOrder.class, orderid);
		if(order== null || order.isTimeout()) return showJsonError(model, "���������ʱ��");
		String opkey = "saveAddress" + order.getMobile();
		boolean allow = operationService.isAllowOperation(opkey, 30, 60*30, 20);
		if(!allow){
			dbLogger.warn("save Address ip:" + ip + ", memberid:" + order.getMobile());
			return showJsonError(model, "��������Ƶ����");
		}
		Long memberid = null;
		if(StringUtils.isNotBlank(JsonUtils.getJsonValueByKey(order.getOtherinfo(), OrderConstant.OTHERKEY_BINDMEMBER))){
			memberid = order.getMemberid();
		}
		ErrorCode<MemberUsefulAddress> memAddress = memberService.saveMemberUsefulAddress(addressid, memberid, realname, liveprovince, liveprovinceName, 
				livecity, livecityName, livecounty, livecountyName, address, mobile, null, null);
		if(memAddress.isSuccess()){
			return showJsonSuccess(model, memAddress.getRetval().getId()+"");
		}else{
			return showJsonError(model, memAddress.getMsg());
		}
	}
}
