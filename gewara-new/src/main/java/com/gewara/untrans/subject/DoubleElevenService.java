package com.gewara.untrans.subject;

import java.util.Map;

import com.gewara.support.ErrorCode;

public interface DoubleElevenService {
	
	public Integer getTodayWinnerCount(Long memberid, String tag);

	// �齱
	public ErrorCode<String> drawClick(Long memberid, String tag, String ip, Integer dayCount);

	// �õ��齱ʱ��
	public ErrorCode<String> getClickTime(Long memberid, String tag);

	// �õ��齱����
	public ErrorCode<String> getClickCount(Long memberid, String tag);

	// �������΢��
	public ErrorCode<String> saveShareWeibo(Long memberid, String tag, String source);

	Map getShareStatusMap(Long memberid, String tag);
}
