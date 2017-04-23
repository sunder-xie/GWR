package com.gewara.json.mobile;

import java.io.Serializable;

import com.gewara.model.BaseObject;

public class MobileInvite extends BaseObject {

	private static final long serialVersionUID = -1122172971222775219L;
	public static final String UNKNOW_ACT="unknow_act";
	private String id;
	private Long memberid;// ������id
	private String mobile;// �����ֻ���
	private String addtime;// ����ʱ��
	private Long registerid;// �ɹ�ע���Աid
	private String relatedid;// ����id
	private String apptype;// �����˲�Ʒ����
	private String ostype;// ������ϵͳ����

	public MobileInvite() {
	}

	public MobileInvite(Long memberid) {
		this.memberid = memberid;
	}

	public MobileInvite(Long memberid, String mobile) {
		this(memberid);
		this.mobile = mobile;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getMemberid() {
		return memberid;
	}

	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	@Override
	public Serializable realId() {
		return id;
	}

	public String getRelatedid() {
		return relatedid;
	}

	public void setRelatedid(String relatedid) {
		this.relatedid = relatedid;
	}

	public String getApptype() {
		return apptype;
	}

	public void setApptype(String apptype) {
		this.apptype = apptype;
	}

	public String getOstype() {
		return ostype;
	}

	public void setOstype(String ostype) {
		this.ostype = ostype;
	}

	public Long getRegisterid() {
		return registerid;
	}

	public void setRegisterid(Long registerid) {
		this.registerid = registerid;
	}

}
