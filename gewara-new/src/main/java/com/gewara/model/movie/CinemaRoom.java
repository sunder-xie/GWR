package com.gewara.model.movie;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.ticket.OpiConstant;
import com.gewara.model.ticket.PlayRoom;
/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public class CinemaRoom extends PlayRoom implements Comparable<CinemaRoom>{
	private static final long serialVersionUID = -1894201195221566397L;
	private Long cinemaid;
	private Integer screenheight; 		//��Ļ��
	private Integer screenwidth; 		//��Ļ��
	private Integer allowsellnum;		//����������
	private String vipflag;				//�Ƿ�ΪVIP
	private String seatmap;				//��λͼ��λͼ��
	private String loveflag;			//�Ƿ���������
	private Date effectivedate;			//��λ��Ч����
	private String playtype;			//�������ͣ���ӳ3D��2D��IMAX
	private String roomDoor;         //Ӱ������
	private String otherinfo;
	private String characteristic;	//��ɫ������
	private String defaultEdition; //Ĭ�ϰ汾�����Ӣ�Ķ���,�ָ 

	public CinemaRoom(){}
	
	public CinemaRoom(Long cinemaId, String roomtype){
		this.cinemaid = cinemaId;
		this.roomtype = roomtype;
		this.num = "0";
		this.linenum = 0;
		this.ranknum = 0;
		this.seatnum = 0;
		this.vipflag = "N";
		this.loveflag = "Y";
		this.allowsellnum = 9999;
	}
	
	public String getCharacteristic() {
		return characteristic;
	}

	public void setCharacteristic(String characteristic) {
		this.characteristic = characteristic;
	}
	
	public Integer getScreenheight() {
		return screenheight;
	}

	public void setScreenheight(Integer screenheight) {
		this.screenheight = screenheight;
	}

	public Integer getScreenwidth() {
		return screenwidth;
	}

	public void setScreenwidth(Integer screenwidth) {
		this.screenwidth = screenwidth;
	}

	public Long getCinemaid() {
		return cinemaid;
	}
	public void setCinemaid(Long cinemaid) {
		this.cinemaid = cinemaid;
	}
	public String getVipflag() {
		return vipflag;
	}
	public void setVipflag(String vipflag) {
		this.vipflag = vipflag;
	}
	public Date getEffectivedate() {
		return effectivedate;
	}
	public void setEffectivedate(Date effectivedate) {
		this.effectivedate = effectivedate;
	}
	@Override
	public int compareTo(CinemaRoom o) {
		return StringUtils.leftPad(""+num, 3, '0').compareTo(StringUtils.leftPad(""+o.num, 3, '0'));
	}
	public String getSeatmap() {
		return seatmap;
	}
	public void setSeatmap(String seatmap) {
		this.seatmap = seatmap;
	}
	public String getLoveflag() {
		return loveflag;
	}
	public void setLoveflag(String loveflag) {
		this.loveflag = loveflag;
	}

	public Integer getAllowsellnum() {
		return allowsellnum;
	}

	public void setAllowsellnum(Integer allowsellnum) {
		this.allowsellnum = allowsellnum;
	}

	public String getPlaytype() {
		return playtype;
	}

	public void setPlaytype(String playtype) {
		this.playtype = playtype;
	}
	
	public boolean hasGewaRoom(){
		return StringUtils.equals(roomtype, OpiConstant.OPEN_GEWARA);
	}
	
	public boolean hasRemoteRoom(){
		return !hasGewaRoom();
	}

	public String getRoomDoor() {
		return roomDoor;
	}

	public void setRoomDoor(String roomDoor) {
		this.roomDoor = roomDoor;
	}

	public String getOtherinfo() {
		return otherinfo;
	}

	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}

	public String getDefaultEdition() {
		return defaultEdition;
	}

	public void setDefaultEdition(String defaultEdition) {
		this.defaultEdition = defaultEdition;
	}

}
