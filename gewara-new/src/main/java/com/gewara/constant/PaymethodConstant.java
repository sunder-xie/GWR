package com.gewara.constant;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.lang.StringUtils;

public abstract class PaymethodConstant implements Serializable {
	private static final long serialVersionUID = -8289964065497333210L;
	
	//������
	public static final String PAYMETHOD_GEWAPAY = "gewaPay";						//�û����
	public static final String PAYMETHOD_SYSPAY = "sysPay";							//ϵͳ�û�
	public static final String PAYMETHOD_ELECARDPAY = "elecardPay";					//ȫ��ʹ��ȯ
	public static final String PAYMETHOD_GEWARA_OFFLINEPAY = "offlinePay";			//��̨����֧��
	//��ֵ
	public static final String PAYMETHOD_CHARGECARD = "ccardPay";					//��ֵ����ֻ������ֵ
	public static final String PAYMETHOD_LAKALA = "lakalaPay";						//������ ��ֻ������ֵ
	public static final String PAYMETHOD_ABCBANKPAY = "abcPay";						//ũ�к�����ֵ��ֻ������ֵ
	public static final String PAYMETHOD_WCANPAY = "wcanPay";						//΢�ܿƼ�������ֵ��ֻ������ֵ
	//֧��----�������
	public static final String PAYMETHOD_PARTNERPAY = "partnerPay";					//�������
	public static final String PAYMETHOD_OKCARDPAY = "okcardPay";					//����OK��
	public static final String PAYMETHOD_SPSDOPAY1 = "spsdoPay";					//ʢ��ʱ����֧��
	//֧��----������
	public static final String PAYMETHOD_ALIPAY = "directPay";						//֧����PC��
	public static final String PAYMETHOD_PNRPAY = "pnrPay";							//�㸶����PC��
	public static final String PAYMETHOD_CMPAY = "cmPay";							//�ƶ��ֻ�֧��PC��
	public static final String PAYMETHOD_TEMPUSPAY = "tempusPay";					//�ڸ�ͨPC��
	public static final String PAYMETHOD_SPSDOPAY2 = "spsdo2Pay";					//ʢ��ͨPC��
	public static final String PAYMETHOD_CHINAPAY1 = "chinaPay";					//����
	public static final String PAYMETHOD_CHINAPAY2 = "china2Pay";					//ChinapayPC��
	public static final String PAYMETHOD_CHINAPAYSRCB = "srcbPay";					//Chinapayũ����--->50000547
	
	public static final String PAYMETHOD_UNIONPAY = "unionPay";						//unionPay
	public static final String PAYMETHOD_UNIONPAY_JS = "unionPay_js";				//Unionpay����PC��
	public static final String PAYMETHOD_UNIONPAY_ACTIVITY = "unionPay_activity";	//unionPay�
	public static final String PAYMETHOD_UNIONPAY_ACTIVITY_JS = "unionPay_activity_js";//unionPay���ջ
	public static final String PAYMETHOD_UNIONPAY_ZJ = "unionPay_zj";		//�㽭����ר��
	public static final String PAYMETHOD_UNIONPAY_SZ = "unionPay_sz";		//���ڵ���ר��
	public static final String PAYMETHOD_UNIONPAY_BJ = "unionPay_bj";		//��������ר��
	public static final String PAYMETHOD_UNIONPAY_GZ = "unionPay_gz";		//���ݵ���ר��
	
	public static final String PAYMETHOD_UNIONPAYFAST = "unionPayFast";				//unionPay V2.0.0 �汾֧��
	public static final String PAYMETHOD_UNIONPAYFAST_ACTIVITY_JS = "unionPayFast_activity_js";//unionPay version 2.0.0�汾 unionPay���ջ
	public static final String PAYMETHOD_UNIONPAYFAST_ACTIVITY_BJ = "unionPayFast_activity_bj";//unionPay version 2.0.0�汾 unionPay�����
	public static final String PAYMETHOD_UNIONPAYFAST_ACTIVITY_SZ = "unionPayFast_activity_sz";//unionPay version 2.0.0�汾���ڵ����
	public static final String PAYMETHOD_UNIONPAYFAST_ACTIVITY_GZ = "unionPayFast_activity_gz";//unionPay version 2.0.0�汾���ݵ����
	public static final String PAYMETHOD_UNIONPAYFAST_ACTIVITY_ZJ = "unionPayFast_activity_zj";//unionPay version 2.0.0�汾�㽭�����
	
