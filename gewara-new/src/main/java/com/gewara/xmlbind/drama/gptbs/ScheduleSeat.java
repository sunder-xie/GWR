package com.gewara.xmlbind.drama.gptbs;

import java.io.Serializable;

import com.gewara.model.BaseObject;
//������λ
public class ScheduleSeat extends BaseObject{
	private static final long serialVersionUID = -1102028537356157035L;
	private Long id;				//ID
	private String lineno;			//��
	private String rankno;			//��(��)
	private Integer x;				//��������X
	private Integer y;				//��������Y
	private Long ticketPriceId;		//�۸�ID
	private Long venueAreaId;		//��������ID
	private Long ticketPoolId;		//Ʊ��ID
	private Long scheduleId;		//����ID
	private Integer status;			//״̬
	private Long programId;			//��ĿID
	private Integer serialNum;		//���
	public Serializable realId() {
		return id;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLineno() {
		return lineno;
	}
	public void setLineno(String lineno) {
		this.lineno = lineno;
	}
	public String getRankno() {
		return rankno;
	}
	public void setRankno(String rankno) {
		this.rankno = rankno;
	}
	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public Long getTicketPriceId() {
		return ticketPriceId;
	}
	public void setTicketPriceId(Long ticketPriceId) {
		this.ticketPriceId = ticketPriceId;
	}
	public Long getVenueAreaId() {
		return venueAreaId;
	}
	public void setVenueAreaId(Long venueAreaId) {
		this.venueAreaId = venueAreaId;
	}
	public Long getTicketPoolId() {
		return ticketPoolId;
	}
	public void setTicketPoolId(Long ticketPoolId) {
		this.ticketPoolId = ticketPoolId;
	}
	public Long getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getProgramId() {
		return programId;
	}
	public void setProgramId(Long programId) {
		this.programId = programId;
	}
	public Integer getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(Integer serialNum) {
		this.serialNum = serialNum;
	}
}
