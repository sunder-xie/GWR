package com.gewara.constant;



public abstract class Flag {
	public static final String POINT_ADDED = "point";	//������Ҽӷ�
	public static final String POINT_IGNORE = "point2"; //����˲��ӷ�TODO: ������
	
	public static final String TOP1 = "top1";// ����̳�ö�
	public static final String TOP2 = "top2";// ����̳�ö�
	public static final String RECOMMEND = "recommend";// �Ƽ�
	public static final String HOT = "hot";// ����
	public static final String TICKET = "ticket";//��Ʊ�û�
	
	
	public static final String FLAG_MEMBER = "member";
	public static final String FLAG_ADMIN = "admin";
	public static final String FLAG_API = "api";
	public static final String FLAG_USER = "api";
	public static final String FLAG_USERFILES = "userfiles";

	/***
	 *  ͼƬ�ϴ�ģ�� - TAG
	 * */
	public static final String FLAG_MICRO = "micro";
	public static final String FLAG_COMMUBG = "commubg";
	public static final String FLAG_HEAD = "head"; //��ͷ��Ϣ

	
	/**
	 *  ��Ӱ��� - ����Ҫ��Ϣ(ͣ����/���ֳ�/ˢ��..etc.)
	 * 
	 */
	public static final String SERVICE_PARK = "park";					// ͣ����
	public static final String SERVICE_OTHER_PARK = "otherPark";		// �ܱ�ͣ����
	public static final String SERVICE_EARLY_END_MPI = "earlyOrEndMpi";	//����ɢ��ͨ��
	public static final String SERVICE_VISACARD = "visacard";			// ˢ��
	public static final String SERVICE_PLAYGROUND = "playground";		// ���ֳ�
	public static final String SERVICE_3D = "3D";						// 3D
	public static final String SERVICE_SALE = "sale";					// ��Ʒ
	public static final String SERVICE_FOOD = "food";					// ����
	public static final String SERVICE_RESTREGION = "restregion";	// ��Ϣ��
	public static final String SERVICE_PAIRSEAT = "pairseat";		// ������
	public static final String SERVICE_RECREATION = "recreation";		// ����
	public static final String SERVICE_SHOPPING = "shopping";		// ����
	public static final String SERVICE_SHOPPING_TIME = "shoppingTime";		// �̳�Ӫҵʱ��
	public static final String SERVICE_CHARACTERISTIC = "characteristic";	//��ɫӰ��
	public static final String SERVICE_IMAX = "imax";					//	IMAX
	public static final String SERVICE_CHILD = "child";				//��ͯƱ�Ż�
	public static final String SERVICE_POPCORN = "popcorn";			//�ײ�
	public static final String SERVICE_WEBCOMMENT = "webcomment";	//���ѵ���
	public static final String SERVICE_CLOSESALEMSG = "closesalemsg";	//ֹͣ��Ʊ��Ϣ
	public static final String SERVICE_MEMBERCARD = "membercard";	//��Ա��
	
	public static final String SERVICE_LINESEAT = "lineseat";		//����ѡ��
	public static final String SERVICE_EXPRESS = "express";			//֧�ֿ��
	public static final String SERVICE_ETICKET = "eticket";			//֧�ֵ���Ʊ
	public static final String SERVICE_POINTPAY = "pointpay";		//֧�ֻ���
	public static final String SERVICE_CARDPAY = "cardpay";			//֧��Ʊȯ��ֵ
	public static final String SERVICE_TICKETDESC = "ticketdesc";	//��Ʊ˵��
	
