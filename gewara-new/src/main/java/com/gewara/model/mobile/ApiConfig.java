package com.gewara.model.mobile;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;

public class ApiConfig extends BaseObject{
	private static final long serialVersionUID = 174545364169043717L;
	public static final Long API2_PARTNERS = 1L;		//֧��api2�ӿڵ����к����̵�id
	public static final Long API2_GEWAMEMBER_PARTNERS = 2L; //֧��api2�ӿڵ����и������û��̻�id
	public static final Long API2_MOBILE_PARTNERS_POINT_MOVIE = 3L; //��Ӱ--api2�ӿ�֧�ֻ���֧��
	public static final Long API2_MOBILE_PARTNERS_POINT_SPORT = 4L; //�˶�--api2�ӿ�֧�ֻ���֧��
	private Long id;
	private String content;
	private Timestamp addtime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Timestamp getAddtime() {
		return addtime;
	}
	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
