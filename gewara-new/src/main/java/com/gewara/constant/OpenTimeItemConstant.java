package com.gewara.constant;

import java.util.Arrays;
import java.util.List;
public abstract class OpenTimeItemConstant {
	public static final String STATUS_NEW 		= "A";			//����λ
	public static final String STATUS_LOCKR 	= "R";			//�����۳�����
	public static final String STATUS_LOCKL 	= "L";			//����[������Ϊ����]
	public static final String STATUS_LOCKLF 	= "LF";			//����[������ǿ������]
	public static final String STATUS_LOCKD 	= "D_GW";		//����[�¶�������]
	public static final String STATUS_SOLD 	= "S_GW";			//�۳�
	public static final String STATUS_DELETE = "delete";		//ɾ��
	public static final String TIGHT_SPORT_TIGHT = "SportTight";
	public static final String TIGHT_SPORT_ID = "sportid";
	public static final String TIGHT_ITEM_ID = "itemid";
	public static final String TIGHT_PLAY_DATE = "playdate";
	public static final String TIGHT_BEFORE_TIME_NUM = "beforeTimeNum";		//����֮ǰ����
	public static final String TIGHT_BEFORE_TIME_COUNT = "beforeTimeCount";	//����֮ǰ�۳�����
	public static final String TIGHT_AFTER_TIME_NUM = "afterTimeNum";		//����֮������
	public static final String TIGHT_AFTER_TIME_COUNT = "afterTimeCount";	//����֮���۳�����
	public static final String TIGHT_TIME_TEMP = "timetemp"; //����
	
	public static final List<String> LOCKEDLIST = Arrays.asList(new String[]{OpenTimeItemConstant.STATUS_LOCKR, OpenTimeItemConstant.STATUS_LOCKL, OpenTimeItemConstant.STATUS_LOCKD, OpenTimeItemConstant.STATUS_LOCKLF});
	
	public static final List<String> remoteStatus = Arrays.asList(new String[]{"W", "S", "D"});
}
