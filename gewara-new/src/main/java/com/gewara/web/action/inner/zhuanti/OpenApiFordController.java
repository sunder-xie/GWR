package com.gewara.web.action.inner.zhuanti;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.ApiConstant;
import com.gewara.json.mobile.FordTestDrive;
import com.gewara.util.ValidateUtil;
import com.gewara.web.action.inner.mobile.BaseOpenApiController;

@Controller
public class OpenApiFordController extends BaseOpenApiController {
	/**
	 * �û�����
	 */
	@RequestMapping("/openapi/mobile/ford/testdrive.xhtml")
	public String savePlayersInfo(FordTestDrive fordTestDrive, ModelMap model) {
		if (fordTestDrive == null || fordTestDrive.checkNotBlankValue()) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "ȱ�ٱ�Ҫ������");
		}
		if (!ValidateUtil.isMobile(fordTestDrive.getMobileNo())) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "����ֻ������ʽ����ȷ��");
		}
		String id = fordTestDrive.getSource() + fordTestDrive.getMobileNo() + fordTestDrive.getDriveName();
		FordTestDrive temp = mongoService.getObject(FordTestDrive.class, "id", id);
		if (temp != null) {
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "���ѱ������벻Ҫ�ظ��ύ��");
		}
		fordTestDrive.setId(id);
		fordTestDrive.setAddTime(System.currentTimeMillis());
		mongoService.saveOrUpdateObject(fordTestDrive, "id");
		return getSingleResultXmlView(model, "success");
	}
	
}