	//֧��----ֱ��
	public static final String PAYMETHOD_BCPAY = "bcPay";							//����ֱ��PC��
	public static final String PAYMETHOD_SPDPAY = "spdPay";							//�ַ�ֱ��PC��
	public static final String PAYMETHOD_CMBPAY = "cmbPay";							//����ֱ��PC��
	public static final String PAYMETHOD_GDBPAY = "gdbPay";							//�㷢ֱ��PC��
	public static final String PAYMETHOD_PSBCPAY = "psbcPay";						//�ʴ�ֱ��PC��
	public static final String PAYMETHOD_HZBANKPAY = "hzbankPay";					//��������ֱ��
	public static final String PAYMETHOD_CCBPOSPAY = "ccbposPay";					//����ֱ��PC��-���ÿ�
	public static final String PAYMETHOD_JSBCHINA = "jsbChina";						//��������ֱ��PC��-���ÿ�
	public static final String PAYMETHOD_SPDPAY_ACTIVITY = "spdPay_activity";		//�ַ�ֱ��PC��-�
	public static final String PAYMETHOD_BOCPAY = "bocPay";							//�й�����ֱ��PC��
	public static final String PAYMETHOD_BOCWAPPAY = "bocWapPay";					//�й�����ֱ��WAP��
	public static final String PAYMETHOD_BOCAGRMTPAY = "bocAgrmtPay";				//�й�����Э��֧��
	//֧��----����
	public static final String PAYMETHOD_UMPAY = "umPay";							//�ƶ�����֧��(��������)
	public static final String PAYMETHOD_UMPAY_SH = "umPay_sh";						//�ƶ�����֧��(��������) �Ϻ�����
	public static final String PAYMETHOD_TELECOM= "telecomPay";						//���Ź̻�����֧����������ֵ
	public static final String PAYMETHOD_MOBILE_TELECOM= "telecomMobilePay";		//�����ֻ�����֧��
	//֧��----��ҵ��
	public static final String PAYMETHOD_YAGAO = "yagaoPay";						//�Ÿ�
	public static final String PAYMETHOD_ONETOWN = "onetownPay";					//һ�ǿ�֧��(�»���ý)
	//֧��----�ֻ��ˣ�ֱ�� + ��������
	public static final String PAYMETHOD_ALIWAPPAY = "aliwapPay";					//֧�����ֻ���-WAP֧��
	public static final String PAYMETHOD_CMWAPPAY = "cmwapPay";						//�ƶ��ֻ�֧���ֻ���-WAP֧��
	public static final String PAYMETHOD_CMBWAPPAY = "cmbwapPay";					//����ֱ���ֻ���
	public static final String PAYMETHOD_CMBWAPSTOREPAY = "cmbwapStorePay";			//����ֱ���ֻ���CMSTORE
	public static final String PAYMETHOD_SPDWAPPAY = "spdWapPay";					//�ַ�ֱ���ֻ���-WAP
	public static final String PAYMETHOD_CMSMARTPAY = "cmSmartPay";					//�ƶ��ֻ�֧����׿��
	public static final String PAYMETHOD_SPDWAPPAY_ACTIVITY = "spdWapPay_activity";	//�ַ�ֱ���ֻ���-�
	public static final String PAYMETHOD_CHINASMARTMOBILEPAY = "chinaSmartMobilePay";//���������ֻ�֧��
	public static final String PAYMETHOD_CHINASMARTJSPAY = "chinaSmartJsPay";		//���������ֻ�֧��-����
	public static final String PAYMETHOD_ALISMARTMOBILEPAY = "aliSmartMobilePay";	//֧�����ֻ���-��ȫ֧��
	public static final String PAYMETHOD_HZWAPPAY = "hzwapPay";						//��������WAP
	public static final String PAYMETHOD_YEEPAY = "yeePay";       					//�ױ�֧��
	public static final String PAYMETHOD_PAYECO_DNA = "payecoDNAPay";       		// ����DNA֧��
	public static final String PAYMETHOD_MEMBERCARDPAY = "memberCardPay";       	// ��Ա��֧��
	public static final String PAYMETHOD_ICBCPAY = "icbcPay";       // ��������ֱ��֧��PC��
	public static final String PAYMETHOD_NJCBPAY = "njcbPay";       // �Ͼ�����ֱ��֧��PC��
	public static final String PAYMETHOD_ABCHINAPAY = "abchinaPay";       // ũҵ����ֱ��֧��PC��
	
