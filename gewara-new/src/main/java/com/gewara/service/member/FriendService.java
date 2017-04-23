/**
 * 
 */
package com.gewara.service.member;

import java.util.List;
import java.util.Map;

import com.gewara.model.user.Friend;
import com.gewara.model.user.FriendInfo;
import com.gewara.model.user.HiddenMember;
import com.gewara.model.user.Member;
/**
 * @author hxs(ncng_2006@hotmail.com)
 * @since Jan 27, 2010 12:18:48 PM
 */
public interface FriendService {
	/**
	 * ����memberid��ѯ����Id
	 */
	List<Long> getFriendIdList(Long memberid, int from, int maxnum);
	/**
	 * ��������ȥ��������
	 * @param memberid
	 * @return
	 */
	Integer getFriendCount(Long memberid);
	/**
	 * ����memberfrom,memberto��ѯ�Ƿ��Ѿ����ͼӺú�������
	 */
	boolean isInvitedFriend(Long memberidfrom, Long memberidto);
	/**
	 * ����memberfrom,memberto��ѯ�Ƿ��Ѿ�����
	 */
	boolean isFriend(Long memberidfrom, Long memberidto);
	
	/**
	 * ����memberid��ѯ������id�б�
	 * @return
	 */
	List<Friend> getFriendList(Long memberid, int from, int manxnum);

	/**
	 * ����memberid��commuidɾ��Ȧ�ӳ�Ա
	 */
	void deleteCommueMember(Long memberid,Long commuid);
	/**
	 * �жϵ�ǰ�û��Ƿ���Ȩ�޲鿴��ǰ���ѵ���Ϣ
	 * @return
	 */
	Map isPrivate(Long memberid);
	Integer getNotJoinCommuFriendCount(Long memberid, Long commuid);
	List<Long> getNotJoinCommuFriendIdList(Long memberid, Long commuid, int from, int maxnum);
	/**
	 * ��ѯ��Ӻ���
	 * @param memberid
	 * @return
	 */
	List<HiddenMember> getHiddenMemberListByMemberid(Long memberid);
	/**
	 * ����memberid, addmemberid��ѯfriendInfoList
	 * @param memberid
	 * @return
	 */
	List<FriendInfo> getFriendInfoListByAddMemberidAndMemberid(Long addmemberid, Long memberid);
	
	/**
	 * �ж��û�ͨ��email��msg������û��Ƿ��Ѿ���¼��
	 */
	boolean isExistsEmail(Long memberid,String email);
	
	/**
	 * ͨ���û��ǳƼ���û��Ƿ����
	 */
	Member checkUserName(String nickname);

	void deleteFriend(Long memberid1, Long memberid2);
	List<Member> getFriendMemberList(Long memberid, int from, int maxnum);
}
