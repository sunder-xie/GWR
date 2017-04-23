package com.gewara.web.action.admin.ajax;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.DiaryConstant;
import com.gewara.constant.ExpGrade;
import com.gewara.constant.Status;
import com.gewara.constant.SysAction;
import com.gewara.constant.TagConstant;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.helper.sys.RelateClassHelper;
import com.gewara.model.BaseObject;
import com.gewara.model.acl.User;
import com.gewara.model.bbs.Bkmember;
import com.gewara.model.bbs.BlackMember;
import com.gewara.model.bbs.Diary;
import com.gewara.model.bbs.DiaryBase;
import com.gewara.model.bbs.DiaryComment;
import com.gewara.model.common.RelateToCity;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Agenda;
import com.gewara.model.user.MemberInfo;
import com.gewara.model.user.SysMessageAction;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.bbs.DiaryService;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.SearchService;
import com.gewara.util.BeanUtil;
import com.gewara.util.BindUtils;
import com.gewara.util.ChangeEntry;
import com.gewara.util.DateUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.web.action.admin.BaseAdminController;
import com.gewara.web.util.ContentHelper;
import com.gewara.xmlbind.bbs.Comment;
@Controller
public class BlogAdminAjaxController extends BaseAdminController{
	@Autowired@Qualifier("blogService")
	private BlogService blogService = null;
	public void setBlogService(BlogService blogService) {
		this.blogService = blogService;
	}
	@Autowired@Qualifier("commentService")
	private CommentService commentService;
	@Autowired@Qualifier("diaryService")
	private DiaryService diaryService;
	public void setDiaryService(DiaryService diaryService) {
		this.diaryService = diaryService;
	}

	@Autowired@Qualifier("searchService")
	private SearchService searchService;
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	/****************************************************************
	 * @param diaryId
	 * @return
	 */
	@RequestMapping("/admin/blog/getDiaryById.xhtml")
	public String getDiaryById(Long diaryId, ModelMap model){
		DiaryBase diary = diaryService.getDiaryBase(diaryId);
		if(diary == null) return showJsonError(model, "�����Ӳ����ڻ�ɾ����");
		String diaryBody = blogService.getDiaryBody(diary.getId());
		Map map = BeanUtil.getBeanMap(diary, false);
		map.put("diaryBody", diaryBody);
		return showJsonSuccess(model, map);
	}
	/**
	 * ��̨
	 * �����޸�Diary
	 * @param tag
	 * @param relatedid
	 * @param diaryId
	 * @param subject
	 * @param body
	 * @param summary
	 * @return
	 */
	@RequestMapping("/admin/blog/updateDiary.xhtml")
	public String updateDiary(Long diaryId, Timestamp addtimenew, String reason1, String body,
			ModelMap model, HttpServletRequest request){
		DiaryBase diary = diaryService.getDiaryBase(diaryId);
		BindUtils.bindData(diary, request.getParameterMap());
		if(StringUtils.isBlank(diary.getSubject())) return showJsonError(model, "���ⲻ��Ϊ�գ�");
		if(StringUtils.isBlank(body)) return showJsonError(model, "���ݲ���Ϊ�գ�");
		//��֤����
		String msg=ValidateUtil.validateNewsContent(null, body);
		if(StringUtils.isNotBlank(msg))return showJsonError(model, msg);
		diary.setUpdatetime(new Timestamp(System.currentTimeMillis()));
		if(addtimenew!=null) diary.setAddtime(addtimenew);
		daoService.updateObject(diary);
		blogService.saveDiaryBody(diary.getId(), null, body);
		SysMessageAction sysMessage=new SysMessageAction(SysAction.STATUS_RESULT);
		sysMessage.setFrommemberid(getLogonUser().getId());
		sysMessage.setBody("���ķ���ġ�"+diary.getSubject()+"��,������Ա�����±༭,ԭ��" + reason1);
		sysMessage.setTomemberid(diary.getMemberid());
		daoService.saveObject(sysMessage);
		return showJsonSuccess(model);
	}
	
	
	/**
	 * ��̨
	 * @param diaryId
	 * @param type
	 * @return
	 */
	@RequestMapping("/admin/blog/updateDiaryType.xhtml")
	public String updateDiaryType(Long diaryId, String type, ModelMap model){
		String dtype = DiaryConstant.DIARY_TYPE_MAP.get(type);
		if(dtype == null) return showJsonError(model, "���ʹ���");
		DiaryBase diary = diaryService.getDiaryBase(diaryId);
		diary.setType(dtype);
		daoService.saveObject(diary);
		return showJsonSuccess(model);
	}
	
