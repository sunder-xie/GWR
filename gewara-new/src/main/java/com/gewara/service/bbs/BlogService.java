package com.gewara.service.bbs;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.model.bbs.Accusation;
import com.gewara.model.bbs.Bkmember;
import com.gewara.model.bbs.BlackMember;
import com.gewara.model.bbs.BlogDataEveryDay;
import com.gewara.model.bbs.commu.Commu;
import com.gewara.model.user.Member;
import com.gewara.model.user.Treasure;
import com.gewara.untrans.monitor.ConfigTrigger;

/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public interface BlogService extends ConfigTrigger{
	/**
	 * @param tag
	 * @param relatedid
	 * @param role
	 * @param includeSub
	 * @param from
	 * @param maxnum
	 * @return ��ȡ���漰�Ӱ��н�ɫ����role�ĳ�Ա(includeSub=true,������Ӱ��)
	 */
	List<Bkmember> getBkmemberList(String tag, Long relatedid, int role,
			boolean includeSub, int from, int maxnum);
	/**
	 * ��ȡĳ��Ա��BkmemberList
	 * @param memberId
	 * @return
	 */
	List<Bkmember> getBkmemberListByMemberId(Long memberId);
	/**
	 * @param tag
	 * @param relatedid
	 * @return �����б�
	 */
	List<Bkmember> getBanzhuList(String tag, Long relatedid);
	/**
	 * ��ȡ�û��ڴ˰���Ȩ��
	 * @param tag
	 * @param relatedid
	 * @return
	 */
	int getMaxRights(String tag, Long relatedid, Long ownerId, Member member);
	/**
	 * ��Member�в���bkmember
	 * @param member
	 * @param tag
	 * @param relatedid
	 * @return
	 */
	Bkmember getBkmember(Member member, String tag, Long relatedid);
	/**
	 * ��ȡ�������б�
	 * @param memberId Ϊ���򷵻����к�����
	 * @return
	 */
	List<BlackMember> getBlackMemberList(Long memberId, int from, int maxnum);
	
    List<BlackMember> getBlackMemberList(Long memberId,String nickname, int from, int maxnum);
	/**
	 * �û��Ƿ��ں�������
	 * @param memberId
	 * @return
	 */
	boolean isBlackMember(Long memberId);
	/**
	 * �������е�����
	 */
	Integer getBlackMembertCount(String nickname);
	List<Commu> getCommunityList(String order,int from,int maxnum);
	Integer getCommunityCount();
	/**
	 * �ٱ�����
	 * @return
	 */
	Integer getAccusationCount();
	List<Accusation> getAccusationList(int from, int maxnum);
	String filterContentKey(String html);
	String filterAllKey(String html);
	boolean rebuildFilterKey();
	boolean rebuildManualFilterKey();
	boolean rebuildMemberRegisterFilterKey();
	
	void rebuildAllFilterKeys();
	/**************************************************************************
	 * ���ۿ�ʼ
	 **************************************************************************/
	/**
	 * ��ȡ��ע��Ϣ(�ҹ�ע���˵�ID)(WAP)
	 */
	List<Long> getTreasureRelatedidList(String citycode, Long memberid, String tag, String action);
	/**
	 * �Ƿ��ظ���ע���û�
	 */
	boolean isTreasureMember(Long fromMemberid, Long toMemberid);
	/**
	 * ȡ����ע��Ϣ
	 */
	boolean cancelTreasure(Long fromMemberid,Long toMemberid,String tag,String action);
	
	/**
	 * ��ѯ�û��ķ�˿��Ϣ
	 */
	List<Long> getFanidListByMemberId(Long memberid,int from,int maxnum);
	/**
	 * ����memberid��ѯ��ע��Ϣ
	 */
	List<Treasure> getTreasureListByMemberId(Long memberId, String[] tag,String[] removieTag, Long relatedid, int from,int maxnum, String... action);
	Integer getTreasureCountByMemberId(Long memberId, String[] tag,String[] removieTag, String... action);
	/**
	 * ����memberid,relateid, tag��ѯtreasure
	 * @param memberid
	 * @param relateid
	 * @param tag
	 * @return
	 */
	Treasure getTreasure(Long memberid, Long relateid, String tag, String action);
	/**
	 * ��ѯ�ҵı�ǩ
	 * @param memberid
	 * @return
	 */
	List<Treasure> getTreasureListByMemberId(Long memberid, int from ,int maxnum);
	/**
	 * @param diaryid
	 * @return
	 */
	String getDiaryBody(long diaryid);
	/**
	 * @param diaryid
	 * @param body
	 */
	void saveDiaryBody(long diaryid, Timestamp updatetime, String body);
	List<Long> getTreasureListByMemberIdList(Long relatedid, String tag, int from, int maxnum, String action);
	boolean isNight();
	boolean allowAddContent(String flag, Long memberid);
	List<Long> getTreasureCinemaidList(String citycode, Long memberid, String action);
	
	void addBlogData(Long userid, String tag, Long relatedid);
	void saveOrUpdateBlogData(Long userid, String tag, Long relatedid, Map<String/*propertyname*/,Integer> keyValueMap);
	void saveOrUpdateBlogDateEveryDay(Long userid, String tag, Long relatedid, String blogtype, Date blogdate, int blogcount);
	
	List<Map> getDiaryMapList(Timestamp starttime, Timestamp endtime);
	
	BlogDataEveryDay getBlogDataEveryDay(String tag, Long relatedid, String blogtype, Date blogdate);
	
	List<Long> getIdListBlogDataByTag(String citycode, String tag, String searchName, String searchKey, boolean asc, String order, int from, int maxnum);
	Integer getIdCountBlogDataByTag(String citycode, String tag, String searchName, String searchKey);
	
	Integer getIdCountEveryDayByTag(String citycode, String tag, String searchName, String searchKey, String blogtype, Date startdate, Date enddate);
	List<Long> getIdListEveryDayByTag(String citycode, String tag, String searchName, String searchKey, String blogtype, Date startdate, Date enddate, int from, int maxnum);
	/**
	 * ����Ƿ�����û�
	 * @param member
	 * @return
	 */
	Integer isBadEgg(Member member);
}
