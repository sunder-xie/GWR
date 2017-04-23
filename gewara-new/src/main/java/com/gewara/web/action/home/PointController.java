/**
 * 
 */
package com.gewara.web.action.home;

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

import com.gewara.command.CommentCommand;
import com.gewara.constant.DramaConstant;
import com.gewara.constant.MemberConstant;
import com.gewara.constant.PointConstant;
import com.gewara.constant.Status;
import com.gewara.constant.SysAction;
import com.gewara.constant.TagConstant;
import com.gewara.constant.content.SignName;
import com.gewara.constant.sys.CacheConstant;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.drama.Drama;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.drama.Theatre;
import com.gewara.model.goods.TicketGoods;
import com.gewara.model.movie.Cinema;
import com.gewara.model.movie.Movie;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.GymOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.user.Festival;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.model.user.Point;
import com.gewara.model.user.ShareMember;
import com.gewara.service.OperationService;
import com.gewara.service.bbs.CommonPartService;
import com.gewara.service.bbs.UserMessageService;
import com.gewara.service.member.PointService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CacheDataService;
import com.gewara.untrans.CacheService;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.PictureComponent;
import com.gewara.untrans.ShareService;
import com.gewara.untrans.gym.SynchGymService;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.RelatedHelper;
import com.gewara.util.StringUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.BaseHomeController;
import com.gewara.xmlbind.bbs.Comment;
import com.gewara.xmlbind.gym.CardItem;
import com.gewara.xmlbind.gym.RemoteGym;
/**
 * @author hxs(ncng_2006@hotmail.com)
 * @since Feb 2, 2010 10:08:18 AM
 */
