package com.gewara.web.action.ajax;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.OdiConstant;
import com.gewara.constant.Status;
import com.gewara.constant.ticket.OpiConstant;
import com.gewara.helper.order.OrderOther;
import com.gewara.model.bbs.Survey;
import com.gewara.model.bbs.SurveyItem;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.drama.TheatreProfile;
import com.gewara.model.express.ExpressConfig;
import com.gewara.model.express.ExpressProvince;
import com.gewara.model.goods.BaseGoods;
import com.gewara.model.goods.TicketGoods;
import com.gewara.model.pay.BuyItem;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.PubSale;
import com.gewara.model.pay.PubSaleOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberUsefulAddress;
import com.gewara.service.OperationService;
import com.gewara.service.bbs.SurveyService;
import com.gewara.service.drama.DramaOrderService;
import com.gewara.service.order.GoodsOrderService;
import com.gewara.service.ticket.TicketOrderService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.drama.TheatreOrderService;
import com.gewara.untrans.impl.ControllerService;
import com.gewara.util.BeanUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;

/**
 * �����ܱ�������Ϣ
 * @author gebiao(ge.biao@gewara.com)
 * @since Nov 28, 2013 5:22:42 PM
 */
@Controller
public class OrderInfoController extends AnnotationController{
	@Autowired@Qualifier("dramaOrderService")
	private DramaOrderService dramaOrderService;
	
	@Autowired@Qualifier("theatreOrderService")
	private TheatreOrderService theatreOrderService;

	@Autowired@Qualifier("goodsOrderService")
	private GoodsOrderService goodsOrderService;
	
