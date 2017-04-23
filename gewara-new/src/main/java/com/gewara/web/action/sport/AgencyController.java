package com.gewara.web.action.sport;
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

import com.gewara.constant.GoodsConstant;
import com.gewara.constant.Status;
import com.gewara.constant.TagConstant;
import com.gewara.constant.content.SignName;
import com.gewara.json.PageView;
import com.gewara.model.agency.Agency;
import com.gewara.model.agency.AgencyToVenue;
import com.gewara.model.agency.Curriculum;
import com.gewara.model.agency.TrainingGoods;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.content.Picture;
import com.gewara.model.content.Video;
import com.gewara.model.drama.DramaStar;
import com.gewara.model.drama.DramaToStar;
import com.gewara.model.goods.GoodsPrice;
import com.gewara.model.sport.Sport;
import com.gewara.model.sport.SportItem;
import com.gewara.model.sport.SportProfile;
import com.gewara.model.user.Member;
import com.gewara.pay.CalendarUtil;
import com.gewara.service.bbs.MarkService;
import com.gewara.service.content.PictureService;
import com.gewara.service.content.VideoService;
import com.gewara.service.drama.DramaToStarService;
import com.gewara.service.order.GoodsService;
import com.gewara.service.sport.OpenTimeTableService;
import com.gewara.untrans.CacheDataService;
import com.gewara.untrans.CommonService;
import com.gewara.untrans.PageCacheService;
import com.gewara.untrans.PageParams;
import com.gewara.untrans.PictureComponent;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.util.PageUtil;

@Controller
public class AgencyController extends BaseSportController {
	
	@Autowired@Qualifier("dramaToStarService")
	private DramaToStarService dramaToStarService;
	@Autowired@Qualifier("pictureService")
	private PictureService pictureService;
	@Autowired@Qualifier("videoService")
	private VideoService videoService;
	@Autowired@Qualifier("commonService")
	private CommonService commonService;
	@Autowired@Qualifier("goodsService")
	private GoodsService goodsService;
	@Autowired@Qualifier("cacheDataService")
	private CacheDataService cacheDataService;
	@Autowired@Qualifier("markService")
	private MarkService markService;
	@Autowired@Qualifier("pictureComponent")
	private PictureComponent pictureComponent;
	@Autowired@Qualifier("pageCacheService")
	private PageCacheService pageCacheService;
	@Autowired@Qualifier("openTimeTableService")
	private OpenTimeTableService openTimeTableService;
	
