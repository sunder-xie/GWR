package com.gewara.web.action.inner.common;

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
import com.gewara.service.bbs.BlogService;
import com.gewara.service.bbs.DiaryService;
import com.gewara.util.BeanUtil;
import com.gewara.web.action.api.BaseApiController;

@Controller
public class ApiDaryController extends BaseApiController {
	
	@Autowired@Qualifier("diaryService")
	private DiaryService diaryService;
	
	@Autowired@Qualifier("blogService")
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
	@RequestMapping("/inner/common/list/diaryListByTag.xhtml")
	public String diaryList(String tag, Long relateid, String type,	String orderField, String asc, Integer from, Integer maxnum,
			@RequestParam(defaultValue = "id,subject", required = false, value = "returnField") String returnField,
			ModelMap model){
		if(StringUtils.isBlank(tag) || from == null || maxnum== null || from <0 || maxnum <= 0 || StringUtils.isBlank(asc)) return getErrorXmlView(model, ApiConstant.CODE_SIGN_ERROR, "��������");
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
		model.put("diaryList", diaryList);
		model.put("diaryContentMap", diaryContentMap);
		model.put("returnField", returnField);
 		return getXmlView(model,"inner/diary/diaryList.vm");
	}
	
	/**
	 * ��������id���ϲ�ѯ����
	 * @param diaryIds ����id����
	 * @param model
	 * @return
	 */
	@RequestMapping("/inner/common/diary/getIdList.xhtml")
	public String getDiaryList(String ids, ModelMap model){
		if(ids == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "��������");
		List<Long> idList = BeanUtil.getIdList(ids, ",");
		if(!idList.isEmpty()){
			List<Diary> diaryList = daoService.getObjectList(Diary.class, idList);
			model.put("diaryList", diaryList);
		}
		return getXmlView(model,"inner/diary/diaryLists.vm");
	}
}
