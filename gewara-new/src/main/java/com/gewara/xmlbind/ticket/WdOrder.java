package com.gewara.xmlbind.ticket;

import java.sql.Timestamp;

public class WdOrder {
	private String snid;//��ﶩ��id
	private Timestamp orderTime;//�µ�ʱ��
	private String cinemaId;//���ӰԺid
	private String cinemaName;//ӰԺ����
	private String seats;//��λ
	private Integer seatNum;//��λ��
	private Double ticketMoney;//���
	private String filmName;//ӰƬ����
	private String showDate;//��ӳ����
	private String showTime;//��ӳʱ��
	private String hallId;//Ӱ��ID
	private String hallName;//Ӱ������
	private Long cId;//ӰԺid
	private String payMode;//��ֵ ��ʾǮ����︶��
	
	public Long getcId() {
		return cId;
	}
	public void setcId(Long cId) {
		this.cId = cId;
	}
	public String getSnid() {
		return snid;
	}
	public void setSnid(String snid) {
		this.snid = snid;
	}
	public Timestamp getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Timestamp orderTime) {
		this.orderTime = orderTime;
	}
	public String getCinemaId() {
		return cinemaId;
	}
	public void setCinemaId(String cinemaId) {
		this.cinemaId = cinemaId;
	}
	public String getCinemaName() {
		return cinemaName;
	}
	public void setCinemaName(String cinemaName) {
		this.cinemaName = cinemaName;
	}
	public String getSeats() {
		return seats;
	}
	public void setSeats(String seats) {
		this.seats = seats;
	}
	public Integer getSeatNum() {
		return seatNum;
	}
	public void setSeatNum(Integer seatNum) {
		this.seatNum = seatNum;
	}
	public Double getTicketMoney() {
		return ticketMoney;
	}
	public void setTicketMoney(Double ticketMoney) {
		this.ticketMoney = ticketMoney;
	}
	public String getFilmName() {
		return filmName;
	}
	public void setFilmName(String filmName) {
		this.filmName = filmName;
	}
	public String getShowDate() {
		return showDate;
	}
	public void setShowDate(String showDate) {
		this.showDate = showDate;
	}
	public String getShowTime() {
		return showTime;
	}
	public void setShowTime(String showTime) {
		this.showTime = showTime;
	}
	public String getHallId() {
		return hallId;
	}
	public void setHallId(String hallId) {
		this.hallId = hallId;
	}
	public String getHallName() {
		return hallName;
	}
	public void setHallName(String hallName) {
		this.hallName = hallName;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
}
