package com.gewara.constant;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.lang.StringUtils;

import com.gewara.model.goods.ActivityGoods;
import com.gewara.model.goods.BaseGoods;
import com.gewara.support.ErrorCode;

public abstract class GoodsConstant {
	public static final String MANAGER_USER = "user";					//����Ա
	public static final String MANAGER_MEMBER = "member";				//��ͨ�û�
	public static final String MANAGER_ORGANIZATION = "organization";	//��֯
	public static final List<String> MANAGER_LIST = Arrays.asList(MANAGER_MEMBER, MANAGER_USER, MANAGER_ORGANIZATION);
	
	public static final String GOODS_TYPE_GOODS = "goods";		
	public static final String GOODS_TYPE_ACTIVITY = "activity";					//��շ�
	public static final String GOODS_TYPE_SPORT = "sport";							//�˶�������ѵ
	public static final String GOODS_TYPE_TICKET = "ticket";							//ͨƱ
	public static final String GOODS_TYPE_TRAINING = "training";					//�˶���ѵ�γ�
	
	//��շѵ�TAG
	public static final String GOODS_TAG_POINT = "point"; 							//���ֶһ���Ʒ
	public static final String GOODS_TAG_GROUPON = "groupon";						//�Ź�
	public static final String GOODS_TAG_BMH = "bmh";								//ӰԺ������Ʒ�����׻�	
	public static final String GOODS_TAG_BMH_SPORT= "bmh_sport";					//�˶�
	public static final String GOODS_TAG_BMH_THEATRE= "bmh_theatre";				//����
	
	
	public static final String DELIVER_ELEC = "elec"; 			//����ȯ
	public static final String DELIVER_ENTITY= "entity"; 		//ʵ��
	public static final String DELIVER_ADDRESS= "address"; 		//��ַ
	public static final String GOODS_SHOPPING_COUNT = "shoppingcount";	//������Ʒ����
	
	public static final String PERIOD_Y = "Y";		//��ʱ��
	public static final String PERIOD_N = "N";		//��ʱ��
	
	public static final String CHECK_GOODS_PRICE = "price";
	public static final String CHECK_GOODS_DISCOUNT = "discount";
	public static final List<String> CHECK_GOODSLIST = Arrays.asList(CHECK_GOODS_PRICE, CHECK_GOODS_DISCOUNT);
	
	
	public static final String FEETYPE_O = "O"; //��������Ʒ���������շ���ѣ�
	public static final String FEETYPE_G = "G";	//Gewara��Ʒ�������Լ����
	public static final String FEETYPE_P = "P";	//����ƽ̨����������Ӷ��
	public static final String FEETYPE_C = "C";	//Ԥ��(��Ʒ��ȯ��ʽʵ��)
	public static final String FEETYPE_T = "T";	//ָ������ƽ̨
	
	public static final Map<String, String> feetypeMap;
	static{
		Map<String, String> tmp = new LinkedHashMap<String, String>();
		tmp.put(FEETYPE_O, "��������Ʒ");
		tmp.put(FEETYPE_G, "Gewara��Ʒ");
		tmp.put(FEETYPE_P, "����ƽ̨");
		tmp.put(FEETYPE_C, "Ԥ��");
		tmp.put(FEETYPE_T, "ָ������ƽ̨");
		feetypeMap = UnmodifiableMap.decorate(tmp);
	}
	public static ErrorCode<String> getBookingStatusStr(BaseGoods goods){
		if(goods==null){
			return ErrorCode.getFailure("���ݲ����ڣ�");
		}
		if(StringUtils.equals("status", Status.N)){
			return ErrorCode.getFailure("������Ԥ��");
		}
		if(goods.getUnitprice()==null){
			return ErrorCode.getFailure("�۸����");
		}
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		if(cur.after(goods.getTotime())){
			return ErrorCode.getFailure("�ѹ���");
		}else {
			if(cur.before(goods.getFromtime())){
				return ErrorCode.getFailure("δ��ʼ");
			}
		}
		if(goods instanceof ActivityGoods){
			if(goods.getRelatedid()==null){
				return ErrorCode.getFailure("��������");
			}
			if(goods.getLimitnum()>=goods.getQuantity()){
				return ErrorCode.getFailure("������");
			}
		}
		return ErrorCode.SUCCESS;
	}
}
