package com.gewara.constant.sys;

import com.gewara.util.StringUtil;

public abstract class MongoData {
	
	public static final Integer Longtype = 18;
	public static final Integer Integertype = 16;
	public static final Integer Objectidtype  = 7;
	public static final Integer Stringtype = 2;
	public static final Integer Nulltype = 10;
	/**
	 *Mongo ����(��NS_��ʶ�ĳ���)
	 **/
	//1���ۺ�����
	public static final String NS_MEMBER_INFO = "memberInfo";	//�û������
	public static final String NS_MEMBERCOUNT = "member.count";					//�û��ۺ�����
	public static final String NS_LASTORDER = "member.last.order";					//�û����һ�ζ�������
	public static final String NS_FIRSTORDER = "member.first.order";					//�û���һ�ζ�������
	//2��ҵ������
	public static final String NS_ACTION_MULTYWSMSG = "websiteMsg.multy"; 		// ���� ��Բ����û�Ⱥ��վ���� Mongo��ʶ
	public static final String NS_RECOMMEND_MEMBER = "recommend.member";	//�Ƽ��û���
	public static final String NS_APPLYBETA_MEMBER = "beta.member";	//�û��ڲ��
	public static final String NS_PROMPT_INFO = "promptinfo";	//��ʾ��Ϣ��
	public static final String NS_INDEX_DATASHEET = "index.datasheet";		//��Ӱ��ҳ����ͳ��
	public static final String NS_EXPLAIN="explain";	//��̨ʹ�ð���
	public static final String NS_PARTNER_CONTACT = "partner.contact";
	public static final String NS_DIARY = "DIARY_BODY";
	public static final String NS_KEFU_REPLYTEMPLATE = "kefu.replytemplate"; 	// �ͷ��ظ�ģ��
	public static final String NS_ACTION_PARTNER  = "partner.action";
	public static final String NS_TICKET_MACHINE_IMAGES = "ticket_machine_images"; // ��̨ͼƬ
	public static final String NS_TICKET_MACHINE_ERROR = "ticket_machine_error";	//һ�������
	public static final String NS_EQUIPMENTSTATUS = "EQUIPMENT_STATUS";			//һ���״̬���
	public static final String NS_JOB_NAMESPACE = "gewa.job";
	public static final String NS_PRIMITIVE = "java.primitive";
	public static final String NS_SIGN = "gewa.sign";
	public static final String NS_TREASURE = "gewa.treasure";
	public static final String NS_MEMBER_MOBILE= "member_139email"; //139����
	public static final String NS_INTEGRAL = "integral";
	public static final String NS_MEMBER_TRAINING_INFO = "member_training_info";		//�û���ѵ��Ϣ
	public static final String NS_CITY_ROOM_CHARACTERISTIC= "city_room_characteristic"; //��������ӵ�е�Ӱ����ɫ
	
	public static final String NS_UNIONPAY_WALLET_MAPPING= "unionpayWalletMapping";  //����Ǯ������ƱƱȯӳ��
	public static final String NS_UNIONPAY_WALLET_URL= "unionpayWalletSPUrl";  //����Ǯ������Ʊר�⶯̬url�䶯
	
	public static final String NS_AUTO_SETTER_SEND_EMAIL= "autoSetterEmail";  //����ȷ���ʼ�
	public static final String NS_AUTO_SETTER_LIMIT = "autoSetterLimit";	//�Զ�������ʱ��������Ϣ

	//3��ר������
	public static final String NS_MAINSUBJECT = "subject.main";					// ר��
	public static final String NS_CHINAPAY_ACTIVITY = "chinapay_activity";		//�����
	public static final String NS_DISNEY_JOIN= "disney_join";
	public static final String NS_DISNEY_MEMBER = "disney_member";
	public static final String NS_ACTIVITY_SINGLES = "actvity_singles";			//�����(mongo��)
	public static final String NS_ACTIVITY_PUBLIC_CINEMA ="public_cinema";		//���˽�ר��(�����Ϊ���ñ�)
	
