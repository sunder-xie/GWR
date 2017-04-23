package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;
/**
 * �˻�������
 * @author acerge(acerge@163.com)
 * @since 2:16:19 PM Nov 13, 2009
 */
public class Adjustment extends BaseObject{
	private static final long serialVersionUID = 5145989176628675817L;
	public static final String STATUS_NEW = "new";				//�������
	public static final String STATUS_CANCEL = "cancel";		//ȡ����
	public static final String STATUS_SUCCESS = "success";		//�˿�ɹ���
	public static final String CORRECT_ADD = "add";				//�����˻����
	public static final String CORRECT_ORDER = "add_order";		//�˶�������
	public static final String CORRECT_SUB = "sub";				//�����˻����
	public static final String CORRECT_REFUND = "sub_refund";	//�˿����
	public static final String CORRECT_DEPOSIT = "sub_deposit"; //�۳���֤��
	private Long id;				//ID
	private Integer version;		//�汾����
	private Long memberid;			//�û�ID
	private String membername;		//�û���
	private Long accountid;			//�˻�ID
	private String correcttype;		//�������ͣ����ӿ������ٿ��� 
	private Integer amount;			//�˿���
	private Timestamp addtime;		//����ʱ��
	private Timestamp updatetime;	//�˿�ʱ��
	private Long clerkid;			//����Ա
	private String status;			//״̬
	private String content;			//˵��
	private String tradeno;			//������
	private Integer bankcharge;		//���˽��
	private Integer othercharge;	//�����˽��
	private Integer depositcharge;	//��֤����
	
	public Adjustment(){
	}
	public Adjustment(Long accountId, Long memberid, String membername, String type){
		this.accountid = accountId;
		this.memberid = memberid;
		this.correcttype = type;
		this.amount = 0;
		this.status = STATUS_NEW;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.membername = membername;
		this.bankcharge = 0;
		this.othercharge = 0;
		this.depositcharge = 0;
		this.version = 0;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Timestamp getAddtime() {
		return addtime;
	}
	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	public Long getClerkid() {
		return clerkid;
	}
	public void setClerkid(Long clerkid) {
		this.clerkid = clerkid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Long getAccountid() {
		return accountid;
	}
	public void setAccountid(Long accountid) {
		this.accountid = accountid;
	}
	public Timestamp getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	public String getCorrecttype() {
		return correcttype;
	}
	public void setCorrecttype(String correcttype) {
		this.correcttype = correcttype;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getSubtractAmount(){//�����ٵ�����
		if(this.getCorrecttype().startsWith(CORRECT_SUB)) return amount;
		return -amount;
	}
	public Integer getAddAmount(){//�����ٵ�����
		if(this.getCorrecttype().startsWith(CORRECT_ADD)) return amount;
		return -amount;
	}
	public String getMembername() {
		return membername;
	}
	public void setMembername(String membername) {
		this.membername = membername;
	}
	public Integer getAddBankcharge(){
		if(this.getCorrecttype().startsWith(CORRECT_ADD)) return bankcharge;
		return -bankcharge;
	}
	public Integer getAddOthercharge(){
		if(this.getCorrecttype().startsWith(CORRECT_ADD)) return othercharge;
		return -othercharge;
	}
	
	public Integer getAddDepositCharge(){
		if(this.getCorrecttype().startsWith(CORRECT_ADD)) return depositcharge;
		return -depositcharge;
	}
	
	public String getTradeno() {
		return tradeno;
	}
	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	public Integer getBankcharge() {
		return bankcharge;
	}
	public void setBankcharge(Integer bankcharge) {
		this.bankcharge = bankcharge;
	}
	public Integer getOthercharge() {
		return othercharge;
	}
	public void setOthercharge(Integer othercharge) {
		this.othercharge = othercharge;
	}
	public Integer getDepositcharge() {
		return depositcharge;
	}
	public void setDepositcharge(Integer depositcharge) {
		this.depositcharge = depositcharge;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
}