	public static final String PAYMETHOD_WXAPPTENPAY = "wxAppTenPay";       //�Ƹ�ͨ΢��֧����App��֧����
	public static final String PAYMETHOD_WXSCANTENPAY = "wxScanTenPay";       //�Ƹ�ͨ΢��֧����WEBɨ�룩
	public static final String PAYMETHOD_WXWCPAY = "wxWCPay";				//΢�Ź��ں�֧��
	public static final String PAYMETHOD_CCBWAPPAY = "ccbWapPay";	//�����ֻ�wap֧��
	public static final String PAYMETHOD_ONECLICKTENPAY = "oneClickTenPay";	//�Ƹ�ͨ�ƶ��ն�һ��֧��
	public static final String PAYMETHOD_BESTPAY = "bestPay";	//��֧��
	public static final String PAYMETHOD_BFBWAPPAY = "bfbWapPay";	//�ٶ�Ǯ��wap֧��
	public static final String PAYMETHOD_BFBPAY = "bfbPay";		//�ٶ�Ǯ��֧��
	
	//֧��----����֧����ʽ����ʹ��
	public static final String PAYMETHOD_SDOPAY = "sdoPay";							//ʢ�����+�ֽ�
	public static final String PAYMETHOD_TENPAY = "tenPay";							//�Ƹ�ͨ
	public static final String PAYMETHOD_IPSPAY= "ipsPay";							//��ѶPC��-���ÿ�֧��
	public static final String PAYMETHOD_BCWAPPAY = "bcwapPay";						//��ͨWAP����
	public static final String PAYMETHOD_ALLINPAY = "allinPay";						//ͨ��֧��
	public static final String PAYMETHOD_ALIBANKPAY = "alibankPay";					//֧�����ֻ�����
	public static final String PAYMETHOD_HANDWAPPAY = "handwapPay";					//����ֻ�
	public static final String PAYMETHOD_HANDWEBPAY = "handwebPay";					//����ֻ�
	public static final String PAYMETHOD_PNRFASTPAY = "pnrfastPay";					//�㸶���֧�� --������������
	public static final String PAYMETHOD_PNRFASTPAY2 = "pnrfastPay2";				//�㸶���֧��2--������������
	public static final String PAYMETHOD_PNRFASTABCPAY = "pnrfastabcPay";			//�㸶���֧��--ũҵ�������ÿ�
	
	//�ѷ���
	//public static final String PAYMETHOD_HAOBAIPAY = "haobaiPay";					//�Ű��ֻ���-�ͻ���
	
