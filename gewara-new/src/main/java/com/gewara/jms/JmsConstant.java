package com.gewara.jms;

public interface JmsConstant {
	String TAG_ORDER = "order";									//������Ϣ
	String TAG_UPADATE_PAGE_CACHE = "update_page_cache";		//����ҳ�滺��
	String TAG_SHARE2Out = "share2Out";							//����΢��
	String TAG_SHARECUSTOM = "shareCustom";						//����΢���Զ���
	String TAG_SAVELOG = "saveLog";								//������־
	String TAG_CHARGE = "charge";								//��ֵ
	
	String TAG_TERMINALBARCODE = "terminalbarcode";				//�ն˶�ά��ȡƱ
	
	String QUEUE_PAY = "paidOrderQueue";						//֧����ɵĶ���
	String QUEUE_CHARGE = "paidChargeQueue";					//֧����ɵĶ���
	String QUEUE_SUCCORDER = "addOrderQueue";					//�ɹ���������
	String QUEUE_UPDATECACHE = "updateCacheQueue";				//���»���
	String QUEUE_SHARE = "shareQueue";							//�������
	String QUEUE_ORDER_ACTIVITYGOODS = "activityGoodsQueue";	//���������
	String QUEUE_ORDER_GYM = "gymOrderQueue";					//����������
	String QUEUE_TICKETPLAYITEM = "ticketPlayItemQueue";		//��Ʊϵͳ��Ƭ����
	
	String QUEUE_TICKETREMOTEORDER = "ticketRemoteOrderQueue";	//�����Ǵ���������	

	String QUEUE_SPIDERPLAYITEM = "spiderPlayItemQueue";		//Spiderϵͳ��Ƭ����
	
	String QUEUE_TERMINAL_ORDER = "terminalOrderQueue";			//�������׳ɹ�֪ͨ���ն˻�
	
	String QUEUE_GPTICKETREMOTEORDER= "gpticketRemoteOrderQueue";	//�ݳ�������Ʊ֪ͨ
}
