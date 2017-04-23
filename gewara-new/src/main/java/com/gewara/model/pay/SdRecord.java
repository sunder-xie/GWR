package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;

/**
 * SpecialDiscount addOrderRecord�ؼۻ������ϸ��¼�������ָ��µ�����
 * @author gebiao(ge.biao@gewara.com)
 * @since Jul 8, 2013 8:46:43 PM
 */
public class SdRecord extends BaseObject {
	private static final long serialVersionUID = -1582775445829862665L;
	private String tradeNo;
	private Integer quantity;
	private Long spcounterid;		//����
	private Timestamp validtime;	//��Чʱ�䣺����ʱ��Ļָ�����
	private String cpcounters;		//�������Ӽ���������������ŷָ�
	
	public SdRecord(){}
	public SdRecord(String tradeNo, int quantity, Long spcounterid, String cpcounters, Timestamp validtime){
		this.tradeNo = tradeNo;
		this.quantity = quantity;
		this.spcounterid = spcounterid;
		this.cpcounters = cpcounters;
		this.validtime = validtime;
	}
	@Override
	public Serializable realId() {
		return tradeNo;
	}

	public Long getSpcounterid() {
		return spcounterid;
	}

	public void setSpcounterid(Long spcounterid) {
		this.spcounterid = spcounterid;
	}

	public Timestamp getValidtime() {
		return validtime;
	}

	public void setValidtime(Timestamp validtime) {
		this.validtime = validtime;
	}

	public String getCpcounters() {
		return cpcounters;
	}

	public void setCpcounters(String cpcounters) {
		this.cpcounters = cpcounters;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
