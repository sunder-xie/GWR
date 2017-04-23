package com.gewara.model.movie;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.Status;
import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;

/**
 * @author acerge(acerge@163.com)
 * @since 3:05:09 PM Jan 15, 2010
 */
public class CinemaProfile extends BaseObject{
	private static final long serialVersionUID = -3804714651086763962L;
	public static final String STATUS_OPEN = "open";
	public static final String STATUS_CLOSE = "close";
	public static final String POPCORN_STATUS_Y ="Y";//�б��׻�
	public static final String POPCORN_STATUS_N ="N";//û�б��׻�
	public static final String SERVICEFEE_Y ="Y";//�з����
	public static final String SERVICEFEE_N ="N";//û�з����
	
	public static final String TAKEMETHOD_P = "P";//�ֳ�����
	public static final String TAKEMETHOD_W = "W";//ӰԺ��Ʊ����
	public static final String TAKEMETHOD_A = "A";//������ȡƱ��
	public static final String TAKEMETHOD_U = "U";//����Ժ��
	public static final String TAKEMETHOD_L = "L";//¬�װ�ӰԺ����ȡƱ��
	public static final String TAKEMETHOD_D = "D";//���Ժ������ȡƱ��
	public static final String TAKEMETHOD_J = "J";//����Ժ������ȡƱ��
	public static final String TAKEMETHOD_M = "M";//ӰԺ��Ա����ȡƱ��
	
	private Long id;				//��Cinema����ID
	private String notifymsg1;		//ȡƱ����
	private String notifymsg2;		//��ǰ3Сʱ���Ѷ���
	private String takemethod; 		//ȡƱ��ʽ: P���ֳ����ͣ�W��ӰԺ��Ʊ���ڣ�A���Զ�ȡƱ��
	private Long topicid;			//ȡƱ����
	private String takeAddress;     //ȡƱλ��
	private String opentime;		//ÿ�쿪�Ź�Ʊʱ�䣬��6:00����д 0600
	private String closetime;		//ÿ��رչ�Ʊʱ��
	private String startsale;		//ÿ�����ײ͵Ŀ�ʼʱ�� 0800
	private String endsale;			//ÿ�����ײ͵Ľ���ʱ�� 2300
	private String popcorn;      	//�Ƿ��Ǻ��б��׻�ӰԺ
	private String servicefee;		//�����
	private String status;			//����״̬
	private Integer cminute;		//��ǰ�೤ʱ��ر�(����)
	private Integer openDay;         //��ǰ����ʱ��(���磺1 ��ʾ1��)
	private String openDayTime;		 //��ǰ���ż���ľ���ʱ��(���磺6:00����д 0600)
	private Integer fee;			//�����
	private String isRefund;		//�Ƿ������ƱY or N
	private String opentype;		//ӰԺ�������ͣ�HFH, MTX, DX
	private String direct;			//�Ƿ�ֱ��Y or N
	private String prompting;		//��ʾ˵��

	@Override
	public Serializable realId() {
		return id;
	}
	public Integer getFee() {
		return fee;
	}
	public void setFee(Integer fee) {
		this.fee = fee;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public CinemaProfile(){
	}
	public CinemaProfile(Long cinemaid){
		this();
		this.opentime = "0000";
		this.closetime = "2400";
		this.cminute = 60;
		this.id = cinemaid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNotifymsg1() {
		return notifymsg1;
	}
	public void setNotifymsg1(String notifymsg1) {
		this.notifymsg1 = notifymsg1;
	}
	public String getNotifymsg2() {
		return notifymsg2;
	}
	public void setNotifymsg2(String notifymsg2) {
		this.notifymsg2 = notifymsg2;
	}
	public Long getTopicid() {
		return topicid;
	}
	public void setTopicid(Long topicid) {
		this.topicid = topicid;
	}
	public String getTakemethod() {
		return takemethod;
	}
	public void setTakemethod(String takemethod) {
		this.takemethod = takemethod;
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
	public String getStartsale() {
		return startsale;
	}
	public void setStartsale(String startsale) {
		this.startsale = startsale;
	}
	public String getEndsale() {
		return endsale;
	}
	public void setEndsale(String endsale) {
		this.endsale = endsale;
	}
	public String getTakeAddress() {
		return takeAddress;
	}
	public void setTakeAddress(String takeAddress) {
		this.takeAddress = takeAddress;
	}
	public boolean isBuyItem(Timestamp playtime){
		if(StringUtils.isNotBlank(startsale) && StringUtils.isNotBlank(endsale)){
			String time = DateUtil.format(playtime, "HHmm");
			if(time.compareTo(startsale)<0 || time.compareTo(endsale)>0) return false;
		}
		return true;
	}
	public String getPopcorn() {
		return popcorn;
	}
	public void setPopcorn(String popcorn) {
		this.popcorn = popcorn;
	}
	public String getTakeInfo(){
		String result = "��λ��ӰԺ�ĸ���������ȡƱ��ȡƱ";
		if(StringUtils.equals(takemethod, TAKEMETHOD_U)){
			result = "��λ��ӰԺ������Ժ������ȡƱ��ȡƱ";
		}else if(StringUtils.equals(takemethod, TAKEMETHOD_W)){
			result = "ӰԺ��Ʊ����ȡƱ";
		}else if(StringUtils.equals(takemethod, TAKEMETHOD_P)){
			result = "�ֳ�����";
		}else if(StringUtils.equals(takemethod, TAKEMETHOD_L)){
			result = "��λ��ӰԺ��¬�װ�ӰԺ����ȡƱ��";
		}else if(StringUtils.equals(takemethod, TAKEMETHOD_D)){
			result = "��λ��ӰԺ�����Ժ������ȡƱ��";
		}else if(StringUtils.equals(takemethod, TAKEMETHOD_J)){
			result = "��λ��ӰԺ�Ľ���Ժ������ȡƱ��";
		}else if(StringUtils.equals(takemethod, TAKEMETHOD_M)){
			result = "��λ��ӰԺ��ӰԺ��Ա����ȡƱ��";
		}
		return result;
	}
	public String getServicefee() {
		return servicefee;
	}
	public void setServicefee(String servicefee) {
		this.servicefee = servicefee;
	}
	public Integer getCminute() {
		return cminute;
	}
	public void setCminute(Integer cminute) {
		this.cminute = cminute;
	}
	public String getIsRefund() {
		return isRefund;
	}
	public void setIsRefund(String isRefund) {
		this.isRefund = isRefund;
	}
	
	public String getDirect() {
		return direct;
	}
	public void setDirect(String direct) {
		this.direct = direct;
	}
	public String getPrompting() {
		return prompting;
	}
	public void setPrompting(String prompting) {
		this.prompting = prompting;
	}
	public boolean hasDirect(){
		return StringUtils.equals(this.direct, Status.Y);
	}
	public String getOpentype() {
		return opentype;
	}
	public void setOpentype(String opentype) {
		this.opentype = opentype;
	}
	public Integer getOpenDay() {
		return openDay;
	}
	public void setOpenDay(Integer openDay) {
		this.openDay = openDay;
	}
	public String getOpenDayTime() {
		return openDayTime;
	}
	public void setOpenDayTime(String openDayTime) {
		this.openDayTime = openDayTime;
	}
	
	public boolean hasDefinePaper(){//ȡƱ��ʽ�Ǹ�����ȡƱ���ҹ�Ʊ����
		return StringUtils.isNotBlank(this.takemethod) && CinemaProfile.TAKEMETHOD_A.equals(this.takemethod) && CinemaProfile.STATUS_OPEN.equals(this.status);
	}
}
