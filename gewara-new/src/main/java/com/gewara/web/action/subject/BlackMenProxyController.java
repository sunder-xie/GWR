package com.gewara.web.action.subject;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.MemberConstant;
import com.gewara.model.user.ShareMember;
import com.gewara.service.OperationService;
import com.gewara.untrans.ShareService;
import com.gewara.util.RandomUtil;
import com.gewara.util.VmUtils;
import com.gewara.web.action.AnnotationController;

/**
 * May 18, 2012 4:21:45 PM
 * @author bob
 * @desc ������ר�� ����
 */
@Controller
public class BlackMenProxyController  extends AnnotationController {
	
	private static final List<String> stringList = Arrays.asList("�������ԡ�ET���Ĵ��۾�������","�������ԡ�����Ԫ�ء�����ɫ�輧",
			"�������ԡ������ա������Ƿ�²","�������ԡ����������ɫ����սʿ","�������ԡ����Ρ���ճҺ����","�������ԡ����ޡ�����ɫ������",
			"�������ԡ��Ǽ��Ժ����ĳ����佫��","�������ԡ�����ս�����ĸ�������","�������ԡ����ν�ա��ĳ���սʿ","�������ԡ��ؼ����ĺš���˧�����ǿ�");
	
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	public void setShareService(ShareService shareService) {
		this.shareService = shareService;
	}
	@Autowired@Qualifier("operationService")
	private OperationService operationService;
	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}

	@RequestMapping("/subject/proxy/blackmen/sendsinafriendMsg.xhtml")
	public String blackmensendsinafriendMsg(Long memberid, ModelMap model){
		if(memberid == null) return showJsonError(model, "���ȵ�¼��");
		List<ShareMember> shareMemberList = shareService.getShareMemberByMemberid(Arrays.asList(MemberConstant.SOURCE_SINA),memberid);
		if(VmUtils.isEmptyList(shareMemberList)) return showJsonError(model, "���Ȱ�����΢����");
		String opkey = "blackmenActivity" + memberid;
		if(!operationService.isAllowOperation(opkey, OperationService.ONE_HOUR, 3)){
			return showJsonError(model, "��������Ƶ����");
		}
		List<String> friendsList = shareService.getSinaFriendList(memberid, 5);
		String resultString = "";
		if(friendsList.isEmpty()){
			resultString = "@������������ "+RandomUtil.getRandomObjectList(stringList, 1).get(0);
		}else{
			List tempList = RandomUtil.getRandomObjectList(stringList, friendsList.size());
			for(int i = 0; i<friendsList.size(); i++){
				resultString += "@" + friendsList.get(i) + " "+ tempList.get(i);
			}
		}
		operationService.updateOperation(opkey, OperationService.ONE_DAY, 3);
		return showJsonSuccess(model, resultString);
	}
	
}