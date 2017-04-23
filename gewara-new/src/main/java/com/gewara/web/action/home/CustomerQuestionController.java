package com.gewara.web.action.home;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.Status;
import com.gewara.constant.order.AddressConstant;
import com.gewara.model.bbs.CustomerAnswer;
import com.gewara.model.bbs.CustomerQuestion;
import com.gewara.model.user.Member;
import com.gewara.service.OperationService;
import com.gewara.service.bbs.CustomerQuestionService;
import com.gewara.support.ServiceHelper;
import com.gewara.util.DateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.BaseHomeController;
import com.gewara.web.util.PageUtil;

@Controller
public class CustomerQuestionController extends BaseHomeController{
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}
	
	@Autowired@Qualifier("customerQuestionService")
	private CustomerQuestionService customerQuestionService;
	public void setCustomerQuestionService(CustomerQuestionService customerQuestionService) {
		this.customerQuestionService = customerQuestionService;
	}
	//������
	@RequestMapping("/home/acct/advise.xhtml")
	public String advise(ModelMap model){
		Member member = getLogonMember();
		model.put("logonMember", member);
		return "home/acct/advise.vm";
	}
	//�û������б�
	@RequestMapping("/home/acct/customerQuestion.xhtml")
	public String customerQuestion(ModelMap model){
		Member member = getLogonMember();
		model.put("logonMember", member);
		return "home/acct/feedback.vm";
	}
	//������ϸ
	@RequestMapping("/home/acct/customerDetail.xhtml")
	public String customerDetail(@RequestParam("qid")Long qid, ModelMap model){
		Member member = getLogonMember();
		CustomerQuestion customerQuestion = daoService.getObject(CustomerQuestion.class, qid);
		if(customerQuestion == null) return show404(model, "�����ʵ���Դ������");
		if(!member.getId().equals(customerQuestion.getMemberid())) return show404(model, "���ܲ鿴���˷�����Ϣ");
		if (Status.isHidden(customerQuestion.getStatus())) {
			return show404(model, "��������������˻�ɾ����");
		}
		model.put("customerQuestion", customerQuestion);
		addCacheMember(model, customerQuestion.getMemberid());
		return "home/acct/customerDetail.vm";
	}
	
	/**
	 * 	�û� - Ͷ�߽��� - ���ӱ��� 
	 */
	@RequestMapping("/home/blog/saveCustomerQ.xhtml")
	public String saveCustomerQ(String email, String tag, String body, String captchaId, String captcha, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError_CAPTCHA_ERROR(model);
	   if(StringUtils.isBlank(email)) return showJsonError(model, "���䲻��Ϊ�գ�");
	   if(StringUtils.isBlank(body)) return showJsonError(model, "���ݲ���Ϊ�գ�");
	   if(!CustomerQuestion.TAG_LIST.contains(tag)) return showJsonError(model, "�ύ���ʹ���");
	   String userID = WebUtils.getRemoteIp(request);
	   String opkey = OperationService.TAG_ADVISE + userID;
	   if(!operationService.updateOperation(opkey, OperationService.HALF_HOUR, 30)){
		   return showJsonError(model, "����������Ƶ�ʲ���̫�죡");
	   }
	   Member member = getLogonMember();
	   String citycode = WebUtils.getAndSetDefault(request, response);
	   CustomerQuestion question = customerQuestionService.addCustomerQuestion(citycode, member.getId(), email, tag, body, AddressConstant.ADDRESS_WEB);
	   if(question == null) return showJsonError(model, "���潨������ʧ�ܣ�");
	   return showJsonSuccess(model, question.getId().toString());
	}
	
	/***
	 *  �û�Ͷ�߽����� - ��ϸ
	 */
	@RequestMapping("/home/blog/customerQList.xhtml")
	public String customerQList(Integer pageNo, ModelMap model){
		Member member = getLogonMember();
		if(pageNo == null) pageNo = 0;
		int rowsPerpage = 15;
		int firstRow = pageNo * rowsPerpage;
		
		int count = customerQuestionService.getCustometQCount(member.getId(), null, null);
		List<CustomerQuestion> customerList = customerQuestionService.getCustomerQList(member.getId(), null, null, firstRow, rowsPerpage);
		
		PageUtil pageUtil = new PageUtil(count, rowsPerpage, pageNo, "/home/blog/customerQList.xhtml", true, true);
		Map params = new HashMap();
		params.put("pageNo", pageNo);
		pageUtil.initPageInfo(params);
		model.put("pageUtil", pageUtil);
		model.put("customerList", customerList);
		addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(customerList));
		model.put("logonMember", member);
		return "common/customerList.vm";
	}
	
	
	@RequestMapping("/home/blog/customerAnswerList.xhtml")
	public String customerDetail(@RequestParam("qid")Long qid, Integer pageNo, ModelMap model) {
		Member member = getLogonMember();
		CustomerQuestion customerQuestion = daoService.getObject(CustomerQuestion.class, qid);
		if(customerQuestion == null) return show404(model, "�����ʵ���Դ������");
		if(!member.getId().equals(customerQuestion.getMemberid())) return show404(model, "���ܲ鿴���˷�����Ϣ");
		if (Status.isHidden(customerQuestion.getStatus())) {
			return show404(model, "��������������˻�ɾ����");
		}
		model.put("customerQuestion", customerQuestion);
		// �û��ظ�
		if (pageNo == null) pageNo = 0;
		int rowsPerpage = 15;
		int firstRow = pageNo * rowsPerpage;
		List<CustomerAnswer> answerList = customerQuestionService.getAnswersByQid(qid, firstRow, rowsPerpage);
		Integer count = customerQuestionService.getAnswerCountByQid(qid);
		model.put("answerList", answerList);
		
		
		PageUtil pageUtil=new PageUtil(count, rowsPerpage, pageNo, "/home/blog/customerAnswerList.xhtml", true, true);
		Map params = new HashMap(); 
		params.put("qid", qid);
		pageUtil.initPageInfo(params);
		model.put("pageUtil", pageUtil);
		model.put("logonMember", member);
		return "common/answerList.vm";
	}
	
	@RequestMapping("/home/blog/saveCustomerAnswer.xhtml")
	public String saveCustomerAnswer(@RequestParam("qid")Long qid, String body, String captchaId, String captcha, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError_CAPTCHA_ERROR(model);
		Member member = getLogonMember();
		String opkey = OperationService.TAG_ADVISE + member.getId();
	   if(!operationService.isAllowOperation(opkey, 40)){
		   return showJsonError(model, "�ظ�Ƶ�ʲ���̫�죡");
	   }
		CustomerQuestion customerQuestion = daoService.getObject(CustomerQuestion.class, qid);
		if(customerQuestion==null) return showJsonError(model, "�����ʵ���Դ������");
		if(StringUtils.equals(CustomerQuestion.Y_STOP, customerQuestion.getStatus()))return showJsonError(model, "�÷��������Ѿ����رգ�");
		if(StringUtils.isBlank(body))return showJsonError(model, "�ظ������ݲ���Ϊ�գ�");
		
		CustomerAnswer customerAnswer = new CustomerAnswer(customerQuestion.getId(), member.getId(), body);
		customerAnswer.setNickname(member.getNickname());
		customerAnswer.setIsAdmin(CustomerAnswer.NO_ADMIN);
		customerAnswer.setCitycode(WebUtils.getAndSetDefault(request, response));
		daoService.saveObject(customerAnswer);
		customerQuestion.setUpdatetime(DateUtil.getCurFullTimestamp());
		customerQuestion.setStatus(CustomerQuestion.Y_NEW);
		daoService.updateObject(customerQuestion);
		operationService.updateOperation(opkey, 40);
		return showJsonSuccess(model);
	}
	
}
