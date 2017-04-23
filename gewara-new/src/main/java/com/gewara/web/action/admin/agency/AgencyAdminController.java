package com.gewara.web.action.admin.agency;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.GoodsConstant;
import com.gewara.constant.TagConstant;
import com.gewara.model.acl.User;
import com.gewara.model.agency.Agency;
import com.gewara.model.agency.AgencyProfile;
import com.gewara.model.agency.AgencyToVenue;
import com.gewara.model.agency.Curriculum;
import com.gewara.model.agency.TrainingGoods;
import com.gewara.model.drama.DramaStar;
import com.gewara.model.drama.DramaToStar;
import com.gewara.model.sport.Sport;
import com.gewara.service.drama.DramaToStarService;
import com.gewara.service.sport.AgencyService;
import com.gewara.untrans.SearchService;
import com.gewara.util.BeanUtil;
import com.gewara.util.BindUtils;
import com.gewara.util.ChangeEntry;
import com.gewara.util.DateUtil;
import com.gewara.util.ValidateUtil;
import com.gewara.web.action.admin.BaseAdminController;

@Controller
public class AgencyAdminController extends BaseAdminController{
	
	@Autowired@Qualifier("agencyService")
	private AgencyService agencyService;
	@Autowired@Qualifier("dramaToStarService")
	private DramaToStarService dramaToStarService;
	@Autowired@Qualifier("searchService")
	private SearchService searchService;

