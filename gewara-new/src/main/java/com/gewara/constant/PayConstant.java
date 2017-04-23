package com.gewara.constant;


public abstract class PayConstant {
	
	//PUSH_CONSTANT
	public static final String PUSH_FLAG_NEW = "new";
	public static final String PUSH_FLAG_PAID = "paid";
	public static final String PUSH_FLAG_SUCCESS = "success";
	public static final String PUSH_FLAG_REFUND = "refund";
	
	public static final String KEY_STEP4 = "step4";					//�̼Һ�����Ʊ�ɹ�ҳ��
	public static final String KEY_IFRAME_URL = "iframeUrl";		//�̼Һ�������Ӧiframe
	public static final String KEY_KEY = "key";						//�����̼�ϵͳ�ӿ��ṩ��key
	public static final String KEY_PARTNERID = "partnerid";			//�����̼�ϵͳ�ӿ��ṩ���˺�
	public static final String KEY_URL = "url";						//�����̼�ϵͳ�ӿ��ṩ��URL
	public static final String KEY_PAYMETHOD = "paymethod";			//�̼Һ������õ�֧����ʽ
	public static final String KEY_ORDERFIXPAY2 = "fixedpay";		//�����Ƿ�̶�֧����ʽ:Partner��Ҳ�����ã��ܶ��̼Ҷ���һ��ʼ֧����ʽ���ǹ̶���
	public static final String KEY_CARDBINDPAY = "cardbindpay";		//����֧����ʽ
	public static final String KEY_BINDGOODS = "bindgoods";			//���������ײͣ�����goodsgift
	public static final String KEY_GOODSGIFT = "goodsgift";			//���ΰ��ײ�
	public static final String KEY_GOODSNUM = "goodsnum";			//
	public static final String KEY_CHANGECOST = "changecost";		//�ɱ��۸ı�
	public static final String KEY_BIND_TRADENO = "bindtradeno";	//�󶨵��ײͶ�����
	public static final String KEY_OPENDISCOUNT = "openDiscount";	//�̼Һ���ҳ�濪���Ż�֧���
	public static final String KEY_USE_SPCODE = "spcode";			//ʹ�õ������ؼۻ


	public static final String KEY_CASH_ACCOUNT = "cash_account";	//�ֽ�+�˻����֧��
	//������
	public static final String CARDTYPE_A = "A";//�ο���ÿ�ε���һ��Ʊ��Ǯ
	public static final String CARDTYPE_B = "B";//����ȯ��ÿ��һ��Ʊ������xxԪ
	public static final String CARDTYPE_C = "C";//��ֵ��ÿ�ε���xxԪ
	public static final String CARDTYPE_D = "D";//��ֵ��ÿ�ε���xxԪ��ÿ��ֻ��1��
	public static final String CARDTYPE_E = "E";//��ֵ����ÿ�ſɳ�ֵxxԪ
	//Discount�е�
	public static final String CARDTYPE_POINT = "P";	//����
	public static final String CARDTYPE_PARTNER = "M";	//�̼Һ���merchant
	public static final String CARDTYPE_DEPOSIT = "T";	//��֤��
	public static final String CARDTYPE_INNER_MOVIE = "IM";	//��Ӱ��Ʊ
	public static final String CARDTYPE_INNER_SPORT = "IS";	//�˶���Ʊ
	
	public static final String DISCOUNT_TAG_ECARD = "ecard";		//�һ�ȯ
	public static final String DISCOUNT_TAG_POINT = "point";		//����֧��
	public static final String DISCOUNT_TAG_PARTNER = "partner"; //�����̼�
	public static final String DISCOUNT_TAG_DEPOSIT = "deposit";	//��֤��
	public static final String DISCOUNT_TAG_INNER = "inner";		//�ڲ��Ż�
	
	//�ۿۿ�ʹ�õİ��
	public static final String APPLY_TAG_MOVIE = "movie";
	public static final String APPLY_TAG_DRAMA = "drama";
	public static final String APPLY_TAG_SPORT = "sport";
	public static final String APPLY_TAG_GYM = "gym";
	public static final String APPLY_TAG_GOODS = "goods";
	//��������ƥ��ģʽ���ų�������
	public static final String MATCH_PATTERN_EXCLUDE = "exclude";
	public static final String MATCH_PATTERN_INCLUDE = "include";
	
	public static final String WAPORG_QIEKE = "qieke";
	public static final String WAPORG_BST = "baishitong";
	public static final String WAPORG_HTC = "htc";
	public static final String WAPORG_BAIDU = "baidu-20120813";
}
