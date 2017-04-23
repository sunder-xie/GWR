package com.gewara.web.action.common;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import com.gewara.Config;
import com.gewara.model.acl.GewaraUser;
import com.gewara.model.acl.User;
import com.gewara.model.user.Member;
import com.gewara.untrans.GewaPicService;
import com.gewara.util.DateUtil;
import com.gewara.util.PictureUtil;
import com.gewara.util.StringUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;
import com.gewara.web.support.ErrorMultipartRequest;

/****
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-10-9����08:59:19
 **********/
@Controller
public class UploadPictureController extends AnnotationController {
	
	// ǰ̨ͨ���ϴ�URL bob.
	private static Map<String, String> uploadPathMap = new HashMap<String, String>();
	private static String thisMonth = DateUtil.format(new Date(), "yyyyMM");
	private static String TAG_WATERMARK = "watermark";
	static{
		uploadPathMap.put("micro", "images/micro/"+ thisMonth + "/");
		uploadPathMap.put("commubg", "images/commubg/"+ thisMonth + "/");
		uploadPathMap.put("moviePic", "images/memberpicture/"+ thisMonth + "/");
		uploadPathMap.put("member", "images/" +thisMonth +"/subject/");
		uploadPathMap.put("mobile", "images/mobile/" + thisMonth + "/");
		uploadPathMap.put("subject", "images/subject/" + thisMonth + "/");
	}
	
	@Autowired@Qualifier("config")
	private Config config;
	public void setConfig(Config config) {
		this.config = config;
	}
	@Autowired@Qualifier("gewaMultipartResolver")
	private MultipartResolver gewaMultipartResolver;
	public void setGewaMultipartResolver(MultipartResolver gewaMultipartResolver) {
		this.gewaMultipartResolver = gewaMultipartResolver;
	}
	@Autowired@Qualifier("gewaPicService")
	private GewaPicService gewaPicService;
	public void setGewaPicService(GewaPicService gewaPicService) {
		this.gewaPicService = gewaPicService;
	}
	@RequestMapping(value = "/common/memberUploadPicture.xhtml")
	public String memberUploadPicture(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model) throws Exception {
		GewaraUser user = loginService.getLogonGewaraUserBySessid(WebUtils.getRemoteIp(request), sessid);
		String res = "/common/fckUploadCallback.xhtml";
		if(user==null) {
			return showUploadError(model, "���ȵ�¼��");
		}
		MultipartHttpServletRequest multipartRequest = gewaMultipartResolver.resolveMultipart(request);
		String result = uploadFile(multipartRequest, PictureUtil.UPLOADTYPE_PIC, user, model, false);
		model.put("submitUrl", res);
		return result;
	}

