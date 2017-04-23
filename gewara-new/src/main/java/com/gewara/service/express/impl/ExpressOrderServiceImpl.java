package com.gewara.service.express.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gewara.constant.ticket.OrderExtraConstant;
import com.gewara.model.acl.User;
import com.gewara.model.express.ExpressOrder;
import com.gewara.model.pay.OrderExtra;
import com.gewara.service.express.ExpressOrderService;
import com.gewara.service.impl.BaseServiceImpl;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.monitor.MonitorService;

@Service("expressOrderService")
public class ExpressOrderServiceImpl extends BaseServiceImpl implements ExpressOrderService {

	@Autowired@Qualifier("monitorService")
	protected MonitorService monitorService;
	
	private String getExpressOrderId(String expresstype, String expressnote){
		return expresstype + "_" + expressnote;
	}
	
	@Override
	public ErrorCode saveExpressOrder(String expressnote, String expresstype, List<String> tradeNoList, User user){
		if(StringUtils.isBlank(expressnote)) return ErrorCode.getFailure("��ݵ��Ų���Ϊ�գ�");
		if(StringUtils.isBlank(expresstype)) return ErrorCode.getFailure("������Ͳ���Ϊ�գ�");
		if(CollectionUtils.isEmpty(tradeNoList)) return ErrorCode.getFailure("�����Ų���Ϊ�գ�");
		if(!OrderExtraConstant.EXPRESS_TYPE_LIST.contains(expresstype)){
			return ErrorCode.getFailure("������ʹ���");
		}
		List<OrderExtra> extraList = new ArrayList<OrderExtra>();
		for (String tradeno : tradeNoList) {
			String tmp = StringUtils.trim(tradeno);
			OrderExtra extra = baseDao.getObjectByUkey(OrderExtra.class, "tradeno", tmp);
			if(extra == null) return ErrorCode.getFailure("��ȷ�϶����ţ�"+ tmp +"���Ƿ�ɹ���������ڣ�");
			if(StringUtils.isNotBlank(extra.getExpressnote()) && StringUtils.isNotBlank(extra.getExpresstype())){
				return ErrorCode.getFailure("�����ţ�" + tmp + "��ʹ�ÿ���̣�"+ OrderExtraConstant.getExpressTypeText(extra.getExpresstype())+ "����ݵ��ţ�" + extra.getExpressnote());
			}
			dbLogger.warn("userid:" + user.getId() + "order_extra:" + extra.getTradeno() +", expressnote:" + expressnote + ", expresstype:" + expresstype);
			extra.setExpressnote(expressnote);
			extra.setExpresstype(expresstype);
			extraList.add(extra);
		}
		String express = getExpressOrderId(expresstype, expressnote);
		ExpressOrder expressOrder = baseDao.getObject(ExpressOrder.class, express);
		if(expressOrder == null){
			expressOrder = new ExpressOrder(expressnote, expresstype);
			baseDao.saveObject(expressOrder);
			monitorService.saveAddLog(user.getId(), ExpressOrder.class, expressOrder.getId(), expressOrder);
		}
		baseDao.saveObjectList(extraList);
		return ErrorCode.SUCCESS;
	}
}
