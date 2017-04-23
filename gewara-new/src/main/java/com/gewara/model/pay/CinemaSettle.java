package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;

/**
 * ӰԺ���˼�¼
 * @author gebiao(ge.biao@gewara.com)
 * @since Sep 3, 2012 11:09:18 PM
 */
public class CinemaSettle extends BaseObject{
	private static final long serialVersionUID = 8597217472925580238L;
	private Long id;
	private Long cinemaid;
	private Timestamp timefrom;			//�µ�ʱ��
	private Timestamp timeto;			//�µ�����ʱ��
	private Timestamp lasttime;			//���ڽ���ʱ��  �����˿�����
	private Timestamp curtime;			//���ڽ���ʱ��
	private Timestamp nexttime;			//Ԥ������ʱ�䣬����δ������ʾ
	private Integer amount;				//���ڽ��˽��
	private Integer lastOrderRefund;	//���ڶ����˿���
	private Integer curOrderRefund;		//���ڶ����˿���
	private Integer adjustment;			//�ֹ���������  ���ӽ��㣬�������ٽ�����
	private String status;				//״̬��Y���Ѿ����㣬N��δ����
	private String remark;
	public CinemaSettle() {
		
	}
	public CinemaSettle(Long cinemaid, Timestamp timefrom, Timestamp timeto, Timestamp lasttime, Timestamp curtime) {
		this.cinemaid = cinemaid;
		this.timefrom = timefrom;
		this.timeto = timeto;
		this.lasttime = lasttime;
		this.curtime = curtime;
		this.amount = 0;
		this.lastOrderRefund = 0;
		this.curOrderRefund = 0;
		this.status = "N";
		this.adjustment = 0;
	}
	public CinemaSettle(CinemaSettle last, Timestamp timeto, Timestamp curtime) {
		this(last.getCinemaid(), last.getTimeto(), timeto, last.getCurtime(), curtime);
	}
	public Long getCinemaid() {
		return cinemaid;
	}
	public void setCinemaid(Long cinemaid) {
		this.cinemaid = cinemaid;
	}
	public Timestamp getTimefrom() {
		return timefrom;
	}
	public void setTimefrom(Timestamp timefrom) {
		this.timefrom = timefrom;
	}
	public Timestamp getTimeto() {
		return timeto;
	}
	public void setTimeto(Timestamp timeto) {
		this.timeto = timeto;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Timestamp getLasttime() {
		return lasttime;
	}
	public void setLasttime(Timestamp lasttime) {
		this.lasttime = lasttime;
	}
	public Timestamp getCurtime() {
		return curtime;
	}
	public void setCurtime(Timestamp curtime) {
		this.curtime = curtime;
	}
	public Integer getLastOrderRefund() {
		return lastOrderRefund;
	}
	public void setLastOrderRefund(Integer lastOrderRefund) {
		this.lastOrderRefund = lastOrderRefund;
	}
	public Integer getCurOrderRefund() {
		return curOrderRefund;
	}
	public void setCurOrderRefund(Integer curOrderRefund) {
		this.curOrderRefund = curOrderRefund;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getAdjustment() {
		return adjustment;
	}
	public void setAdjustment(Integer adjustment) {
		this.adjustment = adjustment;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Timestamp getNexttime() {
		return nexttime;
	}
	public void setNexttime(Timestamp nexttime) {
		this.nexttime = nexttime;
	}
	
}
