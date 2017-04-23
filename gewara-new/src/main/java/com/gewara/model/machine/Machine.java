package com.gewara.model.machine;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.gewara.model.BaseObject;

public class Machine extends BaseObject {
	public static final long serialVersionUID = 30718895092701938L;
	public static final String TYPE_MACHINE_NEW_HOST="newhost";//������
	public static final String TYPE_MACHINE_OLD_HOST="oldhost";//������
	public static final String TYPE_MACHINE_LCD="lcd";//��ʾ��
	public static final String TYPE_MACHINE_VPN="vpn";//VPN
	public static final String TYPE_MACHINE_INTEGRATION="integration";//һ�����
	public static final String TYPE_MACHINE_RPT = "rpt";//���߰�
	public static final String TYPE_MACHINE_EXCHANGE = "exchange";//������
	public static final String TYPE_MACHINE_ROUTER = "router";//·����
	public static final String TYPE_MACHINE_NOTEBOOAK = "notebook"; //�ʼǱ�
	public static final String TYPE_MACHINE_3GCARD = "3gcard"; //3G������
	public static final String TYPE_MACHINE_PHONE = "phone"; //�ƶ�����
	public static final String TYPE_MACHINE_POS = "pos"; //pos��
	
	private Long id;
	private String machinenumber;
	private String machinename;
	private Long cinemaid;
	private Date hfhopendate;
	private Date leavedate;
	private String linkmethod;
	private String touchtype;
	private Integer ticketcount;
	private String machinetype;
	private String machinestatus;
	private String machinecontent;//��������
	private String machineowner;//ʹ����
	private Date buydate;//��������
	private String machineservice;//ά������
	private String machineusage;//vpn ��;
	private Timestamp addtime;
	private Date usedate;//��ʼʹ��ʱ��
	private String remark;//��������ע
	private String ip; //ip��ַ
	private String ipremark; //ip��ע
	private String operMember; //����Ա����Ϣ
	private Timestamp updatetime; //ǩ����������
	private String citycode;
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	public Machine(){
	}
	public Machine(Long cinemaid){
		this.cinemaid = cinemaid;
		this.addtime= new Timestamp(System.currentTimeMillis());
	}
	@Override
	public final Serializable realId() {
		return id;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMachinenumber() {
		return machinenumber;
	}

	public void setMachinenumber(String machinenumber) {
		this.machinenumber = machinenumber;
	}

	public String getMachinename() {
		return machinename;
	}

	public void setMachinename(String machinename) {
		this.machinename = machinename;
	}

	public Long getCinemaid() {
		return cinemaid;
	}

	public void setCinemaid(Long cinemaid) {
		this.cinemaid = cinemaid;
	}

	public Date getHfhopendate() {
		return hfhopendate;
	}

	public void setHfhopendate(Date hfhopendate) {
		this.hfhopendate = hfhopendate;
	}

	public Date getLeavedate() {
		return leavedate;
	}

	public void setLeavedate(Date leavedate) {
		this.leavedate = leavedate;
	}

	public String getLinkmethod() {
		return linkmethod;
	}

	public void setLinkmethod(String linkmethod) {
		this.linkmethod = linkmethod;
	}

	public String getTouchtype() {
		return touchtype;
	}

	public void setTouchtype(String touchtype) {
		this.touchtype = touchtype;
	}

	public Integer getTicketcount() {
		return ticketcount;
	}

	public void setTicketcount(Integer ticketcount) {
		this.ticketcount = ticketcount;
	}

	public String getMachinetype() {
		return machinetype;
	}

	public void setMachinetype(String machinetype) {
		this.machinetype = machinetype;
	}

	public String getMachinestatus() {
		return machinestatus;
	}

	public void setMachinestatus(String machinestatus) {
		this.machinestatus = machinestatus;
	}

	public String getMachinecontent() {
		return machinecontent;
	}

	public void setMachinecontent(String machinecontent) {
		this.machinecontent = machinecontent;
	}

	public Timestamp getAddtime() {
		return addtime;
	}

	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}

	public Date getBuydate() {
		return buydate;
	}

	public void setBuydate(Date buydate) {
		this.buydate = buydate;
	}

	public String getMachineservice() {
		return machineservice;
	}

	public void setMachineservice(String machineservice) {
		this.machineservice = machineservice;
	}

	public String getMachineusage() {
		return machineusage;
	}

	public void setMachineusage(String machineusage) {
		this.machineusage = machineusage;
	}

	public String getMachineowner() {
		return machineowner;
	}

	public void setMachineowner(String machineowner) {
		this.machineowner = machineowner;
	}

	public Date getUsedate() {
		return usedate;
	}

	public void setUsedate(Date usedate) {
		this.usedate = usedate;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIpremark() {
		return ipremark;
	}

	public void setIpremark(String ipremark) {
		this.ipremark = ipremark;
	}

	public String getOperMember() {
		return operMember;
	}

	public void setOperMember(String operMember) {
		this.operMember = operMember;
	}

	public Timestamp getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
}