	/**
	 * ��̨
	 * �߼�ɾ����־��status=deleted
	 * @param diaryId
	 * @return
	 */
	@RequestMapping("/admin/blog/deleteDiary.xhtml")
	public String deleteDiary(Long diaryId,String reason,String reasonDetail, Long relatewara, ModelMap modelMap){
		if(!reason.equals("5")){
			reason = ContentHelper.REASONS.get(reason);
		}else{
			reason = reasonDetail;
		}
		DiaryBase diary = diaryService.getDiaryBase(diaryId);
		if(diary == null) return showJsonError(modelMap, "�����Ӳ����ڻ�ɾ����");
		ChangeEntry changeEntry = new ChangeEntry(diary);
		diary.setStatus(Status.N_DELETE);
		daoService.saveObject(diary);
		SysMessageAction sysMessage=new SysMessageAction(SysAction.STATUS_RESULT);
		sysMessage.setFrommemberid(1l);
		sysMessage.setBody("������ġ�"+diary.getSubject().substring(0,diary.getSubject().length()>5?5:diary.getSubject().length())+"...�������漰��"+reason+"��,�ѱ�����Աɾ��,<br/>�����κ����ʣ���ʹ��վ���Ż��ʼ���gewara@gewara.com��<br/>�����Ա���ߡ�");
		sysMessage.setTomemberid(diary.getMemberid());
		daoService.saveObject(sysMessage);
		memberService.addExpForMember(diary.getMemberid(), -ExpGrade.EXP_DIARY_SUB);
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_USERACTION, "����Ա����"+getLogonUser().getUsername()+"��ɾ�����ӡ�"+diary.getSubject()+"����״̬Ϊ"+Status.N_DELETE+"����ID��"+diary.getId());
		
