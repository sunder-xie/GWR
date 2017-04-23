package com.gewara.web.action.drama;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.CookieConstant;
import com.gewara.constant.DramaConstant;
import com.gewara.constant.OdiConstant;
import com.gewara.constant.TagConstant;
import com.gewara.json.PageView;
import com.gewara.model.common.County;
import com.gewara.model.drama.Drama;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.drama.Theatre;
import com.gewara.model.drama.TheatreProfile;
import com.gewara.support.ErrorCode;
import com.gewara.support.MultiPropertyComparator;
import com.gewara.support.NullPropertyOrder;
import com.gewara.support.ReadOnlyTemplate;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.PageCacheService;
import com.gewara.untrans.PageParams;
import com.gewara.util.BeanUtil;
import com.gewara.util.ClassUtils;
import com.gewara.util.LongitudeAndLatitude;
import com.gewara.util.RelatedHelper;
import com.gewara.util.WebUtils;
import com.gewara.web.util.PageUtil;
import com.gewara.xmlbind.activity.RemoteActivity;
import com.gewara.xmlbind.bbs.Comment;

@Controller
public class NewTheatreController extends BaseDramaController {
	private static final Map<String, String> orderMap = new HashMap<String, String>();
	static{
		orderMap.put("avggeneral", "t.avggeneral");
		orderMap.put("clickedtimes", "t.clickedtimes");
		orderMap.put("boughtcount", "t.boughtcount");
		orderMap.put("diarycount", "t.diarycount");
	}
	public static final List<String> MYRANGE_LIST = Arrays.asList("500", "1000", "1500", "2000", "2500", "3000", "3500", "4000", "4500", "5000");

	@Autowired@Qualifier("readOnlyTemplate")
	private ReadOnlyTemplate readOnlyTemplate;
	public void setReadOnlyHibernateTemplate(ReadOnlyTemplate readOnlyTemplate) {
		this.readOnlyTemplate = readOnlyTemplate;
	}
	@Autowired@Qualifier("hibernateTemplate")
	private HibernateTemplate hibernateTemplate;
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	
	@Autowired@Qualifier("pageCacheService")
	private PageCacheService pageCacheService;
	public void setPageCacheService(PageCacheService pageCacheService) {
		this.pageCacheService = pageCacheService;
	}
	// ���ͼƬ
	@RequestMapping("/theatre/attachRelatePicture.xhtml")
	public String attachTheatrePicture(ModelMap model, Long relatedid, String tag){
		Map dataMap=pictureComponent.attachRelatePicture(tag, relatedid, "");
		model.putAll(dataMap);
		model.put("tag", tag);
		model.put("relatedid", relatedid);
		return "drama/theatre/attachRelatePicture.vm";
	}
	//��ͼģʽ
	@RequestMapping("/theatre/theatreMap.xhtml")
	public String theatreMap(@CookieValue(value=CookieConstant.MEMBER_POINT, required=false) String bpointxy,
			SearchTheatreCommand stc, HttpServletRequest request, ModelMap model, HttpServletResponse response) {
		String citycode = WebUtils.getAndSetDefault(request, response);
		String pointx ="", pointy="";
		if(StringUtils.isNotBlank(bpointxy)){
			List<String> pointList = Arrays.asList(StringUtils.split(bpointxy, ":"));
			if(pointList.size() == 2){
				pointx = pointList.get(0);
				pointy = pointList.get(1);
			}
		}
		stc.setCounty("true");
		model.put("pointx", pointx);
		model.put("pointy", pointy);
		List<Theatre> theatreListPoint = readOnlyTemplate.findByCriteria(getCriteria(stc, citycode));
		if(StringUtils.equals(stc.getBooking(), Theatre.BOOKING_OPEN)){
			List<Long> theatreIdList = dramaPlayItemService.getCurBookingTheatreList(citycode, null);
			theatreListPoint = getNearTheatreList(theatreListPoint, theatreIdList);
		}
		Map<String, List<Theatre>> countyTheatreMap = BeanUtil.groupBeanList(theatreListPoint, "countycode");
		model.put("countyTheatreMap", countyTheatreMap);
		if(StringUtils.isNotBlank(stc.getCountycode())){
			theatreListPoint = countyTheatreMap.get(stc.getCountycode());
		}
		model.put("theatreListPoint", theatreListPoint);
		List<Map> countyGroup = placeService.getPlaceCountyCountMap(Theatre.class, citycode);
		model.put("countyGroup", countyGroup);
		if(StringUtils.isNotBlank(stc.getCountycode())){
			County county = daoService.getObject(County.class, stc.getCountycode());
			model.put("county", county);
		}
		return "drama/wide_theatreMap.vm";
	}
	
