package com.gewara.model.api;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;

public class ApiUserBusiness extends BaseObject{
	private static final long serialVersionUID = 1570825734279704022L;
	private Long id;
	private String showModel;		//չ�ַ�ʽ android ios wap pc �ն˻������ӻ�
	private String coopModel;		//����ģʽ
	private String createOrder;		//�Ƿ��������
	private String moneyto;			//�տ
	private String gewaBusUser;		//��������������
	private String gewaTecUser;		//����������������
	private String partnerBusUser;	//������������ϵ��
	private String partnerTecUser;	//�����̼�����ϵ��
	private Timestamp onTime;		//��������
	private Timestamp offTime;		//��������
	private String webSite;			//���ϵ�ַ
	private String remark;
	//����ģʽ:	1.��API���� ֧����ʽ�Ǻ����� ��IPTV���ն˻���QQ
	//		   	2.ѡ��ӰԺ��ӰƬ������API��ѡ����λ��iframeǶ�� ֧����ʽ�Ǻ����� ��taobao
	//   		3.ѡ��ӰԺ��ӰƬ������API��ѡ����λ��iframeǶ�� ֧����ʽ�Ǹ����� srcbshop
	//			4.��iframeǶ�� ֧����ʽ�Ǻ�����
	//			5.��iframeǶ�� ֧����ʽ�Ǹ�����
	// 			6.ѡ��ӰԺ��ӰƬ��API��ѡ��Ʊ��ת��������
	public ApiUserBusiness(){
		
	}
	@Override
	public Serializable realId() {
		return id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShowModel() {
		return showModel;
	}

	public void setShowModel(String showModel) {
		this.showModel = showModel;
	}

	public String getCoopModel() {
		return coopModel;
	}

	public void setCoopModel(String coopModel) {
		this.coopModel = coopModel;
	}

	public String getCreateOrder() {
		return createOrder;
	}

	public void setCreateOrder(String createOrder) {
		this.createOrder = createOrder;
	}

	public String getMoneyto() {
		return moneyto;
	}

	public void setMoneyto(String moneyto) {
		this.moneyto = moneyto;
	}

	public String getGewaBusUser() {
		return gewaBusUser;
	}

	public void setGewaBusUser(String gewaBusUser) {
		this.gewaBusUser = gewaBusUser;
	}

	public String getGewaTecUser() {
		return gewaTecUser;
	}

	public void setGewaTecUser(String gewaTecUser) {
		this.gewaTecUser = gewaTecUser;
	}

	public String getPartnerBusUser() {
		return partnerBusUser;
	}

	public void setPartnerBusUser(String partnerBusUser) {
		this.partnerBusUser = partnerBusUser;
	}

	public String getPartnerTecUser() {
		return partnerTecUser;
	}

	public void setPartnerTecUser(String partnerTecUser) {
		this.partnerTecUser = partnerTecUser;
	}

	public Timestamp getOnTime() {
		return onTime;
	}

	public void setOnTime(Timestamp onTime) {
		this.onTime = onTime;
	}

	public Timestamp getOffTime() {
		return offTime;
	}

	public void setOffTime(Timestamp offTime) {
		this.offTime = offTime;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
