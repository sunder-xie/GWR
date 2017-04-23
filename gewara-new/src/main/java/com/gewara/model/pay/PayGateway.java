package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;

public class PayGateway extends BaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5328656468568314767L;

	
	/**·�����ͣ���*/
	public static final String ROUTE_TYPE_NONE = "none";
	
	/**·�����ͣ���������*/
	public static final String ROUTE_TYPE_CITY = "city";
	
	/**·�����ͣ�ָ���̻���ʶ*/
	public static final String ROUTE_TYPE_MERCODE = "mercode";
	
	/**֧������״̬��δ����*/
	public static final String STATUS_NO_USE = "NO_USE";
	/**֧������״̬��ʹ����*/
	public static final String STATUS_IN_USE = "IN_USE";
	/**֧������״̬������*/
	public static final String STATUS_DESUETUDE = "DESUETUDE";
	
	private Long id ;
	private String gatewayCode;	//֧������
	private String gatewayName;	//֧����������
	private String supportBank;	//�Ƿ�֧������
	private String gatewayType;	//���ͣ�PLATFORM��֧��ƽ̨��BANK������ֱ����CARD����֧��
	private String status;		//״̬��NO_USE��δ���ã�IN_USE��ʹ���У�DESUETUDE��������
	private String bankTypeKey;	//��������key������ֻ������ģ�����{"C":"���ÿ�","KJ":"������ÿ�֧��"}
	private Timestamp updateTime;	//ͬ��ʱ��
	private String routeStatus; //�̻���·��״̬��OPEN��������CLOSE���رգ�Ĭ�ϣ�CLOSE
	private Timestamp modifyTime;	//�޸�ʱ��
	private String modifyUser;	//����޸���
	
	public PayGateway(){
		this.updateTime = new Timestamp(System.currentTimeMillis());
		this.routeStatus = ROUTE_TYPE_NONE;
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

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getRouteStatus() {
		return routeStatus;
	}

	public void setRouteStatus(String routeStatus) {
		this.routeStatus = routeStatus;
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
