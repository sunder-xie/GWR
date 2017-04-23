package com.gewara.service.drama;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.gewara.model.common.VersionCtl;
import com.gewara.model.draw.DrawActivity;
import com.gewara.model.draw.Prize;
import com.gewara.model.draw.WinnerInfo;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Festival;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.support.ErrorCode;

public interface DrawActivityService {
	/**
	 * ��ѯ�齱�
	 */
	List<DrawActivity> getDrawActivityList(String order, int from, int maxnum);
	/**
	 * ��ѯ�齱�����
	 */
	Integer getDrawActivityCount();
	
	/**
	 * ���ݳ齱�id��ѯ���û�Ľ�Ʒ��Ϣ
	 */
	List<Prize> getPrizeListByDid(Long did,String[] ptype);
	List<Prize> getAvailablePrizeList(Long did, int minleft);
	
	/**
	 * ���ݲ�ͬ���Ϣ����Ʒ���ͣ���ѯ����Ϣ
	 */
	List<WinnerInfo> getWinnerList(Long activityid,List<Long> prizeid, Timestamp startTime,Timestamp endTime, String tag,Long memberid,String mobile,String nickname,int from,int maxnum);
	Integer getMemberWinnerCount(Long memberid, Long activityid, Timestamp startTime,Timestamp endTime);
	/**
	 * ���ݲ�ͬ���Ϣ����Ʒ���ͣ���ѯ����Ϣ����
	 */
	Integer getWinnerCount(Long activityid, List<Long> prizeid, Timestamp startTime, Timestamp endTime, String tag, Long memberid, String mobile,String nickname);
	
	/**
	 * ���ݳ齱�id����Ʒid����ѯ�˽�Ʒ���ͳ�����
	 */
	Integer getSendOutPrizeCount(Long activityid,Long prizeid);
	
	/**
	 * �����ѯ��ȡ���ֽ�Ʒ������
	 */
	List<Map> getPrizeCountInfo(Long activityid,Timestamp startTime,Timestamp endTime);
	
	/**
	 * ��ѯ����齱��û�����
	 */
	Integer getJoinDrawActivityCount(Long activityid);
	Integer getCurDrawActivityNum(DrawActivity da, Long memberid, int totalChance);
	Integer getCurChanceNum(DrawActivity da, Long memberid);
	Integer getUsedChanceNum(DrawActivity da, Long memberid);
	Integer getExtraChanceNum(DrawActivity da, Long memberid);
	Prize runDrawPrize(DrawActivity da, VersionCtl vc, boolean scalper);
	//void updatePrize(Prize prize, VersionCtl vc);
	WinnerInfo addWinner(Prize prize, Long memberId, String nickname, String mobile, String ip);
	SMSRecord sendPrize(Prize prize, WinnerInfo winner, boolean sendsms);
	/**
	 * ����
	 * @param activityid
	 * @param mobile
	 * @return
	 */
	int getWinnerCountByMobile(Long activityid, String mobile);
	WinnerInfo getWinnerInfoByMemberid(Long activityid, Long memberid);
	List<WinnerInfo> getWinnerInfoByMemberid(Long activityid, Long memberid, int from, int maxnum);
	
	/**
	 * �����ֻ��Ų�ѯ����ֻ����ڵ�ǰ�Ƿ��Ѿ���ȡ���һ�ȯ
	 */
	boolean isSendPrize(String mobile,Long memberid,Long activityid,Timestamp gainPrizeTime);
	
	/**
	 * ��ѯ��ǰ�û��������������������
	 */
	Integer getInviteMemberCount(Long memberId, String invitetype, boolean validMobile, Timestamp startTime,Timestamp endTime);
	List<Long> getInviteMemberList(Long memberId, String invitetype, boolean validMobile, Timestamp starttime, Timestamp endtime);
	
	/**
	 * ��ѯ��ǰ�û���������������������¶������͵�ǰ�û����¶�����
	 */
	Integer getInviteOrderNum(DrawActivity da,Long memberid);
	
	//ErrorCode<WinnerInfo> baseClickDraw(DrawActivity da, VersionCtl mvc, boolean scalper, Member member, BuziCallback callback);
	/**
	 * �齱��������
	 */
	ErrorCode<WinnerInfo> baseClickDraw(DrawActivity da, VersionCtl mvc, boolean scalper, Member member);
	ErrorCode<WinnerInfo> baseClickDraw(DrawActivity da, VersionCtl mvc, boolean scalper, Long memberId, String nickname, String mobile);
	ErrorCode<WinnerInfo> baseClickDraw(DrawActivity da, VersionCtl mvc, boolean scalper, Long memberId, String nickname, String mobile, String ip);
	VersionCtl gainMemberVc(String memberId);
	VersionCtl gainPrizeVc(Long prizeId);
	ErrorCode sendNewTaskCardPrize(MemberInfo info, Member member);
	
	/**
	 * ��ȡ���պ�������Ƴ齱��
	 */
	Prize clickPointPrize(Festival festival, Member member);
	/**
	 * Origins�����̻�-��ȡ�������
	 * @param memberid
	 * @param mobile
	 * @return
	 */
	ErrorCode<SMSRecord> getColorBall(Long memberid , String mobile , String msgContent);
	/**
	 * Origins�����̻�-��ȡ�Ż�ȯ
	 * @param memberid
	 * @param mobile
	 * @param msgContent
	 * @return
	 */
	ErrorCode<SMSRecord> getCouponCode(Long memberid , String mobile , String msgContent);
}
