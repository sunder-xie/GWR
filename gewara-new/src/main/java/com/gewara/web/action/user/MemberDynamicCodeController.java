package com.gewara.web.action.user;

import java.util.HashMap;
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

import com.gewara.constant.BindConstant;
import com.gewara.constant.MemberConstant;
import com.gewara.constant.TokenType;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.service.member.BindMobileService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.MonitorService;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.BaseHomeController;

@Controller
public class MemberDynamicCodeController extends BaseHomeController {
	@Autowired@Qualifier("monitorService")
	private MonitorService monitorService;
	public void setMonitorService(MonitorService monitorService) {
		this.monitorService = monitorService;
	}
	@Autowired@Qualifier("bindMobileService")
	private BindMobileService bindMobileService;
	public void setBindMobileService(BindMobileService bindMobileService){
		this.bindMobileService = bindMobileService;
	}
	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;
	public void setPaymentService(PaymentService paymentService){
		this.paymentService = paymentService;
	}
	@RequestMapping("/ajax/mobile/achieve.xhtml")
	public String achieveCode(HttpServletRequest request, String captchaId, String captcha, String mobile, ModelMap model){
		Map jsonMap = new HashMap();
		Map errorMap = new HashMap();
		jsonMap.put("errorMap", errorMap);
		if(StringUtils.isBlank(mobile)) {
			errorMap.put(mobile,  "�������ֻ��ţ�");
			return showJsonError(model,jsonMap);
		}
		String ip = WebUtils.getRemoteIp(request);
		/*boolean iscaptcha = bindMobileService.isNeedToken(TokenType.Register, ip, 2);
		if(iscaptcha){*/
		jsonMap.put("refreshCaptcha", "true");
		model.put("iscaptcha", true);
		if(StringUtils.isBlank(captcha)){
			errorMap.put("captcha", "��������֤�룡");
			return showJsonError(model, jsonMap);
		}else{
			boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
			errorMap.put("captcha", "��֤�����");
			if(!isValidCaptcha) return showJsonError(model, jsonMap);
		}
		/*}
		boolean next = bindMobileService.getAndUpdateToken(TokenType.Register, ip, 2);
		if(next){
			jsonMap.put("refreshCaptcha", "true");
		}*/

		if(!ValidateUtil.isMobile(mobile)) {
			errorMap.put(mobile,  "�ֻ����������");
			return showJsonError(model,jsonMap);
		}
		ErrorCode<SMSRecord> code = bindMobileService.refreshBindMobile(BindConstant.TAG_REGISTERCODE, mobile, ip);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		untransService.sendMsgAtServer(code.getRetval(), false);
		return showJsonSuccess(model);
	}
	@RequestMapping("/ajax/mobile/register.xhtml")
	@ResponseBody
	public String registerCode(){
		return "ok";
	}
	@RequestMapping("/ajax/mobile/newRegister.xhtml")
	public String registerCode(HttpServletRequest request, String captchaId, String captcha, String mobile, ModelMap model){
		Map jsonMap = new HashMap();
		Map errorMap = new HashMap();
		jsonMap.put("errorMap", errorMap);
		if(StringUtils.isBlank(mobile)){
			errorMap.put("mobile", "�������ֻ��ţ�");
			return showJsonError(model, jsonMap);
		}
		String ip = WebUtils.getRemoteIp(request);
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, ip);
		if(!isValidCaptcha){
			errorMap.put("captcha", "��֤�����");
			return showJsonError(model, jsonMap);
		}
		boolean next = bindMobileService.getAndUpdateToken(TokenType.Register, ip, 2);
		if(next){
			jsonMap.put("refreshCaptcha", "true");
		}

