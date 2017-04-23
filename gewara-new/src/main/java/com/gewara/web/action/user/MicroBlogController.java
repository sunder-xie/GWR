package com.gewara.web.action.user;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.sys.MongoData;
import com.gewara.json.MemberStats;
import com.gewara.model.user.Member;
import com.gewara.model.user.Treasure;
import com.gewara.mongo.MongoService;
import com.gewara.service.bbs.BlogService;
import com.gewara.service.content.RecommendService;
import com.gewara.untrans.GewaPicService;
import com.gewara.untrans.MemberCountService;
import com.gewara.untrans.WalaApiService;
import com.gewara.util.JsonUtils;
import com.gewara.util.WebUtils;
import com.gewara.web.action.AnnotationController;

/**
 * �°�����
 * @author lss
 */
@Controller
public class MicroBlogController extends AnnotationController {
	@Autowired@Qualifier("blogService")
	private BlogService blogService;
	@Autowired@Qualifier("memberCountService")
	private MemberCountService memberCountService;

	@Autowired@Qualifier("walaApiService")
	private WalaApiService walaApiService;
	@Autowired@Qualifier("gewaPicService")
	private GewaPicService gewaPicService;
	public void setGewaPicService(GewaPicService gewaPicService) {
		this.gewaPicService = gewaPicService;
	}
	@Autowired@Qualifier("recommendService")
	private RecommendService recommendService;
	public void setRecommendService(RecommendService recommendService) {
		this.recommendService = recommendService;
	}
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}
	
	// ΢��ͼƬɾ��
	@RequestMapping("/wala/delMicroBlogPic.xhtml")
	public String delpic(String picpath, ModelMap model) {
		//֮ǰ�ķ���, ����picpath ֱ�ӻ�ȡ·������ɾ��, ��ʹ��acerge HDFSͼƬɾ������
		// walaͼƬδ�洢�� Picture����, ����ֻ��ɾ����������Ӧ��ͼƬ·������.
		try {
			boolean isSuc = gewaPicService.removePicture(picpath);
			if(isSuc) return showJsonSuccess(model);
		} catch (IOException e) {
			dbLogger.error("", e);
		}
		return showJsonError(model, "ɾ��ʱ����!");
	}


	/**
	 * �������� : ��⵱ǰurl�Ƿ�����ӻ��Ƿ���Ч, ����������� 5 ��, ��� 5 �ζ����ɹ�˵���õ�ַ�����ڻ���Ϊ��Ч��ַ.
	 * 
	 * @param url
	 *           ָ��url�����ַ
	 * 
	 * @return string
	 */
	@RequestMapping("/wala/isConnect.xhtml")
	public String isConnect(@CookieValue(value = LOGIN_COOKIE_NAME, required = false)String sessid, 
			HttpServletRequest request, ModelMap model, String url) {
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		int counts = 0;
		String succ = null;
		while (counts < 2) {
			try {
				URL urlstr = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) urlstr.openConnection();
				connection.setConnectTimeout(3000);
				connection.setReadTimeout(2000);	// ���ö�ȡʱ��, ��Ȼ����ɶ���.
				int state = connection.getResponseCode();
				if (state == 200) {
					succ = connection.getURL().toString();
					if (StringUtils.isNotBlank(succ))
						return showJsonSuccess(model);
				}
				break;
			} catch (Exception ex) {
				counts++;
				continue;
			}
		}
		return showJsonError(model, "�����������Ϊ��Ч���ӣ�");
	}
	
	
	
	/**
	 * �Ƴ���˿��ϵ
	 */
	@RequestMapping("/wala/cancelFans.xhtml")
	public String cancelFans(ModelMap model,Long fansid, @CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request){
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		Member meb = daoService.getObject(Member.class, fansid);
		if (meb == null)
			return showJsonError(model, "����Ҫ�Ƴ��ķ�˿���󲻴��ڣ�");
		boolean b = blogService.cancelTreasure(fansid, member.getId(), Treasure.TAG_MEMBER, Treasure.ACTION_COLLECT);
		if (b){
			memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_FANSCOUNT, 1, false);
			memberCountService.updateMemberCount(meb.getId(), MemberStats.FIELD_ATTENTIONCOUNT, 1, false);
			return showJsonSuccess(model, "�Ƴ���˿�ɹ���");
		}else{
			return showJsonError(model, "�Ƴ���˿ʧ�ܣ�");
		}
	}
	/**
	 * ��ӹ�ע
	 */
	@RequestMapping("/wala/addMicroAttention.xhtml")
	public String addAttention(Long memberid, ModelMap model, @CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request) {
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		if(member == null) return showJsonError_NOT_LOGIN(model);
		if ((member.getId() + "").equals(memberid + "")) {
			return showJsonError(model, "���ܶ��Լ����й�ע��");
		}
		boolean b = blogService.isTreasureMember(member.getId(), memberid);
		if (b) {
			return showJsonError(model, "����ӵ���ע�б��벻Ҫ�ظ�������");
		}
		Treasure treasure = new Treasure(member.getId());
		treasure.setRelatedid(memberid);
		treasure.setAction(Treasure.ACTION_COLLECT);
		treasure.setTag(Treasure.TAG_MEMBER);
		daoService.saveObject(treasure);
		walaApiService.addTreasure(treasure);
		//��ע����˿����һ
		memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_ATTENTIONCOUNT, 1, true);
		memberCountService.updateMemberCount(memberid, MemberStats.FIELD_FANSCOUNT, 1, true);
		//��ע����˿����һ
		
		//���һ����˿֪ͨ
		recommendService.memberAddFansCount(memberid, MongoData.MESSAGE_FANS_ADD, MongoData.MESSAGE_FANS, 1);
		
		//�������������ʾ����Ȥ���û����������עһ���û���ʱ�򣬾�ɾ���������Ȥ�û�
		Map params = memberCountService.getMemberInfoStats(member.getId());
		if(params != null){
			String jsonStr = (String)params.get("recommedPerson");
			List<Map> memberList = JsonUtils.readJsonToObject(List.class, jsonStr);
			for(Map map : memberList){
				String mid = map.get("memberid").toString();
				if(Long.valueOf(mid).equals(memberid)){
					memberList.remove(map);
					break;
				}
			}
			jsonStr = JsonUtils.writeObjectToJson(memberList);
			params.put("recommedPerson", jsonStr);
			
			mongoService.saveOrUpdateMap(params, "myid", MongoData.NS_MEMBER_INFO);
		}
		
		return showJsonSuccess(model, "��ӹ�ע�ɹ���");
	}
	
	/**
	 * ȡ����ע�û�
	 * @param memberid
	 * @param model
	 * @return
	 */
	@RequestMapping("/wala/cancelAttention.xhtml")
	public String cancelAttention(Long memberid, ModelMap model, @CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request) {
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		Member meb = daoService.getObject(Member.class, memberid);
		if (meb == null)
			return showJsonError(model, "ȡ����ע�Ķ��󲻴��ڣ�");
		boolean b = blogService.cancelTreasure(member.getId(), memberid, Treasure.TAG_MEMBER, Treasure.ACTION_COLLECT);
		if (b){
			//��ע����˿����һ
			memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_ATTENTIONCOUNT, 1, false);
			memberCountService.updateMemberCount(memberid, MemberStats.FIELD_FANSCOUNT, 1, false);
			//��ע����˿����һ
			walaApiService.delTreasure(member.getId(), memberid, Treasure.TAG_MEMBER, Treasure.ACTION_COLLECT);
			return showJsonSuccess(model, "ȡ����ע�ɹ���");
		}else{
			return showJsonError(model, "ȡ����עʧ�ܣ�");
		}
	}
	/**
	 * ȡ����ע,���ݣ���Ŀ����
	 * @param model
	 * @param tid
	 * @return
	 */
	@RequestMapping("/wala/cancelTreasure.xhtml")
	public String cancelTreasure(ModelMap model, Long tid, @CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid,
			HttpServletRequest request) {
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		Treasure treasure = daoService.getObject(Treasure.class, tid);
		if (treasure == null)
			return show404(model, "����ʵĶ��󲻴��ڣ�");
		boolean b = false;
		if (member.getId().equals(treasure.getMemberid())) {
			try {
				walaApiService.delTreasure(treasure.getMemberid(), treasure.getRelatedid(), treasure.getTag(), treasure.getAction());
				daoService.removeObject(treasure);
				b = true;
			} catch (Exception e) {
				b = false;
			}
		} else {
			return show404(model, "�˶��������ע�ģ����ܶ���ȡ����ע");
		}
		if (b){
			if(StringUtils.equals(treasure.getTag(), "member")){
				memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_ATTENTIONCOUNT, 1, false);
			}
			memberCountService.updateMemberCount(treasure.getMemberid(), MemberStats.FIELD_FANSCOUNT, 1, false);
			walaApiService.delTreasure(treasure.getMemberid(), treasure.getRelatedid(), treasure.getTag(), treasure.getAction());
			return showJsonSuccess(model, "ȡ����ע�ɹ�!");
		}else{
			return showJsonError(model, "ȡ����עʧ�ܣ�");
		}
	}
}