	public static final String NS_YQ = "invite_code";							//����

	public static final String NS_GBD_WINNER = "gbd_winner";					//�㷢���ÿ������û�
	public static final String NS_ABC_CHARGE = "abc.charge";					//ũҵ���к�����̨��ֵ��¼������Ҫ��
	public static final String NS_WCAN_CHARGE = "wcan.charge";					//���ܻ��ֶһ��߱ҳ�ֵ��¼������Ҫ��
	public static final String NS_GBD_WINNER_DATE = "gbd_winner_date";			//�㷢���ÿ�����
	public static final String NS_GEWA_CUP_NAMESPACE="gewa.cup";
	public static final String NS_ACTION_NAMESPACE = "gf.partner.action";
	public static final String NS_API_WARNCALLBACK = "api.warncallback";		// �̼��޷�������Ʊʱ�ص�
	public static final String NS_PAY_CARDNUMBER = "pay_cardnumber";
	public static final String NS_FLASH_PICTRUE = "flash_pictrue";				//flashͼƬ(mongo��)
	public static final String NS_ACTIVITY_COMMON_PICTRUE = "common_pictrue";	//�û��ϴ�ͼƬ��
	public static final String NS_ACTIVITY_COMMON_MEMBER = "common_member";		//�û���Ϣ��
	public static final String NS_SYSMESSAGEACTION = "sysMessageAction";			//վ���Ŷ�ʱ����
	public static final String NS_WINNING_RECEIPT_INFO = "winningReceiptInfo";	//�û��н�
	public static final String NS_POSTCARD_INFO = "postcardInfo";		//����Ƭ��Ϣ
	public static final String NS_FILMFEST_FIFTEEN = "film_fifteen";	//��15���Ӱ�ڼ��㳡��Ԥ������
	public static final String NS_SINGLEDAY = "single_day";				// ����ڳ���
	public static final String NS_BUYTICKET_RANKING = "buy_ticket_ranking";				// ����ڳ���
	
	public static final String NS_GFBANK_ORDER = "gfbank.order";			//ʹ�ù㷢����֧����ʹ���Żݻ�Ķ���
	public static final String NS_GFBANK_USER = "gfbank.user";				//ǩԼ�㷢�����������û�[ÿ�ܹ㷢���л��������]

	public static final String NS_SUBJECT_COUNT = "activity.count"; 		//��ɫ���˽ڻ����
	public static final String NS_PRICETIER = "price.tier"; 				//�۸����
	public static final String NS_CITYPRICETIER = "cityprice.tier"; 		//�����������
	public static final String NS_VOUCHERCARD_TYPE = "vouchercard_type"; 	//�һ�ȯ����
	public static final String NS_VOUCHERCARD_ISSUERID = "vouchercard_issuerid";//����Ʊȯ����������
	public static final String NS_VOUCHERCARD_CINEMA = "vouchercard_cinema";
	
	public static final String NS_MACHINECONFIG = "machine.machineconfig";	//һ����ػ�ʱ������  add by taiqichao
	public static final String NS_OPI_LOCKNUM = "opilockstat";				//����������λ����ͳ��
	public static final String NS_CCBPOS_ACTIVITY = "ccbPosActivity";
	public static final String NS_CCBPOS_GZ_ACTIVITY = "ccbPosGZActivity"; //���ݽ������л
	public static final String NS_CCBPOS_EVERYDAYTWICE= "ccbEveryDayTwice"; //���ݽ������л
	public static final String NS_CCBPOS_WEEKONE= "ccbWeekOne"; 			//���ݽ������л--һ��һ��
	public static final String NS_CCBPOS_CARDBIN = "ccbPosCardbin";
	public static final String NS_CCBPOS_CARDBIN_2013 = "ccbPosCardbin2013"; //����   �µĻǰ8��4��bin
	public static final String NS_CCBPOS_ORDER = "ccbPosOrder";
	public static final String NS_UNIONPAYFAST_CARDBIN = "unionPayFastCardbin";
	
