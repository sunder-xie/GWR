package com.gewara.model.user;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.constant.Status;
import com.gewara.model.BaseObject;

/**
 * ��ʱ�˺�
 * @author gebiao(ge.biao@gewara.com)
 * @since Dec 2, 2013 4:22:31 PM
 */
public class TempMember extends BaseObject{
	private static final long serialVersionUID = -7348020080962170246L;
	public static final String MEMBERTYPE_NEW = "new";
	public static final String MEMBERTYPE_BIND = "bind";
	public static final String MEMBERTYPE_UNBIND = "unbind";
	
	private Long id;
	private String mobile;		//
	private String tmppwd;		//
	private Long memberid;		//���memberid��Ϊ�գ�������ʱ���ֻ���
	private String flag;		//��ʶ����ĳ���ʶ
	private String status;
	private String membertype;	//�û����ͣ�new ���û�,bind ���û��Ѱ�,unbind �����û�δ��
	private String otherinfo;	//��������
	private String ip;
	private Timestamp addtime;
	public TempMember(){}
	public TempMember(String mobile, String password, String flag, String ip){
		this.status = Status.N;
		this.mobile = mobile;
		this.tmppwd = password;
		this.flag = flag;
		this.ip = ip;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.otherinfo = "{}";
	}
	
	public TempMember(String mobile, Long memberid, String flag, String ip){
		this.status = Status.N;
		this.memberid = memberid;
		this.mobile = mobile;
		this.flag = flag;
		this.ip = ip;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.otherinfo = "{}";
	}
	
	@Override
	public Serializable realId() {
		return id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}

	public String getTmppwd() {
		return tmppwd;
	}

	public void setTmppwd(String tmppwd) {
		this.tmppwd = tmppwd;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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
	public String getMembertype() {
		return membertype;
	}
	public void setMembertype(String membertype) {
		this.membertype = membertype;
	}
	
}
