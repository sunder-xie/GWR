package com.gewara.model.partner;

import java.io.Serializable;

public class BindUnionpayMember implements Serializable{
	//TODO:����
	private static final long serialVersionUID = -4363300428929625915L;
	private String _id;
	private Long memberId;
	private String usrState;//�û�״̬��0:Ԥע���û���1:��ʽע���ã�2:���֧���û�
	private String notifyType;// ֪ͨ���
	private String cardNo;//���ڿ��֧���û����󶨿�֮�󣬻Ὣ���Ž���md5�󷵻أ�������ͬ���ظ�ע��ʹ�ã��ظ���������Χ�������ƣ� 
	private String addTime;
	
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getUsrState() {
		return usrState;
	}
	public void setUsrState(String usrState) {
		this.usrState = usrState;
	}
	public String getNotifyType() {
		return notifyType;
	}
	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	

}
