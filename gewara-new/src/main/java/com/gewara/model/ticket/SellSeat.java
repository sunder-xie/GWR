package com.gewara.model.ticket;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.constant.ticket.SeatConstant;
import com.gewara.model.BaseObject;

/**
 * �ֽ�OpenSeat�����û��������Ϣ���������������仺��
 * @author acerge(acerge@163.com)
 * @since 8:56:18 PM Dec 30, 2010
 */
public class SellSeat extends BaseObject{
	private static final long serialVersionUID = -8412679066637297954L;
	private Long id;				//��OpenSeat����Id
	private Integer version;		//�汾
	private Long mpid;				//��������
	private Long orderid;			//������
	private String seatline;		//��λ�к�
	private String seatrank;		//��λ�к�
	private Timestamp validtime;
	private Integer price;
	private String status;			//״̬
	private String remark;
	public SellSeat(){}
	public SellSeat(OpenSeat oseat, Integer price, Timestamp validtime){
		this.version = 0;
		this.status = SeatConstant.STATUS_NEW;
		this.validtime = validtime;
		this.copyFrom(oseat, price);
	}
	public SellSeat(String seatline, String seatrank, Long mpid){
		this.version = 0;
		this.status = SeatConstant.STATUS_NEW;
		this.mpid = mpid;
		this.seatline = seatline;
		this.seatrank = seatrank;
	}
	public void copyFrom(OpenSeat oseat, Integer sprice) {
		this.id = oseat.getId();
		this.mpid = oseat.getMpid();
		this.seatline = oseat.getSeatline();
		this.seatrank = oseat.getSeatrank();
		this.price = sprice;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Timestamp getValidtime() {
		return validtime;
	}
	public void setValidtime(Timestamp validtime) {
		this.validtime = validtime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Long getMpid() {
		return mpid;
	}
	public void setMpid(Long mpid) {
		this.mpid = mpid;
	}
	public String getSeatrank() {
		return seatrank;
	}
	public void setSeatrank(String seatrank) {
		this.seatrank = seatrank;
	}
	public String getSeatline() {
		return seatline;
	}
	public void setSeatline(String seatline) {
		this.seatline = seatline;
	}
	public boolean isAvailable(Timestamp cur) {
		return status.equals(SeatConstant.STATUS_NEW) && validtime.before(cur);
	}
	public boolean isAvailableBy(Long sorderid, Timestamp cur){
		return status.equals(SeatConstant.STATUS_NEW) && validtime.before(cur) || sorderid.equals(this.orderid);
	}
	public boolean isSold(){
		return SeatConstant.STATUS_SOLD.equals(status);
	}
	public String getKey(){
		return this.seatline+":"+this.seatrank;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public String getSeatLabel(){
		return seatline+"��"+seatrank+"��";
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getOrderid() {
		return orderid;
	}
	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}
}
