package com.gewara.web.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.filter.GenericFilterBean;

import com.gewara.commons.api.ApiSysParamConstants;
import com.gewara.commons.sign.Sign;
import com.gewara.constant.ApiConstant;
import com.gewara.model.acl.WebModule;
import com.gewara.model.api.ApiUser;
import com.gewara.model.api.ApiUserExtra;
import com.gewara.service.DaoService;
import com.gewara.service.api.ApiMobileService;
import com.gewara.service.api.ApiSecureService;
import com.gewara.untrans.monitor.MonitorService;
import com.gewara.web.action.api.ApiAuth;
import com.gewara.web.support.GewaMultipartResolver;
import com.gewara.web.support.RoleUrlMatchHelper;

/**
 * API2.0��ݹ�����
 * 
 * @author taiqichao
 * 
 */
public abstract class BaseApiAuthenticationFilter extends GenericFilterBean {
	private static ThreadLocal<ApiAuth> apiAuthLocal = new ThreadLocal<ApiAuth>();
	protected ApiFilterHelper apiFilterHelper;
	protected RoleUrlMatchHelper rightsHelper;
	
	@Autowired@Qualifier("daoService")
	protected DaoService daoService;
	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
	@Autowired@Qualifier("monitorService")
	private MonitorService monitorService;

	@Autowired@Qualifier("apiSecureService")
	private ApiSecureService apiSecureService;
	@Autowired@Qualifier("apiMobileService")
	private ApiMobileService apiMobileService;
	public static ApiAuth getApiAuth() {
		return apiAuthLocal.get();
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		Long cur = System.currentTimeMillis();
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		if(ServletFileUpload.isMultipartContent(request)){//��װ�ļ��ϴ�����
			request=new GewaMultipartResolver().resolveMultipart(request);
		}
		
		String appkey = request.getParameter(ApiSysParamConstants.APPKEY);
		//�û����У��
		ApiUser apiUser = apiMobileService.getApiUserByAppkey(appkey);
		if (apiUser == null) {
			ApiFilterHelper.writeErrorResponse(response, ApiConstant.CODE_PARTNER_NOT_EXISTS,"�û�������");
			apiFilterHelper.apiLog(request, cur, false);//��¼ʧ����־
			return;
		}
		String sign = request.getParameter(ApiSysParamConstants.SIGN);
		String privateKey = getPrivateKey(apiUser, request);
		//ǩ��У��
		String signData=Sign.signMD5(ApiFilterHelper.getTreeMap(request), privateKey);
		if (!StringUtils.equalsIgnoreCase(sign, signData)) {
			ApiFilterHelper.writeErrorResponse(response, ApiConstant.CODE_PARTNER_NORIGHTS,"У��ǩ������!");
			apiFilterHelper.apiLog(request, cur, false);//��¼ʧ����־
			return;
		}
		
		//Ȩ��У��
		boolean hasRights = checkRights(apiUser, request);
		if(!hasRights){
			ApiFilterHelper.writeErrorResponse(response, ApiConstant.CODE_PARTNER_NORIGHTS,"û��Ȩ��");
			apiFilterHelper.apiLog(request, cur, false);//��¼ʧ����־
			return;
		}
		
		//��������У��ͨ��
		try{
			//���浱ǰ��Ȩ�û�
			ApiUserExtra userExtra = apiMobileService.getApiUserExtraById(apiUser.getId());
			apiAuthLocal.set(new ApiAuth(apiUser, userExtra));
			//ִ�����淽����
			chain.doFilter(request, response);
		}finally{
			//�����ǰ��Ȩ�û�
			apiAuthLocal.set(null);
			//��¼�ɹ���־
			apiFilterHelper.apiLog(request, cur, true);
		}
		
	}
	protected boolean checkRights(ApiUser user, HttpServletRequest request){
		if(user == null){
			return false;
		}
		if(!user.isEnabled()){
			return false;
		}
		return rightsHelper.hasRights(user.getRoles(), request);
	}
	protected abstract String getPrivateKey(ApiUser apiUser, HttpServletRequest request);


	@Override
	protected void initFilterBean() throws ServletException {
		List<WebModule> moduleList = apiSecureService.getApiModuleList();
		rightsHelper = new RoleUrlMatchHelper(moduleList);
		apiFilterHelper = new ApiFilterHelper(monitorService);
	}

}
