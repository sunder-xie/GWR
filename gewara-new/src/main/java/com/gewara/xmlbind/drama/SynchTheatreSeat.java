package com.gewara.xmlbind.drama;

import java.sql.Timestamp;

public class SynchTheatreSeat {

	private Long gewaid;
	private Long busid;
	private Integer lineno;			//ǰ��ڼ���,ϵͳ����
	private Integer rankno;			//����ڼ���,ϵͳ����
	private String seatline;		//��λ�к�
	private String seatrank;		//��λ�к�
	private String loveInd;			//������
	private String seattype;		//�۸�����
	private Long gewaodiid;				//��������
	private Integer price;			//�۸�
	private String status;			//״̬
	private String remark;			//��ע
	private Integer costprice;		//�ɱ���
	private Integer theatreprice;	//��Ժ��
	private Timestamp updatetime; //����ʱ��
	
	public Long getGewaid() {
		return gewaid;
	}
	public void setGewaid(Long gewaid) {
		this.gewaid = gewaid;
	}
	public Long getBusid(){
		return busid;
	}
	public void setBusid(Long busid){
		this.busid = busid;
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
	public String getLoveInd() {
		return loveInd;
	}
	public void setLoveInd(String loveInd) {
		this.loveInd = loveInd;
	}
	public String getSeattype() {
		return seattype;
	}
	public void setSeattype(String seattype) {
		this.seattype = seattype;
	}
	public Long getGewaodiid() {
		return gewaodiid;
	}
	public void setGewaodiid(Long gewaodiid) {
		this.gewaodiid = gewaodiid;
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
	public Timestamp getUpdatetime(){
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime){
		this.updatetime = updatetime;
	}
}
