/**
 * 
 */
package com.gewara.service.member;

import java.util.List;

import com.gewara.model.user.FavoriteTag;


/**
 *  @function ��Ȥ��ǩService
 * 	@author bob.hu
 *	@date	2011-02-22 18:12:14
 */
public interface FavoriteTagService {
 	/**
	 * �����ѯ
	 * */
	List<FavoriteTag> getRandomFavorList(int count);
	
	/**
	 *  ����tag ����FavoriteTag.count++ 
	 * */
	void updateFavoriteTagCount(String tag);
}
