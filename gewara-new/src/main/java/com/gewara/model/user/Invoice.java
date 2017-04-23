package com.gewara.model.user;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.InvoiceConstant;
import com.gewara.model.BaseObject;

public class Invoice extends BaseObject {
	private static final long serialVersionUID = 3884062931033192928L;
	private Long id;
	private String title;//��Ʊ̧ͷ
	private Long memberid;//�û�id
	private Long adminid;//��˹���Աid
	private String address;//�ʼĵ�ַ
	private Integer amount;//��Ʊ���
	private String phone;//�绰
	private String postcode;//��������
	private String contactor;//�ռ���
	private String invoicestatus;//��Ʊ��ȡ״̬
	private Timestamp addtime;//����ʱ��
	private Timestamp opentime;//��Ʊʱ��
	private String invoicecontent;//��ע
	private String invoicetype;//����
	private String relatedid;//������
	private String postnumber;//�ʼĺ�
	private String citycode;//��Ʊ�Ĺ�����
	//20110903
	private Timestamp posttime;//�ʼ�ʱ��
	private String applytype;//��������
	
	private String pretype;			//������orderExtra�� pretypeһ��
	
	public Invoice() {}
	
	public Invoice(Long memberid){
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.invoicestatus = InvoiceConstant.STATUS_APPLY;
		this.memberid = memberid;
	}
	public Invoice(Long memberid, String address, Integer amount, String title, String phone, 
			String postcode, String contactor, String invoicetype, String orderid){
		this(memberid);
		this.address = address;
		this.amount = amount;
		this.title = title;
		this.phone = phone;
		this.postcode = postcode;
		this.contactor = contactor;
		this.invoicetype = invoicetype;
		this.relatedid = orderid;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getContactor() {
		return contactor;
	}

	public void setContactor(String contactor) {
		this.contactor = contactor;
	}

	public Timestamp getAddtime() {
		return addtime;
	}

	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	public String getInvoicestatus() {
		return invoicestatus;
	}
	public void setInvoicestatus(String invoicestatus) {
		this.invoicestatus = invoicestatus;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Long getAdminid() {
		return adminid;
	}
	public void setAdminid(Long adminid) {
		this.adminid = adminid;
	}
	public String getInvoicecontent() {
		return invoicecontent;
	}
	public void setInvoicecontent(String invoicecontent) {
		this.invoicecontent = invoicecontent;
	}
	public String getRelatedid() {
		return relatedid;
	}
	public void setRelatedid(String relatedid) {
		this.relatedid = relatedid;
	}
	public String getPostnumber() {
		return postnumber;
	}
	public void setPostnumber(String postnumber) {
		this.postnumber = postnumber;
	}
	public String getStatusText(){
		return InvoiceConstant.STATUSDESC_MAP.get(invoicestatus);
	}
	public String getEnmobile(){
		if(StringUtils.length(phone)<=4) return phone;
		else return "*******" + phone.substring(phone.length()-4);
	}
	public String getCitycode() {
		return citycode;
	}
	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
	public Timestamp getPosttime() {
		return posttime;
	}
	public void setPosttime(Timestamp posttime) {
		this.posttime = posttime;
	}
	public String getApplytype() {
		return applytype;
	}
	public void setApplytype(String applytype) {
		this.applytype = applytype;
	}

	public String getInvoicetype() {
		return invoicetype;
	}
	public void setInvoicetype(String invoicetype) {
		this.invoicetype = invoicetype;
	}
	public Timestamp getOpentime() {
		return opentime;
	}
	public void setOpentime(Timestamp opentime) {
		this.opentime = opentime;
	}

	public String getPretype() {
		return pretype;
	}

	public void setPretype(String pretype) {
		this.pretype = pretype;
	}
	
}
