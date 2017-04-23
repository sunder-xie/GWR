package com.gewara.web.action.inner.mobile.draw;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.DrawActicityConstant;
import com.gewara.constant.TagConstant;
import com.gewara.json.SeeMovie;
import com.gewara.model.common.VersionCtl;
import com.gewara.model.draw.DrawActivity;
import com.gewara.model.draw.Prize;
import com.gewara.model.draw.WinnerInfo;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Member;
import com.gewara.service.drama.DrawActivityService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.draw.DrawUntransService;
import com.gewara.util.DateUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.util.VmUtils;
import com.gewara.web.action.inner.OpenApiAuth;
import com.gewara.web.action.inner.mobile.BaseOpenApiMobileController;
import com.gewara.web.filter.OpenApiMobileAuthenticationFilter;
@Controller
public class OpenApiMobileDrawController extends BaseOpenApiMobileController {
	
	@Autowired@Qualifier("drawActivityService")
	private DrawActivityService drawActivityService;
	@Autowired@Qualifier("drawUntransService")
	private DrawUntransService drawUntransService;
	
	
	@RequestMapping("/openapi/mobile/draw/joinDraw.xhtml")
	public String draw(String tag, String mobile, ModelMap model){
		if(!ValidateUtil.isMobile(mobile)){
			return getErrorXmlView(model,ApiConstant.CODE_SIGN_ERROR,"�ֻ����벻�Ϸ���");
		}
		DrawActivity da = daoService.getObjectByUkey(DrawActivity.class, "tag", tag, true);
		if(da==null) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR,"������ڣ�");
		if(!StringUtils.equals(da.getShowsite(), "wap")) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR,"���ֻ��˵Ļ���ܲμӣ�");
		Timestamp today = new Timestamp(System.currentTimeMillis());
		if(!(da.getStarttime().getTime() <= today.getTime() && today.getTime() <= da.getEndtime().getTime())){
			return getErrorXmlView(model,ApiConstant.CODE_SIGN_ERROR, "���δ��ʼ��");
		}
		
		Map<String, String> otherinfoMap = VmUtils.readJsonToMap(da.getOtherinfo());
		if(StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_ONLYMOBILE))){
			int count = drawActivityService.getWinnerCountByMobile(da.getId(), mobile);
			if(count>0) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR,"���ֻ������ѳɹ����룬�����ٴ�ʹ��");
		}
		String ip = OpenApiMobileAuthenticationFilter.getOpenApiAuth().getRemoteIp();
		String opkey = tag + ip;
		boolean allow = operationService.isAllowOperation(opkey, 60);
		if(!allow) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR,"��������Ƶ����");
		VersionCtl mvc = drawActivityService.gainMemberVc(mobile);
		try {
			//FIXME:��ţ����
			ErrorCode<WinnerInfo> ec = drawActivityService.baseClickDraw(da, mvc, false, null, null, mobile);
			if(ec == null || !ec.isSuccess()) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR, ec.getMsg());
			WinnerInfo winnerInfo = ec.getRetval();
			if(winnerInfo == null) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR, "��ȡ����ʧ��");
			Prize prize = daoService.getObject(Prize.class, winnerInfo.getPrizeid());
			if(prize == null) return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR, "��ȡ��Ʒʧ��");
			SMSRecord sms =drawActivityService.sendPrize(prize, winnerInfo, true);
			if(sms !=null) untransService.sendMsgAtServer(sms, true);
			operationService.updateOperation(opkey, 60);
		}catch(StaleObjectStateException e){
			return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR,"����ʧ�ܣ�������");
		}catch(HibernateOptimisticLockingFailureException e){
			return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR,"����ʧ�ܣ�������");
		}
		return getSingleResultXmlView(model, "success");
	}
	
	private DrawActivity getValidDrawActivity(String appSource){ 
		Timestamp curtime = DateUtil.getCurFullTimestamp();
		String hql = "from DrawActivity d where d.showsite=? and d.starttime<=? and d.endtime>? order by addtime desc";
		List<DrawActivity> activityList = hibernateTemplate.find(hql, DrawActivity.SHOWSITE_WAP, curtime, curtime);
		for(DrawActivity da : activityList){
			Map<String, String> otherinfoMap = VmUtils.readJsonToMap(da.getOtherinfo());
			String otherappsource = otherinfoMap.get(DrawActicityConstant.TASK_APPSOURCE);
			if(StringUtils.isNotBlank(otherappsource)){
				List<String> appList = Arrays.asList(StringUtils.split(otherappsource, ","));
				if(appList.contains(appSource)){
					return da;
				}
			}
		}
		return null;
	}

	@RequestMapping("/openapi/mobile/draw/joinDrawByAppsource.xhtml")
	public String joinDrawByAppsource(String appSource, ModelMap model) {
		if(StringUtils.isBlank(appSource)){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "app��Դ�Ƿ������ܲ���");
		}
		DrawActivity da = getValidDrawActivity(appSource);
		if (da == null) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "������ڣ�");
		}
		Member member = OpenApiMobileAuthenticationFilter.getOpenApiAuth().getMember();
		String mobile = member.getMobile();
		Map<String, String> otherinfoMap = VmUtils.readJsonToMap(da.getOtherinfo());
		if (StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_MOBILE))) {
			if(StringUtils.isBlank(mobile)){
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û��Ҫ���ֻ�������ܲμ�");
			}
		}
		if(StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_EMAIL))){
			if(StringUtils.isBlank(member.getEmail())){
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û��Ҫ��������ܲμ�");
			}
		}
		
		ErrorCode<String> code = getUnionValid(member, da, otherinfoMap);
		if(!code.isSuccess()){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, code.getMsg());
		}
		VersionCtl mvc = drawActivityService.gainMemberVc(mobile);
		try {
			//FIXME:��ţ����
			ErrorCode<WinnerInfo> ec = drawActivityService.baseClickDraw(da, mvc, false, member.getId(), null, mobile);
			if(ec == null || !ec.isSuccess())
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, ec.getMsg());
			WinnerInfo winnerInfo = ec.getRetval();
			if(winnerInfo == null)
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��ȡ����ʧ�ܣ�������");
			Prize prize = daoService.getObject(Prize.class, winnerInfo.getPrizeid());
			if(prize == null)
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��ȡʧ�ܣ�������");
			SMSRecord sms = drawActivityService.sendPrize(prize, winnerInfo, true);
			if(sms != null)untransService.sendMsgAtServer(sms, true);
		} catch (StaleObjectStateException e) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "����ʧ�ܣ�������");
		} catch (HibernateOptimisticLockingFailureException e) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "����ʧ�ܣ�������");
		}
		return getSingleResultXmlView(model, "success");
	}
	@RequestMapping("/openapi/mobile/draw/isJoinDrawByAppsource.xhtml")
	public String isJoinDraw(String appSource, ModelMap model) {
		Member member = OpenApiMobileAuthenticationFilter.getOpenApiAuth().getMember();
		if (member == null) {
			return getErrorXmlView(model, ApiConstant.CODE_MEMBER_NOT_EXISTS, "�û������ڣ�");
		}
		DrawActivity da = getValidDrawActivity(appSource);
		if (da == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "������ڣ�");
		Map<String, String> otherinfoMap = VmUtils.readJsonToMap(da.getOtherinfo());
		String otherappsource = otherinfoMap.get(DrawActicityConstant.TASK_APPSOURCE);
		if(StringUtils.isBlank(otherappsource)){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "������Դ���ò���Ϊ�գ�");
		}
		List<String> appList = Arrays.asList(StringUtils.split(otherappsource, ","));
		if(!appList.contains(appSource)){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��app��Դ���ڻ��Χ�ڣ�");
		}
		ErrorCode<String> code = getUnionValid(member, da, otherinfoMap);
		if(!code.isSuccess()){
			return getSingleResultXmlView(model, "joined"); //����app �Ѿ��μӹ�
		}
		return getSingleResultXmlView(model, "success");
	}
	private ErrorCode<String> getUnionValid(Member member, DrawActivity da, Map<String, String> otherinfoMap){
		if (StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_ONLYMOBILE))) {
			int count = drawActivityService.getWinnerCountByMobile(da.getId(), member.getMobile());
			if (count>0) {
				return ErrorCode.getFailure("���ֻ������ѳɹ��μӹ��������ٴβμ�");
			}
		}
		if(StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_ONLYONE))){
			WinnerInfo winner = drawActivityService.getWinnerInfoByMemberid(da.getId(), member.getId());
			if(winner!=null){
				return ErrorCode.getFailure(ApiConstant.CODE_DATA_ERROR, "���û��ѳɹ��μӹ��������ٴβμ�");
			}
		}
		return ErrorCode.SUCCESS;
	}
	@RequestMapping("/openapi/mobile/draw/commonDraw.xhtml")
	public String commonDraw(String tag, ModelMap model){
		OpenApiAuth auth = OpenApiMobileAuthenticationFilter.getOpenApiAuth();
		Member member = auth.getMember();
		if (member == null) {
			return getErrorXmlView(model, ApiConstant.CODE_MEMBER_NOT_EXISTS, "�û������ڣ�");
		}
		ErrorCode<String> result = drawUntransService.clickDraw(member, tag, "wap", null, null, null, auth.getRemoteIp());
		if(result.isSuccess()){
			return getSingleResultXmlView(model, result.getRetval());
		}else{
			String errorMsg = "ϵͳ����";
			if(StringUtils.equals(result.getMsg(), "statuss=-1")){
				errorMsg = "���ȵ�¼��";
			}else if(StringUtils.equals(result.getMsg(), "statuss=0")){
				errorMsg = "�δ��ʼ���ѽ�����";
			}else if(StringUtils.equals(result.getMsg(), "statuss=2")){
				errorMsg = "ϵͳ����";
			}else if(StringUtils.equals(result.getMsg(), "statuss=5")){
				errorMsg = "���Ȱ��ֻ���";
			}else if(StringUtils.equals(result.getMsg(), "statuss=7")){
				errorMsg = "���ֲ��㣡";
			}else if(StringUtils.equals(result.getMsg(), "statuss=8")){
				errorMsg = "������֤���䣡";
			}else if(StringUtils.equals(result.getMsg(), "statuss=11")){
				errorMsg = "��ǰ�齱�������㣡";
			}
			return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR, errorMsg);
		}
		
	}
	
	@RequestMapping("/openapi/mobile/draw/appFirstConsumeDraw.xhtml")
	public String appFirstConsumeDraw(String tradeNo, ModelMap model) {
		OpenApiAuth auth = OpenApiMobileAuthenticationFilter.getOpenApiAuth();
		Member member = auth.getMember();
		Map param = new HashMap();
		param.put("memberid", member.getId());
		param.put("tag", TagConstant.TAG_MOVIE);
		// ��ȡǰ���������ж��Ƿ��״ι�Ʊ
		List<SeeMovie> seeOrderList = mongoService.getObjectList(SeeMovie.class, param, "adddate", false, 0, 2);
		if (CollectionUtils.isEmpty(seeOrderList)) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��û��Ʊ��");
		}
		if (seeOrderList.size() > 1 || !StringUtils.equals(tradeNo, seeOrderList.get(0).getTradeNo())) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���״ι�Ʊ��");
		}
		return commonDraw("APPFirstConsume", model);
	}
	
}