	//������ϸҳ
	@RequestMapping("/sport/agencyDetail.xhtml")
	public String agencyDetail(Long id, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, id);
		if(agency == null) return show404(model, "δ�ҵ��˻�����");
		if(StringUtils.equals(agency.getStatus(), Status.N)) return show404(model, "�˻����ѱ�ɾ����");
		String citycode = WebUtils.getAndSetDefault(request, response);
		cacheDataService.getAndSetIdsFromCachePool(Agency.class, id);
		cacheDataService.getAndSetClazzKeyCount(Agency.class, id);
		//����
		List<DramaToStar> tempList = new ArrayList<DramaToStar>();
		List<DramaToStar> dtsList = dramaToStarService.getDramaToStarListByDramaid(TagConstant.TAG_AGENCY, agency.getId(), true);
		tempList.addAll(dtsList);
		//ͼƬ
		List<Picture> pictureList = pictureService.getPictureListByRelatedid(TagConstant.TAG_AGENCY, agency.getId(), 0, 4);
		model.put("pictureList", pictureList);
		//��Ƶ
		List<Video> videoList = videoService.getVideoListByTag(TagConstant.TAG_AGENCY, agency.getId(), 0, 3);
		model.put("videoList", videoList);
		//����
		List discountInfoList = commonService.getCurrentDiscountInfoByRelatedid(TagConstant.TAG_AGENCY, agency.getId());
		model.put("discountInfoList", discountInfoList);
		//��ѵ�γ�
		List<TrainingGoods> trainingGoodsList = agencyService.getTrainingGoodsList(citycode, TagConstant.TAG_AGENCY, agency.getId(), null, null, null, "goodssort", true, true, 0, 100);
		if(!trainingGoodsList.isEmpty()){
			Map<Long,List<DramaToStar>> tcDtsListMap = new HashMap<Long,List<DramaToStar>>();
			for (TrainingGoods trainingGoods : trainingGoodsList) {
				//����
				List<DramaToStar> tcDtsList = dramaToStarService.getDramaToStarListByDramaid(GoodsConstant.GOODS_TYPE_TRAINING, trainingGoods.getId(), false);
				tcDtsListMap.put(trainingGoods.getId(), tcDtsList);
				tempList.addAll(tcDtsList);
			}
			Map<Long, List<TrainingGoods>> tcListMap = BeanUtil.groupBeanList(trainingGoodsList, "itemid");
			List sportItemIdList = Arrays.asList(tcListMap.keySet().toArray());
			Map<Long, SportItem> sportItemMap = daoService.getObjectMap(SportItem.class, sportItemIdList);
			model.put("sportItemIdList", sportItemIdList);
			model.put("tcoursesListMap", tcListMap);
			model.put("sportItemMap", sportItemMap);
			model.put("tcDtsListMap", tcDtsListMap);
		}
		//����
		List<AgencyToVenue> atvList = agencyService.getATVList(agency.getId(), null);
		if(!atvList.isEmpty()){
			Map<Long, Sport> sportMap = new HashMap<Long, Sport>();
			Map<Long, List<SportItem>> itemListMap = new HashMap<Long, List<SportItem>>();
			for (AgencyToVenue atv : atvList) {
				if(sportMap.get(atv.getVenueId()) == null){
					sportMap.put(atv.getVenueId(), daoService.getObject(Sport.class, atv.getVenueId()));
				}
				if(itemListMap.get(atv.getVenueId()) == null){
					itemListMap.put(atv.getVenueId(), sportService.getSportItemListBySportId(atv.getVenueId(), SportProfile.STATUS_OPEN));
				}
			}
			model.put("itemListMap", itemListMap);
			model.put("sportMap", sportMap);
			model.put("atvList", atvList);
		}
		Map<String, Integer> commentMap = commonService.getCommentCount();
		int walaCount = 0;
		if(commentMap.get(agency.getId()+TagConstant.TAG_AGENCY) != null) walaCount = Integer.parseInt(commentMap.get(agency.getId()+TagConstant.TAG_AGENCY)+"");
		model.put("walaCount", walaCount);
		List<Long> starIdList = BeanUtil.getBeanPropertyList(tempList, Long.class, "starid", true);
		Map<Long,DramaStar> starMap = daoService.getObjectMap(DramaStar.class, starIdList);
		model.put("dtsList", dtsList);
		model.put("starMap", starMap);
		model.put("agency", agency);
		//���Ż���
		hotAgencyDate(citycode, model);
		model.putAll(markService.getPercentCount(TagConstant.TAG_AGENCY, agency.getId()));
		return "sport/agency/wide_agencyDetail.vm";
	}
	
	//�γ���ϸҳ
	@RequestMapping("/sport/curriculumDetail.xhtml")
	public String curriculumDetail(Long id, ModelMap model){
		TrainingGoods trainingGoods = daoService.getObject(TrainingGoods.class, id);
		if(trainingGoods == null) return show404(model, "δ�ҵ��˿γ̣�");
		if(!trainingGoods.hasBooking()) return show404(model, "�γ�δ���Ż��ѹرգ�");
		Agency agency = daoService.getObject(Agency.class, trainingGoods.getRelatedid());
		if(agency == null) return show404(model, "δ�ҵ��˻�����");
		if(StringUtils.equals(agency.getStatus(), Status.N)) return show404(model, "�˻����ѱ�ɾ����");
		cacheDataService.getAndSetIdsFromCachePool(TrainingGoods.class, id);
		cacheDataService.getAndSetClazzKeyCount(TrainingGoods.class, id);
		model.put("agencyCount", agencyService.getTrainingGoodsCount(agency.getCitycode(), TagConstant.TAG_AGENCY, agency.getId(), null, null, null, true));
		List<TrainingGoods> tcList = agencyService.getTrainingGoodsList(agency.getCitycode(), TagConstant.TAG_AGENCY, agency.getId(), null, null, null, "goodssort", true, true, 0, 3);
		tcList.remove(trainingGoods);
		model.put("tcList", tcList);
		model.put("agency", agency);
		//����
		if(trainingGoods.getPlaceid() != null){
			List<AgencyToVenue> atvList = agencyService.getATVList(agency.getId(), trainingGoods.getPlaceid());
			if(!atvList.isEmpty()){
				model.put("siList", sportService.getSportItemListBySportId(trainingGoods.getPlaceid(), SportProfile.STATUS_OPEN));
				model.put("sport", daoService.getObject(Sport.class, trainingGoods.getPlaceid()));
				model.put("sportType", atvList.get(0).getAgencytype());
				//ͼƬ
				List<Picture> pictureList = pictureService.getPictureListByRelatedid(TagConstant.TAG_SPORT, trainingGoods.getPlaceid(), 0, 4);
				model.put("pictureList", pictureList);
			}
		}
		//��Ŀ
		SportItem sportItem = daoService.getObject(SportItem.class, trainingGoods.getItemid());
		model.put("sportItem", sportItem);
		List<Long> sportIdList = openTimeTableService.getCurOttSportIdList(sportItem.getId(), trainingGoods.getCitycode());
		Integer bookingCount = 0;
		if(sportIdList != null && !sportIdList.isEmpty()){
			bookingCount = sportIdList.size();
		} 
		model.put("sportItemCount", bookingCount);
		Map<String, Integer> sportItemOpenCount = commonService.getSportItemSportCount();
		model.put("sportItemOpenCount", sportItemOpenCount);
		//����
		List<DramaToStar> tcDtsList = dramaToStarService.getDramaToStarListByDramaid(GoodsConstant.GOODS_TYPE_TRAINING, trainingGoods.getId(), false);
		List<Long> staridList = BeanUtil.getBeanPropertyList(tcDtsList, "starid", true);
		Map<Long, DramaStar> starMap = daoService.getObjectMap(DramaStar.class, staridList);
		model.put("starMap", starMap);
		model.put("tcDtsList", tcDtsList);
		//�۸�
		List<GoodsPrice> goodsPriceList = goodsService.getGoodsPriceList(trainingGoods.getId());
		model.put("goodsPriceList", goodsPriceList);
		model.put("trainingGoods", trainingGoods);
		return "sport/agency/wide_curriculumDetail.vm";
	}
	
	//�����б�ҳ��
	@RequestMapping("/sport/agencyList.xhtml")
	public String agencyList(String searchKey, String order, Integer pageNo, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		String citycode = WebUtils.getAndSetDefault(request, response);
		if(pageNo == null) pageNo = 0;
		int rowsPerpage = 8;
		int firstRow = pageNo * rowsPerpage;
		if(StringUtils.isBlank(order)) order = "isHot";
		PageParams params = new PageParams();
		params.addSingleString("searchKey", searchKey);
		params.addSingleString("order", order);
		params.addInteger("pageNo", pageNo);
		if(StringUtils.isBlank(searchKey) && pageCacheService.isUseCache(request)){
			PageView pageView = pageCacheService.getPageView(request, "sport/agencyList.xhtml", params, citycode);
			if (pageView != null) {
				model.put("pageView", pageView);
				return "pageView.vm";
			}
		}
		//��ർ��
		this.setheadData(citycode, model);
		//���Ͻ�bannerλ
		List<GewaCommend> bannerGclist = commonService.getGewaCommendList(citycode, SignName.SPORTLIST_GEWAACTIVITY, null, null, true,0, 4);
		model.put("bannerGclist", bannerGclist);
		int agencyCount = agencyService.getAgencyCount(searchKey, citycode);
		if(agencyCount > 0){
			List<Agency> agencyList = agencyService.getAgencyList(searchKey, citycode, order, false, firstRow, rowsPerpage);
			Map<Long, List<SportItem>> siListMap = new HashMap<Long, List<SportItem>>();
			for(Agency agency : agencyList){
				siListMap.put(agency.getId(), agencyService.getAgencySportItemList(agency.getId(), citycode));
			}
			model.put("siListMap", siListMap);
			model.put("agencyList", agencyList);
			
			PageUtil pageUtil = new PageUtil(agencyCount, rowsPerpage, pageNo, "sport/agencyList.xhtml", true, true);
			pageUtil.initPageInfo(params.getParams());
			model.put("pageUtil", pageUtil);
			Map<String, Integer> commentMap = commonService.getCommentCount();
			model.put("commentMap", commentMap);
		}
		model.put("agencyCount", agencyCount);
		//�Ҳ�
		rightDate(citycode, model);
		return "sport/agency/wide_agencyList.vm";
	}
	//�γ��б�ҳ
	@RequestMapping("/sport/curriculumList.xhtml")
	public String curriculumList(Long servicetype, String countycode, String indexareacode, String timetype, String fitcrowd, String searchKey,
			Integer fromprice, Integer toprice, String order, Integer pageNo, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		String citycode = WebUtils.getAndSetDefault(request, response);
		if(pageNo == null) pageNo = 0;
		int rowsPerpage = 10;
		int firstRow = pageNo * rowsPerpage;
		//����
		PageParams params = new PageParams();
		params.addLong("servicetype", servicetype);
		params.addSingleString("countycode", countycode);
		params.addSingleString("indexareacode", indexareacode);
		params.addSingleString("timetype", timetype);
		params.addSingleString("fitcrowd", fitcrowd);
		params.addSingleString("searchKey", searchKey);
		params.addInteger("fromprice", fromprice);
		params.addInteger("toprice", toprice);
		params.addSingleString("order", order);
		params.addInteger("pageNo", pageNo);
		if(StringUtils.isBlank(searchKey) && fromprice == null && toprice == null && pageCacheService.isUseCache(request)){
			PageView pageView = pageCacheService.getPageView(request, "sport/curriculumList.xhtml", params, citycode);
			if (pageView != null) {
				model.put("pageView", pageView);
				return "pageView.vm";
			}
		}
		//��ർ��
		this.setheadData(citycode, model);
		//���Ͻ�bannerλ
		List<GewaCommend> bannerGclist = commonService.getGewaCommendList(citycode, SignName.SPORTLIST_GEWAACTIVITY, null, null, true,0, 6);
		model.put("bannerGclist", bannerGclist);
		List<Long> sportIdList = null;
		if(StringUtils.isNotBlank(countycode) || StringUtils.isNotBlank(indexareacode)) sportIdList = sportService.getSportIdByCode(citycode, countycode, indexareacode, 0, 200);
		int curriculumCount = agencyService.getTrainingGoodsCount(citycode, null, servicetype, fitcrowd, timetype, sportIdList, fromprice, toprice, searchKey);
		if(curriculumCount > 0){
			boolean asc = false;
			if(StringUtils.equals(order, "minprice")) asc = true;
			List<TrainingGoods> tcGoodsList = agencyService.getTrainingGoodsList(citycode, null, servicetype, fitcrowd, timetype, sportIdList, fromprice, toprice, searchKey, order, asc, firstRow, rowsPerpage);
			Map<Long, Sport> sportMap = new HashMap<Long, Sport>();
			Map<Long, Agency> agencyMap = new HashMap<Long, Agency>();
			for (TrainingGoods tc : tcGoodsList) {
				if(tc.getPlaceid() != null && sportMap.get(tc.getPlaceid()) == null) sportMap.put(tc.getPlaceid(), daoService.getObject(Sport.class, tc.getPlaceid()));
				if(agencyMap.get(tc.getRelatedid()) == null)	agencyMap.put(tc.getRelatedid(), daoService.getObject(Agency.class, tc.getRelatedid()));
			}
			
			PageUtil pageUtil = new PageUtil(curriculumCount, rowsPerpage, pageNo, "sport/curriculumList.xhtml", true, true);
			pageUtil.initPageInfo(params.getParams());
			model.put("pageUtil", pageUtil);
			model.put("tcGoodsList", tcGoodsList);
			model.put("sportMap", sportMap);
			model.put("agencyMap", agencyMap);
		}
		model.put("curriculumCount", curriculumCount);
		//�˶���Ŀ
		List<SportItem> sportItemList = sportService.getTopSportItemList();
		model.put("sportItemMap", BeanUtil.beanListToMap(sportItemList, "id"));
		model.put("sportItemList", sportItemList);
		//����
		List<Map> countyGroup = placeService.getPlaceCountyCountMap(Sport.class, citycode);
		model.put("countyGroup",countyGroup);
		//��Ȧ
		if(StringUtils.isNotBlank(countycode)){
			List indexareaGroup = new ArrayList();
			indexareaGroup = placeService.getPlaceIndexareaCountMap(Sport.class, countycode);
			model.put("indexareaGroup",indexareaGroup);		
		}
		//�Ҳ�
		rightDate(citycode, model);
		return "sport/agency/wide_curriculumList.vm";
	}
	//����ͼƬ�б�
	@RequestMapping("/sport/agencyPictureList.xhtml")
	public String agencyPictureList(Long agencyId, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, agencyId);
		if(agency == null) return show404(model, "δ�ҵ��˻�����");
		String citycode = WebUtils.getAndSetDefault(request, response);
		hotAgencyDate(citycode, model);
		model.put("agency", agency);
		return "sport/agency/pictureList.vm";
	}
	//����ͼƬ��ϸ
	@RequestMapping("/sport/agencyPictureDetail.xhtml")
	public String agencyPictureDetail(@CookieValue(value = LOGIN_COOKIE_NAME, required = false)String sessid,
			Long agencyId, Long pid, String pvtype, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, agencyId);
		if(agency == null) return show404(model, "δ�ҵ��˻�����");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member != null){
			addCacheMember(model, member.getId());
			model.put("loginMember", member);
		}
		pictureComponent.pictureDetail(model, TagConstant.TAG_AGENCY, agencyId, pid, pvtype);
		String citycode = WebUtils.getAndSetDefault(request, response);
		hotAgencyDate(citycode, model);
		model.put("agency", agency);
		return "sport/agency/pictureDetail.vm";
	}
	@RequestMapping("/sport/ajax/agencyPictureList.xhtml")
	public String agencyPictureList(Long relatedid, Integer pageNo, String type, ModelMap model) {
		Agency agency = daoService.getObject(Agency.class, relatedid);
		if(agency == null) return show404(model, "δ�ҵ��˻�����");
		pictureComponent.pictureList(model, pageNo, TagConstant.TAG_AGENCY, relatedid, type, "/sport/ajax/agencyPictureList.xhtml");
		model.put("agency", agency);
		return "sport/agency/ajaxPictureList.vm";
	}
	//�γ̱�
	@RequestMapping("/sport/ajax/getCurriculumCalendar.xhtml")
	public String getCurriculumCalendar(Long tid, Date playDate, ModelMap model){
		TrainingGoods trainingGoods = daoService.getObject(TrainingGoods.class, tid);
		if(trainingGoods == null) return show404(model, "δ�ҵ��˿γ̣�");
		if(playDate == null) playDate = DateUtil.currentTime();
		int year = DateUtil.getYear(playDate);
		int month = DateUtil.getMonth(playDate);
		CalendarUtil calendarUtil = new CalendarUtil(year, month);
		List<Curriculum> curriculumList = agencyService.getCurriculumList(tid, playDate);
		if(!curriculumList.isEmpty()){
			
		}
		model.put("curriculumList", curriculumList);
		model.put("calendarUtil", calendarUtil);
		model.put("playDate", playDate);
		model.put("trainingGoods", trainingGoods);
		return "sport/agency/ajaxTimegs.vm";
	}
	//������Ƶ
	@RequestMapping("/sport/agencyVideoDetail.xhtml")
	public String agencyVideoDetail(@CookieValue(value = LOGIN_COOKIE_NAME, required = false)String sessid,
			Long agencyId, Long vid, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, agencyId);
		if(agency == null) return show404(model, "δ�ҵ��˻�����");
		String citycode = WebUtils.getAndSetDefault(request, response);
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member != null){
			addCacheMember(model, member.getId());
			model.put("loginMember", member);
		}
		List<Video> videoList = videoService.getVideoListByTag(TagConstant.TAG_AGENCY, agency.getId(), 0, 100);
		if(vid != null){
			Video video = daoService.getObject(Video.class, vid);
			if(video == null) return show404(model, "����Ƶ�����ڻ���ɾ����");
			videoList.remove(video);
			videoList.add(0, video);
		}
		model.put("videoList", videoList);
		model.put("agency", agency);
		hotAgencyDate(citycode, model);
		return "sport/agency/videoDetail.vm";
	}
	private void rightDate(String citycode, ModelMap model){
		//�Ҳ�5����
		List<Sport> hotSportList = sportService.getHotSports(citycode, "generalmark", false, 5);
		model.put("hotSportList", hotSportList);
		//�Ҳ�3����ѵ�γ�
		List<TrainingGoods> trainingList = agencyService.getTrainingGoodsList(citycode, TagConstant.TAG_AGENCY, null, null, null, null, "goodssort", true, true, 0, 3);
		if(!trainingList.isEmpty()){
			Map<Long, Sport> sportMap = new HashMap<Long, Sport>();
			if(model.get("sportMap") != null) sportMap = (Map<Long, Sport>)model.get("sportMap");
			Map<Long, Agency> agencyMap = new HashMap<Long, Agency>();
			if(model.get("agencyMap") != null) agencyMap = (Map<Long, Agency>)model.get("agencyMap");
			for (TrainingGoods tc : trainingList) {
				if(tc.getPlaceid() != null && sportMap.get(tc.getPlaceid()) == null) sportMap.put(tc.getPlaceid(), daoService.getObject(Sport.class, tc.getPlaceid()));
				if(agencyMap.get(tc.getRelatedid()) == null)	agencyMap.put(tc.getRelatedid(), daoService.getObject(Agency.class, tc.getRelatedid()));
			}
			model.put("sportMap", sportMap);
			model.put("agencyMap", agencyMap);
			model.put("trainingList", trainingList);
		}
		hotAgencyDate(citycode, model);
	}
	//�Ҳ�3�����Ż���
	private void hotAgencyDate(String citycode, ModelMap model){
		List<Agency> hotAgencyList = agencyService.getAgencyList(null, citycode, "generalmark", false, 0, 3);
		if(!hotAgencyList.isEmpty()){
			Map<Long, List<TrainingGoods>> tcListMap = new HashMap<Long, List<TrainingGoods>>();
			Map<Long, Integer> tcCountMap = new HashMap<Long, Integer>();
			for (Agency agency : hotAgencyList) {
				tcCountMap.put(agency.getId(), agencyService.getTrainingGoodsCount(citycode, TagConstant.TAG_AGENCY, agency.getId(), null, null, null, true));
				tcListMap.put(agency.getId(), agencyService.getTrainingGoodsList(citycode, TagConstant.TAG_AGENCY, agency.getId(), null, null, null, "goodssort", true, true, 0, 2));
			}
			model.put("tcCountMap", tcCountMap);
			model.put("tcListMap", tcListMap);
			model.put("hotAgencyList", hotAgencyList);
		}
	}
}