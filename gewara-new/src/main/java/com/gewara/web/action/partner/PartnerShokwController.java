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
 *	����E������
 * @author acerge(acerge@163.com)
 * @since 6:40:59 PM Apr 20, 2010
 */
@Controller
public class PartnerShokwController extends BasePartnerController{
	public ApiUser getShokw(){
		return daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_SHOKW);
	}
	//��1����ѡ�񳡴�
	@RequestMapping("/partner/shokw/opiList.xhtml")
	public String shokwOpiList(Date fyrq, Long movieid, ModelMap model) {
		ApiUser apiUser = getShokw();
		return opiList(apiUser, movieid, fyrq, "partner/shokw/step1.vm", model);
	}
	//�ڶ�����ѡ����λ
	@RequestMapping("/partner/shokw/chooseSeat.xhtml")
	public String shokwChooseSeat(HttpServletResponse response, 
			@CookieValue(value="ukey", required=false)String ukey, Long mpid, ModelMap model){
		if(mpid==null) return showError(model, "ȱ�ٲ�����");
		if(StringUtils.isBlank(ukey)){
			ukey = PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/shokw/");
			return showRedirect("partner/shokw/chooseSeat.shtml?mpid=" + mpid, model);
		}else{
			return shokwChooseSeat(ukey, mpid, model);
		}
	}
	@RequestMapping("/partner/shokw/chooseSeat.shtml")
	public String shokwChooseSeat(@CookieValue(value="ukey", required=false) String ukey, Long mpid, ModelMap model){
		if(mpid==null) return showError(model, "ȱ�ٲ�����");
		if(StringUtils.isBlank(ukey)) return showError(model, "������Դ�д���");
		ApiUser apiUser = getShokw();
		return chooseSeat(apiUser, mpid, ukey, "partner/shokw/step2.vm", model);
	}
	@RequestMapping("/partner/shokw/seatPage.xhtml")
	public String seatPage(Long mpid, Long partnerid, Integer price,  
			@CookieValue(value="ukey", required=false) String ukey, HttpServletResponse response, ModelMap model){
		model.put("mpid", mpid);
		model.put("partnerid", partnerid);
		model.put("price", price);
		model.put("ukey", ukey);
		if(StringUtils.isBlank(ukey)) ukey = PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/shokw/");
		ErrorCode<String> code = addSeatData(mpid, partnerid, ukey, model);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		return "partner/seatPage.vm";
	}
	//������:����λ���Ӷ���
	@RequestMapping("/partner/shokw/addOrder.xhtml")
	public String shokwAddOrder(@CookieValue(value="ukey", required=false)String ukey,
			String captchaId, String captcha, Long mpid,
			@RequestParam("mobile")String mobile,
			@RequestParam("seatid")String seatid, HttpServletRequest request, ModelMap model){
		if(mpid==null) return showError(model, "ȱ�ٲ�����");
		boolean validCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		String returnUrl = "partner/shokw/chooseSeat.shtml?mpid=" + mpid + "&mobile=" + mobile + "&r="+ System.currentTimeMillis();
		if(!validCaptcha) return alertMessage(model, "��֤�����", returnUrl);
		ApiUser partner = getShokw();
		if(partner == null) return alertMessage(model, "�Ƿ���������Դ����ȷ��", returnUrl);
		return addOrder(mpid, mobile, seatid, ukey, returnUrl, partner, "", WebUtils.getRemoteIp(request), model);
	}
	//���Ĳ���ȷ�϶���ȥ֧��
	@RequestMapping(method=RequestMethod.POST,value="/partner/shokw/saveOrder.xhtml")
	public String shokwSaveOrder(ModelMap model, String paymethod, String paybank, 
			@RequestParam("orderId")long orderId, String mobile, 
			@CookieValue(value="ukey", required=false)String ukey){
		return saveOrder(orderId, mobile, paymethod, paybank, ukey, model);
	}
	//���Ĳ���ȷ�϶���ȥ֧�������²鿴��
	@RequestMapping("/partner/shokw/showOrder.xhtml")
	public String sdoShowOrder(Long orderId, ModelMap model, 
			@CookieValue(value="ukey", required=false)String ukey){
		ApiUser partner = getShokw();
		return showOrder(ukey, orderId, partner, "partner/shokw/step3.vm", model);
	}
}
