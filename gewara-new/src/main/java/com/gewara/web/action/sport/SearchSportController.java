package com.gewara.web.action.sport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.Status;
import com.gewara.constant.TagConstant;
import com.gewara.constant.content.SignName;
import com.gewara.constant.order.AddressConstant;
import com.gewara.json.MemberStats;
import com.gewara.json.PageView;
import com.gewara.model.common.County;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.sport.Sport;
import com.gewara.model.sport.Sport2Item;
import com.gewara.model.sport.SportItem;
import com.gewara.model.sport.SportProfile;
import com.gewara.model.user.Member;
import com.gewara.service.OperationService;
import com.gewara.service.sport.MemberCardService;
import com.gewara.service.sport.OpenTimeTableService;
import com.gewara.support.ErrorCode;
import com.gewara.support.ReadOnlyTemplate;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.CommonService;
import com.gewara.untrans.MemberCountService;
import com.gewara.untrans.NosqlService;
import com.gewara.untrans.PageCacheService;
import com.gewara.untrans.PageParams;
import com.gewara.untrans.activity.SynchActivityService;
import com.gewara.untrans.impl.ControllerService;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.util.PageUtil;
import com.gewara.xmlbind.activity.RemoteActivity;
import com.gewara.xmlbind.bbs.Comment;
/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
@Controller
public class SearchSportController extends BaseSportController {
	@Autowired@Qualifier("commentService")
	private CommentService commentService;
	@Autowired@Qualifier("commonService")
	private CommonService commonService;
	@Autowired@Qualifier("synchActivityService")
	private SynchActivityService synchActivityService;
	@Autowired@Qualifier("openTimeTableService")
	private OpenTimeTableService openTimeTableService;
	@Autowired@Qualifier("pageCacheService")
	private PageCacheService pageCacheService;
	@Autowired@Qualifier("memberCountService")
	private MemberCountService memberCountService;
	@Autowired@Qualifier("controllerService")
	private ControllerService controllerService;
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	@Autowired@Qualifier("nosqlService")
	private NosqlService nosqlService;
	@Autowired@Qualifier("memberCardService")
	private MemberCardService memberCardService;
	@Autowired@Qualifier("readOnlyTemplate")
	private ReadOnlyTemplate readOnlyTemplate;
	public void setReadOnlyHibernateTemplate(ReadOnlyTemplate readOnlyTemplate) {
		this.readOnlyTemplate = readOnlyTemplate;
	}
	private static final Map<String, String> orderMap = new HashMap<String, String>();
	static{
		orderMap.put("name", "name");
		orderMap.put("clickedtimes", "clickedtimes");
		orderMap.put("booking", "booking");
		orderMap.put("generalmark", "avggeneral");
		orderMap.put("environmentmark", "avgenvironment");
		orderMap.put("servicemark", "avgservice");
		orderMap.put("pricemark", "avgprice");
		orderMap.put("fieldmark", "avgfield");
	}
	
