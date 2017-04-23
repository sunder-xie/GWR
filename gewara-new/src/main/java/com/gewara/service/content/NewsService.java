package com.gewara.service.content;

import java.sql.Timestamp;
import java.util.List;

import com.gewara.model.content.News;
import com.gewara.model.content.NewsPage;

public interface NewsService {
	//3. ����
	List<News> getCurrentNewsByTag(String citycode, String tag, String newstype, final int from, final int num);
	/**
	* ĳ�����������б�
	* @param tag (value1,value2,value3)
	* @param from
	* @param num
	* @return
	*/
	List<News> getNewsListByTag(String citycode, String tag, String newstype, String searchKey, Timestamp addtime, String order, final int from, final int num);
	/**
	 * ĳ��������������
	 * @param tag (value1,value2,value3)
	 * @return
	 */
	Integer getNewsCountByTag(String citycode, String tag, String newstype, String searchKey);
	/**
	 * ĳ����������ţ����磺�������ص�����
	 * @param tag
	 * @param relatedid
	 * @return
	 */
	List<News> getNewsByRelatedidAndTag(String tag,Long relatedid, int from, int maxnum);
	/**
	 * ĳ��������ص����ţ����磺�������ص��������
	 * @param id
	 * @param tag
	 * @param category
	 * @return
	 */
	List<News> getNewsListByTagAndCategory(String citycode, String tag,String newslabel, int from, int maxnum);
	/**
	 * �õ����ŵĵڼ�ҳ����
	 * @param nid
	 * @param pageno
	 * @return
	 */
	NewsPage getNewsPageByNewsidAndPageno(Long nid,Integer pageno);
	/**
	 * �������ŵ�id�õ����ķ�ҳ
	 * @param newsid
	 * @return
	 */
	List<NewsPage> getNewsPageListByNewsid(Long newsid);
	List<News> getNewsListByTagAndRelatedId(String citycode, String tag, Long relatedid, String flag, String... type);
	List<News> getNewsList(String citycode, String tag, Long relatedid, String newstype, String title, int from, int maxnum);
	List<News> getNewsList(String citycode, String tag, Long relatedid, String newstype, String title, String order, boolean asc, int from, int maxnum);
	//TODO ???? 2012-10-12 ��ѯ������List�������Ʋ���Ӧ
	Integer getNewsCount(String citycode, String tag, String newstype, Long relatedid, String title);
	News getNextNews(String tag, Long nid);
	List<News> getNewsList(String citycode, String tag, Long relatedid, String newstype, int from, int maxnum);
	String validateNews(Long newsid);
	/**
	 * ����tag, newstype��ѯ�����б�
	 * @param tag
	 * @param newstype
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<News> getNewsListByNewstype(String citycode, String tag, Long relatedid, String[] newstype, int from, int maxnum);
	/**
	 * ����tag, newstype��ѯ��������
	 * @param tag
	 * @param newstype
	 * @return
	 */
	Integer getNewsCountByNewstype(String citycode, String tag, Long relatedid, String[] newstype);
	
	/**
	 * ��ѯ��Ӱ��Ϣ
	 * @param citycode
	 * @param tag
	 * @param relatedid
	 * @param category
	 * @param categoryid
	 * @param order
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<News> getNewsList(String citycode, String tag, Long relatedid, String category, Long categoryid, String order, int from, int maxnum);
	void updateTips(Long nid);
}
