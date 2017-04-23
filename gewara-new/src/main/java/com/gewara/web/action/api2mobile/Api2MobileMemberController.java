package com.gewara.web.action.api2mobile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.TagConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.json.MemberStats;
import com.gewara.json.PlayItemMessage;
import com.gewara.model.bbs.CustomerQuestion;
import com.gewara.model.common.BaseEntity;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.movie.Cinema;
import com.gewara.model.movie.CinemaProfile;
import com.gewara.model.movie.Movie;
import com.gewara.model.pay.BuyItem;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.SellSeat;
import com.gewara.model.user.Member;
import com.gewara.model.user.Treasure;
import com.gewara.pay.PayUtil;
import com.gewara.service.MessageService;
import com.gewara.service.bbs.CustomerQuestionService;
import com.gewara.service.content.RecommendService;
import com.gewara.service.gewapay.ElecCardService;
import com.gewara.service.member.TreasureService;
import com.gewara.service.order.PubSaleService;
import com.gewara.untrans.MemberCountService;
import com.gewara.untrans.WalaApiService;
import com.gewara.untrans.activity.SynchActivityService;
import com.gewara.untrans.drama.TheatreOrderService;
import com.gewara.untrans.sport.SportUntransService;
import com.gewara.util.DateUtil;
import com.gewara.util.VmUtils;
import com.gewara.web.action.api.ApiAuth;
import com.gewara.web.action.api.BaseApiController;
import com.gewara.web.filter.NewApiAuthenticationFilter;

