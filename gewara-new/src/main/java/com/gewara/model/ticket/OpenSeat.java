package com.gewara.model.ticket;

import java.io.Serializable;

import com.gewara.constant.ticket.SeatConstant;
import com.gewara.model.BaseObject;
import com.gewara.model.movie.RoomSeat;

public class OpenSeat extends BaseObject {
	private static final long serialVersionUID = -8401641826228009250L;
	private Long id;					
	private Long mpid;				//��������
	private String status;			//����״̬
	private Integer lineno;			//ǰ��ڼ���,ϵͳ����
	private Integer rankno;			//����ڼ���,ϵͳ����
	private String seatline;		//��λ�к�
	private String seatrank;		//��λ�к�
	private String seattype;		//�۸�����
	private String loveInd;			//������
	public OpenSeat(){}
	
	public OpenSeat(RoomSeat seat, Long mpid){
		this.lineno = seat.getLineno();
		this.rankno = seat.getRankno();
		this.seatline = seat.getSeatline();
		this.seatrank = seat.getSeatrank();
		this.seattype = SeatConstant.SEAT_TYPE_A;
		this.mpid = mpid;
		this.status = SeatConstant.STATUS_NEW;
		this.loveInd = seat.getLoveInd();
	}
	public String getSeatLabel(){
		return seatline+"��"+seatrank+"��";
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
	public Long getMpid() {
		return mpid;
	}
	public void setMpid(Long mpid) {
		this.mpid = mpid;
	}
	public String getStatus() {
		return status;
	}
	public String getKey(){
		return this.seatline+":"+this.seatrank;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getLineno() {
		return lineno;
	}
	public void setLineno(Integer lineno) {
		this.lineno = lineno;
	}
	public Integer getRankno() {
		return rankno;
	}
	public void setRankno(Integer rankno) {
		this.rankno = rankno;
	}
	public String getSeatline() {
		return seatline;
	}
	public void setSeatline(String seatline) {
		this.seatline = seatline;
	}
	public String getSeatrank() {
		return seatrank;
	}
	public void setSeatrank(String seatrank) {
		this.seatrank = seatrank;
	}
	public boolean isLocked() {
		return "BCD".contains(status);
	}
	public String getSeattype() {
		return seattype;
	}
	public void setSeattype(String seattype) {
		this.seattype = seattype;
	}
	public String getLoveInd() {
		return loveInd;
	}
	public void setLoveInd(String loveInd) {
		this.loveInd = loveInd;
	}
	public String getPosition(){
		return this.lineno+":" + this.rankno; 
	}
}