	public static final String NS_COMMON_WHITEDAY = "subject_whiteday";	//�㽭ʡ��ɫ���˽�ר��
	public static final String NS_COMMON_WHITEDAY_DRAWTIMES = "subject_whiteday_drawTimes";	//�㽭ʡ��ɫ���˽�ר�� �û���ȡ����ѳ齱����
	public static final String NS_COMMON_MOTORSHOW_DRAWTIMES = "subject_motorShow_drawTimes";	//�㽭ʡ��ɫ���˽�ר�� �û���ȡ����ѳ齱����
	
	public static final String NS_COMMON_VOTE = "common_vote";	//ͨ��ͶƱ����
	public static final String NS_WEIXIN = "weixin";			//΢��
	public static final String NS_TIMERIFT = "time_rift";	//ʢ��ʱ���Ѻ���Ȩ��
	public static final String NS_BADEGG = "bad_egg";
	public static final String NS_REGEXP = "reg_exp";
	public static final String NS_DRAWDAYCOUNT = "drawcount"; 

	//��̨�û����뱣��
	public static final String FIELD_REAL_NAME = "realName";				//�н�����
	public static final String FIELD_RECEIPT_ADDRESS = "receiptAddress";	//�ջ���ַ
	public static final String FIELD_TELEPHONE = "telephone";				//��ϵ�绰
	public static final String FIELD_EMAIL = "email";						//��������
	public static final String FIELD_SEX = "sex";							//�Ա�
	

	//public static final String NS_APPCLIENT_DOWNRECORD = "appclientDownRecord";//�ͻ�
	//public static final String NS_PSBCBANK_USER = "psbcbank.user";		//�������������������û�2012-07-05����

	//�û������ۺ�����
	public static final String SYSTEM_ID = "_id";
	public static final String DEFAULT_ID_NAME  = "id";						// �����ֶ� ID_NAME
	public static final String ACTION_MEMBERID = "memberid";
	public static final String ACTION_USERID = "userid";					// �����ֶ�
	public static final String ACTION_MEMBERNAME = "membername";
	public static final String ACTION_BODY = "body";							// �����ֶ�
	public static final String ACTION_ADDTIME = "addtime";						// �����ֶ�
	public static final String ACTION_MODIFYTIME = "modifytime";				// �����ֶ�
	public static final String ACTION_TAG = "tag";								// �����ֶ�
	public static final String ACTION_RELATEDID = "relatedid";					// ����ID
	public static final String ACTION_PARENTID = "parentid";					// ����ID
	public static final String ACTION_ORDERNUM = "ordernum";					// ����
	public static final String ACTION_TITLE = "title";							// ����
	public static final String ACTION_SUBJECT = "subject";						// ����
	public static final String ACTION_TYPE = "type";
	public static final String ACTION_ADDRESS = "address";
	public static final String ACTION_SIGNNAME = "signname";					//�����
	public static final String ACTION_SUPPORT="support";						//֧������
	public static final String ACTION_SUBJECT_TYPE = "subjecttype";				//ר������
	public static final String ACTION_CONTENT = "content";						//����
	public static final String ACTION_CONTENT2 = "content2";					//����2
	public static final String ACTION_PROVINCE="province";
	public static final String ACTION_PROVINCE_NAME="provincename";
	public static final String ACTION_CITYCODE="citycode";
	public static final String ACTION_COUNTYCODE="countycode";
	public static final String ACTION_MULTYWSMSG_MSGID = "msgid";
	public static final String ACTION_MULTYWSMSG_ISREAD = "isread";
	public static final String ACTION_MULTYWSMSG_ISDEL = "isdel";
	public static final String ACTION_PICTRUE_URL = "picurl";					//ͼƬ·��
	public static final String ACTION_COUNT="count";
	public static final String ACTION_TO_NAME = "toname";						//�ռ���
	public static final String ACTION_FROM_NAME = "fromname";					//�ļ���
	public static final String ACTION_FROM_ADDRESS = "fromaddress";				//�ļ��˵�ַ
	public static final String ACTION_TO_POSTCODE = "topostcode";				//�ռ����ʱ�
	public static final String ACTION_FROM_POSTCODE = "frompostcode";			//�ļ����ʱ�
	public static final String ACTION_NAME = "name";
	public static final String ACTION_ENDTIME="endtime";
	public static final String ACTION_STARTTIME="starttime";
	public static final String ACTION_CHECK_TIME="checktime"; //�������
	
