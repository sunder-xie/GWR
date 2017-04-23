package com.gewara.web.action.user;

import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.Config;
import com.gewara.constant.MemberConstant;
import com.gewara.constant.order.AddressConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.json.ValidEmail;
import com.gewara.model.movie.Cinema;
import com.gewara.model.sport.Sport;
import com.gewara.model.user.HiddenMember;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.pay.PayUtil;
import com.gewara.service.OperationService;
import com.gewara.service.PlaceService;
import com.gewara.service.bbs.BlogService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.gym.SynchGymService;
import com.gewara.untrans.monitor.MonitorService;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.BaseHomeController;
@Controller
public class MemberRegController extends BaseHomeController {
	public static final List<String> TAG_CHECK = Arrays.asList("mobile", "email", "nickname");
	@Autowired@Qualifier("monitorService")
	private MonitorService monitorService;
	public void setMonitorService(MonitorService monitorService) {
		this.monitorService = monitorService;
	}
	@Autowired@Qualifier("synchGymService")
	private SynchGymService synchGymService;
	
	@Autowired@Qualifier("placeService")
	private PlaceService placeService;
	public void setPlaceService(PlaceService placeService) {
		this.placeService = placeService;
	}
	@Autowired@Qualifier("config")
	private Config config;
	public void setConfig(Config config) {
		this.config = config;
	}

	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}
	@Autowired@Qualifier("blogService")
	private BlogService blogService;
	public void setBlogService(BlogService blogService) {
		this.blogService = blogService;
	}

	@RequestMapping("/register.xhtml")
	public String register(HttpServletResponse res, ModelMap model, Long from, String encode, 
			HttpServletRequest request, HttpServletResponse response){
		//TODO:��ʱ����
		String t = "N";
		if(t.equals("N")) return "redirect:/emailregister.xhtml";
		String citycode = WebUtils.getAndSetDefault(request, response);
		Integer cinemaCount = placeService.getPlaceCount(Cinema.class, citycode);
		ErrorCode<Integer> gymCountCode = synchGymService.getGymCount(citycode, null, null);
		if(gymCountCode.isSuccess()) model.put("gymCount", gymCountCode.getRetval());
		Integer sportCount = placeService.getPlaceCount(Sport.class, citycode);
		if(StringUtils.isNotBlank(encode) && StringUtil.md5WithKey("" + from).equals(encode)){
			WebUtils.setInviteFromCookie(res, config.getBasePath(), from, "email");
		}
		/*String ip = WebUtils.getRemoteIp(request);
		boolean isNeedCaptha = bindMobileService.isNeedToken(TokenType.Register, ip, 2);
		model.put("needCaptcha", isNeedCaptha);*/
		model.put("cinemaCount", cinemaCount);
		model.put("sportCount", sportCount);
		return "home/register/mobileRegister.vm";
	}
	@RequestMapping("/emailregister.xhtml")
	public String emailRegister(HttpServletResponse res, ModelMap model, Long from, String encode, 
			HttpServletRequest request, HttpServletResponse response){
		String citycode = WebUtils.getAndSetDefault(request, response);
		ErrorCode<Integer> gymCountCode = synchGymService.getGymCount(citycode, null, null);
		if(gymCountCode.isSuccess()) model.put("gymCount", gymCountCode.getRetval());
		if(StringUtils.isNotBlank(encode) && StringUtil.md5WithKey("" + from).equals(encode)){
			WebUtils.setInviteFromCookie(res, config.getBasePath(), from, "email");
		}
		return "home/register/emailRegister.vm";
	}
	@RequestMapping("/savereg.xhtml")
	public String saveregister(HttpServletRequest request, HttpServletResponse response, 
			String captcha, String captchaId, String nickname, String email, String password, String repassword, 
			@CookieValue(required=false) String invfrom, ModelMap model) {
		if(StringUtils.containsIgnoreCase(request.getHeader("user-agent"), "python")) {
			return showJsonError(model, "�Ƿ�ip!!");
		}
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		if(!password.equals(repassword)){
			return showJsonError(model, "����������벻һ�£����������룡");
		}
		String key = blogService.filterAllKey(nickname);
		if(StringUtils.isNotBlank(key)){
			return showJsonError(model, "���зǷ��ؼ��֣�");
		}
		Long inviteid = null;
		String invitetype = null;
		String[] invite = getInviteFrom(invfrom);
		if(invite!=null){
			inviteid = Long.parseLong(invite[0]);
			invitetype = invite[1];
		}
		String ip =  WebUtils.getRemoteIp(request);
		String opkey = "savereg"+ ip;
		boolean allow = operationService.isAllowOperation(opkey, 30, 60*60, 5);
		if(!allow){
			dbLogger.warn("savereg ip:" + ip);
			return showJsonError(model, "��������Ƶ����");
		}
		ErrorCode<Member> code = memberService.regMember(nickname, email, password, inviteid, invitetype, "web", WebUtils.getAndSetDefault(request, response), ip);
		if(code.isSuccess()){
			loginService.autoLogin(request, response, email, password);
			//���͸߼�ȷ���ʼ�
			//String radom = StringUtil.md5WithKey(email);
			gewaMailService.sendRegEmail(code.getRetval());
			operationService.updateOperation(opkey, 30, 60*60, 5);
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_REGISTER, "memberid:" + code.getRetval().getId() + ", ip:" + ip);
			return showJsonSuccess(model);
		}else{
			return showJsonError(model, code.getMsg());
		}
	}
	
	@RequestMapping("/ajax/mobile/savePhoneReg.xhtml")
	public String saveMobileregister(HttpServletRequest request, HttpServletResponse response,
			String nickname, String mobile, String checkpass, String password, String repassword, 
			@CookieValue(required=false) String invfrom, ModelMap model){
		Map jsonMap = new HashMap();
		Map errorMap = new HashMap();
		jsonMap.put("errorMap", errorMap);
		if(StringUtils.isBlank(password) || !ValidateUtil.isPassword(password)){
			errorMap.put("password", "�����ʽ����ȷ,ֻ������ĸ�����֣��»��ߣ�����6��14λ��");
			return showJsonError(model, jsonMap);
		}
		if(!StringUtils.equals(password,repassword)){
			errorMap.put("password", "�����������벻һ�£����������룡");
			return showJsonError(model, jsonMap);
		}
		String key = blogService.filterAllKey(nickname);
		if(StringUtils.isNotBlank(key)){
			jsonMap.put("nickname", "���зǷ��ؼ��֣�");
			return showJsonError(model, jsonMap); 
		}
		Long inviteid = null;
		String invitetype = null;
		String[] invite = getInviteFrom(invfrom);
		if(invite!=null){
			inviteid = Long.parseLong(invite[0]);
			invitetype = invite[1];
		}
		ErrorCode<Member> code = memberService.regMemberWithMobile(nickname, mobile, password, checkpass, inviteid, invitetype, AddressConstant.ADDRESS_WEB, WebUtils.getAndSetDefault(request, response), WebUtils.getRemoteIp(request));
		if(!code.isSuccess()){
			jsonMap.put("msg", code.getMsg());
			return showJsonError(model, jsonMap);
		}

		loginService.autoLogin(request, response, mobile, password);
		return showJsonSuccess(model);
	}
	@RequestMapping("/userSeniorRecognition.xhtml")
	public String userSeniorRecognition(@CookieValue(value = LOGIN_COOKIE_NAME, required = false)String sessid, ModelMap model, Long memberid, String encode, HttpServletRequest request){
		Member member = daoService.getObject(Member.class, memberid);
		if(member==null) return showError(model, "�û������ڣ�");
		Member logMember = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(logMember == null || !logMember.equals(member)){
			return gotoLogin("/userSeniorRecognition.xhtml", request, model);
		}
		if(!StringUtil.md5WithKey(member.getId()+"").equals(encode)) return showError(model, "��������Ч��");
		//TODO:Service����
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		
		if(memberInfo.isFinishedTask(MemberConstant.TASK_CONFIRMREG)){
			model.put("type", "repeat");
		} else {
			memberService.saveNewTask(member.getId(), MemberConstant.TASK_CONFIRMREG);
			model.put("type", "success");
		}
		return "redirect:/checkSuccess.xhtml";
	}
	@RequestMapping(value="/forgetPassword.xhtml",method=RequestMethod.GET)
	public String forgetPassword(){
		return "home/member/getPassword.vm";
	}
	@RequestMapping("/getPassword.xhtml")
	public String getPassword(HttpServletRequest request, ModelMap model, String email, String captchaId, String captcha){
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		
		String ip = WebUtils.getRemoteIp(request);
		String opKey = "getPassword" + email;
		boolean allow = operationService.isAllowOperation(opKey, 5, OperationService.ONE_DAY, 5);
		if(!allow){
			dbLogger.warn("getPassword ip:" + ip);
			return showJsonError(model, "���������Ƶ�������Ժ����ԣ�");
		}
		Map jsonMap = new HashMap();
		if(ValidateUtil.isMobile(email)){
			email = StringUtils.trim(email);
			Member member = daoService.getObjectByUkey(Member.class, "mobile", email, false);
			if(member == null) return showJsonError(model, "���ֻ��Ż�δע�ᣬ��������д��");
			String checkMsg = checkMemberResource(member);
			if(StringUtils.isNotBlank(checkMsg)) return showJsonError(model, checkMsg);
			jsonMap.put("type", "mobile");
			jsonMap.put("email", email);
			String encode = StringUtil.md5WithKey(email + "mobile");
			jsonMap.put("encode", encode);
		}else if(ValidateUtil.isEmail(email)){
			//if(StringUtils.isBlank(checkcode)) return showJsonError(model, "У���벻��Ϊ�գ�");
			//if(!ValidateUtil.isNumber(checkcode, 4, 4)) return showJsonError(model, "У�����ʽ����");
			email = StringUtils.trim(email);
			email = email.toLowerCase();
			Member member = daoService.getObjectByUkey(Member.class, "email", email, false);
			if(member == null) return showJsonError(model, "�����䲻���ڣ�");
			String checkMsg = checkMemberResource(member);
			if(StringUtils.isNotBlank(checkMsg)) return showJsonError(model, checkMsg);
			Long validtime = System.currentTimeMillis() + DateUtil.m_minute*30;
			String validcode = StringUtil.md5(email + StringUtils.substring(member.getPassword(), 8, 24));
			ValidEmail validEmail = new ValidEmail(email, validtime, validcode, ValidEmail.TYPE_PASSWORD);
			mongoService.saveOrUpdateObject(validEmail, MongoData.DEFAULT_ID_NAME);
			gewaMailService.sendGetPasswordMail(member.getNickname(), member.getId(), email, validEmail.getId());
			jsonMap.put("type", "email");
			jsonMap.put("email", email);
			String encode = StringUtil.md5WithKey(email + "email");
			jsonMap.put("encode", encode);
		}else return showJsonError(model, "������ֻ���ʽ����ȷ��");
		operationService.updateOperation(opKey, 5, OperationService.ONE_DAY, 5);
		return showJsonSuccess(model, jsonMap);
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
	@RequestMapping("/securityPass.xhtml")
	public String securityPass(@RequestParam("email")String email, @RequestParam("type")String type, @RequestParam("encode")String encode, ModelMap model){
		String value = StringUtil.md5WithKey(email + type);
		if(!StringUtils.equals(value, encode)) return show404(model, "��������");
		if(!(ValidateUtil.isEmail(email)||ValidateUtil.isMobile(email))) return show404(model, "��������");
		model.put("type", type);
		model.put("email", email);
		return "home/member/getPasswordstep2.vm";
	}
	@RequestMapping("/modifyPassword.xhtml")
	public String modifyPassword(HttpServletRequest request, ModelMap model, String email, String uuid, String encode){
		if(StringUtils.isBlank(encode) || StringUtils.isBlank(email)|| StringUtils.isBlank(uuid)) return showError(model, "���Ӵ���");
		ValidEmail validEmail = mongoService.getObject(ValidEmail.class, MongoData.DEFAULT_ID_NAME, uuid);
		if(validEmail == null){
			dbLogger.error("ip:" + WebUtils.getRemoteIp(request) + ", email:" + email);
			return show404(model, "��������Ч��");
		}
		Member member = daoService.getObjectByUkey(Member.class, "email", email, false);
		if(member == null) return showJsonError(model, "���ݲ����ڣ�");
		String checkMsg = checkMemberResource(member);
		if(StringUtils.isNotBlank(checkMsg)) return show404(model, checkMsg);
		Long cur = System.currentTimeMillis();
		if(cur > validEmail.getValidtime()) return show404(model, "�����ѳ�ʱ�������»�ȡ��");
		if(!StringUtils.equals(PayUtil.md5WithKey(email, "" + member.getId(), uuid), encode)) return showError(model, "��������Ч��");
		model.put("email", email);
		model.put("success", "true");
		return "home/member/getPasswordstep2.vm";
	}
	
	@RequestMapping("/savePassword.xhtml")
	public String savePassword(HttpServletRequest request, ModelMap model, String email, String password, String repassword, String encode, String uuid){
		if(StringUtils.isBlank(email)) return showJsonError(model, "���䲻��Ϊ�գ�");
		if(StringUtils.isBlank(password)) return showJsonError(model, "���벻��Ϊ�գ�");
		if(StringUtils.isBlank(repassword)) return showJsonError(model, "ȷ�����벻��Ϊ�գ�");
		if(!StringUtils.equals(password, repassword)) return showJsonError(model, "�����������벻һ�£����������룡");
		if(!ValidateUtil.isPassword(password)){
			return showJsonError(model, "�����ʽ����ȷ,ֻ������ĸ�����֣��»��ߣ�����6��14λ��");
		}
		String ip = WebUtils.getRemoteIp(request);
		ValidEmail validEmail = mongoService.getObject(ValidEmail.class, "id", uuid);
		if(validEmail == null){
			dbLogger.error("ip: " + ip + ", email: " + email);
			return showJsonError(model, "�Ƿ�������");
		}
		Long cur = System.currentTimeMillis();
		if(cur > validEmail.getValidtime()) return showJsonError(model, "��������ʧЧ�������»�ȡ�����������ӣ�");
		Member member = daoService.getObjectByUkey(Member.class, "email", email, false);
		if(member == null) return showJsonError(model, "���ݲ����ڣ�");
		if(!StringUtils.equals(StringUtil.md5(email + StringUtils.substring(member.getPassword(), 8, 24)), validEmail.getValidcode())){
			return showJsonError(model, "У�����");
		}
		String checkMsg = checkMemberResource(member);
		if(StringUtils.isNotBlank(checkMsg)) return show404(model, checkMsg);
		if(!StringUtils.equals(PayUtil.md5WithKey(email, "" + member.getId(), uuid),encode)) return showJsonError(model, "��������Ч��");
		if(!ValidateUtil.isPassword(password))return showJsonError(model, "�����ʽ����ȷ!");
		boolean danger = baoKuService.isDanger(member.getEmail(), password);
		if(danger) return showJsonError(model, "���ʻ����õ�������ڰ�ȫ���գ���������Ϊ�����룡");
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		Map<String, String> otherInfoMap = JsonUtils.readJsonToMap(memberInfo.getOtherinfo());
		otherInfoMap.remove(MemberConstant.TAG_DANGER);
		memberInfo.setOtherinfo(JsonUtils.writeMapToJson(otherInfoMap));
		member.setPassword(StringUtil.md5(password));
		daoService.saveObjectList(member, memberInfo);
		monitorService.saveMemberLog(member.getId(), MemberConstant.ACTION_MODPWD, null, ip);
		return showJsonSuccess(model, "�����޸ĳɹ�,���¼������������!");
	}
	@RequestMapping("/successPass.dhtml")
	public String successPass(){
		return "home/member/getPasswordstep3.vm";
	}
	@RequestMapping("/checkSuccess.xhtml")
	public String RecognitionSuccessful(ModelMap model, String type){
		model.put("type", type);
		return "home/member/checkSuccess.vm";
	}
	@RequestMapping("/checkMember.xhtml")
	public String checkMember(String tag, String itemvalue, ModelMap model){
		if(!TAG_CHECK.contains(tag)) return showJsonError(model, "��֤���ʹ���");
		//�Ƿ���ڹ��˹ؼ���
		if(StringUtils.isNotBlank(itemvalue) && !StringUtils.contains(itemvalue, "@")){
			String key = blogService.filterAllKey(itemvalue);
			if(StringUtils.isNotBlank(key)){
				dbLogger.warn("registerError:" + itemvalue + ":" + key);
				return showJsonError(model, "���зǷ��ؼ���!");
			}
		}
		if(StringUtils.equals(tag, "nickname")){
			itemvalue = StringUtils.trim(itemvalue);
			boolean nickExist = memberService.isMemberExists(itemvalue, null);
			if(nickExist) return showJsonError(model, "���ǳ��Ѿ����ڣ�");
		}
		//�����ֻ��ż��
		return showJsonSuccess(model);
	}
	@RequestMapping("/setUpMemberPassword.xhtml")
	public String setUpMemberPassword(ModelMap model, Long hiddenmemberid, String encode){
		if(!encode.equals(StringUtil.md5WithKey(hiddenmemberid+"")))return showJsonError(model, "�����ݴ���");
		if(hiddenmemberid==null && StringUtils.isBlank(encode)) return showJsonError(model, "�����ݴ���");
		HiddenMember hiddenMember = daoService.getObject(HiddenMember.class, hiddenmemberid);
		if(hiddenMember==null)
			return showError(model, "�����ݲ����ڣ�");
		model.put("hiddenMember", hiddenMember);
		model.put("encode", encode);
		return "home/member/setUpMemberPassword.vm";
	}
	@RequestMapping("/saveSetUpMemberPassword.xhtml")
	public String saveSetUpMemberPassword(HttpServletRequest request, HttpServletResponse response, 
			ModelMap model, Long hiddenmemberid, String password, String encode, String nickname){
		if(hiddenmemberid==null && StringUtils.isBlank(encode)) return showJsonError(model, "�����ݴ���");
		boolean nickExist = memberService.isMemberExists(nickname, null);
		if(nickExist) return showJsonError(model, "���ǳ��Ѿ����ڣ�");
		HiddenMember hiddenMember = daoService.getObject(HiddenMember.class, hiddenmemberid);
		if(hiddenMember==null) return showJsonError(model, "�����ݲ����ڣ�");
		if(StringUtils.isBlank(hiddenMember.getEmail())) return showJsonError(model, "�����ݴ���");
		if(StringUtils.isBlank(encode) || !StringUtil.md5WithKey(hiddenMember.getId()+"").equals(encode)){
			return showJsonError(model, "�����ݲ����ڣ�");
		}
		String citycode = WebUtils.getAndSetDefault(request, response);
		if(!ValidateUtil.isPassword(password))return showJsonError(model, "�����ʽ����ȷ!");
		ErrorCode<Member> result = memberService.regMember(nickname, hiddenMember.getEmail(), password, hiddenMember.getInviteid(), null, null, citycode, WebUtils.getRemoteIp(request));
		if(result.isSuccess()) daoService.removeObject(hiddenMember);
		return showJsonSuccess(model, "�������óɹ�,���¼������������!");
	}
	
	private String[] getInviteFrom(String fromcookie){
		if(StringUtils.isBlank(fromcookie)) return null;
		String[] pair = StringUtils.split(fromcookie, ":");
		if(pair.length != 3) return null;
		String mykey = StringUtil.md5(pair[0] + pair[1] + "lsdkjf;lkwjer2wlk");
		if(StringUtils.equals(mykey, pair[2])) return pair;
		return null;
	}
	/**
	 * �齱�����
	 */
	@RequestMapping("/inviteDrawFriend.xhtml")
	public String drawInviteFriend(HttpServletResponse res, Long from, String invitetype){
		WebUtils.setInviteFromCookie(res, config.getBasePath(), from, invitetype);
		String str = "invite";
		if("HP_L".equals(invitetype)){
			str = "harryPotter";
		}
		return "redirect:/subject/"+str+"/index.xhtml";
	} 
}