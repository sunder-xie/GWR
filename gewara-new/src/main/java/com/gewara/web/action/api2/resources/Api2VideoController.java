package com.gewara.web.action.api2.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.model.content.Video;
import com.gewara.service.content.VideoService;
import com.gewara.web.action.api.BaseApiController;

/**
 * ��ƵAPI
 * 
 * @author taiqichao
 * 
 */
@Controller
public class Api2VideoController extends BaseApiController {

	@Autowired
	@Qualifier("videoService")
	private VideoService videoService;

	/**
	 * ��ѯ��Ƶ
	 * 
	 * @param tag
	 *            ����
	 * @param relatedid
	 *            ��������
	 * @param from
	 *            ��ǰҳ��
	 * @param maxnum
	 *            ҳ��С
	 * @param orderby 
	 *            ���� ֵ:updatetime,hotvalue
	 * @param model
	 * @return
	 */
	@RequestMapping("/api2/video/videoList.xhtml")
	public String videoList(
			@RequestParam("tag") String tag,
			Long relatedid,
			@RequestParam(defaultValue = "0", required = false, value = "from") Integer from,
			@RequestParam(defaultValue = "20", required = false, value = "maxnum") Integer maxnum,
			@RequestParam(required = false, value = "orderby") String orderby,
			ModelMap model) {
		if (maxnum > 20) {
			maxnum = 20;
		}
		int firstRow = from * maxnum;
		List<Video> videoList = videoService.getVideoListByTag(tag, relatedid,null , orderby, false, firstRow, maxnum);
		int count = videoService.getVideoCountByTag(tag, relatedid,null);
		model.put("videoList", videoList);
		model.put("count", count);
		return getXmlView(model, "api2/resources/videoList.vm");
	}


}
