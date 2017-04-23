package com.gewara.web.action.ajax;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.PayConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.helper.order.OrderContainer;
import com.gewara.model.drama.TheatreProfile;
import com.gewara.model.express.ExpressConfig;
import com.gewara.model.goods.BaseGoods;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.OrderAddress;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberUsefulAddress;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.service.order.GoodsOrderService;
import com.gewara.service.ticket.TicketDiscountService;
import com.gewara.service.ticket.TicketOrderService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.ticket.SpecialDiscountService;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;

/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since Apr 14, 2008 AT 6:32:00 PM
 */
@Controller
public class TradeAjaxController extends AnnotationController {
	@Autowired@Qualifier("specialDiscountService")
	private SpecialDiscountService specialDiscountService;
	@Autowired@Qualifier("ticketOrderService")
	private TicketOrderService ticketOrderService;
	public void setTicketOrderService(TicketOrderService ticketOrderService) {
		this.ticketOrderService = ticketOrderService;
	}	

	@Autowired@Qualifier("goodsOrderService")
	private GoodsOrderService goodsOrderService;
	
	@Autowired@Qualifier("ticketDiscountService")
	private TicketDiscountService ticketDiscountService;
	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;

	@RequestMapping("/ajax/trade/removeDiscount.xhtml")
	public String removeDiscount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long discountId, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(!member.getId().equals(order.getMemberid())) return showJsonError(model, "�����޸����˶�����");
		ErrorCode<GewaOrder> code = ticketOrderService.removeDiscount(order, discountId);
		if(code.isSuccess()) {
			Map jsonMap = new HashMap();
			jsonMap.put("due", code.getRetval().getDue());
			jsonMap.put("totalDiscount", code.getRetval().getDiscount());
			return showJsonSuccess(model, jsonMap);
		}
		return showJsonError(model, code.getMsg());
	}
	@RequestMapping("/ajax/trade/partnerRemoveDiscount.xhtml")
	public String partnerRemoveDiscount(Long orderId, Long discountId, ModelMap model){
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		ErrorCode code = ticketOrderService.removeDiscount(order, discountId);
		if(code.isSuccess()) return showJsonSuccess(model);
		return showJsonError(model, code.getMsg());
	}
	@RequestMapping("/ajax/trade/saveOrderInfo.xhtml")
	public String saveOrderDiscount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, String selectTicket, Long addressRadio,
			HttpServletRequest request, Long orderId, String discounttype, Integer usepoint,String reusepoint, String expressid, ModelMap model){
		TicketOrder order = daoService.getObject(TicketOrder.class, orderId);
		if(order==null) return showJsonError(model, "�˶��������ڣ�");
		if(StringUtils.equals(selectTicket, TheatreProfile.TAKEMETHOD_E)){
			MemberUsefulAddress memberUsefulAddress = daoService.getObject(MemberUsefulAddress.class, addressRadio);
			if(memberUsefulAddress == null) return showJsonError(model, "����д��ݵ�ַ��");
			ExpressConfig expressConfig = daoService.getObject(ExpressConfig.class, expressid);
			ErrorCode<OrderAddress> code = ticketOrderService.createOrderAddress(order, memberUsefulAddress, expressConfig);
			if(!code.isSuccess()){
				return showJsonError(model, code.getMsg());
			}
			ErrorCode<Integer> code2 = ticketOrderService.computeExpressFee(order, expressConfig, code.getRetval().getProvincecode());
			if(!code2.isSuccess()){
				return showJsonError(model, code2.getMsg());
			}
		}else if(StringUtils.equals(selectTicket, TheatreProfile.TAKEMETHOD_A)){
			ErrorCode<Integer> code2 = ticketOrderService.clearExpressFee(order);
			if(!code2.isSuccess()){
				return showJsonError(model, code2.getMsg());
			}
		}
		List<Discount> discountList = null;
		if(StringUtils.equals(order.getPaymethod(), PaymethodConstant.PAYMETHOD_ELECARDPAY)){
			Map<String, String> otherinfoMap = VmUtils.readJsonToMap(order.getOtherinfo());
			if(StringUtils.isNotBlank(otherinfoMap.get(PayConstant.KEY_CARDBINDPAY)) && order.getDiscount() >0
					|| StringUtils.isNotBlank(otherinfoMap.get(PayConstant.KEY_CHANGECOST))){
				if(order.getDue() > 0) {
					//BȯҪ�����ʹ�õ�����Ҫ����λ����һ��
					discountList = paymentService.getOrderDiscountList(order);
					if(discountList.size() < order.getQuantity()){
						return showJsonError(model, "�˶���ֻ��ʹ��ȯ֧����");
					}
				}
			}
		}
		if(StringUtils.equals(discounttype, "point")){//ʹ�û���
			if(usepoint == null) {
				 if(StringUtils.isBlank(reusepoint)) return showJsonError(model, "���ֲ���ȷ!");
			}else{
				String ip = WebUtils.getRemoteIp(request);
				Member member = loginService.getLogonMemberBySessid(ip, sessid);
				if(member == null) return showJsonError(model, "���ȵ�¼��");
				ErrorCode code = ticketDiscountService.usePoint(orderId, member.getId(), usepoint);
				if(code.isSuccess()) return showJsonSuccess(model);
				return showJsonError(model, code.getMsg());
			}
		}else if(StringUtils.equals(discounttype, "card")){//ʹ��ȯ
			boolean usecard = false;
			if(discountList == null) {
				discountList = paymentService.getOrderDiscountList(order);
			}
			for(Discount discount: discountList){
				if("ABCD".indexOf(discount.getCardtype())>=0) usecard = true;
			}
			if(!usecard) return showJsonError(model, "��ѡ���˵�ӰƱȯ�Żݣ���δʹ���κ�Ʊȯ!");
		}else if(StringUtils.equals(discounttype, "none")){
			if(order.getDiscount() > 0) return showJsonError(model, "��ѡ���˲�ʹ���Żݣ���������ʹ���������Ż�!");
		}else if(StringUtils.isNotBlank(discounttype)){//ʹ���ؼۻ
			Long spid = Long.parseLong(discounttype);
			SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
			if(StringUtils.isNotBlank(sd.getValidateUrl())){
				Map jsonMap = new HashMap<String, String>();
				jsonMap.put("url", sd.getValidateUrl() + "?orderId=" + order.getId() + "&spid=" + sd.getId());
				return showJsonSuccess(model, jsonMap);
			}else if(StringUtils.isNotBlank(sd.getVerifyType())){
				Map jsonMap = new HashMap<String, String>();
				jsonMap.put("url", "gewapay/useSpcode.xhtml?orderId=" + order.getId() + "&spid=" + sd.getId());
				return showJsonSuccess(model, jsonMap);
			}
			ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(order.getOrdertype(), orderId, sd, WebUtils.getRemoteIp(request));
			if(discount.isSuccess()) return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
			return showJsonError(model, discount.getMsg());
		}
		return showJsonSuccess(model);
	}
	
	
	@RequestMapping("/ajax/trade/useGoodsDiscount.xhtml")
	public String useGoodsDiscount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, String discounttype, Integer usepoint, String reusepoint, Long addressRadio, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GoodsOrder order = daoService.getObject(GoodsOrder.class, orderId);
		BaseGoods goods = daoService.getObject(BaseGoods.class, order.getGoodsid());
		//TODO:�Ӵ˴������ȥ
		String expressid = goodsOrderService.getExpressid(order);
		if(StringUtils.isNotBlank(expressid)){
			MemberUsefulAddress memberUsefulAddress = daoService.getObject(MemberUsefulAddress.class, addressRadio);
			if(memberUsefulAddress == null) return showJsonError(model, "����д��ݵ�ַ��");
			ExpressConfig expressConfig = daoService.getObject(ExpressConfig.class, goods.getExpressid());
			ErrorCode<OrderAddress> code = ticketOrderService.createOrderAddress(order, memberUsefulAddress, expressConfig);
			if(!code.isSuccess()){
				return showJsonError(model, code.getMsg());
			}
			ErrorCode<Integer> code2 = ticketOrderService.computeExpressFee(order, expressConfig, code.getRetval().getProvincecode());
			if(!code2.isSuccess()){
				return showJsonError(model, code2.getMsg());
			}
		}
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		if(StringUtils.equals(discounttype, "point")){//ʹ�û���
			if(usepoint == null) {
				 if(StringUtils.isBlank(reusepoint)) return showJsonError(model, "���ֲ���ȷ!");
			}else{
				ErrorCode code = goodsOrderService.usePoint(orderId, member.getId(), usepoint);
				if(code.isSuccess()) return showJsonSuccess(model);
				return showJsonError(model, code.getMsg());
			}
		}else if(StringUtils.equals(discounttype, "card")){//ʹ��ȯ
			boolean usecard = false;
			for(Discount discount: discountList){
				if("ABCD".indexOf(discount.getCardtype())>=0) usecard = true;
			}
			if(!usecard) return showJsonError(model, "��ѡ����Ʊȯ�Żݣ���δʹ���κ�Ʊȯ!");
		}else if(StringUtils.equals(discounttype, "none")){
			if(order.getDiscount() > 0) return showJsonError(model, "��ѡ���˲�ʹ���Żݣ���������ʹ���������Ż�!");
		}else if(StringUtils.isNotBlank(discounttype)){
			Long spid = Long.parseLong(discounttype);
			SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
			ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_GOODS, orderId, sd, ip);
			if(discount.isSuccess()) return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
			return showJsonError(model, discount.getMsg());
		}
		return showJsonSuccess(model);
	}
	
	
	@RequestMapping("/ajax/trade/partnerUseSpecialDiscount.xhtml")
	public String partnerUseSpecialDiscount(HttpServletRequest request, Long orderId, Long spid, String ordertype, ModelMap model){
		if(StringUtils.isBlank(ordertype)){
			//TODO:��֤ǰ̨һ��������
			ordertype = OrderConstant.ORDER_TYPE_TICKET;
		}
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(ordertype, orderId, sd, WebUtils.getRemoteIp(request));
		if(discount.isSuccess()) return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
		return showJsonError(model, discount.getMsg());
	}

	@RequestMapping("/ajax/trade/useTrainingGoodsDiscount.xhtml")
	public String useTrainingGoodsDiscount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, String discounttype, Integer usepoint, String reusepoint, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GoodsOrder order = daoService.getObject(GoodsOrder.class, orderId);
		if(StringUtils.equals(discounttype, "point")){//ʹ�û���
			if(usepoint == null) {
				 if(StringUtils.isBlank(reusepoint)) return showJsonError(model, "���ֲ���ȷ!");
			}else{
				ErrorCode code = goodsOrderService.usePoint(orderId, member.getId(), usepoint);
				if(code.isSuccess()) return showJsonSuccess(model);
				return showJsonError(model, code.getMsg());
			}
		}else if(StringUtils.equals(discounttype, "card")){//ʹ��ȯ
			List<Discount> discountList = paymentService.getOrderDiscountList(order);
			boolean usecard = false;
			for(Discount discount: discountList){
				if("ABCD".indexOf(discount.getCardtype())>=0) usecard = true;
			}
			if(!usecard) return showJsonError(model, "��ѡ����Ʊȯ�Żݣ���δʹ���κ�Ʊȯ!");
		}else if(StringUtils.equals(discounttype, "none")){
			if(order.getDiscount() > 0) return showJsonError(model, "��ѡ���˲�ʹ���Żݣ���������ʹ���������Ż�!");
		}else if(StringUtils.isNotBlank(discounttype)){
			Long spid = Long.parseLong(discounttype);
			SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
			ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_GOODS, orderId, sd, ip);
			if(discount.isSuccess()) return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
			return showJsonError(model, discount.getMsg());
		}
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/ajax/trade/validOrder.xhtml")
	public String validOrder(Long orderId, ModelMap model){
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null){
			return showJsonError(model, "���������ڣ�");
		}
		if(order.isAllPaid()) return showJsonSuccess(model);
		return showJsonError(model, "֧��ʧ�ܣ�");
	}
}
