package com.gewara.model.express;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;
//���ͷ�ʽ
public class ExpressConfig extends BaseObject {

	private static final long serialVersionUID = -1612023130138521871L;
	private String id;
	private String name;			//���ͷ�ʽ����
	private String expresstype;		//��������		
	private Timestamp addtime;		//���ʱ��
	private Timestamp updatetime;	//����ʱ��
	private String remark;			//����
	
	public ExpressConfig(){}
	
	public ExpressConfig(String id, String name, String expresstype){
		this.id = id;
		this.name = name;
		this.expresstype = expresstype;
		this.addtime = DateUtil.getCurFullTimestamp();
		this.updatetime = this.addtime;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpresstype() {
		return expresstype;
	}

	public void setExpresstype(String expresstype) {
		this.expresstype = expresstype;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public Serializable realId() {
		return id;
	}

}
