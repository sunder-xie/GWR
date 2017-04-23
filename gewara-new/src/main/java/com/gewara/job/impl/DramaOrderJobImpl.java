package com.gewara.job.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.gewara.constant.sys.ConfigConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.job.JobService;
import com.gewara.model.common.GewaConfig;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.pay.SMSRecord;
import com.gewara.service.DaoService;
import com.gewara.service.MessageService;
import com.gewara.service.OperationService;
import com.gewara.service.drama.DramaProcessService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.UntransService;
import com.gewara.untrans.drama.DramaOrderProcessService;
import com.gewara.util.DateUtil;
import com.gewara.util.StringUtil;

public class DramaOrderJobImpl extends JobService {
	
	@Autowired@Qualifier("daoService")
	private DaoService daoService;
	
	@Autowired@Qualifier("messageService")
	private MessageService messageService;
	
	@Autowired@Qualifier("dramaOrderProcessService")
	private DramaOrderProcessService dramaOrderProcessService;
	
	@Autowired@Qualifier("dramaProcessService")
	private DramaProcessService dramaProcessService;
	
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	
	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	
	private Map<String, Integer> retryMap = new HashMap<String, Integer>();// �������
	
	public void correctOrder() {
		Long cur = System.currentTimeMillis();
		List<DramaOrder> failureList = dramaProcessService.getPaidUnfixOrderList(0, 50);
		if (failureList.size() == 0)
			return;
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_JOB, "�Զ�ȷ�ϡ���������������" + failureList.size() + "��");
		List<String> msgList = new ArrayList<String>();
		List<DramaOrder> processList = new ArrayList<DramaOrder>();
		List<DramaOrder> laterProcessList = new ArrayList<DramaOrder>();
		for (DramaOrder order : failureList) {
			if (retryMap.containsKey(order.getTradeNo())) {
				laterProcessList.add(order);
			} else {
				processList.add(order);
			}
		}
		processList.addAll(laterProcessList);
		for (DramaOrder order : processList) {
			Integer retried = retryMap.get(order.getTradeNo());
			if (retried == null) {
				retried = 0;
			}
			retried++;
			retryMap.put(order.getTradeNo(), retried);
			if (retried > 8) {
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_JOB, "���Դ������࣬����:" + order.getTradeNo());
				continue;
			}
			try {
				ErrorCode<String> result = null;
				if(order.needChangeSeat()){
					result = dramaOrderProcessService.reconfirmOrder(order, 1L, true, false);
				}else{
					result = dramaOrderProcessService.confirmSuccess(order, 1L, true);
				}
				if (result.isSuccess()) {
					String msg = "�Զ�ȷ�ϡ�������������" + order.getTradeNo() + "�ɹ���";
					dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_JOB, msg);
					msgList.add(msg);
				} else {
					String msg = "�Զ�ȷ�ϡ�����������ʧ�ܣ�" + order.getTradeNo() + result.getMsg();
					dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_JOB, msg);
					msgList.add(msg);
				}
			} catch (Exception e) {
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_JOB, StringUtil.getExceptionTrace(e, 5));
			}
			if (System.currentTimeMillis() - cur > DateUtil.m_minute * 13)
				break;
		}
	}

	public void sendWarningMsg() {
		try {
			int failureCount = dramaProcessService.getPaidFailureOrderCount();
			dbLogger.warn("ִ�в�ѯ֧��8Сʱ�ڵģ�10�����޸��µĶ�������Ϊ��" + failureCount);
			if (failureCount > 5) {
				boolean allowSendSMS = operationService.updateOperation("failureDramaOrderWarning", 60 * 60);
				if (allowSendSMS) {
					GewaConfig cfg = daoService.getObject(GewaConfig.class, ConfigConstant.CFG_YUNYIN_MOBILE);
					if (cfg != null && StringUtils.isNotBlank(cfg.getContent())) {
						String[] mobiles = StringUtils.split(cfg.getContent(), ",");
						for (String mobile : mobiles) {
							SMSRecord sms = messageService.addManualMsg(0L, mobile, "Ŀǰ�ݳ������������࣬Ϊ" + failureCount + "��", null);
							if (sms != null)
								untransService.sendMsgAtServer(sms, false);
						}
					}
				}
			}
		} catch (Exception e) {
			dbLogger.warn("����������", e);
		}
	}
}