	@RequestMapping(value = "/common/uploadPicture.xhtml", method=RequestMethod.POST)
	public String uploadPicture(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model) throws Exception {
		GewaraUser user =loginService.getLogonGewaraUserBySessid(WebUtils.getRemoteIp(request), sessid);
		if(user==null){
			return showUploadError(model, "���ȵ�¼��");
		}
		MultipartHttpServletRequest multipartRequest = gewaMultipartResolver.resolveMultipart(request);
		return uploadFile(multipartRequest, PictureUtil.UPLOADTYPE_PIC, user, model, false);
	}
	@RequestMapping(value = "/common/uploadFlash.xhtml", method=RequestMethod.POST)
	public String uploadFlash(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model) throws Exception {
		GewaraUser user = loginService.getLogonGewaraUserBySessid(WebUtils.getRemoteIp(request), sessid);
		if(user==null || !(user instanceof User)){//ֻ�����̨�ϴ�
			return showUploadError(model, "���ȵ�¼��");
		}
		MultipartHttpServletRequest multipartRequest = gewaMultipartResolver.resolveMultipart(request);
		return uploadFile(multipartRequest, PictureUtil.UPLOADTYPE_FLASH, user, model, false);
	}
	@RequestMapping(value = "/common/uploadAPK.xhtml", method=RequestMethod.POST)
	public String uploadAPK(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model) throws Exception {
		GewaraUser user = loginService.getLogonGewaraUserBySessid(WebUtils.getRemoteIp(request), sessid);
		if(user==null || !(user instanceof User)){//ֻ�����̨�ϴ�
			return showUploadError(model, "���ȵ�¼��");
		}
		MultipartHttpServletRequest multipartRequest = gewaMultipartResolver.resolveMultipart(request);
		return uploadFile(multipartRequest, PictureUtil.UPLOADTYPE_MOBILE, user, model, false);
	}
	@RequestMapping(value = "/common/uploadZIP.xhtml", method=RequestMethod.POST)
	public String uploadZIP(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, ModelMap model) throws Exception {
		GewaraUser user = loginService.getLogonGewaraUserBySessid(WebUtils.getRemoteIp(request), sessid);
		if(user==null || !(user instanceof User)){//ֻ�����̨�ϴ�
			return showUploadError(model, "���ȵ�¼��");
		}
		MultipartHttpServletRequest multipartRequest = gewaMultipartResolver.resolveMultipart(request);
		return uploadFile(multipartRequest, PictureUtil.UPLOADTYPE_SPORT, user, model, false);
	}
	//�Ϸ����ļ���չ��
	/**
	 * ͨ���ļ��ϴ����
	 * �������
	 * 1���ϴ����ļ����������Ʋ�����������file1,file2,�����ܶ��ͬ����
	 * 2��callbackUrl���ص�URL�����룩
	 * 3�������������Զ��壩

	 * �ص�������callbackUrl��ʱ���������
	 * 1��successField���ɹ��ϴ��Ĳ������ƣ�����á�@@���ָ����磺file1@@file3@@file4
	 * 2��successFile���ɹ��ϴ�����ļ�������ʽΪ��xxxx.jpg@@yyy.jpg
	 * 3��invalidField���Ƿ��ļ�����
	 * 4��invalidFile���Ƿ��ļ����ƣ�ԭ��������@@�ָ�
	 * 5��paramchk��������֤��md5(successFile+"GewaUploadFile")
	 * 6��errorMsg����������ʱ�Ĵ�����Ϣ����������ʱ�����������ܶ�û����
	 * 7����������ԭ������
	 * 8��realName���ɹ��ϴ��ļ�ԭ��
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	private String uploadFile(MultipartHttpServletRequest multipartRequest, String type, GewaraUser user, ModelMap model, boolean directRedirect) throws Exception {
		if(multipartRequest instanceof ErrorMultipartRequest){
			String msg = ((ErrorMultipartRequest)multipartRequest).getErrorMsg();
			return goBack(model, msg);
		}
		Map<String, String> params = new HashMap<String, String>();
		String callbackUrl = multipartRequest.getParameter("callbackUrl");
		List<String> realName = new ArrayList<String>();
		List<String> invalidField = new ArrayList<String>();
		List<String> invalidFile = new ArrayList<String>();
		List<String> successField = new ArrayList<String>();
		List<String> successFile = new ArrayList<String>();
		List<String> ofilenameList = new ArrayList<String>();
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile file; 
		String ofilename, extname, newname;
		for(String fieldName: fileMap.keySet()){
			file = fileMap.get(fieldName);
			ofilename = StringUtils.lowerCase(file.getOriginalFilename());
			extname = StringUtil.getFilenameExtension(ofilename);
			if(StringUtils.isBlank(extname) || !PictureUtil.isValidExtension(ofilename, type)){//���Ϸ�
				invalidField.add(fieldName);
				invalidFile.add(ofilename);
				continue;
			}
			if(PictureUtil.UPLOADTYPE_PIC.equals(type)){
				newname = gewaPicService.saveToTempPic(file, extname);
				if(StringUtils.isBlank(newname)){
					invalidField.add(fieldName);
					invalidFile.add(ofilename);
					continue;
				}
			}else{
				newname = gewaPicService.saveToTempFile(file, extname);
			}
			ofilenameList.add(ofilename);
			successField.add(fieldName);
			successFile.add(newname);
			realName.add(StringUtils.split(file.getOriginalFilename(), ".")[0]);
		}
		if(type.equals(PictureUtil.UPLOADTYPE_PIC)){
			String notLimit = multipartRequest.getParameter("notLimit");
			if(StringUtils.isBlank(notLimit)) {
				gewaPicService.limitTempFileSize(successFile, 1000, 1000);//���ƴ�С
			}
			if(user instanceof Member && StringUtils.isBlank(params.get(TAG_WATERMARK))){
				gewaPicService.addWaterMark(successFile);
			}
		}
		//��֯����
		params.putAll(getUniqueValueMap(multipartRequest));
		params.put("realName", StringUtils.join(realName, "@@"));
		params.put("ofilename", StringUtils.join(ofilenameList, "@@"));
		params.put("invalidField", StringUtils.join(invalidField, "@@"));
		params.put("invalidFile", StringUtils.join(invalidFile, "@@"));
		params.put("successField", StringUtils.join(successField, "@@"));
		params.put("successFile", StringUtils.join(successFile, "@@"));
		params.put("paramchk", StringUtil.md5(StringUtils.join(successFile, "@@") + config.getString("uploadKey")));
		gewaPicService.saveTempFileListToRemote(successFile);
		if(directRedirect){
			model.putAll(params);
			return "redirect:"+callbackUrl;
		}
		model.put("submitParams", params);
		model.put("method", "post");
		model.put("submitUrl", callbackUrl);
		model.put("pause", false);
		return "tempSubmitForm.vm";
	}
	@RequestMapping("/common/afterUploadPicture.xhtml")
	public String handleFormUpload(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, String paramchk, String callback, String callbackf, String callbackUrl,
			String realName, String successFile, String uploadtag, Long relatedid, ModelMap model) throws IOException{
		String mycheck = StringUtil.md5(successFile + config.getString("uploadKey"));
		if(!mycheck.equals(paramchk)) return showError(model, "У�����");
		String vmFile = "common/afterupdatePic.vm";
		if (StringUtils.isBlank(successFile)) {
			model.put("msg", "�ϴ��ļ�ʧ�ܣ���ȷ���ϴ��ļ��Ƿ�Ϸ���");
			return vmFile; 
		}
		String uploadPath = uploadPathMap.get(uploadtag);
		if(StringUtils.isNotBlank(uploadPath)){
			GewaraUser user = loginService.getLogonGewaraUserBySessid(WebUtils.getRemoteIp(request), sessid);
			gewaPicService.moveRemoteTempTo(user.getId(), uploadtag, relatedid, uploadPath, successFile);//���ļ��ƶ�����ʽ�ļ���
		}else uploadPath = "";
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("realName", realName);
		paramsMap.put("uploadtag", uploadtag);
		paramsMap.put("picpath", uploadPath + successFile);
		paramsMap.put("successFile", successFile);
		paramsMap.put("msg", "ok");
		paramsMap.put("callback", callback);
		paramsMap.put("callbackUrl", callbackUrl);
		model.put("paramsMap", paramsMap);
		model.put("callbackf", callbackf);
		return vmFile;
	}
	
	private String showUploadError(ModelMap model, String msg){
		Map jsonMap = new HashMap();
		jsonMap.put("msg", msg);
		jsonMap.put("success", false);
		model.put("jsonMap", jsonMap);
		return "common/showUploadResult.vm";
	}
	private Map<String, String> getUniqueValueMap(MultipartHttpServletRequest request){
		Map<String, String[]> ms = request.getParameterMap();
		Map<String, String> result = new HashMap<String, String>();
		if(ms==null || ms.size()==0) return result;
	 	for(Map.Entry<String, String[]> m : ms.entrySet()){
			result.put(m.getKey(), m.getValue()[0]);
		}
	 	return result;
	}
	@RequestMapping(value = "/common/processUpload.xhtml")
	public String processFile(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request, String paramchk, String invalidField, 
			String invalidFile, String successField, String successFile, ModelMap model) throws IOException{
		GewaraUser user = loginService.getLogonGewaraUserBySessid(WebUtils.getRemoteIp(request), sessid);
		String mycheck = StringUtil.md5(successFile + config.getString("uploadKey"));
		if(!mycheck.equals(paramchk)) return showError(model, "У�����");
		dbLogger.warn(invalidField + invalidFile + successField);
		String[] fileList = StringUtils.split(successFile, "@@");
		String uploadPath = "images/test/"; //�ϴ�·��
		gewaPicService.limitTempFileSize(Arrays.asList(fileList), 1000, 1000);//���ƴ�С
		List<String> dstFileList = gewaPicService.moveRemoteTempListTo(user.getId(), "test", null, uploadPath, fileList);//���ļ��ƶ�����ʽ�ļ���
		for(String s:dstFileList){
			dbLogger.warn(s);
			//����������
		}
		return showMessage(model, "�ɹ��ϴ��ļ���");
	}
}
