package com.gewara.model.drama;

import java.io.Serializable;

import com.gewara.constant.TheatreSeatConstant;
import com.gewara.model.BaseObject;

public class OpenTheatreSeat extends BaseObject {
	private static final long serialVersionUID = -473963118593047026L;

	private Long id;				
	private Long odiid;				//��������
	private Long dpid;				//
	private Long areaid;			//���������ַ
	private Integer price;			//�۸�
	private Integer theatreprice;	//��Ժ��
	private Integer costprice;		//�ɱ���
	private String status;			//״̬
	private String remark;			//��ע
	//��λ��Ϣ
	private Integer lineno;			//ǰ��ڼ���,ϵͳ����
	private Integer rankno;			//����ڼ���,ϵͳ����
	private String seatline;		//��λ�к�
	private String seatrank;		//��λ�к�
	private String seattype;		//�۸�����
	private String loveInd;			//������
	
	@Override
	public Serializable realId() {
		return id;
	}
	
	public String getLoveInd() {
		return loveInd;
	}
	public void setLoveInd(String loveInd) {
		this.loveInd = loveInd;
	}
	public OpenTheatreSeat(){
	}
	
	public OpenTheatreSeat(TheatreRoomSeat seat, Long dpid, TheatreSeatArea area, TheatreSeatPrice seatPrice){
		this.lineno = seat.getLineno();
		this.rankno = seat.getRankno();
		this.seatline = seat.getSeatline();
		this.seatrank = seat.getSeatrank();
		this.seattype = seatPrice.getSeattype();
		this.odiid = dpid;
		this.dpid = dpid;
		this.status = TheatreSeatConstant.STATUS_NEW;
		this.price = seatPrice.getPrice();
		this.costprice = seatPrice.getCostprice();
		this.theatreprice = seatPrice.getTheatreprice();
		this.loveInd = seat.getLoveInd();
		this.areaid = area.getId();
	}
	
	public String getSeatLabel(){
		return seatline+"��"+seatrank+"��";
	}
	public boolean isAvailable() {
		return status.equals(TheatreSeatConstant.STATUS_NEW);
	}
	public String getKey(){
		return this.seatline+":"+this.seatrank;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOdiid() {
		return odiid;
	}
	public void setOdiid(Long odiid) {
		this.odiid = odiid;
	}
	public Long getDpid() {
		return dpid;
	}
	public void setDpid(Long dpid) {
		this.dpid = dpid;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getLineno() {
		return lineno;
	}
	public void setLineno(Integer lineno) {
		this.lineno = lineno;
	}
	public Integer getRankno() {
		return rankno;
	}
	public void setRankno(Integer rankno) {
		this.rankno = rankno;
	}
	public String getSeatline() {
		return seatline;
	}
	public void setSeatline(String seatline) {
		this.seatline = seatline;
	}
	public String getSeatrank() {
		return seatrank;
	}
	public void setSeatrank(String seatrank) {
		this.seatrank = seatrank;
	}
	public String getSeattype() {
		return seattype;
	}
	public void setSeattype(String seattype) {
		this.seattype = seattype;
	}
	public boolean isLocked() {
		return "BCD".contains(status);
	}
	public Integer getCostprice() {
		return costprice;
	}
	public void setCostprice(Integer costprice) {
		this.costprice = costprice;
	}
	public Integer getTheatreprice() {
		return theatreprice;
	}
	public void setTheatreprice(Integer theatreprice) {
		this.theatreprice = theatreprice;
	}
	public String getPosition(){
		return this.lineno+":" + this.rankno; 
	}
	public Long getAreaid() {
		return areaid;
	}
	public void setAreaid(Long areaid) {
		this.areaid = areaid;
	}
}
