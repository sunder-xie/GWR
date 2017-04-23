package com.gewara.web.action.inner.member;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.SmsConstant;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.service.bbs.BlogService;
import com.gewara.untrans.ShareService;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.web.action.api.BaseApiController;

@Controller
public class InnerApiMemberController extends BaseApiController{
	
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	
	@Autowired@Qualifier("blogService")
	private BlogService blogService;

	/** * ����΢�� * @param tag * @param tagid * @param memberid * @param content * @param picURL
	*/
	@RequestMapping("/inner/member/shareInfo.xhtml")
	public String sendShareInfo(String tag, Long tagid, Long memberid, String content, String picUrl, ModelMap model){
		if(memberid == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "�û�ID�����ڣ�");
		if(StringUtils.isBlank(content))  return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "�������ݲ���Ϊ�գ�");
		try{
			shareService.sendShareInfo(tag, tagid, memberid, content, picUrl);
		}catch(Exception e){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "����΢��ʧ�ܣ�");
		}
		return getXmlView(model, "api/mobile/result.vm");
	}
	
	/**
	 * ��ȡ�û���Ϣ
	 * @param memberId
	 * @return
	*/
	@RequestMapping("/inner/member/getMemberInfo.xhtml")
	 public String getMemberInfo(Long memberId, ModelMap model){
		 if(memberId == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "��������");
		 MemberInfo memberInfo = daoService.getObject(MemberInfo.class, memberId);
		 model.put("memberInfo", memberInfo);
		 return getXmlView(model, "api2/member/memberInfo.vm");
	 }
	
	/**
	 * �����ֻ�����
	 * @param phones �ֻ���,��","�ָ���18721511111,18721511112
	 * @param content
	 */
	@RequestMapping("/inner/activity/sendSMS.xhtml")
	public String sendSMS(String phones, String content, ModelMap model){
		if(phones == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "phonesΪ�գ�");
		if(content == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "contentΪ�գ�");
		String[] mobiles = StringUtils.split(phones, ",");
		boolean isSendMsg = StringUtils.isNotBlank(blogService.filterAllKey(content));//�ж��Ƿ��б�ǩ����������
		Timestamp curtime = DateUtil.getCurFullTimestamp();
		for(int i = 0;i < mobiles.length;i++){
			if(ValidateUtil.isMobile(mobiles[i])){
				SMSRecord sms = new SMSRecord(mobiles[i]);
				if(isSendMsg){
					sms.setStatus(SmsConstant.STATUS_FILTER);
				}
				sms.setTradeNo(DateUtil.format(curtime, "yyMMddHHmmss"));
				sms.setContent(content);
				sms.setSendtime(curtime);
				sms.setSmstype(SmsConstant.SMSTYPE_MANUAL);
				sms.setValidtime(DateUtil.getLastTimeOfDay(curtime));
				sms = untransService.addMessage(sms);
				if(sms!=null)untransService.sendMsgAtServer(sms, true);
			}else{
				return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "������Ĳ����ֻ���ʽ����ȷ��");
			}
		}
		return getSingleResultXmlView(model, "true");
	}
	
	/**
	 * �����û�����ֵ
	 * @param memberId
	 * @param exp
	 */
	@RequestMapping("/inner/activity/addExpForMember.xhtml")
	public String addExpForMember(String memberEncode, Integer exp, ModelMap model){
		if(memberEncode == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "memberIdΪ�գ�");
		Member member =  memberService.getMemberByEncode(memberEncode);
		if(member==null) return getErrorXmlView(model, ApiConstant.CODE_MEMBER_NOT_EXISTS, "�û������ڣ�");
		if(exp == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "expΪ�գ�");
		memberService.addExpForMember(member.getId(), exp);
		return getSingleResultXmlView(model, true);
	}

	/**
	 * ��ȡ�û��ǳơ�ͷ����Ϣ
	 * @param ids
	 * @param model
	 * @return
	 */
	@RequestMapping("/inner/common/member/getIdList.xhtml")
	public String getMemberInfoList(String ids, ModelMap model){
		if(StringUtils.isBlank(ids)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
		List<Long> idList = BeanUtil.getIdList(ids, ",");
		addCacheMember(model, idList);
		model.put("memberIdList", idList);
		return getXmlView(model, "inner/common/cacheMemberList.vm");
	}
	
	/**
	 * �ж��û��Ƿ��ں�������
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/inner/common/member/inBlackList.xhtml")
	public String inBlackList(Long memberId, ModelMap model){
		if(memberId == null) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
		boolean result = false;
		if (blogService.isBlackMember(memberId)) result = true;
		return getSingleResultXmlView(model, result);
	}
	
}
