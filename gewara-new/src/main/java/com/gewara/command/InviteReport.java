package com.gewara.command;

import java.io.Serializable;

/**
 * �ֻ�������Ѷ��ŷ���ͳ��Model
 * @ClassName: InviteReport
 * @author <a href="mailto:yaoper@163.com">Yaoper</a>
 * @date Sep 3, 2012 3:54:21 PM
 * @version V1.0
 */
public class InviteReport implements Serializable{
	private static final long serialVersionUID = -5167685866117951141L;
	private String _id;
	private String day;//����
	private Integer sendNum;//��������
	private Integer failedNum;//����ʧ������
	private String channel;//����ͨ������������
	private Integer delay3MinNum;//����ʱ����ʱ����3��������
	private Integer less1MinNum;//����ʱ��1�����ڵ�����
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public Integer getSendNum() {
		return sendNum;
	}
	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}
	public Integer getFailedNum() {
		return failedNum;
	}
	public void setFailedNum(Integer failedNum) {
		this.failedNum = failedNum;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public Integer getDelay3MinNum() {
		return delay3MinNum;
	}
	public void setDelay3MinNum(Integer delay3MinNum) {
		this.delay3MinNum = delay3MinNum;
	}
	public Integer getLess1MinNum() {
		return less1MinNum;
	}
	public void setLess1MinNum(Integer less1MinNum) {
		this.less1MinNum = less1MinNum;
	}
}