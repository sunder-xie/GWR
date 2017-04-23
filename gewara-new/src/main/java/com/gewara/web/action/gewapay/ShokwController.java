package com.gewara.web.action.gewapay;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.Config;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OrderProcessConstant;
import com.gewara.model.pay.GewaOrder;
import com.gewara.pay.CardpayUtil;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.util.WebUtils;

/**
 * ����E�ǻص��ӿ�
 * @author acerge(acerge@163.com)
 * @since 6:18:42 PM Mar 16, 2010
 */
@Controller
public class ShokwController extends BasePayController{
	@RequestMapping("/pay/shokwReturn.xhtml")
	public String shokwReturn(HttpServletRequest request, String tradeNo, String payamount, String checkvalue, ModelMap model){
		String params = WebUtils.getParamStr(request, true);
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, params);
		String valid = ""+CardpayUtil.validate(tradeNo, payamount, checkvalue);
		if(valid.contains("true")){
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + tradeNo);
			orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_RETURN, PaymethodConstant.PAYMETHOD_OKCARDPAY, params + ",host=" + Config.getServerIp());
			int fee = Integer.parseInt(payamount);
			model.put("tradeNo", tradeNo);
			String payseqno = "xxx";//�޺�
			try{
				ErrorCode<GewaOrder> result = paymentService.netPayOrder(tradeNo, payseqno, fee, PaymethodConstant.PAYMETHOD_OKCARDPAY, "bk", "����E��");
				if(result.isSuccess()) processPay(tradeNo, "����E��");
				if(result.getRetval().surePartner()) return "redirect:/partner/orderResult.xhtml";
				return "redirect:/gewapay/orderResult.xhtml";
			}catch(Exception e){
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "�����������ʧ�ܣ����촦��, �����ţ�" +  tradeNo, e);
				monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  tradeNo, RoleTag.dingpiao);
				GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
				if(order.sureOutPartner()) return "redirect:/partner/orderResult.xhtml";
				else return "redirect:/gewapay/orderResult.xhtml";
			}
		}
		return showError(model, "����ʧ�ܣ�");
	}
	//�ı����ݿ�״̬
	@RequestMapping("/pay/shokwNotify.xhtml")
	@ResponseBody
	public String shokwNotify(HttpServletRequest request, String tradeNo, String payamount, String checkvalue){
		String params = WebUtils.getParamStr(request, true);
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, params);
		String valid = ""+CardpayUtil.validate(tradeNo, payamount, checkvalue);
		if(valid.contains("true")){
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "ǩ���ɹ�" + tradeNo);
			orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_NOTIFY, PaymethodConstant.PAYMETHOD_OKCARDPAY, params + ",host=" + Config.getServerIp());
			/*�����ڲ�ͬ״̬�»�ȡ������Ϣ�������̻����ݿ�ʹ����ͬ��*/
			int fee = Integer.parseInt(payamount);
			String payseqno = "xxx";//�޺�
			try{
				ErrorCode<GewaOrder> result = paymentService.netPayOrder(tradeNo, payseqno, fee, PaymethodConstant.PAYMETHOD_OKCARDPAY, "bk", "����E��");
				if(result.isSuccess()) processPay(tradeNo, "����E��");
			}catch(Exception e){
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "�����������ʧ�ܣ����촦��, �����ţ�" +  tradeNo, e);
				monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" +  tradeNo, RoleTag.dingpiao);
			}
			return "֧���ɹ�" + tradeNo + "success";
		}else{
			return "pay failure(invalidate code)";
		}
	}
}
