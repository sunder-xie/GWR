/**
 * 
 */
package com.gewara.web.action.gewapay;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.Config;
import com.gewara.constant.ChargeConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OrderProcessConstant;
import com.gewara.model.pay.Charge;
import com.gewara.model.pay.GewaOrder;
import com.gewara.pay.AlipaySmartMobileUtil;
import com.gewara.pay.PayUtil;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.untrans.ticket.OrderProcessService;
import com.gewara.util.WebUtils;
import com.gewara.util.XmlUtils;

@Controller
public class AlipaySmartMobileController extends BasePayController {
	public static final int RESULT_CHECK_SIGN_FAILED = 1; // ǩ���ɹ�
	public static final int RESULT_CHECK_SIGN_SUCCEED = 2; // ǩ��ʧ��
	@Autowired
	@Qualifier("orderProcessService")
	private OrderProcessService orderProcessService;

	public void setProcessOrderService(OrderProcessService orderProcessService) {
		this.orderProcessService = orderProcessService;
	}

	@RequestMapping("/pay/alipaySmartMobileNotify.xhtml")
	@ResponseBody
	public String alipaySmartMobileNotify(String sign, String notify_data, HttpServletRequest req) {
		String params = WebUtils.getParamStr(req, true);

		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, params);
		int retVal = RESULT_CHECK_SIGN_FAILED;
		Map<String, Object> resMap = XmlUtils.xml2Map(notify_data);
		boolean check = AlipaySmartMobileUtil.doCheck(notify_data, sign);
		if (check) {
			retVal = RESULT_CHECK_SIGN_SUCCEED;
			String trade_status = resMap.get("trade_status") + "";
			String tradeNo = resMap.get("out_trade_no") + "";
			String serialno = resMap.get("trade_no") + "";
			if (StringUtils.equalsIgnoreCase(trade_status, "TRADE_FINISHED") || StringUtils.equalsIgnoreCase(trade_status, "TRADE_SUCCESS")
					|| StringUtils.equalsIgnoreCase(trade_status, "WAIT_SELLER_SEND_GOODS")) { // �������
				orderMonitorService.addOrderPayCallback(tradeNo, OrderProcessConstant.CALLTYPE_NOTIFY, PaymethodConstant.PAYMETHOD_ALISMARTMOBILEPAY, params + ",host=" + Config.getServerIp());
				try {
					String total_fee = resMap.get("total_fee") + "";
					int fee = new Double(total_fee).intValue();
					if (PayUtil.isChargeTrade(tradeNo)) {
						ErrorCode<Charge> code = paymentService.bankPayCharge(tradeNo, true, serialno, fee, PaymethodConstant.PAYMETHOD_ALISMARTMOBILEPAY, "bk", PaymethodConstant.PAYMETHOD_ALISMARTMOBILEPAY,"alismart");

						if (code.isSuccess()) {
							processCharge(tradeNo, "֧���������ֻ�֧��");
							Charge charge = code.getRetval();
							if (ChargeConstant.isBankPay(charge.getChargetype())) {
								GewaOrder order = daoService.getObject(GewaOrder.class, charge.getOutorderid());
								try {
									orderProcessService.gewaPayOrderAtServer(order.getId(), order.getMemberid(), null, true);
								} catch (Exception e) {
									dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "�����������ʧ�ܣ����촦�������ţ�" + tradeNo, e);
									monitorService.saveSysWarn("�����������ʧ�ܣ����촦��", "�����ţ�" + tradeNo, RoleTag.dingpiao);
								}
							}
						}
					} else {
						ErrorCode<GewaOrder> result = paymentService.netPayOrder(tradeNo, serialno, fee, PaymethodConstant.PAYMETHOD_ALISMARTMOBILEPAY, null,
								"֧���������ֻ�֧��");
						if (result.isSuccess())
							processPay(tradeNo, "֧���������ֻ�֧��");
					}
					return "success";
				} catch (Exception e) {

				}
			} else {
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "֧���������ֻ�֧����֤ǩ���ɹ������Ƕ����Ƿ�֧��״̬��" + tradeNo + ", trade_status=" + trade_status);
			}
		} else {
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "֧���������ֻ�֧����֤ǩ��ʧ��");
		}
		return retVal + "";
	}
	/*
	 * trade_status : ����״̬ total_fee �����׽�� subject ����Ʒ���� out_trade_no
	 * ���ⲿ���׺ţ��̻����׺ţ� notify_reg_time ��֪ͨʱ�� trade_no ��֧�������׺�
	 */
	/**
	 * TRADE_FINISHED��������� WAIT_BUYER_PAY���ȴ�����
	 */
}