	@Autowired@Qualifier("ticketOrderService")
	private TicketOrderService ticketOrderService;

	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}

	@Autowired@Qualifier("surveyService")
	private SurveyService surveyService;
	public void setSurveyService(SurveyService dramaOrderService) {
		this.surveyService = dramaOrderService;
	}
	@Autowired@Qualifier("controllerService")
	private ControllerService controllerService;
	public void setControllerService(ControllerService controllerService) {
		this.controllerService = controllerService;
	}

	@RequestMapping("/ajax/trade/getExpressFee.xhtml")
	public String getExpressFee(Long orderId, String provincecode, ModelMap model){
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) return showJsonError(model, "���������ڻ�ɾ����");
		String expressid = "";
		if(order instanceof TicketOrder){
			TicketOrder tOrder = (TicketOrder)order;
			OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", tOrder.getMpid(), true);
			Map<String, String> otherinfo = VmUtils.readJsonToMap(opi.getOtherinfo());
			if(StringUtils.equals("Y", otherinfo.get(OpiConstant.ADDRESS))){
				expressid = opi.getExpressid();
//				return showJsonSuccess(model,"0");
			}else{
				return showJsonError(model, "�ö�����֧�ֿ�ݣ�");
			}
		}else if(order instanceof DramaOrder){
			DramaOrder dorder = (DramaOrder) order;
			OpenDramaItem odi = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", dorder.getDpid());
			List<BuyItem> buyList = daoService.getObjectListByField(BuyItem.class, "orderid", order.getId());
			List<OpenDramaItem> itemList = dramaOrderService.getOpenDramaItemList(odi, buyList);
			Map<Long, OpenDramaItem> odiMap = BeanUtil.beanListToMap(itemList, "dpid");
			OrderOther orderOther = theatreOrderService.getDramaOrderOtherData(dorder, buyList, odiMap, model);
			if(!StringUtils.contains(orderOther.getTakemethod(), TheatreProfile.TAKEMETHOD_E) || !orderOther.isExpress()){
				return showJsonError(model, "�ö�����֧�ֿ�ݣ�");
			}
			expressid = orderOther.getExpressid();
		}else if(order instanceof GoodsOrder){
			GoodsOrder goodsOrder = (GoodsOrder)order;
			BaseGoods goods = daoService.getObject(BaseGoods.class, goodsOrder.getGoodsid());
			if(goods instanceof TicketGoods){
				expressid = goodsOrderService.getExpressid(goodsOrder);
			}else{
				expressid = (String) BeanUtil.get(goods, "expressid");
			}
			if(StringUtils.isBlank(expressid)) return showJsonError(model, "�ö�����֧�ֿ�ݣ�");
		}
		ExpressConfig expressConfig = daoService.getObject(ExpressConfig.class, expressid);
		ErrorCode<ExpressProvince> code = ticketOrderService.getExpressFee(expressConfig, provincecode);
		if(code.isSuccess()) return showJsonSuccess(model, code.getRetval().getExpressfee()+"");
		return showJsonError(model, code.getMsg());
	}
	@RequestMapping("/gewapay/validEms.xhtml")
	public String emsOrder(@RequestParam("orderId")long orderId, HttpServletRequest request, ModelMap model){
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		String address = request.getParameter("address");
		String realname = request.getParameter("realname");
		boolean isAdd = false;
		if(order instanceof DramaOrder) {
			DramaOrder dorder = (DramaOrder)order;
			OpenDramaItem item = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", dorder.getDpid(), false);
			if(TheatreProfile.TAKEMETHOD_E.equals(item.getTakemethod()) && OdiConstant.OPEN_TYPE_SEAT.equals(item.getOpentype())){
				String result = getEmsValid(realname, address);
				if(StringUtils.isNotBlank(result)) return showJsonError(model, result);
				else isAdd=true;
			}
		}else if(order instanceof PubSaleOrder){
			PubSaleOrder porder = (PubSaleOrder)order;
			PubSale sale = daoService.getObject(PubSale.class, porder.getPubid());
			if(sale.isGoods()) {
				String result = getEmsValid(realname, address);
				if(StringUtils.isNotBlank(result)) return showJsonError(model, result);
				else isAdd = true;
			}
		}
		if(isAdd) {
			String remark = request.getParameter("remark");
			String mobile = request.getParameter("mobile");
			Map<String, String> descMap = VmUtils.readJsonToMap(order.getDescription2());
			descMap.put("��ʵ����", realname);
			descMap.put("��ϸ��ַ", address);
			if(StringUtils.isNotBlank(remark)) descMap.put("��ע", remark);
			descMap.put("��ϵ�ֻ�", mobile);
			order.setDescription2(JsonUtils.writeMapToJson(descMap));
			daoService.saveObject(order);
		}
		return showJsonSuccess(model);
	}
	private String getEmsValid(String realname, String address){
		if(StringUtils.isBlank(realname) || StringUtils.isBlank(address)){
			return  "��ʵ�������ջ���ַ������д����";
		}
		return "";
	}
		
	@RequestMapping("/gewapay/delOldAddress.xhtml")
	public String delOldAddress(Long orderid, @CookieValue(LOGIN_COOKIE_NAME)String sessid, HttpServletRequest request, ModelMap model){
		//TODO:����ajax.....?
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		
		MemberUsefulAddress memAddress = daoService.getObject(MemberUsefulAddress.class, orderid);
		if(memAddress != null){
			if(!member.getId().equals(memAddress.getMemberid())){
				return showJsonError(model, "����ɾ�����˵ĵ�ַ��");
			}
			daoService.removeObject(memAddress);
			return showJsonSuccess(model);
		}
		return showJsonError(model, "ɾ��ʧ�ܣ�");
	}
	
	@RequestMapping("/ajax/saveAddress.xhtml")
	public String saveOrUpdateAddress(Long orderid, @CookieValue(LOGIN_COOKIE_NAME)String sessid, Long addressid, String realname, String mobile, String address, 
			String liveprovince, String liveprovinceName, String livecity, String livecityName, String livecounty, String livecountyName, HttpServletRequest request, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GewaOrder order = daoService.getObject(GewaOrder.class, orderid);
		if(order== null || order.isTimeout()) return showJsonError(model, "���������ʱ��");
		String opkey = "saveAddress" + member.getId();
		boolean allow = operationService.isAllowOperation(opkey, 30, 60*30, 20);
		if(!allow){
			dbLogger.warn("save Address ip:" + ip + ", memberid:" + member.getId());
			return showJsonError(model, "��������Ƶ����");
		}
		ErrorCode<MemberUsefulAddress> memAddress = memberService.saveMemberUsefulAddress(addressid, member.getId(), realname, liveprovince, liveprovinceName, 
				livecity, livecityName, livecounty, livecountyName, address, mobile, null, null);
		if(memAddress.isSuccess()){
			return showJsonSuccess(model, memAddress.getRetval().getId()+"");
		}else{
			return showJsonError(model, memAddress.getMsg());
		}
	}
	
	@RequestMapping("/ajax/trade/saveOrderAddress.xhtml")
	public String saveAddress(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, 
			Long orderId, Long addressRadio, HttpServletRequest request, ModelMap model){
		//TODO:��saveOrderInfo�з��룬��Ҫ�û���������
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		TicketOrder order = daoService.getObject(TicketOrder.class, orderId);
		OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", order.getMpid(), true);
		Map<String, String> otherinfo = VmUtils.readJsonToMap(opi.getOtherinfo());
		if(StringUtils.equals("Y", otherinfo.get(OpiConstant.ADDRESS))){
			MemberUsefulAddress memberUsefulAddress = daoService.getObject(MemberUsefulAddress.class, addressRadio);
			if(memberUsefulAddress == null) return showJsonError(model, "����д��ݵ�ַ��");
			
			Map<String, String> description2 = VmUtils.readJsonToMap(order.getDescription2());
			Map<String, String> orderOtherInfo = VmUtils.readJsonToMap(order.getOtherinfo());
			if(StringUtils.isNotBlank(memberUsefulAddress.getPostalcode())){
				orderOtherInfo.put("postalcode", memberUsefulAddress.getPostalcode());
				description2.put("postalcode", memberUsefulAddress.getPostalcode());
			}
			String address = memberUsefulAddress.getProvincename() + memberUsefulAddress.getCityname() + memberUsefulAddress.getCountyname() + memberUsefulAddress.getAddress();
			orderOtherInfo.put("address", address);
			orderOtherInfo.put("receiver", memberUsefulAddress.getRealname());
			order.setOtherinfo(JsonUtils.writeMapToJson(orderOtherInfo));
			description2.put("address", address);
			description2.put("receiver", memberUsefulAddress.getRealname());
			order.setDescription2(JsonUtils.writeMapToJson(description2));
			order.setExpress(Status.Y);
			daoService.saveObject(order);
		}
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/ajax/trade/saveGoodsOrderAddress.xhtml")
	public String saveGoodsOrderAddress(Long orderId,Long addressRadio,ModelMap model){
		//TODO:�������saveOrderAddress�ϲ�
		GoodsOrder order = daoService.getObject(GoodsOrder.class, orderId);
		Map<String, String> descriptionMap = VmUtils.readJsonToMap(order.getDescription2());
		MemberUsefulAddress memberUsefulAddress = daoService.getObject(MemberUsefulAddress.class, addressRadio);
		if(memberUsefulAddress==null) return showJsonError(model, "��ѡ���ռ��˵�ַ!");
		dbLogger.warn("orderid:" + orderId + ", addressRadio:" + addressRadio +", realaname:" + memberUsefulAddress.getRealname());
		descriptionMap.put("�ռ���", memberUsefulAddress.getRealname());
		String address = memberUsefulAddress.getProvincename() + memberUsefulAddress.getCityname() + memberUsefulAddress.getCountyname() + memberUsefulAddress.getAddress();
		descriptionMap.put("��ϸ��ַ", address);
		descriptionMap.put("�ʱ�", memberUsefulAddress.getPostalcode());
		String description = JsonUtils.writeObjectToJson(descriptionMap);
		order.setDescription2(description);
		order.setExpress(Status.Y);
		daoService.saveObject(order);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/ajax/trade/updateSurvey.xhtml")
	public String updateSurvey(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long surveyId, String surveyBody, String tag, String mark, String url, String captchaId, String captcha, ModelMap model) {
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		return orderSurvey(sessid, request, surveyId, surveyBody, tag, mark, url, model);
	}
	@RequestMapping("/ajax/trade/orderSurvey.xhtml")
	public String orderSurvey(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long surveyId, String surveyBody, String tag, String mark, String url, ModelMap model){
		if(StringUtils.isBlank(mark) || StringUtils.length(mark) <= 5) return showJsonError(model, "����ϸ������ķ�������(������5����)");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		String opkey = member.getId()+"_" + "survey";
		if(!operationService.updateOperation(opkey, 40)) return showJsonError(model, "���������Ƶ�������Ժ����ԣ�");
		if(surveyId != null) {
			Survey survey = daoService.getObject(Survey.class, surveyId);
			if(survey != null) {
				surveyService.updateSurveyResult(member.getId(), surveyId, tag, mark, url);				
			}
		}else {
			SurveyItem surveyItem = surveyService.getSurveyItemByBody(surveyBody);
			if(surveyItem != null) {
				surveyService.updateSurveyResult(member.getId(), surveyItem.getSurveyid(), tag, mark, url);
			}
		}
		return showJsonSuccess(model);
	}
	


}
