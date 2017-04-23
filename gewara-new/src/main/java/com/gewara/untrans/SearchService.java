/**  
 * @Project: shanghai
 * @Title: SearchService.java
 * @Package com.gewara.service
 * @author shenyanghong paul.wei2011@gmail.com
 * @date Aug 10, 2012 5:57:19 PM
 * @version V1.0  
 */

package com.gewara.untrans;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gewara.json.GewaSearchKey;
import com.gewara.model.BaseObject;
import com.gewara.model.common.BaseInfo;

/**
 * @ClassName SearchService
 * @Description վ����������
 * @author weihonglin pau.wei2011@gmail.com
 * @date Aug 10, 2012
 */

public interface SearchService {
	// Ĭ��ÿҳչʾ����
	static final int ROWS_PER_PAGE = 20;
	static final int TOP_SK_COUNT = 10;
	static final String CITY_CODE_ALL = "000000";
	static final String ROWS_COUNT = "count";
	static final String ROWS_SK_LIST = "skList";
	static final String ROWS_INFO = "info";
	static final String TOP_SK_LIST = "topSkList";
	static final String API_SEARCH_SAVESEARCHKEY = "/api/saveSearchkey.xhtml";
	static final String API_SEARCH_SEARCHKEY = "/api/searchKey.xhtml";
	static final String API_SEARCH_SEARCHKEY_NUM = "/api/searchKeyNum.xhtml";
	static final String API_SEARCH_TOPSEARCHKEY = "/api/topSearchKey.xhtml";

	/**
	 * s
	 * 
	 * @Method: searchKey
	 * @Description: ���ݹؼ����������������������Ϣ
	 * @param skey
	 * @param tag
	 * @param category
	 * @param pageNo
	 * @return Map<String,Object>
	 */
	Map<String, Object> searchKey(String ip,String citycode, String skey,String channel, String tag, String category, Integer pageNo);
	Map<String, Object> searchKey(String ip,String citycode,String skey, String channel,String tag, String category, Integer pageNo, Integer rowsPerPage);
	/**
	 * @Method: searchKey
	 * @Description: �Զ���ɣ�ƥ������ؼ�����ʾ
	 * @param tag
	 * @param skey
	 * @param maxnum
	 * @return Map<String,Object>
	 */
	Set<String> searchKey(String citycode,String channel,String tag,String category, String skey, int maxnum);

	/**
	 * @Method: getSearchLight
	 * @Description: ����չʾ���ݸ�����ʾ
	 * @param content
	 * @param skey
	 * @param length
	 * @return String
	 */
	Map<String, Object> getBeanSearchLight(Object bean, String skey);

	/**
	 * @Method: saveBatchSearchKey
	 * @Description: timenumʱ��֮������Ǩ�����ݣ���������ͬ��
	 * @return String
	 */
	String saveBatchSearchKey(Long timenum);

	/**
	 * @Method: saveBatchSearchKey
	 * @Description: �����������ݣ���������ͬ��
	 * @return String
	 */
	void saveSearchKeyList(List<GewaSearchKey> list);

	/**
	 * @Method: getTopSearchKeyList
	 * @Description: ��������
	 * @param count
	 * @return List<String>
	 */
	List<String> getTopSearchKeyList(Integer count);
	/**
	 * @Description: ���͸���ʵ������
	 * @param object
	 */
	void pushSearchKey(Object object);
	
	/** 
	* @Method: reBuildIndex 
	* @Description: �����µ����������ع�����
	* @param clazz
	* @return String
	*/
	
	<T extends BaseObject> int reBuildIndex(Class<T> clazz);
	
	/** 
	* @Method: isCurrentCity 
	* @Description: �жϼ��������Ƿ����ڵ�ǰ����
	* @param bean
	* @param citycode
	* @return boolean
	*/
	boolean isCurrentCity(BaseObject bean,String citycode);
	/** 
	* @Method: getSearchKey 
	* @Description: ����searchkey
	* @param baseInfo
	* @return String
	*/
	<T extends BaseInfo> String getSearchKey(T baseInfo);
	
	
	/** 
	* @Method: sortSK 
	* @Description: �ؼ��ֹ�������ҳ������map����
	* @param List<GewaSearchKey> list
	* @return Map
	*/
	List<GewaSearchKey> sortSK(List<GewaSearchKey> list);

	/**
	 * ����
	 * @param tag
	 * @param skey
	 * @param maxnum
	 * @return
	 */
	Set<String> getSearchKeyList(String tag, String skey, int maxnum);
}
