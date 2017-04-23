package com.gewara.untrans.mobile.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gewara.Config;
import com.gewara.api.pushnf.PushNotifyService;
import com.gewara.api.vo.PushNotifyVo;
import com.gewara.constant.app.PushConstant;
import com.gewara.model.movie.Movie;
import com.gewara.model.pay.TicketOrder;
import com.gewara.service.DaoService;
import com.gewara.service.member.MemberService;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.mobile.PushService;
import com.gewara.util.DateUtil;
import com.gewara.util.GewaLogger;
import com.gewara.util.JsonUtils;
import com.gewara.util.LoggerUtils;
import com.gewara.util.VmUtils;
import com.gewara.xmlbind.bbs.Comment;

@Service("pushService")
public class PushServiceImpl implements PushService {
	private final transient GewaLogger dbLogger = LoggerUtils.getLogger(getClass(), Config.getServerIp(), Config.SYSTEMID);
	@Autowired@Qualifier("daoService")
	private DaoService daoService;
	@Autowired@Qualifier("config")
	private Config config;
	@Autowired@Qualifier("commentService")
	private CommentService commentService;
	
	@Autowired@Qualifier("memberService")
	private MemberService memberService;
	
	@Autowired@Qualifier("pushNotifyService")
	private PushNotifyService pushNotifyService;
	
	@Override
	public void saveTakeTicketAutoPush(Timestamp playTime, String cname,String cnAddress, String mnmae, Long mid,TicketOrder order) {
		if(!StringUtils.equals(config.getString("sendPushServerFlag"),PushConstant.SEND_PUSH_SERVER_FLAG)){
			return;
		}
		Timestamp curTime = DateUtil.getCurFullTimestamp();
		if (getDifferMinute(playTime,curTime)>=30) {
			VmUtils vmUtils = (VmUtils) Config.getPageTools().get("VmUtils");
			Map<String,String> json = new HashMap<String,String>();
			json.put("t", "m2");
			json.put("movieid", order.getMovieid() + "");
			json.put("movielogo",vmUtils.randomPic("cw210h280",daoService.getObject(Movie.class, order.getMovieid()).getLogo()));
			json.put("moviename",mnmae);
			json.put("opentime","����" + DateUtil.format(playTime, "HH:mm"));
			json.put("cinemaname",cname);
			json.put("cinemaaddress",cnAddress);
			json.put("tradeNo",order.getTradeNo());
			json.put("hall",JsonUtils.getJsonValueByKey(order.getDescription2(), "Ӱ��"));
			json.put("seats",order.gainSeatTextFromDesc());
			json.put("ticketnum","");
			json.put("mobile",order.getMobile().substring(order.getMobile().length() - 4));
			Timestamp sentTime = DateUtil.addMinute(playTime, -30);
			StringBuilder content = new StringBuilder();
			content.append("����"); 
			content.append(DateUtil.format(playTime, "HH:mm"));
			content.append("��"); 
			content.append(StringUtils.substring(cname, 0, 10));
			content.append("�ۿ�");
			content.append(StringUtils.substring(mnmae, 0, 10));
			content.append("��");
			content.append("ȡƱ���뼰ȡƱ��λ�õ�½�ͻ��˲�ѯ��");
			json.put("content", content.toString());
			PushNotifyVo notifyVo = new PushNotifyVo(mid,PushConstant.MSGTPYE_T2,"cinema","ȡƱ����",content.toString(),PushConstant.GEWAORDERLINK,order.getTradeNo(),
					sentTime,60 * 60, order.getTradeNo());
			pushNotifyService.sentPushNotify(notifyVo);
			dbLogger.warn("msgType T2"+" memberid "+ mid+" send to push server");
		}else{
			dbLogger.warn("��ӳʱ�����Ʒ���");
		}
	}

