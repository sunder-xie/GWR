package com.gewara.service.ticket.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gewara.Config;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.sys.CacheConstant;
import com.gewara.constant.ticket.OpiConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.helper.order.TicketOrderContainer;
import com.gewara.helper.ticket.TicketUtil;
import com.gewara.model.api.ApiUser;
import com.gewara.model.common.LastOperation;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.Order2SellSeat;
import com.gewara.model.ticket.SellSeat;
import com.gewara.service.OrderException;
import com.gewara.service.impl.BaseServiceImpl;
import com.gewara.service.ticket.TicketOrderService;
import com.gewara.service.ticket.WandaService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CacheService;
import com.gewara.untrans.monitor.MonitorService;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.untrans.ticket.RemoteTicketService;
import com.gewara.untrans.ticket.TicketRollCallService;
import com.gewara.util.DateUtil;
import com.gewara.util.HttpUtils;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.xmlbind.ticket.TicketRemoteOrder;
import com.gewara.xmlbind.ticket.WdParam;

@Service("wandaService")
public class WandaServiceImpl extends BaseServiceImpl implements WandaService{
	private int MAX_MINUTS_TICKETS = 15;	//��ӰƱ���������ʱ�䣨���ӣ�
	@Autowired@Qualifier("cacheService")
	private CacheService cacheService;
	@Autowired@Qualifier("config")
	private Config config;
	@Autowired@Qualifier("ticketOrderService")
	private TicketOrderService ticketOrderService;
	@Value("${wd.webUrl}")
	private String wandaUrl;
	@Value("${wd.wapUrl}")
	private String wapUrl;
	@Value("${wd.thirdPartyCode}")
	private String thirdPartyCode;
	@Autowired@Qualifier("remoteTicketService")
	private RemoteTicketService remoteTicketService;
	@Autowired@Qualifier("ticketRollCallService")
	private TicketRollCallService ticketRollCallService;
	@Autowired@Qualifier("monitorService")
	private MonitorService monitorService;
	@Override
	public TicketOrderContainer createTicketOrder(OpenPlayItem opi, Long memberid, String membername, String ukey, String mobile, 
			TicketRemoteOrder remoteOrder, String randomNum, String tradeNo, ApiUser partner) throws OrderException{
		//1���������
		if(opi==null) throw new OrderException(ApiConstant.CODE_NOT_EXISTS, "���β����ڣ�");
		Map<String, String> remoteInfoMap = JsonUtils.readJsonToMap(remoteOrder.getOtherinfo());
		
		String[] priceList = StringUtils.split(remoteInfoMap.get("price"), "|");
		//���Զ���۸񣬼�¼�ܼ�
		//TODO:ֱ��
		int costprice = new Double(priceList[0]).intValue(); //ʹ�����ĳɱ���
		if(costprice != opi.getCostprice()){
			sendWarn(opi);
			throw new OrderException(ApiConstant.CODE_OPI_UNSYNCH, "���μ۸��Ѹ��ģ����Ժ����ԣ�");
		}

		Long partnerid = PartnerConstant.GEWA_SELF;
		if(partner!=null){
			partnerid = partner.getId();
			membername = membername + "@" + partner.getBriefname();
		}

		//1����֧���ֳ���Ʊ
/*		if(PayConstant.isMacBuy(partnerId)){
			costprice = opi.getPrice()-opi.getFee();
		}
*/		
		//2����֧������۸�
		/*SeatPriceHelper sph = openPlayService.getSeatPriceHelper(opi, order.getAddtime(), partnerId);*/
		//3���Ȳ�֧������
/*		order.setPaybank(paybank);
		if(StringUtils.isNotBlank(paymethod)){
			order.setPaymethod(paymethod);
		}
*/		
		TicketOrder order = new TicketOrder(memberid, membername, ukey, opi);

		//4����֧�������̼�
		order.setPartnerid(partnerid);
		
		String odertitle = opi.getCinemaname()+"��ӰƱ";
		Timestamp validtime = DateUtil.addMinute(order.getAddtime(), MAX_MINUTS_TICKETS);

		List<SellSeat> seatList = checkAndCreateSeat(opi, remoteOrder.getSeatno());

		order.setTradeNo(tradeNo);
		order.setOrdertitle(odertitle);
		order.setMobile(mobile);
		order.setValidtime(validtime);
		order.setCheckpass(randomNum);
		order.setCategory(opi.getOpentype());
		order.setQuantity(seatList.size());
		int totalfee = opi.getGewaprice() * seatList.size();
		order.setUnitprice(opi.getGewaprice());
		order.setTotalfee(totalfee);
		order.setCostprice(costprice);

		int totalcost = costprice * order.getQuantity();
		order.setTotalcost(totalcost);
		//ֱ������״̬
		
		for(SellSeat seat :seatList){
			seat.setValidtime(validtime);
			seat.setRemark(StringUtils.substring("[��" + membername + "]" + StringUtils.defaultString(seat.getRemark()), 0, 500));
		}

		TicketUtil.setOrderDescription(order, seatList, opi);
		baseDao.saveObject(order);
		
		List<Order2SellSeat> o2sList = new ArrayList<Order2SellSeat>();
		for(SellSeat seat :seatList){
			seat.setOrderid(order.getId());
			o2sList.add(new Order2SellSeat(order.getId(), seat.getId()));
		}

		baseDao.saveObjectList(seatList);
		baseDao.saveObjectList(o2sList);
		
		TicketOrderContainer tc = new TicketOrderContainer(order);
		tc.setSeatList(seatList);
		tc.setOpi(opi);
		LastOperation last = new LastOperation("T" + memberid + StringUtil.md5(ukey), order.getTradeNo(), order.getAddtime(), opi.getPlaytime(), "ticket");
		baseDao.saveObject(last);
		return tc;
	}
	private void sendWarn(OpenPlayItem opi){
		Map model = new HashMap();
		model.put("msg", "��ﳡ�γɱ��۸��ģ��������Ƭ��" + opi.getCinemaname() + "," + opi.getPlaytime() + ", " + opi.getMoviename());
		monitorService.saveSysTemplateWarn("���Ź�Ʊ�����д���", "warn/msgmail.vm", model, RoleTag.dingpiao);

	}
	@Override
	public List<SellSeat> checkAndCreateSeat(OpenPlayItem opi, String seatLabel) throws OrderException{
		//1)��ͣ��Ʊ�ر�--ȥ��
		//2)��������ر�--ȥ��
		//3)ѡ��λ���Ƿ�������--ȥ��
		//4)�Ƿ�ѡ������--ȥ��
		List<SellSeat> seatList = createSellSeat(opi, seatLabel);
		//a)��λ�޶�
		if(StringUtils.isNotBlank(opi.getBuylimit()) && !StringUtils.contains(opi.getBuylimit(), ""+seatList.size())){
			throw new OrderException(ApiConstant.CODE_SEAT_NUM_ERROR, "�����ι�����λ����ֻ����" + StringUtil.insertStr(opi.getBuylimit(), "��") + "��");
		}
		return seatList;
	}
	/**
	 * ��λ�Ƿ�����ռ�ã���������ǰ��⣩
	 * @param hfhLockMap ������ϵͳռ�õ���λ
	 * @param seatidList
	 * @return
	 */
	private List<SellSeat> createSellSeat(OpenPlayItem opi, String seatLabel) throws OrderException{
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		List<String[]> seatLocList = TicketUtil.parseSeat(seatLabel);
		List<SellSeat> seatList = new ArrayList<SellSeat>();
		for(String[] loc: seatLocList){
			SellSeat seat = getSellSeatByLoc(opi.getMpid(), loc[0], loc[1]);
			if(seat!=null){
				if(!seat.isAvailable(cur)) throw new OrderException(ApiConstant.CODE_SEAT_OCCUPIED, "��λ" + seat.getSeatLabel() + "��ռ�ã�");
			}else{
				seat = new SellSeat(loc[0], loc[1], opi.getMpid());
				seat.setId(getSellSeatId());
			}
			seat.setPrice(opi.getGewaprice());
			seatList.add(seat);
		}
		return seatList;
		
	}
	private SellSeat getSellSeatByLoc(Long mpid, String seatline, String seatrank){
		String query = "from SellSeat where mpid= ? and seatline = ? and seatrank = ? ";
		List<SellSeat> result = hibernateTemplate.find(query, mpid, seatline, seatrank);
		if(result.isEmpty()) return null;
		return result.get(0);
	}
	@Override
	public ErrorCode<String> getProxySeatPage(String seqno, Long mpid, Long memberid, String membername, String mobile, String oldTradeNo) {
		String key = generateKey(seqno, mpid, memberid, membername, mobile);
		key = key + "@" + StringUtil.md5WithKey(key, 10);
		ErrorCode<String> result = remoteTicketService.getWdUserId(memberid.toString());
		if(result.isSuccess()){
			String callback = config.getString("houtaiPath") + "/admin/ticket/wanda/changeOrder.xhtml?oldTradeNo=" + oldTradeNo + "&key=" + key;
			return getWandaSeatPage(result.getRetval(), seqno, callback);
		}else{
			return result;
		}
	}
	@Override
	public ErrorCode<String> getWapSeatPage(String seqno, Long mpid, Long memberid, String membername, String mobile, String callback, String relkey) {
		ErrorCode<WdParam> param4Wap = remoteTicketService.getWdParam(memberid.toString(), seqno);
		if(!param4Wap.isSuccess()){
			return ErrorCode.getFailure(param4Wap.getMsg());
		}
		try{
			ErrorCode lastOrder = ticketOrderService.processLastOrder(memberid, ""+memberid, seqno);
			if(!lastOrder.isSuccess()){
				return ErrorCode.getFailure(lastOrder.getMsg());
			}
		}catch(Exception e){
			dbLogger.warn(StringUtil.getExceptionTrace(e, 5));
		}
		String key = generateKey(seqno, mpid, memberid, membername, mobile, relkey);
		key = key + "@" + StringUtil.md5WithKey(key, 10);
		WdParam param = param4Wap.getRetval();
		Map<String, String> params = new HashMap<String, String>();
		params.put("thirdPartyCode", thirdPartyCode);
		params.put("userCode", param.getUserCode());
		params.put("cinemaId", param.getCinemaId());
		params.put("showDate", param.getShowDate());
		params.put("filmPK", param.getFilmPK());
		params.put("showId", param.getShowId());
		params.put("payUrl", callback + "?key=" + key);
		return ErrorCode.getSuccessReturn(HttpUtils.getFullUrl(wapUrl, params, "utf-8"));
	}
	@Override
	public ErrorCode<String> getWebSeatPage(String wdUserId, String seqno, Long mpid, Long memberid, String membername, String mobile) {
		//1�������û���û��δ����Ķ���������У�������δ����Ķ���ȡ��
		try{
			ErrorCode lastOrder = ticketOrderService.processLastOrder(memberid, ""+memberid, seqno);
			if(!lastOrder.isSuccess()){
				return ErrorCode.getFailure(lastOrder.getMsg());
			}
		}catch(Exception e){
			dbLogger.warn(StringUtil.getExceptionTrace(e, 5));
		}
		String key = generateKey(seqno, mpid, memberid, membername, mobile);
		key = key + "@" + StringUtil.md5WithKey(key, 10);
		String callback = config.getAbsPath() + "/gewapay/wdOrder.xhtml?key=" + key;
		return getWandaSeatPage(wdUserId, seqno, callback);
	}
	private ErrorCode<String> getWandaSeatPage(String wdUserId, String seqno, String callback) {
		String wdseqno = StringUtils.substring(seqno, 2);
		
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("m", "movieBuy");//�½ӿ�
		params.put("thirdParty", thirdPartyCode);//�½ӿ�
		params.put("spk", wdseqno);
		params.put("sid", StringUtil.getDigitalRandomString(10));

		params.put("callback", callback);
		params.put("userId", wdUserId);
		return ErrorCode.getSuccessReturn(HttpUtils.getFullUrl(wandaUrl, params, "utf-8"));
	}
	
