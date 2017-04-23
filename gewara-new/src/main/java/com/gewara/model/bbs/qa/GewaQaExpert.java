package com.gewara.model.bbs.qa;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.gewara.model.BaseObject;
public class GewaQaExpert extends BaseObject {
	private static final long serialVersionUID = -2898820814440297249L;
	public static Map<String,String> ssMap = new HashMap<String,String>();
	public static final int HOTVALUE_HOT = 30000; // ����
	public static final int HOTVALUE_RECOMMEND = 50000; // �Ƽ�
	public static String STATUS_N = "N";
	public static String STATUS_Y = "Y";
	private Long id;
	private Long memberid; //������
	private String status; //�Ƿ�ͨ�����
	private Integer hotvalue; //�Ƽ�ֵ
	private Long userid; //����Ա
	private String tag;
	private String reason; //��������
	private Timestamp addtime; 
	private Timestamp updatetime;
	@Override
	public Serializable realId() {
		return id;
	}
	
	public GewaQaExpert(){}
	
	public GewaQaExpert(Long memberid){
		this.memberid = memberid;
		this.status = STATUS_N;
		this.hotvalue = 0;
		this.addtime = new Timestamp(System.currentTimeMillis());
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getHotvalue() {
		return hotvalue;
	}
	public void setHotvalue(Integer hotvalue) {
		this.hotvalue = hotvalue;
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
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTagname(){
		return ssMap.get(this.tag);
	}
	static{
		ssMap.put("cinema", "��Ӱ");
		ssMap.put("gym", "����");
		ssMap.put("bar", "�ư�");
		ssMap.put("sport", "�˶�");
		ssMap.put("ktv", "KTV");
		ssMap.put("", "����");
	}
}
