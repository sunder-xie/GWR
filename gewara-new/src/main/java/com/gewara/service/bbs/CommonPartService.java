package com.gewara.service.bbs;

import java.util.Date;

import com.gewara.model.user.Festival;

/**
 * ���÷���
 * @author 
 *
 */
public interface CommonPartService {

	/**
	 * ��ȡ������Ϣ
	 */
	Festival getCurFestival(Date date);
	Festival getNextFestival(Date date);
}
