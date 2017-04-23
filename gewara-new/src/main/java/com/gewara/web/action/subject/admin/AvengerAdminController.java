package com.gewara.web.action.subject.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.SmsConstant;
import com.gewara.constant.SysAction;
import com.gewara.constant.sys.MongoData;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Member;
import com.gewara.mongo.MongoService;
import com.gewara.service.bbs.UserMessageService;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.UntransService;
import com.gewara.util.DateUtil;
import com.gewara.util.StringUtil;
import com.gewara.web.action.admin.BaseAdminController;
import com.gewara.web.util.PageUtil;

@Controller
public class AvengerAdminController extends BaseAdminController {
	public static final String TAG_AVENGER = "TAG_AVENGER";
	
	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	public void setUntransService(UntransService untransService) {
		this.untransService = untransService;
	}
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	public void setMongoService(MongoService mongoService){
		this.mongoService = mongoService;
	}
	@Autowired@Qualifier("userMessageService")
	private UserMessageService userMessageService;
	public void setUserMessageService(UserMessageService userMessageService){
		this.userMessageService = userMessageService;
	}
	
	@RequestMapping("/admin/newsubject/avengerIndex.xhtml")
	public String avengerIndex(){
		return "admin/newsubject/avengerIndex.vm";
	}
	
	@RequestMapping("/admin/newsubject/joinAvenger.xhtml")
	public String joinAvenger(Integer pageNo, ModelMap model){
		if(pageNo == null) pageNo = 0;
		int rowsPer = 30;
		int firstPer = pageNo * rowsPer;
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.TAG_AVENGER);
		params.put(MongoData.ACTION_TAG, "prize");
		int count = mongoService.getCount(MongoData.NS_ACTIVITY_SINGLES, params);
		List<Map> joinMember = mongoService.find(MongoData.NS_ACTIVITY_SINGLES, params, firstPer, rowsPer);
		PageUtil pageUtil = new PageUtil(count, rowsPer, pageNo, "admin/newsubject/joinAvenger.xhtml");
		pageUtil.initPageInfo();
		model.put("pageUtil", pageUtil);
		model.put("joinMember", joinMember);
		return "admin/newsubject/joinAvenger.vm";
	}
	
	@RequestMapping("/admin/newsubject/shareAvenger.xhtml")
	public String shareAvenger(Integer pageNo, ModelMap model){
		if(pageNo == null) pageNo = 0;
		int rowsPer = 30;
		int firstPer = pageNo * rowsPer;
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.TAG_AVENGER);
		params.put(MongoData.ACTION_TAG, "share");
		int count = mongoService.getCount(MongoData.NS_ACTIVITY_SINGLES, params);
		List<Map> joinMember = mongoService.find(MongoData.NS_ACTIVITY_SINGLES, params, "addtime", false, firstPer, rowsPer);
		PageUtil pageUtil = new PageUtil(count, rowsPer, pageNo, "admin/newsubject/shareAvenger.xhtml");
		pageUtil.initPageInfo();
		model.put("pageUtil", pageUtil);
		model.put("joinMember", joinMember);
		return "admin/newsubject/shareAvenger.vm";
	}

	@RequestMapping("/admin/newsubject/avengerMember.xhtml")
	public String avengerManage(ModelMap model){
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.TAG_AVENGER);
		params.put(MongoData.ACTION_TAG, "prize");
		List<Map> memberList = mongoService.find(MongoData.NS_ACTIVITY_COMMON_MEMBER, params, "addtime", false);
		model.put("memberList", memberList);
		return "admin/newsubject/avengerMember.vm";
	}
	
	@RequestMapping("/admin/newssubject/saveAvengerMember.xhtml")
	public String saveAvengerMember(Long memberid, ModelMap model){
		Member member = daoService.getObject(Member.class, memberid);
		if(member == null) return showJsonError(model, "���û������ڣ�");
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.TAG_AVENGER);
		params.put(MongoData.ACTION_TAG, "prize");
		params.put("memberid", memberid);
		params.put("membername", member.getNickname());
		Map dataMap = mongoService.findOne(MongoData.NS_ACTIVITY_COMMON_MEMBER, params);
		if(dataMap != null) return showJsonError(model, "���û������Ѵ��ڣ�");
		params.put(MongoData.DEFAULT_ID_NAME, ServiceHelper.assignID(MongoData.TAG_AVENGER));
		params.put("addtime", DateUtil.currentTime());
		if(member.isBindMobile()){
			SMSRecord sms = new SMSRecord(member.getMobile()); 
			String msg = "��ϲ���Ϊ�������ӿ������������ˡ�����������û��������������ѿ���Ӱ�ʸ�";
			sms.setContent(msg);
			sms.setTradeNo(MongoData.TAG_AVENGER + DateUtil.format(DateUtil.getCurFullTimestamp(),"yyyyMMddHHmmss")+StringUtil.getRandomString(5));
			sms.setSendtime(DateUtil.getCurFullTimestamp());
			sms.setValidtime(DateUtil.addHour(DateUtil.getCurFullTimestamp(), 2) );
			sms.setSmstype(SmsConstant.SMSTYPE_ACTIVITY);
			untransService.sendMsgAtServer(sms, true);
		}
		String siteMsg = "�𾴵����ѣ���ϲ���Ϊ�������ӿ������������ˡ�����������û���" +
				"�����������ѿ���Ӱ�ʸ񣡼�����15�����ڸ��������Ϲ���4�ŵ�ӰƱ����Ʊ��15����" +
				"�����ڸ�������ȫ����������������˻�������ǰ�������Ĺ�Ӱʱ�䣬����ʧЧ��ף����Ӱ��졣";
		userMessageService.sendSiteMSG(member.getId(),  SysAction.STATUS_RESULT, null, siteMsg);
		mongoService.saveOrUpdateMap(params, MongoData.DEFAULT_ID_NAME, MongoData.NS_ACTIVITY_COMMON_MEMBER);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/admin/newssubject/delAvengerMember.xhtml")
	public String delAvengerMember(Long memberid, ModelMap model){
		Member member = daoService.getObject(Member.class, memberid);
		if(member == null) return showJsonError(model, "���û������ڣ�");
		Map params = new HashMap();
		params.put(MongoData.ACTION_TYPE, MongoData.TAG_AVENGER);
		params.put(MongoData.ACTION_TAG, "prize");
		params.put("memberid", memberid);
		Map dataMap = mongoService.findOne(MongoData.NS_ACTIVITY_COMMON_MEMBER, params);
		if(dataMap == null) return showJsonError(model, "���û����������ڣ�");
		mongoService.removeObjectById(MongoData.NS_ACTIVITY_COMMON_MEMBER, MongoData.DEFAULT_ID_NAME, (String)dataMap.get(MongoData.DEFAULT_ID_NAME));
		return showJsonSuccess(model);
	}
}
