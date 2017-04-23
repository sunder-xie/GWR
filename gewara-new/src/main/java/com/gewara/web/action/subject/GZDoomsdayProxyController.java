package com.gewara.web.action.subject;
import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.model.user.Member;
import com.gewara.service.bbs.CommonVoteService;
import com.gewara.util.DateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;

@Controller
public class GZDoomsdayProxyController extends AnnotationController {
	@Autowired@Qualifier("commonVoteService")
	private CommonVoteService commonVoteService;
	
	/****
	 * �㶫ĩ��ר�� ר��
	 * */
	private static final String TAG = "gzdoomsday";
	private static final String FLAG = "gzdoomsday_virtual";
	
	@RequestMapping("/subject/proxy/vote/delvote.xhtml")
	public String delvote(String id, ModelMap model){
		commonVoteService.delVote(id);
		return showJsonSuccess(model);
	}
	
	// �������ͶƱ
	@RequestMapping("/subject/proxy/vote/virtualVote.xhtml")
	public String virtualVote(String itemid, Integer support, ModelMap model){
		commonVoteService.addCommonVote(FLAG, itemid, support);
		return showJsonSuccess(model);
	}

	//����ĩ�ջ�û�ͶƱ
	@RequestMapping("/ajax/subject/proxy/vote/voteit.xhtml")
	public String voteit(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, String itemid, HttpServletRequest request,ModelMap model){
		//1. �ʱ��: 20121212 ~ 20121218
		Timestamp enddate = DateUtil.parseTimestamp("2012-12-19 00:00:00");
		Timestamp curdate = DateUtil.getCurFullTimestamp();
		if(curdate.compareTo(enddate) >= 0) return showJsonError(model, "��ѽ�����");
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		//2. memberid + movieid ��֤, ������ͶƱ����
		Map<String, Object> voteMap = commonVoteService.getSingleVote(TAG, member.getId(), null);
		if(voteMap != null) return showJsonError(model, "����Ͷ��Ʊ��");
		commonVoteService.addVoteMap(TAG, itemid, member.getId(), FLAG);
		return showJsonSuccess(model);
	}
}
