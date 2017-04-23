package com.gewara.service.member;

import java.util.List;

import com.gewara.model.user.Treasure;


/**
 * Service for News��DiscountInfo��Bulletin
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public interface TreasureService {
	/**
	 * �ж�Treasure�Ƿ���ڣ�����memberId��tag��relatedid��ѯ��
	 * @param treasure
	 * @return
	 */
	boolean isExistsTreasure(Treasure treasure);
	/**
	 * ����ĳ�û����ղ�
	 * @param memberId
	 * @param string
	 * @return
	 */
	List<Long> getTreasureIdList(Long memberId, String tag, String action);
	List<Long> getTreasureIdList(Long memberId, String tag, String action, int from, int maxnum);
	/**
	 * ��ȡͬһ�Ķ�������
	 * @param action
	 * @param tag
	 * @param relatedid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Long> getTreasureMemberList(String action, String tag, Long relatedid, int from, int maxnum);
	List<Long> getTreasureMemberList(String action, String tag, Long relatedid, String order, boolean asc, int from, int maxnum);
	List<Treasure> getTreasureList(String action, Long memberid, String tag, Long relatedid, int from, int maxnum);
	Treasure getTreasureByTagMemberidRelatedid(String tag, Long memberid, Long relatedid, String action);
	void saveTreasure(Long memberid, String tag, Long relatedid, String action);
}
