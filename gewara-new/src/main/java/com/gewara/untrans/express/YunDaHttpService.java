package com.gewara.untrans.express;

import com.gewara.model.express.ExpressOrder;
import com.gewara.support.ErrorCode;

public interface YunDaHttpService {
	/**
	 * ͨ����ݵ��Ų�ѯ�����Ϣ
	 * @param ExpressOrder expressOrder	��ݵ���Ϣ
	 * @return
	 */
	ErrorCode<ExpressOrder> qryExpress(ExpressOrder expressOrder);
}
