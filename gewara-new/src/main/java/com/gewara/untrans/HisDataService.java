package com.gewara.untrans;

import java.util.List;
import java.util.Map;

import com.gewara.model.pay.SMSRecordBase;

public interface HisDataService {
	/**
	 * ���ݻ�����ϸ�����������û�ID+�·ݣ�����
	 * @return
	 */
	int backupPointHist();
	/**
	 * ���ְ��û�ID����
	 * @return
	 */
	int createPointIndex();
	/**
	 * ���ݶ��ţ�ID=�ֻ���+ʱ��
	 * @param recordid
	 * @return
	 */
	int backupSMSRecordHist();
	<T extends SMSRecordBase> int saveSMSRecordList(List<T> smsList);
	List<Map<String, String>> getHisSmsList(String mobile);
	int backupOrder();
}
