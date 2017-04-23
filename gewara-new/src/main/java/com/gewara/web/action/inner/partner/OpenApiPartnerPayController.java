package com.gewara.web.action.inner.partner;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.Config;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OrderProcessConstant;
import com.gewara.model.api.ApiUser;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.pay.PartnerPayUtil;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.util.WebUtils;
import com.gewara.web.action.gewapay.BasePayController;
import com.gewara.web.filter.OpenApiPartnerAuthenticationFilter;
import com.gewara.web.support.GewaVelocityView;
@Controller
public class OpenApiPartnerPayController extends BasePayController{
	// �ı����ݿ�״̬
	@RequestMapping("/openapi/partner/payNotify.xhtml")
	public String partnerPayNotify(HttpServletRequest request, String tradeno, String paidAmount, String payseqno, ModelMap model) {
		String ip = OpenApiPartnerAuthenticationFilter.getApiAuth().getPartnerIp();
		String params = WebUtils.getParamStr(request, true);
		String headers = WebUtils.getHeaderStr(request);
		dbLogger.error("�̼Ҷ�������Param:" + params);
		dbLogger.error("�̼Ҷ�������Header:" + headers + ", IP:" + ip);
		ApiUser partner = OpenApiPartnerAuthenticationFilter.getApiAuth().getApiUser();
		if (partner == null) {
			monitorService.saveSysWarn("�̼�API�����̼Ҳ�����,params:",  params + "\nheader:" + headers, RoleTag.jishu);
			return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tradeno + "|" + payseqno + "|partner not exists");
		}
		String valid = PartnerPayUtil.validateOpenApiPay(partner, tradeno, payseqno, paidAmount);
		if(StringUtils.equals("success", valid)) {
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + tradeno);
			orderMonitorService.addOrderPayCallback(tradeno, OrderProcessConstant.CALLTYPE_NOTIFY, PaymethodConstant.PAYMETHOD_PARTNERPAY, params + ",host=" + Config.getServerIp());
			if(!PartnerPayUtil.isValidIp(ip, partner)){//�Ƿ�IP���ã�����
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
					return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, tradeno + "|" + payseqno + "|order not exists");
				}
			}catch(Exception e){
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "", e);
				monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  tradeno, RoleTag.dingpiao);
				return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, tradeno + "|" + payseqno + "|" + "�����쳣");
			}
			return getSingleResultXmlView(model, "success");
		}else{
			monitorService.saveSysWarn("������֤����:" + valid + ip, params, RoleTag.jishu);
			return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tradeno + "|" + payseqno + "|" + valid);
		}
	}
	
	private String getErrorXmlView(ModelMap model, String errorcode, String msg){
		model.put("errmsg", msg);
		model.put("errcode", errorcode);
		model.put(GewaVelocityView.RENDER_XML, "true");
		model.put(GewaVelocityView.KEY_IGNORE_TOOLS, "true");
		return "api/error.vm";
	}
	private String getSingleResultXmlView(ModelMap model, String result){
		model.put(GewaVelocityView.RENDER_XML, "true");
		model.put(GewaVelocityView.KEY_IGNORE_TOOLS, "true");
		model.put("result", result);
		return "api/singleResult.vm";
	}
}
