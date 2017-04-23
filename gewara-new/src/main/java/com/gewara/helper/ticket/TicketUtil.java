package com.gewara.helper.ticket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.ticket.OrderConstant;
import com.gewara.helper.order.GewaOrderHelper;
import com.gewara.model.movie.RoomSeat;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.OpenSeat;
import com.gewara.model.ticket.SellSeat;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;

public abstract class TicketUtil {
	public static final Map<String, String> statusMap = new HashMap<String, String>();
	static{
		statusMap.put(OrderConstant.REMOTE_STATUS_NEW,"�¶���");
		statusMap.put(OrderConstant.REMOTE_STATUS_LOCK,"��λ����");
		statusMap.put(OrderConstant.REMOTE_STATUS_FIXED,"�����ɹ�");
		statusMap.put(OrderConstant.REMOTE_STATUS_UNLOCK,"��λ����");
		statusMap.put(OrderConstant.REMOTE_STATUS_ERROR,"����");
		statusMap.put(OrderConstant.REMOTE_STATUS_CANCEL,"��Ʊ"); 
	}
	public static final List<String[]> parseSeat(String seatText){
		List<String[]> result = new ArrayList<String[]>();
		for(String seat:seatText.split(",")) result.add(seat.split(":"));
		return result;
	}
	public static void setOrderDescription(TicketOrder order, Collection<SellSeat> seatList, OpenPlayItem opi){
		String ordertitle = opi.getCinemaname()+"��ӰƱ";
		order.setOrdertitle(ordertitle);
		Map<String, String> descMap = new HashMap<String, String>();
		descMap.put("ӰƬ", opi.getMoviename());
		descMap.put("Ӱ��", opi.getRoomname());
		descMap.put("����", DateUtil.format(opi.getPlaytime(), "M��d�� HH:mm"));
		descMap.put("ӰƱ", GewaOrderHelper.getSeatText3(seatList));
		order.setDescription2(JsonUtils.writeMapToJson(descMap));
	}
	public static Map getSeatMap(OpenSeat oseat){
		Map result = BeanUtil.getBeanMapWithKey(oseat, "lineno", "rankno", "seatline", "seatrank", "id");
		result.put("key", oseat.getSeatline()+","+oseat.getSeatrank());
		return result;
	}
	public static Map getSeatMap(RoomSeat rseat){
		Map result = BeanUtil.getBeanMapWithKey(rseat, "lineno", "rankno", "seatline", "seatrank", "id");
		result.put("key", rseat.getSeatline()+","+rseat.getSeatrank());
		return result;
		
	}
	public static boolean isValidToken(Long mpid, String token) {
		return StringUtils.equals(StringUtil.md5(mpid+"XJx3829", 15), token);
	}
	public static String getToken(Long mpid){
		return StringUtil.md5(mpid+"XJx3829", 15);
	}
	public static String getStep1Url(Long mpid, String spkey){
		String url = "cinema/order/step1.shtml?mpid=" + mpid + "&tkn=" + getToken(mpid);
		if(StringUtils.isNotBlank(spkey)){
			url += "&spkey=" + spkey;
		}
		return url;
	}
}
