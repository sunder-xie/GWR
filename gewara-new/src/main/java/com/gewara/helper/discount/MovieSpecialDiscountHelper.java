package com.gewara.helper.discount;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.PayConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.helper.order.GewaOrderHelper;
import com.gewara.helper.sys.CachedScript;
import com.gewara.helper.sys.CachedScript.ScriptResult;
import com.gewara.helper.sys.ScriptEngineUtil;
import com.gewara.model.pay.Cpcounter;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.OtherFeeDetail;
import com.gewara.model.pay.Spcounter;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.SellSeat;
import com.gewara.pay.PayValidHelper;
import com.gewara.support.ErrorCode;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;

public class MovieSpecialDiscountHelper extends SpecialDiscountHelper{
	private OpenPlayItem opi;
	private TicketOrder order;
	private List<SellSeat> seatList;
	private List<Discount> discountList;

	public MovieSpecialDiscountHelper(OpenPlayItem opi, TicketOrder order, List<SellSeat> seatList, List<Discount> discountList) {
		this.opi = opi;
		this.order = order;
		this.seatList = seatList;
		this.discountList = discountList;
	}

	@Override
	public String getFullDisabledReason(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList) {
		return getFullDisabledReason(spcounter, cpcounterList, sd, opi, order);
	}
	public static String getFullDisabledReason(Spcounter spcounter, List<Cpcounter> cpcounterList, SpecialDiscount sd, OpenPlayItem opi, TicketOrder order) {
		//����gewaprice, timefrom, timeto, time1, time2, pricegap, price1, price2���
		String reason = "";
		if(StringUtils.isNotBlank(sd.getPaymethod())){
			String[] pay = sd.getPaymethod().split(":");
			if(!StringUtils.equals(pay[0], order.getPaymethod())) reason += "֧����ʽ��֧�֣�";
			if(pay.length > 1 && !StringUtils.equals(pay[1], order.getPaybank())){
				reason += "֧�����ز�֧�֣�";
			}
		}
		if(!isEnabledByFromToTime(sd, order.getAddtime()))
			reason += "���ʱ��Ϊ" + DateUtil.formatTimestamp(sd.getTimefrom()) + "��" + DateUtil.formatTimestamp(sd.getTimeto()) + "��";
		//ע�⣬spcounter����Ϊ��
		String rs = getSpcounterDisabledReason(spcounter, cpcounterList, order.getCitycode(), order.getPartnerid(), order.getQuantity());
		if(StringUtils.isNotBlank(rs)) reason += rs;
		if(order.getQuantity() > sd.getBuynum()|| order.getQuantity() < sd.getMinbuy()){
			if(sd.getBuynum() == sd.getMinbuy()) reason += "������ʶ����빺��" + sd.getBuynum() + "�ţ�";
			else reason += "������ʶ�ֻ�ܹ���" + sd.getMinbuy() + "��" + sd.getBuynum() + "�ţ�";
		}
		reason += getOpiFullDisabledReason(sd, opi, order.getAddtime());
		return reason;
	}

	@Override
	public String getOrderFirstDisabledReason(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList) {
		//����gewaprice, timefrom, timeto, time1, time2, pricegap, price1, price2���
		if(!isEnabledByFromToTime(sd, order.getAddtime())){
			return "���ʱ��Ϊ" + DateUtil.formatTimestamp(sd.getTimefrom()) + "��" + DateUtil.formatTimestamp(sd.getTimeto()) + "��";
		}
		//ע�⣬spcounter����Ϊ��
		String rs = getSpcounterDisabledReason(spcounter, cpcounterList, order.getCitycode(), order.getPartnerid(), order.getQuantity());
		if(StringUtils.isNotBlank(rs)) return rs;
		if(order.getQuantity() > sd.getBuynum()|| order.getQuantity() < sd.getMinbuy()){
			if(sd.getBuynum() == sd.getMinbuy()) return "������ʶ����빺��" + sd.getBuynum() + "�ţ�";
			else return "������ʶ�ֻ�ܹ���" + sd.getMinbuy() + "��" + sd.getBuynum() + "�ţ�";
		}
		return getOpiFirstDisabledReason(sd, opi, order.getAddtime());
	}

	@Override
	public ErrorCode isEnabled(SpecialDiscount sd, PayValidHelper pvh) {
		return isEnabled(sd, opi, pvh);
	}

