package com.gewara.untrans;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gewara.model.user.Member;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.bbs.Comment;
import com.gewara.xmlbind.bbs.CountByMovieIdAddDate;
public interface CommentService {
	/**
	 * ��Ծ�û��������ڷ�wala�������û�
	 * @param maxnum
	 * @return
	 */
	List<Map> getActiveMemberList(int maxnum);
	/**
	 * ��������id��ѯת�ص�������Ϣ
	 */
	List<Comment> getCommentListByTransfer(Long commentId, int from, int maxnum);
	
	Comment getCommentById(Long commentId);
	
	List<Comment> getCommentByIdList(Collection<Long> idList);
	/**
	 * 
	 * ����tag��ѯ���ۼ�¼
	 */
	List<Comment> pointByFreeBackCommentList(String tag,int from, int maxnum);
	
	/**
	 * wap���ҹ�ע��
	 */
	List<Comment> getMyAttentionCommentListByMemberid(Long memberid,int from,int maxnum);
	Integer getMyAttentionCommentCountByMemberid(Long memberid);

	/**
	 * ΢�����ᵽ�ҵ�
	 */
	List<Comment> getMicroBlogListByMemberid(String nickName, Long memberid,int from,int maxnum);
	
	/**
	 * @param tag
	 * @param from
	 * @param maxnum
	 * @return ��������ʱ������
	 */
	List<Comment> getCommentListByTag(String tag, int from, int maxnum);

	/**
	 * ���ݹ�������ĿID�������۵ļ�¼��
	 * @param tag
	 * @return
	 */
	Integer getCommentCountByTag(String tag);
	List<Comment> getCommentListByRelatedId(String tag,String flag, Long relatedId, String order, int from, int maxnum);
	List<Comment> getCommentListByRelatedId(String tag,String flag, Long relatedId, String order, Long mincommentid, int from, int maxnum);
	List<Comment> getHotCommentListByRelatedId(String tag,String flag, Long relatedId, Timestamp startTime, Timestamp endTime, int from, int maxnum);
	Comment getNewCommentByRelatedid(String tag,Long relatedId, Long memberid);
	/**
	 * ���ݹؼ��ֲ�ѯ
	 * @param tag
	 * @param key
	 * @return
	 */
	List<Comment>  getCommentListByKey(String tag, String key);

	/**
	 * ���ݹ�������ĿID�������۵ļ�¼��
	 * @param tag
	 * @return
	 */
	Integer getCommentCountByRelatedId(String tag, Long relatedId);
	/**
	 * ���ݹ�������ĿID�������۵ļ�¼��,flag Ϊticket ��ѯ��Ʊ�û���������
	 * @param tag
	 * @return
	 */
	Integer getCommentCountByRelatedId(String tag,String flag, Long relatedId);

	List<Comment> getCommentListByTags(String[] tag, Long memberid, boolean isTransfer, int from, int maxnum);
	Integer getCommentCountByTags(String[] tag, Long memberid, boolean isTransfer);
	/**
	 * ��ѯ������Ϣ(��̨)
	 */
	List<Comment> getCommentList(String tag, Long relatedid, Long memberid, String body, String status, int from,int maxnum);
	List<Comment> getCommentList(String tag, Long relatedid, Long memberid, String body, String status, Timestamp beginDate, Timestamp endDate, int from,int maxnum);
	/**
	 * ��ѯ������Ϣ����(��̨)
	 * @param id
	 * @param memberid
	 * @param tag
	 * @param relatedid
	 * @param body
	 * @return
	 */
	Integer getCommentCount(String tag, Long relatedid, Long memberid, String body, String status);
	Integer getCommentCount(String tag, Long relatedid, Long memberid, String body, String status, Timestamp beginDate, Timestamp endDate);
	/**
	 * ��ȡ��¥������Ϣ
	 * @return
	 */
	Integer getLongCommentCount(String tag, Long relatedid, String status);
	List<Comment> getLongCommentList(String tag, Long relatedid, String status, int from, int maxnum);	
	/**
	 * ������������
	 * 
	 */
	void updateCommentReplyCount(Long commentid,String type);
	/**
	 * --------------------------------
	 * �°�����
	 * --------------------------------
	 */
	