	public static final String ACTION_ATTACH_MOVIE_ID = "attachmovieid";//Ӱ����id
	public static final String ACTION_ATTACH_MOVIE_MOVIEID = "movieid";//��Ӱ��ID
	public static final String ACTION_LUCK = "luck";//�����û�
	public static final String ACTION_STATUS = "status";//״̬
	public static final String ACTION_REMARK = "remark";
	public static final String ACTION_TRADENO = "tradeNo";
	public static final String ACTION_ATTACH_MOVIE_DESCRIPTION = "description";//����
	public static final String ACTION_ATTACH_MOVIE_POINTVALUE = "pointvalue";//�ӻ���ֵ
	public static final String ACTION_LOTTERY_CODE="lotterycode";//�齱��
	public static final String ACTION_NOTREADCOUNT = "notreadcount";
	public static final String ACTION_OTHERINFO = "otherinfo";
	
	public static final String ACTION_HOME_TAG = "home";
	public static final String ACTION_IDNAME = "acrId";
	public static final String ACTION_VALUENAME = "resultStr";
	
	public static final String ACTION_TAG_SHOWPAY ="showPay";
	public static final String ACTION_ACTIONID = "actionid";
	public static final String ACTION_UKEY = "ukey";
	public static final String ACTION_ACTION= "action";
	
	public static final String ACTION_API_PARTNERNAME = "partnername";
	public static final String ACTION_API_PARTNERKEY = "partnerkey";
	
	public static final String JOB_IDNAME = "jobid";
	public static final String JOB_TITLE = "title";
	public static final String JOB_DESC = "desc";
	
	public static final String SIGNNAME_DAOHANG = "daohang";
	public static final String SIGNNAME_XINZHUAN = "xinzhuan";
	public static final String SIGNNAME_XINWEN = "xinwen";
	public static final String SIGNNAME_XINTU = "xintu";
	public static final String SIGNNAME_TUPIAN = "tupian";
	public static final String SIGNNAME_SHIPIN = "shipin";
	public static final String SIGNNAME_HUDONG = "hudong";
	public static final String SIGNNAME_LUNTAN = "luntan";
	public static final String SIGNNAME_ZHIDAO = "zhidao";
	public static final String SIGNNAME_YINGPIAN = "yingpian";
	public static final String SIGNNAME_HUAJU = "huaju";
	public static final String SIGNNAME_HUODONG = "huodong";
	
	// ͳһר�� signname
	public static final String SIGNNAME_XINWEN_01 = "xinwen_01";
	public static final String SIGNNAME_XINWEN_02 = "xinwen_02";
	public static final String SIGNNAME_XINWEN_03 = "xinwen_03";
	public static final String SIGNNAME_XINWEN_04 = "xinwen_04";
	
