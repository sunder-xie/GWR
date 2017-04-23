package com.gewara.model.machine;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.constant.Status;
import com.gewara.model.BaseObject;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.goods.BaseGoods;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.util.DateUtil;
//������
public class Barcode extends BaseObject{
	private static final long serialVersionUID = -6748209192667235170L;
	public static final Integer BARCODE_MAXNUM = 1000;
	public static final Integer BARCODE_HANDMAXNUM = 50000;
	private Long id;
	private String barcode;			//������
	private String serialno;		//��ˮ��
	private Long relatedid;			//��������id
	private Long placeid;			//����id
	private Long itemid;			//��Ŀid
	private String tradeno;			//������
	private String status;			//״̬		//N:Ĭ������״̬ ,Y:��ͬ��, T:ȡƱ
	private String flag;			//��ʶ		
	private Timestamp validtime;	//��Чʱ��
	private Timestamp taketime;		//ȡƱʱ��
	private Timestamp updatetime;	//����ʱ��
	private Timestamp addtime;		//����ʱ��
	public Barcode(){
		
	}
	public Barcode(Long placeid){
		this.placeid = placeid;
		this.status = Status.N;
		this.flag = Status.Y;
		this.addtime = DateUtil.getMillTimestamp();
		this.updatetime = this.addtime;
	}
	public Barcode(OpenPlayItem opi){
		this.relatedid = opi.getMpid();
		this.placeid = opi.getCinemaid();
		this.itemid = opi.getMovieid();
		this.status = Status.N;
		this.flag = Status.Y;
		this.addtime = DateUtil.getMillTimestamp();
		this.updatetime = this.addtime;
		this.validtime = DateUtil.addHour(opi.getPlaytime(), 3);
	}
	public Barcode(OpenDramaItem odi){
		this.relatedid = odi.getDpid();
		this.placeid = odi.getTheatreid();
		this.itemid = odi.getDramaid();
		this.status = Status.N;
		this.flag = Status.Y;
		this.addtime = DateUtil.getMillTimestamp();
		this.updatetime = this.addtime;
		this.validtime = DateUtil.addHour(odi.getPlaytime(), 3);
	}
	
	public Barcode(BaseGoods goods){
		this.relatedid = goods.getId();
		this.placeid = goods.getRelatedid();
		this.status = Status.N;
		this.flag = Status.Y;
		this.addtime = DateUtil.getMillTimestamp();
		this.updatetime = this.addtime;
		this.validtime = DateUtil.addHour(goods.getTovalidtime(), 3);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRelatedid() {
		return relatedid;
	}
	public void setRelatedid(Long relatedid) {
		this.relatedid = relatedid;
	}
	public Long getPlaceid() {
		return placeid;
	}
	public void setPlaceid(Long placeid) {
		this.placeid = placeid;
	}
	public Long getItemid() {
		return itemid;
	}
	public void setItemid(Long itemid) {
		this.itemid = itemid;
	}
	public String getTradeno() {
		return tradeno;
	}
	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Timestamp getTaketime() {
		return taketime;
	}
	public void setTaketime(Timestamp taketime) {
		this.taketime = taketime;
	}
	public Timestamp getAddtime() {
		return addtime;
	}
	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public Timestamp getValidtime() {
		return validtime;
	}
	public void setValidtime(Timestamp validtime) {
		this.validtime = validtime;
	}
	public Timestamp getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getSerialno() {
		return serialno;
	}
	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}
}
