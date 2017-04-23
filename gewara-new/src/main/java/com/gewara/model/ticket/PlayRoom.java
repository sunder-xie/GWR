package com.gewara.model.ticket;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.BaseObject;

public abstract class PlayRoom extends BaseObject {
	private static final long serialVersionUID = -1894201195221566397L;
	protected Long id;
	protected String roomname;			//����
	protected String content;			//��ϸ����
	protected Integer linenum;			//��λ����
	protected Integer ranknum;			//��λ����
	protected Integer seatnum;			//��λ����
	protected String num;				//����
	protected String sections;			//����������ö��Ÿ���
	protected String roomtype;
	protected Timestamp updatetime;		//��λ����ʱ��
	protected Integer firstline;
	protected Integer firstrank;		
	protected String logo;				//LOGO
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
	public String getRoomname() {
		return roomname;
	}
	public void setRoomname(String roomname) {
		this.roomname = roomname;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getLinenum() {
		return linenum;
	}
	public void setLinenum(Integer linenum) {
		this.linenum = linenum;
	}
	public Integer getRanknum() {
		return ranknum;
	}
	public void setRanknum(Integer ranknum) {
		this.ranknum = ranknum;
	}
	public Integer getSeatnum() {
		return seatnum;
	}
	public void setSeatnum(Integer seatnum) {
		this.seatnum = seatnum;
	}
	public String getSections() {
		return sections;
	}
	public void setSections(String sections) {
		this.sections = sections;
	}
	public Timestamp getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getLogo() {
		return logo;
	}
	public String getLimg() {
		if(StringUtils.isBlank(logo)) return "img/default_head.png";
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getRoomtype() {
		return roomtype;
	}
	public void setRoomtype(String roomtype) {
		this.roomtype = roomtype;
	}
	public Integer getFirstline() {
		return firstline;
	}
	public void setFirstline(Integer firstline) {
		this.firstline = firstline;
	}
	public Integer getFirstrank() {
		return firstrank;
	}
	public void setFirstrank(Integer firstrank) {
		this.firstrank = firstrank;
	}
}
