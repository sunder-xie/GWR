package com.gewara.web.action.admin.express;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.service.express.ExpressOrderService;
import com.gewara.support.ErrorCode;
import com.gewara.web.action.admin.BaseAdminController;

@Controller
public class ExpressAdminController extends BaseAdminController {

	@Autowired@Qualifier("expressOrderService")
	private ExpressOrderService expressOrderService;
	
	@RequestMapping("/admin/express/ajax/saveExpress.xhtml")
	public String saveExpressOrder(String type, String expressnote, String tradenos, ModelMap model){
		if(StringUtils.isBlank(type)) return showJsonError(model, "������Ͳ���Ϊ�գ�");
		if(StringUtils.isBlank(expressnote)) return showJsonError(model, "��ݵ��Ų���Ϊ�գ�");
		if(StringUtils.isBlank(tradenos)) return showJsonError(model, "�����Ų���Ϊ�գ�");
		List<String> tradeNoList = Arrays.asList(StringUtils.split(tradenos, ","));
		if(CollectionUtils.isEmpty(tradeNoList)) return showJsonError(model, "�����Ų���Ϊ�գ�");
		ErrorCode code = expressOrderService.saveExpressOrder(expressnote, type, tradeNoList, getLogonUser());
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		return showJsonSuccess(model);
	}
}
