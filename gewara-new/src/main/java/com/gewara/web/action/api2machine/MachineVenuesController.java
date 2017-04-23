package com.gewara.web.action.api2machine;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.GoodsConstant;
import com.gewara.constant.TagConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.model.api.OrderResult;
import com.gewara.model.api.Synch;
import com.gewara.model.common.BaseEntity;
import com.gewara.model.goods.Goods;
import com.gewara.model.movie.Cinema;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.sport.Sport;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.service.SynchService;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;

@Controller
public class MachineVenuesController extends BaseMachineApiController{
	@Autowired@Qualifier("synchService")
	private SynchService synchService;
	/**
	 * �ṩ��ȡƱ�ն˻����ظ�gewara������Ŀǰ�����У�<ul>
	 * <li>1 : ���ݶ���
	 * <li>2 : ������Ʒ����
	 * <li>3 : ��Ӱ����
	 * <li>4 : ��Ӱ��Ʒ����
	 * </ul>
	 * @param model spring ui ModelMap
	 * @param key
	 * @param encryptCode
	 * @param recordId ������¼id
	 * @param tag �������ͣ�Ŀǰ�� sport��sport
	 * @param ticketnum
	 * @return ����xmlģ����ͼ api/ticket/toSynchronizeOrderList.vm
	 */
	@RequestMapping("/apimac/synch/downGewaOrder.xhtml")
	public String downGewaOrder(ModelMap model, Long relatedid, String tag,String ticketnum,Long sportitemid,
			@RequestParam(defaultValue="0",required=false,value="type")Integer type){
		if(relatedid==null) {
			return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		}
		model.put("downOrderSuccesstime", DateUtil.formatTimestamp(new Timestamp(System.currentTimeMillis())));
		/**����tagֵ��API�����ж� **/
		model.put("tag", tag);
		if(StringUtils.equals(Synch.TAG_SPORT,tag)){
			return downSportOrder(relatedid,model,ticketnum,sportitemid);
		}
		return this.downCinemaOrder(relatedid,model,ticketnum,type);
	}
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
	
	private void initGoodsOrder(List<Map> orderList,Long recordId,Synch synch,Cinema cinema){
		orderList.addAll(getGoodsOderList(recordId,cinema.getName(),synch,4,GoodsConstant.GOODS_TAG_BMH));
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
	
	
	@RequestMapping("/apimac/synch/equipmentStatus.xhtml")
	public String equipmentStatus(
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
		}else if(TagConstant.TAG_CINEMA.equals(type)){
			be = this.daoService.getObject(Cinema.class, sportid);
			placeName = (String) BeanUtil.get(be, "name");
		}
		map.put("id", equipmentid);
		map.put("sportid", sportid+"");
		map.put("type", type);
		map.put("equipmentType", equipmentType);
		map.put("appversion", appversion);
		map.put("sportName", placeName);
		map.put("synchTime", DateUtil.formatTimestamp(DateUtil.getMillTimestamp()));
		this.mongoService.saveOrUpdateMap(map, MongoData.SYSTEM_ID, MongoData.NS_EQUIPMENTSTATUS);
		return getSingleResultXmlView(model, "success");
	}
}
