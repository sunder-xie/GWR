package com.gewara.web.action.community;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.Config;
import com.gewara.constant.DiaryConstant;
import com.gewara.constant.Status;
import com.gewara.constant.SysAction;
import com.gewara.constant.content.SignName;
import com.gewara.constant.sys.JsonDataKey;
import com.gewara.model.bbs.Diary;
import com.gewara.model.bbs.commu.Commu;
import com.gewara.model.bbs.commu.CommuManage;
import com.gewara.model.bbs.commu.CommuMember;
import com.gewara.model.bbs.commu.CommuTopic;
import com.gewara.model.bbs.commu.VisitCommuRecord;
import com.gewara.model.common.County;
import com.gewara.model.common.Indexarea;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.content.Notice;
import com.gewara.model.content.Picture;
import com.gewara.model.user.Album;
import com.gewara.model.user.AlbumComment;
import com.gewara.model.user.Member;
import com.gewara.model.user.SysMessageAction;
import com.gewara.service.PlaceService;
import com.gewara.service.bbs.AlbumService;
import com.gewara.service.bbs.DiaryService;
import com.gewara.service.bbs.UserMessageService;
import com.gewara.service.content.NoticeService;
import com.gewara.service.member.FriendService;
import com.gewara.support.ReadOnlyTemplate;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.GewaPicService;
import com.gewara.util.BeanUtil;
import com.gewara.util.BindUtils;
import com.gewara.util.DateUtil;
import com.gewara.util.RelatedHelper;
import com.gewara.util.ValidateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.util.PageUtil;

/**
 * @author hxs(ncng_2006@hotmail.com)
 * @since Feb 2, 2010 10:08:18 AM
 */
@Controller
public class CommuController extends BaseCommuController {
	@Autowired@Qualifier("readOnlyTemplate")
	private ReadOnlyTemplate readOnlyTemplate;
	public void setReadOnlyHibernateTemplate(ReadOnlyTemplate readOnlyTemplate) {
		this.readOnlyTemplate = readOnlyTemplate;
	}
	@Autowired@Qualifier("config")
	private Config config;
	public void setConfig(Config config) {
		this.config = config;
	}

