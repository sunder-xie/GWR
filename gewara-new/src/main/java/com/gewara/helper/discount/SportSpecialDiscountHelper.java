package com.gewara.helper.discount;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;

import com.gewara.constant.PayConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.model.pay.Cpcounter;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.OtherFeeDetail;
import com.gewara.model.pay.Spcounter;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.sport.OpenTimeItem;
import com.gewara.model.sport.OpenTimeTable;
import com.gewara.pay.PayValidHelper;
import com.gewara.support.ErrorCode;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;

public class SportSpecialDiscountHelper extends SpecialDiscountHelper{
	private SportOrder order;
	private OpenTimeTable ott;
	private List<Discount> discountList;
	private List<OpenTimeItem> otiList;
	public SportSpecialDiscountHelper(SportOrder order, OpenTimeTable ott, List<Discount> discountList, List<OpenTimeItem> otiList){
		this.order = order;
		this.ott = ott;
		this.discountList = discountList;
		this.otiList = otiList;
	}

	@Override
	public String getFullDisabledReason(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList) {
		return getFullDisabledReason(spcounter, cpcounterList, sd, ott, order);
	}
	private String getFullDisabledReason(Spcounter spcounter, List<Cpcounter> cpcounterList, SpecialDiscount sd, OpenTimeTable table, SportOrder sorder) {
		String reason = "";
		if(StringUtils.isNotBlank(sd.getPaymethod())){
			String[] pay = sd.getPaymethod().split(":");
			if(!StringUtils.equals(pay[0], sorder.getPaymethod())) reason += "֧����ʽ��֧�֣�";
			if(pay.length > 1 && !StringUtils.equals(pay[1], sorder.getPaybank())){
				reason += "֧�����ز�֧�֣�";
			}
		}
		if(sorder.getAddtime().before(sd.getTimefrom()) || sorder.getAddtime().after(sd.getTimeto()))
			reason += "���ʱ��Ϊ" + DateUtil.formatTimestamp(sd.getTimefrom()) + "��" + DateUtil.formatTimestamp(sd.getTimeto()) + "��";
		//ע�⣬spcounter����Ϊ��
		String rs = getSpcounterDisabledReason(spcounter, cpcounterList, sorder.getCitycode(), sorder.getPartnerid(), sorder.getQuantity());
		if(StringUtils.isNotBlank(rs)) reason += rs;

		if(sorder.getQuantity() > sd.getBuynum()|| sorder.getQuantity() < sd.getMinbuy()){
			if(sd.getBuynum() == sd.getMinbuy()) reason += "������ʶ����빺��" + sd.getBuynum() + "�ţ�";
			else reason += "�������ֻ��Ԥ��" + sd.getMinbuy() + "��" + sd.getBuynum() + "����λ��";
		}
		reason += getOttFullDisabledReason(sd, table, sorder.getAddtime());
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
			else return "�������ֻ��Ԥ��" + sd.getMinbuy() + "��" + sd.getBuynum() + "����λ��";
		}
		return getOttFirstDisabledReason(sd, ott, order.getAddtime());
	}

	@Override
	public ErrorCode<Integer> getSpdiscountAmount(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList, PayValidHelper pvh) {
		if(sd==null) return ErrorCode.getFailure("���������");
		if(StringUtils.equals(order.getStatus(), OrderConstant.STATUS_NEW_CONFIRM)){
			return ErrorCode.getFailure("�Ѿ�ȷ�ϵĶ��������޸ģ�");
		}
		if(StringUtils.startsWith(order.getStatus(), OrderConstant.STATUS_PAID)){
			return ErrorCode.getFailure("�Ѿ�֧���Ķ��������޸ģ�");
		}
		if(StringUtils.startsWith(order.getStatus(), OrderConstant.STATUS_CANCEL)){
			return ErrorCode.getFailure("�Ѿ�ȡ���Ķ��������޸ģ�");
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
			return ErrorCode.getFailure("�����β�֧�ִ˻");
		}
		if(StringUtils.equals(sd.getOpentype(), SpecialDiscount.OPENTYPE_SPECIAL)){
			if(!StringUtils.contains(ott.getSpflag(), sd.getFlag())){
				return ErrorCode.getFailure("�����β�֧�ִ˻��");
			}
		} else if (!StringUtils.contains(ott.getElecard(), PayConstant.CARDTYPE_PARTNER)) {
			return ErrorCode.getFailure("�����β�֧�ִ˻��");
		}
		
		if(StringUtils.equals(sd.getOpentype(), SpecialDiscount.OPENTYPE_GEWA)){//�������
			if(order.surePartner()) return ErrorCode.getFailure("�����β�֧�ִ˻��");//��������WAP���̼�
		}else if(StringUtils.equals(sd.getOpentype(), SpecialDiscount.OPENTYPE_WAP)){//WAP�
			if(!order.sureGewaPartner()) return ErrorCode.getFailure("�����֧�ַ��ֻ��û�������"); 
			List<Long> ptnidList = BeanUtil.getIdList(sd.getPtnids(), ",");
			if(!ptnidList.contains(order.getPartnerid())){
				return ErrorCode.getFailure("�����֧�ָÿͻ��ˣ�");
			}
		}else if(StringUtils.equals(SpecialDiscount.OPENTYPE_PARTNER, sd.getOpentype())){//�̼һ
			if(!order.getPartnerid().equals(Long.valueOf(sd.getPtnids()))){
				return ErrorCode.getFailure("�����β�֧�ִ˻��");
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
			int disquantity = order.getQuantity()/2;
			if(disquantity>0){
				if(ott.hasPeriod()||ott.hasInning()){
					amount = order.getTotalfee()/2;
				}else{
					List<OpenTimeItem> tmpOtiList = BeanUtil.getSubList(otiList, 0, disquantity);
					for(OpenTimeItem oti: tmpOtiList){
						amount += oti.getPrice();
					}
				}
			}
		}
		if(amount == 0 && sd.getRebates()==0) return ErrorCode.getFailure("�˶������Żݣ�");
		if(amount > order.getTotalfee()) amount = order.getTotalfee();
		return ErrorCode.getSuccessReturn(amount);
	}
	private String getOttFirstDisabledReason(SpecialDiscount sd, OpenTimeTable table, Timestamp addtime) {
		if(!isEnabledByCategoryid(sd, table.getItemid())) return "�����֧�ָ���Ŀ��";
		if(!isEnabledByAddweek(sd, addtime)) return "���������" + sd.getAddweek() + "����";
		if(!isEnabledByWeektype(sd, table.getPlaydate())) return "���������" + sd.getWeektype() + "�ĳ��Σ�";
		if(StringUtils.isNotBlank(sd.getFieldid())){
			List<Long> sdFieldIdList = BeanUtil.getIdList(sd.getFieldid(), ",");
			List<Long> fieldList = BeanUtil.getBeanPropertyList(otiList, Long.class, "fieldid", true);
			if(!sdFieldIdList.isEmpty()&& ListUtils.intersection(sdFieldIdList, fieldList).isEmpty())
				return "������Ʋ�������Щ����ʹ�ã�";
		}
		String add_time = DateUtil.format(addtime, "HHmm");
		if(sd.getAddtime2().compareTo(add_time)<=0  || sd.getAddtime1().compareTo(add_time) > 0){
			return "�������" + sd.getAddtime1().substring(0,2) + ":" + sd.getAddtime1().substring(2) +"��" + 
			sd.getAddtime2().substring(0,2) + ":" + sd.getAddtime2().substring(2) + "����";
		}
		if(StringUtils.isNotBlank(sd.getTime1()) && StringUtils.isNotBlank(sd.getTime2())){
			for (OpenTimeItem item : otiList) {
				if(!table.hasInning()&&(sd.getTime2().compareTo(StringUtils.replace(item.getEndhour(),":",""))<=0 || sd.getTime1().compareTo(StringUtils.replace(item.getHour(),":",""))>0))
					return "������޳��ػ�ʱ��Ϊ" +sd.getTime1().substring(0,2) + ":" + sd.getTime1().substring(2) +"��" + 
						sd.getTime2().substring(0,2) + ":" + sd.getTime2().substring(2) + "����";
			}
		}
		return "";
	}
	public String getOttFullDisabledReason(SpecialDiscount sd, OpenTimeTable table, Timestamp addtime) {
		String reason = "";
		if(!isEnabledByCategoryid(sd, table.getItemid())) reason += "�����֧�ָ��˶���Ŀ��";
		if(!isEnabledByAddweek(sd, addtime)) reason += "���������" + sd.getAddweek() + "����";
		if(!isEnabledByWeektype(sd, table.getPlaydate()))reason += "���������" + sd.getWeektype() + "�ĳ��Σ�";
		if(!isEnabledByAddtime(sd, addtime)){
			reason += "�������" + sd.getAddtime1().substring(0,2) + ":" + sd.getAddtime1().substring(2) +"��" + 
			sd.getAddtime2().substring(0,2) + ":" + sd.getAddtime2().substring(2) + "����";
		}
		return reason;
	}
	@Override
	public ErrorCode isEnabled(SpecialDiscount sd, PayValidHelper pvh){
		return isEnabled(sd, ott, pvh);
	}

	private ErrorCode isEnabled(SpecialDiscount sd, OpenTimeTable table, PayValidHelper pvh) {
		if (sd == null) return ErrorCode.getFailure("���������");
		if(!isEnabledByCitycode(sd, table.getCitycode())) return ErrorCode.getFailure("���в�֧�֣�");
		if(!isEnabledByRelatedid(sd,table.getSportid())) return ErrorCode.getFailure("���ݲ�֧�֣�");
		if(!isEnabledByCategoryid(sd, table.getItemid())) return ErrorCode.getFailure("��Ŀ��֧�֣�");
		if(!isEnabledByItemid(sd, table.getId())) return ErrorCode.getFailure("���β�֧�֣�");
		if(StringUtils.isNotBlank(sd.getPaymethod()) && 
				StringUtils.isNotBlank(table.getOtherinfo())){
			String[] pay = StringUtils.split(sd.getPaymethod());
			if(!pvh.supportPaymethod(pay[0])) return ErrorCode.getFailure("֧�����ƣ�");
		}
		if(order!=null) {
			if(!isEnabledByPrice(sd, order.getUnitprice()))	return ErrorCode.getFailure("���۷�Χ��֧�֣�");
			if(!isEnabledByPricegap(sd, order.getUnitprice() - order.getCostprice())) return ErrorCode.getFailure("�ɱ���֧�֣�");
			if(!isEnabledByCostprice(sd, order.getCostprice())) return ErrorCode.getFailure("�ɱ���Χ��֧�֣�");
		}
		return ErrorCode.SUCCESS;
	}
	public static interface OrderCallback {
		/**
		 * baseDao.saveObject(order)ǰ���
		 * 
		 * @param order
		 *
		 * @author leo.li
		 * Modify Time Mar 26, 2013 5:37:51 PM
		 */
		void processOrder(SpecialDiscount sd, GewaOrder gewaOrder);
	}

}
