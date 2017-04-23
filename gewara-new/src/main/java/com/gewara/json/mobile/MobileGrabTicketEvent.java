package com.gewara.json.mobile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.gewara.constant.AdminCityContant;
import com.gewara.util.DateUtil;

/**
 * �ֻ��ͻ�����Ʊ�
 * 
 * @author taiqichao
 * 
 */
public class MobileGrabTicketEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String GRAB_STATUS_O = "open";// ����
	public static String GRAB_STATUS_C = "close";// �ر�
	public static String GRAB_STATUS_BOOKED="booked";//��������
	
	private String id;
	private String title;// ��Ʊ�����
	private String status;// ״̬ open,close
	private String starttime;// ����ʱ��
	private String citycode;// ����
	private Integer price;// ��ɱ��
	private String addtime;// ���ʱ��
	private String updatetime;// ����޸�ʱ��

	public MobileGrabTicketEvent() {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getCitycodeList() {
		List<String> codes = new ArrayList<String>();
		if (StringUtils.isNotBlank(this.getCitycode())) {
			String[] citys = getCitycode().split(",");
			for (int i = 0; i < citys.length; i++) {
				codes.add(citys[i]);
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(codes);
		} catch (Exception e) {
			return "[]";
		}
	}
	
	public String getCityStr() {
		StringBuilder strBuilder=new StringBuilder();
		if (StringUtils.isNotBlank(this.getCitycode())) {
			String[] citys = getCitycode().split(",");
			for (int i = 0; i < citys.length; i++) {
				strBuilder.append(AdminCityContant.allcityMap.get(citys[i])+",");
			}
		}
		return StringUtils.removeEnd(strBuilder.toString(), ",");
	}

	public String getGrabStatus() {
		Date cur = DateUtil.getCurFullTimestamp();
		Date startTime = DateUtil.parseDate(this.starttime,
				"yyyy-MM-dd HH:mm:ss");
		if (this.status.equals(GRAB_STATUS_O)) {
			if (cur.before(startTime)) {
				return "δ��ʼ";
			} else {
				return "������";
			}
		} else if(this.status.equals(GRAB_STATUS_BOOKED)){
			return "��������";
		}else{
			return "�ѹر�";
		}
	}
	
	public Long getLeftms(){
		Date cur = DateUtil.getCurFullTimestamp();
		Date startTime = DateUtil.parseDate(this.starttime,"yyyy-MM-dd HH:mm:ss");
		Long mod=startTime.getTime()-cur.getTime();
		if(mod<0){
			mod=0L;
		}
		return mod;
	}
	

}
