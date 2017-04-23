package com.gewara.web.action.blog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;

import com.gewara.model.bbs.commu.Commu;
import com.gewara.model.bbs.commu.CommuMember;
import com.gewara.model.user.Member;
import com.gewara.service.OperationService;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.bbs.CommuService;
import com.gewara.service.bbs.DiaryService;
import com.gewara.service.content.PictureService;
import com.gewara.service.drama.DramaService;
import com.gewara.service.member.TreasureService;
import com.gewara.service.movie.MCPService;
import com.gewara.service.order.GoodsOrderService;
import com.gewara.service.sport.SportService;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.gym.SynchGymService;
import com.gewara.web.action.AnnotationController;

public class BBSBaseController extends AnnotationController {
	@Autowired@Qualifier("operationService")
	protected OperationService operationService;
	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}
	@Autowired@Qualifier("treasureService")
	protected TreasureService treasureService;
	@Autowired@Qualifier("blogService")
	protected BlogService blogService;
	public void setBlogService(BlogService blogService) {
		this.blogService = blogService;
	}
	@Autowired@Qualifier("pictureService")
	protected PictureService pictureService;
	public void setPictureService(PictureService pictureService) {
		this.pictureService = pictureService;
	}
	@Autowired@Qualifier("goodsOrderService")
	protected GoodsOrderService goodsOrderService;
	public void setGoodsOrderService(GoodsOrderService goodsOrderService) {
		this.goodsOrderService = goodsOrderService;
	}
	
	@Autowired@Qualifier("diaryService")
	protected DiaryService diaryService;
	public void setDiaryService(DiaryService diaryService) {
		this.diaryService = diaryService;
	}
	@Autowired@Qualifier("commuService")
	protected CommuService commuService;
	public void setCommuService(CommuService commuService) {
		this.commuService = commuService;
	}

	@Autowired@Qualifier("sportService")
	protected SportService sportService;
	public void setSportService(SportService sportService) {
		this.sportService = sportService;
	}

	@Autowired@Qualifier("mcpService")
	protected MCPService mcpService;
	public void setMcpService(MCPService mcpService) {
		this.mcpService = mcpService;
	}
	@Autowired@Qualifier("synchGymService")
	protected SynchGymService synchGymService;
	public void setSynchGymService(SynchGymService synchGymService) {
		this.synchGymService = synchGymService;
	}
	@Autowired@Qualifier("dramaService")
	protected DramaService dramaService;
	public void setDramaService(DramaService dramaService) {
		this.dramaService = dramaService;
	}
	
	protected void getCommuRightData2(ModelMap model, Long cid, Member myMember) {
		Commu commu = daoService.getObject(Commu.class, cid);
		// �ж��Ƿ�ΪȦ�ӳ�Ա
		boolean isCommuMember = commuService.isCommuMember(commu.getId(),
				myMember.getId());
		model.put("isCommuMember", isCommuMember);
		// Ȧ�ӹ���Ա��Ϣ
		Member adminMember = daoService.getObject(Member.class, commu.getAdminid());
		// Ȧ�ӳ�Ա����
		Integer commuMemberNum = commuService.getCommumemberCount(commu.getId(),null);

		
		//Ȧ�ӳ�Ա���������Ȧ��
		List<Commu> commuList = commuService.getAlikeCommuList(cid, 0, 6);

		// ��������Ա
		List<CommuMember> commuMemberList = commuService.getCommuMemberById(cid, null, null, "", 0, 6);
		List<Long> memberidList = ServiceHelper.getMemberIdListFromBeanList(commuMemberList );
		addCacheMember(model, memberidList);
		Map<Long, Member> memberMap = new HashMap<Long, Member>();
		for (CommuMember commuMember : commuMemberList) {
			Member joinCommuMember = daoService.getObject(Member.class,
					commuMember.getMemberid());
			memberMap.put(joinCommuMember.getId(), joinCommuMember);
		}

		model.put("adminMember", adminMember);
		model.put("commuMemberNum", commuMemberNum);
		model.put("commu", commu);
		model.put("member", myMember);
		model.put("commuMemberList", commuMemberList);
		addCacheMember(model, ServiceHelper.getMemberIdListFromBeanList(commuMemberList));
		model.put("memberMap", memberMap);
		model.put("commuList", commuList);
		model.put("isAdmin", myMember.getId().equals(commu.getAdminid()));
		// ��鵱ǰȦ�ӵ�״̬
		String checkstatus = commuService.getCheckStatusByIDAndMemID(commu
				.getId());
		model.put("checkstatus", checkstatus);
		model.put("adminMember", adminMember);
		// �Ҽ����Ȧ��
		List joinCommuList = commuService.getCommuListByMemberId(
				myMember.getId(), 0, 3);
		model.put("joinCommuList", joinCommuList);
	}
	
	/**
	 * �Ƿ��ѹ�ע
	 * @param model
	 * @param memberIdList
	 * @param isTreasureMap
	 */
	protected void isTreasuredInfo(Member member, ModelMap model,List<Long> memberIdList,Map<Long,Boolean> isTreasureMap){
		for (Long memberid : memberIdList) {
			if(!isTreasureMap.keySet().contains(memberid)){
				isTreasureMap.put(memberid,blogService.isTreasureMember(member.getId(),memberid));
			}
		}
		model.put("isTreasureMap", isTreasureMap);
	}

}
