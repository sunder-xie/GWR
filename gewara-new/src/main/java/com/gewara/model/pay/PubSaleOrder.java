package com.gewara.model.pay;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.Status;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.util.DateUtil;

public class PubSaleOrder extends GewaOrder{
	private static final long serialVersionUID = 3626427979990078589L;
	private Long pubid;		//���Ļ��id
	private Integer costprice;		//�ɱ���
	public PubSaleOrder(){
		
	}
	public PubSaleOrder(PubSale sale){
		this.itemfee = 0;
		this.otherfee = 0;
		this.discount = 0;
		this.wabi = 0;
		this.createtime = new Timestamp(System.currentTimeMillis());
		this.addtime = createtime;
		this.updatetime = createtime;
		this.modifytime = createtime;
		this.paymethod = PaymethodConstant.PAYMETHOD_GEWAPAY;
		this.validtime = DateUtil.addMinute(this.addtime, sale.getUnitMinute());
		this.status = OrderConstant.STATUS_NEW;
		this.pricategory = OrderConstant.ORDER_PRICATEGORY_PUBSALE;
		this.alipaid = 0;
		this.gewapaid = 0;
		this.memberid = sale.getMemberid();
		this.membername = sale.getNickname();
		this.pubid = sale.getId();
		this.unitprice = sale.getCurprice()/100;
		this.quantity = 1;
		this.totalfee = this.unitprice;
		this.ordertitle = sale.getName();
		this.partnerid = 1L;
		this.ukey = sale.getMemberid()+"";
		this.settle = OrderConstant.SETTLE_NONE;
		this.express = Status.N;
	}
	public Long getPubid() {
		return pubid;
	}
	public void setPubid(Long pubid) {
		this.pubid = pubid;
	}
	public String getOrdertype(){
		return "pubsale";
	}
	public String getSuccessMsg(){
		return "��������������ܰ��ʾ��������ĸ��������������Ļ�Ѿ��ĳɹ�����������Чʱ���ڶԸö�������ȷ�ϼ�֧����лл��";
	}
	public String getPostMsg(String company, String sno){
		String result = "��������������ܰ��ʾ�������б���Ʒ�Ѽĳ���";
		if(StringUtils.isNotBlank(company)) {
			result = result + "��ݹ�˾Ϊ"+company;
		}
		if(StringUtils.isNotBlank(sno)){
			result = result + "������Ϊ" + sno;
		}
		result = result + "����ע����գ�лл��";
		return result;
	}
	public Integer getCostprice() {
		return costprice;
	}
	public void setCostprice(Integer costprice) {
		this.costprice = costprice;
	}
}
