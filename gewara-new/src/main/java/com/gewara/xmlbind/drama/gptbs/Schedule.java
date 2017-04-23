package com.gewara.xmlbind.drama.gptbs;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.Status;
import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;
//����
public class Schedule extends BaseObject{
	private static final long serialVersionUID = -4228181861726824560L;
	private Long id;						//ID
	private String code;					//���α��
	private String cnName;					//����������
	private String enName;					//����Ӣ����
	private String available;				//�Ƿ���Ч
	private String choseOnline;				//�Ƿ�֧������ѡ��
	private Timestamp playTime;				//����ʱ��
	private Timestamp integerernalTime;		//��Ʊʱ��
	private Timestamp integerernalEndTime;	//����ʱ��
	private Long programId;					//�ݳ���Ŀ���
	private Long venueId;					//����ID
	private Integer logistics;					//��ݷ�ʽ
	private String fixed;					//�Ƿ�̶�ʱ��
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
	public String getIdString(){
		return String.valueOf(id);
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public String getAvailable() {
		return available;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	public String getChoseOnline() {
		return choseOnline;
	}
	public void setChoseOnline(String choseOnline) {
		this.choseOnline = choseOnline;
	}
	public Timestamp getPlayTime() {
		return playTime;
	}
	public void setPlayTime(Timestamp playTime) {
		this.playTime = playTime;
	}
	
	public String getPlaydate(){
		return DateUtil.format(playTime, "yyyy-MM-dd");
	}
	
	public Timestamp getIntegerernalTime() {
		return integerernalTime;
	}
	public void setIntegerernalTime(Timestamp integerernalTime) {
		this.integerernalTime = integerernalTime;
	}
	public Timestamp getIntegerernalEndTime() {
		return integerernalEndTime;
	}
	public void setIntegerernalEndTime(Timestamp integerernalEndTime) {
		this.integerernalEndTime = integerernalEndTime;
	}
	public Long getProgramId() {
		return programId;
	}
	public void setProgramId(Long programId) {
		this.programId = programId;
	}
	public Long getVenueId() {
		return venueId;
	}
	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}
	
	public Integer getLogistics() {
		return logistics;
	}
	public void setLogistics(Integer logistics) {
		this.logistics = logistics;
	}
	public String getFixed() {
		return fixed;
	}
	public void setFixed(String fixed) {
		this.fixed = fixed;
	}
	public boolean hasAvailable(){
		return StringUtils.equals(this.available, Status.Y);
	}
}
