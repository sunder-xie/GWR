package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.helper.order.GewaOrderHelper;
import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;

public abstract class GewaOrder extends BaseObject{
	private static final long serialVersionUID = 4914995483381697551L;
	public static final String RESTATUS_DELETE = "D";// ����ɾ��״̬
	//����״̬
	protected Long id;					//ID
	protected Integer version;			//���°汾
	protected String ordertitle;		//��������
	protected String tradeNo;			//������
	protected String mobile;			//��ϵ�ֻ�
	protected Timestamp createtime;		//�û��µ�ʱ��
	protected Timestamp addtime;		//����ʱ�䣺��һ�δ���ʱ�䡢��������ʱ�䡢��������ʱ��
	protected Timestamp updatetime;		//�û��޸�ʱ��
	protected Timestamp validtime;		//��Чʱ��
	protected Timestamp paidtime;		//����ʱ��
	protected Timestamp modifytime;		//������Ա�޸�
	protected Timestamp playtime;		//����ʱ��
	protected String status;			//����״̬
	protected Long memberid;			//�����û�
	protected Long partnerid;			//�����̼�
	protected String membername;		//�û���/��λ����
	protected String paymethod;			//֧������:վ���˻����Ա�������֧��
	protected String paybank;			//֧������
	protected String payseqno;			//�ⲿ������
	protected String description2;		//��Ʒ����
	protected Long clerkid;				//����������
	protected String remark;			//�ر�˵��
	protected Integer gewapaid;			//�˻����֧���Ľ��
	protected Integer alipaid;			//�Ա���㸶֧���Ľ��
	protected Integer wabi;				//�߱�����
	protected Integer totalcost;		//�ܳɱ���
	protected Integer totalfee;			//�����ܽ��
	protected Integer discount;			//�����Ż�
	protected String disreason;			//�Ż�����
	protected String changehis;			//������ʷ��¼
	protected Integer unitprice;		//����
	protected Integer quantity;			//����
	protected String ukey;				//��ʶPartner����Ψһ�û�
	protected String checkpass;			//ȡƱ����
	protected Integer itemfee;			//��������Ʒ�ܼ�
	protected String otherinfo;			//������Ϣ
	protected String citycode;			//���д���
	protected Integer otherfee;			//������
	protected String settle;			//�Ƿ�����������㣺Y��N
	protected String restatus;			//�Ƿ�ɾ��
	protected String pricategory;		//�������ࣨģ�飩
	protected String category;			//�������
	protected String otherFeeRemark;	//��������ϸ
	protected String express;
	
	private String gatewayCode;//֧�����ش���	
	private String merchantCode;//�̻��ű�ʶ
	
	@Override
	public Serializable realId() {
		return id;
	}
	
	public String getCitycode() {
		return citycode;
	}
	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
	public Integer getItemfee() {
		return itemfee;
	}
	public void setItemfee(Integer itemfee) {
		this.itemfee = itemfee;
	}
	public void setCheckpass(String checkpass) {
		this.checkpass = checkpass;
	}

