package com.gewara.web.action.inner.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.ApiConstant;
import com.gewara.model.sport.Sport;
import com.gewara.web.action.api.BaseApiController;
@Controller
public class ApiActivitySportController extends BaseApiController{
	/**
	 * ��ȡ�����б�
	 * @param movieids
	 * @return
	 */
	@RequestMapping("/inner/activity/sport/getSportByIds.xhtml")
	public String getMovieByIds(String sportids, ModelMap model){
		if(StringUtils.isBlank(sportids)) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "��������");
		List<String> sportidList = Arrays.asList(StringUtils.split(sportids, ","));
		List<Long> sportIds = new ArrayList<Long>();
		for (String string : sportidList) {
			sportIds.add(Long.parseLong(string));
		}
		List<Sport> sportList = daoService.getObjectList(Sport.class, sportIds);
		model.put("sportList", sportList);
		return getXmlView(model, "inner/activity/sportList.vm");
	}
	/**
	 * ��ȡ�˶���Ϣ
	 * @param movieids
	 * @return
	 */
	@RequestMapping("/inner/activity/sport/getSport.xhtml")
	public String getsportById(Long sportid, ModelMap model){
		if(sportid == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "��������");
		Sport sport = daoService.getObject(Sport.class, sportid);
		if(sport == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "���ݲ����ڣ�");
		model.put("sport", sport);
		return getXmlView(model, "inner/activity/sportDetail.vm");
	}
}
