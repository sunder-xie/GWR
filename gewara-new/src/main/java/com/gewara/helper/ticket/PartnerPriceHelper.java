package com.gewara.helper.ticket;

import com.gewara.model.ticket.OpenPlayItem;

public class PartnerPriceHelper {
	public PartnerPriceHelper(){
	}
	public int getPrice(OpenPlayItem opi){
		return opi.getGewaprice();
	}
	
	/**
	 * �����
	 * @param opi
	 * @return
	 */
	public int getServiceFee(OpenPlayItem opi){
		//�����=���߼۸�-�ɱ���
		int fee=this.getPrice(opi)-opi.getCostprice();
		return fee<0?0:fee;
	}
}