	@RequestMapping("/sport/sportList.xhtml")
	public String sportList(SearchSportCommand ssc, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		String citycode = WebUtils.getAndSetDefault(request, response);
		String spkey = request.getParameter("spkey");
		model.put("spkey", spkey);
		//��ҳ
		int count = 0;
		int pageNo = ssc.getPageNo();
		final int rowsPerpage = ssc.getRowsPerpage();
		final int firstRow = pageNo * rowsPerpage;
		//����
		PageParams params = new PageParams();
		params.addSingleString("countycode", ssc.countycode);
		params.addSingleString("indexareacode", ssc.indexareacode);
		params.addNumberStr("servicetype", ssc.servicetype);
		params.addSingleString("sportname", ssc.getSportname());
		params.addLong("lineid", ssc.lineid);
		params.addSingleString("order", ssc.order);
		params.addSingleString("booking", ssc.booking);
		params.addSingleString("park", ssc.park);
		params.addSingleString("indoor", ssc.indoor);
		params.addSingleString("outdoor", ssc.outdoor);
		params.addSingleString("visacard", ssc.visacard);
		params.addSingleString("cupboard", ssc.cupboard);
		params.addSingleString("bathe", ssc.bathe);
		params.addSingleString("restregion", ssc.restregion);
		params.addSingleString("sale", ssc.sale);
		params.addSingleString("train", ssc.train);
		params.addSingleString("meal", ssc.meal);
		params.addSingleString("lease",ssc.lease);
		params.addSingleString("maintain",ssc.maintain);
		params.addLong("stationid", ssc.stationid);
		params.addSingleString("sportids",ssc.sportids);
		params.addInteger("pageNo", pageNo);
		if (StringUtils.isNotBlank(spkey)) {
			params.addSingleString("spkey", spkey);
		}
		if(StringUtils.isBlank(ssc.getSportname()) && StringUtils.isBlank(ssc.getSportids()) && pageCacheService.isUseCache(request)){
			PageView pageView = pageCacheService.getPageView(request, "sport/sportList.xhtml", params, citycode);
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
		if(ssc.sportid != null){
			Sport sport = daoService.getObject(Sport.class, ssc.sportid);
			if(sport==null) return showError(model, "�˶����������ڻ���ɾ����");
		}
		//�˶�������Ŀ
		List<SportItem> sportItemList = sportService.getTopSportItemList();
		model.put("sportItemList", sportItemList);
		//����
		List<Map> countyGroup = placeService.getPlaceCountyCountMap(Sport.class, citycode);
		model.put("countyGroup",countyGroup);
		County curCounty = null;
		if (StringUtils.isNotBlank(ssc.indexareacode)) {
			curCounty = daoService.getObject(County.class, ssc.countycode);
		} else if (StringUtils.isNotBlank(ssc.countycode)) {
			curCounty = daoService.getObject(County.class, ssc.countycode);
		}
		model.put("curCounty", curCounty);
		//��Ȧ
		List indexareaGroup = new ArrayList();
		List subwaylineGroup = new ArrayList();
		if (StringUtils.isNotBlank(ssc.indexareacode)) {
			 model.put("indexareacode", ssc.indexareacode);
			indexareaGroup = placeService.getPlaceIndexareaCountMap(Sport.class, ssc.countycode);
			 
		}else if(StringUtils.isNotBlank(ssc.countycode)){
			model.put("countycode", ssc.countycode);
			indexareaGroup = placeService.getPlaceIndexareaCountMap(Sport.class, ssc.countycode);
			
		}
		subwaylineGroup = placeService.getPlaceGroupMapByCitySubwayline(citycode, "sport");
		if(ssc.lineid!=null){
			List<Map> subwaystationList = placeService.getSubwaystationList(citycode, "theatre", Long.valueOf(ssc.lineid));
			model.put("subwaystationList", subwaystationList);
			if(ssc.stationid!=null){
				model.put("stationid", ssc.stationid);
			}
		}
		model.put("indexareaGroup",indexareaGroup);
		model.put("subwaylineGroup",subwaylineGroup);
		//�˶�����ȡ��
		List<Sport> sportList = new ArrayList<Sport>();
		Long itemid = null;
		if(StringUtils.isNotBlank(ssc.servicetype)){
			itemid = Long.valueOf(ssc.servicetype);
		}
		List<Long> sidList = new ArrayList<Long>();
		List<Long> mySportIdList = new ArrayList<Long>();
		if(StringUtils.isNotBlank(ssc.getSportids())){
			for (String sportid : StringUtils.split(ssc.getSportids(), ",")) {
				mySportIdList.add(Long.parseLong(sportid));
			}
			sidList.addAll(mySportIdList);
		}
		
		List<Long> sportIdList = openTimeTableService.getCurOttSportIdList(itemid, citycode);
		if(StringUtils.equals(ssc.booking, SportProfile.STATUS_OPEN)){
			sidList.addAll(sportIdList);
			if(sportIdList.isEmpty() && itemid != null){
				model.put("searchCount", count);
				return "sport/wide_sportList.vm";
			}
		}
		if(!mySportIdList.isEmpty() && !sportIdList.isEmpty()){
			sidList.addAll(CollectionUtils.intersection(mySportIdList, sportIdList));
		}
		count = Integer.valueOf(readOnlyTemplate.findByCriteria(getCriteria(ssc, citycode, sidList, true)).get(0)+"");
		if(count >0){
			sportList = readOnlyTemplate.findByCriteria(getCriteria(ssc, citycode, sidList, false), firstRow, rowsPerpage);
		}
		Map<Long, List<SportItem>> itemMap = new HashMap<Long, List<SportItem>>();
		Map<Long, SportProfile> spMap = new HashMap<Long, SportProfile>();
		Map<Long, Integer> sportMemberCardCountMap = new HashMap<Long, Integer>();
		for(Sport sport : sportList){
			List<SportItem> itemList = sportService.getSportItemListBySportId(sport.getId(), SportProfile.STATUS_OPEN);
			itemMap.put(sport.getId(), itemList);
			spMap.put(sport.getId(), daoService.getObject(SportProfile.class, sport.getId()));
			int memberCardCount = memberCardService.getBookingMemberCardTypeCountBySportids(sport.getId());
			sportMemberCardCountMap.put(sport.getId(), memberCardCount);
		}
		model.put("sportMemberCardCountMap", sportMemberCardCountMap);
		model.put("spMap", spMap);
		Map<String, Integer> commentMap = commonService.getCommentCount();
		model.put("commentMap", commentMap);
		model.put("itemMap", itemMap);
		model.put("sportIdList", sportIdList);
		model.put("searchCount", count);
		model.put("sportList", sportList);
		PageUtil pageUtil = new PageUtil(count, rowsPerpage, pageNo, "sport/sportList.xhtml", true, true);
		pageUtil.initPageInfo(params.getParams());
		model.put("pageUtil", pageUtil);
		if(StringUtils.isNotBlank(ssc.servicetype)){
			SportItem sportItem = daoService.getObject(SportItem.class, itemid);
			model.put("curSportItem", sportItem);
			List<Long> idList = BeanUtil.getBeanPropertyList(sportList, Long.class, "id", true);
			getItemMapCount(itemid, idList, model);
		}
		return "sport/wide_sportList.vm";
	}
	
	
	
	
	private void getItemMapCount(Long itemid, List<Long> sportIdList,ModelMap model){
		List<Map> dataList = openTimeTableService.getOpenTimeCountByItemid(itemid, sportIdList.toArray(new Long[]{}));
		Map<Long, List> openMap = BeanUtil.groupBeanList(dataList, "sportid");
		model.put("sportBookingMap", openMap);
		Map<Long, Map<String, Integer>> priceMap = new HashMap<Long, Map<String,Integer>>();
		for (Long sportid : sportIdList) {
			Map<String, Integer> priceMapList = sportService.getSportPrice(sportid, itemid);
			priceMap.put(sportid, priceMapList);
		}
		model.put("priceMap", priceMap);
	}	
	private DetachedCriteria getCriteria(SearchSportCommand ssc, String citycode, Collection<Long> bookSportidList, boolean queryCount){
		DetachedCriteria query = DetachedCriteria.forClass(Sport.class, "s");
		if(StringUtils.isNotBlank(ssc.servicetype)){
			DetachedCriteria subQuery = DetachedCriteria.forClass(Sport2Item.class, "t");
			subQuery.add(Restrictions.eq("t.itemid", Long.valueOf(ssc.servicetype)));
			subQuery.add(Restrictions.eqProperty("t.sportid", "s.id"));
			subQuery.add(Restrictions.eq("t.booking", SportProfile.STATUS_OPEN));
			subQuery.setProjection(Projections.property("t.sportid"));
			query.add(Subqueries.exists(subQuery));
		}
		if(StringUtils.isNotBlank(ssc.getSportname())){
			query.add(Restrictions.ilike("name", ssc.getSportname(), MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(ssc.indexareacode)){
			query.add(Restrictions.eq("indexareacode", ssc.indexareacode));
		}else if(StringUtils.isNotBlank(ssc.getCountycode())){
			query.add(Restrictions.eq("countycode", ssc.countycode));
		}else if(StringUtils.isNotBlank(ssc.getBooking())){
			query.add(Restrictions.eq("booking", ssc.booking));
		}else{
			query.add(Restrictions.eq("citycode", citycode));
		}
		// �������ߣ�����������������
		if (ssc.lineid != null) {
			query.add(Restrictions.like("lineidlist", String.valueOf(ssc.lineid), MatchMode.ANYWHERE));
			if(ssc.stationid!=null){
				query.add(Restrictions.eq("stationid", new Long(ssc.stationid)));
			}
		}
		//ͣ����
		if (StringUtils.isNotBlank(ssc.park)) query.add(Restrictions.ilike("otherinfo", ssc.park, MatchMode.ANYWHERE));
		//ˢ��
		if (StringUtils.isNotBlank(ssc.visacard)) query.add(Restrictions.ilike("otherinfo", ssc.visacard, MatchMode.ANYWHERE));
		//������ƾ
		if (StringUtils.isNotBlank(ssc.cupboard)) query.add(Restrictions.ilike("otherinfo", ssc.cupboard, MatchMode.ANYWHERE));
		//ϴ��
		if (StringUtils.isNotBlank(ssc.bathe)) query.add(Restrictions.ilike("otherinfo", ssc.bathe, MatchMode.ANYWHERE));
		//����
		if (StringUtils.isNotBlank(ssc.indoor)) query.add(Restrictions.ilike("otherinfo", ssc.indoor, MatchMode.ANYWHERE));
		//����
		if (StringUtils.isNotBlank(ssc.outdoor)) query.add(Restrictions.ilike("otherinfo", ssc.outdoor, MatchMode.ANYWHERE));
		//��Ϣ��
		if (StringUtils.isNotBlank(ssc.restregion)) query.add(Restrictions.ilike("otherinfo", ssc.restregion, MatchMode.ANYWHERE));
		//�ײ�
		if (StringUtils.isNotBlank(ssc.meal)) query.add(Restrictions.ilike("otherinfo", ssc.meal, MatchMode.ANYWHERE));
		//��Ʒ
		if (StringUtils.isNotBlank(ssc.sale)) query.add(Restrictions.ilike("otherinfo", ssc.sale, MatchMode.ANYWHERE));
		//��ѵ
		if (StringUtils.isNotBlank(ssc.train)) query.add(Restrictions.ilike("otherinfo", ssc.train, MatchMode.ANYWHERE));
		//�������
		if (StringUtils.isNotBlank(ssc.lease)) query.add(Restrictions.ilike("otherinfo", ssc.lease, MatchMode.ANYWHERE));
		//����ά��
		if (StringUtils.isNotBlank(ssc.maintain)) query.add(Restrictions.ilike("otherinfo", ssc.maintain, MatchMode.ANYWHERE));
		//��Ա��
		if (StringUtils.isNotBlank(ssc.membercard)) query.add(Restrictions.ilike("otherinfo", ssc.membercard, MatchMode.ANYWHERE));
		//�Ƿ��Ԥ��
		if(!bookSportidList.isEmpty()){
			query.add(Restrictions.in("id", bookSportidList));
		}
		query.add(Restrictions.or(Restrictions.ne("s.flag", "H"),Restrictions.isNull("s.flag")));
		if(queryCount){
			query.setProjection(Projections.rowCount());
		}else{
			query.addOrder(Order.desc("s.booking"));
			// ����
			if(orderMap.get(ssc.order)!=null && ClassUtils.hasMethod(Sport.class, "get" + StringUtils.capitalize(ssc.order))){
				query.addOrder(Order.desc("s." + orderMap.get(ssc.order)));
			}else{
				query.addOrder(Order.desc("s.hotvalue"));
				query.addOrder(Order.desc("s.clickedtimes"));
			}
			query.addOrder(Order.asc("id"));
		}
		return query;
	}
	//�
	@RequestMapping("/sport/ajax/getActivityList.xhtml")
	public String getActivityList(String type, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		String citycode = WebUtils.getAndSetDefault(request, response);
		if (pageCacheService.isUseCache(request)) {// ��ʹ�û���
			PageParams params = new PageParams();
			params.addSingleString("type", type);
			PageView pageView = pageCacheService.getPageView(request, "sport/ajax/getActivityList.xhtml", params, citycode);
			if (pageView != null) {
				model.put("pageView", pageView);
				return "pageView.vm";
			}
		}
		ErrorCode<List<RemoteActivity>> code = synchActivityService.getActivityListByOrder(citycode, null, RemoteActivity.TIME_CURRENT, TagConstant.TAG_SPORT, null, null, null, "membercount", 0, 4);
		if(code.isSuccess()){
			model.put("activityList", code.getRetval());
		}
		model.put("type", type);
		return "include/sport/mod_hotActivity.vm";
	}
	//����
	@RequestMapping("/sport/ajax/getWalaList.xhtml")
	public String getWalaList(String type, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		String citycode = WebUtils.getAndSetDefault(request, response);
		if (pageCacheService.isUseCache(request)) {// ��ʹ�û���
			PageParams params = new PageParams();
			params.addSingleString("type", type);
			PageView pageView = pageCacheService.getPageView(request, "sport/ajax/getWalaList.xhtml", params, citycode);
			if (pageView != null) {
				model.put("pageView", pageView);
				return "pageView.vm";
			}
		}
		List<Comment> commentList = commentService.getCommentListByRelatedId(TagConstant.TAG_SPORT, null, null, null, 0, 5);
		model.put("commentList", commentList);
		addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(commentList));
		model.put("type", type);
		return "include/sport/mod_newComment.vm";
	}
	//�ҳ�ȥ����
	@RequestMapping("/sport/ajax/getMyFrequented.xhtml")
	public String getMyFrequented(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, HttpServletRequest request, ModelMap model){
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		String sportids = "";
		if(member != null){
			Map memberCountMap = memberCountService.getMemberCount(member.getId());
			if(memberCountMap.get(MemberStats.FIELD_LASTSPORTID) != null) sportids = memberCountMap.get(MemberStats.FIELD_LASTSPORTID)+"";
		}
		return showJsonSuccess(model, sportids);
	}
	//���ų�������
	@RequestMapping("/sport/ajax/addPlayItemMessage.xhtml")
	public String addPlayItemMessage(@CookieValue(value = LOGIN_COOKIE_NAME, required = false)String sessid, HttpServletRequest request,
			String tag, Long relatedid, Long categoryid, String mobile, Date playDate, String captchaId, String captcha, ModelMap model) {
		//TODO:��ʱ�����ã��ȴ�֪ͨ����
		String t = "N";
		if(t.equals("N")) return showJsonError(model, "�ÿ��ų������ѹ�����ʱ�����ã�");
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError_CAPTCHA_ERROR(model);
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		if(playDate == null) return showJsonError(model, "���ų������ڲ���Ϊ�գ�");
		if(!DateUtil.isAfter(playDate)) return showJsonError(model, "�������ڲ����ڵ�ǰʱ��֮ǰ��");
		if(playDate.after(DateUtil.addDay(DateUtil.getCurDate(), 60))) return showJsonError(model, "�������� ֻ����2����֮�ڣ�");
		if (!StringUtils.equals(tag, TagConstant.TAG_SPORT)) return showJsonError(model, "��������");
		if (!ValidateUtil.isMobile(mobile)) return showJsonError(model, "�ֻ��Ÿ�ʽ���Ϸ���");
		String opkey = "sportplayitem" + WebUtils.getRemoteIp(request);
		boolean allow = operationService.isAllowOperation(opkey, 30, OperationService.ONE_DAY, 10);
		if (!allow)	return showJsonError(model, "���������Ƶ�������Ժ����ԣ�");
		Sport sport = daoService.getObject(Sport.class, relatedid);
		if(sport == null) return showJsonError(model, "���ݲ����ڣ�");
		SportProfile sp = daoService.getObject(SportProfile.class, relatedid);
		if(sp == null || !StringUtils.equals(sp.getBooking(), SportProfile.STATUS_OPEN)) return showJsonError(model, "�ó��ݲ�֧��Ԥ����");
		SportItem item = daoService.getObject(SportItem.class, categoryid);
		if(item == null) return showJsonError(model, "��Ŀ�����ڣ�");
		List<OpenTimeTable> ottList = openTimeTableService.getOpenTimeTableList(relatedid, categoryid, playDate, null, 0, 1);
		if (!ottList.isEmpty())	return showJsonError(model, "��ǰʱ���ѿ��ų��أ�");
		String msg = "����ע��<" + sport.getRealBriefname() + "><" + DateUtil.format(playDate, "yyyy-M-d") + "><" + item.getItemname() + ">�����ѿ���Ԥ�������¼����������������Ԥ�� http://t.cn/aodra6 ";
		nosqlService.addPlayItemMessage(member.getId(), TagConstant.TAG_SPORT, relatedid, playDate, categoryid, mobile, Status.N, AddressConstant.ADDRESS_WEB, msg);
		operationService.updateOperation(opkey, 30, OperationService.ONE_DAY, 10);
		return showJsonSuccess(model);
	}
}