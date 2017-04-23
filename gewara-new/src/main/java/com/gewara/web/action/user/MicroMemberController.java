package com.gewara.web.action.user;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.CookieConstant;
import com.gewara.constant.ExpGrade;
import com.gewara.constant.Status;
import com.gewara.constant.order.AddressConstant;
import com.gewara.json.MemberStats;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.model.user.Treasure;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.member.FriendService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.MemberCountService;
import com.gewara.untrans.ShareService;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;
import com.gewara.xmlbind.bbs.Comment;

/**
 * �й������û���ϸҳ
 * @author lss
 *
 */
@Controller
public class MicroMemberController extends AnnotationController {
	@Autowired@Qualifier("commentService")
	private CommentService commentService;

	@Autowired@Qualifier("blogService")
	private BlogService blogService;
	@Autowired@Qualifier("memberCountService")
	private MemberCountService memberCountService;

	
	@Autowired@Qualifier("friendService")
	private FriendService friendService;
	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	public void setShareService(ShareService shareService) {
		this.shareService = shareService;
	}

	/**
	 *  wara ���˽��ܱ���
	 * */
	@RequestMapping("/wala/saveMemberDesc.xhtml")
	public String saveMemberDesc(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model, String memberdesc){
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showError(model, "δ��¼!");
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		memberInfo.setIntroduce(memberdesc);
		daoService.saveObject(memberInfo);
		model.put("memberInfo", memberInfo);
		return "wala/memberdesc.vm";
	}
	
	
	/**
	 * ɾ��΢��
	 */
	@RequestMapping("/wala/deleteMicroBlog.xhtml")
	public String deleteMicroBlog(ModelMap model, Long mid, @CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, HttpServletRequest request) {
		Comment comment = commentService.getCommentById(mid);
		if (comment == null) {
			return show404(model, "��ɾ������Դ������");
		}
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if (comment.getMemberid().equals(member.getId())) {
			comment.setStatus(Status.N_DELETE);
			memberService.addExpForMember(member.getId(), -ExpGrade.EXP_COMMENT_ADD_COMMON);
			commentService.updateComment(comment);
			memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_COMMENTCOUNT, 1, false);
			return showJsonSuccess(model);
		} else {
			return showJsonError(model, "����ɾ�����˵�������");
		}
	}
	
	/**
	 * ת��
	 * 
	 * @param model
	 * @param transferid
	 * @param body
	 * @return
	 */
	@RequestMapping("/wala/addTransferComment.xhtml")
	public String addTransferComment(@CookieValue(value=CookieConstant.MEMBER_POINT, required=false)String pointxy, ModelMap model, Long transferid, 
			String body, @CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, HttpServletRequest request) {
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if (StringUtils.isBlank(body) || "˳��˵��ʲô��...".equals(body))
			body = "ת��������";
		if (StringUtils.isNotBlank(body) && body.length() > 140)
			return showJsonError(model, "ת�ط�������ݳ��Ȳ��ܴ���140�ַ���");
		Comment comment = commentService.getCommentById(transferid);
		if (comment != null) {
			String pointx = null, pointy = null;
			if(StringUtils.isNotBlank(pointxy)){
				List<String> pointList = Arrays.asList(StringUtils.split(pointxy, ":"));
				if(pointList.size() == 2){
					pointx = pointList.get(0);
					pointy = pointList.get(1);
				}
			}
			ErrorCode<Comment> result = commentService.addMicroComment(member, comment.getTag(), comment.getRelatedid(), body, null, AddressConstant.ADDRESS_WEB, transferid, false, null, pointx, pointy, WebUtils.getIpAndPort(ip, request));
			if(result.isSuccess()){
				shareService.sendShareInfo("wala",result.getRetval().getId(), result.getRetval().getMemberid(), null);
				return showJsonSuccess(model);
			}
			return showJsonError(model, result.getMsg());
		}
		return showJsonError(model, "ת��ʧ�ܣ�");
	}
	
	/**
	 * ������ѹ�ϵ
	 */
	@RequestMapping("/wala/removeFriend.xhtml")
	public String removeFriend(Long memberid,ModelMap model, @CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, HttpServletRequest request){
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		Member memberto = daoService.getObject(Member.class,memberid);
		try{
			//������ѹ�ϵ
			friendService.deleteFriend(member.getId(), memberto.getId());
			//ɾ����ע��¼
			blogService.cancelTreasure(member.getId(), memberto.getId(), Treasure.TAG_MEMBER, Treasure.ACTION_COLLECT);
		}catch(Exception e){
			return showJsonError(model,"������ѹ�ϵʧ�ܣ�");
		}
		return showJsonSuccess(model);
	}
	
	/**
	 * ����û��Ƿ����
	 */
	@RequestMapping("/check/checkUserName.xhtml")
	public String checkUserName(ModelMap model,String username, @CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, HttpServletRequest request){
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		Member toMember = friendService.checkUserName(username);
		if(toMember == null){
			return showJsonError(model, "��Ҫ����˽�ŵ��û������ڣ�");
		}
		boolean isTreasure = blogService.isTreasureMember(toMember.getId(), member.getId());
		if(!isTreasure){
			return showJsonError(model, "��Ҫ����˽�ŵ��û���δ��ע�㣡�㲻�ܲ���TA����˽�ţ�");
		}else{
			return showJsonSuccess(model);
		}
	}
}
