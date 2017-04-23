package com.gewara.web.action.inner.activity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.TagConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.model.common.VersionCtl;
import com.gewara.model.draw.DrawActivity;
import com.gewara.model.draw.Prize;
import com.gewara.model.draw.WinnerInfo;
import com.gewara.model.goods.ActivityGoods;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Member;
import com.gewara.service.drama.DrawActivityService;
import com.gewara.service.order.GoodsOrderService;
import com.gewara.service.order.GoodsService;
import com.gewara.support.ErrorCode;
import com.gewara.util.ValidateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.api.BaseApiController;
import com.gewara.web.component.ShLoginService;

@Controller
public class ApiActivityGoodsController extends BaseApiController{
	@Autowired@Qualifier("goodsService")
	private GoodsService goodsService;
	
	@Autowired@Qualifier("goodsOrderService")
	private GoodsOrderService goodsOrderService;
	
	@Autowired@Qualifier("loginService")
	private ShLoginService loginService;
	
	@Autowired@Qualifier("drawActivityService")
	private DrawActivityService drawActivityService;
	
	
	@RequestMapping("/inner/activity/goods/updateGoods.xhtml")
	public String saveActivityGoods(Long gid, /*String sessid, String ip,*/ HttpServletRequest request, ModelMap model){
		/*GewaraUser gewaraUser = loginService.getLogonGewaraUserBySessid(ip, sessid);
		if(gewaraUser == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "���ȵ�¼��");
		if(!(gewaraUser instanceof User)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "û��Ȩ�ޣ�");*/
		Map<String, String> dataMap = WebUtils.getRequestMap(request);
		ErrorCode<ActivityGoods> code = goodsService.saveOrUpdateActivityGoods(PartnerConstant.GEWA_SELF, gid, dataMap);
		if(!code.isSuccess()) return getErrorXmlView(model, code.getErrcode(), code.getMsg());
		model.put("goods", code.getRetval());
		return getXmlView(model, "inner/activity/activityGoods.vm");
	}
	
