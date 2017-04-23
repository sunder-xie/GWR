package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.ChargeConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;
import com.gewara.util.StringUtil;

/**
 * @author acerge(acerge@163.com)
 * @since 6:31:46 PM Aug 13, 2009
 */
public class Charge extends BaseObject{
	private static final long serialVersionUID = 4914995483381697551L;
	public static final List<String> canInvoiceList = Arrays.asList(PaymethodConstant.PAYMETHOD_ALIPAY, PaymethodConstant.PAYMETHOD_PNRPAY, 
			PaymethodConstant.PAYMETHOD_LAKALA, PaymethodConstant.PAYMETHOD_TELECOM);
	//����״̬
	public static final String STATUS_NEW = "new";						//�¶���
	public static final String STATUS_WAITPAY = "new_wait";			//�¶���,�ȴ�����
	public static final String STATUS_PAID = "paid_success";			//�����
	public static final String STATUS_CANCEL = "cancel";				//����ȡ����
	private Integer version;		//���°汾
	private Long id;				//ID
	private String tradeNo;			//������
	private Timestamp addtime;		//����ʱ��
	private Timestamp updatetime;	//�޸�ʱ��
	private Timestamp validtime;	//��Ч����	
	private String status;			//����״̬
	private Long memberid;			//�����û�
	private String membername;		//�û���
	private String paymethod;		//֧������:�Ա����㸶
	private String paybank;			//֧������
	private String payseqno;		//�ⲿ���׺�
	private Integer totalfee;    	//��ֵ���
	private String chargetype;		//��ֵ����:��ֵ �� Ϊ���������ֵ
	private Long outorderid;		//��������id
	private String chargeto;		//��ֵ��ʽ bank, wabi
	
	private String gatewayCode;//֧�����ش���	
	private String merchantCode;//�̻��ű�ʶ
	
	public Charge(){
		this.version = 0;
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
	
	public Charge(String tradeNo, String chargeto){
		this.tradeNo = tradeNo;
		this.status = STATUS_NEW;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.updatetime = this.addtime;
		this.validtime = this.addtime;
		this.chargetype = ChargeConstant.TYPE_CHARGE;
		this.chargeto = chargeto;
		this.version = 0;
	}
	public String getChargetype() {
		return chargetype;
	}

	public void setChargetype(String chargetype) {
		this.chargetype = chargetype;
	}
	/**
	 * @return �����Ľ��
	 */
	public int getFee(){
		return totalfee;
	}
	public boolean isNew(){
		return status.startsWith(STATUS_NEW);
	}
	public boolean isPaid(){
		return status.equals(STATUS_PAID);
	}
	public boolean isCanInvoice(){
		return status.equals(STATUS_PAID) && StringUtils.equals(chargeto, ChargeConstant.WABIPAY) && canInvoiceList.contains(paymethod);
	}
	public String getStatusText(){
		if(isNew()) return "�ȴ�����";
		if(isPaid()) return "��ֵ�ɹ�";
		if(isCancel()) return "ȡ��";
		return "������";
	}
	public String getPaytext(){
		String result = PaymethodConstant.getPaymethodText(paymethod);
		if(PaymethodConstant.PAYMETHOD_CHARGECARD.equals(paymethod)) result="�����"+ payseqno + "����";
		return result;
	}
	public boolean isCancel() {
		return status.startsWith(STATUS_CANCEL);
	}
	public String getDescription() {
		return "Gewara��վ�˻���ֵ��������վ��֧��";
	}
	public int getTotalfee() {
		return totalfee;
	}
	public void setTotalfee(Integer totalfee) {
		this.totalfee = totalfee;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public String getPaymethod() {
		return paymethod;
	}
	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}
	public String getOrdertitle(){
		return "Gewara�˻���ֵ" + "*�����ţ�" + tradeNo;
	}
	public String getPayseqno() {
		return payseqno;
	}
	public void setPayseqno(String payseqno) {
		this.payseqno = payseqno;
	}
	public String getPaybank() {
		return paybank;
	}
	public void setPaybank(String paybank) {
		this.paybank = paybank;
	}
	public String getMembername() {
		return membername;
	}
	public void setMembername(String membername) {
		this.membername = membername;
	}
	public String getSplitTradeno(){
		return StringUtil.getSplitString(this.tradeNo, " ", 4);
	}
	public Long getOutorderid() {
		return outorderid;
	}

	public void setOutorderid(Long outorderid) {
		this.outorderid = outorderid;
	}
	public Timestamp getValidtime() {
		return validtime;
	}
	public void setValidtime(Timestamp validtime) {
		this.validtime = validtime;
	}
	public String getChargeto() {
		return chargeto;
	}
	public void setChargeto(String chargeto) {
		this.chargeto = chargeto;
	}
	
	public boolean hasChargeto(String charge){
		if(StringUtils.isBlank(charge)){
			return false;
		}
		return StringUtils.equals(this.chargeto, charge);
	}
	
	public boolean isOvertime(){
		if(validtime==null) return false;
		return validtime.before(DateUtil.getMillTimestamp());
	}
	
	public boolean hasValid(){
		return StringUtils.startsWith(this.status, STATUS_NEW) && isOvertime(); 
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
}
