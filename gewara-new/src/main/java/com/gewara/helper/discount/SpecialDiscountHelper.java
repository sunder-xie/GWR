package com.gewara.helper.discount;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.gewara.constant.AdminCityContant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.helper.order.GewaOrderHelper;
import com.gewara.helper.sys.CachedScript;
import com.gewara.helper.sys.CachedScript.ScriptResult;
import com.gewara.helper.sys.ScriptEngineUtil;
import com.gewara.model.pay.Cpcounter;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.Spcounter;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.pay.PayValidHelper;
import com.gewara.support.ErrorCode;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;

public abstract class SpecialDiscountHelper {
	protected boolean showMsg = false;
	
	public static final String SPECIAL_RULE_CATEGORY_KEY = "categoryid";	//��ĿID
	public static final String SPECIAL_RULE_RELATE_KEY = "relatedid";		//����ID
	/**
	 * ���������õ�ԭ�򣺶���+����
	 * @param sd
	 * @return
	 */
	public abstract String getOrderFirstDisabledReason(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList);
	/**
	 * �����õ�����ԭ�򣺶���+֧��������+����
	 * @param sd
	 * @return
	 */
	public abstract String getFullDisabledReason(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList);
	public abstract ErrorCode isEnabled(SpecialDiscount sd, PayValidHelper pvh);
	/**
	 * ��ȡ�����ۿ۽��
	 * @param sd
	 * @param spcounter
	 * @param cpcounterList
	 * @param pvh
	 * @return
	 */
	public abstract ErrorCode<Integer> getSpdiscountAmount(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList, PayValidHelper pvh);
	/**
	 * ���Զ���״̬����ⶩ���ؼۻ���
	 * @param sd
	 * @param spcounter
	 * @param cpcounterList
	 * @param pvh
	 * @return
	 */
	public abstract ErrorCode<Integer> validSpdiscountWithoutStatus(SpecialDiscount sd, Spcounter spcounter, List<Cpcounter> cpcounterList, PayValidHelper pvh);
	public static String[] getUniqueKey(SpecialDiscount sd, Spcounter spcounter, GewaOrder order) {
		String opkey = "sd" + sd.getId();
		if(spcounter!=null && StringUtils.equals(spcounter.getCtlmember(), "Y")) {
			opkey = "spd" + spcounter.getId();
		}
		if(!StringUtils.equals(sd.getPeriodtype(), SpecialDiscount.DISCOUNT_PERIOD_A)){
			Timestamp createtime = order.getCreatetime();
			if(StringUtils.equals(sd.getPeriodtype(), SpecialDiscount.DISCOUNT_PERIOD_D)){
				opkey = opkey + 'd' + DateUtil.format(createtime, "MMdd");
			}else if(StringUtils.equals(sd.getPeriodtype(), SpecialDiscount.DISCOUNT_PERIOD_W)){
				opkey = opkey + 'w' + getWeekOfYear(createtime);
			}else if(StringUtils.equals(sd.getPeriodtype(), SpecialDiscount.DISCOUNT_PERIOD_DW)){
				int week = getWeekOfYear(createtime);
				int tmp = 0;
				if(week%2!=0){
					tmp = week/2+1;
				}else {
					tmp = week/2;
				}
				opkey = opkey + 'w' + tmp;
			}else if(StringUtils.equals(sd.getPeriodtype(), SpecialDiscount.DISCOUNT_PERIOD_M)){
				opkey = opkey + 'm' + DateUtil.getMonth(DateUtil.getDateFromTimestamp(createtime));
			}
		}
		if(StringUtils.equals(OrderConstant.UNIQUE_BY_MEMBERID, sd.getUniqueby())){
			return new String[]{opkey + order.getMemberid()};
		}else if(StringUtils.equals(OrderConstant.UNIQUE_BY_MOBILE, sd.getUniqueby())){
			return new String[]{opkey + order.getMobile()};
		}else if(StringUtils.equals(OrderConstant.UNIQUE_BY_PARTNERNAME, sd.getUniqueby())){ //��������û���ʶ
			return new String[]{opkey + GewaOrderHelper.getPartnerUkey(order)};
		}
		if(order.sureOutPartner()){//�̼Ҳ���
			return new String[]{opkey + order.getMobile()};	
		}
		return new String[]{opkey + order.getMemberid(), opkey + order.getMobile()};
	}
	private static int getWeekOfYear(Timestamp time){
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.getDateFromTimestamp(time));
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		return week;
	}
	public static String getSpcounterDisabledReason(Spcounter sc, List<Cpcounter> cpcounterList, String citycode, Long partnerid, int quantity){
		if(StringUtils.equals(sc.getCtltype(), Spcounter.CTLTYPE_QUANTITY)){
			if(sc.getBasenum() <= sc.getSellquantity() || sc.getLimitmaxnum() <= sc.getAllquantity()){
				return "�������������";
			}
		}else if(sc.getBasenum() <= sc.getSellordernum() || sc.getLimitmaxnum() <= sc.getAllordernum()){
			return "�������������";
		}
		if(sc.getAllowaddnum() <= 0){
			return "�µ��������࣬���ɵ�15������δ֧���Ķ����ͷ����";
		}
		Map<String, List<Cpcounter>> cpcounterMap = BeanUtil.groupBeanList(cpcounterList, "flag");
		String msg = getScCity(sc, cpcounterMap.get(Cpcounter.FLAG_CITYCODE), citycode, quantity);
		if(StringUtils.isNotBlank(msg)) return msg;
		msg = getScPartner(sc, cpcounterMap.get(Cpcounter.FLAG_PARTNER), partnerid, quantity);
		return msg;
	}
	
	public static String getScCity(Spcounter sc, List<Cpcounter> cpcounterList, String citycode, int quantity){
		if(cpcounterList == null || cpcounterList.isEmpty()) return "";
		for(Cpcounter cpcounter: cpcounterList){
			String code = cpcounter.getCpcode();
			if(StringUtils.equals(cpcounter.getFlag(), Cpcounter.FLAG_CITYCODE) && Arrays.asList(StringUtils.split(code, ",")).contains(citycode)){
				//1.֧���������
				if(cpcounter.getLimitnum() != null){
					int limitnum = cpcounter.getLimitnum();
					if(StringUtils.equals(sc.getCtltype(), Spcounter.CTLTYPE_QUANTITY)){
						int sellquantity = cpcounter.getSellquantity();
						if(sellquantity + quantity > limitnum) return "�˳�������������";
					}else{
						int sellorder = cpcounter.getSellorder();
						if(sellorder >= limitnum) return "�˳�������������";
					}
				}
				//2.�µ��������
				int count = cpcounter.getAllownum();
				if(count <= 0) return "�˳����µ��������࣬���ɵ�15������δ֧���Ķ����ͷ����";
				if(StringUtils.equals(sc.getCtltype(), Spcounter.CTLTYPE_QUANTITY) && count - quantity<=0) return "�˳����µ��������࣬���ɵ�15������δ֧���Ķ����ͷ����";
				return "";
			}
		}
		return "";
	}
	public static String getScPartner(Spcounter sc, List<Cpcounter> cpcounterList, Long partnerid, int quantity){
		if(cpcounterList == null || cpcounterList.isEmpty()) return "";
		for(Cpcounter cpcounter: cpcounterList){
			String code = cpcounter.getCpcode();
			if(StringUtils.equals(cpcounter.getFlag(),Cpcounter.FLAG_PARTNER) && BeanUtil.getIdList(code, ",").contains(partnerid)){//
				//1.֧���������
				if(cpcounter.getLimitnum() != null){
					int limitnum = cpcounter.getLimitnum();
					if(StringUtils.equals(sc.getCtltype(), Spcounter.CTLTYPE_QUANTITY)){
						int sellquantity = cpcounter.getSellquantity();
						if(sellquantity + quantity > limitnum) return "����������������";
					}else{
						int sellorder = cpcounter.getSellquantity();
						if(sellorder >= limitnum) return "����������������";
					}
				}
				//2.�µ��������
				int count = cpcounter.getAllownum();
				if(count <= 0) return "����������������";
				if(StringUtils.equals(sc.getCtltype(), Spcounter.CTLTYPE_QUANTITY) && count - quantity<=0) return "����������������";
				return "";
			}
		}
		return "";
	}
	//�����µ�����
	public static Cpcounter updateCityAddCounter(Spcounter spcounter, List<Cpcounter> cpcounterList, GewaOrder order){
		if(cpcounterList != null){
			for(Cpcounter cpcounter : cpcounterList){
				String code = cpcounter.getCpcode();
				if(StringUtils.equals(cpcounter.getFlag(), Cpcounter.FLAG_CITYCODE) && Arrays.asList(StringUtils.split(code, ",")).contains(order.getCitycode())){//
					int count = cpcounter.getAllownum();
					if(StringUtils.equals(spcounter.getCtltype(), Spcounter.CTLTYPE_QUANTITY)){
						count -= order.getQuantity();
					}else{
						count --;
					}
					cpcounter.setAllownum(count);
					return cpcounter;
				}
			}
		}
		return null;
	}
	//�̼��µ�����
	public static Cpcounter updatePartnerAddCounter(Spcounter spcounter, List<Cpcounter> cpcounterList, GewaOrder order){
		if(cpcounterList != null){
			//���п��Ƽ���
			for(Cpcounter cpcounter: cpcounterList){
				String code = cpcounter.getCpcode();
				if(StringUtils.equals(cpcounter.getFlag(), Cpcounter.FLAG_PARTNER) && BeanUtil.getIdList(code, ",").contains(order.getPartnerid())){//
					int count = cpcounter.getAllownum();
					if(StringUtils.equals(spcounter.getCtltype(), Spcounter.CTLTYPE_QUANTITY)){
						count -= order.getQuantity();
					}else{
						count --;
					}
					cpcounter.setAllownum(count);
					return cpcounter;
				}
			}
		}
		return null;
	}
	//������������
	public static Cpcounter updateCitySellCounter(List<Cpcounter> cpcounterList, GewaOrder order){
		if(cpcounterList != null){
			for (Cpcounter cpcounter : cpcounterList) {
				String code = cpcounter.getCpcode();
				if(StringUtils.equals(cpcounter.getFlag(), Cpcounter.FLAG_CITYCODE) && Arrays.asList(StringUtils.split(code, ",")).contains(order.getCitycode())){//
					//����
					cpcounter.setSellorder(cpcounter.getSellorder() + 1);
					cpcounter.setSellquantity(cpcounter.getSellquantity() + order.getQuantity());
					//������
					cpcounter.setAllordernum(cpcounter.getAllordernum() + 1);
					cpcounter.setAllquantity(cpcounter.getAllquantity() + order.getQuantity());
					return cpcounter;
				}
			}
		}
		return null;
	}
	//�̼���������
	public static Cpcounter updatePartnerSellCounter(List<Cpcounter> cpcounterList,GewaOrder order){
		if(cpcounterList != null){
			for (Cpcounter cpcounter : cpcounterList) {
				String code = cpcounter.getCpcode();
				if(StringUtils.equals(cpcounter.getFlag(), Cpcounter.FLAG_PARTNER) && BeanUtil.getIdList(code, ",").contains(order.getPartnerid())){//
					//����
					cpcounter.setSellorder(cpcounter.getSellorder() + 1);
					cpcounter.setSellquantity(cpcounter.getSellquantity() + order.getQuantity());
					//������
					cpcounter.setAllordernum(cpcounter.getAllordernum() + 1);
					cpcounter.setAllquantity(cpcounter.getAllquantity() + order.getQuantity());

					return cpcounter;
				}
			}
		}
		return null;
	}
	public static String getTimeStr(int minutes) {
		int hour = minutes/60;
		int min = minutes%60;
		int day = 0;
		if(hour > 24){
			day = hour/24;
			hour = hour % 24;
		}
		String result = (day > 0?day+"��":"") + (hour>0? hour+"Сʱ":"") + (min>0?min+"��":"");
		return result;
	}
	
	public boolean isShowMsg() {
		return showMsg;
	}
	public void setShowMsg(boolean showMsg) {
		this.showMsg = showMsg;
	}

	/**
	 * ����޳��п���
	 * @param sd
	 * @param citycode
	 * @return
	 */
	public static final boolean isEnabledByCitycode(SpecialDiscount sd, String citycode){
		if(StringUtils.isNotBlank(sd.getCitycode())){
			if(StringUtils.isNotBlank(sd.getCitycode())){
				if(!StringUtils.equals(AdminCityContant.CITYCODE_ALL, sd.getCitycode()) && 
					!ArrayUtils.contains(StringUtils.split(sd.getCitycode(), ","), citycode)){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * ����޳��ݿ���
	 * @param sd
	 * @param relatedid
	 * @return
	 */
	public static final boolean isEnabledByRelatedid(SpecialDiscount sd, Long relatedid){
		if(StringUtils.isNotBlank(sd.getRelatedid())){
			List<Long> idList = BeanUtil.getIdList(sd.getRelatedid(), ",");
			if(!idList.isEmpty() && !idList.contains(relatedid)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * �������Ŀ����
	 * @param sd
	 * @param categoryid
	 * @return
	 */
	public static final boolean isEnabledByCategoryid(SpecialDiscount sd, Long categoryid){
		if(StringUtils.isNotBlank(sd.getCategoryid())){
			List<Long> idList = BeanUtil.getIdList(sd.getCategoryid(), ",");
			if(!idList.isEmpty() && !idList.contains(categoryid)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ����޳��ο���
	 * @param sd
	 * @param itemid
	 * @return
	 */
	public static final boolean isEnabledByItemid(SpecialDiscount sd, Long itemid){
		if(StringUtils.isNotBlank(sd.getItemid())){
			List<Long> idList = BeanUtil.getIdList(sd.getItemid(), ",");
			if(!idList.isEmpty() && !idList.contains(itemid)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ������ܼ��µ�����
	 * @param sd
	 * @param addtime
	 * @return
	 */
	public static final <T extends Date> boolean isEnabledByAddweek(SpecialDiscount sd, T addtime){
		if(StringUtils.isNotBlank(sd.getAddweek())){
			String addweek = "" + DateUtil.getWeek(addtime);
			if(!StringUtils.contains(sd.getAddweek(), addweek)) return false;
		}
		return true;
	}
	
	/**
	 * ����޳����ܼ�����
	 * @param sd
	 * @param playtime
	 * @return
	 */
	public static final <T extends Date> boolean isEnabledByWeektype(SpecialDiscount sd, T playtime){
		if(StringUtils.isNotBlank(sd.getWeektype())){
			String openweek = "" + DateUtil.getWeek(playtime);
			if(!StringUtils.contains(sd.getWeektype(), openweek)) return false;
		}
		return true;
	}
	
	/**
	 * ����޳���ʱ��ο���
	 * @param sd
	 * @param playtime
	 * @return
	 */
	public static final <T extends Date> boolean isEnabledByTime(SpecialDiscount sd, T playtime){
		String open_time = DateUtil.format(playtime, "HHmm");
		if(sd.getTime2().compareTo(open_time)<=0  || sd.getTime1().compareTo(open_time) > 0 ){
			return false;
		}
		return true;
	}
	
	/**
	 * ������µ�ʱ��ο���
	 * @param sd
	 * @param addtime
	 * @return
	 */
	public static final <T extends Date> boolean isEnabledByAddtime(SpecialDiscount sd, T addtime) {
		String add_time = DateUtil.format(addtime, "HHmm");
		if(add_time.compareTo(sd.getAddtime1())< 0 || add_time.compareTo(sd.getAddtime2())>0){
			return false;
		}
		return true;
	}
	
	/**
	 * ��������۷�Χ
	 * @param sd
	 * @param price
	 * @return
	 */
	public static final boolean isEnabledByPrice(SpecialDiscount sd, Integer price){
		if(price > sd.getPrice2()  || price < sd.getPrice1()){
			return false;
		}
		return true;
	}
	
	/**
	 * ����޳ɱ��۷�Χ
	 * @param sd
	 * @param costprice
	 * @return
	 */
	public static final boolean isEnabledByCostprice(SpecialDiscount sd, Integer costprice){
		if(costprice> sd.getCostprice2() || costprice <= sd.getCostprice1()){
			return false;
		}
		return true;
	}
	
	/**
	 * ����޳ɱ�������
	 * @param sd
	 * @param pricegap
	 * @return
	 */
	public static final boolean isEnabledByPricegap(SpecialDiscount sd, Integer pricegap){
		return !(sd.getPricegap() > pricegap);		
	}
	
	/**
	 * ��Ƿ�ʼ
	 * @param sd
	 * @param curtime
	 * @return
	 */
	public static final boolean isEnabledByFromToTime(SpecialDiscount sd, Timestamp curtime){
		return sd.getTimefrom().getTime()<= curtime.getTime() && curtime.getTime() < sd.getTimeto().getTime();
		//return sd.getTimefrom().before(curtime) && sd.getTimeto().after(curtime);
	}
	
	/**
	 * �������������ʾ
	 * @param sd
	 * @param simpleRuleMap
	 * @param cache
	 * @return
	 */
	public static final ErrorCode validSpecialcountRule(SpecialDiscount sd, Map<String, Object> context, boolean cache){
		if(StringUtils.isBlank(sd.getSpecialrule()) || CollectionUtils.isEmpty(context)) return ErrorCode.SUCCESS;
		CachedScript script = ScriptEngineUtil.buildCachedScript(sd.getSpecialrule(), cache);
		ScriptResult<String> result = script.run(context);
		if(result.hasError()){
			return ErrorCode.getFailure("�Żݼ������");
		}
		String retval = result.getRetval();
		if(StringUtils.equals("success", retval)){
			return ErrorCode.SUCCESS;
		}
		return ErrorCode.getFailure(retval);
	}
}
