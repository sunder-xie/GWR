package com.gewara.web.action.inner.common;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.ApiConstant;
import com.gewara.model.acl.GewaraUser;
import com.gewara.model.content.Video;
import com.gewara.service.content.VideoService;
import com.gewara.util.ChangeEntry;
import com.gewara.web.action.api.BaseApiController;
import com.gewara.web.component.LoginService;

@Controller
public class ApiVideoController extends BaseApiController {
	
	@Autowired@Qualifier("videoService")
	private VideoService videoService;
	
	@Autowired@Qualifier("loginService")
	private LoginService loginService;
	
	@RequestMapping("/inner/common/single/video.xhtml")
	public String getVideo(Long id, ModelMap model){
		if(id == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
		Video video = daoService.getObject(Video.class, id);
		if(video == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "���ݲ����ڣ�");
		model.put("video", video);
		return getXmlView(model, "inner/common/video.vm");
	}
	
	@RequestMapping("/inner/common/count/videoByTag.xhtml")
	public String getVideoCountByTag(String tag, Long relatedid, Integer hotvalue, ModelMap model){
		if(StringUtils.isBlank(tag)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
		int result = videoService.getVideoCountByTag(tag, relatedid, hotvalue);
		return getSingleResultXmlView(model, result);
	}
	
	@RequestMapping("/inner/common/list/videoByTag.xhtml")
	public String getVideoListByTag(String tag, Long relatedid, Integer hotvalue, String order, String asc, Integer from, Integer maxnum, ModelMap model){
		if(StringUtils.isBlank(tag) || from == null || maxnum ==null || from < 0 || maxnum <=0) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
		List<Video> videoList = videoService.getVideoListByTag(tag, relatedid, hotvalue, order, Boolean.parseBoolean(asc), from, maxnum);
		model.put("videoList", videoList);
		return getXmlView(model, "inner/common/videoList.vm");
	}
	
	@RequestMapping("/inner/common/save/video.xhtml")
	public String saveVideo(Long id, String tag, Long relatedid, String flag, String category, Long categoryid, 
			String url, String videotitle, String logo, String content,  String memberType, Long memberid, String sessid, String ip, ModelMap model){
		if(StringUtils.isBlank(tag) || relatedid == null || StringUtils.isBlank(flag) || StringUtils.isBlank(url) 
				|| StringUtils.isBlank(videotitle)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
		Long userid = null; String userType = "";
		if(StringUtils.isNotBlank(sessid) && StringUtils.isNotBlank(ip)){
			GewaraUser gewaraUser = loginService.getLogonGewaraUserBySessid(ip, sessid);
			if(gewaraUser == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "���ȵ�¼��");
			userid = gewaraUser.getId();
			userType = gewaraUser.getUsertype();
		}else{
			if(StringUtils.isBlank(memberType) || memberid == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
			userid = memberid;
			userType = memberType;
		}
		Video video = null;
		if(id != null){
			video = daoService.getObject(Video.class, id);
			if(video == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "���ݲ����ڻ�ɾ����");
		}else{
			video = new Video(memberid);
			video.setMemberType(userType);
		}
		ChangeEntry changeEntry = new ChangeEntry(video);
		video.setTag(tag);
		video.setRelatedid(relatedid);
		video.setCategory(category);
		video.setCategoryid(categoryid);
		video.setFlag(flag);
		video.setUrl(url);
		video.setVideotitle(videotitle);
		video.setLogo(logo);
		video.setContent(content);
		daoService.saveObject(video);
		monitorService.saveChangeLog(userid, Video.class, video.getId(), changeEntry.getChangeMap(video));
		return getSingleResultXmlView(model, video.getId());
	}
	
	@RequestMapping("/inner/common/delete/video.xhtml")
	public String deleteVideo(Long id, String memberType, Long memberid, String sessid, String ip, ModelMap model){
		if(id == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
		Long userid = null; String userType = "";
		if(StringUtils.isNotBlank(sessid) && StringUtils.isNotBlank(ip)){
			GewaraUser gewaraUser = loginService.getLogonGewaraUserBySessid(ip, sessid);
			if(gewaraUser == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "���ȵ�¼��");
			userid = gewaraUser.getId();
			userType = gewaraUser.getUsertype();
		}else{
			if(StringUtils.isBlank(memberType) || memberid == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
			userid = memberid;
			userType = memberType;
		}
		Video video = daoService.getObject(Video.class, id);
		if(video == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "���ݲ����ڻ�ɾ����");
		if(!userid.equals(video.getMemberid()) || !StringUtils.equals(video.getMemberType(), userType)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "����ɾ�������ϴ���ͼƬ��");
		daoService.removeObject(video);
		monitorService.saveDelLog(userid, id, video);
		return getSingleResultXmlView(model, "true");
	}
}
