package com.gewara.web.action.subject.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.sys.MongoData;
import com.gewara.mongo.MongoService;
import com.gewara.support.ServiceHelper;
import com.gewara.util.DateUtil;
import com.gewara.web.action.admin.BaseAdminController;
@Controller
public class ViewingGroupAdminController extends BaseAdminController {
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}
	/**********************************��Ӱ��ר��**********************************/
	 @RequestMapping("/admin/newsubject/viewingGroupTemplate.xhtml")
	 public String viewingGroupTemplate(HttpServletRequest request,ModelMap model){
		 	Map params = new HashMap();
		 	String citycode = getAdminCitycode(request);
			params.put(MongoData.ACTION_TYPE, MongoData.ACTION_TYPE_VIEWINGGROUPTEMPLATE);
			params.put(MongoData.ACTION_TAG, "viewing");
			params.put(MongoData.ACTION_SIGNNAME, "viewing");
			params.put(MongoData.ACTION_CITYCODE, citycode);
			List<Map> list = mongoService.find(MongoData.NS_MAINSUBJECT, params, MongoData.ACTION_ADDTIME, false);
			model.put("list", list);
			model.put("citycode", citycode);
			return "admin/newsubject/viewingGroupList.vm";
	 }
	
	 	// ��ӹ�Ӱģ��ר��
		@RequestMapping("/admin/newsubject/addViewingGroup.xhtml")
		public String addViewingGroup(String id, ModelMap model){
			if(StringUtils.isNotBlank(id)){
				Map data = mongoService.findOne(MongoData.NS_MAINSUBJECT, MongoData.DEFAULT_ID_NAME, id);
				if(data != null) model.put("data", data);
			}
			return "admin/newsubject/addViewingGroup.vm";
		}
		@RequestMapping("/admin/newsubject/saveViewing.xhtml")
		public String saveViewing(HttpServletRequest request,String id,String title,String newslogo,String seokeywords, String seodescription, String newstitle, String walatitle, ModelMap model){
			Map params = new HashMap();
			int ordernum = 0;
			Date addtime = DateUtil.currentTime();
			String citycode = getAdminCitycode(request);
			if(StringUtils.isNotBlank(id)){
				params = mongoService.findOne(MongoData.NS_MAINSUBJECT, MongoData.DEFAULT_ID_NAME, id); 
				ordernum = (Integer) params.get(MongoData.ACTION_ORDERNUM);
				addtime = (Date) params.get(MongoData.ACTION_ADDTIME);
			}else{
				params.put(MongoData.DEFAULT_ID_NAME, ServiceHelper.assignID(MongoData.buildId()));
			}
			params.put(MongoData.ACTION_TITLE, title);
			params.put(MongoData.ACTION_TAG, "viewing");
			params.put(MongoData.ACTION_SIGNNAME, "viewing");
			params.put(MongoData.ACTION_NEWSTITLE, newstitle);
			params.put(MongoData.ACTION_NEWSLOGO, newslogo);
			params.put(MongoData.ACTION_WALATITLE, walatitle);
			params.put(MongoData.ACTION_ORDERNUM, ordernum);
			params.put(MongoData.ACTION_ADDTIME, addtime);
			params.put(MongoData.ACTION_SEOKEYWORDS, seokeywords);
			params.put(MongoData.ACTION_SEODESCRIPTION, seodescription);
			params.put(MongoData.ACTION_CITYCODE, citycode);
			params.put(MongoData.ACTION_TYPE, MongoData.ACTION_TYPE_VIEWINGGROUPTEMPLATE);
			mongoService.saveOrUpdateMap(params, MongoData.DEFAULT_ID_NAME, MongoData.NS_MAINSUBJECT);
			return showJsonSuccess(model);
		}
		/********************ģ������*********************/
		/**
		 * �鿴ģ��������ϸ
		 * 
		 * */
		@RequestMapping("/admin/newsubject/viewingGcDetail.xhtml")
		public String viewingGcDetail(HttpServletRequest request,String parentid,String signname, ModelMap model) {
			Map params = new HashMap();
			String citycode = getAdminCitycode(request);
			params.put(MongoData.ACTION_CITYCODE, citycode);
			params.put(MongoData.ACTION_PARENTID, parentid);
			params.put(MongoData.ACTION_SIGNNAME, signname);
			params.put(MongoData.ACTION_TAG, "moduleConfig");
			if(StringUtils.isNotBlank(parentid)) {
				Map data = mongoService.findOne(MongoData.NS_MAINSUBJECT, params);
				if(data != null){
					model.put("data", data);
				}
			}
			return "admin/newsubject/viewGcDetail.vm";
		}
		/**
		 * ����ģ������
		 * 
		 * */
		@RequestMapping("/admin/newsubject/saveViewingGcDetail.xhtml")
		public String saveViewingGcDetail(HttpServletRequest request,String id, String tag,String spActivie,String viewBgColor,String viewReport,String movieId,String voteId,String signname,String parentid,String newslogo,ModelMap model){
			Map params = new HashMap();
			int ordernum = 0;
			Date addtime = DateUtil.currentTime();
			String citycode = getAdminCitycode(request);
			if(StringUtils.isNotBlank(id)){
				params = mongoService.findOne(MongoData.NS_MAINSUBJECT, MongoData.DEFAULT_ID_NAME, id);
				if(params.get(MongoData.ACTION_ORDERNUM) != null){
					ordernum = (Integer)params.get(MongoData.ACTION_ORDERNUM);
				}
				if(params.get(MongoData.ACTION_ADDTIME) != null){
					addtime = (Date)params.get(MongoData.ACTION_ADDTIME);
				}
			}else{
				params.put(MongoData.DEFAULT_ID_NAME, ServiceHelper.assignID(tag+signname));
				params.put(MongoData.SYSTEM_ID, MongoData.buildId());
			}
			params.put(MongoData.ACTION_TAG, "moduleConfig");
			params.put(MongoData.ACTION_SIGNNAME, signname);
			params.put(MongoData.ACTION_PARENTID, parentid);
			params.put(MongoData.ACTION_SP_ACTIVIES, spActivie);
			params.put(MongoData.ACTION_VIEWBGCOLOR, viewBgColor);
			params.put(MongoData.ACTION_VIEWREPORT, viewReport);
			params.put(MongoData.ACTION_ATTACH_MOVIE_MOVIEID, movieId);
			params.put(MongoData.ACTION_VOTEID, voteId);
			params.put(MongoData.ACTION_ORDERNUM, ordernum);
			params.put(MongoData.ACTION_ADDTIME, addtime);
			params.put(MongoData.ACTION_CITYCODE, citycode);
			params.put(MongoData.ACTION_TYPE, MongoData.ACTION_TYPE_VIEWINGGROUPTEMPLATE);
			params.put(MongoData.ACTION_NEWSLOGO, newslogo);
			mongoService.saveOrUpdateMap(params, MongoData.DEFAULT_ID_NAME, MongoData.NS_MAINSUBJECT);
			return showJsonSuccess(model);
		}
		/****************����ģ��*******************/
		/**
		 * 
		 * ���ҹ�Ӱ���б��������ǣ��ͼƬ�����ڻعˣ�
		 * @param tag ���������ĳģ�� ��star��
		 * @param signname ������� 		(viewing)
		 * */
		@RequestMapping("/admin/newsubject/getViewingItemList.xhtml")
		public String getViewingItemList(HttpServletRequest request,String parentid,String tag,String signname,ModelMap model){
			model.put("parentid", parentid);
			model.put("tag", tag);
			Map params = new HashMap();
			String citycode = getAdminCitycode(request);
			params.put(MongoData.ACTION_CITYCODE, citycode);
			params.put(MongoData.ACTION_PARENTID, parentid);
			params.put(MongoData.ACTION_SIGNNAME, signname);
			params.put(MongoData.ACTION_TAG, tag);
			if(StringUtils.isNotBlank(parentid)) {
				 List<Map> list = mongoService.find(MongoData.NS_MAINSUBJECT, params, MongoData.ACTION_ORDERNUM, false);
				if(list != null){
					model.put("list", list);
				}
			}
			if(StringUtils.equals(tag, "star")){
				return "admin/newsubject/viewStarList.vm";
			}else if(StringUtils.equals(tag, "activpic")){
				return "admin/newsubject/viewActivPicList.vm";
			}else if(StringUtils.equals(tag, "periodsreview")){//���ڻع�
				return "admin/newsubject/viewActivPicList.vm";
			}else{
				return "";
			}
		}
		/**
		 * 
		 * ��ӹ�Ӱ����
		 * �������ǣ��ͼƬ,���ڻعˣ�
		 * 
		 * */
		@RequestMapping("/admin/newsubject/addViewingItem.xhtml")
		public String addViewingItem(String id,String parentid,String tag,ModelMap model){
			model.put("parentid", parentid);
			model.put("tag", tag);
			if(StringUtils.isNotBlank(id)) {
				Map data = mongoService.findOne(MongoData.NS_MAINSUBJECT, MongoData.DEFAULT_ID_NAME,id);
				if(data != null){
					model.put("data", data);
				}
			}
			if(StringUtils.equals(tag, "star")){
				return "admin/newsubject/addViewStar.vm";
			}else if(StringUtils.equals(tag, "activpic")){
				return "admin/newsubject/addViewItem.vm";
			}else if(StringUtils.equals(tag, "periodsreview")){//���ڻع�
				return "admin/newsubject/addViewItem.vm";
			}else{
				return "";
			}
		}
		/**
		 * ������������
		 * 
		 * */
		@RequestMapping("/admin/newsubject/saveViewingStar.xhtml")
		public String saveViewingStar(HttpServletRequest request,String id,String parentid,String tag,String signname,String membername,String starsex,String constellation,String birthday,String birthplace,String body,String newslogo,ModelMap model){
			Map params = new HashMap();
			int ordernum = 0;
			Date addtime = DateUtil.currentTime();
			String citycode = getAdminCitycode(request);
			
			if(StringUtils.isNotBlank(id)){
				params = mongoService.findOne(MongoData.NS_MAINSUBJECT, MongoData.DEFAULT_ID_NAME, id);
				if(params.get(MongoData.ACTION_ORDERNUM) != null){
					ordernum = (Integer)params.get(MongoData.ACTION_ORDERNUM);
				}
				if(params.get(MongoData.ACTION_ADDTIME) != null){
					addtime = (Date)params.get(MongoData.ACTION_ADDTIME);
				}
			}else{
				params.put(MongoData.DEFAULT_ID_NAME, ServiceHelper.assignID(tag+signname));
				params.put(MongoData.SYSTEM_ID, MongoData.buildId());
			}
				params.put(MongoData.ACTION_TAG, tag);
				params.put(MongoData.ACTION_SIGNNAME, signname);
				params.put(MongoData.ACTION_PARENTID, parentid);
				params.put(MongoData.ACTION_MEMBERNAME, membername);//����
				params.put(MongoData.ACTION_STARSEX, starsex);//�����Ա�
				params.put(MongoData.ACTION_CONSTELLATION, constellation);//����
				params.put(MongoData.ACTION_BIRTHDAY, birthday);//��������
				params.put(MongoData.ACTION_BIRTHPLACE, birthplace);//����
				params.put(MongoData.ACTION_BODY, body);
				params.put(MongoData.ACTION_NEWSLOGO, newslogo);
				params.put(MongoData.ACTION_ORDERNUM, ordernum);
				params.put(MongoData.ACTION_ADDTIME, addtime);
				params.put(MongoData.ACTION_CITYCODE, citycode);
				params.put(MongoData.ACTION_TYPE, MongoData.ACTION_TYPE_VIEWINGGROUPTEMPLATE);
				mongoService.saveOrUpdateMap(params, MongoData.DEFAULT_ID_NAME, MongoData.NS_MAINSUBJECT);
				return showJsonSuccess(model);
		}
		/**
		 * �������
		 * 
		 * */
		@RequestMapping("/admin/newsubject/changeOrderNum_viewstar.xhtml")
		public String changeOrderNumSingles(String id, Integer ordernum, ModelMap model){
			Map data = mongoService.findOne(MongoData.NS_MAINSUBJECT, MongoData.DEFAULT_ID_NAME, id);
			if(data != null){
				data.put(MongoData.ACTION_ORDERNUM, ordernum);
				mongoService.saveOrUpdateMap(data, MongoData.DEFAULT_ID_NAME, MongoData.NS_MAINSUBJECT);
				return showJsonSuccess(model);
			}
			return showJsonError_NOT_FOUND(model);
		}
		/**
		 * �����Ӱ��Ϣ
		 * �������ͼƬ�����ڻعˣ�
		 * 
		 * */
		@RequestMapping("/admin/newsubject/saveViewingItem.xhtml")
		public String saveViewingItem(HttpServletRequest request,String id,String parentid,String tag,String signname,String title,String body,String newslogo,String singles_cinemaurl,ModelMap model){
			Map params = new HashMap();
			int ordernum = 0;
			Date addtime = DateUtil.currentTime();
			String citycode = getAdminCitycode(request);
			if(StringUtils.isNotBlank(id)){
				params = mongoService.findOne(MongoData.NS_MAINSUBJECT, MongoData.DEFAULT_ID_NAME, id);
				if(params.get(MongoData.ACTION_ORDERNUM) != null){
					ordernum = (Integer)params.get(MongoData.ACTION_ORDERNUM);
				}
				if(params.get(MongoData.ACTION_ADDTIME) != null){
					addtime = (Date)params.get(MongoData.ACTION_ADDTIME);
				}
			}else{
				params.put(MongoData.DEFAULT_ID_NAME, ServiceHelper.assignID(tag+signname));
				params.put(MongoData.SYSTEM_ID, MongoData.buildId());
			}
				params.put(MongoData.ACTION_TAG, tag);
				params.put(MongoData.ACTION_SIGNNAME, signname);
				params.put(MongoData.ACTION_PARENTID, parentid);
				params.put(MongoData.ACTION_TITLE, title);
				params.put(MongoData.ACTION_BODY, body);
			if(StringUtils.equals(tag, "periodsreview")){//�ж��Ƿ�Ϊ"���ڻع�"ģ��
				params.put(MongoData.SINGLES_CINEMAURL, singles_cinemaurl);
			}
				params.put(MongoData.ACTION_NEWSLOGO, newslogo);
				params.put(MongoData.ACTION_ORDERNUM, ordernum);
				params.put(MongoData.ACTION_ADDTIME, addtime);
				params.put(MongoData.ACTION_CITYCODE, citycode);
				params.put(MongoData.ACTION_TYPE, MongoData.ACTION_TYPE_VIEWINGGROUPTEMPLATE);
				mongoService.saveOrUpdateMap(params, MongoData.DEFAULT_ID_NAME, MongoData.NS_MAINSUBJECT);
				return showJsonSuccess(model);
		}
}
