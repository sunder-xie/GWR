package com.gewara.web.action.subject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gewara.constant.DrawActicityConstant;
import com.gewara.constant.MemberConstant;
import com.gewara.constant.TagConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.model.common.VersionCtl;
import com.gewara.model.draw.DrawActivity;
import com.gewara.model.draw.Prize;
import com.gewara.model.draw.WinnerInfo;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.mongo.MongoService;
import com.gewara.service.OperationService;
import com.gewara.service.drama.DrawActivityService;
import com.gewara.service.order.OrderQueryService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.ShareService;
import com.gewara.untrans.UntransService;
import com.gewara.util.DateUtil;
import com.gewara.util.ObjectId;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;
import com.gewara.xmlbind.bbs.Comment;

@Controller
public class Subject2013Controller  extends AnnotationController {
	
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	@Autowired@Qualifier("drawActivityService")
	private DrawActivityService drawActivityService;
	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	@Autowired@Qualifier("commentService")
	private CommentService commentService;
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	@Autowired@Qualifier("orderQueryService")
	private OrderQueryService orderQueryService;
	private final String FILEPATH_TIMERIFT = "d:/";
	

	// �齱ǰ�жϹ�Ʊ����, �齱����
	//�������ͣ����ݶ����Ϳۻ��ֳ齱(�ۻ���ÿ��ֻ��һ��)
	@RequestMapping("/subject/proxy/getClickDrawCount.xhtml")
	public String getSurplusCount(Long memberid, String check, String tag, String fromtime, String totime, String citycode, ModelMap model){
		String checkcode = StringUtil.md5(memberid + "njmk5678");
		if(!StringUtils.equals(check, checkcode)) return showJsonSuccess(model, "���ȵ�¼��");
		Member member = daoService.getObject(Member.class, memberid);
		if(member == null) return showJsonSuccess(model, "���ȵ�¼��");
		DrawActivity da = daoService.getObjectByUkey(DrawActivity.class, "tag", tag, true);
		if(da == null || !da.isJoin()) return showJsonSuccess(model, "�δ��ʼ���ѽ�����");
		Timestamp ftime = null;
		Timestamp ttime = null;
		if(StringUtils.isBlank(fromtime) || StringUtils.isBlank(totime)){
			ftime = da.getStarttime();
			ttime = da.getEndtime();
		}else{
			ftime = DateUtil.parseTimestamp(fromtime);
			ttime = DateUtil.parseTimestamp(totime);
		}
		if(StringUtils.isBlank(citycode) || ftime == null || ttime == null || ttime.before(ftime)) return showJsonSuccess(model, "��������");
		if(isChance(memberid, ftime, ttime, citycode, tag)) return showJsonSuccess(model);
		else{
			Map drawMember = getMemberDrawMap(memberid, tag, "point"+tag);
			if(drawMember != null && drawMember.get(MongoData.ACTION_MODIFYTIME) != null){
				Date drawDate = DateUtil.parseDate(drawMember.get(MongoData.ACTION_MODIFYTIME)+"");
				Date cur = DateUtil.parseDate(DateUtil.getCurFullTimestampStr());
				if(cur.equals(drawDate)) return showJsonSuccess(model, "nobuy");
			}
			return showJsonSuccess(model, "everyday");
		}
	}
	
	
	//�鿴�û�ʣ����ѳ齱����			�������ͣ����ݶ��������������
	@RequestMapping("/subject/proxy/getDrawSurplusCount.xhtml")
	public String getDrawSurplusCount(Long memberid, String check, String tag, String citycode, ModelMap model){
		String checkcode = StringUtil.md5(memberid + "njmk5678");
		if(!StringUtils.equals(check, checkcode)) return showJsonSuccess(model, "0");
		DrawActivity da = daoService.getObjectByUkey(DrawActivity.class, "tag", tag, true);
		if(da == null || !da.isJoin()) return showJsonSuccess(model, "0");
		return showJsonSuccess(model, getSurplusCount(da, memberid, tag, citycode)+"");
	}
	
