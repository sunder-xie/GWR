package com.gewara.command;

import java.sql.Timestamp;

import com.gewara.constant.Status;

/**
 * ��������Bean
 * @author acerge(acerge@163.com)
 * @since 12:13:41 PM Oct 17, 2009
 */
public class SearchCourseCommand {
	private Long specialid;		//�γ�ID
	private Long gymid;			//����ID
	private String week;		//����
	private Long memberid;		//�û�ID
	private Integer minute;		//��ѯ����
	private String mobile;		//�ֻ���
	private String bespokeNo;	//���׺�
	private String status = Status.Y;
	private Timestamp timeFrom;//�µ�ʱ�䷶Χ
	private Timestamp timeTo;
	private String gid;			//����
	private Long orderid;
	public Long getGymid() {
		return gymid;
	}
	public void setGymid(Long gymid) {
		this.gymid = gymid;
	}

	public Timestamp getTimeFrom() {
		return timeFrom;
	}
	public void setTimeFrom(Timestamp timeFrom) {
		this.timeFrom = timeFrom;
	}
	public Timestamp getTimeTo() {
		return timeTo;
	}
	public void setTimeTo(Timestamp timeTo) {
		this.timeTo = timeTo;
	}

	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getMinute() {
		return minute;
	}
	public void setMinute(Integer minute) {
		if(minute!=null && minute>14400) this.minute=14400;
		else this.minute = minute;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getSpecialid() {
		return specialid;
	}
	public void setSpecialid(Long specialid) {
		this.specialid = specialid;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getBespokeNo() {
		return bespokeNo;
	}
	public void setBespokeNo(String bespokeNo) {
		this.bespokeNo = bespokeNo;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public Long getOrderid() {
		return orderid;
	}
	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

}
