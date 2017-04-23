package com.gewara.web.action.ajax;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.gewara.constant.DiaryConstant;
import com.gewara.constant.ExpGrade;
import com.gewara.constant.Flag;
import com.gewara.constant.Status;
import com.gewara.constant.SysAction;
import com.gewara.constant.TagConstant;
import com.gewara.json.MemberStats;
import com.gewara.model.bbs.Bkmember;
import com.gewara.model.bbs.Diary;
import com.gewara.model.bbs.DiaryBase;
import com.gewara.model.bbs.DiaryComment;
import com.gewara.model.bbs.VoteChoose;
import com.gewara.model.bbs.VoteOption;
import com.gewara.model.user.Member;
import com.gewara.model.user.SysMessageAction;
import com.gewara.service.OperationService;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.bbs.DiaryService;
import com.gewara.support.ErrorCode;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.MemberCountService;
import com.gewara.untrans.SearchService;
import com.gewara.untrans.ShareService;
import com.gewara.untrans.UntransService;
import com.gewara.untrans.impl.ControllerService;
import com.gewara.untrans.monitor.MonitorService;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.util.BindUtils;
import com.gewara.util.ChangeEntry;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.util.XSSFilter;
import com.gewara.web.action.AnnotationController;
import com.gewara.xmlbind.bbs.Comment;
@Controller
public class BlogAjaxController extends AnnotationController {
	@Autowired@Qualifier("memberCountService")
	private MemberCountService memberCountService;
	@Autowired@Qualifier("monitorService")
	private MonitorService monitorService;
	public void setMonitorService(MonitorService monitorService) {
		this.monitorService = monitorService;
	}
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}	
	@Autowired@Qualifier("controllerService")
	private ControllerService controllerService;
	public void setControllerService(ControllerService controllerService) {
		this.controllerService = controllerService;
	}

	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	public void setUntransService(UntransService untransService) {
		this.untransService = untransService;
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
	@Autowired@Qualifier("diaryService")
	private DiaryService diaryService;
	public void setDiaryService(DiaryService diaryService) {
		this.diaryService = diaryService;
	}
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	public void setShareService(ShareService shareService) {
		this.shareService = shareService;
	}
	@Autowired@Qualifier("commentService")
	private CommentService commentService;
	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}
	@Autowired@Qualifier("searchService")
	private SearchService searchService;
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	/***
	 * ��̳ - ���ӱ���
	 * saveDiary()
	 */
	@RequestMapping("/blog/saveDiary.xhtml")
	public String saveDiary(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, @CookieValue(value=CookieConstant.MEMBER_POINT, required=false) String pointxy,
			HttpServletRequest request, HttpServletResponse response, String captchaId, String captcha, 
			String diarycontent, Long id, ModelMap model){
		Map<String, String> diaryMap = WebUtils.getRequestMap(request);
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError_CAPTCHA_ERROR(model);
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		
		if(member == null) return showJsonError_NOT_LOGIN(model);
		if(blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		Integer eggs = blogService.isBadEgg(member);
		if (eggs != 777) {
			return showJsonError(model, eggs+"");
		}
		DiaryBase diary = null;
		String type = DiaryConstant.DIARY_TYPE_MAP.get(diaryMap.get("tp"));
		if(type == null) return showJsonError(model, "ȱ�����ͣ�");
		boolean isnew = false;

		ChangeEntry changeEntry = null;
		if(id != null){//�޸���־
			diary = diaryService.getDiaryBase(id);
			if(diary == null){
				return showJsonError(model, "�����Ӳ����ڻ�ɾ����");
			}
			if(!diary.canModify())return showJsonError(model, "��ʷ���Ӳ����޸ģ�");
			int maxRights = blogService.getMaxRights(diary.getDtag(), diary.getDrelatedid(), diary.getMemberid(), member);
			if(maxRights < Bkmember.ROLE_BANZHU) return showJsonError_NORIGHTS(model);
			changeEntry = new ChangeEntry(diary);
		}else{
			if(!blogService.allowAddContent(OperationService.TAG_ADDCONTENT, member.getId())){
				return showJsonError(model, "����Ƶ�ʲ���̫�죡");
			}
			diary = new Diary("");
			diary.setMemberid(member.getId());
			diary.setMembername(member.getNickname());
			diary.setReplyname(member.getNickname());
			diary.setReplyid(member.getId());
			isnew = true;
		}
		BindUtils.bindData(diary, diaryMap);
		if(StringUtils.isBlank(diary.getTag())) diary.setRelatedid(null);
		if(StringUtils.isBlank(diary.getCategory()))	diary.setCategoryid(null);
		
		diary.setType(type);
		if(StringUtils.isBlank(diary.getSubject())) return showJsonError(model, "���ⲻ��Ϊ�գ�");
		if(StringUtils.isBlank(diarycontent)) return showJsonError(model, "���ݲ���Ϊ�գ�");
		String checkStr = diary.getSubject() + diarycontent;
		if(WebUtils.checkString(checkStr)) return showJsonError(model, "���зǷ��ַ���");
		diary.setSubject(XSSFilter.filterAttr(diary.getSubject()));
		String key = blogService.filterContentKey(checkStr);
		boolean filter = false;
		boolean isNight = blogService.isNight();
		
		if(StringUtils.isNotBlank(key)){
			diary.setStatus(Status.N_FILTER);
			filter = true;
		}else if(isNight){
			diary.setStatus(Status.N_NIGHT);	// ҹ������
			filter = true;
			isNight = true;
		}else{
			diary.setStatus(Status.Y_NEW);
		}
		if(StringUtils.isBlank(diary.getSummary())) {
			String res = StringUtil.getHtmlText(diarycontent, 400);
			diary.setSummary(res);
		}
		if(StringUtil.getByteLength(diary.getSubject())>60) return showJsonError(model, "���ӱ��������");
		if(StringUtil.getByteLength(diarycontent)>20000) return showJsonError(model, "�������ݹ�����");
		String citycode = WebUtils.getAndSetDefault(request, response);
		diary.setCitycode(citycode);
		// ������������2011(Ӱ��, �ĵ�etc.)
		if(!StringUtils.equals(DiaryConstant.DIARY_TYPE_TOPIC_DIARY, diary.getType())){
			if(diary.getCategoryid() != null && StringUtils.isNotBlank(diary.getCategory()) ){
				diary.setType(DiaryConstant.DIARY_TYPE_COMMENT);
				if(StringUtils.equals(diary.getTag(), TagConstant.TAG_CINEMA)) 
					diary.setDivision(DiaryConstant.DIVISION_A);
			}
		}
		if(StringUtils.isBlank(diary.getDivision()))
			if(diary.getRelatedid()!=null){
				diary.setDivision(DiaryConstant.DIVISION_Y);
			}else diary.setDivision(DiaryConstant.DIVISION_N);
		if(StringUtils.contains(diarycontent, Flag.FLAG_USERFILES))diary.addFlag(Flag.FLAG_USERFILES);
		List<String> imagelist = WebUtils.getPictures(diarycontent);
		if(!imagelist.isEmpty()){
			diary.setDiaryImage(imagelist.get(0));
		}
		diary.setIp(WebUtils.getIpAndPort(ip, request));
		daoService.saveObject(diary);
		blogService.saveDiaryBody(diary.getId(), diary.getUpdatetime(), diarycontent);
		searchService.pushSearchKey(diary);
		if(isnew){//����
			memberService.addExpForMember(member.getId(), ExpGrade.EXP_DIARY_ADD);
			if("cinema".equals(diary.getTag())){
				shareService.sendShareInfo("moviecomment", diary.getId(), member.getId(), null);
			}else if("theatre".equals(diary.getTag())){
				shareService.sendShareInfo("dramacomment", diary.getId(), member.getId(), null);
				//��������ʱ �������۹������ݳ��ͳ��ݵ�������  ��ʼ
//				if("drama".equals(diary.getCategory())){
//					Drama drama = daoService.getObject(Drama.class,diary.getCategoryid());
//					if(drama!=null){
//						drama.addDiarycount(1);
//						daoService.saveObject(drama);
//					}
//				}
//				Theatre theatre = daoService.getObject(Theatre.class,diary.getRelatedid());
//				if(theatre!=null){
//					theatre.addDiarycount(1);
//					daoService.saveObject(theatre);
//				}
				//��������ʱ �������۹������ݳ��ͳ��ݵ�������  ����
			}else{
				shareService.sendShareInfo("topic", diary.getId(), member.getId(), null);
			}
			if(type.equals(DiaryConstant.DIARY_TYPE_COMMENT)){
				boolean bought = false;
				if(diary.getRelatedid()!=null) bought = untransService.isPlayMemberByTagAndId(diary.getMemberid(), diary.getTag(), diary.getRelatedid());
				if(!bought && diary.getCategoryid()!=null) bought = untransService.isPlayMemberByTagAndId(diary.getMemberid(), diary.getCategory(), diary.getCategoryid());
				if(bought){
					diary.addFlag(Flag.TICKET);
					daoService.saveObject(diary);
					//ֻҪ����Ӱ����������������� 2012-02-17
				}
			}
			//��������+1
			memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_DIARYCOUNT, 1, true);
			//��������+1
		}
		// �����޸����Ӽ�log
		if (changeEntry != null) {
			monitorService.saveChangeLog(member.getId(), DiaryComment.class, id, changeEntry.getChangeMap(diary));
		}
		if(filter){
			if(!isNight){
				SysMessageAction sysMessage=new SysMessageAction(SysAction.STATUS_RESULT);
				sysMessage.setFrommemberid(1L);
				sysMessage.setBody("������ӡ�"+diary.getSubject()+"���к���\"���йؼ���\",�ȴ�����Ա��ˣ�");
				sysMessage.setTomemberid(diary.getMemberid());
				daoService.saveObject(sysMessage);
				
				String title = "���˷���������" + key;
				String content = "���˶��ⷢ�����������˹ؼ���memberId = " + member.getId() +",[�û�IP:" + WebUtils.getRemoteIp(request) + "]"+ diary.getSubject() + "\n" + diarycontent;
				monitorService.saveSysWarn(Diary.class, diary.getId(), title, content, RoleTag.bbs);
				Map jsMap = new HashMap();
				jsMap.put("retcode", "403");
				jsMap.put("returl", config.getBasePath()+ "blog/");
				jsMap.put("retmsg", "�㷢������Ӱ���\"���йؼ���\"��ͨ������Ա��˺���ʾ��");
				return showJsonError(model, jsMap);
			}else{
				Map jsMap = new HashMap();
				jsMap.put("retcode", "403");
				jsMap.put("returl", config.getBasePath()+ "blog/");
				jsMap.put("retmsg", "ҹ�䷢���������Ҫͨ������Ա��˺������ʾ��");
				return showJsonError(model, jsMap);
			}
		}
		
		//��������+1
		memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_COMMENTCOUNT, 1, true);
		//��������+1
		String link = config.getBasePath() + "blog/t" + diary.getId();
		String pointx = "", pointy = "";
		if(StringUtils.isNotBlank(pointxy)){
			List<String> pointList = Arrays.asList(StringUtils.split(pointxy, ":"));
			if(pointList.size() == 2){
				pointx = pointList.get(0);
				pointy = pointList.get(1);
			}
		}
		String linkStr = "����������  <a href=\""+link+"\" target=\"_blank\">"+diary.getSubject()+"</a>";
		Map otherinfoMap = new HashMap();
		String summary = diarycontent;
		if(StringUtils.length(summary)>110){
			summary = VmUtils.htmlabbr(summary, 110);
			if(StringUtils.isBlank(summary)){
				summary = "<a href=\"" + config.getBasePath() + "blog/t" + diary.getId()+"\" target=\"_blank\">"+"����>>"+"</a>";
			}else{
				summary = summary + "..."+"<a href=\"" + config.getBasePath() + "blog/t" + diary.getId()+"\" target=\"_blank\">"+"����>>"+"</a>";
			}
			
		}
		otherinfoMap.put("diaryContent", summary);
		otherinfoMap.put("clickedtimes", diary.getClickedtimes());
		String otherinfo = JsonUtils.writeObjectToJson(otherinfoMap);
		ErrorCode<Comment> ec = commentService.addMicroComment(member, TagConstant.TAG_DIARY_MEMBER, diary.getId(),linkStr, diary.getDiaryImage(),null, null, true, null,otherinfo,pointx, pointy, WebUtils.getIpAndPort(ip, request), null);
		if(ec.isSuccess()){
			shareService.sendShareInfo("wala",ec.getRetval().getId(), ec.getRetval().getMemberid(), null);
		}

		if(StringUtils.isNotBlank(request.getParameter("communityid")))//Ȧ�ӷ�������ת��Ȧ�ӻ�����
			return showJsonSuccess(model, request.getParameter("communityid"));
		else
			return showJsonSuccess(model, diary.getId().toString());
	}
	
	
	/**
	 *	��̳ - ����ͶƱ 
	 */
	@RequestMapping("/blog/saveVoteChoose.xhtml")
	public String saveVoteChoose(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model){
		Map<String, String[]> chooseMap = request.getParameterMap();
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		Long did = Long.parseLong(ServiceHelper.get(chooseMap, "did"));
		DiaryBase vote = diaryService.getDiaryBase(did);
		if(vote == null) return showJsonError_NOT_FOUND(model);
		if(vote.getOverdate().before(DateUtil.getBeginningTimeOfDay(new Date()))){//ͶƱ�Ѿ�����
			return showJsonError(model, "ͶƱ�Ѿ�������");
		}
		List list = diaryService.getVoteChooseByDiaryidAndMemberid(did, member.getId());
		if(!list.isEmpty())return showJsonError_REPEATED(model);
		String[] optionid = ServiceHelper.get(chooseMap, "optionid").split(",");
		List<VoteChoose> vcList = new ArrayList<VoteChoose>();
		List<VoteOption> voList = new ArrayList<VoteOption>();
		for(String oid:optionid){
			VoteOption option = daoService.getObject(VoteOption.class, new Long(oid));
			option.addSelelctednum();
			VoteChoose vc = new VoteChoose(member.getId());
			vc.setDiaryid(did);
			vc.setOptionid(new Long(oid));
			vcList.add(vc);
			voList.add(option);
		}
		
		daoService.saveObjectList(voList);
		daoService.saveObjectList(vcList);
		return showJsonSuccess(model);
	}
	
	
	/**
	 *	��̳ - ����ͶƱ 
	 */
	@RequestMapping("/blog/saveVote.xhtml")
	public String saveVote(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, String captchaId, String captcha, 
			HttpServletResponse response, String body, ModelMap model){
		Map<String, String[]> voteMap = request.getParameterMap();
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError_CAPTCHA_ERROR(model);
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		if(blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		if(!blogService.allowAddContent(OperationService.TAG_ADDCONTENT, member.getId())){
			return showJsonError(model, "����Ƶ�ʲ���̫�죡");
		}
		Integer eggs = blogService.isBadEgg(member);
		if (eggs != 777) {
			return showJsonError(model, eggs+"");
		}
		String sType=request.getParameter("radio_multi");
		Diary diary = new Diary("");
		diary.setMemberid(member.getId());
		diary.setMembername(member.getNickname());
		diary.setReplyname(member.getNickname());
		if(sType.equals("topic_vote_multi")){
			diary.setType(DiaryConstant.DIARY_TYPE_TOPIC_VOTE_MULTI);
		}else{
			diary.setType(DiaryConstant.DIARY_TYPE_TOPIC_VOTE_RADIO);
		}
		BindUtils.bindData(diary, voteMap);
		if(StringUtils.indexOf(body.toLowerCase(), "<script") > 0){//��js�����棡
			String title = "���˶��ⷢ����";
			String content = "���˶��ⷢ��������JS���룡memberId = " + member.getId() + "\n" + body;
			monitorService.saveSysWarn(Diary.class, diary.getId(), title, content, RoleTag.bbs);
			return showJsonError_DATAERROR(model);
		}
		if(StringUtils.isBlank(diary.getSummary())) diary.setSummary(StringUtil.getEscapeText(body, 300));
		String citycode = WebUtils.getAndSetDefault(request, response);
		diary.setCitycode(citycode);
		diary.setIp(WebUtils.getIpAndPort(ip, request));
		boolean isNight = blogService.isNight();
		daoService.saveObject(diary);
		searchService.pushSearchKey(diary);//��������������������
		blogService.saveDiaryBody(diary.getId(), diary.getUpdatetime(), body);
		List<VoteOption> voList = new ArrayList();
		for(int i=1;i<=20;i++){
			String optionconent = ServiceHelper.get(voteMap, "option"+i);
			if(StringUtils.isNotBlank(optionconent)){
				VoteOption option = new VoteOption(optionconent, diary.getId());
				voList.add(option);
			}
		}

		daoService.saveObjectList(voList);
		memberService.addExpForMember(member.getId(), ExpGrade.EXP_DIARY_ADD);
		if(isNight){
			Map jsMap = new HashMap();
			jsMap.put("retcode", "403");
			jsMap.put("returl", config.getBasePath()+ "blog/");
			jsMap.put("retmsg", "ҹ�䷢���������Ҫͨ������Ա��˺������ʾ��");
			return showJsonError(model, jsMap);
		}
		return showJsonSuccess(model, diary.getId().toString());
	}
	
	
	/**
	 *  ��̳ - ���ӻظ�
	 */
	@RequestMapping("/blog/saveDiaryComment.xhtml")
	public String saveDiaryComment(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, String captchaId, String captcha,
			HttpServletRequest request, Long commentId, Long diaryId, String body, HttpServletResponse response, ModelMap model){
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		
		if(!blogService.allowAddContent(OperationService.TAG_ADDCONTENT, member.getId())){
			return showJsonError(model, "����Ƶ�ʲ���̫�죡");
		}
		
		if(blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		DiaryBase diary = diaryService.getDiaryBase(diaryId);
		if(diary == null){
			return showJsonError(model, "�����Ӳ����ڻ�ɾ����");
		}
		if(!diary.canModify()) return showJsonError(model, "��ʷ���Ӳ������ۣ�");
		if(diary.getStatus().indexOf(Status.Y_LOCK)>=0) {
			return showJsonError(model, "�������Ѿ�����ס,���ܻظ���");
		}
		if(StringUtil.getByteLength(body)>20000) return showJsonError(model, "�����ַ�������");
		DiaryComment comment = null;
		String key = blogService.filterContentKey(body);
		if(commentId == null){
			comment = new DiaryComment(diary.getId(), member.getId(), body);
			String citycode = WebUtils.getAndSetDefault(request, response);
			comment.setCitycode(citycode);
			comment.setIp(WebUtils.getIpAndPort(ip, request));
			if(StringUtils.isNotBlank(key)) comment.setStatus(Status.N_FILTER);
			diary.addReplycount();
			diary.setReplyid(comment.getMemberid());
			diary.setReplytime(comment.getAddtime());
			diary.setReplyname(member.getNickname());
			if(!Status.Y_DOWN.equals(diary.getStatus()))
				diary.setUtime(comment.getAddtime());
			diary.setViewed(false);
			daoService.saveObjectList(diary, comment);
			memberService.addExpForMember(member.getId(), ExpGrade.EXP_DIARY_REPLYER_ADD);
		}else{
			comment = daoService.getObject(DiaryComment.class, commentId);
			ChangeEntry changeEntry = new ChangeEntry(comment);
			int maxRights = blogService.getMaxRights(diary.getDtag(), diary.getDrelatedid(), comment.getMemberid(), member);
			if(maxRights < Bkmember.ROLE_BANZHU) return showJsonError_NORIGHTS(model);

			if(StringUtils.isNotBlank(key)) comment.setStatus(Status.N_FILTER);
			comment.setBody(body);
			comment.setUpdatetime(new Timestamp(System.currentTimeMillis()));
			daoService.saveObject(comment);
			// �����޸ĵĻظ����Ӽ�log
			monitorService.saveChangeLog(member.getId(), DiaryComment.class, commentId, changeEntry.getChangeMap(comment));
		}
		if(StringUtils.isNotBlank(key)){
			String title = "���˷���������ӻظ���" + key;
			String content = "���˷���������ӻظ����������˹ؼ���memberId = " + member.getId()+",[�û�IP:" + WebUtils.getRemoteIp(request) + "]" + diary.getSubject() + "\n" + body;
			monitorService.saveSysWarn(DiaryComment.class, comment.getId(), title, content, RoleTag.bbs);
		}
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/blog/addDiaryFlower.xhtml")
	public String addDiaryFlower(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long diaryId, String tag, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		String opkey = "adf" + member.getId() + diaryId;
		if(!operationService.updateOperationOneDay(opkey, true)) 
			return showJsonError_REPEATED(model);
		DiaryBase diary = diaryService.getDiaryBase(diaryId);
		if(diary == null)	return showJsonError_DATAERROR(model);
		
		//����֧���������򷴶�����
		if(StringUtils.equals(tag, "oppose")){
			diary.addPoohnum();
			daoService.saveObject(diary);
			return showJsonSuccess(model, diary.getPoohnum().toString());
		}
		diary.addFlowernum();
		daoService.saveObject(diary);
		return showJsonSuccess(model, diary.getFlowernum().toString());		
	}

	@RequestMapping("/blog/getDiaryComment.xhtml")
	public String getDiaryComment(Long replyid, ModelMap model){
		DiaryComment comment = daoService.getObject(DiaryComment.class, replyid);
		if(comment == null) return showJsonError_DATAERROR(model);
		Map result = new HashMap();
		result.put("replycontent", comment.getBody());
		result.put("replyid", comment.getId());
		Member member = daoService.getObject(Member.class, comment.getMemberid());
		if(member!=null)
			result.put("replymember", member.getRealname());
		return showJsonSuccess(model, result);
	}
	
	@RequestMapping("/blog/saveGoLaLaResult.xhtml")
	public String saveGoLaLaResult(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model){
		Map<String, String[]> resultMap = request.getParameterMap();
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if (member == null) return showJsonError_NOT_LOGIN(model);
		if (blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		String tmp = "";
		boolean hasVoted = false;
		for(Map.Entry<String, String[]> entry : resultMap.entrySet()){
			VoteChoose voteChoose = new VoteChoose(member.getId());
			if(entry.getKey().startsWith("vote")){
				String value = entry.getValue()[0];
				if(StringUtils.isNotBlank(value)) {
					String[] voteList = value.split("_");
					boolean isVoted = diaryService.isMemberVoted(member.getId(), Long.parseLong(voteList[0]));
					if(isVoted){ //δͶƱ
						VoteOption option = daoService.getObject(VoteOption.class, Long.parseLong(voteList[1]));
						option.addSelelctednum();
						voteChoose.setDiaryid(Long.parseLong(voteList[0]));
						voteChoose.setOptionid(Long.parseLong(voteList[1]));
						daoService.saveObject(voteChoose);
					}else {
						hasVoted = true;
						Long diaryId = Long.parseLong(voteList[0]);
						DiaryBase diary = diaryService.getDiaryBase(diaryId);
						if(diary != null)
							tmp = tmp +","+ diary.getSubject();
					}
				}
			}
		}
		if(!hasVoted){
			return showJsonSuccess(model);
		} else {
			tmp = tmp.substring(1, tmp.length())+",���Ѿ�ͶƱ��";
			return showJsonError(model, tmp);
		}
	}
	@RequestMapping("/blog/removeDiaryComment.xhtml")
	public String removeDiaryComment(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, String tag, Long replyid, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		DiaryComment comment = daoService.getObject(DiaryComment.class, replyid);
		Integer rights = blogService.getMaxRights(tag, null, comment.getMemberid(), member);
		if(rights < Bkmember.ROLE_BANZHU) return showJsonError_NORIGHTS(model);
		if(comment.getStatus().equals(Status.N_DELETE)) return showJsonError(model, "�����ӻظ��Ѿ�ɾ��!");
		comment.setStatus(Status.N_DELETE);
		memberService.addExpForMember(comment.getMemberid(), -ExpGrade.EXP_DIARY_REPLYER_ADD);
		daoService.saveObject(comment);
		return showJsonSuccess(model);
	}
	
	
	@RequestMapping("/blog/fullDiaryBody.xhtml")
	public String fullDiaryBody(Long diaryid, ModelMap model){
		if(diaryid == null) return "common/diaryBody.vm";
		String diaryBody = blogService.getDiaryBody(diaryid);
		if(StringUtils.isBlank(diaryBody)) {
			return showJsonError(model, "");
		}
		model.put("diaryBody", diaryBody);
		return "common/diaryBody.vm";
	}
}
