package com.gewara.web.action.api2mobile;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.web.action.api.BaseApiController;

/**
 * �ֻ��ͻ����˻����ҵ��
 * @author taiqichao
 * @deprecated
 */
@Controller
public class Api2MobileAccountsController extends BaseApiController {
	/**
	 * �����ֻ��󶨶�̬��֤��
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/mobile/accounts/getBindingCaptcha.xhtml")
	public String getBindingCaptcha(ModelMap model){
		return notSupport(model);
	}
	
	
	/**
	 * ���ֻ���
	 * @param key
	 * @param encryptCode
	 * @param mobile
	 * @param dynamicNumber
	 * @param version
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/mobile/accounts/bindingMobile.xhtml")
	public String bindingMobile(ModelMap model){
		return notSupport(model);
	}
	
	/**
	 * �������ֵ
	 * @param key
	 * @param encryptCode
	 * @param memberEncode
	 * @param cardpass
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/api2/mobile/accounts/ccardPayCharge.xhtml")
	public String ccardPayCharge(ModelMap model){
		return notSupport(model);
	}
	
	/**
	 * �����˻���Ϣ
	 * @param realname
	 * @param password
	 * @param confirmPassword
	 * @param idcard
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/mobile/accounts/saveAccount.xhtml")
	public String saveAccount(ModelMap model){
		return notSupport(model);
	}
	
}
