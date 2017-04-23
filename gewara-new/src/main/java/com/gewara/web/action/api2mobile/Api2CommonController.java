package com.gewara.web.action.api2mobile;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import com.gewara.Config;
import com.gewara.constant.AdminCityContant;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.DiaryConstant;
import com.gewara.constant.Status;
import com.gewara.constant.TagConstant;
import com.gewara.constant.order.AddressConstant;
import com.gewara.constant.sys.ConfigConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.json.AppSourceCount;
import com.gewara.json.MobileApp;
import com.gewara.json.MobileLoadImage;
import com.gewara.json.MobileUpGrade;
import com.gewara.json.mobile.MobileAdvertisingYouMi;
import com.gewara.model.api.ApiUser;
import com.gewara.model.bbs.Diary;
import com.gewara.model.common.GewaConfig;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.movie.Cinema;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.bbs.DiaryService;
import com.gewara.service.content.NewsService;
import com.gewara.support.ErrorCode;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.activity.SynchActivityService;
import com.gewara.util.BeanUtil;
import com.gewara.util.HttpResult;
import com.gewara.util.HttpUtils;
import com.gewara.util.StringUtil;
import com.gewara.web.action.api.ApiAuth;
import com.gewara.web.action.api.BaseApiController;
import com.gewara.web.action.api2.AppDriverUtils;
import com.gewara.web.filter.NewApiAuthenticationFilter;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
@Controller
public class Api2CommonController extends BaseApiController{
	@Autowired
	private AppDriverUtils appDriverUtils;
	@Autowired@Qualifier("config")
	private Config config;
	@Autowired@Qualifier("blogService")
	private BlogService blogService;
	@Autowired@Qualifier("newsService")
	private NewsService newsService;
	@Autowired@Qualifier("synchActivityService")
	private SynchActivityService synchActivityService;
	@Autowired@Qualifier("diaryService")
	private DiaryService diaryService;
	private static final Map<String, String[]> proviceMap;
	private static final Map<String, String[]> sportproviceMap;
	static {
		proviceMap = new LinkedHashMap<String, String[]>();
		proviceMap.put("ֱϽ��", new String[] { "310000", "500000","110000"});
		proviceMap.put("�㽭ʡ", new String[] { "330100", "330200", "330400", "330600", "330500", "331000" });
		proviceMap.put("����ʡ", new String[] { "320100", "320200", "320400", "320500", "320600"});
		proviceMap.put("�㶫ʡ", new String[] { "440100", "440300" });
		proviceMap.put("�Ĵ�ʡ", new String[] { "510100" });
		proviceMap.put("����ʡ", new String[] { "420100" });
		
		sportproviceMap = new LinkedHashMap<String, String[]>();
		sportproviceMap.put("ֱϽ��", new String[] { "310000", "110000"});
		sportproviceMap.put("�㽭ʡ", new String[] { "330100"});
		sportproviceMap.put("����ʡ", new String[] { "320100"});
		sportproviceMap.put("�㶫ʡ", new String[] { "440100", "440300"});
		
	}



