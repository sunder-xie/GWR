/**
 * 
 */
package com.gewara.pay;

import java.util.HashMap;
import java.util.Map;

import com.gewara.Config;
import com.gewara.model.api.ApiUser;
import com.gewara.model.pay.GewaOrder;
import com.gewara.util.DateUtil;
import com.gewara.util.GewaLogger;
import com.gewara.util.HttpResult;
import com.gewara.util.HttpUtils;
import com.gewara.util.JsonUtils;
import com.gewara.util.LoggerUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;


/**
 * @author Administrator
 *
 */
public class UnionpayUtil {
	private static final transient GewaLogger dbLogger = LoggerUtils.getLogger(UnionpayUtil.class, Config.getServerIp(), Config.SYSTEMID);
	
	public static Map<String, String> getPayParams(GewaOrder order,ApiUser partner, String notify){
		Map<String, String> params = new HashMap<String, String>();
		params.put("tradeno", order.getTradeNo());
		params.put("ordertitle", order.getOrdertitle());
		params.put("amount", order.getDue()+"");
		params.put("mobile", order.getMobile());
		params.put("discount", order.getDiscount()+"");
		Map<String, String> desMap = JsonUtils.readJsonToMap(order.getDescription2());
		String moviename = desMap.get("ӰƬ");
		String seat = desMap.get("ӰƱ");
		String room = desMap.get("Ӱ��");
		String palytime = desMap.get("����");
		params.put("description", "ӰƬ="+moviename+";��Ӱ��="+room+";��λ="+seat+";��ӳʱ��="+palytime);
		params.put("addtime", DateUtil.format(order.getAddtime(),"yyyyMMddHHmmss"));
		params.put("checkvalue", StringUtil.md5(order.getTradeNo()+order.getDue()+order.getMobile()+partner.getPrivatekey()));
		params.put("notifyurl", notify);
		params.put("payurl", partner.getAddOrderUrl());
		return params;
	}
	/**
	 * 
	 * @param order
	 * @param partner
	 * @return
	 */
	
	public static HttpResult getQryOrder(GewaOrder order,ApiUser partner) {
		Map<String, String> otherMap = VmUtils.readJsonToMap(partner.getOtherinfo());
		Map<String, String> params = new HashMap<String, String>();
		params.put("tradeno", order.getTradeNo());
		params.put("paidAmount", order.getDue()+"");
		params.put("payseqno", order.getPayseqno());
		params.put("checkvalue", StringUtil.md5(order.getTradeNo()+order.getPayseqno()+order.getDue()+partner.getPrivatekey()));
		HttpResult result = HttpUtils.postUrlAsString(otherMap.get("qryUrl"), params);
		dbLogger.warn("getQryOrder : " + result.getResponse());
		return result;
	}

}
