package com.gewara.web.action.partner;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.model.api.ApiUser;
import com.gewara.support.ErrorCode;
import com.gewara.util.WebUtils;
/**
 * �Ϻ�����
 * @author acerge(acerge@163.com)
 * @since 6:41:43 PM Apr 20, 2010
 */
@Controller
public class PartnerOnlineController extends BasePartnerController{
	public ApiUser getOnline(){
		return daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_ONLINE);
	}
	//��1����ѡ�񳡴�
	@RequestMapping("/partner/online/opiList.xhtml")
	public String onlineOpiList(Date fyrq, Long movieid, ModelMap model) {
		ApiUser apiUser = getOnline();
		return opiList(apiUser, movieid, fyrq, "partner/online/step1.vm", model);
	}
	//�ڶ�����ѡ����λ
	@RequestMapping("/partner/online/chooseSeat.xhtml")
	public String onlineChooseSeat(HttpServletResponse response, 
			@CookieValue(value="ukey", required=false)String ukey, String mpid, ModelMap model){
		if(StringUtils.isBlank(mpid) || "null".equals(mpid)){
			return showError(model, "ȱ�ٲ�����");
		}
		if(StringUtils.isBlank(ukey)){
			ukey = PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/online/");
			return showRedirect("partner/online/chooseSeat.shtml?mpid=" + mpid, model);
		}else{
			return onlineChooseSeat(ukey, mpid, model);
		}
	}
	@RequestMapping("/partner/online/chooseSeat.shtml")
	public String onlineChooseSeat(@CookieValue(value="ukey", required=false) String ukey, String mpid, ModelMap model){
		if(StringUtils.isBlank(mpid) || "null".equals(mpid)) return showError(model, "ȱ�ٲ�����");
		if(StringUtils.isBlank(ukey)) return showError(model, "������Դ�д���");
		ApiUser apiUser = getOnline();
		return chooseSeat(apiUser, new Long(mpid), ukey, "partner/online/step2.vm", model);
	}
	@RequestMapping("/partner/online/seatPage.xhtml")
	public String seatPage(Long mpid, Long partnerid, Integer price,  @CookieValue(value="ukey", required=false) String ukey, ModelMap model){
		model.put("mpid", mpid);
		model.put("partnerid", partnerid);
		model.put("price", price);
		model.put("ukey", ukey);
		if(StringUtils.isBlank(ukey)){
			return showJsonError(model, "ȱ�ٲ�������ˢ�����ԣ�");
		}
		ErrorCode<String> code = addSeatData(mpid, partnerid, ukey, model);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		return "partner/seatPage.vm";
	}
	//������:����λ���Ӷ���
	@RequestMapping("/partner/online/addOrder.xhtml")
	public String onlineAddOrder(@CookieValue(value="ukey", required=false)String ukey,
			String captchaId, String captcha, Long mpid,
			@RequestParam("mobile")String mobile,
			@RequestParam("seatid")String seatid, HttpServletRequest request, ModelMap model){
		if(mpid==null) return showError(model, "ȱ�ٲ�����");
		boolean validCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		String returnUrl = "partner/online/chooseSeat.shtml?mpid=" + mpid + "&mobile=" + mobile + "&r="+ System.currentTimeMillis();
		if(!validCaptcha) return alertMessage(model, "��֤�����", returnUrl);
		ApiUser partner = getOnline();
		return addOrder(mpid, mobile, seatid, ukey, returnUrl, partner, "", WebUtils.getRemoteIp(request), model);
	}
	//���Ĳ���ȷ�϶���ȥ֧��
	@RequestMapping(method=RequestMethod.POST,value="/partner/online/saveOrder.xhtml")
	public String onlineSaveOrder(ModelMap model, @RequestParam("orderId")long orderId, 
			String mobile, @CookieValue(value="ukey", required=false)String ukey){
		String paymethod = "partnerPay";
		return saveOrder(orderId, mobile, paymethod, "", ukey, model);
	}
	//���Ĳ���ȷ�϶���ȥ֧�������²鿴��
	@RequestMapping("/partner/online/showOrder.xhtml")
	public String sdoShowOrder(Long orderId, ModelMap model, 
			@CookieValue(value="ukey", required=false)String ukey){
		ApiUser partner = getOnline();
		return showOrder(ukey, orderId, partner, "partner/online/step3.vm", model);
	}
}