@Controller
public class PointController extends BaseHomeController {
	@Autowired@Qualifier("cacheService")
	private CacheService cacheService;
	@Autowired@Qualifier("commentService")
	private CommentService commentService;
	@Autowired@Qualifier("pointService")
	private PointService pointService;
	@Autowired@Qualifier("commonPartService")
	private CommonPartService commonPartService;
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	@Autowired@Qualifier("cacheDataService")
	private CacheDataService cacheDataService;
	@Autowired@Qualifier("synchGymService")
	private SynchGymService synchGymService;
	@Autowired@Qualifier("pictureComponent")
	private PictureComponent pictureComponent;
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}
	@Autowired@Qualifier("userMessageService")
	private UserMessageService userMessageService;
	public void setUserMessageService(UserMessageService userMessageService) {
		this.userMessageService = userMessageService;
	}
	@RequestMapping("/home/acct/pointList.xhtml")
	public String getPointList(ModelMap model){
		Member member = this.getLogonMember();
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		//�Ƿ��Ѿ������������
		boolean isFinishNewTask = memberInfo.isAllNewTaskFinished();
		boolean hadReceviced = memberInfo.isReceived();
		model.put("hadReceviced", hadReceviced);//ע��ʱ������2020-04-20,���Ѿ���ȡ,��һ�û�����
		model.put("isFinishNewTask", isFinishNewTask);
		model.put("sumPoint", memberInfo.getPointvalue());
		
		model.put("addTime", cacheDataService.getHistoryUpdateTime(CacheConstant.KEY_POINTUPDATE));
		return "home/acct/pointList.vm";
	}

	@RequestMapping("/home/loginPoint.xhtml")
	public String loginPoint(){
		return "redirect:/home/sns/personIndex.xhtml";
	}
	
	@RequestMapping("/everday/acct/mygift.xhtml")
	public String myGift(@CookieValue(value = LOGIN_COOKIE_NAME, required = false)String sessid, 
			HttpServletRequest request, HttpServletResponse response, ModelMap model){
		String citycode = WebUtils.getAndSetDefault(request, response);
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member != null){
			Timestamp cur = DateUtil.getCurFullTimestamp();
			boolean isGetPoint = pointService.isGetLoginPoint(member.getId(), DateUtil.formatDate(new Date()));
			MemberInfo info = daoService.getObject(MemberInfo.class, member.getId());
			if(info.getPointvalue()>=5){
				model.put("pointIsEngouth", true);
			}
			model.put("memberInfo", info);
			//��õ�����ȡ�ĺ����¼
			List<Point> todayPointList = pointService.getPointListByMemberid(member.getId(), PointConstant.TAG_LOGIN_ACTIVIRY, DateUtil.getBeginningTimeOfDay(new Timestamp(System.currentTimeMillis())), null, null, 0, 1);
			if(!todayPointList.isEmpty())model.put("todayPoint", todayPointList.get(0));
			//��ȡ��ǰ�Ľ�����¼
			List<Point> todayRewardsPointList = pointService.getPointListByMemberid(member.getId(), PointConstant.TAG_LOGIN_ACTIVIRY_REWARDS, DateUtil.getBeginningTimeOfDay(new Timestamp(System.currentTimeMillis())), null, null, 0, 1);
			if(!todayRewardsPointList.isEmpty())model.put("todayRewardsPoint", todayRewardsPointList.get(0));
			int continunum = pointService.getPointRewardsDay(member.getId(), cur);
			model.put("continunum", continunum);
			model.put("isGetPoint", isGetPoint);
			model.put("logonMember", member);
		}
		List<GewaCommend> activityList = commonService.getGewaCommendList(citycode, SignName.MEMBER_POINT_ACTIVITY, null, null, true, 0, 4);
		RelatedHelper rh = new RelatedHelper(); 
		model.put("relatedHelper", rh);
		model.put("activityList", activityList);
		commonService.initGewaCommendList("activityList", rh, activityList);
		model.put("commentCount", commentService.searchCommentCount("ÿ�պ��", CommentCommand.TYPE_MODERATOR));
		
		//�����ȡ��������û�
		List<Map> newPointList = pointService.getRecentlyGetPointList(8);
		model.put("newPointList", newPointList);
		List<Map> luckPointList = pointService.getLuckGetPointList(6);
		model.put("luckPointList", luckPointList);
		List<Long> memberidList = BeanUtil.getBeanPropertyList(luckPointList, Long.class, "memberid", true);
		if(!memberidList.isEmpty()){
			List<Comment> commentList = commentService.searchCommentList("ÿ�պ��", CommentCommand.TYPE_MODERATOR, memberidList, 0, memberidList.size());
			Map<Long, Comment> commentMap = BeanUtil.beanListToMap(commentList, "memberid");
			model.put("commentMap", commentMap);
		}
		//���»��ִ���
		Timestamp endTime = DateUtil.getCurFullTimestamp();
		Timestamp startTime = DateUtil.addDay(endTime, -30);
		List<Map> pointDarenList = pointService.getTopPointByDateMap(startTime, endTime, Arrays.asList(PointConstant.TAG_LOGIN_ACTIVIRY_REWARDS,PointConstant.TAG_LOGIN_ACTIVIRY), 0, 5);
		model.put("pointDarenList", pointDarenList);
		//�����ж�
		Date curDate = new Date();
		Festival festival = commonPartService.getCurFestival(curDate);
		if(festival != null){
			model.put("festival", festival);
		}
		//��һ������
		Festival nextFestival = commonPartService.getNextFestival(curDate);
		model.put("nextFestival", nextFestival);
		model.putAll(pictureComponent.getHeadData(TagConstant.TAG_POINT, 0l));
		return "sns/hongbao/index.vm";
	}
	
	/**
	 * �����ȡ���
	 */
	@RequestMapping("/home/clickGetLoginPoint.xhtml")
	public String clickGetLoginPoint(String type, String content, String picUrl, ModelMap model){
		Member member = this.getLogonMember();
		try{
			model.put("type",type);
			Timestamp cur = new Timestamp(System.currentTimeMillis());
			if("festival".equals(type)){
				ErrorCode<Map> resultCode = pointService.addLoginPointInFestival(member);
				if(!resultCode.isSuccess()) return showJsonError(model, resultCode.getMsg());
				model.putAll(resultCode.getRetval());
			}else{
				if(StringUtils.equals(type, "brt")){
					ErrorCode code = pointService.validWeiboPoint(member);
					if(!code.isSuccess()){
						Map<String,Integer> result = new HashMap<String, Integer>();
						result.put("tag", 1);
						return showJsonError(model, result);
					}
				}
				ErrorCode<Point> point = pointService.addLoginPoint(member, type, cur);
				if(!point.isSuccess()) return showJsonError(model, point.getMsg());
				if(StringUtils.equals(type, "brt")){
					if(StringUtils.isBlank(content)) content = member.getNickname();
					content += " ����ɹ���ȡ"+point.getRetval().getPoint()+"����΢���غ�� @������������ http://www.gewara.com/everday/acct/mygift.xhtml";
					if(StringUtils.isBlank(picUrl)) picUrl = "css/home/red/mygift_wb.jpg";
					shareService.sendShareInfo("point", point.getRetval().getId(), member.getId(), content, picUrl);
				}else{
					shareService.sendShareInfo("point",point.getRetval().getId(), member.getId(), type);
				}
				model.put("pointValue", point.getRetval().getPoint());
			}
		} catch (Exception e) {
			dbLogger.error(StringUtil.getExceptionTrace(e));
			model.put("errorMsg", "�����쳣��");
		}
		int continunum = pointService.getPointRewardsDay(member.getId(), DateUtil.getCurFullTimestamp());
		model.put("continunum", continunum);
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		model.put("memberInfo", memberInfo);
		model.put("totalpv", memberInfo.getPointvalue());
		getRecentlyGetPointList(member.getId());
		return "home/acct/pointResult.vm";
	}
	private void getRecentlyGetPointList(Long memberid){
		Point point = pointService.getPointByMemberiAndTagid(memberid, PointConstant.TAG_LOGIN_ACTIVIRY, null);
		if(point != null){
			List<Map> recentlyGetPointList = pointService.getRecentlyGetPointList(8);
			Map cacheMemberInfo = memberService.getCacheMemberInfoMap(point.getMemberid());
			Map map = new HashMap();
			map.put("addtime", point.getAddtime());
			map.put("point", point.getPoint());
			map.put("reason", point.getReason());
			map.put("memberid", point.getMemberid());
			map.put("nickname", cacheMemberInfo.get("nickname"));
			map.put("headpic", cacheMemberInfo.get("headpicUrl"));
			recentlyGetPointList.add(0, map);
			recentlyGetPointList = new ArrayList(BeanUtil.getSubList(recentlyGetPointList, 0, 8));
			cacheService.set(CacheConstant.REGION_ONEDAY, CacheConstant.KEY_RECENTLYGETPOINT, recentlyGetPointList);
			if(point.getPoint()>10){
				cacheService.remove(CacheConstant.REGION_ONEDAY, CacheConstant.KEY_LUCKGETPOINT);
			}
		}
	}
	@RequestMapping("/ajax/shareGetPoint.xhtml")
	public String shareGetPoint(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, HttpServletRequest request, Long orderId, ModelMap model){
		String ip = WebUtils.getRemoteIp(request);
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return showJsonError(model, "���ȵ�¼��");
		boolean allow = operationService.updateOperation("shareGetPoint" + member.getId(), OperationService.HALF_MINUTE, 1);
		if(!allow) return showJsonError(model, "���������Ƶ�������Ժ����ԣ�");
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if(order == null) return showJsonError(model, "�����ڴ˶�����");
		if(!member.getId().equals(order.getMemberid())) return showJsonError(model, "���ݴ���");
		if(!order.isPaidSuccess()) return showJsonError(model, "�������ǽ��׳ɹ�״̬��");
		if(order.getPaidtime().before(DateUtil.addDay(DateUtil.currentTime(), -7))) return showJsonError(model, "����ʱ����ʧЧ��");
		List<ShareMember>  shareMemberList = shareService.getShareMemberByMemberid(Arrays.asList(MemberConstant.SOURCE_SINA, MemberConstant.SOURCE_QQ), member.getId());
		if(shareMemberList.isEmpty()){
			return showJsonError(model, "isNotBind");
		}else{
			for(ShareMember shareMember : shareMemberList) {
				if(StringUtils.equals(shareMember.getSource(), MemberConstant.SOURCE_SINA)){
					Map<String,String> otherMap = JsonUtils.readJsonToMap(shareMember.getOtherinfo());
					if(StringUtils.equals(otherMap.get("accessrights"), "0") || shareMember.getAddtime() == null || otherMap.get("expires") == null){
						return showJsonError(model, "isNotBind");
					}
					Timestamp addtime = shareMember.getAddtime();
					int expires = Integer.parseInt(otherMap.get("expires")+"") - 60;
					Timestamp duetime = DateUtil.addSecond(addtime, expires);
					if(!DateUtil.isAfter(duetime)){
						shareService.updateShareMemberRights(shareMember);
						return showJsonError(model, "isNotBind");
					}
				}
			}
		}
		Point point = pointService.getPointByMemberiAndTagid(member.getId(), PointConstant.TAG_SHARE_ORDER, orderId);
		if(point != null) return showJsonError(model, "���ѳɹ�����������΢���������ظ�������");
		String reason = "��Ʊ�ɹ�����΢����";
		
		//��5����
		Point p = pointService.addPointInfo(member.getId(), 5, reason, PointConstant.TAG_SHARE_ORDER, orderId, null);
		if(p != null){
			String picUrl = "", content = "", url = "http://www.gewara.com/", body = "";
			if(order instanceof TicketOrder){
				TicketOrder ticketOrder = (TicketOrder)order;
				Movie movie = daoService.getObject(Movie.class, ticketOrder.getMovieid());
				Cinema cinema = daoService.getObject(Cinema.class, ticketOrder.getCinemaid());
				if(movie != null && cinema != null ){
					url += "movie/"+movie.getId();
					String playDate = JsonUtils.getJsonValueByKey(order.getDescription2(), "����");
					content = "�ҵ����ָ��������� @������������ ѡ��������"+DateUtil.format(DateUtil.parseDate(playDate,"MM��dd�� HH:mm"),"MM��dd��")+"#"+cinema.getName()+"#"
					+"#"+movie.getName()+"#��ӰƱ "+url;
					if(StringUtils.isNotBlank(movie.getLogo())) picUrl = movie.getLogo();
					body = "��ϲ������Ӱ�ճ̻�5�����ֽ�����";
				}
			}else if(order instanceof GymOrder){
				GymOrder gymOrder = (GymOrder)order;
				RemoteGym gym = null;
				ErrorCode<RemoteGym> gymCode = synchGymService.getRemoteGym(gymOrder.getGymid(), true);
				if(gymCode.isSuccess()) gym = gymCode.getRetval();
				CardItem gymCardItem = null;
				ErrorCode<CardItem> itemCode = synchGymService.getGymCardItem(gymOrder.getGci(), true);
				if(itemCode.isSuccess()) gymCardItem = itemCode.getRetval();
				if(gym != null && gymCardItem != null){
					url += "gym/card/" + gymCardItem.getId();
					content = "���� @������������ ������ " + gym.getRealBriefname() + " ����ݵ� " + gymCardItem.getName() + " ��������������ԤԼȥ�Ͽ�Ŷ������һ��������" + url;
					if(StringUtils.isNotBlank(gymCardItem.getLogo())) picUrl = gymCardItem.getLogo();
					body = "��ϲ���������Ϣ��5�����ֽ�����";
				}
			}else if(order instanceof GoodsOrder){
				GoodsOrder goodsOrder = (GoodsOrder)order;
				TicketGoods goods = daoService.getObject(TicketGoods.class, goodsOrder.getGoodsid());
				if(goods != null){
					Object relate = relateService.getRelatedObject(goods.getTag(), goods.getRelatedid());
					Object category = relateService.getRelatedObject(goods.getCategory(), goods.getCategoryid());
					url = "" + goods.getCategory() +"/" +goods.getCategoryid();
					content = "���� @������������ ������ " + (String)BeanUtil.get(relate, "name") + " ���ݵ� " + (String)BeanUtil.get(category, "name") + "��Ŀ" + goods.getGoodsname() + "������һ��������" + url;
					if(StringUtils.isNotBlank((String)BeanUtil.get(category, "limg"))) picUrl = (String)BeanUtil.get(category, "limg");
					body = "��ϲ�����Ʊ��Ϣ��5�����ֽ�����";
				}
				
			}else if(order instanceof DramaOrder){
				DramaOrder dramaOrder = (DramaOrder)order;
				OpenDramaItem item = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", dramaOrder.getDpid());
				Theatre theatre = daoService.getObject(Theatre.class, dramaOrder.getTheatreid());
				Drama drama = daoService.getObject(Drama.class, dramaOrder.getDramaid());
				url = "drama/" +dramaOrder.getDramaid();
				content = "@��������������";
				if(item.isOpenseat()){
					content += "(ѡ��)";
				}
				content += "������";
				if(item.hasPeriod(Status.Y)){
					content += DateUtil.format(item.getPlaytime(), "M��d��");
				}
				content += " #" + theatre.getRealBriefname() + "#" + drama.getRealBriefname() + "#��(" + (StringUtils.equals(DramaConstant.TYPE_OTHER, drama.getDramatype()) ? "�ݳ�": DramaConstant.getDramaTypeText(drama.getDramatype())) + ")Ʊ������" + order.getTotalAmount()+ "��Ԫ���԰�һ�£���Ľ����ľ�У���ľ�У��� @������׷���� " + url;
				
				if(StringUtils.isNotBlank(drama.getLimg())) picUrl = drama.getLimg();
				body = "��ϲ�����Ʊ��Ϣ��5�����ֽ�����";
			}
			if(StringUtils.isNotBlank(content) && StringUtils.isNotBlank(body)){
				shareService.sendShareInfo(PointConstant.TAG_SHARE_ORDER, p.getId(), member.getId(), content, picUrl);
				userMessageService.sendSiteMSG(member.getId(), SysAction.STATUS_RESULT, null, body);
			}
			return showJsonSuccess(model);
		}
		return showJsonError(model, "�������");
	}
}