	/**
	 * ��ǰ���ó����б�
	 * @author bob.hu
	 * @date 2011-06-02 17:56:11
	 * �������ش�
	 */
	@RequestMapping("/api2/mobile/getUsableCitys.xhtml")
	public String getUsableCitys(String apptype, ModelMap model) {
		Map<String, String> citynameMap = AdminCityContant.getCitycode2CitynameMap();
		Map<String, String> codeMap = AdminCityContant.getCitycode2PinyinMap();
		model.put("citynameMap", citynameMap);
		model.put("codeMap", codeMap);
		if(StringUtils.equals(apptype, TagConstant.TAG_SPORT)){
			model.put("proviceMap", sportproviceMap);
		}else {
			model.put("proviceMap", proviceMap);
		}
		return getXmlView(model, "api/mobile/usableCitys.vm");
	}
	@RequestMapping("/api2/mobile/getApiPath.xhtml")
	public String getApiPath(HttpServletResponse response) throws IOException{
		GewaConfig cfg = daoService.getObject(GewaConfig.class, ConfigConstant.CFG_MOBILE_APIPATH);
		PrintWriter writer = response.getWriter();
		writer.write(cfg.getContent());
		writer.flush();
		writer.close();
		return null;
	}
	/**
	 * ������·API
	 */
	@RequestMapping("/api2/mobile/subwayList.xhtml")
	public String subwayList(String citycode,ModelMap model){
		ApiAuth auth = NewApiAuthenticationFilter.getApiAuth();
		ApiUser partner = auth.getApiUser();
		if(StringUtils.isNotBlank(citycode)){
			if(!partner.supportsCity(citycode)) return getErrorXmlView(model, ApiConstant.CODE_PARTNER_NORIGHTS, "��֧�ֳ���" + citycode);
		}else{
			citycode = partner.getDefaultCity();
		}
		List subwaylineGroup = placeService.getPlaceGroupMapByCitySubwayline(citycode, TagConstant.TAG_CINEMA);
		model.put("subwaylineGroup", subwaylineGroup);
		return getXmlView(model, "api/mobile/subwayList.vm");
	}
	/**
	 * ��¼����
	 * key(�ش�)
	 * encryptCode(�ش�)
	 * apptype:Ӧ�����ͣ�Ϊ�˼��ݵͿͻ��˰汾��
	 */
	@RequestMapping("/api2/mobile/loginImg.xhtml")
	public String loginImg(String apptype, ModelMap model) {
		Map params = new HashMap();
		params.put("status", MobileLoadImage.STATUS_Y);
		apptype = StringUtils.isBlank(apptype) ? TagConstant.TAG_CINEMA : apptype;
		params.put("apptype", apptype);
		List<MobileLoadImage> list = mongoService.getObjectList(MobileLoadImage.class, params, "addTime", false, 0, 1);
		String imgsrc = "";
		for(MobileLoadImage image : list) {
			if(image.hasProgress()){
				imgsrc = config.getString("mobilePath") + image.getImagesrc();
				break;
			}
		}
		model.put("imgsrc", imgsrc);
		return getXmlView(model, "api/mobile/loginImg.vm");
	}

	/**
	 * �������
	 * �������ش���apptype,appsource����
	 * tag:ϵͳiphone/android
	 * apptype:Ӧ�����ͣ�cinema,sport,bar����Ϊ�˼��ݵͰ汾�������ĳɱ��봫��
	 * appSource:Ӧ����Դ�����ݵͰ汾�������ĳɱ��봫��
	 */
	@RequestMapping("/api2/mobile/upGrade.xhtml")
	public String upgrade(String tag, String apptype, String appSource, ModelMap model) {
		apptype = StringUtils.isBlank(apptype) ? TagConstant.TAG_CINEMA : apptype;
		if (StringUtils.isBlank(appSource)) {
			if (StringUtils.equalsIgnoreCase(AddressConstant.ADDRESS_ANDROID,tag))
				appSource = "AS01";// ��ȡgoogemarket�ĸ���·��
			else if(StringUtils.equalsIgnoreCase(AddressConstant.ADDRESS_IPHONE, tag))
				appSource = "AS02";// ��ȡappstore�ĸ���·��
			
		}
		MobileUpGrade upgrade = nosqlService.getLastMobileUpGrade(tag, apptype, appSource);
		if (upgrade!=null){
			model.put("mobileUpGrade", upgrade);
		}
		return getXmlView(model, "api/mobile/upGrade.vm");
	}

