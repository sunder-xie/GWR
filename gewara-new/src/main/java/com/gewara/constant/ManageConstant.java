package com.gewara.constant;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;

/**
 * �ṩ��̨��س���
 * @since Dec 31, 2011, 4:10:09 PM
 * @author acerge(gebiao)
 * @function 
 */
public class ManageConstant {
	public static final Map<String, String> deptMap;
	public static final Map<String, String> applyMap;
	
	static{
		Map<String, String> tmp = new LinkedHashMap<String, String>();
		tmp.put("0102", "0102-������");
		tmp.put("0103", "0103-����");
		tmp.put("0104", "0104-����������");
		tmp.put("0106", "0106-��Ϣ֧������");
		//tmp.put("03", "03-������");
		tmp.put("0101", "0101-�ܾ���");
		//tmp.put("05", "05-��Ʒ��Ӫ��");
		tmp.put("0202", "0202-������������");
		tmp.put("0203", "0203-������Ʒ��");
		tmp.put("0201", "0201-������");
		tmp.put("0301", "0301-Ʒ��ý����");
		tmp.put("0302", "0302-���������");
		tmp.put("0303", "0303-�������۲�");
		tmp.put("0405", "0405-�ݳ���ҵ��");
		tmp.put("0406", "0406-��Ӱ��ҵ��");
		tmp.put("0407", "0407-�˶���ҵ��");
		//tmp.put("12", "12-��վ��Ӫ����");
		//tmp.put("13", "13-�ֻ���Ӫ��");
		deptMap = UnmodifiableMap.decorate(tmp);
		
		tmp = new LinkedHashMap<String, String>();
		tmp.put("01", "01-���ϻ");
		tmp.put("02", "02-�г�����");
		tmp.put("03", "03-�������");
		tmp.put("04", "04-��˾����");
		tmp.put("05", "05-���»");
		tmp.put("06", "06-Ա������");
		tmp.put("07", "07-ý���û�");
		tmp.put("08", "08-�ͷ��⳥");
		tmp.put("09", "09-������Ʊ");
		tmp.put("99", "99-����");
		tmp.put("11", "11-���۸���");
		tmp.put("12", "12-����Ӫ��");
		tmp.put("13", "13-����Ӫ��");
		tmp.put("14", "14-����");
		tmp.put("15", "15-���ֶһ�");
		tmp.put("16", "16-��Ʊ�");
		tmp.put("17", "17-����Ʊȯ");
		tmp.put("18", "18-����ר��");
		applyMap = UnmodifiableMap.decorate(tmp);
	}
}
