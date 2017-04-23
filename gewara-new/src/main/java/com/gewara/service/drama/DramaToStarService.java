package com.gewara.service.drama;

import java.util.List;

import com.gewara.model.drama.Drama;
import com.gewara.model.drama.DramaStar;
import com.gewara.model.drama.DramaToStar;

/**
 *    @function �������� ��ϵ 
 * 	@author bob.hu
 *		@date	2010-12-08 10:16:18
 */
public interface DramaToStarService {

	/**
	 *	���滰�����ǹ�ϵ (starids Ϊ������ǵ�ID)
	 */
	void saveDramaToStar(Long dramaid, String starids);
	
	/**
	 *  ��ѯĳ�������������
	 */
	List<DramaStar> getDramaStarListByDramaid(Long dramaid, String starType, int from, int maxnum, String...notNullPropertys);
	
	/**
	 *  ��ѯĳ���ǹ����Ļ���
	 *  starid : ����ID
	 *  isCurrent: �Ƿ�������ӳ
	 */
	List<Drama> getDramaListByStarid(Long starid);
	List<Drama> getDramaListByStarid(Long starid, boolean isCurrent, int from, int maxnum);
	Integer getDramaCountByStarid(Long starid);
	Integer getDramaCountByStarid(Long starid, boolean isCurrent);

	/**
	 *	���ݻ���IDȡ�ù�����Star��  DramaToStar List
	 */
	List<DramaToStar> getDramaToStarListByDramaid(String type, Long dramaid, boolean isGtZero);
	Integer getStarCount(Long relatedid, Long starid);
}
