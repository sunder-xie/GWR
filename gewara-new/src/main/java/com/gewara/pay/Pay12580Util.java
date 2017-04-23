package com.gewara.pay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gewara.Config;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.model.api.ApiUser;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.util.DateUtil;
import com.gewara.util.GewaLogger;
import com.gewara.util.HttpResult;
import com.gewara.util.HttpUtils;
import com.gewara.util.JsonUtils;
import com.gewara.util.LoggerUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;

public class Pay12580Util {
	private static final transient GewaLogger dbLogger = LoggerUtils.getLogger(Pay12580Util.class, Config.getServerIp(), Config.SYSTEMID);

	public static Map<String, String> getPayParams(GewaOrder order,ApiUser partner, String notify){
		Map<String, String> params = new HashMap<String, String>();
		params.put("sign", StringUtil.md5(order.getTradeNo() + order.getMobile() + DateUtil.format(order.getAddtime(),"yyyyMMddHHmmss")));
		params.put("t", DateUtil.format(order.getAddtime(),"yyyyMMddHHmmss"));
		params.put("orderId", order.getId() + "");
		Map<String,String> descMap = JsonUtils.readJsonToMap(order.getDescription2());
		params.put("desc",  "ӰƬ" + descMap.get("ӰƬ") + ";����" + descMap.get("����"));
		params.put("amout", order.getDue()+"");
		params.put("phone", order.getMobile());
		params.put("callback", notify);
		params.put("payurl", partner.getAddOrderUrl());
		params.put("submitMethod", "post");
		return params;
	}
	/**
	 * 
	 * @param order ����
	 * @param partner 12580������
	 * @param name ����
	 * @param idno ʡ��֤����
	 * @param idtype ֤������
	 * @param iaddr ֤��������ַ
	 * @param cardno ���п���
	 * @param bankprovcity ���п�����
	 * @return
	 */
	public static Map<String, String> getDNAPayParams(GewaOrder order,ApiUser partner,String name,String idno,String idtype,
			String iaddr,String cardno,String bankprovcity){
		Map<String,String> map = VmUtils.readJsonToMap(partner.getOtherinfo());
		Map<String, String> params = new HashMap<String, String>();
		//params.put("app", APP);
		params.put("appid", map.get("appid12580"));
		params.put("t", DateUtil.format(new Date(),"yyyyMMddHHmmss"));
		//params.put("orderid", order.getTradeNo() + "");
		params.put("amount", (order.getDue() * 100 ) + "");//order.getDue()��λΪԪ�� *100ת��Ϊ�Է�Ϊ��λ
		Map<String,String> descMap = JsonUtils.readJsonToMap(order.getDescription2());
		params.put("desc",  "ӰƬ" + descMap.get("ӰƬ") + ";����" + descMap.get("����"));
		params.put("refer", order.getTradeNo() + "");
		params.put("phone", order.getMobile());
		params.put("callback", partner.getNotifyurl());
		params.put("name", name);
		params.put("idno", idno);//֤������
		params.put("idtype",idtype);//֤������
		params.put("idaddr",StringUtils.isBlank(iaddr) ? "":iaddr);//֤��������ַ
		params.put("cardno", cardno);//���п���
		params.put("bankprovcity", StringUtils.isBlank(bankprovcity) ? "":bankprovcity);//���п�����ַ
		params.put("start", DateUtil.format(order.getAddtime(),"yyyyMMddHHmmss"));//������ʼʱ��
		params.put("expire", DateUtil.format(order.getValidtime(),"yyyyMMddHHmmss"));//������Чʱ��
		List<String> keyList = new ArrayList(params.keySet());
		Collections.sort(keyList);
		StringBuilder sb = new StringBuilder();
		for (String key : keyList) {
			sb.append(key).append("=").append(params.get(key)).append("&");
		}
		sb.append("secret=").append(map.get("secret12580"));
		String sign = StringUtil.md5(sb.toString()).toUpperCase();
		dbLogger.errorWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY,sb.toString() + "[sign]" + sign);
		params.put("sign",sign);
		return params;
	}
		
	public static Map<String,String> getQueryParams(String orderid,ApiUser partner){
		Map<String,String> map = VmUtils.readJsonToMap(partner.getOtherinfo());
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", map.get("appid12580"));
		params.put("orderid",orderid);
		params.put("t",DateUtil.format(new Date(),"yyyyMMddHHmmss"));
		List<String> keyList = new ArrayList(params.keySet());
		Collections.sort(keyList);
		StringBuilder sb = new StringBuilder();
		for (String key : keyList) {
			sb.append(key).append("=").append(params.get(key)).append("&");
		}
		String paramsStr = sb.append("secret=").append(map.get("secret12580")).toString();
		params.put("sign", StringUtil.md5(paramsStr));
		return params;
	}
	
	/**
	 * �ز鶩���Ƿ����
	 * @param partner
	 * @param payseqno
	 * @param tradeno
	 * @param paidAmount
	 * @param checkvalue
	 * @return
	 */
	public static String validate(ApiUser partner,String checkvalue,String tradeNo, TicketOrder order) {
		int i = 0;
		Map<String, String> params = getQueryParams(tradeNo,partner);
		while(i < 4){
			HttpResult code = HttpUtils.postUrlAsString(partner.getQryurl(), params);
			dbLogger.warn("tradeNo=" + tradeNo + ", response=" + code.getResponse());
			if(code.isSuccess()){
				Map<String,String> map = VmUtils.readJsonToMap(code.getResponse());
				if(order.getDue() * 100 == Integer.parseInt(map.get("amount")) && "00".equals(map.get("status").trim()) && StringUtils.equals(map.get("code").trim(), "0000")){
					return "success";
				}else{
					dbLogger.errorWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, partner.getQryurl() + "," + params + code.getResponse());
					return "query error:" + code.getResponse();
				}
			}
			i++;
		}
		return "checkvalue error";
	}
}
