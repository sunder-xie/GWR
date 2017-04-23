package com.gewara.web.action.admin.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.ExpGrade;
import com.gewara.constant.Status;
import com.gewara.constant.SysAction;
import com.gewara.model.bbs.qa.GewaAnswer;
import com.gewara.model.bbs.qa.GewaQaExpert;
import com.gewara.model.bbs.qa.GewaQaPoint;
import com.gewara.model.bbs.qa.GewaQuestion;
import com.gewara.model.user.SysMessageAction;
import com.gewara.service.bbs.QaService;
import com.gewara.util.BeanUtil;
import com.gewara.util.BindUtils;
import com.gewara.util.StringUtil;
import com.gewara.web.action.admin.BaseAdminController;

@Controller
public class AdminQaAjaxController extends BaseAdminController{
	@Autowired@Qualifier("qaService")
	private QaService qaService;
	public void setQaService(QaService qaService) {
		this.qaService = qaService;
	}
	public static Map reasons=new HashMap();
	static{
		reasons.put("1", "�����Ϣ");
		reasons.put("2", "�������л���");
		reasons.put("3", "�顢ɫ�������Ȳ�������Ϣ");
		reasons.put("4", "�Ƿ�������Υ����Ϣ");
	}
	//����ɾ��gewaraquestion����
	@RequestMapping("/admin/blog/ajax/batchRemoveQuestionById.xhtml")
	public String batchRemoveQuestionById(String idList,String reason,String reasonDetail, ModelMap model) {
		if(!reason.equals("5")){
			reason=reasons.get(reason).toString();
		}else{
			reason=reasonDetail;
		}
		
		for(String idString : idList.split(",")){
			GewaQuestion question = daoService.getObject(GewaQuestion.class, new Long(idString));
			question.setStatus(Status.N_DELETE);
			question.setReplycount(question.getReplycount()-1);
			daoService.saveObject(question);
			SysMessageAction sysMessage=new SysMessageAction(SysAction.STATUS_RESULT);
			sysMessage.setFrommemberid(1l);
			sysMessage.setBody("������ġ�"+question.getTitle().substring(0,question.getTitle().length()>5?5:question.getTitle().length())+"...�������漰��"+reason+"��,�ѱ�����Աɾ��,<br/>�����κ����ʣ���ʹ��վ���Ż��ʼ���gewara@gewara.com��<br/>�����Ա���ߡ�");
			sysMessage.setTomemberid(question.getMemberid());
			daoService.saveObject(sysMessage);
			GewaQaPoint qaPoint = daoService.getObject(GewaQaPoint.class, new Long(idString));
			daoService.removeObject(qaPoint);
			//��ѯ��Id�µĻش�
			List<GewaAnswer> answerList = qaService.getAnswerListByQuestionid(new Long(idString));
			List<GewaAnswer> answerlist=new ArrayList<GewaAnswer>();
			for(GewaAnswer answer : answerList){
					answer.setStatus(Status.N_DELETE);
					answerlist.add(answer);
			}
			daoService.saveObjectList(answerlist);
		}
		return showJsonSuccess(model);
	}
	//ɾ��gewaraquestion����
	@RequestMapping("/admin/blog/ajax/removeQuestionById.xhtml")
	public String removeQuestionById(Long qid,String reason,String reasonDetail, ModelMap model) {
		if(!reason.equals("5")){
			reason=reasons.get(reason).toString();
		}else{
			reason=reasonDetail;
		}
		GewaQuestion question = daoService.getObject(GewaQuestion.class, qid);
		question.setStatus(Status.N_DELETE);
		question.setReplycount(question.getReplycount()-1);
		daoService.saveObject(question);
		SysMessageAction sysMessage=new SysMessageAction(SysAction.STATUS_RESULT);
		sysMessage.setFrommemberid(1l);
		sysMessage.setBody("������ġ�"+question.getTitle().substring(0,question.getTitle().length()>5?5:question.getTitle().length())+"...�������漰��"+reason+"��,�ѱ�����Աɾ��,<br/>�����κ����ʣ���ʹ��վ���Ż��ʼ���gewara@gewara.com��<br/>�����Ա���ߡ�");
		sysMessage.setTomemberid(question.getMemberid());
		daoService.saveObject(sysMessage);
		GewaQaPoint qaPoint = daoService.getObject(GewaQaPoint.class, qid);
		daoService.removeObject(qaPoint);
		//��ѯ��Id�µĻش�
		List<GewaAnswer> answerList = qaService.getAnswerListByQuestionid(qid);
		List<GewaAnswer> answerlist=new ArrayList<GewaAnswer>();
		for(GewaAnswer answer : answerList){
				answer.setStatus(Status.N_DELETE);
				answerlist.add(answer);
		}
		daoService.saveObjectList(answerlist);
		return showJsonSuccess(model);
	}
	//�޸������ȶ�
	@RequestMapping("/admin/blog/ajax/setQAHotValue.xhtml")
	public String setQAHotValue(Long id, Integer hotvalue, ModelMap model) {
		//�����ȶ���Ϣ
		qaService.updateQAHotValue(id, hotvalue);
		return showJsonSuccess(model);
	}
	//����RECORDIDɾ���ش���Ϣ
	@RequestMapping("/admin/blog/ajax/removeAnswerById.xhtml")
	public String removeAnswerById(Long aid, ModelMap model){
		GewaAnswer answer = daoService.getObject(GewaAnswer.class, aid);
		answer.setStatus(Status.N_DELETE);
		daoService.saveObject(answer);
		memberService.addExpForMember(answer.getMemberid(), -ExpGrade.EXP_ANSWER_DEL_COMMON);
		GewaQuestion question = daoService.getObject(GewaQuestion.class, answer.getQuestionid());
		question.setReplycount(question.getReplycount()-1);
		daoService.saveObject(question);
		return showJsonSuccess(model);
	}
	//�޸�����ר���ȶ�
	@RequestMapping("/admin/blog/ajax/setQAExpertHotValue.xhtml")
	public String setQAExpertHotValue(Long id,Integer hotvalue, ModelMap model){
		qaService.updateQAExpertHotValue(id, hotvalue);
		return showJsonSuccess(model);
	}
	//�޸��Ƿ����
	@RequestMapping("/admin/blog/ajax/setAdminVerify.xhtml")
	public String setAdminVerify(Long id, ModelMap model){
		GewaQaExpert expert  = daoService.getObject(GewaQaExpert.class, id);
		GewaQaExpert qaExpert = qaService.getQaExpertStatusById(id);
		if (GewaAnswer.AS_STATUS_N.equals(qaExpert.getStatus())){
			expert.setStatus(GewaQaExpert.STATUS_Y);
		}else if (GewaAnswer.AS_STATUS_Y.equals(qaExpert.getStatus())){
			expert.setStatus(GewaQaExpert.STATUS_N);
		}
		daoService.saveObject(expert);
		return showJsonSuccess(model);
	}
	
	
	@RequestMapping("/admin/blog/ajax/getGewaAnswerInformation.xhtml")
	public String getGewaAnswerInformation(Long qid, ModelMap model){
		GewaAnswer data = daoService.getObject(GewaAnswer.class,qid);
		Map result = BeanUtil.getBeanMap(data);
		return showJsonSuccess(model, result);
	}
	@RequestMapping("/admin/blog/ajax/saveGewaAnswer.xhtml")
	public String saveGewaAnswer(Long id, HttpServletRequest request, ModelMap model){
		GewaAnswer answer = new GewaAnswer("");
		if (id!=null) {
			answer = daoService.getObject(GewaAnswer.class, new Long(id));
		}
		BindUtils.bindData(answer,request.getParameterMap());
		if(StringUtil.getByteLength(answer.getContent())>20000) return showError(model, "�����ַ�������");
		daoService.saveObject(answer);
		return showJsonSuccess(model);
	}
	@RequestMapping("/admin/blog/ajax/getQuestionById.xhtml")
	public String getQuestionById(Long id, ModelMap model) {
		GewaQuestion qa = daoService.getObject(GewaQuestion.class, id);
		Map result = BeanUtil.getBeanMap(qa);
		return showJsonSuccess(model, result);
	}
}
