package com.gewara.web.action.admin.sys;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.model.acl.User;
import com.gewara.util.BeanUtil;
import com.gewara.util.HttpResult;
import com.gewara.util.HttpUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.admin.BaseAdminController;
/**
 * Jun 7, 2012 6:52:47 PM
 * @author bob
 * @desc ��̨�����ϵͳ�Խ�, �ṩȺ�����ŷ���.
 */
@Controller
public class SMSAdminController extends BaseAdminController{
	private String[] ips = new String[]{"180.166.48.210"};
	private String key = "MAILSYS";
	private String privatekey = "MAILSYS_GEWARA";
	private String API_URL = "http://gewamail.gewala.net/gewamail/sms/addBatch.xhtml";
	private String IMPORT_TMPMOBILE_URL = "http://gewamail.gewala.net/gewamail/sms/tmpMobile/import.xhtml";
	
	public static final String STATUS_W = "W"; 	// �ύ�����
	public static final String STATUS_D = "D";	// �ύ���ܾ�
	public static final String STATUS_Y = "Y";	// �ύ�����,������
	public static final String STATUS_S = "S";	// �ɹ�������
	@Autowired @Qualifier("jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	public void setJdbcTemplate(JdbcTemplate template) {
		jdbcTemplate = template;
	}
	
	/**
	 *    @function ��̨���뵼���ֻ��ŵ�sql + ��������, �����ֻ����б�
	 * 	@author bob.hu
	 *		@date	2012-03-02 11:42:34
	 */
	@RequestMapping("/admin/sysmgr/exportMobileBySql.xhtml")
	public String exportMobileBySql(final String sql, final String batchid,final String type, ModelMap model, HttpServletRequest request, HttpServletResponse response){
		if(StringUtils.isBlank(sql) || StringUtils.isBlank(batchid) || StringUtils.isBlank(type)) return "admin/sysmgr/exportMobileQuery.vm";
		
		User user = getLogonUser();
		String ip = WebUtils.getRemoteIp(request);
		if(!ArrayUtils.contains(ips, ip)) {
			try {
				response.sendError(400, "IP not avliable, bad request!!!!!!!!");
			} catch (IOException e) {}
			return null;
		}
		dbLogger.warn("USERID:[" + user.getId() + "]exportMobileBySql:" + sql);
		if(StringUtils.containsIgnoreCase(sql, "delete") ||
				StringUtils.containsIgnoreCase(sql, "trancate") ||
				StringUtils.containsIgnoreCase(sql, "cardpass") ||
				StringUtils.containsIgnoreCase(sql, "password") ||
				!StringUtils.startsWithIgnoreCase(sql, "select")){
			return forwardMessage(model, "error");
		}
		new Thread(new Runnable(){
			@Override
			public void run() {
				List<String> mobileList = jdbcTemplate.queryForList(sql, String.class);
				dbLogger.warn("�ύ���ţ���" + mobileList.size() + "����");
				List<List<String>> groupList = BeanUtil.partition(mobileList, 10000);
				int i=0;
				for(List<String> group:groupList){
					String mobiles = VmUtils.printList(group);
					HttpResult returncode = sendToSMS(mobiles, batchid,type);
					dbLogger.warn("�ύ���ţ���" + i + "��" + returncode.isSuccess());
					i ++;
				}
			}
		}).start();
		return forwardMessage(model, "�ύ��������������");
	}
	
	private HttpResult sendToSMS(String mobiles, String batchid,String type){
		String encryptCode = StringUtil.md5(key+privatekey);
		Map params = new HashMap();
		params.put("key", key);
		params.put("encryptCode", encryptCode);
		params.put("mobile", mobiles);
		params.put("batchid", batchid);
		if(StringUtils.equals("TMPMOBILE", type)){
			return HttpUtils.postUrlAsString(IMPORT_TMPMOBILE_URL, params);
		}else{
			return HttpUtils.postUrlAsString(API_URL, params);
		}
		
	}
}
