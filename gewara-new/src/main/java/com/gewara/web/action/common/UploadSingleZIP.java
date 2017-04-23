package com.gewara.web.action.common;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gewara.model.acl.GewaraUser;
import com.gewara.model.acl.User;
import com.gewara.untrans.GewaPicService;
import com.gewara.util.DateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;
@Controller
public class UploadSingleZIP extends AnnotationController {
	@Autowired@Qualifier("gewaPicService")
	private GewaPicService gewaPicService;
	public void setGewaPicService(GewaPicService gewaPicService) {
		this.gewaPicService = gewaPicService;
	}
	@RequestMapping(value="/common/uploadSingleZIP.xhtml", method=RequestMethod.GET)
	public String showForm(){
		return "admin/recommend/sport/singleZIP.vm";
	}

	@RequestMapping(value="/common/uploadSingleZIP.xhtml", method=RequestMethod.POST)
	public String handleFormUpload(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, String successFile, String tag, Long relatedid, String callback, 
			ModelMap model) throws Exception {
		GewaraUser user = loginService.getLogonGewaraUserBySessid(WebUtils.getRemoteIp(request), sessid);
		if(user == null || !(user instanceof User)) {
			model.put("msg", "���ȵ�¼��");
			return "redirect:/common/uploadSingleZIP.xhtml";
		}
		String uploadPath = getUploadPath();
		//1����ȡ�ϴ��ľ���·��
		model.put("tag", tag);
		model.put("relatedid", relatedid);
		model.put("uploadPath", uploadPath);
		model.put("callback", callback);
		gewaPicService.moveRemoteTempTo(user.getId(), tag, relatedid, uploadPath, successFile);//���ļ��ƶ�����ʽ�ļ���
		model.put("msg", "ok");
		model.put("picpath", uploadPath + successFile);
		return "redirect:/common/uploadSingleZIP.xhtml";
	}
	private String getUploadPath() {
		return "upload/zip/" + DateUtil.format(new Date(), "yyyyMM") + "/";
	}
}
