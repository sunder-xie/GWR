package com.gewara.model.sport;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import com.gewara.model.BaseObject;

public class SportItem extends BaseObject implements Comparable<SportItem> {
	private static final long serialVersionUID = -673268778354641818L;
	public static final String FLAG_RECOMMEND = "recommend";// �Ƽ�����
	public static final String FLAG_HOT = "hot";// �Ƽ�����
	private Long id;
	private String itemname;
	private Long parentid;
	private String englishname;
	private String content;
	private String remark;
	private Timestamp updatetime;
	private Integer clickedtimes;
	private Integer together; //��һ����
	private Integer playing; //������
	private Integer played; //������
	private Integer collectedtimes;//�ղ�
	private String flag;
	private String logo; //����ͼƬ
	private String seotitle; //SEO�ؼ���
	private String seodescription; //SEO����
	private Integer ordernum;
	private Integer popularIndex;  //����ָ��
	private String openType;		//Ԥ��ģʽ
	private String otherinfo;
	private String type;
	private String booking ;  //�Ƿ�Ԥ�� 
	
	public String getBooking() {
		return booking;
	}

	public void setBooking(String booking) {
		this.booking = booking;
	}

	public String getOtherinfo() {
		return otherinfo;
	}

	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}

	public Integer getPopularIndex() {
		return popularIndex;
	}

	public void setPopularIndex(Integer popularIndex) {
		this.popularIndex = popularIndex;
	}

	public Integer getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(Integer ordernum) {
		this.ordernum = ordernum;
	}

	public SportItem() {}
	
	public SportItem(String itemname){
		this.clickedtimes = 1;
		this.together = 1;
		this.playing = 1;
		this.collectedtimes = 0;
		this.itemname = itemname;
	}
	public String getSeotitle() {
		return seotitle;
	}
	public void setSeotitle(String seotitle) {
		this.seotitle = seotitle;
	}
	public String getSeodescription() {
		return seodescription;
	}
	public void setSeodescription(String seodescription) {
		this.seodescription = seodescription;
	}
	public String getLogo() {
		return logo;
	}
	public String getLimg() {
		if(StringUtils.isBlank(logo)) return "img/default_head.png";
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getEnglishname() {
		return englishname;
	}
	public void setEnglishname(String englishname) {
		this.englishname = englishname;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getParentid() {
		return parentid;
	}
	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}
	public boolean hasChild(){
		return this.parentid==null || this.parentid == 0;
	}

	public Integer getClickedtimes() {
		return clickedtimes;
	}
	public void setClickedtimes(Integer clickedtimes) {
		this.clickedtimes = clickedtimes;
	}
	/**
	 * �γ̵ļ�����Զ�����֯��,��<p>.....</p>
	 * 
	 * @param wordNumber
	 * @return ��һ���е�wordNumber����
	 */
	public String briefContent(int wordNumber){
		if(StringUtils.isBlank(content)) return null;
		String tmp = content.replaceAll("<p>", "").replaceAll("</p>", "");
		return "<p>" + StringUtils.abbreviate(tmp, wordNumber) + "</p>";
	}
	public void updateMembertype(String membertype){
		try {
			String oldvalue = BeanUtils.getProperty(this, membertype);
			Integer newValue = new Integer(oldvalue) + 1;
			BeanUtils.setProperty(this, membertype, newValue);
		} catch (IllegalAccessException e) {
			//ignore
		} catch (InvocationTargetException e) {
			//ignore
		} catch (NoSuchMethodException e) {
			//ignore
		}
	}
	public String getFullname(){
		if(StringUtils.isBlank(englishname)) return itemname;
		return itemname + "(" + englishname + ")";
	}
	public String getItemname() {
		return itemname;
	}
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}
	@Override
	public int compareTo(SportItem o) {
		return this.clickedtimes.compareTo(o.clickedtimes);
	}
	public Integer getTogether() {
		return together;
	}
	public void setTogether(Integer together) {
		this.together = together;
	}
	public Integer getPlaying() {
		return playing;
	}
	public void setPlaying(Integer playing) {
		this.playing = playing;
	}
	public String getName(){
		return itemname;
	}
	public String getUrl(){
		return "sport/item/" + this.getId();
	}
	public String getFirstpic(){
		return this.logo;
	}
 
	public Integer getPlayed() {
		return played;
	}
	public void setPlayed(Integer played) {
		this.played = played;
	}
	public Timestamp getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public Integer getCollectedtimes() {
		return collectedtimes;
	}
	public void setCollectedtimes(Integer collectedtimes) {
		this.collectedtimes = collectedtimes;
	}
	public void addCollectedtimes(){
		this.collectedtimes+=1;
	}
	public static String getFLAG_RECOMMEND() {
		return FLAG_RECOMMEND;
	}
	public static String getFLAG_HOT() {
		return FLAG_HOT;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}
	public String getRealBriefname(){
		return this.itemname;
	}
}
