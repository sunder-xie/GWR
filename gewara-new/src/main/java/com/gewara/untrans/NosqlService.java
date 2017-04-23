package com.gewara.untrans;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gewara.json.MemberSign;
import com.gewara.json.MobileUpGrade;
import com.gewara.json.MovieMpiRemark;
import com.gewara.json.PlayItemMessage;
import com.gewara.json.ViewFilmSchedule;
import com.gewara.json.WDOrderContrast;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.model.pay.TicketOrder;
import com.gewara.xmlbind.ticket.WdOrder;

/**
 * no sql���ҵ�񷽷���װ
 * @author gebiao(ge.biao@gewara.com)
 * @since Dec 28, 2012 3:36:46 PM
 */
public interface NosqlService {
	/**
	 * �û�ǩ������γ��
	 * @param id
	 * @param x  
	 * @param y
	 */
	void memberSign(Long memberid, Double pointx, Double pointy);
	void memberSignBaiDu(Long memberid, Double bpointx, Double bpointy);
	void memberSign(Long memberid, Double pointx, Double pointy, String address);
	/**
	 * ����γ�������Сֵ��ѯ�û��б�
	 * @param pointx
	 * @param pointy
	 * @param r
	 * @param from
	 * @param maxnum
	 * @return
	 */
	List<MemberSign> getMemberSignListByPointR(double pointx, double pointy, long r, int from, int maxnum);
	MemberSign getMemberSign(Long memberid);
	MobileUpGrade getLastMobileUpGrade(String tag, String apptype, String appsource);
	MobileUpGrade getLastMobileUpGradeById(String appid);
	void saveSurveyList(List<Map> mapList);
	List<Map> getSurveyByMemberid(Long memberid);
	PlayItemMessage getPlayItemMessage(Long memberid, String tag, Long relatedid, Date playdate, Long categoryid);
	List<PlayItemMessage> getSendPlayItemMessageList(String tag, String status, String type, int from, int maxnum);
	/**
	 * ��ȡ���Ѷ���
	 * @param memberid
	 * @param categoryid
	 * @return
	 */
	PlayItemMessage addPlayItemMessage(Long memberid, String tag, Long relatedid, Date playdate, Long categoryid, String mobile, String type, String msg);
	PlayItemMessage addPlayItemMessage(Long memberid, String tag, Long relatedid, Date playdate, Long categoryid, String mobile, String flag, String type, String msg);
	/**
	 * ��Ӱ��������
	 * @param movieid
	 * @param citycode
	 * @param maxnum
	 * @return
	 */
	List<MovieMpiRemark> getMovieMpiRemarkList(Long movieid, String citycode, int maxnum);
	
	/**
	 * ��ȡ�Զ�������ͨ�ü��յ�ʱ������
	 * @return
	 */
	Map<String,String> getAutoSetterLimit();
	/**
	 * ��ȡӰ�����ȣ�������ǽ��λ��
	 * @param roomId
	 * @return
	 */
	Map<String,String> getOuterRingSeatByRoomId(Long roomId);
	
	/**
	 * ������ÿ�չ�Ʊ����
	 * @return
	 */
	List<Map> getBuyTicketRanking();
	/**
	 * 16���Ӱ�� ���Ƭ�����ճ�
	 * @param mpi ��Ƭ
	 * @param tag
	 * @param movieId ��Ӱid
	 * @return
	 */
	ViewFilmSchedule addViewFilmSchedule(MoviePlayItem mpi,String tag,Long movieId,long memberId,String source);
	
	/**
	 * ����ÿ�����ͬ���Ķ����͸���������ƥ�䲻�ϵĶ���
	 * @param gewaOrderList
	 * @param wdOrderList
	 */
	List<WDOrderContrast> saveWDOrderContrast(List<TicketOrder> gewaOrderList,List<WdOrder> wdOrderList,Date addDate);
}
