package com.gewara.service.sport.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Service;

import com.gewara.command.SearchOrderCommand;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.MemberCardConstant;
import com.gewara.constant.OpenTimeItemConstant;
import com.gewara.constant.OpenTimeTableConstant;
import com.gewara.constant.PayConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.Status;
import com.gewara.constant.TagConstant;
import com.gewara.constant.sys.ConfigConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.OrderExtraConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.helper.SportOrderHelper;
import com.gewara.helper.discount.SpecialDiscountHelper;
import com.gewara.helper.discount.SportSpecialDiscountHelper;
import com.gewara.helper.discount.SportSpecialDiscountHelper.OrderCallback;
import com.gewara.helper.order.GewaOrderHelper;
import com.gewara.helper.order.OrderContainer;
import com.gewara.helper.order.SportOrderContainer;
import com.gewara.model.api.ApiUser;
import com.gewara.model.common.GewaConfig;
import com.gewara.model.common.LastOperation;
import com.gewara.model.pay.Adjustment;
import com.gewara.model.pay.BuyItem;
import com.gewara.model.pay.Charge;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.MemberAccount;
import com.gewara.model.pay.OrderExtra;
import com.gewara.model.pay.Spcounter;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.sport.CusOrder;
import com.gewara.model.sport.MemberCardInfo;
import com.gewara.model.sport.MemberCardType;
import com.gewara.model.sport.OpenTimeItem;
import com.gewara.model.sport.OpenTimeSale;
import com.gewara.model.sport.OpenTimeSaleMember;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.model.sport.SellDeposit;
import com.gewara.model.sport.SellTimeTable;
import com.gewara.model.sport.Sport;
import com.gewara.model.sport.Sport2Item;
import com.gewara.model.sport.SportField;
import com.gewara.model.sport.SportItem;
import com.gewara.model.sport.SportOrder2TimeItem;
import com.gewara.model.sport.SportProfile;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.pay.PayUtil;
import com.gewara.pay.PayValidHelper;
import com.gewara.service.OrderException;
import com.gewara.service.gewapay.ScalperService;
import com.gewara.service.order.impl.GewaOrderServiceImpl;
import com.gewara.service.sport.GuaranteeOrderService;
import com.gewara.service.sport.MemberCardService;
import com.gewara.service.sport.OpenTimeSaleService;
import com.gewara.service.sport.OpenTimeTableService;
import com.gewara.service.sport.SportOrderService;
import com.gewara.service.sport.SportService;
import com.gewara.support.ErrorCode;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.util.VmUtils;
import com.gewara.xmlbind.sport.RemoteMemberCardInfo;

@Service("sportOrderService")
public class SportOrderServiceImpl extends GewaOrderServiceImpl implements SportOrderService {
	
	@Autowired@Qualifier("sportService")
	public SportService sportService;
	public void setSportService(SportService sportService){
		this.sportService = sportService;
	}
	@Autowired@Qualifier("openTimeTableService")
	private OpenTimeTableService openTimeTableService;
	
	@Autowired@Qualifier("openTimeSaleService")
	private OpenTimeSaleService openTimeSaleService;
	
	@Autowired@Qualifier("guaranteeOrderService")
	private GuaranteeOrderService guaranteeOrderService;
	
	@Autowired@Qualifier("scalperService")
	private ScalperService scalperService;
	
	@Autowired@Qualifier("memberCardService")
	private MemberCardService memberCardService;
	
	@Override
	public ErrorCode validateFieldLock(List<OpenTimeItem> otiList) {
		String msg = "";
		for(OpenTimeItem oti : otiList){
			if(!oti.hasAvailable()) msg = "[" + oti.getFieldname() + oti.getHour() + "]";
		}
		if(StringUtils.isBlank(msg)) return ErrorCode.SUCCESS;
		return ErrorCode.getFailure(msg+"��ռ��");
	}
	@Override
	public ErrorCode checkOrderField(SportOrder order, List<OpenTimeItem> otiList) {
		String msg = "";
		for(OpenTimeItem oti : otiList){
			if(!oti.getMemberid().equals(order.getMemberid())) msg = "[" + oti.getHour() + "]";
			else if(OpenTimeItemConstant.STATUS_LOCKR.equals(oti.getStatus())){
				msg = "[" + oti.getHour() + "]";
			}
		}
		if(StringUtils.isBlank(msg)) return ErrorCode.SUCCESS;
		return ErrorCode.getFailure(ApiConstant.CODE_SEAT_OCCUPIED, msg+"��ռ��");
	}
	@Override
	public Map<Integer, List<Sport>> getProfileSportList() {
		List<SportProfile> spList = hibernateTemplate.find("from SportProfile p where p.booking='open' order by p.sortnum");
		Map<Integer, List<Sport>> spMap = new TreeMap<Integer, List<Sport>>();
		for(SportProfile sp : spList){
			Integer key = sp.getSortnum();
			Sport sport  = baseDao.getObject(Sport.class, sp.getId());
			List<Sport> sportList = new ArrayList<Sport>();
			if(spMap.containsKey(key)){
				sportList = spMap.get(key);
				sportList.add(sport);
			}else{
				sportList.add(sport);
			}
			spMap.put(key, sportList);
		}
		return spMap;
	}

	@Override
	public SportOrder getLastUnpaidSportOrder(Long memberid, String ukey, Long ottid) {
		DetachedCriteria query = DetachedCriteria.forClass(SportOrder.class);
		query.add(Restrictions.eq("memberid", memberid));
		if(ottid!=null) query.add(Restrictions.eq("ottid", ottid));
		query.add(Restrictions.eq("ukey", ukey));
		query.add(Restrictions.like("status", OrderConstant.STATUS_NEW, MatchMode.START));
		query.add(Restrictions.gt("validtime", new Timestamp(System.currentTimeMillis())));
		query.addOrder(Order.desc("addtime"));
		List<SportOrder> result = hibernateTemplate.findByCriteria(query);
		if(result.isEmpty()) return null;
		return result.get(0);
	}
	
