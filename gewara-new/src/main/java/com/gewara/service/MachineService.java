package com.gewara.service;

import java.util.List;

import com.gewara.model.machine.Machine;

public interface MachineService {
	
	/**
	 * ��ѯgewa�����б�
	 * @return
	 */
	List<Machine> getGewaMachineList(String citycode, String machinenumber, String machinename, Long cinemaid, String linkmethod, 
			String machineowner, Integer ticketcount, String machinetype, String machinestatus, int from, int maxnum);
	/**
	 * ��ѯ����
	 * @param machinenumber
	 * @param machinename
	 * @param cinemaid
	 * @param linkmethod
	 * @param touchtype
	 * @param ticketcount
	 * @param machinetype
	 * @param machinestatus
	 * @return
	 */
	Integer gewaMachineCount(String cicycode, String machinenumber, String machinename, Long cinemaid, String linkmethod, 
			String machineowner, Integer ticketcount, String machinetype, String machinestatus);
	/**
	 * ��ѯÿ�ֻ����������
	 */
	Integer getMaxMachineNumber(String machinename,String machineprefix);
}