	public static final List<String> PAYMETHOD_LIST = 
			Arrays.asList(/*����:PAYMETHOD_HAOBAIPAY,*/
					PAYMETHOD_GEWAPAY, PAYMETHOD_CHARGECARD, PAYMETHOD_PNRPAY, 
					PAYMETHOD_ALIPAY, PAYMETHOD_ALIWAPPAY, PAYMETHOD_ALIBANKPAY, PAYMETHOD_LAKALA, 
					PAYMETHOD_SDOPAY, PAYMETHOD_CHINAPAY1, PAYMETHOD_CHINAPAY2, PAYMETHOD_CHINAPAYSRCB, 
					PAYMETHOD_OKCARDPAY, PAYMETHOD_TENPAY, PAYMETHOD_PARTNERPAY, PAYMETHOD_SPSDOPAY1, PAYMETHOD_SPSDOPAY2, 
					PAYMETHOD_CMPAY, PAYMETHOD_IPSPAY, PAYMETHOD_YAGAO, PAYMETHOD_ONETOWN, PAYMETHOD_HANDWEBPAY, 
					PAYMETHOD_HANDWAPPAY, PAYMETHOD_CMBPAY, PAYMETHOD_CMBWAPPAY, PAYMETHOD_BCPAY, PAYMETHOD_BCWAPPAY, 
					PAYMETHOD_GDBPAY, PAYMETHOD_ALLINPAY, PAYMETHOD_ELECARDPAY, PAYMETHOD_SYSPAY, PAYMETHOD_CMWAPPAY, 
					PAYMETHOD_CHINASMARTMOBILEPAY, PAYMETHOD_CHINASMARTJSPAY, PAYMETHOD_ALISMARTMOBILEPAY, PAYMETHOD_UMPAY,PAYMETHOD_UMPAY_SH, 
					PAYMETHOD_SPDPAY, PAYMETHOD_SPDPAY_ACTIVITY,PAYMETHOD_PSBCPAY, PAYMETHOD_SPDWAPPAY, PAYMETHOD_SPDWAPPAY_ACTIVITY, PAYMETHOD_HZBANKPAY, PAYMETHOD_ABCBANKPAY, 
					PAYMETHOD_WCANPAY,PAYMETHOD_UNIONPAY, PAYMETHOD_UNIONPAY_JS, PAYMETHOD_UNIONPAY_ACTIVITY,PAYMETHOD_UNIONPAY_ACTIVITY_JS,PAYMETHOD_UNIONPAY_ZJ,PAYMETHOD_UNIONPAY_SZ,PAYMETHOD_UNIONPAY_BJ,PAYMETHOD_UNIONPAY_GZ,
					PAYMETHOD_TELECOM,PAYMETHOD_MOBILE_TELECOM,
					PAYMETHOD_JSBCHINA, PAYMETHOD_TEMPUSPAY, PAYMETHOD_CCBPOSPAY,PAYMETHOD_PNRFASTPAY,PAYMETHOD_PNRFASTPAY2,PAYMETHOD_YEEPAY,
					PAYMETHOD_CMSMARTPAY, PAYMETHOD_PNRFASTABCPAY,PAYMETHOD_UNIONPAYFAST,PAYMETHOD_UNIONPAYFAST_ACTIVITY_JS,PAYMETHOD_UNIONPAYFAST_ACTIVITY_BJ,
					PAYMETHOD_BOCPAY,PAYMETHOD_BOCWAPPAY,PAYMETHOD_BOCAGRMTPAY, PAYMETHOD_HZWAPPAY, PAYMETHOD_PAYECO_DNA, PAYMETHOD_MEMBERCARDPAY, PAYMETHOD_UNIONPAYFAST_ACTIVITY_SZ,
					PAYMETHOD_UNIONPAYFAST_ACTIVITY_GZ,PAYMETHOD_UNIONPAYFAST_ACTIVITY_ZJ,PAYMETHOD_ICBCPAY, PAYMETHOD_CMBWAPSTOREPAY,PAYMETHOD_NJCBPAY,PAYMETHOD_ABCHINAPAY,PAYMETHOD_WXAPPTENPAY,PAYMETHOD_WXSCANTENPAY,
					PAYMETHOD_CCBWAPPAY, PAYMETHOD_WXWCPAY, PAYMETHOD_ONECLICKTENPAY, PAYMETHOD_BESTPAY, PAYMETHOD_BFBWAPPAY, PAYMETHOD_GEWARA_OFFLINEPAY, PAYMETHOD_BFBPAY);
		
