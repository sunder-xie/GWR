package com.gewara.web.action.admin.machine;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.AdminCityContant;
import com.gewara.constant.sys.MongoData;
import com.gewara.model.acl.User;
import com.gewara.model.machine.Machine;
import com.gewara.model.movie.Cinema;
import com.gewara.mongo.MongoService;
import com.gewara.service.MachineService;
import com.gewara.service.movie.MCPService;
import com.gewara.util.BeanUtil;
import com.gewara.util.BindUtils;
import com.gewara.util.DateUtil;
import com.gewara.web.action.admin.BaseAdminController;
import com.gewara.web.util.PageUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


@Controller
public class MachineAdminController extends BaseAdminController {
	private static Map<String, String> machineMap = new HashMap<String, String>();
	private static Map<String, String> machineNumberMap = new HashMap<String, String>(); //�������
	static{
		machineMap.put("newhost", Machine.TYPE_MACHINE_NEW_HOST);
		machineMap.put("lcd", Machine.TYPE_MACHINE_LCD);
		machineMap.put("vpn", Machine.TYPE_MACHINE_VPN);
		machineMap.put("integration", Machine.TYPE_MACHINE_INTEGRATION);
		machineMap.put("router", Machine.TYPE_MACHINE_ROUTER);
		machineMap.put("exchange",Machine.TYPE_MACHINE_EXCHANGE);
		machineMap.put("rpt", Machine.TYPE_MACHINE_RPT);
		machineMap.put("notebook", Machine.TYPE_MACHINE_NOTEBOOAK);
		machineMap.put("3gcard", Machine.TYPE_MACHINE_3GCARD);
		machineMap.put("phone", Machine.TYPE_MACHINE_PHONE);
		machineMap.put("pos", Machine.TYPE_MACHINE_POS);
	}
	static{
		machineNumberMap.put("newhost", "G-1501N");
		machineNumberMap.put("lcd", "G-1501M");
		machineNumberMap.put("vpn", "G-1501V");
		machineNumberMap.put("integration", "G-1501S");
		machineNumberMap.put("rpt", "G-1501R");//���߰�
		machineNumberMap.put("exchange", "G-1501W");//������
		machineNumberMap.put("router", "G-1501T");//·����
		machineNumberMap.put("notebook", "G-1501B");
		machineNumberMap.put("3gcard", "G-1501G");
		machineNumberMap.put("phone", "G-1501P");
		machineNumberMap.put("pos", "G-1501C");//pos��
	}
	@Autowired@Qualifier("machineService")
	private MachineService machineService;
	public void setMachineService(MachineService machineService) {
		this.machineService = machineService;
	}
	@Autowired@Qualifier("mcpService")
	private MCPService mcpService;
	public void setMcpService(MCPService mcpService) {
		this.mcpService = mcpService;
	}
	
	@Autowired
	@Qualifier("mongoService")
	private MongoService mongoService;
	
	
	