	public static final String ACTION_TYPE_SUBJECT = "subject";
	public static final String ACTION_TYPE_RECOMMEND = "recommend";
	public static final String ACTION_TYPE_SIMPLETEMPLATE = "simpletemplate"; // ��һӰƬ�̶�ģ��
	public static final String ACTION_TYPE_UNIONTEMPLATE = "uniontemplate";	// ͳһר��ģ��
	public static final String ACTION_TYPE_SUBUNIONTEMPLATE = "subuniontemplate";	// ͳһר��ģ��
	public static final String ACTION_TYPE_VIEWINGGROUPTEMPLATE = "viewinggrouptemplate";//ͳһ��Ӱ��ר��ģ��
	
	
	public static final String ACTION_BOARD = "board";
	public static final String ACTION_BOARDRELATEDID = "boardrelatedid";
	public static final String ACTION_NEWSTITLE = "newstitle";
	public static final String ACTION_NEWSSUBJECT = "newssubject";
	public static final String ACTION_NEWSLOGO = "newslogo";
	public static final String ACTION_NEWSSMALLLOGO = "newssmalllogo";
	public static final String ACTION_NEWSLINK = "newslink";
	public static final String ACTION_NEWSBOARD = "newsboard";
	public static final String ACTION_NAVIGATION = "navigation";
	public static final String ACTION_WALATITLE = "walatitle";
	public static final String ACTION_JSONINFO = "jsoninfo";		// ��json����
	public static final String ACTION_SUBJECTTYPE = "subjecttype";	// ר������: movie / drama etc..
	public static final String ACTION_LINKCOLOR = "linkcolor";
	public static final String ACTION_SEOKEYWORDS ="seokeywords";			//�ؼ���
	public static final String ACTION_SEODESCRIPTION ="seodescription";		//�ؼ�����
	/******��Ӱ���ֶ�*********/
	public static final String ACTION_SP_ACTIVIES = "sp_activies";//�����
	public static final String ACTION_VIEWBGCOLOR = "viewbgcolor";//������ɫ
	public static final String ACTION_VIEWREPORT = "viewReport";//��Ӱ����ACTION_ATTACH_MOVIE_MOVIEID
	public static final String ACTION_VOTEID = "voteid";//ͶƱID
	//��Ӱ�������ֶ�
	public static final String ACTION_STARSEX = "starsex";//�����Ա�
	public static final String ACTION_CONSTELLATION = "constellation";//��������
	public static final String ACTION_BIRTHDAY = "birthday";//����
	public static final String ACTION_BIRTHPLACE = "birthplace";//������
	
	/******ʢ��ʱ���ѷ���Ȩ��ģ��*********/
	public static final String ACTION_SEQ ="seq";
	public static final String ACTION_PRIVILEGED_CODE = "privilegedcode";
	public static final String ACTION_IS_USED = "isused";
	public static final String ACTION_MEMBER_ID = "memberid";
	public static final String ACTION_USED_TIME = "usedtime";
	
	// ר����ģ����
	public static final String L_UNIONSUB_XINWEN1 	= "L_xinwen_01";	// 
	public static final String L_UNIONSUB_XINWEN2 	= "L_xinwen_02";
	public static final String L_UNIONSUB_XINWEN3 	= "L_xinwen_03";
	public static final String L_UNIONSUB_SHIPIN 	= "L_shipin";
	public static final String L_UNIONSUB_SHIPIN2	= "L_shipin_02";
	public static final String L_UNIONSUB_JUZHAO 	= "L_juzhao";
	public static final String L_UNIONSUB_JUZHAO2	= "L_juzhao_02";
	public static final String L_UNIONSUB_HUODONG 	= "L_huodong";
	public static final String L_UNIONSUB_WALA 		= "L_wala";
	public static final String L_UNIONSUB_BIANJI	= "L_bianji";
	public static final String L_UNIONSUB_DAOHANG	= "L_daohang";
	public static final String L_UNIONSUB_CHOUJIANG	= "L_choujiang";
	public static final String L_UNIONSUB_MINGXINPIAN  = "L_mingxinpian";
	public static final String L_UNIONSUB_TOUPIAO = "L_toupiao";
	
