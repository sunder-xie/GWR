package com.gewara.json.gewacup;

import java.io.Serializable;
import java.util.Date;


public class MiddleTable implements Serializable{
	
	private static final long serialVersionUID = 354896119229586409L;
	private String id;			
	private String fromPlayer;	//�����֤
	private String fromid;		//���players
	private String toid;		//���players
	private String toPlayer;	//�����֤���players
	private String type;		//����
	private Date addtime;		
	private String yearstype;	//�ٰ����
	private Long clubInfoId;	//���ֲ����
	private Long memberid;	
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFromPlayer() {
		return fromPlayer;
	}
	public void setFromPlayer(String fromPlayer) {
		this.fromPlayer = fromPlayer;
	}
	public String getToPlayer() {
		return toPlayer;
	}
	public void setToPlayer(String toPlayer) {
		this.toPlayer = toPlayer;
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
	public Long getClubInfoId() {
		return clubInfoId;
	}
	public void setClubInfoId(Long clubInfoId) {
		this.clubInfoId = clubInfoId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFromid() {
		return fromid;
	}
	public void setFromid(String fromid) {
		this.fromid = fromid;
	}
	public String getToid() {
		return toid;
	}
	public void setToid(String toid) {
		this.toid = toid;
	}

}