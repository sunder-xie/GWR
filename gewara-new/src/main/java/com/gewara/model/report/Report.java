package com.gewara.model.report;

import java.io.Serializable;

import com.gewara.model.BaseObject;

/**
 * ��̬��ѯ����
 * @author gebiao(ge.biao@gewara.com)
 * @since Sep 6, 2012 3:11:13 PM
 */
public class Report extends BaseObject{
	private static final long serialVersionUID = -2178815430486728627L;
	private Long id;
	private String name;		//��ѯ����
	private String category;	//����
	private String qrysql;		//��ѯ���
	private String params;		//����[{"fieldorder":"1","fieldname":"addtime","fieldtype":"timestamp", "display":"����ʱ��"},{"fieldorder":"2","fieldname":"type","fieldtype":"string","selector":{"hfh":"����","gewara":"Gewara"}]
	private String fields;		//��ʾ�ֶ�[{"fieldorder":"1","fieldname":"addtime","fieldtype":"timestamp"}]
	private String displayname;	//��ʾ���� {"addtime":"����ʱ��", "type":"����"}
	private Integer maxnum;		//ÿҳ�������
	private String roles;		//��ɫȨ��
	private String description;	//��ϸ˵��
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public Integer getMaxnum() {
		return maxnum;
	}
	public void setMaxnum(Integer maxnum) {
		this.maxnum = maxnum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public String getQrysql() {
		return qrysql;
	}
	public void setQrysql(String qrysql) {
		this.qrysql = qrysql;
	}
}
