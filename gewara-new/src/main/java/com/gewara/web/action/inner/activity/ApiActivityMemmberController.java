package com.gewara.web.action.inner.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.ApiConstant;
import com.gewara.model.user.MemberInfo;
import com.gewara.web.action.api.BaseApiController;
@Controller
public class ApiActivityMemmberController extends BaseApiController {
	/**
	 * �����û�id���ϲ�ѯ�û�
	 * @param memberIds
	 * @param model
	 * @return
	 */
	@RequestMapping("/inner/activity/member/getMemberInfoList.xhtml")
	public String getMemberInfoList(String memberids, ModelMap model){
		if(StringUtils.isBlank(memberids)) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "��������");
		List<String> memberidList = Arrays.asList(StringUtils.split(memberids, ","));
		List<Long> memberIds = new ArrayList<Long>();
		for (String string : memberidList) {
			memberIds.add(Long.parseLong(string));
		}
		List<MemberInfo> memberinfoList = new ArrayList<MemberInfo>();
		if(memberIds.size() > 0) {
			memberinfoList = daoService.getObjectList(MemberInfo.class, memberIds);
		}
		model.put("memberinfoList", memberinfoList);
		return getXmlView(model, "inner/activity/memberInfoList.vm");
	}
}
