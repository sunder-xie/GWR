/**
 * 
 */
package com.gewara.web.action.gewapay;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.api.pay.request.ActivationQueryRequest;
import com.gewara.api.pay.request.SendSmsRequest;
import com.gewara.api.pay.response.ActivationQueryResponse;
import com.gewara.api.pay.response.SendSmsResponse;
import com.gewara.api.pay.service.UnionPayFastApiService;
import com.gewara.constant.PayConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.Status;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.helper.discount.SportSpecialDiscountHelper.OrderCallback;
import com.gewara.helper.order.JsonKeyOrderCallback;
import com.gewara.helper.order.OrderContainer;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.PayMerchant;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.user.Member;
import com.gewara.mongo.MongoService;
import com.gewara.pay.BackConstant;
import com.gewara.pay.NewPayUtil;
import com.gewara.pay.UnionpayFastUtil;
import com.gewara.service.pay.GatewayService;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CooperateService;
import com.gewara.untrans.ticket.SpecialDiscountService;
import com.gewara.util.CAUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.HttpResult;
import com.gewara.util.HttpUtils;
import com.gewara.util.JsonUtils;
import com.gewara.util.ValidateUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;

@Controller
public class CooperateController extends AnnotationController{
	@Autowired@Qualifier("specialDiscountService")
	private SpecialDiscountService specialDiscountService;
	@Autowired@Qualifier("cooperateService")
	private CooperateService cooperateService;
	public void setCooperateService(CooperateService cooperateService) {
		this.cooperateService = cooperateService;
	}
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}
	
	@Autowired@Qualifier("gatewayService")
	private GatewayService gatewayService;
	
	@Autowired@Qualifier("unionPayFastApiService")
	private UnionPayFastApiService unionPayFastApiService;
	
	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;
	
	@RequestMapping("/ajax/cooperate/shbankDiscount.xhtml")
	public String shbankDiscount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, String preCardno, String postCardno, ModelMap model){
		if(StringUtils.length(postCardno)!=4 || !StringUtils.isNumeric(postCardno)){
			return showJsonError(model, "����Ŀ��Ų���ȷ");
		}
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		ErrorCode<String> retCode = cooperateService.checkShbankCode(orderId, preCardno, postCardno);
		if(!retCode.isSuccess()) return showJsonError(model, retCode.getMsg());
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, orderId, sd, new JsonKeyOrderCallback(BackConstant.shbankcardno, preCardno + "~" + postCardno), ip);
		if(discount.isSuccess()) {
			return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
		}
		return showJsonError(model, discount.getMsg());
	}
	@RequestMapping("/ajax/cooperate/xybankDiscount.xhtml")
	public String xybankDiscount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, String preCardno, String postCardno, ModelMap model){
		if(StringUtils.length(postCardno)!=4 || !StringUtils.isNumeric(postCardno)){
			return showJsonError(model, "����Ŀ��Ų���ȷ");
		}
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		ErrorCode<String> retCode = cooperateService.checkXybankCode(orderId, preCardno, postCardno);
		if(!retCode.isSuccess()) return showJsonError(model, retCode.getMsg());
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, orderId, sd, new JsonKeyOrderCallback(BackConstant.xybankcardno, preCardno + "~" + postCardno), ip);
		if(discount.isSuccess()) {
			return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
		}
		return showJsonError(model, discount.getMsg());
	}
	@RequestMapping("/ajax/cooperate/hxbankDiscount.xhtml")
	public String hxbankDiscount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, String preCardno, String postCardno, ModelMap model){
		if(StringUtils.length(postCardno)!=4 || !StringUtils.isNumeric(postCardno)){
			return showJsonError(model, "����Ŀ��Ų���ȷ");
		}
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		ErrorCode<String> retCode = cooperateService.checkHxbankCode(orderId, preCardno, postCardno);
		if(!retCode.isSuccess()) return showJsonError(model, retCode.getMsg());
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, orderId, sd, new JsonKeyOrderCallback(BackConstant.hxbankcardno, preCardno + "~" + postCardno),  ip);
		if(discount.isSuccess()) {
			return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
		}
		return showJsonError(model, discount.getMsg());
	}
	
	@RequestMapping("/ajax/cooperate/queryOrderResult.xhtml")
	public String queryOrderResult(String paymethod,String tradeNo, ModelMap model){
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order != null && order.isAllPaid()){
			return showJsonSuccess(model,"00"); 
		}
		Map<String, String> paramMap = new LinkedHashMap<String, String>();
		paramMap.put("paymethod", paymethod);
		paramMap.put("tradeNo",tradeNo);
		String paramStr = JsonUtils.writeMapToJson(paramMap);
		String sign = CAUtil.doSign(paramStr, NewPayUtil.getMerprikey(), "utf-8", "SHA1WithRSA");
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("merid", NewPayUtil.getMerid());
		try {
			postMap.put("params", Base64.encodeBase64String(paramStr.getBytes("UTF-8")));
			postMap.put("sign", sign);
			
			String payurl = NewPayUtil.getPayurl();
			if(gatewayService.isSwitch(paymethod)){
				payurl = NewPayUtil.getNewPayurl();
			}
			HttpResult code = HttpUtils.postUrlAsString(payurl.replaceAll("getPayParams.xhtml", "queryOrderResult.xhtml"), postMap);
			if(code.isSuccess()){
				String res = new String(Base64.decodeBase64(code.getResponse()), "utf-8");
				Map<String, String> returnMap = VmUtils.readJsonToMap(res);
				Map<String,String> result = VmUtils.readJsonToMap(returnMap.get("submitParams"));
				if(StringUtils.equals(result.get("resultCode"), "00")){
					return showJsonSuccess(model,"00");
				}else if(StringUtils.equals(result.get("resultCode"), "0002")){
					return showJsonSuccess(model,result.get("responseMsg"));
				}
				return showJsonError(model,result.get("responseMsg"));
			}else{
				return this.showJsonError(model, "�����쳣�����Ժ���в鿴");
			}
		} catch (Exception e) {
			return showJsonError(model, "�����쳣�����Ժ���в鿴");
		}
	}
	
	//�÷��������ڲ����ؼۻ�����Բ�����Żݻ����ֱ��������鿨��
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount.xhtml")
	public String unionPay2Discount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, String cardNumber,  ModelMap model){
		if(orderId == null) return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		if(!StringUtils.equals(member.getId()+"", order.getMemberid()+"")){
			return showJsonError(model, "�Ƿ�������");
		}
		if(StringUtils.isBlank(cardNumber) || cardNumber.length() > 19 || cardNumber.length() < 13){
			return showJsonError(model, "��������ȷλ�������п��ţ�");
		}
		/***/
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		if(discountList != null && !discountList.isEmpty()){
			for(Discount discount: discountList){
				if(StringUtils.equals(PayConstant.DISCOUNT_TAG_PARTNER, discount.getTag())){
					ErrorCode<String> retCode = cooperateService.checkUnionPayFastCode(order,order.getPaybank(), cardNumber,discount.getRelatedid());
					if(!retCode.isSuccess()) {
						return showJsonError(model, retCode.getMsg());
					}
					break;
				}
			}
		}
		
		ErrorCode<Map> errorCode = validateCardNumber(order, order.getPaymethod(), cardNumber);
		if(!errorCode.isSuccess()){
			return showJsonError(model,errorCode.getMsg());
		}
		Map jsonMap = errorCode.getRetval();
		
		Map<String, String> other = JsonUtils.readJsonToMap(order.getOtherinfo());
		other.put("cardNumber", cardNumber);
		other.put("validateCardStatus", (String)jsonMap.get("retval"));
		other.put("phoneNumber", (String)jsonMap.get("phoneNumber"));
		order.setOtherinfo(JsonUtils.writeMapToJson(other));
		daoService.saveObject(order);
		
		return showJsonSuccess(model, jsonMap);
	}
	
	/**
	 * ����2.0��֤֧�����������֤�������ؼۻ��һЩ��֤
	 * 
	 * @param sessid
	 * @param request
	 * @param orderId
	 * @param cardNumber
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Mar 20, 2013 5:00:31 PM
	 */
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity.xhtml")
	public String unionPayFastDiscountActivity(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "ALL");
	}
	
	/**
	 * �����й����У�����2.0��֤֧�����������֤�������ؼۻ��һЩ��֤
	 * 
	 * @param sessid
	 * @param request
	 * @param orderId
	 * @param spid
	 * @param paymethod
	 * @param cardNumber
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 7, 2013 6:11:29 PM
	 */
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/sz.xhtml")
	public String unionPayFastDiscountActivitySZ(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "SZ");
	}
	
	
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/nyyh.xhtml")
	public String unionPayFastDiscountActivityNYYH(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "NYYH");
	}
	
	/**
	 * ����ũ���л����֤��bin��ÿ��ֻ�ܲμ�һ�λ
	 * 
	 * @param sessid
	 * @param request
	 * @param orderId
	 * @param spid
	 * @param paymethod
	 * @param cardNumber
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 28, 2013 6:00:32 PM
	 */
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/cqnsyh.xhtml")
	public String unionPayFastDiscountActivityCqnsyh(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "CQNSYH");
	}
	
	/**
	 * �������ѽڻ����������Ϊ62����ÿ��ֻ�ܲμ�һ�λ
	 * 
	 * @param sessid
	 * @param request
	 * @param orderId
	 * @param spid
	 * @param paymethod
	 * @param cardNumber
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 28, 2013 5:32:30 PM
	 */
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/youjie.xhtml")
	public String unionPayFastDiscountActivityYoujie(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "YOUJIE");
	}
	
	/**
	 * �����������ÿ����������Ϻ����̻��ţ���ÿ�������Ϊÿ��ÿ����ʹ��һ�Ρ�
	 * 
	 * @param sessid
	 * @param request
	 * @param orderId
	 * @param spid
	 * @param paymethod
	 * @param cardNumber
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Jun 9, 2013 11:47:59 AM
	 */
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/wzyh.xhtml")
	public String unionPayFastDiscountActivitywzcb(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "WZCB");
	}
	
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/zdyh.xhtml")
	public String unionPayFastDiscountActivityzdcb(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "ZDCB");
	}
	
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/srcb.xhtml")
	public String unionPayFastDiscountActivitysrcb(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "SRCB");
	}
	
	// ��������
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/psbc.xhtml")
	public String unionPayFastDiscountActivityPsbc(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "PSBC");
	}
	
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/common.xhtml")
	public String unionPayFastDiscountActivityCommon(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		if(orderId == null) return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		if(!StringUtils.equals(member.getId()+"", order.getMemberid()+"")){
			return showJsonError(model, "�Ƿ�������");
		}
		if(StringUtils.isBlank(paymethod)){
			return showJsonError(model, "��ѡ����ȷ��֧����ʽ��");
		}
		if(StringUtils.isBlank(cardNumber) || cardNumber.length() > 19 || cardNumber.length() < 13){
			return showJsonError(model, "��������ȷλ�������п��ţ�");
		}
		//���Ҫ��֤��bin
		ErrorCode<String> retCode = cooperateService.checkCommonCardbinOrCardNumLimit(order, spid, cardNumber);
		if(!retCode.isSuccess()) {
			return showJsonError(model, retCode.getMsg());
		}
		ErrorCode<Map> errorCode = validateCardNumber(order, PaymethodConstant.PAYMETHOD_UNIONPAYFAST, cardNumber);
		if(!errorCode.isSuccess()){
			return showJsonError(model,errorCode.getMsg());
		}
		Map jsonMap = errorCode.getRetval();
		final String validateCardStatus = (String) jsonMap.get("retval");	
		final String phoneNumber = (String) jsonMap.get("phoneNumber");
		String ip = WebUtils.getRemoteIp(request);
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		//specialDiscountService.useSpecialDiscount ����������޸��˶�����֧����ʽ��Ĭ����ѡ�Żݻ��ά����֧����ʽ�ĵ�һ��
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(order.getOrdertype(), orderId, sd, new OrderCallback(){
			@Override
			public void processOrder(SpecialDiscount sd2, GewaOrder gewaOrder) {
				Map<String, String> other = JsonUtils.readJsonToMap(gewaOrder.getOtherinfo());
				other.put("cardNumber", cardNumber);
				other.put("validateCardStatus", validateCardStatus);
				other.put("phoneNumber", phoneNumber);
				other.put("hasCardNumber", "Y");
				other.put("discountByPaymethod", paymethod);//��confirmOrder.vmҳ�����û�û��ѡ���֧����ʽ���˵���
				other.put(BackConstant.unionfastcardno, cardNumber);
				String[] pay = StringUtils.split(paymethod, ":");
				gewaOrder.setOtherinfo(JsonUtils.writeMapToJson(other));
				gewaOrder.setPaymethod(pay[0]);//��֧����ʽ�ĳ��û�ѡ���
				gewaOrder.setPaybank(pay.length > 1 ? pay[1] : null);
			}
			
		},ip);
		if(discount.isSuccess()) {
			jsonMap.put("amount",discount.getRetval().getCurUsedDiscount().getAmount());
			return showJsonSuccess(model, jsonMap);
		}		
		return showJsonError(model, discount.getMsg());
	}
	
	private String unionPayFastDiscountAJ(String sessid,HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model,String activeType){
		if(orderId == null) return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		if(!StringUtils.equals(member.getId()+"", order.getMemberid()+"")){
			return showJsonError(model, "�Ƿ�������");
		}
		if(StringUtils.isBlank(paymethod)){
			return showJsonError(model, "��ѡ����ȷ��֧����ʽ��");
		}

		if(StringUtils.isBlank(cardNumber) || cardNumber.length() > 19 || cardNumber.length() < 13){
			return showJsonError(model, "��������ȷλ�������п��ţ�");
		}
		
		String[] prik = StringUtils.split(paymethod,":");
		String paybank = prik.length > 1 ? prik[1] : null;
		
		
		//���Ҫ��֤��bin
		//TODO order.getPaybank()֧��ҳ����ǰ����ֵû�� 
		ErrorCode<String> retCode = null;
		if("SZ".equals(activeType)){
			retCode = cooperateService.checkUnionPayFastCodeForSZ(order,paybank, cardNumber,spid);
		}else if("CQNSYH".equals(activeType)){
			retCode = cooperateService.checkUnionPayFastCodeForCqnsyh(order,paybank, cardNumber,spid);
		}else if("YOUJIE".equals(activeType)){
			retCode = cooperateService.checkUnionPayFastCodeForYouJie(order,paybank, cardNumber,spid);
		}else if("NYYH".equals(activeType)){
			retCode = cooperateService.checkUnionPayFastCodeForNyyh(order,paybank, cardNumber,spid);
		}else if("WZCB".equals(activeType)){
			retCode = cooperateService.checkUnionPayFastCodeForWzcb(order,paybank, cardNumber,spid);
		}else if("ZDCB".equals(activeType)){
			retCode = cooperateService.checkUnionPayFastCodeForZdcb(order,paybank, cardNumber,spid);
		}else if("shenzhenPingAn".equals(activeType)){
			retCode = cooperateService.checkUnionPayFastShenZhenCodeForPingAn(order, paybank, cardNumber, spid);
		}else if("guangzhouBocWeekOne".equals(activeType)){
			retCode = cooperateService.checkUnionPayFastGuangzhouCodeForBocByWeekone(order, paybank, cardNumber, spid);
		}else if("guangzhouBocMonthTwo".equals(activeType)){
			retCode = cooperateService.checkUnionPayFastGuangzhouCodeForBocByMonthTwo(order, paybank, cardNumber, spid);
		}else if("SRCB".equals(activeType)){
			retCode = cooperateService.checkCommonCardbinOrCardNumLimit(order, spid, cardNumber);
		}else if("PSBC".equals(activeType)){
			retCode = cooperateService.checkCommonCardbinOrCardNumLimit(order, spid, cardNumber);
		}else{
			retCode = cooperateService.checkUnionPayFastCode(order,paybank, cardNumber,spid);
		}
		
		if(!retCode.isSuccess()) {
			return showJsonError(model, retCode.getMsg());
		}
		
		ErrorCode<Map> errorCode = validateCardNumber(order, PaymethodConstant.PAYMETHOD_UNIONPAYFAST, cardNumber);//�˴�ֻ����PaymethodConstant.PAYMETHOD_UNIONPAYFAST
		if(!errorCode.isSuccess()){
			return showJsonError(model,errorCode.getMsg());
		}
		Map jsonMap = errorCode.getRetval();
		final String validateCardStatus = (String) jsonMap.get("retval");	
		final String phoneNumber = (String) jsonMap.get("phoneNumber");
		String ip = WebUtils.getRemoteIp(request);
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		//specialDiscountService.useSpecialDiscount ����������޸��˶�����֧����ʽ��Ĭ����ѡ�Żݻ��ά����֧����ʽ�ĵ�һ��
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(order.getOrdertype(), orderId, sd, new OrderCallback(){

			@Override
			public void processOrder(SpecialDiscount sd2, GewaOrder gewaOrder) {
				Map<String, String> other = JsonUtils.readJsonToMap(gewaOrder.getOtherinfo());
				other.put("cardNumber", cardNumber);
				other.put("validateCardStatus", validateCardStatus);
				other.put("phoneNumber", phoneNumber);
				other.put("hasCardNumber", "Y");
				other.put("discountByPaymethod", paymethod);//��confirmOrder.vmҳ�����û�û��ѡ���֧����ʽ���˵���
				other.put(BackConstant.unionfastcardno, cardNumber);
				String[] pay = StringUtils.split(paymethod, ":");
				gewaOrder.setOtherinfo(JsonUtils.writeMapToJson(other));
				gewaOrder.setPaymethod(pay[0]);//��֧����ʽ�ĳ��û�ѡ���
				gewaOrder.setPaybank(pay.length > 1 ? pay[1] : null);
			}
			
		},ip);
		if(discount.isSuccess()) {
			jsonMap.put("amount",discount.getRetval().getCurUsedDiscount().getAmount());
			return showJsonSuccess(model, jsonMap);
		}		
		return showJsonError(model, discount.getMsg());
	}
	
	/**
	 * ��������2.0��֤֧�����������֤�������ؼۻ��һЩ��֤
	 * 
	 * @param sessid
	 * @param request
	 * @param orderId
	 * @param spid
	 * @param cardNumber
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Mar 20, 2013 5:08:06 PM
	 */
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/ajs.xhtml")
	public String unionPay2DiscountActivity(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid,final String cardNumber,  ModelMap model){
		if(orderId == null) return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		if(!StringUtils.equals(member.getId()+"", order.getMemberid()+"")){
			return showJsonError(model, "�Ƿ�������");
		}
		if(StringUtils.isBlank(cardNumber) || cardNumber.length() > 19 || cardNumber.length() < 13){
			return showJsonError(model, "��������ȷλ�������п��ţ�");
		}
		//���Ҫ��֤��bin
		ErrorCode<String> retCode = cooperateService.checkUnionPayFastAJS(order,cardNumber,spid);
		if(!retCode.isSuccess()) {
			return showJsonError(model, retCode.getMsg());
		}
		
		ErrorCode<Map> errorCode = validateCardNumber(order, PaymethodConstant.PAYMETHOD_UNIONPAYFAST_ACTIVITY_JS, cardNumber);
		if(!errorCode.isSuccess()){
			return showJsonError(model,errorCode.getMsg());
		}
		
		Map jsonMap = errorCode.getRetval();
		String ip = WebUtils.getRemoteIp(request);
		return unionpayFast2UseSpecialDiscount(ip, spid, order, cardNumber, jsonMap, model);
	}
	/**
	 * �����������binУ��
	 * @param sessid
	 * @param request
	 * @param orderId
	 * @param spid
	 * @param cardNumber
	 * @param model
	 * @return
	 */
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/abj.xhtml")
	public String unionPay2DiscountActivityBJ(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid,final String cardNumber,  ModelMap model){
		if(orderId == null) return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		if(!StringUtils.equals(member.getId()+"", order.getMemberid()+"")){
			return showJsonError(model, "�Ƿ�������");
		}
		if(StringUtils.isBlank(cardNumber) || cardNumber.length() > 19 || cardNumber.length() < 13){
			return showJsonError(model, "��������ȷλ�������п��ţ�");
		}
		//���Ҫ��֤��bin
		ErrorCode<String> retCode = cooperateService.checkUnionPayFastBJ(order,cardNumber,spid);
		if(!retCode.isSuccess()) {
			return showJsonError(model, retCode.getMsg());
		}
		ErrorCode<Map> errorCode = validateCardNumber(order, PaymethodConstant.PAYMETHOD_UNIONPAYFAST_ACTIVITY_BJ, cardNumber);
		if(!errorCode.isSuccess()){
			return showJsonError(model,errorCode.getMsg());
		}
		Map jsonMap = errorCode.getRetval();
		String ip = WebUtils.getRemoteIp(request);
		return this.unionpayFast2UseSpecialDiscount(ip, spid, order, cardNumber, jsonMap, model);
	}
	/**
	 * ����2.0�ʹ���ؼۻ
	 * @param ip
	 * @param spid
	 * @param order
	 * @param cardNumber
	 * @param jsonMap
	 * @param model
	 * @return
	 */
	private String unionpayFast2UseSpecialDiscount(String ip,long spid,GewaOrder order,final String cardNumber,
			Map jsonMap,ModelMap model){
		final String validateCardStatus = (String) jsonMap.get("retval");	
		final String phoneNumber = (String) jsonMap.get("phoneNumber");
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(order.getOrdertype(), order.getId(), sd, new OrderCallback(){
			@Override
			public void processOrder(SpecialDiscount sd2, GewaOrder gewaOrder) {
				Map<String, String> other = JsonUtils.readJsonToMap(gewaOrder.getOtherinfo());
				other.put(BackConstant.unionfastajscardno, cardNumber);
				other.put("cardNumber", cardNumber);
				other.put("validateCardStatus", validateCardStatus);
				other.put("hasCardNumber", "Y");
				other.put("phoneNumber", phoneNumber);
				gewaOrder.setOtherinfo(JsonUtils.writeMapToJson(other));
			}			
		},ip);
		if(discount.isSuccess()) {
			jsonMap.put("amount",discount.getRetval().getCurUsedDiscount().getAmount());
			return showJsonSuccess(model, jsonMap);
		}
		return showJsonError(model, discount.getMsg());
	}
	
	private ErrorCode<Map> validateCardNumber(GewaOrder order,String paymethod,String cardNumber){
		if(gatewayService.isSwitch(PaymethodConstant.PAYMETHOD_UNIONPAYFAST)){
			return validateCardNumberNew(order, cardNumber);
		}else {
			return validateCardNumberOld(order, paymethod, cardNumber);
		}
	}
	
	/**
	 * 
	 * 
	 * @param paymethod   ��Ӧ�̻���ʶ������������
	 * @param cardNumber
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Nov 7, 2013 2:30:55 PM
	 */
	private ErrorCode<Map> validateCardNumberNew(GewaOrder order,String cardNumber){
		//String merchantCode = paymethod;
		ErrorCode<PayMerchant> code = gatewayService.findMerchant(order.getCitycode(), PaymethodConstant.PAYMETHOD_UNIONPAYFAST);
		if(!code.isSuccess()){
			dbLogger.warn("tradeNo is " + order.getTradeNo() + " " + code.getMsg());
			return ErrorCode.getFailure(code.getMsg());
		}
		String merchantCode = code.getRetval().getMerchantCode();
		ActivationQueryRequest request = new ActivationQueryRequest(merchantCode, cardNumber);
		ActivationQueryResponse response = unionPayFastApiService.activationQuery(request);
		if(!response.isSuccess()){
			return ErrorCode.getFailure(response.getMsg());
		}
		
		if(StringUtils.isBlank(response.getPhoneNumber())){
			return ErrorCode.getFailure("���п����ֻ���Ϊ��");
		}
		Map jsonMap = new HashMap();
		jsonMap.put("retval", response.getActivateStatus());
		jsonMap.put("phoneNumber",response.getPhoneNumber());//���п��ﶨ���ֻ���
		return ErrorCode.getSuccessReturn(jsonMap);
	}
	
	/**
	 * Ϊ������֤֧��2.0��֤����
	 * 
	 * @param order
	 * @param paymethod
	 * @param cardNumber
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Mar 20, 2013 4:55:39 PM
	 */
	private ErrorCode<Map> validateCardNumberOld(GewaOrder order,String paymethod,String cardNumber){
		HttpResult result = UnionpayFastUtil.getCardActivateStatus(paymethod,cardNumber);
		if(!result.isSuccess()){
			return ErrorCode.getFailure(result.getMsg());
		}
		Map<String, String> responses = UnionpayFastUtil.parseUnionpayResponse(result.getResponse());
		if(!UnionpayFastUtil.checkSign(paymethod,responses)){
			return ErrorCode.getFailure("ǩ����֤δͨ�����Ƿ�����!");
		}
		if(!StringUtils.equals("00",responses.get("respCode"))){
			String respMsg = responses.get("respMsg");
			if(StringUtils.equals("60", responses.get("respCode"))){
				respMsg = "���ã�����" + respMsg + ",��������Ҳ����ӽ��п�ͨ��";
			}
			return ErrorCode.getFailure(respMsg);
		}
				
		Map jsonMap = new HashMap();
		String activateStatus = responses.get("activateStatus");
		jsonMap.put("retval", activateStatus);
		Map<String, String> cupReserved = UnionpayFastUtil.parseUnionpayResponse(responses.get("cupReserved").replace("}", "").replace("{", ""));
		/**
		if("1".equals(activateStatus)){
			if(cupReserved.get("expiry") == null || cupReserved.get("transLimit") ==null){
				dbLogger.error(responses.get("cupReserved"));
				return ErrorCode.getFailure("���п���֤����");
			}
			
			if(DateUtil.format(DateUtil.currentTime(), "yyyyMMdd").compareTo(cupReserved.get("expiry")) > 0){
				return ErrorCode.getFailure("���ã�����ͨ��С��֧����Ч���ѹ������ܽ���֧����");
			}
			if(order.getDue() * 100 > Integer.parseInt(cupReserved.get("transLimit"))){
				return ErrorCode.getFailure("���ã����Ķ�����������ͨ��С��֧���ĵ����޶��ȣ����ܽ���֧����");
			}
		}
		*/
		
		if(StringUtils.equals("0", activateStatus) || StringUtils.equals("1", activateStatus)){
			if(StringUtils.isBlank(cupReserved.get("phoneNumber"))){
				dbLogger.error(responses.get("cupReserved"));
				return ErrorCode.getFailure("���п����ֻ���Ϊ��");
			}
			
			jsonMap.put("phoneNumber",cupReserved.get("phoneNumber"));//���п��ﶨ���ֻ���
			return ErrorCode.getSuccessReturn(jsonMap);
		}else{
			dbLogger.error(responses.get("cupReserved"));
			return ErrorCode.getFailure("���п���֤������ȷ���Ƿ�ͨ������֤֧����");
		}
		
	}
	
	
	@RequestMapping("/ajax/cooperate/unionPayFastSendSms.xhtml")
	public String unionPay2SendSms(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId ,String cardNumber,String phoneNumber, ModelMap model){
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(!member.getId().equals(order.getMemberid())){
			return showJsonError(model, "���ɶ����˶���������");
		}
		if(StringUtils.isBlank(phoneNumber)){
			phoneNumber = order.getMobile();
		}
		
		if(gatewayService.isSwitch(PaymethodConstant.PAYMETHOD_UNIONPAYFAST)){
			//String merchantCode = order.getPaymethod();
			ErrorCode<PayMerchant> code = gatewayService.findMerchant(order.getCitycode(), PaymethodConstant.PAYMETHOD_UNIONPAYFAST);
			if(!code.isSuccess()){
				dbLogger.warn("tradeNo is " + order.getTradeNo() + " " + code.getMsg());
				return showJsonError(model, code.getMsg());
			}
			String merchantCode = code.getRetval().getMerchantCode();
			SendSmsRequest sendSmsRequest = new SendSmsRequest(merchantCode, order.getTradeNo(), cardNumber, phoneNumber, order.getDue() * 100);
			SendSmsResponse sendSmsResponse = unionPayFastApiService.sendSms(sendSmsRequest);
			if(!sendSmsResponse.isSuccess()){
				return showJsonError(model, sendSmsResponse.getMsg());
			}			
		}else{
			HttpResult result = UnionpayFastUtil.sendSms(order,cardNumber,phoneNumber);
			if(!result.isSuccess()){			
				return showJsonError(model, result.getMsg());
			}
			Map<String, String> responses = UnionpayFastUtil.parseUnionpayResponse(result.getResponse());
			if(!UnionpayFastUtil.checkSign(order.getPaymethod(),responses)){
				return showJsonError(model, "ǩ����֤δͨ�����Ƿ�����!");
			}
			if(!StringUtils.equals("00",responses.get("respCode"))){
				return showJsonError(model, responses.get("respMsg"));
			}
		}		
		
		return showJsonSuccess(model);
	}
	
	
	@RequestMapping("/validate/cooperate/shbank.xhtml")
	@ResponseBody
	public String shbankDiscount(String otherinfo, String tradeno, Long orderid){
		ErrorCode<String> code = cooperateService.checkShbankBack(orderid, otherinfo, "true");
		if(!code.isSuccess()){
			String msg = code.getMsg();
			if(StringUtils.equals(code.getErrcode(),"error")){
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "��ҵ������֤�Ƿ���" + tradeno + ", ��֤���" + msg);
				return "error";
			}
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "�Ϻ�������֤�Ƿ���" + tradeno + ", ��֤���" + msg);
			return msg;
		}
		return "success";
	}
	
	@RequestMapping("/validate/cooperate/xybank.xhtml")
	@ResponseBody
	public String xybankDiscount(String otherinfo, String tradeno, Long orderid){
		ErrorCode<String> code = cooperateService.checkXybankBack(orderid, otherinfo, "true");
		if(!code.isSuccess()){
			String msg = code.getMsg();
			if(StringUtils.equals(code.getErrcode(),"error")){
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "��ҵ������֤�Ƿ���" + tradeno + ", ��֤���" + msg);
				return "error";
			}
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "��ҵ������֤�Ƿ���" + tradeno + ", ��֤���" + msg);
			return msg;
		}
		return "success";
	}
	@RequestMapping("/validate/cooperate/gfbank.xhtml")
	@ResponseBody
	public String gfbank(Long orderid){
		GewaOrder order = daoService.getObject(GewaOrder.class, orderid);
		Map<String, String> map = new HashMap<String, String>();
		map.put("orderid", orderid+"");
		map.put("tradeno", order.getTradeNo());
		map.put("memberid", order.getMemberid()+"");
		map.put("week", DateUtil.getCnWeek(order.getAddtime()));//�ܼ�
		map.put("draw", Status.N);								//�Ƿ��Ѿ��齱
		map.put("winnerid", "");								//�񽱼�¼ID
		mongoService.saveOrUpdateMap(map, "orderid", MongoData.NS_GFBANK_ORDER);
		return "success";
	}
	
	
	/**
	 * ����ƽ�����У���ÿ�������Ϊÿ��ÿ����ʹ��һ�Ρ�
	 */
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/shenzhenPingAn.xhtml")
	public String shenzhenPingAn(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "shenzhenPingAn");
	}
	/**
	 * �����й����У�����������ÿ�������Ϊÿ��ÿ����ʹ��һ�Ρ�
	 */
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/guangzhouBocWeekOne.xhtml")
	public String guangzhouBocWeekOne(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "guangzhouBocWeekOne");
	}
	/**
	 * �����й����У���Ʊ����ÿ����1�ʶ�����ÿ����2�ʶ���
	 */
	@RequestMapping("/ajax/cooperate/unionPayFastDiscount/activity/guangzhouBocMonthTwo.xhtml")
	public String guangzhouBocMonthTwo(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, final String paymethod,final String cardNumber,  ModelMap model){
		return unionPayFastDiscountAJ(sessid, request, orderId, spid, paymethod, cardNumber, model, "guangzhouBocMonthTwo");
	}
	/**
	 * �ؼۻ�������Ŷε��ֻ����������֤
	 * @param sessid
	 * @param request
	 * @param orderId
	 * @param spid
	 * @param mobile
	 * @param model
	 * @return
	 */
	@RequestMapping("/ajax/cooperate/spDiscount/activity/valiMobileCommon.xhtml")
	public String valiMobileCommon(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid,final String mobile,  ModelMap model){
		if(orderId == null) {
			return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		}
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) {
			return showJsonError(model, "���ȵ�¼��");
		}
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) {
			return showJsonError(model, "��ȷ����Ҫ֧���Ķ����Ƿ���ȷ��");
		}
		if(!StringUtils.equals(member.getId()+"", order.getMemberid()+"")){
			return showJsonError(model, "�Ƿ�������");
		}
		if(!ValidateUtil.isMobile(mobile)){
			return showJsonError(model, "��������ȷ�ֻ����룡");
		}
		//���Ҫ��֤��bin
		ErrorCode<String> retCode = cooperateService.checkCommonCardbinOrCardNumLimit(order, spid, mobile);
		if(!retCode.isSuccess()) {
			return showJsonError(model, retCode.getMsg());
		}
		String ip = WebUtils.getRemoteIp(request);
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		//specialDiscountService.useSpecialDiscount ����������޸��˶����Ĺ�Ʊ�ֻ�����
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(order.getOrdertype(), orderId, sd, new OrderCallback(){
			@Override
			public void processOrder(SpecialDiscount sd2, GewaOrder gewaOrder) {
				gewaOrder.setMobile(mobile);
			}
			
		},ip);
		if(discount.isSuccess()) {
			Map jsonMap = new HashMap();
			jsonMap.put("amount",discount.getRetval().getCurUsedDiscount().getAmount());
			return showJsonSuccess(model, jsonMap);
		}		
		return showJsonError(model, discount.getMsg());
	}
}
