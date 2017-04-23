/**
 * 
 */
package com.gewara.service.member;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.gewara.model.acl.User;
import com.gewara.model.user.Jobs;
import com.gewara.model.user.JobsUp;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.model.user.MemberInfoMore;
import com.gewara.model.user.MemberUsefulAddress;
import com.gewara.model.user.OpenMember;
import com.gewara.model.user.TempMember;
import com.gewara.support.ErrorCode;

/**
 * @author hxs(ncng_2006@hotmail.com)
 * @since Jan 28, 2010 1:36:10 PM
 */
public interface MemberService {
	/**
	 * �����ǳ�ģ������
	 * @param nickname
	 * @return
	 */
	List<Member> searchMember(String nickname, int from, int maxnum);
	/**
	 * �����ǳ�ģ�����Ұ������û���
	 * @param nickname
	 * @return
	 */
	int searchMemberCount(String nickname);
	Member getMemberByNickname(String nickName);
	/**
	 * �ж��Ƿ���Emai���ǳ���ͬ���û�
	 * @param emailOrNicknameOrMobile
	 * @param memberId
	 * @return
	 */
	boolean isMemberExists(String emailOrNickname, Long memberId);
	/**
	 * ������Դ�ͼ����¼����ȡ�����ʻ�
	 * @param source
	 * @param loginname
	 * @return
	 */
	OpenMember getOpenMemberByLoginname(String source, String loginname);
	/**
	 * ����memberid��ȡ���������˻�
	 * @param source
	 * @param memberid
	 * @return
	 */
	OpenMember getOpenMemberByMemberid(String source, Long memberid);
	
	/**
	 *  MemberInfoMore - �����û�ID, ��ѯƥ��Ľ�������/��������
	 */
	List<MemberInfoMore> getMemberinfoMoreList(Long memberid, String tag);
	OpenMember createOpenMember(String citycode, String source, String shortSource, String loginname, String ip);
	OpenMember createOpenMemberWithNickname(String citycode, String source, String shortSource, String loginname, String nickname, String ip);
	ErrorCode<Member> regMember(String nickname, String email, String password, Long inviteid, String invitetype, String regfrom, String citycode, String ip);
	ErrorCode<Member> regMemberWithMobile(String nickname, String mobile, String password, String checkpass, Long inviteid, String invitetype, String regfrom, String citycode, String ip);
	ErrorCode<Member> regMemberWithMobile(String nickname, String mobile, String password, Long inviteid, String invitetype, String regfrom, String citycode, String ip);
	ErrorCode<Member> createMemberWithBindMobile(String mobile, String checkpass, String citycode, String ip);
	ErrorCode<Member> createMemberWithDrawMobile(String mobile, String checkpass, String citycode, String ip);
	
	/**
	 * @function Ⱥ��վ����, ��ѯʹ��
	 * @author bob.hu
	 *	@date	2011-04-29 14:14:40
	 */
	List<MemberInfo> getMemberInfoByOtherInfo(String dkey);
	/**
	 * ��������
	 */
	MemberInfo saveNewTask(Long memberid, String newtask);
	/**
	 * @function �û��ֻ���Ψһ�Լ��
	 */
	boolean isMemberMobileExists(String mobile); 
	
	Integer getInviteCountByMemberid(Long memberid, Timestamp startTime, Timestamp endTime);
	/****
	 *  δ������(������������, �����Ż���ѯ)
	 * */
	Integer getMemberNotReadMessageCount(Long memberid);
	/****
	 *  δ������(�ռ���)
	 * */
	Integer getMemberNotReadNormalMessageCount(Long memberid);
	/****
	 *  δ������(ϵͳ��Ϣ)
	 * */
	Integer getMemberNotReadSysMessageCount(Long memberid);
	/**
	 * @param memberidList
	 * @return Map(memerid, Map(id,nickname,headpic)
	 */
	Map<Long, Map> getCacheMemberInfoMap(Collection<Long> memberidList);
	Map<Long, String> getCacheHeadpicMap(Collection<Long> memberidList);
	Map getCacheMemberInfoMap(Long memberid);
	String getCacheHeadpicMap(Long memberid);

	/**
	 * ��ȡְλ��Ϣ
	 * @param member
	 */
	Jobs getMemberPosition(Integer exp);

	/**
	 * ��ȡ��һְλ��Ϣ
	 * @param exp
	 * @return
	 */
	Jobs getMemberNextPosition(Integer exp);
	/**
	 * ���Ӿ���ֵ
	 * @param exp
	 * @return
	 */
	void addExpForMember(Long memberId, Integer exp);
	void saveJobUp(Long memberId, Long jobsId, String position);

