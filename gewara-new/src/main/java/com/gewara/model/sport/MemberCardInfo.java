package com.gewara.model.sport;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.MemberCardConstant;
import com.gewara.model.BaseObject;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;

public class MemberCardInfo extends BaseObject {
	private static final long serialVersionUID = 1137725062227169433L;
	private Long id;
	private Long typeid;
	private String cardTypeUkey;
	private Long memberid;
	private String memberCardCode;			//��Ա����
	private String name;
	private String sex;
	private String mobile;
	private Integer overMoney;				//���/ʣ�����
	private Timestamp validtime;			//��Ч��,��ֵ��ʾ������
	private String cardStatus;				//��״̬��1.���ã�2ͣ�ã�3����
	private String fitItem;					//������Ŀ ��Ϊ��
	private String belongVenue;				//��������/���ó��� �ο��������ݣ��� ���ó���
	private Timestamp addtime;				//����ʱ��
	private String tradeno;					//������
	private String typetitle;				//����������
	private String bindStatus;				//�Ƿ��
	
	public MemberCardInfo(){
		
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
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getMemberCardCode() {
		return memberCardCode;
	}
	public void setMemberCardCode(String memberCardCode) {
		this.memberCardCode = memberCardCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Integer getOverMoney() {
		return overMoney;
	}
	public void setOverMoney(Integer overMoney) {
		this.overMoney = overMoney;
	}
	
	public String getCardStatus() {
		return cardStatus;
	}
	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}
	public String getFitItem() {
		return fitItem;
	}
	public void setFitItem(String fitItem) {
		this.fitItem = fitItem;
	}
	public String getBelongVenue() {
		return belongVenue;
	}
	public void setBelongVenue(String belongVenue) {
		this.belongVenue = belongVenue;
	}
	public Timestamp getAddtime() {
		return addtime;
	}
	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	public Long getTypeid() {
		return typeid;
	}
	public void setTypeid(Long typeid) {
		this.typeid = typeid;
	}
	public Timestamp getValidtime() {
		return validtime;
	}
	public void setValidtime(Timestamp validtime) {
		this.validtime = validtime;
	}
	public String getCardTypeUkey() {
		return cardTypeUkey;
	}
	public void setCardTypeUkey(String cardTypeUkey) {
		this.cardTypeUkey = cardTypeUkey;
	}
	public String getStatusText(){
		return MemberCardConstant.cardstatusMap.get(cardStatus);
	}
	public boolean hasBooking(){
		return true;
	}
	public String getTradeno() {
		return tradeno;
	}
	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	public boolean hasAvailableByOtt(OpenTimeTable ott){
		if(!StringUtils.equals(cardStatus, MemberCardConstant.CARD_STATUS_Y)){
			return false;
		}
		if(StringUtils.isNotBlank(fitItem)){
			List<Long> itemList = BeanUtil.getIdList(fitItem, ",");
			if(!itemList.contains(ott.getItemid())){
				return false;
			}
		}
		List<Long> sportidList = BeanUtil.getIdList(belongVenue, ",");
		if(!sportidList.contains(ott.getSportid())){
			return false;
		}
		if(validtime!=null){
			if(validtime.before(DateUtil.getMillTimestamp())){
				return false;
			}
		}
		return true;
	}
	public String getTypetitle() {
		return typetitle;
	}
	public void setTypetitle(String typetitle) {
		this.typetitle = typetitle;
	}
	public String getBindStatus() {
		return bindStatus;
	}
	public void setBindStatus(String bindStatus) {
		this.bindStatus = bindStatus;
	}
}