	private String generateKey(String seqno, Long mpid, Long memberid, String membername, String mobile, String relkey) {
		String key = StringUtil.getRandomString(10);
		Map<String, String> info = new HashMap<String, String>();
		info.put("mpid", mpid.toString());
		info.put("memberid", memberid.toString());
		info.put("membername", membername);
		info.put("mobile", mobile);
		info.put("seqno", seqno);
		info.put("gotime", DateUtil.getCurFullTimestampStr());
		if(StringUtils.isNotBlank(relkey)) {
			info.put("relkey", relkey);
		}
		cacheService.set(CacheConstant.REGION_TWENTYMIN, "WDO" + key, info);
		return key;
	}
	private String generateKey(String seqno, Long mpid, Long memberid, String membername, String mobile) {
		return generateKey(seqno, mpid, memberid, membername, mobile, null);
	}
	@Override
	public Map<String, String> getKeyResult(String key) {
		return (Map<String, String>) cacheService.get(CacheConstant.REGION_TWENTYMIN, "WDO" + key);
	}
	private Long getSellSeatId(){
		Long id = jdbcTemplate.queryForLong("SELECT WEBDATA.SEAT_SEQUENCE.NEXTVAL FROM DUAL");
		return id;
	}

	@Override
	public ErrorCode checkAllowChangeSeat(TicketOrder order) {
		if(!StringUtils.equals(order.getCategory(), OpiConstant.OPEN_WD)){
			return ErrorCode.getFailure("����ﶩ����");
		}
		//1������Ƿ�����λ������
		if(!order.isPaidUnfix()){
			return ErrorCode.getFailure("����״̬����ȷ����������λ������");
		}
		//2���Ƿ���15min�ڵĶ�����֧��5���Ӻ󶩵�����ֹ�������ڴ���
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		//TODO:������ﶩ��
		if(DateUtil.addMinute(order.getPaidtime(), 5).after(cur)){
			return ErrorCode.getFailure("����ʱ�䲻��ȷ�����Ժ����ԣ�");
		}
		//3�����Զ�̶���״̬�������ǳ�ʱȡ��
		ErrorCode<TicketRemoteOrder> remoteOrder = remoteTicketService.checkRemoteOrder(order);
		if(!remoteOrder.isSuccess()) return remoteOrder;
		return ErrorCode.SUCCESS;
		//TODO:������飿��
	}
	@Override
	public ErrorCode<Map<String, Object>> validCreateWdOrder(String key, Long mid, String wdOrderId){
		String[] keyPair = StringUtils.split(key, "@");
		if(StringUtils.isBlank(key) || keyPair.length!=2 || keyPair[0].equals(StringUtil.md5WithKey(keyPair[0], 10))){
			return ErrorCode.getFailure("���������д���");
		}
		Map<String, String> info = getKeyResult(keyPair[0]);
		if(info==null){
			return ErrorCode.getFailure("���������ѳ�ʱ��");
		}
		Long mpid = Long.parseLong(info.get("mpid"));
		Long memberid = Long.parseLong(info.get("memberid"));
		if(mid!=null && !memberid.equals(mid)){
			return ErrorCode.getFailure("�����޸����˶�����");
		}
		/*TODO���ƶ���ǰһ��*/
		OpenPlayItem opi = baseDao.getObjectByUkey(OpenPlayItem.class, "mpid", mpid, true);
		if(opi == null || opi.isUnOpenToGewa()) return ErrorCode.getFailure("��������ֹͣ��Ʊ��");
		String mobile = info.get("mobile");
		String seqno = info.get("seqno");
		if(ticketRollCallService.isTicketRollCallMember(memberid, mobile)){
			return ErrorCode.getFailure("����ʺŹ�Ʊ���ޣ�����ϵ�ͷ���4000-406-506��");
		}
		dbLogger.warn("�û��¶�����" + memberid + ":" + wdOrderId);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("seqno", seqno);
		retMap.put("memberid", memberid);
		retMap.put("mobile", mobile);
		retMap.put("relkey", info.get("relkey"));
		retMap.put("opi", opi);
		retMap.put("info", info);
		if(info.get("gotime")!=null) {
			retMap.put("gotime", info.get("gotime"));
		}
		return ErrorCode.getSuccessReturn(retMap);
	}
}