	@RequestMapping("/admin/machine/gewaMachineList.xhtml")
	public String gewaMachineList(HttpServletRequest request, ModelMap model, MachineCommand gmc) throws Exception{
		String citycode = getAdminCitycode(request);
		model.put("citycode", citycode);
		int fristPerPage = gmc.pageNo * gmc.rowsPerpage;
		if(StringUtils.equals(citycode, "310000")){
			if(StringUtils.equals(gmc.citycode, "000000")){
				citycode=null;
			}
		}
		Integer gewaMachineCount=machineService.gewaMachineCount(citycode,gmc.machinenumber, gmc.machinename, gmc.cinemaid, gmc.linkmethod, 
				gmc.machineowner, gmc.ticketcount, gmc.machinetype, gmc.machinestatus);
		Map<Long, Cinema> cinemaMap=new HashMap<Long, Cinema>();
		List<Machine> gewaMachineList=machineService.getGewaMachineList(citycode, gmc.machinenumber, gmc.machinename, gmc.cinemaid, gmc.linkmethod, 
				gmc.machineowner, gmc.ticketcount, gmc.machinetype, gmc.machinestatus, fristPerPage, gmc.rowsPerpage);
		Collections.sort(gewaMachineList, new PropertyComparator("cinemaid", false, false));
		for(Machine gewama: gewaMachineList){
			cinemaMap.put(gewama.getId(), daoService.getObject(Cinema.class, gewama.getCinemaid()));
		}
		List<Cinema> cinemaList = mcpService.getBookingCinemaList(getAdminCitycode(request));
		PageUtil pageUtil = new PageUtil(gewaMachineCount, gmc.rowsPerpage, gmc.pageNo, "/admin/machine/gewaMachineList.xhtml");
		Map params=new HashMap();
		params.put("machinenumber",gmc.machinenumber);
		params.put("machinename",gmc.machinename);
		params.put("cinemaid",gmc.cinemaid);
		params.put("linkmethod",gmc.linkmethod);
		params.put("machinenumber",gmc.machinenumber);
		params.put("ticketcount",gmc.ticketcount);
		params.put("machinetype",gmc.machinetype);
		params.put("citycode", gmc.citycode);
		params.put("machinestatus",gmc.machinestatus);
		pageUtil.initPageInfo(params);
		model.put("cinemaList", cinemaList);
		model.put("pageUtil", pageUtil);
		model.put("cinemaMap", cinemaMap);
		model.put("machineMap", machineMap);
		model.put("gewaMachineList", gewaMachineList);
		model.put("cityMap", AdminCityContant.getCitycode2CitynameMap());
		return "admin/machine/gewamachineList.vm";
	}
	@RequestMapping("/admin/machine/addGewaMachine.xhtml")
	public String addGewaMachine(HttpServletRequest request, ModelMap model, Long gewamachineid){
		String citycode = getAdminCitycode(request);
		Machine gewaMachine=null;
		if(gewamachineid != null) gewaMachine=daoService.getObject(Machine.class, gewamachineid);
		List<Cinema> openCinemaList = mcpService.getBookingCinemaList(citycode);
		List<Cinema> cinemaList = mcpService.getCinemaListByCitycode(citycode, 1, 200);
		model.put("gewaMachine", gewaMachine);
		model.put("opencinemaList", openCinemaList);
		model.put("cinemaList",cinemaList);
		model.put("machineMap", machineMap);
		model.put("cityMap", AdminCityContant.getCitycode2CitynameMap());
		return "admin/machine/gewaMachineForms.vm";
	}
	
	@RequestMapping("/admin/machine/setGewaMachineByOperMember.xhtml")
	public String setGewaMachineByOperMember(String machineId, ModelMap model){
		User user = getLogonUser();
		if(StringUtils.isBlank(machineId))return showJsonError(model, "�����ݲ���Ϊ��...");
		Machine machine = daoService.getObject(Machine.class, new Long(machineId));
		if(machine == null) return showJsonError(model, "�����ݲ�����...");
		machine.setOperMember(user.getNickname()+","+user.getId());
		machine.setUpdatetime(DateUtil.getCurFullTimestamp());
		daoService.saveObject(machine);
		Map result = BeanUtil.getBeanMap(machine);
		return showJsonSuccess(model, result);
	}
	
