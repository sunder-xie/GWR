/**
 * 
 */
package com.gewara.bank;

public class OrderQuery {
	private String OrderNo;			//������
	private String MerchantNo;		//�̻���
	private String Mac;				//��ǩ
	private Integer SignType;		//��ǩ����
	private String Remark1;
	private String Remark2;
	public String getRemark1() {
		return Remark1;
	}
	public void setRemark1(String remark1) {
		Remark1 = remark1;
	}
	public String getRemark2() {
		return Remark2;
	}
	public void setRemark2(String remark2) {
		Remark2 = remark2;
	}
	public String getOrderNo() {
		return OrderNo;
	}
	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}
	public String getMerchantNo() {
		return MerchantNo;
	}
	public void setMerchantNo(String merchantNo) {
		MerchantNo = merchantNo;
	}
	public String getMac() {
		return Mac;
	}
	public void setMac(String mac) {
		Mac = mac;
	}
	public Integer getSignType() {
		return SignType;
	}
	public void setSignType(Integer signType) {
		SignType = signType;
	}
	
}
