package com.gewara.job;


public interface TicketOrderJob {
	/**
	 *��ʱ������������paidFailure�������� 
	 */
	void correctOrder();
	void checkHfhOrder();
	/**
	 * ��ʱ���Ͷ���
	 */
	void sendCallbackOrder();
	/**
	 * û��֪ͨ�Ķ���
	 */
	void unNotifyOrder();
	//void addOpiUpdate(Long mpid, boolean isFinished);
}
