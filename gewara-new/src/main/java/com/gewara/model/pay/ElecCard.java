package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.AdminCityContant;
import com.gewara.constant.PayConstant;
import com.gewara.constant.TagConstant;
import com.gewara.constant.order.ElecCardConstant;
import com.gewara.model.BaseObject;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
public class ElecCard extends BaseObject {
	private static final long serialVersionUID = 3754546507279229426L;
	private Long id;
	private String cardno;			//����
	private String cardpass;		//����
	private String status;			//״̬
	private ElecCardBatch ebatch;	//����
	private Long deluserid;			//������
	private Timestamp deltime;		//����ʱ��
	private Long possessor;			//ӵ����
	private Long gainer;			//��ȡ��
	private String mobile;			//��ȡ�ֻ���󶨱�־
	private Long orderid;			//ʹ�õĶ�����
	private Integer version;		//
	private Timestamp begintime;
	private Timestamp endtime;
	
	private String tmpPass;			//��ʱʹ��
	public ElecCard(){}
	public ElecCard(String cardno){
		this.cardno = cardno;
		this.status = ElecCardConstant.STATUS_NEW;
	}
	public ElecCard(ElecCardBatch ebatch){
		this.status = ElecCardConstant.STATUS_NEW;
		this.ebatch = ebatch;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public Timestamp getBegintime() {
		return begintime;
	}
	public void setBegintime(Timestamp begintime) {
		this.begintime = begintime;
	}
	public Timestamp getEndtime() {
		return endtime;
	}
	public void setEndtime(Timestamp endtime) {
		this.endtime = endtime;
	}

	public Timestamp getDeltime() {
		return deltime;
	}
	public void setDeltime(Timestamp deltime) {
		this.deltime = deltime;
	}
	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getCardpass() {
		return cardpass;
	}
	public void setCardpass(String cardpass) {
		this.cardpass = cardpass;
	}
	public ElecCardBatch getEbatch() {
		return ebatch;
	}
	public void setEbatch(ElecCardBatch ebatch) {
		this.ebatch = ebatch;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public Long getPossessor() {
		return possessor;
	}
	public void setPossessor(Long possessor) {
		this.possessor = possessor;
	}
	public String getWeektype() {
		return ebatch.getWeektype();
	}
	public String getValidcinema() {
		return ebatch.getValidcinema();
	}
	public String getValidmovie() {
		return ebatch.getValidmovie();
	}
	public String getValiditem() {
		return ebatch.getValiditem();
	}
	public String getValidprice() {
		return ebatch.getValidprice();
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getCardtype(){
		return ebatch.getCardtype();
	}
	public Long getGainer() {
		return gainer;
	}
	public void setGainer(Long gainer) {
		this.gainer = gainer;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Long getOrderid() {
		return orderid;
	}
	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}
	public Long getDeluserid() {
		return deluserid;
	}
	public void setDeluserid(Long deluserid) {
		this.deluserid = deluserid;
	}
	

	//~~~~~~~~~~biz method~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public Timestamp getTimefrom(){
		if(this.begintime!=null) return this.begintime;
		return ebatch.getTimefrom();
	}
	public Timestamp getTimeto(){
		if(this.endtime!=null) return this.endtime;
		return ebatch.getTimeto();
	}

	public String gainStatusText(){
		if(this.status.equals(ElecCardConstant.STATUS_SOLD)) return "�۳�";
		if(this.status.equals(ElecCardConstant.STATUS_NEW)) return "����";
		if(this.status.equals(ElecCardConstant.STATUS_DISCARD)) return "����";
		if(this.status.equals(ElecCardConstant.STATUS_USED)) return "��ʹ��";
		if(this.status.equals(ElecCardConstant.STATUS_LOCK)) return "�Ѷ���";
		return "δ֪";
	}
	public boolean isUsed(){
		return this.status.equals(ElecCardConstant.STATUS_USED);
	}
	public boolean available(){
		return validtime() && this.status.equals(ElecCardConstant.STATUS_SOLD);
	}
	public boolean validTag(String tag){
		return StringUtils.equals(ebatch.getTag(), tag);
	}
	public boolean expiredUnused(){//����δʹ�õ�
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		if(endtime!=null){
			return this.status.equals(ElecCardConstant.STATUS_SOLD) && cur.after(endtime);
		}
		return this.status.equals(ElecCardConstant.STATUS_SOLD) && cur.after(ebatch.getTimeto());
	}
	private boolean validtime(){
		if(begintime!=null && endtime!=null) {
			Timestamp cur = new Timestamp(System.currentTimeMillis());
			return begintime.before(cur) && endtime.after(cur);
		}else if(endtime!=null){
			Timestamp cur = new Timestamp(System.currentTimeMillis());
			return ebatch.getTimefrom().before(cur) && endtime.after(cur);
		}else{
			return ebatch.isValidtime();
		}
	}
	public String gainErrorMsg(){
		String msg = "";
		if(isUsed()) msg += "�˿��ѱ�ʹ�ù���";
		else if(ElecCardConstant.STATUS_NEW.equals(this.status)) msg = msg + "�˿����ڴ���״̬��";
		else if(ElecCardConstant.STATUS_DISCARD.equals(this.status)) msg = msg + "�˿��ѷ�����";
		if(begintime!=null && endtime!=null) {
			if(!validtime()) msg += "�˿�ֻ����" + DateUtil.formatTimestamp(this.begintime)+"��"+DateUtil.formatTimestamp(this.endtime)+"�ڼ�ʹ�ã�";
		}else {
			if(!ebatch.isValidtime()) msg += "�˿�ֻ����" + DateUtil.formatTimestamp(ebatch.getTimefrom())+"��"+DateUtil.formatTimestamp(ebatch.getTimeto())+"�ڼ�ʹ�ã�";
		}
		return msg;
	}	
	/**
	 * �Ƿ���г�����
	 * @return
	 */
	public boolean canDelay(){
		if(status.equals(ElecCardConstant.STATUS_SOLD) && StringUtils.equals(ebatch.getSoldType(), ElecCardBatch.SOLD_TYPE_S) && 
				ebatch.getDelayDays() != null && ebatch.getDelayUseDays() != null && ebatch.getDelayUseDays() > 0 && ebatch.getDelayFee() != null && ebatch.getDelayFee() > 0){
			 Timestamp curMill = DateUtil.getMillTimestamp();
			if(DateUtil.getDiffSecond(curMill,this.getTimeto()) > 0 && 
					DateUtil.getDiffSecond(curMill,DateUtil.addDay(getTimeto(),ebatch.getDelayDays())) <= 0){
				return true;
			}
		}
		return false;
	}
	
	public boolean canUse(Date date, Long cinemaId, Long movieId, Long mpid, String citycode){
		if(!available() || !status.equals(ElecCardConstant.STATUS_SOLD)) return false;
		if(date != null){
			String week = ""+DateUtil.getWeek(date);
			if(StringUtils.isNotBlank(ebatch.getWeektype()) && ebatch.getWeektype().indexOf(week) == -1)
				return false;
		}
		if(StringUtils.isNotBlank(ebatch.getValidcinema())){
			List<Long> cidList = BeanUtil.getIdList(ebatch.getValidcinema(), ",");
			if(!cidList.contains(cinemaId)) return false;
		}
		if(StringUtils.isNotBlank(ebatch.getValidmovie())){
			List<Long> cidList = BeanUtil.getIdList(ebatch.getValidmovie(), ",");
			if(!cidList.contains(movieId)) return false;
		}
		if(StringUtils.isNotBlank(ebatch.getValiditem())){
			List<Long> cidList = BeanUtil.getIdList(ebatch.getValiditem(), ",");
			if(!cidList.contains(mpid)) return false;
		}
		if(!isCanUseCity(citycode)) return false;
		if(!isUseCurTime()) return false;
		return true;
	}
	public boolean isCanUseCity(String citycode){
		boolean result = StringUtils.equals(AdminCityContant.CITYCODE_ALL, ebatch.getCitycode()) || StringUtils.contains(ebatch.getCitycode(), citycode);
		if(StringUtils.equals(ebatch.getCitypattern(), PayConstant.MATCH_PATTERN_EXCLUDE)) return !result;
		return result;
	}
	public boolean isUseCurTime(){
		String opentime = ebatch.getAddtime1();
		String closetime = ebatch.getAddtime2();
		if(StringUtils.isNotBlank(opentime) && StringUtils.isNotBlank(closetime)){
			String hh = DateUtil.format(new Timestamp(System.currentTimeMillis()), "HHmm");
			if(hh.compareTo(opentime)<0 || hh.compareTo(closetime)>0) return false;
		}
		return true;
	}
	public String gainUsage(){
		String ctype = getCardtype();
		if("A".equals(ctype)){
			String msg = StringUtils.equals(this.ebatch.getTag(), TagConstant.TAG_SPORT)?"����һ�����أ�����ʹ�ö���":"����һ��Ʊ������ʹ�ö���";
			return msg;
		}
		if("B".equals(ctype)){
			if(StringUtils.isNotBlank(ebatch.getLimitdesc())){
				return ebatch.getLimitdesc();
			}
			if(StringUtils.equals(ebatch.getTag(), TagConstant.TAG_MOVIE) || StringUtils.equals(ebatch.getTag(), TagConstant.TAG_DRAMA)){
				return "ÿ����λ����ֵ" + ebatch.getAmount() + "Ԫ������������֧��";
			}else {
				return "ÿ����������ֵ" + ebatch.getAmount() + "Ԫ������������֧��";
			}
		}
		if("C".equals(ctype)) return "�����ֽ�" + ebatch.getAmount() + "Ԫ";
		if("D".equals(ctype)) return "�����ֽ�" + ebatch.getAmount() + "Ԫ��ÿ������1��";
		return "";
	}
	public boolean needActivation(){
		return StringUtils.equals(ebatch.getActivation(), ElecCardBatch.ACTIVATION_Y) && StringUtils.isBlank(getMobile());
	}
	public void putTmpPass(String pass){
		this.tmpPass = pass;
	}
	public String gainTempPass() {
		return tmpPass;
	}
}
