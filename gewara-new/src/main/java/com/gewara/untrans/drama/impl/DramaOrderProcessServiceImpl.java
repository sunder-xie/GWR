package com.gewara.untrans.drama.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gewara.api.gpticket.vo.ticket.DramaRemoteOrderVo;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.ticket.OpiConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.drama.OpenDramaItem;
import com.gewara.model.drama.OpenTheatreSeat;
import com.gewara.model.drama.SellDramaSeat;
import com.gewara.model.drama.TheatreSeatArea;
import com.gewara.model.pay.BuyItem;
import com.gewara.service.OrderException;
import com.gewara.service.drama.DramaProcessService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.AbstractOrderProcessService;
import com.gewara.untrans.drama.DramaOrderProcessService;
import com.gewara.untrans.drama.RemoteDramaService;
import com.gewara.untrans.drama.TheatreOperationService;
import com.gewara.util.BeanUtil;
import com.gewara.util.StringUtil;

@Service("dramaOrderProcessService")
public class DramaOrderProcessServiceImpl extends AbstractOrderProcessService implements DramaOrderProcessService {
	
	@Autowired@Qualifier("remoteDramaService")
	private RemoteDramaService remoteDramaService;
	
	@Autowired@Qualifier("theatreOperationService")
	private TheatreOperationService theatreOperationService;
	
	@Autowired@Qualifier("dramaProcessService")
	private DramaProcessService dramaProcessService;
	
