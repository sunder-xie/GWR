package com.gewara.web.action.api;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.GoodsConstant;
import com.gewara.constant.TagConstant;
import com.gewara.constant.sys.ConfigConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.helper.SportOrderHelper;
import com.gewara.json.SportUpGrade;
import com.gewara.model.api.OrderResult;
import com.gewara.model.api.Synch;
import com.gewara.model.common.BaseEntity;
import com.gewara.model.common.GewaConfig;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.drama.Theatre;
import com.gewara.model.goods.Goods;
import com.gewara.model.movie.Cinema;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.sport.OpenTimeItem;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.sport.Sport;
import com.gewara.model.sport.SportItem;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.service.SynchService;
import com.gewara.service.sport.SportOrderService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.gym.SynchGymService;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.xmlbind.gym.RemoteGym;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Controller
public class ApiVenuesController extends BaseApiController {
	@Autowired@Qualifier("sportOrderService")
	private SportOrderService sportOrderService;
	@Autowired@Qualifier("synchService")
	private SynchService synchService;
	@Autowired@Qualifier("synchGymService")
	private SynchGymService synchGymService;
	
	@RequestMapping("/api/synch/checkTicket.xhtml")
	public String checkTicket(String key,String encryptCode,ModelMap model,String type,
			String equipmentid,Long sportid,Long itemid,String subMobile,String checkpass){
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		if(StringUtils.isBlank(equipmentid))return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "�豸id����Ϊ�գ�");
		String result = null;
		if(TagConstant.TAG_SPORT.equals(type)){
			result = checkSportOrder(checkpass, subMobile, sportid, equipmentid, itemid, model);
		}else if(TagConstant.TAG_CINEMA.equals(type)){
			result = checkGoodsOrder(checkpass, subMobile, sportid, equipmentid, model);
		}else if(TagConstant.TAG_DRAMA.equals(type)){
			result = checkDramaOrder(checkpass, subMobile, sportid, equipmentid, model);
		}else{
			return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "�������ʹ���");
		}
		return result;
	}
	
	private String checkDramaOrder(String checkpass, String subMobile, Long sportid, String equipmentid, ModelMap model){
		DramaOrder dorder = this.getDramaOrderByPass(checkpass, sportid);		
		if(dorder == null)return checkGoodsOrder(checkpass, subMobile, sportid, equipmentid, model);
		String result = this.checkGewaOrder(dorder, subMobile, !dorder.getTheatreid().equals(sportid),equipmentid,OrderResult.ORDERTYPE_DRAMA, model);
		if(result != null)return result;
		OpenDramaItem odi=daoService.getObjectByUkey(OpenDramaItem.class, "dpid", dorder.getDpid(), false);
		model.put("date", DateUtil.format(odi.getPlaytime(), "MM��dd��")+DateUtil.format(odi.getPlaytime(), "HH:mm"));
		model.put("dramaname", odi.getDramaname());
		model.put("sportname", odi.getTheatrename());
		model.put("price", dorder.getUnitprice());
		model.put("quantity", dorder.getQuantity());
		model.put("tradeNo", dorder.getTradeNo());
		model.put("type", "drama");
		return getXmlView(model, "api/ticket/ticketInfo.vm");
	}
	
	private String checkSportOrder(String checkpass, String subMobile, Long sportid, String equipmentid, Long itemid, ModelMap model){
		SportOrder sorder = this.getSportOrderByPass(checkpass,sportid,itemid);
		if(sorder == null) return checkGoodsOrder(checkpass, subMobile, sportid,equipmentid, model);
		String result = this.checkGewaOrder(sorder, subMobile,!sorder.getSportid().equals(sportid),equipmentid,OrderResult.ORDERTYPE_SPORT, model);
		if(result != null)return result;
		List<OpenTimeItem> otiList = sportOrderService.getMyOtiList(sorder.getId());
		OpenTimeTable table = daoService.getObject(OpenTimeTable.class, sorder.getMpid());
		model.put("validtime", DateUtil.format(table.getPlaydate(), "yyyy-MM-dd"));
		model.put("itemname", table.getItemname());
		model.put("sportname", table.getSportname());
		model.put("hour", getFieldStr(SportOrderHelper.getFieldText(otiList)));
		model.put("sorder", sorder);
		model.put("tradeNo", sorder.getTradeNo());
		model.put("type", "sport");
		return getXmlView(model, "api/ticket/ticketInfo.vm");
	}
	
	private String checkGoodsOrder(String checkpass, String subMobile, Long sportid, String equipmentid, ModelMap model){
		GoodsOrder gorder = this.getGoodsOrderByPass(checkpass);
		if(gorder == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "�ֻ�ĩ��λ��ȡƱ�������");
		Goods goods = this.daoService.getObject(Goods.class, gorder.getGoodsid());
		String result = this.validGoodsOrder(gorder, goods, model);
		if(result != null)return result;
		result = this.checkGewaOrder(gorder, subMobile, !goods.getRelatedid().equals(sportid), equipmentid,OrderResult.ORDERTYPE_MEAL, model);
		if(result != null)return result;
		String sportname = this.getSportNameById(sportid);
		model.put("sportname", sportname);
		model.put("goodsname", goods.getShortname());
		Timestamp validtime = goods.getTovalidtime() != null ? goods.getTovalidtime() : gorder.getValidtime();
		model.put("validtime", DateUtil.format(validtime,"MM��dd��"));
		model.put("unitprice", goods.getUnitprice());
		model.put("quantity", gorder.getQuantity());
		model.put("summary", goods.getPrintcontent());
		model.put("tradeNo", gorder.getTradeNo());
		model.put("type", "cinema");
		return getXmlView(model, "api/ticket/ticketInfo.vm");
	}
	
	private String checkGewaOrder(GewaOrder gewaOrder, String subMobile, boolean checkSportid,String equipmentid,String type, ModelMap model){
		String mobile = gewaOrder.getMobile().substring(7, 11);
		if(!StringUtil.md5(mobile).equals(subMobile))return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "�ֻ�ĩ��λ��ȡƱ�������");
		OrderResult orderResult = this.updateOrderResult(gewaOrder,type);
		if(orderResult == null)return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "�˶����Ѿ�ȡ��Ʊ�ˣ�");
		if(checkSportid) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "�뵽��Ʊ����ȡƱ��");
		orderResult.setCaption(equipmentid);
		this.daoService.saveObject(orderResult);
		return null;
	}
	
	private String validGoodsOrder(GoodsOrder gorder,Goods goods, ModelMap model){
		long currentTime = System.currentTimeMillis();
		if(goods.getFromvalidtime() != null && goods.getFromvalidtime().getTime() > currentTime){
			return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��δ��ȡƱʱ�䣡");
		}
		if(goods.getTovalidtime() != null && goods.getTovalidtime().getTime() < currentTime){
			return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "�����ѹ���");
		}
		if(goods.getFromvalidtime() == null && goods.getTovalidtime() == null ){
			if(gorder.getValidtime().getTime() < currentTime){
				return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "�����Ѿ����ڣ�");
			}
		}
		return null;
	}
	
	private String getFieldStr(String hour){
		String hourStr = "";
		String [] hourArr = hour.split("\\)");
		for (int i = 0; i < hourArr.length; i++) {
			if(hourArr[i].indexOf("(") != -1)
			hourStr +=  "@" + hourArr[i].replace("(", "");
		}
		return hourStr.replaceFirst("@", "");
	}
	
	private OrderResult updateOrderResult(GewaOrder sorder,String type){
		OrderResult orderResult = this.daoService.getObject(OrderResult.class , sorder.getTradeNo());
		if(orderResult == null){
			orderResult = new OrderResult();
			orderResult.setIstake("N");
			orderResult.setResult("Y");
			orderResult.setTradeno(sorder.getTradeNo());
			orderResult.setTaketime(new Timestamp(System.currentTimeMillis()));
			orderResult.setUpdatetime(new Timestamp(System.currentTimeMillis()));
			orderResult.setTicketnum(sorder.getQuantity());
			orderResult.setOrdertype(type);
		}else if(!"Y".equals(orderResult.getIstake())){
			orderResult.setIstake("N");
			orderResult.setTaketime(new Timestamp(System.currentTimeMillis()));
			orderResult.setUpdatetime(new Timestamp(System.currentTimeMillis()));
		}else{
			orderResult.setUpdatetime(new Timestamp(System.currentTimeMillis()));
			this.daoService.saveObject(orderResult);
			return null;
		}
		return orderResult;
	}
	
	private String getSportNameById(long sportid){
		String sportname = null;
		Cinema cinema = this.daoService.getObject(Cinema.class, sportid);
		if(cinema == null){
			Sport sport =this.daoService.getObject(Sport.class, sportid);
			if(sport == null){
				Theatre theatre = this.daoService.getObject(Theatre.class, sportid);
				sportname = theatre.getName();
			}else{
				sportname = sport.getName();
			}
		}else{
			sportname = cinema.getName();
		}
		return sportname;
	}
	
	@RequestMapping("/api/synch/sysConfig.xhtml")
	public String synchConfig(String key,String encryptCode,ModelMap model,
			String equipmentid,Long sportid,String itemid){
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		List<SportItem> itemList = this.daoService.getAllObjects(SportItem.class);
		model.put("itemList", itemList);
		model.put("equipmentid", equipmentid);
		model.put("sportid", sportid);
		model.put("itemid", itemid);
		String checkTicket_url = daoService.getObject(GewaConfig.class, ConfigConstant.CFG_CHECKTICKET).getContent();
		model.put("checkTicket_url", checkTicket_url);
		return getXmlView(model, "api/ticket/syshConfig.vm");
	}

	@SuppressWarnings("deprecation")
	@RequestMapping("/api/synch/printSuccess.xhtml")
	public String printSuccess(String key,String encryptCode,String tradeNo, ModelMap model){
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_API, "tradeNo:"+tradeNo+"key:"+key);
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		if(StringUtils.isBlank(tradeNo))return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "�����ò���Ϊ�գ�");
		OrderResult orderResult = this.daoService.getObject(OrderResult.class ,tradeNo);
		orderResult.setIstake("Y");
		orderResult.setUpdatetime(new Timestamp(System.currentTimeMillis()));
		this.daoService.saveObject(orderResult);
		return this.getDirectXmlView(model, "<result>true</result>");
	}
	
	private SportOrder getSportOrderByPass(String checkpass,Long sportid,Long itemid){
		DetachedCriteria query = DetachedCriteria.forClass(SportOrder.class);
		query.add(Restrictions.eq("checkpass",checkpass));
		query.add(Restrictions.eq("sportid",sportid));
		query.add(Restrictions.eq("itemid",itemid));
		query.addOrder(Order.desc("createtime"));
		List<SportOrder> list = this.hibernateTemplate.findByCriteria(query,0,1);
		return list.isEmpty()?null:list.get(0);
	}
	
	private GoodsOrder getGoodsOrderByPass(String checkpass){
		DetachedCriteria query = DetachedCriteria.forClass(GoodsOrder.class);
		query.add(Restrictions.eq("checkpass",checkpass));
		query.addOrder(Order.desc("createtime"));
		List<GoodsOrder> list = this.hibernateTemplate.findByCriteria(query,0,1);
		return list.isEmpty()?null:list.get(0);
	}
	
	private DramaOrder getDramaOrderByPass(String checkpass,Long sportid){
		DetachedCriteria query = DetachedCriteria.forClass(DramaOrder.class);
		query.add(Restrictions.eq("checkpass",checkpass));
		query.add(Restrictions.eq("theatreid",sportid));
		query.addOrder(Order.desc("createtime"));
		List<DramaOrder> list = this.hibernateTemplate.findByCriteria(query,0,1);
		return list.isEmpty()?null:list.get(0);
	}
	
	/**
	 * pos/pad�豸״̬����
	 * @param key
	 * @param encryptCode
	 * @param equipmentid
	 * @param sportid
	 * @param relatedid
	 * @param type
	 * @param model
	 * @return
	 */
	//TODO:ɾ�����Ƴ�mongo
	@SuppressWarnings("deprecation")
	@RequestMapping("/api/synch/equipmentStatus.xhtml")
	public String equipmentStatus(
			String key,
			String encryptCode,
			String equipmentid,
			@RequestParam(required=false,value="equipmentType",defaultValue="pos")
			String equipmentType,
			Long sportid,
			Long relatedid,
			String type,
			String appversion,
			ModelMap model){
		if(sportid == null){
			sportid = relatedid;
		}
		
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		if(StringUtils.isBlank(equipmentid))return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "�豸id����Ϊ�գ�");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(MongoData.SYSTEM_ID, equipmentid);
		Map<String, Object> map = this.mongoService.findOne(MongoData.SYSTEM_ID, paramMap);
		if(map == null){
			map = new HashMap<String, Object>();
			map.put(MongoData.SYSTEM_ID, equipmentid);
		}
		BaseEntity be = null;
		String placeName = "����id����";
		
		if(TagConstant.TAG_SPORT.equals(type)){
			be = this.daoService.getObject(Sport.class, sportid);
			placeName = (String) BeanUtil.get(be, "name");
		}else if(TagConstant.TAG_DRAMA.equals(type)){
			be = this.daoService.getObject(Theatre.class, sportid);
			placeName = (String) BeanUtil.get(be, "name");
		}else if(TagConstant.TAG_CINEMA.equals(type)){
			be = this.daoService.getObject(Cinema.class, sportid);
			placeName = (String) BeanUtil.get(be, "name");
		}else if(TagConstant.TAG_GYM.equals(type)){
			ErrorCode<RemoteGym> code = synchGymService.getRemoteGym(sportid, true);
			if(code.isSuccess()){
				placeName = (String) BeanUtil.get(code.getRetval(), "name");
			}
		}

		map.put("id", equipmentid);
		map.put("sportid", sportid+"");
		map.put("type", type);
		map.put("equipmentType", equipmentType);
		map.put("appversion", appversion);
		map.put("sportName", placeName);
		map.put("synchTime", DateUtil.formatTimestamp(DateUtil.getMillTimestamp()));
		this.mongoService.saveOrUpdateMap(map, MongoData.SYSTEM_ID, MongoData.NS_EQUIPMENTSTATUS);
		return getDirectXmlView(model, "<result>success</result>");
	}
	/**
	 * �ն˻����ض����������ϸ�������
	 * @param model spring ui ModelMap
	 * @param results ���ض���������Ϣ��ʽ(���������)��1.������@ȡƱʱ��#������@ȡƱʱ�� ��ʾ�û���ͨ���ն˻�ȡƱ�ɹ�����������ɹ�
						2.������#������ ��ʾ�ն˻����ض��������ش洢�ɹ�
	 * @return xml��ͼģ��
	 */
	@RequestMapping("/api/synch/ticketFeedback.xhtml")
	public String ticketFeedback(ModelMap model,String results ){
		if(StringUtils.isBlank(results)) {
			return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "��������");
		}
		dbLogger.warn("API: ticket feedback results:" + results);
		String[] orderResults = StringUtils.split(results, "#");//����ؽ����и����
		for(String result : orderResults){
			String[] tickets = StringUtils.split(result, "@");//��Ե���������@�и�õ������ź�ȡƱʱ��
			OrderResult orderResult = daoService.getObject(OrderResult.class, StringUtils.trim(tickets[0]));
			if(orderResult == null){
				orderResult = new OrderResult();
				orderResult.setTradeno(StringUtils.trim(tickets[0]));
			}
			if(tickets.length > 1){
				orderResult.setTaketime(DateUtil.parseTimestamp(tickets[1]));
				orderResult.setIstake("Y");
			}
			GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", orderResult.getTradeno(), false);
			if(order != null) {
				orderResult.setOrdertype(getOrderTypeByGewaOrder(order));
				orderResult.setTicketnum(order.getQuantity());
				orderResult.setUpdatetime(new Timestamp(System.currentTimeMillis()));
				Long placeid = null;
				if(order instanceof TicketOrder){
					placeid = ((TicketOrder)order).getCinemaid();
				}else if(order instanceof GoodsOrder){
					placeid = ((GoodsOrder)order).getPlaceid();
				}
				orderResult.setPlaceid(placeid);
				daoService.saveObject(orderResult);
			}
			dbLogger.warn("API: ticket feedback over:");
		}
		model.put("successtime", new Timestamp(System.currentTimeMillis()));
		return getXmlView(model, "api/ticket/ticketStatus.vm");
	}
	/**
	 * ����GewaOrder����������������õ� orderType
	 * @param order
	 * @return
	 */
	private String getOrderTypeByGewaOrder(GewaOrder order){
		if(order instanceof TicketOrder) {
			return OrderResult.ORDERTYPE_TICKET;
		}else if(order instanceof GoodsOrder){
			return OrderResult.ORDERTYPE_MEAL;
		}else if(order instanceof DramaOrder){
			return OrderResult.ORDERTYPE_DRAMA;
		}else if(order instanceof SportOrder){
			return OrderResult.ORDERTYPE_SPORT;
		}
		return "";
	}
	/**
	 * �ṩ��ȡƱ�ն˻����ظ�gewara������Ŀǰ�����У�<ul>
	 * <li>1 : ���ݶ���
	 * <li>2 : ������Ʒ����
	 * <li>3 : ��Ӱ����
	 * <li>4 : ��Ӱ��Ʒ����
	 * <li>5 : ���綩��
	 * <li>6 : ������Ʒ����
	 * </ul>
	 * @param model spring ui ModelMap
	 * @param key
	 * @param encryptCode
	 * @param recordId ������¼id
	 * @param tag �������ͣ�Ŀǰ�� drama��sport��sport
	 * @param ticketnum
	 * @return ����xmlģ����ͼ api/ticket/toSynchronizeOrderList.vm
	 */
	@RequestMapping("/api/synch/downGewaOrder.xhtml")
	public String downGewaOrder(ModelMap model,String key,String encryptCode,Long relatedid, String tag,String ticketnum,Long sportitemid,
			@RequestParam(defaultValue="0",required=false,value="type")Integer type){
		ApiAuth apiAuth = checkRights(encryptCode, ""+relatedid, key);
		if(!apiAuth.isChecked()) {
			return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		}
		if(relatedid==null) {
			return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		}
		model.put("downOrderSuccesstime", DateUtil.format(new Timestamp(System.currentTimeMillis()),"yyyy-MM-dd HH:mm:ss"));
		/**����tagֵ��API�����ж� **/
		model.put("tag", tag);
		if(StringUtils.equals(Synch.TAG_SPORT,tag)){
			return downSportOrder(relatedid,model,ticketnum,sportitemid);
		}
		return downCinemaOrder(relatedid,model,ticketnum,type);
	}
	/**
	 * ���ص�Ӱ����
	 * @param apiUser
	 * @param recordId
	 * @param model
	 * @param ticketnum
	 * @return
	 */
	private String downCinemaOrder(Long recordId,ModelMap model,String ticketnum,Integer type){
		Cinema cinema = daoService.getObject(Cinema.class, recordId);
		if(cinema == null){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "û�и�ӰԺ��");
		}
		Synch synch = null;
		if(StringUtils.equals(type+"", "0")){
			synch = getGoodsSynch(recordId, Synch.TGA_CINEMA, ticketnum);
		}else {
			synch = getSynch(recordId,Synch.TGA_CINEMA,ticketnum);
		}
		//a) ��type=0ʱ���ӿڽ����ظ����Ǹ�ӰԺ��Ʒ����
	    //b) ��type=1ʱ���ӿڽ����ظ�ӰԺ��Ӱ����
	    //c) ��type=2ʱ���ӿڷ��ظ�ӰԺ��Ʒ+��Ӱ����
		List<Map> orderList = new ArrayList<Map>();
		if(type==0){
			initGoodsOrder(orderList,recordId,synch,cinema);
		}else if(type==1){
			initTicketOrder(recordId, cinema, synch, orderList);
		}else if(type==2){
			initGoodsOrder(orderList,recordId,synch,cinema);
			initTicketOrder(recordId, cinema, synch, orderList);
		}
		model.put("orderList", orderList);
		return getXmlView(model,"api/ticket/toSynchronizeOrderList.vm");
	}

	private void initTicketOrder(Long recordId, Cinema cinema, Synch synch,
			List<Map> orderList) {
		List<TicketOrder> cinemaOrderList = synchService.getOrderListByCinemaIdAndLasttime(recordId, DateUtil.addSecond(synch.getSuccesstime(), -10));
		for(TicketOrder cinemaOrder:cinemaOrderList){
			//{"ӰƬ":"���ӵ���","����":"12��30�� 22:00","ӰƱ":"B��15��30Ԫ,B��16��30Ԫ"}
			Map<String, String> descriptionMap = JsonUtils.readJsonToMap(cinemaOrder.getDescription2());
			String playDate = descriptionMap.get("����");
			OpenPlayItem opi = daoService.getObjectByUkey(OpenPlayItem.class, "mpid", cinemaOrder.getMpid(), true);
			if(opi != null){
				playDate = DateUtil.format(opi.getPlaytime(),"yyyy-MM-dd HH:mm:ss");
			}
			orderList.add(getOrderMap(
					cinemaOrder.getTradeNo(),cinemaOrder.getMobile().substring(7),
					cinema.getName(),descriptionMap.get("ӰƬ"),playDate,
					StringUtils.replace(descriptionMap.get("ӰƱ"),",","@"),StringUtil.md5(cinemaOrder.getCheckpass()),
					3,cinemaOrder.getQuantity(),cinemaOrder.getTotalfee(),DateUtil.formatTimestamp(cinemaOrder.getAddtime()),
					cinemaOrder.getMpid(),recordId,getSyncType(cinemaOrder.getTradeNo())));
		}
	}
	
	private void initGoodsOrder(List<Map> orderList,Long recordId,Synch synch,Cinema cinema){
		orderList.addAll(getGoodsOderList(recordId,cinema.getName(),synch,4,GoodsConstant.GOODS_TAG_BMH));
	}
	
	/**
	 * get ͬ������
	 * ����ͬ������������Ѿ����ڴ˶���������ȡƱ״̬�����ֶ�ȫ�����ǡ�1
	 * ��ǿ��ͬ���������ֶ�����ȫ������ 2
	 * ��ɾ����ɾ���˶��� 3
	 * @param tradeNo
	 * @return
	 */
	private String getSyncType(String tradeNo){
		OrderResult orderResult = daoService.getObject(OrderResult.class, tradeNo);
		String flag = "1";
		if(orderResult != null){
			flag = orderResult.getResult();
		}
		if(OrderResult.RESULTU.equals(flag)){
			flag = "2";
		}else if(OrderResult.RESULTD.equals(flag)){
			flag = "3";
		}else{
			flag = "1";
		}
		return flag;
	}
	/**
	 * �����˶����ݶ���
	 * @param apiUser
	 * @param recordId
	 * @param model
	 * @param ticketnum
	 * @return ������ͼxmlģ��
	 */
	private String downSportOrder(Long recordId, ModelMap model, String ticketnum, Long sportitemid){
		Sport sport = daoService.getObject(Sport.class, recordId);
		if(sport == null){
			return getErrorXmlView(model,ApiConstant.CODE_DATA_ERROR,"û�и��˶�����");
		}
		Synch synch = getSynch(recordId,Synch.TAG_SPORT,ticketnum);
		List<SportOrder> sportOrderList = synchService.getOrderListBySportIdAndLasttime(recordId, sportitemid, DateUtil.addSecond(synch.getSuccesstime(), -10));
		List<Map> orderList = new ArrayList<Map>();
		for(SportOrder sportOrder:sportOrderList){
			//{"��ϸ":"(1�ų��أ�15:00)(2�ų��أ�15:00)","�˶���Ŀ":"��ë��A","�˶�����":"������ë���","ʱ��":"2011-12-30"}
			Map<String, String> descMap = JsonUtils.readJsonToMap(sportOrder.getDescription2());
			String detail = "";
			if(StringUtils.isNotBlank(descMap.get("ʱ��"))){
				detail = paseSportDetail(descMap.get("��ϸ"),true) + "@" + descMap.get("ʱ��");
			}else{
				detail =  paseSportDetail(descMap.get("��ϸ"),false);
			}
			OpenTimeTable ott = daoService.getObjectByUkey(OpenTimeTable.class, "id", sportOrder.getMpid(), true);
			String playDate = DateUtil.format(ott.getPlaydate(),"yyyy-MM-dd HH:mm:ss");
			orderList.add(getOrderMap(sportOrder.getTradeNo(),sportOrder.getMobile().substring(7),
					descMap.get("�˶�����"),descMap.get("�˶���Ŀ"),playDate,
					detail,StringUtil.md5(sportOrder.getCheckpass()),
					1,sportOrder.getQuantity(),sportOrder.getTotalfee(),DateUtil.formatTimestamp(sportOrder.getAddtime()),
					sportOrder.getOttid(),recordId,this.getSyncType(sportOrder.getTradeNo())));
		}
		orderList.addAll(getGoodsOderList(recordId,sport.getName(),synch,2,GoodsConstant.GOODS_TAG_BMH_SPORT));
		model.put("orderList", orderList);
		return getXmlView(model,"api/ticket/toSynchronizeOrderList.vm");
	}
	/**
	 * ���������
	 * "21:00-22:00 17�ų��� 50Ԫ;20:00-21:00 18�ų��� 50Ԫ;21:00-22:00 19�ų��� 50Ԫ;22:00-23:00 20�ų��� 50Ԫ;"
	 * (1�ų��أ�15:00)(2�ų��أ�15:00)
	 * �ַ���
	 * @param detail
	 * @param swin �Ƿ�����Ӿ
	 * @return
	 */
	private  String paseSportDetail(String detail,boolean swin){
		if(detail.indexOf("(") != -1){
			detail = StringUtils.replace(detail, ")(", "@");
			detail = StringUtils.replace(detail, "(", "");
			detail = StringUtils.replace(detail, ")", "");
		}else{
			String[] details = StringUtils.split(detail,";");
			StringBuilder sb = new StringBuilder();
			for(String str:details){
				if(StringUtils.isNotBlank(str)){
					String[] strs = StringUtils.split(str," ");
					if(swin){
						return strs[0];
					}
					sb.append(strs[1]).append("��").append(strs[0]).append(" ").append(strs[2]);
				}
				sb.append("@");
			}
			detail = sb.toString();
			if(detail.lastIndexOf("@") == detail.length() - 1){
				detail = detail.substring(0, detail.length() - 1);
			}
		}
		return detail;
	}
	
	/**
	 * ��ȡ��Ӧ�Ļ�����Ʒ��������Ӱ��Ʒ�������˶���Ʒ����
	 * @param apiUser �����û�
	 * @param recordId ����id
	 * @param businissName spring ui ModelMap
	 * @param synch
	 * @param orderType ��ͬ�Ķ������ͣ����磺������Ʒ����(orderType = 6)
	 * @return ��Ʒ������ͼxmlģ��
	 */
	private List<Map> getGoodsOderList(Long recordId,String businissName,Synch synch,int orderType,String goosType){
		Timestamp suctime = synch.getSuccesstime();
		if(synch.getGsuctime()!=null && suctime!=null && synch.getGsuctime().after(suctime)){
			suctime = synch.getGsuctime();
		}
		List<GoodsOrder> goodsOrderList = synchService.getGoodsOrderListByRelatedidAndLasttime(recordId, DateUtil.addSecond(suctime, -10),goosType);
		List<Map> orderList = new ArrayList<Map>();
		StringBuilder sb = new StringBuilder();
		for(GoodsOrder order : goodsOrderList){
			Goods goods = daoService.getObject(Goods.class, order.getGoodsid());
			String printcontent = goods.getPrintcontent();
			if(StringUtils.isNotBlank(printcontent)){
				if(printcontent.indexOf("+") != -1){
					printcontent = StringUtils.replace(printcontent,"+","@");
				}else{
					printcontent = StringUtils.replace(printcontent,",","@");
				}
			}else {
				printcontent = "";
			}
			orderList.add(getOrderMap(order.getTradeNo(), order.getMobile().substring(7),
					businissName,goods.getShortname(),DateUtil.format(order.getValidtime(),"yyyy-MM-dd HH:mm:ss"),
						printcontent,StringUtil.md5(order.getCheckpass()),orderType,
							order.getQuantity(),order.getDue(),DateUtil.formatTimestamp(order.getAddtime()),
							order.getGoodsid(),recordId,getSyncType(order.getTradeNo())));//
			sb.append(order.getTradeNo()).append(",");
		}
		this.dbLogger.warn("����ͬ������Ʒ�����ŷֱ�Ϊ��" + sb.toString());
		return orderList;
	}
	//TODO: �ع�
	/**
	 * ��װ������ͼxmlģ���ļ��ж�Ӧkey=value��
	 * @param objects {"OrderCode","Mobile","BusinissName","ItemName",
				"PlayDate","OrderDetail","CheckPassword", "OrderType", "TotalNum", 
				"TotalPrice", "OrderTime"} ��Ӧ��˳��value
	 * @return map
	 */
	private Map getOrderMap(Object...objects){
		String [] fieldArr = {"OrderCode","Mobile","BusinissName","ItemName",
				"PlayDate","OrderDetail","CheckPassword", "OrderType", "TotalNum", 
				"TotalPrice", "OrderTime","StadiumDetailID","StadiumID","SyncType"};
		Map order = new LinkedHashMap();
		for(int index = 0;index < objects.length;index++){
			order.put(fieldArr[index], objects[index]);
		}
		return order;
	}
	/**
	 * ��ȡ���ض�����ͬ����¼��Ϣ
	 * @param recordId
	 * @param synchTag
	 * @param ticketnum
	 * @return
	 */
	private Synch getSynch(Long recordId,String synchTag,String ticketnum){
		Synch synch = daoService.getObject(Synch.class, recordId);
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		Timestamp curDate = DateUtil.addDay(DateUtil.getCurTruncTimestamp(), -8);
		if(synch == null){
			synch = new Synch(recordId,synchTag);
		}else if(synch.getSuccesstime() != null && curDate.before(synch.getSuccesstime())){
			curDate = null;
		}
		synch = apiService.saveSynchWithCinema(synch, cur, curDate, ticketnum, null);
		return synch;
	}
	
	
	private Synch getGoodsSynch(Long recordId,String synchTag,String ticketnum){
		Synch synch = daoService.getObject(Synch.class, recordId);
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		Timestamp curDate = DateUtil.addDay(DateUtil.getCurTruncTimestamp(), -8);
		if(synch == null){
			synch = new Synch(recordId,synchTag);
		}else if(synch.getSuccesstime() != null && curDate.before(synch.getSuccesstime())){
			curDate = null;
		}
		synch = apiService.saveSynchGoodsWithCinema(synch, cur, curDate, ticketnum, null);
		return synch;
	}
	
	/**
	 * �˶��ն˻�Ӧ�������ӿ�
	 * @param key
	 * @param encryptCode
	 * @param apptype Ӧ������ ��ѡֵ:SPORT_ITS(�����ն�ϵͳ),SPORT_BOOKING(��ë������Ԥ��ϵͳ)
	 * @param model
	 * @return
	 */
	@RequestMapping("/api/synch/getUpGradeFile.xhtml")
	public String getUpGradeFile(
			String key,
			String encryptCode,
			@RequestParam(required=false,value="apptype",defaultValue=SportUpGrade.SPORT_APP_BOOKING) String apptype,
			ModelMap model){
		if(StringUtils.isNotBlank(encryptCode)){
			ApiAuth apiAuth = checkRights(encryptCode, key);
			if(!apiAuth.isChecked()) {
				return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
			}
		}
		DBObject params=new BasicDBObject();
		params.put("apptype", apptype.trim());
		List<SportUpGrade> upGradeList=mongoService.getObjectList(SportUpGrade.class, params, "addTime", false, 0, 1);
		model.put("upGrade", upGradeList.isEmpty() ? null : upGradeList.get(0));
		return getXmlView(model, "api/sport/sportUpGrade.vm");
	}
}
