package com.gewara.json;

import java.io.Serializable;

/**
 * �û��ۺ�����
 * @author gebiao(ge.biao@gewara.com)
 * @since Jan 7, 2013 5:33:32 PM
 */
public class MemberStats implements Serializable{
	private static final long serialVersionUID = 6804929470612610706L;
	public static final String FIELD_LASTCINEMAID = "lastCinemaid";
	public static final String FIELD_LASTMOVIEID = "lastMovieid";
	public static final String FIELD_LASTSPORTID = "lastSportid";
	public static final String FIELD_ATTENTIONCOUNT = "attentioncount";		//��ע��
	public static final String FIELD_FANSCOUNT = "fanscount"; 				//��˿��
	public static final String FIELD_DIARYCOUNT = "diarycount"; 			//������
	public static final String FIELD_COMMUCOUNT = "commucount"; 			//Ȧ����
	public static final String FIELD_COMMENTCOUNT = "commentcount"; 		//������
}