	/**
	 * ����ͳ��
	 * 
	 * �������ش���citycode��apptype����
	 * @param key
	 * @param encryptCode
	 * @param appSource
	 *           Ӧ����Դ
	 * @param osType
	 *           ϵͳ����
	 * @param deviceId
	 *           �豸ID
	 * @param apptype
	 *           Ӧ������ ��Ӱ���˶����ư�
	 * @param flag
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/mobile/insOpenCount.xhtml")
	public String installOpen(HttpServletRequest request, String apptype, 
			String appSource, String osType, String deviceid,String deviceMAC, String citycode, ModelMap model) {
		if (StringUtils.isBlank(citycode)) citycode = AdminCityContant.CITYCODE_SH;
		if (StringUtils.isNotBlank(appSource) && StringUtils.isNotBlank(osType) && StringUtils.isNotBlank(deviceid)) {
			osType = osType.toUpperCase();
			if (StringUtils.isBlank(apptype)) apptype = TagConstant.TAG_CINEMA;// ������ǰ��Ӱ�Ͱ汾
			logAppSource(request, citycode, null,AppSourceCount.TYPE_IO, apptype);
		}
		if(StringUtils.isNotBlank(deviceMAC)){
			try {
				Map params = new HashMap();
				params.put("apptype", apptype);
				params.put("deviceid", deviceMAC.toLowerCase());
				List<MobileAdvertisingYouMi> mobileAdvertisings = mongoService.getObjectList(MobileAdvertisingYouMi.class,params,"addTime", false, 0, 1);
				if(mobileAdvertisings != null && mobileAdvertisings.size() > 0){
					for(MobileAdvertisingYouMi mobileAdvertising : mobileAdvertisings){
						dbLogger.warn("�ֻ�ios����ƹ㣺deviceMAC " + deviceMAC + "---appsource" + mobileAdvertising.getAppsource());
						if("AS47".equals(mobileAdvertising.getAppsource())){
							notifyYouMi(mobileAdvertising,deviceid);
						}else if("AS48".equals(mobileAdvertising.getAppsource())){
							notifyLiMei(mobileAdvertising,deviceid);
						}else if("AS80".equals(mobileAdvertising.getAppsource())){//���ն���
							appDriverUtils.notifyAppDriver(mobileAdvertising, deviceid);
						}
					}
				}
				
			} catch (Exception e) {
				dbLogger.warn("�ֻ�ios����ƹ㣺deviceMAC �ص�����" + deviceMAC + ":exception:" + e.getMessage());
			}
		}
		return getXmlView(model, "api/mobile/result.vm");
	}

	private void notifyYouMi(MobileAdvertisingYouMi aym,String deviceid){
		if(aym != null){
			dbLogger.warn("�ֻ�ios���׹���ƹ�ص���deviceMAC " + aym.getDeviceid());
			String t = System.currentTimeMillis()/1000 + "";
			String sig = StringUtil.md5("439a68c84b672e20" + StringUtil.md5(aym.getDeviceid()) + t).substring(12, 20);
			StringBuilder url = new StringBuilder(aym.getUrl());
			url.append("&ts=0").append("&t=").append(t).append("&sig=").append(sig);
			HttpResult code = HttpUtils.getUrlAsString(url.toString());
			if(code.isSuccess()){
				aym.setYmRecord(code.getResponse());
				aym.setOpenUDID(deviceid);
				mongoService.saveOrUpdateObject(aym, MongoData.DEFAULT_ID_NAME);
			}
		}
	}
	private void notifyLiMei(MobileAdvertisingYouMi aym,String deviceid){
		if(aym != null){
			StringBuilder url = new StringBuilder("http://api.lmmob.com/capCallbackApi/1/?");
			url.append("appId=").append(aym.getApptype()).append("&udid=").append(aym.getDeviceid().toUpperCase()).append("&returnFormat=1");
			HttpResult code = HttpUtils.getUrlAsString(url.toString());
			if(code.isSuccess()){
				aym.setYmRecord(code.getResponse());
				aym.setOpenUDID(deviceid);
				mongoService.saveOrUpdateObject(aym, MongoData.DEFAULT_ID_NAME);
			}
		}
	}
			
			
	/**
	 * �ͻ�����ҳ�������
	 * �����ش���citycode����
	 */
	@RequestMapping("/api2/mobile/phoneIndexAdvert.xhtml")
	public String phoneIndexAdvert(String tag, String citycode, ModelMap model) {
		ApiAuth auth = NewApiAuthenticationFilter.getApiAuth();
		ApiUser partner = auth.getApiUser();
		if (StringUtils.isNotBlank(citycode)) {
			if (!partner.supportsCity(citycode))
				return getErrorXmlView(model, ApiConstant.CODE_PARTNER_NORIGHTS, "��֧�ֳ���" + citycode);
		} else {
			citycode = partner.getDefaultCity();
		}
		GewaCommend gc = apiMobileService.getPhoneIndexAdvertInfo(citycode, tag);
		model.put("phoneAdvert", BeanUtil.getBeanMapWithKey(gc, "id", "title", "simpleLink", "limg", "relatedid"));
		return getXmlView(model, "api/mobile/phoneIndexAdvert.vm");
	}

