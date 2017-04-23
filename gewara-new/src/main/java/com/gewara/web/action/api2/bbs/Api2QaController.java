package com.gewara.web.action.api2.bbs;

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

/**
 * �ʴ�API
 * 
 * @author taiqichao
 * 
 */
@Controller
public class Api2QaController extends BaseApiController {

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

	/**
	 * �ʴ��б�
	 * 
	 * @param tag
	 *            ������ǩ
	 * @param relatedid
	 *            ��������id
	 * @param from
	 *            ҳ��
	 * @param maxnum
	 *            ҳ��С
	 * @param citycode
	 *            ���д���
	 * @param status
	 *            ״̬����ѡֵ:N(�����),Y(�ѽ��),Z(����),noproper(�������)
	 * @param orderby
	 *            ���򣬿�ѡֵ:clickedtimes(��ע��)��hotvalue(�ȶ�)��addtime(���ʱ��)��updatetime
	 *            (����ʱ��)
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/qa/questionList.xhtml")
	public String questionList(
			@RequestParam("tag") String tag,
			Long relatedid,
			@RequestParam(defaultValue = "0", required = false, value = "from") Integer from,
			@RequestParam(defaultValue = "20", required = false, value = "maxnum") Integer maxnum,
			@RequestParam(required = false, value = "citycode") String citycode,
			@RequestParam(required = false, value = "status", defaultValue = "N") String status,
			@RequestParam(required = false, value = "orderby") String orderby,
			ModelMap model) {
		if (maxnum > 50) {
			maxnum = 50;
		}
		List<GewaQuestion> questionList = qaService.getQuestionList(citycode,tag, relatedid, status, orderby, from, maxnum);
		int count = qaService.getQuestionCount(citycode, tag, relatedid, status);
		model.put("count", count);
		model.put("questionList", questionList);
		return getXmlView(model, "api2/qa/questionList.vm");
	}
	
	/**
	 * ��ѯ�����ѽ�������Լ���Ѵ�
	 * @return
	 */
	@RequestMapping("/api2/qa/newQaList.xhtml")
	public String newQaList(
			String tag,
			Long relatedid,
			@RequestParam(defaultValue = "5", required = false, value = "maxnum") Integer maxnum,
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
		return getXmlView(model, "api2/qa/newQaList.vm");
	}
	

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
	@SuppressWarnings("deprecation")
	@RequestMapping("/api2/qa/addQuestion.xhtml")
	public String addQuestion(String citycode,String memberEncode,
			String tag,Long relatedid,
			@RequestParam(required = true, value = "tag")  String category,
			@RequestParam(required = true, value = "relatedid") Long categoryid, String title, ModelMap model) {
		Member member = memberService.getMemberByEncode(memberEncode);
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
		return this.getDirectXmlView(model, "<data><result>true</result><questionid>"+q.getId()+"</questionid></data>");
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
	@RequestMapping("/api2/qa/addAnswer.xhtml")
	public String answer(String memberEncode,
			@RequestParam(required = true, value = "questionid") Long questionid,
			String ip, String content, ModelMap model) {
		Member member = memberService.getMemberByEncode(memberEncode);
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
		} else {
			daoService.saveObject(answer);
		}
		daoService.saveObject(question);
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
		return getXmlView(model, "api/mobile/result.vm");
	}

}
