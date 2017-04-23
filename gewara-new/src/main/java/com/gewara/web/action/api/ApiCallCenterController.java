package com.gewara.web.action.api;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.ApiConstant;
import com.gewara.constant.ticket.OrderConstant;
import com.gewara.model.drama.DramaOrder;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.GymOrder;
import com.gewara.model.pay.SportOrder;
import com.gewara.model.pay.TicketOrder;
import com.gewara.model.user.Invoice;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberInfo;
import com.gewara.service.OperationService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.activity.SynchActivityService;
import com.gewara.untrans.hbase.HBaseService;
import com.gewara.untrans.hbase.HbaseData;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.xmlbind.activity.RemoteActivity;
import com.gewara.xmlbind.activity.RemoteApplyjoin;

@Controller
public class ApiCallCenterController extends BaseApiController {
	@Autowired@Qualifier("hbaseService")
	private HBaseService hbaseService;
	
	@Autowired@Qualifier("synchActivityService")
	private SynchActivityService synchActivityService;
	public void setActivityRemoteService(SynchActivityService synchActivityService) {
		this.synchActivityService = synchActivityService;
	}
	
	/**
	 * �����ֻ������ȡ�����һ���ɹ������Ķ�����
	 * @param key
	 * @param encryptCode
	 * @param mobile
	 * @param model
	 * @return
	 */
	@RequestMapping("/api/oa/getLastTradeNoByMobile.xhtml")
	public String getLastTradeNoByMobile(String key,String encryptCode, String mobile, ModelMap model){
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		if(mobile == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		List<GewaOrder> orderList = getOrderList(mobile);
		model.put("orderList", orderList);
		return getXmlView(model, "api/info/member/gewaOrderList.vm");
	}
	
	private List<GewaOrder> getOrderList(String mobile){
		DetachedCriteria query=DetachedCriteria.forClass(GewaOrder.class);
		query.add(Restrictions.eq("status", OrderConstant.STATUS_PAID_SUCCESS));
		query.add(Restrictions.eq("mobile", mobile));
		query.add(Restrictions.ge("paidtime", DateUtil.addHour(DateUtil.getMillTimestamp(), -24)));
		query.addOrder(Order.desc("paidtime"));
		List<GewaOrder> orderList = hibernateTemplate.findByCriteria(query, 0, 10);
		return orderList;
	}
	
	/**
	 * ��ȡ��Ա��Ϣ
	 */
	@RequestMapping("/api/oa/member.xhtml")
	public String getMemberByMobile(String key,String encryptCode, String mobile,ModelMap model){
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		if(mobile == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		Member member = getMember(mobile);
		if(member == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "û���ҵ��û���Ϣ��");
		MemberInfo memberInfo = daoService.getObject(MemberInfo.class, member.getId());
		model.put("memberInfo",memberInfo);
		model.put("member",member);
		return getXmlView(model, "api/info/member/member.vm");
	}

	/**
	 * ��ȡ�û���Ʊ��Ϣ
	 */
	@RequestMapping("/api/oa/gewaOrder.xhtml")
	public String getTicketByMobile(String key,String encryptCode, String email,String status,ModelMap model){
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		if(email == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		List<GewaOrder> list = this.getGewaOrder(email, status);
		if(list == null || list.isEmpty()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "û���ҵ���Ʊ��Ϣ��");
		model.put("gewaOrderList",list);
		return getXmlView(model, "api/info/member/gewaOrder.vm");
	}

	/**
	 * ��ȡ��Ʊ��Ϣ
	 */
	@RequestMapping("/api/oa/getTicketByTradeNo.xhtml")
	public String getTicketByTradeNo(String key,String encryptCode,
			String tradeNo,String tag, ModelMap model){
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		if(tradeNo == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		if("more".equals(tag)){
			return this.getTicketByMoreTradeNo(tradeNo, model);
		}
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeNo, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "û���ҵ�������Ϣ��");
		List<GewaOrder> list = new ArrayList<GewaOrder>();
		Member member = this.daoService.getObject(Member.class, order.getMemberid());
		list.add(order);
		model.put("gewaOrderList",list);
		model.put("member",member);
		return getXmlView(model, "api/info/member/gewaOrder.vm");
	}
	
	private String getTicketByMoreTradeNo(String tradeNo,ModelMap model){
		String[] tradeNos = StringUtils.split(tradeNo, ",");
		DetachedCriteria criteria = DetachedCriteria.forClass(GewaOrder.class);
		criteria.add(Restrictions.in("tradeNo", tradeNos))
			.addOrder(Order.desc("addtime"));
		List<GewaOrder> orders = this.hibernateTemplate.findByCriteria(criteria);
		if(orders == null || orders.size() == 0){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "û���ҵ�������Ϣ��");
		}
		List<Long> memberids = BeanUtil.getBeanPropertyList(orders, long.class, "memberid", false);
		List<Member> members = this.hibernateTemplate.findByCriteria(DetachedCriteria.forClass(Member.class)
				.add(Restrictions.in("id",memberids)));
		Map<Long,Member> memberMap = BeanUtil.beanListToMap(members, "id");
		model.put("gewaOrderList", orders);
		model.put("member",memberMap);
		return getXmlView(model, "api/info/member/gewaOrderMore.vm");
	}
	/**
	 * ��ȡ��������ʷ��¼
	 */
	@RequestMapping("/api/oa/getTicketHistoryLog.xhtml")
	public String getTicketHistoryLog(String key,String encryptCode,
			String tradeNo,ModelMap model){
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		if(tradeNo == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		//��������ʷ��¼
		Map<String, String> params = new HashMap<String, String>();
		params.put("tradeNo", tradeNo);
		Map<Long, Map<String, String>> changeList = hbaseService.getMultiVersionRow(HbaseData.TABLE_GEWAORDER, tradeNo.getBytes(), 100);
		model.put("hisLogList", changeList.values());
		return getXmlView(model, "api/info/member/gewaOrderHisList.vm");
	}
	/**
	 * ��ȡ��Ʊ��Ϣ
	 */
	@RequestMapping("/api/oa/getInvoiceById.xhtml")
	public String getInvoiceById(String key,String encryptCode, Long id,ModelMap model){
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		if(id == null) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		Invoice invoice = daoService.getObjectByUkey(Invoice.class, "id", id, false);
		if(invoice == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "û���ҵ���Ʊ��Ϣ��");
		model.put("invoice",invoice);
		return getXmlView(model, "api/info/member/invoice.vm");
	}
	
	private List<GewaOrder> getGewaOrder(String email, String status){
		Member member = getMember(email);
		if(member == null) return null;
		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Timestamp beginTime = DateUtil.addDay(endTime, -30);
		DetachedCriteria query = DetachedCriteria.forClass(GewaOrder.class);
		query.add(Restrictions.ge("addtime", beginTime));
		query.add(Restrictions.le("addtime", endTime));
		query.add(Restrictions.eq("memberid", member.getId()));
		query.add(Restrictions.like("status", status, MatchMode.START));
		query.addOrder(Order.desc("addtime"));
		List<GewaOrder> list =hibernateTemplate.findByCriteria(query);
		return list;
	}
	public Member getMember(String mobile){
		DetachedCriteria query = DetachedCriteria.forClass(Member.class);
		Criterion mobileOrEmail = Restrictions.or(Restrictions.eq("mobile", mobile), Restrictions.eq("email", mobile));
		query.add(Restrictions.or(Restrictions.eq("nickname", mobile),mobileOrEmail));
		List<Member> list =hibernateTemplate.findByCriteria(query);
		if(list.isEmpty()) return null;
		return list.get(0);
	}

	@RequestMapping("/api/oa/reSend.xhtml")
	public String reSend(String key, String encryptCode, String tradeno, ModelMap model){
		ApiAuth auth = checkRights(encryptCode, tradeno, key);
		if(!auth.isChecked()) return getErrorXmlView(model, auth.getCode(), auth.getMsg());
		if(StringUtils.isBlank(tradeno)) return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		GewaOrder order = daoService.getObjectByUkey(GewaOrder.class, "tradeNo", tradeno, false);
		if(order == null) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "��ѯ��Ϣ�����ڣ�");
		if(!StringUtils.equals(order.getStatus(), OrderConstant.STATUS_PAID_SUCCESS)){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�ǳɹ��Ķ������ܷ�����Ϣ");
		}
		if(!order.isPaidSuccess()) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "δ�ɹ��Ķ�����");
		Timestamp yesterday = DateUtil.addHour(DateUtil.getCurFullTimestamp(), -24);
		if(order.getPaidtime().before(yesterday)) return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "ֻ���ط���һ��֮�ڵĶ������ţ�");
		String opkey = OperationService.TAG_SENDTICKETPWD + auth.getApiUser().getId() + order.getId();
		if(!operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 3, 3)){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR,  "ͬһ�������ֻ�ܷ���3�Σ�");
		}
		untransService.reSendOrderMsg(order);
		operationService.updateOperation(opkey, OperationService.ONE_DAY * 3, 3);
		return getXmlView(model, "api/outorder/result.vm");
	}
	
	
	private static final List<String> orderTypeList=new ArrayList<String>();
	
	static{
		orderTypeList.add("ticket");
		orderTypeList.add("sport");
		orderTypeList.add("drama");
		orderTypeList.add("gym");
		orderTypeList.add("goods");
	}
	
	/**
	 * ��ȡ��Ʊ�б���
	 */
	@RequestMapping("/api/oa/getOrderList.xhtml")
	public String getOrderList(
			@RequestParam(required=true,value="key") String key,
			@RequestParam(required=true,value="encryptCode") String encryptCode,
			@RequestParam(defaultValue="0",required=false,value="pageNo")Integer pageNo,
			@RequestParam(defaultValue="20",required=false,value="maxnum")Integer maxnum,
			@RequestParam(required=true,value="beginTime") Timestamp beginTime,
			@RequestParam(required=true,value="endTime") Timestamp endTime,
			String citycode,
			String orderType,
			Long relatedid,
			ModelMap model){
		ApiAuth apiAuth = checkRights(encryptCode, key);
		if(!apiAuth.isChecked()) {
			return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		}
		if(maxnum<20){
			maxnum=20;
		}
		if(maxnum>500){
			maxnum=500;
		}
		//����ʱ�������ڿ�ʼʱ��
		if(endTime.before(beginTime)){
			return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "����ʱ�������ڿ�ʼʱ��");
		}
		Timestamp curDate=DateUtil.getCurFullTimestamp();//��ǰʱ��
		Timestamp tempDate=DateUtil.addHour(curDate, -24);//��ǰʱ���24Сʱ
		//��ʼʱ�䲻��С�ڵ�ǰʱ���24Сʱ
		if(beginTime.before(tempDate)){
			return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "��ʼʱ�䲻��С�ڵ�ǰʱ���24Сʱ");
		}
		//��ʼʱ��С�ڵ�ǰʱ����Ĭ��Ϊ��ǰʱ���24Сʱ
		if(beginTime.before(curDate)){
			beginTime=tempDate;
		}
		if(StringUtils.isNotBlank(orderType)&&!orderTypeList.contains(orderType)){
			return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "δ֪�Ķ�������");
		}
		List<GewaOrder> orderList=getGewaOrderList(beginTime,endTime,pageNo,maxnum,citycode,orderType,relatedid);
		long count=getGewaOrderListCount(beginTime,endTime,citycode,orderType,relatedid);
		model.put("orderList", orderList);
		model.put("count", count);
		return getXmlView(model, "api/info/member/gewaOrderList.vm");
	}
	
	/**
	 * ��ѯ����
	 * @param beginTime ��ʼʱ��
	 * @param endTime ����ʱ��
	 * @param pageNo ҳ��
	 * @param maxnum ҳ��С
	 * @param citycode ���д���
	 * @param orderType ��������
	 * @param relatedid ��Ŀid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<GewaOrder> getGewaOrderList(Timestamp beginTime,Timestamp endTime,Integer pageNo,Integer maxnum,String citycode,String orderType,Long relatedid){
		DetachedCriteria query = getQuery(beginTime, endTime, citycode,orderType, relatedid);
		query.addOrder(Order.desc("addtime"));
		List<GewaOrder> list =hibernateTemplate.findByCriteria(query,pageNo*maxnum,maxnum);
		return list;
	}
	/**
	 * ��ѯ��������
	 * @param beginTime ��ʼʱ��
	 * @param endTime ����ʱ��
	 * @param citycode ���д���
	 * @param orderType ��������
	 * @param relatedid ��Ŀid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private long getGewaOrderListCount(Timestamp beginTime,Timestamp endTime,String citycode,String orderType, Long relatedid){
		DetachedCriteria query = getQuery(beginTime, endTime, citycode,orderType, relatedid);
		query.setProjection(Projections.rowCount());
		List<Long> list =hibernateTemplate.findByCriteria(query);
		if (list.isEmpty()) return 0;
		return Integer.parseInt(""+list.get(0));
	}
	
	private DetachedCriteria getQuery(Timestamp beginTime, Timestamp endTime,
			String citycode, String orderType, Long relatedid) {
		Class<?> orderClass=GewaOrder.class;
		String relatedKey=null;
		if("ticket".equals(orderType)){
			orderClass=TicketOrder.class;
			relatedKey="movieid";
		}else if("sport".equals(orderType)){
			orderClass=SportOrder.class;
			relatedKey="itemid";
		}else if("gym".equals(orderType)){
			orderClass=GymOrder.class;
			relatedKey="gci";
		}else if("drama".equals(orderType)){
			orderClass=DramaOrder.class;
			relatedKey="dramaid";
		}
		DetachedCriteria query = DetachedCriteria.forClass(orderClass);
		query.add(Restrictions.ge("addtime", beginTime));
		query.add(Restrictions.le("addtime", endTime));
		query.add(Restrictions.eq("status", "paid_success"));
		query.add(Restrictions.isNull("changehis"));
		if(StringUtils.isNotBlank(citycode)){
			query.add(Restrictions.eq("citycode", citycode));
		}
		if(StringUtils.isNotBlank(relatedKey)&&null!=relatedid){
			query.add(Restrictions.eq(relatedKey, relatedid));
		}
		return query;
	}
	
	/**
	 * ��ȡ�û���ʵ��������ַ���ֻ���Ϣ
	 */
	@RequestMapping("/api/oa/getMemberInfoList.xhtml")
	public String getMemberInfoList(
			String key,
			String encryptCode,
			Long userid,
			String username,
			String mobile,
			Integer pageNo,
			Integer maxnum,
			String type,
			ModelMap model){
		
		ApiAuth apiAuth = checkRights(encryptCode, key);
		
		if(!apiAuth.isChecked()){
			return getErrorXmlView(model, apiAuth.getCode(), apiAuth.getMsg());
		} 
		
		if(null==userid&&StringUtils.isBlank(username)&&StringUtils.isBlank(mobile)){
			return getErrorXmlView(model, ApiConstant.CODE_PARAM_ERROR, "���ݲ�������");
		}
		
		Member member=null;
		if(null!=userid){
			member=daoService.getObject(Member.class, userid);
		}else if(StringUtils.isNotBlank(username)){
			member=memberService.getMemberByNickname(username);
		}else if(StringUtils.isNotBlank(mobile)){
			member=daoService.getObjectByUkey(Member.class, "mobile", mobile, true);
		}
		
		if(null==member){
			return getErrorXmlView(model, ApiConstant.CODE_DATA_ERROR, "�û������ڣ�");
		}
		
		Map<Long, String> actNameMap = new HashMap<Long, String>();//�
		List<Map> dramaOrderList = new ArrayList<Map>();//���綩��
		List<RemoteApplyjoin> applyJoinList = new ArrayList<RemoteApplyjoin>();
		if(StringUtils.equals(type, "activity")){
			ErrorCode<List<RemoteApplyjoin>> codeJoin = synchActivityService.getApplyJoinListByMemberid(member.getId(), pageNo*maxnum, maxnum);
			if(codeJoin.isSuccess()){
				applyJoinList = codeJoin.getRetval();
				List<Long> activityIds = BeanUtil.getBeanPropertyList(applyJoinList, Long.class, "relatedid", true);
				ErrorCode<List<RemoteActivity>> code = synchActivityService.getRemoteActivityListByIds(activityIds);
				if(code.isSuccess() && code.getRetval() != null){
					for(RemoteApplyjoin join : applyJoinList){
						for(RemoteActivity activity : code.getRetval()){
							if(join.getRelatedid().equals(activity.getId())){
								actNameMap.put(join.getId(), activity.getTitle());
								break;
							}
						}
					}
				}
				/*for (RemoteApplyjoin applyJoin : applyJoinList) {
					ErrorCode<RemoteActivity> code = synchActivityService.getRemoteActivity(applyJoin.getRelatedid());
					if(code.isSuccess()){
						RemoteActivity activity = code.getRetval();
						actNameMap.put(applyJoin.getId(), activity.getTitle());
					}
				}*/
			}
			
		}else if(StringUtils.equals(type, "drama_order")){
			String hql="from DramaOrder where memberid=? order by addtime ";
			List<DramaOrder> orderList=daoService.queryByRowsRange(hql, pageNo*maxnum, maxnum, member.getId());
			for (DramaOrder dramaOrder : orderList) {
				dramaOrderList.add(JsonUtils.readJsonToMap(dramaOrder.getDescription2()));
			}
		}
		model.put("member", member);
		model.put("applyJoinList", applyJoinList);
		model.put("actNameMap", actNameMap);
		model.put("dramaOrderList", dramaOrderList);
		return getXmlView(model, "api/info/member/memberInfoList.vm");
	}
	
}
