package com.gewara.xmlbind.bbs;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.constant.Status;

public class ReComment implements Serializable {
	private static final long serialVersionUID = 6937601037315369836L;
	public static final String ADDRESS_WEB="web";
	public static final String ADDRESS_WAP="wap";
	public static final String TAG_COMMENT = "comment"; //�ظ�����
	public static final String TAG_RECOMMENT = "recomment"; //�ظ��ظ�
	private Long id;
	private Long memberid;
	private Long relatedid;
	private String body;
	private Timestamp addtime;
	private Long tomemberid;
	private String status;
	private String address;//������Դ
	private String tag;
	private Long transferid;
	private Integer isread;//��������Ƿ��ѿ�,0:δ����1���Ѷ�
	private Integer toRead;//��Իظ��Ƿ��ѿ�,0:δ����1���Ѷ�
	public Integer getIsread() {
		return isread;
	}
	public void setIsread(Integer isread) {
		this.isread = isread;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public ReComment(){}
	public ReComment(Long memberid) {
		this.memberid = memberid;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.status = Status.Y_NEW;
		this.address = ADDRESS_WEB;
		this.isread = 0;
		this.toRead = 0;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRelatedid() {
		return relatedid;
	}

	public void setRelatedid(Long relatedid) {
		this.relatedid = relatedid;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Timestamp getAddtime() {
		return addtime;
	}

	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public Long getTomemberid() {
		return tomemberid;
	}
	public void setTomemberid(Long tomemberid) {
		this.tomemberid = tomemberid;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Long getTransferid() {
		return transferid;
	}
	public void setTransferid(Long transferid) {
		this.transferid = transferid;
	}
	public Integer getToRead() {
		return toRead;
	}
	public void setToRead(Integer toRead) {
		this.toRead = toRead;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	
}
