package com.gewara.untrans.express;

import com.gewara.model.express.ExpressOrder;
import com.gewara.support.ErrorCode;

public interface ExpressService {

	/**
	 * ͨ����ݵ��Ż�ȡ������Ϣ
	 * @param expressOrder		��ݵ�����Ϣ
	 * @return
	 */
	ErrorCode<ExpressOrder> qryExpress(ExpressOrder expressOrder);

}
