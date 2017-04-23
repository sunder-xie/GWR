package com.gewara.xmlbind.partner;

import java.io.Serializable;

public class PartnerBoxPayUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String result;
	private String partnerUserId;
	private String parterId;//������ID
	private String iboxUserId;
	private String token;
	private String signType;//���ܷ�ʽ1,MD5  2 ,RSA
	private String signMsg;//ǩ������
	
	//����Ϊ�������� ����֧����������
	private String orderSerial;//���Ӷ�����ˮ��
	private String createTime;//���ɶ���ʱ��
	private String bizType;//ҵ������ 1����ӰƱ��2���ɻ�Ʊ��3����Ʊ
	private String orderNo;//������
	private String orderTime;//����ʱ��
	private String orderAmount;//�������Է�Ϊ��λ
	private String callbackUrl;//֧���ɹ����̻���Ҫ���ӻص��Ľӿ�·��
	private String orderStatus;//����״̬ Y����֧����N��δ֧��
	private String payTime;
	private String cutOffTime;
	
	private String sysRefNo;
	
	public String getSysRefNo() {
		return sysRefNo;
	}
	public void setSysRefNo(String sysRefNo) {
		this.sysRefNo = sysRefNo;
	}
	
	public String getCutOffTime() {
		return cutOffTime;
	}
	public void setCutOffTime(String cutOffTime) {
		this.cutOffTime = cutOffTime;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getPartnerUserId() {
		return partnerUserId;
	}
	public void setPartnerUserId(String partnerUserId) {
		this.partnerUserId = partnerUserId;
	}
	public String getParterId() {
		return parterId;
	}
	public void setParterId(String parterId) {
		this.parterId = parterId;
	}
	public String getIboxUserId() {
		return iboxUserId;
	}
	public void setIboxUserId(String iboxUserId) {
		this.iboxUserId = iboxUserId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSignType() {
		return signType;
	}
	public void setSignType(String signType) {
		this.signType = signType;
	}
	public String getSignMsg() {
		return signMsg;
	}
	public void setSignMsg(String signMsg) {
		this.signMsg = signMsg;
	}
	public String getOrderSerial() {
		return orderSerial;
	}
	public void setOrderSerial(String orderSerial) {
		this.orderSerial = orderSerial;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	

}
