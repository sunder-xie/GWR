package com.gewara.model.bbs;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;

public class Moderator extends BaseObject {
	private static final long serialVersionUID = -7731021100306840229L;
	public static final String TYPE_TODAY = "today";//���ջ���
	public static final String TYPE_HOT = "hot";//���Ż���
	public static final String TYPE_MEMBER = "member";//�û���ע����
	public static final Integer SHOW_TYPE_NONE = 0;//����ʾ
	public static final Integer SHOW_TYPE_WAP = 1;//���ֻ�������ʾ
	public static final Integer SHOW_TYPE_ALL = 2;//����վ���ֻ�����ʾ
	public static final Integer SHOW_TYPE_WEB = 3;//����վ��ʾ
	private Long id;
	private String memberid;
	private String title;
	private String summary;
	private Integer ordernum;
	private Timestamp addtime;
	private String type;
	private Integer showfloor;//�Ƿ���ʾ¥��
	private Integer showaddress;//��ʾ��ַ
	private String mstatus;//״̬
	private Integer commentcount;
	
	public String getMstatus() {
		return mstatus;
	}

	public void setMstatus(String mstatus) {
		this.mstatus = mstatus;
	}

	public Integer getShowfloor() {
		return showfloor;
	}

	public void setShowfloor(Integer showfloor) {
		this.showfloor = showfloor;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Moderator() {}
	
	public Moderator(String memberid) {
		this.type = TYPE_TODAY;
		this.ordernum = 0;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.showfloor = 0;
		this.showaddress = 0;
		this.memberid = memberid;
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

	public String getMemberid() {
		return memberid;
	}

	public void setMemberid(String memberid) {
		this.memberid = memberid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Integer getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(Integer ordernum) {
		this.ordernum = ordernum;
	}

	public Timestamp getAddtime() {
		return addtime;
	}

	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}

	public Integer getShowaddress() {
		return showaddress;
	}

	public void setShowaddress(Integer showaddress) {
		this.showaddress = showaddress;
	}

	public Integer getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(Integer commentcount) {
		this.commentcount = commentcount;
	}

}
