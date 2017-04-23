package com.gewara.model.pay;

import java.io.Serializable;

import com.gewara.constant.ticket.OrderConstant;
import com.gewara.model.BaseObject;

public class Discount extends BaseObject{
	private static final long serialVersionUID = -8589068211776703733L;
	private Long id;				//ID
	private Long orderid;			//����ID
	private String tag;				//�ۿ�����
	private Long relatedid;			//��������(�翨��,�û�ID,�̼�ID)
	private Long goodsid;			//��������Ʒ
	private String description;		//����
	private String cardtype;		//�ۿۿ���
	private Integer amount;			//�ۿ۽��
	private Long batchid;			//����ID
	private String status;			//״̬���μ�OrderConstant.DISCOUNT_STATUS
	@Override
	public Serializable realId() {
		return id;
	}
	public Long getBatchid() {
		return batchid;
	}

	public void setBatchid(Long batchid) {
		this.batchid = batchid;
	}

	public Discount(){}
	
	public Discount(Long orderid, String tag, Long relatedid, String cardtype){
		this.orderid = orderid;
		this.tag = tag;
		this.relatedid = relatedid;
		this.cardtype = cardtype;
		this.status = OrderConstant.DISCOUNT_STATUS_N;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Long getRelatedid() {
		return relatedid;
	}
	public void setRelatedid(Long relatedid) {
		this.relatedid = relatedid;
	}
	public Long getOrderid() {
		return orderid;
	}
	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Long getGoodsid() {
		return goodsid;
	}
	public void setGoodsid(Long goodsid) {
		this.goodsid = goodsid;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
