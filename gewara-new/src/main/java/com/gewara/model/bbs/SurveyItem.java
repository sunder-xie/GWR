package com.gewara.model.bbs;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;

public class SurveyItem extends BaseObject {
	private static final long serialVersionUID = 7949823691095330472L;
	
	public static final String SINGLETYPE="single"; 
	public static final String MULTIPLETYPE="multiple"; 
	public static final String TEXTTYPE="text"; 
	private Long id;
	private Long surveyid;			//�����ʾ�Ϊsurvey���е�recordid
	private Integer itemid;			//�ʾ���Ŀ��ţ�����Ϊ�����׼
	private String status;			//��Ŀ״̬���Ƿ����Σ���ѡ'N','Y'
	private String itemtype;		//��Ŀ���ͣ�'single'Ϊ��ѡ,'multiple'Ϊ��ѡ,'text'Ϊ�ı�����ѡ�Ͷ�ѡ���׷���ı�
	private Timestamp addtime;		//����ʱ��
	private Timestamp updatetime;	//�޸�ʱ��
	private String body;				//��Ŀ����

	public SurveyItem() {}
	
	public SurveyItem(Long surveyid, String body) {
		this.surveyid = surveyid;
		this.itemid = 1;
		this.itemtype = "text";
		this.status = "Y";
		this.addtime = DateUtil.getCurFullTimestamp();
		this.updatetime = DateUtil.getCurFullTimestamp();
		this.body = body;
	}
	public SurveyItem(Long surveyid, String body, String itemtype) {
		this.surveyid = surveyid;
		this.itemid = 1;
		this.itemtype = itemtype;
		this.status = "Y";
		this.addtime = DateUtil.getCurFullTimestamp();
		this.updatetime = DateUtil.getCurFullTimestamp();
		this.body = body;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	
	public Long getSurveyid() {
		return surveyid;
	}

	public void setSurveyid(Long surveyid) {
		this.surveyid = surveyid;
	}

	public Integer getItemid() {
		return itemid;
	}

	public void setItemid(Integer itemid) {
		this.itemid = itemid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public Timestamp getAddtime() {
		return addtime;
	}

	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}

	public Timestamp getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
