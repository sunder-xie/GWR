package com.gewara.untrans.subject;

import com.gewara.model.user.TempMember;
import com.gewara.support.ErrorCode;

public interface BaiFuBaoService {

	// �齱
	ErrorCode<String> drawClick(Long memberid, String ip);
	// �μ�����
	long joinCount();
	// �Ƿ������ʸ���
	boolean hasQualifications(Long memberid);
	ErrorCode<String> getPayUrl(TempMember tm);
	String queryOrder(String tradeNo);
	void refreshCounter();
	ErrorCode<TempMember> processPaySuccess(Long tmid);
	ErrorCode<String> checkStatus(String mobile, String password);

}
