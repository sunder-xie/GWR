package com.gewara.service.sport;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.model.sport.OpenTimeItem;
import com.gewara.model.sport.ProgramItemTime;
import com.gewara.model.sport.Sport;
import com.gewara.model.sport.Sport2Item;
import com.gewara.model.sport.SportItem;
import com.gewara.model.sport.SportItemPrice;
import com.gewara.model.sport.SportPrice;
import com.gewara.model.sport.SportPriceTable;
import com.gewara.model.sport.SportProfile;

/**
 * Movie,Sport,PlayTime Service
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public interface SportService{
	boolean updateSportFlag(Long sid, String flag);
	//�޸����Ŷ�
	void updateSportHotValue(Long sid,Integer hotvalue);
	//end. service method
	List<Sport> getHotSports(String citycode, String order, boolean isHot, int maxnum);
	//���ݳ���������Ȧ�õ��˶�����ID
	List<Long> getSportIdByCode(String citycode, String countycode, String indexareacode, int from, int maxnum);
	int getSportCountByCode(String citycode, String countycode, String indexareacode);
	List<SportPriceTable> getPricetableList(Long itemid, int from, int maxnum) ;
	int getPricetableCount();
	List<SportPriceTable> getRandomPricetableList(Long itemid,int maxnum);
	List<SportItem> getTopSportItemList();
	/**
	 * @return all parent id is not null
	 */
	List<SportItem> getAllSportItem();
	List<SportItem> getSubSportItemList(Long sportItemId, String type);
	List<SportItem> getHotSportItemList(int from, int maxnum);
	/**
	 * �Ƽ��˶���Ŀ
	 * @param sportId
	 * @param value
	 * @return
	 */
	boolean updateSportItemFlagValue(Long sid, String value);
	List<SportItem> getCommendSportItemList(int from, int maxnum);
	/**
	 * List<Map(countycode,num)>
	 * @param id
	 * @return
	 */
	List<Map> getCountyAndSportNum(Long id);
	/**
	 * ͨ������ȡ�м۸��ĳ����б�
	 * @param countycode
	 * @return List<Sport>
	 */
	List<Map> getSportListByCountyCode(Long id, String countycode);
	List<SportPriceTable> getPriceTableListBySportId(Long sportid);
	/**
	 * ���ݳ���sportid,itemid��ѯ
	 */
	SportPriceTable getSportPriceTable(Long sportid, Long itemid);
	/**
	 * ����sprotpriceTableid��ѯ��Ŀ�۸�
	 */
	List<SportPrice> getSportPriceList(Long priceTableid);
	
	/**
	 *  ���� itemName ƥ�� itemList
	 */
	List<SportItem> getSportlistLikeItemname(String key);
	List<SportItem> getSportItemListBySportId(Long sportId, String booking);
	List<Sport2Item> getSport2ItemListBySportId(Long sportId);
	Sport2Item getSport2Item(Long sportId, Long itemId);
	List<SportPrice> getPriceList(Long priceTableId);
	
	SportPrice getSportPriceByPriceTableId(Long priceTableId);
	
	List<Sport> getSportByItemAndClickTimes(Long itemdId,int from,int max);
	
	List<Sport> getBookingEqOpenSport(String citycode,String bookingstatus);
	
	List<Map> getMaxHourAndMinHour(Long sportid);
	
	List<SportItem> getSportItemBySportId(Long sportid);
	
	List<Sport> getCurSportList(String orderField);
	
	//�õ������С��ƽ���ļ۸�
	Map<String,Integer> getSportPrice(Long sportid, Long itemid);
	
	/**
	 * �õ�ĳһ����������¼۸�
	 */
	Map<String,Integer> getSportPriceByOtt(Long sportid, Long itemid, Long ottid);
	//�õ�������ĿID
	List<Long> getBookingItemList(Long itemid, String citycode);
	//�õ�������Ŀ�ĳ���ID
	List<Long> getBookingSportIdList(Long itemid, String citycode);

	List<Long> getNearSportList(double maxLd, double minLd, double maxLa,double minLa,Long itemid, String citycode);
	List<Long> getSportList(String type, String name,String countycode,Long subwayid,Long itemid);
	List<Long> getSportListByOrder(Long memberid, int from, int maxnum);
	List<Long> getMemberListByOrder(Long sportid, Timestamp addtime, int from, int maxnum);
	List<Long> getSportProfileListByItemId(List<Long> idList,Long itemId); 
	List<SportPrice> getPriceList(Long sportid, Long itemid);
	List<ProgramItemTime> getProgramItemTimeList(Long sportid, Long itemid); 
	
	
	List<Long> getSportBySportItem(Long itemid, String citycode);
	Integer getSportCountBySportItem(Long itemid, String citycode, boolean isopen);
	/**
	 * ��ѯ�۸��
	 */
	SportItemPrice getSportItemPriceBySportIdAndItemId(Long sportid, Long itemid, Integer week);
	List<SportItemPrice> getSportItemPriceListBySportIdAndItemId(Long sportid, Long itemid);
	List<Sport> getSportList(String flag, String key, String citycode, int from, int maxnum);
	Integer getSportCount(String flag, String key, String citycode);
	List<SportProfile> getSportProfileList(String key, String citycode, Long siId, String company, boolean isBooking, int from, int maxnum);
	Integer getSportProfileCount(String key, String citycode, Long siId, String company, boolean isBooking);
	
	/**
	 * ��ѯ�Ƿ��Ԥ��
	 */
	List<String> getBookingSportItemList();
	//�˶���Ŀ�б�ҳ
	List<SportItem> getSportItemList(String itemname, Long parentid, String type, String order, int from, int maxnum);
	//�˶���Ŀ�б�ҳ����
	int getSportItemCount(String itemname, Long parentid, String type);
	
	/**
	 * @param orderId
	 * @return
	 * ��ѯ����������OpenPlayItem
	 */
	List<OpenTimeItem> getOrderPlayItemList(Long orderId);
	/**
	 * ���¼۸�
	 * @param sportid
	 * @param itemid
	 * @param minprice
	 * @param avgprice
	 * @param maxprice
	 */
	void updateSportItemPrice(Long sportid, Long itemid, Integer minprice, Integer avgprice, Integer maxprice);
}
