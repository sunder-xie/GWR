package com.gewara.xmlbind.ticket;

import java.sql.Timestamp;

public class TicketRoom {
	private Long id;					//Ӱ��ID
	private String name;				//Ӱ������
	private Long cinemaid;				//ӰԺID
	private String roomnum;				//Ӱ�����
	private Integer linenum;			//��λ����
	private Integer ranknum;			//��λ����
	private Integer seatnum;			//��λ����
	private String roomtype;			//��λ����
	private Timestamp updatetime;		//����ʱ��
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getCinemaid() {
		return cinemaid;
	}
	public void setCinemaid(Long cinemaid) {
		this.cinemaid = cinemaid;
	}
	public String getRoomnum() {
		return roomnum;
	}
	public void setRoomnum(String roomnum) {
		this.roomnum = roomnum;
	}
	public Integer getLinenum() {
		return linenum;
	}
	public void setLinenum(Integer linenum) {
		this.linenum = linenum;
	}
	public Integer getRanknum() {
		return ranknum;
	}
	public void setRanknum(Integer ranknum) {
		this.ranknum = ranknum;
	}
	public Integer getSeatnum() {
		return seatnum;
	}
	public void setSeatnum(Integer seatnum) {
		this.seatnum = seatnum;
	}
	public String getRoomtype() {
		return roomtype;
	}
	public void setRoomtype(String roomtype) {
		this.roomtype = roomtype;
	}
	public Timestamp getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	
}
