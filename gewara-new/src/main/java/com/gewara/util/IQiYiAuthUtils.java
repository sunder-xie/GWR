package com.gewara.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class IQiYiAuthUtils {
	public static final String API_KEY = "7cef6275ed934b4fb3483d8b01cd0222";
	/** ר����Ϣ�ӿ�*/
	public static final String URL_IQIYI_ALBUM = "http://expand.video.iqiyi.com/api/album/info.json";
	/** ר�������ӿ�*/
	public static final String URL_IQIYI_SEARCH = "http://expand.video.iqiyi.com/api/search/list.json";
	/** ר���б�ӿ�*/
	public static final String URL_IQIYI_ALBUM_LIST = "http://expand.video.iqiyi.com/api/album/list.json";
	
	/** ��Ƶ��Ϣ�ӿ�*/
	public static final String URL_IQIYI_VEDIO = "http://expand.video.iqiyi.com/api/video/info.json";
	/** ���ݸ���ʱ��β�ѯ��Ƶ��Ϣ�ӿ�*/
	public static final String URL_IQIYI_VEDIO_LIST = "http://expand.video.iqiyi.com/api/video/list.json";
	/**
	 Ƶ���б�
	 */
	public static final String URL_IQIYI_CATEGORY_LIST = "http://expand.video.iqiyi.com/api/category/list.json";
	/**����ʱ���ȡר���б�*/
	public static final String URL_IQIYI_ALBUM_LIST_DATE = "http://expand.video.iqiyi.com/api/album/udlist.json";
	/**���а� */
	public static final String URL_IQIYI_TOP = "http://expand.video.iqiyi.com/api/top/list.json";
	/** ��Դ�б�*/
	public static final String URL_IQIYI_SOURCE_LIST = "http://expand.video.iqiyi.com/api/s/list.json";
	
	
	/**
	 * Ƶ���б�
	 * @return
	 */
	public static String getCategoryList(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey",API_KEY);
		HttpResult result = HttpUtils.getUrlAsString(URL_IQIYI_CATEGORY_LIST, params);
		if(result.isSuccess()){
			return result.getResponse();
		}
		return null;
	}
	/**
	 * ר������
	 * @param keywords �ؼ���
	 * @param pageNo ��ʼҳ  
	 * @param pageSize ����
	 * @return
	 */
	public static String searchAlbum(String keywords,String threeCategory,int pageNo,int pageSize,String categoryIds){
		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey",API_KEY);
		params.put("keyWord",keywords);
		params.put("pageNo", pageNo + "");
		params.put("pageSize", pageSize + "");
		if(StringUtils.isNotBlank(threeCategory)){
			params.put("threeCategory", threeCategory);
		}
		params.put("categoryIds",categoryIds);
		HttpResult result = HttpUtils.getUrlAsString(URL_IQIYI_SEARCH, params);
		if(result.isSuccess()){
			return result.getResponse();
		}
		return null;
	}
	public static String getAlbumListByDate(Date startTime,Date endTime,int pageNo,int pageSize,String status,String categoryId){
		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey",API_KEY);
		params.put("startTime",DateUtil.format(startTime, "yyyyMMddHHmmss"));
		params.put("endTime",DateUtil.format(endTime, "yyyyMMddHHmmss"));
		params.put("pageNo",pageNo + "");
		params.put("pageSize",pageSize + "");
		params.put("status",status);
		params.put("categoryId",categoryId);
		HttpResult result = HttpUtils.getUrlAsString(URL_IQIYI_ALBUM_LIST_DATE, params);
		if(result.isSuccess()){
			return result.getResponse();
		}
		return null;
	}
	/**
	 * ��ȡר���б�
	 * @param keywords
	 * @param categoryId
	 * @param scode
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static String getAlbumList(String keywords,String categoryId,String scode,int pageNo,int pageSize){
		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey",API_KEY);
		params.put("keyWord",keywords);
		params.put("categoryId",categoryId);
		params.put("scode",scode);
		params.put("pageNo", pageNo + "");
		params.put("pageSize", pageSize + "");
		HttpResult result = HttpUtils.getUrlAsString(URL_IQIYI_ALBUM_LIST, params);
		if(result.isSuccess()){
			return result.getResponse();
		}
		return null;
	}
	/**
	 * ר������
	 * @param albumId ר��id
	 * @return
	 */
	public static String getAlbumDetail(String albumId){
		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey",API_KEY);
		params.put("albumId", albumId);
		HttpResult result = HttpUtils.getUrlAsString(URL_IQIYI_ALBUM, params);
		if(result.isSuccess()){
			return result.getResponse();
		}
		return null;
	}
	/**
	 * ��Ƶ��Ϣ
	 * @param tvId ��Ƶid
	 * @return
	 */
	public static String getVedioInfo(String tvId){
		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey",API_KEY);
		params.put("tvId", tvId);
		HttpResult result = HttpUtils.getUrlAsString(URL_IQIYI_VEDIO, params);
		if(result.isSuccess()){
			return result.getResponse();
		}
		return null;
	}
	/**
	 * ����ʱ���ѯ��Ƶ�б�
	 * @param startTime
	 * @param endTime
	 * @param pageNo
	 * @param pageSize
	 * @param status
	 * @param categoryId
	 * @return
	 */
	public static String getVideoList(Date startTime,Date endTime,int pageNo,int pageSize,String status,String categoryId){
		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey",API_KEY);
		params.put("startTime",DateUtil.format(startTime, "yyyyMMddHHmmss"));
		params.put("endTime",DateUtil.format(endTime, "yyyyMMddHHmmss"));
		params.put("pageNo",pageNo + "");
		params.put("pageSize",pageSize + "");
		params.put("status",status);
		params.put("categoryId",categoryId);
		HttpResult result = HttpUtils.getUrlAsString(URL_IQIYI_VEDIO_LIST, params);
		if(result.isSuccess()){
			return result.getResponse();
		}
		return null;
	}
	/**
	 * ���а�����
	 * @param topType ���֣� 0 ���գ� 1 ���ܣ� 2���£� 3���ܣ� 4
	 * @param categoryId
	 * @param limit
	 * @return
	 */
	public static String getTopList(String topType,String categoryId,int limit){
		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey",API_KEY);
		params.put("topType",topType);
		params.put("categoryId",categoryId);
		params.put("limit",limit + "");
		HttpResult result = HttpUtils.getUrlAsString(URL_IQIYI_TOP, params);
		if(result.isSuccess()){
			return result.getResponse();
		}
		return null;
	}
	/**
	 * ��ȡ��Դ�б�
	 * @param categoryId
	 * @param keyWord
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static String getScourceList(String categoryId,String keyWord,int pageNo,int pageSize){
		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey",API_KEY);
		params.put("keyWord",keyWord);
		params.put("categoryId",categoryId);
		params.put("pageNo",pageNo + "");
		params.put("pageSize",pageSize + "");
		HttpResult result = HttpUtils.getUrlAsString(URL_IQIYI_SOURCE_LIST, params);
		if(result.isSuccess()){
			return result.getResponse();
		}
		return null;
	}
	
}
