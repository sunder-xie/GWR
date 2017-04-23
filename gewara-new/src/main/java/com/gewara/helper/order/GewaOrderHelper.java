package com.gewara.helper.order;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.PayConstant;
import com.gewara.model.pay.BuyItem;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.OtherFeeDetail;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.model.ticket.SellSeat;
import com.gewara.support.ErrorCode;
import com.gewara.support.MultiPropertyComparator;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.XSSFilter;

public class GewaOrderHelper {
	public static void useDiscount(GewaOrder order, List<Discount> discountList, Discount discount){
		discountList.remove(discount);
		discountList.add(discount);
		List<String> discountReason = BeanUtil.getBeanPropertyList(discountList, String.class, "description", false);
		order.setDisreason(StringUtils.join(discountReason, ";"));
		order.setDiscount(getDiscountAmount(discountList));
	}
	public static void unuseDiscount(GewaOrder order, List<Discount> discountList, Discount discount){
		discountList.remove(discount);
		List<String> discountReason = BeanUtil.getBeanPropertyList(discountList, String.class, "description", false);
		order.setDisreason(StringUtils.join(discountReason, ";"));
		order.setDiscount(getDiscountAmount(discountList));
	}
	private static Integer getDiscountAmount(List<Discount> discountList){//ʵʱ��ȡ�ۿ۽��
		int sum = 0;
		for(Discount discount: discountList){
			sum += discount.getAmount();
		}
		return sum;
	}
	public static String getBuyItemName(List<BuyItem> itemList){
		String result = "";
		if(itemList.size() > 0){
			for(BuyItem item : itemList){
				result = "," + result + item.getGoodsname();
			}
			return result.substring(1);
		}
		return result;
	}


	/**
	 * ��ȡû�е�ֵ�ļ۸���ߵ���λ
	 * @param num
	 * @return
	 */
	public static SellSeat getMaxSellSeat(List<SellSeat> seatList, List<Discount> discountList){
		if(seatList != null && seatList.size() > 0){
			Collections.sort(seatList, new MultiPropertyComparator<SellSeat>(new String[]{"price"}, new boolean[]{false}));
			if(discountList.isEmpty()) return seatList.get(0);
			List<Long> idList = BeanUtil.getBeanPropertyList(discountList, Long.class, "goodsid", true);
			for(SellSeat os:seatList){
				if(!idList.contains(os.getId())) return os;
			}
		}
		return null;
	}
	/**
	 * ��ȡû�е�ֵ�ļ۸���ߵ���λ
	 * @param num
	 * @return
	 */
	public static List<SellSeat> getMaxSellSeat(List<SellSeat> seatList, List<Discount> discountList, int count){
		Collections.sort(seatList, new MultiPropertyComparator(new String[]{"price"}, new boolean[]{false}));
		if(discountList.isEmpty()) return BeanUtil.getSubList(seatList, 0, count);
		List<Long> idList = BeanUtil.getBeanPropertyList(discountList, Long.class, "goodsid", true);
		for(SellSeat os:seatList){
			if(idList.contains(os.getId())) seatList.remove(os);
		}
		return BeanUtil.getSubList(seatList, 0, count);
	}


	/**
	 * ����Ҫ�õ�
	 * @return
	 */
	public static String getSeatText(List<SellSeat> seatList){
		String seatText = "";
		for(SellSeat seat:seatList) seatText += "," + seat.getSeatLabel();
		if(StringUtils.isBlank(seatText)) return "";
		return seatText.substring(1);
	}
	public static String getSeatText3(Collection<SellSeat> seatList){//�м۸�
		String seatText = "";
		for(SellSeat seat:seatList) seatText += "," + seat.getSeatLabel() + seat.getPrice() + "Ԫ";
		if(StringUtils.isBlank(seatText)) return "";
		return seatText.substring(1);
	}
	public static String getMacBuySeatText(List<SellSeat> seatList, OpenPlayItem opi){//�м۸�
		String seatText = "";
		for(SellSeat seat:seatList) seatText += "," + seat.getSeatLabel() + opi.getPrice() + "Ԫ";
		if(StringUtils.isBlank(seatText)) return "";
		return seatText.substring(1);
	}
	public static String getOrderSeatText(List<SellSeat> seatList, Integer price){//�м۸�
		String seatText = "";
		for(SellSeat seat: seatList) seatText += "," + seat.getSeatLabel() + price + "Ԫ";
		if(StringUtils.isBlank(seatText)) return "";
		return seatText.substring(1);
	}
	public static String getOrderSeatTextWithService(TicketOrder order, List<SellSeat> seatList){//�м۸�
		int costprice = order.getCostprice();
		int gewaprice = order.getUnitprice();
		String seatText = "";
		for(SellSeat seat: seatList){
			seatText += "," + seat.getSeatLabel() + costprice + "Ԫ";
			if(costprice < gewaprice){
				seatText += (gewaprice-costprice) + "Ԫ"; 
			}else{
				seatText += "0Ԫ"; 
			}
		}
		if(StringUtils.isBlank(seatText)) return "";
		return seatText.substring(1);
	}
	