	@RequestMapping("/inner/activity/goods/getGoods.xhtml")
	public String getActivityGoods(Long gid, ModelMap model){
		if(gid == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��Ʒ��Ų���Ϊ�գ�");
		ActivityGoods goods = daoService.getObject(ActivityGoods.class, gid);
		if(goods == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��Ʒ��Ϣ�����ڣ�");
		model.put("goods", goods);
		return getXmlView(model, "inner/activity/activityGoods.vm");
	}
	
	@RequestMapping("/inner/activity/goods/lockGoods.xhtml")
	public String lockGoods(Long gid, String mobile, Integer joinnum, String realname, String address, Timestamp jointime, String sessid, String ip, ModelMap model){
		Member member = loginService.getLogonMemberBySessid(ip, sessid);
		if(member == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "���ȵ�¼��");
		if(gid == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��Ʒ��Ų���Ϊ�գ�");
		if(joinnum == null || joinnum<=0) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "������Ʒ��������");
		if(!ValidateUtil.isMobile(mobile)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "�ֻ��Ŵ���");
		if(jointime == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "�μ�ʱ�䲻��Ϊ�գ�");
		ActivityGoods goods = daoService.getObject(ActivityGoods.class, gid);
		if(goods == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��Ʒ��Ϣ�����ڣ�");
		if(!goods.hasBooking()) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "����Ʒ�����⣡");
		ErrorCode<GoodsOrder> code = goodsOrderService.addActivityGoodsOrder(goods, member, null, mobile, joinnum, realname, address, jointime);
		if(!code.isSuccess()) return getErrorXmlView(model, code.getErrcode(), code.getMsg());
		model.put("order", code.getRetval());
		return getXmlView(model, "inner/activity/goodsOrder.vm");
	}
	
	
	@RequestMapping("/inner/activity/goods/getPaidSuccessOrderCount.xhtml")
	public String getPaidSuccessOrderCount(Long activityid, ModelMap model){
		List<ActivityGoods> goodsList = goodsService.getGoodsList(ActivityGoods.class, null, TagConstant.TAG_ACTIVITY, activityid, false, false, false, "id", false);
		int count = 0;
		for(ActivityGoods ag : goodsList){
			count += goodsOrderService.getGoodsOrderQuantity(ag.getId(), OrderConstant.STATUS_PAID_SUCCESS); 
		}
		return getSingleResultXmlView(model, count);
	}
	@RequestMapping("/inner/activity/goods/getPaidSuccessAndReturnOrderList.xhtml")
	public String getPaidSuccessAndReturnOrderList(Long activityid, Timestamp starttime, Timestamp endtime, ModelMap model){
		List<ActivityGoods> goodsList = goodsService.getGoodsList(ActivityGoods.class, null, TagConstant.TAG_ACTIVITY, activityid, false, false, false, "id", false);
		List<GoodsOrder> orderList = new ArrayList<GoodsOrder>();
		for(ActivityGoods ag : goodsList){
			orderList.addAll(goodsOrderService.getGoodsOrderList(ag.getId(), OrderConstant.STATUS_PAID_SUCCESS, null, null, starttime, endtime));
			orderList.addAll(goodsOrderService.getGoodsOrderList(ag.getId(), OrderConstant.STATUS_PAID_RETURN, null, null, starttime, endtime));
		}
		model.put("orderList", orderList);
		return getXmlView(model, "inner/activity/goodsOrderList.vm");
	}
	@RequestMapping("/inner/activity/goods/clickDrawByBuyCount.xhtml")
	public String clickDrawByBuyCount(String tradeno, String tag, ModelMap model){
		if(StringUtils.isBlank(tradeno) || StringUtils.isBlank(tag)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tag+"("+tradeno+")"+"��������");
		DrawActivity da = daoService.getObjectByUkey(DrawActivity.class, "tag", tag, true);
		if(da == null || !da.isJoin()) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tag+"("+tradeno+")"+"�δ��ʼ���ѽ�����");
		GoodsOrder goodsOrder = daoService.getObjectByUkey(GoodsOrder.class, "tradeNo", tradeno);
		if(goodsOrder == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tag+"("+tradeno+")"+"δ�ҵ��˻������");
		Member member = daoService.getObject(Member.class, goodsOrder.getMemberid());
		int drawtimes = drawActivityService.getMemberWinnerCount(member.getId(), da.getId(), da.getStarttime(), da.getEndtime());
		if(drawtimes > goodsOrder.getQuantity()) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tag+"("+tradeno+")"+"�û��Ʒ�����齱������ʹ����ϣ�");
		try {
			VersionCtl mvc = drawActivityService.gainMemberVc(""+member.getId());
			//FIXME:��ţ���� ��ֻ��һ����Ʒ����Ʊ��ֱ�Ӱ󶨵��˺�
			ErrorCode<WinnerInfo> ec = drawActivityService.baseClickDraw(da, mvc, false, member);
			if(ec == null || !ec.isSuccess()) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tag+"("+tradeno+")"+"��Ʒ�ѷ��꣡");
			WinnerInfo winnerInfo = ec.getRetval();
			if(winnerInfo == null) getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tag+"("+tradeno+")"+"��Ʒ�ѷ��꣡");
			Prize prize = daoService.getObject(Prize.class, winnerInfo.getPrizeid());
			if(prize == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tag+"("+tradeno+")"+"��Ʒ�ѷ��꣡");
			SMSRecord sms = drawActivityService.sendPrize(prize, winnerInfo, true);
			if(sms !=null) untransService.sendMsgAtServer(sms, false);
		}catch(StaleObjectStateException e){
			return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tag+"("+tradeno+")"+"ϵͳ����");
		}catch(HibernateOptimisticLockingFailureException e){
			return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, tag+"("+tradeno+")"+"ϵͳ����");
		}
		return getSingleResultXmlView(model, "success");
	}
	@RequestMapping("/inner/activity/goods/getOrderInfo.xhtml")
	public String getOrderInfo(@RequestParam(required = true, value = "tradeNo") String tradeNo, ModelMap model){
		GoodsOrder order = daoService.getObjectByUkey(GoodsOrder.class, "tradeNo", tradeNo, false);
		if (order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "����������!");
		model.put("order", order);
		return getXmlView(model, "inner/activity/goodsOrder.vm");
	}
	
}

