package com.gewara.web.action.subject;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.CookieConstant;
import com.gewara.constant.DrawActicityConstant;
import com.gewara.constant.MemberConstant;
import com.gewara.constant.Status;
import com.gewara.constant.TagConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.model.common.VersionCtl;
import com.gewara.model.content.News;
import com.gewara.model.content.Picture;
import com.gewara.model.content.Video;
import com.gewara.model.draw.DrawActivity;
import com.gewara.model.draw.Prize;
import com.gewara.model.draw.WinnerInfo;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.model.user.ShareMember;
import com.gewara.mongo.MongoService;
import com.gewara.service.OperationService;
import com.gewara.service.content.NewsService;
import com.gewara.service.content.PictureService;
import com.gewara.service.content.VideoService;
import com.gewara.service.drama.DramaPlayItemService;
import com.gewara.service.drama.DrawActivityService;
import com.gewara.service.order.OrderQueryService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.ShareService;
import com.gewara.untrans.UntransService;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;
import com.gewara.xmlbind.bbs.Comment;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;
@Controller
public class RedCatProxyController extends AnnotationController{
	@Autowired@Qualifier("videoService")
	private VideoService videoService;
	public void setVideoService(VideoService videoService){
		 this.videoService = videoService;
	}
	@Autowired@Qualifier("newsService")
	private NewsService newsService;
	public void setnewsService(NewsService newsService){
		this.newsService = newsService;
	}
	@Autowired@Qualifier("pictureService")
	private PictureService pictureService;
	public void setPictureService(PictureService pictureService){
		this.pictureService = pictureService;
	}
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	public void setmongoService(MongoService mongoService){
		this.mongoService = mongoService;
	}
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	public void setOperationService(OperationService operationService){
		this.operationService = operationService;
	}
	@Autowired@Qualifier("dramaPlayItemService")
	private DramaPlayItemService dramaPlayItemService;
	public void setDramaPlayItemService(DramaPlayItemService dramaPlayItemService){
		this.dramaPlayItemService = dramaPlayItemService;
	}
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	public void setShareService(ShareService shareService){
		this.shareService = shareService;
	}
	@Autowired@Qualifier("orderQueryService")
	private OrderQueryService orderQueryService;
	public void setOrderQueryService(OrderQueryService orderQueryService){
		this.orderQueryService = orderQueryService;
	}
	@Autowired@Qualifier("drawActivityService")
	private DrawActivityService drawActivityService;
	public void setDrawActivityService(DrawActivityService drawActivityService){
		this.drawActivityService = drawActivityService;
	}
	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	public void setUntransService(UntransService untransService){
		this.untransService = untransService;
	}
	@Autowired@Qualifier("commentService")
	private CommentService commentService;
	public void setCommentService(CommentService commentService){
		this.commentService = commentService;
	}
	//���־�è������
	@RequestMapping("/subject/proxy/musiccat/getMusicCatIndex.xhtml")
	public String getMusicCatIndex(Long did, ModelMap model){
		if(did == null) return showJsonError(model, "��������"); 
		List<Video> videoList = videoService.getVideoListByTag(TagConstant.TAG_DRAMA, did, 0, 4);
		return showJsonSuccess(model, JsonUtils.writeObjectToJson(videoList));
	}
	
	@RequestMapping("/subject/proxy/musiccat/getDramaPicList.xhtml")
	public String getDramaPicList(Long did, ModelMap model){
		if(did == null) return showJsonError(model, "��������"); 
		List<Picture> dramaPictureList = pictureService.getPictureListByRelatedid(TagConstant.TAG_DRAMA, did, 0, 8);
		return showJsonSuccess(model, JsonUtils.writeObjectToJson(dramaPictureList));
	}
	
	@RequestMapping("/subject/proxy/musiccat/getDramaNewsList.xhtml")
	public String getDramaNewsList(Long did, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		if(did == null) return showJsonError(model, "��������"); 
		String citycode = WebUtils.getAndSetDefault(request, response);
		List<News> newsList = newsService.getNewsList(citycode, TagConstant.TAG_DRAMA, did, "", 0, 10);
		return showJsonSuccess(model, JsonUtils.writeObjectToJson(newsList));
	}
	
