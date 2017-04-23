package com.gewara.web.action.community;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.Config;
import com.gewara.constant.ExpGrade;
import com.gewara.constant.Status;
import com.gewara.constant.SysAction;
import com.gewara.constant.TagConstant;
import com.gewara.json.MemberStats;
import com.gewara.model.bbs.Diary;
import com.gewara.model.bbs.DiaryBase;
import com.gewara.model.bbs.commu.Commu;
import com.gewara.model.bbs.commu.CommuCard;
import com.gewara.model.bbs.commu.CommuManage;
import com.gewara.model.bbs.commu.CommuMember;
import com.gewara.model.bbs.commu.CommuTopic;
import com.gewara.model.bbs.commu.VisitCommuRecord;
import com.gewara.model.common.BaseInfo;
import com.gewara.model.common.County;
import com.gewara.model.content.Notice;
import com.gewara.model.content.Picture;
import com.gewara.model.user.Album;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.model.user.SysMessageAction;
import com.gewara.model.user.Treasure;
import com.gewara.model.user.UserMessage;
import com.gewara.model.user.UserMessageAction;
import com.gewara.service.OperationService;
import com.gewara.service.PlaceService;
import com.gewara.service.bbs.AlbumService;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.bbs.CommuService;
import com.gewara.service.bbs.DiaryService;
import com.gewara.service.bbs.UserMessageService;
import com.gewara.service.content.NoticeService;
import com.gewara.service.member.FriendService;
import com.gewara.support.ErrorCode;
import com.gewara.support.ReadOnlyTemplate;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.GewaPicService;
import com.gewara.untrans.SearchService;
import com.gewara.untrans.ShareService;
import com.gewara.untrans.WalaApiService;
import com.gewara.untrans.monitor.MonitorService;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.util.BeanUtil;
import com.gewara.util.BindUtils;
import com.gewara.util.ChangeEntry;
import com.gewara.util.JsonUtils;
import com.gewara.util.PictureUtil;
import com.gewara.util.StringUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.util.XSSFilter;
import com.gewara.web.action.BaseHomeController;
import com.gewara.web.util.PageUtil;
import com.gewara.xmlbind.bbs.Comment;

@Controller
public class AllCommuController extends BaseHomeController{
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
	@Autowired@Qualifier("walaApiService")
	private WalaApiService walaApiService;
	@Autowired@Qualifier("diaryService")
	private DiaryService diaryService;
	public void setDiaryService(DiaryService diaryService) {
		this.diaryService = diaryService;
	}
	@Autowired@Qualifier("placeService")
	private PlaceService placeService;
	public void setPlaceService(PlaceService placeService) {
		this.placeService = placeService;
	}
	@Autowired@Qualifier("commentService")
	private CommentService commentService;
	@Autowired@Qualifier("commuService")
	private CommuService commuService;
	public void setCommuService(CommuService commuService) {
		this.commuService = commuService;
	}
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	@Autowired@Qualifier("noticeService")
	private NoticeService noticeService;
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
	
