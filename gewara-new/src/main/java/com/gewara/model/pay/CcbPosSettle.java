package com.gewara.model.pay;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;

public class CcbPosSettle extends BaseObject{
	private static final long serialVersionUID = 217714096143066976L;
	private Long id;			
	private String cardpan;		//����
	private Integer amount;		//���׽��
	private Integer handfee; 	//������
	private String preno;		//��Ȩ��
	private Date settledate;	//��������	
	
	private String tradeno;		//������
	private Date paiddate;		//��������
	private Integer alipaid;	
	private String payseqno;	//������������
	private String settle;		//����״̬ Y
	
	public CcbPosSettle(){
		this.settle = "N";
	}
	public CcbPosSettle(String[] info, String payseqno){
		this();
		this.cardpan = info[0];
		this.settledate = DateUtil.parseDate(info[1], "yyyyMMdd");
		this.preno = info[2];
		this.amount = new Double(info[3]).intValue();
		this.handfee = new Double((new Double(info[4])*100)).intValue();
		this.payseqno = payseqno;
	}
	public boolean isEqAmount(){
		return StringUtils.equals(amount+"", alipaid+"");
	}
	public String getCardpan() {
		return cardpan;
	}
	public void setCardpan(String cardpan) {
		this.cardpan = cardpan;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getHandfee() {
		return handfee;
	}
	public void setHandfee(Integer handfee) {
		this.handfee = handfee;
	}
	public String getPreno() {
		return preno;
	}
	public void setPreno(String preno) {
		this.preno = preno;
	}
	public Date getSettledate() {
		return settledate;
	}
	public void setSettledate(Date settledate) {
		this.settledate = settledate;
	}
	public String getTradeno() {
		return tradeno;
	}
	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	public Integer getAlipaid() {
		return alipaid;
	}
	public void setAlipaid(Integer alipaid) {
		this.alipaid = alipaid;
	}
	public String getPayseqno() {
		return payseqno;
	}
	public void setPayseqno(String payseqno) {
		this.payseqno = payseqno;
	}
	public String getSettle() {
		return settle;
	}
	public void setSettle(String settle) {
		this.settle = settle;
	}
	public Date getPaiddate() {
		return paiddate;
	}
	public void setPaiddate(Date paiddate) {
		this.paiddate = paiddate;
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
	public String getSettleText(){
		if(StringUtils.equals(settle, "Y")){
			return "�ɹ�����";
		}else if(StringUtils.equals(settle, "N_P")){
			return "�۸񲻵ȣ�����ʧ��";
		}else{
			return "δ����";
		}
	}
	public boolean isNeDate(){
		if(settledate==null || paiddate==null) return true;
		return settledate.compareTo(paiddate)!=0;
	}
}
