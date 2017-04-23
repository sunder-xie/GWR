package com.gewara.model.drama;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.common.BaseEntity;

public class DramaStar extends BaseEntity {
	private static final long serialVersionUID = -505984720950483214L;
	public static final String TYPE_STAR = "star";//��Ա
	public static final String TYPE_TROUPE = "troupe";//����
	public static final String TYPE_DIRECTOR="director";//����
	public static final String TAG_DRAMASTAR = "dramastar";	// ҳ��tagʹ��
	private Date birthday;
	private String tag;
	private String state;
	private String bloodtype;
	private String constellation;
	private String height;
	private String hometown;
	private String graduated;
	private String job;
	private String website;
	private Long troupe;	// ��������
	private Date establishtime;	// ��������(����ʱ��)
	private String startype;		// ����(��Ա/����)
	private String representative;			// <!-- ������������ -->
	private String representativeRelate;	//<!-- �������������� json��ʽ -->
	private Integer starnum;		//��Ա����
	private Integer worknum;		//��Ʒ����
	
	public DramaStar(){}
	
	public DramaStar(String name) {
		this.name = name;
		this.troupe = 0L;	
		this.generalmark = 21;
		this.generalmarkedtimes = 3;
		this.quguo = 1;
		this.xiangqu = 1;
		this.clickedtimes = 20;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.collectedtimes = 1;
		this.starnum = 0;
		this.worknum = 0;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getBloodtype() {
		return bloodtype;
	}
	public void setBloodtype(String bloodtype) {
		this.bloodtype = bloodtype;
	}
	public String getConstellation() {
		return constellation;
	}
	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getGraduated() {
		return graduated;
	}
	public void setGraduated(String graduated) {
		this.graduated = graduated;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public Long getTroupe() {
		return troupe;
	}
	public void setTroupe(Long troupe) {
		this.troupe = troupe;
	}
	public String getHometown() {
		return hometown;
	}
	public void setHometown(String hometown) {
		this.hometown = hometown;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public Date getEstablishtime() {
		return establishtime;
	}
	public void setEstablishtime(Date establishtime) {
		this.establishtime = establishtime;
	}
	public String getStartype() {
		return startype;
	}
	public void setStartype(String startype) {
		this.startype = startype;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	
	public Integer getStarnum() {
		return starnum;
	}

	public void setStarnum(Integer starnum) {
		this.starnum = starnum;
	}

	public Integer getWorknum() {
		return worknum;
	}

	public void setWorknum(Integer worknum) {
		this.worknum = worknum;
	}

	public String getLimg(){
		if(StringUtils.isBlank(logo)) return "img/default_pic.png";
		return logo;
	}

	public String getRepresentative() {
		return representative;
	}

	public void setRepresentative(String representative) {
		this.representative = representative;
	}

	public String getRepresentativeRelate() {
		return representativeRelate;
	}

	public void setRepresentativeRelate(String representativeRelate) {
		this.representativeRelate = representativeRelate;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