	@Autowired@Qualifier("friendService")
	private FriendService friendService;
	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}
	@Autowired@Qualifier("placeService")
   private PlaceService placeService;
	public void setPlaceService(PlaceService placeService) {
		this.placeService = placeService;
	}
	@Autowired@Qualifier("userMessageService")
	private UserMessageService userMessageService;
	public void setUserMessageService(UserMessageService userMessageService) {
		this.userMessageService = userMessageService;
	}
	@Autowired@Qualifier("albumService")
	private AlbumService albumService;
	public void setAlbumService(AlbumService albumService) {
		this.albumService = albumService;
	}
	@Autowired@Qualifier("noticeService")
	private NoticeService noticeService;
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
	@Autowired @Qualifier("diaryService")
	private DiaryService diaryService;
	public void setDiaryService(DiaryService diaryService) {
		this.diaryService = diaryService;
	}
	
	@Autowired @Qualifier("gewaPicService")
	private GewaPicService gewaPicService;
	public void setGewaPicService(GewaPicService gewaPicService) {
		this.gewaPicService = gewaPicService;
	}
	
	@RequestMapping("/quan/commuDetail.xhtml")
	public String commuDetail(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model,Long commuid,Long commutopicid, Long from, 
			HttpServletResponse response){
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null || !commu.hasStatus(Status.Y)){
			return showError(model, "�������Ȧ���Ѿ���ɾ����");
		}
		//msn�������start
		String invitetype="msnInviteFriend";
		if(from != null)
			WebUtils.setInviteFromCookie(response, config.getBasePath(), from, invitetype);
		//msn�������end
		
		int activityCount = 5;//����� 20 5
		int diaryCount = 10;//��������30,10
		int albumCount = 8;//���ͼƬ����16 8
		int voteCount = 5;//ͶƱ����10 5
		//����
		List<Diary> diaryList = null;
		List<CommuTopic> commuTopicList = commonService.getCommuTopicList(commuid, 0, 100);
		Map<String, String> layoutMap = jsonDataService.getJsonData(JsonDataKey.KEY_COMMULAYOUT + commuid);
		String[] commuTopicType = new String[]{DiaryConstant.DIARY_TYPE_TOPIC_DIARY,DiaryConstant.DIARY_TYPE_COMMENT};
		if("1".equals(layoutMap.get("diarytemplate"))){
			activityCount =Integer.valueOf(layoutMap.get("activity"));
			diaryCount =Integer.valueOf(layoutMap.get("diary"));
			albumCount =Integer.valueOf(layoutMap.get("album"));
			voteCount = Integer.valueOf(layoutMap.get("vote"));
			diaryList = commuService.getCommuDiaryListById(Diary.class, commuid,commuTopicType,commutopicid,0,diaryCount);
			model.put("diaryList", diaryList);
			model.put("commuTopicList", commuTopicList);
			model.put("diaryCount", diaryCount);
		}else if("2".equals(layoutMap.get("diarytemplate"))) {
			activityCount = Integer.valueOf(layoutMap.get("activitys"));
			albumCount = Integer.valueOf(layoutMap.get("albums"));
			voteCount = Integer.valueOf(layoutMap.get("votes"));
			Map<Long,List<Diary>> commuTopicMap = new HashMap<Long,List<Diary>>();
			if(!commuTopicList.isEmpty()){
				for (CommuTopic ct : commuTopicList) {
					Long id = ct.getId();
					diaryList = commuService.getCommuDiaryListById(Diary.class, commuid,commuTopicType,id,0,ct.getDisplaynum());
					commuTopicMap.put(id, diaryList);
				}
				model.put("commuTopicList", commuTopicList);
				model.put("commuTopicMap", commuTopicMap);
			}else{
				diaryList = commuService.getCommuDiaryListById(Diary.class, commuid,commuTopicType,commutopicid,0, diaryCount);
				model.put("diaryList", diaryList);
				model.put("diaryCount", diaryCount);
			}
		}else{
			diaryList = commuService.getCommuDiaryListById(Diary.class, commuid,commuTopicType,commutopicid,0, diaryCount);
			model.put("diaryList", diaryList);
			model.put("commuTopicList", commuTopicList);
		}
		model.put("activityCount", activityCount);
		model.put("albumCount", albumCount);
		model.put("voteCount", voteCount);
		model.putAll(getCommuCommonData(model, commu, member));
		//ͶƱ
		String[] type = new String[]{DiaryConstant.DIARY_TYPE_TOPIC_VOTE};
		List<Diary> voteList = commuService.getCommuDiaryListById(Diary.class, commuid, type ,null,0, voteCount);
		//���
		List<Picture> albumImageList = albumService.getPicturesByCommuidList(commuid, 0, albumCount);
		List<Notice> noticeList = noticeService.getNoticeListByCommuid(commuid, Notice.TAG_COMMU, 0, 1);
		if(!noticeList.isEmpty()) model.put("notice", noticeList.get(0));
		model.put("albumImageList", albumImageList);
		model.put("voteList", voteList);
		model.put("layout",layoutMap.get("diarytemplate"));
		
		// �������
		String[] invitefrom = WebUtils.getCookie4ProtectedPage(request, "page4pro");
		if(invitefrom == null){
			WebUtils.setCookie4ProtectedPage(request, response, "/");
			commu.setClickedtimes(commu.getClickedtimes()+1);
			daoService.saveObject(commu);
		}
		
		// ��鵱ǰȦ�ӵ�״̬
		String checkstatus = commuService.getCheckStatusByIDAndMemID(commuid);
		model.put("checkstatus", checkstatus);
		if(member!=null) {
			model.put("isAdmin", member.getId().equals(commu.getAdminid()));
			//Ȧ�ӳ�Ա���ʵļ�¼
			VisitCommuRecord visitCommuRecord = commuService.getVisitCommuRecordByCommuidAndMemberid(commuid, member.getId());
			if(visitCommuRecord==null){ //��һ�η���
				visitCommuRecord = new VisitCommuRecord(member.getId(), commuid);
			}
			if(visitCommuRecord.getLasttime().before(DateUtil.getCurTruncTimestamp())){//���ջ�û�з���Ȧ��
				visitCommuRecord.setVisitnum(visitCommuRecord.getVisitnum()+1);
			}
			visitCommuRecord.setLasttime(new Timestamp(System.currentTimeMillis()));
			try {
				daoService.saveObject(visitCommuRecord);
			} catch (Exception e) {
				dbLogger.error("", e);
				return "home/community/index.vm";
			}
		}
		return "home/community/index.vm";
	}
	
	/**
	 * Ȧ��������֤ 
	 */
	@RequestMapping("/home/commu/applyCertification.xhtml")
	public String applyCertification(ModelMap model, Long commuid){
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null || !commu.hasStatus(Status.Y)){
			showError(model, "�������Ȧ���Ѿ���ɾ����");
		}
		model.put("commu", commu);
		//��鵱ǰȦ���Ƿ������
		String checkstatus = commuService.getCheckStatusByIDAndMemID(commuid);
		if(CommuManage.STATUS_WAIT.equals(checkstatus)){
			return showError(model, "�����������������...");
		}else if(CommuManage.STATUS_PASS.equals(checkstatus)){
			return showError(model, "���������Ѿ�ͨ��");
		}
		model.put("logonMember", getLogonMember());
		return "home/community/customer.vm";
	}
	/**
	 * Ȧ��������֤��Ϣ���� 
	 */
	@RequestMapping("/home/commu/saveapplyCertification.xhtml")
	public String saveApplyCertification(ModelMap model, Long commuid, HttpServletRequest request){
		Map<String, String[]> daMap = request.getParameterMap();
		if(commuid==null){return showJsonError(model, "���ȵ�¼Ȧ��!");}
		String applymemberid = ServiceHelper.get(daMap, "applymemberid");
		Member member = getLogonMember();
		if(StringUtils.isBlank(applymemberid)||member==null){return showJsonError(model, "���ȵ�¼!");}
		if(!StringUtils.equals(member.getId()+"", applymemberid)){return showJsonError(model, "�����޸ı��˵�Ȧ��!");}
		CommuManage commuManage = new CommuManage(commuid);
		if(!ValidateUtil.isNumber(request.getParameter("contactphone"))) return showJsonError(model, "��ϵ�绰��ʽ����ȷ!");
		if(!ValidateUtil.isIDCard(request.getParameter("idnumber"))) return showJsonError(model, "���֤�Ÿ�ʽ����ȷ!");
		if(!ValidateUtil.isEmail(request.getParameter("email"))) return showJsonError(model, "email��ʽ����ȷ!");
		BindUtils.bindData(commuManage, daMap);
		commuManage.setCheckstatus(CommuManage.STATUS_WAIT);
		daoService.saveObject(commuManage);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/home/commu/saveApplyAddCommu.xhtml")
	public String saveAddCommu(ModelMap model, Long commuid, String body){
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null || !commu.hasStatus(Status.Y)){
			return showJsonError(model, "�������Ȧ���Ѿ���ɾ����");
		}
		Member member=this.getLogonMember();
		model.putAll(getCommuCommonData(model, commu, member));
		boolean isExistSysMessageAction = userMessageService.isExistSysMessageAction(commuid, member.getId(), SysAction.ACTION_APPLY_COMMU_JOIN, true);
		if(!isExistSysMessageAction) {//�����ڣ�û��������Ȧ��
			//���û��Ƿ���������Ȧ����,������ɾ��������Ϣ
			SysMessageAction sysMessageAction = userMessageService.getSysMessageAction(commuid, member.getId(), SysAction.ACTION_APPLY_COMMU_INVITE, false);
			if(sysMessageAction != null) {
				//���ڣ�ɾ��
				SysMessageAction sysMA = daoService.getObject(SysMessageAction.class, sysMessageAction.getId());
				daoService.removeObject(sysMA);
			}
			SysMessageAction systemAction = new SysMessageAction(member.getId(), commu.getAdminid(), body, commuid, SysAction.ACTION_APPLY_COMMU_JOIN);
			daoService.saveObject(systemAction);
			model.put("commuid", commuid);
			Map params = new HashMap();
			params.put("commuid", commuid);
			params.put("msg", "����ɹ�����ȴ�Ȧ�ӹ���Ա����ˣ�");
			return showJsonSuccess(model,params);
		}else{
			return showJsonError(model, "���Ѿ��������Ȧ�ӣ���ȴ�Ȧ�ӹ���Ա��ˣ�");
		}
	}
	
	@RequestMapping("/home/commu/inviteAddCommu.xhtml")
	public String inviteAddCommu(ModelMap model, Long commuid, Integer pageNo){
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null || !commu.hasStatus(Status.Y)){
			return showError(model, "�������Ȧ���Ѿ���ɾ����");
		}
		Member member = getLogonMember();
		//�����б�
		Map<Long, Member> memberMap = new HashMap<Long, Member>();
		//��ѯȦ�����г�Ա
		if(pageNo == null) pageNo=0;
		Integer rowsPerPage=40;
		Integer firstRowsPage = pageNo*rowsPerPage;
		Integer friendCount = friendService.getNotJoinCommuFriendCount(member.getId(), commuid);
		//����member.getId(), commuIdList ��ѯ�����л�û�вμӸ�Ȧ�ӵ�friendidList
		List<Long> friendidList = friendService.getNotJoinCommuFriendIdList(member.getId(), commuid, firstRowsPage, rowsPerPage);
		for(Long friendId : friendidList){
			Member memberInfo = daoService.getObject(Member.class, friendId);
			memberMap.put(friendId, memberInfo);
		}
		Map params = new HashMap(); 
		params.put("commuid", new String[]{commuid+""});
		PageUtil pageUtil = new PageUtil(friendCount, rowsPerPage, pageNo, "home/commu/inviteAddCommu.xhtml", true, true);
		pageUtil.initPageInfo(params);
		model.putAll(getCommuCommonData(model, commu, member));
		model.put("friendidList", friendidList);
		addCacheMember(model,friendidList);
		model.put("membersMap", memberMap);
		model.put("commuid", commuid);
		model.put("pageUtil", pageUtil);
		return "home/community/inviteAddCommuInfo.vm";
	}
	
	@RequestMapping("/home/commu/saveInviteAddCommu.xhtml")
	public String saveInviteAddCommu(HttpServletRequest request, ModelMap model, Long commuid){
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null || !commu.hasStatus(Status.Y)){
			return showJsonError(model, "�������Ȧ���Ѿ���ɾ����");
		}
		Map<String, String[]> friendMap = request.getParameterMap();
		for(String mmap : friendMap.keySet()){
			if(mmap.startsWith("friendid")){
				//�Ƿ��Ѿ�����������
				boolean isExistSysMessageAction = userMessageService.isExistSysMessageAction(commuid, new Long(friendMap.get(mmap)[0]), SysAction.ACTION_APPLY_COMMU_INVITE, false);
				if(!isExistSysMessageAction){
					//���û��Ƿ���������Ȧ����,������ɾ��������Ϣ
					SysMessageAction sysMessageAction = userMessageService.getSysMessageAction(commuid, new Long(friendMap.get(mmap)[0]), SysAction.ACTION_APPLY_COMMU_JOIN, true);
					//���ڣ�ɾ��
					if(sysMessageAction != null) {
						SysMessageAction sysMA = daoService.getObject(SysMessageAction.class, sysMessageAction.getId());
						daoService.removeObject(sysMA);
					}
					SysMessageAction systemAction = new SysMessageAction(commu.getAdminid(), new Long(friendMap.get(mmap)[0]), commu.getName(), commuid, SysAction.ACTION_APPLY_COMMU_INVITE);
					daoService.saveObject(systemAction);
				}
			}
		}
		return showJsonSuccess(model);
	}

	/**
	 * ��ȡȦ�ӻ�����Ϣ
	 * 
	 * @return
	 */
	@RequestMapping("/quan/getCommuDiaryList.xhtml")
	public String getCommuDiary(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, HttpServletRequest request, ModelMap model, Long commuid, Integer pageNo,Long commutopicid) {
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null || !commu.hasStatus(Status.Y)){
			return show404(model, "�������Ȧ���Ѿ���ɾ����");
		}
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member==null) return showError(model, "���ȵ�¼��");
		if(pageNo == null)pageNo = 0;
		int rowsPerPage = 20;
		int firstPerPage = pageNo * rowsPerPage;
		List<CommuTopic> commuTopicList = commonService.getCommuTopicList(commuid, 0, 100);
		// Ȧ�ӻ�����Ϣ
		List<Diary> listCommuDiary = commuService.getCommuDiaryListById(Diary.class, commuid,null,commutopicid,
				firstPerPage, rowsPerPage);
		Integer countCommuDiary = commuService.getCommuDiaryCount(Diary.class, commuid,null,commutopicid);
		PageUtil pageUtil = new PageUtil(countCommuDiary, rowsPerPage, pageNo,
				"quan/getCommuDiaryList.xhtml", true, true);

		//��ǰ�û�����������Ϣ
		List<Diary> listFriendDiary=diaryService.getFriendDiaryList(DiaryConstant.DIARY_TYPE_TOPIC_DIARY, null, null, member.getId(), 0, 5);
		model.put("listFriendDiary", listFriendDiary);
		//��ǰ�û����ѵĻ
			
		//����Ȧ��
		List<Commu> hotCommuList=commuService.getHotCommuList(0,6);
		model.put("hotCommuList", hotCommuList);
		Map params=new HashMap();
		params.put("commuid",commuid);
		params.put("commutopicid", commutopicid);
		pageUtil.initPageInfo(params);
		model.put("listCommuDiary", listCommuDiary);
		model.putAll(getCommuCommonData(model, commu, member));
		model.put("pageUtil", pageUtil);
		model.put("commuTopicList", commuTopicList);
		return "home/community/commentList.vm";
	}

	
	/**
	 * ��ȡȦ�ӳ�Ա��Ϣ
	 */
	@RequestMapping("/quan/getCommuMemberList.xhtml")
	public String getCommumemberInfo(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model, Long commuid, Integer pageNo) {
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null || !commu.hasStatus(Status.Y)){
			return show404(model, "�������Ȧ���Ѿ���ɾ����");
		}
		if (pageNo == null) pageNo = 0;
		Integer rowsPerPage = 21;
		Integer firstPerPage = pageNo * rowsPerPage;
		List<Commu> listCommuMemberLoveCommu=commuService.getCommuMemberLoveToCommuList(commuid,0,6);
		List<CommuMember> listCommuMember = commuService.getCommuMemberById(commuid, null, null, "", 
				firstPerPage, rowsPerPage);
		addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(listCommuMember));
		Integer countcommumember = commuService.getCommumemberCount(commuid, null);
		PageUtil pageUtil = new PageUtil(countcommumember, rowsPerPage, pageNo, "quan/getCommuMemberList.xhtml", true, true);
		Map params=new HashMap();
		params.put("commuid", commuid);
		pageUtil.initPageInfo(params);
		//�������
		model.put("listCommuMemberLoveCommu", listCommuMemberLoveCommu);
		model.put("listCommuMember", listCommuMember);
		model.put("pageUtil", pageUtil);
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		model.putAll(getCommuCommonData(model, commu, member));
		return "home/community/commuMemberList.vm";
	}
	/**
	 * ��ȡȦ�ӳ�Ա��Ϣ,���й���
	 */
	@RequestMapping("/home/commu/manageCommuMemberList.xhtml")
	public String manageCommumemberInfo(ModelMap model, Long commuid, Integer pageNo) {
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null || !commu.hasStatus(Status.Y)){
			return show404(model, "�������Ȧ���Ѿ���ɾ����");
		}
		Member member = getLogonMember();
		if(!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())) return showError(model, "ֻ�й���Ա����Ȩ�޲���!");
		if (pageNo == null) pageNo = 0;
		Integer rowsPerPage = 20;
		Integer firstPerPage = pageNo * rowsPerPage;
		//����Ա
		Map<Long, Member> adminMemberMap=new HashMap<Long, Member>();
		Map<Long, VisitCommuRecord> adminVCRMap=new HashMap<Long, VisitCommuRecord>();
		Map<Long ,CommuMember> adminCommuMemberMap=new HashMap<Long, CommuMember>();
		List<Long> adminIdList=new ArrayList<Long>();
		adminIdList.add(commu.getAdminid());
		if(commu.getSubadminid()!=0) adminIdList.add(commu.getSubadminid());
		for(Long adminid: adminIdList){
			Member adminMember = daoService.getObject(Member.class, adminid);
			VisitCommuRecord adminvcr = commuService.getVisitCommuRecordByCommuidAndMemberid(commuid, adminid);
			adminMemberMap.put(adminid, adminMember);
			adminVCRMap.put(adminid, adminvcr);
			adminCommuMemberMap.put(adminid, daoService.getObject(CommuMember.class, adminid));
		}
		model.put("adminIdList", adminIdList);
		model.put("adminMemberMap", adminMemberMap);
		model.put("adminVCRMap", adminVCRMap);
		model.put("adminCommuMemberMap", adminCommuMemberMap);
		Map<Long, Member> commuMemberMap = new HashMap<Long, Member>();//�洢Ȧ�ӳ�Ա��Ϣ
		Map<Long, VisitCommuRecord> visitCommuRecordMap = new HashMap<Long, VisitCommuRecord>();
		Integer countCommumember = commuService.getCommumemberCount(commuid, commu.getAdminid());
		List<CommuMember> commonCommuMemberList = commuService.getCommuMemberById(commuid, commu.getAdminid(), commu.getSubadminid(), "",
				firstPerPage, rowsPerPage);
		for (CommuMember cb : commonCommuMemberList){
			commuMemberMap.put(cb.getId(), daoService.getObject(Member.class, cb.getMemberid()));
			VisitCommuRecord visitMemberRecord=commuService.getVisitCommuRecordByCommuidAndMemberid(commuid, cb.getMemberid());
			visitCommuRecordMap.put(cb.getId(), visitMemberRecord);
		}
		//δ����׼����Ȧ�ӵ��û�
		List<SysMessageAction> sysMessageActionList = userMessageService.getSysMessageActionListByActionidAndActionAndStatus(commuid,
				SysAction.ACTION_APPLY_COMMU_JOIN, SysAction.STATUS_APPLY, 0, 500);
		List<Long> upApporeMemberidList = BeanUtil.getBeanPropertyList(sysMessageActionList, Long.class, "frommemberid", true);
		addCacheMember(model, upApporeMemberidList);
		//Ȧ�Ӻ�����
		Map<Long, Member> blackMemberMap=new HashMap<Long, Member>();
		Map<Long, VisitCommuRecord> bVisitCommuRecordMap = new HashMap<Long, VisitCommuRecord>();
		List<CommuMember> commuBlackMemberList = commuService.getCommuMemberById(commuid, null, null, Status.Y, 0, 1000);
		for(CommuMember blackMembers :commuBlackMemberList){
			Member blackMember=daoService.getObject(Member.class, blackMembers.getMemberid());
			blackMemberMap.put(blackMembers.getId(), blackMember);
			VisitCommuRecord bVisitMemberRecord=commuService.getVisitCommuRecordByCommuidAndMemberid(commuid, blackMembers.getMemberid());
			bVisitCommuRecordMap.put(blackMembers.getId(), bVisitMemberRecord);
		}
		PageUtil pageUtil = new PageUtil(countCommumember, rowsPerPage, pageNo,"home/commu/manageCommuMemberList.xhtml", true, true);
		model.put("commuBlackMemberList", commuBlackMemberList);
		model.put("blackMemberMap", blackMemberMap);
		model.put("bVisitCommuRecordMap", bVisitCommuRecordMap);
		Map params=new HashMap();
		params.put("commuid", commuid);
		pageUtil.initPageInfo(params);
		model.put("visitCommuRecordMap", visitCommuRecordMap);
		model.put("commonCommuMemberList", commonCommuMemberList);
		model.put("pageUtil", pageUtil);
		model.put("commuMemberMap", commuMemberMap);
		model.put("sysMessageActionList", sysMessageActionList);
		if(pageNo>0) model.put("commumember", "member");
		model.putAll(getCommuCommonData(model, commu, member));
		return "home/community/manage/memberManage.vm";
	}
	
	@RequestMapping("/quan/albumList.xhtml")
	public String albumList(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, HttpServletRequest request, ModelMap model, Long commuid, Integer pageNo) {
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null || !commu.hasStatus(Status.Y)){
			return show404(model, "�������Ȧ���Ѿ���ɾ����");
		}
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(pageNo==null) pageNo=0;
		int rowsPerPage=12;
		int start = pageNo * rowsPerPage;
		int count=0;
		List<Album> albumList=commuService.getCommuAlbumById(commuid, start, rowsPerPage);
		Map<Long,Integer> imageNum = new HashMap<Long, Integer>();
		for(Album album:albumList){
			Integer num = albumService.getPictureountByAlbumId(album.getId());
			imageNum.put(album.getId(), num);
		}
		count=commuService.getCommuAlbumCountById(commuid);
		PageUtil pageUtil=new PageUtil(count,rowsPerPage,pageNo,"quan/albumList.xhtml", true, true);
		Map params = new HashMap(); 
		params.put("commuid", commuid);
		pageUtil.initPageInfo(params);
		model.put("imageNum",imageNum);
		model.put("pageUtil",pageUtil);
		model.put("albumList", albumList);
		model.put("member",member);
		model.putAll(getCommuCommonData(model, commu, member));
		return "home/community/albumList.vm";
	}
	/**
	 * Ȧ�����Ȩ�޿���
	 * @param model
	 * @param albumid
	 * @param pageNo
	 * @return
	 */
	private String commuShowController(Member member,Album album,Commu commu,ModelMap model){
		if(member!=null){
			if(member.getId().equals(album.getMemberid()) || commu.getAdminid().equals(member.getId()) || commu.getSubadminid().equals(member.getId())){
				model.put("isShowCommuAlbum", true);
			}else{
				model.put("isShowCommuAlbum", false);
			}
		}else{
			model.put("isShowCommuAlbum", false);
		}
		return null;
	}
	
	@RequestMapping("/quan/commu/albumImageList.xhtml")
	public String albumImageList(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model, Long albumid, Integer pageNo) {
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		Album album = daoService.getObject(Album.class, albumid);
		if(album == null) return show404(model, "����������Ϣ��");
		Commu commu = daoService.getObject(Commu.class, album.getCommuid());
		if(commu == null) return show404(model, "����ʵ���������");
		if(!commu.hasStatus(Status.Y)) return show404(model, "������Ȧ���ѱ�ɾ�ˣ�");
		model.putAll(getCommuCommonData(model, commu, member));
		if(pageNo==null) pageNo=0;
		int rowsPerPage=20;
		int start = pageNo * rowsPerPage;
		int count=0;
		commuShowController(member, album, commu, model);
		List<Picture> albumImageList = albumService.getPictureByAlbumId(albumid, start, rowsPerPage);
		count = albumService.getPictureountByAlbumId(albumid);
		PageUtil pageUtil = new PageUtil(count,rowsPerPage,pageNo,"quan/commu/albumImageList.xhtml", true, true);
		Member albumMember = daoService.getObject(Member.class, album.getMemberid());
		Map params = new HashMap(); 
		params.put("albumid", albumid);
		params.put("commuid", album.getCommuid());
		pageUtil.initPageInfo(params);
		if(member!=null){
			if (album.getMemberid().equals(member.getId())) model.put("ismycommu",true);
			model.put("mymember",member);
		}
		model.put("pageUtil",pageUtil);
		model.put("albumid", albumid);
		model.put("albumImageList",albumImageList);
		model.put("albumMember",albumMember);
		model.put("album",album);
		model.put("commuid", album.getCommuid());
		return "home/community/albumImageList.vm";
	}
	@RequestMapping("/home/commu/deleteCommuMember.xhtml")
	public String deleteCommuMember(ModelMap model,Long commuid,Long memberid){
		Member member=getLogonMember();
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null || !commu.hasStatus(Status.Y)) return show404(model, "��Ȧ���Ѿ���ɾ����");
		Map map = getCommuCommonData(model, commu, member);
		Member admin = (Member)map.get("adminMember");
		if((member.getId()+"").equals(admin.getId()+"")){//�жϵ�ǰ�û��Ƿ������Ȧ�ӵĹ���Ա
			friendService.deleteCommueMember(memberid, commuid);
			//����ϵͳ��Ϣ
			String title = "����Ա"+member.getNickname()+"�����Ȧ��"+commu.getName()+"���߳���";
			SysMessageAction sysmessage=new SysMessageAction(SysAction.STATUS_RESULT);
			sysmessage.setFrommemberid(member.getId());
			sysmessage.setBody(title);
			sysmessage.setTomemberid(memberid);
			daoService.saveObject(sysmessage);
		}
		model.put("commuid", commuid);
		return showRedirect("/quan/getCommuMemberList.xhtml", model);
	}
	// ����ͼ ɾ��
	@RequestMapping("/home/commu/delPic.xhtml")
	public String commuDelPic(Long commuid, ModelMap model){
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null || !commu.hasStatus(Status.Y)){
			return showJsonError(model, "�������Ȧ���Ѿ���ɾ����");
		}
		boolean issuc = false;
		try {
			issuc = gewaPicService.removePicture(commu.getCommubg());
			commu.setCommubg(null);
			daoService.saveObject(commu);
		} catch (IOException e) {
			dbLogger.error("", e);
			return showJsonError_DATAERROR(model);
		}
		if(issuc){
			return showJsonSuccess(model);
		}else{
			return showJsonError_DATAERROR(model);
		}
	}
	
	@RequestMapping("/home/commu/manageLoading.xhtml")
	public String manageLoading(Long commuid, String mtag, ModelMap model){
		String str = null;
		if("logo".equals(mtag)){
			str = "home/community/manage/logo.vm";
		}else if("layout".equals(mtag)) {
			Map<String, String> layoutMap = jsonDataService.getJsonData(JsonDataKey.KEY_COMMULAYOUT + commuid);
			model.put("layoutMap", layoutMap);
			str = "home/community/manage/layout.vm";
		}else if("color".equals(mtag)){
			Map<String, String> colorMap = jsonDataService.getJsonData(JsonDataKey.KEY_COMMUCOLOR + commuid);
			model.put("colorMap", colorMap);
			str = "home/community/manage/color.vm";
		}else if("bgpic".equals(mtag)) {
			// ��ǰȦ�ӵı��� Ψһ
			Commu commu = daoService.getObject(Commu.class, commuid);
			String commubgpic = commu.getCommubg();
			if(StringUtils.isNotBlank(commubgpic)){
				model.put("commubgpic", commubgpic);
			}
			str = "home/community/manage/bgpic.vm";
		}
		Commu commu = daoService.getObject(Commu.class, commuid);
		model.put("commu", commu);
		return str;
	}
	@RequestMapping("/home/commu/saveColor.xhtml")
	public String saveColor(Long commuid, String colors, ModelMap model){
		Map<String, String> dataMap = WebUtils.parseQueryStr(colors, "UTF-8");
		Member member=getLogonMember();
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(!commu.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
		if(!member.getId().equals(commu.getAdminid())) return showJsonError(model, "��û�����Ȩ��");
		jsonDataService.saveJsonData(JsonDataKey.KEY_COMMUCOLOR + commuid, JsonDataKey.KEY_COMMUCOLOR, dataMap);
		return showJsonSuccess(model);
	}
	@RequestMapping("/home/commu/saveLayout.xhtml")
	public String saveLayout(Long commuid, String layouts, ModelMap model){
		Map<String, String> dataMap = WebUtils.parseQueryStr(layouts, "UTF-8");
		String template = dataMap.get("diarytemplate");
		if(template == null) return showJsonError(model, "��ѡ���Ⲽ�ַ�ʽ!");
		try{
			int layTemplate =Integer.valueOf(template+"");
			int activity = 0;
			int vote = 0;
			int album = 0;
			int diary = 0;
			if(layTemplate ==1){
				activity = Integer.valueOf(dataMap.get("activity")+"");
				vote = Integer.valueOf(dataMap.get("vote")+"");
				album = Integer.valueOf(dataMap.get("album")+"");
				diary = Integer.valueOf(dataMap.get("diary")+"");
			}else{
				activity = Integer.valueOf(dataMap.get("activitys")+"");
				vote = Integer.valueOf(dataMap.get("votes")+"");
				album = Integer.valueOf(dataMap.get("albums")+"");
			}
			if(activity>20||activity<0) return showJsonError(model, "�����ֻ����0-20��֮�䣡");
			if(vote>10||vote<0) return showJsonError(model, "ͶƱ����ֻ����0-10��֮�䣡");
			if(album>16||album<0) return showJsonError(model, "���ͼƬ����ֻ����0-16��֮�䣡");
			if(diary>30||diary<0) return showJsonError(model, "���ͼƬ����ֻ����0-30��֮�䣡");
		}catch(NumberFormatException e){
			return showJsonError(model, "ֻ��������������");
		}
		Member member=getLogonMember();
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(!commu.hasStatus(Status.Y)) return showJsonError(model, "������Ȧ���Ѿ���ɾ����");
		if(!member.getId().equals(commu.getAdminid())) return showJsonError(model, "��û�����Ȩ��");
		jsonDataService.saveJsonData(JsonDataKey.KEY_COMMULAYOUT + commuid, JsonDataKey.KEY_COMMULAYOUT, dataMap);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/home/commu/applyAddCommuInfo.xhtml")
	public String addCommuInfo(ModelMap model, Long commuid){
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null) return show404(model, "����ʵ�Ȧ����ɾ������û�з���Ȩ�޷��ʣ�");
		if(!commu.hasStatus(Status.Y)) return show404(model, "��Ȧ���Ѿ���ɾ����");
		model.put("commuid", commuid);
		model.putAll(getCommuCommonData(model, commu, getLogonMember()));
		return "home/community/applyAddCommuInfo.vm";
	}
	
	@RequestMapping("/home/commu/isCommuBlack.xhtml")
	public String isCommuBlack(Long commuid,ModelMap model){
		Member member = getLogonMember();
		CommuMember commuMember=commuService.getCommuMemberByMemberidAndCommuid(member.getId(), commuid);
		if(commuMember == null) return showJsonError(model, "�㲻��Ȧ�ӳ�Ա�����ܽ��д˲�����");
		if(CommuMember.FLAG_BLACK.equals(commuMember.getFlag())){
			return showJsonError(model,"�㱻����С���ݣ���ʱ�������˲�����");
		}
		return showJsonSuccess(model);
	}
	//Ȧ���б�
	@RequestMapping("/quan/index.xhtml")
	public String commuIndex(ModelMap model, Integer pageNo, String tag, Long relatedid, String keyword, 
			String type, String countycode, HttpServletRequest request, HttpServletResponse response){
		if(pageNo==null) pageNo = 0;
		Integer rowsPerPage = 10;
		Integer firstRows = rowsPerPage*pageNo;
		Integer commuCount = 0;
		String stag= tag;
		String scountycode= countycode;
		Map<Long, Integer> activityCountMap = new HashMap<Long, Integer>();
		Map<Long, County> countyMap = new HashMap<Long, County>();
		Map<Long, Indexarea> indexareaMap = new HashMap<Long, Indexarea>();
		//Ȧ���Ƽ�
		List<Commu> commuList = new ArrayList<Commu>();
		String citycode = WebUtils.getAndSetDefault(request, response);
		if(StringUtils.isBlank(tag) && StringUtils.isBlank(keyword) && StringUtils.isBlank(type) && StringUtils.isBlank(countycode)){//Ȧ���Ƽ�����
			List<GewaCommend> gewaCommendCommuList = commonService.getGewaCommendList(citycode, SignName.COMMU_INDEX, null, "commu", true, firstRows, rowsPerPage);
			commuCount = commonService.getGewaCommendCount(citycode, SignName.COMMU_INDEX, null, "commu", true);
			for(GewaCommend gcc: gewaCommendCommuList){
				Commu commu = daoService.getObject(Commu.class, gcc.getRelatedid());
				if(commu ==null || !commu.hasStatus(Status.Y)) continue;
				commuList.add(commu);
				countyMap.put(commu.getId(), daoService.getObject(County.class, commu.getCountycode()));
				indexareaMap.put(commu.getId(), daoService.getObject(Indexarea.class, commu.getIndexareacode()));
			}
		}else {//��������
			if("all".equals(countycode)) countycode="";
			if("all".equals(tag))tag = "";
			commuList = commuService.getCommuBySearch(tag, citycode, relatedid, keyword, "", countycode, firstRows, rowsPerPage);
			commuCount = commuService.getCommuCountBySearch(tag, citycode, relatedid, keyword, "", countycode);
			for(Commu commu: commuList){
				countyMap.put(commu.getId(), daoService.getObject(County.class, commu.getCountycode()));
				indexareaMap.put(commu.getId(), daoService.getObject(Indexarea.class, commu.getIndexareacode()));
			}
		}
		PageUtil pageUtil = new PageUtil(commuCount,rowsPerPage,pageNo,"quan/index.xhtml", true, true);
		model.put("activityCountMap", activityCountMap);
		model.put("countyMap", countyMap);
		model.put("indexareaMap", indexareaMap);
		RelatedHelper rh = new RelatedHelper(); 
		model.put("relatedHelper", rh);
		Map<Serializable, String> relatedMap = BeanUtil.getKeyValuePairMap(commuList,"relatedid", "tag");
		Map<Serializable, String> categoryMap = BeanUtil.getKeyValuePairMap(commuList, "smallcategoryid", "smallcategory");
		relateService.addRelatedObject(1,"relatedMap",rh,relatedMap);
		relateService.addRelatedObject(1,"categoryMap",rh,categoryMap);
		//����Ȧ�Ӵ������Ϣ
		List<Map> tagList = commuService.getCommuType();
		model.put("tagList", tagList);
		List<County> countyList = placeService.getCountyByCityCode(citycode);
		Map<String, Integer> countyCountMap=new HashMap<String, Integer>();
		for(County county: countyList){
			Integer countyCount = getCommuCountByCountycode(county.getCountycode(), tag, relatedid);
			countyCountMap.put(county.getCountycode(), countyCount);
		}
		model.put("countyListNum", commuService.getCommuCountBySearch(tag, citycode, relatedid, null, "", null));
		model.put("countyList", countyList);
		model.put("countyCountMap", countyCountMap);
		//����Ȧ��
		List<Commu> newCommuList = commuService.getCommuList(0, 9);
		model.put("newCommuList", newCommuList);
		//���Ż���
		List<Diary> hotDiaryList = diaryService.getHotCommuDiary(Diary.class, citycode, true,DiaryConstant.DIARY_TYPE_TOPIC_DIARY,0,10);
		model.put("hotDiaryList", hotDiaryList);
		Map params = new HashMap();
		params.put("tag", stag);
		params.put("type", type);
		params.put("keyword", keyword);
		params.put("relatedid", relatedid);
		params.put("countycode", scountycode);
		model.put("pageUtil", pageUtil);
		pageUtil.initPageInfo(params);
		model.put("count", commuCount);
		model.put("commuList", commuList);
		return "home/community/searchCommunity.vm";
	}
	
	public Integer getCommuCountByCountycode(String countycode, String tag, Long relatedid){
		DetachedCriteria query = DetachedCriteria.forClass(Commu.class);
		query.add(Restrictions.eq("countycode", countycode));
		query.add(Restrictions.like("status", Status.Y, MatchMode.START));
		if("qita".equals(tag)) query.add(Restrictions.isNull("tag"));
		if(StringUtils.isNotBlank(tag) && !"qita".equals(tag)) query.add(Restrictions.eq("tag", tag));
		if(relatedid!=null && StringUtils.equals(tag, "cinema")){
			query.add(Restrictions.eq("smallcategory", "movie"));
			query.add(Restrictions.eq("smallcategoryid", relatedid));
		}else if(relatedid!=null && StringUtils.equals(tag, "gym")){
			query.add(Restrictions.eq("smallcategory", "gymcourse"));
			query.add(Restrictions.eq("smallcategoryid", relatedid));
		}else if(relatedid!=null && StringUtils.equals(tag, "sport")){
			query.add(Restrictions.eq("smallcategory", "sportservice"));
			query.add(Restrictions.eq("smallcategoryid", relatedid));
		}
		query.setProjection(Projections.rowCount());
		List<Commu> commuList=readOnlyTemplate.findByCriteria(query);
		if(commuList.isEmpty()) return 0;
		return new Integer(commuList.get(0)+"");
	}
	
	/**
	 * Ȧ�����ͼƬ��ϸ
	 */
	@RequestMapping("/quan/commu/imageDetailList.xhtml")
	public String imageDetailList(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model,Long albumid,Long curAlbumPicId){
		Album album = daoService.getObject(Album.class, albumid);
		if(album == null) return show404(model, "��᲻���ڣ�");
		Commu commu = daoService.getObject(Commu.class, album.getCommuid());
		if(commu == null) return showError(model, "��������");
		if(!commu.hasStatus(Status.Y)){
			return show404(model, "��Ȧ���ѱ�ɾ����");
		}
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		Map<Long,String[]> albumImageMap = new LinkedHashMap<Long, String[]>();
		List<Picture> albumImageList = albumService.getPictureByAlbumId(albumid, 0, 500);
		for (Picture ai : albumImageList) {
			String[] albumimages = new String[3];
			albumimages[0] = ai.getLogo();
			albumimages[1] = ai.getDescription()==null?"��һ������ʲô��û���£�":ai.getDescription();
			albumimages[2] = ai.getName()==null?"��һ������ʲô��û���£�":ai.getName();
			albumImageMap.put(ai.getId(), albumimages);
		}
		model.put("albumImageMap", albumImageMap);
		model.put("curAlbumImage", daoService.getObject(Picture.class, curAlbumPicId));
		model.put("albumid", albumid);
		model.put("curAlbum", album);
		model.putAll(getCommuCommonData(model, commu, member));
		model.put("member", member);
		List<AlbumComment> imageCommentList = albumService.getPictureComment(curAlbumPicId, 0,30);
		if(imageCommentList.size()>0){
			Map<Long, Member> memberMap = new HashMap<Long, Member>();
			List<Long> commentMemberIdList = BeanUtil.getBeanPropertyList(imageCommentList, Long.class, "memberid",true);
			List<Member> memberList = daoService.getObjectList(Member.class, commentMemberIdList);
			memberMap = BeanUtil.beanListToMap(memberList, "id");
			model.put("memberMap",memberMap);
		}
		if(member != null){
			commuShowController(member, album, commu, model);
		}
		model.put("curAlbumMember", daoService.getObject(Member.class, album.getMemberid()));
		model.put("imageCommentList", imageCommentList);
		addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(imageCommentList));
		model.put("logonMember", member);
		return "home/community/commuImageDetail.vm";
	}
	/**
	 * ����������
	 */
	@RequestMapping("/home/commu/setAlbumCover.xhtml")
	public String setAlbumCover(Long albumId,String imageUrl,ModelMap model){
		Member member = getLogonMember();
		Album album = daoService.getObject(Album.class, albumId);
		if(album == null) return showError(model, "��᲻���ڣ�");
		Commu commu = daoService.getObject(Commu.class, album.getCommuid());
		if(commu == null) return showJsonError(model, "��������");
		if(!commu.hasStatus(Status.Y)) return showJsonError(model, "������Ȧ���Ѿ���ɾ����");
		if(member.getId().equals(album.getMemberid()) || member.getId().equals(commu.getAdminid())
				|| member.getId().equals(commu.getSubadminid())){
			album.setLogo(imageUrl);
			daoService.updateObject(album);
			return showJsonSuccess(model);
		}else{
			return showJsonError(model, "����Ȩ�����˲�����");
		}
	
	}
	
	/**
	 * ����Ȧ�ӱ���
	 */
	@RequestMapping("/home/commu/updatecommubgpic.xhtml")
	public String updatecommubgpic(Long commuid, String picpath, ModelMap model){
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu != null){
			if(!commu.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
			commu.setCommubg(picpath);
			commu.setUpdatetime(DateUtil.getCurFullTimestamp());
			daoService.saveObject(commu);
			return showJsonSuccess(model);
		}
		return showJsonError_DATAERROR(model);
	}
}
