package com.gewara.xmlbind.drama.gptbs;

import java.io.Serializable;

import com.gewara.model.BaseObject;

public class Ticket extends BaseObject{
	private static final long serialVersionUID = -6581023678395472709L;
	private Long id;				//ticketPriceId
	private Long ticketLimit;		//������
	private Long ticketTotal;		//վƱ����
	private Long venueAreaId;		//��������id
	private Double price;			//�۸�
	private Long color;				//��ɫ
	private String description;		//����
	private Long ticketCount;		//Ʊ��
	private Long scheduleAreaId;	//��������ID
	private Long ticketPriceId;		//Ʊ��ID
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
	public Long getTicketLimit() {
		return ticketLimit;
	}
	public void setTicketLimit(Long ticketLimit) {
		this.ticketLimit = ticketLimit;
	}
	public Long getTicketTotal() {
		return ticketTotal;
	}
	public void setTicketTotal(Long ticketTotal) {
		this.ticketTotal = ticketTotal;
	}
	public Long getVenueAreaId() {
		return venueAreaId;
	}
	public void setVenueAreaId(Long venueAreaId) {
		this.venueAreaId = venueAreaId;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Long getColor() {
		return color;
	}
	public void setColor(Long color) {
		this.color = color;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getTicketCount() {
		return ticketCount;
	}
	public void setTicketCount(Long ticketCount) {
		this.ticketCount = ticketCount;
	}
	public Long getScheduleAreaId() {
		return scheduleAreaId;
	}
	public void setScheduleAreaId(Long scheduleAreaId) {
		this.scheduleAreaId = scheduleAreaId;
	}
	public Long getTicketPriceId() {
		return ticketPriceId;
	}
	public void setTicketPriceId(Long ticketPriceId) {
		this.ticketPriceId = ticketPriceId;
	}
	
}