	public Integer getUnitprice() {
		return unitprice;
	}
	public void setUnitprice(Integer unitprice) {
		this.unitprice = unitprice;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Timestamp getAddtime() {
		return addtime;
	}
	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Timestamp getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	public String getPricategory() {
		return pricategory;
	}

	public void setPricategory(String pricategory) {
		this.pricategory = pricategory;
	}

	public String getCheckpass() {
		return checkpass;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}
	public String getPaymethod() {
		return paymethod;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Timestamp getValidtime() {
		return validtime;
	}
	public void setValidtime(Timestamp validtime) {
		this.validtime = validtime;
	}
	public String getOrdertitle() {
		return ordertitle;
	}
	public void setOrdertitle(String ordertitle) {
		this.ordertitle = ordertitle;
	}
	public Long getClerkid() {
		return clerkid;
	}
	public void setClerkid(Long clerkid) {
		this.clerkid = clerkid;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getAlipaid() {
		return alipaid;
	}
	public void setAlipaid(Integer alipaid) {
		this.alipaid = alipaid;
	}

	public String getPaymethodText(){
		return PaymethodConstant.getPaymethodText(paymethod);
	}
	public Integer getGewapaid() {
		return gewapaid;
	}
	public void setGewapaid(Integer gewapaid) {
		this.gewapaid = gewapaid;
	}
	
	public Timestamp getPaidtime() {
		return paidtime;
	}
	public void setPaidtime(Timestamp paidtime) {
		this.paidtime = paidtime;
	}
	public Timestamp getModifytime() {
		return modifytime;
	}
	public void setModifytime(Timestamp modifytime) {
		this.modifytime = modifytime;
	}
	public boolean isNetPaid(){
		return alipaid > 0;
	}
	//Ӧ����
	public Integer getDue(){
		return totalfee + itemfee  + otherfee  - discount < 0? 0:totalfee + itemfee + otherfee - discount;
	}
	public Integer getTotalAmount(){
		return totalfee + itemfee + otherfee;
	}
	public String getPaybank() {
		return paybank;
	}
	public void setPaybank(String paybank) {
		this.paybank = paybank;
	}
	public abstract String getOrdertype();
	public String getPayseqno() {
		return payseqno;
	}
	public void setPayseqno(String payseqno) {
		this.payseqno = payseqno;
	}
	public Integer getDiscount() {
		return discount;
	}
	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
	public String getChangehis() {
		return changehis;
	}
	public void setChangehis(String changehis) {
		this.changehis = changehis;
	}
	public void addChangehis(String name, String change) {
		String result = JsonUtils.addJsonKeyValue(changehis, name, change);
		this.changehis = result;
	}
	public Integer getTotalfee() {
		return totalfee;
	}
	public void setTotalfee(Integer totalfee) {
		this.totalfee = totalfee;
	}
	public String getDisreason() {
		return disreason;
	}
	public void setDisreason(String disreason) {
		this.disreason = disreason;
	}
	public String getMembername() {
		return membername;
	}
	public void setMembername(String membername) {
		this.membername = membername;
	}
	public String getUkey() {
		return ukey;
	}
	public void setUkey(String ukey) {
		this.ukey = ukey;
	}
	public Long getPartnerid() {
		return partnerid;
	}
	public Integer getTotalcost() {
		return totalcost;
	}
	public void setTotalcost(Integer totalcost) {
		this.totalcost = totalcost;
	}
	public void setPartnerid(Long partnerid) {
		this.partnerid = partnerid;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
	public String getDescription2() {
		return description2;
	}
	public void setDescription2(String description2) {
		this.description2 = description2;
	}
	public Timestamp getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}
	public Integer getOtherfee() {
		return otherfee;
	}
	public void setOtherfee(Integer otherfee) {
		this.otherfee = otherfee;
	}

	public String getSettle() {
		return settle;
	}

	public void setSettle(String settle) {
		this.settle = settle;
	}

	public Integer getWabi() {
		return wabi;
	}

	public void setWabi(Integer wabi) {
		this.wabi = wabi;
	}
	public String getRestatus() {
		return restatus;
	}

	public void setRestatus(String restatus) {
		this.restatus = restatus;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getOtherFeeRemark() {
		return otherFeeRemark;
	}

	public void setOtherFeeRemark(String otherFeeRemark) {
		this.otherFeeRemark = otherFeeRemark;
	}

	public Timestamp getPlaytime() {
		return playtime;
	}

	public void setPlaytime(Timestamp playtime) {
		this.playtime = playtime;
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public String getGatewayCode() {
		return gatewayCode;
	}

	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public String getStatusText(){
		return OrderConstant.statusMap.get(getFullStatus());
	}
	public String getFullStatus(){
		if(status.startsWith(OrderConstant.STATUS_NEW) && isTimeout()) return OrderConstant.STATUS_TIMEOUT;
		return status;
	}
	public String getStatusText2(){//��ʾ���û���
		if(isCancel()) return "��ʱȡ��";
		if(isPaidSuccess()) return "���׳ɹ�";
		if(StringUtils.equals(status, OrderConstant.STATUS_PAID_RETURN)) return "���˿�";
		if(isAllPaid()) return "��֧��";
		return OrderConstant.statusMap.get(status);
	}
	public boolean isNew(){
		return status.startsWith(OrderConstant.STATUS_NEW) && !isTimeout();
	}
	public boolean isPaidFailure(){
		return OrderConstant.STATUS_PAID_FAILURE.equals(status) && (getDue() - gewapaid - alipaid <= 0);
	}
	public boolean isPaidUnfix(){
		return (OrderConstant.STATUS_PAID_UNFIX.equals(status)) && (getDue() - gewapaid - alipaid <= 0);
	}
	public boolean isPaidSuccess(){
		return OrderConstant.STATUS_PAID_SUCCESS.equals(status) && (getDue() - gewapaid - alipaid <= 0);
	}
	public boolean isAllPaid(){
		return StringUtils.startsWith(status, OrderConstant.STATUS_PAID) && (getDue() - gewapaid - alipaid <= 0) ;
	}
	public boolean isNotAllPaid(){
		return StringUtils.startsWith(status, OrderConstant.STATUS_PAID) && (getDue() - gewapaid - alipaid > 0) ;
	}
	public Integer getRealPaid(){
		return gewapaid+alipaid;
	}
	/**
	 * ���õ���ȯֱ��֧�������ý��ɳ����������
	 * @return
	 */
	public boolean isZeroPay() {
		return getDue()<= 0 && discount > 0;
	}
	public boolean isCancel(){
		return StringUtils.startsWith(status, OrderConstant.STATUS_CANCEL) || 
				StringUtils.startsWith(status, OrderConstant.STATUS_NEW) && isTimeout();
	}
	public boolean isTimeout(){
		return validtime!=null && validtime.before(new Timestamp(System.currentTimeMillis()));
	}
	/**
	 * ���Դ���
	 * @return
	 */
	public boolean canProcess(){
		return this.updatetime.before(DateUtil.addMinute(new Timestamp(System.currentTimeMillis()), -3));
	}
	public boolean isTimeoutCancel(){
		return isTimeout() && status.equals(OrderConstant.STATUS_NEW);
	}
	public Integer gainInvoiceDue(){
		Integer due = this.getAlipaid() + this.getGewapaid() - this.getWabi();
		return due;
	}
	public String gainUkey(){
		return GewaOrderHelper.getPartnerUkey(this);
	}
	public Integer gainRealUnitprice(){
		return totalfee/quantity;
	}
	//TODO:ҵ�񷽷���д��ȡ��Ӳ����
	public boolean surePartner(){
		return this.partnerid>1;
	}
	public boolean sureOutPartner(){//�ⲿ�̼�
		return this.memberid > PartnerConstant.MAX_MEMBERID;
	}
	public boolean sureGewaPartner(){//�ڲ�WAP,IPHONE...
		return this.partnerid>1 && this.memberid < PartnerConstant.MAX_MEMBERID;
	}

}
