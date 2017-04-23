package com.gewara.web.action.blog;

import java.sql.Timestamp;
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

import com.gewara.Config;
import com.gewara.constant.CookieConstant;
import com.gewara.constant.ExpGrade;
import com.gewara.constant.Status;
import com.gewara.constant.SysAction;
import com.gewara.constant.TagConstant;
import com.gewara.model.bbs.qa.GewaAnswer;
import com.gewara.model.bbs.qa.GewaQaExpert;
import com.gewara.model.bbs.qa.GewaQaPoint;
import com.gewara.model.bbs.qa.GewaQuestion;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.service.OperationService;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.bbs.QaService;
import com.gewara.service.bbs.UserMessageService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.SearchService;
import com.gewara.untrans.ShareService;
import com.gewara.untrans.impl.ControllerService;
import com.gewara.untrans.monitor.MonitorService;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.util.BeanUtil;
import com.gewara.util.BindUtils;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.WebUtils;
import com.gewara.util.XSSFilter;
import com.gewara.web.action.AnnotationController;
import com.gewara.xmlbind.bbs.Comment;
@Controller
public class AjaxQAController extends AnnotationController {
	
	@Autowired@Qualifier("controllerService")
	private ControllerService controllerService;
	public void setControllerService(ControllerService controllerService) {
		this.controllerService = controllerService;
	}
	@Autowired@Qualifier("userMessageService")
	private UserMessageService userMessageService;
	public void setUserMessageService(UserMessageService userMessageService) {
		this.userMessageService = userMessageService;
	}
	@Autowired@Qualifier("monitorService")
	private MonitorService monitorService;
	public void setMonitorService(MonitorService monitorService) {
		this.monitorService = monitorService;
	}
	@Autowired@Qualifier("config")
	private Config config;
	public void setConfig(Config config) {
		this.config = config;
	}
	@Autowired@Qualifier("blogService")
	private BlogService blogService;
	public void setBlogService(BlogService blogService) {
		this.blogService = blogService;
	}