	public static final String R_UNIONSUB_XINWEN4 	= "R_xinwen_04";
	public static final String R_UNIONSUB_YINGPIAN = "R_yingpian";
	public static final String R_UNIONSUB_HUAJU 	= "R_huaju";
	public static final String R_UNIONSUB_SHIPIN 	= "R_shipin";
	public static final String R_UNIONSUB_JUZHAO2	= "R_juzhao_02";
	public static final String R_UNIONSUB_HUODONG	= "R_huodong";
	public static final String R_UNIONSUB_ZHIDAO 	= "R_zhidao";
	public static final String R_UNIONSUB_LUNTAN 	= "R_luntan";
	public static final String R_UNIONSUB_WALA 		= "R_wala";
	public static final String R_UNIONSUB_BIANJI	= "R_bianji";
	
	public static final String T_UNIONSUB_JUZHAO = "T_juzhao";
	public static final String T_UNIONSUB_JUZHAO2 = "T_juzhao2";
	public static final String T_UNIONSUB_BIANJI	= "T_bianji";
	
	public static final String B_UNIONSUB_JUZHAO = "B_juzhao";
	public static final String B_UNIONSUB_JUZHAO2 = "B_juzhao2";
	public static final String B_UNIONSUB_BIANJI	= "B_bianji";
	
	//�Ա�ר��ģ��
	public static final String L_COMPARE_MOVIE = "L_movie";
	public static final String R_COMPARE_MOVIE = "R_movie";
	public static final String L_COMPARE_NEWS = "L_news";
	public static final String R_COMPARE_NEWS = "R_news";
	
	public static final String SINGLES_FOREIGNID = "singles_foreignid";//���ID
	public static final String SINGLES_CINEMAURL = "singles_cinemaurl";//����
	public static final String SINGLE_TIMES = "singles_time"; //ʱ��
	
	public static final String DOUBLE_FESTIVAL_THIRTEEN="DF_thirteen";	//˫���ڸ������
	public static final String DOUBLE_FESTIVAL_POM="DF_pom";
	public static final String DOUBLE_FESTIVAL_TREE="DF_tree";
	public static final String DOUBLE_FESTIVAL_PARTY="DF_party";
	
	public static final String VALENTINE_SWEET_IMAGE = "sweet_image";	//���˽ڣ�����Ӱ��
	public static final String VALENTINE_SESSION_CINEMA = "session_cinema";	//���˽�ר��
	public static final String VALENTINE_MOVIE_DAREN = "movie_daren";	//��Ӱ����
	public static final String VALENTINE_ACTIVITY_DAREN = "activity_daren";//�����
	public static final String VALENTINE_PHONE_DAREN = "phone_daren";	//�ֻ�����
	public static final String VALENTINE_SCENE_LOVE = "scene_love";		//�ֳ�����
	public static final String VALENTINE_HAPPY = "happy";			//ɹ�Ҹ�
	
	public static final String WHITE_DAY = "whiteday";//��ɫ���˽�

	public static final String ANNUALSELECTION = "annualSelection";	//ר��ع�
	public static final String ANNUALSELECTION_AD = "ad";			//ר��ع˹��
	public static final String ANNUALSELECTION_CRITICS = "critics";	//Ӱ��
	public static final String ANNUALSELECTION_CITY_OF_LOVE = "cityOfLove";	//���а���
	public static final String ANNUALSELECTION_SAME_ART = "sameArt";		//��ν����
	public static final String ANNUALSELECTION_IDOL_INVINCIBLE = "idolInvincible";	//ż���޵�
	public static final String ANNUALSELECTION_REJUVENATE = "rejuvenate";			//���ϻ�ͯ
	public static final String ANNUALSELECTION_ANNUAL_JUXIAN= "annualJuxian";		//��Ⱦ���
	
	public static final String ALIENBATTLEFIELD = "alienBattlefield";	//����ս������˹�ᣩ
	public static final String PEPSICOLA = "pepsiCola"; //���¿��ֺ���ר��
	
	public static final String BATTLESHIP = "battleship";		//����ս��
	