	//������Ժ
	@RequestMapping("/theatre/searchNearTheatre.xhtml")
	public String searchNearTheatre(@CookieValue(value=CookieConstant.MEMBER_POINT, required=false) String bpointxy,
			SearchTheatreCommand stc, HttpServletRequest request, ModelMap model, HttpServletResponse response) {
		String citycode = WebUtils.getAndSetDefault(request, response);
		String pointx ="", pointy="";
		if(StringUtils.isNotBlank(bpointxy)){
			List<String> pointList = Arrays.asList(StringUtils.split(bpointxy, ":"));
			if(pointList.size() == 2){
				pointx = pointList.get(0);
				pointy = pointList.get(1);
			}
		}
		model.put("pointx", pointx);
		model.put("pointy", pointy);
		int pageNo = stc.getPageNo();
		final int rowsPerpage = stc.getRowsPerpage();
		final int firstRow = pageNo * rowsPerpage;
		Integer count = 0;
		List<Theatre> theatreListPoint = new ArrayList<Theatre>(0);
		if(stc.getMyRange() == null) stc.setMyRange("3000");
		else if(!MYRANGE_LIST.contains(stc.getMyRange())) stc.setMyRange("3000");
		List<Long> theatreIdList = dramaPlayItemService.getCurBookingTheatreList(citycode, stc.getCountycode());
		if(StringUtils.isBlank(pointx) || StringUtils.isBlank(pointy)){
			if(StringUtils.equals(stc.getBooking(), Theatre.BOOKING_OPEN)){
				count = theatreIdList.size();
				theatreListPoint = daoService.getObjectList(Theatre.class, theatreIdList);
				theatreListPoint = BeanUtil.getSubList(theatreListPoint, firstRow, rowsPerpage);
			}else{
				count =  Integer.parseInt(readOnlyTemplate.findByCriteria(getCriteria(stc, citycode).setProjection(Projections.rowCount())).get(0)+"");
				theatreListPoint = readOnlyTemplate.findByCriteria(getCriteria(stc, citycode), firstRow, rowsPerpage);
			}
		}else{
			theatreListPoint = commonService.getBaiDuNearPlaceObjectList(Theatre.class, citycode, stc.getCountycode(), pointx, pointy, Double.parseDouble(stc.getMyRange()));
			if(StringUtils.equals(stc.getBooking(), Theatre.BOOKING_OPEN)){
				theatreListPoint = getNearTheatreList(theatreListPoint, theatreIdList);
			}
			count = theatreListPoint.size();
			theatreListPoint = BeanUtil.getSubList(theatreListPoint, firstRow, rowsPerpage);
		}
		Collections.sort(theatreListPoint, new MultiPropertyComparator(new String[]{ClassUtils.hasMethod(Theatre.class, "get" + StringUtils.capitalize(stc.order))? stc.order: "booking","id"}, new boolean[]{false,true}));
		model.put("count", count);
		model.put("theatreIdList", theatreIdList);
		model.put("theatreListPoint", theatreListPoint);
		
		Map<Long, Integer> RangeMap = new HashMap<Long, Integer>();
		if(StringUtils.isNotBlank(pointx) && StringUtils.isNotBlank(pointy) && !theatreListPoint.isEmpty()){
			for (Theatre theatre : theatreListPoint) {
				if(StringUtils.isNotBlank(theatre.getBpointx())&& StringUtils.isNotBlank(theatre.getBpointy())){
					double Range = LongitudeAndLatitude.getDistance(Double.parseDouble(pointy), Double.parseDouble(pointx), Double.parseDouble(theatre.getBpointy()), Double.parseDouble(theatre.getBpointx()));
					RangeMap.put(theatre.getId(), (int)Range);
				}
			}
		}
		model.put("RangeMap", RangeMap);
		PageUtil pageUtil = new PageUtil(count, rowsPerpage, pageNo, "theatre/searchNearTheatre.xhtml", true, true);
		Map params = new HashMap();
		if(StringUtils.equals(stc.getBooking(), Theatre.BOOKING_OPEN)){
			params.put("booking", stc.getBooking());
		}
		if(StringUtils.isNotBlank(stc.getOrder())) {
			params.put("order", stc.getOrder());
		}
		params.put("myRange", stc.getMyRange());
		pageUtil.initPageInfo(params);
		model.put("pageUtil", pageUtil);
		dramaInfo(citycode, theatreListPoint, model);
		List<Long> theatreidList=dramaPlayItemService.getTheatreidList(citycode, null, true);
		model.put("theatreidList", theatreidList);
		Map<String, Integer> commentMap = commonService.getCommentCount();
		model.put("commentMap", commentMap);
		model.put("dramaTypeMap", DramaConstant.dramaTypeMap);
		return "drama/theatre/theatreListNearby.vm";
	}
	
