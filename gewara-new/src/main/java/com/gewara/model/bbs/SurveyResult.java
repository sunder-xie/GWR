package com.gewara.model.bbs;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;

public class SurveyResult extends BaseObject {
	private static final long serialVersionUID = -4102497864896980635L;
	private Long id;
	private Long surveyid;		//�ʾ�id��Ϊsurvey���е�recordid
	private Integer itemid;		//����id��Ϊsurvey_item���е�itemid
	private Integer optionid;	//ѡ��id��Ϊsurvey_option���е�optionid
	private Long memberid;		//�����ʾ������û�id
	private Timestamp addtime;	//����ʱ��
	private String flag;			//ѡ��ѡ��״̬��'Y'Ϊѡ����'N'Ϊδѡ��
	private String mark;			//ѡ��׷���ı�
	private String otherinfo;	//������Ϣ
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

	public Integer getOptionid() {
		return optionid;
	}

	public void setOptionid(Integer optionid) {
		this.optionid = optionid;
	}

	public Long getMemberid() {
		return memberid;
	}

	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}

	public Timestamp getAddtime() {
		return addtime;
	}

	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getOtherinfo() {
		return otherinfo;
	}

	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
}
