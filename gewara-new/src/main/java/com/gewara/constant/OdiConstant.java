package com.gewara.constant;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.gewara.model.drama.DramaPlayItem;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.util.DateUtil;

public abstract class OdiConstant {

	public static final String STATUS_BOOK = "Y"; // ����Ԥ��
	public static final String STATUS_NOBOOK = "N"; // ������Ԥ��
	public static final String STATUS_DISCARD = "D"; // ����
	
	public static final String ORDER_NEW = "orderNew";
	
	public static final List<String> STATUS_LIST = Arrays.asList(STATUS_BOOK, STATUS_NOBOOK, STATUS_DISCARD);
	
	public static final int CLOSE_MIN = 60; // ��ǰ�ر�ʱ��
	public static final String OPEN_TYPE_SEAT = "seat";
	public static final String OPEN_TYPE_PRICE = "price";
	
	public static final String TAKEMETHOD_QUPIAOJI = "A";	//�Զ�ȡƱ��
	public static final String TAKEMETHOD_KUAIDI = "E";		//���
	
	public static final String UNOPENGEWA = "unopengewa";					//���β��Ը���������
	public static final String UNSHOWGEWA = "unshowgewa";					//���β��Ը�������ʾ
	
	public static final String CHECK_THEATRE_PRICE = "price";
	public static final String CHECK_THEATRE_DISCOUNT = "discount";
	public static final List<String> CHECK_THEATRELIST = Arrays.asList(CHECK_THEATRE_PRICE, CHECK_THEATRE_DISCOUNT);
	
	public static List<String> SEATTYPE_LIST = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
	
	public static final double DEFAULT_DISCOUNT = 100.0;
	
	public static final int MAX_MINUTS_TICKETS = 15;
	
	public static final int MAX_BUY = 6;					//ÿ����๺Ʊ����
	public static final int ODI_MAX_BUY = 30;				//����ÿ����๺Ʊ����
	public static final int SEND_MSG_3H = 180;				//���Ѷ���ʱ��(����)
	
	//��λͼˢ��Ƶ��
	public static final int SECONDS_SHOW_SEAT = 900;		//��ʾ��λͼ��20����
	public static final int SECONDS_ADDORDER = 300;		//�µ���5����
	public static final int SECONDS_UPDATE_SEAT = 60;		//���£�1����
	public static final int SECONDS_FORCEUPDATE_SEAT = 10;		//���£�10��
	
	
	public static final String PARTNER_GEWA = "GEWA";		
	public static final String PARTNER_GPTBS = "GPTBS";		//�������ͣ���Ʊ��ϵͳ�Խ�
	
	public static final String DISQUANTITY_DITYPE_G = "G";	//�Ż����� G(���������Ż�)
	public static final String DISQUANTITY_DITYPE_P = "P";  //�Ż����� P(���췽�Ż�)
	
	public static final String PTYPE_P = "P";
	public static final String PTYPE_Q = "Q";
	
	public static final Map<String, String> partnerTextMap;
	public static final Map<String, String>	opentypeTextMap;
	static{
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put(PARTNER_GPTBS, "Ʊ��ϵͳ");
		tmp.put(PARTNER_GEWA, "������");
		partnerTextMap = MapUtils.unmodifiableMap(tmp);
		Map<String, String> openTextTmp = new HashMap<String, String>();
		openTextTmp.put(OPEN_TYPE_SEAT, "ѡ��");
		openTextTmp.put(OPEN_TYPE_PRICE, "�۸�");
		opentypeTextMap = MapUtils.unmodifiableMap(openTextTmp);
	}
	
	
	public static Timestamp getFullPlaytime(Date playdate, String playtime){
		if(playdate == null || StringUtils.isBlank(playtime)) return null;
		String playdatestr = DateUtil.formatDate(playdate);
		String playtimestr = playdatestr + " " + playtime + ":00";
		return DateUtil.parseTimestamp(playtimestr);
	}
	
	
	public static String getDifferent(OpenDramaItem odi, DramaPlayItem dpi){
		String msg = "";
		if(!odi.getDramaid().equals(dpi.getDramaid())){
			msg += ",movie:" + dpi.getDramaid() + "--->" + dpi.getDramaid(); 
		}
		if(!odi.getRoomid().equals(dpi.getRoomid())){
			msg += ",room:" + odi.getRoomid() + "--->" + dpi.getRoomid(); 
		}
		if(!StringUtils.equals(odi.getRoomname(), dpi.getRoomname())){
			msg += ",roomname:" + odi.getRoomname() + "--->" + dpi.getRoomname(); 
		}
		if(!odi.getPlaytime().equals(dpi.getPlaytime())){
			msg += ",playtime:" + DateUtil.formatTimestamp(odi.getPlaytime()) + "--->" + DateUtil.formatTimestamp(dpi.getPlaytime()); 
		}
		if(!odi.getEndtime().equals(dpi.getEndtime())){
			msg += ",endtime:" + DateUtil.formatTimestamp(odi.getEndtime()) + "--->" + DateUtil.formatTimestamp(dpi.getEndtime()); 
		}
		if(!StringUtils.equals(odi.getLanguage(), dpi.getLanguage())){
			msg += ",language:" + odi.getLanguage() + "--->" + dpi.getLanguage(); 
		}
		if(!StringUtils.equals(odi.getOpentype(), dpi.getOpentype())){
			msg += ",opentype:" + odi.getOpentype() + "--->" + dpi.getOpentype(); 
		}
		if(!StringUtils.equals(odi.getPeriod(), dpi.getPeriod())){
			msg += ",period:" + odi.getPeriod() + "--->" + dpi.getPeriod(); 
		}
		if(!odi.hasGewara()){
			if(!StringUtils.equals(odi.getSellerseq(), dpi.getSellerseq())){
				msg += "sellerseq��" + odi.getSellerseq() + "--->" + dpi.getSellerseq();
			}
		}
		return StringUtils.substring(msg, 1);
	}
}