		// ���� wala
		if(relatewara != null){
			Comment comment = commentService.getCommentById(relatewara);
			comment.setStatus(Status.N_DELETE);
			commentService.saveComment(comment);
		}
		searchService.pushSearchKey(diary);
		monitorService.saveChangeLog(getLogonUser().getId(), Diary.class, diary.getId(), changeEntry.getChangeMap(diary));
		return showJsonSuccess(modelMap);
	}
	/**
	 * �ָ�����״̬
	 * @param model
	 * @param diaryid
	 * @return
	 */
	@RequestMapping("/admin/blog/resumeDiary.xhtml")
	public String resumeDiary(ModelMap model, Long diaryid){
		DiaryBase diary = diaryService.getDiaryBase(diaryid);
		if(diary==null){
			return showJsonError(model, "�����Ӳ�����!");
		}
		diary.setStatus(Status.Y_NEW);
		daoService.saveObject(diary);
		searchService.pushSearchKey(diary);
		//��վ��ϵͳ��Ϣ
		SysMessageAction sysMessage=new SysMessageAction(SysAction.STATUS_RESULT);
		sysMessage.setFrommemberid(1l);
		sysMessage.setBody("������ġ�"+diary.getSubject().substring(0,diary.getSubject().length()>5?5:diary.getSubject().length())+"...�������ѱ�����Ա�ָ�,<br/>�����κ����ʣ���ʹ��վ���Ż��ʼ���gewara@gewara.com��<br/>�����Ա���ߡ�");
		sysMessage.setTomemberid(diary.getMemberid());
		daoService.saveObject(sysMessage);
		//�ָ�����ֵ
		memberService.addExpForMember(diary.getMemberid(), ExpGrade.EXP_DIARY_SUB);
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_USERACTION, "����Ա����"+getLogonUser().getUsername()+"���ָ������ӡ�"+diaryid+"����״̬Ϊ"+Status.Y_NEW);
		return showJsonSuccess(model);
	}
	/**
	 * �ָ����ӻָ�״̬
	 * @param model
	 * @param diarycommentid
	 * @return
	 */
	@RequestMapping("/admin/blog/resumeDiaryComment.xhtml")
	public String resumeDiaryComment(ModelMap model, Long diarycommentid){
		DiaryComment diaryComment = daoService.getObject(DiaryComment.class, diarycommentid);
		if(diaryComment == null) return showJsonError(model, "���������Բ�����!");
		diaryComment.setStatus(Status.Y_NEW);
		memberService.addExpForMember(diaryComment.getMemberid(), ExpGrade.EXP_DIARY_REPLYER_ADD);
		daoService.saveObject(diaryComment);
		dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_USERACTION, "����Ա����"+getLogonUser().getUsername()+"���ָ����������ԡ�"+diarycommentid+"����״̬Ϊ"+Status.Y_NEW);
		return showJsonSuccess(model);
	}
	
	/**
	 * ��̨
	 *	����ɾ��
	 */
	@RequestMapping("/admin/blog/batchDeleteDiary.xhtml")
	public String batchDeleteDiary(String idListString, String reason,String reasonDetail, ModelMap model){
		if(!reason.equals("5")){
			reason = ContentHelper.REASONS.get(reason);
		}else{
			reason = reasonDetail;
		}
		DiaryBase diary = null;
		SysMessageAction sysMessage = null;
		String[] idList = StringUtils.split(idListString, ',');
		if(idList==null) return showJsonError(model, "��ѡ��Ҫɾ���ļ�¼��");
		for(String idString : idList){
			diary = diaryService.getDiaryBase(new Long(idString)); 
			if(diary != null){
				String status = diary.getStatus();
				diary.setStatus(Status.N_DELETE);
				diary.setUpdatetime(new Timestamp(System.currentTimeMillis()));
				daoService.saveObject(diary);
				sysMessage = new SysMessageAction(SysAction.STATUS_RESULT);
				sysMessage.setFrommemberid(1l);
				sysMessage.setBody("������ġ�"+diary.getSubject().substring(0,diary.getSubject().length()>5?5:diary.getSubject().length())+"...�������漰��"+reason+"��,�ѱ�����Աɾ��,<br/>�����κ����ʣ���ʹ��վ���Ż��ʼ���gewara@gewara.com��<br/>�����Ա���ߡ�");
				sysMessage.setTomemberid(diary.getMemberid());
				daoService.saveObject(sysMessage);
				searchService.pushSearchKey(diary);
				memberService.addExpForMember(diary.getMemberid(), -ExpGrade.EXP_DIARY_SUB);
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_USERACTION, "����Ա����"+getLogonUser().getUsername()+"���ı������ӡ�"+diary.getId()+"����״̬"+status +"Ϊ"+ Status.N_DELETE);
			}
		}
		return showJsonSuccess(model);
	}
	/**
	 * ��̨
	 */
	@RequestMapping("/admin/blog/addBlackMember.xhtml")
	public String addBlackMember(Long memberId, String description, ModelMap model){
		User user = getLogonUser();
		List<BlackMember> blackMemberList = blogService.getBlackMemberList(memberId,-1,-1);
		if(blackMemberList.isEmpty()){
			BlackMember bm = new BlackMember(memberId, description, user.getId());
			daoService.saveObject(bm);
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_USERACTION, "����Ա����"+getLogonUser().getUsername()+"���ѱ��Ϊ��"+memberId+"�����û������������");
			return showJsonSuccess(model);
		}
		return showJsonError(model, "���ں������У�");
	}
	@RequestMapping("/admin/blog/addBlackMemberByIds.xhtml")
	public String addBlackMemberByIds(String memberIds, String description, ModelMap model){
		User user = getLogonUser();
		String[] ids = StringUtils.split(memberIds, ",");
		List<Long> blackList = new ArrayList<Long>();
		if(ids != null){
			for (String id : ids) {
				Long memberId = Long.parseLong(id);
				if(blackList.contains(memberId)) continue;
				blackList.add(memberId);
				List<BlackMember> blackMemberList = blogService.getBlackMemberList(memberId,-1,-1);
				if(blackMemberList.isEmpty()){
					BlackMember bm = new BlackMember(memberId, description, user.getId());
					daoService.saveObject(bm);
					dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_USERACTION, "����Ա����"+getLogonUser().getUsername()+"���ѱ��Ϊ��"+memberId+"�����û������������");
				}
			}
		}
		return showJsonSuccess(model);
	}
	
	/**
	 * ��̨ 
	 */
	@RequestMapping("/admin/blog/removeBlackMember.xhtml")
	public String removeBlackMember(Long blackMemberId, ModelMap model){
		daoService.removeObjectById(BlackMember.class, blackMemberId);
		return showJsonSuccess(model);
	}
	
	
	/***
	 * ��̨ 
	 */
	@RequestMapping("/admin/blog/updateDiaryFlag.xhtml")
	public String updateDiaryFlag(String flag, Long diaryId, String vflag, ModelMap model){
		DiaryBase diary = diaryService.getDiaryBase(diaryId);
		List<String> flagList = new ArrayList<String>();
		if(StringUtils.isNotBlank(diary.getFlag())){
			String[] flagArr=StringUtils.split(diary.getFlag(), ",");
			for(int i=0;i<flagArr.length;i++){
				flagList.add(flagArr[i]);
			}
		}
		if(flagList.contains(vflag)) {//ɾ��
			for(int i=0;i<flagList.size();i++){
				if(flagList.get(i).equals(vflag))
					flagList.remove(vflag);
			}
		}else if(!flagList.contains(vflag)){
			flagList.add(vflag);//���
			if(StringUtils.equals(vflag, "hot")){
				if(StringUtils.equals(diary.getType(), DiaryConstant.DIARY_TYPE_COMMENT)){
					memberService.addExpForMember(diary.getMemberid(), ExpGrade.EXP_TALK_HOT);
				}else if(StringUtils.equals(diary.getType(), DiaryConstant.DIARY_TYPE_TOPIC_DIARY) || StringUtils.equals(diary.getType(), DiaryConstant.DIARY_TYPE_TOPIC_VOTE)){
					memberService.addExpForMember(diary.getMemberid(), ExpGrade.EXP_DIARY_HOT);
				}
			}
			if(StringUtils.equals(vflag, "recommend")){
				memberService.addExpForMember(diary.getMemberid(), ExpGrade.EXP_DIARY_RECOMMEND);
			}
			if(StringUtils.equals(flag, "top1") && !flagList.contains("top2")) flagList.add("top2");//����̳�ö������������̳Ҳ�ö�
		}
		String toFlag="";
		for(String str: flagList){
			toFlag=toFlag+","+str;
		}
		if(StringUtils.isNotBlank(toFlag)){
			if(toFlag.startsWith(",")) toFlag = toFlag.substring(1);
			if(toFlag.endsWith(",")) toFlag = toFlag.substring(0, toFlag.length()-1);
		}
		diary.setFlag(toFlag);
		diary.setUpdatetime(new Timestamp(System.currentTimeMillis()));
		List listFlags = Arrays.asList(StringUtils.split(diary.getFlag(), ","));
		if(listFlags.contains("top1")){
			SysMessageAction sysmessage=new SysMessageAction(SysAction.STATUS_RESULT);
			sysmessage.setFrommemberid(getLogonUser().getId());
			sysmessage.setBody("�������ӡ�"+diary.getSubject()+"���ѱ�����Ա�ö���������20����ֵ!^_^");
			sysmessage.setTomemberid(diary.getMemberid());
			daoService.saveObject(sysmessage);

			MemberInfo member=daoService.getObject(MemberInfo.class, diary.getMemberid());
			member.setExpvalue(member.getExpvalue()+20);
			daoService.updateObject(member);
		}
		daoService.updateObject(diary);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/admin/blog/updateRelateToCityFlag.xhtml")
	public String updateRelateToCityFlag(String flag, Long relatedid, String citycode, String tag, String vflag, ModelMap model){
		if(StringUtils.isBlank(citycode)||StringUtils.isBlank(tag)||relatedid == null) return showJsonError(model, "��������");
		BaseObject object = null;
		if(StringUtils.equals(tag, TagConstant.TAG_DIARY)){
			object = diaryService.getDiaryBase(relatedid);
		}else object = (BaseObject)relateService.getRelatedObject(tag, relatedid);
		ChangeEntry changeEntry = new ChangeEntry(object);
		String division = (String)BeanUtil.get(object, "division");
		String  oldFlag = (String)BeanUtil.get(object, "flag");
		String oldCitycode = (String)BeanUtil.get(object, "citycode");
		RelateToCity relateToCity = null;
		List<String> flagList = new ArrayList<String>();
		if(StringUtils.equals(division, DiaryConstant.DIVISION_N)){
			List<RelateToCity> reList = commonService.getRelateToCity(tag, relatedid, citycode, null);
			if(!reList.isEmpty()){
				relateToCity = reList.get(0);
			}
		}
		if(relateToCity != null){
			oldFlag = relateToCity.getFlag();
		}
		if(StringUtils.isNotBlank(oldFlag)){
			flagList.addAll(Arrays.asList(StringUtils.split(oldFlag, ",")));
		}
		if(flagList.contains(vflag)) {//ɾ��
			for(int i=0;i<flagList.size();i++){
				if(flagList.get(i).equals(vflag))
					flagList.remove(vflag);
			}
		}else if(!flagList.contains(vflag)){
			flagList.add(vflag);//���
			if(StringUtils.equals(flag, "top1") && !flagList.contains("top2")) flagList.add("top2");//����̳�ö������������̳Ҳ�ö�
		}
		String toFlag = StringUtils.join(flagList.toArray(), ",");
		if(relateToCity != null){
			ChangeEntry changeEntry2 = new ChangeEntry(relateToCity);
			relateToCity.setFlag(toFlag);
			if(StringUtils.equals(oldCitycode, citycode)){
				BeanUtil.set(object, "flag", toFlag);
				BeanUtil.set(object, "updatetime", DateUtil.getCurFullTimestamp());
				daoService.saveObject(object);
				monitorService.saveChangeLog(getLogonUser().getId(), RelateClassHelper.getRelateClazz(tag), relatedid, changeEntry.getChangeMap(object));
			}
			daoService.saveObject(relateToCity);
			monitorService.saveChangeLog(getLogonUser().getId(), RelateToCity.class, relateToCity.getId(), changeEntry2.getChangeMap(relateToCity));
		}else{
			BeanUtil.set(object, "flag", toFlag);
			BeanUtil.set(object, "updatetime", DateUtil.getCurFullTimestamp());
			daoService.saveObject(object);
			monitorService.saveChangeLog(getLogonUser().getId(), RelateClassHelper.getRelateClazz(tag), relatedid, changeEntry.getChangeMap(object));
		}
		return showJsonSuccess(model);
	}
	/***
	 * ��̨ 
	 */
	@RequestMapping("/admin/blog/updateDiaryStatus.xhtml")
	public String updateDiaryStatus(Long did, String value, ModelMap model){
		DiaryBase diary = diaryService.getDiaryBase(did);
		if(diary!=null){
			String oldStatus = diary.getStatus();
			if(oldStatus.indexOf(Status.Y_DOWN)>=0){
				if(value.indexOf(Status.Y_DOWN)<0) diary.setUtime(diary.getReplytime());
				else diary.setUtime(Timestamp.valueOf("2007-01-01 00:00:00"));
			}else { 
				if(value.indexOf(Status.Y_DOWN)>=0) diary.setUtime(Timestamp.valueOf("2007-01-01 00:00:00"));
				else diary.setUtime(diary.getReplytime());
			}
			diary.setStatus(value);
			daoService.saveObject(diary);
			searchService.pushSearchKey(diary);//��������������������
			return showJsonSuccess(model);
		}
		return showJsonError_DATAERROR(model);
	}

	/**
	 * ��̨����ɾ������
	 * @param model
	 * @param idListString
	 * @return
	 */
	@RequestMapping("/admin/blog/deleteAgendaList.xhtml")
	public String deleteAgndaList(ModelMap model, String idListString){
		String[] idList = StringUtils.split(idListString, ",");
		if(idList == null) return showJsonError(model, "��ѡ��Ҫɾ���ļ�¼��");
		Agenda agenda = null;
		for(String idString : idList){
			agenda = daoService.getObject(Agenda.class, new Long(idString));
			if(agenda == null) return showJsonError_NOT_FOUND(model);
			daoService.removeObject(agenda);
		}
		return showJsonSuccess(model);
	}
	
	/**
	 * ��̨�����޸������״̬
	 * @param model
	 * @param idListString
	 * @param status
	 * @return
	 */
	@RequestMapping("/admin/blog/updateAgendaList.xhtml")
	public String updateAgendaList(ModelMap model, String idListString, String status){
		String[] idList = StringUtils.split(idListString, ",");
		if(idList==null) return showJsonError(model, "��ѡ��Ҫɾ���ļ�¼��");
		List<SMSRecord> smsList = null;
		for(String idString : idList){
			smsList = daoService.getObjectListByField(SMSRecord.class, "relatedid", new Long(idString));
			if(smsList == null || StringUtils.isBlank(status)) continue;
			for(SMSRecord sms : smsList){
				sms.setStatus(status);
				daoService.saveObject(sms);
			}
		}
		return showJsonSuccess(model);
	}
	/**
	 * ɾ������
	 * @param tag
	 * @param relatedid
	 * @param banzhuId
	 * @return
	 */
	@RequestMapping("/admin/blog/removeBanzhu.xhtml")
	public String removeBanzhu(Long banzhuId, ModelMap model) {
		Bkmember bkmember = daoService.getObject(Bkmember.class, banzhuId);
		if (bkmember == null) return showJsonError(model, "�ð��������ڻ��Ѿ�ɾ����");
		daoService.removeObject(bkmember);
		return showJsonSuccess(model);
	}
	/**
	 * ���óɰ���
	 * @param tag
	 * @param relatedid
	 * @param banzhuId
	 * @return
	 */
	@RequestMapping("/admin/blog/setToBanzhu.xhtml")
	public String setToBanzhu(Long banzhuId, ModelMap model) {
		Bkmember bkmember = daoService.getObject(Bkmember.class, banzhuId);
		if (bkmember == null) return showJsonError(model, "�ð��������ڻ��Ѿ�ɾ����");
		bkmember.setRole(Bkmember.ROLE_BANZHU);
		daoService.saveObject(bkmember);
		return showJsonSuccess(model); //"�ɹ����Ӱ���Ȩ�ޣ�";
	}

	@RequestMapping("/admin/blog/setToManager.xhtml")
	public String setToManager(Long banzhuId, ModelMap model) {
		Bkmember bkmember = daoService.getObject(Bkmember.class, banzhuId);
		if (bkmember == null) return showJsonError(model, "�ð��������ڻ��Ѿ�ɾ����");
		bkmember.setRole(Bkmember.ROLE_MANAGER);
		daoService.saveObject(bkmember);
		return showJsonSuccess(model); //"�ɹ����ӹ���Ա��ݣ�";
	}
}
