package com.gewara.xmlbind.pay.gateway;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="gateway")
public class BindPayGateway{
	
	private Long id ;
	private String gatewayCode;	//֧������
	private String gatewayName;	//֧����������
	private String supportBank;	//�Ƿ�֧������
	private String gatewayType;	//���ͣ�PLATFORM��֧��ƽ̨��BANK������ֱ����CARD����֧��
	private String status;		//״̬��NO_USE��δ���ã�IN_USE��ʹ���У�DESUETUDE��������
	private String bankTypeKey;	//��������key������ֻ������ģ�����{"C":"���ÿ�","KJ":"������ÿ�֧��"}
	
	@XmlElementWrapper(name = "merchants")
    @XmlElement(name = "merchant")
	private Set<BindPayMerchant> merchantList;
	
	@XmlElementWrapper(name = "banks")
    @XmlElement(name = "bank")
	private Set<BindPayGatewayBank> bankList;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGatewayCode() {
		return gatewayCode;
	}

	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}

	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public String getSupportBank() {
		return supportBank;
	}

	public void setSupportBank(String supportBank) {
		this.supportBank = supportBank;
	}

	public String getGatewayType() {
		return gatewayType;
	}

	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBankTypeKey() {
		return bankTypeKey;
	}

	public void setBankTypeKey(String bankTypeKey) {
		this.bankTypeKey = bankTypeKey;
	}

	public Set<BindPayMerchant> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(Set<BindPayMerchant> merchantList) {
		this.merchantList = merchantList;
	}

	public Set<BindPayGatewayBank> getBankList() {
		return bankList;
	}

	public void setBankList(Set<BindPayGatewayBank> bankList) {
		this.bankList = bankList;
	}

	

}
