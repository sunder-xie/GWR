package com.gewara.model.sport;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.constant.Status;
import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;

public class Guarantee extends BaseObject {
	private static final long serialVersionUID = -6472564510693501627L;
	public static String[] disallowBindField = new String[]{"citycode", "citycode", "addtime", "updatetime"};
	private Long id;						
	private String name;					//����
	private Integer price;					//�۸�
	private Integer costprice;				//����۸� 
	private String otherinfo;				//������Ϣ
	private String status;					//״̬
	private String citycode;				//���б���
	private String remark;
	private String description;				//˵��
	
	private Long createuser;				//������
	private Timestamp addtime;				//����ʱ��
	private Timestamp updatetime;			//����ʱ��
	private String ordermsg;				//��������
	
	@Override
	public Serializable realId() {
		return id;
	}

	public Guarantee(){}
	
	public Guarantee(Integer price){
		this.price = price;
		this.costprice = 0;
		this.status = Status.Y;
		this.addtime = DateUtil.getCurFullTimestamp();
		this.updatetime = this.addtime;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getCostprice() {
		return costprice;
	}

	public void setCostprice(Integer costprice) {
		this.costprice = costprice;
	}

	public String getOtherinfo() {
		return otherinfo;
	}

	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getCreateuser() {
		return createuser;
	}

	public void setCreateuser(Long createuser) {
		this.createuser = createuser;
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

	public String getOrdermsg() {
		return ordermsg;
	}

	public void setOrdermsg(String ordermsg) {
		this.ordermsg = ordermsg;
	}
	
}
