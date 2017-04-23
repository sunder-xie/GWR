package com.gewara.model.bbs.commu;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;

public class VisitCommuRecord extends BaseObject {
	
	private static final long serialVersionUID = 8561087145219580142L;
	private Long id;
	private Long commuid; //Ȧ��id
	private Long memberid;//��Աid
	private Integer visitnum; //���ʴ���
	private Timestamp addtime;//���η���ʱ��
	private Timestamp lasttime; //�ϴη���ʱ��
	
	public VisitCommuRecord() {}
	
	public VisitCommuRecord(Long memberid){
		this.addtime=new Timestamp(System.currentTimeMillis());
		this.memberid = memberid;
	}
	public VisitCommuRecord(Long memberid, Long commuid){
		this.addtime=new Timestamp(System.currentTimeMillis());
		this.lasttime=this.addtime;
		this.commuid = commuid;
		this.memberid = memberid;
		this.visitnum = 1;
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
	public Long getCommuid() {
		return commuid;
	}
	public void setCommuid(Long commuid) {
		this.commuid = commuid;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Integer getVisitnum() {
		return visitnum;
	}
	public void setVisitnum(Integer visitnum) {
		this.visitnum = visitnum;
	}
	public Timestamp getAddtime() {
		return addtime;
	}
	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	public Timestamp getLasttime() {
		return lasttime;
	}
	public void setLasttime(Timestamp lasttime) {
		this.lasttime = lasttime;
	}
	public void addVisitNum(){
		this.visitnum +=1;
	}
}
