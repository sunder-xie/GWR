package com.gewara.json;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.pay.GewaOrder;

public class AppSourceCount implements Serializable{
	private static final long serialVersionUID = 4008994603934771097L;
	public static final String TYPE_IO 		= "io";		//��װ�ʹ�
	public static final String TYPE_REG 	= "reg";		//ע��
	public static final String TYPE_LOGIN 	= "login";	//��¼
	public static final String TYPE_ORDER 	= "order";	//�¶���
	public static final String FLAG_INSTALL = "install";	//�û���װӦ��
	public static final String FLAG_OPEN = "open";			//�û���Ӧ��
	private String appSource;		//Ӧ����Դ ���磺91 mark
	private String osType;			//OS ���� ���磺iphone
	private String deviceid;		//�豸��
	private String flag;			//open:��(1)��install����װ(0)
	private String type;			//����
	private Long orderid;			//����id
	private String tradeno;			//������
	private Long partnerid;			//������id
	private Long memberid;			//�û�ID
	private Timestamp addtime;		//ʱ��
	private String citycode;		//���б���
	private String osVersion;		//ϵͳ�汾
	private String mobileType;		//�ֻ��ͺ�
	private String apptype;			//Ӧ������
	private String appVersion;		//�ֻ��ͻ��˰汾
	private String orderOrigin;
	
	private String newdeviceid;//���豸�ţ���֤�ֻ���Ψһ��
	
	public AppSourceCount(String appSource){
		this.appSource = appSource;
		this.addtime = new Timestamp(System.currentTimeMillis());
	}
	public AppSourceCount(GewaOrder order){
		this.addtime = order.getAddtime();
		this.tradeno = order.getTradeNo();
		this.orderid = order.getId();
		this.memberid = order.getMemberid();
		this.partnerid = order.getPartnerid();
		this.citycode = order.getCitycode();
		this.type = AppSourceCount.TYPE_ORDER;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getApptype() {
		return apptype;
	}
	public void setApptype(String apptype) {
		this.apptype = apptype;
	}
	public String getCitycode() {
		return citycode;
	}
	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = StringUtils.lowerCase(osVersion);
	}
	public String getMobileType() {
		return mobileType;
	}
	public void setMobileType(String mobileType) {
		this.mobileType = mobileType;
	}
	public AppSourceCount(){
		
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		if("0".equals(flag)){
			this.flag = FLAG_INSTALL;
		}else if("1".equals(flag)){
			this.flag = FLAG_OPEN;
		}else{
			this.flag = flag;
		}
	}
	public String getAppSource() {
		return appSource;
	}
	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public Timestamp getAddtime() {
		return addtime;
	}
	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	public String getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
	public Long getOrderid() {
		return orderid;
	}
	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getNewdeviceid() {
		return newdeviceid;
	}
	public void setNewdeviceid(String newdeviceid) {
		this.newdeviceid = newdeviceid;
	}
	public String getOrderOrigin() {
		return orderOrigin;
	}
	public void setOrderOrigin(String orderOrigin) {
		this.orderOrigin = orderOrigin;
	}
	public String getTradeno() {
		return tradeno;
	}
	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	public Long getPartnerid() {
		return partnerid;
	}
	public void setPartnerid(Long partnerid) {
		this.partnerid = partnerid;
	}
	
	
	
}