	public static Discount getUsedPoint(List<Discount> discountList){
		for(Discount discount:discountList){
			if(PayConstant.DISCOUNT_TAG_POINT.equals(discount.getTag())) return discount;
		}
		return null;
	}
	public static BuyItem getItemByGoodsId(List<BuyItem> itemList, Long goodsid) {
		for(BuyItem item: itemList){
			if(item.getRelatedid().equals(goodsid)) return item;
		}
		return null;
	}
	public static void refreshItemfee(GewaOrder order, List<BuyItem> itemList) {
		int fee = 0;
		for(BuyItem item: itemList){
			fee += item.getDue();
		}
		order.setItemfee(fee);
	}
	
	public static void refreshOtherfee(GewaOrder order, Collection<OtherFeeDetail> otherFeeList){
		int otherfee = 0;
		for (OtherFeeDetail otherFeeDetail : otherFeeList) {
			otherfee += otherFeeDetail.getFee() * otherFeeDetail.getQuantity();
		}
		order.setOtherfee(otherfee);
	}
	
	
	public static List<BuyItem> getBuyList(List<BuyItem> itemList){
		List<BuyItem> result = new ArrayList<BuyItem>();
		for(BuyItem item : itemList){
			if(item.getUnitprice()>0) {
				result.add(item);
			}
		}
		return result;
	}
	public static ErrorCode<String> validDelGewaOrder(GewaOrder order, Timestamp playtime){
		if(StringUtils.equals(order.getRestatus(), GewaOrder.RESTATUS_DELETE)){
			return ErrorCode.getFailure("����״̬��ɾ����");
		}
		if(!order.isPaidSuccess()) {
			return ErrorCode.getFailure("����״̬����ȷ��");
		}
		if(playtime!=null){
			if(DateUtil.getMillTimestamp().before(DateUtil.addHour(playtime, 2))){
				return ErrorCode.getFailure("������Ч�ڷ����ڣ�����ɾ��");
			}
		}else {
			Map<String, String> descMap = VmUtils.readJsonToMap(order.getDescription2());
			String playDate = descMap.get("ʱ��");
			if(order instanceof SportOrder){
				playDate = descMap.get("Ԥ�Ƶ���ʱ��");
			}
			if(StringUtils.isBlank(playDate)){
				return ErrorCode.getFailure("������Ч�ڷ����ڣ�����ɾ��");
			}
			double dateNum =  DateUtil.getDiffHour(DateUtil.getCurFullTimestamp(),DateUtil.parseDate(playDate,"yyyy-MM-dd HH:mm"));
			if(dateNum<2){
				return ErrorCode.getFailure("������Ч�ڷ�Χ");
			}
		}
		return ErrorCode.SUCCESS;
	}
	public static String getPartnerUkey(GewaOrder order){
		String membername = order.getMembername();
		int idx = membername.lastIndexOf("@");
		if(idx > 0) return membername.substring(0, idx);
		return "";
	}

	public static ErrorCode<String> validGreetings(String greetings){
		if(StringUtils.isBlank(greetings)){
			return ErrorCode.getSuccessReturn("");
		}
		final String reg = "[a-zA-Z0-9\u4e00-\u9fa5\\pP]+";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(greetings);
		if(!matcher.matches()){
			return ErrorCode.getFailure("���ݲ��ܰ��������ַ���");
		}
		greetings = StringUtils.replace(greetings, "&", "");
		greetings = XSSFilter.filterSpecStr(greetings);
		greetings = XSSFilter.filterAttr(greetings);
		return ErrorCode.getSuccessReturn(greetings);
	}
}
