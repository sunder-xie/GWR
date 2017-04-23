package com.gewara.service.pay.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Service;

import com.gewara.api.pay.domain.Bank;
import com.gewara.api.pay.domain.Gateway;
import com.gewara.api.pay.domain.Merchant;
import com.gewara.model.common.City;
import com.gewara.model.pay.PayCityMerchant;
import com.gewara.model.pay.PayGateway;
import com.gewara.model.pay.PayGatewayBank;
import com.gewara.model.pay.PayInterfaceSwitch;
import com.gewara.model.pay.PayMerchant;
import com.gewara.service.impl.BaseServiceImpl;
import com.gewara.service.pay.GatewayService;
import com.gewara.support.ErrorCode;
import com.gewara.util.BeanUtil;
import com.gewara.util.ChangeEntry;

@Service("gatewayService")
public class GatewayServiceImpl extends BaseServiceImpl implements GatewayService {
	
	@Override
	public void synAllGateway(List<Gateway> gatewayList){
		List<PayGateway> oldGatewayList = baseDao.getAllObjects(PayGateway.class);
		Map<String,PayGateway> oldGatewayMap = BeanUtil.beanListToMap(oldGatewayList, "id");
		
		for(Gateway bgw : gatewayList){
			PayGateway gateway = oldGatewayMap.remove(bgw.getId());
			if(gateway == null){
				gateway = new PayGateway();
				BeanUtil.copyProperties(gateway, bgw);
				baseDao.saveObject(gateway);
				addMerchant(bgw.getMerchantList());
				addBank(bgw.getBankList());
			}else{
				ChangeEntry changeEntry = new ChangeEntry(gateway);
				BeanUtil.copyProperties(gateway, bgw);
				Map<String, String> changeMap = changeEntry.getChangeMap(gateway);
				if(!changeMap.isEmpty()){
					gateway.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					baseDao.saveObject(gateway);
				}
				updateMerchant(bgw.getId(), bgw.getMerchantList());
				updateBank(bgw.getId(), bgw.getBankList());
			}
		}
		
		for(PayGateway gateway : oldGatewayMap.values()) {
			baseDao.removeObject(gateway);
			deleteMerchant(gateway.getId());
			deleteBank(gateway.getId());
		}
	}
	
	private void addMerchant(List<Merchant> merchantList){
		if(merchantList == null || merchantList.isEmpty()) return;
		for(Merchant bmer : merchantList){
			PayMerchant merchant = new PayMerchant();
			BeanUtil.copyProperties(merchant, bmer);
			baseDao.saveObject(merchant);
		}
	}
	
	private void addBank(List<Bank> bankList){
		if(bankList == null || bankList.isEmpty()) return;
		for(Bank bbank : bankList){
			PayGatewayBank bank = new PayGatewayBank();
			BeanUtil.copyProperties(bank, bbank);
			baseDao.saveObject(bank);
		}
	}
	
	private void updateMerchant(Long gatewayId,List<Merchant> merchantList){
		if(merchantList == null || merchantList.isEmpty()) return;

		List<PayMerchant> oldMerchantList = baseDao.getObjectListByField(PayMerchant.class, "gatewayId", gatewayId);
		Map<String,PayMerchant> oldMerchantMap = BeanUtil.beanListToMap(oldMerchantList, "id");
		for(Merchant bmer : merchantList){
			PayMerchant merchant = oldMerchantMap.remove(bmer.getId());
			if(merchant == null){
				merchant = new PayMerchant();
				BeanUtil.copyProperties(merchant, bmer);
				baseDao.saveObject(merchant);
			}else{
				ChangeEntry changeEntry = new ChangeEntry(merchant);
				BeanUtil.copyProperties(merchant, bmer);
				Map<String, String> changeMap = changeEntry.getChangeMap(merchant);
				if(!changeMap.isEmpty()){
					merchant.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					baseDao.saveObject(merchant);
				}
			}
		}
		
		for(PayMerchant merchant : oldMerchantMap.values()) {
			baseDao.removeObject(merchant);
		}
	}
	
	private void updateBank(Long gatewayId,List<Bank> bankList){
		if(bankList == null || bankList.isEmpty()) return;

		List<PayGatewayBank> oldBankList = baseDao.getObjectListByField(PayGatewayBank.class, "gatewayId", gatewayId);
		Map<String,PayGatewayBank> oldBankMap = BeanUtil.beanListToMap(oldBankList, "id");
		for(Bank bbank : bankList){
			PayGatewayBank bank = oldBankMap.remove(bbank.getId());
			if(bank == null){
				bank = new PayGatewayBank();
				BeanUtil.copyProperties(bank, bbank);
				baseDao.saveObject(bank);
			}else{
				ChangeEntry changeEntry = new ChangeEntry(bank);
				BeanUtil.copyProperties(bank, bbank);
				Map<String, String> changeMap = changeEntry.getChangeMap(bank);
				if(!changeMap.isEmpty()){
					bank.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					baseDao.saveObject(bank);
				}
			}
		}
		
		for(PayGatewayBank bank : oldBankMap.values()) {
			baseDao.removeObject(bank);
		}
	}
	
	private void deleteMerchant(Long gatewayId){
		List<PayMerchant> oldMerchantList = baseDao.getObjectListByField(PayMerchant.class, "gatewayId", gatewayId);
		baseDao.removeObjectList(oldMerchantList);
	}
	
