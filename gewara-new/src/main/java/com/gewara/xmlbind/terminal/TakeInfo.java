package com.gewara.xmlbind.terminal;

import java.sql.Timestamp;

public class TakeInfo{
	private Long id;				
	private String tradeno;			//������
	private String serialno;		
	private Timestamp playtime;
	private String ordertype;		//��������
	private Long placeid;			//����id
	private Timestamp synchtime;	//ͬ��ʱ��
	private Timestamp callbacktime;	//���ػش�ʱ��
	private Timestamp taketime;		//ȡƱʱ��
	private Timestamp updatetime;	//����ʱ��
	private String synchtype;		//ͬ������
	private String callback;		//���ػش�
	private String synch;			//�Ƿ�ͬ��
	private Integer synchNum;		//ͬ������
	private String type;			
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTradeno() {
		return tradeno;
	}
	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	public String getSerialno() {
		return serialno;
	}
	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}
	public Timestamp getPlaytime() {
		return playtime;
	}
	public void setPlaytime(Timestamp playtime) {
		this.playtime = playtime;
	}
	public String getOrdertype() {
		return ordertype;
	}
	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}
	public Long getPlaceid() {
		return placeid;
	}
	public void setPlaceid(Long placeid) {
		this.placeid = placeid;
	}
	public Timestamp getSynchtime() {
		return synchtime;
	}
	public void setSynchtime(Timestamp synchtime) {
		this.synchtime = synchtime;
	}
	public Timestamp getCallbacktime() {
		return callbacktime;
	}
	public void setCallbacktime(Timestamp callbacktime) {
		this.callbacktime = callbacktime;
	}
	public Timestamp getTaketime() {
		return taketime;
	}
	public void setTaketime(Timestamp taketime) {
		this.taketime = taketime;
	}
	public Timestamp getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	public String getSynchtype() {
		return synchtype;
	}
	public void setSynchtype(String synchtype) {
		this.synchtype = synchtype;
	}
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getSynch() {
		return synch;
	}
	public void setSynch(String synch) {
		this.synch = synch;
	}
	public Integer getSynchNum() {
		return synchNum;
	}
	public void setSynchNum(Integer synchNum) {
		this.synchNum = synchNum;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
