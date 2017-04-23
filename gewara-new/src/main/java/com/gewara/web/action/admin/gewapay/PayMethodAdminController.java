package com.gewara.web.action.admin.gewapay;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.PaymethodConstant;
import com.gewara.model.pay.PayMethod;
import com.gewara.util.ChangeEntry;
import com.gewara.web.action.admin.BaseAdminController;

@Controller
public class PayMethodAdminController extends BaseAdminController {

	@RequestMapping("/admin/gewapay/paymethodList.xhtml")
	public String payMethodList(ModelMap model){
		Map<String, String> payMap = PaymethodConstant.getPayTextMap();
		List<PayMethod> methodList = daoService.getObjectList(PayMethod.class, payMap.keySet());
		model.put("methodList", methodList);
		return "admin/gewapay/paymethodList.vm";
	}
	
	@RequestMapping("/admin/gewapay/getPaymethod.xhtml")
	public String getPaymethod(String paymethod, ModelMap model){
		if(StringUtils.isBlank(paymethod)){
			return showJsonError(model, "֧����ʽ����Ϊ�գ�");
		}
		PayMethod pay = daoService.getObject(PayMethod.class, paymethod);
		if(pay == null){
			return showJsonError(model, "֧����ʽ�����ڻ�ɾ����");
		}
		model.put("pay", pay);
		return "admin/gewapay/paymethod.vm";
	}
	
	@RequestMapping("/admin/gewapay/savePaymethod.xhtml")
	public String savePaymethod(String paymethod, String mangerUrl, ModelMap model){
		if(StringUtils.isBlank(paymethod)){
			return showJsonError(model, "֧����ʽ����Ϊ�գ�");
		}
		PayMethod pay = daoService.getObject(PayMethod.class, paymethod);
		if(pay == null){
			return showJsonError(model, "֧����ʽ�����ڻ�ɾ����");
		}
		ChangeEntry changeEntry = new ChangeEntry(pay);
		pay.setMangerUrl(StringUtils.trim(mangerUrl));
		daoService.saveObject(pay);
		monitorService.saveChangeLog(getLogonUser().getId(), PayMethod.class, pay.getPayMethod(), changeEntry.getChangeMap(pay));
		return showJsonSuccess(model);
	}
}