	private void deleteBank(Long gatewayId){
		List<PayGatewayBank> oldBankList = baseDao.getObjectListByField(PayGatewayBank.class, "gatewayId", gatewayId);
		baseDao.removeObjectList(oldBankList);
	}
	
	@Override
	public boolean isSwitch(String gatewayCode){
		if(StringUtils.isBlank(gatewayCode)) return false;
		
		PayInterfaceSwitch interfaceSwitch = baseDao.getObject(PayInterfaceSwitch.class, gatewayCode);
		if(interfaceSwitch != null){
			return true;
		}
		return false;
	}
	
	@Override
	public ErrorCode<PayMerchant> findMerchant(String cityCode,String gatewayCode){
		return findMerchant(cityCode, gatewayCode, null);
	}
	
	@Override
	public ErrorCode<PayMerchant> findMerchant(String cityCode,String gatewayCode,String merchantCode){
		PayGateway payGateway = baseDao.getObjectByUkey(PayGateway.class, "gatewayCode", gatewayCode);
		if(payGateway == null){
			ErrorCode.getFailure("֧�����ز�����");
		}
		ErrorCode<PayMerchant> code = null;
		if(StringUtils.equals(payGateway.getRouteStatus(), PayGateway.ROUTE_TYPE_CITY)){
			code = findCityMerchant(cityCode, payGateway.getId());
		}else if(StringUtils.equals(payGateway.getRouteStatus(), PayGateway.ROUTE_TYPE_MERCODE)){
			code = findMerchantByMerCode(merchantCode);
		}else{
			code = findDefaultMerchant(payGateway.getId());
			
		}
		return code;
	}
	
	/**
	 * ���ݳ���ָ�����̻���ʶ�������̻�
	 * 
	 * @param cityCode  ���� ����
	 * @param gatewayId ֧������id
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Oct 18, 2013 6:23:40 PM
	 */
	private ErrorCode<PayMerchant> findCityMerchant(String cityCode,Long gatewayId){
		List<PayCityMerchant> cmList = baseDao.getObjectListByField(PayCityMerchant.class, "gatewayId", gatewayId);
		Map<String,List<PayCityMerchant>> cmListMap = BeanUtil.groupBeanList(cmList, "areaType");
		
		String merchantCode = null;
		//��������
		List<PayCityMerchant> clist = cmListMap.get(PayCityMerchant.AREATYPE_C);
		if(clist != null && !clist.isEmpty()){
			for(PayCityMerchant cm : clist){
				if(StringUtils.equals(cityCode, cm.getAreaCode())){
					merchantCode = cm.getMerchantCode();
					PayMerchant payMerchant = baseDao.getObjectByUkey(PayMerchant.class, "merchantCode", merchantCode);
					return ErrorCode.getSuccessReturn(payMerchant);
				}
			}
		}
		
		
		//��ʡ
		City city = baseDao.getObject(City.class, cityCode);
		List<PayCityMerchant> plist = cmListMap.get(PayCityMerchant.AREATYPE_P);
		if(plist != null && !plist.isEmpty()){
			for(PayCityMerchant cm : plist){
				if(StringUtils.equals(city.getProvince().getProvincecode(), cm.getAreaCode())){
					merchantCode = cm.getMerchantCode();
					PayMerchant payMerchant = baseDao.getObjectByUkey(PayMerchant.class, "merchantCode", merchantCode);
					return ErrorCode.getSuccessReturn(payMerchant);
				}
			}
		}		
		
		//���ȡĬ��
		return findDefaultMerchant(gatewayId);
		
	}
	
	private ErrorCode<PayMerchant> findMerchantByMerCode(String merchantCode){
		if(StringUtils.isBlank(merchantCode)){
			return ErrorCode.getFailure("֧��ͨ��������");
		}
		PayMerchant merchant = baseDao.getObjectByUkey(PayMerchant.class, "merchantCode", merchantCode);
		if(merchant == null){
			return ErrorCode.getFailure("֧��ͨ��������");
		}
		return ErrorCode.getSuccessReturn(merchant);
	}
	
	/**
	 * ��ȡ�̻���ʶ
	 * �̻��Ȱ�id����С��������
	 * �������Ĭ�ϣ�ȡ��һ������Ĭ�ϵ��̻�������
	 * ���û����Ĭ�ϣ�ֱ��ȡ��һ��������
	 * 
	 * @param gatewayId ֧������id
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Oct 18, 2013 4:29:34 PM
	 */
	private ErrorCode<PayMerchant> findDefaultMerchant(Long gatewayId){		
		List<PayMerchant> merchantList = baseDao.getObjectListByField(PayMerchant.class, "gatewayId", gatewayId);
		if(merchantList == null || merchantList.isEmpty()){
			return ErrorCode.getFailure("֧��ͨ��������");
		}
		Collections.sort(merchantList, new PropertyComparator("id", true, true));
		PayMerchant merchant = null;
		for(PayMerchant t : merchantList){
			if(t.isDefault()){
				merchant = t;
				break;
			}
		}
		if(merchant == null){//û����Ĭ��ȡ��һ��
			merchant = merchantList.get(0);
		}
		return ErrorCode.getSuccessReturn(merchant);
	}
		
	
	@Override
	public ErrorCode<PayMerchant> findDefaultMerchant(String gatewayCode){
		PayGateway payGateway = baseDao.getObjectByUkey(PayGateway.class, "gatewayCode", gatewayCode);
		if(payGateway == null){
			ErrorCode.getFailure("֧�����ز�����");
		}
		return findDefaultMerchant(payGateway.getId());
	}
}
