package com.gewara.model.user;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;

/**
 * ���ⲿ�̼Һ�����¼�˺�
 * uk(loginname,source),uk(memberid)
 * @author acerge(acerge@163.com)
 * @since 2:24:19 PM Dec 12, 2010
 */
public class OpenMember extends BaseObject{
	private static final long serialVersionUID = 349587236L;

	private Long id;
	private Long memberid;			//�󶨵��û�
	private String loginname;		//��¼����Email��mobile��
	private String source;			//��Դ
	private String category;		//С�� ����֧��������Ϊ��ݵ�½��Ǯ��
	private String nickname;		//�������û����ǳ�
	private String otherinfo;		//��������
	private Long relateid;			//��ǰͬ���û�ID
	private Timestamp validtime;	//��Чʱ��
	public OpenMember(){}
	public OpenMember(Long memberid, String source, String loginname,Long relateid) {
		this.memberid = memberid;
		this.source = source;
		this.loginname = loginname;
		this.relateid = relateid;
	}
	public OpenMember(String source,String loginname,Long relateid){
		this.source = source;
		this.loginname = loginname;
		this.relateid = relateid;
	}
	public Long getRelateid() {
		return relateid;
	}
	public void setRelateid(Long relateid) {
		this.relateid = relateid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Timestamp getValidtime() {
		return validtime;
	}
	public void setValidtime(Timestamp validtime) {
		this.validtime = validtime;
	}
}
