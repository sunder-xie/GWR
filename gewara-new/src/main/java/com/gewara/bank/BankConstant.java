package com.gewara.bank;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;

public class BankConstant {
	private static Map<String, String> alipayBankMap = null;		//֧����PC����
	private static Map<String, String> alipayCreditMap = null;		//֧����PC������ÿ�
	private static Map<String, String> alipayKjCreditMap = null;	//֧����PC������ÿ�
	private static Map<String, String> alipayMotoCreditMap = null;	//֧����PC���֧�����ÿ�ǰ��
	private static List<String> alipayWapCreditList = null;			//֧����WAP���ÿ�
	private static List<String> alipayWapDebitList = null;			//֧����WAP��ǿ�
	private static Map<String, String> pnrBankMap = null;			//�㸶PC����
	static{
		alipayInit();
		alipayWapInit();
		pnrInit();
	}
	public static void alipayWapInit(){
		alipayWapCreditList = new LinkedList<String>();
		alipayWapCreditList.add("��������,aliwapPay:CREDITCARD_CCB");
		alipayWapCreditList.add("��������,aliwapPay:CREDITCARD_ICBC");
		alipayWapCreditList.add("�㷢����,aliwapPay:CREDITCARD_GDB");
		alipayWapCreditList.add("�й�����,aliwapPay:CREDITCARD_BOC");
		alipayWapCreditList.add("��������,aliwapPay:CREDITCARD_HXBANK");
		alipayWapCreditList.add("��������,aliwapPay:CREDITCARD");
		alipayWapDebitList = new LinkedList<String>();
		alipayWapDebitList.add("��������,aliwapPay:DEBITCARD_CCB");
		alipayWapDebitList.add("��ͨ����,aliwapPay:DEBITCARD_COMM");
		alipayWapDebitList.add("ũҵ����,aliwapPay:DEBITCARD_ABC");
		alipayWapDebitList.add("�й�����,aliwapPay:DEBITCARD_BOC");
		alipayWapDebitList.add("��������,aliwapPay:DEBITCARD");
	}
	public static void alipayInit(){
		Map<String, String> tmpMap = new LinkedHashMap<String, String>();
		tmpMap.put("BOCB2C", "�й�����");
		tmpMap.put("ICBCB2C", "�й���������");
		tmpMap.put("CMB", "��������");
		tmpMap.put("CCB", "�й���������");
		tmpMap.put("ABC", "�й�ũҵ����");
		tmpMap.put("SPDB", "�Ϻ��ֶ���չ����");
		tmpMap.put("COMM", "��ͨ����");
		tmpMap.put("SPABANK", "ƽ������");
		tmpMap.put("SHBANK", "�Ϻ�����");
		tmpMap.put("CIB", "��ҵ����");
		tmpMap.put("CMBC", "�й���������");
		tmpMap.put("CITIC", "��������");
		tmpMap.put("CEBBANK", "�������");
		alipayBankMap = UnmodifiableMap.decorate(tmpMap);
	
		//���ÿ�֧������
		tmpMap = new LinkedHashMap<String, String>();
		tmpMap.put("DEicbc301","�й���������");
		//tmpMap.put("DEccb301","�й���������");
		tmpMap.put("DEcmb301","��������");
		tmpMap.put("DEboc301","�й�����");
		tmpMap.put("DEcomm301","��ͨ����");
		tmpMap.put("DEgdb301","�㶫��չ����");
		tmpMap.put("DEcib301","��ҵ����");
		tmpMap.put("DEceb301","�й��������");
		tmpMap.put("DEspabank301","ƽ������");
		tmpMap.put("DEcitic302","��������");
		tmpMap.put("DEspdb301","�ַ�����");
		//tmpMap.put("DEshbank301","�Ϻ�����");
		alipayCreditMap = UnmodifiableMap.decorate(tmpMap);
		
		tmpMap = new LinkedHashMap<String, String>();
		tmpMap.put("KJICBC", "��������");
		tmpMap.put("KJABC", "ũҵ����");
		tmpMap.put("KJCMB", "��������");
		tmpMap.put("KJCCB", "��������");
		tmpMap.put("KJBOC", "�й�����");
		tmpMap.put("KJSDB", "���ڷ�չ����");
		tmpMap.put("KJCEB", "�������");
		//tmpMap.put("KJSPABANK", "ƽ������");
		alipayKjCreditMap = UnmodifiableMap.decorate(tmpMap);
		
		tmpMap = new LinkedHashMap<String, String>();
		tmpMap.put("MOCCB-MOTO-CREDIT", "�й���������");
		tmpMap.put("MOICBC-MOTO-CREDIT", "�й���������");
		tmpMap.put("MOBOC-MOTO-CREDIT", "�й�����");
		tmpMap.put("MOHXBANK-EXPRESS-CREDIT", "��������");
		tmpMap.put("MOABC-EXPRESS-CREDIT", "�й�ũҵ����");
		alipayMotoCreditMap = UnmodifiableMap.decorate(tmpMap);
	}
	public static void pnrInit(){
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put("07","��������");
		tmp.put("10","�й���������");
		tmp.put("12","�й���������");
		tmp.put("25","�й���������");
		tmp.put("45","�й�����");
		tmp.put("50","ƽ������");
		tmp.put("41","��ͨ����");
		tmp.put("09","��ҵ����");
		tmp.put("29","�й�ũҵ����");
		tmp.put("36","�й��������");
		tmp.put("16","�ֶ���չ����");
		tmp.put("33","��������");
		tmp.put("13","��������");
		tmp.put("49","�Ͼ�����");
		tmp.put("53","��������");
		tmp.put("51","��������");
		tmp.put("52","��������");
		tmp.put("14","���ڷ�չ����");
		pnrBankMap = UnmodifiableMap.decorate(tmp);
	}
	public static Map<String, String> getAlipayBankMap() {
		return alipayBankMap;
	}
	public static Map<String, String> getAlipayCreditMap() {
		return alipayCreditMap;
	}
	public static Map<String, String> getAlipayKjCreditMap() {
		return alipayKjCreditMap;
	}
	public static Map<String, String> getAlipayMotoCreditMap() {
		return alipayMotoCreditMap;
	}
	public static Map<String, String> getPnrBankMap() {
		return pnrBankMap;
	}
}
