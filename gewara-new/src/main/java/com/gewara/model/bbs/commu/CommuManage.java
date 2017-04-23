package com.gewara.model.bbs.commu;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.BaseObject;

/**
 *    @function Ȧ����֤
 * 	@author bob.hu
 *		@date	2010-11-08 12:07:36
 */
public class CommuManage extends BaseObject {
	
	public static final String STATUS_NOTYET 	= "T";	// δ���
	public static final String STATUS_WAIT 	= "W";	// �ȴ����
	public static final String STATUS_PASS 	= "Y";	// ���ͨ��
	public static final String STATUS_NOTPASS = "N";	// ���δͨ��
	
	private Long id;
	private Long commuid;
	private Long applymemberid;
	private String checkstatus;
	private Timestamp addtime;
	private String realname;
	private Integer sex;
	private String idnumber;          
	private String contactphone;
	private String email;
	private String qq;
	private String msn;
	private String remark;
	
	private String alipayname;	// ֧������ʵ����
	private String alipay;		// ֧�����˻�
	
	/**
	 *  ��������(�߼���Ҫ)
	 */
	private String communame;

	private static final long serialVersionUID = 1L;
	@Override
	public Serializable realId() {
		return id;
	}
	
	public CommuManage(){}
	
	public CommuManage(Long commuid){
		this.commuid = commuid;
		this.checkstatus = CommuManage.STATUS_NOTYET;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.sex = 1;
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

	public Long getApplymemberid() {
		return applymemberid;
	}

	public void setApplymemberid(Long applymemberid) {
		this.applymemberid = applymemberid;
	}

	public String getCheckstatus() {
		return checkstatus;
	}

	public void setCheckstatus(String checkstatus) {
		this.checkstatus = checkstatus;
	}

	public Timestamp getAddtime() {
		return addtime;
	}

	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}



	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCommuname() {
		return communame;
	}

	public void setCommuname(String communame) {
		this.communame = communame;
	}

	public String getIdnumber() {
		return idnumber;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}

	public String getContactphone() {
		return contactphone;
	}

	public void setContactphone(String contactphone) {
		this.contactphone = contactphone;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAlipay() {
		return alipay;
	}

	public void setAlipay(String alipay) {
		this.alipay = alipay;
	}

	public String getAlipayname() {
		return alipayname;
	}

	public void setAlipayname(String alipayname) {
		this.alipayname = alipayname;
	}

	public String getEnmobile(){
		String result = contactphone;
		if(StringUtils.length(result)<=4) return result;
		return "*******" + result.substring(result.length()-4);
	}

}
