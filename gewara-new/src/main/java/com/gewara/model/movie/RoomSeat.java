package com.gewara.model.movie;
import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.BaseObject;

/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public class RoomSeat extends BaseObject implements Comparable<RoomSeat>{
	private static final long serialVersionUID = -4016785855588367848L;
	public static final String INITSTATUS_CLOSE = "C";
	public static final String INITSTATUS_OPEN = "O";
	private Long id;
	private Long roomid;		//Ӱ��ID
	private Integer lineno;		//ǰ��ڼ���,ϵͳ����
	private Integer rankno;		//����ڼ���,ϵͳ����
	private String seatline;	//��λ�к�
	private String seatrank;	//��λ�к�
	private String loveInd;		//������ 0����ͨ��λ 1������������λ��� 2���������ڶ���λ���
	private String initstatus;	//ÿ�ο��ų�ʼ״̬
	public RoomSeat() {
	}
	public RoomSeat(Long roomid, int lineno, int rankno){
		this.roomid = roomid;
		this.lineno = lineno;
		this.rankno = rankno;
		this.seatline = lineno + "";
		this.seatrank = rankno + "";
		this.loveInd = "0";
		this.initstatus = INITSTATUS_OPEN;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public Long getRoomid() {
		return roomid;
	}

	public void setRoomid(Long roomid) {
		this.roomid = roomid;
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

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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
	public String getSeatLabel(){
		return seatline+"��"+seatrank+"��";
	}
	@Override
	public int compareTo(RoomSeat o) {
		if(this.equals(o)) return 0;
		if(!roomid.equals(o.roomid)) return roomid.compareTo(o.roomid);
		return o.lineno*100 + o.rankno - lineno*100 - rankno;
	}
	public String getLoveInd() {
		return loveInd;
	}
	public void setLoveInd(String loveInd) {
		this.loveInd = loveInd;
	}
	public String getInitstatus() {
		return initstatus;
	}
	public void setInitstatus(String initstatus) {
		this.initstatus = initstatus;
	}
	public static boolean isValidStatus(String initstatus) {
		return StringUtils.contains("CO", initstatus);
	}
	public String getPosition(){
		return this.lineno+":" + this.rankno; 
	}
	public String getKey(){
		return this.seatline+":"+this.seatrank;
	}
}