	//20130116�˶��ͺ��ר��齱			�������ͣ����ݶ������齱������������δʹ���Żݵģ�
	@RequestMapping("/subject/proxy/sendRedPacket/clickDraw.xhtml")
	public String sendRedPacketClickDraw(Long memberid, String check, String tag, ModelMap model, HttpServletRequest request){
		String checkcode = StringUtil.md5(memberid + "njmk5678");
		if(!StringUtils.equals(check, checkcode)) return showJsonSuccess(model, "���ȵ�¼��");
		Member member = daoService.getObject(Member.class, memberid);
		if(member == null) return showJsonSuccess(model, "���ȵ�¼��");
		String opkey = tag + member.getId();
		boolean allow = operationService.updateOperation(opkey, 10);
		if(!allow) return showJsonSuccess(model, "��������Ƶ�������Ժ����ԣ�");
		DrawActivity da = daoService.getObjectByUkey(DrawActivity.class, "tag", tag, true);
		if(da == null||!da.isJoin()) return showJsonSuccess(model, "�δ��ʼ���ѽ�����");
		try {
			MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
			Map<String, String> otherinfoMap = VmUtils.readJsonToMap(da.getOtherinfo());
			if(StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_MOBILE)) && !member.isBindMobile()) return showJsonSuccess(model, "���Ȱ��ֻ���");
			if(StringUtils.isNotBlank(otherinfoMap.get(DrawActicityConstant.TASK_EMAIL)) && !memberInfo.isFinishedTask(MemberConstant.TASK_CONFIRMREG)) return showJsonSuccess(model, "������֤���䣡");
			if(getSportSurplusCount(da, memberid) <= 0)	return showJsonSuccess(model, "�㵱ǰ���ܲμӳ齱��");
			VersionCtl mvc = drawActivityService.gainMemberVc(""+member.getId());
			//FIXME:��ţ����
			ErrorCode<WinnerInfo> ec = drawActivityService.baseClickDraw(da, mvc, false, member);
			if(ec == null || !ec.isSuccess()) return showJsonSuccess(model, "��Ʒ�ѷ��꣡");
			WinnerInfo winnerInfo = ec.getRetval();
			if(winnerInfo == null) return showJsonSuccess(model, "��Ʒ�ѷ��꣡");
			Prize prize = daoService.getObject(Prize.class, winnerInfo.getPrizeid());
			if(prize == null) return showJsonSuccess(model, "��Ʒ�ѷ��꣡");
			SMSRecord sms = drawActivityService.sendPrize(prize, winnerInfo, true);
			if(sms !=null) untransService.sendMsgAtServer(sms, false);
			Map otherinfo = VmUtils.readJsonToMap(prize.getOtherinfo());
			if(otherinfo.get(DrawActicityConstant.TASK_WALA_CONTENT) != null){
				String link = null;
				if(otherinfo.get(DrawActicityConstant.TASK_WALA_LINK) != null){
					link = otherinfo.get(DrawActicityConstant.TASK_WALA_LINK)+"";
					link = "<a href=\""+link+"\" target=\"_blank\" rel=\"nofollow\">"+"���ӵ�ַ"+"</a>";
				}
				ErrorCode<Comment> result = commentService.addComment(member, TagConstant.TAG_TOPIC, null, otherinfo.get(DrawActicityConstant.TASK_WALA_CONTENT)+"", link, false, null, null, WebUtils.getRemoteIp(request));
				if(result.isSuccess()) {
					shareService.sendShareInfo("wala",result.getRetval().getId(), result.getRetval().getMemberid(), null);
				}
			}
			return showJsonSuccess(model, prize.getRemark()+"&"+prize.getPtype()+"&"+prize.getOtype());
		}catch(StaleObjectStateException e){
			return showJsonSuccess(model, "ϵͳ����");
		}catch(HibernateOptimisticLockingFailureException e){
			return showJsonSuccess(model, "ϵͳ����");
		}
	}
	
	//�鿴�û�ʣ����ѳ齱����			�������ͣ������˶����۶���
	@RequestMapping("/subject/proxy/sendRedPacket/getDrawSurplusCount.xhtml")
	public String getSendRedPacketCount(Long memberid, String check, String tag, ModelMap model){
		String checkcode = StringUtil.md5(memberid + "njmk5678");
		if(!StringUtils.equals(check, checkcode)) return showJsonSuccess(model, "0");
		DrawActivity da = daoService.getObjectByUkey(DrawActivity.class, "tag", tag, true);
		if(da == null || !da.isJoin()) return showJsonSuccess(model, "0");
		return showJsonSuccess(model, getSportSurplusCount(da, memberid)+"");
	}
	
	@RequestMapping("/subject/proxy/timeRift/clickDraw.xhtml")
	@ResponseBody
	public String riftClickDraw(Long memberid, String check, String tag) {
		String checkcode = StringUtil.md5(memberid + "njmk5678");
		if (!StringUtils.equals(check, checkcode))
			return "���ȵ�¼��";
		Member member = daoService.getObject(Member.class, memberid);
		if (member == null)
			return "���ȵ�¼��";
		String opkey = tag + member.getId();
		boolean allow = operationService.updateOperation(opkey, 10);
		if (!allow)
			return "��������Ƶ�������Ժ����ԣ�";

		Map<String, Object> drawMember = getMemberDrawByTimeRift(memberid, tag, "point" + tag);
		String usedCode = getPrivilegedCodeByMemberId(memberid);
		if (drawMember != null && drawMember.get(MongoData.ACTION_ADDTIME) != null)
			return  "success:getIt-"+usedCode;
		
		String privilegedCode = getUnusedPrivilegedCode(memberid);
		if (StringUtils.equals(privilegedCode, "nomore")) {
			return "��Ȩ���Ѿ���������";
		}else if (StringUtils.equals(privilegedCode, "syserr")||privilegedCode.length()!=16) {
			return "ϵͳ����";
		}else{
			saveMemberDrawByTimeRift(memberid, tag, "point" + tag);
			return "success:"+privilegedCode;
		}
	}
	
	@RequestMapping("/admin/sysmgr/timeRift/importData.xhtml")
	@ResponseBody
	public String importData(String fileName) {
		String msg = loadTimeRiftTxt(fileName);
		return  msg;
	}
	
	@SuppressWarnings("unchecked")
	public String getUnusedPrivilegedCode(Long memberId) {
		String privilegedCode ="";
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(MongoData.ACTION_IS_USED, "N");
			Map<String, Object> map = mongoService.findOne(MongoData.NS_TIMERIFT, params);
			if (map != null && map.get(MongoData.ACTION_PRIVILEGED_CODE) != null) {
				privilegedCode = (String) map.get(MongoData.ACTION_PRIVILEGED_CODE);
				map.put(MongoData.ACTION_IS_USED, "Y");
				map.put(MongoData.ACTION_MEMBER_ID, memberId);
				map.put(MongoData.ACTION_USED_TIME, new Date());
				mongoService.saveOrUpdateMap(map, MongoData.SYSTEM_ID, MongoData.NS_TIMERIFT);
			} else {
				privilegedCode = "nomore";
			}
		} catch (Exception e) {
			dbLogger.error("ʱ���ѷ���Ȩ���ȡʧ�ܣ�������Ϣ��", e);
			privilegedCode = "syserr";
		}
		return privilegedCode;
	}

	private String getPrivilegedCodeByMemberId(Long memberId) {
		String privilegedCode = "";
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(MongoData.ACTION_IS_USED, "Y");
			params.put(MongoData.ACTION_MEMBER_ID, memberId);
			Map<String, Object> map = mongoService.findOne(MongoData.NS_TIMERIFT, params);
			if (map != null && map.get(MongoData.ACTION_PRIVILEGED_CODE) != null) {
				privilegedCode = (String) map.get(MongoData.ACTION_PRIVILEGED_CODE);
			} else {
				privilegedCode = "nocode";
			}
		} catch (Exception e) {
			dbLogger.error("ʱ���ѷ���Ȩ���ȡʧ�ܣ�������Ϣ��", e);
			privilegedCode = "syserr";
		}
		return privilegedCode;
	}
	
	private Map<String, Object> getMemberDrawByTimeRift(Long memberid, String type, String tag) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(MongoData.ACTION_MEMBERID, memberid);
		params.put(MongoData.ACTION_TYPE, type);
		params.put(MongoData.ACTION_TAG, tag);
		return mongoService.findOne(MongoData.NS_ACTIVITY_COMMON_MEMBER, params);
	}

	private void saveMemberDrawByTimeRift(Long memberid, String type, String tag) {
		Map<String, Object> drawMember = new HashMap<String, Object>();
		String curString = DateUtil.getCurFullTimestampStr();
		drawMember.put(MongoData.SYSTEM_ID, ObjectId.uuid());
		drawMember.put(MongoData.ACTION_MEMBERID, memberid);
		drawMember.put(MongoData.ACTION_TYPE, type);
		drawMember.put(MongoData.ACTION_TAG, tag);
		drawMember.put(MongoData.ACTION_ADDTIME, curString);
		mongoService.saveOrUpdateMap(drawMember, MongoData.SYSTEM_ID, MongoData.NS_ACTIVITY_COMMON_MEMBER);
	}
	
	private int getSportSurplusCount(DrawActivity da, Long memberid){
		int pay = orderQueryService.getNoPreferentialSportOrderCount(memberid, da.getStarttime(), da.getEndtime(), OrderConstant.STATUS_PAID_SUCCESS);
		int count = drawActivityService.getMemberWinnerCount(memberid, da.getId(), da.getStarttime(), da.getEndtime());
		return pay - count;
	}
	
	private int getSurplusCount(DrawActivity da, Long memberid, String tag, String citycode){
		int count = drawActivityService.getInviteMemberCount(memberid, "email", true, da.getStarttime(), da.getEndtime());
		int pay = orderQueryService.getMemberTicketCountByMemberid(memberid, da.getStarttime(), da.getEndtime(), OrderConstant.STATUS_PAID_SUCCESS, citycode);
		int drawcount = 0;
		Map drawMap = getMemberDrawMap(memberid, tag, tag);
		if(drawMap != null && drawMap.get("drawtimes") != null) drawcount = Integer.parseInt(drawMap.get("drawtimes")+"");
		return count + pay - drawcount;
	}
	
	
	private Map getMemberDrawMap(Long memberid, String type, String tag){
		Map params = new HashMap();
		params.put(MongoData.ACTION_MEMBERID, memberid);
		params.put(MongoData.ACTION_TYPE, type);
		params.put(MongoData.ACTION_TAG, tag);
		return mongoService.findOne(MongoData.NS_ACTIVITY_COMMON_MEMBER, params);
	}
	
	private boolean isChance(Long memberid, Timestamp ftime, Timestamp ttime, String citycode, String tag){
		int pay = orderQueryService.getMemberTicketCountByMemberid(memberid, ftime, ttime, OrderConstant.STATUS_PAID_SUCCESS, citycode);
		Map drawMember = getMemberDrawMap(memberid, tag, tag);
		int drawtimes = 0;
		if(drawMember != null && drawMember.get("drawtimes") != null) drawtimes = Integer.parseInt(drawMember.get("drawtimes")+"");
		if(pay - drawtimes > 0) return true;
		else return false;
	}
	
	@SuppressWarnings("unchecked")
	public String loadTimeRiftTxt(String fileName) {
		StringBuffer sb = new StringBuffer();
		if (fileName == null || fileName.indexOf("..") > 0 || fileName.indexOf("//") > 0 || fileName.indexOf("\\") > 0) {
			return "�ļ������������ַ���";
		}
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader(FILEPATH_TIMERIFT + fileName);
			br = new BufferedReader(reader);
			
			String s1 = null;
			List<Map> rowList = new ArrayList<Map>();
			
			dbLogger.error("��ʼ�������ݣ�" + fileName);
			int rowSize = 0;
			long start = System.currentTimeMillis();
			while ((s1 = br.readLine()) != null) {
				Map<String, Object> map = new HashMap<String,Object>();
				String[] s = s1.split("\t");
				String seqNO  = null;
				String privilegedCode  = null;
				long memberId  = 0;
				Date usedTime  = null;
				String isUsed  = "N";

				if (s.length >= 2) {
					privilegedCode = s[1];
					seqNO= s[0];
				} 
				if (privilegedCode == null || privilegedCode.length() < 16) {
					dbLogger.error(fileName + "����������:" + " s.length=" + s.length + " authenticode:" + privilegedCode.length());
					sb.append(fileName + "����������:" + " s.length=" + s.length + " authenticode:" + privilegedCode.length());
				} else {
					map.put(MongoData.SYSTEM_ID, ObjectId.uuid());
					map.put(MongoData.ACTION_SEQ,seqNO);
					map.put(MongoData.ACTION_PRIVILEGED_CODE,privilegedCode);
					map.put(MongoData.ACTION_IS_USED,isUsed);
					map.put(MongoData.ACTION_MEMBER_ID,memberId);
					map.put(MongoData.ACTION_USED_TIME,usedTime);
					rowList.add(map);
				}

				if (rowList.size() >= 5000) {
					mongoService.addMapList(rowList, MongoData.SYSTEM_ID, MongoData.NS_TIMERIFT, true, true);
					dbLogger.error(fileName + "�Ѿ����룺" + rowSize);
					rowSize = rowSize + rowList.size();
					rowList.clear();
				}
			}

			if (rowList.size() > 0) {
				mongoService.addMapList(rowList, MongoData.SYSTEM_ID, MongoData.NS_TIMERIFT, true, true);
				rowSize = rowSize + rowList.size();
				rowList.clear();
			}
			dbLogger.error("����������ɣ�" + fileName + " ��������:" + rowSize + " ��ʱ:" + (System.currentTimeMillis() - start) / 1000);
			sb.append("����������ɣ�" + fileName + " ��������:" + rowSize + " ��ʱ:" + (System.currentTimeMillis() - start) / 1000);
		} catch (Exception e) {
			dbLogger.error("�������", e);
			sb.append("�������"+e.getMessage());
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
		return sb.toString();
	}

}