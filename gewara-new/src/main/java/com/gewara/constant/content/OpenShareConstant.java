package com.gewara.constant.content;

import java.util.Arrays;
import java.util.List;



public class OpenShareConstant {
	
	public static final String WEIBO_APPKEY = "2536251945";
	public static final String WEIBO_SECRET = "0157289cf7bf64f72c31ba3415991d7e";
	public static final String WEIBO_OAUTH_URL = "https://api.weibo.com/oauth2/authorize";
	public static final String WEIBO_OAUTH_ACCESS_URL = "https://api.weibo.com/oauth2/access_token";
	public static final String WEIBO_OAUTH_UPLOAD_TEXT = "https://api.weibo.com/2/statuses/update.json";
	public static final String WEIBO_OAUTH_UPLOAD_PIC_TEXT = "https://upload.api.weibo.com/2/statuses/upload.json";
	public static final String WEIBO_OAUTH_GET_FRIENDS = "https://api.weibo.com/2/friendships/friends.json";
	public static final String WEIBO_OAUTH_GET_USERSHOW = "https://api.weibo.com/2/users/show.json";
	
	
	//��΢��״̬start
	public static final int WEIBO_BIND_STATUS_UNDEFINED = 0; //δ��΢��
	public static final int WEIBO_BIND_STATUS_EXPIRED = 1;	//��ʱ���ѹ���
	public static final int WEIBO_BIND_STATUS_SUCCESS = 2;	//�󶨳ɹ�����Ч����
	//��΢��״̬end
	
	
	//��������ǩstart
	public static final String TAG_SHARE_DIARY_MOVIE = "share_diary_movie";							//���ӵ�Ӱ
	public static final String TAG_SHARE_DIARY_DRAMA = "share_diary_drama";							//���ӻ���
	public static final String TAG_SHARE_DIARY_TOPIC = "share_diary_topic";							//������ͨ
	
	public static final String TAG_SHARE_ACTIVITY_JOIN = "share_activity_join";					//��μ�
	public static final String TAG_SHARE_ACTIVITY_LAUNCH = "share_activity_launch";				//�����
	
	public static final String TAG_SHARE_TICKET_DRAMA = "share_ticket_drama";						//��Ʊ����
	public static final String TAG_SHARE_TICKET_MOVIE = "share_ticket_movie";						//��Ʊ��Ӱ
	
	public static final String TAG_SHARE_WALA_TRANSFER = "share_wala_transfer";					//walaת��
	public static final String TAG_SHARE_WALA_TOPIC = "share_wala_topic";							//wala��ͨ
	public static final String TAG_SHARE_WALA_DRAMA = "share_wala_drama";							//wala����
	public static final String TAG_SHARE_WALA_MOVIE = "share_wala_movie";							//wala��Ӱ
	public static final String TAG_SHARE_WALA_OTHER = "share_wala_other";							//wala����
	
	public static final String TAG_SHARE_AGENDA_OTHER = "share_agenda_other";						//����
	
	public static final String TAG_SHARE_POINT_FESTIVAL = "share_point_festival";					//���պ��
	public static final String TAG_SHARE_POINT_REWARDS = "share_point_rewards";					//������ȡ
	public static final String TAG_SHARE_POINT_BIT_POSITIVE = "share_point_bit_positive";		//ð������
	public static final String TAG_SHARE_POINT_BIT_NEGATIVE = "share_point_bit_negative";		//ð�ո���
	public static final String TAG_SHARE_POINT_BRT = "share_point_brt";								//΢����
	public static final String TAG_SHARE_POINT_STABLE = "share_point_stable";						//�ȶ���
	public static final String TAG_SHARE_POINT_ORDER = "share_point_order";							//��Ʊ����ַ���
	//��������ǩend
	
	public static final List<String> SHARETAGLIST =  Arrays.asList(new String[]{TAG_SHARE_DIARY_MOVIE, TAG_SHARE_DIARY_DRAMA, TAG_SHARE_DIARY_TOPIC, TAG_SHARE_ACTIVITY_JOIN,
			TAG_SHARE_ACTIVITY_LAUNCH, TAG_SHARE_TICKET_DRAMA, TAG_SHARE_TICKET_MOVIE, TAG_SHARE_WALA_TRANSFER, TAG_SHARE_WALA_TOPIC, TAG_SHARE_WALA_DRAMA, TAG_SHARE_WALA_MOVIE,
			TAG_SHARE_WALA_OTHER, TAG_SHARE_AGENDA_OTHER, TAG_SHARE_POINT_FESTIVAL,TAG_SHARE_POINT_REWARDS, TAG_SHARE_POINT_BIT_POSITIVE, TAG_SHARE_POINT_BIT_NEGATIVE,
			TAG_SHARE_POINT_BRT, TAG_SHARE_POINT_STABLE, TAG_SHARE_POINT_ORDER});
}
