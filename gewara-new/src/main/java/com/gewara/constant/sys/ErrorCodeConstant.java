package com.gewara.constant.sys;

import com.gewara.constant.ApiConstant;
import com.gewara.support.ErrorCode;
public class ErrorCodeConstant {
	public static ErrorCode NOT_LOGIN = ErrorCode.getFailure(ApiConstant.CODE_NOTLOGIN, "����û�е�¼�����ȵ�¼��");
	public static ErrorCode NORIGHTS = ErrorCode.getFailure(ApiConstant.CODE_USER_NORIGHTS, "��û��Ȩ�ޣ�");
	public static ErrorCode REPEATED = ErrorCode.getFailure(ApiConstant.CODE_REPEAT_OPERATION, "�����ظ�������");
	public static ErrorCode NOT_FOUND = ErrorCode.getFailure(ApiConstant.CODE_NOT_EXISTS, "δ�ҵ�������ݣ�");
	public static ErrorCode DATEERROR = ErrorCode.getFailure(ApiConstant.CODE_DATA_ERROR, "�����д���");
	public static ErrorCode BLACK_LIST = ErrorCode.getFailure(ApiConstant.CODE_SIGN_ERROR, "�������У�");
}
