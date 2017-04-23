package com.gewara.service.gewapay;

import java.util.List;
import java.util.Map;

import com.gewara.model.acl.User;
import com.gewara.model.report.Report;
import com.gewara.support.ErrorCode;

public interface DynReportService {
	/**
	 * ��ȡ���ж�̬����
	 * @return
	 */
	List<Report> getDynReportList();

	ErrorCode saveReport(Report report, User user);
	/**
	 * ��鱨�������Ȩ��
	 * @param report
	 * @param user
	 */
	void checkRights(Report report, User user);

	List<Map<String, Object>> getReportDataList(Report report, int from, List params, User user);
	
	List<Map<String, Object>> queryMapBySQL(String sql, int from, int maxnum, Object... params);

}
