package com.gewara.model.sport;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.OpenTimeTableConstant;
import com.gewara.constant.Status;
import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;
public class OpenTimeTable extends BaseObject {
	private static final long serialVersionUID = 3570412474021382971L;

	private Long id;
	private Long sportid; 					// ����ID
	private Long itemid; 					// ��ĿID
	private String sportname; 				// ������
	private String itemname; 				// ��Ŀ��
	private Date playdate; 					// ����
	private Integer price; 					// �۸�
	private String status; 					// ״̬
	private Integer costprice; 				// �ɱ���
	private Integer gewaprice; 				// ����
	private Integer sportprice;				// ���ݼ�
	private Timestamp opentime; 			// ����Ԥ��ʱ��
	private Timestamp closetime; 			// �ر�Ԥ��ʱ��
	private String starttime; 				// ��ʼʱ��
	private String endtime; 				// ����ʱ��
	
	private Integer minpoint;				//	ʹ�û�������
	private Integer maxpoint;				//	ʹ�û�������
	
	private String elecard;					//	�Ż�ȯ
	private String remark;					//	����
	private String otherinfo;				//	������Ϣ
	
	private Long remoteid;					//	Զ�̳���ID
	private String rstatus;					//	Զ��״̬
	private String ver;						//	�汾���ϰ�1.0, �°�2.0��
	private String spflag;					//	������ʶ
	private String tkey;					//	��¼��ʶ sportid-itemid-playdate
	private String openType;				//	Ԥ��ģʽ
	private Integer unitMinute;				// 	Ԥ����λ����
	private Integer week;					//	����
	
	private Integer quantity = 0;			//	һ�����ؿ�����		
	private Integer sales = 0;				//	�۳�������
	private Integer remain = 0;				//	ʣ���Ԥ������
	private String citycode;				//	���б���
	
	public OpenTimeTable(){}
	
	public OpenTimeTable(ProgramItemTime pit, Date playdate){
		this.itemid = pit.getItemid();
		this.sportid = pit.getSportid();
		this.playdate = playdate;
		this.status = OpenTimeTableConstant.STATUS_NOBOOK;
		this.starttime = pit.getStarttime();
		this.endtime = pit.getEndtime();
		this.price = pit.getPrice();
		this.costprice = pit.getCostprice();
		this.sportprice = pit.getSportprice();
		this.rstatus = Status.Y;
		this.quantity = 0;
		this.remain = 0;
		this.ver = OpenTimeTableConstant.VERSION_V2;
		
		this.tkey = this.sportid + "-" + this.itemid + "-" + DateUtil.format(this.playdate, "yyyyMMdd")+ "-" + this.starttime;
		
		this.minpoint = 500;
		this.maxpoint = 10000;

		this.elecard = "ABDM";
		this.sales = 0;
		this.week = DateUtil.getWeek(this.playdate);
		this.openType = pit.getOpenType();
		this.unitMinute = pit.getUnitMinute();
		this.citycode = pit.getCitycode();
	}
	
	public Long getRemoteid() {
		return remoteid;
	}
	
