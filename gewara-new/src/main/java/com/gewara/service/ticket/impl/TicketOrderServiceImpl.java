package com.gewara.service.ticket.impl;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.GoodsConstant;
import com.gewara.constant.PayConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.sys.ConfigConstant;
import com.gewara.constant.ticket.OpiConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.constant.ticket.SeatConstant;
import com.gewara.helper.order.GewaOrderHelper;
import com.gewara.helper.order.OrderContainer;
import com.gewara.helper.order.TicketOrderContainer;
import com.gewara.helper.ticket.SeatPriceHelper;
import com.gewara.helper.ticket.TicketUtil;
import com.gewara.model.api.ApiUser;
import com.gewara.model.common.GewaConfig;
import com.gewara.model.common.LastOperation;
import com.gewara.model.goods.Goods;
import com.gewara.model.goods.GoodsGift;
import com.gewara.model.pay.BuyItem;
import com.gewara.model.pay.GoodsOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.OpenSeat;
import com.gewara.model.ticket.Order2SellSeat;
import com.gewara.model.ticket.SellSeat;
import com.gewara.model.user.Member;
import com.gewara.pay.PayUtil;
import com.gewara.service.OrderException;
import com.gewara.service.order.GoodsOrderService;
import com.gewara.service.order.impl.GewaOrderServiceImpl;
import com.gewara.service.ticket.TicketOrderService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.LockService;
import com.gewara.untrans.impl.LockServiceImpl.AtomicCounter;
import com.gewara.untrans.monitor.OrderMonitorService;
import com.gewara.untrans.ticket.TicketOperationService;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.xmlbind.ticket.WdOrder;