	//��̨�����б�
	@RequestMapping("/admin/agency/getAgencyList.xhtml")
	public String getAgencyList(String searchKey, HttpServletRequest request, ModelMap model){
		List<Agency> agencyList = new ArrayList<Agency>();
		if(ValidateUtil.isNumber(searchKey)){
			agencyList.add(daoService.getObject(Agency.class, searchKey));
		}else{
			agencyList = agencyService.getAgencyList(searchKey, getAdminCitycode(request), "hotvalue", false, 0, 100);
		}
		model.put("agencyList", agencyList);
		return "/admin/agency/agencyList.vm";
	}
	//��̨��ȡ����������Ϣ
	@RequestMapping("/admin/agency/modifyAgencyDetail.xhtml")
	public String modifyAgencyDetail(Long agencyId, ModelMap model){
		if(agencyId != null){
			Agency agency = daoService.getObject(Agency.class, agencyId);
			model.put("agency", agency);
		}
		return "/admin/agency/agencyForm.vm";
	}
	//��̨�������
	@RequestMapping("/admin/agency/ajax/saveAgency.xhtml")
	public String saveAgency(Long agencyId, String name, HttpServletRequest request, ModelMap model) {
		Timestamp curTimestamp = DateUtil.getCurFullTimestamp();
		Agency agency = null;
		if(agencyId == null){
			agency = new Agency(name, getAdminCitycode(request));
		}else{
			agency = daoService.getObject(Agency.class, agencyId);
		}
		agency.setUpdatetime(curTimestamp);
		ChangeEntry changeEntry = new ChangeEntry(agency);
		Map<String, String[]> dataMap = request.getParameterMap(); 
		BindUtils.bindData(agency, dataMap);
		agency = daoService.saveObject(agency);
		monitorService.saveChangeLog(getLogonUser().getId(), Agency.class, agency.getId(),changeEntry.getChangeMap(agency));
		searchService.pushSearchKey(agency);
		return showJsonSuccess(model, agency.getId()+"");
	}
	//��̨�޸Ļ���״̬������ֵ
	@RequestMapping("/admin/agency/ajax/updateAgencyHotValueOrStatus.xhtml")
	public String setAgencyHotValue(Long agencyId, Integer hotvalue, String status, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, agencyId);
		if(agency == null) return showJsonError(model, "δ�ҵ��˻�����");
		ChangeEntry changeEntry = new ChangeEntry(agency);
		if(hotvalue != null) agency.setHotvalue(hotvalue);
		if(StringUtils.isNotBlank(status)){
			agency.setStatus(status);
			List<TrainingGoods> trainingGoodsList = agencyService.getTrainingGoodsList(agency.getCitycode(), TagConstant.TAG_AGENCY, agencyId, null, null, null, "goodssort", true, false, 0, 500);
			for (TrainingGoods trainingGoods : trainingGoodsList) {
				trainingGoods.setStatus(status);
			}
			daoService.saveObjectList(trainingGoodsList);
		}
		daoService.saveObject(agency);
		monitorService.saveChangeLog(getLogonUser().getId(), Agency.class, agency.getId(),changeEntry.getChangeMap(agency));
		return showJsonSuccess(model);
	}
	//��̨��ȡ�����ĳ�פ�����б�
	@RequestMapping("/admin/agency/getAgencyToVenueList.xhtml")
	public String getAgencyToVenueList(Long agencyId, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, agencyId);
		if(agency == null) return show404(model, "δ�ҵ��˻�����");
		List<AgencyToVenue> atvList = daoService.getObjectListByField(AgencyToVenue.class, "agencyId", agencyId);
		List<Long> idList = BeanUtil.getBeanPropertyList(atvList, Long.class, "venueId", true);
		Map<Long, Sport> sportMap = daoService.getObjectMap(Sport.class, idList);
		model.put("sportMap", sportMap);
		model.put("atvList", atvList);
		model.put("agency", agency);
		return "/admin/agency/agencyToVenueList.vm";
	}
	//�õ���������
	@RequestMapping("/admin/agency/getAgencyToVenue.xhtml")
	public String getAgencyToVenue(Long id, Long agencyId, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, agencyId);
		if(agency == null) return showJsonError(model, "δ�ҵ��˻�����");
		AgencyToVenue atv = daoService.getObject(AgencyToVenue.class, id);
		if(atv != null){
			Sport sport = daoService.getObject(Sport.class, atv.getVenueId());
			if(sport == null) return showJsonError(model, "δ�ҵ��˹������ݣ�");
			model.put("atv", atv);
			model.put("sport", sport);
		}
		model.put("agency", agency);
		return "/admin/agency/agencyToVenueForm.vm";
	}
	//��ӳ�פ����
	@RequestMapping("/admin/agency/saveAgencyToVenue.xhtml")
	public String saveAgencyToVenue(Long id, Long agencyId, Long venueId, String agencytype, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, agencyId);
		if(agency == null) return showJsonError(model, "δ�ҵ��˻�����");
		Sport sport = daoService.getObject(Sport.class, venueId);
		if(sport == null) return showJsonError(model, "δ�ҵ��˹������ݣ�");
		AgencyToVenue atv = null;
		if(id == null){
			atv = new AgencyToVenue();
			atv.setNumsort(0);
		}else{
			atv = daoService.getObject(AgencyToVenue.class, id);
		}
		atv.setAgencyId(agencyId);
		atv.setVenueId(venueId);
		atv.setAgencytype(agencytype);
		daoService.saveObject(atv);
		return showJsonSuccess(model);
	}
	//ɾ����פ����
	@RequestMapping("/admin/agency/delAgencyToVenue.xhtml")
	public String delAgencyToVenue(Long id, ModelMap model){
		daoService.removeObjectById(AgencyToVenue.class, id);
		return showJsonSuccess(model);
	}
	//����
	@RequestMapping("/admin/agency/changeNumsort.xhtml")
	public String changeNumsort(Long id,Integer numsort, ModelMap model){
		if(numsort == null) return showJsonError(model, "������Ϊ�գ�");
		AgencyToVenue atv = daoService.getObject(AgencyToVenue.class, id);
		if(atv == null) return showJsonError(model, "δ�ҵ��˳�פ���ݣ�");
		atv.setNumsort(numsort);
		daoService.saveObject(atv);
		return showJsonSuccess(model);
	}
	//�õ������Ľ����б�
	@RequestMapping("/admin/agency/getSportStarList.xhtml")
	public String getSportStarList(Long agencyId, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, agencyId);
		if(agency == null) return show404(model, "δ�ҵ��˻�����");
		List<DramaToStar> dtsList = dramaToStarService.getDramaToStarListByDramaid(TagConstant.TAG_AGENCY, agencyId, false);
		List<Long> starIdList = BeanUtil.getBeanPropertyList(dtsList, Long.class, "starid", true);
		Map<Long,DramaStar> starMap = daoService.getObjectMap(DramaStar.class, starIdList);
		model.put("dtsList", dtsList);
		model.put("starMap", starMap);
		model.put("agency", agency);
		return "/admin/agency/sportStarList.vm";
	}
	@RequestMapping("/admin/agency/getSportStar.xhtml")
	public String getSportStar(Long agencyId, Long id, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, agencyId);
		if(agency == null) return show404(model, "δ�ҵ��˻�����");
		if(id != null){
			DramaToStar dts = daoService.getObject(DramaToStar.class, id);
			model.put("dts", dts);
		}
		model.put("agency", agency);
		return "/admin/agency/sportStarForm.vm";
	}
	//�����������
	@RequestMapping("/admin/agency/saveSporStar.xhtml")
	public String saveSporStar(Long id, Long relatedId, Long starId, String type, ModelMap model){
		DramaStar star = daoService.getObject(DramaStar.class, starId);
		if(star == null || StringUtils.equals(star.getTag(), TagConstant.TAG_DRAMA)) return showJsonError(model, "δ�ҵ��˽�����˽��������˶�������");
		if(StringUtils.equals(type, GoodsConstant.GOODS_TYPE_TRAINING)){
			TrainingGoods trainingGoods = daoService.getObject(TrainingGoods.class, relatedId);
			if(trainingGoods == null) return showJsonError(model, "δ�ҵ��˿γ̣�");
		}else{
			type = TagConstant.TAG_AGENCY;
			Agency agency = daoService.getObject(Agency.class, relatedId);
			if(agency == null) return showJsonError(model, "δ�ҵ��˻�����");
		}
		int count = dramaToStarService.getStarCount(relatedId, starId);
		if(count == 0){
			DramaToStar dts = null;
			if(id == null){
				dts = new DramaToStar();
				dts.setNumsort(0);
			}else{
				dts = daoService.getObject(DramaToStar.class, id);
			}
			dts.setTag(type);
			dts.setDramaid(relatedId);
			dts.setStarid(starId);
			daoService.saveObject(dts);
		}
		return showJsonSuccess(model);
	}
	@RequestMapping("/admin/agency/changeDtsNumsort.xhtml")
	public String changeDtsNumsort(Long id,Integer numsort, ModelMap model){
		if(numsort == null) return showJsonError(model, "������Ϊ�գ�");
		DramaToStar dts = daoService.getObject(DramaToStar.class, id);
		if(dts == null) return showJsonError(model, "δ�ҵ��˽�����");
		dts.setNumsort(numsort);
		daoService.saveObject(dts);
		return showJsonSuccess(model);
	}
	@RequestMapping("/admin/agency/delDts.xhtml")
	public String delDts(Long id, ModelMap model){
		daoService.removeObjectById(DramaToStar.class, id);
		return showJsonSuccess(model);
	}
	@RequestMapping("/admin/agency/delCurriculum.xhtml")
	public String delCurriculum(Long id, ModelMap model){
		daoService.removeObjectById(Curriculum.class, id);
		return showJsonSuccess(model);
	}
	@RequestMapping("/admin/agency/saveBaseData.xhtml")
	public String saveBaseData(Long agencyId, HttpServletRequest request, ModelMap model){
		Agency agency = daoService.getObject(Agency.class, agencyId);
		if(agency == null) return showJsonError(model, "��������");
		AgencyProfile profile = daoService.getObject(AgencyProfile.class, agencyId);
		if(profile==null) profile = new AgencyProfile();
		ChangeEntry changeEntry = new ChangeEntry(profile);
		BindUtils.bindData(profile, request.getParameterMap());
		profile.setId(agency.getId());
		if(profile.getMobiles() != null){
			String mobiles = StringUtils.replace(profile.getMobiles(), "��", ",");
			String[] mobileList = StringUtils.split(mobiles, ",");
			for(String mobile : mobileList){
				if (!ValidateUtil.isMobile(mobile)) {
					return showJsonError(model, mobile+"���ֻ�������");
				}
			}
			profile.setMobiles(StringUtils.join(mobileList, ","));
		}
		daoService.saveObject(profile);
		User user = getLogonUser();
		model.put("agencyId", agencyId);
		model.put("msg", "����ɹ���");
		if(!StringUtils.equals(agency.getBooking(), profile.getStatus())) {
			agency.setBooking(profile.getStatus());
			daoService.saveObject(agency);
		}
		monitorService.saveChangeLog(user.getId(), AgencyProfile.class, profile.getId(), changeEntry.getChangeMap(profile));
		return "redirect:/admin/agency/baseData.xhtml";
	}
	@RequestMapping("/admin/agency/baseData.xhtml")
	public String baseData(Long agencyId, ModelMap model){
		Agency agency =  daoService.getObject(Agency.class, agencyId);
		model.put("agency", agency);
		AgencyProfile profile = daoService.getObject(AgencyProfile.class, agencyId);
		model.put("profile", profile);
		return "admin/agency/baseData.vm";
	}
}
