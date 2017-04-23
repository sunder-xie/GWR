package com.gewara.helper.discount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.PayConstant;
import com.gewara.constant.order.ElecCardConstant;
import com.gewara.model.pay.ElecCard;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.pay.PayValidHelper;
import com.gewara.support.ErrorCode;
import com.gewara.util.BeanUtil;
import com.gewara.util.DateUtil;
import com.gewara.util.VmUtils;

/**
 * ����ȯͨ�ù���
 * @author acerge(acerge@163.com)
 * @since 7:21:43 PM Jul 10, 2011
 */
public class ElecCardHelper {

	/**
	 * ��Գ��ι���
	 * @param card
	 * @param opi
	 * @return
	 */
	public static ErrorCode getDisableReason(ElecCard card, OpenPlayItem opi) {
		//1���жϿ��Ƿ���Ч
		if(StringUtils.equals(card.getStatus(), ElecCardConstant.STATUS_USED)) return ErrorCode.getFailure("��ȯ�ѱ�ʹ�ã�");
		if(!card.available()) return ErrorCode.getFailure("��ȯ�ѹ��ڣ�");
		if(!card.validTag(PayConstant.APPLY_TAG_MOVIE)) return ErrorCode.getFailure("�˿������ڵ�Ӱ���ʹ��");
		Long batchid = card.getEbatch().getId();
		boolean isSupportCard = new PayValidHelper(VmUtils.readJsonToMap(opi.getOtherinfo())).supportCard(batchid);
		if(!isSupportCard) return ErrorCode.getFailure("��ȯ��֧���ڸó���ʹ�ã�");
		
		if(!StringUtils.contains(opi.getElecard(), card.getCardtype())){
			return ErrorCode.getFailure("�����β�֧��ʹ�ô˶һ�ȯ");
		}
		if(StringUtils.isNotBlank(card.getWeektype())){
			String week = ""+DateUtil.getWeek(opi.getPlaytime());
			if(card.getWeektype().indexOf(week) < 0){ 
				return ErrorCode.getFailure("��ȯ����Ϊֻ������" + card.getWeektype() + "ʹ�ã�");
			}
		}
		if(StringUtils.isNotBlank(card.getValidcinema())){
			List<Long> cidList = BeanUtil.getIdList(card.getValidcinema(), ",");
			if(!cidList.contains(opi.getCinemaid())){
				return ErrorCode.getFailure("��ȯ��֧���ڴ�ӰԺʹ�ã�");
			}
		}
		if(!card.isUseCurTime()){//ʱ�������
			String opentime = card.getEbatch().getAddtime1();
			String closetime = card.getEbatch().getAddtime2();
			return ErrorCode.getFailure("��ȯ����Ϊֻ����" + opentime + "��" +  closetime + "ʱ����ʹ�ã�");
		}
		//���Ƴ���ʱ���
		if(StringUtils.isNotBlank(card.getEbatch().getOpentime()) && StringUtils.isNotBlank(card.getEbatch().getClosetime())){
			String playtime = DateUtil.format(opi.getPlaytime(), "HHmm");
			String opentime = card.getEbatch().getOpentime();
			String closetime = card.getEbatch().getClosetime();
			if(playtime.compareTo(opentime)<0 || playtime.compareTo(closetime)>0)
				return ErrorCode.getFailure("��ȯ���Ƴ���ֻ����" + opentime + "��" + closetime + "ʱ����ʹ�ã�");
		}
		if(!card.isCanUseCity(opi.getCitycode())){
			return ErrorCode.getFailure("��ȯ��֧���ڸó���ʹ�ã�");
		}
		if(StringUtils.isNotBlank(card.getValidmovie())){
			List<Long> cidList = BeanUtil.getIdList(card.getValidmovie(), ",");
			if(!cidList.contains(opi.getMovieid())){
				return ErrorCode.getFailure("ӰƬ����ʹ�ô˶һ�ȯ��");
			}
		}
		if(StringUtils.isNotBlank(card.getValiditem())){
			List<Long> cidList = BeanUtil.getIdList(card.getValiditem(), ",");
			if(!cidList.contains(opi.getMpid())){
				return ErrorCode.getFailure("�����β���ʹ�ô˶һ�ȯ��");
			}
		}
		if(!isEditionMatch(card.getCardtype(), card.getEbatch().getEdition(), opi.getEdition())){
			if(StringUtils.equals(card.getEbatch().getEdition(), ElecCardConstant.EDITION_ALL)){
				return ErrorCode.getFailure("��ȯ����Ϊֻ�ܶһ�2D��3D�汾��ӰƬ��");
			}
			return ErrorCode.getFailure("��ȯ����Ϊֻ�ܶһ�" + card.getEbatch().getEdition() + "�汾��ӰƬ��");
		}
		return ErrorCode.SUCCESS;
	}
	public static boolean isEditionMatch(String cardtype, String cardEdition, String opiEdition) {
		if(StringUtils.contains("BCD", cardtype)) return true;//BCD�����ް汾
		if(StringUtils.contains(opiEdition, "IMAX") || StringUtils.contains(opiEdition, "��Ļ")){
			return StringUtils.contains(cardEdition, "IMAX");
		}else if(StringUtils.contains(opiEdition, "4D")){
			return StringUtils.contains(cardEdition, "4D");
		}
		if(StringUtils.isBlank(cardEdition) || StringUtils.equals(cardEdition, ElecCardConstant.EDITION_ALL)) return true;
		if(opiEdition.contains(ElecCardConstant.EDITION_3D)) return StringUtils.equals(ElecCardConstant.EDITION_3D, cardEdition);
		return !StringUtils.equals(ElecCardConstant.EDITION_3D, cardEdition);
	}
	
	public static String getSupportCard(OpenPlayItem opi){
		if(StringUtils.isBlank(opi.getElecard()) || StringUtils.equals(opi.getElecard(), "M")){
			return "";
		}
		List<String> result = new ArrayList<String>(3);
		Map<String, String> map = ElecCardConstant.getNormalMap();
		
		if(StringUtils.contains(opi.getEdition(), "IMAX")){
			map = ElecCardConstant.getImaxMap();
		}
		for(char c: opi.getElecard().toCharArray()){
			if(map.containsKey("" + c)){
				result.add(map.get("" + c));
			}
		}
		return StringUtils.join(result, "��");
	}
	public static boolean supportAllCard(OpenPlayItem opi){
		return StringUtils.isNotBlank(opi.getElecard()) && opi.getElecard().contains("A") 
				&& opi.getElecard().contains("B") && opi.getElecard().contains("D");
	}
}
