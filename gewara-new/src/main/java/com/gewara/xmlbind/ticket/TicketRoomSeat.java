package com.gewara.xmlbind.ticket;

public class TicketRoomSeat {
	private Long id;
	private Long roomid;		//Ӱ��ID
	private Integer lineno;		//ǰ��ڼ���,ϵͳ����
	private Integer rankno;		//����ڼ���,ϵͳ����
	private String seatline;	//��λ�к�
	private String seatrank;	//��λ�к�
	private String loveInd;		//������ 0����ͨ��λ 1������������λ��� 2���������ڶ���λ���
	private String seatno;		//��λ���
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRoomid() {
		return roomid;
	}
	public void setRoomid(Long roomid) {
		this.roomid = roomid;
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
	public String getLoveInd() {
		return loveInd;
	}
	public void setLoveInd(String loveInd) {
		this.loveInd = loveInd;
	}
	public String getSeatno() {
		return seatno;
	}
	public void setSeatno(String seatno) {
		this.seatno = seatno;
	}
	
}
