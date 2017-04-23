package com.gewara.web.action.drama;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.AdminCityContant;
import com.gewara.constant.CityData;
import com.gewara.constant.DramaConstant;
import com.gewara.constant.Flag;
import com.gewara.constant.OdiConstant;
import com.gewara.constant.SmsConstant;
import com.gewara.constant.Status;
import com.gewara.constant.TagConstant;
import com.gewara.json.PageView;
import com.gewara.model.common.BaseInfo;
import com.gewara.model.common.Relationship;
import com.gewara.model.common.UserOperation;
import com.gewara.model.content.HeadInfo;
import com.gewara.model.content.News;
import com.gewara.model.content.Picture;
import com.gewara.model.drama.Drama;
import com.gewara.model.drama.DramaStar;
import com.gewara.model.drama.Theatre;
import com.gewara.model.drama.TheatreField;
import com.gewara.model.drama.TheatreProfile;
import com.gewara.model.movie.Cinema;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.sport.Sport;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberPicture;
import com.gewara.service.OperationService;
import com.gewara.service.bbs.MarkService;
import com.gewara.service.content.NewsService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CacheDataService;
import com.gewara.untrans.MailService;
import com.gewara.untrans.PageCacheService;
import com.gewara.untrans.PageParams;
import com.gewara.untrans.UntransService;
import com.gewara.untrans.impl.ControllerService;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.MarkHelper;
import com.gewara.util.ValidateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.util.PageUtil;
import com.gewara.xmlbind.activity.RemoteActivity;
import com.gewara.xmlbind.gym.RemoteGym;

@Controller
public class TheatreInfoController extends BaseDramaController {

