package com.gewara.service.ticket.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.SmsConstant;
import com.gewara.constant.Status;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.constant.ticket.SeatConstant;
import com.gewara.helper.ticket.SeatPriceHelper;
import com.gewara.model.acl.User;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.OpenSeat;
import com.gewara.model.ticket.SellSeat;
import com.gewara.service.OrderException;
import com.gewara.service.ticket.InsteadTicketOrderService;

@Service("insteadTicketOrderService")
public class InsteadTicketOrderServiceImpl extends TicketProcessServiceImpl implements InsteadTicketOrderService {
	
	@Override
	public TicketOrder addTicketOrder(OpenPlayItem opi, TicketOrder oldTicketOrder, List<Long> seatIdList, User user) throws OrderException{
		Set<Long> seatIdSet = new LinkedHashSet<Long>(seatIdList);
		List<OpenSeat> seatList = baseDao.getObjectList(OpenSeat.class, seatIdSet);
		return addTicketOrderInternal(opi, oldTicketOrder, seatList, user);
	}
	
	protected TicketOrder addTicketOrderInternal(OpenPlayItem opi, TicketOrder oldTicketOrder, List<OpenSeat> oseatList, User user) throws OrderException{
		if(oldTicketOrder == null){
			throw new OrderException(ApiConstant.CODE_SIGN_ERROR, "ԭ���������ڣ�");
		}
		if(!oldTicketOrder.isPaidSuccess()){
			throw new OrderException(ApiConstant.CODE_SIGN_ERROR, "�ǳɹ��������ܸ���������λ��");
		}
		if(opi == null){
			throw new OrderException(ApiConstant.CODE_SIGN_ERROR, "���δ������ݲ����ڣ�");
		}
		return this.changeSeat(opi, oseatList, oldTicketOrder, user);
	}
	
	/**
	 * �޸Ķ����ĵ��ۣ��ܼۣ����Ż�
	 * @param opi
	 * @param newseatList
	 * @param oldOrder
	 * @param user
	 * @return
	 * @throws OrderException
	 */
	private TicketOrder changeSeat(OpenPlayItem opi, List<OpenSeat> oseatList, TicketOrder oldOrder, User user) throws OrderException{
		validateOpiSeat(opi, oseatList);
		if(oseatList.size() != oldOrder.getQuantity()){
			throw new OrderException(ApiConstant.CODE_SIGN_ERROR, "��λ���붩��������ȣ�");
		}
		TicketOrder order = super.changeSeat(opi, oseatList, oldOrder);
		order.setClerkid(user.getId());
		int costprice = opi.getCostprice();
		if(order.getPartnerid() != null && PartnerConstant.isMacBuy(order.getPartnerid())){
			costprice = opi.getPrice()-opi.getFee();
		}
		//�����϶���������λ
		List<SellSeat> sellSeatList = ticketOrderService.getOrderSeatList(oldOrder.getId());
		Timestamp validtime = new Timestamp(System.currentTimeMillis()-1000);
		for (SellSeat sellSeat : sellSeatList) {
			if(sellSeat.getOrderid().equals(oldOrder.getId())){
				sellSeat.setStatus(SeatConstant.STATUS_SELLING);
				sellSeat.setValidtime(validtime);
				baseDao.saveObject(sellSeat);
			}
		}
		//ȡ�������źͷ��ʼ�
		List<SMSRecord> smsList = baseDao.getObjectListByField(SMSRecord.class, "tradeNo", order.getTradeNo());
		List<SMSRecord> delSmsList = new ArrayList<SMSRecord>();
		for (SMSRecord smsRecord : smsList) {
			smsRecord.setTradeNo(oldOrder.getTradeNo());
			if(!StringUtils.contains(smsRecord.getStatus(), Status.Y)){
				smsRecord.setStatus( SmsConstant.STATUS_D + smsRecord.getStatus());
				delSmsList.add(smsRecord);
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_ORDER_PAY, "����������ȡ������ID��"+smsRecord.getId()+"  �����ţ�"+smsRecord.getTradeNo());
			}
		}
		baseDao.saveObjectList(delSmsList);
		SeatPriceHelper sph = new SeatPriceHelper(opi, order.getPartnerid());
		int totalfee = 0;
		int maxprice = 0;
		for(OpenSeat oseat :oseatList){
			int price = sph.getPrice(oseat);
			if(price>maxprice) maxprice = price;
			totalfee = totalfee + price;
		}
		oldOrder.setStatus(OrderConstant.STATUS_SYS_CHANGE_CANCEL);
		order.addChangehis(OrderConstant.CHANGEHIS_KEY_SUCCESSCHANGE, "true");
		order.setUnitprice(maxprice);
		order.setTotalfee(totalfee);
		order.setCostprice(costprice);
		order.setTotalcost(order.getQuantity()*costprice);
		order.setStatus(OrderConstant.STATUS_PAID_FAILURE);
		ticketOrderService.computeChangeOrderFee(order);
		baseDao.saveObjectList(order, oldOrder);
		return order;
	}
	
	private void validateOpiSeat(OpenPlayItem opi, List<OpenSeat> seatList) throws OrderException{
		for (OpenSeat openSeat : seatList) {
			if(!opi.getMpid().equals(openSeat.getMpid())){
				throw new OrderException(ApiConstant.CODE_SIGN_ERROR, "���ܿ糡�ι������ݣ�");
			}
		}
	}
}
