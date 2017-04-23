/**
 * 
 */
package com.gewara.untrans.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.json.cooperate.UnionPayFastCardbin;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.mongo.MongoService;
import com.gewara.pay.BackConstant;
import com.gewara.service.DaoService;
import com.gewara.service.OperationService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CooperateService;
import com.gewara.util.JsonUtils;
import com.gewara.util.VmUtils;

@Service("cooperateService")
public class CooperateServiceImpl implements CooperateService {
	public static final String DISNEY_NEW = "N";
	public static final String DISNEY_UNUSE = "L";
	public static final String DISNEY_USE = "Y";
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	public void setMongoService(MongoService mongoService) {
		this.mongoService = mongoService;
	}
	@Autowired@Qualifier("daoService")
	private DaoService daoService;
	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
	@Autowired@Qualifier("operationService")
	private OperationService operationService;

	public void setOperationService(OperationService operationService) {
		this.operationService = operationService;
	}
	private String[] SHBANKCODE = new String[]{"940021", "622892", "622985", "622468", "622987", "621243", "621050", "622278", "622279", "438600"};
	@Override
	public ErrorCode<String> checkShbankCode(Long orderid, String preCardno, String endCardno) {
		
		return this.checkBankCode(orderid, preCardno, endCardno, SHBANKCODE);
	}
	private String[] XYBANKCODE = new String[]{"90592", "966666","622909"};
	@Override
	public ErrorCode<String> checkXybankCode(Long orderid, String preCardno, String endCardno) {
		
		return this.checkBankCode(orderid, preCardno, endCardno, XYBANKCODE);
	}
	private String[] HXBANKCODE = new String[]{"539867", "528708","523959","528708","622636","622637","622638","625969"};
	@Override
	public ErrorCode<String> checkHxbankCode(Long orderid, String preCardno, String endCardno) {
		return this.checkHxBankCode(orderid, preCardno, endCardno, HXBANKCODE);
	}
	
	/**
	 * ��֤��bin
	 * 
	 * @param order
	 * @param paybank
	 * @param cardNumber
	 * @param spid
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 28, 2013 5:38:46 PM
	 */
	private ErrorCode<String> checkUnionPayFastCardBin(GewaOrder order,String paybank,String cardNumber,Long spid){
		if(StringUtils.isBlank(paybank)){
			paybank = "UNIONPAYFAST";
		}
		
		List<Map> qryMapList = mongoService.find(MongoData.NS_UNIONPAYFAST_CARDBIN + ":" + paybank, new HashMap());
		if(qryMapList == null || qryMapList.isEmpty()){
			return ErrorCode.SUCCESS;
		}
		for(Map prefixMap : qryMapList){
			if(StringUtils.startsWith(cardNumber,prefixMap.get("cardbin").toString())){
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderid", order.getId()+"");
				map.put("cardno", cardNumber);
				map.put("memberid", order.getMemberid()+"");
				map.put("bankname", paybank);
				map.put("paid_success", "false");
				mongoService.saveOrUpdateMap(map, "orderid", MongoData.NS_PAY_CARDNUMBER);
				return ErrorCode.getSuccessReturn("");
			}
		}
		return ErrorCode.getFailure("�ܱ�Ǹ��������Ŀ��Ų��ڴ˴��Ż��У����������룡");
	}
	
