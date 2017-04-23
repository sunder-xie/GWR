package com.gewara.service.content;

import java.util.List;

import com.gewara.model.movie.SpecialActivity;



/**
 *  @function ר���б�
 * 	@author bob.hu
 *	@date	2011-05-31 15:12:08
 */
public interface SpecialActivityService {
	List<SpecialActivity> getSpecialActivityList(String status, String flag, String relatedid, int from, int maxnum);
	int getSpecialActivityCount(String status, String flag, String relatedid);	
	/**
	 * ����flag�ڷ�Χ�ڲ��һ��flag��Ӧ�Ĳ�ֹһ��
	 * @param status
	 * @param flag
	 * @param relatedid
	 * @param searchKey
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<SpecialActivity> getSpecialActivityList(String status, String flag, String relatedid, String searchKey, int from, int maxnum);
	int getSpecialActivityCount(String status, String flag, String relatedid, String searchKey);
}
