/**
 * 
 */
package com.gewara.service.bbs;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.model.bbs.Diary;
import com.gewara.model.bbs.DiaryBase;
import com.gewara.model.bbs.commu.Commu;
import com.gewara.model.bbs.commu.CommuCard;
import com.gewara.model.bbs.commu.CommuManage;
import com.gewara.model.bbs.commu.CommuMember;
import com.gewara.model.bbs.commu.VisitCommuRecord;
import com.gewara.model.user.Album;
import com.gewara.model.user.Member;


/**
 * @author chenhao(sky_stefanie@hotmail.com)
 */
public interface CommuService {
	/**
	 * ����Ȧ��id��ѯȦ��������Ϣ
	 */
	<T extends DiaryBase> List<T> getCommuDiaryListById(Class<T> clazz, Long id, String[] type,Long commuTopicId, int from, int maxnum);
	
	/**
	 *  ��������������ѯ����
	 */
	<T extends DiaryBase> List<T> getCommuDiaryListBySearch(Class<T> clazz, Long id, String type, Long commuTopicId, Date fromDate, Integer flag, String text, int from, int maxnum);
	<T extends DiaryBase> Integer getCommuDiaryCountBySearch(Class<T> clazz, Long id,String type,Long commuTopicId, Date fromDate, Integer flag, String text);
	
	/**
	 * ����Ȧ��id��ѯȦ��������Ϣ����
	 */
	<T extends DiaryBase> Integer getCommuDiaryCount(Class<T> clazz, Long id,String[] type,Long commuTopicId);
	
	/**
	 * ����Ȧ��id��ѯȦ����Ա��Ϣ
	 */
	List<CommuMember> getCommuMemberById(Long id, Long adminid, Long subadminid, String blackmember, int from, int maxnum);
	/**
	 * ��ȡ���¼���Ȧ�ӵ��û�
	 */
	List<CommuMember> getCommuMemberByCommu(String tag, Long relatedid, Long adminid, Long subadminid, String blackmember, int from, int maxnum);
	/**
	 * ����Ȧ��id��ѯȦ����Ա��Ϣ����
	 */
	Integer getCommumemberCount(Long id, Long adminid);
 
	/**
	 * ���ݵ�ǰ�û�id��ѯ�����������Ȧ�ӵĻ�����Ϣ
	 */
	List<Diary> getAllCommuDiaryById(Long id, int from, int maxnum);
	
	

	/**
	 * ���ݵ�ǰ�û�id��ѯ�����������Ȧ�ӵĻ�����Ϣ����
	 */
	Integer getAllCommuDiaryCountById(Long id);
	
	
	
	/**
	 * ���ݵ�ǰ�û�id��ѯ�����������Ȧ�ӵĳ�Ա��Ϣ
	 */
	List<Member> getAllCommuMemberById(Long id, int from, int maxnum);
	
	/**
	 * ���ݵ�ǰ�û�id��ѯ�����������Ȧ�ӵĳ�Ա��Ϣ����
	 */
	Integer getAllCommuMemberCountById(Long id);
	
	
	
	/**
	 * �����ѯȦ�Ӵ������Ϣ
	 */
	List<Map> getCommuType();
	
	/**
	 * ����Ȧ�Ӵ�����tag��ѯȦ��С����������Ϣ
	 */
	List<Map> getCommuSmallByTag(String tag);
	
	

	/**
	 * �ж��Ƿ���Ȧ�ӳ�Ա
	 */
	boolean isCommuMember(Long commuid, Long memberid);
	
	List<Commu> getCommunityListByMemberid(Long memberid, int from, int maxnum);

	List<Commu> getCommunityListByHotvalue(String tag, Long relatedid, boolean memberNum, Long hotvalue, int from, int maxnum);

	List<Commu> getCommunityListByHotvalue(Long hotvalue, int from, int maxnum);
	/**
	 * ����commuid��ѯ���������Ȧ�ӵĳ�Աmemberid
	 */
	List<Long> getCommuMemberIdListByCommuId(Long commuid);
	/**
	 * ����Ȧ����Ϣ
	 */
	List<Commu> getHotCommuList(int from, int maxnum);
	/**
	 * ���ݵ�ǰ�û���ѯ���������Ȧ����Ϣ
	 */
	List<Commu> getCommuListByMemberId(Long memberid, int from, int maxnum);
	
	/**
	 * ���ݵ�ǰ�û���ѯ���������Ȧ����Ϣ����
	 */
	Integer getCommuCountByMemberId(Long memberid);
	
	/**
	 * ��ѯ��ǰȦ�ӵĳ�ԱҲϲ��ȥ��Ȧ����Ϣ
	 */
	List<Commu> getCommuMemberLoveToCommuList(Long commuid, int from, int maxnum);
	/**
	 * �����û�id��ѯ������Ȧ�ӵ�����б�
	 */
	List<Album> getJoinedCommuAlbumList(Long id, int from, int maxnum);
	/**
	 * �����û�id��ѯ������Ȧ�ӵ��������
	 */
	Integer getJoinedCommuAlbumCount(Long memberid);
	
	/**
	 * ����Ȧ��id��ѯȦ�������Ϣ
	 */
	List<Album> getCommuAlbumById(Long id, int from, int maxnum);
	/**
	 * ����Ȧ��id��ѯȦ���������
	 */
	Integer getCommuAlbumCountById(Long id);

	/**
	 *  ����Ȧ��ID ��ѯ��ǰȦ���ܹ���ͼƬ����
	 */
	Integer getPictureCountByCommuid(Long commuid);
	
