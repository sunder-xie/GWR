package com.gewara.web.action.admin.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.MemberConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.json.MemberStats;
import com.gewara.model.acl.GewaraUser;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.service.bbs.SnsService;
import com.gewara.service.drama.DrawActivityService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.MemberCountService;
import com.gewara.util.VmUtils;
import com.gewara.web.action.admin.BaseAdminController;
import com.gewara.web.util.PageUtil;

@Controller
public class NewTaskAdminController extends BaseAdminController {
	@Autowired@Qualifier("memberCountService")
	private MemberCountService memberCountService;
	@Autowired@Qualifier("snsService")
	private SnsService snsService;
	public void setSnsService(SnsService snsService) {
		this.snsService = snsService;
	}
	@Autowired@Qualifier("drawActivityService")
	private DrawActivityService drawActivityService;
	public void setDrawActivityService(DrawActivityService drawActivityService) {
		this.drawActivityService = drawActivityService;
	}

	//��̨�û������������
	@RequestMapping("/admin/sns/taskList.xhtml")
	public String adminTaskList(ModelMap model, Long memberid, String nickname, 
			String mobile, String email, Integer pageNo){
		if(memberid==null && StringUtils.isBlank(nickname) && StringUtils.isBlank(mobile) 
				&& StringUtils.isBlank(email) ) return "admin/sns/newtaskList.vm";
		if(pageNo==null) pageNo=0;
		Integer rowsPage=18;
		Integer from =pageNo*rowsPage;
		List<Member> memberList = snsService.searchMember(memberid, nickname, mobile, email, from, rowsPage);
		Integer memberCount = snsService.searchMemberCount(memberid, nickname, mobile, email);
		Map<String, Boolean> headPicMap = new HashMap<String, Boolean>();
		Map<String, Boolean> buyticketMap = new HashMap<String, Boolean>();
		Map<String, Boolean> movieCommentMap = new HashMap<String, Boolean>();
		Map<String, Boolean> confirmRegMap = new HashMap<String, Boolean>();
		Map<String, Boolean> fiveFriendMap = new HashMap<String, Boolean>();
		Map<String, Boolean> bindMoileMap = new HashMap<String, Boolean>();
		Map<String,Boolean> sendWalaMap = new HashMap<String,Boolean>();
		Map<String,Boolean> joincommuMap = new HashMap<String,Boolean>();
		for(Member member : memberList){
			MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
			List<String> otherNewTasksList = memberInfo.getOtherNewtaskList();
			headPicMap.put(member.getId()+"", memberInfo.isFinishedTask(MemberConstant.TASK_UPDATE_HEAD_PIC));//ͷ��
			headPicMap.put(member.getId()+"_isTake", VmUtils.contains(otherNewTasksList, MemberConstant.TASK_UPDATE_HEAD_PIC));//�Ƿ�����ȡ����
			
			buyticketMap.put(member.getId()+"", memberInfo.isFinishedTask(MemberConstant.TASK_BUYED_TICKET));//��Ʊ
			buyticketMap.put(member.getId()+"_isTake",VmUtils.contains(otherNewTasksList, MemberConstant.TASK_BUYED_TICKET));
			
			movieCommentMap.put(member.getId()+"", memberInfo.isFinishedTask(MemberConstant.TASK_MOVIE_COMMENT));//Ӱ��
			movieCommentMap.put(member.getId()+"_isTake", VmUtils.contains(otherNewTasksList, MemberConstant.TASK_MOVIE_COMMENT));
			
			confirmRegMap.put(member.getId()+"", memberInfo.isFinishedTask(MemberConstant.TASK_CONFIRMREG));//����
			confirmRegMap.put(member.getId()+"_isTake", VmUtils.contains(otherNewTasksList, MemberConstant.TASK_CONFIRMREG));
			
			fiveFriendMap.put(member.getId()+"", memberInfo.isFinishedTask(MemberConstant.TASK_FIVEFRIEND));//��ע�������
			fiveFriendMap.put(member.getId()+"_isTake", VmUtils.contains(otherNewTasksList, MemberConstant.TASK_FIVEFRIEND));
			
			bindMoileMap.put(member.getId()+"", memberInfo.isFinishedTask(MemberConstant.TASK_BINDMOBILE));//���ֻ�
			bindMoileMap.put(member.getId()+"_isTake", VmUtils.contains(otherNewTasksList, MemberConstant.TASK_BINDMOBILE));
			
			sendWalaMap.put(member.getId()+"", memberInfo.isFinishedTask(MemberConstant.TASK_SENDWALA));//������
			sendWalaMap.put(member.getId()+"_isTake", VmUtils.contains(otherNewTasksList, MemberConstant.TASK_SENDWALA));
			
			joincommuMap.put(member.getId()+"", memberInfo.isFinishedTask(MemberConstant.TASK_JOINCOMMU));//����Ȧ��
			joincommuMap.put(member.getId()+"_isTake", VmUtils.contains(otherNewTasksList, MemberConstant.TASK_JOINCOMMU));
		}
		PageUtil pageUtil=new PageUtil(memberCount, rowsPage, pageNo, "admin/sns/taskList.xhtml");
		Map params = new HashMap();
		params.put("email", email);
		params.put("mobile", mobile);
		params.put("nickname", nickname);
		params.put("memberid", memberid);
		pageUtil.initPageInfo(params);
		model.put("pageUtil",pageUtil);
		model.put("headPicMap", headPicMap);
		model.put("buyticketMap", buyticketMap);
		model.put("movieCommentMap", movieCommentMap);
		model.put("confirmRegMap", confirmRegMap);
		model.put("fiveFriendMap", fiveFriendMap);
		model.put("bindMoileMap", bindMoileMap);
		model.put("sendWalaMap", sendWalaMap);
		model.put("joincommuMap", joincommuMap);
		model.put("memberList", memberList);
		model.put("memberCount", memberCount);
		
		return "admin/sns/newtaskList.vm";
	}
	//��̨�ͷ��ֶ�����û������������
	@RequestMapping("/admin/sns/addMemberNewTask.xhtml")
	public String addMemberNewTask(ModelMap model, Long memberid){
		MemberInfo memberInfo=daoService.getObject(MemberInfo.class, memberid);
		GewaraUser user = getLogonUser();
		if(memberInfo==null) return showJsonError(model, "��������!");
		if(memberInfo.isReceived()){
			return showJsonError(model, "���û��Ѿ���ȡ������������֣�");
		}else{
			Member member = daoService.getObject(Member.class, memberInfo.getId());
			ErrorCode code = drawActivityService.sendNewTaskCardPrize(memberInfo, member);
			if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		}
		dbLogger.warn("��������ӻ��ֵĹ���Ա��" + user.getRealname());
		return showJsonSuccess(model, "��ȡ����������ֳɹ�!");
	}
	@RequestMapping("/admin/sns/refreshTask.xhtml")
	public String refreshTask(ModelMap model, Long memberid){
		Member member=daoService.getObject(Member.class, memberid);
		if(member==null) return showJsonError(model, "�û������ڣ�");
		MemberInfo info = daoService.getObject(MemberInfo.class, member.getId());
		if(!info.isReceived()){
			String result = "";
			//�Ƿ��Ѿ������ӰƱ
			if(!info.isFinishedTask(MemberConstant.TASK_BUYED_TICKET)){
				boolean isBuyTicket = isBoughtTicket(member.getId());
				if(isBuyTicket) {
					memberService.saveNewTask(member.getId(), MemberConstant.TASK_BUYED_TICKET);
					result += "��Ʊ������ɣ�";
				}
			}
			//�Ƿ���5λ���ѻ��ע5λ����,�����������һ��
			if(!info.isFinishedTask(MemberConstant.TASK_FIVEFRIEND)){
				Map dataMap = memberCountService.getMemberCount(member.getId());
				Integer friendCount = 0;
				if(dataMap!=null){
					friendCount = (Integer)dataMap.get(MemberStats.FIELD_ATTENTIONCOUNT);
					if(friendCount==null) friendCount = 0;
				}
				model.put("friendCount", friendCount);
				if(friendCount>=5){
					memberService.saveNewTask(member.getId(), MemberConstant.TASK_FIVEFRIEND);
					result += "��ӹ�ע������ɣ�";
				}
			}
			//�Ƿ���ֻ�
			if(!info.isFinishedTask(MemberConstant.TASK_BINDMOBILE)){
				if(member.isBindMobile()){
					memberService.saveNewTask(info.getId(), MemberConstant.TASK_BINDMOBILE);
					result += "���ֻ�������ɣ�";
				}
			}
			if(!info.isFinishedTask(MemberConstant.TASK_MOVIE_COMMENT)){
				//TODO:����Ƿ񷢹�Ӱ��
				//memberService.saveNewTask(member.getId(), MemberConstant.TASK_MOVIE_COMMENT);
			}

			if(StringUtils.isNotBlank(result)) {
				return showJsonError(model, result);
			}
		}
		return showJsonError(model, "û�и��µ����ݣ�");
	}
	private boolean isBoughtTicket(Long memberid) {
		String query = "select count(id) from TicketOrder where memberid=? and status=?";
		List result = hibernateTemplate.find(query, memberid, OrderConstant.STATUS_PAID_SUCCESS);
		return Integer.parseInt(""+result.get(0)) > 0;
	}
}