	public void saveFilmwatchRemindAutoPush(Timestamp playTime, String cname, String caddress, String mnmae,Long movieId, Long mid, String tradeNo){
		if(!StringUtils.equals(config.getString("sendPushServerFlag"),PushConstant.SEND_PUSH_SERVER_FLAG)){
			return;
		}	
		Timestamp curTime = DateUtil.getCurFullTimestamp();
			if (getDifferMinute(playTime,curTime)>=180) {
				VmUtils vmUtils = (VmUtils) Config.getPageTools().get("VmUtils");
				Map<String,String> json = new HashMap<String,String>();
				json.put("t", "m1");
				json.put("movieid", movieId + "");
				json.put("movielogo",vmUtils.randomPic("cw210h280",daoService.getObject(Movie.class, movieId).getLogo()));
				json.put("moviename",mnmae);
				json.put("opentime","����" + DateUtil.format(playTime, "HH:mm"));
				json.put("cinemaname",cname);
				json.put("cinemaaddress",caddress);
				json.put("tradeNo",tradeNo);
				Timestamp sentTime = DateUtil.addHour(playTime, -3);
				StringBuilder content = new StringBuilder();
				content.append("����"); 
				content.append(DateUtil.format(playTime, "HH:mm"));
				content.append("��"); 
				content.append(StringUtils.substring(cname, 0, 10));
				content.append("�ۿ�");
				content.append(StringUtils.substring(mnmae, 0, 10));
				content.append("��");
				content.append("�߷�ʱ�Σ�������ǰ��СʱȡƱ��");
				json.put("content", content.toString());
				PushNotifyVo notifyVo = new PushNotifyVo(mid,PushConstant.MSGTPYE_T1,"cinema","��Ӱ����",content.toString(),PushConstant.MEMBERCENTER,null,
						sentTime,60 * 60, tradeNo);
				pushNotifyService.sentPushNotify(notifyVo);
				dbLogger.warn("msgType T1"+" memberid "+ mid+" send to push server");
			}else{
				dbLogger.warn("��ӳʱ�����Ʒ���");
			}
	}
	
	@Override
	public void saveSendWalaAutoPush(Timestamp endTime, String mnmae, Long mid,Long movieId, String tradeNo) {
		if(!StringUtils.equals(config.getString("sendPushServerFlag"),PushConstant.SEND_PUSH_SERVER_FLAG)){
			return;
		}
		Map<String,String> json = new HashMap<String,String>();
		json.put("t", "m5");
		json.put("movieid", movieId + "");
		List<Comment> commentList = commentService.getHotCommentListByRelatedId("movie","", movieId, null, null,0, 1);
		String hotWala = "";
		Comment c = null;
		String score = "";
		String nick = "";
		String avatar = "";
		if(!VmUtils.isEmptyList(commentList)){
			c = commentList.get(0);
			nick = c.getNickname();
			score = c.getGeneralmark() == null ? "" : c.getGeneralmark() + "";
			Set<Long> idSet = new HashSet<Long>();
			idSet.add(c.getMemberid());
			VmUtils vmUtils = (VmUtils) Config.getPageTools().get("VmUtils");
			Map<Long, Map> newinfoMap = memberService.getCacheMemberInfoMap(idSet);
			avatar = vmUtils.randomPic("cw50h50",(String)newinfoMap.get(c.getMemberid()).get("headpicUrl"));
			hotWala = VmUtils.perlString(VmUtils.subLastText("#",c.getBody()),config.getBasePath(),(String)config.getPageMap().get("picPath"));
		}
		json.put("wala",hotWala);
		json.put("score",score);
		json.put("nick",nick);
		json.put("avatar",avatar);
		Timestamp sentTime = DateUtil.addMinute(endTime, 10);
		StringBuilder content = new StringBuilder();
		content.append("����");
		content.append(StringUtils.substring(mnmae, 0, 10));
		content.append("���Ƿ����ڶ�ta����߶�����֣�������������һ�£�������������ѷ�����Ĺ�Ӱ���ܡ�");
		json.put("content", content.toString());
		PushNotifyVo notifyVo = new PushNotifyVo(mid,PushConstant.MSGTPYE_T4,"cinema","������������",content.toString(),
				PushConstant.MOVIE_SEND_WALA,movieId + "",sentTime,60 * 60, tradeNo);
		PushNotifyVo notifyVo1 = new PushNotifyVo(mid,PushConstant.MSGTPYE_T4,"cinema","������������",JsonUtils.writeMapToJson(json),
				PushConstant.MOVIE_SEND_WALA,movieId + "",sentTime,60 * 60, tradeNo,"2.0");
		pushNotifyService.sentPushNotify(notifyVo);
		pushNotifyService.sentPushNotify(notifyVo1);
		dbLogger.warn("msgType T4"+" memberid "+ mid+" send to push server");
	}
	private static long getDifferMinute(Timestamp time1, Timestamp time2) {
		long differ = (time1.getTime() - time2.getTime()) / 1000 / 60;
		return differ;
	}
}