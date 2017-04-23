package com.gewara.web.action.gewapay;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.user.Member;
import com.gewara.pay.PayValidHelper;

@Controller
public class GewaPayJumpController extends BasePayController{
	/**
	 * �ؼۻ�������Ŷε��ֻ����������֤
	 * @param orderId
	 * @param spid
	 * @param model
	 * @return
	 */
	@RequestMapping("/gewapay/jump/validateSpMobile.xhtml")
	public String validateSpMobile(Long orderId,Long spid, ModelMap model) {
		Member member = getLogonMember();
		if(member == null){
			dbLogger.error("memer is null:orderid=" + orderId + "|spid=" + spid);
		}
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) {
			return show404(model, "�����޸����˵Ķ�����");
		}
		if (order.isAllPaid() || order.isCancel()) {
			return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		}
		model.put("order", order);
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		if(sd == null){
			return show404(model, "��ѡ����Żݻ����ȷ��������ѡ��");
		}
		return "gewapay/jumpValidateMobile.vm";
	}
	
	/**
	 * ������֤֧��2.0,�Żݻ-��ת����
	 * 
	 * @param orderId
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Mar 19, 2013 5:44:15 PM
	 */
	@RequestMapping("/gewapay/jump/unionPayFast.xhtml")
	public String unionPayFastJump(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "ALL");
		return unionPayFastJumpComm(orderId, spid, model);
	}
	
	/**
	 * �����й����У�������֤֧��2.0,�Żݻ-��ת����
	 * 
	 * 
	 * @param orderId
	 * @param spid
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 7, 2013 6:10:51 PM
	 */
	@RequestMapping("/gewapay/jump/unionPayFast/sz.xhtml")
	public String unionPayFastJumpForSZ(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "SZ");
		return unionPayFastJumpComm(orderId, spid, model);
	}
	
	/**
	 * ũ��-����������Ӫ��� ��������֤֧��2.0,�Żݻ-��ת����
	 * 
	 * @param orderId
	 * @param spid
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 28, 2013 5:30:39 PM
	 */
	@RequestMapping("/gewapay/jump/unionPayFast/nyyh.xhtml")
	public String unionPayFastJumpForNyyh(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "NYYH");
		return unionPayFastJumpComm(orderId, spid, model);
	}
	
	/**
	 * ����ũ���л��������֤֧��2.0,�Żݻ-��ת����
	 * 
	 * @param orderId
	 * @param spid
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 28, 2013 5:30:39 PM
	 */
	@RequestMapping("/gewapay/jump/unionPayFast/cqnsyh.xhtml")
	public String unionPayFastJumpForCqnsyh(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "CQNSYH");
		return unionPayFastJumpComm(orderId, spid, model);
	}
	
	/**
	 * �������ѽڻ��������֤֧��2.0,�Żݻ-��ת����
	 * 
	 * @param orderId
	 * @param spid
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 28, 2013 5:30:39 PM
	 */
	@RequestMapping("/gewapay/jump/unionPayFast/youjie.xhtml")
	public String unionPayFastJumpForYoujie(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "YOUJIE");
		return unionPayFastJumpComm(orderId, spid, model);
	}
	
	/**
	 * �����������ÿ����������Ϻ����̻��ţ���ÿ�������Ϊÿ��ÿ����ʹ��һ�Ρ�
	 * 
	 * @param orderId
	 * @param spid
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Jun 9, 2013 11:57:45 AM
	 */
	@RequestMapping("/gewapay/jump/unionPayFast/wzyh.xhtml")
	public String unionPayFastJumpForWzcb(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "WZCB");
		return unionPayFastJumpComm(orderId, spid, model);
	}
	
	/**
	 * ������л��������ʾ
	 * 
	 * @param orderId
	 * @param spid
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Jul 2, 2013 5:33:04 PM
	 */
	@RequestMapping("/gewapay/jump/unionPayFast/zdyh.xhtml")
	public String unionPayFastJumpForZdcb(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "ZDCB");
		return unionPayFastJumpComm(orderId, spid, model);
	}
	
	private String unionPayFastJumpComm(Long orderId,Long spid, ModelMap model) {
		Member member = getLogonMember();
		if(member == null){
			dbLogger.error("memer is null:orderid=" + orderId + "|spid=" + spid);
		}
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		
		SpecialDiscount sd = daoService.getObject(SpecialDiscount.class, spid);
		if(sd == null || StringUtils.isBlank(sd.getPaymethod())){
			return show404(model, "��ѡ����Żݻ����ȷ��������ѡ��");
		}
		
		List<String> limitPayList = paymentService.getLimitPayList();
		PayValidHelper valHelp = new PayValidHelper(sd.getPaymethod());
		String[] paymethodArr = StringUtils.split(sd.getPaymethod(), ",");
		for(String t : paymethodArr){
			limitPayList.remove(t);
		}
		valHelp.setLimitPay(limitPayList);
		model.put("valHelp", valHelp);
		
		return "gewapay/jumpunionPayFast.vm";
	}
	
	/**
	 * ����������֤֧��2.0,�Żݻ-��ת����
	 * 
	 * @param orderId
	 * @param model
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Mar 19, 2013 5:44:15 PM
	 */
	@RequestMapping("/gewapay/jump/unionPayFastAJS.xhtml")
	public String unionPayFastAJSJump(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/jumpunionPayFastAJS.vm";
	}
	
	@RequestMapping("/gewapay/jump/unionPayFastBJ.xhtml")
	public String unionPayFastBJJump(Long orderId, ModelMap model) {
		Member member = getLogonMember();
		GewaOrder order = daoService.getObject(GewaOrder.class, orderId);
		if (!order.getMemberid().equals(member.getId())) return show404(model, "�����޸����˵Ķ�����");
		if (order.isAllPaid() || order.isCancel()) return show404(model, "���ܱ�����֧�����ѣ���ʱ��ȡ���Ķ�����");
		model.put("order", order);
		return "gewapay/jumpunionPayFastBJ.vm";
	}
	
	
	
	@RequestMapping("/gewapay/jump/unionPayFast/shenzhenPingan.xhtml")
	public String shenzhenPingan(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "shenzhenPingAn");
		return unionPayFastJumpComm(orderId, spid, model);
	}
	@RequestMapping("/gewapay/jump/unionPayFast/guangzhouBocWeekOne.xhtml")
	public String guangzhouBocWeekOne(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "guangzhouBocWeekOne");
		return unionPayFastJumpComm(orderId, spid, model);
	}
	@RequestMapping("/gewapay/jump/unionPayFast/guangzhouBocMonthTwo.xhtml")
	public String guangzhouBocMonthTwo(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "guangzhouBocMonthTwo");
		return unionPayFastJumpComm(orderId, spid, model);
	}

	//�Ϻ�ũ��ҵ����
	@RequestMapping("/gewapay/jump/unionPayFast/srcb.xhtml")
	public String srcb(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "SRCB");
		return unionPayFastJumpComm(orderId, spid, model);
	}
	
	//��������
	@RequestMapping("/gewapay/jump/unionPayFast/psbc.xhtml")
	public String psbc(Long orderId,Long spid, ModelMap model) {
		model.put("activeType", "PSBC");
		return unionPayFastJumpComm(orderId, spid, model);
	}
}
