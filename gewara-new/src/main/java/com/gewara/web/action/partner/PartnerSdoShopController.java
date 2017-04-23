package com.gewara.web.action.partner;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.model.api.ApiUser;
import com.gewara.pay.PayUtil;
import com.gewara.pay.SpSdoUtil;
import com.gewara.support.ErrorCode;
import com.gewara.util.ValidateUtil;
import com.gewara.util.WebUtils;
/**
 * ʢ���̳�
 * @author acerge(acerge@163.com)
 * @since 2:37:33 PM Sep 1, 2010
 */
@Controller
public class PartnerSdoShopController extends BasePartnerController{
	public ApiUser getSpsdo(){
		return daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_SPSDO);
	}
	//test
	@RequestMapping("/partner/spsdo/testSeat2.xhtml")
	public String spsdoChooseSeat2(String mpid, ModelMap model){
		String mobile = "13651668441";
		String orderid = "xx"+PayUtil.getChargeTradeNo();
		String version = "101008a";
		String paychannel = "04,17";
		model.put("mpid", mpid);
		model.put("mobile", mobile);
		model.put("orderid", orderid);
		model.put("version", version);
		model.put("paychannel", paychannel);
		String mac = SpSdoUtil.getMac(mobile, ""+mpid, orderid, paychannel, version);
		model.put("mac", mac);
		return "redirect:/partner/spsdo/chooseSeat.xhtml";
		//md5(version +MPID+ orderid + Mobile+ event + price + paychannel + md5Key)
	}
	@RequestMapping("/partner/spsdo/testSeat.xhtml")
	public String spsdoChooseSeat(String mpid, ModelMap model){
		String bankcode = "ICBC";
		String event = "spsdoTest";
		String mobile = "13651668441";
		String orderid = "xx"+PayUtil.getChargeTradeNo();
		String paychannel = "04";
		String price = "40";
		String version = "101008a";
		model.put("bankcode", bankcode);
		model.put("event", event);
		model.put("mpid", mpid);
		model.put("mobile", mobile);
		model.put("orderid", orderid);
		model.put("paychannel", paychannel);
		model.put("price", price);
		model.put("version", version);
		String mac = SpSdoUtil.getMac(bankcode, event, mobile, ""+mpid, orderid, paychannel, price, version);
		model.put("mac", mac);
		return "redirect:/partner/spsdo/chooseSeat.xhtml";
		//md5(version +MPID+ orderid + Mobile+ event + price + paychannel + md5Key)
	}
	//��1����ѡ�񳡴�
	@RequestMapping("/partner/spsdo/opiList.xhtml")
	public String spsdoOpiList(Date fyrq, Long movieid, ModelMap model) {
		ApiUser apiUser = getSpsdo();
		if(apiUser==null) return showMessage(model, "�Ƿ��û������ܶ�����");
		return opiList(apiUser, movieid, fyrq, "partner/spsdo/step1.vm", model);
	}
	//�ڶ�����ѡ����λ
	@RequestMapping("/partner/spsdo/chooseSeat.xhtml")
	public String spsdoChooseSeat(HttpServletRequest request, 
			String bankcode, String event, String mobile, Long mpid, 
			String orderid, String paychannel, String price, String version,
			String mac, String encQryStr, ModelMap model){
		if(mpid==null) return showError(model, "ȱ�ٲ�����");
		String ukey = "";
		if(StringUtils.isNotBlank(orderid)){
			if(!ValidateUtil.isMobile(mobile)) return forwardMessage(model, "�ֻ��Ų���ȷ��");
			String macnew = SpSdoUtil.getMac(bankcode, event, mobile, ""+mpid, orderid, paychannel, price, version);
			String macold = SpSdoUtil.getMac("" + mpid, mobile, orderid);
			if(!StringUtils.equalsIgnoreCase(macold, mac) && !StringUtils.equalsIgnoreCase(macnew,mac)) return showError(model, "����У�����");
			encQryStr = PartnerUtil.getParamStr(request);
			ukey = orderid;
			model.put("mobile", mobile);
		}else{
			Map<String, String> reqParamMap = PartnerUtil.getParamMap(encQryStr);
			if(!StringUtils.equals(""+mpid, reqParamMap.get("mpid"))) {
				return showError(model, StringUtils.defaultIfEmpty(reqParamMap.get("ERROR_MSG"), "���ӵ�ַ��Դ����"));
			}
			ukey = reqParamMap.get("orderid");
			model.put("mobile", reqParamMap.get("mobile"));
		}
		model.put("encQryStr", encQryStr);
		ApiUser apiUser = getSpsdo();
		
		return chooseSeat(apiUser, mpid, ukey, "partner/spsdo/step2.vm", model);
	}
	@RequestMapping("/partner/spsdo/seatPage.xhtml")
	public String seatPage(Long mpid, Long partnerid, Integer price,  
			@CookieValue(value="ukey", required=false) String ukey, HttpServletResponse response, ModelMap model){
		model.put("mpid", mpid);
		model.put("partnerid", partnerid);
		model.put("price", price);
		model.put("ukey", ukey);
		if(StringUtils.isBlank(ukey)) ukey = PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/spsdo/");
		ErrorCode<String> code = addSeatData(mpid, partnerid, ukey, model);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		return "partner/seatPage.vm";
	}
	//������:����λ���Ӷ���
	@RequestMapping("/partner/spsdo/addOrder.xhtml")
	public String spsdoAddOrder(String captchaId, String captcha, Long mpid, String encQryStr,
			@RequestParam("seatid")String seatid, HttpServletRequest request, ModelMap model){
		if(mpid==null) return showError(model, "ȱ�ٲ�����");
		boolean validCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		String returnUrl = "partner/spsdo/chooseSeat.xhtml?mpid=" + mpid + "&encQryStr=" + encQryStr + "&r="+ System.currentTimeMillis();
		if(!validCaptcha) return alertMessage(model, "��֤�����", returnUrl);
		ApiUser partner = getSpsdo();
		Map<String, String> reqParamMap = PartnerUtil.getParamMap(encQryStr);
		if(!StringUtils.equals(""+mpid, reqParamMap.get("mpid"))) {
			return showError(model, StringUtils.defaultIfEmpty(reqParamMap.get("ERROR_MSG"), "���ӵ�ַ��Դ����"));
		}
		String event = reqParamMap.get("event");
		String ukey = reqParamMap.get("orderid");
		model.put("encQryStr", encQryStr);
		String mobile = reqParamMap.get("mobile");
		return addOrder(mpid, mobile, seatid, ukey, returnUrl, partner, reqParamMap.get("orderid"), event, PaymethodConstant.PAYMETHOD_SPSDOPAY1, WebUtils.getRemoteIp(request), model);
	}
	//���Ĳ���ȷ�϶���ȥ֧��
	@RequestMapping(method=RequestMethod.POST,value="/partner/spsdo/saveOrder.xhtml")
	public String spsdoSaveOrder(ModelMap model, String paymethod, 
			@RequestParam("orderId")long orderId, String encQryStr){
		Map<String, String> reqParamMap = PartnerUtil.getParamMap(encQryStr);
		if(StringUtils.isBlank(reqParamMap.get("mpid"))) {
			return showError(model, StringUtils.defaultIfEmpty(reqParamMap.get("ERROR_MSG"), "���ӵ�ַ��Դ����"));
		}
		String paybank = reqParamMap.get("paychannel");
		if(StringUtils.isNotBlank(reqParamMap.get("bankcode"))){
			paybank = paybank + ":" + reqParamMap.get("bankcode");
		}
		model.put("encQryStr", encQryStr);
		String ukey = reqParamMap.get("orderid");
		return saveOrder(orderId, "", paymethod, paybank, ukey, model);
	}
	//���Ĳ���ȷ�϶���ȥ֧�������²鿴��
	@RequestMapping("/partner/spsdo/showOrder.xhtml")
	public String spsdoShowOrder(Long orderId, String encQryStr, ModelMap model){
		Map<String, String> reqParamMap = PartnerUtil.getParamMap(encQryStr);
		if(StringUtils.isBlank(reqParamMap.get("mpid"))) {
			return showError(model, StringUtils.defaultIfEmpty(reqParamMap.get("ERROR_MSG"), "���ӵ�ַ��Դ����"));
		}
		ApiUser partner = getSpsdo();
		String ukey = reqParamMap.get("orderid");
		model.put("encQryStr", encQryStr);
		return showOrder(ukey, orderId, partner, "partner/spsdo/step3.vm", model);
	}
}