@Controller
public class Api2MobileMemberController extends BaseApiController {
	@Autowired@Qualifier("treasureService")
	private TreasureService treasureService;
	@Autowired@Qualifier("elecCardService")
	private ElecCardService elecCardService;
	@Autowired@Qualifier("messageService")
	private MessageService messageService;
	@Autowired@Qualifier("sportUntransService")
	private SportUntransService sportUntransService;
	@Autowired@Qualifier("customerQuestionService")
	private CustomerQuestionService customerQuestionService;
	@Autowired@Qualifier("theatreOrderService")
	private TheatreOrderService theatreOrderService;
	@Autowired@Qualifier("pubSaleService")
	private PubSaleService pubSaleService;
	@Autowired@Qualifier("walaApiService")
	private WalaApiService walaApiService;
	@Autowired@Qualifier("synchActivityService")
	private SynchActivityService synchActivityService;
	public void setActivityRemoteService(SynchActivityService synchActivityService) {
		this.synchActivityService = synchActivityService;
	}
	@Autowired@Qualifier("memberCountService")
	private MemberCountService memberCountService;
	@Autowired@Qualifier("recommendService")
	private RecommendService recommendService;
	/**
	 * һ�������Ƿ��ע
	 */
	@RequestMapping("/api2/mobile/isCollection.xhtml")
	public String isCollection(String tag, long relatedid,String memberEncode, ModelMap model){
		Member member = memberService.getMemberByEncode(memberEncode);
		if(member == null) {
			return getErrorXmlView(model, ApiConstant.CODE_MEMBER_NOT_EXISTS, "�û������ڣ�");
		}
		Treasure treasure = treasureService.getTreasureByTagMemberidRelatedid(tag, member.getId(), relatedid, "collect");
		if(treasure != null){
			model.put("isCollection", true);
		}else{
			model.put("isCollection", false);
		}
		return getXmlView(model, "api/mobile/isCollect.vm");
	}
	/**
	 * ���ӰԺ����Ӱ��ע
	 */
	@RequestMapping("/api2/mobile/addCollection.xhtml")
	public String addCollection(ModelMap model, 
			String tag, Long relatedid, String memberEncode,String isPush,String osType,String appVersion){
		Member member = memberService.getMemberByEncode(memberEncode);
		if(member == null) return getErrorXmlView(model, ApiConstant.CODE_MEMBER_NOT_EXISTS, "�û������ڣ�");
		Treasure treasure = treasureService.getTreasureByTagMemberidRelatedid(tag, member.getId(), relatedid, "collect");
		if(StringUtils.equals(tag,"member")){
			if(treasure != null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���û����ѹ�ע����");
			Member myMember = daoService.getObject(Member.class, relatedid);
			if(myMember==null)return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
			if(StringUtils.equals(member.getId()+"", relatedid+"")) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܹ�ע�Լ���"); 
			//��ע����˿����һ
			memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_ATTENTIONCOUNT, 1, true);
			//��ע����˿����һ
			memberCountService.updateMemberCount(relatedid, MemberStats.FIELD_FANSCOUNT, 1, true);
			//���һ����˿֪ͨ
			recommendService.memberAddFansCount(relatedid, MongoData.MESSAGE_FANS_ADD, MongoData.MESSAGE_FANS, 1);
		}else if(StringUtils.equals(tag,TagConstant.TAG_ACTIVITY)){
			if(treasure != null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û���ѹ�ע����");
			synchActivityService.addClickedtimes(relatedid);
		}else if(StringUtils.equals(tag,TagConstant.TAG_GYM)){
			if(treasure != null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�ó������ѹ�ע����");
		}else{
			if(treasure != null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "����Ŀ���ѹ�ע����");
			BaseEntity relate = (BaseEntity) relateService.getRelatedObject(tag, relatedid);
			if(relate == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "�ղص���Ŀ�����ڣ�");
			if(StringUtils.equals("movie", tag) && StringUtils.equals("Y", isPush)){
				this.savePlayItemMessage(member.getId(), (Movie)relate, osType.toLowerCase(), appVersion);
			}
			relate.addCollection();
			daoService.saveObject(relate);
		}
		treasure = new Treasure(member.getId(), tag, relatedid, Treasure.ACTION_COLLECT);
		walaApiService.addTreasure(treasure);
		daoService.saveObject(treasure);
		return getXmlView(model, "api/mobile/result.vm");
	}
	
	private void savePlayItemMessage(long memberId, Movie movie,String osType,String appVersion){
		String msg = "��ܰ��ʾ����Ӱ��" + movie.getName() + "���ѿ�����Ʊ����ӭ��Ʊ�ۿ���#version#" + appVersion;
		nosqlService.addPlayItemMessage(memberId, "cinema", null, movie.getReleasedate(), movie.getId(), null, osType, msg);
	}
	
	private String cancelPlayItemMessage(long memberId,long movieId){
		Map params = new HashMap();
		params.put("tag", "cinema");
		params.put("categoryid", movieId);
		params.put("memberid", memberId);
		List<PlayItemMessage> playItemList = mongoService.find(PlayItemMessage.class, params);
		if (!playItemList.isEmpty()){
			mongoService.removeObjectList(playItemList, MongoData.DEFAULT_ID_NAME);
		}
		return "success";
	}
	
	private void addCollect(long relatedid,int num){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(MongoData.SYSTEM_ID, relatedid);
		paramMap.put("tag", TagConstant.TAG_CONACTIVITY);
		Map<String, Object> map = this.mongoService.findOne(MongoData.NS_SIGN, paramMap);
		if(map == null){
			map = new HashMap();
			map.put(MongoData.SYSTEM_ID, relatedid);
			map.put("count", 0);
		}
		map.put("count", new Integer((map.get("count")+""))+num);
		map.put("tag",  TagConstant.TAG_CONACTIVITY);
		mongoService.saveOrUpdateMap(map, MongoData.SYSTEM_ID, MongoData.NS_SIGN);
	}
	
	
	/**
	 * ȡ����ע
	 */
	@RequestMapping("/api2/mobile/collectDel.xhtml")
	public String collectDel(String tag, String memberEncode, Long relatedid, ModelMap model){
		if(StringUtils.isBlank(tag) || relatedid==null ||StringUtils.isBlank(memberEncode))
			return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		Member member = memberService.getMemberByEncode(memberEncode);
		if(member == null) return getErrorXmlView(model, ApiConstant.CODE_MEMBER_NOT_EXISTS, "�û������ڣ�");
		if(tag.equals("member")){
			Member myMember = daoService.getObject(Member.class, relatedid);
			if(myMember==null)return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		}
		if(StringUtils.equals(member.getId()+"", relatedid+"")) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�����쳣"); 
		Treasure treasure = treasureService.getTreasureByTagMemberidRelatedid(tag, member.getId(), relatedid, Treasure.ACTION_COLLECT);
		if(treasure==null) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�Ҳ�����¼��");
		}
		walaApiService.delTreasure(treasure.getMemberid(), treasure.getRelatedid(), treasure.getTag(), treasure.getAction());
		daoService.removeObject(treasure);
		if(tag.equals(TagConstant.TAG_ACTIVITY)){
			addCollect(relatedid,-1);
		}else if(StringUtils.equals(TagConstant.TAG_MOVIE, tag)){
			cancelPlayItemMessage(member.getId(),relatedid);
		}else if(StringUtils.equals(tag, "member")){
			memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_ATTENTIONCOUNT, 1, false);
			memberCountService.updateMemberCount(relatedid, MemberStats.FIELD_FANSCOUNT, 1, false);
		}
		return getXmlView(model, "api/mobile/result.vm");
	}
	
