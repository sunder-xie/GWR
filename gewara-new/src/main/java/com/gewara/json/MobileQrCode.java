package com.gewara.json;

import java.io.Serializable;
import java.util.Date;
/**
 * �ֻ��ͻ�������
 * @author liushusong
 *
 */

public class MobileQrCode implements Serializable{
	private static final long serialVersionUID = 9120829955266984166L;
	private String id;
	private String title;//����
	private String flag;//����
	private String url;//���ӵ�ַ
	private Integer width;//��ά��ͼƬ���
	private Integer height;//��ά��ͼƬ�߶�
	private String qrCodePath;//��ά��ͼƬ��ַ
	private String waterPath;//ˮӡͼƬ��ַ
	private Date addTime;
	public MobileQrCode(){}
	public MobileQrCode(String title,String flag,String url){
		this.title = title;
		this.flag = flag;
		this.url = url;
		this.addTime = new Date();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public String getQrCodePath() {
		return qrCodePath;
	}
	public void setQrCodePath(String qrCodePath) {
		this.qrCodePath = qrCodePath;
	}
	public String getWaterPath() {
		return waterPath;
	}
	public void setWaterPath(String waterPath) {
		this.waterPath = waterPath;
	}
}
