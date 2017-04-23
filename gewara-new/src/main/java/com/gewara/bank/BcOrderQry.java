/**
 * 
 */
package com.gewara.bank;

import org.apache.commons.lang.StringUtils;

/**
 * @author Administrator
 *
 */
public class BcOrderQry {
	private String order;		//������
	private String orderDate;	//��������
	private String orderTime;	//��������
	private String amount;		//���
	private String tranDate;	//֧������
	private String tranTime;	//֧��ʱ��
	private String tranState;	//֧������״̬ 1[�ɹ�]
	private String orderState;	//����״̬[0δ֧����1��֧����2�ѳ�����3�Ѳ����˻���4�˻������С�5��ȫ���˻�]
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTranDate() {
		return tranDate;
	}
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}
	public String getTranTime() {
		return tranTime;
	}
	public void setTranTime(String tranTime) {
		this.tranTime = tranTime;
	}
	public String getTranState() {
		return tranState;
	}
	public void setTranState(String tranState) {
		this.tranState = tranState;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public boolean isPaid(){
		return StringUtils.equals(tranState, "1");
	}
}
