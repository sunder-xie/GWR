package com.gewara.helper.discount;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.PayConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.model.pay.Cpcounter;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.GymOrder;
import com.gewara.model.pay.OtherFeeDetail;
import com.gewara.model.pay.Spcounter;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.pay.PayValidHelper;
import com.gewara.support.ErrorCode;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.xmlbind.gym.CardItem;

public class GymSpecialDiscountHelper extends SpecialDiscountHelper{
	private GymOrder order;
	private CardItem item;
	private List<Discount> discountList;
	public GymSpecialDiscountHelper(GymOrder order, CardItem item, List<Discount> discountList){
		this.order = order;
		this.item = item;
		this.discountList = discountList;
	}

	@Override
	public String getFullDisabledReason(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList) {
		return getFullDisabledReason(spcounter, cpcounterList, sd, item, order);
	}
	private static String getFullDisabledReason(Spcounter spcounter, List<Cpcounter> cpcounterList, SpecialDiscount sd, CardItem item, GymOrder order) {
		String reason = "";
		if(StringUtils.isNotBlank(sd.getPaymethod())){
			String[] pay = sd.getPaymethod().split(":");
			if(!StringUtils.equals(pay[0], order.getPaymethod())) reason += "֧����ʽ��֧�֣�";
			if(pay.length > 1 && !StringUtils.equals(pay[1], order.getPaybank())){
				reason += "֧�����ز�֧�֣�";
			}
		}
		if(order.getAddtime().before(sd.getTimefrom()) || order.getAddtime().after(sd.getTimeto()))
			reason += "���ʱ��Ϊ" + DateUtil.formatTimestamp(sd.getTimefrom()) + "��" + DateUtil.formatTimestamp(sd.getTimeto()) + "��";
		//ע�⣬spcounter����Ϊ��
		String rs = getSpcounterDisabledReason(spcounter, cpcounterList, order.getCitycode(), order.getPartnerid(), order.getQuantity());
		if(StringUtils.isNotBlank(rs)) reason += rs;
		if(order.getQuantity() > sd.getBuynum()|| order.getQuantity() < sd.getMinbuy()){
			if(sd.getBuynum() == sd.getMinbuy()) reason += "������ʶ����빺��" + sd.getBuynum() + "�ţ�";
			else reason += "������ʶ�ֻ�ܹ���" + sd.getMinbuy() + "��" + sd.getBuynum() + "�ţ�";
		}
		reason += getOttFullDisabledReason(sd, item, order.getAddtime());
		return reason;
	}
	@Override
	public String getOrderFirstDisabledReason(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList) {
		if(order.getAddtime().before(sd.getTimefrom()) || order.getAddtime().after(sd.getTimeto())){
			return "���ʱ��Ϊ" + DateUtil.formatTimestamp(sd.getTimefrom()) + "��" + DateUtil.formatTimestamp(sd.getTimeto()) + "��";
		}
		//ע�⣬spcounter����Ϊ��
		String rs = getSpcounterDisabledReason(spcounter, cpcounterList, order.getCitycode(), order.getPartnerid(), order.getQuantity());
		if(StringUtils.isNotBlank(rs)) return rs;
		if(order.getQuantity() > sd.getBuynum()|| order.getQuantity() < sd.getMinbuy()){
			if(sd.getBuynum() == sd.getMinbuy()) return "������ʶ����빺��" + sd.getBuynum() + "�ţ�";
			else return "������ʶ�ֻ�ܹ���" + sd.getMinbuy() + "��" + sd.getBuynum() + "�ţ�";
		}
		return getOttFirstDisabledReason(sd, item, order.getAddtime());
	}

