package com.gewara.web.action.gewapay;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.Config;
import com.gewara.constant.BindConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.TokenType;
import com.gewara.constant.sys.ConfigConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.OrderProcessConstant;
import com.gewara.helper.order.JsonKeyOrderCallback;
import com.gewara.helper.order.OrderContainer;
import com.gewara.model.common.GewaConfig;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.user.Member;
import com.gewara.mongo.MongoService;
import com.gewara.pay.CCBPosPayUtil;
import com.gewara.service.OperationService;
import com.gewara.service.member.BindMobileService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.impl.ControllerService;
import com.gewara.untrans.ticket.SpecialDiscountService;
import com.gewara.util.DateUtil;
import com.gewara.util.HttpResult;
import com.gewara.util.HttpUtils;
import com.gewara.util.JsonUtils;
import com.gewara.util.ObjectId;
import com.gewara.util.StringUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Controller
public class FastPayController extends BasePayController {
	public static final String CARD_PEX = "^\\d{16}$";
	public static final String DATE_PEX = "^\\d{4}$";
	
	@Autowired@Qualifier("specialDiscountService")
	private SpecialDiscountService specialDiscountService;
	@Autowired@Qualifier("bindMobileService")
	private BindMobileService bindMobileService;
	public void setBindMobileService(BindMobileService bindMobileService){
		this.bindMobileService = bindMobileService;
	}
	
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}
	
	@Autowired@Qualifier("controllerService")
	private ControllerService controllerService;
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}
	@RequestMapping("/ajax/mobile/ccbcode.xhtml")
	public String ccbMobileCode(@CookieValue(value=LOGIN_COOKIE_NAME,required=false)String sessid, 
			String mobile, String captchaId, String captcha, ModelMap model, HttpServletRequest request){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		//if(StringUtils.isBlank(member.getMobile())) return showJsonError(model, "���Ȱ��ֻ��ţ�");
		Map jsonMap = new HashMap();
		Map errorMap = new HashMap();
		jsonMap.put("errorMap", errorMap);
		if(StringUtils.isBlank(mobile)){
			errorMap.put("mobile", "�������ֻ��ţ�");
			return showJsonError(model, jsonMap);
		}
		if(!ValidateUtil.isMobile(mobile)){
			errorMap.put("mobile","�ֻ����������");
			return showJsonError(model, jsonMap);
		}
		boolean iscaptcha = bindMobileService.isNeedToken(TokenType.CCBMobile, ip, 2);
		if(iscaptcha){
			jsonMap.put("refreshCaptcha", "true");
			model.put("iscaptcha", iscaptcha);
			if(StringUtils.isBlank(captcha)){
				errorMap.put("captcha", "��������֤�룡");
				return showJsonError(model, jsonMap);
			}else{
				boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
				errorMap.put("captcha", "��֤�����");
				if(!isValidCaptcha) return showJsonError(model, jsonMap);
			}
		}
		boolean next = bindMobileService.getAndUpdateToken(TokenType.CCBMobile, ip, 2);
		if(next){
			jsonMap.put("refreshCaptcha", "true");
		}
		ErrorCode<SMSRecord> code = bindMobileService.refreshBindMobile(BindConstant.TAG_CCBANKCODE, mobile, ip);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		untransService.sendMsgAtServer(code.getRetval(), false);
		
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/gewapay/ccbquickpay.xhtml")
	public String fastPayCCBank(@CookieValue(value=LOGIN_COOKIE_NAME,required=false)String sessid, 
			Long orderId, String ccbCardno, String modCard, String mobile, String checkpass, String errorMsg, HttpServletRequest request, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showError(model, "���ȵ�¼��");
		model.put("member", member);
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) return showMessageAndReturn(model, request, "���������ڣ�");
		if(StringUtils.isBlank(ccbCardno)){ 
			ccbCardno = VmUtils.getJsonValueByKey(order.getOtherinfo(), "ccbCardno");
			modCard = "N";
		}
		model.put("ccbCardno", ccbCardno);
		model.put("modCard", modCard);
		model.put("order", order);
		model.put("mobile", mobile);
		model.put("checkpass", checkpass);
		model.put("errorMsg", errorMsg);
		return "gewapay/ccbQuickPay.vm";
	}
	private String showPayMsg(ModelMap model, String errorMsg){
		model.put("errorMsg", errorMsg);
		return "redirect:/gewapay/ccbquickpay.xhtml";
	}
	@RequestMapping("/gewapay/saveFastPay.xhtml")
	public String saveFastPay(@CookieValue(value=LOGIN_COOKIE_NAME,required=false)String sessid, Long orderId, 
			String creditCard, String expire_month, String expire_year, String cvv2, String mobile, 
			String checkpass, HttpServletRequest request, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) return showError(model, "���������ڣ�");
		if(!StringUtils.equals(member.getId()+"", order.getMemberid()+"")) return showError(model, "���ܲ鿴���˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return showError(model, "�����޸���֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("orderId", orderId);
		if(order.isAllPaid()) {
			return "redirect:/gewapay/orderResult.xhtml";
		}
		model.put("mobile", mobile);
		model.put("checkpass", checkpass);
		String modCard = "N";
		String ccbCardno = VmUtils.getJsonValueByKey(order.getOtherinfo(), "ccbCardno");
		if(StringUtils.isBlank(ccbCardno)) { 
			ccbCardno = creditCard;
			modCard = "Y";
		}
		model.put("ccbCardno", ccbCardno);
		model.put("modCard", modCard);
		//1����֤������Ϣ
		if(StringUtils.isBlank(ccbCardno)){
			return showPayMsg(model, "���ÿ��Ų���Ϊ�գ�");
		}
		if(StringUtils.isBlank(expire_month) || StringUtils.isBlank(expire_year)){
			return showPayMsg(model, "���ÿ���Ч���ڲ���Ϊ�գ�");
		}
		if(StringUtils.length(expire_month)!=2 || StringUtils.length(expire_year)!=2){
			return showPayMsg(model, "���ÿ���Ч���ڸ�ʽ����");
		}
		//if(!isValidCardbin(ccbCardno)) return showPayMsg(model, "����Ч�Ľ������ÿ�����");
		if(StringUtils.isBlank(cvv2) || StringUtils.length(cvv2)!=3){
			return showPayMsg(model, "CVV2���ʽ����");
		}
		if(StringUtils.isBlank(mobile) || !ValidateUtil.isMobile(mobile)){
			return showPayMsg(model, "�ֻ����������");
		}
		if(StringUtils.isBlank(checkpass)){
			return showPayMsg(model, "�ֻ���֤�벻��Ϊ�գ�");
		}
		
		if(StringUtils.isBlank(StringUtil.findFirstByRegex(ccbCardno, CARD_PEX))){
			return showPayMsg(model, "���ÿ��Ÿ�ʽ����");
		}
		String expire = expire_year + expire_month;
		if(StringUtils.isBlank(StringUtil.findFirstByRegex(expire, DATE_PEX))){
			return showPayMsg(model, "��Ч�ڸ�ʽ����");
		}
		if(order instanceof SportOrder){
			if(isParticipateDiscountSport(ccbCardno)) return showPayMsg(model, "ϵͳæ�����Ժ����ԣ�");
		}
		
		ErrorCode code = bindMobileService.checkBindMobile(BindConstant.TAG_CCBANKCODE, mobile, checkpass);
		if(!code.isSuccess()) return showPayMsg(model, code.getMsg());
		boolean allow = operationService.updateOperation("pay" + orderId, 30);
		//2����֤�Ż�
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("trade_no", order.getTradeNo());
		params.put("cardno", ccbCardno);
		params.put("amount", ""+order.getDue());
		params.put("cvv2", cvv2);
		params.put("expire", expire);
		if(!allow) {
			return showPayMsg(model, "ϵͳ���ڴ������15-20���鿴�ҵĶ�����");
		}
		GewaConfig gconfig = daoService.getObject(GewaConfig.class, ConfigConstant.CFG_CCBPOSPAY);
		String payurl = CCBPosPayUtil.getPayUrl(gconfig);
		HttpResult result = HttpUtils.postUrlAsString(payurl, params);
		if(result.isSuccess()){
			Map<String, String> returnMap = JsonUtils.readJsonToMap(result.getResponse());
			orderMonitorService.addOrderPayCallback(order.getTradeNo(), OrderProcessConstant.CALLTYPE_RETURN, PaymethodConstant.PAYMETHOD_CCBPOSPAY, result.getResponse() + ",host=" + Config.getServerIp());
			if(StringUtils.equals(returnMap.get("result"), "success")){
				ErrorCode<GewaOrder> payResult = paymentService.netPayOrder(order.getTradeNo(), returnMap.get("payseqno"), order.getDue(), PaymethodConstant.PAYMETHOD_CCBPOSPAY, "ccb", "����POS");
				if(payResult.isSuccess()) {
					processPay(order.getTradeNo(), "����POS");
					GewaOrder rorder = payResult.getRetval();
					Map map = new HashMap();
					map.put("tradeno", rorder.getTradeNo());
					map.put("paiddate", DateUtil.format(rorder.getPaidtime(), "yyyyMMdd"));
					map.put("alipaid", rorder.getAlipaid()+"");
					map.put("payseqno", rorder.getPayseqno());
					map.put("settle", "N");
					mongoService.saveOrUpdateMap(map, "tradeno", MongoData.NS_CCBPOS_ORDER);
				}
				model.remove("mobile");
				model.remove("checkpass");
				model.remove("modCard");
				model.remove("ccbCardno");
				return "redirect:/gewapay/orderResult.xhtml";
			}else{
				return showPayMsg(model, returnMap.get("message"));
			}
		}else{
			return showPayMsg(model, "�����������");
		}
	}
	//�������ÿ�֧���Ż�
	@RequestMapping("/gewapay/ccb.xhtml")
	public String ccb(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/ccbpay.vm";
	}
	
	//���ݵ�Ӱ�������ÿ�֧���Ż�
	@RequestMapping("/gewapay/ccbGZ.xhtml")
	public String ccbGZ(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/ccbpayGZ.vm";
	}
	
	@RequestMapping("/ajax/trade/ccbGZDiscount.xhtml")//����
	public String ccbGZDiscount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, String creditCard, ModelMap model){
		if(StringUtils.isBlank(StringUtil.findFirstByRegex(creditCard, CARD_PEX))){
			return showJsonError(model, "���ÿ��Ÿ�ʽ����");
		}
		if(!isValidCardPrefixAndSuffix(creditCard)) return showJsonError(model, "�Բ����װ����û���������Ŀ��Ų��ڱ���ķ�Χ�ڣ�����������鿴�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		if(isParticipateDiscountTicketOrder(creditCard)) return showJsonError(model, "�Բ����װ����û������Ѿ��μӹ��˻�ˡ�");
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, orderId, sd, new JsonKeyOrderCallback("ccbCardno", creditCard), ip);
		if(discount.isSuccess()) {
			return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
		}
		return showJsonError(model, discount.getMsg());
	}
	
	@RequestMapping("/ajax/trade/ccbDiscount.xhtml")//����
	public String ccbDiscount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long orderId, Long spid, String creditCard, ModelMap model){
		if(StringUtils.isBlank(StringUtil.findFirstByRegex(creditCard, CARD_PEX))){
			return showJsonError(model, "���ÿ��Ÿ�ʽ����");
		}
		//��binУ��[ǰ6λУ��]
		if(!isValidCardbin(creditCard)) return showJsonError(model, "����Ч�Ľ������ÿ�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		if(isParticipateDiscountSport(creditCard)) return showJsonError(model, "ϵͳæ�����Ժ����ԣ�");
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_SPORT, orderId, sd, new JsonKeyOrderCallback("ccbCardno", creditCard), ip);
		if(discount.isSuccess()) {
			return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
		}
		return showJsonError(model, discount.getMsg());
	}
	/**
	 * ���п�������֤
	 * @param orderid
	 * @param request
	 * @return
	 */
	@RequestMapping("/trade/ccbpos/validateDiscount.xhtml")
	@ResponseBody
	public String chinapayDiscount(Long orderid,HttpServletRequest request){
		GewaOrder order = daoService.getObject(GewaOrder.class, orderid);
		String ccbCardno = VmUtils.getJsonValueByKey(order.getOtherinfo(), "ccbCardno");
		if(isParticipateDiscount(ccbCardno)){
			return "repeat:"+ccbCardno;
		}
		addParticipate(ccbCardno, orderid);
		return "success";
	}
	/**
	 * ���ݽ��п�������֤����
	 * @param cardNo
	 * @param request
	 * @return
	 */
	@RequestMapping("/trade/ccbpos/validateGZDiscount.xhtml")
	@ResponseBody
	public String ccbposPayDiscount(Long orderid,HttpServletRequest request){
		GewaOrder order = daoService.getObject(GewaOrder.class, orderid);
		String ccbCardno = VmUtils.getJsonValueByKey(order.getOtherinfo(), "ccbCardno");
		if(isParticipateDiscountTicketOrder(ccbCardno)){
			return "repeat:"+ccbCardno;
		}
		Map<String, Object> toSave = new HashMap<String, Object>();
		toSave.put(MongoData.SYSTEM_ID, ObjectId.uuid());
		toSave.put("cardNo", ccbCardno);
		toSave.put("orderid", orderid);
		toSave.put("addtime", DateUtil.formatTimestamp(DateUtil.getMillTimestamp()));
		mongoService.saveOrUpdateMap(toSave, MongoData.SYSTEM_ID, MongoData.NS_CCBPOS_GZ_ACTIVITY, false, true);
		return "success";
	}
	
	private boolean isParticipateDiscountSport(String cardNo){
		List<Date> weekdateList = DateUtil.getCurWeekDateList(new Date());
		Date weekbegin =  weekdateList.get(0);
		Date weekend = weekdateList.get(6);
		Timestamp weekbegneintime = DateUtil.getBeginTimestamp(weekbegin);
		Timestamp weekendtime = DateUtil.getLastTimeOfDay(new Timestamp(weekend.getTime()));
		
		DBObject query = mongoService.queryAdvancedDBObject("addtime", new String[]{">=", "<="}, 
				new Object[]{DateUtil.formatTimestamp(weekbegneintime), DateUtil.formatTimestamp(weekendtime)});
		query.put("cardNo", cardNo);
		List<Map> qryMapList = mongoService.find(MongoData.NS_CCBPOS_ACTIVITY, query);
		return VmUtils.size(qryMapList)>=1;
	}
	private boolean isParticipateDiscount(String cardNo){
		Timestamp curtime = new Timestamp(System.currentTimeMillis());
		Timestamp begneintime = DateUtil.getBeginTimestamp(curtime);
		Timestamp endtime = DateUtil.getLastTimeOfDay(curtime);
		
		DBObject query = mongoService.queryAdvancedDBObject("addtime", new String[]{">=", "<="}, 
				new Object[]{DateUtil.formatTimestamp(begneintime), DateUtil.formatTimestamp(endtime)});
		query.put("cardNo", cardNo);
		List<Map> qryMapList = mongoService.find(MongoData.NS_CCBPOS_ACTIVITY, query);
		return VmUtils.size(qryMapList)>=2;
	}
	
	private boolean isParticipateDiscountTicketOrder(String cardNo){
		DBObject query = new BasicDBObject();
		query.put("cardNo", cardNo);
		List<Map> qryMapList = mongoService.find(MongoData.NS_CCBPOS_GZ_ACTIVITY, query);
		return VmUtils.size(qryMapList)>=1;
	}
	
	private void addParticipate(String cardNo, Long orderid){
		Map<String, Object> toSave = new HashMap<String, Object>();
		toSave.put(MongoData.SYSTEM_ID, ObjectId.uuid());
		toSave.put("cardNo", cardNo);
		toSave.put("orderid", orderid);
		toSave.put("addtime", DateUtil.formatTimestamp(DateUtil.getMillTimestamp()));
		mongoService.saveOrUpdateMap(toSave, MongoData.SYSTEM_ID, MongoData.NS_CCBPOS_ACTIVITY, false, true);
	}
	private boolean isValidCardbin(String cardno){
		String startbin = StringUtils.substring(cardno, 0, 6);
		List<String> cardbinList = new ArrayList<String>();
		List<Map> qryMapList = mongoService.find(MongoData.NS_CCBPOS_CARDBIN, new HashMap<String, Object>());
		for(Map map : qryMapList){
			cardbinList.add(map.get("cardbin")+"");
		}
		boolean isres = cardbinList.contains(startbin);
		if(!isres){
			dbLogger.warn("������֤��bin����" + cardno);
		}
		return isres;
	}
	
	private boolean isValidCardPrefixAndSuffix(String cardno){
		String prefix = StringUtils.substring(cardno, 0, 8);
		int cardLength = cardno.length();
		String suffix = StringUtils.substring(cardno, cardLength - 4, cardLength);
		Map tmpMap = mongoService.findOne(MongoData.NS_CCBPOS_CARDBIN_2013, "_id",prefix + suffix);
		if(tmpMap == null || tmpMap.isEmpty() || 
				!StringUtils.equals(prefix,(String)tmpMap.get("prefixbin")) || !StringUtils.equals(suffix,(String)tmpMap.get("suffixbin"))){
			dbLogger.warn("������֤��bin����" + cardno);
			return false;
		}
		return true;
	}
	
	
	
	
	
	//��Ӱ�������ÿ�֧���Ż���д����---- ÿ������
	@RequestMapping("/gewapay/ccbEveryDayTwice.xhtml")
	public String ccbMovieDiscount(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/ccbpayEveryDayTwice.vm";
	}
	//��Ӱ�������ÿ�֧���Ż� ---ÿ������
	@RequestMapping("/ajax/trade/ccbEveryDayTwice.xhtml")//����
	public String ccbMovieDiscount(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
				HttpServletRequest request, Long orderId, Long spid, String creditCard, ModelMap model){
		if(StringUtils.isBlank(StringUtil.findFirstByRegex(creditCard, CARD_PEX))){
			return showJsonError(model, "���ÿ��Ÿ�ʽ����");
		}
		if(!isValidCardbin(creditCard)) return showJsonError(model, "����Ч�Ľ������ÿ�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		
		if(isEveryDayTwice(creditCard)) return showJsonError(model, "�Բ��𣬸��Żݻÿ�����μ����Ρ�");
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, orderId, sd, ip);
		if(discount.isSuccess()) {
			GewaOrder order = discount.getRetval().getOrder();
			order.setOtherinfo(JsonUtils.addJsonKeyValue(order.getOtherinfo(), "ccbCardno", creditCard));
			daoService.saveObject(order);
			return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
		}
		return showJsonError(model, discount.getMsg());
	}
	/**
	 * ÿ�����κ�����֤����
	 * @param cardNo
	 * @param request
	 * @return
	 */
	@RequestMapping("/trade/ccbpos/ccbEveryDayTwice.xhtml")
	@ResponseBody
	public String ccbEveryDayTwice(Long orderid){
		GewaOrder order = daoService.getObject(GewaOrder.class, orderid);
		String ccbCardno = VmUtils.getJsonValueByKey(order.getOtherinfo(), "ccbCardno");
		if(isEveryDayTwice(ccbCardno)){
			return "repeat:"+ccbCardno;
		}
		Map<String, Object> toSave = new HashMap<String, Object>();
		toSave.put(MongoData.SYSTEM_ID, ObjectId.uuid());
		toSave.put("cardNo", ccbCardno);
		toSave.put("orderid", orderid);
		toSave.put("addtime", DateUtil.formatTimestamp(DateUtil.getMillTimestamp()));
		mongoService.saveOrUpdateMap(toSave, MongoData.SYSTEM_ID, MongoData.NS_CCBPOS_EVERYDAYTWICE, false, true);
		return "success";
	}
	private boolean isEveryDayTwice(String cardNo){
		Timestamp curtime = new Timestamp(System.currentTimeMillis());
		Timestamp begneintime = DateUtil.getBeginTimestamp(curtime);
		Timestamp endtime = DateUtil.getLastTimeOfDay(curtime);
		
		DBObject query = mongoService.queryAdvancedDBObject("addtime", new String[]{">=", "<="}, 
				new Object[]{DateUtil.formatTimestamp(begneintime), DateUtil.formatTimestamp(endtime)});
		query.put("cardNo", cardNo);
		List<Map> qryMapList = mongoService.find(MongoData.NS_CCBPOS_EVERYDAYTWICE, query);
		return VmUtils.size(qryMapList)>=2;
	}
	
	//�����ݿ��¼��ʾ�������֤������Ϊ֧����ʽ��ƽ��/�չ����֧����ʹ��
	@RequestMapping("/trade/unionpayfast/addoperation.xhtml")
	@ResponseBody
	public String addoperation(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = spid + ":" + cardNumber;
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 5, 1, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
	
		
	
	/**
	 * ���������-2.0��֤֧������֤����
	 * һ�����ţ���һ����ڣ�һ������ֻ����һ��
	 * 
	 * @param tradeNo      ������
	 * @param spid         �ŻݻID
	 * @param otherinfo    ������otherinfo
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Mar 13, 2013 4:37:46 PM
	 */
	@RequestMapping("/trade/unionPayFastAJS/addoperations.xhtml")
	@ResponseBody
	public String addoperationForUnionpayfastAJS(String tradeno,String otherinfo, Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = spid + ":" + cardNumber;
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 30, 1, tradeno);
			if(allow) {
				return "success";
			}else{
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "tradeno " + tradeno + ", only one time in one month��");
			} 
		}
		return "false";
	}
	/**
	 * ������֤2.0-���� � ͬһ����һ��ʹ��һ��
	 * @param tradeno
	 * @param otherinfo
	 * @param spid
	 * @return
	 */
	@RequestMapping("/trade/unionPayFastBJ/addoperations.xhtml")
	@ResponseBody
	public String addoperationForUnionpayfastBJ(String tradeno,String otherinfo, Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = spid + ":" + cardNumber;
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 6, 1, tradeno);
			if(allow) {
				return "success";
			}else{
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "tradeno " + tradeno + ", only one time in one week��");
			} 
		}
		return "false";
	}
	
	/**
	 * ���������-2.0��֤֧�����й����У�����֤����
	 * һ�����ţ���һ����ڣ�һ���������ֻ�������Σ���һ��������ֻ����һ��
	 * 
	 * @param orderid
	 * @param spid
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Mar 26, 2013 2:04:50 PM
	 */
	@RequestMapping("/trade/unionpayfast/boc/addoperation.xhtml")
	@ResponseBody
	public String addoperationForUnionpayfastBOC(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = spid + ":"+ cardNumber;
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 7, OperationService.ONE_DAY * 30, 2, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
		
	
	/**
	 * ���������-2.0��֤֧�����й����У����ݣ�������֤����
	 * һ�����ţ���һ����ڣ�һ��������ֻ����һ��
	 * 
	 * @param orderid
	 * @param spid
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Mar 26, 2013 2:04:50 PM
	 */
	@RequestMapping("/trade/unionpayfast/boc/sz/addoperation.xhtml")
	@ResponseBody
	public String addoperationForUnionpayfastSZBOC(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = spid + ":"+ cardNumber;
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 6, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
	
	@RequestMapping("/trade/unionpayfast/nyyh/addoperation.xhtml")
	@ResponseBody
	public String addoperationForNyyh(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = getOpkey(spid, cardNumber);
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 6, 1, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
	
	@RequestMapping("/trade/unionpayfast/cqnsyh/addoperation.xhtml")
	@ResponseBody
	public String addoperationForCqnsyh(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = spid + ":" + cardNumber;
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 6, 1, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
	
	@RequestMapping("/trade/unionpayfast/youjie/addoperation.xhtml")
	@ResponseBody
	public String addoperationForYoujie(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = getOpkey(spid, cardNumber);
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 6, 1, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
	
	@RequestMapping("/trade/paymethod/common/addoperation.xhtml")
	@ResponseBody
	public String addOperationForCommon(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = getOpkey(spid, cardNumber);
			SpecialDiscount specialDiscount = daoService.getObject(SpecialDiscount.class, spid);
			boolean allow = false;
			if(specialDiscount.getCardNumPeriodSpan() != null){
				allow = operationService.updateOperation(opkey, OperationService.ONE_MINUTE * specialDiscount.getCardNumPeriodIntvel(), 
						OperationService.ONE_MINUTE * specialDiscount.getCardNumPeriodSpan(), specialDiscount.getCardNumLimitnum(), tradeno);
			}else{
				allow = operationService.updateOperation(opkey, OperationService.ONE_MINUTE * specialDiscount.getCardNumPeriodIntvel(), 
						specialDiscount.getCardNumLimitnum(), tradeno);
			}
			if(allow){
				return "success"; 
			}
		}
		return "false";
	}
	
	@RequestMapping("/trade/unionpayfast/wzyh/addoperation.xhtml")
	@ResponseBody
	public String addoperationForWzcb(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = getOpkey(spid, cardNumber);
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 6, 1, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
	
	@RequestMapping("/trade/unionpayfast/zdyh/addoperation.xhtml")
	@ResponseBody
	public String addoperationForUnionpayfastZdyh(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = getOpkey(spid, cardNumber);
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 6, OperationService.ONE_DAY * 30, 2, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
	
	private String getOpkey(Long spid,String cardNumber){
		SpecialDiscount specialDiscount = daoService.getObject(SpecialDiscount.class, spid);
		String opkey = spid + ":" + cardNumber;
		if(specialDiscount != null && StringUtils.isNotBlank(specialDiscount.getCardUkey())){
			opkey = "spd" + specialDiscount.getCardUkey() + ":" + cardNumber;
		}else if(specialDiscount != null && specialDiscount.getSpcounterid() != null){
			opkey = "spd" + specialDiscount.getSpcounterid() + ":" + cardNumber;
		}
		return opkey;
	}
	
	//�������ÿ�֧���Ż���д����---- ÿ���Ĵ�
	@RequestMapping("/gewapay/jump/ccb/130528.xhtml")
	public String ccbActivity130528(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/jumpCCBActivity130528.vm";
	}
	
	//�������ÿ�֧���Ż� ---ÿ���Ĵ�
	@RequestMapping("/ajax/trade/ccb/discount/130528.xhtml")//����
	public String ccbasDiscount130528(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
				HttpServletRequest request, Long orderId, Long spid, String creditCard, ModelMap model){
		if(StringUtils.isBlank(StringUtil.findFirstByRegex(creditCard, CARD_PEX))){
			return showJsonError(model, "���ÿ��Ÿ�ʽ����");
		}
		if(!isValidCardbin(creditCard)) return showJsonError(model, "����Ч�Ľ������ÿ�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		
		String opkey = spid + ":"+ creditCard;
		boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY,4);
		if(!allow){
			return showJsonError(model, "�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ���ĴΣ�");
		}
		
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, orderId, sd, ip);
		if(discount.isSuccess()) {
			GewaOrder order = discount.getRetval().getOrder();
			order.setOtherinfo(JsonUtils.addJsonKeyValue(order.getOtherinfo(), "ccbCardno", creditCard));
			daoService.saveObject(order);
			return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
		}
		return showJsonError(model, discount.getMsg());
	}
	
	@RequestMapping("/trade/ccb/addoperation/130528.xhtml")
	@ResponseBody
	public String ccbAddoperation130528(String tradeno,String otherinfo,Long spid){		
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("ccbCardno")){
			String cardNumber = map.get("ccbCardno");
			String opkey = spid + ":" + cardNumber;
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY, 4, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
	
	@RequestMapping("/trade/unionpayfast/shenzhenPingAn/addoperation.xhtml")
	@ResponseBody
	public String shenzhenPingAn(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = getOpkey(spid, cardNumber);
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 6, 1, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
	
	@RequestMapping("/trade/unionpayfast/guangzhouBocWeekOne/addoperation.xhtml")
	@ResponseBody
	public String guangzhouBocWeekOne(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = getOpkey(spid, cardNumber);
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_DAY * 6, 1, tradeno);
			if(allow) return "success"; 
		}
		return "false";
	}
	
	@RequestMapping("/trade/unionpayfast/guangzhouBocMonthTwo/addoperation.xhtml")
	@ResponseBody
	public String guangzhouBocMonthTwo(String tradeno,String otherinfo,Long spid){
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = getOpkey(spid, cardNumber);
			boolean allow = operationService.updateOperation(opkey+"_1", OperationService.ONE_DAY * 6, 1, tradeno);
			if(allow){
				allow = operationService.updateOperation(opkey+"_2", OperationService.ONE_DAY * 30, 2, tradeno);
				if(allow) return "success"; 
			}
		}
		return "false";
	}
	
	
	
	@RequestMapping("/gewapay/ccbWeekone.xhtml")
	public String ccbWeekone(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/ccbpayWeekOne.vm";
	}
	@RequestMapping("/ajax/trade/ccbWeekOne.xhtml")//����
	public String ccbWeekOne(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
				HttpServletRequest request, Long orderId, Long spid, String creditCard, ModelMap model){
		if(StringUtils.isBlank(StringUtil.findFirstByRegex(creditCard, CARD_PEX))){
			return showJsonError(model, "���ÿ��Ÿ�ʽ����");
		}
		if(!isValidCardbin(creditCard)) return showJsonError(model, "����Ч�Ľ������ÿ�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		String opkey = spid + ":"+ creditCard;
		boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_WEEK, 1);
		if(!allow){
			return showJsonError(model, "�Բ��𣬸��Żݻÿ�����μ�һ�Ρ�");
		}
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		ErrorCode<OrderContainer> discount = specialDiscountService.useSpecialDiscount(OrderConstant.ORDER_TYPE_TICKET, orderId, sd, ip);
		if(discount.isSuccess()) {
			GewaOrder order = discount.getRetval().getOrder();
			order.setOtherinfo(JsonUtils.addJsonKeyValue(order.getOtherinfo(), "ccbCardno", creditCard));
			daoService.saveObject(order);
			return showJsonSuccess(model, ""+discount.getRetval().getCurUsedDiscount().getAmount());
		}
		return showJsonError(model, discount.getMsg());
	}
	@RequestMapping("/trade/ccbpos/ccbWeekOne.xhtml")
	@ResponseBody
	public String ccbWeekOne(String tradeno,String otherinfo,Long spid){		
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("ccbCardno")){
			String cardNumber = map.get("ccbCardno");
			String opkey = spid + ":" + cardNumber;
			boolean allow = operationService.updateOperation(opkey, OperationService.ONE_WEEK, 1, tradeno);
			if(allow) return "success"; 
		}
		return "repea card����"+tradeno;
	}
	
}