	@Override
	public void cancelSportOrder(SportOrder order, Long memberid, String reason) {
		if(order.isNew() && order.getMemberid().equals(memberid)){
			OpenTimeTable ott = baseDao.getObject(OpenTimeTable.class, order.getOttid());
			if(ott.hasPeriod()||ott.hasInning()){
				Timestamp validtime = new Timestamp(System.currentTimeMillis()-1000);
				order.setStatus(OrderConstant.STATUS_REPEAT);
				order.setValidtime(validtime);
				baseDao.saveObject(order);				
			}else{
				Timestamp validtime = new Timestamp(System.currentTimeMillis()-1000);
				order.setStatus(OrderConstant.STATUS_REPEAT);
				order.setValidtime(validtime);
				List<OpenTimeItem> otiList = getMyOtiList(order.getId());
				for(OpenTimeItem oti : otiList){
					oti.setValidtime(validtime);
					baseDao.saveObject(oti);
				}
				baseDao.saveObject(order);
				dbLogger.warn("ȡ��δ֧��������" + order.getTradeNo() + "," + reason);
			}
		}
	}
	@Override
	public String getRemoteOtiids(List<OpenTimeItem> otiList){
		List<Long> ridsList = BeanUtil.getBeanPropertyList(otiList, Long.class, "rotiid", true);
		String strids = StringUtils.join(ridsList, ",");
		return strids;
	}
	@Override
	public ErrorCode<SportOrder> addSportOrder(OpenTimeTable ott, String fields, Long cardid, ErrorCode<RemoteMemberCardInfo> rmcode, String mobile, Member member, ApiUser partner)
			throws OrderException {
		GewaConfig gewaConfig = baseDao.getObject(GewaConfig.class, ConfigConstant.CFG_PAUSE_SPORT);
		Timestamp cur = DateUtil.getCurFullTimestamp();
		Timestamp pause = Timestamp.valueOf(gewaConfig.getContent());
		if(cur.before(pause)){
			return ErrorCode.getFailure("��ͣ��Ʊ��" + DateUtil.format(pause, "HH:mm"));
		}
		if(ott == null) return ErrorCode.getFailure("�ó��β����ڣ�");
		List<Long> fieldidList = BeanUtil.getIdList(fields, ",");
		if(fieldidList.size()==0) return ErrorCode.getFailure("��ѡ�񳡵ؼ�ʱ�䣡");
		if(fieldidList.size()>4) return ErrorCode.getFailure("ÿ�����ѡ4��ʱ��Σ�");
		//��ȡ�ĳ��ݵ�����ʱ�� cpf
		Sport2Item sport2Item = sportService.getSport2Item(ott.getSportid(), ott.getItemid());
		if(!sport2Item.isOpen()) return ErrorCode.getFailure("���ڿ���ʱ���ڽ���Ԥ����");
		Integer limitMinutes = sport2Item.getLimitminutes();
		//��ȡ�ĳ��ݵ�����ʱ�� cpf
		if(!ott.isBooking()) return ErrorCode.getFailure("�ݲ�����Ԥ����");
		if(!ott.hasField()) return ErrorCode.getFailure("�Ƿ�����");
		if(ott.getPlaydate().compareTo(DateUtil.getBeginningTimeOfDay(new Date()))==0) { //ʱ���ʱ
			for(Long id : fieldidList ){
				OpenTimeItem item = baseDao.getObject(OpenTimeItem.class, id);
				if(item.getHour().compareTo(item.gainZhour(limitMinutes))<0) return ErrorCode.getFailure(item.getHour()+"�ѹ��ڲ��ܹ���");
			}
		}
		Set<Long> fieldidList2 = new LinkedHashSet<Long>(fieldidList);
		List<OpenTimeItem> otiList = baseDao.getObjectList(OpenTimeItem.class, fieldidList2);
		ErrorCode code = validateFieldLock(otiList);
		if(!code.isSuccess()) return ErrorCode.getFailure(code.getMsg());
		Map<String, List<OpenTimeItem>> saleMap = BeanUtil.groupBeanList(otiList, "itemtype");
		if(!CollectionUtils.isEmpty(saleMap.get(OpenTimeTableConstant.ITEM_TYPE_VIE))){
			List<Long> otsIdList = BeanUtil.getBeanPropertyList(saleMap.get(OpenTimeTableConstant.ITEM_TYPE_VIE), "otsid", true);
			List<OpenTimeSale> saleList = baseDao.getObjectList(OpenTimeSale.class, otsIdList);
			for (OpenTimeSale sale : saleList) {
				if(sale.hasBooking()) return ErrorCode.getFailure("���۳��Σ��Ƿ���Ʊ��");
			}
		}
		ErrorCode code2 = validateOpenTimeItem(ott, otiList, cardid);
		if(!code2.isSuccess()) return ErrorCode.getFailure(code2.getMsg());
		ErrorCode<String> ccode = memberCardService.reMemberCardInfo(ott, cardid, rmcode, member);
		if(!ccode.isSuccess()){
			return ErrorCode.getFailure(ccode.getMsg());
		}
		boolean isMemberCardPay = false;
		MemberCardInfo card = null;
		if(cardid!=null){
			card = baseDao.getObject(MemberCardInfo.class, cardid);
			MemberCardType mct = baseDao.getObject(MemberCardType.class, card.getTypeid());
			ErrorCode<String> mctcode = memberCardService.validCardByOtt(mct, card, ott, otiList);
			if(!mctcode.isSuccess()){
				return ErrorCode.getFailure(mctcode.getMsg());
			}
			isMemberCardPay = true;
		}

		Long memberid = member.getId();
		String membername = member.getNickname();
		Long partnerid = PartnerConstant.GEWA_SELF; 
		if(partner!=null){
			membername = membername + "@" + partner.getBriefname();
			partnerid = partner.getId();
		}
		Timestamp t = DateUtil.addDay(new Timestamp(ott.getPlaydate().getTime()),1);
		String randomNum = nextRandomNum(t, 8, "0");
		SportOrder order = new SportOrder(memberid, membername, ott, ""+memberid);
		order.setStatus(OrderConstant.STATUS_NEW);
		order.setPartnerid(partnerid);
		String odertitle = ott.getSportname() + ott.getItemname() + "����Ԥ��";
		Timestamp addtime = new Timestamp(System.currentTimeMillis());
		Timestamp validtime = DateUtil.addMinute(addtime, OpenTimeTableConstant.MAX_MINUTS_TICKETS);
		order.setTradeNo(PayUtil.getSportTradeNo());
		order.setOrdertitle(odertitle);
		order.setMobile(mobile);
		order.setValidtime(validtime);
		order.setCheckpass(randomNum);
		Integer total = 0;
		Integer unitprice = 0;
		Integer costprice = 0;
		Integer sumcost = 0;
		String minhour = otiList.get(0).getHour();
		for(OpenTimeItem oti : otiList){
			if(oti.getPrice()<=0) return ErrorCode.getFailure("�۸��д�����ѡ����������");
			oti.setValidtime(validtime);
			oti.setMemberid(memberid);
			total += oti.getPrice();
			unitprice = oti.getPrice();
			costprice = oti.getCostprice();
			sumcost += oti.getCostprice();
			baseDao.saveObject(oti);
			if(oti.getHour().compareTo(minhour)<0) minhour = oti.getHour();
		}
		if(total<=0) return ErrorCode.getFailure("�۸��д�����ѡ����������");
		order.setUnitprice(unitprice);
		order.setCostprice(costprice);
		order.setQuantity(otiList.size());
		order.setTotalfee(total);
		order.setUpdatetime(addtime);
		order.setModifytime(addtime);
		order.setTotalcost(sumcost);
		setOrderDescription(order, ott, card, otiList, minhour);
		setOrderOtherinfo(order, sumcost); 	//��¼�ܳɱ�
		order.setCitycode(ott.getCitycode());
		order.setCardid(cardid);
		if(isMemberCardPay){
			order.setPaymethod(PaymethodConstant.PAYMETHOD_MEMBERCARDPAY);
		}
		baseDao.saveObject(order);
		for(OpenTimeItem oti : otiList){
			baseDao.saveObject(new SportOrder2TimeItem(order.getId(), oti.getId()));
			newBuyItem(ott, oti, 1);
		}
		if(ott.getRemoteid() != null){
			CusOrder cusOrder = new CusOrder();
			cusOrder.setOrderid(order.getId());
			baseDao.saveObject(cusOrder);
		}
		operationService.updateLastOperation("S" + memberid + StringUtil.md5(String.valueOf(memberid)), order.getTradeNo(), order.getAddtime(), ott.getPlayTimeByHour(minhour), "sport");
		return ErrorCode.getSuccessReturn(order);
	}
	
