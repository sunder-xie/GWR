package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.BaseObject;

public class PayMerchant extends BaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9165412894541987043L;
	
	public static final String DEFAULT_Y = "Y";//
	public static final String DEFAULT_N = "N";//
	
	private Long id;	//����
	private Long gatewayId;	//֧������ID
	private String merchantCode;	//�̻��ű�ʶ
	private String cityCode;	//����
	private String acquiringBank;	//�յ���
	private String description;	//�̻���˵��
	private String status;	//�̻���״̬��NO_USE��δ���ã�IN_USE��ʹ���У�DESUETUDE��������
	private Timestamp updateTime;	//ͬ��ʱ��	
	private String isDefault;	//�Ƿ�Ĭ��
	private Timestamp modifyTime;	//�޸�ʱ��
	private String modifyUser;	//����޸���
	
	public PayMerchant(){
		this.updateTime = new Timestamp(System.currentTimeMillis());
		this.isDefault = DEFAULT_N;
	}
	
	public PayMerchant(String modifyUser){
		this();
		this.modifyUser = modifyUser;
	}

	@Override
	public Serializable realId() {
		// TODO Auto-generated method stub
		return id;
	}
	
	public boolean isDefault(){
		return StringUtils.equals(PayMerchant.DEFAULT_Y, isDefault);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getAcquiringBank() {
		return acquiringBank;
	}

	public void setAcquiringBank(String acquiringBank) {
		this.acquiringBank = acquiringBank;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public Timestamp getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

}
