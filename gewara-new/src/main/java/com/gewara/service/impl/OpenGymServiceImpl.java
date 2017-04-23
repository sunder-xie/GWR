package com.gewara.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gewara.command.SearchOrderCommand;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.PayConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.Status;
import com.gewara.constant.order.GymCardItemConstant;
import com.gewara.constant.sys.ConfigConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.helper.discount.GymSpecialDiscountHelper;
import com.gewara.helper.discount.SpecialDiscountHelper;
import com.gewara.helper.discount.SportSpecialDiscountHelper.OrderCallback;
import com.gewara.helper.order.GewaOrderHelper;
import com.gewara.helper.order.GymOrderContainer;
import com.gewara.helper.order.GymOrderHelper;
import com.gewara.helper.order.OrderContainer;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.pay.ElecCardBatch;
import com.gewara.model.pay.GymOrder;
import com.gewara.model.pay.Spcounter;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.pay.PayUtil;
import com.gewara.pay.PayValidHelper;
import com.gewara.service.OpenGymService;
import com.gewara.service.OrderException;
import com.gewara.service.gewapay.ScalperService;
import com.gewara.service.order.impl.GewaOrderServiceImpl;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.gym.SynchGymService;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.ValidateUtil;
import com.gewara.util.VmUtils;
import com.gewara.xmlbind.gym.CardItem;
import com.gewara.xmlbind.gym.RemoteGym;
import com.gewara.xmlbind.gym.RemoteSpecialCourse;

