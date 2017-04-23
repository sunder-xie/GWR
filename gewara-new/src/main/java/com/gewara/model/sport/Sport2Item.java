package com.gewara.model.sport;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;

public class Sport2Item  extends BaseObject{
	private static final long serialVersionUID = 4922446985511575269L;
	public static final String N = "N";						//��֧����Ʊ�ͻ�Ʊ
	public static final String Y_REFUND = "Y_REFUND";		//֧����Ʊ
	public static final String Y_EXCHANGE = "Y_EXCHANGE";	//֧�ֻ�Ʊ
	public static final String Y = "Y";						//֧����Ʊ�ͻ�Ʊ
	public static final String RANGE = "range";				//Զ��ֱ��ͬ��
	public static final String GEWA = "gewa";				//����������
	
	public static final String OPEN_STATUS = "openStatus";	//ͬ��ʱ�����Ƿ񿪷�
	public static final String OPEN_BEFORE = "openBefore";	//�Զ����ų���ʱ����ǰ�����쿪��
	
	private Long id;
	private Long sportid;
	private Long itemid;
	private Integer minprice;
	private Integer maxprice;
	private Integer avgprice;
	private String takemethod;
	private String notifymsg1;	
	private String notifymsg2;
	private String notifymsg3;
	private String overmsg;
	private String booking;
	private Integer sortnum;
	private Integer limitminutes;   //ǰ̨����ʱ�䣺���Ϊ60���ӣ�����ʱ�����60���ӣ����ڵĶ�������Ԥ��
	private Long diaryid;			//����ID
	private String tickettype; 		//ȡƱ����
	private String exitsreturn;    	//�Ƿ�֧����Ʊ
	private Integer returnminutes; 	//��̨��Ʊ��ʱ������
	private String returnmoneytype; //�۳������ѵ�����
	private Double returnmoney;		//Ǯ��
	private Integer returnsmallmoney;//�ٷֱȵ�ʱ��Ǯ������Сֵ
	
	private String exitschange;		//�Ƿ�֧�ֻ�Ʊ
	private Integer changeminutes; 	//��Ʊ��ʱ������
	private String changemoneytype;	//�۳������ѵ�����
	private Double changemoney;		//Ǯ��
	private Integer changesmallmoney;//�ٷֱȵ�ʱ��Ǯ������Сֵ
	private String otherinfo;
	private String description;		//��������
	private String sporttype;		//��������
	private String opentype;		//Ԥ����ʽ
	private String createtype;		//�������ɷ�ʽ
	private Integer cycle;			//��������
	private String opentime;		//����ʱ��
	private String closetime;		//�ر�ʱ��
	private String prompting;    //����˵��

	public Sport2Item(){}
	public Sport2Item(Long sportid, Long itemid){
		this.sportid = sportid;
		this.itemid = itemid;
		this.minprice = 5;
		this.maxprice = 5;
		this.avgprice = 5;
		this.otherinfo = "{}";
		this.booking = SportProfile.STATUS_OPEN;
		this.sortnum = 0;
		this.limitminutes = 0;
		this.returnminutes = 0;
		this.returnmoneytype = "A";
		this.returnmoney = 0.0;
		this.changeminutes = 0;
		this.changemoneytype = "A";
		this.changemoney = 0.0;
		this.tickettype = "A";
		this.returnsmallmoney = 0;
		this.changesmallmoney = 0;
	}
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
	public Long getSportid() {
		return sportid;
	}
	public void setSportid(Long sportid) {
		this.sportid = sportid;
	}
	public Long getItemid() {
		return itemid;
	}
	public void setItemid(Long itemid) {
		this.itemid = itemid;
	}
	public String getTakemethod() {
		return takemethod;
	}
	public Integer getMinprice() {
		return minprice;
	}
	public void setMinprice(Integer minprice) {
		this.minprice = minprice;
	}
	public Integer getMaxprice() {
		return maxprice;
	}
	public void setMaxprice(Integer maxprice) {
		this.maxprice = maxprice;
	}
	public Integer getAvgprice() {
		return avgprice;
	}
	public void setAvgprice(Integer avgprice) {
		this.avgprice = avgprice;
	}
	public void setTakemethod(String takemethod) {
		this.takemethod = takemethod;
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
	public String getNotifymsg3() {
		return notifymsg3;
	}
	public void setNotifymsg3(String notifymsg3) {
		this.notifymsg3 = notifymsg3;
	}
	
	public String getOvermsg() {
		return overmsg;
	}
	public void setOvermsg(String overmsg) {
		this.overmsg = overmsg;
	}
	public String getBooking() {
		return booking;
	}
	public void setBooking(String booking) {
		this.booking = booking;
	}
	public Integer getSortnum() {
		return sortnum;
	}
	public void setSortnum(Integer sortnum) {
		this.sortnum = sortnum;
	}
	public Integer getLimitminutes() {
		return limitminutes;
	}
	public void setLimitminutes(Integer limitminutes) {
		this.limitminutes = limitminutes;
	}
	public Long getDiaryid() {
		return diaryid;
	}
	public void setDiaryid(Long diaryid) {
		this.diaryid = diaryid;
	}
	public String getTickettype() {
		return tickettype;
	}
	public void setTickettype(String tickettype) {
		this.tickettype = tickettype;
	}
	public String getExitsreturn() {
		return exitsreturn;
	}
	public void setExitsreturn(String exitsreturn) {
		this.exitsreturn = exitsreturn;
	}
	public Integer getReturnminutes() {
		return returnminutes;
	}
	public void setReturnminutes(Integer returnminutes) {
		this.returnminutes = returnminutes;
	}
	public String getReturnmoneytype() {
		return returnmoneytype;
	}
	public void setReturnmoneytype(String returnmoneytype) {
		this.returnmoneytype = returnmoneytype;
	}
	public Double getReturnmoney() {
		return returnmoney;
	}
	public void setReturnmoney(Double returnmoney) {
		this.returnmoney = returnmoney;
	}
	public Integer getReturnsmallmoney() {
		return returnsmallmoney;
	}
	public void setReturnsmallmoney(Integer returnsmallmoney) {
		this.returnsmallmoney = returnsmallmoney;
	}
	public String getExitschange() {
		return exitschange;
	}
	public void setExitschange(String exitschange) {
		this.exitschange = exitschange;
	}
	public Integer getChangeminutes() {
		return changeminutes;
	}
	public void setChangeminutes(Integer changeminutes) {
		this.changeminutes = changeminutes;
	}
	public String getChangemoneytype() {
		return changemoneytype;
	}
	public void setChangemoneytype(String changemoneytype) {
		this.changemoneytype = changemoneytype;
	}
	public Double getChangemoney() {
		return changemoney;
	}
	public void setChangemoney(Double changemoney) {
		this.changemoney = changemoney;
	}
	public Integer getChangesmallmoney() {
		return changesmallmoney;
	}
	public void setChangesmallmoney(Integer changesmallmoney) {
		this.changesmallmoney = changesmallmoney;
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
	
	public String getTicketContent(String type){
		if("B".equals(type)) return "��ƾ��ȡƱ���ž����ݹ���ԱЭ��ȡƱ���볡";
		if("C".equals(type)) return "��ƾ��ȡƱ����������ȡƱ����ȡƱ";
		if("D".equals(type)) return "��ƾ��ȡƱ���ž����ݹ���Ա��֤���볡";
		if("E".equals(type)) return "��ƾ��ȡƱ�������ֳ��ĸ�����������Ա����ȡ�볡Ʊ";
		return "";
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSporttype() {
		return sporttype;
	}
	public void setSporttype(String sporttype) {
		this.sporttype = sporttype;
	}
	public String getOpentype() {
		return opentype;
	}
	public void setOpentype(String opentype) {
		this.opentype = opentype;
	}
	public String getCreatetype() {
		return createtype;
	}
	public void setCreatetype(String createtype) {
		this.createtype = createtype;
	}
	public Integer getCycle() {
		return cycle;
	}
	public void setCycle(Integer cycle) {
		this.cycle = cycle;
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
	public String getPrompting() {
		return prompting;
	}
	public void setPrompting(String prompting) {
		this.prompting = prompting;
	}
	public boolean isOpen(){
		Timestamp curTime = DateUtil.getCurFullTimestamp();
		String curtime = DateUtil.format(DateUtil.getCurFullTimestamp(), "yyyy-MM-dd");
		if(StringUtils.isBlank(this.opentime) && StringUtils.isBlank(this.closetime)) return true;
		boolean checkOpen = false;
		boolean checkClose = false;
		if(StringUtils.isNotBlank(this.opentime)){
			Timestamp openTime = DateUtil.parseTimestamp(curtime + " " + StringUtils.substring(this.opentime, 0, 2) + ":" + StringUtils.substring(this.opentime, 2, 4) + ":00");
			if(openTime != null) checkOpen = openTime.before(curTime);
		}else{
			checkOpen = true;
		}
		if(StringUtils.isNotBlank(this.closetime)){
			Timestamp closeTime = DateUtil.parseTimestamp(curtime + " " + StringUtils.substring(this.closetime, 0, 2) + ":" + StringUtils.substring(this.closetime, 2, 4) + ":00");
			if(closeTime != null) checkClose = closeTime.after(curTime);
		}else{
			checkClose = true;
		}
		return checkOpen && checkClose;
	}
}
