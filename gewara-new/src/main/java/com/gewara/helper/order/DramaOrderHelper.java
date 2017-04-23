/**
 * 
 */
package com.gewara.helper.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.gewara.model.drama.SellDramaSeat;
import com.gewara.model.pay.Discount;
import com.gewara.support.MultiPropertyComparator;
import com.gewara.util.BeanUtil;

/**
 * @author Administrator
 * 
 */
public class DramaOrderHelper {
	public static String getSeatText(Collection<SellDramaSeat> oseatList) {
		String seatText = "";
		for (SellDramaSeat oseat : oseatList) seatText += "," + oseat.getSeatLabel();
		if (StringUtils.isBlank(seatText)) return "";
		return seatText.substring(1);
	}

	public static String getSeatText3(Collection<SellDramaSeat> oseatList) {// �м۸�
		String seatText = "";
		for (SellDramaSeat oseat : oseatList)
			seatText += "," + oseat.getSeatLabel() + oseat.getPrice() + "Ԫ";
		if (StringUtils.isBlank(seatText))
			return "";
		return seatText.substring(1);
	}

	public static String getOrderSeatText(Integer price, Collection<SellDramaSeat> oseatList) {// �м۸�
		String seatText = "";
		for (SellDramaSeat oseat : oseatList)
			seatText += "," + oseat.getSeatLabel() + price + "Ԫ";
		if (StringUtils.isBlank(seatText))
			return "";
		return seatText.substring(1);
	}

	public static String getOrderSeatTextWithService(Integer gewaprice, Integer costprice, Collection<SellDramaSeat> oseatList) {// �м۸�
		String seatText = "";
		for (SellDramaSeat oseat : oseatList) {
			seatText += "," + oseat.getSeatLabel() + costprice + "Ԫ";
			if (costprice < gewaprice) {
				seatText += (gewaprice - costprice) + "Ԫ";
			} else {
				seatText += "0Ԫ";
			}
		}
		if (StringUtils.isBlank(seatText))
			return "";
		return seatText.substring(1);
	}
	public static String getDramaOrderSeatText(List<SellDramaSeat> oseatList) {// �м۸�
		String seatText = "";
		for (SellDramaSeat oseat : oseatList) {
			seatText += "," + oseat.getSeatLabel() + oseat.getPrice() + "Ԫ"+oseat.getTheatreprice()+"Ԫ";
		}
		if (StringUtils.isBlank(seatText)) return "";
		return seatText.substring(1);
	}
	
	public static SellDramaSeat getMaxSellSeat(List<SellDramaSeat> oseatList, List<Discount> discountList) {
		List<SellDramaSeat> result = new ArrayList<SellDramaSeat>(oseatList);
		Collections.sort(result, new MultiPropertyComparator<SellDramaSeat>(new String[]{"price"}, new boolean[]{false}));
		if (discountList.isEmpty()) return result.get(0);
		List<Long> idList = BeanUtil.getBeanPropertyList(discountList, Long.class, "goodsid", true);
		for (SellDramaSeat os : oseatList) {
			if (!idList.contains(os.getId()))
				return os;
		}
		return null;
	}
}