	@RequestMapping("/admin/machine/saveGewaMachine.xhtml")
	public String saveGewaMachine(ModelMap model, Long gewamachineid,Long cinemaid,Long vpncinemaid, HttpServletRequest request)throws Exception{
		String citycode = getAdminCitycode(request);
		String machinename=request.getParameter("machinename");
		if(StringUtils.isBlank(machinename)) return showJsonError(model, "�������Ʋ��ܿգ�");
		Machine gewaMachine=null;
		if(gewamachineid !=null) gewaMachine= daoService.getObject(Machine.class, gewamachineid);
		else gewaMachine=new Machine(cinemaid);
		Map<String, String[]> dataMap=request.getParameterMap();
		BindUtils.bindData(gewaMachine, dataMap);
		gewaMachine.setCitycode(citycode);
		if(cinemaid != null && vpncinemaid == null) gewaMachine.setCinemaid(cinemaid);
		else if(cinemaid == null && vpncinemaid != null) gewaMachine.setCinemaid(vpncinemaid);
		if(gewaMachine.getId() == null){
			Object[] machinenumbers = dataMap.get("machinenumber");
			String machinenumber = "";
			if(machinenumbers.length!=0){ machinenumber = (String)machinenumbers[0];}
			String[] machines = machinenumber.split("[, ]+");
			if(machines.length>10) return showJsonError(model, "����������ݲ��ܴ���10");
			List<Machine> machineList = new ArrayList<Machine>();
			for (String string : machines) {
				if(!StringUtils.contains(string, machineNumberMap.get(machinename))) return showJsonError(model, "�������豸���Ʋ���");
				Machine machine = new Machine(cinemaid);
				PropertyUtils.copyProperties(machine, gewaMachine);
				machine.setMachinenumber(string);
				machineList.add(machine);
			}
			daoService.addObjectList(machineList);
		}else	daoService.saveObject(gewaMachine); 
		return showJsonSuccess(model, gewaMachine.getId()+"");
	}
	@RequestMapping("/admin/machine/deleteGewaMachine.xhtml")
	public String deleteGewaMachine(ModelMap model, Long gewamachineid){
		Machine gewaMachine=daoService.getObject(Machine.class, gewamachineid);
		if(gewaMachine==null) return showJsonError(model, "���ݲ����ڣ�");
		daoService.removeObject(gewaMachine);
		return showJsonSuccess(model);
	}
	
	@RequestMapping("/admin/machine/getMaxMachineNumber.xhtml")
	public String getMaxMachineNumber(ModelMap model,String machinename){
		String machineprefix = machineNumberMap.get(machinename);
		Integer maxCount = machineService.getMaxMachineNumber(machinename, machineprefix);
		if(maxCount == null) return showJsonError(model, "�豸��ų�����, ��ˢ��ҳ������!");
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumIntegerDigits(4);
		return showJsonSuccess(model,machineprefix+nf.format(maxCount+1).replace(",",""));
	}
	
	@RequestMapping("/admin/machine/updateMachinenumber.xhtml")
	public String updateMachinenumber(ModelMap model){
		Set<String> machineNumberSet = machineNumberMap.keySet();
		for(String machineName : machineNumberSet){
			DetachedCriteria query = DetachedCriteria.forClass(Machine.class);
			query.add(Restrictions.eq("machinename", machineName));
			List<Machine> machineList = hibernateTemplate.findByCriteria(query);
			if(machineList.size() == 0) continue;
			for(Machine machine : machineList){
				if(!StringUtils.contains(machine.getMachinenumber(), machineNumberMap.get(machineName))){
					machine.setMachinenumber(machineNumberMap.get(machineName) + StringUtils.substring(machine.getMachinenumber(), 7));
				}
			}
			List<Machine> machineList2 = new ArrayList<Machine>();
			Collections.sort(machineList, new PropertyComparator("machinenumber", false, true));
			for(int i = 0;i < machineList.size();i++){
				int num1 = Integer.parseInt(StringUtils.substring(machineList.get(i).getMachinenumber(), 7));
				for(int j = i + 1;j < machineList.size();j++){
					int num2 = Integer.parseInt(StringUtils.substring(machineList.get(j).getMachinenumber(), 7));
					if(num1 == num2 && !machineList2.contains(machineList.get(j))){
						machineList2.add(machineList.get(j));
					}
				}
			}
			machineList.removeAll(machineList2);
			int max = Integer.parseInt(StringUtils.substring(machineList.get(machineList.size()-1).getMachinenumber(), 7));
			for(int i = 0;i < machineList2.size();i++){
				String num = (max + 1 + i) + "";
				int length = 4 - num.length();
				for(int j = 0;j < length;j++){
					num = "0" + num;
				}
				machineList2.get(i).setMachinenumber(machineNumberMap.get(machineName) + num);
			}
			machineList.addAll(machineList2);
			daoService.saveObjectList(machineList);
		}
		return showJsonSuccess(model);
	}
	
	
	