	@Autowired@Qualifier("controllerService")
	private ControllerService controllerService;
	public void setControllerService(ControllerService controllerService){
		this.controllerService = controllerService;
	}
	
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	public void setOperationService(OperationService operationService){
		this.operationService = operationService;
	}
	@Autowired@Qualifier("mailService")
	private MailService mailService;
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}
	
	@Autowired@Qualifier("markService")
	private MarkService markService;
	public void setMarkService(MarkService markService){
		this.markService = markService;
	}
	
	@Autowired@Qualifier("newsService")
	private NewsService newsService;
	public void setNewsService(NewsService newsService){
		this.newsService = newsService;
	}
	
	@Autowired@Qualifier("cacheDataService")
	private CacheDataService cacheDataService;
	
	@Autowired@Qualifier("pageCacheService")
	private PageCacheService pageCacheService;
	public void setPageCacheService(PageCacheService pageCacheService){
		this.pageCacheService = pageCacheService;
	}
	
	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	public void setUntransService(UntransService untransService){
		this.untransService = untransService;
	}
	
	//�����ֻ�����
	@RequestMapping("/theatre/ajax/sendMessage.xhtml")
	public String sendMessage(@CookieValue(value= LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, Long tid, String mobile, String captchaId, String captcha, ModelMap model){
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha)return showJsonError(model, "��֤�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null)return showJsonError(model, "���ȵ�¼��");
		if(!ValidateUtil.isMobile(mobile))return showJsonError(model, "�ֻ������ʽ����ȷ��");
		Theatre theatre = daoService.getObject(Theatre.class, tid);
		if(theatre == null)return showJsonError(model, "��ԺԺ�����ڻ�ɾ����");
		String opkey = "theatre_" + member.getId()+ "_" + theatre.getId();
		boolean allow = operationService.updateOperation(opkey, 10);
		if(!allow)return showJsonError(model, "�������ȥƵ�������Ժ����ԣ�");
		String tradeNo = "theatre_" +  theatre.getId();
		Timestamp curtime = DateUtil.getCurFullTimestamp();
		Timestamp endtime = DateUtil.getLastTimeOfDay(curtime);
		String opsmskey = "theatre_" + member.getId() + "_" + theatre.getId() + "_sms" + DateUtil.formatDate(curtime);
		UserOperation op = daoService.getObject(UserOperation.class, opsmskey);
		if(op != null && op.getOpnum() >= 3) return showJsonError(model, "���ŷ��ʹ����ѳ������ƣ�");
		String msgContent = theatre.getRealBriefname()+"  ��ַ:"+theatre.getAddress()+",��Ʊ��Ϣ�鿴http://t.cn/zHOpWzy";
		if(msgContent.length() > 60){
			msgContent = msgContent.substring(0, 60);
			mailService.sendEmail("www.gewara.com", theatre.getName()+"���ݶ������ݹ�������", "���ݶ��ų���60���ַ����뼰ʱ�����޸ģ�", "sandy.chen@gewara.com");
		}
		SMSRecord sms = new SMSRecord(mobile);
		sms.setTradeNo(tradeNo);
		sms.setContent(msgContent);
		sms.setSendtime(curtime);
		sms.setSmstype(SmsConstant.SMSTYPE_MANUAL);
		sms.setValidtime(endtime);
		sms.setTag(TagConstant.TAG_THEATRE);
		sms.setMemberid(member.getId());
		sms.setRelatedid(tid);
		sms = untransService.addMessage(sms);
		if(sms != null) untransService.sendMsgAtServer(sms, true);
		operationService.updateOperation(opsmskey, OperationService.ONE_DAY, 3);
		
		return showJsonSuccess(model);
	}
	
	//��Ѷ
	@RequestMapping("/theatre/newsList.xhtml")
	public String theatreNewsList(Long tid, Integer pageNo, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		Theatre theatre = daoService.getObject(Theatre.class, tid);
		if(theatre == null) return show404(model, "����ʵĻ��糡�ݲ����ڣ�");
		model.put("theatre", theatre);
		String citycode = theatre.getCitycode();
		WebUtils.setCitycode(request, citycode, response);
		if(pageNo == null) pageNo = 0;
		int rowsPerPage = 15;
		int firstPerPage = pageNo * rowsPerPage;
		List<News> newsList = newsService.getNewsList(citycode, TagConstant.TAG_THEATRE, tid, "", firstPerPage, rowsPerPage);
		int rowsCount = newsService.getNewsCount(citycode, TagConstant.TAG_THEATRE, "", tid, null);
		PageUtil pageUtil = new PageUtil(rowsCount, rowsPerPage, pageNo, "theatre/"+theatre.getId()+"/newslist", true, true);
		pageUtil.initPageInfo();
		model.put("theatreNewsList", newsList);
		model.put("pageUtil", pageUtil);
		getHeadInfo(tid, citycode, model);
		model.putAll(getHeadData(tid));
		getTheatreNews(citycode, tid, model);
		getActivityList(citycode, tid, model);
		getVenue(theatre, model);
		return "drama/theatre/new_theatreNews.vm";
	}
	
	//��ԺͼƬ
	@RequestMapping("/theatre/theatrePictureList.xhtml")
	public String newPictureList(Long tid, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		Theatre theatre = daoService.getObject(Theatre.class, tid);
		if(theatre == null) return show404(model, "����ʵĻ��糡�ݲ����ڣ�");
		model.put("theatre", theatre);
		String citycode = theatre.getCitycode();
		WebUtils.setCitycode(request, citycode, response);
		
		int pictureCount = pictureService.getPictureCountByRelatedid(TagConstant.TAG_THEATRE, tid);
		int memberPictureCount = pictureService.getMemberPictureCount(tid, TagConstant.TAG_THEATRE, null, TagConstant.FLAG_PIC, Status.Y);
		model.put("pictureCount", pictureCount);
		model.put("memberPictureCount", memberPictureCount);
		
		getHeadInfo(tid, citycode, model);
		model.putAll(getHeadData(tid));
		getTheatreNews(citycode, tid, model);
		getActivityList(citycode, tid, model);
		getVenue(theatre, model);
		List<Drama> dramaInfos = new ArrayList<Drama>();
		List<Drama> bookList = new ArrayList<Drama>();
		putTheatreInfoStatic(citycode, theatre, dramaInfos, bookList, model);
		model.put("subs", "picture");
		return "drama/wide_theatreDetail.vm";
	}
	
	@RequestMapping("/theatre/ajax/theatrePictureList.xhtml")
	public String commonPictureList(ModelMap model,String tag, Long relatedid, Integer pageNo, String type){
		pictureComponent.pictureList(model, pageNo, tag, relatedid, type, "/theatre/ajax/theatrePictureList.xhtml");
		return "drama/theatre/newAjaxPictureList.vm";
	}
	
	//��ԺͼƬ��ϸ
	@RequestMapping("/theatre/theatrePictureDetail.xhtml")
	public String newPictureDetail(ModelMap model, Long tid, Long pid, String type, HttpServletRequest request, HttpServletResponse response){
		if(StringUtils.isBlank(type)) type = "apic";
		Theatre theatre=null;
		if(pid!=null){
			Picture picture=daoService.getObject(Picture.class, pid);
			if(picture!=null){
				theatre=daoService.getObject(Theatre.class, picture.getRelatedid());
			}else {
				MemberPicture mpicture=daoService.getObject(MemberPicture.class, pid);
				theatre=daoService.getObject(Theatre.class, mpicture.getRelatedid());
			}
		}else{
			theatre=daoService.getObject(Theatre.class, tid);
		}
		if(theatre == null) return show404(model, "����ʵĻ��糡�ݲ����ڣ�");
		model.put("theatre", theatre);
		tid = theatre.getId();
		pictureComponent.pictureDetail(model, TagConstant.TAG_THEATRE, tid, pid, type);
		String citycode = theatre.getCitycode();
		WebUtils.setCitycode(request, citycode, response);
		
		getHeadInfo(tid, citycode, model);
		model.putAll(getHeadData(tid));
		getTheatreNews(citycode, tid, model);
		getActivityList(citycode, tid, model);
		getVenue(theatre, model);
		
		return "drama/theatre/new_theatrePictureDetail.vm";
	}
	
	// ���ͼƬ
	@RequestMapping("/theatre/attachPicture.xhtml")
	public String newAttachPicture(ModelMap model, Long relatedid, String tag, HttpServletRequest request, HttpServletResponse response){
		Theatre theatre = daoService.getObject(Theatre.class, relatedid);
		if(theatre == null) return show404(model, "����ʵĻ��糡�ݲ����ڣ�");
		model.put("theatre", theatre);
		String citycode = theatre.getCitycode();
		WebUtils.setCitycode(request, citycode, response);
		Map dataMap=pictureComponent.attachRelatePicture(tag, relatedid, "");
		model.putAll(dataMap);
		model.put("tag", tag);
		model.put("relatedid", relatedid);
		
		getHeadInfo(relatedid, citycode, model);
		model.putAll(getHeadData(relatedid));
		return "drama/theatre/new_attachPicture.vm";
	}
	//��ͼ
	@RequestMapping("/theatre/baiduMap.xhtml")
	public String newTheatreMap(Long id, String from,String end, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		Theatre theatre = daoService.getObject(Theatre.class, id);
		if(theatre == null) return show404(model, "����ʵĻ��糡�ݲ����ڣ�");
		model.put("theatre", theatre);
		String citycode = theatre.getCitycode();
		WebUtils.setCitycode(request, citycode, response);
		
		String cityname = AdminCityContant.getCitycode2CitynameMap().get(citycode);
		if(StringUtils.isNotBlank(from)) {
			cityname = AdminCityContant.getCitycode2CitynameMap().get(citycode);
			if(from.indexOf(cityname)==-1) from =cityname + "��" + from;
		}
		model.put("object", theatre);
		model.put("end", end);
		model.put("from", from);
		model.put("cityname", cityname);
		model.put("cityData", new CityData());
		model.put("citycode", citycode);
		
		getHeadInfo(id, citycode, model);
		model.putAll(getHeadData(id));
		return "drama/theatre/new_theatreMap.vm";
	}
	//ͷ����Ϣ 
	private void getHeadInfo(Long tid, String citycode, ModelMap model){
		model.putAll(markService.getGradeCount(TagConstant.TAG_THEATRE, tid));
		model.put("goCount", markService.getMarkValueCount(TagConstant.TAG_THEATRE, tid, "generalmark", 5, 10));
		int playCount = dramaService.getCurPlayDramaCount(tid);
		model.put("playCount", playCount);
		//commentService.getCommentCountByRelatedId(TagConstant.TAG_THEATRE, tid)
		Integer commentCount = commonService.getCommentCount().get(tid+TagConstant.TAG_THEATRE);
		model.put("commentCount", commentCount);
		int picCount = pictureService.getPictureCountByRelatedid(TagConstant.TAG_THEATRE, tid);
		int memberPictureCount = pictureService.getMemberPictureCount(tid, TagConstant.TAG_THEATRE, null, TagConstant.FLAG_PIC, Status.Y);
		model.put("picCount", picCount + memberPictureCount);
		int newsCount = newsService.getNewsCount(citycode, TagConstant.TAG_THEATRE, "", tid, null);
		model.put("newsCount", newsCount);
		model.put("markHelper", new MarkHelper());
	}
	
	//ͷ����ͷ
	private Map getHeadData(Long tid) {
		Relationship relationship = commonService.getRelationship(Flag.FLAG_HEAD, TagConstant.TAG_THEATRE, tid, DateUtil.getCurFullTimestamp());
		Map headDataMap = new HashMap();
		HeadInfo headInfo = null;
		if (relationship != null) {
			headInfo = daoService.getObject(HeadInfo.class, relationship.getRelatedid1());
		}
		headDataMap.put("headInfo", headInfo);
		return headDataMap;
	}
	
	//��Ժ��Ѷ	
	private void getTheatreNews(String citycode, Long tid, ModelMap model){
		List<News> newsList=newsService.getNewsList(citycode, TagConstant.TAG_THEATRE, tid, "", 0, 5);
		model.put("newsList", newsList);
	}
	//��ػ 
	private void getActivityList(String citycode, Long tid, ModelMap model){
		ErrorCode<List<RemoteActivity>> code = synchActivityService.getActivityList(citycode, RemoteActivity.ATYPE_GEWA, RemoteActivity.TIME_CURRENT, TagConstant.TAG_THEATRE, tid, null, null, 0, 2);
		if(code.isSuccess()){
			model.put("activityList", code.getRetval());
		}
	}
	
	//�ܱ����ֳ���
	private void getVenue(Theatre theatre, ModelMap model){
		//�˶�
		Integer walaSportCount = 0;
		Sport sport = placeService.getZbPlace(Sport.class, theatre.getCountycode(), theatre.getIndexareacode());
		if(sport != null){
			//commentService.getCommentCountByRelatedId(TagConstant.TAG_SPORT, sport.getId())
			 walaSportCount = commonService.getCommentCount().get(sport.getId()+TagConstant.TAG_SPORT);
		}
		model.put("walaSportCount", walaSportCount);
		model.put("zbsport", sport);
		
		//����
		Integer walaGymCount = 0;
		ErrorCode<List<RemoteGym>> code = synchGymService.getGymList(theatre.getCitycode(), theatre.getCountycode(), theatre.getIndexareacode(), "clickedtimes", false, 0, 1);
		if(code.isSuccess() && !code.getRetval().isEmpty()){
			RemoteGym gym = code.getRetval().get(0);
			if(gym != null){
				walaGymCount = commonService.getCommentCount().get(gym.getId()+TagConstant.TAG_GYM);
			}
			model.put("walaGymCount", walaGymCount);
			model.put("zbgym", gym);
		}
		
		//��Ժ
		Integer walaTheatreCount = 0;
		Theatre theatres = placeService.getZbPlace(Theatre.class, theatre.getCountycode(), theatre.getIndexareacode());
		if(theatres != null){
			walaTheatreCount = commonService.getCommentCount().get(theatres.getId()+TagConstant.TAG_THEATRE);
		}
		model.put("walaTheatreCount", walaTheatreCount);
		model.put("zbdrama", theatres);
		
		//��ӰԺ
		Integer walaCinemaCount = 0;
		Cinema cinema = placeService.getZbPlace(Cinema.class, theatre.getCountycode(), theatre.getIndexareacode());
		if(cinema != null){
			walaCinemaCount = commonService.getCommentCount().get(cinema.getId()+TagConstant.TAG_CINEMA);
		}
		model.put("walaCinemaCount", walaCinemaCount);
		model.put("zbmovie", cinema);
	}
	
	//��������ҳ
	@RequestMapping("/theatre/theatreDetail.xhtml")
	public String newTheatreDetail_new(Long tid, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		Theatre theatre = daoService.getObject(Theatre.class, tid);
		if(theatre == null) return show404(model, "����ʵĻ��糡�ݲ����ڣ�");
		model.put("theatre", theatre);
		String citycode = theatre.getCitycode();
		WebUtils.setCitycode(request, citycode, response);
		//���û������� 
		cacheDataService.getAndSetIdsFromCachePool(Theatre.class, tid);
		cacheDataService.getAndSetClazzKeyCount(Theatre.class, tid);
		if(pageCacheService.isUseCache(request)){
			PageParams params = new PageParams();
			params.addLong("tid", tid);
			PageView pageView = pageCacheService.getPageView(request, "theatre/theatreDetail.xhtml", params, citycode);
			if(pageView != null){
				model.put("pageView", pageView);
				return "pageView.vm";
			}
		}
		List<Drama> dramaInfos = new ArrayList<Drama>();
		List<Drama> bookList = new ArrayList<Drama>();
		//��������
		List<Drama> dramaList = dramaService.getCurPlayDramaList(tid, 0, 20);
		dramaInfos.addAll(dramaList);
		List<Drama> dramaNewList = dramaService.getCurDramaList(tid,"enddate",0,5);
		model.put("dramaNewList", dramaNewList);
		dramaInfos.addAll(dramaNewList);
		getMemLast(theatre.getId(),model);
		putTheatreInfoStatic(citycode,theatre,dramaInfos, bookList, model);
		Collection<Drama> intersectionList = CollectionUtils.intersection(dramaList, bookList);
		dramaList.removeAll(intersectionList);
		Collections.sort(dramaList, new PropertyComparator("enddate", false, true));
		dramaList.addAll(0, intersectionList);
		model.put("dramaList", dramaList);
		model.put("subs","index");
		return "drama/wide_theatreDetail.vm";
	}
	
	//��ȡ�ݳ��ĵ��ݺ���Ա�б�
	private void getDDList(List<Drama> dramaIds,ModelMap model){
		Map<Long, List<DramaStar>> dramaStarListMap= new HashMap<Long, List<DramaStar>>();
		Map<Long, List<DramaStar>> dramaDirectorListMap= new HashMap<Long, List<DramaStar>>();
		for(Drama drama:dramaIds){
			if(StringUtils.isNotBlank(drama.getActors())){
				List<Long> actorsIdList = BeanUtil.getIdList(drama.getActors(), ",");
				List<DramaStar> actorsList = daoService.getObjectList(DramaStar.class, actorsIdList);
				dramaStarListMap.put(drama.getId(), actorsList);
			}
			if(StringUtils.isNotBlank(drama.getDirector())){
				List<Long> directorIdList = BeanUtil.getIdList(drama.getDirector(), ",");
				List<DramaStar> directorsList = daoService.getObjectList(DramaStar.class, directorIdList);
				List<DramaStar> newDirectorsList = new ArrayList<DramaStar>();
				for(DramaStar dramaStar : directorsList){
					if(!StringUtils.contains(drama.getDirector(), dramaStar.getName())){
						newDirectorsList.add(dramaStar);
					}
				}
				dramaDirectorListMap.put(drama.getId(), directorsList);
			}
		}
		model.put("dramaStarListMap", dramaStarListMap);
		model.put("dramaDirectorListMap", dramaDirectorListMap);
	}
	//������û������
	private void getMemLast(Long theatreId,ModelMap model){
		List<Map> payMemberList = untransService.getPayMemberListByTagAndId(TagConstant.TAG_THEATRE,theatreId, 0, 8);
		if(payMemberList.size() == 8){
			model.put("payMemberList", payMemberList);
			Map<String, Map> memberMap = new HashMap<String, Map>();
			for(Map order : payMemberList){
				memberMap.put(order.get("tradeNo") + "", memberService.getCacheMemberInfoMap((Long) order.get("memberid")));
			}
			model.put("memberMap", memberMap);
		}
	}
	//������ӳ�ݳ�����,������
	private void getheadInfoNew(Long tid, ModelMap model){
		int playCount = dramaService.getCurPlayDramaCount(tid);
		model.put("playCount", playCount);
		//commentService.getCommentCountByRelatedId(TagConstant.TAG_THEATRE, tid)
		Integer commentCount = commonService.getCommentCount().get(tid+TagConstant.TAG_THEATRE);
		if(commentCount == null){
			commentCount = 0;
		}
		model.put("commentCount", commentCount);
	}
	//ͳһ��ȡ�ݳ����Ƿ��ѡ��,�Ƿ����Ʊ,�Լ��ݳ��ļ۸�
	private void putDramaInfo(String citycode, List<Long> dramaList, ModelMap model){
		Map<Long, List<Theatre>> theatreMap = new HashMap<Long, List<Theatre>>();
		Map<Long, List<Integer>> dramaPriceMap = new HashMap<Long, List<Integer>>();
		//TODO �п��ٰ�ѭ����Ĳ�ѯ�ó��� �������ע�͵��ķ�������
		for(Long dramaId : dramaList){
			theatreMap.put(dramaId, dramaPlayItemService.getTheatreList(citycode, dramaId, false, 2));//��Ϊ�漰����������,����Ҫ�����Ƿ�Ҫ�����ݿ���ֱ��ȡ,����ʱ����
			dramaPriceMap.put(dramaId, dramaPlayItemService.getPriceList(null, dramaId, DateUtil.getCurFullTimestamp(), null, false));
		}
		model.put("theatreMap", theatreMap);
		model.put("priceListMap",dramaPriceMap);
	}
	//��ȡ�����ܱ���Ϣ
	private void getVenueNew(Theatre theatre, ModelMap model){
		List<BaseInfo> zbs = new ArrayList<BaseInfo>();
		//�˶�
		Sport sport = placeService.getZbPlace(Sport.class, theatre.getCountycode(), theatre.getIndexareacode());
		zbs.add(sport);
		//��Ժ
		Theatre theatres = placeService.getZbPlace(Theatre.class, theatre.getCountycode(), theatre.getIndexareacode());
		zbs.add(theatres);
		//��ӰԺ
		Cinema cinema = placeService.getZbPlace(Cinema.class, theatre.getCountycode(), theatre.getIndexareacode());
		zbs.add(cinema);
		model.put("zb", zbs);
	}
	//��������ҳ�Ĺ���������Ϣ
	private void putTheatreInfoStatic(String citycode,Theatre theatre,List<Drama> dramaInfos, final List<Drama> bookList, ModelMap model){
		Long tid = theatre.getId();
		//���ݷ����б�
		List<TheatreField> fieldList = daoService.getObjectListByField(TheatreField.class, "theatreid", theatre.getId());
		model.put("fieldList", fieldList);
		model.put("dramaTypeList", dramaService.getDramaTypeList(citycode));
		//��ԺͼƬ
		List<Picture> picList = pictureService.getPictureListByRelatedid(TagConstant.TAG_THEATRE, theatre.getId(),-1,-1);
		model.put("pictureList",picList);
		model.put("picCount", picList.size());		
		//��ӳ��Ŀ��ȡ��ǰ�ɹ�Ʊ����Ʊ��������Ŀչʾ
		List<Long> openseatList = openDramaService.getCurDramaidList(citycode, OdiConstant.OPEN_TYPE_SEAT);
		List<Long> bookingList = openDramaService.getCurDramaidList(citycode);
		model.put("openseatList",openseatList);
		model.put("bookingList", bookingList);
		List<Drama> bookDramaList = daoService.getObjectList(Drama.class, bookingList);
		bookList.addAll(bookDramaList);
		Collections.sort(bookDramaList, new PropertyComparator("boughtcount", false, false));
		List<Drama> interestDramaList = BeanUtil.getSubList(bookDramaList, 0, 4);
		model.put("hotDramaList",interestDramaList);
		dramaInfos.addAll(interestDramaList);
		model.put("markData", markService.getMarkdata(TagConstant.TAG_DRAMA));
		model.put("markHelper", new MarkHelper());
		//�жϳ����Ƿ������ѡ��
		Integer odiCount = openDramaService.getOdiCountByTheatreid(theatre.getId(), null, OdiConstant.OPEN_TYPE_SEAT);
		if(odiCount > 0) model.put("isSeat",true);
		else model.put("isSeat",false);
		//�жϳ���ȡƱ��ʽ
		TheatreProfile tp = daoService.getObject(TheatreProfile.class,tid);
		if(tp!=null)
			model.put("isTakeMethod",tp.getTakemethod());
		getheadInfoNew(tid, model);
		getTheatreNews(citycode, tid, model);
		getActivityList(citycode, tid, model);	
		getVenueNew(theatre, model);
		List<Long> dramaIds = BeanUtil.getBeanPropertyList(dramaInfos, Long.class,"id",true);
		putDramaInfo(citycode, dramaIds, model);
		getDDList(dramaInfos,model);
		model.put("dramaTypeMap", DramaConstant.dramaTypeMap);
	}
	//���
	@RequestMapping("/theatre/theatreIntroduce.xhtml")
	public String newTheatreIntroduce_new(Long tid, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		Theatre theatre = daoService.getObject(Theatre.class, tid);
		if(theatre == null) return show404(model, "����ʵĻ��糡�ݲ����ڣ�");
		model.put("theatre", theatre);
		String citycode = theatre.getCitycode();
		WebUtils.setCitycode(request, citycode, response);
		List<Drama> bookList = new ArrayList<Drama>();
		putTheatreInfoStatic(citycode,theatre,new ArrayList<Drama>(), bookList, model);
		model.put("subs","introduce");
		return "drama/wide_theatreDetail.vm";
	}
	
	//����
	@RequestMapping("/theatre/commentList.xhtml")
	public String newCommentList_new(Long tid,
				HttpServletResponse response, HttpServletRequest request, ModelMap model){
		Theatre theatre = daoService.getObject(Theatre.class, tid);
		if(theatre == null) return show404(model, "����ʵĻ��糡�ݲ����ڣ�");
		model.put("theatre", theatre);
		String citycode = theatre.getCitycode();
		WebUtils.setCitycode(request, citycode, response);
		List<Drama> bookList = new ArrayList<Drama>();
		putTheatreInfoStatic(citycode,theatre,new ArrayList<Drama>(), bookList, model);
		model.put("subs","wala");
		return "drama/wide_theatreDetail.vm";
	}
}
