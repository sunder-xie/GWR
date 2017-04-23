package com.gewara.web.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;

import com.gewara.constant.SmsConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.json.MemberStats;
import com.gewara.model.acl.GewaraUser;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.mongo.MongoService;
import com.gewara.service.bbs.ModeratorService;
import com.gewara.untrans.BaoKuService;
import com.gewara.untrans.CommonService;
import com.gewara.untrans.GewaMailService;
import com.gewara.untrans.MemberCountService;
import com.gewara.untrans.UntransService;
import com.gewara.untrans.impl.ControllerService;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.RandomUtil;
import com.gewara.util.ValidateUtil;

public class BaseHomeController extends AnnotationController {
	@Autowired@Qualifier("memberCountService")
	protected MemberCountService memberCountService;
	@Autowired@Qualifier("commonService")
	protected CommonService commonService;
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}
	@Autowired@Qualifier("controllerService")
	protected ControllerService controllerService;
	public void setControllerService(ControllerService controllerService) {
		this.controllerService = controllerService;
	}
	
	@Autowired@Qualifier("moderatorService")
	protected ModeratorService moderatorService;
	public void setModeratorService(ModeratorService moderatorService) {
		this.moderatorService = moderatorService;
	}
	@Autowired@Qualifier("gewaMailService")
	protected GewaMailService gewaMailService;
	public void setGewaMailService(GewaMailService gewaMailService) {
		this.gewaMailService = gewaMailService;
	}
	@Autowired@Qualifier("untransService")
	protected UntransService untransService;
	public void setUntransService(UntransService untransService) {
		this.untransService = untransService;
	}
	@Autowired@Qualifier("baoKuService")
	protected BaoKuService baoKuService;
	
	protected final Member getLogonMember(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth == null) return null;
		if(auth.isAuthenticated() && !auth.getName().equals("anonymous")){//��¼
			GewaraUser user = (GewaraUser) auth.getPrincipal();
			if(user instanceof Member) {
				return (Member) user;
			}
		}
		return null;
	}
	@Autowired
	protected MongoService mongoService;
	protected void getHomeLeftNavigate(MemberInfo memberInfo, ModelMap model){
		Map dataMap = memberCountService.getMemberCount(memberInfo.getId());
		model.put("commentCount", dataMap.get(MemberStats.FIELD_COMMENTCOUNT));
		model.put("treasureCount", dataMap.get(MemberStats.FIELD_ATTENTIONCOUNT));
		model.put("fansCount", dataMap.get(MemberStats.FIELD_FANSCOUNT));
		//����Ȧ��
		this.hotCommuList(memberInfo.getId(), model);
		//����Ȥ����
		this.myIntersetedPerson(memberInfo.getId(), model);
		//���Ż
		this.getRecommendActivtyList(memberInfo.getId(), model);
		//���Ż���
		model.put("moderatorList", this.getRandomHotModerator(5));
		model.put("pageNoModerator", 1);
		//��ǩ
		String favortags = memberInfo.getFavortag();
		if(StringUtils.isNotBlank(favortags)){
			List<String> myfavTags = Arrays.asList(StringUtils.split(favortags, "|"));
			model.put("myfavTags", myfavTags);
		}
	}
	
	//����Ȧ��
	protected void hotCommuList(Long memberid, ModelMap model){
		model.put("commuMapList", getRandomData(memberid, "recommendCommu", 3));
	}
	
	//�Ҹ���Ȥ����
	protected void myIntersetedPerson(Long memberid, ModelMap model){
		model.put("memberMapList", getRandomData(memberid, "recommedPerson", 3));
	}
	
	//�õ��Ƽ��
	protected void getRecommendActivtyList(Long memberid, ModelMap model){
		model.put("activityMapList", getRandomData(memberid, "recomendActivity", 3));
	}
	
	protected List getRandomHotModerator(int length){
		List<Map> moderatorList = moderatorService.updateHotModeratorFromCache(0,100);
		moderatorList = RandomUtil.getRandomObjectList(moderatorList, length);
		return moderatorList;
	}
	
	private List<Map> getRandomData(Long memberid, String key, int length){
		Map params = mongoService.findOne(MongoData.NS_MEMBER_INFO, "myid", memberid);
		List<Map> mapList = new ArrayList<Map>();
		if(params != null && params.get(key) != null){
			String jsonStr = (String)params.get(key);
			List<Map> memberList = JsonUtils.readJsonToObject(List.class, jsonStr);
			mapList = RandomUtil.getRandomObjectList(memberList, length);
		}
		return mapList;
	}
	
	protected void sendWarning(String msg, Member member, String... mobiles){
		if(ArrayUtils.isEmpty(mobiles)) return;
		Timestamp cur = DateUtil.getCurFullTimestamp();
		for (String mobile : mobiles) {
			if(ValidateUtil.isMobile(mobile)){
				String tmp = "����"+DateUtil.getCurTimeStr()+" �ڸ����������������˰�"+ msg + "��������㱾�˲������벻�����˶��ţ�";
				String ukey = "M" + DateUtil.format(DateUtil.getCurFullTimestamp(), "yyMMddHHmmss");
				SMSRecord sms = new SMSRecord(ukey, mobile, tmp, cur, DateUtil.addHour(cur, 2), SmsConstant.SMSTYPE_NOW);
				untransService.sendMsgAtServer(sms, false);
			}else if(ValidateUtil.isEmail(mobile)){
				String tmp = "����" + DateUtil.formatTime(cur) + "�ڸ����������������˰�"+ msg + "��������㱾�˲������벻�������ʼ���";
				gewaMailService.sendAdviseEmail(member.getNickname(), tmp, mobile);
			}
		}
	}
}
