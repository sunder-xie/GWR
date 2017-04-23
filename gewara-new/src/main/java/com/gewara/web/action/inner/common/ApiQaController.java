package com.gewara.web.action.inner.common;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.Config;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.ExpGrade;
import com.gewara.constant.Status;
import com.gewara.constant.SysAction;
import com.gewara.model.bbs.qa.GewaAnswer;
import com.gewara.model.bbs.qa.GewaQaExpert;
import com.gewara.model.bbs.qa.GewaQaPoint;
import com.gewara.model.bbs.qa.GewaQuestion;
import com.gewara.model.user.Member;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.bbs.QaService;
import com.gewara.service.bbs.UserMessageService;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.util.BeanUtil;
import com.gewara.util.StringUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.api.BaseApiController;
import com.gewara.web.component.ShLoginService;

@Controller
public class ApiQaController extends BaseApiController {
	@Autowired
	@Qualifier("qaService")
	private QaService qaService;

	@Autowired
	@Qualifier("blogService")
	private BlogService blogService;

	@Autowired
	@Qualifier("config")
	private Config config;

	@Autowired
	@Qualifier("userMessageService")
	private UserMessageService userMessageService;
	
	@Autowired@Qualifier("loginService")
	private ShLoginService loginService;
	
	/**
	 * ��ӽ�������ѯ
	 * 
	 * @param citycode
	 * @param memberEncode
	 * @param tag
	 * @param category
	 * @param relatedid
	 * @param categoryid
	 * @param title
	 * @param model
	 * @return
	 */
	@RequestMapping("/inner/common/member/addQuestion.xhtml")
	public String addQuestion(String citycode, String sessid, String ip, String tag, Long relatedid, 
			String category, Long categoryid, String title, ModelMap model) {
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if (member == null) {
			return getErrorXmlView(model, ApiConstant.CODE_MEMBER_NOT_EXISTS,"�û������ڣ�");
		}
		if (StringUtils.isBlank(title)) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,"��ѯ���ݲ���Ϊ�գ�");
		}
		if (StringUtils.length(title) > 100) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,"��ѯ���ݲ��ܳ���100���ַ���");
		}
		GewaQuestion q = new GewaQuestion(member.getId());
		q.setTag(tag);
		q.setRelatedid(relatedid);
		if (category != null)
			q.setCategory(category);
		if (category != null)
			q.setCategoryid(categoryid);
		q.setTitle(title);
		q.setCitycode(citycode);
		q.setMembername(member.getNickname());
		daoService.saveObject(q);
		return getSingleResultXmlView(model, q.getId());
	}
	
	/**
	 * ��ӻظ�
	 * 
	 * @param memberEncode
	 * @param questionid
	 * @param ip
	 * @param content
	 * @param model
	 * @return
	 */
	@RequestMapping("/inner/common/member/addAnswer.xhtml")
	public String answer(String sessid, Long questionid, String ip, String content, ModelMap model) {
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if (member == null) {
			return getErrorXmlView(model, ApiConstant.CODE_MEMBER_NOT_EXISTS,"�û������ڣ�");
		}
		GewaAnswer answer = new GewaAnswer("");
		answer.setQuestionid(questionid);
		answer.setContent(content);
		answer.setMemberid(member.getId());
		answer.setIp(ip);
		GewaQuestion question = daoService.getObject(GewaQuestion.class,answer.getQuestionid());
		if(null==question){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,"���ⲻ���ڣ�");
		}
		if (StringUtils.isBlank(answer.getContent())) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,"�ش����ݲ���Ϊ�գ�");
		}
		if (StringUtil.getByteLength(answer.getContent()) > 4000) {
			return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR,"�ش����ݲ��ܳ���4000���ַ�������ǰ��������"+ StringUtil.getByteLength(answer.getContent())+ "�ַ�");
		}
		if (WebUtils.checkPropertyAll(answer)) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,"���зǷ��ַ���");
		}
		boolean addPoint = false;
		Integer point = ExpGrade.EXP_ANSWER_ADD_COMMON;
		if (question.getMemberid().longValue() != member.getId().longValue()
				&& !qaService.isAnswerQuestion(answer.getQuestionid(),
						member.getId())) { // ���ش�������û����Ӿ���ֵ(�������Լ�)
			addPoint = true;
			GewaQaExpert expert = qaService.getQaExpertByMemberid(member
					.getId());
			if (expert != null
					&& GewaQaExpert.STATUS_Y.equals(expert.getStatus())) {
				memberService.addExpForMember(member.getId(),ExpGrade.EXP_ANSWER_ADD_EXPERT); // ר��
				point = ExpGrade.EXP_ANSWER_ADD_EXPERT;
			} else {
				memberService.addExpForMember(member.getId(),ExpGrade.EXP_ANSWER_ADD_COMMON); // ��ͨ����
			}
		}
		Long mid = member.getId();
		Member questionMember = daoService.getObject(Member.class,question.getMemberid());
		question.setReplymemberid(mid);
		question.setUpdatetime(new Timestamp(System.currentTimeMillis()));
		question.addReplycount();
		if (GewaQuestion.QS_STATUS_Z.equals(question.getQuestionstatus()))
			question.setQuestionstatus(GewaQuestion.QS_STATUS_N);
		String key = blogService.filterContentKey(answer.getContent());
		if (StringUtils.isNotBlank(key)) {
			answer.setStatus(Status.N_FILTER);
			daoService.saveObject(answer);
			String title = "���˷�����֪���ظ���" + key;
			String value = "���˶��֪ⷢ���ظ����������˹ؼ���memberId = " + member.getId()+ ",[�û�IP:" + ip + "]" + question.getTitle() + "\n"+ answer.getContent();
			monitorService.saveSysWarn(title, value, RoleTag.bbs);
		} 
		daoService.saveObjectList(question, answer);
		if (addPoint) {
			GewaQaPoint sendquestion = new GewaQaPoint(question.getId(),answer.getId(), member.getId(), point,GewaQaPoint.TAG_REPLYQUESTION);
			daoService.saveObject(sendquestion);
		}
		if (!question.getMemberid().equals(mid)
				&& (GewaQuestion.QS_STATUS_N.equals(question
						.getQuestionstatus()) || GewaQuestion.QS_STATUS_Z
						.equals(question.getQuestionstatus()))) {// �����ʼ�
			model.put("questionnickname", questionMember.getNickname());
			model.put("nickname", member.getNickname());
			model.put("question",
					BeanUtil.getBeanMapWithKey(question, "id", "title"));
			model.put("content", answer.getContent());
			String body = "���ڸ�����֪���ϵ����ʣ� <a href='" + config.getBasePath()+ "qa/qaDetail.xhtml?qid=" + question.getId() + "'>"+ question.getTitle() + "</a> �����µĻش��ˡ�";
			userMessageService.sendSiteMSG(question.getMemberid(),SysAction.ACTION_QUESTION_NEW_ANSWER, question.getId(),body);
		}
		if (StringUtils.equals(answer.getStatus(), Status.N_FILTER)) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,"�㷢������Ӱ��������йؼ��ʡ���ͨ������Ա��˺���ʾ!");
		}
		return getSingleResultXmlView(model, "true");
	}
	
	/**
	 * ��ѯ�����ѽ�������Լ���Ѵ�
	 * @return
	 */
	@RequestMapping("/inner/common/qa/newQaList.xhtml")
	public String newQaList(String tag,	Long relatedid,	@RequestParam(defaultValue = "5", required = false, value = "maxnum") Integer maxnum,
			@RequestParam(required = false, value = "citycode") String citycode,
			ModelMap model) {
		if (maxnum > 50) {
			maxnum = 50;
		}
		List<Long> memberidList=new ArrayList<Long>();
		List<GewaQuestion> questionList =qaService.getQuestionList(citycode,tag,relatedid,GewaQuestion.QS_STATUS_Y,"addtime",0,maxnum);
		//��ѻش�
		Map<Long, GewaAnswer> answerMap = new HashMap<Long, GewaAnswer>();
		for (GewaQuestion gewaQuestion : questionList) {
			GewaAnswer gewaAnswer = qaService.getBestAnswerByQuestionid(gewaQuestion.getId());
			answerMap.put(gewaQuestion.getId(), gewaAnswer);
			memberidList.add(gewaAnswer.getMemberid());
			memberidList.add(gewaQuestion.getMemberid());
		}
		model.put("answerMap", answerMap);
		addCacheMember(model, memberidList);
		model.put("questionList", questionList);
		return getXmlView(model, "inner/common/newQaList.vm");
	}
}
