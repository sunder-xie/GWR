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

import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.model.api.ApiUser;
import com.gewara.support.ErrorCode;
import com.gewara.util.StringUtil;
import com.gewara.util.WebUtils;
/**
 * ��ͨ�ֹ��̳�
 * @author acerge(acerge@163.com)
 * @since 4:40:08 PM Apr 26, 2011
 */
@Controller
public class PartnerWogoController extends BasePartnerController{
	public ApiUser getWogo(){
		return daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_WOGO);
	}
	//��1����ѡ�񳡴�
	@RequestMapping("/partner/wogo/opiList.xhtml")
	public String wogoOpiList(Date fyrq, Long movieid, HttpServletResponse response, @CookieValue(value="ukey", required=false)String ukey, ModelMap model) {
		ApiUser apiUser = getWogo();
		if(StringUtils.isBlank(ukey)){
			ukey = PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/wogo/");
		}
		return opiList(apiUser, movieid, fyrq, "partner/wogo/step1.vm", model);
	}
	@RequestMapping("/partner/wogo/testSeat.xhtml")
	public String wogoChooseSeat(Long mpid, ModelMap model){
		String userid = "test001";
		ApiUser apiUser = getWogo();
		String encryptCode = StringUtil.md5(userid + apiUser.getPrivatekey());
		model.put("userid", userid);
		model.put("encryptCode", encryptCode);
		model.put("mpid", ""+mpid);
		return "redirect:/partner/wogo/chooseSeat.xhtml";
	}
	//�ڶ�����ѡ����λ
	@RequestMapping("/partner/wogo/chooseSeat.xhtml")
	public String wogoChooseSeat(HttpServletResponse response, HttpServletRequest request, 
			@CookieValue(value="ukey", required=false)String ukey, Long mpid, 
			String userid, String encryptCode, String encQryStr, ModelMap model){
		if(mpid==null) return showError(model, "ȱ�ٲ�����");
		ApiUser apiUser = getWogo();
		if(StringUtils.isNotBlank(userid)){
			String mycheck = StringUtil.md5(userid + apiUser.getPrivatekey());
			if(!StringUtils.equalsIgnoreCase(encryptCode, mycheck)){
				return showError(model, "�Ƿ��û���");
			}
			encQryStr = PartnerUtil.getParamStr(request, "userid");
			
		}else if(StringUtils.isNotBlank(encQryStr)){
			Map<String, String> reqParamMap = PartnerUtil.getParamMap(encQryStr);
			if(StringUtils.isBlank(reqParamMap.get("userid"))) return showError(model, "�Ƿ��û���");
		}else{
			 return showError(model, "�Ƿ��û���");
		}
		if(StringUtils.isBlank(ukey)){
			ukey = PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/wogo/");
			return showRedirect("/partner/wogo/chooseSeat.shtml?mpid=" + mpid + "&encQryStr=" + encQryStr, model);
		}else{
			return wogoChooseSeat(encQryStr, mpid, ukey, model);
		}
	}
	@RequestMapping("/partner/wogo/chooseSeat.shtml")
	public String wogoChooseSeat(String encQryStr, Long mpid, 
			@CookieValue(value="ukey", required=false) String ukey, ModelMap model){
		if(mpid==null) return showError(model, "ȱ�ٲ�����");
		if(StringUtils.isBlank(ukey)) return showError(model, "������Դ�д���");
		ApiUser apiUser = getWogo();
		model.put("encQryStr", encQryStr);
		return chooseSeat(apiUser, mpid, ukey, "partner/wogo/step2.vm", model);
	}
	@RequestMapping("/partner/wogo/seatPage.xhtml")
	public String seatPage(Long mpid, Long partnerid, Integer price,  @CookieValue(value="ukey", required=false) String ukey, ModelMap model){
		model.put("mpid", mpid);
		model.put("partnerid", partnerid);
		model.put("price", price);
		model.put("ukey", ukey);
		ErrorCode<String> code = addSeatData(mpid, partnerid, ukey, model);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		return "partner/seatPage.vm";
	}
	//������:����λ���Ӷ���
	@RequestMapping("/partner/wogo/addOrder.xhtml")
	public String wogoAddOrder(@CookieValue(value="ukey", required=false)String ukey,
			String captchaId, String captcha, Long mpid, String encQryStr, 
			@RequestParam("mobile")String mobile,
			@RequestParam("seatid")String seatid, HttpServletRequest request, ModelMap model){
		if(mpid==null || StringUtils.isBlank(ukey)) return showError(model, "ȱ�ٲ�����");
		boolean validCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		String returnUrl = "partner/wogo/chooseSeat.shtml?mpid=" + mpid + "&mobile=" + mobile + "&encQryStr=" + encQryStr + "&r="+ System.currentTimeMillis();
		if(!validCaptcha) return alertMessage(model, "��֤�����", returnUrl);
		ApiUser partner = getWogo();
		if(StringUtils.isBlank(encQryStr)) return showError(model, "�Ƿ��û���");
		Map<String, String> reqParamMap = PartnerUtil.getParamMap(encQryStr);
		String userid = reqParamMap.get("userid");
		if(StringUtils.isBlank(userid)) return showError(model, "�Ƿ��û���");

		return addOrder(mpid, mobile, seatid, ukey, returnUrl, partner, userid, WebUtils.getRemoteIp(request), model);
	}
	//���Ĳ���ȷ�϶���ȥ֧��
	@RequestMapping(method=RequestMethod.POST,value="/partner/wogo/saveOrder.xhtml")
	public String wogoSaveOrder(ModelMap model, String paymethod, String paybank, 
			@RequestParam("orderId")long orderId, String mobile, 
			@CookieValue(value="ukey", required=true)String ukey){
		return saveOrder(orderId, mobile, paymethod, paybank, ukey, model);
	}
	//���Ĳ���ȷ�϶���ȥ֧�������²鿴��
	@RequestMapping("/partner/wogo/showOrder.xhtml")
	public String wogoShowOrder(Long orderId, ModelMap model, 
			@CookieValue(value="ukey", required=true)String ukey){
		ApiUser partner = getWogo();
		return showOrder(ukey, orderId, partner, "partner/wogo/step3.vm", model);
	}
}
