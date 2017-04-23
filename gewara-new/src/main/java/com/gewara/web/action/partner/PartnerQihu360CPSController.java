package com.gewara.web.action.partner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.json.order.OutOriginOrder;
import com.gewara.support.ErrorCode;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.HttpUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.inner.mobile.BaseOpenApiController;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 360cps�����ƹ� 
 * ����鿴http://union.360.cn/help/apidocnew#defaultFocus
 */
@Controller
public class PartnerQihu360CPSController extends BaseOpenApiController {
	private static final String CP_KEY = "B13A05D4F49C8014300643D7352E012E";
	// 360��ͬ���2012316453
	private static final String BID = "2012316453";
	private static final int CPS_COOKIE_TIME = 60 * 60 * 24 * 30;
	
	private static final String SIGN_FAILED_URL = "http://open.union.360.cn/gofailed";

	/**
	 * 1.������������ת�ӿڽ��յ����� 
	 * 2.ͨ��post��ʽ��ȡ��Ӧ������
	 * 3.�����յ��Ĳ�����Ϣ��¼����cookie��(����д��һ��������)��cookie��Ч��ʱ��Ϊ30�죬���cookie����Ч�ڣ���Ϊ360CPS����Ч�ڡ�����˴��鿴CPS��Ч�ڵľ���˵��
	 * 4.�жϻ�ȡ����bid������Ԥ�ȷ����bid�����Ƿ���ͬ��������ǩ����֤��
	 * 5.������ת����ȡ������Ʒurl��ҳ�棨��ת����header�ض���ʽ��http״̬����302��,���û�л�ȡ��url������Ĭ��ҳ
	 * 
	 */
	@RequestMapping("/outOrigin/360cps.xhtml")
	public String outOrigin360(PartnerQihu360CPSModel param, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String sign = StringUtil.md5(param.getBid() + "#" + param.getActive_time() + "#" + CP_KEY 
					+ "#" + (StringUtils.isNotBlank(param.getQid()) ? param.getQid() : "") 
					+ "#" + (StringUtils.isNotBlank(param.getQmail()) ? param.getQmail() : "")
					+ "#" + (StringUtils.isNotBlank(param.getQname()) ? param.getQname() : ""));
			String ip = WebUtils.getRemoteIp(request);
			if (!StringUtils.equals(param.getBid(), BID) || !StringUtils.equalsIgnoreCase(sign, param.getSign())) {
				signerrorPostTo360cps(param, ip);
				dbLogger.errorWithType("360CPS", BeanUtil.getBeanMap(param).toString());
			} else {
				add360cpsCookie(param, response);
			}
		} catch (Exception e) {
			dbLogger.error(e.getMessage(), e);
		}
		String redirectURL = StringUtils.isNotBlank(param.getUrl()) ? param.getUrl() : "/cinema/searchOpi.xhtml";
		response.sendRedirect(redirectURL);
		return null;
	}

	/**
	 * 360CPS������ѯ
	 */
	@RequestMapping("/outOrigin/360cpsOrderQuery.xhtml")
	public String orderQuery(PartnerQihu360CPSQueryModel param, ModelMap model) {
		String sign = StringUtil.md5(param.getBid() + "#" + param.getActive_time() + "#" + CP_KEY);
		
		if (!StringUtils.equals(param.getBid(), BID) || !StringUtils.equalsIgnoreCase(sign, param.getSign())) {
			return getXmlView(model, "partner/360cps/orderresult.vm");
		}
		
		DBObject queryCondition = new BasicDBObject();
		DBObject OutOriginDbObject = mongoService.queryBasicDBObject("outOrigin", "=", "360cps");
		queryCondition.putAll(OutOriginDbObject);
		
		DBObject bidDbObject = mongoService.queryBasicDBObject("bid", "=", BID);
		queryCondition.putAll(bidDbObject);
		
		boolean isQuery = false;
		if (StringUtils.isNotBlank(param.getOrder_ids())) {
			DBObject tradeNoListDbObject = mongoService.queryBasicDBObject("order_id", "in", param.getOrder_ids().split(","));
			queryCondition.putAll(tradeNoListDbObject);
			isQuery = true;
		}
		if (StringUtils.isNotBlank(param.getStart_time()) && StringUtils.isNotBlank(param.getEnd_time())) {
			DBObject addTimeDbObject = mongoService.queryAdvancedDBObject("order_time", new String[]{">=","<"}, new String[]{param.getStart_time(), param.getEnd_time()});
			queryCondition.putAll(addTimeDbObject);
			isQuery = true;
		}
		
		if (StringUtils.isNotBlank(param.getUpdstart_time()) && StringUtils.isNotBlank(param.getUpdend_time())) {
			DBObject updateTimeDbObject = mongoService.queryAdvancedDBObject("order_updtime", new String[]{">=","<"}, new String[]{param.getUpdstart_time(), param.getUpdend_time()});
			queryCondition.putAll(updateTimeDbObject);
			isQuery = true;
		}
		
		if (StringUtils.isNotBlank(param.getLast_order_id())) {
			DBObject tradeNoDbObject = mongoService.queryBasicDBObject("order_id", ">", param.getLast_order_id());
			queryCondition.putAll(tradeNoDbObject);
		}
		List<OutOriginOrder> orderList = new ArrayList<OutOriginOrder>();
		if (isQuery) {
			orderList = mongoService.getObjectList(OutOriginOrder.class, queryCondition, "order_id", true, 0, 2000);
		}
		
		model.put("bid", BID);
		model.put("orderList", orderList);
		return getXmlView(model, "partner/360cps/orderresult.vm");
	}
	
	
	
	@RequestMapping("/outOrigin/syn360cpsOrder.xhtml")
	public String cpsOrderQuery(ModelMap model) {
		ErrorCode<String> synCode = ticketOrderService.syn360CPSOrderByDay(DateUtil.addDay(new Date(), 1));
		return getSingleResultXmlView(model, synCode.getRetval() + synCode.getMsg());
	}

	private void add360cpsCookie(PartnerQihu360CPSModel param, HttpServletResponse response) {
		String origin = "360cps";
		// 30��cookieʱ��
		long validtime = System.currentTimeMillis() + DateUtil.m_day*30;
		
		String cookievalue = origin + ":" + validtime + ":" + StringUtil.md5WithKey(origin + validtime, 8)
				+ ":" + param.getExt() + "#" + (StringUtils.isBlank(param.getQihoo_id()) ? "" : param.getQihoo_id())
				+ "#" + (StringUtils.isBlank(param.getQid())?"":param.getQid()) + "#" + param.getBid();
		Cookie cookie = new Cookie("origin", cookievalue);
		cookie.setPath("/");
		cookie.setDomain(".gewara.com");
		cookie.setMaxAge(CPS_COOKIE_TIME);// 24 hour
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}
	
	private void signerrorPostTo360cps(PartnerQihu360CPSModel param, String ip) {
		Map<String, String> paramMap = new HashedMap();
		paramMap.put("bid", BID);
		paramMap.put("active_time", String.valueOf(System.currentTimeMillis()));
		paramMap.put("pre_bid", param.getBid());
		paramMap.put("pre_active_time", param.getActive_time());
		paramMap.put("pre_sign", param.getSign());
		paramMap.put("from_url", param.getFrom_url());
		paramMap.put("from_ip", ip);
		HttpUtils.postUrlAsString(SIGN_FAILED_URL, paramMap);
	}
}
