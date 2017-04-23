package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.ticket.OrderConstant;
import com.gewara.model.BaseObject;

public class BaseOrderExtra extends BaseObject {
	private static final long serialVersionUID = 4821814105905969549L;
	public static final String LEVEL_INIT = "init";
	public static final String LEVEL_MAIN = "main";
	public static final String LEVEL_FINISH = "finish";
	
	protected Long id;					//����ID
	protected String tradeno;			//������
	protected String status;			//����״̬gewOrder ---> status
	protected Timestamp addtime;		//�µ�ʱ��
	protected Timestamp updatetime;		//����ʱ��
	protected String invoice;			//�Ƿ�ɿ���Ʊ,Y�ѿ�,N�ɿ�,F���ɿ�
	protected String pretype;			//����E����ӪM
	protected Long memberid;			//�û�ID
	protected Long partnerid;			//������ID
	protected String ordertype;			//��������
	protected String expressnote;		//��ݵ���
	protected String expresstype;		//�������
	protected String processLevel;		//���ڴ������
	protected String expressStatus;		//���״̬
	protected String dealStatus;		//����״̬
	protected Long dealUser;			//�����û�
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

	public Timestamp getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}
	
	public String getPretype() {
		return pretype;
	}

	public void setPretype(String pretype) {
		this.pretype = pretype;
	}

	public Long getMemberid() {
		return memberid;
	}

	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}

	public Long getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(Long partnerid) {
		this.partnerid = partnerid;
	}

	public String getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}

	public String getExpressnote() {
		return expressnote;
	}

	public void setExpressnote(String expressnote) {
		this.expressnote = expressnote;
	}

	public String getExpresstype() {
		return expresstype;
	}

	public void setExpresstype(String expresstype) {
		this.expresstype = expresstype;
	}

	public boolean hasExpressType(String type){
		return StringUtils.equals(this.expresstype, type);
	}
	
	public boolean hasPaidSuccess(){
		return StringUtils.equals(this.status, OrderConstant.STATUS_PAID_SUCCESS);
	}
	
	@Override
	public Serializable realId(){
		return id;
	}

	public String getProcessLevel() {
		return processLevel;
	}

	public void setProcessLevel(String processLevel) {
		this.processLevel = processLevel;
	}

	public String getExpressStatus() {
		return expressStatus;
	}

	public void setExpressStatus(String expressStatus) {
		this.expressStatus = expressStatus;
	}

	public String getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}

	public Long getDealUser() {
		return dealUser;
	}

	public void setDealUser(Long dealUser) {
		this.dealUser = dealUser;
	}
}
