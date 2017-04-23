/**
 * 
 */
package com.gewara.bank;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * @author Administrator
 *
 */
public class SpsdoOrderQry {
	public static final Map<String, String> STATUS_MAP = new HashMap<String, String>();
	//01-�ɹ� 02-ʧ�� 03-δ֪ 04-�˿��� 05-���˿�
	static {
		STATUS_MAP.put("01", "�ɹ�");
		STATUS_MAP.put("02", "ʧ��");
		STATUS_MAP.put("03", "δ֪");
		STATUS_MAP.put("04", "�˿���");
		STATUS_MAP.put("05", "���˿�");
	}
	private String code;
	private String message;
	private String orderNo;
	private String serialNo;
	private String orderAmount;
	private String payAmount;
	private String status;
	private String payTime;
	private String requestTime;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}
	public String getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}
	public boolean isPaid(){
		return StringUtils.equals(this.status, "01");
	}
}