	public static final String SERVICE_PARK_RECOMMEND = "parkRecommend";					// ͣ�����Ƽ�
	public static final String SERVICE_PARK_RECOMMEND_REMARK = "parkRecommendRemark";		// ͣ�����Ƽ�����ͣ��λ
	public static final String SERVICE_VISACARD_RECOMMEND = "visacardRecommend";		// ˢ���Ƽ�
	public static final String SERVICE_3D_RECOMMEND = "3DRecommend";						// 3D�Ƽ�
	public static final String SERVICE_SALE_RECOMMEND = "saleRecommend";					// ��Ʒ�Ƽ�
	public static final String SERVICE_FOOD_RECOMMEND = "foodRecommend";					// �����Ƽ�
	public static final String SERVICE_RESTREGION_RECOMMEND = "restregionRecommend";	// ��Ϣ���Ƽ�
	public static final String SERVICE_PAIRSEAT_RECOMMEND = "pairseatRecommend";		// �������Ƽ�
	public static final String SERVICE_RECREATION_RECOMMEND = "recreationRecommend";		// ����
	public static final String SERVICE_SHOPPING_RECOMMEND = "shoppingRecommend";		// ����
	public static final String SERVICE_SHOPPING_TIME_RECOMMEND = "shoppingTimeRecommend";		// �̳�Ӫҵʱ��
	public static final String SERVICE_CHARACTERISTIC_RECOMMEND = "characteristicRecommend";	//��ɫӰ���Ƽ�
	public static final String SERVICE_IMAX_RECOMMEND = "imaxRecommend";					//	IMAX�Ƽ�
	public static final String SERVICE_CHILD_RECOMMEND = "childRecommend";				//��ͯƱ�Ż��Ƽ�
	public static final String SERVICE_COMMENTID = "commentID";								//ӰԺ����
	//public static final String SERVICE_JOINT_CINEMA = "jointCinema";						//����Ժ��
	
	public static final String SERVICE_CUPBOARD_RECOMMEND = "cupboardRecommend";		//������ƾ�Ƽ�
	public static final String SERVICE_BATHE_RECOMMEND = "batheRecommend";				//ϴ�����Ƽ�
	public static final String SERVICE_MEAL_RECOMMENDL = "mealRecommend";						//�ײ��Ƽ�
	public static final String SERVICE_TRAIN_RECOMMENDL = "trainRecommend";					//רҵ��ѵ�Ƽ�
	public static final String SERVICE_LEASE_RECOMMENDL ="leaseRecommend";					//��������Ƽ�
	public static final String SERVICE_MAINTAIN_RECOMMENDL ="maintainRecommend";			//����ά���Ƽ�
	public static final String SERVICE_MEMBERCARD_RECOMMENDL = "membercardRecommend";	//��Ա��
	
	public static final String SERVICE_CUPBOARD = "cupboard";		//������ƾ
	public static final String SERVICE_BATHE = "bathe";				//ϴ����
	public static final String SERVICE_INDOOR = "indoor";			//����
	public static final String SERVICE_OUTDOOR = "outdoor";			//����
	public static final String SERVICE_SITECOUNT = "sitecount";		//��������
	public static final String SERVICE_TRAIN="train";				//רҵ��ѵ
	public static final String SERVICE_MEAL="meal";					//�ײ�
	public static final String SERVICE_HEIGHTVENUE ="heightvenue";	//���ݸ߶�
	public static final String SERVICE_FLOORING ="flooring";		//�ذ����
	public static final String SERVICE_LEASE ="lease";				//�������	
	public static final String SERVICE_MAINTAIN ="maintain";		//����ά��
	public static final String SERVICE_OPENINFO ="openinfo";		//GYM�������
	public static final String SERVICE_SEOTITLE ="seotitle";		//GYM seotitle
	public static final String SERVICE_SEODESCRIPTION ="seodescription";			//GYM seodesc
	public static final String SERVICE_EXPLOSIVE = "explosive";		//������ָ��
	public static final String SERVICE_CALORIE = "calorie";			//��·��ָ��
	public static final String SERVICE_ENDURANCE = "endurance";		//����ָ��
	public static final String SERVICE_RATIO = "ratio";				//��Ů����
	
	public static final String SERVICE_QUALIFICATIONS = "qualifications";//��������
	public static final String SERVICE_UID = "uid";		//�������
	
	//������Ŀ
	public static final String APPLE_PEOPLE= "applypeople";				//������Ⱥ
	public static final String CONSUMPTION_LEVEL= "consumptionlevel";				//����ˮƽ
	public static final String DIFFICULT_EASY= "difficulteasy";				//���׳̶�
	public static final String ESSENTIALEQUIPMENT= "essentialequipment";				//װ��
	
	
	public static final String FLAG_HISTORY = "history";
}
