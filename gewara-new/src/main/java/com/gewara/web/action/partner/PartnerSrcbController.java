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
import com.gewara.pay.ChinapayUtil;
import com.gewara.support.ErrorCode;
import com.gewara.util.StringUtil;
import com.gewara.util.WebUtils;
/**
 * 上海农商银行
 * @author acerge(acerge@163.com)
 * @since 4:44:05 PM Mar 31, 2011
 */
@Controller
public class PartnerSrcbController extends BasePartnerController{
	public ApiUser getSrcb(){
		return daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_SRCB);
	}
	@RequestMapping("/partner/srcb/opiTest.xhtml")
	public String srcbOpiList(ModelMap model) {
		ApiUser apiUser = getSrcb();
		String termId = "T0001";
		String check = StringUtil.md5(termId+apiUser.getPrivatekey());
		model.put("termId", termId);
		model.put("check", check);
		return "redirect:/partner/srcb/opiList.xhtml";
	}
	
	//第1步：选择场次
	@RequestMapping("/partner/srcb/opiList.xhtml")
	public String srcbOpiList(HttpServletRequest request, HttpServletResponse response, 
			Date fyrq, Long movieid, String termId, String check, String encQryStr,
			@CookieValue(value="ukey", required=false)String ukey, ModelMap model) {
		ApiUser apiUser = getSrcb();
		if(StringUtils.isBlank(ukey)){
			ukey = PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/srcb/");
		}
		if(StringUtils.isNotBlank(termId)){
			String mycheck = StringUtil.md5(termId+apiUser.getPrivatekey());
			if(!mycheck.equalsIgnoreCase(check)) return showMessage(model, "校验错误!");
			encQryStr = PartnerUtil.getParamStr(request, "termId");
		}else if(StringUtils.isNotBlank(encQryStr)){
			Map<String, String> reqParamMap = PartnerUtil.getParamMap(encQryStr);
			if(StringUtils.isBlank(reqParamMap.get("termId"))) return showError(model, "终端错误！");
		}else{
			return showError(model, "终端错误！");
		}
		model.put("encQryStr", encQryStr);
		return opiList(apiUser, movieid, fyrq, "partner/srcb/step1.vm", model);
	}
	//第二步：选择座位
	@RequestMapping("/partner/srcb/chooseSeat.xhtml")
	public String srcbChooseSeat(HttpServletResponse response, String encQryStr, 
			@CookieValue(value="ukey", required=false)String ukey, Long mpid, ModelMap model){
		if(mpid==null) return showError(model, "缺少参数！");
		if(StringUtils.isBlank(ukey)){
			ukey = PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/srcb/");
			return showRedirect("/partner/srcb/chooseSeat.shtml?mpid=" + mpid + "&encQryStr=" + encQryStr, model);
		}else{
			return srcbChooseSeat(encQryStr, mpid, ukey, model);
		}
	}
	@RequestMapping("/partner/srcb/chooseSeat.shtml")
	public String srcbChooseSeat(String encQryStr, Long mpid, 
			@CookieValue(value="ukey", required=false) String ukey, ModelMap model){
		if(mpid==null) return showError(model, "缺少参数！");
		if(StringUtils.isBlank(ukey)) return showError(model, "链接来源有错误！");
		ApiUser apiUser = getSrcb();
		model.put("encQryStr", encQryStr);
		return chooseSeat(apiUser, mpid, ukey, "partner/srcb/step2.vm", model);
	}
	@RequestMapping("/partner/srcb/seatPage.xhtml")
	public String seatPage(Long mpid, Long partnerid, Integer price,  @CookieValue(value="ukey", required=false) String ukey, ModelMap model){
		model.put("mpid", mpid);
		model.put("partnerid", partnerid);
		model.put("price", price);
		model.put("ukey", ukey);
		ErrorCode<String> code = addSeatData(mpid, partnerid, ukey, model);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		return "partner/seatPage.vm";
	}
	//第三步:锁座位，加订单
	@RequestMapping("/partner/srcb/addOrder.xhtml")
	public String srcbAddOrder(@CookieValue(value="ukey", required=false)String ukey,
			String captchaId, String captcha, Long mpid, String encQryStr, 
			@RequestParam("mobile")String mobile,
			@RequestParam("seatid")String seatid, HttpServletRequest request, ModelMap model){
		if(mpid==null || StringUtils.isBlank(ukey)) return showError(model, "缺少参数！");
		boolean validCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		Map<String, String> reqParamMap = PartnerUtil.getParamMap(encQryStr);
		String termId = reqParamMap.get("termId");
		if(StringUtils.isBlank(termId)) return showError(model, "终端错误！");

		String returnUrl = "partner/srcb/chooseSeat.shtml?mpid=" + mpid + "&mobile=" + mobile + "&encQryStr=" + encQryStr + "&r="+ System.currentTimeMillis();
		if(!validCaptcha) return alertMessage(model, "验证码错误！", returnUrl);
		ApiUser partner = getSrcb();
		if(partner == null) return alertMessage(model, "非法订单，来源不正确！", returnUrl);
		return addOrder(mpid, mobile, seatid, ukey, returnUrl, partner, termId, WebUtils.getRemoteIp(request), model);
	}
	//第四步：确认订单去支付
	@RequestMapping(method=RequestMethod.POST,value="/partner/srcb/saveOrder.xhtml")
	public String srcbSaveOrder(ModelMap model, @RequestParam("orderId")long orderId, 
			String mobile, @CookieValue(value="ukey", required=true)String ukey){
		return saveOrder(orderId, mobile, PaymethodConstant.PAYMETHOD_CHINAPAYSRCB, ChinapayUtil.BANK_CODE_SRCB, ukey, model);
	}
	//第四步：确认订单去支付（重新查看）
	@RequestMapping("/partner/srcb/showOrder.xhtml")
	public String srcbShowOrder(Long orderId, ModelMap model, 
			@CookieValue(value="ukey", required=true)String ukey){
		ApiUser partner = getSrcb();
		return showOrder(ukey, orderId, partner, "partner/srcb/step3.vm", model);
	}
}
