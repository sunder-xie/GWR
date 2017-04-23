package com.gewara.constant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;

public abstract class DiaryConstant implements Serializable {

	private static final long serialVersionUID = 7800942669108910128L;

	// ��������־�����ͣ�һ������ͶƱ���ӡ�Ӱ��
	public static final String DIARY_TYPE_ALL = ""; // ����
	public static final String DIARY_TYPE_COMMENT = "comment"; // Ӱ�����ĵã�������
	public static final String DIARY_TYPE_TOPIC_DIARY = "topic_diary"; // һ������
	public static final String DIARY_TYPE_TOPIC_VOTE_RADIO = "topic_vote_radio"; // ͶƱ����ѡ��
	public static final String DIARY_TYPE_TOPIC_VOTE_MULTI = "topic_vote_multi"; // ͶƱ����ѡ��
	public static final String DIARY_TYPE_TOPIC = "topic"; // ��������
	public static final String DIARY_TYPE_TOPIC_VOTE = "topic_vote"; // ͶƱ
	public static final String DIVISION_Y = "Y";	// Ӱ��
	public static final String DIVISION_N = "N";	// �������
	public static final String DIVISION_A = "A";	// ȫ��
	public static final Map<String, String> DIARY_TYPE_MAP;
	static {
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put("0", DIARY_TYPE_ALL);
		tmp.put("1", DIARY_TYPE_COMMENT);
		tmp.put("2", DIARY_TYPE_TOPIC_DIARY);
		tmp.put("3", DIARY_TYPE_TOPIC_VOTE_RADIO);
		tmp.put("4", DIARY_TYPE_TOPIC_VOTE_MULTI);
		tmp.put("5", DIARY_TYPE_TOPIC);
		tmp.put("6", DIARY_TYPE_TOPIC_VOTE);
		DIARY_TYPE_MAP = UnmodifiableMap.decorate(tmp);
	}
}
