package com.gewara.model.pay;

import java.io.Serializable;

import com.gewara.model.BaseObject;

public class OtherFeeDetail extends BaseObject{
	private static final long serialVersionUID = 2420637586145837449L;
	public static final String FEETYPE_E = "E";		//��ݷ�
	public static final String FEETYPE_U = "U";		//�ƶ�������
	public static final String FEETYPE_C = "C";		//����������������ķ���
	
	
	private Long id;
	private Long orderid;		//������id
	private Integer fee;		//����
	private Integer quantity;	//����
	private String feetype;		//��������
	private String reason;		//ԭ��  ��orderid+reason��Ψһ
	
	public OtherFeeDetail(){
	}
	public OtherFeeDetail(Long orderid, String feetype, Integer fee, String reason){
		this(orderid, feetype, fee, reason, 1);
	}
	
	public OtherFeeDetail(Long orderid, String feetype, Integer fee, String reason, Integer quantity){
		this.orderid = orderid;
		this.feetype = feetype;
		this.fee = fee;
		this.reason = reason;
		this.quantity = quantity;
	}
	
	public OtherFeeDetail(OtherFeeDetail otherFeeDetail){
		this.orderid = otherFeeDetail.getOrderid();
		this.fee = otherFeeDetail.getFee();
		this.quantity = otherFeeDetail.getQuantity();
		this.feetype = otherFeeDetail.getFeetype();
		this.reason = otherFeeDetail.getReason();
	}
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getFeetype() {
		return feetype;
	}
	public void setFeetype(String feetype) {
		this.feetype = feetype;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrderid() {
		return orderid;
	}
	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}
	public Integer getFee() {
		return fee;
	}
	public void setFee(Integer fee) {
		this.fee = fee;
	}
	@Override
	public Serializable realId() {
		return id;
	}
}
