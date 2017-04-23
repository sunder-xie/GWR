package com.gewara.web.action.ajax;

import java.util.Arrays;
import java.util.Date;
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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.api.pay.request.ActivationQueryRequest;
import com.gewara.api.pay.request.GetBindParamsRequest;
import com.gewara.api.pay.request.SendPayRequest;
import com.gewara.api.pay.response.ActivationQueryResponse;
import com.gewara.api.pay.response.GetBindParamsResponse;
import com.gewara.api.pay.response.SendPayResponse;
import com.gewara.api.pay.service.UnionPayFastApiService;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.helper.order.JsonKeyOrderCallback;
import com.gewara.helper.order.OrderContainer;
import com.gewara.model.api.ApiUser;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.PayMerchant;
import com.gewara.model.pay.SpCode;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.user.Member;
import com.gewara.mongo.MongoService;
import com.gewara.pay.ChinapayUtil;
import com.gewara.pay.UnionpayFastUtil;
import com.gewara.service.pay.GatewayService;
import com.gewara.service.ticket.TicketDiscountService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.ticket.SpecialDiscountService;
import com.gewara.util.DateUtil;
import com.gewara.util.HttpResult;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.gewapay.BasePayController;
import com.gewara.web.action.partner.PartnerUtil;

@Controller
public class DiscountValidateController extends BasePayController {
	@Autowired@Qualifier("specialDiscountService")
	private SpecialDiscountService specialDiscountService;
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}
	@Autowired@Qualifier("gatewayService")
	private GatewayService gatewayService;
	
	@Autowired@Qualifier("unionPayFastApiService")
	private UnionPayFastApiService unionPayFastApiService;

	@Autowired@Qualifier("ticketDiscountService")
	private TicketDiscountService ticketDiscountService;
	@RequestMapping("/gewapay/useSpcode.xhtml")
	public String useSpcode(Long orderId, Long spid, ModelMap model){
		Member member = getLogonMember();
		SpecialDiscount spdiscount = daoService.getObject(SpecialDiscount.class, spid);
		List<SpCode> spcodeList = ticketDiscountService.getSpCodeList(member.getId(), spid, 0, 100);
		model.put("spdiscount", spdiscount);
		model.put("spcodeList", spcodeList);
		model.put("orderId", orderId);
		return "gewapay/useSpcode.vm";
	}
	@RequestMapping("/gewapay/addDiscountBySpcode.xhtml")
	public String useSpcode(HttpServletRequest request, Long orderId, String spcodePass, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = getLogonMember();
		ErrorCode<? extends OrderContainer> useResult = specialDiscountService.useSpecialDiscountBySpCodePass(OrderConstant.ORDER_TYPE_TICKET, orderId, ip, member.getId(), spcodePass);
		if(useResult.isSuccess()) {
			return showJsonSuccess(model, ""+useResult.getRetval().getCurUsedDiscount().getAmount());
		}
		return showJsonError(model, useResult.getMsg());
	}
	
	//~~~~~~~~~~~~~~~~~~���л~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	private static List<Integer> zgyhCardList = Arrays.asList(
			512315, 512316, 512411, 512412, 514957, 514958, 552741, 552742, 553131, 409665, 409666, 409667, 409668, 
			409669, 409670, 409671, 409672, 438088, 438089, 356833, 356835, 622760, 622788, 518377, 628388, 514958, 
			625905, 625906, 625907, 625908, 625909, 625910, 621725, 620513, 620514, 377677, 525746, 524865, 622761, 
			622759, 42410631, 42410731, 42410831, 42410931, 42411031, 42411131, 51837931, 51837831, 51847631, 51847531, 
			51847431, 54776631, 55886831, 62275231, 62275331, 62275531, 62275431, 62275631, 62275731, 62275831);
	@RequestMapping("/ajax/trade/bocDiscount.xhtml")//�й�����
	public String zgyhCardList(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, Integer preCardno, String postCardno, ModelMap model){
		if(StringUtils.length(postCardno)!=4 || !StringUtils.isNumeric(postCardno) || 
				!zgyhCardList.contains(preCardno) && !zgyhCardList.contains(preCardno/100)) 
			return showJsonError(model, "����Ŀ��Ų���ȷ");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<? extends OrderContainer> useResult = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, orderId, sd, new JsonKeyOrderCallback("boccardno", preCardno + "~" + postCardno), ip);
		if(useResult.isSuccess()) {
			return showJsonSuccess(model, ""+useResult.getRetval().getCurUsedDiscount().getAmount());
		}
		return showJsonError(model, useResult.getMsg());
	}
	@RequestMapping("/trade/chinapay/chinabankDiscount.xhtml")
	public String chinabankDiscount(@CookieValue(required=false,value="chinaPayUkey") String chinaPayUkey, 
			@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, ModelMap model){
		Map<String, String> reqParamMap = PartnerUtil.getParamMap(chinaPayUkey);
		String userId = reqParamMap.get("chinaPay_userId");
		if(isParticipateDiscount(userId)){
			model.put("sid", "61277562");
			model.put("msg", "�˻ÿ�������˻�ÿ��ֻ���Ż�һ�Σ�");
			return "redirect:/subject/qiangpiao.xhtml";
		}
		ApiUser apiUser = daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_UNION);
		String result = ChinapayUtil.isAllowDiscount(apiUser,userId);
		if(StringUtils.equals("success", result)){
			String ip = WebUtils.getRemoteIp(request);
			Member member = loginService.getLogonMemberBySessid(ip, sessid);
			if(member == null) return showJsonError(model, "���ȵ�¼��");
			SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
			ErrorCode<OrderContainer> useResult = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, orderId, sd, new JsonKeyOrderCallback("userId", userId), WebUtils.getRemoteIp(request));
			if(useResult.isSuccess()) {
				model.put("orderId", orderId);
				return "redirect:/gewapay/confirmOrder.xhtml";
			}else{
				model.put("sid", "61277562");
				model.put("msg", useResult.getMsg());
				return "redirect:/subject/qiangpiao.xhtml";
			}
		}
		model.put("sid", "61277562");
		model.put("msg", "�˻�������˺Ű����п������Ȱ󶨣�");
		return "redirect:/subject/qiangpiao.xhtml";
	}

	@RequestMapping("/trade/chinapay/validateDiscount.xhtml")
	@ResponseBody
	public String chinapayDiscount(Long orderid,HttpServletRequest request){
		String result = this.validateDiscount(orderid, request);
		dbLogger.warn("result:"+result);
		return result;
	}
	

	private boolean isParticipateDiscount(String userId){
		Map<String, Object> qparam = new HashMap<String, Object>();
		qparam.put("userId", userId);	
		qparam.put("status", "paid");
		qparam.put("addDate", DateUtil.formatDate(new Date()));
		List<Map> map = mongoService.find(MongoData.NS_CHINAPAY_ACTIVITY, qparam);
		return !map.isEmpty();
	}
	
	private String validateDiscount(Long orderid,HttpServletRequest request){
		dbLogger.warn("validateDiscount:"+WebUtils.getRequestMap(request).toString());
		ApiUser apiUser = daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_UNION);
		TicketOrder order = daoService.getObject(TicketOrder.class, orderid);
		String userId = VmUtils.getJsonValueByKey(order.getOtherinfo(), "userId");
		if(isParticipateDiscount(userId)){return "repeat:"+userId;}
		addParticipate(userId, orderid);
		return ChinapayUtil.isAllowDiscount(apiUser, userId);
	}
	
	private void addParticipate(String userId,Long orderid){
		Map<String, Object> toSave = new HashMap<String, Object>();
		toSave.put(MongoData.SYSTEM_ID, MongoData.buildId());
		toSave.put("userId", userId);
		toSave.put("orderid", orderid);
		toSave.put("status", "paid");
		toSave.put("type", "gewara"+DateUtil.formatTimestamp(DateUtil.getMillTimestamp()));
		toSave.put("addDate", DateUtil.formatDate(new Date()));
		mongoService.saveOrUpdateMap(toSave, MongoData.SYSTEM_ID, MongoData.NS_CHINAPAY_ACTIVITY, false, true);
	}

	//�й��������ÿ�֧���Ż�
	@RequestMapping("/gewapay/boc.xhtml")
	public String boc(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/bocpay.vm";
	}
	//��˹��֧���Ż�
	@RequestMapping("/gewapay/disney.xhtml")
	public String disney(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/disneypay.vm";
	}
	//�Ϻ�����֧���Ż�
	@RequestMapping("/gewapay/shbank.xhtml")
	public String shbank(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/shbankpay.vm";
	}
	//��ҵ����֧���Ż�
	@RequestMapping("/gewapay/xybank.xhtml")
	public String xybank(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/xybankpay.vm";
	}
	
	//��������֧���Ż�
	@RequestMapping("/gewapay/hxbank.xhtml")
	public String hxbank(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/hxbankpay.vm";
	}
	
	//����2.0�汾֧��ҳ��
	@RequestMapping("/gewapay/unionPayFast.xhtml")
	public String unionPay(Long orderId, String checkpass,ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null){
			 return show404(model, "���������ڣ�");
		}
		if(!StringUtil.md5(order.getId() + "&paymethod=" + order.getPaymethod()).equals(checkpass)){
			return show404(model, "�Ƿ���Դ���밴��ȷ�Ĳ���������У�");
		}
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		model.put("payName",mongoService.findOne(MongoData.NS_UNIONPAYFAST_CARDBIN + "Name:" + order.getPaybank(), "_id", "payName"));
		Map<String,String> otherInfoMap = VmUtils.readJsonToMap(order.getOtherinfo());
		String hasCardNumber = otherInfoMap.get("hasCardNumber");//֮ǰ�Ƿ������������
		if(StringUtils.endsWith(hasCardNumber, "Y")){
			String cardNumber = otherInfoMap.get("cardNumber");
			model.put("hasCardNumber", hasCardNumber);
			model.put("cardNumber", cardNumber);
			model.put("phoneNumber", otherInfoMap.get("phoneNumber"));
			model.put("validateCardStatus", otherInfoMap.get("validateCardStatus"));
		}
		return "gewapay/unionPayFast.vm";
	}
	
	//����2.0ȥ֧��
	@RequestMapping("/gewapay/toUnionpayFast.xhtml")
	public String toUnionPay(Long orderId,String smsCode,String cardNumber, HttpServletRequest request,ModelMap model) {
		if(StringUtils.isBlank(smsCode)){
			return show404(model, "��������֤�룡");
		}
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (order == null) return show404(model, "���������ڣ�");
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");

		Map<String, String> otherinfoMap = VmUtils.readJsonToMap(order.getOtherinfo());
		if(!StringUtils.equals(otherinfoMap.get("cardNumber"), cardNumber)){
			return this.showJsonError(model,"����Ƿ��۸�֧�����ţ�");
		}
		String ip = WebUtils.getRemoteIp(request);
		dbLogger.warn("the ip of the tradeNo " + order.getTradeNo() + " for toUnionPay is :" + ip);
		
		otherinfoMap.put("customerIp", ip);//��תʱip���ܱ��۸ģ���תǰ��ip����otherInfo��
		otherinfoMap.put("smsCode", smsCode);
		order.setOtherinfo(JsonUtils.writeMapToJson(otherinfoMap));		
		daoService.saveObject(order);

		Map jsonReturn = new HashMap();
		jsonReturn.put("url", paymentService.getOrderPayUrl2(order));
		jsonReturn.put("pay", order.getPaymethod());
		return this.showJsonSuccess(model,jsonReturn);
	}
	
	//����2.0֧��
	@RequestMapping("/gewapay/doUnionpayFast.xhtml")
	public String doUnionpay(HttpServletRequest request,ModelMap model){
		//TODO:���йؼ���
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, WebUtils.getParamStr(request, true));
		Map<String,String> params = WebUtils.getRequestMap(request);
		GewaOrder order = this.daoService.getObjectByUkey(GewaOrder.class, "tradeNo", params.get("orderNumber"), false);
		Map<String, String> otherinfoMap = VmUtils.readJsonToMap(order.getOtherinfo());
		model.put("order",order);
		params.put("customerIp", otherinfoMap.get("customerIp"));////��תʱip���ܱ��۸ģ���ת��ip��otherInfo��ȡ
		String response = doUnionpay(params);
		if(!StringUtils.equals("success|" + params.get("orderNumber"), response)){
			model.put("errorMsg", response);
			return "gewapay/unionPayFastError.vm";
		}
		String cardNum = otherinfoMap.get("cardNumber");
		
		if(gatewayService.isSwitch(PaymethodConstant.PAYMETHOD_UNIONPAYFAST)){
			//String merchantCode = order.getPaymethod();
			ErrorCode<PayMerchant> code = gatewayService.findMerchant(order.getCitycode(), PaymethodConstant.PAYMETHOD_UNIONPAYFAST);
			if(!code.isSuccess()){
				dbLogger.warn("TradeNo is " + order.getTradeNo() + " : " + code.getMsg());
				return showError(model, code.getMsg());
			}
			String merchantCode = code.getRetval().getMerchantCode();
			ActivationQueryRequest activationQueryRequest = new ActivationQueryRequest(merchantCode, cardNum);
			ActivationQueryResponse activationQueryResponse = unionPayFastApiService.activationQuery(activationQueryRequest);
			if(activationQueryResponse.isSuccess() && StringUtils.equals("1", activationQueryResponse.getActivateStatus())){
				model.put("cardNum",cardNum);
				if(activationQueryResponse.getTransLimit() != null){
					model.put("transLimit", activationQueryResponse.getTransLimit());
				}
				if(activationQueryResponse.getSumLimit() != null){
					model.put("sumLimit", activationQueryResponse.getSumLimit());
				}
				if(activationQueryResponse.getTransLimit() != null && activationQueryResponse.getSumLimit() != null && StringUtils.isNotBlank(activationQueryResponse.getExpiry())){
					model.put("cupReserved", activationQueryResponse.getCupReserved());
				}
			}
			
		}else{
			HttpResult cardStatus = UnionpayFastUtil.getCardActivateStatus(order.getPaymethod(),cardNum);
			if(cardStatus.isSuccess()){
				Map<String, String> responses = UnionpayFastUtil.parseUnionpayResponse(cardStatus.getResponse());
				if(StringUtils.equals("00",responses.get("respCode")) && StringUtils.equals("1",responses.get("activateStatus"))){
					Map<String,String> cupReserved = UnionpayFastUtil.parseUnionpayResponse(responses.get("cupReserved").replace("}", "").replace("{", ""));
					model.put("cardNum",cardNum);
					if(StringUtils.isNotBlank(cupReserved.get("transLimit"))){
						model.put("transLimit", Integer.parseInt(cupReserved.get("transLimit"))/100);
					}
					if(StringUtils.isNotBlank(cupReserved.get("sumLimit"))){
						model.put("sumLimit", Integer.parseInt(cupReserved.get("sumLimit"))/100);
					}
					
					if(StringUtils.isNotBlank(cupReserved.get("transLimit")) && StringUtils.isNotBlank(cupReserved.get("sumLimit")) && StringUtils.isNotBlank(cupReserved.get("expiry"))){
						model.put("cupReserved", cupReserved);
					}				
				}
			}
		}
		
		return "gewapay/unionPayFastResult.vm";
	}
	
	
	private String doUnionpay(Map<String,String> params){
		dbLogger.warnMap(LogTypeConstant.LOG_TYPE_ORDER_PAY, params);
		String tradeNo = null;
		if(gatewayService.isSwitch(PaymethodConstant.PAYMETHOD_UNIONPAYFAST)){
			SendPayRequest request = new SendPayRequest();
			request.setParams(params);
			SendPayResponse response = unionPayFastApiService.sendPay(request);
			if(!response.isSuccess()){
				return response.getMsg();
			}
			tradeNo = response.getTradeNo();
		}else{
			HttpResult result = UnionpayFastUtil.sendPay( params);
			if(!result.isSuccess()){
				return result.getMsg();
			}
			Map<String, String> responses = UnionpayFastUtil.parseUnionpayResponse(result.getResponse());
			if(!StringUtils.equals("00",responses.get("respCode"))){
				return responses.get("respMsg");
			}
			tradeNo = responses.get("orderNumber");
		}
		
		return "success|" + tradeNo;
	}
	
	@RequestMapping("/gewapay/toBindUnionpayFastCard.xhtml")
	public String bindUnionpayFastCard(String paymethod,String cardNumber,ModelMap model,HttpServletRequest request,HttpServletResponse response){
		if(StringUtils.isBlank(paymethod)){
			return showError(model, "������󣡣�");
		}
		if(StringUtils.isBlank(cardNumber) || cardNumber.length() > 19 || cardNumber.length() < 13){
			return showError(model, "��������ȷλ�������п��ţ�");
		}
		
		Map<String,String> params = null;
		String bindCardUrl = null;
		if(gatewayService.isSwitch(PaymethodConstant.PAYMETHOD_UNIONPAYFAST)){
			//����2.0�������ϵ��̻��ţ��̻���ʶ�����ϵ�֧����ʽά����һ��
			//String merchantCode = paymethod;
			String citycode = WebUtils.getAndSetDefault(request, response);
			ErrorCode<PayMerchant> code = gatewayService.findMerchant(citycode, PaymethodConstant.PAYMETHOD_UNIONPAYFAST);
			if(!code.isSuccess()){
				dbLogger.warn("cardNumber is " + cardNumber + " : " + code.getMsg());
				return showError(model, code.getMsg());
			}
			String merchantCode = code.getRetval().getMerchantCode();
			GetBindParamsRequest getBindParamsRequest = new GetBindParamsRequest(merchantCode, cardNumber);
			GetBindParamsResponse getBindParamsResponse = unionPayFastApiService.getBindParams(getBindParamsRequest);
			if(!getBindParamsResponse.isSuccess()){
				return showError(model, "��������󣡣�");
			}
			params = getBindParamsResponse.getParams();
			bindCardUrl = getBindParamsResponse.getBindCardUrl();
		}else{
			params = UnionpayFastUtil.getToBindCardParams(paymethod,cardNumber);
			bindCardUrl = UnionpayFastUtil.getBindCardUrl();
		}
		
		//Map<String,String> params = UnionpayFastUtil.getToBindCardParams(paymethod,cardNumber);
		model.put("method", "post");
		model.put("submitParams", params);
		model.put("submitUrl", bindCardUrl);
		return "tempSubmitForm.vm";
	}
}
