package com.gewara.untrans;

import java.sql.Timestamp;
import java.util.List;

import com.gewara.model.bbs.LinkShare;
import com.gewara.model.user.Member;
import com.gewara.model.user.ShareMember;



public interface ShareService {

	/**
	 * �����ݷ�������΢��,��Ѷ΢�������ݿ��ܰ��������ӣ���Ʊ����ӰӰ�����������������ţ����
	 */
	void sendMicroInfo(LinkShare linkShare);
	/**
	 * ���Ҫ�������Ϣ
	 */
	void sendShareInfo(String tag,Long tagid,Long memberid,String category);
	LinkShare addShareInfo(String tag,Long tagid,Long memberid,String category,String type);
	
	//�Զ����������
	void sendShareInfo(String tag, Long tagid, Long memberid, String content, String picUrl);
	void sendCustomInfo(LinkShare linkShare);
	LinkShare addShareInfo(String tag, Long tagid, Long memberid, String type, String content, String picUrl);
	//�õ����˹�ע�˵�����
	List<String> getSinaFriendList(Long memberid, int count);
	
	//�û���΢����Ϣ
	ShareMember getShareMemberByLoginname(String source, String loginname);
	
	//������Դ��ȡ����ͬ���û�
	List<ShareMember> getShareMemberByMemberid(List<String> source, Long memberid);
	void createShareMember(Member member, String source, String loginname, String token, String tokensecret, String expires);
	void updateShareMemberRights(ShareMember shareMember);
	
	/**
	 * ����������ѯ�����΢��
	 * 
	 * */
	List searchShareSinaHisList(Timestamp starttime,Timestamp endtime,String status,String shareType,int from,int maxNum);
	
	int searchShareCount(Timestamp starttime,Timestamp endtime,String status,String shareType);
}
