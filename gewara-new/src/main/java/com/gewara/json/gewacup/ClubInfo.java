package com.gewara.json.gewacup;

import java.io.Serializable;
import java.util.Date;


public class ClubInfo implements Serializable{
	
	private static final long serialVersionUID = 4045168091886382589L;
	private Long id;			//Ȧ��ID����
	private String communame;	//Ȧ������
	private String contact;		//��ϵ��
	private String idcards;		//���֤
	private String idcardslogo;	//���֤��ӡ��
	private String phone;		//��ϵ�˵绰
	private Date addtime;	//���ʱ��
	private String yearstype;	//�ٰ����
	private Long memberid;		//������ID
	private String membername;
	private Long orderid;		//������
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getMembername() {
		return membername;
	}
	public void setMembername(String membername) {
		this.membername = membername;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getIdcards() {
		return idcards;
	}
	public void setIdcards(String idcards) {
		this.idcards = idcards;
	}
	public String getIdcardslogo() {
		return idcardslogo;
	}
	public void setIdcardslogo(String idcardslogo) {
		this.idcardslogo = idcardslogo;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	public String getYearstype() {
		return yearstype;
	}
	public void setYearstype(String yearstype) {
		this.yearstype = yearstype;
	}
	public Long getOrderid() {
		return orderid;
	}
	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}
	public String getCommuname() {
		return communame;
	}
	public void setCommuname(String communame) {
		this.communame = communame;
	}
}