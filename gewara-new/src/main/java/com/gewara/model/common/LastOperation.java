package com.gewara.model.common;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;

/**
 * ������¼�û����һ��ĳ�ֲ�������ˮ��
 * @author gebiao(ge.biao@gewara.com)
 * @since Mar 5, 2013 2:53:41 PM
 */
public class LastOperation extends BaseObject{
	private static final long serialVersionUID = 4241005391113922079L;
	private String lastkey;			//�磺ticket + memberid��ʾ���һ����TicketOrder��
	private String tag;				//���� TICKET
	private String lastvalue;		//ֵ
	private Timestamp lasttime;		//���ʱ��
	private Timestamp validtime;	//��Чʱ��
	public LastOperation(){
	}
	public LastOperation(String lastkey, String lastvalue, Timestamp lasttime, Timestamp validtime, String tag) {
		this.lastkey = lastkey;
		this.lastvalue = lastvalue;
		this.lasttime = lasttime;
		this.validtime = validtime;
		this.tag = tag;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Timestamp getLasttime() {
		return lasttime;
	}
	public void setLasttime(Timestamp lasttime) {
		this.lasttime = lasttime;
	}
	public Timestamp getValidtime() {
		return validtime;
	}
	public void setValidtime(Timestamp validtime) {
		this.validtime = validtime;
	}
	@Override
	public Serializable realId() {
		return lastkey;
	}
	public String getLastvalue() {
		return lastvalue;
	}
	public void setLastvalue(String lastvalue) {
		this.lastvalue = lastvalue;
	}
	public String getLastkey() {
		return lastkey;
	}
	public void setLastkey(String lastkey) {
		this.lastkey = lastkey;
	}
	
}
