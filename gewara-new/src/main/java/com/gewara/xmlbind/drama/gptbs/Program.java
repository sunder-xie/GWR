package com.gewara.xmlbind.drama.gptbs;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;
//��Ŀ
public class Program extends BaseObject{
	private static final long serialVersionUID = 4965724876634999303L;
	private Long id;				//ID
	private String code;			//��Ŀ���
	private String cnName;			//��Ŀ������
	private String enName;			//��ĿӢ����
	private Long stadiumId;			//����
	private Long venueId;			//����
	private Timestamp startTime;	//��Ŀ��ʼʱ��
	private Timestamp endTime;		//��Ŀ����ʱ��
	private Integer typeId;			//��Ŀ����
	private String available;		//�Ƿ���Ч
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public Long getStadiumId() {
		return stadiumId;
	}
	public void setStadiumId(Long stadiumId) {
		this.stadiumId = stadiumId;
	}
	public Long getVenueId() {
		return venueId;
	}
	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public Integer getTypeId() {
		return typeId;
	}
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
	public String getAvailable() {
		return available;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	
}