	public void setRemoteid(Long remoteid) {
		this.remoteid = remoteid;
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

	public Date getPlaydate() {
		return playdate;
	}

	public void setPlaydate(Date playdate) {
		this.playdate = playdate;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getCostprice() {
		return costprice;
	}

	public void setCostprice(Integer costprice) {
		this.costprice = costprice;
	}

	public Integer getGewaprice() {
		return gewaprice;
	}

	public void setGewaprice(Integer gewaprice) {
		this.gewaprice = gewaprice;
	}

	public Timestamp getOpentime() {
		return opentime;
	}

	public void setOpentime(Timestamp opentime) {
		this.opentime = opentime;
	}

	public Timestamp getClosetime() {
		return closetime;
	}

	public void setClosetime(Timestamp closetime) {
		this.closetime = closetime;
	}
	
	@Override
	public Serializable realId() {
		return id;
	}
	

	public String getSportname() {
		return sportname;
	}

	public void setSportname(String sportname) {
		this.sportname = sportname;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public boolean isBooking() {
		Timestamp curtime = new Timestamp(System.currentTimeMillis());
		if(opentime == null || closetime == null) return false;
		return status.equals(OpenTimeTableConstant.STATUS_BOOK) && opentime.before(curtime)&& closetime.after(curtime) 
				&& (StringUtils.equals(openType, OpenTimeTableConstant.OPEN_TYPE_FIELD) &&"Y".equals(rstatus) 
						|| StringUtils.equals(openType, OpenTimeTableConstant.OPEN_TYPE_PERIOD)
						|| StringUtils.equals(openType, OpenTimeTableConstant.OPEN_TYPE_INNING)) ;
	}

	public boolean hasOpentype(String type){
		if(StringUtils.isBlank(type)) return false;
		return StringUtils.equals(this.openType, type);
	}
	
	public boolean hasPeriod(){
		return hasOpentype(OpenTimeTableConstant.OPEN_TYPE_PERIOD);
	}
	public boolean hasField(){
		return hasOpentype(OpenTimeTableConstant.OPEN_TYPE_FIELD);
	}
	public boolean hasInning(){
		return hasOpentype(OpenTimeTableConstant.OPEN_TYPE_INNING);
	}
	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getRstarttime() {
		return getRtime(getStarttime());
	}

	public String getRendtime() {
		return getRtime(getEndtime());
	}
	public String getRstatus() {
		return rstatus;
	}
	public void setRstatus(String rstatus) {
		this.rstatus = rstatus;
	}
	public String getRtime(String str) {
		String tmp = str;
		if (tmp.length() == 3) tmp = "0" + tmp;
		String time = tmp.substring(0, 2) + ":" + tmp.substring(2, 4);
		return time;
	}
	
	
	public String getElecard() {
		return elecard;
	}
	public void setElecard(String elecard) {
		this.elecard = elecard;
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getMinpoint() {
		return minpoint;
	}
	public void setMinpoint(Integer minpoint) {
		this.minpoint = minpoint;
	}
	public Integer getMaxpoint() {
		return maxpoint;
	}
	public void setMaxpoint(Integer maxpoint) {
		this.maxpoint = maxpoint;
	}
	public boolean isOpenPointPay(){
		return maxpoint !=null && maxpoint > 0;
	}
	public boolean isOpenCardPay(){
		return StringUtils.containsAny(this.elecard, "ABD");
	}
	public boolean isDisCountPay(){
		return StringUtils.contains(this.elecard, "M");
	}
	public Timestamp getPlayTimeByHour(String hour){
		String playdatehour=DateUtil.format(playdate, "yyyy-MM-dd")+" "+hour+":00";
		return DateUtil.parseTimestamp(playdatehour);
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getSpflag() {
		return spflag;
	}
	public void setSpflag(String spflag) {
		this.spflag = spflag;
	}

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		if(sales == null) sales = 0;
		this.sales = sales;
	}
	
	public void addSales(int num){
		if(this.sales == null) this.sales = 0;
		this.sales += num;
	}

	public String getTkey() {
		return tkey;
	}

	public void setTkey(String tkey) {
		this.tkey = tkey;
	}

	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public Integer getSportprice() {
		return sportprice;
	}

	public void setSportprice(Integer sportprice) {
		this.sportprice = sportprice;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	public Integer getUnitMinute() {
		return unitMinute;
	}

	public void setUnitMinute(Integer unitMinute) {
		this.unitMinute = unitMinute;
	}
	
	public boolean isExpired(){
		return this.playdate.before(DateUtil.getCurDate());
	}

	public Integer getRemain() {
		return remain;
	}

	public void setRemain(Integer remain) {
		this.remain = remain;
	}
	public boolean hasRemoteOtt(){
		return remoteid!=null;
	}
}
