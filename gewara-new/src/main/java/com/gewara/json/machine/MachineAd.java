package com.gewara.json.machine;


//һ������
public class MachineAd {
	private Long id;			
	private String venueid;		//����id
	private String adversion;	//�汾
	private String zipurl;		//ѹ������ַ
	private String remark;		//����
	private String addtime;
	private String startTime; //�����Ч��ʼʱ��
	private String endTime; //�����Ч����ʱ��
	private String type;//��������
	private String nickName;//�����id
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVenueid() {
		return venueid;
	}
	public void setVenueid(String venueid) {
		this.venueid = venueid;
	}
	public String getAdversion() {
		return adversion;
	}
	public void setAdversion(String adversion) {
		this.adversion = adversion;
	}
	public String getZipurl() {
		return zipurl;
	}
	public void setZipurl(String zipurl) {
		this.zipurl = zipurl;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
