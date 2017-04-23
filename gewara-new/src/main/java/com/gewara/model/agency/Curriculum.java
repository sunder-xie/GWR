package com.gewara.model.agency;

import java.io.Serializable;
import java.util.Date;

import com.gewara.model.BaseObject;


/**
 * @author wkxyl9
 * @since 2013-03-14 16:20:00
 */
public class Curriculum extends BaseObject{
	
	private static final long serialVersionUID = -384495826383244326L;
	private Long id;
	private Long relatedid;					//�����γ�				
	private Date fromdate;					//��ʼ����
	private Date todate;						//��������
	private String classtime;				//�Ͽ�ʱ��
	private String categoryids;			//������ʦID����,������
	private String title;					
	private String remark;
	private String cycletype;				//ѭ�����ͣ��ܣ����ڣ�ÿ�죩
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
	public Date getFromdate() {
		return fromdate;
	}
	public void setFromdate(Date fromdate) {
		this.fromdate = fromdate;
	}
	public Date getTodate() {
		return todate;
	}
	public void setTodate(Date todate) {
		this.todate = todate;
	}
	public String getClasstime() {
		return classtime;
	}
	public void setClasstime(String classtime) {
		this.classtime = classtime;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getRelatedid() {
		return relatedid;
	}
	public void setRelatedid(Long relatedid) {
		this.relatedid = relatedid;
	}
	public String getCategoryids() {
		return categoryids;
	}
	public void setCategoryids(String categoryids) {
		this.categoryids = categoryids;
	}
	public String getCycletype() {
		return cycletype;
	}
	public void setCycletype(String cycletype) {
		this.cycletype = cycletype;
	}
	
}