@Service("openGymService")
public class OpenGymServiceImpl extends GewaOrderServiceImpl implements OpenGymService {
	@Autowired@Qualifier("synchGymService")
	private SynchGymService synchGymService;
	@Autowired@Qualifier("scalperService")
	private ScalperService scalperService;
	@Override
	public ErrorCode<GymOrder> addGymOrder(CardItem cardItem, Integer quantity, String mobile, String speciallist, Date startdate, Member member, String origin){
		if(cardItem == null) return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "������");
		if(!ValidateUtil.isMobile(mobile)) return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "�ֻ����д���");
		if(startdate == null) return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "�������ڲ���Ϊ�գ�");
		if(quantity == null || quantity<1) return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "��ѡ�񳡽���������");
		if(quantity >5) return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "ÿ�����ѡ��5�Ž�����");
		Date curDate = DateUtil.getCurDate();
		if(StringUtils.equals(cardItem.getItemType(), GymCardItemConstant.CARDTYPE_TIMES)){
			if(DateUtil.getDiffDay(curDate, startdate) != 0){
				 return ErrorCode.getFailure("�������ڴ���");
			}
		}else{
			Date enablDate = DateUtil.addDay(curDate, cardItem.getEnableDay());
			if(enablDate.before(startdate)) return ErrorCode.getFailure("�������ڴ���");
		}
		List<Long> specialIdList = BeanUtil.getIdList(speciallist, ",");
		if(specialIdList.isEmpty()) return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "�γ̲���Ϊ�գ�");
		Long memberid= member.getId();
		String ukey = memberid+"";
		String membername = member.getNickname();
		GymOrder lastPaidFailure = getLastPaidFailureOrder(memberid, String.valueOf(memberid));
		if(lastPaidFailure != null) {
			return ErrorCode.getFailure("������һ�������ȴ�����������Ϊ" + lastPaidFailure.getTradeNo() + "�����Ժ������¶�����");
		}
		GymOrder lastUnpaidOrder = getLastUnpaidGymOrder(memberid, ""+ukey, null); 
		if(lastUnpaidOrder != null) {
			cancelGymOrder(lastUnpaidOrder, member.getId(), OrderConstant.STATUS_REPEAT, "���¶���");
		}

		Timestamp t = DateUtil.getCurFullTimestamp();
		String checkpass = nextRandomNum(t, 8, "0");
		GymOrder order = GymOrderHelper.createGymOrder(memberid, membername, cardItem, ukey);
		order.setCostprice(cardItem.getCostprice());
		order.setQuantity(quantity);
		order.setUnitprice(cardItem.getPrice());
		int totalfee = order.getUnitprice()*order.getQuantity();
		int totalcost = order.getCostprice()*order.getQuantity();
		order.setTotalfee(totalfee);
		order.setTotalcost(totalcost);
		order.setMobile(mobile);		
		order.setCheckpass(checkpass);
		order.setTradeNo(PayUtil.getGymTradeNo());
		if(order.getTotalfee()<=0) return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "�۸��д���");
		Map<String, String> otherInfoMap = new HashMap<String, String>();
		otherInfoMap.put(GymOrder.KEY_REMOTE_GYM, cardItem.getGymid()+"");
		otherInfoMap.put(GymOrder.KEY_SPECAILID, StringUtils.join(specialIdList,","));
		otherInfoMap.put(GymOrder.KEY_STARTDATE, DateUtil.format(startdate, "yyyy-MM-dd"));
		Date enddate = DateUtil.addDay(startdate, cardItem.getValidDay());
		otherInfoMap.put(GymOrder.KEY_ENDDATE, DateUtil.format(enddate, "yyyy-MM-dd"));
		order.setOtherinfo(JsonUtils.writeObjectToJson(otherInfoMap));
		setOrderDescription(order, cardItem);
		baseDao.saveObject(order);
		if(StringUtils.isNotBlank(origin)){
			addOrderOrigin(order, origin);
		}
		return ErrorCode.getSuccessReturn(order);
	}
	
	private void setOrderDescription(GymOrder order, CardItem gymCardItem){
		RemoteGym gym = gymCardItem.getGym();
		String ordertitle = gym.getRealBriefname() + "����";
		order.setOrdertitle(ordertitle);
		Map<String, String>	otherInfoMap = JsonUtils.readJsonToMap(order.getOtherinfo());
		Map<String, String> descMap = JsonUtils.readJsonToMap(order.getDescription2());
		List<RemoteSpecialCourse> specialList = gymCardItem.getSpecialCourseList();
		List<String> speciannameList = BeanUtil.getBeanPropertyList(specialList, String.class, "name", true);
		descMap.put("������", gym.getName());
		descMap.put("��ϸ", gymCardItem.getName());
		descMap.put("�����γ�", StringUtils.join(speciannameList, " "));
		descMap.put("��Ч��", otherInfoMap.get(GymOrder.KEY_STARTDATE) + " �� " + otherInfoMap.get(GymOrder.KEY_ENDDATE));
		descMap.put("��Ч������", String.valueOf(gymCardItem.getValidDay()));
		descMap.put("������", gymCardItem.getCardTypeText());
		order.setDescription2(JsonUtils.writeObjectToJson(descMap));
	}
	
	@Override
	public GymOrder getLastPaidFailureOrder(Long memberid, String ukey) {
		DetachedCriteria query = DetachedCriteria.forClass(GymOrder.class);
		query.add(Restrictions.eq("memberid", memberid));
		query.add(Restrictions.eq("ukey", ukey));
		query.add(Restrictions.like("status", OrderConstant.STATUS_PAID_FAILURE, MatchMode.START));
		query.add(Restrictions.gt("addtime", DateUtil.addHour(new Timestamp(System.currentTimeMillis()), -12)));
		query.addOrder(Order.desc("addtime"));
		List<GymOrder> result = hibernateTemplate.findByCriteria(query);
		if(result.isEmpty()) return null;
		return result.get(0);
	}
	
	@Override
	public GymOrder getLastUnpaidGymOrder(Long memberid, String ukey, Long gci) {
		DetachedCriteria query = DetachedCriteria.forClass(GymOrder.class);
		query.add(Restrictions.eq("memberid", memberid));
		if(gci != null )query.add(Restrictions.eq("gci", gci));
		query.add(Restrictions.eq("ukey", ukey));
		query.add(Restrictions.like("status", OrderConstant.STATUS_NEW, MatchMode.START));
		query.add(Restrictions.gt("validtime", new Timestamp(System.currentTimeMillis())));
		query.addOrder(Order.desc("addtime"));
		List<GymOrder> result = hibernateTemplate.findByCriteria(query, 0, 1);
		if(result.isEmpty()) return null;
		return result.get(0);
	}
	
	@Override
	public ErrorCode<GymOrder> checkOrderCard(GymOrder order){
		Map<String, String> otherInfoMap = JsonUtils.readJsonToMap(order.getOtherinfo());
		if(!otherInfoMap.containsKey("realname") || !otherInfoMap.containsKey("IDcard")) return ErrorCode.getFailure("�뷵����һ������д���������֤�ţ�");
		return ErrorCode.getSuccessReturn(order);
	}
	
	@Override
	public OrderContainer processOrderPay(GymOrder order) throws OrderException{
		return processOrderPayInternal(order);
	}
	@Override
	public ErrorCode processGymOrder(GymOrder order){
		if(order.isPaidUnfix()){
			Timestamp cur = new Timestamp(System.currentTimeMillis());
			order.setUpdatetime(cur);
			order.setModifytime(cur);
			order.setValidtime(DateUtil.addDay(cur, 180));
			//���¶���
			order.setStatus(OrderConstant.STATUS_PAID_SUCCESS);
			baseDao.saveObject(order);
			processOrderExtra(order);
			return ErrorCode.SUCCESS;
		}else{
			return ErrorCode.getFailure("����״̬����ȷ��");
		}
	}
	
	@Override
	public ErrorCode cancelGymOrder(String tradeNo, Long memberid, String status, String reason) {
		GymOrder order = baseDao.getObjectByUkey(GymOrder.class, "tradeNo", tradeNo, false);
		return cancelGymOrder(order, memberid, status,  reason);
	}
	private ErrorCode cancelGymOrder(GymOrder order, Long memberid, String status, String reason) {
		if(order == null) return ErrorCode.getFailure("��Ч������");
		if(!StringUtils.startsWith(status, OrderConstant.STATUS_CANCEL)) return ErrorCode.getFailure("״̬����");
		if(StringUtils.startsWith(order.getStatus(), OrderConstant.STATUS_CANCEL)) return ErrorCode.getFailure("�ö�����ȡ����");
		if(StringUtils.startsWith(order.getStatus(), OrderConstant.STATUS_PAID)) return ErrorCode.getFailure("֧���ɹ���������ȡ����");
		if(order.isNew() && order.getMemberid().equals(memberid)){
			Timestamp validtime = new Timestamp(System.currentTimeMillis()-1000);
			order.setStatus(status);
			order.setValidtime(validtime);
			baseDao.saveObject(order);
			dbLogger.warn("ȡ��δ֧��������" + order.getTradeNo() + "," + reason);
		}
		return ErrorCode.SUCCESS;
	}
	
	@Override
	public ErrorCode usePoint(Long orderId, CardItem item, Long memberId, int usePoint){
		ErrorCode<String> pcode = pointService.validUsePoint(memberId);
		if(!pcode.isSuccess()) return ErrorCode.getFailure(pcode.getMsg());
		GymOrder order = baseDao.getObject(GymOrder.class, orderId);
		ErrorCode code = paymentService.validUse(order);
		if(!code.isSuccess()) return code;
		MemberInfo info = baseDao.getObject(MemberInfo.class, memberId);
		if(info.getPointvalue() < usePoint) return ErrorCode.getFailure("���Ļ��ֲ�����");

		if(item.getMaxpoint() < usePoint) return ErrorCode.getFailure("��ʹ�õĻ��ֳ�������" + item.getMaxpoint());
		int amount = usePoint/ConfigConstant.POINT_RATIO;
		usePoint = amount * ConfigConstant.POINT_RATIO;
		if(usePoint < item.getMinpoint() || amount == 0){
			return ErrorCode.getFailure("��ʹ�õĻ�����������" + item.getMinpoint());
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
	public ErrorCode<GymOrder> useElecCard(Long orderId, CardItem item, ElecCard card, Long memberid){
		GymOrder order = baseDao.getObject(GymOrder.class, orderId);
		if(order == null) return ErrorCode.getFailure("�ö����Ų����ڣ�");
		return useElecCard(order, item, card, memberid);
	}
	private ErrorCode<GymOrder> useElecCard(GymOrder order, CardItem item, ElecCard card, Long memberid){
		if(!order.isNew()) return ErrorCode.getFailure("����״̬����" + order.getStatusText() + "����");
		if(!card.getEbatch().getTag().equals(PayConstant.APPLY_TAG_GYM)) return ErrorCode.getFailure("��ȯ�����ڽ�����ʹ�ã�");
		ErrorCode validCode = paymentService.validUse(order);
		if(!validCode.isSuccess()) return validCode;
		if(!StringUtils.contains(item.getElecard(), card.getCardtype())){
			return ErrorCode.getFailure("�˶һ�ȯ�����ڱ�����ʹ��");
		}
		
		Long batchid = card.getEbatch().getId();
		boolean isSupportCard = new PayValidHelper(VmUtils.readJsonToMap(item.getOtherinfo())).supportCard(batchid);
		if(!isSupportCard) return ErrorCode.getFailure("�ý�����֧�ָ�ȯ��ʹ�ã�");
		if(card.needActivation()) return ErrorCode.getFailure("��ȯ���輤�����ʹ�ã�");
		
		ErrorCode<Discount> code = getDiscount(order, item, card, memberid);
		if(!code.isSuccess()) return ErrorCode.getFailure(code.getMsg());
		Discount discount = code.getRetval();
		baseDao.saveObject(discount);
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		GewaOrderHelper.useDiscount(order, discountList, discount);
		baseDao.saveObject(order);
		return ErrorCode.getSuccessReturn(order);
	}
	private ErrorCode<Discount> getDiscount(GymOrder order, CardItem item, ElecCard card, Long memberid){
		//1���жϿ��Ƿ���Ч
		if(!card.available()) return ErrorCode.getFailure("�˶һ�ȯ�Ѿ������ʧЧ��");
		if(!card.validTag(PayConstant.APPLY_TAG_GYM)) return ErrorCode.getFailure("��ȯ�����ڽ�����ʹ��");
		if(order.sureOutPartner()){//��Gewa�̼�
			if(memberid!=null && !memberid.equals(card.getPossessor()))
				return ErrorCode.getFailure("�����ñ��˵Ķһ�ȯ��");
			if(card.getPossessor() != null) return ErrorCode.getFailure("�˿������¼��ʹ��");
		}else/*Gewa*/ if(card.getPossessor()!=null && !card.getPossessor().equals(memberid)){
			return ErrorCode.getFailure("�����ñ��˵Ķһ�ȯ��");
		}
		String validpartner = card.getEbatch().getValidpartner();
		if(StringUtils.isNotBlank(validpartner)){
			if(!VmUtils.contains(validpartner.split(","), order.getPartnerid()+"")) {
				return ErrorCode.getFailure("�˶һ�ȯ�������ڸö�����");
			}
		}
		
		ElecCardBatch batch = card.getEbatch();
		String bindpay = batch.getBindpay();
		if(StringUtils.isNotBlank(batch.getCosttype())){
			if(order.sureOutPartner()) return ErrorCode.getFailure("��ȯ�����ڸ�������վ���ֻ���ʹ�ã�");
			bindpay = PaymethodConstant.PAYMETHOD_ELECARDPAY + ":" + batch.getId(); //�ı�ɱ��۵ıع̶�֧����ʽ��
		}
		Map<String, String> otherinfoMap = VmUtils.readJsonToMap(order.getOtherinfo());
		
		if(StringUtils.isNotBlank(card.getValidcinema())){
			List<Long> cidList = BeanUtil.getIdList(card.getValidcinema(), ",");
			if(!cidList.contains(order.getGymid())){
				return ErrorCode.getFailure("�˶һ�ȯ�����ڴ˳���ʹ�ã�");
			}
		}
		if(!card.isUseCurTime()){//ʱ�������
			String opentime = card.getEbatch().getAddtime1();
			String closetime = card.getEbatch().getAddtime2();
			return ErrorCode.getFailure("�˶һ�ȯֻ����" + opentime + "��" +  closetime + "ʱ����ʹ�ã�");
		}
		if(!card.isCanUseCity(order.getCitycode())) return ErrorCode.getFailure("��ȯ��֧���ڸó���ʹ�ã�");
		if(StringUtils.isNotBlank(card.getValiditem())){
			List<Long> cidList = BeanUtil.getIdList(card.getValiditem(), ",");
			if(!cidList.contains(order.getGci())){
				return ErrorCode.getFailure("����������ʹ�ô˶һ�ȯ��");
			}
		}
		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		if(StringUtils.isNotBlank(bindpay) ){//��֧��
			for(Discount discount: discountList){
				ElecCard c = baseDao.getObject(ElecCard.class, discount.getRelatedid());
				if(c==null || !c.getEbatch().equals(batch)) return ErrorCode.getFailure("ֻ��ʹ��ͬһ���ε�ȯ��");
			}
		}else if(StringUtils.isNotBlank(otherinfoMap.get(PayConstant.KEY_CARDBINDPAY))){
			return ErrorCode.getFailure("�����󶨵�֧����ʽ��ȯ�󶨵�֧����ʽ�����ݣ�");
		}
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
		if(card.getEbatch().getCardtype().equals(PayConstant.CARDTYPE_C) ||
			card.getEbatch().getCardtype().equals(PayConstant.CARDTYPE_D)){
			amount = card.getEbatch().getAmount();
			description = card.getCardno() + "����" + amount + "Ԫ";
		}else if(card.getEbatch().getCardtype().equals(PayConstant.CARDTYPE_A) ||
				card.getEbatch().getCardtype().equals(PayConstant.CARDTYPE_B)){
			Integer maxAmount = card.getEbatch().getAmount();
			amount = item.getPrice();
			description = card.getCardno() + "����" + amount + "Ԫ";
			if(maxAmount != null && amount > maxAmount){
				if(card.getEbatch().getCardtype().equals(PayConstant.CARDTYPE_B)){
					amount = maxAmount;
					description = card.getCardno() + "����" + amount + "Ԫ";
				}else return ErrorCode.getFailure("�һ������ļ۸����ȯ��ʹ���޶���ܶһ���");
			}
			goodsid = order.getOci();
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
	public ErrorCode<OrderContainer> useSpecialDiscount(Long orderId, SpecialDiscount sd, OrderCallback callback){
		GymOrder order = baseDao.getObject(GymOrder.class, orderId);
		if(sd == null) return ErrorCode.getFailure("���������");
		if(!order.sureOutPartner()){
			if(StringUtils.equals(sd.getBindmobile(), Status.Y)){
				Member member = baseDao.getObject(Member.class, order.getMemberid());
				if(!member.isBindMobile()){
					return ErrorCode.getFailure("�û������ֻ�����ʹ�ã�");
				}
				
				ErrorCode<String> scalper = this.scalperService.checkScalperLimited(member.getId(), member.getMobile(), sd.getId());
				if(!scalper.isSuccess()){
					dbLogger.error("orderId:" + orderId + " memberID:" + member.getId() + " mobile:" + member.getMobile() + scalper.getMsg());
					return ErrorCode.getFailure("ϵͳ��æ��������!");
				}
			}
		}
		//FIXME:û�취��������������ĳɴ���CardItem
		ErrorCode<CardItem> code = synchGymService.getGymCardItem(order.getGci(), true);
		CardItem item = code.getRetval();

		List<Discount> discountList = paymentService.getOrderDiscountList(order);
		Spcounter spcounter = paymentService.getSpdiscountCounter(sd);
		ErrorCode<Discount> discount = getSpdiscount(spcounter, order, discountList, item, sd);
		if(discount.isSuccess()){
			//�����µ�������
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
			OrderContainer container = new GymOrderContainer();
			container.setOrder(order);
			container.setDiscountList(discountList);
			container.setCurUsedDiscount(discount.getRetval());
			container.setSpdiscount(sd);
			return ErrorCode.getSuccessReturn(container);
		}
		return ErrorCode.getFailure(discount.getMsg());
	}
	
	private ErrorCode<Discount> getSpdiscount(Spcounter spcounter, GymOrder order, List<Discount> discountList, CardItem item, SpecialDiscount sd) {
		PayValidHelper pvh = new PayValidHelper(VmUtils.readJsonToMap(item.getOtherinfo()));
		List<String> limitPayList = paymentService.getLimitPayList();
		pvh.setLimitPay(limitPayList);
		SpecialDiscountHelper sdh = new GymSpecialDiscountHelper(order, item, discountList);
		ErrorCode<Integer> result = paymentService.getSpdiscountAmount(sdh, order, sd, spcounter, pvh);
		if(!result.isSuccess()) return ErrorCode.getFailure(result.getMsg());
		Discount discount = new Discount(order.getId(), PayConstant.DISCOUNT_TAG_PARTNER, sd.getId(), PayConstant.CARDTYPE_PARTNER);
		discount.setAmount(result.getRetval());
		discount.setDescription(sd.getDescription());
		return ErrorCode.getSuccessReturn(discount);
	}
	
	@Override
	public List<GymOrder> getGymOrderList(SearchOrderCommand soc, int from, int maxnum){
		DetachedCriteria query = DetachedCriteria.forClass(GymOrder.class);
		if(StringUtils.isNotBlank(soc.getMobile())) query.add(Restrictions.like("mobile", soc.getMobile(), MatchMode.ANYWHERE));
		if(StringUtils.isNotBlank(soc.getTradeNo())) query.add(Restrictions.eq("tradeNo", soc.getTradeNo()));
		Timestamp curTime = DateUtil.getCurFullTimestamp();
		if(soc.getMinute()!=null){
			Timestamp fromtime = DateUtil.addMinute(curTime, -soc.getMinute());
			query.add(Restrictions.ge("addtime", fromtime));
		}
		if(StringUtils.isNotBlank(soc.getOrdertype())){
			if(soc.getOrdertype().equals(OrderConstant.STATUS_CANCEL)){
				query.add(Restrictions.or(Restrictions.like("status", soc.getOrdertype(), MatchMode.START),
						Restrictions.and(Restrictions.like("status", OrderConstant.STATUS_NEW, MatchMode.START), 
								Restrictions.lt("validtime", curTime))));
			}else{
				query.add(Restrictions.like("status", soc.getOrdertype(), MatchMode.START));
				if(StringUtils.startsWith(soc.getOrdertype(), OrderConstant.STATUS_NEW)){//�����й�ʱ�Զ�ȡ�����˵�
					query.add(Restrictions.ge("validtime", curTime));
				}
			}
		}
		if(soc.getMemberid() != null) query.add(Restrictions.eq("memberid", soc.getMemberid()));
		if(soc.getGymid() != null) query.add(Restrictions.eq("gymid", soc.getGymid()));
		if(soc.getOrderid()!=null) query.add(Restrictions.eq("id", soc.getOrderid()));
		query.addOrder(Order.desc("addtime"));
		List<GymOrder> orderList = hibernateTemplate.findByCriteria(query, from, maxnum);
		return orderList;
	}
}
