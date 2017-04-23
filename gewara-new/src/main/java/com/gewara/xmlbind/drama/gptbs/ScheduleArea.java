package com.gewara.xmlbind.drama.gptbs;

import java.io.Serializable;

import com.gewara.model.BaseObject;
//��������
public class ScheduleArea extends BaseObject{
	private static final long serialVersionUID = -1932439244314902861L;
	private Long id;					//ID
	private String cnName;				//����������
	private String enName;				//����Ӣ����
	private String description;			//��������
	private String standing;			//�Ƿ�վƱ
	private Integer total;				//վƱ����
	private Integer limit;				//������
	private Long venueId;				//�������ݱ��
	private Integer gridWidth;			//�����
	private Integer gridHeight;			//���߶�
	private Long venueAreaId;			//��������ID
	private Long scheduleId;			//����ID
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStanding() {
		return standing;
	}
	public void setStanding(String standing) {
		this.standing = standing;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public Long getVenueId() {
		return venueId;
	}
	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}
	public Integer getGridWidth() {
		return gridWidth;
	}
	public void setGridWidth(Integer gridWidth) {
		this.gridWidth = gridWidth;
	}
	public Integer getGridHeight() {
		return gridHeight;
	}
	public void setGridHeight(Integer gridHeight) {
		this.gridHeight = gridHeight;
	}
	public Long getVenueAreaId() {
		return venueAreaId;
	}
	public void setVenueAreaId(Long venueAreaId) {
		this.venueAreaId = venueAreaId;
	}
	public Long getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}
	
}
