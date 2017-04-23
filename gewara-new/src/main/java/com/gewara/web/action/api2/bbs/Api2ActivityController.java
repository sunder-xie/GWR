package com.gewara.web.action.api2.bbs;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.TagConstant;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.support.ErrorCode;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.activity.SynchActivityService;
import com.gewara.web.action.api.ApiAuth;
import com.gewara.web.action.api.BaseApiController;
import com.gewara.web.filter.NewApiAuthenticationFilter;
import com.gewara.xmlbind.activity.RemoteActivity;
import com.gewara.xmlbind.activity.RemoteTreasure;
import com.gewara.xmlbind.bbs.Comment;

/**
 * �API
 * @author taiqichao
 *
 */
@Controller
public class Api2ActivityController extends BaseApiController{
	@Autowired@Qualifier("synchActivityService")
	private SynchActivityService synchActivityService;

	private String getCommonActivityDetail(Long activityid, String memberEncode, String isSimpleHtml, Integer width, Integer height, boolean isMovie,  ModelMap model){
		if(StringUtils.isBlank(activityid+""))return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "activityId����Ϊ�գ�");
		ErrorCode<RemoteActivity> code = synchActivityService.getRemoteActivity(activityid);
		if(!code.isSuccess())  return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, code.getMsg());
		RemoteActivity activity = code.getRetval();
		Member member = null;
		if(StringUtils.isNotBlank(memberEncode)){
			member = memberService.getMemberByEncode(memberEncode);
			if(member==null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "�û������ڣ�");
		}
		boolean isInterest = false;
		boolean isJoin = false;
		if(member!=null){
			//��ȡ�û��ղصĻ�б������ж��Ƿ���ڸû�����������isInterestΪtrue
			List<String> operList = synchActivityService.memberOperActivityResult(activityid, member.getId());
			if(operList.size()>=2){
				isInterest = StringUtils.equals(operList.get(0), "true");
				isJoin = StringUtils.equals(operList.get(1), "true");
			}
		}
		//��ѯ�����ID
		if(activity!=null&&activity.getRelatedid()!=null){
			Object relate1 = null;
			Object relate2 = null;
			if(activity.getRelatedid()!=null){
				relate1 = relateService.getRelatedObject(activity.getTag(), activity.getRelatedid());
			}
			if(activity.getCategoryid()!=null){
				relate2 = relateService.getRelatedObject(activity.getCategory(), activity.getCategoryid());
			}
			model.put("relate1", relate1);
			model.put("relate2", relate2);
		}
		String body = activity.getContentdetail();
		String content = getSimpleHtmlContent(body, isSimpleHtml, width, height);
		if(!isMovie && StringUtils.isNotBlank(content)){
			content = content.replaceAll(" href=\"[^>]+\"", "");
		}
		model.put("activity", activity);
		model.put("isJoin", isJoin);
		model.put("content", content);
		model.put("isInterest", isInterest);
		List<RemoteActivity> activityList = new ArrayList<RemoteActivity>();
		activityList.add(activity);
		addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(activityList));
		return getXmlView(model, "api2/activity/activityDetail.vm");
	}
	/**
	 * �����
	 * @param activityId
	 * @param memberEncode
	 * @param returnField
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/activity/activityDetail.xhtml")
	public String activityDetail(Long activityid, String memberEncode, String isSimpleHtml, Integer width, Integer height,  ModelMap model){
		return getCommonActivityDetail(activityid, memberEncode, isSimpleHtml, width, height, false, model);
	}
	/**
	 * ��Ӱ�������
	 * @param activityId
	 * @param memberEncode
	 * @param returnField
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/activity/getMovieActivityDetail.xhtml")
	public String getMovieActivityDetail(Long activityid,String memberEncode, String isSimpleHtml, Integer width, Integer height,  ModelMap model){
		return getCommonActivityDetail(activityid, memberEncode, isSimpleHtml, width, height, true, model);
	}
	/**
	 * ����ĳ����Ŀ�Ļ�б� ���磺(��ë��)
	 * @param categoryid ��ǰ������Ŀ��ID
	 * @param from ��ʼλ��
	 * @param maxnum ÿ�ζ�ȡ������
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/activity/activityListByTime.xhtml")
	public String activityListByTime(String citycode, String tag, Long relatedid, String category, Long categoryid, Timestamp starttime, Timestamp endtime, String orderField, String asc, int from, int maxnum, ModelMap model){
		if(StringUtils.isBlank(citycode) || starttime==null || endtime==null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "ȱ�ٲ�����");
		List<RemoteActivity> activityList = new ArrayList<RemoteActivity>();
		ErrorCode<List<RemoteActivity>> code = synchActivityService.getActivityListByTime(citycode, null, starttime, endtime, null, tag, relatedid, category, categoryid, orderField, asc, from, maxnum);
		if(code.isSuccess()) activityList = code.getRetval();
		putRelateMap(activityList, model);
		return getXmlView(model, "api2/activity/activityList.vm");
	}

	@RequestMapping("/api2/activity/activityListByType.xhtml")
	public String activityListByTime(String citycode, String countycode, String tag, Long relatedid, 
			String category, Long categoryid, String atype, String datetype, Integer timetype, String isFee,
			String orderField, int from, int maxnum, ModelMap model){
		if(StringUtils.isBlank(citycode) || StringUtils.isBlank(tag)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "ȱ�ٲ�����");
		if(StringUtils.isBlank(orderField)) orderField = "addtime";
		List<RemoteActivity> activityList = new ArrayList<RemoteActivity>();
		ErrorCode<List<RemoteActivity>> code = synchActivityService.getActivityListByType(citycode, countycode, atype, datetype, timetype, tag, relatedid, category, categoryid, isFee, orderField, from, maxnum);
		if(code.isSuccess()) activityList = code.getRetval();
		putRelateMap(activityList, model);
		return getXmlView(model, "api2/activity/activityList.vm");
	}
	
	/**
	 * ����ĳ�����ݵĻ�б� 
	 * @param categoryid ��ǰ������Ŀ��ID
	 * @param from ��ʼλ��
	 * @param maxnum ÿ�ζ�ȡ������
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/activity/activityListByRelatedid.xhtml")
	public String activityListByTag(String citycode, String tag, Long relatedid, String category, Long categoryid, Timestamp starttime, Timestamp endtime, String orderField, String asc, int from, int maxnum, ModelMap model){
		if(StringUtils.isBlank(tag) || relatedid==null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "ȱ�ٲ�����");
		List<RemoteActivity> activityList = new ArrayList<RemoteActivity>();
		ErrorCode<List<RemoteActivity>> code = synchActivityService.getActivityListByTag(citycode, null, null, tag, relatedid, category, categoryid, starttime, endtime, orderField, asc, from, maxnum);
		if(code.isSuccess()) activityList = code.getRetval();
		putRelateMap(activityList, model);
		return getXmlView(model, "api2/activity/activityList.vm");
	}
	 
	/**
	 * ����ĳ����Ŀ�Ļ������(��ë��)
	 * @param categoryid ��ǰ������Ŀ��ID
	 * @param from ��ʼλ��
	 * @param maxnum ÿ�ζ�ȡ������
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/activity/activityCountByTime.xhtml")
	public String activityByTag(String citycode, String tag, Long relatedid, String category, Long categoryid, Timestamp starttime, Timestamp endtime, ModelMap model){
		if(starttime==null || endtime==null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "ȱ�ٲ�����");
		ErrorCode<Integer> code = synchActivityService.getActivityCountByTime(citycode, null, starttime, endtime, null, tag, relatedid, category, categoryid);
		if(!code.isSuccess()) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, code.getMsg());
		Integer count = code.getRetval();
		model.put("count", count);
		return getSingleResultXmlView(model, count);
	}
	/**
	 * ���ݻID���ϲ�ѯ���Ϣ
	 * @param ids �id����
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/activity/activityListByIds.xhtml")
	public String activityList(String ids, ModelMap model){
		if(ids == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "��������");
		List<String> activityidList = Arrays.asList(StringUtils.split(ids, ","));
		List<Long> aidIds = new ArrayList<Long>();
		for (String aid : activityidList) {
			aidIds.add(Long.parseLong(aid));
		}
		List<RemoteActivity> activityList = new ArrayList<RemoteActivity>();
		if(activityidList.size() > 0) { 
			ErrorCode<List<RemoteActivity>> code = synchActivityService.getRemoteActivityListByIds(aidIds);
			if(code.isSuccess()) activityList = code.getRetval();
		}
		putRelateMap(activityList, model);
		return getXmlView(model, "api2/activity/activityList.vm"); 
	}
	
	/**
	 * �����Ȥ���û��б�
	 * @param memberid �û�id
	 * @param tag ��ǩ
	 * @param relatedid ����id
	 * @param from ��ʼֵ
	 * @param maxnum ÿҳ��ʾ��
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/activity/interestedMemberList.xhtml")
	public String getMemberByInterested(Long activityid, String asc, int from, int maxnum, ModelMap model){
		ErrorCode<List<RemoteTreasure>> code = synchActivityService.getTreasureList(activityid, asc, from, maxnum);
		if(!code.isSuccess())  return getErrorXmlView(model,  ApiConstant.CODE_SIGN_ERROR, code.getMsg());
		List<RemoteTreasure> treasureList = code.getRetval();
		Map<Long, MemberInfo> infoMapList = new HashMap<Long, MemberInfo>();
		for(RemoteTreasure treasure : treasureList){
			infoMapList.put(treasure.getMemberid(), daoService.getObject(MemberInfo.class, treasure.getMemberid()));
		}
		model.put("treasureList", treasureList);
	    model.put("infoMapList", infoMapList);
		return getXmlView(model, "api2/activity/interestedMemberList.vm");
	}
	/**
	 * �μӻ�û��б�
	 * @param activityid
	 * @param from ��ʼֵ
	 * @param maxnum ÿҳ��ʾ��
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/activity/joinMemberList.xhtml")
	public String joinMemberList(ModelMap model){
		return notSupport(model);
	}
	/**
	 * ����Ȥ�Ļ
	 * @param memberEncode �û�
	 * @param tag ��ǩ
	 * @param relatedid ����id
	 * @param from ��ʼֵ
	 * @param maxnum ÿҳ��ʾ��
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/activity/collActivityList.xhtml")
	public String interestedActivity(ModelMap model){
		return notSupport(model);
	}
	/**
	 * �����ټ�
	 * @param memberEncode
	 * @param sportItemId
	 * @param statarTime
	 * @param priceInfo
	 * @param sportId
	 * @param content
	 * @param pic
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/activity/addActivity.xhtml")
	public String saveActivity(ModelMap model){
		return notSupport(model);
	}
	
	/**
	 * �μӻ
	 * @param memberEncode
	 * @param activityId
	 * @param realname
	 * @param sex
	 * @param joinnum
	 * @param joindate
	 * @param mobile
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/activity/joinActivity.xhtml")
	public String joinActivity(ModelMap model){
		return notSupport(model);
	}
	
	/**
	 * ȡ���μӻ
	 * @param memberEncode
	 * @param activityId
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/activity/cancelJoinActivity.xhtml")
	public String cancelJoinActivity(ModelMap model){
		return notSupport(model);
	}
	
	/**
	 * ����ղ�
	 * @param memberEncode
	 * @param tag
	 * @param relatedid
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/activity/addCollection.xhtml")
	public String addCollection(ModelMap model){
		return notSupport(model);
	}
	
	/**
	 * ȡ���ղ�
	 * @param memberEncode
	 * @param tag
	 * @param relatedid
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/activity/cancelCollection.xhtml")
	public String cancelCollection(ModelMap model){
		return notSupport(model);
	}
	
	/**
	 * ��б�
	 * @param tag
	 * @param memberEncode
	 * @param citycode
	 * @param type
	 * @param returnField
	 * @param distance
	 * @param pointx
	 * @param pointy
	 * @param from
	 * @param maxnum
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/activity/activityList.xhtml")
	public String activityList(String tag, Long relatedid, String category, Long categoryid, String citycode, int from,int maxnum, ModelMap model){
		ApiAuth auth = NewApiAuthenticationFilter.getApiAuth();
		if(StringUtils.isBlank(citycode))citycode = auth.getApiUser().getDefaultCity();
		if(StringUtils.isBlank(tag)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "tag����Ϊ�գ�");
		if(maxnum > 20) maxnum = 20;
		List<RemoteActivity> activityList = new ArrayList<RemoteActivity>();
		ErrorCode<List<RemoteActivity>> code = synchActivityService.getActivityList(citycode, null, null, tag, relatedid, category, categoryid, from, maxnum);
		if(code.isSuccess()){
			activityList = code.getRetval();
			Collections.sort(activityList, new PropertyComparator("activityStartTime", false, true));
		}
		putRelateMap(activityList, model);
		return getXmlView(model, "api2/activity/activityList.vm"); 
	}
	
	/**
	 * �����û���ѯ�
	 * @param memberEncode
	 * @param tag
	 * @param from
	 * @param maxnum
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/activity/memberActivityList.xhtml")
	public String memberidActivity(ModelMap model){
		return notSupport(model);
	}
	
	@RequestMapping("/api2/activity/memberJoinActivityList.xhtml")
	public String memberJoinActivityList(ModelMap model){
		return notSupport(model);
	}
	
	@RequestMapping("/api2/activity/getCommentListByActivityid.xhtml")
	public String memberidActivity(Long activityid, String orderField, String haveface, Integer from, Integer maxnum, ModelMap model){
		List<Comment> commentList = commentService.getCommentListByRelatedId(TagConstant.TAG_ACTIVITY,null, activityid, orderField, from, maxnum);
		getCommCommentData(model, commentList, haveface);
		return getXmlView(model, "api2/comment/commentList.vm");
	}
	
	@RequestMapping("/api2/activity/getActivityListBySignname.xhtml")
	public String getActivityListBySignname(String citycode, String signname, Integer from, Integer maxnum, ModelMap model){
		ErrorCode<List<RemoteActivity>> code = synchActivityService.getActivityListBySignname(citycode, signname, from, maxnum);
		List<RemoteActivity> activityList = new ArrayList<RemoteActivity>();
		if(code.isSuccess()) activityList = code.getRetval();
		putRelateMap(activityList, model);
		return getXmlView(model, "api2/activity/activityList.vm");
	}
	
	/**
	 * �����
	 * @param activityId
	 * @param memberEncode
	 * @param returnField
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/activity/operActivityResult.xhtml")
	public String activityDetail(ModelMap model){
		return notSupport(model);
	}
}
