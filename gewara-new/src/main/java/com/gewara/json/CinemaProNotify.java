package com.gewara.json;

import java.io.Serializable;

/**
 * �̼�ϵͳ����
 * @author gang.liu
 *
 */
public class CinemaProNotify implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -753235878383454430L;
	
	public static final String STATUS_NEW = "new";//������   new �� 
	public static final String STATUS_PROCESS_Y = "process_y";//������   ����ɹ� 
	public static final String STATUS_PROCESS_N = "process_n";//������   ����ʧ�� 

	private String _id;
	
	private String num; //���
	
	private Long cinemaId;
	
	private String cinemaName;
	
	private String addTime;
	
	private String title;//����
	
	private String content;//����
	
	private String publishUser;//�����û�
	
	private Long checkUserId;//�����û�
	
	private String checkUserName;//�����û�
	
	private String checkTime;//����ʱ��
	
	private String status;//������   new ��  process_y ����ɹ�  process_n ����ʧ��
	
	private String remark;//�����¼

	public CinemaProNotify(){}
	
	public CinemaProNotify(String _id,String num,Long cinemaId,String cinemaName,String addTime,
			String title,String content,String publishUser){
		this._id = _id;
		this.num = num;
		this.cinemaId = cinemaId;
		this.cinemaName = cinemaName;
		this.addTime = addTime;
		this.title = title;
		this.content = content;
		this.publishUser = publishUser;
		this.status = CinemaProNotify.STATUS_NEW;
		
	}
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Long getCinemaId() {
		return cinemaId;
	}

	public void setCinemaId(Long cinemaId) {
		this.cinemaId = cinemaId;
	}

	public String getCinemaName() {
		return cinemaName;
	}

	public void setCinemaName(String cinemaName) {
		this.cinemaName = cinemaName;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPublishUser() {
		return publishUser;
	}

	public void setPublishUser(String publishUser) {
		this.publishUser = publishUser;
	}

	public Long getCheckUserId() {
		return checkUserId;
	}

	public void setCheckUserId(Long checkUserId) {
		this.checkUserId = checkUserId;
	}

	public String getCheckUserName() {
		return checkUserName;
	}

	public void setCheckUserName(String checkUserName) {
		this.checkUserName = checkUserName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
