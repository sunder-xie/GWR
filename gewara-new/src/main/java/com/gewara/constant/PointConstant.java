package com.gewara.constant;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;

import com.gewara.constant.content.SignName;

public class PointConstant {
	public static final Integer LOGIN_REWARDS_DAYNUM = 7; 					//���ֽ�������
	public static final String TAG_CONFIRM = "confirm"; 					//�û�ע��߼�ȷ��
	public static final String TAG_LOGIN_ACTIVIRY = "loginactivity";		//ÿ�ջ�����
	public static final String TAG_LOGIN_ACTIVIRY_REWARDS = "loginrewards";	//ÿ�ջ��ֽ���
	public static final String TAG_INVITED_FRIEND_TICKET = "invitefriend_ticket";	//������Ѷ�Ʊ
	public static final String TAG_INVITED_FRIEND_GOODS = "invitefriend_goods";		//�����������Ʒ
	public static final String TAG_TRADE = "trade";					//�������
	public static final String TAG_CHARGETOWABI = "chargetowabi";	//��ֵ�߱�
	
	/**�˿��**/
	public static final String TAG_REFUND_TRADE = "trade_refund";
	
	public static final String TAG_SHARE_ORDER = "share_order";		//����Ʊ
	public static final String TAG_PUBSALE="pubsale";				//����
	public static final String TAG_ADD_INFO = "addinfo";			//�����Ϣ(ӰƬ�����ݵȣ�
	public static final String TAG_CORRECT = "correct";				//����(ӰƬ�����ݡ���Ŀ�ȣ�
	public static final String TAG_CONTENT = "content";				//�ֶ�����ԭ��
	public static final String TAG_ACCUSATION ="accusation";		//�ٱ�
	
	//public static final String TAG_CHARGE = "charge";				//���ճ�ֵ
	//public static final String TAG_BAY_GOODS = "bayGoods";
	//public static final String TAG_ADDMICRO_WAP = "microwap";		//�����ֻ�wap����
	//public static final String TAG_ADDMICRO_WEB = "microweb";		//������ҳ������

	//public static final String TAG_ATTACHMOVIE="attachmovie";		//��Ӿ���ӻ���
	//public static final String TAG_ATTACHPICTURE="attachpicture";	//���ͼƬ�ӻ���
	//public static final String TAG_ATTACHVIDEO="attachvideo";		//�����Ƶ�ӻ���
	//public static final String TAG_ACTIVITY="activity";			//�μӻ���Ļ���
	//public static final String TAG_SUBJECT_ACTIVITY="subject";	//�μ�ר�����ͻ����Ļ��ֺ����ƴר���ʶ+

	//public static final String TAG_PAYMENT = "pay";				//��ֵ
	//public static final String TAG_INVITED_FRIEND = "invite"; 	//�������ע��gewara
	//public static final String TAG_INVITE = "login";				//�û�ע��
	//public static final String TAG_NEW_TASK = "newtask";			//��������
	//public static final String TAG_BANKTOWABI = "banktowabi";		//�˻����ת��Ϊ�߱�
	
	//public static final String TAG_OTHER = "other";
	
	public static final String FREEBACK_COMMENTCINEMA = "commentcinema"; //�ۺ�У���������
	public static final String FREEBACK_COMMENTMOVIE = "commentmovie"; //һ�仰Ӱ��
	public static final String FREEBACK_COMMENTDRAMA = "commentdrama"; //һ�仰����
	public static final String FREEBACK_COMMENTSPORT = "commentsport"; //�˶����ݵ���
	public static final String REPLY_MESSAGEMOVIE = "replymessagemovie";		//��Ӱ�ظ�����
	public static final String REPLY_MESSAGEDRAMA = "replymessagedrama";		//����ظ�����
	public static final String REPLY_MESSAGESPORT = "replymessagesport";		//�˶��ظ�����
	public static final String FREEBACK_DIARYMOVIE = "diarymovie"; //�ۺ��:����Ӱ��
	public static final String FREEBACK_COMMENTTHEATRE = "commenttheatre"; //�ۺ�У�������Ժ
	public static final String FREEBACK_DIARYDRAMA = "diarydrama"; //�ۺ��:�������
	public static final String UPDATE_PLACE = "update_place";//���³�����Ϣ
	public static final String PERFECT_PLACE = "perfect_place";//���Ƴ�����Ϣ
	public static final String ADD_ITEM = "add_item";//�����Ŀ��Ϣ
	public static final String UPDATE_ITEM = "update_item";//������Ŀ��Ϣ
	public static final String PERFECT_ITEM = "perfect_item";//������Ŀ��Ϣ
	public static final String POINT_EXCAHNGE = "pointexchange";