	//��ȡèͼ
	@RequestMapping("/subject/proxy/redccat/getRedCatPic.xhtml")
	public String getRedCatPic(Integer from, Integer max, String orderField, ModelMap model){
		if(from == null || max== null)  return showJsonError(model, "��������");
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.DRAMA_REDCAT);
		params.put(MongoData.ACTION_STATUS, Status.Y);
		List<Map> joinCatList = mongoService.find(MongoData.NS_ACTIVITY_COMMON_PICTRUE, params, orderField, false, from, max);
		return showJsonSuccess(model, JsonUtils.writeObjectToJson(joinCatList));
	}
	//��ȡר�� ���λͼƬ
	@RequestMapping("/subject/proxy/redccat/getRedCatCommPic.xhtml")
	public String getRedCatCommPic(String type, String tag, ModelMap model){
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, type);
		params.put(MongoData.ACTION_TAG, tag);
		List picList = mongoService.find(MongoData.NS_ACTIVITY_COMMON_PICTRUE, params, MongoData.ACTION_ORDERNUM, true);
		return showJsonSuccess(model, JsonUtils.writeObjectToJson(picList));
	}
	// �ݳ�����
	@RequestMapping("/subject/proxy/redccat/getDramaPlayCount.xhtml")
	public String getDramaPlayCount(Long did, ModelMap model){
		Timestamp curTime = DateUtil.getCurFullTimestamp();
		Integer dramaPlayCount = dramaPlayItemService.getDramaCount(did, curTime);
		return showJsonSuccess(model, dramaPlayCount+"");
	}
	
	//ͳ�����вμӻ��è����
	@RequestMapping("/subject/proxy/redccat/getRedCatPicCount.xhtml")
	public String getRedCatPicCount(ModelMap model){
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.DRAMA_REDCAT);
		params.put(MongoData.ACTION_STATUS, Status.Y);
		int count = mongoService.getCount(MongoData.NS_ACTIVITY_COMMON_PICTRUE, params);
		return showJsonSuccess(model, count+"");
	}
	
	//�ϴ�ͼƬ
	@RequestMapping("/subject/ajax/uploadRedCat.xhtml")
	public String uploadRedCat(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, String newslogo, String catname, String summary, String content,HttpServletRequest request, ModelMap model){
		String statusString = isBeginning();
		if(!StringUtils.equals(statusString, "beginning")) return showJsonError(model, statusString);
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		if(StringUtils.isBlank(newslogo)) return showJsonError(model, "ͼƬ����Ϊ�գ�");
		boolean allow = operationService.updateOperation("uploadRedCat" + member.getId(), OperationService.HALF_MINUTE, 1);
		if(!allow) return showJsonError(model, "���������Ƶ�������Ժ����ԣ�");
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.DRAMA_REDCAT);
		params.put(MongoData.ACTION_MEMBERID, member.getId());
		params.put(MongoData.ACTION_STATUS, Status.Y);
		int count = mongoService.getCount(MongoData.NS_ACTIVITY_COMMON_PICTRUE, params);
		if(count > 0) return showJsonError(model, "���ͼƬ��ͨ����ˣ������ظ�������");
		if(StringUtils.isBlank(catname)) return showJsonError(model, "�ǳƲ���Ϊ�գ�");
		if (StringUtils.length(catname) > 20) return showJsonError(model, "�ǳƵ����ݲ��ܳ���20���ַ���");
		if(WebUtils.checkString(catname)) return showJsonError(model, "�ǳƵ����ݲ��ܳ��ַǷ��ַ���");
		if (StringUtils.isBlank(summary)) return showJsonError(model, "���Լ������ݲ���Ϊ�գ�");
		if (StringUtils.length(summary) > 200) return showJsonError(model, "���Լ������ݲ��ܳ���200���ַ���");
		if(WebUtils.checkString(summary))return showJsonError(model, "���Լ������ݲ��ܳ��ַǷ��ַ���");
		Map map = new HashMap();
		map.put(MongoData.ACTION_ADDTIME, DateUtil.getCurFullTimestamp());
		map.put(MongoData.ACTION_STARTTIME, DateUtil.format(DateUtil.getCurFullTimestamp(), "yyyy-MM-dd HH:mm:ss"));
		map.put(MongoData.SYSTEM_ID, System.currentTimeMillis() + StringUtil.getRandomString(5));
		map.put(MongoData.GEWA_CUP_MEMBERID, member.getId());
		map.put(MongoData.ACTION_MEMBERNAME, member.getNickname());
		map.put(MongoData.ACTION_PICTRUE_URL, newslogo);
		map.put(MongoData.ACTION_SUPPORT, 0);
		map.put(MongoData.ACTION_TYPE, MongoData.DRAMA_REDCAT);
		map.put(MongoData.ACTION_STATUS, Status.Y_NEW);
		map.put(MongoData.ACTION_TITLE, catname);
		map.put(MongoData.ACTION_CONTENT, summary);
		mongoService.saveOrUpdateMap(map, MongoData.SYSTEM_ID, MongoData.NS_ACTIVITY_COMMON_PICTRUE);
		if(StringUtils.isNotBlank(content)){
			String url = "http://www.gewara.com/zhuanti/redCarpetCat.xhtml";
			shareService.sendShareInfo(MongoData.DRAMA_REDCAT, null, member.getId(), content+url, null);
		}
		return showJsonSuccess(model);
	}
	//ͶƱ
	@RequestMapping("/subject/ajax/supportCatPic.xhtml")
	public String supportCatPic(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, String id, HttpServletRequest request, ModelMap model){
		String statusString = isBeginning();
		if(!StringUtils.equals(statusString, "beginning")) return showJsonError(model, statusString);
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		if(id == null)return showJsonError_NOT_FOUND(model);
		Map picMap = mongoService.findOne(MongoData.NS_ACTIVITY_COMMON_PICTRUE, MongoData.SYSTEM_ID, id);
		if(picMap == null) return showJsonError(model, "�����ڴ�ͼƬ��");
		if(StringUtils.equals(member.getId()+"", picMap.get("memberid")+"")) return showJsonError(model, "�����Ը��Լ�����ƬͶƱ��");
		boolean allow = operationService.updateOperation(MongoData.DRAMA_REDCAT + member.getId(), OperationService.HALF_MINUTE, 1);
		if(!allow) return showJsonError(model, "���������Ƶ�������Ժ����ԣ�");
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.DRAMA_REDCAT);
		params.put(MongoData.ACTION_MEMBERID, member.getId());
		int count = mongoService.getCount(MongoData.NS_ACTIVITY_COMMON_MEMBER, params);
		if(count >= 1) return showJsonError(model, "ͶƱ���������꣬��л��Ĳ��룡");
		Map memberMap = new HashMap();
		memberMap.put(MongoData.SYSTEM_ID, System.currentTimeMillis() + StringUtil.getRandomString(5));
		memberMap.put(MongoData.ACTION_TYPE, MongoData.DRAMA_REDCAT);
		memberMap.put(MongoData.ACTION_RELATEDID, id);
		memberMap.put(MongoData.ACTION_ADDTIME, System.currentTimeMillis());
		memberMap.put(MongoData.ACTION_MEMBERID, member.getId());
		picMap.put(MongoData.ACTION_SUPPORT, new Integer(picMap.get("support")+"") + 1);
		mongoService.saveOrUpdateMap(picMap, MongoData.SYSTEM_ID, MongoData.NS_ACTIVITY_COMMON_PICTRUE);
		mongoService.saveOrUpdateMap(memberMap, MongoData.SYSTEM_ID, MongoData.NS_ACTIVITY_COMMON_MEMBER);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/subject/proxy/redccat/getRedCatStatus.xhtml")
	public String getRedCatStatus(ModelMap model){
		return showJsonSuccess(model, isBeginning());
	}
	
	//�ʱ���ж�
	private String isBeginning(){
		Map query = new HashMap();
		query.put(MongoData.ACTION_TAG, MongoData.DRAMA_REDCAT);
		query.put(MongoData.ACTION_TYPE, MongoData.DRAMA_REDCAT);
		Map redcatMap = mongoService.findOne(MongoData.NS_ACTIVITY_SINGLES, query);
		if(redcatMap == null) return "���δ��ʼ�������ڴ���";
		Date curTimestamp = DateUtil.currentTime();
		Date startTimestamp = (Date)redcatMap.get("starttime");
		Date endTimestamp = (Date)redcatMap.get("endtime");
		if(curTimestamp.before(startTimestamp)) return "���δ��ʼ�������ڴ���";
		if(curTimestamp.after(endTimestamp)) return "��ѽ�������л���룡";
		return "beginning";
	}
	
	//��̨���
	@RequestMapping("/admin/newsubject/toredcat.xhtml")
	public String toredcat(ModelMap model){
		Map query = new HashMap();
		query.put(MongoData.ACTION_TAG, MongoData.DRAMA_REDCAT);
		query.put(MongoData.ACTION_TYPE, MongoData.DRAMA_REDCAT);
		Map redcatMap = mongoService.findOne(MongoData.NS_ACTIVITY_SINGLES, query);
		model.put("redcatMap", redcatMap);
		return "admin/newsubject/after/redcat.vm";
	}
	

	/**
	 * ���־�-èר��
	 * @param model
	 * @return
	 */
	
	//��̨���
	@RequestMapping("/admin/newsubject/tomusiccat.xhtml")
	public String tomusiccat(ModelMap model){
		Map query = new HashMap();
		query.put(MongoData.ACTION_TAG, MongoData.DRAMA_MUSICCAT);
		query.put(MongoData.ACTION_TYPE, MongoData.DRAMA_MUSICCAT);
		Map redcatMap = mongoService.findOne(MongoData.NS_ACTIVITY_SINGLES, query);
		model.put("redcatMap", redcatMap);
		return "admin/newsubject/after/musiccat.vm";
	}
	
	//��ȡ�Ƽ���Ƭ,��Ѷ
	@RequestMapping("/subject/proxy/musiccat/getMusicCatPic.xhtml")
	public String getMusicCatPic(Integer from, Integer max, String tag, ModelMap model){
		if(from == null || max== null)  return showJsonError(model, "��������");
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.DRAMA_MUSICCAT);
		params.put(MongoData.ACTION_TAG, tag);
		BasicDBObject query = new BasicDBObject();
		query.put(QueryOperators.GT, 0);
		params.put(MongoData.ACTION_ORDERNUM, query);
		List<Map> joinCatList = mongoService.find(MongoData.NS_ACTIVITY_COMMON_PICTRUE, params, MongoData.ACTION_ORDERNUM, true, from, max);
		return showJsonSuccess(model, JsonUtils.writeObjectToJson(joinCatList));
	}
	@RequestMapping("/subject/ajax/musiccatActivity.xhtml")
	public String flashClickDraw(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, 
			@CookieValue(value=CookieConstant.MEMBER_POINT,required=false)String pointxy, String tag, HttpServletRequest request, ModelMap model){
		if(StringUtils.isBlank(tag)) return showJsonError(model, "����ʧ�ܣ������ԣ�");
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		String opkey = tag + member.getId();
		boolean allow = operationService.updateOperation(opkey, 10);
		if(!allow) return showJsonError(model, "�벻Ҫ��������Ƶ����");
		DrawActivity da = daoService.getObjectByUkey(DrawActivity.class, "tag", tag, true);
		if(da == null||!da.isJoin()) return showJsonError(model, "���λδ��ʼ���ѽ�����");
		Map<String, String> otherinfoMap = VmUtils.readJsonToMap(da.getOtherinfo()); 
		if(StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_MOBILE)) && !member.isBindMobile()) return showJsonError(model, "����ֻ���");
		if(StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_EMAIL)) && !memberInfo.isFinishedTask(MemberConstant.TASK_CONFIRMREG)) return showJsonError(model, "������䣡");
		if(StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_TICKET)) && StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_MOVIEID))){
			Long movieid = Long.parseLong(otherinfoMap.get(DrawActicityConstant.TASK_MOVIEID));
			int pay = orderQueryService.getMemberOrderCountByMemberid(member.getId(), movieid);
			if(pay == 0) return showJsonError(model, "�빺�����־硶è�����ٳ齱��");
		}
		if(StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_WEIBO))){
			List<ShareMember> shareMemberList = shareService.getShareMemberByMemberid(Arrays.asList(MemberConstant.SOURCE_SINA, MemberConstant.SOURCE_QQ),member.getId());
			if(VmUtils.isEmptyList(shareMemberList)) return showJsonError(model, "���΢����");
		}
		//�Ѿ��齱����
		int drawtimes = drawActivityService.getMemberWinnerCount(member.getId(), da.getId(), da.getStarttime(), da.getEndtime());
		//�ɹ������������
		int count = drawActivityService.getInviteMemberCount(member.getId(), "musiccat", true, da.getStarttime(), da.getEndtime());
		count = count / 3;//����3������1�λ���
		//�����̺ë��ѡ
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.DRAMA_REDCAT);
		params.put(MongoData.ACTION_MEMBERID, member.getId());
		int joincount = mongoService.getCount(MongoData.NS_ACTIVITY_COMMON_PICTRUE, params);
		if(joincount > 0) count = count + 1;
		//���򻰾�è������
		int pay = orderQueryService.getMemberOrderCountByMemberid(member.getId(), 61122299L);
		if(pay > 0) count = count + 2; // ����2�λ���
		if(count+1-drawtimes <= 0) return showJsonError(model, "�齱���������꣡");
		
		VersionCtl mvc = drawActivityService.gainMemberVc(""+member.getId());
		try {
			//FIXME:��ţ����
			ErrorCode<WinnerInfo> ec = drawActivityService.baseClickDraw(da, mvc, false, member);//���ɽ�Ʒ��Ϣ
			if(ec == null || !ec.isSuccess()) return showJsonError(model, "���λδ��ʼ���ѽ�����"); //���λδ��ʼ���ѽ�����
			WinnerInfo winnerInfo = ec.getRetval();
			if(winnerInfo == null) return showJsonError(model, "ϵͳ��æ��");
			Prize prize = daoService.getObject(Prize.class, winnerInfo.getPrizeid());
			if(prize == null) return showJsonError(model, "ϵͳ��æ��");
			SMSRecord sms =drawActivityService.sendPrize(prize, winnerInfo, true);
			if(sms !=null) untransService.sendMsgAtServer(sms, false);
			Map otherinfo = VmUtils.readJsonToMap(prize.getOtherinfo());
			if(otherinfo.get(DrawActicityConstant.TASK_WALA_CONTENT) != null){
				String link = null;
				if(otherinfo.get(DrawActicityConstant.TASK_WALA_LINK) != null){
					link = otherinfo.get(DrawActicityConstant.TASK_WALA_LINK)+"";
					link = "<a href=\""+link+"\" target=\"_blank\" rel=\"nofollow\">"+"���ӵ�ַ"+"</a>";
				}
				String pointx = null, pointy = null;
				if(StringUtils.isNotBlank(pointxy)){
					List<String> pointList = Arrays.asList(StringUtils.split(pointxy, ":"));
					if(pointList.size() == 2){
						pointx = pointList.get(0);
						pointy = pointList.get(1);
					}
				}
				ErrorCode<Comment> result = commentService.addComment(member, TagConstant.TAG_TOPIC, null, otherinfo.get(DrawActicityConstant.TASK_WALA_CONTENT)+"", link, false, pointx, pointy, ip);
				if(result.isSuccess()) {
					shareService.sendShareInfo("wala",result.getRetval().getId(), result.getRetval().getMemberid(), null);
				}
			} 
			Map map = new HashMap();
			map.put("ptype", prize.getPtype());
			map.put("plevel", prize.getPlevel());
			map.put("otype", prize.getOtype());
			return showJsonSuccess(model, map);
		}catch(StaleObjectStateException e){
			return showJsonError(model, "ϵͳ��æ��");
		}catch(HibernateOptimisticLockingFailureException e){
			return showJsonError(model, "ϵͳ��æ��");
		}
	}
	//�н���Ϣ
	@RequestMapping("/subject/proxy/musiccat/getWinnerList.xhtml")
	public String getWinnerList(String tag, ModelMap model){
		DrawActivity da = daoService.getObjectByUkey(DrawActivity.class, "tag", tag, true);
		if(da == null) return showJsonError(model, "��������");
		List<Prize> prizeList = drawActivityService.getPrizeListByDid(da.getId(), new String[]{"remark","P"});
		List<Long> prizeIdList = BeanUtil.getBeanPropertyList(prizeList, Long.class, "id", true);
		List<WinnerInfo> winnerList = drawActivityService.getWinnerList(da.getId(), prizeIdList, null, null, "system", null, null, null, 0, 12);
		List<Map> infoMapList = BeanUtil.getBeanMapList(winnerList, new String[]{"memberid","nickname","prizeid"});
		for(Map info : infoMapList){
			Prize prize = daoService.getObject(Prize.class, Long.valueOf(info.get("prizeid")+""));
			info.put("plevel", prize.getPlevel());
		}
		return showJsonSuccess(model, JsonUtils.writeObjectToJson(infoMapList));
	}
	//��ȡ�ʱ��
	@RequestMapping("/subject/proxy/musiccat/getMusicCatStatus.xhtml")
	public String getRedCatStatus(String tag, String type, ModelMap model){
		return showJsonSuccess(model, isStart(tag, type));
	}
	//�ʱ���ж�
	private String isStart(String tag, String type){
		Map query = new HashMap();
		query.put(MongoData.ACTION_TAG, tag);
		query.put(MongoData.ACTION_TYPE, type);
		Map redcatMap = mongoService.findOne(MongoData.NS_ACTIVITY_SINGLES, query);
		if(redcatMap == null) return "���δ��ʼ�������ڴ���";
		Date curTimestamp = DateUtil.currentTime();
		Date startTimestamp = (Date)redcatMap.get("starttime");
		Date endTimestamp = (Date)redcatMap.get("endtime");
		if(curTimestamp.before(startTimestamp)) return "���δ��ʼ�������ڴ���";
		if(curTimestamp.after(endTimestamp)) return "��ѽ�������л���룡";
		return "beginning";
	}
}
