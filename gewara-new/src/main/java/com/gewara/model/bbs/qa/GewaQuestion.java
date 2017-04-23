package com.gewara.model.bbs.qa;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.gewara.constant.Status;
import com.gewara.model.BaseObject;

public class GewaQuestion extends BaseObject {
	private static final long serialVersionUID = 5578727148166770087L;
	public static Map<String,String> ssMap = new HashMap<String,String>();
	public static final int HOTVALUE_HOT = 30000; // ����
	public static final int HOTVALUE_RECOMMEND = 50000; // �Ƽ�
	public static final String QS_STATUS_N = "N"; //�����
	public static final String QS_STATUS_Y = "Y"; //�ѽ��
	public static final String QS_STATUS_Z = "Z"; //����
	public static final String QS_STATUS_NOPROPER = "noproper"; //�������
	public static final Integer MAXDAYS = 15; //������ʱ��
	private Long id;
	private String title; // ����
	private String content; // ����
	private String addinfo; //����
	private Integer reward; // ���ͷ�
	private String tag; // ��飺movie,gym,....
	private String category;
	private Long categoryid;
	private Long relatedid;
	private Long memberid; // ������
	private Integer replycount; // ���ظ�����
	private Long replymemberid; // ���ظ���
	private Integer clickedtimes;
	private Integer hotvalue;
	private String questionstatus; // ���״̬ �����:N, �ѽ����Y, ������Z  ������� noproper
	private String status;// ɾ��״̬ δɾ����N ��ɾ����Y
	private Timestamp addtime;
	private Timestamp updatetime;
	private Timestamp modtime;	//����޸�ʱ��
	private Timestamp addinfotime;
	private Timestamp recommendtime;
	private Timestamp dealtime;
	private Long tomemberid; // ��Ta����
	private String countycode;
	private String membername;
	private String citycode;
	private String ip; //������IP
	private BaseObject relate;
	private BaseObject relate2;
	
	public String getMembername() {
		return membername;
	}

	public void setMembername(String membername) {
		this.membername = membername;
	}

	public String getCountycode() {
		return countycode;
	}

	public void setCountycode(String countycode) {
		this.countycode = countycode;
	}
	public GewaQuestion(){}
	
	public GewaQuestion(Long memberid) {
		this.memberid = memberid;
		this.hotvalue = 0;
		this.replycount = 0;
		this.clickedtimes = 0;
		this.questionstatus = QS_STATUS_Z;
		this.status = Status.Y_NEW;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.updatetime = addtime;
		this.recommendtime = addtime;
		this.modtime = addtime;
		this.reward = 0;
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Integer getReplycount() {
		return replycount;
	}

	public void setReplycount(Integer replycount) {
		this.replycount = replycount;
	}

	public Long getMemberid() {
		return memberid;
	}

	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}

	public Long getReplymemberid() {
		return replymemberid;
	}

	public void setReplymemberid(Long replymemberid) {
		this.replymemberid = replymemberid;
	}

	public Long getTomemberid() {
		return tomemberid;
	}

	public void setTomemberid(Long tomemberid) {
		this.tomemberid = tomemberid;
	}

	public Integer getClickedtimes() {
		return clickedtimes;
	}

	public void setClickedtimes(Integer clickedtimes) {
		this.clickedtimes = clickedtimes;
	}

	public Integer getHotvalue() {
		return hotvalue;
	}

	public void setHotvalue(Integer hotvalue) {
		this.hotvalue = hotvalue;
	}

	public String getQuestionstatus() {
		return questionstatus;
	}

	public void setQuestionstatus(String questionstatus) {
		this.questionstatus = questionstatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Long getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(Long categoryid) {
		this.categoryid = categoryid;
	}

	public Long getRelatedid() {
		return relatedid;
	}

	public void setRelatedid(Long relatedid) {
		this.relatedid = relatedid;
	}

	public Integer getReward() {
		return reward;
	}

	public void setReward(Integer reward) {
		this.reward = reward;
	}

	public void addReplycount() {
		this.replycount++;
	}

	public Timestamp getRecommendtime() {
		return recommendtime;
	}

	public void setRecommendtime(Timestamp recommendtime) {
		this.recommendtime = recommendtime;
	}
	public String getTagname(){
		return ssMap.get(this.tag);
	}
	static{
		ssMap.put("cinema", "����Ӱ");
		ssMap.put("gym", "ȥ����");
		ssMap.put("bar", "�ݾư�");
		ssMap.put("sport", "���˶�");
		ssMap.put("ktv", "KTV");
		ssMap.put("theatre", "����");
		ssMap.put("activity", "�");
		ssMap.put("gymcard", "����");
		ssMap.put("gymcurriculum", "����γ�");
		ssMap.put("", "����");
	}
	public String getAddinfo() {
		return addinfo;
	}

	public void setAddinfo(String addinfo) {
		this.addinfo = addinfo;
	}
	
	public Timestamp getAddinfotime() {
		return addinfotime;
	}

	public void setAddinfotime(Timestamp addinfotime) {
		this.addinfotime = addinfotime;
	}

	public Timestamp getDealtime() {
		return dealtime;
	}

	public void setDealtime(Timestamp dealtime) {
		this.dealtime = dealtime;
	}
	public Timestamp getModtime() {
		return modtime;
	}
	public void setModtime(Timestamp modtime) {
		this.modtime = modtime;
	}
	public String getCname() {
		return this.title;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	public String getIp(){
		return ip;
	}
	public void setIp(String ip){
		this.ip = ip;
	}
	
	public BaseObject getRelate() {
		return relate;
	}

	public void setRelate(BaseObject relate) {
		this.relate = relate;
	}

	public BaseObject getRelate2() {
		return relate2;
	}

	public void setRelate2(BaseObject relate2) {
		this.relate2 = relate2;
	}
}