	//�ܱ�ԤԼ����
	private List<Theatre> getNearTheatreList(List<Theatre> theatreListPoint, List<Long> theatreIdList){
		List<Theatre> bookingTheatreList = daoService.getObjectList(Theatre.class, theatreIdList);
		theatreListPoint = ListUtils.intersection(theatreListPoint, bookingTheatreList);
		return theatreListPoint;
	}
	
	private DetachedCriteria getCriteria(SearchTheatreCommand stc, String citycode){
		DetachedCriteria query = DetachedCriteria.forClass(Theatre.class,"t");
		query.add(Restrictions.eq("t.citycode", citycode));
		if(StringUtils.isNotBlank(stc.getTheatrename())){
			query.add(Restrictions.ilike("t.name", stc.getTheatrename(), MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(stc.indexareacode)){
			query.add(Restrictions.eq("t.indexareacode", stc.indexareacode));
		}else if(StringUtils.isNotBlank(stc.getCountycode()) && !stc.hasUnCounty()){
			query.add(Restrictions.eq("t.countycode", stc.countycode));
		}else{
			query.add(Restrictions.eq("t.citycode", citycode));
		}
		// �������ߣ�����������������
		if (stc.lineid != null) {
			query.add(Restrictions.like("t.lineidlist", String.valueOf(stc.lineid), MatchMode.ANYWHERE));
			if(stc.stationid!=null){
				query.add(Restrictions.eq("t.stationid", new Long(stc.stationid)));
			}
		}
		// ͣ��λ
		if (StringUtils.isNotBlank(stc.park)) {
			query.add(Restrictions.like("t.otherinfo", "park",  MatchMode.ANYWHERE));
		}
		//�Ƿ�ɹ�Ʊ
		if(StringUtils.equals(stc.booking, Theatre.BOOKING_OPEN)){
			query.add(Restrictions.eq("t.booking", stc.booking));
		}
		//ˢ��
//		if(StringUtils.isNotBlank(stc.getVisacard()) && stc.getVisacard()!=null){
//			query.add(Restrictions.isNotNull("visacard"));
//		}
//		if(StringUtils.isNotBlank(stc.getBooking())){
//			query.add(Restrictions.eq("booking", stc.getBooking()));
//		}
		//����ȡƱ
		if(StringUtils.isNotBlank(stc.takeMethod)){
			DetachedCriteria subQuery = DetachedCriteria.forClass(TheatreProfile.class, "tp");
			subQuery.add(Restrictions.eqProperty("tp.id","t.id"));
			subQuery.add(Restrictions.like("tp.takemethod", TheatreProfile.TAKEMETHOD_A,MatchMode.ANYWHERE));
			subQuery.setProjection(Projections.property("tp.id"));
			query.add(Subqueries.exists(subQuery));
		}
		//�Ƿ��ѡ��
		if(StringUtils.isNotBlank(stc.chooseSeat)){
			DetachedCriteria subQuery = DetachedCriteria.forClass(OpenDramaItem.class,"od");
			subQuery.add(Restrictions.eqProperty("od.theatreid","t.id"));
			subQuery.add(Restrictions.eq("od.opentype", OdiConstant.OPEN_TYPE_SEAT));
			subQuery.add(Restrictions.eq("od.status", OdiConstant.STATUS_BOOK));
			subQuery.setProjection(Projections.property("od.id"));
			query.add(Subqueries.exists(subQuery));
		}
		// ����
		if (StringUtils.isNotBlank(stc.order) && orderMap.get(stc.order) != null) {
			query.addOrder(Order.desc(orderMap.get(stc.order)));
		} else {
			query.addOrder(NullPropertyOrder.desc("booking"));
			query.addOrder(Order.desc("clickedtimes"));
			query.addOrder(Order.desc("hotvalue"));
		}
		query.addOrder(Order.asc("id"));
		return query;
	}
	
	private void dramaInfo(String citycode, List<Theatre> theatreList, ModelMap model){
		Map<Long,List<Drama>> curPlayDramaMap = new HashMap<Long, List<Drama>>();//��ǰ����������ӳ�Ļ�����Ϣ
		Map<Long, Integer> curPlayDramaCountMap = new HashMap<Long, Integer>();
		Map<Long, Boolean> isSeatMap = new HashMap<Long, Boolean>();
		for (Theatre theatre2 : theatreList) {
			List<Drama> dramaList = dramaService.getCurPlayDramaList(theatre2.getId(), 0, 2);
			curPlayDramaMap.put(theatre2.getId(), dramaList);
			Integer dramaCount = dramaService.getCurPlayDramaCount(theatre2.getId());
			curPlayDramaCountMap.put(theatre2.getId(), dramaCount);
			Integer odiCount = openDramaService.getOdiCountByTheatreid(theatre2.getId(), null, OdiConstant.OPEN_TYPE_SEAT);
			if(odiCount > 0) isSeatMap.put(theatre2.getId(), true);
			else isSeatMap.put(theatre2.getId(), false);
		}
		model.put("curPlayDramaMap", curPlayDramaMap);
		model.put("curPlayDramaCountMap", curPlayDramaCountMap);
		model.put("isSeatMap", isSeatMap);
		//�����Ƽ�
		List<Drama> curDramaList = openDramaService.getCurPlayDrama(citycode, 0, 4);
		Map<Long, List<Theatre>> theatreMap = new HashMap<Long, List<Theatre>>();
		Map<Long, List<Integer>> priceListMap = new HashMap<Long, List<Integer>>();
		for(Drama curDrama : curDramaList){
			theatreMap.put(curDrama.getId(), dramaPlayItemService.getTheatreList(citycode, curDrama.getId(), true, 2));
			priceListMap.put(curDrama.getId(), dramaPlayItemService.getPriceList(null, curDrama.getId(), null, null, false));
		}
		model.put("theatreMap", theatreMap);
		model.put("priceListMap", priceListMap);
		model.put("curDramaList", curDramaList);
		//����
		RelatedHelper rh = new RelatedHelper();
		model.put("relatedHelper", rh);
		List<RemoteActivity> activityList = new ArrayList<RemoteActivity>();
		ErrorCode<List<RemoteActivity>> code = synchActivityService.getActivityList(citycode, RemoteActivity.ATYPE_GEWA, RemoteActivity.TIME_CURRENT, TagConstant.TAG_THEATRE, null, null, null, 0, 3);
		if(code.isSuccess()){
			activityList = code.getRetval();
		}
		model.put("activityList", activityList);
		List<Serializable> theatreIdList = BeanUtil.getBeanPropertyList(activityList, Serializable.class, "relatedid", true);
		relateService.addRelatedObject(1, "activityList", rh, TagConstant.TAG_THEATRE, theatreIdList);
	}
	
	//�����б�
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/theatre/theatreList.xhtml")
	public String theatreList_new(ModelMap model, HttpServletRequest request, HttpServletResponse response,String ofcome, SearchTheatreCommand stc){
		int pageNo = stc.getPageNo();
		final int rowsPerpage = stc.getRowsPerpage();
		final int firstRow = pageNo * rowsPerpage;
		String citycode = WebUtils.getAndSetDefault(request, response);
//		if(true)
		PageParams params = new PageParams();
		params.addLong("lineid", stc.lineid);
		params.addLong("stationid", stc.stationid);
		params.addSingleString("countycode", stc.countycode);
		params.addSingleString("park", stc.park);
		params.addSingleString("chooseSeat", stc.chooseSeat);
		params.addSingleString("takeMethod", stc.takeMethod);
		params.addSingleString("park", stc.park);
		params.addSingleString("booking", stc.booking);
		params.addInteger("pageNo", pageNo);
		params.addSingleString("theatrename", stc.getTheatrename());
		if (pageCacheService.isUseCache(request)) {// ��ʹ�û���
			PageView pageView = pageCacheService.getPageView(request, "theatre/theatreList.xhtml", params, citycode);
			if (pageView != null) {
				model.put("pageView", pageView);
				return "pageView.vm";
			}
		}
		//����
		List<Map> countyGroup = placeService.getPlaceCountyCountMap(Theatre.class, citycode);
		model.put("countyGroup",countyGroup);
		//������
		List<Map<String,Object>> subwaylineGroup = placeService.getPlaceGroupMapByCitySubwayline(citycode, TagConstant.TAG_THEATRE);
		model.put("subwaylineGroup",subwaylineGroup);
		//���糡���б�
		List<Theatre> theatreList = new ArrayList<Theatre>();
		Integer rowsCount = 0;
		if(StringUtils.isNotBlank(ofcome)){
			theatreList = hibernateTemplate.findByCriteria(getCriteria(stc, citycode), firstRow, rowsPerpage);
			rowsCount = Integer.valueOf(hibernateTemplate.findByCriteria(getCriteria(stc, citycode)).size()+"");
		}else{
			theatreList = hibernateTemplate.findByCriteria(getCriteria(stc, citycode), firstRow, rowsPerpage);
			rowsCount = Integer.valueOf(hibernateTemplate.findByCriteria(getCriteria(stc, citycode)).size()+"");
		}
		model.put("theatreList",theatreList);
		//��ȡ������ȡƱ�ĳ����б�
		List<Long> idList = new ArrayList<Long>();
		for(Theatre thea:theatreList)
			idList.add(thea.getId());
		if(!idList.isEmpty())
			model.put("takeMethodMap",daoService.getObjectPropertyMap(TheatreProfile.class, "id", "takemethod", idList));
		//��ҳ��Ϣ
		PageUtil pageUtil = new PageUtil(rowsCount, rowsPerpage, pageNo, "theatre/theatreList.xhtml", true, true);
		pageUtil.initPageInfo(params.getParams());
		model.put("pageUtil", pageUtil);
		model.put("rowsCount", rowsCount);
		//���ݾ�Ŀ��ȡ��ǰ���������ҹ�Ʊ�����ľ�Ŀ
		List<Long> openseatList = openDramaService.getCurDramaidList(citycode, OdiConstant.OPEN_TYPE_SEAT);
		List<Long> bookingList = openDramaService.getCurDramaidList(citycode);
		model.put("openseatList",openseatList);
		model.put("bookingList", bookingList);
		List<Drama> bookList = daoService.getObjectList(Drama.class, bookingList);
		Collections.sort(bookList,new MultiPropertyComparator(new String[]{"boughtcount", "clickedtimes"}, new boolean[]{false, false}));
		List<Drama> hotDramaList = BeanUtil.getSubList(bookList, 0, 4);
		Map<Long, List<Theatre>> theatreMap = new HashMap<Long, List<Theatre>>();
		Map<Long, List<Integer>> priceListMap = new HashMap<Long, List<Integer>>();
		for(Drama curDrama : hotDramaList){
			theatreMap.put(curDrama.getId(), dramaPlayItemService.getTheatreList(citycode, curDrama.getId(), true, 2));
			priceListMap.put(curDrama.getId(), dramaPlayItemService.getPriceList(null, curDrama.getId(), null, null, false));
		}
		model.put("theatreMap", theatreMap);
		model.put("priceListMap", priceListMap);
		model.put("hotDramaList",hotDramaList);
		//��ȡ�ɹ�Ʊ���ݳ�
		List<Long> theatreidList=dramaPlayItemService.getTheatreidList(citycode, null, true);
		model.put("theatreidList",theatreidList);
		//��ȡ���ڽ����еĹٷ�������ձ������������ɽ���Զ����
		ErrorCode<List<RemoteActivity>> code = synchActivityService.getActivityListByOrder(citycode, RemoteActivity.ATYPE_GEWA,RemoteActivity.TIME_CURRENT,TagConstant.TAG_THEATRE,null, null,null,"duetime",0,3);
		if(code.isSuccess()){
			model.put("activityList",code.getRetval());
		}
		//��ȡ�������ݳ�����������
		List<Comment> commentList = commentService.getCommentListByTags(new String[]{"drama"}, null, true, 0, 10);
		model.put("commentList", commentList);
		addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(commentList));
		getCurPlayDrama(citycode, theatreList,model);
		model.put("dramaTypeMap", DramaConstant.dramaTypeMap);
		return "drama/wide_theatreList.vm"; 
	}
	//��ȡ�����������ݵľ�Ŀ
	private void getCurPlayDrama(String citycode, List<Theatre> theatreList, ModelMap model){
		Map<Long,List<Drama>> curPlayDramaMap = new HashMap<Long, List<Drama>>();//��ǰ����������ӳ�Ļ�����Ϣ
		Map<Long, Integer> curPlayDramaCountMap = new HashMap<Long, Integer>();
		List<Long> theatreIds = new ArrayList<Long>();
		for (Theatre theatre2 : theatreList) {
			List<Drama> dramaList = dramaService.getCurPlayDramaList(theatre2.getId(), 0, 2);
			curPlayDramaMap.put(theatre2.getId(), dramaList);
//			Integer dramaCount = dramaService.getCurPlayDramaCount(theatre2.getId());
			curPlayDramaCountMap.put(theatre2.getId(), dramaList.size());
			theatreIds.add(theatre2.getId());
		}
		model.put("curPlayDramaMap", curPlayDramaMap);
		model.put("curPlayDramaCountMap", curPlayDramaCountMap);
		model.put("seatList", dramaPlayItemService.getTheatreidList(citycode, null, OdiConstant.OPEN_TYPE_SEAT, true));
	}
}