	@Autowired@Qualifier("qaService")
	private QaService qaService;
	public void setQaService(QaService qaService) {
		this.qaService = qaService;
	}
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}
	@Autowired@Qualifier("commentService")
	private CommentService commentService;
	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	public void setShareService(ShareService shareService) {
		this.shareService = shareService;
	}
	
	@Autowired@Qualifier("searchService")
	private SearchService searchService;
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
	//��֪��
	@RequestMapping("/qa/saveQa.xhtml")
	public String saveQuestion(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			@CookieValue(value=CookieConstant.MEMBER_POINT,required=false)String pointxy, HttpServletRequest request,
			Long id, String title, String reward, String captchaId, String captcha, 
			HttpServletResponse response, ModelMap model) {
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		String msg = "";
		if (member == null) return showJsonError(model, "���ȵ�¼��");
		if (blogService.isBlackMember(member.getId())) return showJsonError(model, "��������");
		if (!qaService.isQuestion(member.getId(), GewaQuestion.MAXDAYS)) {
			msg = "���ϴε����ʳ���"+GewaQuestion.MAXDAYS+"����û�д���,��ʱ���ܷ���������!";
			return showJsonError(model, msg);
		}
		Integer eggs = blogService.isBadEgg(member);
		if (eggs != 777) {
			return showJsonError(model, eggs+"");
		}
		MemberInfo mi = daoService.getObject(MemberInfo.class, member.getId());
		if (mi != null && mi.getExpvalue() < Integer.parseInt(reward)){
			msg = "�����ڵľ���ֵ��" + mi.getExpvalue() + "����" + reward;
			return showJsonError(model, msg);
		}else if (mi == null && !"0".equals(reward)) {
			msg = "�����ڵľ���ֵ��0,�����ܸ�����ֵ!";
			return showJsonError(model, msg);
		}
		if (StringUtils.isBlank(title)) return showJsonError(model, "���ⲻ��Ϊ��");
		if (WebUtils.checkString(title)) return showJsonError(model, "���⺬�зǷ��ַ���");
		if (StringUtil.getByteLength(title)>100) return showJsonError(model, "����̫��,���ܳ���50����");
		GewaQuestion question = null;
		String opkey = OperationService.TAG_ADDCONTENT + member.getId();
		
		boolean isnew = false;
		if (id != null) {// �޸���־
			question = daoService.getObject(GewaQuestion.class, id);
			if (!question.getMemberid().equals(member.getId())) return showJsonError(model, "��û��Ȩ��");
			question.setModtime(new Timestamp(System.currentTimeMillis()));
		} else {
			if(!blogService.allowAddContent(OperationService.TAG_ADDCONTENT, member.getId())){
				msg = "�㷢�����ʵ�ʱ����̫��, Ъ���ٷ�!";
				return showJsonError(model, msg);
			}
			question = new GewaQuestion(member.getId());
			question.setMembername(member.getNickname());
			isnew = true;
		}
		Map dataMap = request.getParameterMap();
		BindUtils.bindData(question, dataMap);
		if(WebUtils.checkPropertyAll(question)) return showJsonError(model, "���зǷ��ַ���");
		if(StringUtil.getByteLength(question.getContent())>8000) return showJsonError(model, "�����ַ�������");
		question = XSSFilter.filterObjAttrs(question, "title","content");
		String key = blogService.filterContentKey(question.getTitle() + question.getContent());
		boolean filter = false;
		boolean isNight = false;
		if(StringUtils.isNotBlank(key)){
			question.setStatus(Status.N_FILTER);
			filter = true;
		}else{
			if(blogService.isNight()){
				question.setStatus(Status.N_NIGHT); //ҹ������
				filter = true;
				isNight = true;
			}
		}
		String citycode = WebUtils.getAndSetDefault(request, response);
		question.setCitycode(citycode);
		question.setIp(WebUtils.getIpAndPort(ip, request));
		daoService.saveObject(question);
		if (isnew) {
			GewaQaPoint qaPoint = new GewaQaPoint(question.getId(), member.getId(), ExpGrade.EXP_QUESTION_ADD, GewaQaPoint.TAG_SENDQUESTION);
			daoService.saveObject(qaPoint);
			memberService.addExpForMember(member.getId(), ExpGrade.EXP_QUESTION_ADD-question.getReward());
			operationService.updateOperation(opkey, 40);
		}
		if(filter){
			if(!isNight){
				String etitle = "���˷�����֪����" + key;
				String content = "���˶������⣬�������˹ؼ���memberId = " + member.getId()+",[�û�IP:" + WebUtils.getRemoteIp(request) + "]" + question.getTitle() + "\n" + question.getContent();
				monitorService.saveSysWarn(GewaQuestion.class, question.getId(), etitle, content, RoleTag.bbs);
			}else{
				Map jsMap = new HashMap();
				jsMap.put("retcode", 403);
				jsMap.put("returl", config.getBasePath()+"qa/");
				jsMap.put("retmsg", "ҹ�䷢���֪����Ҫͨ������Ա��˺������ʾ��");
				return showJsonError(model, jsMap);
			}
		}else{// ���ѡ�з���΢��,
			String link = config.getBasePath() + "qa/q" + question.getId();
			String linkstr = "���ʣ�<a href=\""+link+"\" target=\"_blank\" rel=\"nofollow\">"+question.getTitle()+"</a>";
			String pointx = null, pointy = null;
			if(StringUtils.isNotBlank(pointxy)){
				List<String> pointList = Arrays.asList(StringUtils.split(pointxy, ":"));
				if(pointList.size() == 2){
					pointx = pointList.get(0);
					pointy = pointList.get(1);
				}
			}
			String timeinfo = "";
			if (GewaQuestion.QS_STATUS_N.equals(question.getQuestionstatus()) || GewaQuestion.QS_STATUS_Z.equals(question.getQuestionstatus())) {
				Timestamp starttime = question.getAddtime();
				Timestamp endtime = new Timestamp(System.currentTimeMillis());
				long milliseconds = (DateUtil.addDay(starttime, GewaQuestion.MAXDAYS).getTime() - endtime.getTime()) / 1000;
				if (milliseconds > 0) {
					long days = milliseconds / (24 * 60 * 60);// ��������
					long hours = (milliseconds - days * 24 * 60 * 60) / (60 * 60);// ����Сʱ��
					timeinfo = "�������������" + days + "��" + hours + "Сʱ";
				} else {
					timeinfo = "��������Ҫ������";
				}
			} else {
				if (question.getDealtime() != null && GewaQuestion.QS_STATUS_Y.equals(question.getQuestionstatus()))
					timeinfo ="���ʱ�䣺" + DateUtil.format(question.getDealtime(), "yyyy-MM-dd HH:mm");
			}
			Map otherinfoMap = new HashMap();
			otherinfoMap.put("timeinfo", timeinfo);
			otherinfoMap.put("reward", question.getReward());
			String otherinfo = JsonUtils.writeObjectToJson(otherinfoMap);
			ErrorCode<Comment> ec = commentService.addMicroComment(member, TagConstant.TAG_QA_MEMBER, question.getId(),  linkstr, "", null, null, false, null, otherinfo,pointx,pointy, WebUtils.getIpAndPort(ip, request), null);
			if(ec.isSuccess()){
				shareService.sendShareInfo("wala",ec.getRetval().getId(), ec.getRetval().getMemberid(), null);
			}
		}
		searchService.pushSearchKey(question);//��������������������
		return showJsonSuccess(model, ""+question.getId());
	}
	//�ش�
	@RequestMapping("/qa/saveAnswer.xhtml")
	public String answer(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model, String captchaId, String captcha) {
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if (member == null) return showJsonError_NOT_LOGIN(model);
		if (blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		String opkey = OperationService.TAG_ADDCONTENT + member.getId();
		boolean allow = operationService.isAllowOperation(opkey, 40);
		if(!allow) return showJsonError(model, "�������̫Ƶ����, Ъ���ٷ���!");
		Integer eggs = blogService.isBadEgg(member);
		if (eggs != 777) {
			return showJsonError(model, eggs+"");
		}
		GewaAnswer answer = new GewaAnswer("");
		Map<String, String[]> answerMap = request.getParameterMap();
		BindUtils.bindData(answer, answerMap);
		answer.setMemberid(member.getId());
		answer.setIp(WebUtils.getIpAndPort(ip, request));
		answer.setContent(XSSFilter.filterAttr(answer.getContent()));
		if (StringUtils.isBlank(answer.getContent())) return showJsonError(model, "�ش����ݲ���Ϊ�գ�");
		if (StringUtil.getByteLength(answer.getContent())>4000)return showJsonError(model, "�ش����ݲ��ܳ���4000���ַ�������ǰ��������"+StringUtil.getByteLength(answer.getContent())+"�ַ�");
		if (WebUtils.checkPropertyAll(answer)) return showJsonError(model, "���зǷ��ַ���");
		GewaQuestion question = daoService.getObject(GewaQuestion.class, answer.getQuestionid());
		boolean addPoint = false;
		Integer point = ExpGrade.EXP_ANSWER_ADD_COMMON;
		if (question.getMemberid().longValue() != member.getId().longValue() && !qaService.isAnswerQuestion(answer.getQuestionid(), member.getId())) { // ���ش�������û����Ӿ���ֵ(�������Լ�)
			addPoint = true;
			GewaQaExpert expert = qaService.getQaExpertByMemberid(member.getId());
			if (expert != null && GewaQaExpert.STATUS_Y.equals(expert.getStatus())) {
				memberService.addExpForMember(member.getId(), ExpGrade.EXP_ANSWER_ADD_EXPERT); // ר��
				point = ExpGrade.EXP_ANSWER_ADD_EXPERT;
			} else {
				memberService.addExpForMember(member.getId(), ExpGrade.EXP_ANSWER_ADD_COMMON); // ��ͨ����
			}
		}
		Long mid = member.getId();
		Member questionMember = daoService.getObject(Member.class, question.getMemberid());
		question.setReplymemberid(mid);
		question.setUpdatetime(new Timestamp(System.currentTimeMillis()));
		question.addReplycount();
		if (GewaQuestion.QS_STATUS_Z.equals(question.getQuestionstatus()))
			question.setQuestionstatus(GewaQuestion.QS_STATUS_N);
		String key = blogService.filterContentKey(answer.getContent());
		if(StringUtils.isNotBlank(key)){
			answer.setStatus(Status.N_FILTER);
			daoService.saveObject(answer);
			String title = "���˷�����֪���ظ���" + key;
			String content = "���˶��֪ⷢ���ظ����������˹ؼ���memberId = " + member.getId() +",[�û�IP:" + WebUtils.getRemoteIp(request) + "]" + question.getTitle() + "\n" + answer.getContent();
			monitorService.saveSysWarn(title, content, RoleTag.bbs);
		}else {
			daoService.saveObject(answer);
		}
		daoService.saveObject(question);
		operationService.updateOperation(opkey, 40);
		if (addPoint) {
			GewaQaPoint sendquestion = new GewaQaPoint(question.getId(), answer.getId(), member.getId(), point, GewaQaPoint.TAG_REPLYQUESTION);
			daoService.saveObject(sendquestion);
		}
		if (!question.getMemberid().equals(mid) && (GewaQuestion.QS_STATUS_N.equals(question.getQuestionstatus()) || 
				GewaQuestion.QS_STATUS_Z.equals(question.getQuestionstatus()))) {// �����ʼ�
			model.put("questionnickname", questionMember.getNickname());
			model.put("nickname", member.getNickname());
			model.put("question", BeanUtil.getBeanMapWithKey(question, "id", "title"));
			model.put("content", answer.getContent());
			String body = "���ڸ�����֪���ϵ����ʣ� <a href='"+config.getBasePath()+"qa/qaDetail.xhtml?qid="+question.getId()+"'>"+question.getTitle()+"</a> �����µĻش��ˡ�";
			userMessageService.sendSiteMSG(question.getMemberid(), SysAction.ACTION_QUESTION_NEW_ANSWER, question.getId(), body);
		}
		if(StringUtils.equals(answer.getStatus(), Status.N_FILTER)) return showJsonError_KEYWORD(model);
		return showJsonSuccess(model);
	}
	// �������
	@RequestMapping("/qa/noproper.xhtml")
	public String noproper(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long qid, ModelMap model) {
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if (member == null) return showJsonError_NOT_LOGIN(model);
		if (blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		GewaQuestion question = daoService.getObject(GewaQuestion.class, qid);
		if (question.getMemberid().longValue() != member.getId().longValue()) return showJsonError_NORIGHTS(model);
		question.setQuestionstatus(GewaQuestion.QS_STATUS_NOPROPER);
		question.setDealtime(new Timestamp(System.currentTimeMillis()));
		daoService.saveObject(question);
		return showJsonSuccess(model);
	}
	// ɾ��
	@RequestMapping("/qa/delQuestion.xhtml")
	public String delQuestion(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long qid, ModelMap model) {
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if (member == null) return showJsonError_NOT_LOGIN(model);
		if (blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		GewaQuestion question = daoService.getObject(GewaQuestion.class, qid);
		if (question.getMemberid().longValue() != member.getId().longValue()) return showJsonError_NORIGHTS(model);
		question.setStatus(Status.N_DELETE);
		GewaQaPoint qaPoint = qaService.getGewaQaPointByQuestionidAndTag(qid, GewaQaPoint.TAG_SENDQUESTION);
		daoService.saveObject(question);
		daoService.removeObject(qaPoint);
		List<GewaAnswer> answerList = qaService.getAnswerListByQuestionid(qid);
		for (GewaAnswer answer : answerList) {
			answer.setStatus(Status.N_DELETE);
		}
		daoService.saveObjectList(answerList);
		memberService.addExpForMember(member.getId(), -ExpGrade.EXP_DIARY_ADD);
		searchService.pushSearchKey(question);//��������������������
		return showJsonSuccess(model);
	}
	// ����
	@RequestMapping("/qa/addInfo.xhtml")
	public String addInfo(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long qid, ModelMap model) {
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if (member == null) return showJsonError_NOT_LOGIN(model);
		if (blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		GewaQuestion question = daoService.getObject(GewaQuestion.class, qid);
		Map<String, String[]> questionMap = request.getParameterMap();
		BindUtils.bindData(question, questionMap);
		if (StringUtils.isBlank(question.getAddinfo())) return showJsonError(model, "�������ݲ���Ϊ�գ�");
		if (StringUtil.getByteLength(question.getAddinfo()) > 400) return showJsonError(model, "�������ݲ��ܳ���200���֣�");
		question.setAddinfotime(new Timestamp(System.currentTimeMillis()));
		question.setModtime(new Timestamp(System.currentTimeMillis()));
		question.setAddinfo(XSSFilter.filterAttr(question.getAddinfo()));
		daoService.saveObject(question);
		return showJsonSuccess(model);
	}

	// ������ȷ��
	@RequestMapping("/qa/bestAnswer.xhtml")
	public String bestAnswer(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long qid, Long aid, ModelMap model) {
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if (member == null) return showJsonError_NOT_LOGIN(model);
		if (blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		GewaQuestion question = daoService.getObject(GewaQuestion.class, qid);
		if (GewaQuestion.QS_STATUS_Y.equals(question.getQuestionstatus()))
			return showJsonError(model, "�����Ѿ�����Ѵ�,��������!");
		GewaAnswer answer = daoService.getObject(GewaAnswer.class, aid);
		if (member.getId().longValue() == answer.getMemberid().longValue())
			return  showJsonError(model, "���������Լ��Ļش�Ϊ��Ѵ�!");
		int reward = question.getReward();
		memberService.addExpForMember(answer.getMemberid(), reward); // ���Ӿ���ֵ
		//blogService.addMemberExperience(question.getMemberid(), -reward); // ��������ֵ
		answer.setAnswerstatus(GewaAnswer.AS_STATUS_Y);
		question.setQuestionstatus(GewaQuestion.QS_STATUS_Y);
		question.setDealtime(new Timestamp(System.currentTimeMillis()));
		GewaQaPoint bestAnswer = new GewaQaPoint(question.getId(), answer.getId(), answer.getMemberid(), question.getReward(),
				GewaQaPoint.TAG_BESTANSWER);
		daoService.saveObject(bestAnswer);
		daoService.saveObject(answer);
		daoService.saveObject(question);
		Member answermember = daoService.getObject(Member.class, answer.getMemberid());
		model.put("answernickname", answermember.getNickname());
		model.put("nickname", member.getNickname());
		model.put("question", BeanUtil.getBeanMapWithKey(question, "id", "reward"));
		String body = "���ڸ�����֪���Ļظ� <a href='"+config.getBasePath()+"qa/qaDetail.xhtml?qid="+question.getId()+"'>"+question.getTitle()+"</a> ������Ϊ��Ѵ��ˡ�";
		userMessageService.sendSiteMSG(answer.getMemberid(), SysAction.ACTION_ANSWER_IS_BEST, answer.getId(), body);
		return showJsonSuccess(model);
	}
	
	// ����ר��
	@RequestMapping("/qa/applyExpert.xhtml")
	public String applyExpert(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, String reason, ModelMap model) {
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if (member == null) return showJsonError_NOT_LOGIN(model);
		if (blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		Map<String, String[]> expertMap = request.getParameterMap();
		if (StringUtil.getByteLength(reason) > 400) return showJsonError(model, "�ҵ����Ʋ��ܳ���200���֣�");
		GewaQaExpert expert = qaService.getQaExpertByMemberid(member.getId());
		if (expert != null) {
			if (GewaQaExpert.STATUS_Y.equals(expert.getStatus()))
				return showJsonError(model, "���Ѿ���֪��ר��!");
			else {
				expert.setAddtime(new Timestamp(System.currentTimeMillis()));
			}
		} else {
			expert = new GewaQaExpert(member.getId());
		}
		BindUtils.bindData(expert, expertMap);
		daoService.saveObject(expert);
		return showJsonSuccess(model);
	}
}