	/**
	 * ����IPhone/android�ֻ��ͻ��˷�������
	 * devicetoken,tag ���봫��
	 * ����������ʱδ�Ǳ��룬Ϊ�˼��ݵͿͻ��˰汾
	 * 
	 * tag:ϵͳ����android/iphone
	 * deviceid:�豸id��android��iphone�豸Ψһ��ʾ��
	 * apptype:Ӧ������
	 * devicetoken:��Ҫ���android��������android��push��Ϣ
	 * 
	 */
	@RequestMapping("/api2/mobile/addDeviceToken.xhtml")
	public String addDeviceToken( ModelMap model) {
		return getXmlView(model, "api/mobile/result.vm");
	}
	/**
	 * ��ȡ��ǰ���е�������Ϣ
	 * 
	 * @param key
	 * @param citycode
	 * @param encryptCode
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/mobile/county.xhtml")
	public String county(String citycode, ModelMap model) {
		if (citycode == null)
			return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		List<Map> countyGroup = placeService.getPlaceCountyCountMap(Cinema.class, citycode);
		model.put("countyGroup", countyGroup);
		return getXmlView(model, "api/mobile/county.vm");
	}
	/**
	 * �ֻ��ͻ���-����������Ӧ���б�
	 * 
	 * @param key
	 *           gewara�����key
	 * @param encryptCode
	 *           Md5(key+ pravitekey+sign) {@link}com.gewara.model.api.ApiUser
	 *           is attribute private sign:ǩ����ʵ�ַ�ʽ�������еķǿմ����������key,
	 *           encryptCode�⣩��ͨ��a-z�����ʹ��key=value&key=value����
	 * @param apptype
	 *           application type ,eg:cinema��bar��sport and so on
	 * @param osType
	 *           mobile os type ,eg:iphone��ANDROID
	 * @param version
	 *           default 1.0 API version.��ʱֻ����־�鿴
	 * @param model
	 *           org.springframework.ui.ModelMap
	 * @return xml view ;patterm is otherAppList.vm
	 */
	@RequestMapping("/api2/common/otherAppList.xhtml")
	public String showOtherApplicationList(String apptype, String osType, ModelMap model, HttpServletRequest request) {
		String[] params = { "apptype", "osType" };
		if (validateParamsRequired(params, request)) {
			return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "apptype or osType is null");
		}
		DBObject object = new BasicDBObject();
		object.put("ostype", osType);
		object.put("status", Status.Y);
		BasicDBList list = new BasicDBList();
		list.add(apptype);
		object.put("coverapp", new BasicDBObject("$in", list));
		List<MobileApp> mobileappList = mongoService.getObjectList(MobileApp.class, object, "sortFlag", true, 0, 100);
		model.put("appList", mobileappList);
		return getXmlView(model, "api/mobile/otherAppList.vm");
	}
	/**
	 * ��ȡӰԺȡƱ����
	 * @param key
	 * @param encryptCode
	 * @param diaryid ȡƱ��������ID
	 * @param version default 1.0 API version.��ʱֻ����־�鿴
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/common/getTicketHelp.xhtml")
	public String getTicketHelp(Long diaryid, ModelMap model, HttpServletRequest request){
		if(diaryid == null){
			return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR,"diaryid����Ϊ��");
		}
		String diaryContent = blogService.getDiaryBody(diaryid);
		if(diaryContent.indexOf("http://img") != -1){
			diaryContent = StringUtils.replace(diaryContent, "/userfiles", "/sw300h300/userfiles");
		}else{
			diaryContent = StringUtils.replace(diaryContent, "/userfiles", config.getString("picPath") + "sw300h300/userfiles");
		}
		diaryContent = StringUtils.replace(diaryContent, "src=\"/sw300h300", "src=\"" + config.getString("picPath") + "sw300h300");
		diaryContent = diaryContent.replace("style", "css");
		model.put("diaryContent",diaryContent);
		return getXmlView(model, "api/mobile/ticketHelp.vm");
	}
	/**
	 * validate parameter is required
	 * 
	 * @param params
	 *           eg:{"apptype","osType"}
	 * @param request
	 *           HttpServletRequest
	 * @return if all parameter is not blank ,return true
	 */
	private boolean validateParamsRequired(String[] params, HttpServletRequest request) {
		if (params == null) {
			return false;
		}
		for (String str : params) {
			if (!StringUtils.isBlank(request.getParameter(str))) {
				return false;
			}
		}
		return true;
	}
	@RequestMapping(value = "/api2/mobile/relateCount.xhtml")
	public String movieRelateCount(String citycode, String fields, String tag, Long relatedid, ModelMap model){
		if(StringUtils.isBlank(citycode) || StringUtils.isBlank(fields) || StringUtils.isBlank(tag) || relatedid==null){
			return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR,"ȱ�ٲ���");
		}
		int newsCount=0, activityCount=0, commentCount=0, diaryCount=0, pictureCount=0;
		List<String> filedList = Arrays.asList(fields.split(","));
		if(filedList.contains("newsCount")){
			newsCount = newsService.getNewsCount(citycode, tag, null, relatedid, null);
		}
		String tag1 = null, category1 = null; Long relatedid1=null, categoryid1 = null;
		boolean isatag = false;
		if(ServiceHelper.isTag(tag)){
			tag1 = tag;
			isatag = true;
			relatedid1 = relatedid;
		}else if(ServiceHelper.isCategory(tag)){
			category1 = tag;
			categoryid1 = relatedid;
			isatag = true;
		}else {
			activityCount = 0;
		}
		if(isatag){
			if(filedList.contains("activityCount")){
				ErrorCode<Integer> code = synchActivityService.getCurrActivityCount(citycode, null, null, tag1, relatedid1, category1, categoryid1, null, null);
				if(code.isSuccess()) activityCount = code.getRetval();
			}
		}
		if(filedList.contains("commentCount")){
			commentCount = commentService.getCommentCountByRelatedId(TagConstant.TAG_MOVIE, relatedid);
		}
		if(filedList.contains("diaryCount")){
			String type = null;
			if(StringUtils.equals(tag, TagConstant.TAG_MOVIE)) type = DiaryConstant.DIARY_TYPE_COMMENT;
			diaryCount = diaryService.getDiaryCount(Diary.class, citycode, type, TagConstant.TAG_MOVIE, relatedid);
		}
		if(filedList.contains("pictureCount")){
			pictureCount = pictureService.getPictureCountByRelatedid(TagConstant.TAG_MOVIE, relatedid);
			pictureCount = pictureCount + pictureService.getMemberPictureCount(relatedid, TagConstant.TAG_MOVIE, null, TagConstant.FLAG_PIC, Status.Y);
		}
		model.put("newsCount", newsCount);
		model.put("activityCount", activityCount);
		model.put("commentCount", commentCount);
		model.put("diaryCount", diaryCount);
		model.put("pictureCount", pictureCount);
		return getXmlView(model, "api2/mobile/relateCount.vm");
	}
	
}
