package com.gewara.web.action.home;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.Config;
import com.gewara.command.EmailRecord;
import com.gewara.constant.Status;
import com.gewara.constant.SysAction;
import com.gewara.constant.content.CommonType;
import com.gewara.model.bbs.commu.Commu;
import com.gewara.model.content.News;
import com.gewara.model.drama.Drama;
import com.gewara.model.movie.Movie;
import com.gewara.model.user.EmailInvite;
import com.gewara.model.user.FriendInfo;
import com.gewara.model.user.HiddenMember;
import com.gewara.model.user.Member;
import com.gewara.model.user.SysMessageAction;
import com.gewara.model.user.Treasure;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.content.NewsService;
import com.gewara.service.drama.DramaService;
import com.gewara.service.member.FriendService;
import com.gewara.service.movie.MCPService;
import com.gewara.support.ErrorCode;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.MailService;
import com.gewara.untrans.WalaApiService;
import com.gewara.untrans.activity.SynchActivityService;
import com.gewara.util.BeanUtil;
import com.gewara.util.BindUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.BaseHomeController;
import com.gewara.web.action.user.ProcessSendEmail;
import com.gewara.xmlbind.activity.RemoteActivity;
import com.gewara.xmlbind.bbs.Comment;

@Controller
public class FriendController extends BaseHomeController {
	//TODO:�Ƴ�
	@Autowired
	@Qualifier("mailService")
	private MailService mailService;
	@Autowired
	@Qualifier("config")
	private Config config;
	public void setConfig(Config config) {
		this.config = config;
	}

	@Autowired
	@Qualifier("friendService")
	private FriendService friendService;

	public void setMemberService(FriendService friendService) {
		this.friendService = friendService;
	}

	@Autowired
	@Qualifier("mcpService")
	private MCPService mcpService;

	public void setMcpService(MCPService mcpService) {
		this.mcpService = mcpService;
	}

	@Autowired
	@Qualifier("synchActivityService")
	private SynchActivityService synchActivityService;

	public void setActivityRemoteService(SynchActivityService synchActivityService) {
		this.synchActivityService = synchActivityService;
	}

	@Autowired
	@Qualifier("blogService")
	private BlogService blogService;

	public void setBlogService(BlogService blogService) {
		this.blogService = blogService;
	}

	@Autowired
	@Qualifier("dramaService")
	private DramaService dramaService;

	public void setDramaService(DramaService dramaService) {
		this.dramaService = dramaService;
	}

