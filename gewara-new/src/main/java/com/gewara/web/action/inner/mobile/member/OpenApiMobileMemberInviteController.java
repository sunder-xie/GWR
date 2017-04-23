package com.gewara.web.action.inner.mobile.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.web.action.inner.mobile.BaseOpenApiController;
@Controller
public class OpenApiMobileMemberInviteController extends BaseOpenApiController{
	/**
	 * ��ѯ�����Ŀ���״̬
	 */
	@RequestMapping("/openapi/mobile/member/inviteUsabled.xhtml")
	public String getInviteUsabled(ModelMap model){
		return getSingleResultXmlView(model, "failure");
	}
	
	

	/**
	 * �ֻ��ͻ��˶��������û�
	 */
	@RequestMapping("/openapi/mobile/member/sendInvite.xhtml")
	public String sendInvite(ModelMap model) {
		return notSupport(model);
	}
	
	/**
	 * ��ѯ����ע���������
	 */
	@RequestMapping("/openapi/mobile/member/getInivteActInfo.xhtml")
	public String getInivteActInfo(ModelMap model) {
		return notSupport(model);
	}
	
	/**
	 * ��ѯ�û������ֻ�����
	 */
	@RequestMapping("/openapi/mobile/member/getInivteMobile.xhtml")
	public String getInivteMobile(ModelMap model) {
		return notSupport(model);
	}
	
}
