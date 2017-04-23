package com.gewara.untrans;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.json.MovieTrendsCount;
import com.gewara.model.bbs.MemberMark;

/**
 * Ԥ�⼴����ӳ��Ӱ��δ���µ�����Ļ�����������
 */
public interface MovieTrendsService {

	/**
	 * ��ѯָ����������ӰƬ��ͳ�����ݣ�����Ϊnull����new ArrayList<Map>()��
	 * 
	 * @param queryDate
	 *            ��ѯ����
	 * @return List<Map>
	 */
	public List<Map> queryMovieTrendsCountByDate(String queryDate);

	/**
	 * ����ӰƬID�����������ѯͳ�Ƶ����ݣ�����Ϊ�շ���new ArrayList
	 * 
	 * @param movieid
	 *            ӰƬID
	 * @param startDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��������
	 * @return List<MovieTrendsCount>
	 */
	public List<MovieTrendsCount> getMovieTrendsListByMovieIdDate(Long movieid, Date startDate, Date endDate);

	/**
	 * ��ȡĬ�ϲ�ѯ��ʱ�䣬���queryDate��Ϊ�գ���queryDate�����Ϊ����ȡMovieTrendsCount�����������һ��
	 * 
	 * @param queryDate
	 *            ����
	 * @return String Ĭ�ϲ�ѯʱ��
	 */
	public String getDefaultQueryDate(Date queryDate);

	/**
	 * ����ӰƬID��flag��Y����Ʊ|N��δ��Ʊ����ȡӰƬ��������
	 * 
	 * @param movieid
	 *            ӰƬID
	 * @param flag
	 *            Y:��Ʊ|N��δ��Ʊ
	 * @param queryDate
	 *            ��ѯ����
	 * @return Long
	 */
	public Long getMemberMarkCountByMovieid(Long movieid, String flag, Date queryDate);

	/**
	 * ����ӰƬID��flag��Y����Ʊ|N��δ��Ʊ����ȡ������ϸ
	 * 
	 * @param movieid
	 *            ӰƬID
	 * @param flag
	 *            Y:��Ʊ|N��δ��Ʊ
	 * @param queryDate
	 *            ��ѯ����
	 * @param from
	 *            ��ʼ����
	 * @param maxnum
	 *            ����¼��
	 * @return List<MemberMark>
	 */
	public List<MemberMark> getMemberMarkListByMovieid(Long movieid, String flag, Date queryDate, int from, int maxnum);

	/**
	 * ��ʱ����ӰƬͳ������
	 */
	public void saveMovieTrendsCount();

}
