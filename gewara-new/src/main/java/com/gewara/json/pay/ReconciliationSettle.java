package com.gewara.json.pay;

import java.io.Serializable;

/**
 * ���ж���mogon�洢����
 * @author gang.liu
 * ��ǰֻ���ڽ�������
 */
public class ReconciliationSettle implements Serializable{

	private static final long serialVersionUID = -5976004654721880152L;
	private String _id;
	private String tradeNo;//������
	private String addTime;//�µ�ʱ��
	private String amount;//�������
	private String sysTraceNo;//���ж�����  ��Ϊ���ж���ϵͳ���ٺ�
	private String rspCd;//��Ӧ��
	private String settleDate;//��������
	private String authID;//Ԥ��Ȩ��
	private String payMethod;
	
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSysTraceNo() {
		return sysTraceNo;
	}
	public void setSysTraceNo(String sysTraceNo) {
		this.sysTraceNo = sysTraceNo;
	}
	public String getRspCd() {
		return rspCd;
	}
	public void setRspCd(String rspCd) {
		this.rspCd = rspCd;
	}
	public String getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}
	public String getAuthID() {
		return authID;
	}
	public void setAuthID(String authID) {
		this.authID = authID;
	}
}
