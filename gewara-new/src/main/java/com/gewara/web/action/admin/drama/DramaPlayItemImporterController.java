package com.gewara.web.action.admin.drama;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import com.gewara.service.drama.DramaPlayItemImporter;
import com.gewara.util.DateUtil;
import com.gewara.web.action.AnnotationController;

/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
@Controller
public class DramaPlayItemImporterController extends AnnotationController {
	@Autowired@Qualifier("dramaPlayItemImporter")
	private DramaPlayItemImporter dramaPlayItemImporter;
	
	@Autowired@Qualifier("gewaMultipartResolver")
	private MultipartResolver gewaMultipartResolver;
	public void setGewaMultipartResolver(MultipartResolver gewaMultipartResolver) {
		this.gewaMultipartResolver = gewaMultipartResolver;
	}
	@RequestMapping(value="/admin/drama/importPlayItems.xhtml", method=RequestMethod.GET)
	public String showForm(){
		return "admin/drama/importXLSForm.vm";
	}
	
	@RequestMapping(value="/admin/drama/importPlayItems.xhtml", method=RequestMethod.POST)
	public String handleFormUpload(ModelMap model, HttpServletRequest req) throws Exception {
		List<String> msgList = new ArrayList<String>();
		MultipartHttpServletRequest multipartRequest = gewaMultipartResolver.resolveMultipart(req);
		MultipartFile file = multipartRequest.getFile("file");
		if(file == null){
			msgList.add("�ϴ��ļ�Ϊ��!");
			return forwardMessage(model, msgList);
		}
		String orifilename = file.getOriginalFilename();
		if(!(StringUtils.endsWithIgnoreCase(orifilename, "xls") ||StringUtils.endsWithIgnoreCase(orifilename, "xlsx"))){
			msgList.add("��ȷ���ϴ��ļ�Ϊ2excel�ļ�(xls,xlsx)!");
			return forwardMessage(model, msgList); 
		}
		
		String uploadDir = getRealPath("/resources/dramaplaytime/");
		File dirPath = new File(uploadDir);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
		String fileName = DateUtil.format(new Date(), "MM_dd_HH_mm");
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		String fullFileName = uploadDir + userid +fileName + ".xls";
		InputStream stream = null;
		OutputStream fos = null;
		
		try {
			stream = file.getInputStream();
			fos = new BufferedOutputStream(new FileOutputStream(fullFileName));
			IOUtils.copy(stream, fos);
			stream.close();
		} catch (IOException e) {
			msgList.add("�ϴ��ļ�ʱ���ִ���");
		} finally{
			if(fos!=null){
				try{
					fos.close();
				}catch(Exception e){/*ignore*/}
			}
			if(stream != null){
				try{
					stream.close();
				}catch(Exception e){/*ignore*/}
			}
		}
		String tag = multipartRequest.getParameter("tag");
		dramaPlayItemImporter.importPlayTime(fullFileName, msgList, tag);
		return forwardMessage(model, msgList);
	}
}