	/**
	 * �ҵ�Ʊȯ
	 */
	@RequestMapping("/api2/mobile/cardList.xhtml")
	public String cardList(String memberEncode,String tag,Integer from,Integer maxnum,ModelMap model) {
		Member member = memberService.getMemberByEncode(memberEncode);
		if(member == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û������ڣ�");
		if(maxnum != null && maxnum >20) maxnum = 20;
		if(tag == null )tag = TagConstant.TAG_MOVIE;
		List<ElecCard> cardList = elecCardService.getCardListByMemberid(member.getId(), tag, from, maxnum);
		Collections.sort(cardList,new PropertyComparator("status", true, false));
		model.put("cardList", cardList);
		return getXmlView(model, "api2/mobile/cardList.vm");
	}
	private <T extends GewaOrder> List<T> getOrderListByMemberId(Class<T> clazz, Long memberId, int days, int from, int maxnum) {
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		Timestamp qtime = DateUtil.addDay(cur, - days);
		DetachedCriteria query = DetachedCriteria.forClass(clazz);
		query.add(Restrictions.eq("memberid", memberId));
		query.add(Restrictions.or(Restrictions.and(
						Restrictions.like("status", OrderConstant.STATUS_NEW, MatchMode.START), 
						Restrictions.gt("validtime", cur)), 
					Restrictions.like("status", OrderConstant.STATUS_PAID, MatchMode.START)));
		query.add(Restrictions.or(Restrictions.isNull("restatus"), Restrictions.ne("restatus", GewaOrder.RESTATUS_DELETE)));
		query.add(Restrictions.ge("addtime", qtime));
		query.addOrder(Order.desc("addtime"));
		List<T> result = hibernateTemplate.findByCriteria(query, from, maxnum);
		return result;
	}
	
	/**
	 * �ҵĶ���
	 */
	@RequestMapping("/api2/mobile/orderList.xhtml")
	public String orderList(String memberEncode,String tradeNo,Integer from,Integer maxnum,ModelMap model){
		ApiAuth auth = NewApiAuthenticationFilter.getApiAuth();
		Map<Long,String> passwordMap = new HashMap<Long, String>();
		List<TicketOrder> ticketOrderList = null;
		List<TicketOrder> newOrderList = new ArrayList<TicketOrder>();
		Member member = memberService.getMemberByEncode(memberEncode);
		if(member == null) return getErrorXmlView(model,ApiConstant.CODE_MEMBER_NOT_EXISTS, "�û������ڣ�");
		Map<Long, Movie> movieMap = new HashMap<Long, Movie>();
		Map<Long, OpenPlayItem> opiMap = new HashMap<Long, OpenPlayItem>();
		TicketOrder lastUnpaidOrder = null;
		if(StringUtils.isBlank(tradeNo)){
			ticketOrderList = getOrderListByMemberId(TicketOrder.class, member.getId(), 360, from, maxnum);
			if(!VmUtils.isEmptyList(ticketOrderList)){
				for (TicketOrder ticketOrder : ticketOrderList) {
					if(ticketOrder.isPaidSuccess()  && member.isBindMobile()){
						List<SellSeat> seatList = ticketOrderService.getOrderSeatList(ticketOrder.getId());
						String password = messageService.getOrderPassword(ticketOrder, seatList);
						if(StringUtils.isNotBlank(password)){
							passwordMap.put(ticketOrder.getId(), password);
						}
					}else if(lastUnpaidOrder==null && ticketOrder.isNew()){
						lastUnpaidOrder = ticketOrder;
					}
				}
			}
		}else{
			TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeNo, false);
			if(order != null && order.getMemberid().equals(member.getId())){
				if (order.isCancel()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ܲ����ѣ���ʱ��ȡ���Ķ�����");
				if (order.isNew()) {
					lastUnpaidOrder = order;
				}else if(order.isPaidSuccess()){
					ticketOrderList =  Arrays.asList(order);
					if(member.isBindMobile()) {
						List<SellSeat> seatList = ticketOrderService.getOrderSeatList(order.getId());
						String password = messageService.getOrderPassword(order, seatList);
						if(StringUtils.isNotBlank(password)){
							passwordMap.put(order.getId(), password);
						}
					}
				}
			}else{
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "������Ϣ����");
			}
		}
		if(lastUnpaidOrder!=null){
			movieMap.put(lastUnpaidOrder.getId(), daoService.getObject(Movie.class, lastUnpaidOrder.getMovieid()));
			opiMap.put(lastUnpaidOrder.getId(), daoService.getObjectByUkey(OpenPlayItem.class, "mpid", lastUnpaidOrder.getMpid(), false));
		}
		Map<Long, BuyItem> buyItemMap = new HashMap<Long, BuyItem>();
		if(ticketOrderList!=null){
			for(TicketOrder torder : ticketOrderList){
				if(torder.getItemfee()>0){
					//TODO:���´������BuyItem
					BuyItem item = daoService.getObjectByUkey(BuyItem.class, "orderid", torder.getId(), false);
					if(item!=null) buyItemMap.put(torder.getId(), item);
				}
				newOrderList.add(torder);
				movieMap.put(torder.getId(), daoService.getObject(Movie.class, torder.getMovieid()));
				opiMap.put(torder.getId(), daoService.getObjectByUkey(OpenPlayItem.class, "mpid", torder.getMpid(), false));
			}
			if(lastUnpaidOrder!=null && newOrderList.contains(lastUnpaidOrder)) newOrderList.remove(lastUnpaidOrder);
		}
		model.put("buyItemMap", buyItemMap);
		model.put("orderList", newOrderList);
		model.put("partner", auth.getApiUser());
		model.put("passwordMap", passwordMap);
		model.put("movieMap", movieMap);
		model.put("opiMap", opiMap);
		model.put("lastUnpaidOrder", lastUnpaidOrder);
		return getXmlView(model, "api2/order/orderList.vm");
	}
	/**
	 * �ҵĶ���
	 */
	@RequestMapping("/api2/mobile/ticketOrderDetail.xhtml")
	public String ticketOrderDetail(String memberEncode, String tradeNo, ModelMap model){
		if(StringUtils.isBlank(memberEncode) || StringUtils.isBlank(tradeNo)){
			return getErrorXmlView(model,ApiConstant.CODE_PARAM_ERROR, "ȱ�ٲ���");
		}
		Member member = memberService.getMemberByEncode(memberEncode);
		if(member == null) return getErrorXmlView(model,ApiConstant.CODE_MEMBER_NOT_EXISTS, "�û������ڣ�");
		TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		if(!order.getMemberid().equals(member.getId())) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR, "���ܲ�ѯ���˵Ķ���");
		List<SellSeat> seatList = ticketOrderService.getOrderSeatList(order.getId());
		if(order.getItemfee()>0){
			//TODO:���´������BuyItem
			BuyItem item = daoService.getObjectByUkey(BuyItem.class, "orderid", order.getId(), false);
			model.put("buyItem", item);
		}
		String passmsg = "";
		if(order.isPaidSuccess()) passmsg= messageService.getOrderPassword(order, seatList);
		Cinema cinema = daoService.getObject(Cinema.class, order.getCinemaid());
		CinemaProfile profile = daoService.getObject(CinemaProfile.class, order.getCinemaid());
		model.put("order", order);
		model.put("passmsg", passmsg);
		model.put("opi", daoService.getObjectByUkey(OpenPlayItem.class, "mpid", order.getMpid(), false));
		model.put("movie", daoService.getObject(Movie.class, order.getMovieid()));
		model.put("cinema", cinema);
		if(profile!=null) model.put("diaryid", profile.getTopicid());
		model.put("subwaylineMap", placeService.getSubwaylineMap(cinema.getCitycode()));
		return getXmlView(model, "api2/order/ticketOrderDetail.vm");
	}
	
	/**
	 * ���������Ϣ
	 */
	@RequestMapping("/api2/mobile/addComplain.xhtml")
	public String addComplain(ModelMap model, String citycode, String body, String email, String phonetype,String osVersion, String appVersion, String mobileType) {
		if(StringUtils.isNotBlank(appVersion)){
			body = body + " [appVersion:" + appVersion + "]";
		}
		if(StringUtils.isNotBlank(mobileType)){
			body = body + " [mobileType:" + mobileType + "]";
		}
		if(StringUtils.isNotBlank(osVersion)){
			body = body + " [osVersion:" + osVersion + "]";
		}
		CustomerQuestion question = customerQuestionService.addCustomerQuestion(citycode, null, email, CustomerQuestion.TAG_ADVISE, body, phonetype);
		if(question == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�����������ʧ�ܣ�");
		return getXmlView(model, "api/mobile/result.vm");
	}
	
	
	/**
	 * ��Ʊȯ
	 */
	@RequestMapping("/api2/mobile/bindCardInfo.xhtml")
	public String bindCardInfo(ModelMap model){
		return notSupport(model);
	}
	
	/**
	 * ȡ������
	 */
	@RequestMapping("/api2/mobile/cancelOrder.xhtml")
	public String cancel(String memberEncode,Long orderid,String tradeNo,ModelMap model){
		ApiAuth auth = NewApiAuthenticationFilter.getApiAuth();
		GewaOrder order = null;
		if((orderid == null && StringUtils.isNotBlank(tradeNo))||(orderid != null && StringUtils.isNotBlank(tradeNo))){
			order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		}else if(orderid != null && StringUtils.isBlank(tradeNo)){
			order = daoService.getObject(GewaOrder.class, orderid);
		}else return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��������");
		if(order == null) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR, "���������ڣ�");
		Long memberid = 0l;
		if(StringUtils.isBlank(memberEncode)){
			if(order.getMemberid().equals(PartnerConstant.IPHONE)){
				memberid = auth.getApiUser().getId();
			}else{
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "������Ϣ����");
			}
		}else{
			Member member = memberService.getMemberByEncode(memberEncode);
			if(member == null) return getErrorXmlView(model,ApiConstant.CODE_MEMBER_NOT_EXISTS,"�û������ڣ�");
			if(!member.getId().equals(order.getMemberid())) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR,"����Ȩ�޲����˶�����");
			memberid = member.getId();
		}
		if(!order.isNew()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "����ȡ���ö���");
		if(order instanceof TicketOrder) {
			ticketOrderService.cancelTicketOrder2(order.getTradeNo(), memberid, OrderConstant.STATUS_USER_CANCEL, "�û�ȡ��");
		}else if(order instanceof SportOrder){
			SportOrder sorder = (SportOrder)order;
			sportUntransService.cancelSportOrder(sorder, memberid, "�û�ȡ��");
		}else if(order instanceof DramaOrder){
			theatreOrderService.cancelDramaOrder(tradeNo, memberid+"", "�û�ȡ��");
		}else if(PayUtil.isPubSaleOrder(tradeNo)){
			pubSaleService.cancelPubSaleOrder(tradeNo, memberid, "�û�ȡ��");
		}
		return getXmlView(model, "api/mobile/result.vm");
	}
	
}
