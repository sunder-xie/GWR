package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;

/**
 * @author gebiao(ge.biao@gewara.com)
 * @since May 29, 2012 3:23:18 PM
 */
public class Spcounter extends BaseObject{
	private static final long serialVersionUID = 3758832149177783157L;
	//�ؼۻ��������
	public static final String CTLTYPE_ORDER = "order";			//���ݶ�������
	public static final String CTLTYPE_QUANTITY = "quantity";	//������������
	private Integer version;
	private Long id;
	private String ctlmember;			//����һ�����û�Ψһ������Y��N������Լ�����
	private String ctltype;				//�������ͣ����ݶ����� �� Ʊ��
	private Integer limitmaxnum;		//���������������
	private Integer basenum;			//ÿ���µ�������
	private Integer allowaddnum;		//����µ�����������

	private Timestamp periodtime;		//����ʱ��
	private Integer periodMinute;		//���ڷ���
	
	private Integer sellquantity;		//������������
	private Integer sellordernum;		//����������������
	private Integer allquantity;		//����������		
	private Integer allordernum;		//������������
	
	public Spcounter(){}
	
	public Spcounter(String ctltype){
		this.version = 0;
		this.ctltype = ctltype;
		this.periodtime = DateUtil.getCurTruncTimestamp();
		this.sellordernum = 0;
		this.sellquantity = 0;
		this.allquantity = 0;
		this.allordernum = 0;
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
	public String getCtltype() {
		return ctltype;
	}
	public void setCtltype(String ctltype) {
		this.ctltype = ctltype;
	}
	public Integer getLimitmaxnum() {
		return limitmaxnum;
	}
	public void setLimitmaxnum(Integer limitmaxnum) {
		this.limitmaxnum = limitmaxnum;
	}
	public Integer getSellquantity() {
		return sellquantity;
	}
	public void setSellquantity(Integer sellquantity) {
		this.sellquantity = sellquantity;
	}
	public Integer getSellordernum() {
		return sellordernum;
	}
	public void setSellordernum(Integer sellordernum) {
		this.sellordernum = sellordernum;
	}
	public String getCtlmember() {
		return ctlmember;
	}
	public void setCtlmember(String ctlmember) {
		this.ctlmember = ctlmember;
	}
	public Integer getAllowaddnum() {
		return allowaddnum;
	}
	public void setAllowaddnum(Integer allowaddnum) {
		this.allowaddnum = allowaddnum;
	}
	public Timestamp getPeriodtime() {
		return periodtime;
	}
	public void setPeriodtime(Timestamp periodtime) {
		this.periodtime = periodtime;
	}
	public Integer getPeriodMinute() {
		return periodMinute;
	}
	public void setPeriodMinute(Integer periodMinute) {
		this.periodMinute = periodMinute;
	}
	public Integer getAllquantity() {
		return allquantity;
	}
	public void setAllquantity(Integer allquantity) {
		this.allquantity = allquantity;
	}
	public Integer getAllordernum() {
		return allordernum;
	}
	public void setAllordernum(Integer allordernum) {
		this.allordernum = allordernum;
	}
	public Integer getBasenum() {
		return basenum;
	}
	public void setBasenum(Integer basenum) {
		this.basenum = basenum;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
