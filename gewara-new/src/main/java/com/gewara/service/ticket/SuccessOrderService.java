package com.gewara.service.ticket;

import com.gewara.helper.order.DramaOrderContainer;
import com.gewara.helper.order.GoodsOrderContainer;
import com.gewara.helper.order.SportOrderContainer;
import com.gewara.helper.order.TicketOrderContainer;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;

public interface SuccessOrderService {
	/**
	 * �������ɹ���
	 * 1���Զ����ײ�
	 * 2�����Ӷ������
	 * @param order
	 * @return Container(order,smsList)
	 */
	TicketOrderContainer processTicketOrderSuccess(TicketOrder order);
	
	/**
	 * @param order
	 * @return Container(order,)
	 */
	TicketOrderContainer updateTicketOrderStats(TicketOrder order);
	
	DramaOrderContainer processDramaOrderSuccess(DramaOrder gewaOrder);

	SportOrderContainer processSportOrderSuccess(SportOrder gewaOrder);

	GoodsOrderContainer processGoodsOrderSuccess(GoodsOrder order);
}