	@Override
	public ErrorCode<Integer> getSpdiscountAmount(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList, PayValidHelper pvh) {
		if (sd == null) return ErrorCode.getFailure("���������");
		if (StringUtils.equals(order.getStatus(), OrderConstant.STATUS_NEW_CONFIRM)) {
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
				return ErrorCode.getFailure("�˻��֧����ѡ���֧����ʽ��");
			pvh = new PayValidHelper(order.getPaymethod());
		}
		if (discountList.size() > 0) {
			return ErrorCode.getFailure("���ܺ������Żݷ�ʽ���ã�");
		}
		ErrorCode enable = isEnabled(sd, pvh);
		if (!enable.isSuccess()) {
			if(isShowMsg()) return ErrorCode.getFailure(enable.getMsg());
			return ErrorCode.getFailure("�����β�֧�ִ˻");
		}
		if (StringUtils.equals(sd.getOpentype(), SpecialDiscount.OPENTYPE_SPECIAL)) {
			if (!StringUtils.contains(opi.getSpflag(), sd.getFlag()) && StringUtils.isBlank(sd.getVerifyType())) {
				return ErrorCode.getFailure("�����β�֧�ִ˻��");
			}
		} else if (!StringUtils.contains(opi.getElecard(), PayConstant.CARDTYPE_PARTNER)) {
			return ErrorCode.getFailure("�����β�֧�ִ˻��");
		}

		if (StringUtils.equals(sd.getOpentype(), SpecialDiscount.OPENTYPE_GEWA)) {// �������
			if (order.surePartner()) return ErrorCode.getFailure("�����β�֧�ִ˻��");// ��������WAP���̼�
		} else if (StringUtils.equals(sd.getOpentype(), SpecialDiscount.OPENTYPE_WAP)) {// WAP�
			if (!order.sureGewaPartner()) return ErrorCode.getFailure("�����֧�ַ��ֻ��û�������");
			List<Long> ptnidList = BeanUtil.getIdList(sd.getPtnids(), ",");
			if(!ptnidList.contains(order.getPartnerid())){
				return ErrorCode.getFailure("�����֧�ָÿͻ��ˣ�");
			}
		} else if (StringUtils.equals(SpecialDiscount.OPENTYPE_PARTNER, sd.getOpentype())) {// �̼һ
			if(!order.getPartnerid().equals(Long.valueOf(sd.getPtnids()))){
				return ErrorCode.getFailure("�����β�֧�ִ˻��");
			}
		}
		String disableReason = getOrderFirstDisabledReason(sd, spcounter, cpcounterList);
		if (StringUtils.isNotBlank(disableReason))
			return ErrorCode.getFailure(disableReason);

		int amount = 0;
		if (StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_PERORDER)) {
			amount = sd.getDiscount();
		} else if (StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_PERTICKET)) {
			amount = sd.getDiscount() * order.getQuantity();
		} else if (StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_FIXPRICE)) {
			amount = order.getTotalfee() - sd.getDiscount() * order.getQuantity();
		} else if (StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_PERCENT)) {
			amount = order.getTotalfee() * sd.getDiscount() / 100;
		} else if (StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_BUYONE_GIVEONE)) {
			int disquantity = order.getQuantity() / 2;
			if (disquantity > 0) {
				List<SellSeat> mseatList = GewaOrderHelper.getMaxSellSeat(seatList, discountList, disquantity);
				for (SellSeat seat : mseatList) {
					if (order.surePartner()) {
						amount += order.getUnitprice();
					} else {
						amount += seat.getPrice();
					}
				}
			}
		} else if(StringUtils.equals(sd.getDistype(), SpecialDiscount.DISCOUNT_TYPE_EXPRESSION)){//���ʽ
			ScriptResult<Integer> result = compute(order, opi, sd.getExpression(), true);
			if(result.hasError()){
				return ErrorCode.getFailure("�Żݼ������");
			}
			amount = result.getRetval();
		}
		if (amount == 0 && sd.getRebates() == 0)
			return ErrorCode.getFailure("�˶������Żݣ�");
		if (amount > order.getTotalfee())
			amount = order.getTotalfee();
		return ErrorCode.getSuccessReturn(amount);
	}
	public static ScriptResult<Integer> compute(TicketOrder order, OpenPlayItem opi, String expression, boolean cache){
		CachedScript script = ScriptEngineUtil.buildCachedScript(expression, cache);
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("order", BeanUtil.getBeanMapWithKey(order, "mobile","addtime","memberid","partnerid","membername","paymethod","paybank","quantity","unitprice"));
		context.put("opi", BeanUtil.getBeanMapWithKey(opi, "mpid","movieid","cinemaid","playtime","price","costprice","gewaprice","language","edition","citycode","opentype"));
		ScriptResult<Integer> result = script.run(context);
		return result;
	}
	public static String getOpiFirstDisabledReason(SpecialDiscount sd, OpenPlayItem opi, Timestamp addtime) {
		if(!isEnabledByCategoryid(sd, opi.getMovieid())) return "�����֧�ָ�ӰƬ��";
		if(!isEnabledByAddweek(sd, addtime)) return "���������" + sd.getAddweek() + "����";
		if(!isEnabledByWeektype(sd, opi.getPlaytime())) return "���������" + sd.getWeektype() + "�ĳ��Σ�";
		if(!isEnabledByTime(sd, opi.getPlaytime())){
			return "�������" + sd.getTime1().substring(0,2) + ":" + sd.getTime1().substring(2) +"��" + 
				sd.getTime2().substring(0,2) + ":" + sd.getTime2().substring(2) + "�ĳ��Σ�";
		}
		if(!isEnabledByAddtime(sd, addtime)){
			return "�������" + sd.getAddtime1().substring(0,2) + ":" + sd.getAddtime1().substring(2) +"��" + 
			sd.getAddtime2().substring(0,2) + ":" + sd.getAddtime2().substring(2) + "����";
		}
		if(StringUtils.isNotBlank(sd.getFieldid())){
			List<Long> roomidList = BeanUtil.getIdList(sd.getFieldid(), ",");
			if(!roomidList.contains(opi.getRoomid())){
				return "�����֧�ָ�����";
			}
		}
		return "";
	}
	private static String getOpiFullDisabledReason(SpecialDiscount sd, OpenPlayItem opi, Timestamp addtime) {
		String reason = "";
		if(!isEnabledByCategoryid(sd, opi.getMovieid())) reason += "�����֧�ָ�ӰƬ��";
		if(!isEnabledByAddweek(sd, addtime))  reason += "���������" + sd.getAddweek() + "����";
		if(!isEnabledByWeektype(sd, opi.getPlaytime())) reason += "���������" + sd.getWeektype() + "�ĳ��Σ�";
		if(!isEnabledByTime(sd, opi.getPlaytime())){
			reason += "�������" + sd.getTime1().substring(0,2) + ":" + sd.getTime1().substring(2) +"��" + 
				sd.getTime2().substring(0,2) + ":" + sd.getTime2().substring(2) + "�ĳ��Σ�";
		}
		if(!isEnabledByAddtime(sd, addtime)){
			reason += "�������" + sd.getAddtime1().substring(0,2) + ":" + sd.getAddtime1().substring(2) +"��" + 
			sd.getAddtime2().substring(0,2) + ":" + sd.getAddtime2().substring(2) + "����";
		}
		if(StringUtils.isNotBlank(sd.getFieldid())){
			List<Long> roomidList = BeanUtil.getIdList(sd.getFieldid(), ",");
			if(!roomidList.contains(opi.getRoomid())){
				reason += "�����֧�ָ�����";
			}
		}
		return reason;
	}
	public static ErrorCode isEnabled(SpecialDiscount sd, OpenPlayItem opi, PayValidHelper pvh){
		if (sd == null) return ErrorCode.getFailure("���������");
		if(!isEnabledByCitycode(sd, opi.getCitycode())) return ErrorCode.getFailure("���в�֧�֣�");
		if(!isEnabledByRelatedid(sd, opi.getCinemaid())) return ErrorCode.getFailure("���ݲ�֧�֣�");
		if(!isEnabledByCategoryid(sd, opi.getMovieid())) return ErrorCode.getFailure("��Ӱ��֧�֣�");
		if(!isEnabledByItemid(sd, opi.getMpid())) return ErrorCode.getFailure("���β�֧�֣�");
		if(!isEnableByEdtion(sd, opi.getEdition())) return ErrorCode.getFailure("�汾��֧�֣�");
		if(StringUtils.isNotBlank(sd.getPaymethod()) && 
				StringUtils.isNotBlank(opi.getOtherinfo())){
			String[] pay = StringUtils.split(sd.getPaymethod());
			if(!pvh.supportPaymethod(pay[0])) 
				return ErrorCode.getFailure("֧�����ƣ�");
		}
		if(opi.getGewaprice() > sd.getPrice2()  || opi.getGewaprice() < sd.getPrice1()){
			return ErrorCode.getFailure("���۷�Χ��֧�֣�");
		}
		int costprice = opi.getCostprice() == null ? 0 : opi.getCostprice();
		if(!isEnabledByPricegap(sd, opi.getGewaprice() - costprice)) return ErrorCode.getFailure("�ɱ���֧�֣�");
		if(!isEnabledByCostprice(sd, costprice)) return ErrorCode.getFailure("�ɱ���Χ��֧�֣�");
		return ErrorCode.SUCCESS;
	}
	
	public static boolean isEnableByEdtion(SpecialDiscount sd, String edition){
		if(StringUtils.isNotBlank(sd.getEdition())){
			List<String> editionList = Arrays.asList(StringUtils.split(sd.getEdition(), ","));
			if(!editionList.isEmpty() && !editionList.contains(edition)){
				return false;
			}
		}
		return true;
	}
}
