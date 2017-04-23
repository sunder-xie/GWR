/**
 * 
 */
package com.gewara.pay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.gewara.constant.ticket.OpiConstant;

/**
 * @author Administrator
 *
 */
public class PayValidHelper {
	private boolean nonesetmethod = true;
	private boolean nonesetcard = true;
	private List<String> paymethodList = new ArrayList<String>();//�������غ�
	private List<String> partPaymethodList = new ArrayList<String>();//���������غ�
	private List<String> batchidList = new ArrayList<String>();
	private String defaultpaymethod;
	private String payoption;
	private String cardoption;
	private List<String> limitPayList = new ArrayList<String>();
	public String getPayoption() {
		return payoption;
	}
	public void setPayoption(String payoption) {
		this.payoption = payoption;
	}
	public String getCardoption() {
		return cardoption;
	}
	public void setCardoption(String cardoption) {
		this.cardoption = cardoption;
	}

	public List<String> getBatchidList() {
		return batchidList;
	}
	public void setBatchidList(List<String> batchidList) {
		this.batchidList = batchidList;
	}
	public PayValidHelper(){}
	
	public PayValidHelper(String multiPaymethods){
		nonesetmethod = false;
		payoption = "canuse";
		paymethodList.addAll(Arrays.asList(StringUtils.split(multiPaymethods, ",")));
		if(StringUtils.contains(multiPaymethods, ":")){
			for(String pay: paymethodList){
				partPaymethodList.add(StringUtils.contains(pay, ":")?StringUtils.split(pay, ":")[0]:pay);
			}
		}else{
			partPaymethodList.addAll(paymethodList);
		}
	}
	public PayValidHelper(Map<String, String> paySetting){
		if(paySetting.containsKey(OpiConstant.PAYOPTION)){ 
			String mtl = paySetting.get(OpiConstant.PAYCMETHODLIST);
			if(StringUtils.isNotBlank(mtl)) {
				paymethodList = Arrays.asList(mtl.split(","));
				if(StringUtils.contains(mtl, ":")){
					for(String pay: paymethodList){
						partPaymethodList.add(StringUtils.contains(pay, ":")?StringUtils.split(pay, ":")[0]:pay);
					}
				}else{
					partPaymethodList.addAll(paymethodList);
				}
				defaultpaymethod = paySetting.get(OpiConstant.DEFAULTPAYMETHOD);
			}
			payoption = paySetting.get(OpiConstant.PAYOPTION);
			nonesetmethod = false;
		}
		if(paySetting.containsKey(OpiConstant.CARDOPTION)){ 
			batchidList= Arrays.asList(paySetting.get(OpiConstant.BATCHIDLIST).split(","));
			cardoption = paySetting.get(OpiConstant.CARDOPTION);
			nonesetcard = false;
		}
	}
	public boolean denyAll(){
		if(nonesetmethod) return false;
		return StringUtils.equals("canuse", payoption) && paymethodList.isEmpty();
	}
	//ȯ������֤
	public boolean supportCard(Long batchid){
		if(nonesetcard) return true;
		if(StringUtils.equals("canuse", cardoption)){
			return batchidList.contains(batchid+"");
		}else if(StringUtils.equals("notuse", cardoption)){
			return !batchidList.contains(batchid+"");
		}
		return true;
	}
	
	public String getDefaultpaymethod() {
		return defaultpaymethod;
	}
	public void setDefaultPaymethod(String defaultpaymethod) {
		this.defaultpaymethod = defaultpaymethod;
	}
	public String getDefMethod(String method) {
		if(supportPaymethod(method)) return method;
		return defaultpaymethod;
	}
	public boolean isNonesetmethod() {
		return nonesetmethod;
	}
	public void setNonesetmethod(boolean nonesetmethod) {
		this.nonesetmethod = nonesetmethod;
	}
	public boolean isNonesetcard() {
		return nonesetcard;
	}
	public void setNonesetcard(boolean nonesetcard) {
		this.nonesetcard = nonesetcard;
	}
	/**
	 * ֧����ʽ��֤������ȷ�����غţ�
	 * @param paymethods�����ŷָ��������غ�
	 * @return
	 */
	public boolean supportAny(String paymethods){
		if(nonesetmethod) return true;
		if(denyAll()) return false;
		if(StringUtils.equals("canuse", payoption)){
			return CollectionUtils.containsAny(partPaymethodList, Arrays.asList(paymethods.split(",")));
		}else if(StringUtils.equals("notuse", payoption)){//���ܰ��� ���غ�
			List<String> support = new ArrayList<String>(Arrays.asList(paymethods.split(",")));
			support.removeAll(partPaymethodList);
			return !support.isEmpty();
		}
		return true;
	}
	public boolean supportPaymethod(String fullPaymethod){
		if(limitPayList!=null) {
			boolean islimit = limitPayList.contains(fullPaymethod);
			if(islimit) return false;
		}
		if(nonesetmethod) return true;
		if(denyAll()) return false;
		String[] pair = StringUtils.split(fullPaymethod, ":");
		if(StringUtils.equals("canuse", payoption)){
			if(paymethodList.contains(fullPaymethod)) return true;
			if(pair.length==2){
				return paymethodList.contains(pair[0]);
			}
			return false;
		}else if(StringUtils.equals("notuse", payoption)){
			if(paymethodList.contains(fullPaymethod)) return false;
			if(pair.length==2){
				return !paymethodList.contains(pair[0]);
			}
			return true;
		}
		return true;
	}
	/**
	 * ֧����ʽ�Ƿ���ݣ���src=pnrPay��dest=pnrPay:10�����
	 * @param src
	 * @param dest
	 * @return
	 */
	public static boolean isCompatible(String src, String dest) {
		return StringUtils.equals(src, dest)|| StringUtils.startsWith(dest, src);
	}
	public List<String> getPaymethodList() {
		return paymethodList;
	}
	
	public void setLimitPay(List<String> limitPayList) {
		this.limitPayList.addAll(limitPayList);
	}
	public List<String> getLimitPayList() {
		return limitPayList;
	}
}
