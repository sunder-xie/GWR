package com.gewara.untrans.ticket.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gewara.Config;
import com.gewara.constant.ApiConstant;
import com.gewara.constant.sys.ConfigConstant;
import com.gewara.model.common.GewaConfig;
import com.gewara.service.DaoService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.ticket.TicketQueueService;
import com.gewara.util.DateUtil;
import com.gewara.util.GewaLogger;
import com.gewara.util.LoggerUtils;
import com.gewara.util.VmUtils;
@Service("ticketQueueService")
public class TicketQueueServiceImpl implements TicketQueueService, InitializingBean{
	private final transient GewaLogger dbLogger = LoggerUtils.getLogger(getClass(), Config.getServerIp(), Config.SYSTEMID);
	private static final String QUEUE_IP = "IQ";						//ip Queue
	private static final String QUEUE_MEMBER = "MQ";				//Member Queue
	private static final String QUEUE_CINEMA = "CQ";				//Cinema Queue
	private static final String QUEUE_MEMBER_TOTAL = "AMQ";		//All Member Queue
	private static final String QUEUE_PARTNER = "PQ";				//Partner Queue
	private static final String QUEUE_PARTNER_TOTAL = "APQ";		//All Partner Queue
	
	private static final int CACHETIME = 60*60*2;	//����ʱ��
	private long configUpdatetime = 0;
	private boolean memberEnabled = true;		//�û��µ��Ƿ�����
	private boolean partnerEnabled = true;		//�̼��µ��Ƿ�����
	private boolean onlyStats = true;			//�ǲ�ֻ��¼ͳ����Ϣ
	
	private int allMemberMaxTimes = 5000;		//�����û���������
	private int allMemberRoundTime = 10;		//�����û�����������ӣ�
	
	private int memberMaxTimes = 60;				//�û�������
	private int memberRoundTime = 10;			//�û�����������ӣ�
	
	private int ipMaxTimes = 60;					//ip������
	private int ipRoundTime = 2;					//IP����������ӣ�
	
	private int partnerMaxTimes = 1000;			//������
	private int partnerRoundTime = 2;			//�̼Ҽ���������ӣ�

	private int allPartnerMaxTimes = 5000;		//�����̼Ҵ�������
	private int allPartnerRoundTime = 10;		//�����̼Ҽ���������ӣ�

	private int cinemaMaxTimes = 5000;	//ӰԺ������
	private int cinemaRoundTime = 5;				//ӰԺʱ����
	private Map<Long/*cinemaid*/, Integer> cinemaMaxTimesMap = new HashMap<Long, Integer>();					//ӰԺ���Ʊ��������

	/*****************ͳ����Ϣ*********************/
	private Map<Long/*cinema*/, Integer> cinemaViewCount = new HashMap<Long, Integer>();						//��������ӰԺ����

	private Map<Long/*partnerid*/, Integer> partnerRejectedCount = new HashMap<Long, Integer>();				//�����̻����ܾ�����
	private Map<Long/*partnerid*/, Timestamp> partnerLastRejectedTime = new HashMap<Long, Timestamp>();	//�����̻����һ�α��ܾ�ʱ��
	private int totalPartnerRejectCount = 0;	//�̼ұ��ܾ����ܴ���
	private int totalPartnerCount = 0;			//�̼ҷ����ܴ���

	private Map<Long/*memberid*/, Integer> memberRejectedCount = new HashMap<Long, Integer>();				//�����û����ܾ�����
	private Map<Long/*memberid*/, Timestamp> memberLastRejectedTime = new HashMap<Long, Timestamp>();		//�����û����һ�α��ܾ�ʱ��
	private int totalMemberRejectCount = 0;	//�û����ܾ����ܴ���
	private int totalMemberCount = 0;			//�û������ܴ���
	
	private Map<String/*ip*/, Integer> ipRejectedCount = new HashMap<String, Integer>();						//IP���ܾ�����
	private Map<String/*ip*/, Timestamp> ipLastRejectedTime = new HashMap<String, Timestamp>();				//IP���һ�α��ܾ�ʱ��

	private Map<Long/*cinemaid*/, Integer> cinemaRejectedCount = new HashMap<Long, Integer>();				//����ӰԺ���ܾ�����
	private Map<Long/*cinemaid*/, Timestamp> cinemaLastRejectedTime = new HashMap<Long, Timestamp>();		//����ӰԺ���һ�α��ܾ�ʱ��

	@Autowired@Qualifier("daoService")
	private DaoService daoService;
	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
	
	@Autowired@Qualifier("memcachedClient")
	private MemcachedClient memcachedClient;	//
	private int getMin(Timestamp time){
		String minStr = DateUtil.format(time, "HHmm");
		int min = Integer.parseInt(minStr.substring(0,2)) * 60 + Integer.parseInt(minStr.substring(2));
		return min;
	}
	@Override
	public Map getStatistics() {
		Map stats = new HashMap();
		stats.put("partnerRejectedCount", new HashMap(partnerRejectedCount));
		stats.put("partnerLastRejectedTime", new HashMap(partnerLastRejectedTime));
		stats.put("totalPartnerCount", totalPartnerCount);
		stats.put("totalPartnerRejectCount", totalPartnerRejectCount);
		stats.put("memberRejectedCount", new HashMap(memberRejectedCount));
		stats.put("memberLastRejectedTime", new HashMap(memberLastRejectedTime));

		stats.put("totalMemberRejectCount", totalMemberRejectCount);
		stats.put("totalMemberCount", totalMemberCount);
		
		stats.put("ipRejectedCount", new HashMap(ipRejectedCount));
		stats.put("ipLastRejectedTime", new HashMap(ipLastRejectedTime));
		
		stats.put("cinemaViewCount", new HashMap(cinemaViewCount));

		stats.put("cinemaRejectedCount", new HashMap(cinemaRejectedCount));
		stats.put("cinemaLastRejectedTime", new HashMap(cinemaLastRejectedTime));
		return stats;
	}
	@Override
	public void refreshCurrent(String newConfig) {
		GewaConfig config = daoService.getObject(GewaConfig.class, ConfigConstant.CFG_TICKET_QUEUE);
		if(config!=null && config.getUpdatetime().getTime() > configUpdatetime){
			Map<String, String> jsonMap = VmUtils.readJsonToMap(config.getContent());
			try{
				memberEnabled = StringUtils.equals("true", jsonMap.get("memberEnabled"));
				partnerEnabled = StringUtils.equals("true", jsonMap.get("partnerEnabled"));
				onlyStats = StringUtils.equals("true", jsonMap.get("onlyStats"));
				
				allMemberMaxTimes = Integer.parseInt(jsonMap.get("allMemberMaxTimes"));
				allMemberRoundTime = Integer.parseInt(jsonMap.get("allMemberRoundTime"));
				
				ipMaxTimes = Integer.parseInt(jsonMap.get("ipMaxTimes"));
				ipRoundTime = Integer.parseInt(jsonMap.get("ipRoundTime"));

				partnerMaxTimes = Integer.parseInt(jsonMap.get("partnerMaxTimes"));
				partnerRoundTime = Integer.parseInt(jsonMap.get("partnerRoundTime"));
				
				allPartnerMaxTimes = Integer.parseInt(jsonMap.get("allPartnerMaxTimes"));
				allPartnerRoundTime = Integer.parseInt(jsonMap.get("allPartnerRoundTime"));
				
				memberMaxTimes = Integer.parseInt(jsonMap.get("memberMaxTimes"));
				memberRoundTime = Integer.parseInt(jsonMap.get("memberRoundTime"));
				
				cinemaMaxTimes = Integer.parseInt(jsonMap.get("cinemaMaxTimes"));
				cinemaRoundTime = Integer.parseInt(jsonMap.get("cinemaRoundTime"));
			}catch(Exception e){
				dbLogger.warn("", e);
				memberEnabled = false;
				partnerEnabled = false;
			}
		}
		GewaConfig cinemaConfig = daoService.getObject(GewaConfig.class, ConfigConstant.CFG_TICKET_QUEUE_CINEMA);
		if(cinemaConfig != null){
			try{
				cinemaMaxTimesMap.clear();
				Map<String, String> jsonMap = VmUtils.readJsonToMap(cinemaConfig.getContent());
				for(String key: jsonMap.keySet()){
					cinemaMaxTimesMap.put(new Long(key), new Integer(jsonMap.get(key)));
				}
			}catch(Exception e){
				
			}
		}
	}
	@Override
	public ErrorCode isMemberAllowed(Long memberid, Long cinemaid, String ip) {
		if(!memberEnabled) return ErrorCode.SUCCESS;
		totalMemberCount++;
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		int min = getMin(cur);
		try {
			//1.�ܿ���
			Integer totalCount = (Integer) memcachedClient.get(QUEUE_MEMBER_TOTAL + min);
			if(totalCount==null) totalCount = 1;
			long totalSum = totalCount + getLastSum(QUEUE_MEMBER_TOTAL, min, allMemberRoundTime);
			
			if(totalSum > allMemberMaxTimes){
				totalMemberRejectCount += 1;
				if(!onlyStats) return ErrorCode.getFailure("ӰԺ��Ʊϵͳ�����1~3�������ԣ�");
			}
			
			//2.IP����
			String ipkey = QUEUE_IP + ip + "AT";
			Integer ipcount = (Integer) memcachedClient.get(ipkey + min);
			if(ipcount==null) ipcount = 1;
			long ipSum = ipcount + getLastSum(ipkey, min, ipRoundTime);
			
			if(ipSum > ipMaxTimes){
				ipLastRejectedTime.put(ip, cur);
				Integer iptimes = ipRejectedCount.get(ip);
				if(iptimes == null) ipRejectedCount.put(ip, 1);
				else ipRejectedCount.put(ip, iptimes +1);
				if(!onlyStats) return ErrorCode.getFailure("����������IP����Ƶ�ʹ��ߣ����1~3���Ӻ����ԣ�");
			}
			
			//3.�û�����
			String mkey = QUEUE_MEMBER + memberid + "AT";
			Integer memberCount = (Integer) memcachedClient.get(mkey + min);
			if(memberCount==null) memberCount = 1;
			Integer memberSum = memberCount + getLastSum(mkey, min, memberRoundTime);
			if(memberSum > memberMaxTimes){
				memberLastRejectedTime.put(memberid, cur);
				Integer membertimes = memberRejectedCount.get(memberid);
				if(membertimes == null) memberRejectedCount.put(memberid, 1);
				else memberRejectedCount.put(memberid, membertimes +1);
				if(!onlyStats) return ErrorCode.getFailure("���ķ���Ƶ�ʹ��ߣ����1~3���Ӻ����ԣ�");
			}
			//4��ӰԺ����
			String ckey = QUEUE_CINEMA + cinemaid + "AT";
			Integer cinemaCount = (Integer) memcachedClient.get(ckey + min);
			if(cinemaCount==null) cinemaCount = 1;
			Integer cinemaSum = cinemaCount + getLastSum(ckey + min, min, cinemaRoundTime);
			int cm = getCinemaMaxTimes(cinemaid);
			if(cinemaSum > cm){
				cinemaLastRejectedTime.put(memberid, cur);
				Integer cinematimes = cinemaRejectedCount.get(cinemaid);
				if(cinematimes == null) cinemaRejectedCount.put(cinemaid, 1);
				else cinemaRejectedCount.put(cinemaid, cinematimes +1);
				if(!onlyStats) return ErrorCode.getFailure("ӰԺ��������æ�����1~3���Ӻ����ԣ�");
			}
			
			
			memcachedClient.set(QUEUE_MEMBER_TOTAL + min, CACHETIME, totalCount+1);
			memcachedClient.set(QUEUE_IP + min, CACHETIME, ipcount+1);
			memcachedClient.set(QUEUE_MEMBER + min, CACHETIME, memberCount+1);
			Integer cinemacount = cinemaViewCount.get(cinemaid);
			cinemaViewCount.put(cinemaid, cinemacount==null?1:cinemacount+1);
			return ErrorCode.SUCCESS;		
		} catch (Exception e) {
			dbLogger.error("", e);
			return ErrorCode.SUCCESS;
		}
	}
	@Override
	public ErrorCode isPartnerAllowed(Long partnerid, Long cinemaid) {
		if(!partnerEnabled) return ErrorCode.SUCCESS;
		totalPartnerCount++;
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		int min = getMin(cur);
		try {
			//1.�����̼��ܿ���
			Integer totalCount = (Integer) memcachedClient.get(QUEUE_PARTNER_TOTAL + min);
			if(totalCount==null) totalCount = 1;
			Integer totalSum = totalCount + getLastSum(QUEUE_PARTNER_TOTAL, min, allPartnerRoundTime);
			
			if(totalSum > allPartnerMaxTimes){
				totalPartnerRejectCount += 1;
				if(!onlyStats) return ErrorCode.getFailure(ApiConstant.CODE_UNKNOWN_ERROR, "ӰԺ��Ʊϵͳ�����1~3�������ԣ�");
			}
			//2.�����̼ҿ���
			Integer partnerCount = (Integer) memcachedClient.get(QUEUE_PARTNER + min);
			if(partnerCount==null) partnerCount = 1;
			Integer partnerTotal = partnerCount + getLastSum(QUEUE_PARTNER, min, partnerRoundTime);
			if(partnerTotal > partnerMaxTimes){
				partnerLastRejectedTime.put(partnerid, cur);
				Integer partnertimes = partnerRejectedCount.get(partnerid);
				if(partnertimes == null) partnerRejectedCount.put(partnerid, 1);
				else partnerRejectedCount.put(partnerid, partnertimes +1);
				if(!onlyStats) return ErrorCode.getFailure(ApiConstant.CODE_UNKNOWN_ERROR, "ӰԺ��Ʊϵͳ�����1~3�������ԣ�");
			}

			memcachedClient.set(QUEUE_PARTNER_TOTAL + min, CACHETIME, partnerCount + 1);
			memcachedClient.set(QUEUE_PARTNER + min, CACHETIME, partnerCount + 1);
			
			Integer cinemacount = cinemaViewCount.get(cinemaid);
			cinemaViewCount.put(cinemaid, cinemacount==null?1:cinemacount+1);
			return ErrorCode.SUCCESS;
		} catch (Exception e) {
			dbLogger.error("", e);
			return ErrorCode.SUCCESS;
		}
	}
	private int getLastSum(String keypre, int curMin, int roundTime){
		String[] keys = new String[roundTime-1];
		for(int i = 0; i<roundTime -1; i++){
			keys[i] = keypre + (curMin - i -1);
		}
		int sumCount = 0;
		try {
			Map<String, Object> lastMap = memcachedClient.getBulk(Arrays.asList(keys));
			for(Object c: lastMap.values()){
				sumCount += (Integer) c;
			}
			return sumCount;
		} catch (Exception e) {
			dbLogger.error("", e);
			return 0;
		}
	}
	@Override
	public void clearData() {
		clearData(QUEUE_IP, 20);
		clearData(QUEUE_MEMBER, 20);
		clearData(QUEUE_MEMBER_TOTAL, 20);
		clearData(QUEUE_PARTNER, 20);
		clearData(QUEUE_PARTNER_TOTAL, 20);
	}
	private void clearData(String keypre, int count){
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		int min = getMin(cur);
		for(int i=0;i<count; i++){
			OperationFuture<Boolean> result = memcachedClient.delete(keypre + (min-i));
			dbLogger.warn(keypre + (min-i) + result.getStatus().isSuccess());
		}
	}
	private int getCinemaMaxTimes(Long cinemaid){
		if(cinemaMaxTimesMap.containsKey(cinemaid)) return cinemaMaxTimesMap.get(cinemaid);
		return cinemaMaxTimes;
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
}
