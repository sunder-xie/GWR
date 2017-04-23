package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.BaseObject;

public class PayGatewayBank extends BaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6345576359683946792L;
	
	private Long id;	//����
	private Long gatewayId;	//֧������ID
	private String gwraBankCode;	//�������д���
	private String bankName;	//��������
	private String bankType;	//�������ͣ�����֧��ƽ̨���д��벻һ������֧������Ĭ��ֵΪ��DEFAULT
	private Timestamp updateTime;	//ͬ��ʱ��
	
	public PayGatewayBank(){
		this.updateTime = new Timestamp(System.currentTimeMillis());
	}
		
	public String getPayBank(){
		if(StringUtils.isBlank(bankType) || StringUtils.equals("DEFAULT", bankType)){
			return gwraBankCode;
		}
		return gwraBankCode + "_" + bankType;
	}
	
	@Override
	public Serializable realId() {
		// TODO Auto-generated method stub
		return id;
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

	public String getGwraBankCode() {
		return gwraBankCode;
	}

	public void setGwraBankCode(String gwraBankCode) {
		this.gwraBankCode = gwraBankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
}