	//public static final String WYWK_ACTIVITY = "wywkActivity";	//���ɻ
	//public static final String WYWK_IP = "wywkIP";	//����IP
	
	public static final String AVENGERS_ACTIVITY = "avengersActivity";	//����������
	public static final String TAG_AVENGER = "TAG_AVENGER";
	public static final String JAZZ_TYPE = "jazz";
	
	public static final String TITANIC = "titanic";				//̩̹��˺�
	public static final String TITANIC_POSTCARD = "postcard";	//����Ƭ
	public static final String TITANIC_VOTE = "vote";			//ͶƱ
	public static final String TITANIC_CHILDACTIVITY = "childActivity"; //��վ�
	public static final String TITANIC_CHILDATYPE ="childType";
	public static final String SITES_HANGZHOU = "hangzhou"; //���ݷ�վ
	
	public static final String GYM_BELLYDANCE = "bellydanceGYM";	//�����Ƥ��
	public static final String SPORT_POOL_PARTY = "poolparty";		//Ӿ���ɶ�
	public static final String DRAMA_REDCAT = "drama_redcat"; //�����̺è
	public static final String DRAMA_MUSICCAT = "drama_musiccat"; //���־�-è
	
	public static final String MESSAGE_FANS_ADD = "add";		//���fans��
	public static final String MESSAGE_FANS = "fans";
	public static final String MESSAGE_FANS_REMOVE = "remove";	//�Ƴ�fans��
	
	public static final String IDNAME_YQ = "HT";
	public static final String FIELD_GW = "GW";
	public static final String FIELD_HT = "HT";
	public static final String FIELD_YG = "YG";
	public static final String FIELD_XS = "XS";
	public static final String FIELD_GJ = "GJ";
	public static final String FIELD_5173 = "5173";
	
	public static final String GEWA_CUP_IDNAME="cupid";
	public static final String GEWA_CUP_STATUS="cupstatus";
	public static final String GEWA_CUP_MEMBERID="memberid";
	public static final String GEWA_CUP_ORDERID="orderid";
	
	public static final String GEWA_CUP_YEARS_2013 = "gewaCup2013";		//�ٰ����
	public static final String GEWA_CUP_YEARS_2012 = "gewaCup2012";		//�ٰ����
	public static final String GEWA_CUP_ANSWER = "answer";				//�ٱ�
	public static final String GEWA_CUP_BOY_SINGLE = "boysingle";		//�е�
	public static final String GEWA_CUP_BOY_DOUBLE = "boydouble";		//��˫
	public static final String GEWA_CUP_GIRL_SINGLE = "girlsingle";		//Ů��
	public static final String GEWA_CUP_GIRL_DOUBLE = "girldouble";		//Ů˫
	public static final String GEWA_CUP_MIXED_DOUBLE = "mixeddouble";	//��˫
	
	
	
	//��ҳ����ͳ��
	public static final String INDEX_KEY = "index"; //ȫվ��ҳkey
	public static final String INDEX_TICKET_COUNT = "ticketcount"; //��ӰƱ��
	public static final String INDEX_DRAMA_COUNT = "dramacount";	//����Ʊ��
	public static final String INDEX_SPORT_COUNT = "sportcount";	//�˶�Ʊ��
	public static final String INDEX_POINT_COUNT = "pointcount";	//�������
	public static final String INDEX_COMMENT_COUNT = "commentcount";	//������
	public static final String INDEX_DIARY_COUNT = "diarycount";	//��������Ӱ�����������˶��ĵã�
	public static final String INDEX_ALL_DIARY_COUNT = "alldiarycount"; //��������
	public static final String INDEX_JOIN_ACTIVITY_COUNT = "joinactivitycount"; //�μӻ����
	
	public static final String NS_TELE_MOBILE = "teleSms"; //���Ŷ���
	
	public static String buildId(){
		return buildId(2);
	}
	public static String buildId(int num){
		return StringUtil.getRandomString(num) + System.currentTimeMillis();
	}
}
