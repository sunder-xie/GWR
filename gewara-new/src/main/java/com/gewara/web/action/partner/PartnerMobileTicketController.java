package com.gewara.web.action.partner;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.constant.CityData;
import com.gewara.constant.PayConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.content.SignName;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.model.api.ApiUser;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.movie.Movie;
import com.gewara.model.pay.TicketOrder;
import com.gewara.pay.MobileTicketUtil;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CommonService;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;

/**
 * �ƶ�Ʊ��
 */
@Controller
public class PartnerMobileTicketController extends BasePartnerController{
	@Autowired@Qualifier("commonService")
	private CommonService commonService;
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}
	private ApiUser getMobileTicket(){
		return daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_MOBILETICKET);
	}

	@RequestMapping("/partner/mobile/index.xhtml")
	public String index(String token, String encQryStr, 
			String moviename, HttpServletResponse response, ModelMap model, HttpServletRequest request) {
		ApiUser partner = getMobileTicket();
		String citycode = getCitycodeByPartner(partner, request, response);
		model.put("partnerCityMap", CityData.getCityNameMap(partner.getCitycode()));
		List<Movie> movieList = getOpenMovieListByDate(partner, citycode, null);
		super.filterMovieList(moviename, movieList, model);
		model.put("partner", partner);
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		ErrorCode code = addInitParams(token, encQryStr,null, model);
		if(!code.isSuccess()) return forwardMessage(model, code.getMsg());
		model.put("iframeUrl", VmUtils.getJsonValueByKey(partner.getOtherinfo(), PayConstant.KEY_IFRAME_URL));
		return "partner/mobile/index.vm";
	}
	@RequestMapping("/partner/mobile/movieDetail.xhtml")
	public String movieDetail(Long movieid, @CookieValue(required=false,value="ukey") String ukey, String tokenId,
			String encQryStr, Date fyrq, HttpServletResponse response, ModelMap model, HttpServletRequest request){
		ApiUser partner = getMobileTicket();
		Movie movie = daoService.getObject(Movie.class, movieid);
		if(movie==null) return forwardMessage(model, "ӰƬ�����ڣ�");
		String citycode = this.getCitycodeByPartner(partner, request, response);
		model.put("partnerCityMap", CityData.getCityNameMap(partner.getCitycode()));
		addOpiListData(partner, movieid, fyrq, null, null, model, citycode);
		opiList(partner, movieid, fyrq, "partner/mobile/step1.vm", model);
		model.put("movie", movie);
		model.put("encQryStr", encQryStr);
		if(StringUtils.isBlank(encQryStr)){
			ErrorCode code = addInitParams(tokenId, encQryStr,null, model);
			if(!code.isSuccess()) return forwardMessage(model, code.getMsg());
		}
		if(StringUtils.isBlank(ukey)) {
			PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/mobile/");
		}
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		model.put("iframeUrl", VmUtils.getJsonValueByKey(partner.getOtherinfo(), PayConstant.KEY_IFRAME_URL));

		return "partner/mobile/movieDetail.vm";
	}
	@RequestMapping("/partner/mobile/opiList.xhtml")
	public String unionOpiList(HttpServletResponse response, Date fyrq, Long movieid, String token, String encQryStr,
			@CookieValue(required=false,value="ukey") String ukey, ModelMap model, HttpServletRequest request) {
		if(StringUtils.isBlank(ukey)) {
			PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/mobile/");
		}
		ErrorCode code = addInitParams(token, encQryStr,null, model);
		if(!code.isSuccess()) return forwardMessage(model, code.getMsg());
		ApiUser partner = getMobileTicket();
		
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		
		String citycode = getCitycodeByPartner(partner, request, response);
		model.put("partnerCityMap", CityData.getCityNameMap(partner.getCitycode()));
		addOpiListData(partner, movieid, fyrq, null, null, model, citycode);
		return "partner/mobile/step1.vm";
	}
	//�ڶ�����ѡ����λ
	@RequestMapping("/partner/mobile/chooseSeatTest.xhtml")
	public String cmwifiChooseSeat(Long mpid, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		if(mpid==null) return forwardMessage(model, "ȱ�ٲ�����");
		ApiUser partner = getMobileTicket();
		String userId = "123";
		model.put("userid", userId);
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		this.getCitycodeByPartner(partner, request, response);
		model.put("partnerCityMap", CityData.getCityNameMap(partner.getCitycode()));
		return chooseSeat(partner, mpid, userId, "partner/mobile/step2.vm", model);
	}
	
	//�ڶ�����ѡ����λ
	@RequestMapping("/partner/mobile/chooseSeat.xhtml")
	public String cmwifiChooseSeat(Long mpid,String token,String encQryStr, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		if(mpid==null) return forwardMessage(model, "ȱ�ٲ�����");
		Map<String, String> reqParamMap = PartnerUtil.getParamMap(encQryStr);
		String userid = reqParamMap.get("userid");
		if(token != null){
			ErrorCode code = addInitParams(token, encQryStr,null, model);
			if(!code.isSuccess()) return forwardMessage(model, code.getMsg());
		}else if(StringUtils.isBlank(userid)) { 
			return forwardMessage(model, "���ȵ�¼��");
		}
		ApiUser partner = getMobileTicket();
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		model.put("encQryStr", encQryStr);
		getCitycodeByPartner(partner, request, response);
		model.put("partnerCityMap", CityData.getCityNameMap(partner.getCitycode()));
		return chooseSeat(partner, mpid, userid, "partner/mobile/step2.vm", model);
	}
	@RequestMapping("/partner/mobile/seatPage.xhtml")
	public String seatPage(Long mpid, Long partnerid, Integer price, @CookieValue(value="ukey", required=false) String ukey, ModelMap model){
		model.put("mpid", mpid);
		model.put("partnerid", partnerid);
		model.put("price", price);
		model.put("ukey", ukey);
		ErrorCode<String> code = addSeatData(mpid, partnerid, ukey, model);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		return "partner/seatPage.vm";
	}
	
	//�ڶ�����ѡ����λ
	@RequestMapping("/partner/mobile/apichooseSeat.xhtml")
	public String apichooseSeat(Long mpid,String token,@CookieValue(required=false,value="ukey") String ukey,
			String basePath,ModelMap model, HttpServletRequest request, HttpServletResponse response){
		if(mpid==null) return forwardMessage(model, "ȱ�ٲ�����");	
		if(StringUtils.isBlank(ukey)) ukey = PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/mobile/");
		ErrorCode code = addInitParams(token, null,basePath, model);
		if(!code.isSuccess()) return forwardMessage(model, code.getMsg());
		ApiUser partner = getMobileTicket();
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		getCitycodeByPartner(partner, request, response);
		model.put("partnerCityMap", CityData.getCityNameMap(partner.getCitycode()));
		if(StringUtils.isBlank(basePath))basePath = "http://www.sh.10086.cn/ticket/";
		this.setUkCookie(response, "basePath", basePath, "/partner/mobile");
		return chooseSeat(partner, mpid, ukey, "partner/mobile/apistep2.vm", model);
	}
	
	//������:����λ���Ӷ���
	@RequestMapping("/partner/mobile/addOrder.xhtml")
	public String cmwifiAddOrder(@CookieValue(required=false,value="ukey") String ukey,
			String captchaId, String captcha, Long mpid, String encQryStr, 
			@CookieValue(required=false,value="basePath") String basePath,
			@RequestParam("mobile") String mobile, @RequestParam("seatid")String seatid, HttpServletRequest request, ModelMap model){
		Map<String, String> params = PartnerUtil.getParamMap(basePath);
		basePath = params.get("mobile_basePath");
		if(StringUtils.isBlank(basePath))basePath = "http://www.sh.10086.cn/ticket/";
		if(mpid==null) return showJsonError(model, "ȱ�ٲ�����");
		if(StringUtils.isBlank(encQryStr)) return forwardMessage(model, "���ȵ�¼��");
		boolean validCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!validCaptcha) return showJsonError(model, "��֤�����");
		Map<String, String> paramMap = PartnerUtil.getParamMap(encQryStr);
		String userid = paramMap.get("userid"); 
		if(StringUtils.isBlank(userid)) return showJsonError(model, "��ȡ�û���ʶ����,�����µ�¼��");
		ApiUser partner = getMobileTicket();
		model.put("userid", userid);
		ErrorCode code = addOrder(mpid, mobile, seatid, ukey, partner, userid, null, null, PaymethodConstant.PAYMETHOD_PARTNERPAY, WebUtils.getRemoteIp(request), model);
		if(code.isSuccess()){
			this.addBasePath(model, basePath);
			return showJsonSuccess(model, model.get("orderId")+","+mobile);
		}
		return showJsonError(model, code.getMsg());
	
	}
	
	//���Ĳ���ȷ�϶���ȥ֧�������²鿴��
	@RequestMapping("/partner/mobile/showOrder.xhtml")
	public String cmwifiShowOrder(Long orderId, ModelMap model,@CookieValue(value="ukey", required=false)String ukey, 
			HttpServletRequest request, HttpServletResponse response){
		ApiUser partner = getMobileTicket();
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		this.getCitycodeByPartner(partner, request, response);
		model.put("partnerCityMap", CityData.getCityNameMap(partner.getCitycode()));
		return showOrder(ukey, orderId, partner, "partner/mobile/confirmOrder.vm", model);
	}
	
	//���Ĳ���ȷ�϶���ȥ֧��
	@RequestMapping(method=RequestMethod.POST,value="/partner/mobile/saveOrder.xhtml")
	public String cmwifiSaveOrder(ModelMap model, String mobile,
			@CookieValue(required=false,value="ukey") String ukey, String paybank,
			@RequestParam("orderId")long orderId){
		if(StringUtils.isBlank(ukey)) return forwardMessage(model, "ȱ�ٲ�����");
		return saveOrder(orderId, mobile, PaymethodConstant.PAYMETHOD_PARTNERPAY, paybank, ukey, model);
	}
	
	//���Ͷ���
	@RequestMapping("/partner/mobile/pushOrder.xhtml")
	@ResponseBody
	public String pushOrder(String tradeno){
		TicketOrder order = daoService.getObjectByUkey(TicketOrder.class, "tradeNo", tradeno, false);
		String userid = "";
		if(StringUtils.indexOf(order.getMembername(), "@") != -1){
			userid = StringUtils.substring(order.getMembername(), 0,StringUtils.indexOf(order.getMembername(), "@")) ;
		}
		Map<String, String> dataMap = JsonUtils.readJsonToMap(order.getOtherinfo());
		String basePath = dataMap.get("basePath");
		String result = MobileTicketUtil.pushSuccessOrder(order, getMobileTicket(),userid,basePath);
		if(StringUtils.equals("0000", result)){
			return "success";
		}
		return result;
	}
	
	//������:����λ���Ӷ���
	@RequestMapping("/partner/mobile/apiAddOrder.xhtml")
	public String apiAddOrder(@CookieValue(required=false,value="ukey") String ukey,
			String captchaId, String captcha, Long mpid, String encQryStr, 
			@RequestParam("mobile") String mobile, @RequestParam("seatid")String seatid, 
			HttpServletRequest request, @CookieValue(required=false,value="basePath") String basePath,ModelMap model){
		Map<String, String> params = PartnerUtil.getParamMap(basePath);
		basePath = params.get("mobile_basePath");
		if(StringUtils.isBlank(basePath)) basePath = "http://www.sh.10086.cn/ticket/";
		if(mpid==null) return showJsonError(model, "ȱ�ٲ�����");
		if(StringUtils.isBlank(encQryStr)) return forwardMessage(model, "���ȵ�¼��");
		boolean validCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!validCaptcha) return showJsonError(model, "��֤�����");
		Map<String, String> paramMap = PartnerUtil.getParamMap(encQryStr);
		String userid = paramMap.get("userid"); 
		if(StringUtils.isBlank(userid)) return showJsonError(model, "��ȡ�û���ʶ����,�����µ�¼��");
		ApiUser partner = getMobileTicket();
		model.put("userid", userid);
		String paymethod = "partnerPay";
		String result = addOrderAndPay(mpid, mobile, seatid, paymethod, null, ukey, partner, userid, 0, model);
		this.addBasePath(model, basePath);
		return result;
	}
	
	private void addBasePath(ModelMap model,String basePath){
		Long orderId = Long.valueOf(model.get("orderId")+"");
		TicketOrder order = this.daoService.getObject(TicketOrder.class, orderId);
		Map<String, String> dataMap = JsonUtils.readJsonToMap(order.getOtherinfo());
		dataMap.put("basePath", basePath);
		order.setOtherinfo(JsonUtils.writeMapToJson(dataMap));
		this.daoService.saveObject(order);
	}
	
	private ErrorCode addInitParams(String token, String encQryStr,String basePath, ModelMap model){
		if(StringUtils.isNotBlank(token)){
			ErrorCode<String> user = getLoginUser(token,basePath);
			if(!user.isSuccess()) return user;
			Map<String, String> params = new LinkedHashMap<String, String>();
			params.put("userid", user.getRetval());
			String ukey = DateUtil.format(new Date(), "MMss") + StringUtil.getRandomString(4);
			params.put("ukey", ukey);
			encQryStr = PartnerUtil.getParamStr(params);
		}else if(StringUtils.isBlank(encQryStr)){
			return ErrorCode.getFailure("���ȵ�¼��");
		}
		model.put("encQryStr", encQryStr);
		return ErrorCode.SUCCESS;
	}
	
	private ErrorCode getLoginUser(String token,String basePath){
		//if(true)return ErrorCode.getSuccessReturn(StringUtil.getRandomString(10));
		Map<String, String> userMap = MobileTicketUtil.getUserLoginStatus(this.getMobileTicket(),token,basePath);
		if(userMap != null){
			String result = userMap.get("userId");
			return ErrorCode.getSuccessReturn(result);
		}
		return ErrorCode.getFailure("���ȵ�¼��");
	}
	
	public String setUkCookie(HttpServletResponse response,String cookieName,String cookieValue, String path){
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile_basePath", cookieValue);
		String ukey = PartnerUtil.getParamStr(params);
		Cookie cookie = new Cookie(cookieName, ukey);
		cookie.setPath(path);
		cookie.setMaxAge(60 * 60 * 12);//12 hour
		response.addCookie(cookie);
		return ukey;
	}
	
}