	/**
	 * ����һ����ػ�����
	 * @param model
	 * @return
	 */
	@RequestMapping("/admin/machine/saveMachineConfig.xhtml")
	public String saveMachineConfig(
			@RequestParam(required=false,value="id")
			String id,
			@RequestParam(required=true,value="venueId")
			Long venueId,
			String venueName, 
			@RequestParam(required=true,value="defShutDownTime")
			String defShutDownTime,
			@RequestParam(required=false,value="unitTime",defaultValue="0")
			Integer unitTime,
			ModelMap model){
		
		//У��ӰԺ�����Ƿ����
		int count=0;
		if(StringUtils.isBlank(id)){//�������
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("venueId", venueId);
			count=mongoService.getCount(MongoData.NS_MACHINECONFIG, params);
		}else{//�޸����
			DBObject queryCondition = new BasicDBObject();
			DBObject venueIdObj = mongoService.queryBasicDBObject("venueId", "=", venueId);
			DBObject idObj = mongoService.queryBasicDBObject(MongoData.SYSTEM_ID,"!=", id);
			queryCondition.putAll(venueIdObj);
			queryCondition.putAll(idObj);
			count = mongoService.getCount(MongoData.NS_MACHINECONFIG, queryCondition);
		}
		if(count>0){
			return showJsonError(model, "��ӰԺ�����Ѿ�����,��������ӰԺID");
		}
		
		//Ĭ�Ϲػ�ʱ���ʽУ��
		SimpleDateFormat sdfHM=new SimpleDateFormat("HH:mm");
		try {
			sdfHM.parse(defShutDownTime);
		} catch (ParseException e) {
			return showJsonError(model, "Ĭ�Ϲػ�ʱ���ʽ���ô���(��ȷ��ʽHH:mm)");
		}
		
		Map<String, Object> data=new HashMap<String, Object>();
		if(StringUtils.isBlank(id)){//����
			data.put(MongoData.SYSTEM_ID, MongoData.buildId());
		}else{
			data.put(MongoData.SYSTEM_ID, id);
		}
		
		data.put("venueId", venueId);//ӰԺid
		data.put("venueName", venueName);//ӰԺ����
		data.put("defShutDownTime", defShutDownTime);//Ĭ���Զ��ػ�ʱ��
		data.put("unitTime", unitTime);//��λʱ��(����)
		mongoService.saveOrUpdateMap(data, MongoData.SYSTEM_ID, MongoData.NS_MACHINECONFIG);
		return showJsonSuccess(model);
	}
	
	
	/**
	 * ɾ��һ����ػ�����
	 * @param model
	 * @return
	 */
	@RequestMapping("/admin/machine/removeMachineConfig.xhtml")
	public String saveMachineConfig(ModelMap model,@RequestParam(required=true,value="id") String id){
		mongoService.removeObjectById(MongoData.NS_MACHINECONFIG, MongoData.SYSTEM_ID, id);
		model.put("msg", "success");
		return showJsonSuccess(model);
	}
	
	/**
	 * һ����ػ������б�
	 * @param model
	 * @return
	 */
	@RequestMapping("/admin/machine/machineConfigList.xhtml")
	public String machineConfigList(ModelMap model,
			@RequestParam(defaultValue="0",required=false,value="pageNo")Integer pageNo){
		Integer pageSize=20;
		List<Map> machineConfigList=mongoService.find(MongoData.NS_MACHINECONFIG,pageNo*pageSize, pageSize);
		int totalCount=mongoService.getCount(MongoData.NS_MACHINECONFIG);
		PageUtil pageUtil = new PageUtil(totalCount, pageSize, pageNo, "admin/machine/machineConfigList.xhtml");
		pageUtil.initPageInfo();
		model.put("pageUtil", pageUtil);
		model.put("machineConfigList", machineConfigList);
		return "admin/machine/machineConfigList.vm";
	}
	
	
	
}