/**
 * ��ģ�������û���Ʊ�������漰����Ҫ����
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
@Service("ticketOrderService")
public class TicketOrderServiceImpl extends GewaOrderServiceImpl implements TicketOrderService, InitializingBean {
	@Autowired@Qualifier("lockService")
	private LockService lockService;
	@Autowired@Qualifier("orderMonitorService")
	private OrderMonitorService orderMonitorService;
	@Autowired@Qualifier("ticketOperationService")
	private TicketOperationService ticketOperationService;
	public void setTicketOperationService(TicketOperationService ticketOperationService) {
		this.ticketOperationService = ticketOperationService;
	}
	@Autowired@Qualifier("goodsOrderService")
	private GoodsOrderService goodsOrderService;
	public void setGoodsOrderService(GoodsOrderService goodsOrderService) {
		this.goodsOrderService = goodsOrderService;
	}
	private AtomicCounter counter;
	@Override
	public ErrorCode checkOrderSeat(TicketOrder order, List<SellSeat> seatList){
		String msg = "";
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		for(SellSeat seat: seatList){
			if(!seat.isAvailableBy(order.getId(), cur)) msg += "[" + seat.getSeatLabel() + "]";
		}
		if(StringUtils.isBlank(msg)) return ErrorCode.SUCCESS;
		return ErrorCode.getFailure(ApiConstant.CODE_SEAT_OCCUPIED, "��λ" + msg + "��ռ�ã�");
	}
	@Override
	public String getTicketTradeNo() {
		String s = PayUtil.FLAG_TICKET + DateUtil.format(new Date(), "yyMMddHHmmss");
		long num = counter.incrementAndGet()%1000;
		s += StringUtils.leftPad("" + num, 3, '0'); // ������
		return s;
	}

	/**
	 * ��λ�Ƿ�����ռ�ã���������ǰ��⣩
	 * @param hfhLockMap ������ϵͳռ�õ���λ
	 * @param seatidList
	 * @return
	 */
	@Override
	public ErrorCode validateSeatLock(List<OpenSeat> oseatList, Map<Long/*id*/, SellSeat> sellSeatMap, List<String> hfhLockList){
		String msg = "";
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		for(OpenSeat oseat: oseatList){
			SellSeat seat = sellSeatMap.get(oseat.getId());
			if(seat!=null && !seat.isAvailable(cur) || oseat.isLocked() || hfhLockList.contains(oseat.getKey())) msg += "[" + oseat.getSeatLabel() + "]";
		}
		if(StringUtils.isBlank(msg)) return ErrorCode.SUCCESS;
		return ErrorCode.getFailure("��λ" + msg + "��ռ�ã�");
	}
	
	@Override
	@Transactional(propagation=Propagation.NESTED)
	public List<SellSeat> createSellSeat(List<OpenSeat> oseatList){
		List<SellSeat> result = new ArrayList<SellSeat>();
		Timestamp validtime = new Timestamp(System.currentTimeMillis()-1000);
		OpenPlayItem opi = baseDao.getObjectByUkey(OpenPlayItem.class, "mpid", oseatList.get(0).getMpid(), true);
		int price = opi.getGewaprice();
		for(OpenSeat oseat:oseatList){
			SellSeat sellSeat = baseDao.getObject(SellSeat.class, oseat.getId());
			if(sellSeat == null){ 
				sellSeat = new SellSeat(oseat, price, validtime);
				baseDao.addObject(sellSeat);
			}
			result.add(sellSeat);
		}
		return result;
	}
	
	/**
	 * �ж��Ƿ���Ϊѡ����seatidList��Щ��λ�����˿��Ա���ġ�����������λ(һ���ܲ���)
	 * @param mpid
	 * @param hfhLockMap 
	 * @param seatidList
	 * @return
	 */
	private final ErrorCode validateSeatPosition(Long mpid, List<OpenSeat> selectedSeat, List<String> hfhLockList) {
		//���кŽ����е���λ����
		Map<Integer, List<OpenSeat>> lineMap = BeanUtil.groupBeanList(selectedSeat, "lineno");
		//�ж�ÿ���Ƿ�Ϸ�
		ErrorCode code = ErrorCode.getFailure(ApiConstant.CODE_SEAT_BREAK_RULE, "ѡ��ʱ���뾡��ѡ����һ�����λ����Ҫ���µ����Ŀ�����λ��");
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		for(Integer lineno: lineMap.keySet()){
			int seatnum = lineMap.get(lineno).size();//�ڴ��������λ����
			//1��Ӱ�����һ����λ״̬ͼ
			List<OpenSeat> lineseatList = getLineSeatListByMpid(mpid, lineno);
			int[] nowrow = new int[getMaxRank(lineseatList)+2];//0:��ʾ��λ������,�±�0�����һ����Ϊ�ڱ�Ԫ��
			for(OpenSeat oseat:lineseatList){
				SellSeat seat = baseDao.getObject(SellSeat.class, oseat.getId());
				if((seat ==null || seat.isAvailable(cur)) && !oseat.isLocked()/*δ�۳�����λ*/ && !hfhLockList.contains(oseat.getKey())) 
					nowrow[oseat.getRankno()]=1;//����
			}
			//2���ҵ����еĹµ���λ������ͳ���ܹ�ʣ�����λ��
			int now = getIsolatedSeat(nowrow);
			//3���ҵ�����Ĺµ���λ������ͳ���ܹ�ʣ�����λ��
			int[] laterrow = Arrays.copyOf(nowrow, nowrow.length);
			for(OpenSeat oseat:lineMap.get(lineno)) laterrow[oseat.getRankno()] = 0;
			
			int later = getIsolatedSeat(laterrow);
			//4���Ա��жϽ����
			if(now >= later) continue; //1)�µ���ٻ򲻱䣬������һ��
			//2)�����˹µ㣬�ж��Ƿ����������ʵ���λѡ��
			List<Integer> seatnumList = getMaxBlankSeatnumList(nowrow);
			Collections.sort(seatnumList);
			//a)���<=2����λ�����к��ʵĲ���������µ�
			if(seatnum==1){//1����λ
				if(seatnumList.get(seatnumList.size()-1)>=3) return code;
			}else if(seatnum==2){
				if(seatnumList.get(seatnumList.size()-1)>=4) return code;
				if(seatnumList.contains(2)) return code;
			}else if(seatnum==3){
				if(seatnumList.get(seatnumList.size()-1)>=5) return code;
				if(seatnumList.contains(3)) return code;
				if(later > now + 1) return code;//ֻ�ܲ���һ���յ�
			}else if(seatnum==4){
				if(seatnumList.get(seatnumList.size()-1)>=6) return code;
				if(seatnumList.contains(4)) return code;
				if(later > now + 1) return code;//ֻ�ܲ���һ���յ�
			}else if(seatnum==5){//���һ��ѡ�����λ
				if(seatnumList.get(seatnumList.size()-1)>=7) return code;
				if(seatnumList.contains(5)) return code;
				if(later > now + 1) return code;//ֻ�ܲ���һ���յ�
			}
		}
		return ErrorCode.SUCCESS;
	}
	/**
	 * ��֤�������������
	 * @param seatList
	 * @return
	 */
	@Override
	public ErrorCode validLoveSeat(List<OpenSeat> oseatList){
		Map<String, String> seatMap = new HashMap<String, String>();
		for(OpenSeat oseat: oseatList){
			if(!oseat.getLoveInd().equals("0")){
				seatMap.put(oseat.getLineno()+","+oseat.getRankno(), oseat.getLoveInd());
			}
		}
		if(seatMap.isEmpty()) return ErrorCode.SUCCESS;
		List<String> keyList = new ArrayList<String>(seatMap.keySet());
		String tmpInd = null;
		for(String key: keyList){
			String v = seatMap.get(key);
			String[] r = key.split(",");
			if(v.equals("1")){
				tmpInd = seatMap.get(r[0] + "," + (Integer.parseInt(r[1])+1));
			}else{
				tmpInd = seatMap.get(r[0] + "," + (Integer.parseInt(r[1])-1));
			}
			if(tmpInd == null) return ErrorCode.getFailure("���������ܵ�����");
		}
		return ErrorCode.SUCCESS;
	}
	private final List<OpenSeat> getLineSeatListByMpid(Long mpid, int lineno) {
		String hql = "from OpenSeat s where s.mpid=? and s.lineno=?";
		List<OpenSeat> result = hibernateTemplate.find(hql, mpid, lineno);
		return result;
	}
	private final int getMaxRank(List<OpenSeat> oseatList){
		int max = 0;
		for(OpenSeat oseat:oseatList) max=Math.max(max, oseat.getRankno());
		return max;
	}
	/**
	 * �ҵ��µ���λ������ͳ���ܹ�ʣ�����λ��
	 * @param seatmap
	 * @return
	 */
	private final int getIsolatedSeat(int[] seatrow){
		int isolatedNum = 0;
		for(int i=0, length = seatrow.length -1;i<length;i++){
			if(seatrow[i]==1){//û��ռ��
				if(seatrow[i-1]==0 && seatrow[i+1]==0) isolatedNum++;
			}
		}
		return isolatedNum;
	}
	/**
	 * �����հ�������λΪһ�飬�ҵ�������ĸ���
	 * @param seatrow
	 * @return
	 */
	private final List<Integer> getMaxBlankSeatnumList(int[] seatrow){
		List<Integer> result = new ArrayList<Integer>();
		for(int i=0,length = seatrow.length -1;i<length;){
			if(seatrow[i]==0) i++;
			else{
				int num=0;
				while(seatrow[i]==1 && i < length){
					num++;i++;
				}
				result.add(num);
			}
		}
		return result;
	}
	@Override
	public OpenSeat getOpenSeatByLoc(Long mpid, String seatline, String seatrank){
		String query = "from OpenSeat where mpid= ? and seatline = ? and seatrank = ? ";
		List<OpenSeat> result = hibernateTemplate.find(query, mpid, seatline, seatrank);
		if(result.isEmpty()) return null;
		return result.get(0);
	}
	
	/**
	 * ��ǰ���������������������һ�ι���ʱ��
	 * @param mobile
	 * @return
	 */
	@Override
	public String getOrderHis(String mobile){
		String sql = "SELECT BUYTIMES FROM WEBDATA.MOBILE_BUYTIMES WHERE MOBILE = ?";
		List<String> orderHis = jdbcTemplate.queryForList(sql, String.class, mobile);
		if(orderHis.isEmpty()) return "";
		return orderHis.get(0);
	}
	
	@Override
	public TicketOrder getLastUnpaidTicketOrder(Long memberid, String ukey, long mpid) {
		Assert.notNull(mpid);
		TicketOrder order = getLastTicketOrder(memberid, ukey);
		if(order!=null && order.isNew() && order.getMpid().equals(mpid)) return order;
		return null;
	}
	@Override
	public void cancelLockFailureOrder(TicketOrder order) {
		cancelTicketOrder(order, order.getMemberid(), OrderConstant.STATUS_REPEAT, "������λ����");
	}
	@Override
	public TicketOrder cancelTicketOrder2(String tradeNo, Long memberid, String status, String reason) {
		TicketOrder order = baseDao.getObjectByUkey(TicketOrder.class, "tradeNo", tradeNo, false);
		cancelTicketOrder2(order, memberid, status, reason);
		return order;
	}
	private void cancelTicketOrder2(TicketOrder order, Long memberid, String status, String reason) {
		boolean skipRemote = StringUtils.equals(order.getStatus(), OrderConstant.STATUS_NEW_UNLOCK) ;
		List<SellSeat> seatList = cancelTicketOrder(order, memberid, status, reason);
		if(!skipRemote && seatList!=null && !seatList.isEmpty()){
			ticketOperationService.unlockRemoteSeat(order, seatList);
		}
	}
	private List<SellSeat> cancelTicketOrder(TicketOrder order, Long memberid, String status, String reason) {
		if(!StringUtils.startsWith(status, OrderConstant.STATUS_CANCEL)) return  null;
		if(order == null) return null;
		if(order.isNew() && order.getMemberid().equals(memberid)){
			Timestamp validtime = new Timestamp(System.currentTimeMillis()-1000);
			order.setStatus(status);
			order.setValidtime(validtime);
			List<SellSeat> seatList = getOrderSeatList(order.getId());
			for(SellSeat oseat: seatList){
				if(order.getId().equals(oseat.getOrderid())){
					oseat.setValidtime(validtime);
					baseDao.saveObject(oseat);
				}
			}
			baseDao.saveObject(order);
			orderMonitorService.addOrderChangeLog(order.getTradeNo(), "�Զ�ȡ��", order,  reason);
			return seatList;
		}
		return null;
	}
	@Override
	public OrderContainer processOrderPay(TicketOrder order, OpenPlayItem opi, List<SellSeat> seatList) throws OrderException {
		if(order.getStatus().startsWith(OrderConstant.STATUS_PAID)){
			if(order.isNotAllPaid()) throw new OrderException(ApiConstant.CODE_DATA_ERROR, "����δ����");
			if(order.isPaidFailure()){//��Ǯ�ˣ���״̬����δ�ɹ��������
				return processOrderPayInternal(order);
			}
			return null;
		}else{
			throw new OrderException(ApiConstant.CODE_DATA_ERROR, "����״̬����ȷ��");
		}
	}