	/**
	 * ����֮·��ѯ
	 * @param memberId
	 * @return
	 */
	List<JobsUp> getJobsUpByMemberId(Long memberId);
	/**
	 * ͨ��emailȡ�û�
	 * @param email
	 * @return
	 */
	Member getMemberByEmail(String email);
	Map getMemberJobsInfo(Long memberid);
	ErrorCode<Member> doLoginByEmailOrMobile(String loginName, String plainPass);
	ErrorCode<String> getAndSetMemberEncode(Member member);
	Member getMemberByEncode(String memberEncode);
	void updateMemberByMemberEncode(String memberEncode, Member member);
	/**
	 * ��ȡ�û����õ�ַ
	 * @param memberid
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<MemberUsefulAddress> getMemberUsefulAddressByMeberid(Long memberid, int from, int maxnum);
	
	/**
	 * �����û����õ�ַ
	 */
	ErrorCode<MemberUsefulAddress> saveMemberUsefulAddress(Long id, Long memberid, String realname, String provincecode, String provincename,
			String citycode, String cityname, String countycode, String countyname, String address, String mobile, String postalcode, String IDcard);
	MemberInfo updateDanger(Long memberid);
	/**
	 * ���ֻ�
	 * @param member
	 * @param mobile
	 * @param checkpass
	 * @param ip
	 * @return
	 */
	ErrorCode bindMobile(Member member, String mobile, String checkpass, String remoteIp);
	/**
	 * ��̨�û��������ֻ�
	 * @param member
	 * @param mobile
	 * @param checkpass
	 * @param user
	 * @param remoteIp
	 * @return
	 */
	ErrorCode bindMobile(Member member, String mobile, String checkpass, User user, String remoteIp);
	/**
	 * �����ֻ���
	 * @param member
	 * @param newmobile
	 * @param checkpass
	 * @param remoteIp
	 * @return
	 */
	ErrorCode changeBind(Member member, String newmobile, String checkpass, String remoteIp);

	/**
	 * ����ֻ���
	 * @param member
	 * @param dynamicCode
	 * @param remoteIp
	 * @return
	 */
	ErrorCode unbindMobile(Member member, String checkpass, String remoteIp);
	/**
	 * ����Ա�����ֶ�����
	 * @param memberid
	 * @param user
	 * @param remoteIp
	 * @return
	 */
	ErrorCode unbindMobileByAdmin(Long memberid, User user, String remoteIp);
	/**
	 * �������û���¼
	 * @param citycode
	 * @param source
	 * @param shortSource
	 * @param loginname
	 * @param nickname
	 * @param ip
	 * @param userInfo
	 * @return
	 */
	OpenMember createOpenMemberWithBaseInfo(String citycode, String source, String loginname,String ip,Map<String, Object> userInfo);
	/**
	 * �������˺Ű󶨸������˺�
	 * @param citycode
	 * @param source
	 * @param shortSource
	 * @param loginname
	 * @param ip
	 * @return
	 */
	OpenMember openMbrBindGewaMbr(String source, String loginname,String ip,Long memberid);
	/**
	 * �ǲ��ܽ�������ֻ���
	 * @param member
	 * @return
	 */
	boolean canChangeMobile(Member member);
	OpenMember createOpenMemberByMember(Member member, String source, String loginname);
	
	/**
	 * �������û�ע��������ʺ�
	 * @param userid �������û�Ψһ��ʶ
	 * @param loginName ע��������ʺŵ����䣨�ֻ���
	 * @param nickname ע��������ʺ��û��ǳ�
	 * @param password ע��������ʺŵ�����
	 * @param citycode ����
	 * @param regForm ע����Դ���磺WEB��WAP��IOS��android��
	 * @param source ��������Դ���磺΢�ŵ�
	 * @param ip ע��������ʺŵ�IP��ַ
	 * @return ErrorCode<Member>
	 */
	public ErrorCode<Member> createMemberWithPartner(String userid, String loginName,  String nickname, String password, String citycode, 
			String regForm, String source, String ip);
	
	/**
	 * �����ֻ������ѯ�û�
	 * @param mobile
	 * @return
	 */
	Member getMemberByMobile(String mobile);
	
	/**
	 * Ϊ�绰�µ���̨���������û�
	 * @param mobile
	 * @return
	 */
	ErrorCode<Member> createWithMobile(String mobile, User user);
	/**
	 * �û������ڣ��ֻ��Ų����ڣ�����Ҫ��������
	 * @param mobile
	 * @param password
	 * @param flag
	 * @return
	 */
	ErrorCode<TempMember> createTempMember(String mobile, String password, String flag, String ip, Map<String, String> otherMap);
	/**
	 * �û���ע�ᣬδ���ֻ�����ʱ���ɰ�
	 * 1���Ѱ��ֻ��û�:
	 *    a)δ����TempMember����ֱ������һ��TempMember
	 *    b)������TempMember��ֱ��ʹ��ԭ����
	 * 2��δ���ֻ��û���
	 * @param mobile
	 * @param memberid
	 * @param flag
	 * @return
	 */
	ErrorCode<TempMember> createTempMemberBind(Member member, String mobile, String flag, String ip);
	/**
	 * ��TempMember������ʽע���˺�
	 * @param tm
	 * @return
	 */
	ErrorCode<Member> createMemberFromTempMember(TempMember tm);
	/**
	 * ��X�ֻ�״̬
	 * @param tm
	 * @return
	 */
	ErrorCode<Member> bindMobileFromTempMember(TempMember tm);
}