	@Autowired@Qualifier("gewaPicService")
	private GewaPicService gewaPicService;
	public void setGewaPicService(GewaPicService gewaPicService) {
		this.gewaPicService = gewaPicService;
	}
	@Autowired@Qualifier("albumService")
	private AlbumService albumService;
	public void setAlbumService(AlbumService albumService) {
		this.albumService = albumService;
	}
	@Autowired@Qualifier("friendService")
	private FriendService friendService;
	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}
	@Autowired@Qualifier("userMessageService")
	private UserMessageService userMessageService;
	public void setUserMessageService(UserMessageService userMessageService) {
		this.userMessageService = userMessageService;
	}
	
	@Autowired@Qualifier("searchService")
	private SearchService searchService;
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
	@Autowired@Qualifier("blogService")
	private BlogService blogService;
	public void setBlogService(BlogService blogService) {
		this.blogService = blogService;
	}
	
	@Autowired@Qualifier("monitorService")
	private MonitorService monitorService;
	public void setMonitorService(MonitorService monitorService) {
		this.monitorService = monitorService;
	}
	
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	/***
	 *  ��������
	 */
	public String commonData(ModelMap model, Long commuid, Member member){
		return commonData(model, commuid, true, member);
	}
	public String commonData(ModelMap model, Long commuid, boolean isshowMember, Member member){
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null)return show404(model, "���ݴ���!");
		if(!commu.hasStatus(Status.Y)) return show404(model, "����ʵ�Ȧ�ӣ��Ѵ�ϵͳ��ɾ�����޷����ʣ�");
		boolean b = commuService.isCommuMember(commuid, member.getId());
		if(!b) return showError(model, "����Ȩ���˲�����");
		if(!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())) return showError(model, "�㲻�ǹ���Ա����Ȩ���˲���!");
		model.put("commu", commu);
		if(isshowMember){
			model.putAll(controllerService.getCommonData(model, member, member.getId()));
		}
		// ��鵱ǰȦ�ӵ�״̬
		String checkstatus = commuService.getCheckStatusByIDAndMemID(commuid);
		model.put("checkstatus", checkstatus);
		model.put("logonMember", member);
		return null;
	}
	
	/**
	 * ��ǰ�û������������Ȧ�ӵĻ�����Ϣ
	 * @param model
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/home/commu/allCommuDiaryList.xhtml")
	public String allCommuInfo(ModelMap model,Integer pageNo){
		Member member = getLogonMember();
		if(pageNo==null) pageNo=0;
		int rowsPerPage=20;
		int count=0;
		List<Diary> listCommuDiary =commuService.getAllCommuDiaryById(member.getId(), pageNo*rowsPerPage, rowsPerPage);
		Map<Long/*commuid*/, Commu> mapCommuDiary = daoService.getObjectMap(Commu.class, BeanUtil.getBeanPropertyList(listCommuDiary, Long.class, "communityid", true));
		count=commuService.getAllCommuDiaryCountById(member.getId());
		PageUtil pageUtil=new PageUtil(count,rowsPerPage,pageNo,"/home/commu/allCommuDiaryList.xhtml", true, true);
		pageUtil.initPageInfo();
		model.put("pageUtil",pageUtil);
		model.put("listCommuDiary", listCommuDiary);
		model.put("mapCommuDiary", mapCommuDiary);
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		return "home/community/allCommuDiaryList.vm";
	}
	
	
	@RequestMapping("/home/commu/allCommuAlbumList.xhtml")
	public String allCommuAlbumList(ModelMap model,Integer pageNo){
		Member member = getLogonMember();
		if(member == null) return showError(model, "����û��¼���뷵�ص�¼��");
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		model.put("memberInfo",memberInfo);
		if(pageNo==null) pageNo=0;
		int rowsPerPage=12;
		int start = pageNo * rowsPerPage;
		int count=0;
		List<Album> albumList=commuService.getJoinedCommuAlbumList(member.getId(), start, rowsPerPage);
		Map<Long,Integer> imageNum = new HashMap<Long, Integer>();
		for(Album album:albumList){
			Integer num = albumService.getPictureountByAlbumId(album.getId());
			imageNum.put(album.getId(), num);
		}
		Map<Long/*commuid*/,Commu> commuList = daoService.getObjectMap(Commu.class, BeanUtil.getBeanPropertyList(albumList, Long.class, "commuid", true));
		count=commuService.getJoinedCommuAlbumCount(member.getId());
		PageUtil pageUtil=new PageUtil(count,rowsPerPage,pageNo,"/home/commu/allCommuAlbumList.xhtml", true, true);
		pageUtil.initPageInfo();
		model.put("albumCommuList",commuList);
		model.put("imageNum",imageNum);
		model.put("pageUtil",pageUtil);
		model.put("albumList", albumList);
		return "home/community/allCommuAlbumList.vm";
	}
	
	/**
	 * �����Ȧ����Ϣ
	 * @param model
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/home/commu/commuList.xhtml")
	public String joinCommu(ModelMap model,Integer pageNo, Long memberid){
		Member mymember = getLogonMember();
		if(memberid==null){//�Լ�
			memberid = mymember.getId();
		}
		//else mymember = daoService.getObject(Member.class, memberid);
		//if(mymember == null) return showError(model, "���û������ڣ�");
		
		//�жϷ���Ȩ��(����������)
		if(memberid!=null && !memberid.equals(mymember.getId())){
			model.putAll(friendService.isPrivate(memberid));
		}
		model.putAll(controllerService.getCommonData(model, mymember, memberid));
		//Member member = daoService.getObject(Member.class, memberid);
		if(pageNo==null) pageNo=0;
		int rowsPerPage=10;
		int count=0;
		List<Commu> listCommu=new ArrayList<Commu>();
		listCommu=commuService.getCommuListByMemberId(memberid, pageNo*rowsPerPage, rowsPerPage);
		count=commuService.getCommuCountByMemberId(memberid);
		if(listCommu.size()==0){//û���롢����Ȧ�ӣ�����Ϊ�Ƽ�Ȧ��
			listCommu = commuService.getCommunityListByHotvalue(Commu.HOTVALUE_RECOMMEND, pageNo*rowsPerPage, rowsPerPage);
			count = commuService.getCommunityCountByHotvalue(Commu.HOTVALUE_RECOMMEND);
		}
		Map<Long, Integer> diaryCountMap = new HashMap<Long, Integer>();
		Map<Long, Integer> activityCountMap = new HashMap<Long, Integer>();
		Map<Long, Integer> albumCountMap = new HashMap<Long, Integer>();
		Map mapCommuCount=new HashMap();
		for(Commu commu:listCommu){
			//TODO: ʹ�þۺ�����
			diaryCountMap.put(commu.getId(), commuService.getCommuDiaryCount(Diary.class, commu.getId(), null, null));
			albumCountMap.put(commu.getId(), commuService.getCommuAlbumCountById(commu.getId()));
			mapCommuCount.put(commu.getId(), commuService.getCommumemberCount(commu.getId(), null));
		}
		model.put("diaryCountMap", diaryCountMap);
		model.put("activityCountMap", activityCountMap);
		model.put("albumCountMap", albumCountMap);
		PageUtil pageUtil=new PageUtil(count,rowsPerPage,pageNo,"/home/commu/commuList.xhtml", true, true);
		Map params = new HashMap(); 
		params.put("memberid", new String[]{memberid+""});
		pageUtil.initPageInfo(params);
		model.put("mapCommuCount", mapCommuCount);
		model.put("pageUtil", pageUtil);
		model.put("relateMap", relateActivityCommu(listCommu));
		model.put("listCommu", listCommu);
		return "home/community/commuList.vm";
	}
	@RequestMapping("/home/commu/friendCommuList.xhtml")
	public String friendCommuList(ModelMap model,Integer pageNo){
		if(pageNo == null) pageNo = 0;
		int maxnum = 10;
		Integer from = pageNo*maxnum;
		Member member = getLogonMember();
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		
		Map<Long, Integer> diaryCountMap = new HashMap<Long, Integer>();
		Map<Long, Integer> activityCountMap = new HashMap<Long, Integer>();
		Map<Long, Integer> albumCountMap = new HashMap<Long, Integer>();
		Map<Long, Map> memberMap = new HashMap<Long, Map>();
		Map mapCommuCount=new HashMap();
		Map <Long, Commu> friendCommuMap = commuService.getFriendCommuMap(member.getId(), from, maxnum);
		List<Commu> commuList = new ArrayList<Commu>(friendCommuMap.values());
		
		Integer count = commuService.getFriendCommuCount(member.getId());

		PageUtil pageUtil = new PageUtil(count, maxnum, pageNo,"home/commu/friendCommuList.xhtml", true, true); 
		for(Long friendid: friendCommuMap.keySet()){
			Commu commu = friendCommuMap.get(friendid);
			diaryCountMap.put(commu.getId(), commuService.getCommuDiaryCount(Diary.class, commu.getId(), null, null));
			albumCountMap.put(commu.getId(), commuService.getPictureCountByCommuid(commu.getId()));
			mapCommuCount.put(commu.getId(), commuService.getCommumemberCount(commu.getId(), null));
			memberMap.put(commu.getId(), memberService.getCacheMemberInfoMap(friendid));
		}
		model.put("pageUtil", pageUtil);
		model.put("memberMap", memberMap);
		model.put("mapCommuCount", mapCommuCount);
		model.put("diaryCountMap", diaryCountMap);
		model.put("activityCountMap", activityCountMap);
		model.put("albumCountMap", albumCountMap);
		model.put("commuList", commuList);
		model.put("logonMember", member);
		return "home/community/friendCommuList.vm";
	}
	/**
	 *  �������Ȧ��
	 * */
	private Map<Long, String> relateActivityCommu(List<Commu> listCommu){
		Map<Long, String> map = new HashMap<Long, String>();
		if(listCommu != null && listCommu.size() > 0){
			for(Commu commu : listCommu){
				Long cid = commu.getId();
				map.put(cid, this.commuService.getCheckStatusByIDAndMemID(cid));
			}
		}
		return map;
	}
	
	@RequestMapping("/home/commu/searchCommu.xhtml")
	public String searchCommu(ModelMap model, String tag, Long relatedid, String sort, 
			Integer pageNo, String keyword, String countycode, 
			HttpServletRequest request, HttpServletResponse response) {
		Member member=getLogonMember();
		if (pageNo == null)
			pageNo = 0;
		int rowsPerPage = 10;
		int firstPages=pageNo*rowsPerPage;
		int count = 0;// ����������
		// Ȧ����Ϣ
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		List<Commu> listCommu = new ArrayList<Commu>();
		List<Map> tagList = commuService.getCommuType();
		model.put("tagList", tagList);
		String citycode = WebUtils.getAndSetDefault(request, response);
		List<County> countyList = placeService.getCountyByCityCode(citycode);
		Map<String, Integer> countyCountMap=new HashMap<String, Integer>();
		for(County county: countyList){
			Integer countyCount = commuService.getCommuCountByCountycode(county.getCountycode());
			countyCountMap.put(county.getCountycode(), countyCount);
		}
		model.put("countyList", countyList);
		model.put("countyCountMap", countyCountMap);
		if("all".equals(countycode)) countycode="";
		listCommu=commuService.getCommuBySearch(tag, citycode, relatedid, keyword, sort, countycode, firstPages,rowsPerPage);
		count=commuService.getCommuCountBySearch(tag, citycode, relatedid, keyword, sort, countycode);
		//��ѯ�Ƿ��Ѿ���Ȧ�ӳ�Ա
		Map<Long,Boolean> mapIsCommuMember=new HashMap<Long, Boolean>();
		if(listCommu!=null){
			for (Commu c : listCommu) {
				mapIsCommuMember.put(c.getId(), commuService.isCommuMember(c.getId(), member.getId()));
			}
		}
		Map params = new HashMap();// �洢��ҳ����
		params.put("tag",tag);
		params.put("relatedid",relatedid);
		params.put("keyword",keyword);
		params.put("sort",sort);
		PageUtil pageUtil = new PageUtil(count, rowsPerPage, pageNo,
				"/home/commu/searchCommu.xhtml", true, true);
		pageUtil.initPageInfo(params);
		model.put("pageUtil", pageUtil);
		model.put("params", params);
		model.put("mapIsCommuMember", mapIsCommuMember);
		model.put("tagList", tagList);
		model.put("listCommu", listCommu);
		model.put("count", count);
		return "home/community/searchCommu.vm";
	}
	
	/**
	 * ���Ȧ��
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/home/commu/saveCommu.xhtml")
	public String addCommu(HttpServletRequest request, Long commuid, String tag, Long relatedid, String category, Long categoryid,
			String captchaId, String captcha,ModelMap model,  HttpServletResponse response) {
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		Member member = getLogonMember();
		if(member == null) return showJsonError_NOT_LOGIN(model);
		if (blogService.isBlackMember(member.getId())) return showJsonError(model, "�������У�");
		if(!member.isBindMobile()) return showJsonError(model, "���Ȱ��ֻ���");
		String opkey = OperationService.TAG_ADDCONTENT + member.getId();
		boolean allow = operationService.isAllowOperation(opkey, 60);
		if(!allow) return showJsonError(model, "�������̫Ƶ����, Ъ���ٷ��ɣ�");
		Integer eggs = blogService.isBadEgg(member);
		if (eggs != 777) {
			return showJsonError(model, eggs+"");
		}
		Map map = request.getParameterMap();
		Commu c =null;
		ChangeEntry changeEntry = null;
		if(commuid!=null){
			c=daoService.getObject(Commu.class, commuid);
			if(c==null)
				return showError(model, "�������Ȧ�Ӳ����ڻ��ѱ�ɾ����");
			if(!c.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
			changeEntry = new ChangeEntry(c);
		}else
			c = new Commu("");
		c.setAdminid(member.getId());
		BindUtils.bindData(c, map);
		c.setIp(WebUtils.getIpAndPort(WebUtils.getRemoteIp(request), request));
		c.setCitycode(WebUtils.getAndSetDefault(request, response));
		c.setTag(tag);
		c.setRelatedid(relatedid);
		c.setSmallcategory(category);
		c.setSmallcategoryid(categoryid);
		if(c.getName().length()>30) return showJsonError(model,"Ȧ�����Ƹ�ʽ���Ȳ��ܴ���30����!");
		if(WebUtils.checkPropertyAll(c)) return showJsonError(model, "���зǷ��ַ���");
		boolean b = commuService.isExistCommuName(commuid, c.getName());
		if(b) return showJsonError(model, "�Ѵ��ڴ�Ȧ�����ƣ������ظ���ӣ�");
		c = XSSFilter.filterObjAttrs(c, "name","info");
		String commubaseinfo = c.getName() + c.getInfo();
		String key = blogService.filterContentKey(commubaseinfo);
		boolean isNight = true;//blogService.isNight();
		if(StringUtils.isNotBlank(key)){
			c.setStatus(Status.N_DELETE);
			String etitle = "���˴�������Ȧ�ӣ�" + key;
			String content = "���˴�������Ȧ�ӣ��������˹ؼ���memberId = " + member.getId()+",[�û�IP:" + WebUtils.getRemoteIp(request) + "]" + c.getName() + "\n" + c.getInfo();
			monitorService.saveSysWarn(Commu.class, c.getId(), etitle, content, RoleTag.bbs);
		}else if(isNight){
			c.setStatus(Status.N_NIGHT);
		}
		
		// ����������ʾ�ַ�
		boolean existsInvalidSymbol = VmUtils.isExistsInvalidSymbol(commubaseinfo);
		if(existsInvalidSymbol){
			c.setStatus(Status.N_DELETE);
			String etitle = "���˴�������Ȧ�ӣ�" + "��⵽�������ַ���";
			String content = "���˴�������Ȧ�ӣ��������˹ؼ���memberId = " + member.getId()+",[�û�IP:" + WebUtils.getRemoteIp(request) + "]" + c.getName() + "\n" + c.getInfo();
			monitorService.saveSysWarn(Commu.class, c.getId(), etitle, content, RoleTag.bbs);
		}
		try {
			daoService.saveObject(c);
			if(commuid==null){
				memberService.addExpForMember(member.getId(), ExpGrade.EXP_COMMU_ADD);
				commuService.joinCommuMember(member.getId(),c.getId());
				CommuCard  cc = new CommuCard(member.getId());
				cc.setCommuid(commuid);
				daoService.addObject(cc);
			}
		} catch (Exception e) {
			dbLogger.error("", e);
			return showJsonError(model, "���Ȧ��ʧ�ܣ�");
		}
		// ����log
		if (changeEntry != null) {
			monitorService.saveChangeLog(member.getId(), Commu.class, c.getId(), changeEntry.getChangeMap(c));
		}
		searchService.pushSearchKey(c);//��������������������
		operationService.updateOperation(opkey, 40);
		/*if (isNight) {
			return showJsonError(model, "ҹ�䴴����Ȧ����Ҫͨ������Ա��˺������ʾ��");
		}*/
		return showJsonSuccess(model);
	}

	/**
	 * ���Ȧ����ת
	 */
	@RequestMapping("/home/commu/addCommu.xhtml")
	public String redirectAddCommu(String tag, Long relatedid, Long commuid, ModelMap model){
		Commu commu=null;
		if(commuid!=null){
			commu=daoService.getObject(Commu.class, commuid);
			if(commu==null)
				return show404(model, "�������Ȧ�Ӳ����ڻ��ѱ�ɾ����");
			if(!commu.hasStatus(Status.Y)) return show404(model, "��Ȧ���Ѿ���ɾ����");
		}else
			commu = new Commu("");
		Member member = getLogonMember();
		if(ServiceHelper.isTag(tag)){
			commu.setTag(tag);
			commu.setRelatedid(relatedid);
		}else if(ServiceHelper.isCategory(tag)){
			commu.setTag(ServiceHelper.getTag(tag));
			commu.setSmallcategory(tag);
			commu.setSmallcategoryid(relatedid);
		}
		if(commu.getRelatedid() != null){
			Object relate = relateService.getRelatedObject(commu.getTag(), commu.getRelatedid());
			model.put("relate", relate);
			String countycode=(String) BeanUtil.get(relate, "countycode");
			if(relate instanceof BaseInfo && StringUtils.isNotBlank(countycode)){
				model.put("indexareaList", placeService.getIndexareaByCountyCode(countycode));
				model.put("countycode", countycode);
				String indexareacode = (String) BeanUtil.get(relate, "indexareacode");
				model.put("indexareacode", indexareacode);
				List placeList = placeService.getPlaceListByTag(tag, countycode, indexareacode);
				model.put("placeList", placeList);
			}
		}
		if (commu.getSmallcategoryid()!= null) {
			Object relate2 = relateService.getRelatedObject(commu.getSmallcategory(),commu.getSmallcategoryid());
			model.put("relate2", relate2);
		}
		model.put("commu", commu);
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		model.put("config", config);
		return "home/community/addCommunity.vm";
	}
	
	@RequestMapping("/home/commu/applyAddCommu.xhtml")
	public String addCommu(Long commuid, HttpServletRequest request, ModelMap model) {
		Member member=this.getLogonMember();
		if(member == null) return showError_NOT_LOGIN(model);
		//��ѯȦ����Ϣ
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null || !commu.hasStatus(Status.Y)) return show404(model, "��Ȧ���Ѿ���ɾ����");
		//��ѯ�Ƿ��Ѿ���Ȧ�ӳ�Ա
		boolean isMember = commuService.isCommuMember(commuid, member.getId());
		if(isMember) return goBack(model, "���Ѿ���Ȧ�ӳ�Ա���벻Ҫ�ظ���ӣ�");
		
		if("public".equals(commu.getPublicflag())){//Ȧ����Ȩ��,ֱ�Ӽ���Ȧ��
			CommuMember commuMember = new CommuMember(member.getId());
			commuMember.setCommuid(commuid);
			commuMember.setFlag(CommuMember.FLAG_NORMAL);
			daoService.saveObject(commuMember);
			commu.addCommumembercount();
			daoService.updateObject(commu);
			
			Treasure treasure = new Treasure(member.getId(), TagConstant.TAG_COMMU, commuid, "collect");
			walaApiService.addTreasure(treasure);
			//�������
			//��������+1
			memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_COMMENTCOUNT, 1, true);
			//��������+1
			String memberlinkstr= "<a href=\"" + config.getBasePath() + "home/sns/othersPersonIndex.xhtml?memberid=" + member.getId() + "\" target=\"_blank\">"+member.getNickname()+"</a> ";
			String link = config.getBasePath() + "quan/" + commu.getId();
			String linkStr = memberlinkstr+" ������ <a href=\""+link+"\" target=\"_blank\">"+commu.getName()+"</a> Ȧ��";
			Map otherinfoMap = new HashMap();
			String info = "";
			if(StringUtils.length(commu.getInfo())>38){
				info = VmUtils.htmlabbr(commu.getInfo(), 38);
			}else{
				info = commu.getInfo();
			}
			otherinfoMap.put("info", info);
			otherinfoMap.put("commumembercount", commu.getCommumembercount());
			Integer CommuDiaryCount = commuService.getCommuDiaryCount(Diary.class, commuid,null,null);
			otherinfoMap.put("commuDiaryCount", CommuDiaryCount);
			String otherinfo = JsonUtils.writeObjectToJson(otherinfoMap);
			ErrorCode<Comment> ec = commentService.addMicroComment(member, TagConstant.TAG_COMMU_MEMBER, commu.getId(), linkStr, commu.getLogo(), null, null, false, null, otherinfo,null,null,WebUtils.getIpAndPort(WebUtils.getRemoteIp(request), request), null);
			if(ec.isSuccess()){
				shareService.sendShareInfo("wala",ec.getRetval().getId(), ec.getRetval().getMemberid(), null);
			}
		}else {
			model.put("commuid", commuid);
			return showRedirect("/home/commu/applyAddCommuInfo.xhtml", model);
		}
		model.put("commuid", commuid);
		return showRedirect("/quan/commuDetail.xhtml", model);
	}
	
	@RequestMapping("/home/commu/manage.xhtml")
	public String manage(Long commuid, ModelMap model){
		Member member=getLogonMember();
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null)return show404(model, "���ݴ���!");
		if(!commu.hasStatus(Status.Y)) return show404(model, "��Ȧ���Ѿ���ɾ����");
		if(!member.getId().equals(commu.getAdminid())) return showError(model, "��û�����Ȩ��!");
		model.put("isAdmin", member.getId().equals(commu.getAdminid()));
		model.put("commu", commu);
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		// ��鵱ǰȦ�ӵ�״̬
		String checkstatus = commuService.getCheckStatusByIDAndMemID(commuid);
		model.put("checkstatus", checkstatus);
		return "home/community/manage/index.vm";
	}
	
	/**
	 * �ϴ�Ȧ��LogoͼƬ
	 */
	@RequestMapping("/home/commu/uploadCommuLogo.xhtml")
	public String uploadHeadLogo(String paramchk, String successFile, ModelMap model) throws Exception{
		String mycheck = StringUtil.md5(successFile + config.getString("uploadKey"));
		if(!mycheck.equals(paramchk)) return forwardMessage(model, "У�����");
		Map jsonMap = new HashMap();
		jsonMap.put("filename", successFile);
		jsonMap.put("success", true);
		model.put("jsonMap", jsonMap);
		return "common/showUploadResult.vm";
	}
	@RequestMapping("/home/commu/updateCommuLogo.xhtml")
	public String updateCommuLogo(double imgW, double imgH, 
			double imgleft, double imgtop, String filename, Long commuid, ModelMap model) throws Exception{
		Commu commu =daoService.getObject(Commu.class, commuid);
		if(!commu.hasStatus(Status.Y)) return show404(model, "��Ȧ���Ѿ���ɾ����");
		Member member=getLogonMember();
		String realHeadpic = PictureUtil.getAlbumPicpath() + filename;
		String fromPath = gewaPicService.getTempFilePath(filename);
		gewaPicService.saveToLocal(new File(fromPath), "/image/temp/" + filename);
		String tmpPath = gewaPicService.getTempFilePath("wh_"+filename);
		PictureUtil.resize(fromPath, tmpPath, (int)imgW, (int)imgH); //�ı��С
		PictureUtil.crop(tmpPath, fromPath, 90, 90, (int)imgleft, (int)imgtop); //����
		gewaPicService.moveRemoteTempTo(member.getId(), "commu", commu.getId(), PictureUtil.getAlbumPicpath(), filename);
		commu.setLogo(realHeadpic);
		daoService.saveObject(commu);
		File f = new File(fromPath);
		if(f.exists()){
			f.delete();
		}
		f = new File(tmpPath);
		if(f.exists()){
			f.delete();
		}
		model.put("commuid", commuid);
		return showRedirect("/home/commu/manage.xhtml", model);
	}
	
	
	//Ȧ�ӹ������
	@RequestMapping("/home/commu/managernotice.xhtml")
	public String managerNotice(ModelMap model,Long commuid,Integer pageNo){
		String validdata = commonData(model, commuid, getLogonMember()); 
		if(validdata != null)return validdata;
		
		if(pageNo == null) pageNo = 0;
		Integer rowsPerPage = 10;
		Integer from = pageNo*rowsPerPage;
		
		List<Notice> noticeList = noticeService.getNoticeListByCommuid(commuid, Notice.TAG_COMMU, from,rowsPerPage);
		Integer noticeCount = noticeService.getNoticeCountByCount(commuid, Notice.TAG_COMMU);
		PageUtil pageUtil = new PageUtil(noticeCount,rowsPerPage,pageNo,"home/commu/managernotice.xhtml", true, true);
		Map params = new HashMap();
		params.put("commuid",commuid);
		pageUtil.initPageInfo(params);
		model.put("pageUtil",pageUtil);
		model.put("noticeList",noticeList);
		return "home/community/manage/commuNotice.vm";
	}
	//ɾ��Ȧ�ӹ�����Ϣ
	@RequestMapping("/home/commu/delecommunotice.xhtml")
	public String deleCommuNotice(ModelMap model,Long id,Long commuid){
		Member member=getLogonMember();
		String validata = getAjaxData(model, commuid, member);
		if(validata != null) return validata;
		try{
			daoService.removeObjectById(Notice.class, id);
			return showJsonSuccess(model,"ɾ���ɹ���");
		}catch(Exception e){
			return showJsonError(model, "�����ݣ�ɾ��ʧ�ܣ�");
		}
	}
	
	// ����Ԥ����
	@RequestMapping("/home/commu/preLoadNotice.xhtml")
	public String preLoadNotice(ModelMap model,Long commuid, Long id){
		String validdata = commonData(model, commuid, getLogonMember()); 
		if(validdata != null)return validdata;
		
		Notice notice = daoService.getObject(Notice.class, id);
		if(notice == null){
			return showJsonError_NOT_FOUND(model);
		}
		return showJsonSuccess(model, BeanUtil.getBeanMap(notice, false));
	}
	
	//���Ȧ�ӹ�����Ϣ
	@RequestMapping("/home/commu/addcommunotice.xhtml")
	public String addCommuNotice(ModelMap model,String body,Long commuid, Long id){
		Member member=getLogonMember();
		if(member == null) return showJsonError_NOT_LOGIN(model);
		String ajaxdata = getAjaxData(model, commuid, member);
		if(ajaxdata != null) return ajaxdata;
		if(StringUtils.isBlank(body)) return showError(model, "�������ݲ���Ϊ�գ�");
		if(StringUtil.getByteLength(body)>20000) return showJsonError(model, "���������ַ�������");
		if(WebUtils.checkString(body)) return showJsonError(model, "�������ݺ��зǷ��ַ���");
		Notice notice = null;
		try{
			if(id == null){
				notice = new Notice(member.getId()); 
			}else{
				notice = daoService.getObject(Notice.class, id);
				if(notice == null){
					return showJsonError_NOT_FOUND(model);
				}
			}
			notice.setBody(XSSFilter.filterAttr(body));
			notice.setRelatedid(commuid);
			notice.setTag(Notice.TAG_COMMU);
			daoService.saveObject(notice);
			return showJsonSuccess(model);
		}catch(Exception e){
			return showJsonError(model,"���ʧ��!����������������Ƿ���ȷ!");
		}
	}
	
	/**
	 *	����ͨ��¼
	 */
	@RequestMapping("/home/commu/messageLog.xhtml")
	public String messageLog(ModelMap model,Long commuid) {
		Member member=getLogonMember();
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null)return showError(model, "���ݴ���!");
		if(!commu.hasStatus(Status.Y)) return show404(model, "����ʵ�Ȧ�ӣ��Ѵ�ϵͳ��ɾ�����޷����ʣ�");
		boolean b = commuService.isCommuMember(commuid, member.getId());
		if(!b) return goBack(model,"�㲻�Ǵ�Ȧ�ӵĳ�Ա������Ȩ���˲�����");
		List<CommuCard> commuCardList = readOnlyTemplate.findByCriteria(getCommuCard(commuid,member.getId()));
		if(!commuCardList.isEmpty()) model.put("commuCard",commuCardList.get(0));
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		model.put("commu", commu);
		// ��鵱ǰȦ�ӵ�״̬
		String checkstatus = commuService.getCheckStatusByIDAndMemID(commuid);
		model.put("checkstatus", checkstatus);
		return "home/community/manage/messageLog.vm";
	}
	
	private DetachedCriteria getCommuCard(Long commuid,Long memberid){
		DetachedCriteria query = DetachedCriteria.forClass(CommuCard.class);
		query.add(Restrictions.eq("commuid",commuid));
		query.add(Restrictions.eq("memberid", memberid));
		return query;
	}
	
	//���Ȧ����Ƭ
	@RequestMapping("/home/commu/addmessagelog.xhtml")
	public String addMessageLog(ModelMap model,HttpServletRequest request,Long commuid){
		Member member = this.getLogonMember();
		if(member == null) return showJsonError_NOT_LOGIN(model);
		boolean isCommuMember =  commuService.isCommuMember(commuid, member.getId());
		if(!isCommuMember) return showJsonError(model, "�㲻�Ǵ�Ȧ�ӵĳ�Ա���������˲�����");
		DetachedCriteria query = DetachedCriteria.forClass(CommuCard.class);
		query.add(Restrictions.eq("commuid",commuid));
		query.add(Restrictions.eq("memberid",member.getId()));
		List commuCardList = readOnlyTemplate.findByCriteria(query);
		CommuCard cc = null;
		if(commuCardList.isEmpty()){
			cc = new CommuCard(member.getId());
		}else{
			cc = (CommuCard) commuCardList.get(0);
		}
		Map map = request.getParameterMap();
		BindUtils.bindData(cc, map);
		if(StringUtils.isNotBlank(cc.getPhone())){
			boolean b = ValidateUtil.isPhone(cc.getPhone());
			if(!b) return showJsonError(model, "��������ֻ���������");
		}
		if(WebUtils.checkPropertyAll(cc)) return showJsonError(model, "���зǷ��ַ���");
		try{
			cc = XSSFilter.filterObjAttrs(cc, "realname","company","position","remark");
			daoService.saveObject(cc);
			return showJsonSuccess(model);
		}catch(Exception e){
			return showJsonError(model, "����ʧ�ܣ�");
		}
	}
	
	
	/**
	 *	������� 
	 */
	@RequestMapping("/home/commu/commuDiaryManage.xhtml")
	public String commuDiaryManage(ModelMap model, Long commuid, Integer pageNo) {
		String validdata = commonData(model, commuid, getLogonMember()); 
		if(validdata != null)return validdata;
		
		// Ȧ������
		List<CommuTopic> commuTopicList = commonService.getCommuTopicList(commuid, -1, -1);
		model.put("commuTopicList", commuTopicList);
		// ȫ������
		if(pageNo==null) pageNo=0;
		int rowsPerPage=20;
		int start = pageNo * rowsPerPage;
		List<Diary> diarylist = commuService.getCommuDiaryListById(Diary.class, commuid, null, null, start, rowsPerPage);
		int count = commuService.getCommuDiaryCount(Diary.class, commuid, null, null);
		PageUtil pageUtil=new PageUtil(count,rowsPerPage,pageNo,"/home/commu/commuDiaryManage.xhtml", true, true);
		Map params = new HashMap(); 
		params.put("commuid", commuid);
		pageUtil.initPageInfo(params);
		model.put("pageUtil",pageUtil);
		model.put("diarylist", diarylist);
		return "home/community/manage/commuDiaryManage.vm";
	}
	/**
	 *  Ajax ����Ȧ������ �ֱ�������(������ѯ)
	 *  Long id, String type, Long commuTopicId, String fromDate, Integer flag, String text, int from, int maxnum
	 */
	@RequestMapping("/home/commu/commuDiaryManageTopic.xhtml")
	public String commuDiaryManageTopic(ModelMap model, Long commuid, Long topicid, Integer pageNo, Date fromDate, Integer flag, String text) {
		String validdata = commonData(model, commuid, false, getLogonMember()); 
		if(validdata != null)return validdata;
		
		if(pageNo==null) pageNo=0;
		int rowsPerPage=20;
		int start = pageNo * rowsPerPage;
		
		topicid = (topicid == 0 ? null : topicid);
		flag = (flag == 0 ? null : flag);
		List<Diary>	diarylist = commuService.getCommuDiaryListBySearch(Diary.class, commuid, null, topicid, fromDate, flag, text, start, rowsPerPage);
		int count = commuService.getCommuDiaryCountBySearch(Diary.class, commuid, null, topicid, fromDate, flag, text);
		PageUtil pageUtil=new PageUtil(count,rowsPerPage,pageNo,"/home/commu/commuDiaryManageTopic.xhtml", true, true);
		Map params = new HashMap(); 
		params.put("commuid", commuid);
		params.put("topicid", topicid);
		pageUtil.initPageInfo(params);
		model.put("pageUtil",pageUtil);
		model.put("diarylist", diarylist);
		return "home/community/manage/diarytable.vm";
	}
	/**
	 *  ����ת�� ���ǰ�ļ���.
	 */
	@RequestMapping("/home/commu/loadCommuTopic.xhtml")
	public String loadCommuTopic(Long commuid, Long did, ModelMap model){
		String validdata = commonData(model, commuid, false, getLogonMember()); 
		if(validdata != null)return validdata;
		
		DiaryBase diary = diaryService.getDiaryBase(did);
		if(diary == null){
				return showError(model, "���ݴ���!");
		}
		
		List<CommuTopic> commuTopicList = commonService.getCommuTopicList(commuid, -1, -1);
		Long primaryKey = diary.getModeratorid();
		if(primaryKey == null){
			model.put("commuTopicList", commuTopicList);
		}else{
			CommuTopic commuTopic = daoService.getObject(CommuTopic.class, primaryKey);
			commuTopicList.remove(commuTopic);
			model.put("commuTopicList", commuTopicList);
		}
		return "home/community/manage/selcommutopic.vm";
	}
	/**
	 *  ����ת��
	 */
	@RequestMapping("/home/commu/diary2otherTopic.xhtml")
	public String diary2otherTopic(ModelMap model, Long commuid, Long did, Long topicid){
		String validdata = commonData(model, commuid, false, getLogonMember()); 
		if(validdata != null)return validdata;
		
		if(did == null){return showJsonError(model, "���ݴ���!");}
		DiaryBase diary = diaryService.getDiaryBase(did);
		diary.setModeratorid(topicid);
		daoService.saveObject(diary);
		return showJsonSuccess(model);
	}
	/**
	 *  �����ö�
	 */
	
	/**
	 *  ����ɾ��
	 */
	@RequestMapping("/home/commu/diary2del.xhtml")
	public String diary2del(ModelMap model, Long commuid, Long did){
		String validdata = commonData(model, commuid, false, getLogonMember()); 
		if(validdata != null)return validdata;
		
		if(did == null){return showJsonError(model, "���ݴ���!");}
		DiaryBase diary =  diaryService.getDiaryBase(did);
		diary.setStatus(Status.N_DELETE);
		daoService.saveObject(diary);
		searchService.pushSearchKey(diary);//��������������������
		return showJsonSuccess(model);
	}
	
	
	/**
	 *	ɾ��Ȧ��
	 */
	@RequestMapping("/home/commu/deleteCommu.xhtml")
	public String deleteCommu(ModelMap model, Long commuid) {
		String validdata = commonData(model, commuid, getLogonMember()); 
		if(validdata != null)return validdata;
		return "home/community/manage/deleteCommu.vm";
	}
	/**
	 *	ת��Ȧ��
	 */
	@RequestMapping("/home/commu/assignCommuShow.xhtml")
	public String assignCommuShow(ModelMap model, Long commuid) {
		String validdata = commonData(model, commuid, getLogonMember()); 
		if(validdata != null)return validdata;
		return "home/community/manage/extendCommuManage.vm";
	}
	
	/**
	 *	������
	 */
	@RequestMapping("/home/commu/photoManage.xhtml")
	public String photoManage(ModelMap model, Long commuid,Integer pageNo) {
		Member member = getLogonMember();
		String validdata = commonData(model, commuid, member); 
		if(validdata != null)return validdata;
		
		if(pageNo==null) pageNo=0;
		int rowsPerPage=12;
		int start = pageNo * rowsPerPage;
		int count=0;
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		List<Album> albumList=commuService.getCommuAlbumById(commuid, start, rowsPerPage);
		Map<Long,Integer> imageNum = new HashMap<Long, Integer>();
		for(Album album:albumList){
			Integer num = commuService.getPictureCountByCommuid(album.getId());
			imageNum.put(album.getId(), num);
		}
		count=commuService.getCommuAlbumCountById(commuid);
		PageUtil pageUtil=new PageUtil(count,rowsPerPage,pageNo,"/home/commu/photoManage.xhtml", true, true);
		Map params = new HashMap(); 
		params.put("commuid", commuid);
		pageUtil.initPageInfo(params);
		model.put("imageNum",imageNum);
		model.put("pageUtil",pageUtil);
		model.put("albumList", albumList);
		model.put("manageCommu",true);
		return "home/community/manage/photoManage.vm";
	}
	/**
	 *	Ⱥ������ 
	 */
	@RequestMapping("/home/commu/commuMessage.xhtml")
	public String commuMessage(ModelMap model, Long commuid, Integer pageNo) {
		Member member=getLogonMember();
		String validdata = commonData(model, commuid, member); 
		if(validdata != null)return validdata;
		Commu commu = (Commu)model.get("commu");
		if(pageNo==null) pageNo=0;
		int rowsPerPage=20;
		int start = pageNo * rowsPerPage;
		Integer commuMemberCount = commuService.getCommumemberCount(commu.getId() ,null);
		Map<Long, VisitCommuRecord> visitCommuRecordMap = new HashMap<Long, VisitCommuRecord>();
		List<CommuMember> commuMemberList = commuService.getCommuMemberById(commu.getId(), null, null, "", start, rowsPerPage);
		PageUtil pageUtil=new PageUtil(commuMemberCount, rowsPerPage, pageNo, "home/commu/commuMessage.xhtml", true, true);
		for(CommuMember commuMember:commuMemberList){
			VisitCommuRecord visitCommuRecord=commuService.getVisitCommuRecordByCommuidAndMemberid(commuid, commuMember.getMemberid());
			visitCommuRecordMap.put(commuMember.getId(), visitCommuRecord);
		}
		List<Long> memberIdList = ServiceHelper.getMemberIdListFromBeanList(commuMemberList);
		addCacheMember(model, memberIdList);
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		Map params = new HashMap(); 
		params.put("commuid", commu.getId());
		pageUtil.initPageInfo(params);
		model.put("commuMemberList", commuMemberList);
		model.put("pageUtil", pageUtil);
		model.put("visitCommuRecordMap", visitCommuRecordMap);
		model.put("commu",commu);
		return "home/community/manage/commuMessage.vm";
	}
	//������Ϣ
	@RequestMapping("/home/commu/sendSysMessage.xhtml")
	public String sendSysMessage(ModelMap model, Long commuid, HttpServletRequest request, String captchaId, String captcha){
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		Map<String, String[]> memberMap = request.getParameterMap();
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null)return showJsonError(model, "��Ȧ�Ӳ����ڣ�");
		if(!commu.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
		Member member = getLogonMember();
		if(blogService.isBlackMember(member.getId())) return showJsonError_BLACK_LIST(model);
		if(!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())) return showError(model, "��û�д˲���Ȩ��!");
		String checkStr = request.getParameter("systitle") + request.getParameter("syscontent");
		if(WebUtils.checkString(checkStr)) return showJsonError(model, "���зǷ��ַ���");
		if(StringUtils.isNotBlank(blogService.filterContentKey(checkStr))) return showJsonError(model, "�������˹ؼ���");
		for(String mmap : memberMap.keySet()){
			if(mmap.startsWith("memberid")){
				CommuCard commuCard=commuService.getCommuCardByCommuidAndMemberid(new Long(memberMap.get(mmap)[0]), commuid);
				if(commuCard == null || Status.Y.equals(commuCard.getMessageset())){//�Ƿ����Ⱥ����Ϣ֪ͨ
					UserMessage userMessage = new UserMessage(""); 
					userMessage.setContent(XSSFilter.filterAttr(request.getParameter("syscontent")));
					String message= XSSFilter.filterAttr(request.getParameter("systitle"))+"����"+commu.getName()+"��Ȧ��֪ͨ";
					userMessage.setSubject(message);
					daoService.saveObject(userMessage);
					
					UserMessageAction uma = new UserMessageAction(new Long(memberMap.get(mmap)[0]));
					BindUtils.bindData(uma, request.getParameterMap());
					uma.setFrommemberid(member.getId());
					uma.setTomemberid(new Long(memberMap.get(mmap)[0]));
					uma.setUsermessageid(userMessage.getId());
					if(uma.getGroupid()==null) {//�·�������
						uma.setGroupid(userMessage.getId());
					}
					daoService.saveObject(uma);
				}
			}
		}
		return showJsonSuccess(model);
	}
	/**
	 *	�޸�Ȧ������ 
	 */
	@RequestMapping("/home/commu/updateCommu.xhtml")
	public String updateCommu(ModelMap model, Long commuid) {
		Member member = getLogonMember();
		String validdata = commonData(model, commuid, member); 
		if(validdata != null)return validdata;
		Commu commu = (Commu)model.get("commu");
		if(member!=null) {
			model.put("isAdmin", member.getId().equals(commu.getAdminid()));
		}
		// ȡ����Ȥ��ǩ
		String[] interesttags = StringUtils.split(commu.getInteresttag(), "\\|");
		if(interesttags != null && StringUtils.isNotBlank(interesttags[0])){
			List<String> taglist = Arrays.asList(interesttags);
			model.put("taglist", taglist);
		}
		
		// Ȧ�ӳ�Ա��
		model.put("memberCount", commuService.getCommumemberCount(commuid, null));
		// Ȧ�ӻ�����
		model.put("diaryCount", commuService.getCommuDiaryCount(Diary.class, commuid, null, null));
		// Ȧ�������
		model.put("albumCount", commuService.getCommuAlbumCountById(commuid));
		// Ȧ��ͶƱ��
		
		if(commu.getRelatedid() != null){
			Object relate = relateService.getRelatedObject(commu.getTag(), commu.getRelatedid());
			if(relate!=null) {
				String countycode=(String) BeanUtil.get(relate, "countycode");
				if(relate instanceof BaseInfo && StringUtils.isNotBlank(countycode)){
					model.put("indexareaList", placeService.getIndexareaByCountyCode(countycode));
					model.put("countycode", countycode);
					String indexareacode = (String) BeanUtil.get(relate, "indexareacode");
					model.put("indexareacode", indexareacode);
					List placeList = placeService.getPlaceListByTag(commu.getTag(), countycode, indexareacode);
					model.put("placeList", placeList);
				}
				model.put("relate", relate);
			}
		}
		
		if(commu.getSmallcategoryid()!=null){
			Object relate2 = relateService.getRelatedObject(commu.getSmallcategory(), commu.getSmallcategoryid());
			model.put("relate2", relate2);
		}
		if(StringUtils.isNotBlank(commu.getCountycode())){
			County county = daoService.getObject(County.class, commu.getCountycode());
			model.put("county", county);
		}
		setCommuRelated(commu);
		return "home/community/manage/updateCommu.vm";
	}
	private void setCommuRelated(Commu commu){
		if(commu == null) return;
		Object relate = null;
		if (StringUtils.isNotBlank(commu.getTag()) && commu.getRelatedid() != null) {
			relate = relateService.getRelatedObject(commu.getTag(), commu.getRelatedid());
			commu.setRelate(relate);
		}
		if (StringUtils.isNotBlank(commu.getSmallcategory()) && commu.getSmallcategoryid() != null) {
			relate = relateService.getRelatedObject(commu.getSmallcategory(), commu.getSmallcategoryid());
			commu.setRelate2(relate);
		}
	}
	// ����Ȧ����Ȥ��ǩ
	@RequestMapping("/home/commu/addCommuInsterestTag.xhtml")
	public String addCommuInsterestTag(ModelMap model, Long commuid, String tag){
		String validdata = commonData(model, commuid, false, getLogonMember()); 
		if(validdata != null)return validdata;
		Commu commu = (Commu)model.get("commu");
		String tags = commu.getInteresttag() == null ? "" : commu.getInteresttag();
		String[] interesttags = StringUtils.split(tags, "\\|");
		if(interesttags != null && interesttags.length > 0){
			List<String> taglist = Arrays.asList(interesttags);
			tag = StringUtils.trim(tag);
			if(taglist.contains(tag)){
				return showError(model, "�����ظ���ӱ�ǩ��");
			}
		}
		tags += tag + "|";
		commu.setInteresttag(tags);
		daoService.saveObject(commu);
		getInterestTags(model, commu);
		return "home/community/manage/commuinsteresttag.vm";
	}
	private Map getInterestTags(ModelMap model, Commu commu){
		List<String> taglist = Arrays.asList(StringUtils.split(commu.getInteresttag(), "\\|"));
		model.put("taglist", taglist);
		return model;
	}
	// ɾ��Ȧ����Ȥ��ǩ
	@RequestMapping("/home/commu/delCommuInsterestTag.xhtml")
	public String delCommuInsterestTag(ModelMap model, Long commuid, String tag){
		String validdata = commonData(model, commuid, false, getLogonMember()); 
		if(validdata != null)return validdata;
		Commu commu = (Commu)model.get("commu");
		String[] interesttags = StringUtils.split(commu.getInteresttag(), "\\|");
		if(ArrayUtils.contains(interesttags, tag)){
			interesttags = (String[])ArrayUtils.removeElement(interesttags, tag);
		}
		StringBuilder sb = new StringBuilder();
		for(String str : interesttags){
			sb.append(str);
			sb.append("|");
		}
		commu.setInteresttag(sb.toString());
		daoService.saveObject(commu);
		return showJsonSuccess(model);
	}
	
	/**
	 * ������б�
	 */
	@RequestMapping("/home/commu/commuTopicsList.xhtml")
	public String getCommuTopicList(ModelMap model,Long commuid,Integer pageNo){
		if(pageNo == null) pageNo = 0;
		String validdata = commonData(model, commuid, getLogonMember()); 
		if(validdata != null)return validdata;
		Integer rowsPerPage = 20;
		Integer from = pageNo * rowsPerPage;
		List<CommuTopic> commuTopicList = commonService.getCommuTopicList(commuid, from, rowsPerPage);
		Integer count = commonService.getCommuTopicCount(commuid);
		Map params = new HashMap();
		params.put("commuid",commuid);
		PageUtil pageUtil = new PageUtil(count ,rowsPerPage,pageNo,"home/commu/commuTopicsList.xhtml", true, true);
		pageUtil.initPageInfo(params);
		model.put("pageUitl", pageUtil);
		model.put("commuTopicList",commuTopicList);
		model.put("commu",daoService.getObject(Commu.class, commuid));
		return "home/community/manage/diaryModelManage.vm";
	}
	
	/**
	 * ��ӻ�����
	 */
	@RequestMapping("/home/commu/addCommuTopic.xhtml")
	public String addCommuTopic(ModelMap model,Long commuid,String topicname){
		Member member=getLogonMember();
		if(WebUtils.checkString(topicname)) return showJsonError(model, "���⺬�зǷ��ַ���");
		String ajaxdata = getAjaxData(model, commuid, member);
		if(ajaxdata != null) return ajaxdata;
		CommuTopic ct = new CommuTopic(commuid);
		ct.setTopicname(topicname);
		try{
			daoService.saveObject(ct);
			return showJsonSuccess(model);
		}catch(Exception e){
			return showJsonError(model, "�»��������ʧ�ܣ�");
		}
	}
	/**
	 * ɾ��������
	 */
	@RequestMapping("/home/commu/delCommuTopic.xhtml")
	public String delCommuTopic(ModelMap model,Long id,Long commuid){
		Member member=getLogonMember();
		String ajaxdata = getAjaxData(model, commuid, member);
		if(ajaxdata != null) return ajaxdata;
		try{
			daoService.removeObjectById(CommuTopic.class, id);
			return showJsonSuccess(model);
		}catch(Exception e){
			return showJsonError(model,"ɾ��ʧ�ܣ�");
		}
	}
	/**
	 * �޸Ļ�������Ϣ
	 */
	@RequestMapping("/home/commu/updateCommuTopic.xhtml")
	public String updateCommuTopic(ModelMap model,HttpServletRequest request,Long commuid,Integer commuTopicCount){
		Member member=getLogonMember();
		String ajaxdata = getAjaxData(model, commuid, member);
		if(ajaxdata != null) return ajaxdata;
		Map map = request.getParameterMap();
		if(commuTopicCount>0){
			for (int i = 0; i<commuTopicCount; i++) {
				String[] topicname = (String[]) map.get("topicname"+i);
				String[] ordernum = (String[]) map.get("ordernum"+i);
				String[] displaynum = (String[]) map.get("displaynum"+i);
				if(StringUtils.isBlank(topicname[0])) return showJsonError(model, "Ȧ�ӻ��������Ʋ���Ϊ�գ�");
				if(topicname[0].length()>10) return showJsonError(model,"Ȧ�ӻ��������Ʋ��ܳ���10���֣�");
				if(WebUtils.checkString(topicname[0])) return showJsonError(model, "���⺬�зǷ��ַ���");
				if(StringUtils.isBlank(ordernum[0])) return showJsonError(model, "��ʾ˳����Ϊ�գ�");
				if(!ValidateUtil.isNumber(ordernum[0])) return showJsonError(model, "��ʾ˳��ֵֻ���������֣�");
				if(StringUtils.isBlank(displaynum[0])) return showJsonError(model, "��ʾ����������Ϊ�գ�");
				if(!ValidateUtil.isNumber(displaynum[0])) return showJsonError(model, "��ʾ������ֵֻ���������֣�");
				if(Integer.valueOf(displaynum[0]+"")>30||Integer.valueOf(displaynum[0]+"")<0){
					return showJsonError(model, "��ʾ������ֵֻ������0-30֮������֣�");
				}
			}
			for (int i = 0; i<commuTopicCount; i++) {
				String[] id = (String[]) map.get("id"+i);
				String[] topicname = (String[]) map.get("topicname"+i);
				String[] ordernum = (String[]) map.get("ordernum"+i);
				String[] displaynum = (String[]) map.get("displaynum"+i);
				CommuTopic ct = daoService.getObject(CommuTopic.class, Long.valueOf(id[0]));
				ct.setCommuid(commuid);
				ct.setTopicname(topicname[0]);
				ct.setOrdernum(Integer.valueOf(ordernum[0]));
				ct.setDisplaynum(Integer.valueOf(displaynum[0]));
				daoService.updateObject(ct);
			}
		}else{
			return showError(model, "����Ȧ�ӻ�����࣡");
		}
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/home/commu/logoutCommu.xhtml")
	public String logoutCommu(ModelMap model,Long commuid){
		Member member = getLogonMember();
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null)return show404(model, "���ݴ���!");
		if(!commu.hasStatus(Status.Y)) return show404(model, "����ʵ�Ȧ�ӣ��Ѵ�ϵͳ��ɾ�����޷����ʣ�");
		if(member.getId().equals(commu.getAdminid())) return showJsonError(model, "Ȧ�Ӵ����߲����˳�Ȧ�ӣ�");
		boolean isCommuMember = commuService.isCommuMember(commuid, member.getId());
		if(!isCommuMember) return showJsonError(model,"ֻ��Ȧ�ӳ�Ա���д˲���Ȩ��");
		DetachedCriteria query = DetachedCriteria.forClass(CommuMember.class);
		query.add(Restrictions.eq("commuid", commuid));
		query.add(Restrictions.eq("memberid", member.getId()));
		List commuMemberlist = readOnlyTemplate.findByCriteria(query, 0, 1);
		try{
			if(!commuMemberlist.isEmpty()){
				CommuMember cm = (CommuMember) commuMemberlist.get(0);
				daoService.removeObject(cm);
				//����ϵͳ��Ϣ
				String str = "�û�"+member.getNickname()+"�˳�Ȧ��"+"��"+commu.getName()+"��";
				SysMessageAction sysmessage=new SysMessageAction(SysAction.STATUS_RESULT);
				sysmessage.setFrommemberid(member.getId());
				sysmessage.setBody(str);
				sysmessage.setTomemberid(commu.getAdminid());
				daoService.saveObject(sysmessage);
			}
			return showJsonSuccess(model);
		}catch(Exception e){
			return showJsonError(model,"�˳�Ȧ��ʧ�ܣ�");
		}
	}
	
	@RequestMapping("/home/commu/delCommu.xhtml")
	public String delCommu(ModelMap model, Long commuid){
		Member member = getLogonMember();
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null)return showJsonError(model, "���ݴ���!");
		if(!commu.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
		if(!member.getId().equals(commu.getAdminid())) {
			return showJsonError(model, "ֻ�д����߲���Ȩ�޲�����");
		}
		try{
			commu.setStatus(Status.N_DELETE);
			daoService.updateObject(commu);
			searchService.pushSearchKey(commu);//��������������������
			return showJsonSuccess(model);
		}catch(Exception e){
			return showJsonError(model, "ɾ��ʧ�ܣ�");
		}
	}
	
	private String getAjaxData(ModelMap model,Long commuid,Member member){
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null)return show404(model, "���ݴ���!");
		if(!commu.hasStatus(Status.Y)) return show404(model, "����ʵ�Ȧ�ӣ��Ѵ�ϵͳ��ɾ�����޷����ʣ�");
		model.put("commu",commu);
		if(!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())) return showJsonError(model, "ֻ�й���Ա����Ȩ�޲�����");
		return null;
	}

	//����Ȧ�Ӻ���������׼����Ȧ��
	@RequestMapping("/home/commu/commuUnapproveAndBlackMember.xhtml")
	public String saveCommuBlackMember(ModelMap model, HttpServletRequest request, Long commuid, String ctype){
		Member member = getLogonMember();
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu==null) return showJsonError(model,"Ȧ�Ӳ����ڣ�");
		if(!commu.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
		if(!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())) return showError(model, "ֻ�й���Ա����Ȩ�޲���!");
		Map<String ,String[]> memberMap=request.getParameterMap();
		String str = "";
		if(ctype.equals("approvecommu")){//��׼����
			if(member.getId().equals(commu.getAdminid())|| member.getId().equals(commu.getSubadminid())){
				for(String mmap : memberMap.keySet()){
					if(mmap.startsWith("blackmemberid")){
						SysMessageAction sys = userMessageService.getSysMessageAction(commuid, new Long(memberMap.get(mmap)[0]), SysAction.ACTION_APPLY_COMMU_JOIN, true);
						CommuMember commuMember = new CommuMember(sys.getFrommemberid());
						commuMember.setCommuid(sys.getActionid());
						daoService.saveObject(commuMember);
						//����ϵͳ��Ϣ״̬�ı�
						sys.setStatus(SysAction.STATUS_AGREE);
						daoService.saveObject(sys);
						
						//����ϵͳ��Ϣ
						SysMessageAction newsma = new SysMessageAction(SysAction.STATUS_RESULT);
						newsma.setFrommemberid(member.getId());
						newsma.setTomemberid(sys.getFrommemberid());
						newsma.setActionid(sys.getId());
						str = "��ϲ��ͨ����˳ɹ�����Ȧ�� " + commu.getName();
						newsma.setBody(str);
						daoService.saveObject(newsma);
					}
				}
			}
		}else if(ctype.equals("commonmember")){//ͨ�����
			for(String mmap : memberMap.keySet()){
				if(mmap.startsWith("blackmemberid")){
					CommuMember tmpcommuMember = commuService.getCommuMemberByMemberidAndCommuid(new Long(memberMap.get(mmap)[0]), commuid);
					CommuMember commuMember = daoService.getObject(CommuMember.class, tmpcommuMember.getId());
					commuMember.setFlag(CommuMember.FLAG_NORMAL);
					daoService.saveObject(commuMember);
					//����ϵͳ��Ϣ
					String title = "��ϲ��ͨ����˳ɹ�����Ȧ��" + commu.getName();
					this.sendSysMessage(member.getId(), title, Long.valueOf(memberMap.get(mmap)[0]));
				}
			}
		}else if(ctype.equals("delete")){//ɾ����������Ա
			for(String mmap : memberMap.keySet()){
				if(mmap.startsWith("blackmemberid")){
					if(commu.getAdminid().equals(new Long(memberMap.get(mmap)[0]))) return showJsonError(model, "����ɾ������Ա��");
					if((member.getId()).equals(commu.getAdminid()) || member.getId().equals(commu.getSubadminid())){//�жϵ�ǰ�û��Ƿ������Ȧ�ӵĹ���Ա
						friendService.deleteCommueMember(new Long(memberMap.get(mmap)[0]), commuid);
						//����ϵͳ��Ϣ
						String title = "����Ա"+member.getNickname()+"�����Ȧ��"+commu.getName()+"���߳���";
						this.sendSysMessage(member.getId(), title, Long.valueOf(memberMap.get(mmap)[0]));
						//ɾ�����ʼ�¼
						VisitCommuRecord visitCommuRecord = commuService.getVisitCommuRecordByCommuidAndMemberid(commuid, new Long(memberMap.get(mmap)[0]));
						daoService.removeObject(visitCommuRecord);
					}
				}
			}
		}
		return showJsonSuccess(model);
	}
	//���ó�Ա���
	@RequestMapping("/home/commu/commuMember.xhtml")
	public String commuMember(ModelMap model, HttpServletRequest request, Long commuid, String commutype){
		Member member = getLogonMember();
		Commu commu=daoService.getObject(Commu.class, commuid);
		if(commu==null)return showJsonError(model, "Ȧ�Ӳ����ڣ�");
		if(!commu.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
		if(!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())) return showError(model, "ֻ�й���Ա����Ȩ�޲���!");
		Map<String ,String[]> memberMap = request.getParameterMap();
		if(StringUtils.isBlank(commutype))commutype="common";
		if(commutype.equals("common")){//��Ϊ��ͨ��Ա
			for(String mmap : memberMap.keySet()){
				if(mmap.startsWith("memberid")){
					CommuMember commuMember=commuService.getCommuMemberByMemberidAndCommuid(new Long(memberMap.get(mmap)[0]), commuid);
					if(commu.getAdminid().equals(new Long(memberMap.get(mmap)[0]))) return showJsonError(model, "���ܽ������߽�Ϊ��ͨ��Ա!");
					if(commuMember != null && !commu.getSubadminid().equals(Long.valueOf((memberMap.get(mmap)[0])))) return showJsonError(model, "������ͨ��Ա!");
					commu.setSubadminid(0L);
					daoService.saveObject(commu);
					//����ϵͳ��Ϣ
					String str = "����Ա"+member.getNickname()+"���������Ȧ��"+commu.getName()+"�Ĺ���Ա�ʸ�";
					this.sendSysMessage(member.getId(), str, Long.valueOf(memberMap.get(mmap)[0]));
				}
			}
		} else if(commutype.equals("administer")){//��Ϊ����Ա
			Set memberSet = memberMap.keySet();
			int i=0;
			for (Object object : memberSet) {
				if((object+"").startsWith("memberid")){
					i++;
				}
			}
			Collection<String[]> params = memberMap.values();
			if(i>1) return showJsonError(model, "��ʱֻ֧�����һ������Ա!");
			if(commu.getSubadminid()!=0) return showJsonError(model, "��ʱֻ֧��һ������Ա��");
			for (String[] str : params) {
				if((commu.getAdminid()+"").equals(str[0])) return showJsonError(model, "��ʱֻ֧��һ������Ա!");
			}
			commu.setSubadminid(new Long(memberMap.get("memberid")[0]));
			daoService.saveObject(commu);
			
			String str = "����Ա"+member.getNickname()+"��������Ϊ"+commu.getName()+"�Ĺ���Ա";
			this.sendSysMessage(member.getId(), str, Long.valueOf(memberMap.get("memberid")[0]));
		} else if(commutype.equals("blackmember")){ //����Ȧ�ӵĺ�����
			for(String mmap : memberMap.keySet()){
				if(mmap.startsWith("memberid")){
					if(commu.getAdminid().equals(new Long(memberMap.get(mmap)[0])) ||
							commu.getSubadminid().equals(new Long(memberMap.get(mmap)[0]))){//�ж��Ƿ����Ա
						return showJsonError(model, "����Ա���ܹ���С����!");
					}else {
						CommuMember commuMember = commuService.getCommuMemberByMemberidAndCommuid(new Long(memberMap.get(mmap)[0]), commuid);
						commuMember.setFlag(CommuMember.FLAG_BLACK);
						daoService.saveObject(commuMember);
						//����ϵͳ��Ϣ
						String str = "����Ա"+member.getNickname()+"���������"+commu.getName()+ "��С���ݷ�ʡ���ڽ��С����֮ǰ�㲻����Ȧ�ڽ��з����ͷ�����Ȳ�����";
						this.sendSysMessage(member.getId(), str, Long.valueOf(memberMap.get(mmap)[0]));
					}
				}
			}
		}else if(commutype.equals("deletemember")){
			for(String mmap : memberMap.keySet()){
				if(mmap.startsWith("memberid")){
					if(commu.getAdminid().equals(new Long(memberMap.get(mmap)[0]))) return showJsonError(model, "����ɾ������Ա��");
					if((member.getId()).equals(commu.getAdminid()) || member.getId().equals(commu.getSubadminid())){//�жϵ�ǰ�û��Ƿ������Ȧ�ӵĹ���Ա
						friendService.deleteCommueMember(new Long(memberMap.get(mmap)[0]), commuid);
						//����ϵͳ��Ϣ
						String title = "����Ա"+member.getNickname()+"�����Ȧ��"+commu.getName()+"���߳���";
						this.sendSysMessage(member.getId(), title, Long.valueOf(memberMap.get(mmap)[0]));
						//ɾ�����ʼ�¼
						VisitCommuRecord visitCommuRecord = commuService.getVisitCommuRecordByCommuidAndMemberid(commuid, new Long(memberMap.get(mmap)[0]));
						daoService.removeObject(visitCommuRecord);
					}
				}
			}
		}
		return showJsonSuccess(model);
	}
	/**
	 * Ȧ�������ϸ��Ϣ
	 */
	@RequestMapping("/home/commu/manageAlbumImageList.xhtml")
	public String albumImageList(ModelMap model, Long albumid,Integer pageNo) {
		Member mymember = getLogonMember();
		Album album = daoService.getObject(Album.class, albumid);
		if(album == null) return showError(model, "����������Ϣ��");
		String validdata = commonData(model, album.getCommuid(), getLogonMember()); 
		if(validdata != null)return validdata;
		if(pageNo==null) pageNo=0;
		int rowsPerPage=20;
		int start = pageNo * rowsPerPage;
		int count = 0;
		List<Picture> albumImageList = albumService.getPictureByAlbumId(albumid, start, rowsPerPage);
		count = albumService.getPictureountByAlbumId(albumid);
		PageUtil pageUtil = new PageUtil(count,rowsPerPage,pageNo,"home/commu/manageAlbumImageList.xhtml", true, true);
		Member albumMember = daoService.getObject(Member.class, album.getMemberid());
		Map params = new HashMap(); 
		params.put("albumid", albumid);
		params.put("commuid", album.getCommuid());
		pageUtil.initPageInfo(params);
		if (album.getMemberid().equals(mymember.getId())) model.put("ismycommu",true);
		model.put("mymember",mymember);
		model.put("pageUtil",pageUtil);
		model.put("albumid", albumid);
		model.put("albumImageList",albumImageList);
		model.put("albumMember",albumMember);
		model.put("album",album);
		model.put("commuid", album.getCommuid());
		model.put("commu",model.get("commu"));
		model.put("isShowCommuAlbum", true);
		model.putAll(controllerService.getCommonData(model, mymember, mymember.getId()));
		return "home/community/manage/manageAlbumImageList.vm";
	}
	private void sendSysMessage(Long adminid, String syscontent, Long memberid){
		//����ϵͳ��Ϣ
		SysMessageAction sysmessage=new SysMessageAction(SysAction.STATUS_RESULT);
		sysmessage.setFrommemberid(adminid);
		sysmessage.setBody(syscontent);
		sysmessage.setTomemberid(memberid);
		daoService.saveObject(sysmessage);
	}
	
	// ��֧������Ϣ
	@RequestMapping("/home/commu/commuBindAlipay.xhtml")
	public String commuBindAlipay(ModelMap model, Long commuid){
		Member member = getLogonMember();
		String validdata = commonData(model, commuid, member); 
		if(validdata != null)return validdata;
		Commu commu = daoService.getObject(Commu.class, commuid);
		if(commu == null)return show404(model, "���ݴ���!");
		if(!commu.hasStatus(Status.Y)) return show404(model, "����ʵ�Ȧ�ӣ��Ѵ�ϵͳ��ɾ�����޷����ʣ�");
		if(member != null ){
			// ��¼��
			if(!(member.getId().equals(commu.getAdminid()))){
				// �ǹ���Ա
				return showError(model, "��û�в�����Ȩ��!");
			}
			model.putAll(controllerService.getCommonData(model, member, member.getId()));
			model.put("commu", commu);
			model.put("commuManage", commuService.getCommuManageByCommuid(commuid));
		}
		return "home/community/manage/commubindalipay.vm";
	}
	// ����֧������Ϣ�� commu_manage
	@RequestMapping("/home/commu/saveCommumanage4Alipay.xhtml")
	public String saveCommumanage4Alipay(ModelMap model, String alipay, String alipayname, String contactphone, Long commumanageid){
		if(StringUtils.isBlank(alipay))	return showJsonError(model, "֧�����˻�����");
		if(StringUtils.isBlank(alipayname))	return showJsonError(model, "֧�����˻���������");
		
		CommuManage commuManage = daoService.getObject(CommuManage.class, commumanageid);
		if(commuManage != null){
			commuManage.setAlipay(alipay);
			commuManage.setAlipayname(alipayname);
			commuManage.setContactphone(contactphone);
			daoService.saveObject(commuManage);
			return showJsonSuccess(model);
		}
		return showJsonError_DATAERROR(model);
	}
}