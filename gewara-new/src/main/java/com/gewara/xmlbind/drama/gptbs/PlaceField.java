package com.gewara.xmlbind.drama.gptbs;


public class PlaceField {
	private Long id;			//ID
	private String cnName;		//������
	private String enName;		//Ӣ����
	private String background;	//���ݵ�ͼ
	private Long stadiumId;		//��������
	private String available;	//�Ƿ���Ч
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
	public String getBackground() {
		return background;
	}
	public void setBackground(String background) {
		this.background = background;
	}
	public Long getStadiumId() {
		return stadiumId;
	}
	public void setStadiumId(Long stadiumId) {
		this.stadiumId = stadiumId;
	}
	public String getAvailable() {
		return available;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	
}
