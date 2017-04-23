package com.gewara.json;

import java.io.Serializable;

public class WDOrderContrast implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1031706591270683305L;

	private String _id;
	
	private String date ;//����  2013-09-26
	
	private Long cinemaId;
	
	private String tradeNo; //������  �� wd��������wd�Ķ����ţ� gewara�ľ���gewa������
	
	private String orderType;//�������� ���ףı�ʾֻ������� gewaraû��¼��  gewa��ʾ���ϵͳδͬ����
	
	private String seats;//��λ
	
	private Integer seatNum;//��λ��
	
	private Integer ticketMoney;//���
	
	private String filmName;//ӰƬ����
	
	private String playTime;//��ӳʱ��
	
	private String addTime;
	
	private String roomName;
	
	private String payMode;
	
	public WDOrderContrast(){}
	
	public WDOrderContrast(String _id,String date,Long cinemaId,String tradeNo,String orderType,String seats,
			Integer seatNum,Integer ticketMoney,String filmName,String playTime,String addTime){
		this._id = _id;
		this.date = date;
		this.cinemaId = cinemaId;
		this.tradeNo = tradeNo;
		this.orderType = orderType;
		this.seats = seats;
		this.seatNum = seatNum;
		this.ticketMoney = ticketMoney;
		this.filmName = filmName;
		this.playTime = playTime;
		this.addTime = addTime;
	}
	
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public Long getCinemaId() {
		return cinemaId;
	}
	public void setCinemaId(Long cinemaId) {
		this.cinemaId = cinemaId;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
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
	public Integer getTicketMoney() {
		return ticketMoney;
	}
	public void setTicketMoney(Integer ticketMoney) {
		this.ticketMoney = ticketMoney;
	}
	public String getFilmName() {
		return filmName;
	}
	public void setFilmName(String filmName) {
		this.filmName = filmName;
	}
	public String getPlayTime() {
		return playTime;
	}

	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	
	
}