/*	*//**
	 * �жϸö����Ƿ���Ҫ���ٻ���
	 * @param order
	 * @return
	 *//*
	private int getDeductPoint(OpenPlayItem opi, TicketOrder order, List<SellSeat> seatList){
		SeatPrice deductPrice = getFirstDeductPrice(order.getMpid());
		if(deductPrice != null) {
			for(SellSeat seat: seatList){
				if(deductPrice.getPrice().equals(seat.getPrice())){
					return deductPrice.getDeductpoint();
				}
			}
		}
		return - opi.getGivepoint();
	}
	*//**
	 * @param mpid
	 * @return
	 *//*
	private SeatPrice getFirstDeductPrice(Long mpid){
		String query = "from SeatPrice s where s.mpid = ? and s.deductpoint > 0";
		List<SeatPrice> result = hibernateTemplate.find(query, mpid);
		if(result.isEmpty()) return null;
		return result.get(0);
	}*/
	
	@Override
	public List<SellSeat> getOrderSeatList(Long orderId){
		String query = "from SellSeat where id in (select t.seatid from Order2SellSeat t where t.orderid = ?) ";
		List<SellSeat> seatList = hibernateTemplate.find(query, orderId);
		return seatList;
	}
	private void bindOrderGoodsGift(TicketOrderContainer tc, OpenPlayItem opi) {
		TicketOrder order = tc.getTicketOrder();
		if(order.sureOutPartner()) return;
		GoodsGift bindGift = goodsOrderService.getBindGoodsGift(opi, PartnerConstant.GEWA_SELF);
		if(bindGift!=null){
			Goods goods = baseDao.getObject(Goods.class, bindGift.getGoodsid());
			if(goods.getUnitprice()==0){
				if(bindGift.isGainGift(order.getQuantity())){
					Map<String, String> otherinfoMap = VmUtils.readJsonToMap(order.getOtherinfo());
					otherinfoMap.put(PayConstant.KEY_GOODSGIFT, ""+goods.getId());
					otherinfoMap.put(PayConstant.KEY_GOODSNUM, ""+bindGift.getRatenum(order.getQuantity()));
					order.setOtherinfo(JsonUtils.writeMapToJson(otherinfoMap));
					baseDao.saveObject(order);
					tc.setBindGift(bindGift);
					tc.setGoods(goods);
				}
			}else {
				List<BuyItem> itemList = baseDao.getObjectListByField(BuyItem.class, "orderid", order.getId());
				if(bindGift.isGainGift(order.getQuantity())){
					BuyItem item = new BuyItem(order.getQuantity(), goods);
					item.setUnitprice(goods.getUnitprice());
					item.setQuantity(bindGift.getRatenum(order.getQuantity()));
					item.setOrderid(order.getId());
					item.setMemberid(order.getMemberid());
					item.setTotalfee(goods.getUnitprice()*item.getQuantity());
					item.setTotalcost(goods.getCostprice()*item.getQuantity());
					item.setRelatedid(goods.getId());
					Timestamp validtime = DateUtil.getLastTimeOfDay(opi.getPlaytime());
					item.setValidtime(DateUtil.addHour(validtime, 2));
					baseDao.saveObject(item);
					itemList.add(item);
					GewaOrderHelper.refreshItemfee(order, itemList);
					baseDao.saveObject(order);
					tc.setBindGift(bindGift);
					tc.setGoods(goods);
					dbLogger.warn("������Ʒ��ӵ�buyitem:" + goods.getGoodsname());
				}
			}
		}
	}
	@Override
	public ErrorCode<GoodsGift> addOrderGoodsGift(TicketOrder order, OpenPlayItem opi, Long goodsid, Integer quantity) {
		if(order.sureOutPartner()) return ErrorCode.getFailure("�����д����ⲿ�̼��ײͣ�");
		Goods goods = baseDao.getObject(Goods.class, goodsid);
		if(goods == null) return ErrorCode.getFailure("û�ҵ���Ӧ����Ʒ��");
		GoodsGift gift = baseDao.getObjectByUkey(GoodsGift.class, "goodsid", goodsid, true);
		if(gift != null && gift.isGainGift(order.getQuantity())){
			quantity = gift.getRatenum(order.getQuantity());
		}
		if(quantity<=0) return ErrorCode.getFailure("�����ײ���������");
		if(goods.getUnitprice()==0){
			if(gift != null){
				if(!goodsOrderService.isValidGoodsGift(opi, gift, PartnerConstant.GEWA_SELF)){
					return ErrorCode.getSuccessReturn(gift) ;
				}
			}
			Map<String, String> otherinfoMap = VmUtils.readJsonToMap(order.getOtherinfo());
			otherinfoMap.put(PayConstant.KEY_GOODSGIFT, ""+goods.getId());
			otherinfoMap.put(PayConstant.KEY_GOODSNUM, ""+quantity);
			order.setOtherinfo(JsonUtils.writeMapToJson(otherinfoMap));
			baseDao.saveObject(order);
		}else {
			if(quantity <= 0 || quantity > 5) return ErrorCode.getFailure("������Ʒ��������");
			List<BuyItem> itemList = baseDao.getObjectListByField(BuyItem.class, "orderid", order.getId());
			BuyItem item = new BuyItem(quantity, goods);
			item.setUnitprice(goods.getUnitprice());
			item.setQuantity(quantity);
			item.setOrderid(order.getId());
			item.setMemberid(order.getMemberid());
			item.setTotalfee(goods.getUnitprice()*quantity);
			item.setTotalcost(goods.getCostprice()*quantity);
			item.setRelatedid(goods.getId());
			Timestamp validtime = DateUtil.getLastTimeOfDay(opi.getPlaytime());
			item.setValidtime(DateUtil.addHour(validtime, 2));
			baseDao.saveObject(item);
			itemList.add(item);
			GewaOrderHelper.refreshItemfee(order, itemList);
			baseDao.saveObject(order);
			dbLogger.warn("������Ʒ��ӵ�buyitem:" + goods.getGoodsname());
		}
		return ErrorCode.getSuccessReturn(gift) ;
	}
	@Override
	public ErrorCode checkPauseBooking(OpenPlayItem opi) {
		GewaConfig config = baseDao.getObject(GewaConfig.class, ConfigConstant.CFG_PAUSE_TICKET);
		String time = null;
		Map<String/**/, String/**/> timeMap = JsonUtils.readJsonToMap(config.getContent());

		time = timeMap.get(opi.getCitycode());
		if(StringUtils.isBlank(time)){//����ͨ���ٻ�ȡһ��
			time = timeMap.get(opi.getOpentype());
		}
		if(StringUtils.isBlank(time)){
			time = timeMap.get("000000");
		}
		if(StringUtils.isBlank(time)){
			return ErrorCode.SUCCESS;
		}
		String msg = timeMap.get("msg");
		try{
			Timestamp cur = new Timestamp(System.currentTimeMillis());
			String pair[] = StringUtils.split(time, "~");
			if(pair.length == 2){
				Timestamp from = Timestamp.valueOf(pair[0]);
				Timestamp to = Timestamp.valueOf(pair[1]);
				if(cur.after(from) && cur.before(to)){
					if(StringUtils.isNotBlank(msg)){
						return ErrorCode.getFailure(msg);
					}else{
						return ErrorCode.getFailure("ӰԺ���������Ӳ���������" + DateUtil.formatTimestamp(to) + "���ԣ�");
					}
				}
			}else{
				Timestamp pause = Timestamp.valueOf(time);
				if(cur.before(pause)){
					if(StringUtils.isNotBlank(msg)){
						return ErrorCode.getFailure(msg);
					}else{
						return ErrorCode.getFailure("ӰԺ���������Ӳ���������" + DateUtil.formatTimestamp(pause) + "���ԣ�");
					}
				}
			}
		}catch(Exception e){
			dbLogger.warn(StringUtil.getExceptionTrace(e, 5));
		}
		return ErrorCode.SUCCESS;
	}
	@Override
	public void processSuccess(TicketOrder order, List<SellSeat> seatList, Timestamp curtime) {
		order.setStatus(OrderConstant.STATUS_PAID_SUCCESS);
		order.setSettle(OrderConstant.SETTLE_Y);
		order.setCheckpass(order.getCheckpass().replaceAll("X", ""));// ��ֹ�������
		order.setUpdatetime(curtime);
		order.setModifytime(curtime);
		Timestamp validtime = DateUtil.addDay(curtime, 180);
		order.setValidtime(validtime);
		for (SellSeat seat : seatList) {
			seat.setStatus(SeatConstant.STATUS_SOLD);
			seat.setValidtime(DateUtil.addDay(order.getPlaytime(), 15));
			seat.setOrderid(order.getId());
		}
		//ͳһ�ɹ���
		String orderHis = getOrderHis(order.getMobile());
		if(StringUtils.isNotBlank(orderHis)) {
			order.addChangehis(OrderConstant.CHANGEHIS_KEY_BUYTIMES, orderHis);
		}
		baseDao.saveObject(order);
		baseDao.saveObjectList(seatList);
		processOrderExtra(order);
	}
	private Map<Long/*id*/, SellSeat> checkAndCreateSeat(OpenPlayItem opi, List<OpenSeat> oseatList, List<String> hfhLockList) throws OrderException{
		ErrorCode booking = checkPauseBooking(opi);
		if(!booking.isSuccess()){
			throw new OrderException(ApiConstant.CODE_OPI_CLOSED, booking.getMsg());
		}

		//a)��������
		if(oseatList.size() > opi.gainLockSeat()) throw new OrderException(ApiConstant.CODE_SEAT_NUM_ERROR, "һ�����ֻ��ѡ��" + opi.gainLockSeat() + "����λ��");
		if(oseatList.size()==0) throw new OrderException(ApiConstant.CODE_SEAT_NUM_ERROR, "����Ҫѡ��һ����λ��");
		//ȡ������������λ
		if(StringUtils.isNotBlank(opi.getBuylimit()) && !StringUtils.contains(opi.getBuylimit(), ""+oseatList.size())){
			throw new OrderException(ApiConstant.CODE_SEAT_NUM_ERROR, "�����ι�����λ����ֻ����" + StringUtil.insertStr(opi.getBuylimit(), "��") + "��");
		}
		//b)�Ƿ�����Ԥ��
		List<SellSeat> seatList = createSellSeat(oseatList);
		Map<Long/*id*/, SellSeat> sellSeatMap = BeanUtil.beanListToMap(seatList, "id");
		ErrorCode code = validateSeatLock(oseatList, sellSeatMap, hfhLockList);
		if(!code.isSuccess()) throw new OrderException(ApiConstant.CODE_SEAT_OCCUPIED, code.getMsg());
		//c)ѡ��λ���Ƿ�������
		code = validateSeatPosition(opi.getMpid(), oseatList, hfhLockList);
		if(!code.isSuccess()) throw new OrderException(ApiConstant.CODE_SEAT_POS_ERROR, code.getMsg());
		//d)�Ƿ�ѡ������
		code = validLoveSeat(oseatList);
		if(!code.isSuccess()) throw new OrderException(ApiConstant.CODE_SEAT_POS_ERROR, code.getMsg());
		return sellSeatMap;
	}
	@Override
	public TicketOrderContainer addTicketOrder(OpenPlayItem opi, List<Long> seatidList, Long memberid, String membername, String mobile, Integer point,
			String randomNum, List<String> hfhLockList) throws OrderException {
		//1)��λ�ؼ�Ʊ�л������ƣ�
		ErrorCode code = checkOrderSeatLimit(opi, point);
		if(!code.isSuccess()) throw new OrderException(ApiConstant.CODE_SEAT_LIMITED, code.getMsg());

		Set<Long> seatidList2 = new LinkedHashSet<Long>(seatidList);
		List<OpenSeat> oseatList = baseDao.getObjectList(OpenSeat.class, seatidList2);
		TicketOrderContainer tc = addTicketOrderInternal(oseatList, opi, memberid, membername, hfhLockList, mobile, randomNum, PartnerConstant.GEWA_SELF);
		//ǿ�ư��ײ�
		bindOrderGoodsGift(tc, opi);
		return tc;
	}
	@Override
	public TicketOrderContainer addTicketOrder(OpenPlayItem opi, List<Long> seatidList, Member member, ApiUser partner, String mobile,
			String randomNum, List<String> hfhLockList) throws OrderException {
		Set<Long> seatidList2 = new LinkedHashSet<Long>(seatidList);
		List<OpenSeat> oseatList = baseDao.getObjectList(OpenSeat.class, seatidList2);
		Long memberid = member.getId();
		String membername = member.getNickname()+"@"+partner.getBriefname();
		TicketOrderContainer tc = addTicketOrderInternal(oseatList, opi, memberid, membername, hfhLockList, mobile, randomNum, partner.getId());
		//ǿ�ư��ײ�
		bindOrderGoodsGift(tc, opi);
		return tc;
	}
	@Override
	public TicketOrderContainer addTicketOrder(OpenPlayItem opi, String seatLabel, Member member, ApiUser partner, String mobile, String randomNum, List<String> hfhLockList) throws OrderException {
		Long memberid = member.getId();
		String membername = member.getNickname()+"@"+partner.getBriefname();
		List<String[]> seatLocList = TicketUtil.parseSeat(seatLabel);
		List<OpenSeat> oseatList = new ArrayList<OpenSeat>();
		for(String[] loc: seatLocList){
			OpenSeat seat = getOpenSeatByLoc(opi.getMpid(), loc[0], loc[1]);
			if(seat != null) oseatList.add(seat);
		}
		TicketOrderContainer tc = addTicketOrderInternal(oseatList, opi, memberid, membername, hfhLockList, mobile, randomNum, partner.getId());
		//ǿ�ư��ײ�
		bindOrderGoodsGift(tc, opi);
		return tc;
	}

	@Override
	public TicketOrderContainer addPartnerTicketOrder(OpenPlayItem opi, List<Long> seatidList, ApiUser partner, 
			String mobile, String checkpass, String ukey, String userid, String paymethod, String paybank, 
			List<String> hfhLockList) throws OrderException {
		Set<Long> seatidList2 = new LinkedHashSet<Long>(seatidList);
		List<OpenSeat> oseatList = baseDao.getObjectList(OpenSeat.class, seatidList2);
		return addPartnerOrderInternal(userid, oseatList, opi, hfhLockList, mobile, checkpass, partner, ukey, paymethod, paybank);
	}
	@Override
	public TicketOrderContainer addPartnerTicketOrder(OpenPlayItem opi, String seatLabel, ApiUser partner, 
			String mobile, String randomNum, String ukey, String userid, String paymethod, 
			String paybank, List<String> hfhLockList) throws OrderException {
		List<String[]> seatLocList = TicketUtil.parseSeat(seatLabel);
		List<OpenSeat> oseatList = new ArrayList<OpenSeat>();
		for(String[] loc: seatLocList){
			OpenSeat seat = getOpenSeatByLoc(opi.getMpid(), loc[0], loc[1]);
			if(seat != null) oseatList.add(seat);
		}
		return addPartnerOrderInternal(userid, oseatList, opi, hfhLockList, mobile, randomNum, partner, ukey, paymethod, paybank);
	}
	
	protected TicketOrderContainer addTicketOrderInternal(List<OpenSeat> oseatList, OpenPlayItem opi, Long memberid, String membername, 
			List<String> hfhLockList, String mobile, String randomNum, Long partnerId) throws OrderException {
		return addTicketOrderInternal2(oseatList, opi, memberid, membername, hfhLockList, mobile, randomNum, partnerId, memberid+"", null, null);
	}
	protected TicketOrderContainer addPartnerOrderInternal(String userid, List<OpenSeat> oseatList, OpenPlayItem opi,
			List<String> hfhLockList, String mobile, String randomNum, ApiUser partner, String ukey, String paymethod, String paybank) throws OrderException {
		Long memberid = partner.getId();
		String membername = StringUtils.isBlank(userid) ? partner.getBriefname():userid+"@"+partner.getBriefname();
		return addTicketOrderInternal2(oseatList, opi, memberid, membername, hfhLockList, mobile, randomNum, partner.getId(), ukey, paymethod, paybank);
	}

	private TicketOrderContainer addTicketOrderInternal2(List<OpenSeat> oseatList, OpenPlayItem opi, Long memberid, String membername, 
			List<String> hfhLockList, String mobile, String randomNum, Long partnerId, String ukey, String paymethod, String paybank) throws OrderException {
	
		Map<Long, SellSeat> sellSeatMap = checkAndCreateSeat(opi, oseatList, hfhLockList);
		TicketOrder order = new TicketOrder(memberid, membername, ukey, opi);
		int costprice = opi.getCostprice();
		if(PartnerConstant.isMacBuy(partnerId)){
			costprice = opi.getPrice()-opi.getFee();
		}
		SeatPriceHelper sph = new SeatPriceHelper(opi, partnerId);

		order.setPaybank(paybank);
		if(StringUtils.isNotBlank(paymethod)){
			order.setPaymethod(paymethod);
		}
		
		order.setPartnerid(partnerId);
		String odertitle = opi.getCinemaname()+"��ӰƱ";
		Timestamp validtime = DateUtil.addMinute(order.getAddtime(), opi.gainLockMinute());
		order.setTradeNo(getTicketTradeNo());
		order.setOrdertitle(odertitle);
		order.setMobile(mobile);
		order.setValidtime(validtime);
		order.setCheckpass(randomNum);
		order.setCategory(opi.getOpentype());
		if(opi.hasGewara()){
			order.setStatus(OrderConstant.STATUS_NEW);
		}

		order.setQuantity(sellSeatMap.size());
		int totalfee = 0;
		int maxprice = 0;
		for(OpenSeat oseat :oseatList){
			SellSeat sseat = sellSeatMap.get(oseat.getId());
			int price = sph.getPrice(oseat);
			if(price>maxprice) maxprice = price;
			sseat.copyFrom(oseat, price);
			sseat.setValidtime(validtime);
			sseat.setRemark(StringUtils.substring("[��" + membername + "]" + StringUtils.defaultString(sseat.getRemark()), 0, 500));
			totalfee = totalfee + price;
		}

		order.setUnitprice(maxprice);
		order.setTotalfee(totalfee);
		order.setCostprice(costprice);
		order.setTotalcost(sellSeatMap.size()*costprice);

		TicketUtil.setOrderDescription(order, sellSeatMap.values(), opi);

		baseDao.saveObject(order);
		List<Order2SellSeat> o2sList = new ArrayList<Order2SellSeat>();
		for(SellSeat sseat: sellSeatMap.values()){
			sseat.setOrderid(order.getId());
			o2sList.add(new Order2SellSeat(order.getId(), sseat.getId()));
		}
		baseDao.saveObjectList(sellSeatMap.values());
		baseDao.saveObjectList(o2sList);
		
		TicketOrderContainer tc = new TicketOrderContainer(order);
		tc.setSeatList(new ArrayList<SellSeat>(sellSeatMap.values()));
		tc.setOseatList(oseatList);
		tc.setOpi(opi);
		LastOperation last = new LastOperation("T" + memberid + StringUtil.md5(ukey), order.getTradeNo(), order.getAddtime(), opi.getPlaytime(), "ticket");
		baseDao.saveObject(last);
		return tc;
	}

	private ErrorCode checkOrderSeatLimit(OpenPlayItem opi, Integer point) {
		String msg = ""; 
		if(opi.getGivepoint()< 0  && point + opi.getGivepoint() < 0) {
			msg = msg + "���Ļ��ֲ���,���ܹ���!";
		}
		if(StringUtils.isNotBlank(msg)) return ErrorCode.getFailure(msg);
		return ErrorCode.SUCCESS;
	}
	

	@Override
	public List<TicketOrder> getTicketOrderListByUkey(Long parentid, String ukey, String status) {
		DetachedCriteria query=DetachedCriteria.forClass(TicketOrder.class);
		if(StringUtils.isNotBlank(status))query.add(Restrictions.eq("status", OrderConstant.STATUS_PAID_SUCCESS));
		query.add(Restrictions.eq("partnerid", parentid));
		query.add(Restrictions.eq("ukey", ukey));
		query.addOrder(Order.desc("addtime"));
		List<TicketOrder> orderList=hibernateTemplate.findByCriteria(query);
		return orderList;
	}
	
	@Override
	public ErrorCode<GoodsOrder> addBindGoodsOrder(TicketOrder torder, String randomNum) {
		if(!torder.isPaidSuccess()) return ErrorCode.getFailure("ֻ�гɹ��Ķ������������ײͣ�");
		Map<String, String> otherinfoMap = VmUtils.readJsonToMap(torder.getOtherinfo());
		String bindTradeNo = otherinfoMap.get(PayConstant.KEY_BIND_TRADENO);
		if(StringUtils.isNotBlank(bindTradeNo)) return ErrorCode.getFailure("�Ѿ������ײͣ�" + bindTradeNo);
		String bindGoods = otherinfoMap.get(PayConstant.KEY_BINDGOODS);
		Integer count = 1;
		if(StringUtils.isBlank(bindGoods)) {//��鳡�����ް�
			bindGoods = otherinfoMap.get(PayConstant.KEY_GOODSGIFT);
		}
		if(StringUtils.isBlank(bindGoods)) return ErrorCode.getFailure("�˶���û���ײͣ�");
		if(StringUtils.isNotBlank(otherinfoMap.get(PayConstant.KEY_GOODSNUM))) {
			count = Integer.parseInt(otherinfoMap.get(PayConstant.KEY_GOODSNUM));
		}
		Goods goods = baseDao.getObject(Goods.class, new Long(bindGoods));
		
		GoodsOrder goodsOrder = new GoodsOrder(torder.getMemberid(), torder.getMembername(), goods);
		goodsOrder.setTradeNo(PayUtil.FLAG_GOODS + StringUtils.substring(torder.getTradeNo(), PayUtil.FLAG_GOODS.length()));
		goodsOrder.setMobile(torder.getMobile());
		goodsOrder.setPaidtime(goodsOrder.getAddtime());
		goodsOrder.setQuantity(count);
		goodsOrder.setTotalfee(goodsOrder.getOrderAmount());
		goodsOrder.setDiscount(0);
		goodsOrder.setPaymethod(PaymethodConstant.PAYMETHOD_SYSPAY);
		goodsOrder.setPaybank(OrderConstant.SYSBANK_GIFT);
		goodsOrder.setPayseqno(torder.getTradeNo());
		goodsOrder.setPlaceid(torder.getCinemaid());
		//TODO:�˴��붩����partnerId��ͬ����δ���
		goodsOrder.setPartnerid(torder.getPartnerid());
		goodsOrder.setCheckpass(randomNum);
		goodsOrder.setStatus(OrderConstant.STATUS_PAID_SUCCESS);
		int maxday = 60;
		Timestamp validtime = DateUtil.addDay(goodsOrder.getAddtime(), maxday);
		if(GoodsConstant.GOODS_TAG_BMH.equals(goods.getTag())) { 
			OpenPlayItem opi = baseDao.getObjectByUkey(OpenPlayItem.class, "mpid",  torder.getMpid(), true);
			Timestamp lasttime = DateUtil.getLastTimeOfDay(opi.getPlaytime());
			validtime = DateUtil.addHour(lasttime, 2);
		}
		goodsOrder.setPricategory( OrderConstant.ORDER_PRICATEGORY_MOVIE);
		goodsOrder.setCategory(goods.getGoodstype());
		goodsOrder.setValidtime(validtime);
		goodsOrder.setCitycode(torder.getCitycode());
		goodsOrder.setAlipaid(goodsOrder.getDue());
		int costprice = 0;
		if(goods.getCostprice()!=null) costprice = goods.getCostprice();
		goodsOrder.setCostprice(costprice);
		goodsOrder.setTotalcost(costprice*count);
		Map<String, String> descMap = new HashMap<String, String>();
		descMap.put("��Ʒ����", goods.getGoodsname());
		descMap.put("�󶨶���", torder.getTradeNo());
		goodsOrder.setDescription2(JsonUtils.writeMapToJson(descMap));
		baseDao.saveObject(goodsOrder);
		otherinfoMap.put(PayConstant.KEY_BIND_TRADENO, goodsOrder.getTradeNo());
		torder.setOtherinfo(JsonUtils.writeMapToJson(otherinfoMap));
		baseDao.saveObject(torder);
		return ErrorCode.getSuccessReturn(goodsOrder);
	}
	
	@Override
	public ErrorCode<String> isValidateSeatPosition(OpenPlayItem opi, String seatLabel, List<String> hfhLockList){
		List<String[]> seatLocList = TicketUtil.parseSeat(seatLabel);
		List<OpenSeat> oseatList = new ArrayList<OpenSeat>();
		for(String[] loc: seatLocList){
			OpenSeat seat = getOpenSeatByLoc(opi.getMpid(), loc[0], loc[1]);
			if(seat != null) oseatList.add(seat);
		}
		return validateSeatPosition(opi.getMpid(), oseatList, hfhLockList);
	}
	@Override
	public ErrorCode processLastOrder(Long memberid, String ukey, String msg) {
		TicketOrder lastOrder = getLastTicketOrder(memberid, ukey);
		if(lastOrder==null) return ErrorCode.SUCCESS;
		if(lastOrder.getStatus().startsWith(OrderConstant.STATUS_PAID_FAILURE)){
			return ErrorCode.getFailure("������һ�������ȴ�����������Ϊ" + lastOrder.getTradeNo() + "�����Ժ������¶�����");
		}
		if(lastOrder.isNew()){//ȡ��δ֧��
			//String log = "�Զ�ȡ�����һ�ʶ�����" + memberid + ":" + lastOrder.getTradeNo() + ":"+ lastOrder.getStatus() + ",msg=" + msg;
			//dbLogger.warn(log);
			cancelTicketOrder2(lastOrder, memberid, OrderConstant.STATUS_REPEAT, "�Զ�ȡ����" + msg);
		}
		return ErrorCode.SUCCESS;
	}
	private TicketOrder getLastTicketOrder(Long memberid, String ukey){
		LastOperation last = baseDao.getObject(LastOperation.class, "T" + memberid + StringUtil.md5(ukey));
		if(last==null) return null;
		TicketOrder order = baseDao.getObjectByUkey(TicketOrder.class, "tradeNo", last.getLastvalue());
		return order;
	}
	@Override
	public List<TicketOrder> wdOrderContrast(Date date,List<WdOrder> wdOrderList) {
		Map<String,WdOrder> wdOrderMap = BeanUtil.beanListToMap(wdOrderList, "snid");
		DetachedCriteria query = DetachedCriteria.forClass(TicketOrder.class);
		query.add(Restrictions.eq("category", OpiConstant.OPEN_WD));
		query.add(Restrictions.eq("status", OrderConstant.STATUS_PAID_SUCCESS));
		query.add(Restrictions.ge("addtime", date));
		query.add(Restrictions.le("addtime", DateUtil.getLastTimeOfDay(date)));
		List<TicketOrder> orderList = hibernateTemplate.findByCriteria(query);
		List<TicketOrder> tmpList = new ArrayList<TicketOrder>();
		for(TicketOrder order : orderList){
			String snsId = JsonUtils.getJsonValueByKey(order.getOtherinfo(), "hfhId");
			WdOrder wdOrder = wdOrderMap.get(snsId);
			if(wdOrder != null){
				wdOrderList.remove(wdOrder);
				tmpList.add(order);
			}
		}
		orderList.removeAll(tmpList);
		return orderList;
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		counter = lockService.getAtomicCounter("TicketTradeNO");
		long value = counter.get();
		if(value > 999999){
			counter.set(0);
		}
	}
	@Override
	public ErrorCode<TicketOrder> removeBuyItem(Long memberid, Long itemid) {
		BuyItem item = baseDao.getObject(BuyItem.class, itemid);
		if(item==null) return ErrorCode.getFailure("���ݲ����ڣ�");
		
		if(!item.getMemberid().equals(memberid)) return ErrorCode.getFailure("��û��Ȩ��!");
		TicketOrder order = baseDao.getObject(TicketOrder.class, item.getOrderid());
		if(OrderConstant.STATUS_NEW_CONFIRM.equals(order.getStatus())) return ErrorCode.getFailure("�˶������ڴ�����״̬��������ɾ��!");
		
		List<BuyItem> itemList = baseDao.getObjectListByField(BuyItem.class, "orderid", order.getId());
		itemList.remove(item);
		baseDao.removeObject(item);
		GewaOrderHelper.refreshItemfee(order, itemList);
		baseDao.saveObject(order);
		return ErrorCode.getSuccessReturn(order);
	}
}
