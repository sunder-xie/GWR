/**
 * 
 */
package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.model.BaseObject;
/**
 * �˻�ԭ·�˿�
 * @author gebiao(ge.biao@gewara.com)
 * @since Aug 24, 2012 1:35:57 PM
 */
public class AccountRefund extends BaseObject {
	private static final long serialVersionUID = 6029378080103301843L;
	public static final String STATUS_APPLY = "apply";			//������
	public static final String STATUS_ACCEPT = "accept"; 		//�ѽ���
	public static final String STATUS_UNACCEPT = "reject"; 		//�������˿�
	public static final String STATUS_FAIL = "fail"; 			//�˿�ʧ��
	public static final String STATUS_DEBIT = "debit";			//�˻��ۿ�
	public static final String STATUS_SUCCESS = "success";		//���ص�����֧���ɹ�

	private static Map<String, String> textMap = new HashMap<String, String>();
	private static Map<String, String> wayMap = new HashMap<String, String>();
	private Long id;
	private Integer version;			//�汾
	private String tradeno;		//������
	private String reason;		//ԭ��
	private String status;		//״̬
	private String origin;		//��Դ��refund:�����˿�, apply:�ͷ��������� charge ��ֵ�˿�
	private Integer amount;		//�˿���
	private Long memberid;
	private Long partnerid;		//�̼��˿�
	private String mobile;
	private Timestamp addtime;	//��������
	private Timestamp dealtime; //����ʱ��
	private String remark;		//�ر�˵��
	private String paymethod;	//֧����ʽ
	private Long applyuser;		//������
	private Long dealuser;	//������
	static{
		textMap.put(STATUS_APPLY, "������");
		textMap.put(STATUS_ACCEPT, "�ѽ���");
		textMap.put(STATUS_UNACCEPT, "�������˿�");
		textMap.put(STATUS_FAIL, "�˿�ʧ��");
		textMap.put(STATUS_SUCCESS, "�˿�ɹ�");
	}
	public AccountRefund(Long memberid, String mobile){
		this.memberid = memberid;
		this.mobile = mobile;
		this.status = STATUS_APPLY;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.dealtime = new Timestamp(System.currentTimeMillis());
	}
	public AccountRefund(){
	}
	public AccountRefund(OrderRefund refund) {
		this.tradeno = refund.getTradeno();
		this.reason = refund.getReason();
		this.status = STATUS_ACCEPT;
		this.origin = "apply";
		this.amount = refund.getGewaRetAmount();
		this.memberid = refund.getMemberid();
		this.partnerid = refund.getPartnerid();
		this.mobile = refund.getMobile();
		this.applyuser = refund.getApplyuser();
		this.remark = refund.getApplyinfo();
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.version = 0;
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
	public String getStatusText(String sstatus){
		return textMap.get(sstatus);
	}
	public String getWayText(String way){
		return wayMap.get(way);
	}
	public Timestamp getDealtime() {
		return dealtime;
	}
	public void setDealtime(Timestamp dealtime) {
		this.dealtime = dealtime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getPaymethod() {
		return paymethod;
	}
	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}
	public Long getApplyuser() {
		return applyuser;
	}
	public void setApplyuser(Long applyuser) {
		this.applyuser = applyuser;
	}
	public Long getDealuser() {
		return dealuser;
	}
	public void setDealuser(Long dealuser) {
		this.dealuser = dealuser;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Long getPartnerid() {
		return partnerid;
	}
	public void setPartnerid(Long partnerid) {
		this.partnerid = partnerid;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public boolean isOutPartner() {
		return memberid>PartnerConstant.MAX_MEMBERID;
	}
}