	public static final List<String> MOBILE_PAYMETHOD_LIST = Arrays.asList(PAYMETHOD_ALIWAPPAY, PAYMETHOD_CMWAPPAY, PAYMETHOD_CMBWAPPAY, PAYMETHOD_CMBWAPSTOREPAY, PAYMETHOD_SPDWAPPAY, 
			PAYMETHOD_CMSMARTPAY, PAYMETHOD_SPDWAPPAY_ACTIVITY, PAYMETHOD_CHINASMARTMOBILEPAY, PAYMETHOD_CHINASMARTJSPAY, PAYMETHOD_BOCWAPPAY, 
			PAYMETHOD_ALISMARTMOBILEPAY, PAYMETHOD_HZWAPPAY ,PAYMETHOD_WXAPPTENPAY,PAYMETHOD_WXSCANTENPAY, PAYMETHOD_CCBWAPPAY, PAYMETHOD_WXWCPAY, PAYMETHOD_ONECLICKTENPAY, PAYMETHOD_BFBWAPPAY);
	private static Map<String, String> payTextMap;	
	static{
		Map<String, String> tmp = new LinkedHashMap<String, String>();
		tmp.put(PAYMETHOD_SYSPAY, "ϵͳ");
		tmp.put(PAYMETHOD_GEWAPAY, "�������");
		tmp.put(PAYMETHOD_ELECARDPAY, "����ȯ");
		tmp.put(PAYMETHOD_GEWARA_OFFLINEPAY, "��̨����֧��");
		tmp.put(PAYMETHOD_CHARGECARD, "���߳�ֵ��");
		tmp.put(PAYMETHOD_LAKALA, "������");
		tmp.put(PAYMETHOD_ABCBANKPAY,"ũ�к���");
		tmp.put(PAYMETHOD_WCANPAY,"΢�ܿƼ����ֶһ�");
		
		tmp.put(PAYMETHOD_PNRPAY, "�㸶����PC��");
		tmp.put(PAYMETHOD_ALIPAY, "֧����PC��");
		tmp.put(PAYMETHOD_CMPAY, "�ƶ��ֻ�֧��PC��");
		tmp.put(PAYMETHOD_CHINAPAY1, "��������");
		tmp.put(PAYMETHOD_CHINAPAY2, "ChinapayPC��");
		tmp.put(PAYMETHOD_SPSDOPAY2, "ʢ��ͨPC��");
		tmp.put(PAYMETHOD_PAYECO_DNA, "����DNA֧��");
		
		tmp.put(PAYMETHOD_CMBPAY, "����ֱ��PC��");
		tmp.put(PAYMETHOD_CMBWAPPAY, "����ֱ���ֻ���");
		tmp.put(PAYMETHOD_CMBWAPSTOREPAY, "�����ֻ���-STORE");
		tmp.put(PAYMETHOD_BCPAY, "����ֱ��PC��");
		tmp.put(PAYMETHOD_GDBPAY, "�㷢ֱ��PC��");
		tmp.put(PAYMETHOD_BOCPAY, "�й�����ֱ��PC��");
		tmp.put(PAYMETHOD_BOCAGRMTPAY, "�й�����Э��֧��");
		tmp.put(PAYMETHOD_SPDPAY, "�ַ�ֱ��PC��");
		tmp.put(PAYMETHOD_SPDPAY_ACTIVITY, "�ַ�ֱ��PC��-�");
		tmp.put(PAYMETHOD_SPDWAPPAY_ACTIVITY, "�ַ�ֱ���ֻ���-�");
		tmp.put(PAYMETHOD_PSBCPAY, "�ʴ�ֱ��PC��");
		tmp.put(PAYMETHOD_HZBANKPAY, "��������");
		tmp.put(PAYMETHOD_HZWAPPAY, "��������WAP");
		tmp.put(PAYMETHOD_JSBCHINA, "��������ֱ��PC��-���ÿ�");
		tmp.put(PAYMETHOD_TEMPUSPAY, "�ڸ�ͨPC��");
		tmp.put(PAYMETHOD_YEEPAY, "�ױ�֧��PC��");
		tmp.put(PAYMETHOD_CCBPOSPAY, "����ֱ��PC��-���ÿ�");
		tmp.put(PAYMETHOD_ICBCPAY, "��������ֱ��֧��PC��");	
		tmp.put(PAYMETHOD_NJCBPAY, "�Ͼ�����ֱ��֧��PC��");
		tmp.put(PAYMETHOD_ABCHINAPAY, "ũҵ����ֱ��֧��PC��");	
		
		tmp.put(PAYMETHOD_ALIWAPPAY, "֧�����ֻ���-WAP֧��");
		tmp.put(PAYMETHOD_BOCWAPPAY, "�й�����ֱ��WAP��");
		tmp.put(PAYMETHOD_CMWAPPAY, "�ƶ��ֻ�֧���ֻ���-WAP֧��");
		tmp.put(PAYMETHOD_SPDWAPPAY, "�ַ�ֱ���ֻ���-WAP");
		tmp.put(PAYMETHOD_CHINASMARTMOBILEPAY, "�����ֻ�����֧��");
		tmp.put(PAYMETHOD_CHINASMARTJSPAY, "���������ֻ���-���������յ�");
		tmp.put(PAYMETHOD_ALISMARTMOBILEPAY, "֧�����ֻ���-��ȫ֧��");
		tmp.put(PAYMETHOD_CMSMARTPAY, "�ƶ��ֻ�֧����׿��");
		
		tmp.put(PAYMETHOD_UNIONPAY, "unionPay����֧��");
		tmp.put(PAYMETHOD_UNIONPAY_JS, "unionPay����");
		tmp.put(PAYMETHOD_UNIONPAY_ACTIVITY, "unionPay�");
		tmp.put(PAYMETHOD_UNIONPAY_ACTIVITY_JS, "unionPay���ջ");
		tmp.put(PAYMETHOD_UNIONPAY_ZJ, "unionPay�㽭");
		tmp.put(PAYMETHOD_UNIONPAY_SZ, "unionPay����");
		tmp.put(PAYMETHOD_UNIONPAY_BJ, "unionPay����");
		tmp.put(PAYMETHOD_UNIONPAY_GZ, "unionPay����");
		
		tmp.put(PAYMETHOD_UNIONPAYFAST, "unionPayFast�������֧��");
		tmp.put(PAYMETHOD_UNIONPAYFAST_ACTIVITY_JS, "unionPayFast���ջ");	
		tmp.put(PAYMETHOD_UNIONPAYFAST_ACTIVITY_BJ, "������֤2.0�����");
		tmp.put(PAYMETHOD_UNIONPAYFAST_ACTIVITY_SZ, "������֤2.0���ڻ");
		tmp.put(PAYMETHOD_UNIONPAYFAST_ACTIVITY_GZ, "������֤2.0���ݻ");
		tmp.put(PAYMETHOD_UNIONPAYFAST_ACTIVITY_ZJ, "������֤2.0�㽭");
		
		
		tmp.put(PAYMETHOD_PNRFASTPAY, "�㸶���֧��--�������ÿ�");
		tmp.put(PAYMETHOD_PNRFASTPAY2, "�㸶���֧��--�������ÿ�");
		tmp.put(PAYMETHOD_PNRFASTABCPAY, "�㸶���֧��--ũ�����ÿ�");
		
		
		tmp.put(PAYMETHOD_TELECOM, "���Ź̻�����֧��");
		tmp.put(PAYMETHOD_MOBILE_TELECOM, "�����ֻ�����֧��");
		tmp.put(PAYMETHOD_UMPAY, "�ƶ�����֧��(��������)");
		tmp.put(PAYMETHOD_UMPAY_SH, "�ƶ�����֧��(��������)_�Ϻ�");
		tmp.put(PAYMETHOD_YAGAO, "�Ÿ߿�֧��(�������)");
		tmp.put(PAYMETHOD_ONETOWN, "һ�ǿ�֧��(�»���ý)");
		
		tmp.put(PAYMETHOD_PARTNERPAY, "������");
		tmp.put(PAYMETHOD_OKCARDPAY, "����OK");
		tmp.put(PAYMETHOD_SPSDOPAY1, "ʢ�����");
		tmp.put(PAYMETHOD_CHINAPAYSRCB, "Chinapayũ����");
		tmp.put(PAYMETHOD_MEMBERCARDPAY, "��Ա��֧��");
		
		tmp.put(PAYMETHOD_WXAPPTENPAY, "�Ƹ�ͨ΢��֧����App��֧����");
		tmp.put(PAYMETHOD_WXSCANTENPAY, "�Ƹ�ͨ΢��֧����WEBɨ�룩");
		tmp.put(PAYMETHOD_WXWCPAY, "΢�Ź��ں�֧��");
		tmp.put(PAYMETHOD_CCBWAPPAY, "�����ֻ�wap֧��");
		tmp.put(PAYMETHOD_ONECLICKTENPAY, "�Ƹ�ͨ�ƶ��ն�һ��֧��");
		tmp.put(PAYMETHOD_BESTPAY, "��֧��");
		tmp.put(PAYMETHOD_BFBWAPPAY, "�ٶ�Ǯ��wap֧��");
		tmp.put(PAYMETHOD_BFBPAY, "�ٶ�Ǯ��֧��");
		
		//tmp.put(PAYMETHOD_HAOBAIPAY, "�Ű��ֻ���-�ͻ���");
		//����ʹ�õ�֧����ʽ
		tmp.put(PAYMETHOD_ALIBANKPAY, "֧������������WAP");
		tmp.put(PAYMETHOD_SDOPAY, "ʢ�����");
		tmp.put(PAYMETHOD_BCWAPPAY, "��ͨWAP");
		tmp.put(PAYMETHOD_ALLINPAY, "ͨ��");
		tmp.put(PAYMETHOD_TENPAY, "�Ƹ�ͨ");
		tmp.put(PAYMETHOD_HANDWEBPAY, "����WEB");
		tmp.put(PAYMETHOD_HANDWAPPAY, "����WAP");
		tmp.put(PAYMETHOD_IPSPAY, "��ѶPC��-���ÿ�֧��");
		
		
		payTextMap = UnmodifiableMap.decorate(tmp);
	}
	
	public static String getPaymethodText(String paymethod){
		if(payTextMap.get(paymethod)!=null) return payTextMap.get(paymethod);
		if(StringUtils.equals("card", paymethod)) return "�һ�ȯ";
		return "δ֪";
	}
	public static final boolean isValidPayMethod(String paymethod){
		return StringUtils.isNotBlank(paymethod) && PaymethodConstant.PAYMETHOD_LIST.contains(paymethod);
	}
	public static Map<String, String> getPayTextMap(){
		return payTextMap;
	}
	
	public static List<String> getMobilePayList(){
		String[] pays = new String[]{PAYMETHOD_ALIWAPPAY,PAYMETHOD_CMWAPPAY,PAYMETHOD_CMBWAPPAY,PAYMETHOD_SPDWAPPAY,
				PAYMETHOD_SPDWAPPAY_ACTIVITY,PAYMETHOD_CHINASMARTMOBILEPAY,PAYMETHOD_CHINASMARTMOBILEPAY,
				PAYMETHOD_CHINASMARTJSPAY,PAYMETHOD_ALISMARTMOBILEPAY};
		return Arrays.asList(pays);
	}
}
