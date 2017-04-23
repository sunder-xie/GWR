package com.gewara.web.action.partner;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.content.SignName;
import com.gewara.model.api.ApiUser;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.movie.Movie;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CommonService;
import com.gewara.util.BeanUtil;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.util.PageUtil;
/**
 * 
 * ͨ�ú����� ������Ʊ iframeǶ��
 */
@Controller
public class PartnerMovieCommonController extends BasePartnerController {
	@Autowired@Qualifier("commonService")
	private CommonService commonService;
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}
	private ApiUser getApiUser(String key){
		return daoService.getObjectByUkey(ApiUser.class, "partnerkey", key, true);
	}
	private static final int pageSize = 10;
	
	private boolean validPartner(String sign,ApiUser partner){
		if(partner == null){
			return false;
		}
		if(StringUtils.equals(sign, StringUtil.md5("key=" + partner.getPartnerkey() + "&privateKey=" + partner.getPrivatekey()))){
			return true;
		}
		return false;
	}
	
	private void parsePartnerOtherInfo(ApiUser partner, ModelMap model){
		Map<String, String> otherInfo = VmUtils.readJsonToMap(partner.getOtherinfo());
		model.put("partner", partner);
		model.put("proxyUrl", otherInfo.get("proxyUrl"));
		int iframeWidth = 1000;
		if(StringUtils.isNotBlank(otherInfo.get("iframeWidth"))){
			try {
				iframeWidth = Integer.parseInt(otherInfo.get("iframeWidth"));
			} catch (NumberFormatException e) {
			}
		}
		model.put("iframeWidth", iframeWidth);
	}

	@RequestMapping("/partner/common/index.xhtml")
	public String index(String moviename, ModelMap model,String sign, String key,
			@CookieValue(required=false,value="ukey") String ukey, HttpServletResponse response,HttpServletRequest request,Integer pageNo){
		if(StringUtils.isBlank(key)){
			return forwardMessage(model, "��ȷ����ȷ����Դ������");
		}
		ApiUser partner = getApiUser(key);
		if(!validPartner(sign, partner)){
			return forwardMessage(model, "��ȷ����ȷ����Դ������");
		}
		if(StringUtils.isBlank(ukey)) {
			PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/common/");
		}
		List<Movie> movieList = getOpenMovieListByDate(partner, partner.getDefaultCity(), null);
		int rowsCount = StringUtils.isBlank(moviename) ? movieList.size() : 1;
		if(pageNo==null) pageNo = 0;
		PageUtil pageUtil = new PageUtil(rowsCount , pageSize, pageNo,"partner/common/index.xhtml", true, true);
		pageUtil.initPageInfo(request.getParameterMap());
		movieList = BeanUtil.getSubList(movieList, pageNo*pageSize, pageSize);
		model.put("pageUtil", pageUtil);
		model.put("movieList", movieList);
		super.filterMovieList(moviename, movieList, model);
		parsePartnerOtherInfo( partner,model);
		model.put("sign", sign);
		model.put("key", key);
		return "partner/movieCommon/index.vm";
	}
	
	@RequestMapping("/partner/common/movieDetail.xhtml")
	public String movieDetail(Long movieid, String sign,@CookieValue(required=false,value="ukey") String ukey,Date fyrq, 
			String key,HttpServletResponse response, ModelMap model){
		ApiUser partner = getApiUser(key);
		if(!validPartner(sign, partner)){
			return forwardMessage(model, "��ȷ����ȷ����Դ������");
		}
		Movie movie = daoService.getObject(Movie.class, movieid);
		if(movie==null) return forwardMessage(model, "ӰƬ�����ڣ�");
		opiList(partner, movieid, fyrq, "partner/movieCommon/step1.vm", model);
		model.put("movie", movie);
		if(StringUtils.isBlank(ukey)) {
			PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/common/");
		}
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		model.put("sign", sign);
		model.put("key", key);
		parsePartnerOtherInfo( partner,model);
		return "partner/movieCommon/movieDetail.vm";
	}
	
	//�ڶ�����ѡ����λ
	@RequestMapping("/partner/common/chooseSeat.xhtml")
	public String chooseSeat(Long mpid, String sign,String key, @CookieValue(required=false,value="ukey") String ukey,ModelMap model){
		if(mpid==null) return showError(model, "ȱ�ٲ�����");
		ApiUser partner = getApiUser(key);
		if(!validPartner(sign, partner)){
			return forwardMessage(model, "��ȷ����ȷ����Դ������");
		}
		parsePartnerOtherInfo( partner,model);
		model.put("sign", sign);
		model.put("key", key);
		return chooseSeat(partner, mpid, ukey, "partner/movieCommon/chooseSeat.vm", model);
	}
	
	@RequestMapping("/partner/common/seatPage.xhtml")
	public String seatPage(Long mpid, Long partnerid, Integer price,  @CookieValue(value="ukey", required=false) String ukey, ModelMap model){
		model.put("mpid", mpid);
		model.put("partnerid", partnerid);
		model.put("price", price);
		model.put("ukey", ukey);
		ErrorCode<String> code = addSeatData(mpid, partnerid, ukey, model);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		return "partner/new_seatPage.vm";
	}
	//������:����λ���Ӷ���
	@RequestMapping("/partner/common/addOrder.xhtml")
	public String addOrder(String captchaId, String captcha, Long mpid,
			@CookieValue(value="ukey", required=false) String ukey,
			@RequestParam("mobile")String mobile,
			String key,String sign,
			@RequestParam("seatid")String seatid, HttpServletRequest request, ModelMap model){
		ApiUser partner = getApiUser(key);
		if(!validPartner(sign, partner)){
			return forwardMessage(model, "��ȷ����ȷ����Դ������");
		}
		boolean validCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!validCaptcha) return showJsonError(model, "��֤�����");
		String paymethod = PaymethodConstant.PAYMETHOD_ALIPAY;
		ErrorCode code = addOrder(mpid, mobile, seatid, ukey, partner, partner.getBriefname(), null, null, paymethod, WebUtils.getRemoteIp(request), model);
		if(code.isSuccess()) {
			return showJsonSuccess(model, model.get("orderId")+"");
		}
		return showJsonError(model, code.getMsg());
	}
	//���Ĳ���ȷ�϶���ȥ֧�������²鿴��
	@RequestMapping("/partner/common/showOrder.xhtml")
	public String showOrder(Long orderId, String key,String sign,  ModelMap model, 
			  @CookieValue(value="ukey", required=false) String ukey,
			String phonenumber){
		ApiUser partner = getApiUser(key);
		if(!validPartner(sign, partner)){
			return forwardMessage(model, "��ȷ����ȷ����Դ������");
		}
		model.put("phonenumber", phonenumber);
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		parsePartnerOtherInfo( partner,model);
		return showOrder(ukey, orderId, partner, "partner/movieCommon/confirmOrder.vm", model);
	}
	//���Ĳ���ȷ�϶���ȥ֧��
	@RequestMapping(method=RequestMethod.POST,value="/partner/common/saveOrder.xhtml")
	public String saveOrder(ModelMap model, String mobile,@CookieValue(value="ukey", required=false) String ukey,
			@RequestParam("orderId")long orderId){
		String paymethod = PaymethodConstant.PAYMETHOD_ALIPAY;
		return saveOrder(orderId, mobile, paymethod, "", ukey, model);
	}
}
