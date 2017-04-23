package com.gewara.web.action.api2.bbs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.ApiConstant;
import com.gewara.model.bbs.Diary;
import com.gewara.model.user.Member;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.bbs.DiaryService;
import com.gewara.util.BeanUtil;
import com.gewara.web.action.api.BaseApiController;

/**
 * ��־API
 * @author taiqichao
 *
 */
@Controller
public class Api2DiaryController extends BaseApiController{

	@Autowired
	@Qualifier("diaryService")
	private DiaryService diaryService;
	
	@Autowired
	@Qualifier("blogService")
	private BlogService blogService;
	
	/**
	 * ��־�б� 
	 * @param tag ��ǩ
	 * @param relateid ��������id
	 * @param type ����  comment(Ӱ�����ĵã�����),topic_diary(һ������),topic_vote_radio(ͶƱ����ѡ��),topic_vote_multi(ͶƱ����ѡ��),topic(��������),topic_vote(ͶƱ)
	 * @param orderField �����ֶ�
	 * @param from ��ǰҳ��
	 * @param maxnum ҳ��С
	 * @param returnField �����ֶ� (diaryid,subject,memberid,nickname,memberlogo,summary,flowernum,replycount,diaryImage,content)
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/diary/diaryList.xhtml")
	public String diaryList(
			String tag,
			Long relateid,
			String type,
			String orderField,
			String asc,
			@RequestParam(defaultValue = "0", required = false, value = "from") Integer from,
			@RequestParam(defaultValue = "20", required = false, value = "maxnum") Integer maxnum,
			@RequestParam(defaultValue = "id,subject", required = false, value = "returnField") String returnField,
			ModelMap model){
		if (maxnum > 50) {
			maxnum = 50;
		}
		if("diary".equals(type)){
			type = "%diary";
		}
		List<Diary> diaryList = diaryService.getDiaryListByOrder(Diary.class, null, type, tag, relateid, null, null, orderField, Boolean.parseBoolean(asc), from, maxnum);
		Map<Long,String> diaryContentMap = new HashMap<Long,String>();
		if(returnField.indexOf("content")!=-1){//��ϸ����
			for(Diary diary:diaryList){
				String diaryContent  = blogService.getDiaryBody(diary.getId());
				diaryContentMap.put(diary.getId(),diaryContent);
			}
			List<Long> memberidList = BeanUtil.getBeanPropertyList(diaryList, Long.class, "memberid", true);
			addCacheMember(model, memberidList);
		}
		
		int count=diaryService.getDiaryCount(Diary.class, null, type, tag, relateid);
		model.put("count", count);
		model.put("diarys", diaryList);
		model.put("diaryContentMap", diaryContentMap);
		model.put("returnField", returnField);
 		return getXmlView(model,"api/mobile/diaryList.vm");
	}
	
	/**
	 * ��־��ϸ
	 * 
	 * @param diaryid
	 * @param returnField
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/diary/diaryDetail.xhtml")
	public String diaryDetail(Long diaryid,String returnField,ModelMap model){
		if(diaryid == null){
			return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "diaryid is not null");
		}
		if(StringUtils.isBlank(returnField)){
			returnField = "id,subject";
		}
		model.put("returnField", returnField);
		model.put("diary", this.daoService.getObject(Diary.class, diaryid));
		model.put("content", blogService.getDiaryBody(diaryid));
		return getXmlView(model,"api/mobile/diaryDetail.vm");
	}
	
	/**
	 * ��������id���ϲ�ѯ����
	 * @param diaryIds ����id����
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/diary/getDiaryList.xhtml")
	public String getDiaryList(String ids, ModelMap model){
		if(ids == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "��������");
		List<String> memberidList = Arrays.asList(StringUtils.split(ids, ","));
		List<Long> memberIds = new ArrayList<Long>();
		for (String string : memberidList) {
			memberIds.add(Long.parseLong(string));
		}
		List<Diary> diaryLists = null;
		if(memberIds.size() > 0)
			diaryLists = daoService.getObjectList(Diary.class, memberIds);
		model.put("diaryLists", diaryLists);
		return getXmlView(model,"api2/diary/diaryLists.vm");
	}
	
	/**
	 * ��������id���ϲ�ѯ����
	 * @param diaryIds ����id����
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/diary/addFlower.xhtml")
	public String getDiaryList(Long diaryid, String memberEncode, String tag, ModelMap model){
		if(StringUtils.isBlank(memberEncode)) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "ȱ�ٲ���memberEncode");
		Member member = memberService.getMemberByEncode(memberEncode);
		if(member==null) {
			return getErrorXmlView(model,  ApiConstant.CODE_DATA_ERROR, "�û������ڣ�");
		}
		String opkey = "adf" + member.getId() + diaryid;
		if(!operationService.updateOperationOneDay(opkey, true)) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "�����ظ�����");
		Diary diary = daoService.getObject(Diary.class, diaryid);
		if(diary == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "���ݲ�����");
		if(StringUtils.equals(tag, "oppose")){ //��������
			diary.addPoohnum();
		}else {
			diary.addFlowernum();
		}
		daoService.saveObject(diary);
		return getSingleResultXmlView(model, diary.getFlowernum());
	}
}
