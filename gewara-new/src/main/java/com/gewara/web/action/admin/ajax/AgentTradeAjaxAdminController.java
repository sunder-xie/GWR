package com.gewara.web.action.admin.ajax;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.BindConstant;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.SMSRecord;
import com.gewara.service.member.BindMobileService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.UntransService;
import com.gewara.util.ValidateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.admin.BaseAdminController;

@Controller
public class AgentTradeAjaxAdminController extends BaseAdminController {


	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	
	@Autowired@Qualifier("bindMobileService")
	private BindMobileService bindMobileService;
	
	//��Ʒ���ѵ����
	@RequestMapping("/admin/drama/agent/modOrderMobile.xhtml")
	public String modOrderMobile(Long orderid, String mobile, ModelMap model) {
		GewaOrder order = daoService.getObject(GewaOrder.class, orderid);
		if(order==null) return showJsonError_NOT_FOUND(model);
		if(!ValidateUtil.isMobile(mobile)) return showJsonError(model, "�ֻ���ʽ����ȷ");
		if (order.isAllPaid() || order.isCancel()) return showJsonError(model, "�����޸���֧�����ѣ���ʱ��ȡ���Ķ�����");
		order.setMobile(mobile);
		daoService.saveObject(order);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/admin/drama/agent/sendMobile.xhtml")
	public String sendMobile(HttpServletRequest request, String mobile, ModelMap model){
		if(!ValidateUtil.isMobile(mobile)){
			return showJsonError(model, "�ֻ��Ÿ�ʽ����");
		}
		String ip = WebUtils.getRemoteIp(request);
		ErrorCode<SMSRecord> code = bindMobileService.refreshNoSecurityBindMobile(BindConstant.TAG_REGISTERCODE, mobile, ip, BindConstant.ADMIN_MOBILE_TEMPLATE);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		untransService.sendMsgAtServer(code.getRetval(), false);
		return showJsonSuccess(model);
	}
}
