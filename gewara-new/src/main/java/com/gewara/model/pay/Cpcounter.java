package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;

public class Cpcounter extends BaseObject {
	private static final long serialVersionUID = -1582775445829862665L;
	public static final String FLAG_CITYCODE = "city";
	public static final String FLAG_PARTNER = "partner";
	private Long id;
	private Integer version;
	private Long spcounterid;		//����
	private String flag;			//���ࣺ�����л��̼�
	private String cpcode;			//���л��̼Ҵ���,
	private Integer allownum;		//�µ���������
	private Integer basenum;		//������������
	private Integer limitnum;		//�ɽ�����

	private Integer allordernum;	//�µ�����
	private Integer allquantity;	//����������

	private Integer sellorder;		//�����µ���
	private Integer sellquantity;	//��������
	private Timestamp addtime;		
	private Timestamp updatetime;
	
	public Cpcounter(){}
	
	public Cpcounter(Long spcounterid, String flag, String cpcode, Integer basenum){
		this.version=0;
		this.spcounterid = spcounterid;
		this.flag = flag;
		this.cpcode = cpcode;
		this.allownum = 0;
		this.basenum = basenum;

		this.sellorder = 0;
		this.sellquantity = 0;
		this.allquantity = 0;
		this.allordernum = 0;
		
		this.addtime = DateUtil.getCurFullTimestamp();
		this.updatetime = this.addtime;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSpcounterid() {
		return spcounterid;
	}

	public void setSpcounterid(Long spcounterid) {
		this.spcounterid = spcounterid;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getCpcode() {
		return cpcode;
	}

	public void setCpcode(String cpcode) {
		this.cpcode = cpcode;
	}

	public Integer getBasenum() {
		return basenum;
	}

	public void setBasenum(Integer basenum) {
		this.basenum = basenum;
	}

	public Integer getAllownum() {
		return allownum;
	}

	public void setAllownum(Integer allownum) {
		this.allownum = allownum;
	}

	public Integer getLimitnum() {
		return limitnum;
	}

	public void setLimitnum(Integer limitnum) {
		this.limitnum = limitnum;
	}

	public Integer getSellorder() {
		return sellorder;
	}

	public void setSellorder(Integer sellorder) {
		this.sellorder = sellorder;
	}

	public Integer getSellquantity() {
		return sellquantity;
	}

	public void setSellquantity(Integer sellquantity) {
		this.sellquantity = sellquantity;
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

	@Override
	public Serializable realId() {
		return id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getAllordernum() {
		return allordernum;
	}

	public void setAllordernum(Integer allordernum) {
		this.allordernum = allordernum;
	}

	public Integer getAllquantity() {
		return allquantity;
	}

	public void setAllquantity(Integer allquantity) {
		this.allquantity = allquantity;
	}

}
