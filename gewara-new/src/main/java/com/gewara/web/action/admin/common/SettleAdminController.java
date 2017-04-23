package com.gewara.web.action.admin.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.order.SettleConfigConstant;
import com.gewara.model.acl.User;
import com.gewara.model.drama.Drama;
import com.gewara.model.drama.DramaSettle;
import com.gewara.model.sport.Sport;
import com.gewara.model.sport.SportItem;
import com.gewara.model.sport.SportSettle;
import com.gewara.service.SettleService;
import com.gewara.support.ErrorCode;
import com.gewara.util.VmUtils;
import com.gewara.web.action.admin.BaseAdminController;

@Controller
public class SettleAdminController extends BaseAdminController {

	@Autowired@Qualifier("settleService")
	private SettleService settleService;
	
	@RequestMapping("/admin/dramaTicket/getDramaSettle.xhtml")
	public String getDramaSettle(Long id, Long dramaid, ModelMap model) {
		Drama drama = null;
		if(id != null){
			DramaSettle settle = daoService.getObject(DramaSettle.class, id);
			if(settle == null) return showJsonError(model, "������ʲ����ڣ�");
			model.put("settle", settle);
			drama = daoService.getObject(Drama.class, settle.getDramaid());
		}else{
			drama = daoService.getObject(Drama.class, dramaid);
		}
		if(drama == null) return showJsonError(model, "�ݳ���Ŀ�����ڣ�");
		model.put("drama", drama);
		model.put("discountTypeMap", SettleConfigConstant.DISCOUNT_TYPEMAP);
		return "admin/theatreticket/dramasettle.vm";
	}
	
	@RequestMapping("/admin/dramaTicket/saveDramaSettle.xhtml")
	public String saveDramaSettle(Long id, Long dramaid, Double discount, String distype, ModelMap model) {
		User user = getLogonUser();
		DramaSettle settle = null;
		Drama drama = daoService.getObject(Drama.class, dramaid);
		if(drama == null) return showJsonError(model, "��Ŀ�����ڣ�");
		if(id!=null){
			settle = daoService.getObject(DramaSettle.class, id);
			if(settle == null) return showJsonError(model, "������ʲ����ڻ�ɾ����");
		}else {
			ErrorCode<DramaSettle> code = settleService.addDramaSettle(getLogonUser().getId(), dramaid, discount, distype);
			if(!code.isSuccess()) return showJsonError(model, code.getMsg());
			dbLogger.warn("�û���"+user.getId()+"�������:"+ VmUtils.formatPercent(discount, 100.0));
			settle = code.getRetval();
		}
		return showJsonSuccess(model, ""+settle.getId());
	}
	
	@RequestMapping("/admin/sport/open/saveSettle.xhtml")
	public String getSportSettle(Long sportid, Long itemid, Double discount, String remark, ModelMap model){
		Sport sport = daoService.getObject(Sport.class, sportid);
		if(sport == null) return showJsonError(model, "�ó��ݲ����ڣ�");
		SportItem item = daoService.getObject(SportItem.class, itemid);
		if(item == null) return showJsonError(model, "����Ŀ�����ڣ�");
		User user = getLogonUser();
		ErrorCode<SportSettle> resultCode = settleService.addSportSettle(user.getId(), sportid, itemid, discount, SettleConfigConstant.DISCOUNT_TYPE_PERCENT, remark);
		if(resultCode.isSuccess()){
			dbLogger.warn("�û���"+user.getId()+"�������:"+ VmUtils.formatPercent(discount, 100.0));
			return showJsonSuccess(model);
		}else{
			return showJsonError(model, resultCode.getMsg());
		}
	}
	
}
