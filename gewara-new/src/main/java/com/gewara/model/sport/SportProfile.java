package com.gewara.model.sport;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.BaseObject;

public class SportProfile extends BaseObject{
	private static final long serialVersionUID = 2725805400089821424L;
	public static final String STATUS_OPEN = "open";
	public static final String STATUS_CLOSE = "close";
	public static final String EXITSRETURN_YES = "Y";	//֧����Ʊ
	public static final String EXITSRETURN_NO = "N";	//��֧����Ʊ
	public static final String RETURNMONEYTYPE_B= "B";	//���ն����ۿۣ�   ��A������Чֵ
	public static final String RETURNMONEYTYPE_C= "C";	//���չ̶�ֵ��   ��A������Чֵ
	public static final String RETURNMONEYTYPE_D= "D";	//���հٷֱȣ�   ��A������Чֵ
	
	public static final String EXITSCHANGE_YES = "Y";	//֧�ֻ�Ʊ
	public static final String EXITSCHANGE_NO = "N";	//��֧�ֻ�Ʊ
	public static final String CHANGEMONEYTYPE_D= "D";	//���հٷֱȣ�   ��A������Чֵ
	public static final String CHANGEMONEYTYPE_C= "C";	//���չ̶�ֵ��   ��A������Чֵ
	
	public static final String TICKETTYPE_B = "B";	//POS��ȡƱ�٣����ݷ�ȡƱ     ��A������Чֵ
	public static final String TICKETTYPE_C = "C";	//POS��ȡƱ�ڣ�����ȡƱ     ��A������Чֵ
	public static final String TICKETTYPE_D = "D";	//ƾ�ֻ��˹�ʶ�𣬳��ݹ�����Ա�˶���Ϣ     ��A������Чֵ
	public static final String TICKETTYPE_E = "E";	//�ֳ���Ʊ��������������Ա��Ʊ     ��A������Чֵ
	
	public static final String PRETYPE_ENTRUST = "E"; //ί�д���
	public static final String PRETYPE_MANAGE = "M";	//������Ӫ
	
	private Long id;	
	private String encryptCode;
	private String opentime;
	private String closetime;		
	private String booking;			//�Ƿ񿪷Ź�Ʊ
	private Integer sortnum;		//����
	private String company;			//��˾����
	private String pretype;			//Ԥ������
	private String premessage;		// ����˵��
	
	private String citycode;
	
	public Integer getSortnum() {
		return sortnum;
	}
	public void setSortnum(Integer sortnum) {
		this.sortnum = sortnum;
	}
	public SportProfile(){
		
	}
	public SportProfile(Long sportid){
		this.id = sportid;
		this.sortnum = 0;
	}
	public String getBooking() {
		return booking;
	}
	public void setBooking(String booking) {
		this.booking = booking;
	}
	public String getEncryptCode() {
		return encryptCode;
	}
	public void setEncryptCode(String encryptCode) {
		this.encryptCode = encryptCode;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public String getOpentime() {
		return opentime;
	}
	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}
	public String getClosetime() {
		return closetime;
	}
	public void setClosetime(String closetime) {
		this.closetime = closetime;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getCitycode() {
		return citycode;
	}
	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
	public String getPretype() {
		return pretype;
	}
	public void setPretype(String pretype) {
		this.pretype = pretype;
	}
	
	public String getPremessage() {
		return premessage;
	}
	public void setPremessage(String premessage) {
		this.premessage = premessage;
	}
	public boolean hasPretype(String type){
		return StringUtils.equals(this.pretype, type);
	}
}
