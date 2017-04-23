package com.gewara.service.express;

import java.util.List;

import com.gewara.model.acl.User;
import com.gewara.service.BaseService;
import com.gewara.support.ErrorCode;

public interface ExpressOrderService extends BaseService {

	/**
	 * ������ݵ�����Ϣ
	 * @param expressnote		��ݵ���
	 * @param expresstype		�������
	 * @param tradeNoList		��������
	 * @param user				�û���Ϣ
	 * @return
	 */
	ErrorCode saveExpressOrder(String expressnote, String expresstype, List<String> tradeNoList, User user);

}
