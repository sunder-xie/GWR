package com.gewara.helper.api;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.order.ElecCardConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.SpCode;
import com.gewara.model.pay.TicketOrder;
import com.gewara.util.DateUtil;
import com.gewara.util.VmUtils;

public class GewaApiOrderHelper {
	//��������
	public static Map<String, Object> getOrderMap(GewaOrder order){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderid", order.getId());
		params.put("ordertitle", order.getOrdertitle());
		params.put("tradeno", order.getTradeNo());
		params.put("citycode", order.getCitycode());
		params.put("addtime", order.getAddtime());
		params.put("playtime", order.getPlaytime());
		params.put("validtime", order.getValidtime());
		params.put("amount", order.getDue());
		params.put("totalfee", order.getTotalfee());
		params.put("mobile", order.getMobile());
		params.put("unitprice", order.getUnitprice());
		params.put("quantity", order.getQuantity());
		params.put("status", ApiConstant.getMappedOrderStatus(order.getFullStatus()));
		params.put("discount", order.getDiscount());
		params.put("disreason", order.getDisreason());
		params.put("ukey", order.getUkey());
		if(order.isAllPaid()) {
			params.put("paidtime", order.getPaidtime());
			params.put("paidAmount", order.getAlipaid());
			params.put("payseqno", order.getPayseqno());
		}
		if(StringUtils.equals(order.getStatus(), OrderConstant.STATUS_PAID_RETURN)){
			params.put("refundtime", order.getUpdatetime());
		}
		return params;
	}
	//��Ӱ����
	public static Map<String, Object> getTicketOrderMap(TicketOrder order){
		Map<String, String> descMap = VmUtils.readJsonToMap(order.getDescription2());
		Map<String, Object> params = getOrderMap(order);
		params.put("ordertype", OrderConstant.ORDER_TYPE_TICKET);
		params.put("movieid", order.getMovieid());
		params.put("cinemaid", order.getCinemaid());
		params.put("mpid", order.getMpid());
		params.put("seat", descMap.get("ӰƱ"));
		params.put("moviename", descMap.get("ӰƬ"));
		params.put("roomname", descMap.get("Ӱ��"));
		return params;
	}
	//���綩��
	public static Map<String, Object> getDramaOrderMap(DramaOrder order){
		Map<String, Object> params = getOrderMap(order);
		params.put("otherfee", order.getOtherfee());
		params.put("ordertype", OrderConstant.ORDER_TYPE_DRAMA);
		params.put("dramaid", order.getDramaid());
		params.put("theatreid", order.getTheatreid());
		params.put("dpid", order.getDpid());
		return params;
	}
	//��Ʒ����
	public static Map<String, Object> getGoodsOrderMap(GoodsOrder order){
		Map<String, String> descMap = VmUtils.readJsonToMap(order.getDescription2());
		Map<String, Object> params = getOrderMap(order);
		params.put("ordertype", OrderConstant.ORDER_TYPE_GOODS);
		params.put("goodsid", order.getGoodsid());
		params.put("placeid", order.getPlaceid());
		params.put("itemid", order.getItemid());
		params.put("goodsname", descMap.get("��Ʒ����"));
		return params;
	}
	
	//ȯ
	public static Map<String, Object> getCardMap(ElecCard card){
		String statusText = "";
		String status = card.getStatus();
		if(StringUtils.equals(status, ElecCardConstant.STATUS_USED)){
			statusText = "��ʹ��";
		}else if(StringUtils.equals(status, ElecCardConstant.STATUS_SOLD)){
			Timestamp curtime = DateUtil.getMillTimestamp();
			if(card.getTimeto().before(curtime)) statusText = "�ѹ���";
			else statusText = "δʹ��";
		}else if(StringUtils.equals(status, ElecCardConstant.STATUS_DISCARD)){
			statusText = "����";
		}else if(StringUtils.equals(status, ElecCardConstant.STATUS_NEW)){
			statusText = "����";
		}
		String cardtype = card.getCardtype();
		String name = "";
		if(StringUtils.equals(cardtype, "A")){
			name = "�һ�ȯ";
		}else if(StringUtils.equals(cardtype, "D")){
			name = card.getEbatch().getAmount()+"Ԫ��ֵȯ";
		}else if(StringUtils.equals(cardtype, "B")){
			name = "����ȯ";
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cardno", card.getCardno());
		params.put("name", name);
		params.put("cardtype", cardtype);
		params.put("starttime", card.getTimefrom());
		params.put("endtime", card.getTimeto());
		params.put("status", statusText);
		params.put("edition", card.getEbatch().getEdition());
		params.put("amount", card.getEbatch().getAmount());
		params.put("cardtag", card.getEbatch().getTag());
		params.put("channelinfo", card.getEbatch().getChannelinfo());
		return params;
	}
	//�Ż���
	public static Map<String, Object> getSpCodeMap(SpCode code){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sendtime", code.getSendtime());
		params.put("usedcount", code.getUsedcount());
		params.put("sdid", code.getSdid());
		params.put("pass", code.gainTemppass());
		return params;
	}
}