	@Autowired
	@Qualifier("newsService")
	private NewsService newsService;

	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}

	@Autowired
	@Qualifier("commentService")
	private CommentService commentService;

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	@Autowired
	@Qualifier("walaApiService")
	private WalaApiService walaApiService;

	// �Ӻ���
	@RequestMapping("/home/friend/addFriend.xhtml")
	public String addFriend(ModelMap model, Long memberid) {
		Member fmember = daoService.getObject(Member.class, memberid);
		if (fmember == null)
			return showError(model, "���û������ڣ�");
		Member member = getLogonMember();
		if (memberid.equals(member.getId()) || friendService.isFriend(member.getId(), memberid)) {// �Ѿ��Ǻ���
			return showMessage(model, "�Ѿ��Ǻ��ѣ�����Ҫ�Ӻ��ѣ�");
		}
		model.put("micromember", daoService.getObject(Member.class, memberid));
		addCacheMember(model, memberid);
		model.putAll(controllerService.getCommonData(model, member, memberid));
		// model.put("memberDetail", fmember);
		return "home/friend/addFriend.vm";
	}

	@RequestMapping("/home/friend/inviteFriend.xhtml")
	public String inviteFriend(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		Member member = getLogonMember();
		String citycode = WebUtils.getAndSetDefault(request, response);
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		String title = "";
		String content = "";
		List<Movie> movieList = mcpService.getCurMovieListByMpiCount(citycode, 0, 1);
		if (movieList.size() > 0) {
			title += "�������" + member.getNickname() + "������������������Ϊ����";
		}
		model.put("title", title);
		model.put("content", content);
		model.put("encode", StringUtil.md5WithKey("" + member.getId()));
		return "home/friend/invite.vm";
	}

	private Map getSendInviteFriend2(String citycode) {
		Map map = new HashMap();
		// �
		List<RemoteActivity> activityList = new ArrayList<RemoteActivity>();
		ErrorCode<List<RemoteActivity>> code = synchActivityService.getRemoteActivityByMemberid(1984078L, null, 0, 1);
		if (code.isSuccess())
			activityList = code.getRetval();
		// ����
		List<News> newsList = newsService.getCurrentNewsByTag(citycode, null, CommonType.NEWSTYPE_NEWS, 0, 1);
		if (activityList.size() > 0)
			map.put("activity", activityList.get(0));
		if (newsList.size() > 0)
			map.put("news", newsList.get(0));
		return map;
	}

	// ������Ѽ���
	@RequestMapping("/home/friend/msnSendEmail.xhtml")
	public String msnSendEmail(ModelMap model, String msnEmail, String commuid) {
		Member member = getLogonMember();
		Thread t = new ProcessSendEmail(msnEmail, member.getId() + "", commuid, config.getBasePath());
		t.start();
		if (StringUtils.isNotBlank(msnEmail)) {
			String[] emails = msnEmail.split(",");
			for (String string : emails) {
				if (!friendService.isExistsEmail(member.getId(), string)) {
					EmailInvite ei = new EmailInvite(member.getId(), string);
					daoService.saveObject(ei);// ����msn����¼�û��������msn����
				}
			}
		}
		return showJsonSuccess(model);
	}

	@RequestMapping("/home/friend/inviteFriendSystem.xhtml")
	public String sendSystem(ModelMap model, String[] msnEmail, HttpServletRequest request, HttpServletResponse response) {
		Member member = getLogonMember();
		String citycode = WebUtils.getAndSetDefault(request, response);
		model.putAll(getSendInviteFriend2(citycode));
		String body = "�������Ϊ����";
		for (String msn : msnEmail) {
			Member inviteMember = daoService.getObjectByUkey(Member.class, "email", msn, false);
			if (inviteMember != null) {
				boolean invitedFriend = friendService.isInvitedFriend(member.getId(), inviteMember.getId());// �Ƿ��Ѿ������
				boolean isFriend = friendService.isFriend(member.getId(), inviteMember.getId());// �Ƿ��Ǻ���
				if (!invitedFriend && !isFriend) {
					SysMessageAction systemAction = new SysMessageAction(member.getId(), inviteMember.getId(), body, null,
							SysAction.ACTION_APPLY_FRIEND_ADD);
					daoService.saveObject(systemAction);
				}
			}
		}
		return showJsonSuccess(model);
	}

	@RequestMapping("/home/inviteFriendByEmail.xhtml")
	public String inviteFriendByEmail(ModelMap model, String captchaId, String captcha, String email, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isBlank(email)){
			return showJsonError(model, "������Է���email��");
		}
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		String citycode = WebUtils.getAndSetDefault(request, response);
		Member member = getLogonMember();
		String body = request.getParameter("body");
		String nickname = request.getParameter("nickname");
		String title = request.getParameter("title");
		String commuid = request.getParameter("commuid");
		String commuName = "";
		if (StringUtils.isNotBlank(commuid)) {
			Commu commu = daoService.getObject(Commu.class, Long.parseLong(commuid));
			commuName = commu.getName();
			model.put("commu", BeanUtil.getBeanMapWithKey(commu, "id", "name"));
		}
		model.put("member", BeanUtil.getBeanMapWithKey(member, "id", "nickname", "email", "headpicUrl"));
		model.put("body", body);
		model.put("title", title);
		model.put("encode", StringUtil.md5WithKey("" + member.getId()));
		email = StringUtil.substitute(email, "[�� ;]", ",", false);
		String[] emails = email.split(",");
		if (emails.length > 10)
			return showJsonError(model, "�������10�������ַ��");
		for (String mail : emails) {
			try {
				if (ValidateUtil.isEmail(mail)) {
					if (!friendService.isExistsEmail(member.getId(), email)) {
						EmailInvite ei = new EmailInvite(member.getId(), mail);
						daoService.saveObject(ei);// ��¼�û��������email����
					}
					if (StringUtils.isNotBlank(commuid)) {
						String etitle = "�������" + member.getNickname() + "���������Ȧ��" + commuName;
						//TODO:ҵ�񷽷�����GewaMailService
						mailService.sendTemplateEmail(EmailRecord.SENDER_GEWARA, etitle, "mail/inviteFriendToCommunity.vm", model, email);
					} else {
						// ����
						List<Comment> commentList = commentService.getCommentList(null, null, member.getId(), "", Status.Y_NEW, 0, 3);
						model.put("commentList", BeanUtil.getBeanMapList(commentList, "body", "addtime", "address"));
						addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(commentList));
						// ������ӳ��Ӱ������
						List<Long> dramaidList = dramaService.getNowDramaList(citycode, 0, 2);
						List<Drama> dramaList = new ArrayList<Drama>();
						if (dramaidList.size() > 0)
							dramaList = daoService.getObjectList(Drama.class, dramaidList);
						if (dramaList.size() > 0)
							model.put("dramaOne", BeanUtil.getBeanMapWithKey(dramaList.get(0), "limg", "dramaname", "clickedtimes"));
						if (dramaList.size() > 1)
							model.put("dramaTwo", BeanUtil.getBeanMapWithKey(dramaList.get(1), "limg", "dramaname", "clickedtimes"));

						List<Movie> movieList = new ArrayList<Movie>();
						movieList = mcpService.getCurMovieListByMpiCount(citycode, 0, 2);
						if (movieList.size() > 0)
							model.put("movieOne", BeanUtil.getBeanMapWithKey(movieList.get(0), "limg", "moviename", "clickedtimes"));
						if (movieList.size() > 1)
							model.put("movieTwo", BeanUtil.getBeanMapWithKey(movieList.get(1), "limg", "moviename", "clickedtimes"));
						//TODO:ҵ�񷽷�����GewaMailService
						mailService.sendTemplateEmail(nickname, title, "mail/inviteFriendByEmail.vm", model, email);
					}
				}
			} catch (Exception e) {
				dbLogger.error("", e);
			}
		}
		return showJsonSuccess(model);
	}

	// ���ͼӺ�������
	@RequestMapping("/home/friend/saveFriendInvite.xhtml")
	public String addFriend(ModelMap model, Long memberid, String body) {
		Member member = getLogonMember();
		if (memberid.equals(member.getId()))
			return showJsonError(model, "�Լ����ܼ��Լ�Ϊ���ѣ�");
		boolean invitedFriend = friendService.isInvitedFriend(member.getId(), memberid);
		if (invitedFriend)
			return showJsonError(model, "���Ѿ����ͼ�Ϊ��������,��Ⱥ� �Է���ȷ��!");
		boolean isFriend = friendService.isFriend(member.getId(), memberid);
		if (isFriend)
			return showJsonError(model, "�Է��Ѿ�����ĺ���!");
		Member member2 = daoService.getObject(Member.class, memberid);
		if (member2 == null)
			return showJsonError(model, "���û������ڣ�");
		boolean isTre = blogService.isTreasureMember(member.getId(), memberid);
		if (!isTre) {
			Treasure treasure = new Treasure(member.getId(), Treasure.TAG_MEMBER, memberid, Treasure.ACTION_COLLECT);
			walaApiService.addTreasure(treasure);
			daoService.saveObject(treasure);
		}
		SysMessageAction systemAction = new SysMessageAction(member.getId(), member2.getId(), body, member2.getId(),
				SysAction.ACTION_APPLY_FRIEND_ADD);
		daoService.saveObject(systemAction);
		return showJsonSuccess(model);
	}

	// ɾ������
	@RequestMapping("/home/friend/deleteFriend.xhtml")
	public String deleteFriend(ModelMap model, Long memberid) {
		Member member = getLogonMember();
		friendService.deleteFriend(member.getId(), memberid);
		return showJsonSuccess(model);
	}

	// ɾ������
	@RequestMapping("/home/friend/deleteInfo.xhtml")
	public String deleteInfo(ModelMap model, Long id, boolean flag) {
		Member member = getLogonMember();
		// Map jsonMap = new HashMap();
		if (!flag) {// ɾ�����غ���(hiddenMember)
			HiddenMember hiddenMember = daoService.getObject(HiddenMember.class, id);
			if (hiddenMember == null)
				return showJsonError(model, "���û������ڣ�");
			if (!hiddenMember.getInviteid().equals(member.getId()))
				return showJsonError(model, "��ûȨ��ɾ����");
			daoService.removeObject(hiddenMember);
			// jsonMap.put("memberid", member.getId());
		} else {// ɾ�����ѱ�ע
			FriendInfo friendInfo = daoService.getObject(FriendInfo.class, id);
			if (friendInfo == null)
				return showJsonError(model, " ���û������ڣ�");
			if (!friendInfo.getAddmemberid().equals(member.getId()))
				return showJsonError(model, "��ûȨ��ɾ����");
			daoService.removeObject(friendInfo);
		}
		return showJsonSuccess(model);
	}

	@RequestMapping("/home/friend/addMember.xhtml")
	public String addMember(ModelMap model) {
		Member member = getLogonMember();
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		return "home/friend/addMember.vm";
	}

	@RequestMapping("/home/friend/saveAddMember.xhtml")
	public String saveAddMember(ModelMap model, String email, String realname, String mobile, HttpServletRequest request) {
		Member exists = daoService.getObjectByUkey(Member.class, "email", email, false);
		if (exists != null)
			return showJsonError(model, "�û��Ѿ�����");
		exists = memberService.getMemberByNickname(realname);
		if (exists != null)
			return showJsonError(model, "�û��Ѿ�����");

		Member member = getLogonMember();
		HiddenMember hiddenMember = new HiddenMember("");
		if (StringUtils.isNotBlank(realname)) {
			boolean matchNickname = ValidateUtil.isCNVariable(realname, 1, 20);
			if (!matchNickname)
				return showJsonError(model, "���Ƹ�ʽ����ȷ�����ܰ���������ţ�");
		}
		if (StringUtils.isNotBlank(email)) {
			boolean matchemail = ValidateUtil.isEmail(email);
			if (!matchemail)
				return showJsonError(model, "Email��ʽ����ȷ!");
		}
		if (StringUtils.isNotBlank(mobile)) {
			boolean matchmobile = ValidateUtil.isMobile(mobile);
			if (!matchmobile)
				return showJsonError(model, "�ֻ���ʽ����ȷ!");
		}
		if (StringUtils.isBlank(mobile) && StringUtils.isBlank(email)) {
			return showJsonError(model, "�ֻ���Email����ȫΪ��!");
		}
		Map dataMap = request.getParameterMap();
		BindUtils.bindData(hiddenMember, dataMap);
		daoService.saveObject(hiddenMember);
		if (StringUtils.isNotBlank(email)) {// ������ͷ����ʼ�
			String radom = StringUtil.md5WithKey(hiddenMember.getId() + "");
			String returnUrl = "setUpMemberPassword.xhtml?hiddenmemberid=" + hiddenMember.getId() + "&encode=" + radom;
			model.put("member", BeanUtil.getBeanMapWithKey(member, "id", "nickname", "email"));
			model.put("hiddenMember", BeanUtil.getBeanMapWithKey(hiddenMember, "realname"));
			model.put("returnUrl", returnUrl);
			String title = "�������" + member.getNickname() + "�ڸ����������Ϊ����";
			//TODO:ҵ�񷽷�����GewaMailService
			mailService.sendTemplateEmail(EmailRecord.SENDER_GEWARA, title, "mail/inviteToGewara.vm", model, email);
		}
		return showJsonSuccess(model);
	}

	@RequestMapping("/home/friend/existFriendList.xhtml")
	public String existFriendList(ModelMap model, String nickName, String email) {
		Member member = getLogonMember();
		Member existMember = null;
		nickName = StringUtils.trim(nickName);
		email = StringUtils.trim(email);
		boolean matchNickname = ValidateUtil.isCNVariable(nickName, 1, 20);
		boolean matchemail = ValidateUtil.isEmail(email);
		if (matchemail) {
			existMember = daoService.getObjectByUkey(Member.class, "email", email, false);
		}
		if (existMember == null && matchNickname) {
			existMember = memberService.getMemberByNickname(nickName);
		}
		if (existMember != null) {
			boolean isMyFriend = friendService.isFriend(member.getId(), existMember.getId());
			boolean b = existMember.getId().equals(member.getId()) || isMyFriend;
			model.put("isMyFriend", b);
			model.put("existMember", existMember);
		}
		model.put("isExist", existMember == null ? false : true);
		return "home/friend/addMemberInfo.vm";
	}

	@RequestMapping("/home/friend/saveFriendInfo.xhtml")
	public String saveFriendInfo(ModelMap model, Long friendinfoid, String realname, String email, String mobile, Long memberinfoid) {
		Member member = getLogonMember();
		FriendInfo friendInfo = new FriendInfo();
		if (StringUtils.isNotBlank(realname)) {
			boolean matchNickname = ValidateUtil.isCNVariable(realname, 1, 20);
			if (!matchNickname)
				return showJsonError(model, "���Ƹ�ʽ����ȷ�����ܰ���������ţ�");
		}
		if (StringUtils.isNotBlank(email)) {
			boolean matchemail = ValidateUtil.isEmail(email);
			if (!matchemail)
				return showJsonError(model, "�ʼ���ʽ����ȷ!");
		}
		if (StringUtils.isNotBlank(mobile)) {
			boolean matchmobile = ValidateUtil.isMobile(mobile);
			if (!matchmobile)
				return showJsonError(model, "�ֻ���ʽ����ȷ!");
		}
		if (StringUtils.isBlank(realname) && StringUtils.isBlank(email) && StringUtils.isBlank(mobile)) {
			return showJsonError(model, "����,Email,�ֻ�����ȫΪ�գ�");
		}
		if (friendinfoid != null)
			friendInfo = daoService.getObject(FriendInfo.class, friendinfoid);
		friendInfo.setAddmemberid(member.getId());// �����
		friendInfo.setMemberid(memberinfoid);// memberid
		friendInfo.setEmail(email);
		friendInfo.setMobile(mobile);
		friendInfo.setRealname(realname);
		daoService.saveObject(friendInfo);
		return showJsonSuccess(model);
	}

	@RequestMapping(value = "/processSendEmail.xhtml", method = RequestMethod.POST)
	@ResponseBody
	public String processSendEmail(String emailListStr, String memberid, String commuid, String check, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String ck = StringUtil.md5WithKey(memberid, 10);
		if (!ck.equals(check))
			return "��֤����";
		String[] emailList = StringUtils.split(emailListStr, ",");
		Member member = daoService.getObject(Member.class, new Long(memberid));
		model.put("member", member);
		if (member == null)
			return "�û�������";
		String commuName = null;
		if (StringUtils.isNotBlank(commuid)) {
			Commu commu = daoService.getObject(Commu.class, new Long(commuid));
			if (commu == null)
				return "Ȧ�Ӳ�����";
			if(!commu.hasStatus(Status.Y)) return "��Ȧ���ѱ�ɾ�ˣ�";
			commuName = commu.getName();
			model.put("commu", commu);
		}
		String citycode = WebUtils.getAndSetDefault(request, response);
		for (String msn : emailList) {
			Member inviteMember = daoService.getObjectByUkey(Member.class, "email", msn, false);
			if (inviteMember == null && ValidateUtil.isEmail(msn)) {
				if (StringUtils.isNotBlank(commuid)) {
					String title = "�������" + member.getNickname() + "���������Ȧ��" + commuName;
					//TODO:ҵ�񷽷�����GewaMailService
					mailService.sendTemplateEmail(EmailRecord.SENDER_GEWARA, title, "mail/inviteFriendToCommunity.vm", model, msn);
				} else {
					model.put("encode", StringUtil.md5WithKey("" + member.getId()));
					// ����
					List<Comment> commentList = commentService.getCommentList(null, null, member.getId(), "", Status.Y_NEW, 0, 3);
					model.put("commentList", BeanUtil.getBeanMapList(commentList, "body", "addtime", "address"));
					addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(commentList));
					// ������ӳ��Ӱ������
					List<Long> dramaidList = dramaService.getNowDramaList(citycode, 0, 2);
					List<Drama> dramaList = new ArrayList<Drama>();
					if (dramaidList.size() > 0)
						dramaList = daoService.getObjectList(Drama.class, dramaidList);
					if (dramaList.size() > 0)
						model.put("dramaOne", BeanUtil.getBeanMapWithKey(dramaList.get(0), "limg", "dramaname", "clickedtimes"));
					if (dramaList.size() > 1)
						model.put("dramaTwo", BeanUtil.getBeanMapWithKey(dramaList.get(1), "limg", "dramaname", "clickedtimes"));

					List<Movie> movieList = new ArrayList<Movie>();
					movieList = mcpService.getCurMovieListByMpiCount(citycode, 0, 2);
					if (movieList.size() > 0)
						model.put("movieOne", BeanUtil.getBeanMapWithKey(movieList.get(0), "limg", "moviename", "clickedtimes"));
					if (movieList.size() > 1)
						model.put("movieTwo", BeanUtil.getBeanMapWithKey(movieList.get(1), "limg", "moviename", "clickedtimes"));
					String title = "�������" + member.getNickname() + "������������������Ϊ����";
					//TODO:ҵ�񷽷�����GewaMailService
					mailService.sendTemplateEmail(EmailRecord.SENDER_GEWARA, title, "mail/inviteFriendByEmail.vm", model, msn);
				}
			}
		}
		return "success";
	}
}