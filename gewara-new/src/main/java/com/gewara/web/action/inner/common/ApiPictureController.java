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
import com.gewara.model.common.UploadPic;
import com.gewara.model.content.Picture;
import com.gewara.util.ChangeEntry;
import com.gewara.util.StringUtil;
import com.gewara.web.action.api.BaseApiController;
import com.gewara.web.component.LoginService;

@Controller
public class ApiPictureController extends BaseApiController {

	@Autowired@Qualifier("loginService")
	private LoginService loginService;
	
	@RequestMapping("/inner/common/single/picture.xhtml")
	public String getVideo(Long id, ModelMap model){
		if(id == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
		Picture picture = daoService.getObject(Picture.class, id);
		if(picture == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "���ݲ����ڣ�");
		model.put("picture", picture);
		return getXmlView(model, "inner/common/picture.vm");
	}
	
	@RequestMapping("/inner/common/list/pictureByTag.xhtml")
	public String getPictureListByTag(String tag, Long relatedid, String order, String asc, Integer from, Integer maxnum, ModelMap model){
		if(StringUtils.isBlank(tag) || from == null || maxnum ==null ||  from < 0 || maxnum <=0) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
		List<Picture> pictureList = pictureService.getPictueList(tag, relatedid, order, Boolean.parseBoolean(asc), from, maxnum);
		model.put("pictureList", pictureList);
		return getXmlView(model, "inner/common/pictureList.vm");
	}
	
	@RequestMapping("/inner/common/count/pictureByTag.xhtml")
	public String getPictureCountByTag(String tag, Long relatedid, ModelMap model){
		if(StringUtils.isBlank(tag) || relatedid == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
		int result = pictureService.getPictureCountByRelatedid(tag, relatedid);
		return getSingleResultXmlView(model, result);
	}

	@RequestMapping("/inner/common/save/picture.xhtml")
	public String savePrice(Long id, String tag, Long relatedid, String category, Long categoryid, 
			String picturename, String name, String description, String memberType, Long memberid, String sessid, String ip, ModelMap model){
		if(StringUtils.isBlank(tag) || relatedid == null || StringUtils.isBlank(picturename)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
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
		Picture picture = null;
		if(id != null){
			picture = daoService.getObject(Picture.class, id);
			if(picture == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "���ݲ����ڻ�ɾ����");
			picture.setPicturename(picturename);
		}else{
			picture = new Picture(tag, relatedid, userid, picturename);
			picture.setMemberType(userType);
		}
		ChangeEntry changeEntry = new ChangeEntry(picture);
		picture.setCategory(category);
		picture.setCategoryid(categoryid);
		picture.setDescription(description);
		picture.setName(name);
		daoService.saveObject(picture);
		monitorService.saveChangeLog(userid, Picture.class, picture.getId(), changeEntry.getChangeMap(picture));
		return getSingleResultXmlView(model, picture.getId());
	}
	
	@RequestMapping("/inner/common/delete/picture.xhtml")
	public String deletePicture(Long id, String memberType, Long memberid, String sessid, String ip, ModelMap model){
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
		Picture picture = daoService.getObject(Picture.class, id);
		if(picture == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "���ݲ����ڻ�ɾ����");
		if(!userid.equals(picture.getMemberid()) || !StringUtils.equals(picture.getMemberType(), userType)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "����ɾ�������ϴ���ͼƬ��");
		daoService.removeObject(picture);
		monitorService.saveDelLog(userid, id, picture);
		return getSingleResultXmlView(model, "true");
	}
	//TODO:gewapic����������
	@RequestMapping("/synch/uploadList.xhtml")
	public String uploadList(Long modifytime, String check, ModelMap model){
		String mycheck = StringUtil.md5(modifytime+"xxxx");
		if(!mycheck.equals(check)) {
			return getErrorXmlView(model, "", "У�����");
		}
		String qry = "from UploadPic u where u.modifytime>=? order by modifytime";
		List<UploadPic> uploadList = daoService.queryByRowsRange(qry, 0, 500, modifytime);
		model.put("uploadList", uploadList);
		return getXmlView(model, "api/picture/uploadList.vm");
	}
}