	@Override
	public ErrorCode reconfirmOrder(DramaOrder order, Long userid,	boolean isAuto, boolean reChange) {
		String processKey = PROCESS_ORDER + order.getId();
		boolean allow = operationService.updateOperation(processKey, PROCESS_INTERVAL);
		if (!allow)	return ErrorCode.getFailure("����(ϵͳ)���ڴ�����ȴ�1-2���ӣ�");
		OpenDramaItem odi = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", order.getDpid(), true);
		try {
			if (!odi.hasGewara()) {
				ErrorCode<DramaRemoteOrderVo> remoteOrder = remoteDramaService.checkOrder(order.getId(), false);
				if (!remoteOrder.isSuccess()) {
					return ErrorCode.getFailure(remoteOrder.getMsg());
				}
				if (remoteOrder.getRetval().hasStatus(OrderConstant.REMOTE_STATUS_FIXED)) {// �Ѿ��ɹ�
					if (order.isPaidFailure() || order.isPaidUnfix()) {
						ErrorCode result = processOrderInternal(order, "����ȷ��", null);
						if (result.isSuccess()) {
							dbLogger.warn(userid + "ת������״̬Ϊ���׳ɹ���"	+ order.getTradeNo());
							return ErrorCode.getSuccess("���ɹ�,ת���ɹ���");
						} else {
							return ErrorCode.getSuccess("���ɹ�,ת��ʧ�ܣ�" + result);
						}
					}
				}
			}
			if (!order.getStatus().equals(OrderConstant.STATUS_PAID_UNFIX)) {
				return ErrorCode.getFailure("ֻ�С���λ�����������ſ�����ȷ�ϣ�");
			}
			ErrorCode code = null;
			if(odi.isOpenseat()){
				List<SellDramaSeat> oldSeatList = dramaOrderService.getDramaOrderSeatList(order.getId());
				TheatreSeatArea seatArea = daoService.getObject(TheatreSeatArea.class, order.getAreaid());
				ErrorCode<List<String>> remoteLockList = theatreOperationService.updateRemoteLockSeat(seatArea, OpiConstant.SECONDS_ADDORDER, false);
				if(!remoteLockList.isSuccess()){
					return ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "����ʧ�ܣ����������쳣��");
				}
				List<OpenTheatreSeat> seatList = getOriginalSeat(order, oldSeatList);
				DramaOrder newOrder = dramaProcessService.changeSeat(odi, seatList, order, reChange, remoteLockList.getRetval());
				code = confirmSuccessInternal(newOrder, userid, isAuto);
			}else{
				ErrorCode remoteOrder = reconfirmPrice(odi, order, reChange);
				if(!remoteOrder.isSuccess()) return remoteOrder;
				code = confirmSuccessInternal(order, userid, isAuto);
			}
			return code;
			// FIXME:����Exception
		} catch (OrderException e){
			dbLogger.warn(StringUtil.getExceptionTrace(e, 10));
			return ErrorCode.getFailure(e.getMsg());
		} catch (Exception e) {
			dbLogger.warn("", e);
			return ErrorCode.getFailure(e.getClass() + " Exception: " + e.getMessage());
		} finally{
			operationService.resetOperation(processKey, PROCESS_INTERVAL);
		}
	}

	private ErrorCode reconfirmPrice(OpenDramaItem odi, DramaOrder order, boolean reChange) throws OrderException{
		if(!odi.isOpenprice()) throw new OrderException(ApiConstant.CODE_DATA_ERROR, "��ѡ�����Σ��޸���λ����");
		if(order.isSeatChanged() && !reChange) throw new OrderException(ApiConstant.CODE_DATA_ERROR, "���ܵڶ������¶�����");
		List<BuyItem> buyList = daoService.getObjectListByField(BuyItem.class, "orderid", order.getId());
		ErrorCode code = theatreOperationService.lockRemotePrice(odi, order, order.getMobile(), buyList);
		return code;
	}
	
	@Override
	public List<OpenTheatreSeat> getOriginalSeat(DramaOrder order, List<SellDramaSeat> seatList) throws OrderException{
		if(!order.isPaidUnfix()) throw new OrderException(ApiConstant.CODE_DATA_ERROR, "ֻ���޸���λ����������");
		OpenDramaItem odi = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", order.getDpid(), true);
		if(!odi.hasGewara()){
			ErrorCode<Boolean> result = theatreOperationService.unlockRemoteSeat(order.getId());
			if(result.isSuccess() && result.getRetval()){
				theatreOperationService.removeLockSeatFromQryItemResponse(order.getAreaid(), seatList);
			}
		}
		List<OpenTheatreSeat> oseatList = daoService.getObjectList(OpenTheatreSeat.class, BeanUtil.getBeanPropertyList(seatList, Long.class, "id", true));
		return oseatList;
	}
	
	@Override
	public ErrorCode confirmSuccess(DramaOrder order, Long userid, boolean isAuto) {
		String processKey = PROCESS_ORDER + order.getId();
		boolean allow = operationService.updateOperation(processKey, PROCESS_INTERVAL);
		if (!allow)	return ErrorCode.getFailure("����(ϵͳ)���ڴ�����ȴ�1-2���ӣ�");
		try{
			return confirmSuccessInternal(order, userid, isAuto);
		}finally{
			operationService.resetOperation(processKey, PROCESS_INTERVAL);
		}
	}
	
	private ErrorCode confirmSuccessInternal(DramaOrder order, Long userid, boolean isAuto) {
		try {
			DramaRemoteOrderVo remoteOrder = null;
			OpenDramaItem odi = daoService.getObjectByUkey(OpenDramaItem.class, "dpid", order.getDpid(), true);
			if (!odi.hasGewara()) {
				ErrorCode<DramaRemoteOrderVo> checkResult = remoteDramaService.checkOrder(order.getId(), true);
				if (!checkResult.isSuccess()) {
					return ErrorCode.getFailure(checkResult.getMsg());
				}
				remoteOrder = checkResult.getRetval();
			}
			if(order.isPaidUnfix()){//��λ������
				if (remoteOrder != null && !remoteOrder.hasStatus(OrderConstant.REMOTE_STATUS_FIXED) && order.needChangeSeat())
					return ErrorCode.getFailure("ֻ�л�����λ�Ĳ���ȷ�ϳɹ���");
			}
			String username = "�Զ�";
			if (!isAuto) {
				if (userid == null) return ErrorCode.getFailure("���ȵ�¼��");
				username = "" + userid;
			}
			if (!order.isPaidFailure() && !order.isPaidUnfix())
				return ErrorCode.getFailure("״̬�д���");
			ErrorCode result = processOrderInternal(order, "����ȷ��", null);
			if (result.isSuccess()) {
				dbLogger.warn(username + "ת������״̬Ϊ���׳ɹ���" + order.getTradeNo());
				return ErrorCode.SUCCESS;
			} else {
				return result;
			}
		} catch (Exception e) {
			dbLogger.warn("", e);
			return ErrorCode.getFailure(e.getClass() + " Exception: " + e.getMessage());
		}
	}
}