	@Override
	public ErrorCode<String> checkUnionPayFastCode(GewaOrder order,String paybank,String cardNumber,Long spid) {
		ErrorCode<String> result = checkUnionPayFastCardBin(order, paybank, cardNumber, spid);
		if(!result.isSuccess()) return result;
		
		String opkey = spid + ":"+ cardNumber;
		if(StringUtils.equals(paybank, "PABASFB")){
			boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 5);
			if(!allow){
				return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�");
			}
		}else if(StringUtils.equals(paybank, "BOC")){
			boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 7, OperationService.ONE_DAY * 30, 2);
			if(!allow){
				return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�����һ���������ֻ��ʹ�����Σ�");
			}
		}
		return ErrorCode.getSuccessReturn("");
		
	}
	
	
	@Override
	public ErrorCode<String> checkUnionPayFastCodeForSZ(GewaOrder order,String paybank,String cardNumber,Long spid) {		
		ErrorCode<String> result = checkUnionPayFastCardBin(order, paybank, cardNumber, spid);
		if(!result.isSuccess()) return result;
		
		String opkey = spid + ":"+ cardNumber;
		if(StringUtils.equals(paybank, "BOC")){
			boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 6);
			if(!allow){
				return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�");
			}
		}
		return ErrorCode.getSuccessReturn("");		
	}
	
	@Override
	public ErrorCode<String> checkUnionPayFastCodeForNyyh(GewaOrder order,String paybank,String cardNumber,Long spid) {
		if(!StringUtils.equals(paybank, "ABC")){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻֻ֧��ũҵ���У�");
		}
		ErrorCode<String> result = checkUnionPayFastCardBin(order, paybank, cardNumber, spid);
		if(!result.isSuccess()) return result;		
		String opkey = getOpkey(spid, cardNumber);
		boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 6);
		if(!allow){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�");
		}
		return ErrorCode.getSuccessReturn("");
	}
	
	@Override
	public ErrorCode<String> checkUnionPayFastCodeForCqnsyh(GewaOrder order,String paybank,String cardNumber,Long spid) {
		if(!StringUtils.equals(paybank, "CQRCB")){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻֻ֧������ũ�����У�");
		}
		ErrorCode<String> result = checkUnionPayFastCardBin(order, paybank, cardNumber, spid);
		if(!result.isSuccess()) return result;
		String opkey = getOpkey(spid, cardNumber);
		boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 6);
		if(!allow){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�");
		}
		return ErrorCode.getSuccessReturn("");
	}
	
	@Override
	public ErrorCode<String> checkUnionPayFastCodeForYouJie(GewaOrder order,String paybank,String cardNumber,Long spid) {
		ErrorCode<String> result = checkUnionPayFastCardBin(order, paybank, cardNumber, spid);
		if(!result.isSuccess()) return result;
		String opkey = getOpkey(spid, cardNumber);
		
		//opkey = "spd" + spcounter.getId();
		//String opkey = "UNIONPAYFASTYOUJIE0608:"+ cardNumber;
		boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 6);
		if(!allow){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�ֻ��ʹ��һ�Σ�");
		}
		return ErrorCode.getSuccessReturn("");
	}
	
	public ErrorCode<String> checkUnionPayFastCodeForWzcb(GewaOrder order,String paybank,String cardNumber,Long spid) {
		if(!StringUtils.equals(paybank, "WZCB")){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻֻ֧���������У�");
		}
		ErrorCode<String> result = checkUnionPayFastCardBin(order, paybank, cardNumber, spid);
		if(!result.isSuccess()) return result;
		String opkey = getOpkey(spid, cardNumber);
		boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 6);
		if(!allow){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�");
		}
		return ErrorCode.getSuccessReturn("");
	}
	
	public ErrorCode<String> checkUnionPayFastCodeForZdcb(GewaOrder order,String paybank,String cardNumber,Long spid) {
		if(!StringUtils.equals(paybank, "ZDCB")){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻֻ֧���������У�");
		}
		ErrorCode<String> result = checkUnionPayFastCardBin(order, paybank, cardNumber, spid);
		if(!result.isSuccess()) return result;

		String opkey = getOpkey(spid, cardNumber);
		boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 6, OperationService.ONE_DAY * 30, 2);
		if(!allow){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�����һ���������ֻ��ʹ�����Σ�");
		}
		return ErrorCode.getSuccessReturn("");
	}
	
	public ErrorCode<String> checkCommonCardbinOrCardNumLimit(GewaOrder order,Long spid,String cardNumber){
		SpecialDiscount specialDiscount = daoService.getObject(SpecialDiscount.class, spid);
		if(StringUtils.equals("true",specialDiscount.getCardNumUnique())){
			String opkey = getOpkey(spid, cardNumber);
			if(specialDiscount.getCardNumPeriodSpan() == null){
				boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_MINUTE * specialDiscount.getCardNumPeriodIntvel(), 
						specialDiscount.getCardNumLimitnum());
				if(!allow){
					return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�" + specialDiscount.getCardNumPeriodIntvel() + "������ֻ��ʹ��" + 
							specialDiscount.getCardNumLimitnum() + "�Σ�");	
				}
			}else{
				boolean allow = operationService.isAllowOperation(opkey,  OperationService.ONE_MINUTE * specialDiscount.getCardNumPeriodIntvel(), 
					OperationService.ONE_MINUTE * specialDiscount.getCardNumPeriodSpan(), specialDiscount.getCardNumLimitnum());
				if(!allow){
					return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�" + specialDiscount.getCardNumPeriodIntvel() + "������ֻ��ʹ��һ�Σ�����" + 
							specialDiscount.getCardNumPeriodSpan() + "�����ֻ��ʹ��" + specialDiscount.getCardNumLimitnum() + "�Σ�");
				}
			}
		}
		if(StringUtils.isNotBlank(specialDiscount.getCardbinUkey())){
			// ���ڰѿ�bin���ö��Ƶ�UnionPayFastCardbin�����У�������ǰһ��cardbin���ö�Ӧmongo��һ�����ϣ���Ϊ�˼����ⲿ�ִ��������
			// �ȵ������ϵĿ�BIN�Ż�����ˣ�����ɾ��
			List<Map> qryMapList = mongoService.find(specialDiscount.getCardbinUkey(), new HashMap());
			// �����ԭ���ļ���û��ȡ����BIN��������ڵ�UnionPayFastCardbin��������л�ȡ��Bin�ţ�������ԭ����ͬ�����ݽṹ
			if (CollectionUtils.isEmpty(qryMapList)) {
				UnionPayFastCardbin unionPayFastCardbin = mongoService.getObject(UnionPayFastCardbin.class, "cardbinUkey", specialDiscount.getCardbinUkey());
				if (unionPayFastCardbin != null && CollectionUtils.isNotEmpty(unionPayFastCardbin.getCardbinList())) {
					for (String cardbin : unionPayFastCardbin.getCardbinList()) {
						Map cardbinMap = new HashMap();
						cardbinMap.put("cardbin", cardbin);
						qryMapList.add(cardbinMap);
					}
				}
			}
			if(qryMapList == null || qryMapList.isEmpty()){
				return ErrorCode.SUCCESS;
			}
			for(Map prefixMap : qryMapList){
				String prefixbin = prefixMap.get("cardbin") == null ? "" : prefixMap.get("cardbin").toString();
				String suffixbin = prefixMap.get("suffixCardbin") == null ? "" : prefixMap.get("suffixCardbin").toString();
				if((StringUtils.isNotBlank(prefixbin) && StringUtils.startsWith(cardNumber,prefixbin) && StringUtils.isBlank(suffixbin))
						|| (StringUtils.isNotBlank(suffixbin) && StringUtils.endsWith(cardNumber,suffixbin) && StringUtils.isBlank(prefixbin))
						|| (StringUtils.isNotBlank(prefixbin) && StringUtils.startsWith(cardNumber,prefixbin) && 
								StringUtils.isNotBlank(suffixbin) && StringUtils.endsWith(cardNumber,suffixbin))){
					Map<String, String> map = new HashMap<String, String>();
					map.put("orderid", order.getId()+"");
					map.put("cardno", cardNumber);
					map.put("memberid", order.getMemberid()+"");
					map.put("bankname", order.getPaybank());
					map.put("paid_success", "false");
					mongoService.saveOrUpdateMap(map, "orderid", MongoData.NS_PAY_CARDNUMBER);
					return ErrorCode.getSuccessReturn("");
				}
			}
			return ErrorCode.getFailure("�ܱ�Ǹ��������Ŀ��Ų��ڴ˴��Ż��У����������룡");
		}
		return ErrorCode.getSuccessReturn("");
	}
	
	private String getOpkey(Long spid,String cardNumber){
		SpecialDiscount specialDiscount = daoService.getObject(SpecialDiscount.class, spid);
		String opkey = spid + ":" + cardNumber;
		if(specialDiscount != null && StringUtils.isNotBlank(specialDiscount.getCardUkey())){
			opkey = "spd" + specialDiscount.getCardUkey() + ":" + cardNumber;
		}else if(specialDiscount != null && specialDiscount.getSpcounterid() != null){
			opkey = "spd" + specialDiscount.getSpcounterid() + ":" + cardNumber;
		}
		return opkey;
	}
	
	/**
	 * ��������2.0��ݽӿ�֧����binУ�飬����ʹ�ô���
	 * 
	 * @param order
	 * @param cardNumber
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Mar 19, 2013 7:40:07 PM
	 */
	@Override
	public ErrorCode<String> checkUnionPayFastAJS(GewaOrder order,String cardNumber,Long spid) {
		String paybank = PaymethodConstant.PAYMETHOD_UNIONPAYFAST_ACTIVITY_JS;
		
		List<Map> qryMapList = mongoService.find(MongoData.NS_UNIONPAYFAST_CARDBIN + ":" + paybank, new HashMap());
		if(qryMapList == null || qryMapList.isEmpty()){
			return ErrorCode.SUCCESS;
		}
		//String opkey = PayUtil.PAYMETHOD_UNIONPAYFAST_ACTIVITY_JS + ":" + cardNumber;	
		for(Map prefixMap : qryMapList){
			if(StringUtils.startsWith(cardNumber,prefixMap.get("cardbin").toString())){
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderid", order.getId()+"");
				map.put("cardno", cardNumber);
				map.put("memberid", order.getMemberid()+"");
				map.put("bankname", paybank);
				map.put("paid_success", "false");
				mongoService.saveOrUpdateMap(map, "orderid", MongoData.NS_PAY_CARDNUMBER);
				
				//���������-2.0��֤֧���� һ�����ţ���һ����ڣ�һ������ֻ����һ��
				String opkey = spid + ":" + cardNumber;	
				boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 30);
				if(!allow){
					return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ������ֻ��ʹ��һ�Σ�");
				}					
					
				return ErrorCode.getSuccessReturn("");
			}
		}
		return ErrorCode.getFailure("�ܱ�Ǹ��������Ŀ��Ų��ڴ˴��Ż��У����������룡");
	}
	
	/**
	 * ��������2.0���ݽӿ�֧����binУ�飬����ʹ�ô���
	 * @param order
	 * @param cardNumber
	 * @return
	 */
	@Override
	public ErrorCode<String> checkUnionPayFastBJ(GewaOrder order,String cardNumber,Long spid) {
		String paybank = PaymethodConstant.PAYMETHOD_UNIONPAYFAST_ACTIVITY_BJ;
		List<Map> qryMapList = mongoService.find(MongoData.NS_UNIONPAYFAST_CARDBIN + ":" + paybank, new HashMap());
		if(qryMapList == null || qryMapList.isEmpty()){
			return ErrorCode.SUCCESS;
		}
		for(Map prefixMap : qryMapList){
			if(StringUtils.startsWith(cardNumber,prefixMap.get("cardbin").toString())){
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderid", order.getId()+"");
				map.put("cardno", cardNumber);
				map.put("memberid", order.getMemberid()+"");
				map.put("bankname", paybank);
				map.put("paid_success", "false");
				mongoService.saveOrUpdateMap(map, "orderid", MongoData.NS_PAY_CARDNUMBER);
				//���������-2.0��֤֧���� һ�����ţ���һ����ڣ�һ����ֻ����һ��
				String opkey = spid + ":" + cardNumber;	
				boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 6);
				if(!allow){
					return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�");
				}					
				return ErrorCode.getSuccessReturn("");
			}
		}
		return ErrorCode.getFailure("�ܱ�Ǹ��������Ŀ��Ų��ڴ˴��Ż��У����������룡");
	}
	
	@Override
	public ErrorCode checkShbankBack(Long orderid, String otherinfo, String isSave) {
		Map<String, String> map = VmUtils.readJsonToMap(otherinfo);
		String backcardno = map.get(BackConstant.cardNumber);
		String precardno = map.get(BackConstant.shbankcardno);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cardNumber", backcardno);
		params.put("paid_success", "true");
		Map fm = mongoService.findOne(MongoData.NS_PAY_CARDNUMBER, params);
		if(fm!=null) return ErrorCode.getFailure("error", "�˿��Ѿ��й��Żݣ������ٲμ��Żݣ�");
		if(StringUtils.isNotBlank(precardno) && StringUtils.isNotBlank(backcardno)){
			int len = backcardno.length();
			if(len<7) return ErrorCode.getFailure("�Ϻ����п�, ���ţ�"+backcardno + "�����д���");
			String[] pres = precardno.split("~");
			if(pres.length<2) return ErrorCode.getFailure("�Ϻ����п�, ���ţ�" + backcardno + "�����д���");
			String pre1 = pres[0];
			String pre2 = pres[1];
			String back1 = StringUtils.substring(backcardno, 0, 6);
			String back2 = StringUtils.substring(backcardno, len-4, len);
			if(StringUtils.equals(pre1, back1) && StringUtils.equals(pre2, back2)){
				if(StringUtils.equalsIgnoreCase(isSave, "true")){
					Map<String, String> mg = new HashMap<String, String>();
					mg.put("orderid", orderid+"");
					mg.put("cardNumber", backcardno);
					mg.put("paid_success", "true");
					mongoService.saveOrUpdateMap(mg, "orderid", MongoData.NS_PAY_CARDNUMBER);
				}
				return ErrorCode.SUCCESS;
			}else {
				return ErrorCode.getFailure(backcardno + "�������Ϻ����п��Ĺ���" + precardno);
			}
		}
		return ErrorCode.SUCCESS;
	}
	
	@Override
	public ErrorCode checkXybankBack(Long orderid, String otherinfo, String isSave) {
		return this.checkbankBack(orderid, otherinfo, BackConstant.xybankcardno, isSave, "��ҵ����");
	}

	private ErrorCode checkbankBack(Long orderid, String otherinfo,String key, String isSave,String bankName) {
		GewaOrder gewaOrder = daoService.getObject(GewaOrder.class, orderid);
		Map<String, String> map = VmUtils.readJsonToMap(otherinfo);
		String backcardno = map.get(BackConstant.cardNumber);
		String precardno = map.get(key);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cardNumber", backcardno);
		params.put("paid_success", "true");
		Map fm = mongoService.findOne(MongoData.NS_PAY_CARDNUMBER, params);
		if(fm!=null) return ErrorCode.getFailure("error", "�˿��Ѿ��й��Żݣ������ٲμ��Żݣ�");
		if(StringUtils.isNotBlank(precardno) && StringUtils.isNotBlank(backcardno)){
			int len = backcardno.length();
			if(len<7) return ErrorCode.getFailure(bankName+"��, ���ţ�"+backcardno + "�����д���");
			String[] pres = precardno.split("~");
			if(pres.length<2) return ErrorCode.getFailure(bankName+"��, ���ţ�" + backcardno + "�����д���");
			String pre1 = pres[0];
			String pre2 = pres[1];
			String back1 = StringUtils.substring(backcardno, 0, pre1.length());
			String back2 = StringUtils.substring(backcardno, len-4, len);
			if(StringUtils.equals(pre1, back1) && StringUtils.equals(pre2, back2)){
				if(StringUtils.equalsIgnoreCase(isSave, "true")){
					Map<String, String> mg = new HashMap<String, String>();
					mg.put("orderid", orderid+"");
					mg.put("memberid", gewaOrder.getMemberid()+"");
					mg.put("bankname", BackConstant.YXBACK);
					mg.put("cardNumber", backcardno);
					mg.put("paid_success", "true");
					mongoService.saveOrUpdateMap(mg, "orderid", MongoData.NS_PAY_CARDNUMBER);
				}
				return ErrorCode.SUCCESS;
			}else {
				return ErrorCode.getFailure(backcardno + "������"+bankName+"�Ĺ���" + precardno);
			}
		}
		return ErrorCode.SUCCESS;
	
	}
	private ErrorCode<String> checkHxBankCode(Long orderid,String preCardno,String endCardno,String[] prefixs){
		for(String prefix : prefixs){
			if(StringUtils.startsWith(preCardno, prefix)){
				/**
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("cardno", preCardno + "~" + endCardno);
				params.put("paid_success", "true");
				Map fm = mongoService.findOne(MongoData.NS_PAY_CARDNUMBER, params);
				if(fm!=null) return ErrorCode.getFailure("�˿��Ѿ��й��Żݣ������ٲμ��Żݣ�");
				*/
				GewaOrder gewaOrder = daoService.getObject(GewaOrder.class, orderid);
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderid", orderid+"");
				map.put("cardno", preCardno + "~" + endCardno);
				map.put("memberid", gewaOrder.getMemberid()+"");
				map.put("bankname", BackConstant.HXBACK);
				map.put("paid_success", "false");
				mongoService.saveOrUpdateMap(map, "orderid", MongoData.NS_PAY_CARDNUMBER);
				return ErrorCode.getSuccessReturn("");
			}
		}
		return ErrorCode.getFailure("����������ÿ����Ų��ڴ˴��Żݷ�Χ��");
	}
	
	private ErrorCode<String> checkBankCode(Long orderid,String preCardno,String endCardno,String[] prefixs){
		for(String prefix : prefixs){
			if(StringUtils.startsWith(preCardno, prefix)){
				/**
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("cardno", preCardno + "~" + endCardno);
				params.put("paid_success", "true");
				Map fm = mongoService.findOne(MongoData.NS_PAY_CARDNUMBER, params);
				if(fm!=null) return ErrorCode.getFailure("�˿��Ѿ��й��Żݣ������ٲμ��Żݣ�");
				*/
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderid", orderid+"");
				map.put("cardno", preCardno + "~" + endCardno);
				map.put("paid_success", "false");
				mongoService.saveOrUpdateMap(map, "orderid", MongoData.NS_PAY_CARDNUMBER);
				return ErrorCode.getSuccessReturn("");
			}
		}
		return ErrorCode.getFailure("������Ľ�ǿ����Ų��ڴ˴��Żݷ�Χ��");
	}
	//����ƽ������ һ�ſ�һ��ֻ��ʹ��һ��
	@Override
	public ErrorCode<String> checkUnionPayFastShenZhenCodeForPingAn(GewaOrder order,String paybank,String cardNumber,Long spid) {
		if(!StringUtils.equals(paybank, "PAB")){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻֻ֧��ƽ�����У�");
		}
		ErrorCode<String> result = checkUnionPayFastCardBin(order, "unionPayFast_activity_sz_PAB", cardNumber, spid);
		if(!result.isSuccess()) return result;
		String opkey = getOpkey(spid, cardNumber);
		boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 6);
		if(!allow){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�");
		}
		return ErrorCode.getSuccessReturn("");
	}
		
	//����ƽ������ һ�ſ�һ��ֻ��ʹ��һ��
	@Override
	public ErrorCode<String> checkUnionPayFastGuangzhouCodeForBocByWeekone(GewaOrder order,String paybank,String cardNumber,Long spid) {
		if(!StringUtils.equals(paybank, "BOC")){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻֻ֧��ƽ�����У�");
		}
		ErrorCode<String> result = checkUnionPayFastCardBin(order, "unionPayFast_activity_gz_BOCSH", cardNumber, spid);
		if(!result.isSuccess()) return result;
		String opkey = getOpkey(spid, cardNumber);
		boolean allow = operationService.isAllowOperation(opkey, OperationService.ONE_DAY * 6);
		if(!allow){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�");
		}
		return ErrorCode.getSuccessReturn("");
	}
	
	//�����й����� һ�ſ�һ����ֻ��ʹ������
	@Override
	public ErrorCode<String> checkUnionPayFastGuangzhouCodeForBocByMonthTwo(GewaOrder order,String paybank,String cardNumber,Long spid) {
		if(!StringUtils.equals(paybank, "BOC")){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻֻ֧���й����У�");
		}
		ErrorCode<String> result = checkUnionPayFastCardBin(order, "unionPayFast_activity_gz_BOCSH", cardNumber, spid);
		if(!result.isSuccess()) return result;
		String opkey = getOpkey(spid, cardNumber);
		boolean allow = operationService.isAllowOperation(opkey+"_1", OperationService.ONE_DAY * 6);
		if(!allow){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ����ֻ��ʹ��һ�Σ�");
		}
		allow = operationService.isAllowOperation(opkey+"_2", OperationService.ONE_DAY * 30, 2);
		if(!allow){
			return ErrorCode.getFailure("�ܱ�Ǹ�����Żݻÿ�����п�һ������ֻ��ʹ�����Σ�");
		}
		return ErrorCode.getSuccessReturn("");
	}

	@Override
	public boolean addCardnumOperation(String tradeno, String otherinfo,
			Long spid) {
		Map<String, String> map = JsonUtils.readJsonToMap(otherinfo);
		if(map.containsKey("cardNumber")){
			String cardNumber = map.get("cardNumber");
			String opkey = getOpkey(spid, cardNumber);
			SpecialDiscount specialDiscount = daoService.getObject(SpecialDiscount.class, spid);
			boolean allow = false;
			if(specialDiscount.getCardNumPeriodSpan() != null){
				allow = operationService.updateOperation(opkey, OperationService.ONE_MINUTE * specialDiscount.getCardNumPeriodIntvel(), 
						OperationService.ONE_MINUTE * specialDiscount.getCardNumPeriodSpan(), specialDiscount.getCardNumLimitnum(), tradeno);
			}else{
				allow = operationService.updateOperation(opkey, OperationService.ONE_MINUTE * specialDiscount.getCardNumPeriodIntvel(), 
						specialDiscount.getCardNumLimitnum(), tradeno);
			}
			return allow; 
		}
		return false;
	}
}