	public static final Integer SCORE_CORR = 20;//����ӷ�
	public static final Integer SCORE_ADDMOVIE = 20;//��ӵ�Ӱ�ӷ�
	public static final Integer SCORE_ADDPLACE = 20;//��ӳ��ݼӷ�
	public static final Integer SCORE_FREEBACK = 30;//�ۺ��
	
	public static final Integer SCORE_USERCONFIRM = 50;		//�û��߼�ȷ�ϼӻ��� ��֤����
	public static final Integer SCORE_USERSENDWALA = 2;		//�������������ӻ���
	public static final Integer SCORE_USERJOINCOMMU = 3;	//���������Ȧ�Ӽӻ���
	public static final Integer SCORE_USERFIVEFRIEND = 5;	//��������ӹ�ע�ӻ���
	public static final Integer SCORE_USERHEADPIC = 10;		//�����������ͷ��ӻ���
	public static final Integer SCORE_USERBINDMOBILE = 20;	//����������ֻ��ӻ���
	public static final Integer SCORE_USERMOVIECOMMENT = 5;	//��������дӰ���ӻ���
	
	public static final Integer SCORE_USERREGISTER = 100;	//�û�ע��
	public static final Integer SCORE_LOGIN_REWARDS = 10;	//������¼����
	public static final Integer SCORE_ACCUSATION = 5; 		//�ٱ�
	public static final Map<String, String> pointTagMap;
	
	static{
		Map<String, String> tmp = new LinkedHashMap<String, String>();
		tmp = new LinkedHashMap<String, String>();
		tmp.put(POINT_EXCAHNGE, "���ֶһ�");
		tmp.put(TAG_CONTENT, "�ֶ�����ԭ��");
		tmp.put(TAG_LOGIN_ACTIVIRY_REWARDS, "ÿ�ջ��ֽ���");
		tmp.put(TAG_TRADE, "�����ɽ�");
		tmp.put(TagConstant.AGENDA_ACTION_PUBSALE, "����");
		tmp.put(FREEBACK_COMMENTCINEMA, "��������");
		tmp.put(SignName.DRAWACTIVITY, "�齱�");
		tmp.put(TAG_LOGIN_ACTIVIRY, "ÿ�ջ�����ȡ");
		tmp.put(FREEBACK_COMMENTTHEATRE, "�ۺ�У�������Ժ");
		tmp.put(TAG_CONFIRM, "�߼�ȷ��");
		tmp.put(TAG_INVITED_FRIEND_TICKET, "������Ѷ�Ʊ");
		tmp.put(FREEBACK_COMMENTDRAMA, "һ�仰����");
		tmp.put(TAG_ACCUSATION, "�ٱ�");
		tmp.put(FREEBACK_DIARYDRAMA, "�ۺ��:�������");
		tmp.put(FREEBACK_DIARYMOVIE, "�ۺ��:����Ӱ��");
		//tmp.put(TAG_ATTACHPICTURE, "�ϴ�ͼƬ");
		//tmp.put(TAG_ADDMICRO_WAP, "�����ֻ�wap����");
		//tmp.put(TAG_ATTACHMOVIE, "��Ӿ���ӻ���");
		//tmp.put(TAG_ATTACHVIDEO, "�����Ƶ�ӻ���");
		//tmp.put(TAG_CHARGE, "���ճ�ֵ");
		//tmp.put(TAG_NEW_TASK, "��������");
		//tmp.put(TAG_INVITE, "�û�ע��");
		//tmp.put(TAG_INVITED_FRIEND, "�������ע��gewara");
		//tmp.put(TAG_PAYMENT, "��ֵ");
		tmp.put(TAG_CORRECT, "����(ӰƬ�����ݡ���Ŀ�ȣ�");
		tmp.put(TAG_ADD_INFO, "�����Ϣ(ӰƬ�����ݵȣ�");
		pointTagMap = UnmodifiableMap.decorate(tmp);
	}
	
}