	/**
	 * �����û�
	 */
	List<Map> getHotMicroMemberList(String tag, Long memberid, int maxnum);
	
	/**
	 * ��������(�������ˣ���Ƶ��ͼƬ������)
	 * 
	 */
	List<Comment> searchCommentList(String searchkey,String type,int from,int maxnum);
	List<Comment> searchCommentList(String searchkey,String type, List<Long> memberidList, int from,int maxnum);
	Integer searchCommentCount(String searchkey,String type);
	/**
	 * ��ȡȫ��������Ϣ������ת��,ͼƬ����Ƶ��
	 * @param commentList
	 * @param name
	 * @return
	 */
	Map getAllCommentList(List<Comment> commentList,String name);
	
	ErrorCode<Comment> addComment(Member member, String tag, Long relatedid, String body, String link, boolean ignoreCheck, String pointx, String pointy, String ip);
	ErrorCode<Comment> addComment(Member member, String tag, Long relatedid, String body, String link, boolean ignoreCheck, Integer generalmark, String pointx, String pointy, String ip);
	ErrorCode<Comment> addMicroComment(Member member, String tag, Long relatedid, String body, String link, String address, boolean ignoreCheck, String pointx, String pointy, String ip);
	ErrorCode<Comment> addMicroComment(Member member, String tag, Long relatedid, String body, String link, String address, Long transferid, boolean ignoreCheck, Integer generalmark, String pointx, String pointy, String ip);
	
	/**
	 *  ����tag + address ��ѯ (For MAS)
	 * **/
	List<Comment> getCommentsByTagAndAddress(String tag, String address, Timestamp starttime, Timestamp endtime, String topic, String handle, int from, int maxnum);
	Integer getCommentCountByTagAndAddress(String tag, String address, Timestamp starttime, Timestamp endtime, String topic, String handle);
	List<Comment> getModeratorDetailList(String topic, boolean asc, int from, int maxnum);
	
	List<Comment> getCommentListByTagMemberids(String[] tag,List<Long> ids,Timestamp startTime,Timestamp endTime, int from, int maxnum);
	/**
	 * ��ѯ�û������ƱӰԺ������
	 * @param memberid �û�ID
	 * @param from ��ʼ����
	 * @param maxnum ���������
	 * @return Comment ���ϣ���û����size = 0 �� isEmpty = true
	 */
	List<Comment> getCommentListByMemberid(Long memberid, int from, int maxnum);
	
	/**
	 * ���ݻ������Ʋ�ѯ��������
	 * @param name
	 * @return
	 */	
	Integer getModeratorDetailCount(String topic);
	
	ErrorCode<Comment> addMicroComment(Member member, String tag, Long relatedid, String body, String link, 
			String address, Long transferid, boolean ignoreInterval, Integer generalmark, String otherInfo,String pointx, String pointy, String ip, String apptype);

	//��̨�������
	List<Comment> getCommentList(Long memberid, Timestamp starttime, Timestamp endtime, String transfer, String status, String keyname, String isMicro, int from, int maxnum);
	Integer getCommentCount(Long memberid, Timestamp starttime, Timestamp endtime, String transfer, String status, String keyname, String isMicro);
	
	List<Long> getTopAddMemberidList(String tag, int maxnum);
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	Long saveComment(Comment comment);
	Long updateComment(Comment comment);
	List<Comment> getCommentList(String[] tags, Long memberId, Date beginDate, Date endDate, int from, int maxnum);
	Integer getCommentCount(String tag,String flag,Long relatedid, String status, Long memberid, Long transferid, String body, Timestamp startTime,	Timestamp endTime);
	Integer pointByFreeBackCommentCount(String tag);
	List<Comment> getCommentListByMemberIdAndTags(String[] tags, Long memberId, Date beginDate, Date endDate, int from, int maxnum);
	void deleteComment(Long commentId);
	List<CountByMovieIdAddDate> getCountByMovieIdAddDate(String movieIds, String type, Date date);
	List<HashMap> getTaskCommentList();
	void addReplyToComment(String mobile, String msg, String ip);
	List<Comment> getHotCommentListByTopic(String topic, Timestamp startTime, Timestamp endTime, String order, int from, int maxnum);
}