	/**
	 * ����Ȧ��
	 */
	void joinCommuMember(Long memberid,Long commuid);
	
	/**
	 * �ж��û��Ƿ��Ѿ�����������Ҫ�����Ȧ��
	 */
	boolean isJoinCommuMember(Long memberid,Long commuid);
	
	/**
	 * ��ѯȦ����Ϣ
	 */
	List<Commu> getCommuBySearch(String tag, String citycode,Long smallcategoryid,String value,String sort, String countycode, int from, int maxnum);
	
	/**
	 * ��ѯȦ�ӵ�����
	 */
	Integer getCommuCountBySearch(String tag, String citycode,Long smallcategoryid,String value,String sort, String countycode);
	/**
	 * ��ѯ����Ȧ��
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Commu> getCommuList(int from, int maxnum);
	/**
	 * ����commuid,memberid��ѯCommuCard
	 * @param memberid
	 * @param commuid
	 * @return
	 */
	CommuCard getCommuCardByCommuidAndMemberid(Long memberid, Long commuid);
	/**
	 * ����memberid,commuid��ѯ��Ա����Ȧ�ӵĴ�������ʱ��
	 * @param memberid
	 * @param commuid
	 * @return
	 */
	VisitCommuRecord getVisitCommuRecordByCommuidAndMemberid(Long commuid, Long memberid);
	/**
	 * ����memberid,commuid��ѯȦ�ӳ�Ա
	 * @param memberid
	 * @param commuid
	 * @return
	 */
	CommuMember getCommuMemberByMemberidAndCommuid(Long memberid, Long commuid);
	
	/**
	 * �ж�Ȧ�������Ƿ��ظ�
	 */
	boolean isExistCommuName(Long commuid,String communame);
	/**
	 * ����memberid, date����ѯdateʱ���Ƿ��Ѿ�����Ȧ��
	 * @param memberid
	 * @param date
	 * @return
	 */
	boolean isHadVisitCommuByMemberidAndDate(Long memberid ,String date);
	
	/**
	 * ����Ȧ��relatedid��ѯȦ���б���Ȧ�ӳ�Ա����
	 * @param tag
	 * @param relatedid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Commu> getCommuListByTagAndRelatedid(String citycode, String tag, Long relatedid, int from, int maxnum);
	
	/**
	 *  ���� commuid,  ƥ�� CommuManage, ��ѯ״̬
	 *  ����ʱ Assert.notNull()
	 */
	String getCheckStatusByIDAndMemID(Long commuid);
	
	void initCommuRelate(List<Commu> commuList);
	
	/**
	 * �Ƿ���Ȧ�ӹ���Ա
	 * @param commuid
	 * @param memberid
	 * @return
	 */
	boolean isCommuAdminByMemberid(Long commuid, Long memberid);
	/**
	 * ����countycode��ѯȦ������
	 * @param countycode
	 * @return
	 */
	Integer getCommuCountByCountycode(String countycode);
	/**
	 * ����commuid��ѯȦ�ӻ�������
	 * @param commuid
	 * @return
	 */
	Integer getCommuDiaryCountByCommuid(Long commuid);
	
	/**
	 *  ����Ȧ��ID ���Ҷ�Ӧ��commuManage
	 */
	CommuManage getCommuManageByCommuid(Long commuid);
	
	/**
	 * @param memberid
	 * @param from
	 * @param maxnum
	 * @return Map<friendid, commu>
	 */
	Map<Long, Commu> getFriendCommuMap(Long memberid, int from, int maxnum);
	Integer getFriendCommuCount(Long memberid);

	/**
	 * ����hotvalue��ѯȦ������
	 * @param hotvalue
	 * @return
	 */
	Integer getCommunityCountByHotvalue(Long hotvalue);
	List<CommuMember> getCommuMemberListByMemberid(Long memberid, int from, int maxnum);
	List<CommuCard> getCommuCardListByMemberid(Long memberid, int from, int maxnum);
	
	/**
	 * ����ӵ�е�Ȧ��
	 * @param memberid
	 * @return
	 */
	List<Commu> getOwnerCommuList(Long memberid);
/**
	 * ��ѯmemberid�����Ȧ��
	 * @param memberid
	 * @return
	 */
	List<Commu> getManagedCommuList(Long memberid);

	/**
	 * �����Ȧ�ӵĳ�Ա��������ЩȦ�ӣ�
	 * @param commuid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<Commu> getAlikeCommuList(Long commuid, int from, int maxnum);
	/**
	 * @param tag
	 * @param from
	 * @param maxnum
	 * @return ��������ʱ������
	 */
	List<Commu> getCommunityListByTag(String tag, String order, int from, int maxnum);
	Integer getCommunityCountByTag(String tag);
	
	/**
	 * ��ѯȦ���б���������
	 */
	List<Commu> getCommuListOrderByProperty(String tag ,int from, int maxnum, String order);
	
	/**
	 * @param tag
	 * @param relatedId
	 * @param from
	 * @param maxnum
	 * @return ��������ʱ������
	 */
	List<Commu> getCommunityListByRelatedId(String tag, Long relatedid, int from, int maxnum);

	Integer getCommunityCountByRelatedId(String tag, Long relatedid);

	/**
	 * ����Ȧ�ӵĹ���
	 * @param communityList
	 */
	void initCommunityRelate(List<Commu> communityList);
	/**
	 * ���ݱ�ǩtag�õ�Ȧ�ӵ�id
	 * @param tag
	 * @return
	 */
	List<Long> getCommuIdByTag(String tag);

}
