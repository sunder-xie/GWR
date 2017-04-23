package com.gewara.xmlbind.drama.gptbs;

import java.io.Serializable;

import com.gewara.model.BaseObject;
//���μ۸�
public class SchedulePrice extends BaseObject{
	private static final long serialVersionUID = -3760191427326507862L;
	private Long id;				//ID
	private Long ticketPriceId;		//Ʊ��ID
	private Long scheduleAreaId;	//��������Id
	private Integer ticketLimit;		//������
	private Integer ticketTotal;		//վƱ����
	public Serializable realId() {
		return id;
	}
	
	public String getIdString(){
		return String.valueOf(id);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTicketPriceId() {
		return ticketPriceId;
	}
	public void setTicketPriceId(Long ticketPriceId) {
		this.ticketPriceId = ticketPriceId;
	}
	public Long getScheduleAreaId() {
		return scheduleAreaId;
	}
	public void setScheduleAreaId(Long scheduleAreaId) {
		this.scheduleAreaId = scheduleAreaId;
	}
	public Integer getTicketLimit() {
		return ticketLimit;
	}
	public void setTicketLimit(Integer ticketLimit) {
		this.ticketLimit = ticketLimit;
	}
	public Integer getTicketTotal() {
		return ticketTotal;
	}
	public void setTicketTotal(Integer ticketTotal) {
		this.ticketTotal = ticketTotal;
	}
	
}
