package com.gewara.xmlbind.api;



//����ӰԺ��λ
public class ApiOpenTimeItem{
	private Long id;
	private Long ottid;			//����ID
	private Long fieldid;		//����ID
	private String fieldname;	//������
	private String hour;			//ʱ���
	private Integer norprice;	//��׼��
	private String status;		//����״̬
	private String playtime;	
	
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ApiOpenTimeItem))
			return false;
		final ApiOpenTimeItem temp = (ApiOpenTimeItem) o;
		return !(getId() != null ? !(getId().equals(temp.getId())) : (temp
				.getId() != null));
	}

	public int hashCode() {
		return (getId() != null ? getId().hashCode() : 0);
	}
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPlaytime() {
		return playtime;
	}
	public void setPlaytime(String playtime) {
		this.playtime = playtime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getFieldid() {
		return fieldid;
	}
	public void setFieldid(Long fieldid) {
		this.fieldid = fieldid;
	}
	public Long getOttid() {
		return ottid;
	}
	public void setOttid(Long ottid) {
		this.ottid = ottid;
	}
	public String getFieldname() {
		return fieldname;
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public Integer getNorprice() {
		return norprice;
	}

	public void setNorprice(Integer norprice) {
		this.norprice = norprice;
	}
}
