package com.gewara.xmlbind.drama.gptbs;


public class PlaceFieldAreaSeat {
	
	private Long id;				//ID
	private String lineno;			//��
	private String rankno;			//��(��)
	private Integer x;				//��������X
	private Integer y;				//��������Y
	private Integer status;			//״̬
	private Integer serialNum;		//���
	
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(Integer serialNum) {
		this.serialNum = serialNum;
	}

}
