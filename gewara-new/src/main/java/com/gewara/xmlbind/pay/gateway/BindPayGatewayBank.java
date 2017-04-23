package com.gewara.xmlbind.pay.gateway;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="bank")
public class BindPayGatewayBank{
	
	private Long id;	//����
	private Long gatewayId;	//֧������ID
	private String gwraBankCode;	//�������д���
	private String bankName;	//��������
	private String bankType;	//�������ͣ�����֧��ƽ̨���д��벻һ������֧������Ĭ��ֵΪ��DEFAULT
	
	
	public String getPayBank(){
		if(StringUtils.isBlank(bankType) || StringUtils.equals("DEFAULT", bankType)){
			return gwraBankCode;
		}
		return gwraBankCode + "_" + bankType;
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
}
