/**
 * 
 */
package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.constant.ticket.RefundConstant;
import com.gewara.model.BaseObject;
/**
 * �����˿�
 * @author gebiao(ge.biao@gewara.com)
 * @since Aug 20, 2012 4:43:34 PM
 */
public class OrderRefund extends BaseObject {
	private static final long serialVersionUID = 6029378080103301843L;
	private Long id;
	private Integer version;			//�汾
	private String tradeno;				//������
	private String ordertype;			//��������
	private Long placeid;				//����ID
	private Long memberid;				//�û�ID
	private String mobile;				//�ֻ���
	private String orderstatus;			//״̬���ͣ�paid_failure: ��������  paid_success: �ɽ����� paid_failure_unfix: ��λ������
	private String refundtype;			//�˿����ͣ�unknown, all ȫ���˿part �����˿supplement ����
	private Long partnerid;				//�����̼�
	private Timestamp expiretime;		//��������ʱ��
	
	private Integer gewaRetAmount;		//Gewara�˿�
	private Integer merRetAmount;		//�̼��˿�

	private Integer oldSettle;			//ԭ������
	private Integer newSettle;			//�½�����
	private String settletype;			//��������
	private String cardno;				//����ȯ��
	
	private String opmark;				//������ʶ����¼��Ҫ����ɵĲ���
	private String reason;				//ԭ��
	private String retback;				//�Ƿ�Ҫԭ·���أ�Y����Ҫ��N������Ҫ��O��δ֪, �μ�����(Other)��S: ���ύ����(Submit) R�������Ѿ�����(Refund)��F�����񷵻�����(Failure)
	private Long applyuser;				//������
	private String applyinfo;			//������Ϣ
	
	private String otherinfo;			//������Ϣ�������ˣ����񷵻���Ϣ
	
	
	//���㷽ʽ
	private Timestamp addtime;			//��������
	private Timestamp refundtime; 		//�˿�ʱ��
	private String status;				//״̬
	private String dealinfo;			//������Ϣ

	private String preinfo;				//Ԥ����Ҫ��
	private String cancelinfo;			//�ж�����
	
	public OrderRefund(){}
	public OrderRefund(GewaOrder order, Long userid){
		this.tradeno = order.getTradeNo();
		this.memberid = order.getMemberid();
		this.mobile = order.getMobile();
		this.orderstatus = order.getStatus();
		this.ordertype = order.getOrdertype();
		this.partnerid = order.getPartnerid();
		this.gewaRetAmount = 0;	//Ĭ�ϲ��˿�
		this.merRetAmount = 0;
		this.applyuser = userid;
		this.status = RefundConstant.STATUS_APPLY;
		this.addtime = new Timestamp(System.currentTimeMillis());
	}
	public void copyFrom(OrderRefund newrefund) {
		this.tradeno = newrefund.tradeno;
		this.memberid = newrefund.memberid;
		this.mobile = newrefund.mobile;
		this.orderstatus = newrefund.orderstatus;
		this.partnerid = newrefund.partnerid;
		this.gewaRetAmount = newrefund.gewaRetAmount;
		this.merRetAmount = newrefund.merRetAmount;
		this.applyuser = newrefund.applyuser;
		this.status = newrefund.status;
		this.addtime = newrefund.addtime;
		this.refundtype = newrefund.refundtype;
		this.expiretime = newrefund.expiretime;
		this.cardno = newrefund.cardno;
		this.opmark = newrefund.opmark;
		this.reason = newrefund.reason;
		this.retback = newrefund.retback;
		this.applyinfo = newrefund.applyinfo;
		this.refundtime = newrefund.refundtime;
		this.dealinfo = newrefund.dealinfo;
		this.preinfo = newrefund.preinfo;
		this.cancelinfo = newrefund.cancelinfo;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public Timestamp getAddtime() {
		return addtime;
	}
	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTradeno() {
		return tradeno;
	}
	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getOpmark() {
		return opmark;
	}
	public void setOpmark(String opmark) {
		this.opmark = opmark;
	}
	public String getRetback() {
		return retback;
	}
	public void setRetback(String retback) {
		this.retback = retback;
	}
	public Integer getGewaRetAmount() {
		return gewaRetAmount;
	}
	public void setGewaRetAmount(Integer gewaRetAmount) {
		this.gewaRetAmount = gewaRetAmount;
	}
	public Integer getMerRetAmount() {
		return merRetAmount;
	}
	public void setMerRetAmount(Integer merRetAmount) {
		this.merRetAmount = merRetAmount;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public Long getPartnerid() {
		return partnerid;
	}
	public void setPartnerid(Long partnerid) {
		this.partnerid = partnerid;
	}
	public String getOrderstatus() {
		return orderstatus;
	}
	public void setOrderstatus(String orderstatus) {
		this.orderstatus = orderstatus;
	}
	public Timestamp getRefundtime() {
		return refundtime;
	}
	public void setRefundtime(Timestamp refundtime) {
		this.refundtime = refundtime;
	}
	public Long getApplyuser() {
		return applyuser;
	}
	public void setApplyuser(Long applyuser) {
		this.applyuser = applyuser;
	}
	public String getRefundtype() {
		return refundtype;
	}
	public void setRefundtype(String refundtype) {
		this.refundtype = refundtype;
	}
	public String getApplyinfo() {
		return applyinfo;
	}
	public void setApplyinfo(String applyinfo) {
		this.applyinfo = applyinfo;
	}
	public String getDealinfo() {
		return dealinfo;
	}
	public void setDealinfo(String dealinfo) {
		this.dealinfo = dealinfo;
	}
	public Timestamp getExpiretime() {
		return expiretime;
	}
	public void setExpiretime(Timestamp expiretime) {
		this.expiretime = expiretime;
	}
	public boolean gainExpired(){//�ǹ��ڶ���
		return expiretime != null && expiretime.before(new Timestamp(System.currentTimeMillis())); 
	}
	public String getPreinfo() {
		return preinfo;
	}
	public void setPreinfo(String preinfo) {
		this.preinfo = preinfo;
	}
	public String getCancelinfo() {
		return cancelinfo;
	}
	public void setCancelinfo(String cancelinfo) {
		this.cancelinfo = cancelinfo;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Integer getOldSettle() {
		return oldSettle;
	}
	public void setOldSettle(Integer oldSettle) {
		this.oldSettle = oldSettle;
	}
	public Integer getNewSettle() {
		return newSettle;
	}
	public void setNewSettle(Integer newSettle) {
		this.newSettle = newSettle;
	}
	public String getSettletype() {
		return settletype;
	}
	public void setSettletype(String settletype) {
		this.settletype = settletype;
	}
	public String getOrdertype() {
		return ordertype;
	}
	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}
	public Long getPlaceid() {
		return placeid;
	}
	public void setPlaceid(Long placeid) {
		this.placeid = placeid;
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
}
