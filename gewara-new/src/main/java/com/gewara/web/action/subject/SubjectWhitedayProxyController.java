package com.gewara.web.action.subject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.MemberConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.model.user.ShareMember;
import com.gewara.mongo.MongoService;
import com.gewara.service.OperationService;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.ShareService;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.ObjectId;
import com.gewara.util.StringUtil;
import com.gewara.web.action.AnnotationController;
@Controller
public class SubjectWhitedayProxyController extends AnnotationController{
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	
	@Autowired@Qualifier("commentService")
	protected CommentService commentService;
	
	@RequestMapping("/subject/proxy/whitedaySubject.xhtml")
	public String save(Long memberid,long cinemaId, String cinemaName,String countyCode,String birthday,String check, ModelMap model){
		if(memberid == null) {
			return showJsonError(model, "���ȵ�¼��");
		}
		Member member = daoService.getObject(Member.class, memberid);
		if(member == null) {
			return showJsonError(model, "���ȵ�¼��");
		}
		String checkcode = StringUtil.md5(memberid + "njmk5678");
		if(!StringUtils.equals(check, checkcode)) {
			return showJsonError(model, "���ȵ�¼��");
		}
		if(StringUtils.isBlank(cinemaName)){
			return showJsonError(model,"��ѡ��ӰԺ��");
		}
		if(StringUtils.isBlank(countyCode)){
			return showJsonError(model,"��ѡ������");
		}
		if(StringUtils.isBlank(birthday)){
			return showJsonError(model,"����д���գ�");
		}
		Map params = new HashMap();
		params.put("memberid", memberid);
		params.put("addTime", DateUtil.format(new Date(), "yyyy-MM-dd"));
		Map memberMap = mongoService.findOne(MongoData.NS_COMMON_WHITEDAY, params);
		if(memberMap == null){
			memberMap = new HashMap();
			memberMap.put("memberid", memberid);
			memberMap.put("cinemaId", cinemaId);
			memberMap.put("cinemaName", cinemaName);
			memberMap.put("countyCode", countyCode);
			memberMap.put("birthday", birthday);
			memberMap.put("addTime", DateUtil.format(new Date(), "yyyy-MM-dd"));
			memberMap.put(MongoData.SYSTEM_ID, ObjectId.uuid());
			mongoService.saveOrUpdateMap(memberMap, MongoData.SYSTEM_ID, MongoData.NS_COMMON_WHITEDAY);
			MemberInfo info = daoService.getObject(MemberInfo.class, member.getId());
			if(1 == info.getBuyticket()){
				addDrawTimes(memberid,"TEST");
			}
			return showJsonSuccess(model);
		}
	//	NS_COMMON_WHITEDAY_DRAWTIMES
		return showJsonError(model,"�����ѽ��в��ԣ�������������");
	}
	private void addDrawTimes(Long memberid,String type){
		Date now = new Date();
		Map params = new HashMap();
		params.put("memberid", memberid);
		params.put("addTime", DateUtil.format(now, "yyyy-MM-dd"));
		params.put("type", type);
		Map map = mongoService.findOne(MongoData.NS_COMMON_WHITEDAY_DRAWTIMES, params);
		if(map == null){
			map = new HashMap();
			map.put("memberid", memberid);
			map.put("addTime", DateUtil.format(now, "yyyy-MM-dd"));
			map.put("updateTime", DateUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
			map.put("freeTimes", 1);
			map.put("useTime", 0);
			map.put("type", type);
			map.put(MongoData.SYSTEM_ID, ObjectId.uuid());
			mongoService.saveOrUpdateMap(map, MongoData.SYSTEM_ID, MongoData.NS_COMMON_WHITEDAY_DRAWTIMES);
		}
	}
	@RequestMapping("/subject/proxy/getWhitedayData.xhtml")
	public String get(Long memberid,String check,ModelMap model){
		if(memberid == null) {
			return showJsonError(model, "���ȵ�¼��");
		}
		Member member = daoService.getObject(Member.class, memberid);
		if(member == null) {
			return showJsonError(model, "���ȵ�¼��");
		}
		String checkcode = StringUtil.md5(memberid + "njmk5678");
		if(!StringUtils.equals(check, checkcode)) {
			return showJsonError(model, "���ȵ�¼��");
		}
		Map params = new HashMap();
		params.put("memberid", memberid);
		params.put("addTime", DateUtil.format(new Date(), "yyyy-MM-dd"));
		Map memberMap = mongoService.findOne(MongoData.NS_COMMON_WHITEDAY, params);
		if(memberMap == null){
			return showJsonError(model, "�����ύ����Ŷ��");
		}
		Map resultMap = new HashMap();
		resultMap.put("success", true);
		return showJsonSuccess(model,JsonUtils.writeMapToJson(resultMap));
		
	}
	
	@RequestMapping("/subject/proxy/getFreeTimes.xhtml")
	public String getFreeTimes(Long memberid,String check,ModelMap model){
		if(memberid == null) {
			return showJsonError(model, "���ȵ�¼��");
		}
		Member member = daoService.getObject(Member.class, memberid);
		if(member == null) {
			return showJsonError(model, "���ȵ�¼��");
		}
		String checkcode = StringUtil.md5(memberid + "njmk5678");
		if(!StringUtils.equals(check, checkcode)) {
			return showJsonError(model, "���ȵ�¼��");
		}
		Map params = new HashMap();
		params.put("memberid", memberid);
		params.put("addTime", DateUtil.format(new Date(), "yyyy-MM-dd"));
		params.put("useTime", 0);
		List<Map> map = mongoService.find(MongoData.NS_COMMON_WHITEDAY_DRAWTIMES, params);
		Map resultMap = new HashMap();
		resultMap.put("success", true);
		resultMap.put("retval", map == null ? 0 : map.size());
		return this.showJsonSuccess(model,JsonUtils.writeMapToJson(resultMap));
	}
	//����΢��
	@RequestMapping("/subject/proxy/sharesWhitedayInfo.xhtml")
	public String sharesTicketInfo(Long memberid, String check, String content, ModelMap model){
		String checkcode = StringUtil.md5(memberid + "njmk5678");
		if(!StringUtils.equals(check, checkcode)) {
			return showJsonSuccess(model, "nologin");
		}
		Member member = daoService.getObject(Member.class, memberid);
		if(member == null) {
			return showJsonError(model, "���ȵ�¼��");
		}
		if(StringUtils.isBlank(content)) {
			return showJsonError(model, "�������ݲ���Ϊ�գ�");
		}
		boolean allow = operationService.updateOperation("shareFilmFest" + member.getId(), OperationService.HALF_MINUTE, 5);
		if(!allow) {
			return showJsonError(model, "���������Ƶ�������Ժ����ԣ�");
		}
		List<ShareMember>  shareMemberList = shareService.getShareMemberByMemberid(Arrays.asList(MemberConstant.SOURCE_SINA), member.getId());
		if(shareMemberList.isEmpty()) {
			return showJsonError(model, "���Ȱ�����΢����");
		}
		shareService.sendShareInfo("subject", null, member.getId(), content, null);
		addDrawTimes(memberid,"WEIBO");
		return showJsonSuccess(model,"success");
	}
}
