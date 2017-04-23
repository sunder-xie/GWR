package com.gewara.web.action.drama;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.OdiConstant;
import com.gewara.constant.TagConstant;
import com.gewara.constant.content.SignName;
import com.gewara.json.PageView;
import com.gewara.model.bbs.qa.GewaAnswer;
import com.gewara.model.bbs.qa.GewaQuestion;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.content.HeadInfo;
import com.gewara.model.drama.Drama;
import com.gewara.model.drama.Theatre;
import com.gewara.pay.CalendarUtil;
import com.gewara.service.bbs.CommuService;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.PageCacheService;
import com.gewara.untrans.PageParams;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.RelatedHelper;
import com.gewara.util.WebUtils;
import com.gewara.xmlbind.bbs.Comment;

@Controller
public class DramaIndexController extends BaseDramaController {
	
	@Autowired@Qualifier("pageCacheService")
	private PageCacheService pageCacheService;
	public void setPageCacheService(PageCacheService pageCacheService) {
		this.pageCacheService = pageCacheService;
	}
	@Autowired@Qualifier("commuService")
	protected CommuService commuService;
	public void setCommuService(CommuService commuService) {
		this.commuService = commuService;
	}
	
	//����
	private void getCommentList(ModelMap model){
		List<Comment> commentList = commentService.getCommentListByTags(new String[]{"drama"}, null, true, 0, 5);
		addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(commentList));
		model.put("commentList", commentList);
		Map<Long, Comment> tranferCommentMap = new HashMap<Long, Comment>();// ת������
		for (Comment comment : commentList) {
			if (comment.getTransferid() != null) {
				Comment c = commentService.getCommentById(comment.getTransferid());
				if (c != null && StringUtils.isNotBlank(c.getBody())) {
					tranferCommentMap.put(c.getId(), c);
				}
			}
		}
		model.put("tranferCommentMap", tranferCommentMap);
	}
	
	@RequestMapping("/ajax/drama/dramaCalendar.xhtml")
	public String dramaCalendar(String date, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		String citycode = WebUtils.getAndSetDefault(request, response);
		Date date2 = DateUtil.parseDate(date);
		List<Drama> curDramaList = dramaService.getCurDramaByDate(DateUtil.getBeginTimestamp(date2), citycode, "hotvalue", 0, 20);
		model.put("curDramaList", curDramaList);
		Map<Long, List<Theatre>> theatreMap = new HashMap<Long, List<Theatre>>();
		Map<Long, List<Integer>> dramaPriceMap = new HashMap<Long, List<Integer>>();
		Map<Long, Boolean> bookingMap = new HashMap<Long, Boolean>();
		for(Drama drama : curDramaList){
			theatreMap.put(drama.getId(), dramaPlayItemService.getTheatreList(citycode, drama.getId(), false, 2));
			dramaPriceMap.put(drama.getId(), dramaPlayItemService.getPriceList(null, drama.getId(), DateUtil.getCurFullTimestamp(), null, false));
			bookingMap.put(drama.getId(), dramaPlayItemService.isBookingByDramaId(drama.getId()));
		}
		model.put("theatreMap", theatreMap);
		model.put("dramaPriceMap", dramaPriceMap);
		model.put("bookingMap", bookingMap);
		return "drama/ajax_dramaList.vm";
	}
	@RequestMapping("/drama/index.xhtml")
	public String index_new(ModelMap model, HttpServletRequest request, HttpServletResponse response){
		String citycode = WebUtils.getAndSetDefault(request, response);
		List<Long> dramaList = new ArrayList<Long>();
		if(pageCacheService.isUseCache(request)){//��ʹ�û���
			PageParams params = new PageParams();
			PageView pageView = pageCacheService.getPageView(request, "drama/index.xhtml", params, citycode);
			if(pageView!=null){
				model.put("pageView", pageView);
				return "pageView.vm";
			}
		}
		RelatedHelper rh = new RelatedHelper();
		model.put("relatedHelper", rh);
		model.putAll(getHeadData(citycode));
		getBigPic(citycode,model);
		getHotKey(citycode,model);
		getDrama(citycode,model,rh,dramaList);
		getAlDrama(citycode,model,rh,dramaList);
		getNewSellDrama(citycode,model,dramaList);
		getSubject(citycode,model);
		getNews(citycode,model,rh);
		getActivity(citycode,model,rh);
		getStar(citycode,model,rh);
		getHotdiary(citycode,model,rh);
		//getDramaByTime(citycode,model,DateUtil.currentTime(),dramaList);
		getRecruitDiary(citycode,model);
		getHotDrame(citycode,model,dramaList);
		getTheatre(citycode,model,rh);
		getShowCommentList(model);
		getDiaryList(citycode,model,rh);
		getQuestion(citycode,model);
		putDramaInfo(citycode,dramaList,model);
		return "drama/wide_index.vm";
	}
	
	//ͷ����ͷ
	private Map getHeadData(String citycode){
		Map model = new HashMap();
		List<GewaCommend> gcHeadList = commonService.getGewaCommendList(citycode, null, SignName.DRAMA_HEADINFO, null, HeadInfo.TAG, true, true, 0, 1);
		HeadInfo headInfo = null;
		if(!gcHeadList.isEmpty()){
			headInfo = daoService.getObject(HeadInfo.class, gcHeadList.get(0).getRelatedid());
		}
		model.put("headInfo",headInfo);
		return model;
	}
	
	//��ҳ��ͼ��ȡ
	private void getBigPic(String citycode, ModelMap model){
		List<GewaCommend> picList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_BIGPIC, null, null, true, 0, 8);
		model.put("picList", picList);
	}
	//��ҳ�ؼ���ȡ
	private void getHotKey(String citycode,ModelMap model){
		List<GewaCommend> keyList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_HOTKEY, null, null, true, 0, 5);
		model.put("keyList",keyList);
	}
	//��ҳ�Ƽ���Ŀ��ȡ
	private void getDrama(String citycode,ModelMap model,RelatedHelper rh,List<Long> dramaIdList){
		List<GewaCommend> dramaList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_DRAMA, null, null, true, 0, 6);
		commonService.initGewaCommendList("dramaList", rh, dramaList);
		for(GewaCommend gc:dramaList)
			dramaIdList.add(gc.getRelatedid());
		model.put("dramaList",dramaList);
	}
	//��ҳ�����Ƽ���4����Ŀ��ȡ
	private void getAlDrama(String citycode,ModelMap model,RelatedHelper rh,List<Long> dramaIdList){
		List<GewaCommend> dramaAlList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_DRAMAALONE, null, null, true, 0, 4);
		commonService.initGewaCommendList("dramaAlList", rh, dramaAlList);
		for(GewaCommend gc:dramaAlList)
			dramaIdList.add(gc.getRelatedid());
		model.put("dramaAlList",dramaAlList);
	}
	//��ȡ�����ʼ��Ʊ��4����Ŀ(������Ʊ���ο���ʱ��ȡ������۵�4����Ŀ)
	private void getNewSellDrama(String citycode,ModelMap model,List<Long> dramaIdList){
		List<Drama> newSellDramaList = dramaService.getDramaListLastOpenTime(citycode, 0, 4);
		for(Drama dr:newSellDramaList)
			dramaIdList.add(dr.getId());
		model.put("newSellDramaList",newSellDramaList);
	}
	//��ҳר���Ƽ���ȡ
	private void getSubject(String citycode,ModelMap model){
		List<GewaCommend> subjectList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_SUBJECT, null, null, true, 0, 1);
		//TODO ��Ҫ���븱����
		if(subjectList.size()>0)model.put("subjectList",subjectList.get(0));
	}
	//��ҳ��Ѷ�Ƽ���ȡ
	private void getNews(String citycode,ModelMap model,RelatedHelper rh){
		List<GewaCommend> newsList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_NEWS, null, null, true, 0, 3);
		commonService.initGewaCommendList("newsList", rh, newsList);
		model.put("newsList",newsList);
	}
	//��ҳ�Ƽ����ȡ
	private void getActivity(String citycode,ModelMap model,RelatedHelper rh){
		List<GewaCommend> activityList = commonService.getGewaCommendList(citycode,null,SignName.SHOWINDEX_ACTIVITY, null, null, true,true, 0, 4);
		commonService.initGewaCommendList("activityList", rh, activityList);
		model.put("activityList",activityList);
	}
	//��ҳ�Ƽ���������ǻ�ȡ
	private void getStar(String citycode,ModelMap model,RelatedHelper rh){
		List<GewaCommend> starList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_STAR, null, null, true, 0, 6);
		commonService.initGewaCommendList("starList", rh, starList);
		model.put("starList",starList);
		Map<Long, List<Drama>> starDramaCountMap = new HashMap<Long,List<Drama>>();
		for(GewaCommend commend : starList){
			Long starId = commend.getRelatedid();
			starDramaCountMap.put(starId, dramaToStarService.getDramaListByStarid(starId, true,-1,-1));
		}
		model.put("starDramaCountMap", starDramaCountMap);
	}
	//��ҳ�������ۻ�ȡ
	private void getHotdiary(String citycode,ModelMap model,RelatedHelper rh){
		List<GewaCommend> diaryList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_HOTDIARY, null, null, true, 0, 8);
		commonService.initGewaCommendList("diaryList", rh, diaryList);
		Map<String, Integer> diaryCountMap = commonService.getDiaryCount();
		model.put("diaryConutMap", diaryCountMap);
		model.put("diaryList",diaryList);
	}
	//��ȡĳ�յ������ݳ�
	private void getDramaByTime(String citycode, Date monthDay, ModelMap model){
		Map<String, Integer> dramaCountMap = dramaService.getMonthDramaCount(citycode, monthDay);
		int year = DateUtil.getYear(monthDay);
		int month = DateUtil.getMonth(monthDay);
		CalendarUtil calendarUtil = new CalendarUtil(year, month);
		model.put("calendarUtil", calendarUtil);
		model.put("dramaCountMap", dramaCountMap);
	}
	//��̨�Ƽ�׷���Ż�ȡ
	private void getRecruitDiary(String citycode,ModelMap model){
		List<GewaCommend> recruitDiaryList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_RECRUIT_DIARY, null, null, true, 0, 4);
		model.put("recruitDiaryList",recruitDiaryList);
	}
	//��ҳ������Ŀ��ȡ
	private void getHotDrame(String citycode,ModelMap model,List<Long> dramaIdList){
		List<Drama> hotDramaList = dramaService.getDramaListByMonthOpenDramaItem(citycode, false, 10);
		for(Drama dr:hotDramaList)
			dramaIdList.add(dr.getId());
		model.put("hotDramaList",hotDramaList);
	}
	//��ҳ���ų��ݻ�ȡ
	private void getTheatre(String citycode,ModelMap model,RelatedHelper rh){
		List<GewaCommend> theatreList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_THEATRE, null, null, true, 0, 5);
		commonService.initGewaCommendList("theatreList", rh, theatreList);
		Map<Long, Integer> curDramaCountMap = new HashMap<Long, Integer>();
		for(GewaCommend commend : theatreList){
			Long theatreid = commend.getRelatedid();
			curDramaCountMap.put(theatreid, dramaService.getCurPlayDramaCount(theatreid));
		}
		model.put("curDramaCountMap", curDramaCountMap);
		model.put("theatreList",theatreList);
	}
	//��ҳ�ݳ�������ȡ
	private void getShowCommentList(ModelMap model){
		getCommentList(model);
	}
	//��ȡ���µĹ����ݳ���֪��
	private void getQuestion(String citycode,ModelMap model){
		List<GewaQuestion> questionList = qaService.getQuestionByQsAndTagList(citycode, "", TagConstant.TAG_THEATRE, "modtime",5);
		Map<Long,GewaAnswer> answers = new HashMap<Long,GewaAnswer>();
		for(GewaQuestion gq:questionList){
			GewaAnswer ga = qaService.getBestAnswerByQuestionid(gq.getId());
			if(ga==null){
				List<GewaAnswer> gaList = qaService.getAnswerListByQuestionid(gq.getId());
				if(!gaList.isEmpty()){
					ga = gaList.get(gaList.size() - 1);
				}
			}
			answers.put(gq.getId(),ga);
		}
		addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(answers.values()));
		model.put("questionAnswer",answers);
		model.put("questionList",questionList);
	}
	//��̨�Ƽ��ݳ������ӻ�ȡ
	private void getDiaryList(String citycode, ModelMap model,RelatedHelper rh){
		List<GewaCommend> dramaDiaryList = commonService.getGewaCommendList(citycode, SignName.SHOWINDEX_DIARY, null, null, true, 0, 10);
		commonService.initGewaCommendList("dramaDiaryList", rh, dramaDiaryList);
		model.put("dramaDiaryList", dramaDiaryList);
	}
	//ͳһ��ȡ�ݳ����Ƿ��ѡ��,�Ƿ����Ʊ,�Լ��ݳ��ļ۸�
	private void putDramaInfo(String citycode, List<Long> dramaList,ModelMap model){
		Map<Long, List<Theatre>> theatreMap = new HashMap<Long, List<Theatre>>();
		Map<Long, List<Integer>> dramaPriceMap = new HashMap<Long, List<Integer>>();
		List<Long> bookingList = openDramaService.getCurDramaidList(citycode);
		//TODO �п��ٰ�ѭ����Ĳ�ѯ�ó��� �������ע�͵��ķ�������
		for(Long dramaId : dramaList){
			theatreMap.put(dramaId, dramaPlayItemService.getTheatreList(citycode, dramaId, false, 2));
			//��Ϊ�漰����������,����Ҫ�����Ƿ�Ҫ�����ݿ���ֱ��ȡ,����ʱ����
			dramaPriceMap.put(dramaId, dramaPlayItemService.getPriceList(null, dramaId, DateUtil.getCurFullTimestamp(), null, false));
		}
		List<Long> openSeatList = openDramaService.getCurDramaidList(citycode, OdiConstant.OPEN_TYPE_SEAT); 
		model.put("openSeatList", openSeatList);
		model.put("theatreMap", theatreMap);
		model.put("dramaPriceMap",dramaPriceMap);
		model.put("bookingList", bookingList);
	}
	@RequestMapping("/drama/ajax/dramaMonthCalendar.xhtml")
	public String dramaMonthCalendar(String ycrq, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		String citycode = WebUtils.getAndSetDefault(request, response);
		Date playdate = null, monthDate = DateUtil.getMonthFirstDay(DateUtil.getCurDate());
		if(!DateUtil.isValidDate(ycrq)){
			ycrq = DateUtil.formatDate(monthDate);
			playdate = monthDate;
		}else{
			playdate = DateUtil.getMonthFirstDay(DateUtil.parseDate(ycrq));
			if(playdate.before(monthDate)){
				playdate = monthDate;
				ycrq = DateUtil.formatDate(playdate);
			}
		}
		Date monthMinDate = dramaService.getDramaMinMonthDate(citycode);
		if(monthMinDate != null && playdate.before(monthMinDate)){
			playdate = monthMinDate;
			ycrq = DateUtil.formatDate(playdate);
		}
		if(pageCacheService.isUseCache(request)){//��ʹ�û���
			PageParams params = new PageParams();
			params.addDateStr("ycrq", ycrq);
			PageView pageView = pageCacheService.getPageView(request, "drama/ajax/dramaMonthCalendar.xhtml", params, citycode);
			if(pageView!=null){
				model.put("pageView", pageView);
				return "pageView.vm";
			}
		}
		getDramaByTime(citycode, playdate, model);
		return "include/drama/mod_dramaMonthCalender.vm";
	}
	
	@RequestMapping("/drama/ajax/dramaList.xhtml")
	public String dramaList(String ycrq, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		String citycode = WebUtils.getAndSetDefault(request, response);
		Date playdate = null, curDate = DateUtil.getCurDate();
		if(!DateUtil.isValidDate(ycrq)){
			ycrq = DateUtil.formatDate(curDate);
			playdate = curDate;
		}else{
			playdate = DateUtil.parseDate(ycrq);
			if(playdate.before(curDate)){
				playdate = curDate;
			}
		}
		if(pageCacheService.isUseCache(request)){//��ʹ�û���
			PageParams params = new PageParams();
			params.addDateStr("ycrq", ycrq);
			PageView pageView = pageCacheService.getPageView(request, "drama/ajax/dramaList.xhtml", params, citycode);
			if(pageView!=null){
				model.put("pageView", pageView);
				return "pageView.vm";
			}
		}
		WebUtils.getAndSetDefault(request, response);
		List<Drama> curDramaList = dramaService.getCurDramaByDate(citycode, DateUtil.getBeginTimestamp(playdate), "hotvalue", 0, 50);
		model.put("curDramaList", curDramaList);
		List<Long> dramaIdList = BeanUtil.getBeanPropertyList(curDramaList, "id", true);
		putDramaInfo(citycode, dramaIdList, model);
		
		return "include/drama/mod_dramaListCalender.vm";
	}
}