		if(!ValidateUtil.isMobile(mobile)){
			errorMap.put("mobile","�ֻ����������");
			return showJsonError(model, jsonMap);
		}
		Member member = daoService.getObjectByUkey(Member.class, "mobile", mobile, false);
		if(member != null){
			MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
			Map<String, String> otherInfoMap = JsonUtils.readJsonToMap(memberInfo.getOtherinfo());
			String bindstats = otherInfoMap.get(MemberConstant.TAG_SOURCE);
			if(!StringUtils.equals(memberInfo.getSource(), MemberConstant.REGISTER_CODE) ||(StringUtils.equals(memberInfo.getSource(), MemberConstant.REGISTER_CODE) && !StringUtils.equals(bindstats, "fail"))){
				errorMap.put("mobile", "���ֻ����ѱ�ʹ�ã�<a href='/login.xhtml' style='color:#333'>������¼</a>");
				return showJsonError(model, jsonMap);
			}
		}
		ErrorCode<SMSRecord> code = bindMobileService.refreshBindMobile(BindConstant.TAG_REGISTERCODE, mobile, ip);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		untransService.sendMsgAtServer(code.getRetval(), false);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/ajax/mobile/asynchLogin.xhtml")
	public String opiLogin(String mobile, String checkpass, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		Map jsonMap = new HashMap();
		Map errorMap = new HashMap();
		jsonMap.put("errorMap", errorMap);
		if(StringUtils.isBlank(checkpass)) {
			errorMap.put("checkpass",  "��̬�벻��Ϊ�գ�");
			return showJsonError(model,jsonMap);
		}
		if(!ValidateUtil.isMobile(mobile)) {
			errorMap.put("mobile", "�ֻ������ʽ����ȷ��");
			return showJsonError(model,jsonMap);
		}
		
		String citycode = WebUtils.getAndSetDefault(request, response);
		ErrorCode<Member> memberCode = memberService.createMemberWithBindMobile(mobile, checkpass, citycode, WebUtils.getRemoteIp(request));
		if(!memberCode.isSuccess()) return showJsonError(model, memberCode.getMsg());
		Member member = memberCode.getRetval();
		loginService.autoLoginByDyncode(request, response, member);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/ajax/mobile/modifyPassword.xhtml")
	public String modifyPassword(HttpServletRequest request, String captchaId, String captcha, String mobile, ModelMap model){
		Map jsonMap = new HashMap();
		Map errorMap = new HashMap();
		String ip = WebUtils.getRemoteIp(request);
		boolean iscaptcha = bindMobileService.isNeedToken(TokenType.ModifyPass, ip, 2);
		jsonMap.put("errorMap", errorMap);
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
		boolean next = bindMobileService.getAndUpdateToken(TokenType.ModifyPass, ip, 2);
		if(next){
			jsonMap.put("refreshCaptcha", "true");
		}
		if(StringUtils.isBlank(mobile)) {
			errorMap.put(mobile, "�ֻ��Ų���Ϊ�գ�");
			return showJsonError(model,jsonMap);
		}
		if(!ValidateUtil.isMobile(mobile)) {
			errorMap.put(mobile, "�ֻ������ʽ����ȷ��");
			return showJsonError(model, jsonMap);
		}
		Member member = daoService.getObjectByUkey(Member.class, "mobile", mobile, false);
		if(member == null) {
			errorMap.put(mobile, "���ֻ��Ż�δע�ᣬ��������д��");
			return showJsonError(model, jsonMap);
		}
		String checkMsg = checkMemberResource(member);
		if(StringUtils.isNotBlank(checkMsg)) return showJsonError(model, checkMsg);
		ErrorCode<SMSRecord> code = bindMobileService.refreshBindMobile(BindConstant.TAG_BACKPASS, mobile, ip);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		untransService.sendMsgAtServer(code.getRetval(), false);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/ajax/mobile/savePassword.xhtml")
	public String savePassword(String mobile, String checkpass, String password, String repassword, ModelMap model, HttpServletRequest request){
		Map jsonMap = new HashMap();
		Map errorMap = new HashMap();
		jsonMap.put("errorMap", errorMap);
		if(StringUtils.isBlank(checkpass)) {
			errorMap.put(checkpass, "��̬�벻��Ϊ�գ�");
			return showJsonError(model,jsonMap);
		}
		if(StringUtils.isBlank(password)) {
			errorMap.put(password,  "���벻��Ϊ�գ�");
			return showJsonError(model,jsonMap);
		}
		if(StringUtils.isBlank(repassword)) {
			errorMap.put(repassword,  "ȷ�����벻��Ϊ�գ�");
			return showJsonError(model,jsonMap);
		}
		if(!StringUtils.equals(password, repassword)) {
			errorMap.put(repassword, "ȷ���������¼���벻һ�£�");
			return showJsonError(model,jsonMap);
		}
		if(!ValidateUtil.isPassword(password)){
			errorMap.put(password, "�����ʽ����ȷ,ֻ������ĸ�����֣��»��ߣ�����6��14λ��");
			return showJsonError(model, jsonMap);
		}
		if(!ValidateUtil.isMobile(mobile)) {
			errorMap.put(mobile, "�ֻ������ʽ����ȷ��");
			return showJsonError(model,jsonMap);
		}
		Member member = daoService.getObjectByUkey(Member.class, "mobile", mobile, false);
		if(member == null) {
			errorMap.put(mobile, "���ֻ��Ų����ڣ�");
			return showJsonError(model, jsonMap);
		}
		if(StringUtils.isNotBlank(member.getEmail())){
			boolean danger = baoKuService.isDanger(member.getEmail(), password);
			if(danger) return showJsonError(model, "���ʻ����õ�������ڰ�ȫ���գ���������Ϊ�����룡");
		}
		ErrorCode code = bindMobileService.checkBindMobile(BindConstant.TAG_BACKPASS, mobile, checkpass);
		if(!code.isSuccess()){
			jsonMap.put("msg", code.getMsg());
			return showJsonError(model, jsonMap);
		}
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		Map<String, String> otherInfoMap = JsonUtils.readJsonToMap(memberInfo.getOtherinfo());
		otherInfoMap.remove(MemberConstant.TAG_DANGER);
		memberInfo.setOtherinfo(JsonUtils.writeMapToJson(otherInfoMap));
		member.setPassword(StringUtil.md5(password));
		daoService.saveObjectList(member, memberInfo);
		monitorService.saveMemberLog(member.getId(), MemberConstant.ACTION_MODPWD, null, WebUtils.getRemoteIp(request));
		return showJsonSuccess(model);
	}
	private String checkMemberResource(Member member){
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		if(StringUtils.equals(memberInfo.getSource(), MemberConstant.REGISTER_APP) || StringUtils.equals(memberInfo.getSource(), MemberConstant.REGISTER_CODE)){
			Map<String,String> otherInfoMap = JsonUtils.readJsonToMap(memberInfo.getOtherinfo());
			if(StringUtils.equals(otherInfoMap.get(MemberConstant.TAG_SOURCE), "fail")){
				return "���ʺŻ�û�����õ�¼���룬����ʹ���ֻ���̬���¼���������룡";
			}
		}
		return null;
	}
	
	@RequestMapping("/home/acct/obtain.xhtml")
	public String obtainCode(HttpServletRequest request, ModelMap model){
		Member member = getLogonMember();
		if(!member.isBindMobile()) return showJsonError(model, "���Ȱ��ֻ��ţ�");
		
		ErrorCode<SMSRecord> code = bindMobileService.refreshBindMobile(BindConstant.TAG_ACCOUNT_BACKPASS , member.getMobile(), WebUtils.getRemoteIp(request));
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		untransService.sendMsgAtServer(code.getRetval(), false);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/home/acct/modifyPayPassword.xhtml")
	public String modifyPayPassword(ModelMap model){
		Member member = getLogonMember();
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		return "home/acct/forgetPasswordstep1.vm";
	}
	
	@RequestMapping("/home/acct/saveAccountPass.xhtml")
	public String saveAccountPass(String checkpass, String password, String repassword, HttpServletRequest request, ModelMap model){
		Member member = getLogonMember();
		Map jsonMap = new HashMap();
		Map errorMap = new HashMap();
		jsonMap.put("errorMap", errorMap);
		if(StringUtils.isBlank(checkpass)){
			errorMap.put("checkpass", "��̬�벻��Ϊ�գ�");
			return showJsonError(model, jsonMap);
		}
		ErrorCode<Map> mapCode = paymentService.mobileResetAccountPass(member, password, repassword, checkpass);
		if(!mapCode.isSuccess()){
			Map dataMap = mapCode.getRetval();
			String msg = (String) dataMap.get("msg");
			if(StringUtils.isNotBlank(msg)){
				jsonMap.putAll(dataMap);
			}else errorMap.putAll(dataMap);
			return showJsonError(model, jsonMap);
		}
		monitorService.saveMemberLog(member.getId(), MemberConstant.ACTION_MDYPAYPWD, null, WebUtils.getRemoteIp(request));
		return showJsonSuccess(model);
	}
	//����Ʊȯʱ��ö�̬��
	@RequestMapping("/ajax/mobile/achieveElecCard.xhtml")
	public String achieveElecCardCode(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, String mobile, 
			 HttpServletRequest request, ModelMap model){
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		if(!ValidateUtil.isMobile(mobile)) return showJsonError(model, "�ֻ������ʽ����ȷ��");
		if(member.isBindMobile() && !StringUtils.equals(member.getMobile(), mobile)) return showJsonError(model, "checkband");
		Map jsonMap = new HashMap();
		Map errorMap = new HashMap();
		jsonMap.put("errorMap", errorMap);
		String ip = WebUtils.getRemoteIp(request);
		ErrorCode<SMSRecord> code = bindMobileService.refreshBindMobile(BindConstant.TAG_DYNAMICCODE_CARD, mobile, ip);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		untransService.sendMsgAtServer(code.getRetval(), false);
		return showJsonSuccess(model);
	}
}
