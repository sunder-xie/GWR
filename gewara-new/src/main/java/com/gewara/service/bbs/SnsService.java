package com.gewara.service.bbs;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.model.bbs.BlackMember;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
/**
 * ��̨�û�����
 * @author liushusong
 *
 */
public interface SnsService {
	/**
	 * �����û����ƣ��û���ţ��ֻ��Ų�ѯ�û���Ϣ
	 */
	List<Member> searchMember(Long memberid, String nickname,String mobile,String email, int from,int maxnum);
	/**
	 * �����û����ƣ�ǩ��������ѯ�û���Ϣ
	 */
	List<Map> getMemberListByUpdatetime(Timestamp starttime, Timestamp endtime, int from, int maxnum);
	/**
	 * �����û����ƣ�ǩ��������ѯ�û���Ϣ������
	 */
	Integer getMemberCountByUpdatetime(Timestamp starttime, Timestamp endtime);
	/**
	 * �޸��û�����,�����û���¼
	 */
	void updateMemberPasswordAndDelete(Member m);
	
	/**
	 * �ж��û��Ƿ��ѱ����������
	 */
	BlackMember isJoinBlackMember(Long memberid);
	
	/**
	 * �����û����ƣ��û���ţ��ֻ��Ų�ѯ�û�����
	 */
	Integer searchMemberCount(Long memberid,String nickname,String mobile,String email);
	
	/**
	 * ��ѯȫ������ֵ
	 */
	Integer getSumExpValue(Integer startExp,Integer endExp);
	/**
	 * ����ʱ��Σ�����ֵ�β�ѯ�û�����ֵ��Ϣ
	 */
	List<MemberInfo> getMemberExpValueList(Integer startExp,Integer endExp,int from,int maxnum);
	Integer getMemberExpValueCount(Integer startExp,Integer endExp);
}