	@Override
	public ErrorCode<SportOrder> addSportOrder(OpenTimeSale ots) throws OrderException{
		if(!ots.hasLockStatus(OpenTimeTableConstant.SALE_STATUS_SUCCESS)){
			return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "����δ�ɹ���");
		}
		OpenTimeTable ott = baseDao.getObject(OpenTimeTable.class, ots.getOttid());
		OpenTimeSaleMember saleMember = openTimeSaleService.getLastOtsMember(ots.getId());
		if(saleMember != null && !VmUtils.eq(ots.getMemberid(), saleMember.getMemberid())){
			ots.setMemberid(saleMember.getMemberid());
			ots.setNickname(saleMember.getNickname());
			ots.setJointime(saleMember.getAddtime());
		}
		SellDeposit deposit = guaranteeOrderService.getSellDeposit(ots.getId(), ots.getMemberid(), SellDeposit.STATUS_PAID_SUCCESS);
		if(deposit == null) throw new OrderException(ApiConstant.CODE_SIGN_ERROR, "��֤�����");
		if(!ots.getMemberid().equals(deposit.getMemberid())) return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "�����޸�������Ϣ��");
		SportOrder order = baseDao.getObject(SportOrder.class, ots.getId());
		if(order != null) return ErrorCode.getSuccessReturn(order);
		Timestamp t = DateUtil.addDay(new Timestamp(ott.getPlaydate().getTime()),1);
		order = new SportOrder(ots.getMemberid(), ots.getNickname(), ott, String.valueOf(ots.getMemberid()));
		order.setStatus(OrderConstant.STATUS_NEW);
		order.setPartnerid(PartnerConstant.GEWA_SELF);
		String odertitle = ott.getSportname() + ott.getItemname() + "����Ԥ��";
		Timestamp addtime = new Timestamp(System.currentTimeMillis());
		Timestamp validtime = DateUtil.addHour(addtime, OpenTimeTableConstant.MAX_HOUR_TICKETS);
		String randomNum = nextRandomNum(t, 8, "0");
		order.setTradeNo(PayUtil.getSportTradeNo());
		order.setOrdertitle(odertitle);
		order.setMobile(deposit.getMobile());
		order.setValidtime(validtime);
		order.setCheckpass(randomNum);
		int unitprice = 0, costprice = 0, sumcost = 0;
		List<OpenTimeItem> otiList = baseDao.getObjectList(OpenTimeItem.class, BeanUtil.getIdList(ots.getOtiids(), ","));
		ErrorCode code = validateFieldLock(otiList);
		if(!code.isSuccess()){
			dbLogger.warn("validateFieldLock:" + code.getMsg());
			throw new OrderException(ApiConstant.CODE_SIGN_ERROR, code.getMsg());
		}
		for(OpenTimeItem oti : otiList){
			if(oti.getPrice()<=0) return ErrorCode.getFailure("�۸��д�����ѡ����������");
			oti.setValidtime(validtime);
			oti.setMemberid(ots.getMemberid());
			unitprice = oti.getPrice();
			costprice = oti.getCostprice();
			sumcost += oti.getCostprice();
			baseDao.saveObject(oti);
		}
		order.setUnitprice(unitprice);
		order.setCostprice(costprice);
		order.setQuantity(otiList.size());
		order.setTotalfee(ots.getCurprice());
		order.setUpdatetime(addtime);
		order.setModifytime(addtime);
		order.setTotalcost(sumcost);
		setOrderDescription(order, ott, null, otiList, ots.getStarttime());
		setOrderOtherinfo(order, sumcost); 	//��¼�ܳɱ�
		Map<String,String> otherinfoMap = JsonUtils.readJsonToMap(order.getOtherinfo());
		otherinfoMap.put(PayConstant.DISCOUNT_TAG_DEPOSIT, String.valueOf(deposit.getPrice()));
		otherinfoMap.put("depositId", String.valueOf(deposit.getId()));
		
		order.setOtherinfo(JsonUtils.writeMapToJson(otherinfoMap));
		order.setCitycode(ott.getCitycode());
		baseDao.saveObject(order);
		ots.setOrderid(order.getId());
		ots.setPaidvalidtime(order.getValidtime());
		baseDao.saveObject(ots);
		for(OpenTimeItem oti : otiList){
			baseDao.saveObject(new SportOrder2TimeItem(order.getId(), oti.getId()));
			newBuyItem(ott, oti, 1);
		}
		if(ott.hasRemoteOtt()){
			CusOrder cusOrder = new CusOrder();
			cusOrder.setOrderid(order.getId());
			baseDao.saveObject(cusOrder);
		}
		useSellDeposit(order, ots, deposit, addtime);
		return ErrorCode.getSuccessReturn(order);
	}
	
	@Override
	public ErrorCode<SportOrder> addSportOrder(Long otiid, String starttime, Integer time, Integer quantity, String mobile, Member member, ApiUser partner){
		GewaConfig gewaConfig = baseDao.getObject(GewaConfig.class, ConfigConstant.CFG_PAUSE_SPORT);
		Timestamp cur = DateUtil.getCurFullTimestamp();
		Timestamp pause = Timestamp.valueOf(gewaConfig.getContent());
		if(cur.before(pause)){
			return ErrorCode.getFailure(ApiConstant.CODE_OPI_CLOSED, "��ͣ��Ʊ��" + DateUtil.format(pause, "HH:mm"));
		}
		OpenTimeItem oti = baseDao.getObject(OpenTimeItem.class, otiid);
		if(oti == null) return ErrorCode.getFailure("���ظ�ʱ�β����ڣ�");
		if(!oti.hasStatusNew() || cur.after(oti.getValidtime())) return ErrorCode.getFailure("������ʱ�β�����Ԥ����");
		/*if(oti.hasItemType(OpenTimeTableConstant.ITEM_TYPE_VIP)) return ErrorCode.getFailure("������Ϊ��Ա���أ����߻�Աͨ����");
		if(oti.hasItemType(OpenTimeTableConstant.ITEM_TYPE_VIE)){
			//OpenTimeSale
			return ErrorCode.getFailure("������Ϊ���ĳ��أ����߾���ͨ����");
		} */
		OpenTimeTable ott = baseDao.getObject(OpenTimeTable.class, oti.getOttid());
		if(ott == null) return ErrorCode.getFailure("���β����ڣ�");
		Sport2Item sport2Item = sportService.getSport2Item(ott.getSportid(), ott.getItemid());
		if(!sport2Item.isOpen()) return ErrorCode.getFailure("���ڿ���ʱ���ڽ���Ԥ����");
		if(!ott.isBooking()) return ErrorCode.getFailure("����������Ԥ����");
		if(!ott.hasPeriod() || !oti.hasPeriod()) return ErrorCode.getFailure("��ʱ��γ��λ򳡵أ�");
		return createSportOrder(ott, oti, starttime, mobile, quantity, time, member, partner);
	}
	@Override
	public ErrorCode<SportOrder> addSportOrder(Long otiid, String starttime, Integer quantity, String mobile, Member member, ApiUser parnter){
		GewaConfig gewaConfig = baseDao.getObject(GewaConfig.class, ConfigConstant.CFG_PAUSE_SPORT);
		Timestamp cur = DateUtil.getCurFullTimestamp();
		Timestamp pause = Timestamp.valueOf(gewaConfig.getContent());
		if(cur.before(pause)){
			return ErrorCode.getFailure(ApiConstant.CODE_OPI_CLOSED, "��ͣ��Ʊ��" + DateUtil.format(pause, "HH:mm"));
		}
		OpenTimeItem oti = baseDao.getObject(OpenTimeItem.class, otiid);
		if(oti == null) return ErrorCode.getFailure("���ظ�ʱ�β����ڣ�");
		if(!oti.hasStatusNew() || cur.after(oti.getValidtime())) return ErrorCode.getFailure("������ʱ�β�����Ԥ����");
		OpenTimeTable ott = baseDao.getObject(OpenTimeTable.class, oti.getOttid());
		if(ott == null) return ErrorCode.getFailure("���β����ڣ�");
		Sport2Item sport2Item = sportService.getSport2Item(ott.getSportid(), ott.getItemid());
		if(!sport2Item.isOpen()) return ErrorCode.getFailure("���ڿ���ʱ���ڽ���Ԥ����");
		if(!ott.isBooking()) return ErrorCode.getFailure("����������Ԥ����");
		if(!ott.hasInning() || !oti.hasInning()) return ErrorCode.getFailure("�Ǿ������λ򳡵أ�");
		return createSportOrder(ott, oti, starttime, mobile, quantity, null, member, parnter);
	}
	private ErrorCode<SportOrder> createSportOrder(OpenTimeTable ott, OpenTimeItem oti, String starttime, String mobile, Integer quantity, Integer time, Member member, ApiUser partner){
		if((!ott.hasInning() || !oti.hasInning()) && (!ott.hasPeriod() || !oti.hasPeriod())) return ErrorCode.getFailure("���λ򳡵����ݴ���");
		Timestamp cur = DateUtil.getCurFullTimestamp();
		Date curDate = DateUtil.getDateFromTimestamp(cur);
		Sport2Item sport2Item = sportService.getSport2Item(ott.getSportid(), ott.getItemid());
		Date validDate = DateUtil.addMinute(curDate, sport2Item.getLimitminutes());
		Date curDate2 = DateUtil.getBeginningTimeOfDay(curDate);
		String hour = DateUtil.format(validDate, "HH:mm");
		if(DateUtil.getDiffDay(ott.getPlaydate(), curDate2) == 0){
			if(oti.getEndhour().compareTo(hour)<=0) return ErrorCode.getFailure(ott.getEndtime() + "�ѹ��ڲ��ܹ���");
		}
		if(StringUtils.isBlank(starttime)) ErrorCode.getFailure("��ʼʱ��β���Ϊ�գ�");
		if(quantity == null) return ErrorCode.getFailure("ѡ����������Ϊ�գ�");
		if(quantity <1 ||quantity >4) return ErrorCode.getFailure("Ԥ������ֻ����1-4����");
		if(StringUtils.isBlank(mobile)) return ErrorCode.getFailure("�ֻ��Ų���Ϊ�գ�");
		if(!ValidateUtil.isMobile(mobile)) return ErrorCode.getFailure("�ֻ��Ÿ�ʽ����");
		List<String> timeList = SportOrderHelper.getStarttimeList(ott.getPlaydate(), oti);
		if(!timeList.contains(starttime)) return ErrorCode.getFailure("�볡ʱ�䲻�ڸó���ʱ������ڣ�");
		if(ott.hasPeriod()){
			if(time == null) return ErrorCode.getFailure("ʱ������Ϊ�գ�");
			List<Integer> periodList = SportOrderHelper.getPeriodList(ott.getPlaydate(), oti);
			if(!periodList.contains(time)) return ErrorCode.getFailure("ʱ������");
		}else{
			time = oti.getUnitMinute();
		}
		int count = getSellTimeTableCount(oti.getId(), starttime);
		if(quantity+ count > oti.getQuantity()) return ErrorCode.getFailure("��Ԥ������������");
		Long memberid = member.getId();
		String membername = member.getNickname();
		Long partnerid = PartnerConstant.GEWA_SELF;
		if(partner!=null) { 
			membername = membername + "@" + partner.getBriefname();
			partnerid = partner.getId();
		}
		/*SportOrder lastUnpaidOrder = getLastUnpaidSportOrder(memberid, member.getId()+"", ott.getId());
		if(lastUnpaidOrder != null) cancelSportOrder(lastUnpaidOrder, member.getId(), "�ظ�����");*/
		SportOrder order = new SportOrder(memberid, membername, ott, ""+memberid);
		order.setStatus(OrderConstant.STATUS_NEW);
		order.setPartnerid(partnerid);
		String odertitle = ott.getSportname() + ott.getItemname() + "����Ԥ��";
		Timestamp validtime = DateUtil.addMinute(cur, OpenTimeTableConstant.MAX_MINUTS_TICKETS);
		order.setTradeNo(PayUtil.getSportTradeNo());
		order.setOrdertitle(odertitle);
		order.setMobile(mobile);
		order.setValidtime(validtime);
		String randomNum = nextRandomNum(cur, 8, "0");
		order.setCheckpass(randomNum);
		Integer unitprice = oti.getPrice();
		Integer total = unitprice * quantity;
		Integer costprice = oti.getCostprice();
		Integer sumcost = costprice * quantity;
		if(oti.hasUnitTime() && oti.hasPeriod()){
			int tmpHour = time/oti.getUnitMinute();
			total = tmpHour * total;
			sumcost = tmpHour * sumcost;
		}
		if(total<=0) return ErrorCode.getFailure("�۸��д�����ѡ����������");
		order.setUnitprice(oti.getPrice());
		order.setCostprice(costprice);
		order.setQuantity(quantity);
		order.setTotalfee(total);
		order.setUpdatetime(cur);
		order.setModifytime(cur);
		order.setTotalcost(sumcost);
		String endtime = setOrderDescription(order, ott, oti, starttime, time);
		setOrderOtherinfo(order, sumcost); 	//��¼�ܳɱ�
		order.setCitycode(ott.getCitycode());
		baseDao.saveObject(order);
		if(ott.getRemoteid() != null){
			CusOrder cusOrder = new CusOrder();
			cusOrder.setOrderid(order.getId());
			baseDao.saveObject(cusOrder);
		}
		newBuyItem(ott, oti, quantity);
		createSellTimeTable(order, oti, starttime,endtime, time);
		operationService.updateLastOperation("S" + memberid + StringUtil.md5(String.valueOf(memberid)), order.getTradeNo(), order.getAddtime(), ott.getPlayTimeByHour(starttime), "sport");
		return ErrorCode.getSuccessReturn(order);
	}
	public Integer getSellTimeTableCount(Long otiid, String starttime){
		String hql = "select count(*) from SellTimeTable stt where stt.otiid=? and stt.starttime=? and (stt.status=? or stt.status=? and stt.validtime>? )";
		List<Long> countList = hibernateTemplate.find(hql, otiid, starttime, SellTimeTable.STATUS_SOLD, SellTimeTable.STATUS_NEW, DateUtil.getCurFullTimestamp());
		if(countList.isEmpty()) return 0;
		return Long.valueOf(countList.get(0)).intValue();
	}
	private void createSellTimeTable(SportOrder order, OpenTimeItem oti, String starttime, String endtime, Integer time){
		SellTimeTable stt = new SellTimeTable(order.getId(), oti, order.getValidtime());
		stt.setStarttime(starttime);
		stt.setEndtime(endtime);
		stt.setSumMinute(time);
		stt.setOtiid(oti.getId());
		stt.setQuantity(order.getQuantity());
		baseDao.saveObject(stt);
	}
	private String setOrderDescription(SportOrder order, OpenTimeTable ott, OpenTimeItem oti, String starttime, Integer time){
		Map<String, String> descMap = VmUtils.readJsonToMap(order.getDescription2());
		descMap.put("�˶�����", ott.getSportname());
		descMap.put("�˶���Ŀ", ott.getItemname());
		String startDate = DateUtil.format(ott.getPlaydate(),"yyyy-MM-dd") + " " + starttime + ":00";
		descMap.put("Ԥ�Ƶ���ʱ��", startDate);
		String otiDate = DateUtil.format(ott.getPlaydate(),"yyyy-MM-dd") + " " + oti.getHour() + " - " +
										DateUtil.format(ott.getPlaydate(),"yyyy-MM-dd") + " " + oti.getEndhour();
		descMap.put("ʱ��", otiDate);
		if((ott.hasPeriod() || ott.hasInning()) && oti.hasUnitTime()){
			descMap.put("ʱ��", time + "����");
		}else{
			descMap.put("ʱ��", "����ʱ");
		}
		Timestamp curTime = DateUtil.parseTimestamp(startDate);
		String endtime = "";
		String remark = "";
		if(oti.hasUnitTime()){
			Timestamp endTime = DateUtil.addMinute(curTime, time);
			endtime = DateUtil.format(endTime, "HH:mm");
		}else{
			Timestamp endTime = ott.getPlayTimeByHour(oti.getEndhour());
			endtime = DateUtil.format(endTime, "HH:mm");
		}
		if(oti.hasPeriod()){
			remark = starttime + "-" + endtime + " " + order.getQuantity() +"��  "+ order.getTotalfee()+"Ԫ";
		}else if(oti.hasInning()){
			remark = starttime + "-" + endtime + " " + order.getQuantity() +"�� "+ order.getTotalfee()+"Ԫ";
		}
		descMap.put("��ϸ", remark);
		order.setDescription2(JsonUtils.writeMapToJson(descMap));
		return endtime;
	}
	private void setOrderDescription(SportOrder order, OpenTimeTable ott, MemberCardInfo card, List<OpenTimeItem> otiList, String minhour){
		Map<String, String> descMap = VmUtils.readJsonToMap(order.getDescription2());
		descMap.put("�˶�����", ott.getSportname());
		descMap.put("�˶���Ŀ", ott.getItemname());
		descMap.put("ʱ��", DateUtil.format(ott.getPlaydate(),"yyyy-MM-dd") + " " + minhour + ":00");
		descMap.put("��ϸ", SportOrderHelper.getFieldText(otiList));
		if(card!=null){
			descMap.put(MemberCardConstant.VIPCARD, card.getMemberCardCode());
		}
		order.setDescription2(JsonUtils.writeMapToJson(descMap));
	}
	private void setOrderOtherinfo(SportOrder order, Integer sumcost) {
		Map<String, String> infoMap = VmUtils.readJsonToMap(order.getOtherinfo());
		infoMap.put("sumcost", sumcost+"");
		order.setOtherinfo(JsonUtils.writeMapToJson(infoMap));
	}
	@Override
	public OrderContainer processOrderPay(SportOrder order, OpenTimeTable ott) throws OrderException{
		return processOrderPayInternal(order);
	}
	@Override
	public void processSportOrder(SportOrder order, OpenTimeTable ott, List<OpenTimeItem> otiList) throws OrderException{
		if(order.isPaidUnfix()){
			Timestamp cur = new Timestamp(System.currentTimeMillis());
			if(ott.hasPeriod()||ott.hasInning()){
				SellTimeTable stt = baseDao.getObject(SellTimeTable.class, order.getId());
				stt.setStatus(SellTimeTable.STATUS_SOLD);
				baseDao.saveObject(stt);
			}else{
				//�ٴμ���Ƿ�����λ��������س�ͻ��
				String msg = "";
				for(OpenTimeItem oti: otiList){
					if(!oti.getMemberid().equals(order.getMemberid()) && !oti.hasAvailable()){
						msg += "[" + oti.getHour() + "]";
					}
				}
				if(StringUtils.isNotBlank(msg)) throw new OrderException(ApiConstant.CODE_DATA_ERROR, "ʱ��" + msg + "��ռ�ã�");
				for(OpenTimeItem oti : otiList){
					oti.setStatus(OpenTimeItemConstant.STATUS_SOLD);
					oti.setValidtime(order.getValidtime());
					oti.setMemberid(order.getMemberid());
					oti.setValidtime(order.getValidtime());
					baseDao.saveObject(oti);
				}
				OpenTimeSale ots = baseDao.getObjectByUkey(OpenTimeSale.class, "orderid", order.getId());
				if(ots != null){
					ots.setLockStatus(OpenTimeTableConstant.SALE_STATUS_SUCCESS_PAID);
					baseDao.saveObject(ots);
				}
			}
			ott.addSales(order.getQuantity());
			order.setUpdatetime(cur);
			order.setModifytime(cur);
			order.setValidtime(DateUtil.addDay(cur, 180));
			order.setStatus(OrderConstant.STATUS_PAID_SUCCESS);
			order.setSettle(OrderConstant.SETTLE_Y);
			baseDao.saveObject(ott);
			baseDao.saveObject(order);
			OrderExtra orderExtra = processOrderExtra(order);
			SportProfile sp = baseDao.getObject(SportProfile.class, ott.getSportid());
			sp.setPretype(sp.getPretype());
			if(sp.hasPretype(SportProfile.PRETYPE_ENTRUST)){
				orderExtra.setInvoice(OrderExtraConstant.INVOICE_F);
				baseDao.saveObject(orderExtra);
			}
		}else{
			throw new OrderException(ApiConstant.CODE_DATA_ERROR, "����״̬����ȷ��");
		}
	}
	@Override
	public List<OpenTimeTable> getOttList(Long sportid, Long itemid, Date from, Date to, boolean open){
		DetachedCriteria query = DetachedCriteria.forClass(OpenTimeTable.class);
		if(sportid != null) query.add(Restrictions.eq("sportid", sportid));
		if(itemid != null) query.add(Restrictions.eq("itemid", itemid));
		if(from != null) query.add(Restrictions.ge("playdate", from));
		if(to != null) query.add(Restrictions.le("playdate", to));
		query.add(Restrictions.ne("status", OpenTimeTableConstant.STATUS_DISCARD));
		if(open) query.add(Restrictions.eq("status", OpenTimeTableConstant.STATUS_BOOK));
		query.addOrder(Order.asc("itemid"));
		query.addOrder(Order.asc("playdate"));
		query.addOrder(Order.asc("id"));
		List<OpenTimeTable> result = hibernateTemplate.findByCriteria(query);
		return result;
	}
	@Override
	public Integer getOttCount(Long sportid, Long itemid, Date from, Date to, boolean open){
		DetachedCriteria query = DetachedCriteria.forClass(OpenTimeTable.class);
		if(sportid != null) query.add(Restrictions.eq("sportid", sportid));
		if(itemid != null) query.add(Restrictions.eq("itemid", itemid));
		if(from != null) query.add(Restrictions.ge("playdate", from));
		if(to != null) query.add(Restrictions.le("playdate", to));
		query.add(Restrictions.ne("status", OpenTimeTableConstant.STATUS_DISCARD));
		if(open) query.add(Restrictions.eq("status", OpenTimeTableConstant.STATUS_BOOK));
		query.setProjection(Projections.rowCount());
		List<Long> result = hibernateTemplate.findByCriteria(query);
		return Integer.valueOf(result.get(0)+"");
	}
	@Override
	public List<SportItem> getOpenSportItemList(Long sportid, Date from, Date to, boolean open){
		DetachedCriteria query = DetachedCriteria.forClass(OpenTimeTable.class);
		if(sportid != null) query.add(Restrictions.eq("sportid", sportid));
		if(from != null) query.add(Restrictions.ge("playdate", from));
		if(to != null) query.add(Restrictions.le("playdate", to));
		query.add(Restrictions.ne("status", OpenTimeTableConstant.STATUS_DISCARD));
		if(open) query.add(Restrictions.eq("status", OpenTimeTableConstant.STATUS_BOOK));
		query.setProjection(Projections.distinct(Projections.property("itemid")));
		List<Long> itemidList = hibernateTemplate.findByCriteria(query);
		List<SportItem> itemList = baseDao.getObjectList(SportItem.class, itemidList);
		return itemList;
	}
	@Override
	public OpenTimeTable getOtt(Long sportid, Long itemid, Date playdate){
		DetachedCriteria query = DetachedCriteria.forClass(OpenTimeTable.class);
		query.add(Restrictions.eq("sportid", sportid));
		query.add(Restrictions.eq("itemid", itemid));
		query.add(Restrictions.eq("playdate", playdate));
		query.add(Restrictions.ne("status", OpenTimeTableConstant.STATUS_DISCARD));
		List<OpenTimeTable> ottList = hibernateTemplate.findByCriteria(query, 0, 1);
		if(ottList.isEmpty()) return null;
		return ottList.get(0);
	}
	@Override
	public List<SportOrder> getSportOrderList(SearchOrderCommand soc) {
		return getSportOrderList(soc, 0, 500);
	}
	@Override
	public List<SportOrder> getSportOrderList(SearchOrderCommand soc, int from, int maxnum) {
		DetachedCriteria query = DetachedCriteria.forClass(SportOrder.class);
		if(StringUtils.isNotBlank(soc.getMobile())) query.add(Restrictions.eq("mobile", soc.getMobile()));
		if(StringUtils.isNotBlank(soc.getTradeNo())) query.add(Restrictions.eq("tradeNo", soc.getTradeNo()));
		if(soc.getMinute()!=null){
			Timestamp fromtime = DateUtil.addMinute(new Timestamp(System.currentTimeMillis()), -soc.getMinute());
			query.add(Restrictions.ge("addtime", fromtime));
		}
		if(StringUtils.isNotBlank(soc.getOrdertype())){//�����й�ʱ�Զ�ȡ�����˵�
			if(soc.getOrdertype().equals(OrderConstant.STATUS_CANCEL)){
				query.add(Restrictions.or(Restrictions.like("status", soc.getOrdertype(), MatchMode.START),
						Restrictions.and(Restrictions.like("status", OrderConstant.STATUS_NEW, MatchMode.START), 
								Restrictions.lt("validtime", new Timestamp(System.currentTimeMillis())))));
			}else{
				query.add(Restrictions.like("status", soc.getOrdertype(), MatchMode.START));
				if(StringUtils.startsWith(soc.getOrdertype(), OrderConstant.STATUS_NEW)){//�����й�ʱ�Զ�ȡ�����˵�
					query.add(Restrictions.ge("validtime", new Timestamp(System.currentTimeMillis())));
				}
			}
		}
		if(soc.getMemberid()!=null) query.add(Restrictions.eq("memberid", soc.getMemberid()));
		if(soc.getSportid()!=null) query.add(Restrictions.eq("sportid", soc.getSportid()));
		if(soc.getItemid()!=null)query.add(Restrictions.eq("itemid", soc.getItemid()));
		if(soc.getOrderid()!=null) query.add(Restrictions.eq("id", soc.getOrderid()));
		if(soc.getOttid()!=null) query.add(Restrictions.eq("ottid", soc.getOttid()));
		query.addOrder(Order.desc("addtime"));
		List<SportOrder> orderList = hibernateTemplate.findByCriteria(query,from,maxnum);
		return orderList;
	}
	@Override
	public List<SportOrder> getValidSportOrderList(SearchOrderCommand soc, int from, int maxnum) {
		DetachedCriteria query = DetachedCriteria.forClass(SportOrder.class);
		if(StringUtils.isNotBlank(soc.getMobile())) query.add(Restrictions.eq("mobile", soc.getMobile()));
		if(StringUtils.isNotBlank(soc.getTradeNo())) query.add(Restrictions.eq("tradeNo", soc.getTradeNo()));
		if(soc.getMinute()!=null){
			Timestamp fromtime = DateUtil.addMinute(new Timestamp(System.currentTimeMillis()), -soc.getMinute());
			query.add(Restrictions.ge("addtime", fromtime));
		}
		query.add(Restrictions.not(Restrictions.like("status", OrderConstant.STATUS_CANCEL, MatchMode.START)));
		if(soc.getMemberid()!=null) query.add(Restrictions.eq("memberid", soc.getMemberid()));
		if(soc.getSportid()!=null) query.add(Restrictions.eq("sportid", soc.getSportid()));
		if(soc.getItemid()!=null)query.add(Restrictions.eq("itemid", soc.getItemid()));
		if(soc.getOrderid()!=null) query.add(Restrictions.eq("id", soc.getOrderid()));
		if(soc.getOttid()!=null) query.add(Restrictions.eq("ottid", soc.getOttid()));
		query.addOrder(Order.desc("addtime"));
		List<SportOrder> orderList = hibernateTemplate.findByCriteria(query,from,maxnum);
		return orderList;
	}
	@Override
	public List<SportField> getSportFieldList(Long sportid, Long itemid){
		String qry = "from SportField s where s.sportid=? and s.itemid=? order by s.ordernum asc";
		return hibernateTemplate.find(qry, sportid, itemid);
	}
	@Override
	public List<SportField> getSportFieldList(Long ottid){
		String qry = "select distinct oti.fieldid from OpenTimeItem oti where oti.ottid=? and oti.status!=? and exists(select f.id from SportField f where f.id=oti.fieldid and f.status=?)";
		List<Long> fieldidList = hibernateTemplate.find(qry, ottid, OpenTimeItemConstant.STATUS_DELETE, "Y");
		List<SportField> fieldList = baseDao.getObjectList(SportField.class, fieldidList);
		Collections.sort(fieldList, new PropertyComparator("ordernum", false, true));
		return fieldList;
	}
	@Override
	public List<SportField> getAllSportFieldList(Long ottid){
		String qry = "select distinct oti.fieldid from OpenTimeItem oti where oti.ottid=?";
		List<Long> fieldidList = hibernateTemplate.find(qry, ottid);
		List<SportField> fieldList = baseDao.getObjectList(SportField.class, fieldidList);
		Collections.sort(fieldList, new PropertyComparator("ordernum", false, true));
		return fieldList;
	}
	@Override
	public List<String> getPlayHourList(Long ottid, String status) {
		if(StringUtils.isNotBlank(status)){
			String qry = "select distinct oti.hour from OpenTimeItem oti where oti.ottid=? and oti.status<>? order by oti.hour";
			List<String> list = hibernateTemplate.find(qry, ottid, status);
			return list;
		}
		String qry = "select distinct oti.hour from OpenTimeItem oti where oti.ottid=? order by oti.hour";
		List<String> list = hibernateTemplate.find(qry, ottid);
		return list;
	}
	@Override
	public List<Long> getMemberidListBySportid(Long sportid, Timestamp addtime, int from, int maxnum) {
		DetachedCriteria qry = DetachedCriteria.forClass(SportOrder.class);
		qry.add(Restrictions.eq("sportid", sportid));
		qry.add(Restrictions.ne("status", OrderConstant.STATUS_PAID_SUCCESS));
		qry.add(Restrictions.gt("addtime", addtime));
		qry.addOrder(Order.desc("addtime"));
		List<SportOrder> orderList = hibernateTemplate.findByCriteria(qry, 0, maxnum);
		List<Long> memberidList = BeanUtil.getBeanPropertyList(orderList, Long.class, "memberid", true);
		return memberidList;
	}
	@Override
	public List<SportOrder> getOrderListByMemberid(Long memberid){
		DetachedCriteria qry = DetachedCriteria.forClass(SportOrder.class);
		qry.add(Restrictions.eq("memberid", memberid));
		qry.add(Restrictions.eq("status", OrderConstant.STATUS_PAID_SUCCESS));
		List<SportOrder> orderList = hibernateTemplate.findByCriteria(qry);
		return orderList;
	}
	private ErrorCode getDiscount(SportOrder order, OpenTimeTable table, ElecCard card, Long memberid){
		//1���жϿ��Ƿ���Ч
		if(!card.available()) return ErrorCode.getFailure("�˶һ�ȯ�Ѿ������ʧЧ��");
		if(!card.validTag(PayConstant.APPLY_TAG_SPORT)) return ErrorCode.getFailure("�˿��������˶����ʹ��");
		if(card.getPossessor()!=null && !card.getPossessor().equals(memberid)){
			return ErrorCode.getFailure("�����ñ��˵Ķһ�ȯ��");
		}
		if(StringUtils.isNotBlank(card.getWeektype())){
			String week = ""+DateUtil.getWeek(table.getPlaydate());
			if(card.getWeektype().indexOf(week) < 0){ 
				return ErrorCode.getFailure("�˶һ�ȯֻ������" + card.getWeektype() + "ʹ�ã�");
			}
		}
		if(StringUtils.isNotBlank(card.getValidcinema())){
			List<Long> cidList = BeanUtil.getIdList(card.getValidcinema(), ",");
			if(!cidList.contains(order.getSportid())){
				return ErrorCode.getFailure("�˶һ�ȯ�����ڴ˳���ʹ�ã�");
			}
		}
		if(!card.isUseCurTime()){//ʱ�������
			String opentime = card.getEbatch().getAddtime1();
			String closetime = card.getEbatch().getAddtime2();
			return ErrorCode.getFailure("�˶һ�ȯֻ����" + opentime + "��" +  closetime + "ʱ����ʹ�ã�");
		}
		
		if(StringUtils.isNotBlank(card.getValidmovie())){
			List<Long> cidList = BeanUtil.getIdList(card.getValidmovie(), ",");
			if(!cidList.contains(order.getItemid())){
				return ErrorCode.getFailure("���˶���Ŀ����ʹ�ô˶һ�ȯ��");
			}
		}
		if(StringUtils.isNotBlank(card.getValiditem())){
			List<Long> cidList = BeanUtil.getIdList(card.getValiditem(), ",");
			if(!cidList.contains(order.getOttid())){
				return ErrorCode.getFailure("�����β���ʹ�ô˶һ�ȯ��");
			}
		}
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		if("D".equals(card.getCardtype()) && discountList.size() > 0){
			return ErrorCode.getFailure("����ȯ�����ظ�ʹ�û��������Żݷ�ʽ���ã�");
		}
		for(Discount discount: discountList){
			if(discount.getRelatedid().equals(card.getId()))
				return ErrorCode.getFailure("�˶һ�ȯ��ʹ�ã�");
			if(("C".equals(card.getCardtype()) || "A".equals(card.getCardtype())) 
					&& !card.getCardtype().equals(discount.getCardtype())){
				return ErrorCode.getFailure("�˶һ�ȯ�����������Żݷ�ʽ���ã�");
			}
		}
		int amount = 0; Long goodsid = null;
		String description = "";
		OpenTimeItem item = null;
		if(table.hasField()){
			List<OpenTimeItem> otiList = getMyOtiList(order.getId());
			item = SportOrderHelper.getMaxOpenTimeItem(otiList, discountList, card.getEbatch().getOpentime(), card.getEbatch().getClosetime());
			if(item==null) return ErrorCode.getFailure("�Ѿ�û�г��ؿ���ʹ�öһ�ȯ��");
		}else{
			SellTimeTable stt = baseDao.getObject(SellTimeTable.class, order.getId());
			int num = stt.getSumMinute()/stt.getUnitMinute();
			if( num == 0) num = 1;
			if(discountList.size()>= num * order.getQuantity()) return ErrorCode.getFailure("�Ѿ�û��ʱ��ο���ʹ�öһ�ȯ��");
			item = baseDao.getObject(OpenTimeItem.class,  stt.getOtiid());
			if(table.hasPeriod()&&StringUtils.isNotBlank(card.getEbatch().getOpentime()) && StringUtils.isNotBlank(card.getEbatch().getClosetime())){
				if(card.getEbatch().getClosetime().compareTo(StringUtils.replace(item.getEndhour(),":",""))<=0 || card.getEbatch().getOpentime().compareTo(StringUtils.replace(item.getHour(),":",""))>0)
					return ErrorCode.getFailure("�˶һ�ȯ�����ڸ�ʱ���ʹ�ã�");
			}
		}
		if(card.getEbatch().getCardtype().equals(PayConstant.CARDTYPE_C) ||
			card.getEbatch().getCardtype().equals(PayConstant.CARDTYPE_D)){
			amount = card.getEbatch().getAmount();
			description = card.getCardno() + "����" + amount + "Ԫ";
		}else if(card.getEbatch().getCardtype().equals(PayConstant.CARDTYPE_A)){
			Integer maxAmount = card.getEbatch().getAmount();
			if(table.hasPeriod() || table.hasInning()){
				SellTimeTable stt = baseDao.getObject(SellTimeTable.class, order.getId());
				if(maxAmount != null && stt.getPrice()>maxAmount) return ErrorCode.getFailure("�һ����εļ۸����ȯ��ʹ���޶���ܶһ���");
				goodsid = stt.getOtiid();
				description = card.getCardno() + "����" + stt.getStarttime()+ "-" + stt.getEndtime() + ",��λΪ" + stt.getUnitMinute() + "��һ��ʱ��";
				amount = stt.getPrice();
			}else if(table.hasField()){
				if(maxAmount != null && item.getPrice()>maxAmount) return ErrorCode.getFailure("�һ����صļ۸����ȯ��ʹ���޶���ܶһ���");
				goodsid = item.getId();
				description = card.getCardno() + "����" + item.getHour();
				amount = item.getPrice();
			}
		}else {
			return ErrorCode.getFailure("����ȯ����ʹ�ã�");
		}
		if(amount <= 0) return ErrorCode.getFailure("ʹ�ô˶һ�ȯ�ò����κ��Żݣ��뿴ʹ��˵����");
		Discount discount = new Discount(order.getId(), PayConstant.DISCOUNT_TAG_ECARD, card.getId(), card.getCardtype());
		discount.setDescription(description);
		discount.setGoodsid(goodsid);
		discount.setBatchid(card.getEbatch().getId());
		discount.setAmount(amount);
		return ErrorCode.getSuccessReturn(discount);
	}
	@Override
	public ErrorCode usePoint(Long orderId, Long memberId, int usePoint){
		ErrorCode<String> pcode = pointService.validUsePoint(memberId);
		if(!pcode.isSuccess()) return ErrorCode.getFailure(pcode.getMsg());
		SportOrder order = baseDao.getObject(SportOrder.class, orderId);
		ErrorCode code = paymentService.validUse(order);
		if(!code.isSuccess()) return code;
		if(order.hasMemberCardPay()){
			return ErrorCode.getFailure("��Ա��֧����֧��ʹ�û��֣�");
		}
		MemberInfo info = baseDao.getObject(MemberInfo.class, memberId);
		if(info.getPointvalue() < usePoint) return ErrorCode.getFailure("���Ļ��ֲ�����");

		OpenTimeTable table = baseDao.getObject(OpenTimeTable.class, order.getOttid());
		if(table.getMaxpoint() < usePoint) return ErrorCode.getFailure("��ʹ�õĻ��ֳ�������" + table.getMaxpoint());
		int amount = usePoint/ConfigConstant.POINT_RATIO;
		usePoint = amount * ConfigConstant.POINT_RATIO;
		if(usePoint < table.getMinpoint() || amount == 0){
			return ErrorCode.getFailure("��ʹ�õĻ�����������" + table.getMinpoint());
		}
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		for(Discount discount: discountList){
			if(discount.getTag().equals(PayConstant.DISCOUNT_TAG_POINT))
				return ErrorCode.getFailure("���Ѿ�ʹ�ù����֣����иı䣬����ȡ����");
			if(PayConstant.CARDTYPE_D.equals(discount.getCardtype())){
				return ErrorCode.getFailure("���ֲ��ܺ��Ż�ȯһ��ʹ�ã�");
			}
			if(PayConstant.CARDTYPE_PARTNER.equals(discount.getCardtype())){
				return ErrorCode.getFailure("�Ѿ�ʹ���������Żݣ�����ͬʱʹ�û��֣�");
			}
		}
		
		Discount discount = new Discount(order.getId(), PayConstant.DISCOUNT_TAG_POINT, memberId, PayConstant.CARDTYPE_POINT);
		discount.setDescription(usePoint + "���ֵ���" + amount + "Ԫ");
		discount.setAmount(amount);
		baseDao.saveObject(discount);
		GewaOrderHelper.useDiscount(order, discountList, discount);
		baseDao.saveObject(order);
		
		return ErrorCode.SUCCESS;
	}
	@Override
	public ErrorCode<SportOrderContainer> useElecCard(Long orderId, ElecCard card, Long memberid){
		SportOrder order = baseDao.getObject(SportOrder.class, orderId);
		return useElecCard(order, card, memberid);
	}
	private ErrorCode<SportOrderContainer> useElecCard(SportOrder order, ElecCard card, Long memberid){
		if(!order.isNew()) return ErrorCode.getFailure("����״̬����" + order.getStatusText() + "����");
		if(!card.getEbatch().getTag().equals("sport")) return ErrorCode.getFailure("��ȯ�������˶����ʹ�ã�");
		ErrorCode validCode = paymentService.validUse(order);
		if(!validCode.isSuccess()) return validCode;
		OpenTimeTable table = baseDao.getObject(OpenTimeTable.class, order.getOttid());
		if(!StringUtils.contains(table.getElecard(), card.getCardtype())){
			return ErrorCode.getFailure("�˶һ�ȯ�����ڱ�����ʹ��");
		}
		if(order.hasMemberCardPay()){
			return ErrorCode.getFailure("��Ա��֧����֧��ȯ��");
		}
		Long batchid = card.getEbatch().getId();
		boolean isSupportCard = new PayValidHelper(VmUtils.readJsonToMap(table.getOtherinfo())).supportCard(batchid);
		if(!isSupportCard) return ErrorCode.getFailure("�ó��β�֧�ָ�ȯ��ʹ�ã�");
		
		ErrorCode<Discount> code = getDiscount(order, table, card, memberid);
		if(!code.isSuccess()) return ErrorCode.getFailure(code.getMsg());
		Discount discount = code.getRetval();
		baseDao.saveObject(discount);
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		GewaOrderHelper.useDiscount(order, discountList, discount);
		baseDao.saveObject(order);
		SportOrderContainer soc = new SportOrderContainer(order);
		soc.setCurUsedDiscount(discount);
		return ErrorCode.getSuccessReturn(soc);
	}
	
	
	@Override
	public List<OpenTimeItem> getMyOtiList(Long orderid) {
		String qry = "from OpenTimeItem o where o.id in(select st.otiid from SportOrder2TimeItem st where st.orderid=?)";
		List<OpenTimeItem> otiList = hibernateTemplate.find(qry, orderid);
		return otiList;
	}
    @Override
	public String getMyOtiHour(Long orderid) {
		String qry = "select min(o.hour) from OpenTimeItem o where o.id in(select st.otiid from SportOrder2TimeItem st where st.orderid=?)";
		List<String> otiList = hibernateTemplate.find(qry, orderid);
		return otiList.get(0);
	}
	
	private void useSellDeposit(SportOrder order, OpenTimeSale ots, SellDeposit deposit, Timestamp cur) throws OrderException{
		if(deposit == null) throw new OrderException(ApiConstant.CODE_SIGN_ERROR, "���۱�֤�����");
		if(deposit.hasStatus(SellDeposit.STATUS_PAID_USE)) return;
		MemberAccount account = baseDao.getObjectByUkey(MemberAccount.class, "memberid", ots.getMemberid());
		Charge charge = baseDao.getObject(Charge.class, deposit.getChargeid());
		Adjustment adjustment = new Adjustment(account.getId(), deposit.getMemberid(), ots.getNickname(), Adjustment.CORRECT_DEPOSIT);
		adjustment.setContent("��֤��۳�");
		adjustment.setAmount(deposit.getPrice());
		adjustment.setDepositcharge(deposit.getPrice());
		adjustment.setContent(charge.getTradeNo()+"��֤��۳�");
		adjustment.setTradeno(charge.getTradeNo());
		adjustment.setUpdatetime(cur);
		adjustment.setClerkid(0L);
		ErrorCode code = paymentService.approveAdjustment(adjustment, account, adjustment.getClerkid());
		if(!code.isSuccess()){
			throw new OrderException(code.getErrcode(), code.getMsg());
		}
		deposit.setStatus(SellDeposit.STATUS_PAID_USE);
		baseDao.saveObject(deposit);
		dbLogger.warn("SellDeposit id:" + deposit.getId() + "," + SellDeposit.STATUS_PAID_SUCCESS + "==>" +SellDeposit.STATUS_PAID_USE);
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		Discount discount = new Discount(order.getId(), PayConstant.DISCOUNT_TAG_DEPOSIT, order.getMemberid(), PayConstant.CARDTYPE_DEPOSIT);
		discount.setDescription("��֤�����" + deposit.getPrice() + "Ԫ");
		discount.setAmount(deposit.getPrice());
		discount.setGoodsid(deposit.getId());
		baseDao.saveObject(discount);
		GewaOrderHelper.useDiscount(order, discountList, discount);
		baseDao.saveObject(order);
	}
	
	@Override
	public ErrorCode<OrderContainer> useSpecialDiscount(Long orderId, SpecialDiscount sd, OrderCallback callback){
		SportOrder order = baseDao.getObject(SportOrder.class, orderId);
		if(sd == null) return ErrorCode.getFailure("���������");
		if(!order.sureOutPartner()){
			if(StringUtils.equals(sd.getBindmobile(), Status.Y)){
				Member member = baseDao.getObject(Member.class, order.getMemberid());
				if(!member.isBindMobile()){
					return ErrorCode.getFailure("�û������ֻ�����ʹ�ã�");
				}
				
				ErrorCode<String> scalper = this.scalperService.checkScalperLimited(member.getId(), member.getMobile(), sd.getId());
				if(!scalper.isSuccess()){
					dbLogger.error("orderId:" + orderId +  " memberID:" + member.getId() + " mobile:" + member.getMobile() + scalper.getMsg());
					return ErrorCode.getFailure("ϵͳ��æ��������!");
				}
			}
		}
		OpenTimeTable ott = baseDao.getObject(OpenTimeTable.class, order.getOttid());
		List<OpenTimeItem> otiList = getMyOtiList(order.getId());
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		Spcounter spcounter = paymentService.getSpdiscountCounter(sd);
		ErrorCode<Discount> discount = getSpdiscount(spcounter, order, otiList, discountList, ott, sd);
		if(discount.isSuccess()){
			paymentService.updateSpdiscountAddCount(sd, spcounter, order);
			baseDao.saveObject(discount.getRetval());
			GewaOrderHelper.useDiscount(order, discountList, discount.getRetval());
			if(StringUtils.isNotBlank(sd.getPaymethod())){
				String[] payList = StringUtils.split(sd.getPaymethod(), ",");
				String[] pay = StringUtils.split(payList[0], ":");
				order.setPaymethod(pay[0]);
				if(pay.length >1) order.setPaybank(pay[1]);
			}
			if(callback != null) callback.processOrder(sd, order);
			baseDao.saveObject(order);
			OrderContainer container = new SportOrderContainer(order);
			container.setDiscountList(discountList);
			container.setCurUsedDiscount(discount.getRetval());
			container.setSpdiscount(sd);
			return ErrorCode.getSuccessReturn(container);
		}
		return ErrorCode.getFailure(discount.getMsg());
	}
	private ErrorCode<Discount> getSpdiscount(Spcounter spcounter, SportOrder order, List<OpenTimeItem> otiList, List<Discount> discountList, OpenTimeTable ott, SpecialDiscount sd) {
		SpecialDiscountHelper sdh = new SportSpecialDiscountHelper(order, ott, discountList, otiList);
		List<String> limitPayList = paymentService.getLimitPayList();
		PayValidHelper pvh = new PayValidHelper(VmUtils.readJsonToMap(ott.getOtherinfo()));
		pvh.setLimitPay(limitPayList);
		ErrorCode<Integer> result = paymentService.getSpdiscountAmount(sdh, order, sd, spcounter, pvh);
		if(!result.isSuccess()) return ErrorCode.getFailure(result.getMsg());
		Discount discount = new Discount(order.getId(), PayConstant.DISCOUNT_TAG_PARTNER, sd.getId(), PayConstant.CARDTYPE_PARTNER);
		discount.setAmount(result.getRetval());
		discount.setDescription(sd.getDescription());
		return ErrorCode.getSuccessReturn(discount);
	}

	@Override
	public Integer getSportOpenTimeTableCount(Long sportid) {
		Integer week=DateUtil.getWeek(new Date());
		DetachedCriteria query = DetachedCriteria.forClass(OpenTimeTable.class,"s");
		query.add(Restrictions.eq("s.status",Status.Y));
		query.add(Restrictions.eq("s.rstatus",Status.Y));
		query.add(Restrictions.ge("s.playdate", DateUtil.getBeginTimestamp(DateUtil.addDay(new Date(), -(week-1)))));
		query.add(Restrictions.le("s.playdate", DateUtil.getBeginTimestamp(DateUtil.addDay(new Date(), 7-week))));
		query.add(Restrictions.eq("s.sportid", sportid));
		query.setProjection(Projections.rowCount());
		List result= hibernateTemplate.findByCriteria(query);
		if(result.isEmpty()) return 0;
		return Integer.parseInt("" + result.get(0));
	}
	
	@Override
	public Integer getOpenTimeItemCount(Long ottid, String status, String hour){
		DetachedCriteria query = DetachedCriteria.forClass(OpenTimeItem.class, "o");
		DetachedCriteria subquery = DetachedCriteria.forClass(SportField.class, "s");
		subquery.add(Restrictions.eqProperty("o.fieldid", "s.id"));
		subquery.add(Restrictions.eq("s.status", Status.Y));
		subquery.setProjection(Projections.property("s.id"));
		query.add(Subqueries.exists(subquery));
		query.add(Restrictions.eq("o.ottid", ottid));
		query.add(Restrictions.eq("o.status", status));
		query.add(Restrictions.gt("o.hour", hour));
		query.setProjection(Projections.rowCount());
		List result= hibernateTemplate.findByCriteria(query);
		if(result.isEmpty()) return 0;
		return Integer.parseInt("" + result.get(0));
	}
	
	private ErrorCode validateOpenTimeItem(OpenTimeTable ott, List<OpenTimeItem> otiList, Long cardid){
		Map<String, List<OpenTimeItem>> bindOtiMap = BeanUtil.groupBeanList(otiList, "bindInd");
		if(!bindOtiMap.isEmpty()){
			List<OpenTimeItem> otiAllList = openTimeTableService.getOpenItemList(ott.getId());
			Map<String, List<OpenTimeItem>> bindOtiAllMap = BeanUtil.groupBeanList(otiAllList, "bindInd");
			for (String bindKey : bindOtiMap.keySet()) {
				if(!bindKey.equals("0")){
					List<OpenTimeItem> boaList = bindOtiAllMap.get(bindKey);
					if(boaList == null || boaList.isEmpty()) return ErrorCode.getFailure("��������������ѡ�������أ�");
					if(!otiList.containsAll(boaList)) return ErrorCode.getFailure("����Ԥ��ͬһ���ص�"+boaList.size()+"��������ʱ��Σ�");
				}
			}
		}
		if(cardid==null){
			for(OpenTimeItem oti : otiList){
				if(oti.needMemberCardPay()){
					return ErrorCode.getFailure(oti.getHour()+"��Ҫ��Ա��֧����");
				}
			}
		}
		return ErrorCode.SUCCESS;
	}
	@Override
	public ErrorCode<String> delOtt(Long ottid){
		String qry = "select count(*) from SportOrder s where s.ottid=?";
		List result = hibernateTemplate.find(qry, ottid);
		int count = Integer.valueOf(result.get(0)+"");
		if(count>0) return ErrorCode.getFailure("�ó����Ѿ��ж�������ɾ��");
		OpenTimeTable ott = baseDao.getObject(OpenTimeTable.class, ottid);
		List<OpenTimeItem> otiList = openTimeTableService.getOpenItemList(ott.getId());
		baseDao.removeObject(ott);
		baseDao.removeObjectList(otiList);
		return ErrorCode.SUCCESS;
	}
	
	@Override
	public ErrorCode<SportOrder> processLastOrder(Long memberid, String ukey) {
		SportOrder lastOrder = getLastSportOrder(memberid, ukey);
		if(lastOrder==null) return ErrorCode.SUCCESS;
		if(lastOrder.getStatus().startsWith(OrderConstant.STATUS_PAID_FAILURE)){
			return ErrorCode.getFailure("������һ�������ȴ�����������Ϊ" + lastOrder.getTradeNo() + "�����Ժ������¶�����");
		}
		return ErrorCode.getSuccessReturn(lastOrder);
	}
	private SportOrder getLastSportOrder(Long memberid, String ukey){
		LastOperation last = baseDao.getObject(LastOperation.class, "S" + memberid + StringUtil.md5(ukey));
		if(last==null) return null;
		SportOrder order = baseDao.getObjectByUkey(SportOrder.class, "tradeNo", last.getLastvalue());
		return order;
	}
	
	private BuyItem newBuyItem(OpenTimeTable ott, OpenTimeItem oti, final int quantity){
		BuyItem item = new BuyItem(quantity);
		Timestamp playtime = ott.getPlayTimeByHour(oti.getHour());
		String goodsname = oti.getFieldname() + oti.getPrice() + "Ԫ [" + oti.getHour() + "-" + oti.getEndhour() +"]";
		item.setGoodsname(goodsname);
		item.setPlacetype(TagConstant.TAG_SPORT);
		item.setPlaceid(oti.getSportid());
		item.setItemid(oti.getItemid());
		item.setItemtype("sportitem");
		item.setCostprice(oti.getCostprice());
		item.setOriprice(oti.getNorprice());
		item.setUnitprice(oti.getPrice());
		item.setQuantity(quantity);
		item.setPlaytime(playtime);
		item.setRemark(oti.getRemark());
		item.setCitycode(oti.getCitycode());
		item.setRelatedid(oti.getOttid());
		item.setTag(oti.getOpenType());
		int totalcost = 0,	totalfee = 0;
		totalcost = oti.getCostprice() * quantity;
		totalfee = oti.getPrice() * quantity;
		item.setSettleid(oti.getSettleid());
		item.setTotalfee(totalfee);
		item.setTotalcost(totalcost);
		String checkpass = nextRandomNum(DateUtil.addDay(playtime, 1), 8, "0");
		item.setCheckpass(checkpass);
		Map<String, String> otherInfoMap = JsonUtils.readJsonToMap(item.getOtherinfo());
		otherInfoMap.put("upsetprice", oti.getUpsetprice()+"");
		item.setOtherinfo(JsonUtils.writeMapToJson(otherInfoMap));
		Map<String, String> descMap = new HashMap<String, String>();
		descMap.put("ʱ��", DateUtil.format(playtime,"yyyy-MM-dd HH:mm"));
		descMap.put("����", oti.getFieldname());
		descMap.put("otiid", oti.getId()+"");
		item.setDescription(JsonUtils.writeMapToJson(descMap));
		return item;
	}
}