	@Override
	public ErrorCode<Integer> getSpdiscountAmount(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList, PayValidHelper pvh) {
		if(sd==null) return ErrorCode.getFailure("���������");
		if(StringUtils.equals(order.getStatus(), OrderConstant.STATUS_NEW_CONFIRM)){
			return ErrorCode.getFailure("�Ѿ�ȷ�ϵĶ��������޸ģ�");
		}
		return validSpdiscountWithoutStatus(sd, spcounter, cpcounterList, pvh);
	}
	@Override
	public ErrorCode<Integer> validSpdiscountWithoutStatus(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList, PayValidHelper pvh){
		Map<String, String> otherFeeMap = JsonUtils.readJsonToMap(order.getOtherFeeRemark());
		String umpayfee = otherFeeMap.get(OtherFeeDetail.FEETYPE_U);
		if (StringUtils.isNotBlank(umpayfee) && Integer.parseInt(umpayfee)>0) {
			if (!pvh.supportPaymethod(order.getPaymethod()))
				ErrorCode.getFailure("�˻��֧����ѡ���֧����ʽ��");
			pvh = new PayValidHelper(order.getPaymethod());
		}
		if(discountList.size() > 0){
			return ErrorCode.getFailure("���ܺ������Żݷ�ʽ���ã�");
		}
		ErrorCode enable = isEnabled(sd, pvh);
		if (!enable.isSuccess()) {
			if(isShowMsg()) return ErrorCode.getFailure(enable.getMsg());
			return ErrorCode.getFailure("��������֧�ִ˻");
		}
		if(StringUtils.equals(sd.getOpentype(), SpecialDiscount.OPENTYPE_SPECIAL)){
			if(!StringUtils.contains(item.getSpflag(), sd.getFlag())){
				return ErrorCode.getFailure("��������֧�ִ˻��");
			}
		}else if (!StringUtils.contains(item.getElecard(), PayConstant.CARDTYPE_PARTNER)) {
			return ErrorCode.getFailure("��������֧�ִ˻��");
		}
		
		if(StringUtils.equals(sd.getOpentype(), SpecialDiscount.OPENTYPE_GEWA)){//�������
			if(order.surePartner()) return ErrorCode.getFailure("��������֧�ִ˻��");//��������WAP���̼�
		}else if(StringUtils.equals(sd.getOpentype(), SpecialDiscount.OPENTYPE_WAP)){//WAP�
			if(!order.sureGewaPartner()) return ErrorCode.getFailure("�����֧�ַ��ֻ��û�������"); 
			List<Long> ptnidList = BeanUtil.getIdList(sd.getPtnids(), ",");
			if(!ptnidList.contains(order.getPartnerid())){
				return ErrorCode.getFailure("�����֧�ָÿͻ��ˣ�");
			}
		}else if(StringUtils.equals(SpecialDiscount.OPENTYPE_PARTNER, sd.getOpentype())){//�̼һ
			if(!order.getPartnerid().equals(Long.valueOf(sd.getPtnids()))){
				return ErrorCode.getFailure("��������֧�ִ˻��");
			}
			//�̼Ҷ�����ֱ���ж�֧����ʽ�Ƿ���ȷ
			if(!sd.isValidPaymethod(order.getPaymethod(), order.getPaybank())){
				return ErrorCode.getFailure("֧����ʽ����ȷ�����ܲ���˻��");
			}
		}
		
		String disableReason = getOrderFirstDisabledReason(sd, spcounter, cpcounterList);
		if(StringUtils.isNotBlank(disableReason)) return ErrorCode.getFailure(disableReason);
		
		int amount = 0;
		if(StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_PERORDER)){
			amount = sd.getDiscount();
		}else if(StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_PERTICKET)){
			amount = sd.getDiscount() * order.getQuantity();
		}else if(StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_FIXPRICE)){
			amount = order.getTotalfee() - sd.getDiscount() * order.getQuantity();
		}else if(StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_PERCENT)){
			amount = order.getTotalfee() * sd.getDiscount() /100;
		}else if(StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_BUYONE_GIVEONE)){
			amount = order.getTotalfee()/2;

		}
		if(amount == 0 && sd.getRebates()==0) return ErrorCode.getFailure("�˶������Żݣ�");
		if(amount > order.getTotalfee()) amount = order.getTotalfee();
		return ErrorCode.getSuccessReturn(amount);
	}
	public static String getOttFirstDisabledReason(SpecialDiscount sd, CardItem item, Timestamp addtime) {
		if(!isEnabledByAddweek(sd, addtime)) return "���������" + sd.getAddweek() + "����";
		if(!isEnabledByRelatedid(sd, item.getGymid())) return "�����֧�ָó���ʹ�ã�";
		if(!isEnabledByItemid(sd, item.getId())) return "�����֧�ָÿ�ʹ�ã�";
		if(!isEnabledByAddtime(sd, addtime)){
			return "�������" + sd.getAddtime1().substring(0,2) + ":" + sd.getAddtime1().substring(2) +"��" + 
			sd.getAddtime2().substring(0,2) + ":" + sd.getAddtime2().substring(2) + "����";
		}
		return "";
	}
	private static String getOttFullDisabledReason(SpecialDiscount sd, CardItem item, Timestamp addtime) {
		String reason = "";
		if(!isEnabledByAddweek(sd, addtime)) reason += "���������" + sd.getAddweek() + "����";
		if(!isEnabledByRelatedid(sd, item.getGymid())) reason += "�����֧�ָó���ʹ�ã�";
		if(!isEnabledByItemid(sd, item.getId())) reason += "�����֧�ָÿ�ʹ�ã�";
		if(!isEnabledByAddtime(sd, addtime)){
			reason += "�������" + sd.getAddtime1().substring(0,2) + ":" + sd.getAddtime1().substring(2) +"��" + 
			sd.getAddtime2().substring(0,2) + ":" + sd.getAddtime2().substring(2) + "����";
		}
		return reason;
	}
	@Override
	public ErrorCode isEnabled(SpecialDiscount sd, PayValidHelper pvh){
		return isEnabled(sd, item, pvh);
	}

	public static ErrorCode isEnabled(SpecialDiscount sd, CardItem item, PayValidHelper pvh) {
		if (sd == null) return ErrorCode.getFailure("���������");
		if(!isEnabledByRelatedid(sd, item.getGymid()))	return ErrorCode.getFailure("���ݲ�֧�֣�");
		if(StringUtils.isNotBlank(sd.getPaymethod()) && 
				StringUtils.isNotBlank(item.getOtherinfo())){
			String[] pay = StringUtils.split(sd.getPaymethod());
			if(!pvh.supportPaymethod(pay[0])) return ErrorCode.getFailure("֧�����ƣ�");
		}
		if(!isEnabledByPrice(sd, item.getPrice())) return ErrorCode.getFailure("���۷�Χ��֧�֣�");
		if(!isEnabledByPricegap(sd, item.getPrice() - item.getCostprice())) return ErrorCode.getFailure("�ɱ���֧�֣�");
		if(!isEnabledByCostprice(sd, item.getCostprice())) return ErrorCode.getFailure("�ɱ���Χ��֧�֣�");
		return ErrorCode.SUCCESS;
	}
}
