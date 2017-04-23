package com.gewara.web.action.gewapay;


import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.Config;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OrderProcessConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.model.api.ApiUser;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.pay.PartnerPayUtil;
import com.gewara.pay.Pay12580Util;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.util.WebUtils;

/**
 * �������ص��ӿ�
 * 
 * @author acerge(acerge@163.com)
 * @since 6:18:42 PM Mar 16, 2010
 */
@Controller
public class PartnerPayController extends BasePayController {
	@RequestMapping("/pay/partnerPayReturn.xhtml")
	public String partnerPayReturn(String key, String tradeno, String paidAmount, String checkvalue, ModelMap model,HttpServletRequest request) {
		String remoteIp = WebUtils.getRemoteIp(request);
		String params = WebUtils.getParamStr(request, true);
		String headers = WebUtils.getHeaderStr(request);
		dbLogger.error("�̼Ҷ�������Param:" + params);
		dbLogger.error("�̼Ҷ�������Header:" + headers + ", IP:" + remoteIp);
		
		TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeno, false);
		if (order == null) {
			return showError(model, "���������ڣ�");
		}
		ApiUser partner = daoService.getObjectByUkey(ApiUser.class, "partnerkey", key, true);
		if (partner == null) {
			return showError(model, "�̻������ڣ�");
		}
		if (!checkvalue.equalsIgnoreCase(PartnerPayUtil.getCheckValue(partner.getPrivatekey(), tradeno, paidAmount))) {
			return showError(model, "�Ƿ��Ķ�����Ϣ��");
		}
		model.put("tradeNo", tradeno);
		return "redirect:/partner/orderResult.xhtml";
	}

	// �ı����ݿ�״̬
	@RequestMapping("/pay/partnerPayNotify.xhtml")
	@ResponseBody
	public String partnerPayNotify(HttpServletRequest request, String key, String tradeno, 
			String paidAmount, String payseqno, String version, String checkvalue) {
		String remoteIp = WebUtils.getRemoteIp(request);
		String params = WebUtils.getParamStr(request, true);
		String headers = WebUtils.getHeaderStr(request);
		dbLogger.error("�̼Ҷ�������Param:" + params);
		dbLogger.error("�̼Ҷ�������Header:" + headers + ", IP:" + remoteIp);
		ApiUser partner = daoService.getObjectByUkey(ApiUser.class, "partnerkey", key, true);
		if (partner == null) {
			monitorService.saveSysWarn("�̼�API�����̼Ҳ�����,params:",  params + "\nheader:" + headers, RoleTag.jishu);
			return tradeno + "|" + payseqno + "|partner not exists";
		}
		if(!partner.isRole(ApiUser.ROLE_PAYORDER)){
			monitorService.saveSysWarn("�̼�API���󣺲�֧���̼�֧��,params:",  params + "\nheader:" + headers, RoleTag.jishu);
			return tradeno + "|" + payseqno + "|pay not supported";
		}
		String valid = PartnerPayUtil.validate(partner, tradeno, payseqno, paidAmount, checkvalue);
		if(StringUtils.equals("success", valid)) {
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + tradeno);
			orderMonitorService.addOrderPayCallback(tradeno, OrderProcessConstant.CALLTYPE_NOTIFY, PaymethodConstant.PAYMETHOD_PARTNERPAY, params + ",host=" + Config.getServerIp());
			if(!PartnerPayUtil.isValidIp(remoteIp, partner)){//�Ƿ�IP���ã�����
				monitorService.saveSysWarn("�̼�API�����̼Ҹ���Ƿ�IP����,params:", params + "\nheader:" + headers, RoleTag.jishu);
			}
			/* �����ڲ�ͬ״̬�»�ȡ������Ϣ�������̼����ݿ�ʹ����ͬ�� */
			int fee = new Double(paidAmount).intValue();
			try{
				ErrorCode<GewaOrder> result = paymentService.netPayOrder(tradeno, payseqno, fee, PaymethodConstant.PAYMETHOD_PARTNERPAY, "bk", partner.getBriefname());
				if(result.isSuccess()) processPay(tradeno, partner.getBriefname());
				TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeno, false);
				if (order == null) {
					monitorService.saveSysWarn("�̼�API�����̼Ҷ���������,params:", params + "\nheader:" + headers, RoleTag.jishu);
					return tradeno + "|" + payseqno + "|order not exists";
				}
			}catch(Exception e){
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "", e);
				monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  tradeno, RoleTag.dingpiao);
				return tradeno + "|" + payseqno + "|" + "�����쳣";
			}
			if(StringUtils.equals(version, "2.0")) return tradeno + "|" + payseqno + "|success";
			return "success";
		}else{
			monitorService.saveSysWarn("������֤����:" + valid + remoteIp, params, RoleTag.jishu);
			return tradeno + "|" + payseqno + "|" + valid;
		}
	}
	/**
	 * 
	 * @param request
	 * @param orderid 12580�����֧�������ɵĶ���id
	 * @param code 
	 * @param seq ������ˮ��
	 * @param platform
	 * @param amount ֱ�ӽ��
	 * @param msg ֧��˵��
	 * @param sign ǩ��У��
	 * @param refer gewara������
	 * @return
	 */
	// �ı����ݿ�״̬
	@RequestMapping("/pay/partner12580PayNotify.xhtml")
	@ResponseBody
	public String partner12580PayNotify(HttpServletRequest request, String orderid, String code,String seq,
			String platform, String amount,String msg,String sign,String refer) {
		String params = WebUtils.getParamStr(request, true);
		String headers = WebUtils.getHeaderStr(request);
		dbLogger.error("�̼Ҷ�������Param:" + params);
		dbLogger.error("�̼Ҷ�������Header:" + headers);
		if(!"0000".equals(code) || !"dnapay".equals(platform)){
			dbLogger.error("֧������������Ϊ��" + orderid + ".����:" + refer + "֧�����," + msg );
			monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  refer, RoleTag.dingpiao);
			return "200 OK";
		}

		ApiUser partner = daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_12580);
		String tradeNo = refer;
		TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeNo, false);
		if (order == null) {
			monitorService.saveSysWarn("�̼�API�����̼Ҷ���������,params:", params + "\nheader:" + headers, RoleTag.jishu);
			return tradeNo + "|" + seq + "|order not exists";
		}
		String valid = Pay12580Util.validate(partner, sign,orderid,order);
		if(StringUtils.equals("success", valid)) {
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + tradeNo);
			orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_NOTIFY, PaymethodConstant.PAYMETHOD_PARTNERPAY, params + ",host=" + Config.getServerIp());
			String remoteIp = WebUtils.getRemoteIp(request);
			if(!PartnerPayUtil.isValidIp(remoteIp, partner)){//�Ƿ�IP���ã�����
				monitorService.saveSysWarn("�̼�API�����̼Ҹ���Ƿ�IP����,params:", params + "\nheader:" + headers, RoleTag.jishu);
			}
			/* �����ڲ�ͬ״̬�»�ȡ������Ϣ�������̼����ݿ�ʹ����ͬ�� */
			int fee = new Double(amount).intValue()/100;
			try{
				ErrorCode<GewaOrder> result = paymentService.netPayOrder(tradeNo, seq, fee, PaymethodConstant.PAYMETHOD_PARTNERPAY, "bk", partner.getBriefname());
				if(result.isSuccess()) processPay(tradeNo, partner.getBriefname());
			}catch(Exception e){
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "", e);
				monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  tradeNo, RoleTag.dingpiao);
			}
			return "200 OK";
		}else{
			monitorService.saveSysWarn("�̼�API���󣺷Ƿ��Ķ�����Ϣ,params:", params + "\nheader:" + headers, RoleTag.jishu);
			return "valid is fail" + valid;
		}
	}
	
	//�ɲ�������̻�id
	List<Long> partnerIdList = Arrays.asList(new Long[]{
			PartnerConstant.PARTNER_SHOKW,//����E��
			PartnerConstant.PARTNER_IPTV,//iptv
			PartnerConstant.PARTNER_ANXIN_TERM,//��������
			PartnerConstant.PARTNER_ANXIN_WEB//��������
	});

	@RequestMapping("/pay/partnerPayCheck.xhtml")
	@ResponseBody
	public String partnerPayCheck(String tradeno){
		TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeno, false);
		if(order == null)return "partnerPayCheck order not find";
		if(partnerIdList.contains(order.getPartnerid())){
			return "success";
		}else{		
			dbLogger.error("�̼Ҷ������÷���URLΪ�գ�tradeno:" + tradeno);
			return "partnerIdList not contains:"+order.getPartnerid();
		}
	}
	
}
