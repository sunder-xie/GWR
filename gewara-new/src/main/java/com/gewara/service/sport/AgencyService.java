package com.gewara.service.sport;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.gewara.model.acl.User;
import com.gewara.model.agency.Agency;
import com.gewara.model.agency.AgencyToVenue;
import com.gewara.model.agency.Curriculum;
import com.gewara.model.agency.TrainingGoods;
import com.gewara.model.sport.SportItem;
import com.gewara.support.ErrorCode;


public interface AgencyService {
	
	List<Agency> getAgencyList(String name, String citycode, String orderField, boolean asc, int from, int maxnum);
	int getAgencyCount(String name, String citycode);
	/**
	 * ���ݳ��б����ѯ��ѵ������goodssort˳������
	 * @param citycode		���б���
	 * @param tag				��ѵ��������
	 * @param relatedid		��ѵ����ID	
	 * @param category		��ѵ��Ŀ����
	 * @param categoryid		����ID
	 * @param isTovaltime	�Ƿ���Ч
	 * @param isGtZero		�Ƿ����0
	 * @param from			��ѯ��
	 * @param maxnum		�������
	 * @return
	 */
	List<TrainingGoods> getTrainingGoodsList(String citycode, String tag, Long relatedid, String category, Long categoryid, Long placeid, String order, boolean asc, boolean isTovaltime, int from, int maxnum);
	int getTrainingGoodsCount(String citycode, String tag, Long relatedid, String category, Long categoryid, Long placeid, boolean isTovaltime);
	List<TrainingGoods> getTrainingGoodsList(String citycode, Long relatedid, Long categoryid, String fitcrowd, String timetype, List<Long> sportIdList,
			Integer fromprice, Integer toprice, String searchKey, String order, boolean asc, int from, int maxnum);
	int getTrainingGoodsCount(String citycode, Long relatedid, Long categoryid, String fitcrowd, String timetype, List<Long> sportIdList,
			Integer fromprice, Integer toprice, String searchKey);
	ErrorCode<TrainingGoods> saveTrainingGoods(Long gid, String citycode, String goodsname, String tag, Long relatedid, String category, 
			Long categoryid, Long placeid, Timestamp fromvalidtime, Timestamp tovalidtime, String summary, String description, String fitcrowd,
			String timetype, String seotitle, String seodescription, Integer quantity, String showtime, Integer minquantity, User user);
	//�γ̱�
	List<Curriculum> getCurriculumList(Long relatedid, Date playDate);
	//�õ���ѵ��������ѵ��Ŀ
	List<SportItem> getAgencySportItemList(Long agencyId, String citycode);
	//��פ����
	List<AgencyToVenue> getATVList(Long agencyId, Long venueId); 
	void clearTrainingGoodsPreferential(TrainingGoods goods);
}
