package com.gewara.untrans.ticket;

import java.util.List;

import com.gewara.json.TempRoomSeat;
import com.gewara.model.acl.User;
import com.gewara.support.ErrorCode;
/**
 * 
 * @author john.zhou@gewara.com
 *
 */
public interface TempRoomSeatService {
	/**
	 * ƥ����λ���ݣ���ʽ: "1:1,1:2,2:1,2:2,15:14"
	 */
	public static final String BATCH_PEX = "^[0-9a-zA-Z]{1,3}:[0-9a-zA-Z]{1,3}(,[0-9a-zA-Z]{1,3}:[0-9a-zA-Z]{1,3})*$";
	public static final String SINGLE_PEX = "^[0-9a-zA-Z]{1,3}:[0-9a-zA-Z]{1,3}$";
	/**
	 * ͨ��Ӱ��ID��ģ����������������λ���
	 * @param roomid Ӱ��ID
	 * @param tmpname ģ������
	 * @param user ����Ա��Ϣ
	 * @return �ɹ����� ģ�����ݶ��󣬷�֮���ش�����Ϣ
	 */
	ErrorCode<TempRoomSeat> addRoomSeat(Long roomid, String tmpname, User user);
	/**
	 * ͨ��Ӱ��ID��ģ�����Ʋ�ѯӰ��������λ���
	 * @param roomid Ӱ��ID
	 * @param tmpname ģ������
	 * @return Ӱ��������λ���
	 */
	TempRoomSeat getRoomSeat(Long roomid, String tmpname);
	
	/**
	 * ͨ��Ӱ��ID��ģ�������޸�Ӱ��������λ���
	 * @param roomid Ӱ��ID
	 * @param tmpname ģ������
	 * @param seatbody ��λ����,��ʽ: "1:1 ��12:13" ���ַ�����:ǰ���ִ�����λ�кţ�:�����ִ�����λ�к�
	 * @param add Ϊtrue������λ����,flaseΪɾ������
	 * @param user ����Ա��Ϣ
	 * @return �ɹ����� "success"����֮���ش�����Ϣ
	 */
	ErrorCode<String> updateRoomSeat(Long roomid, String tmpname, String seatbody, boolean add, User user);
	
	/**
	 * ͨ��Ӱ��ID��ģ�����������޸�Ӱ��������λ���
	 * @param roomid Ӱ��ID
	 * @param tmpname ģ������
	 * @param seatbody ��λ����,��ʽ: "1:1,1:2,2:1,2:2,15:14" ���ַ�����:ǰ���ִ�����λ�кţ�:�����ִ�����λ�к�
	 * @param add Ϊtrue������λ����,flaseΪɾ������
	 * @param user ����Ա��Ϣ
	 * @return �ɹ����� "success"����֮���ش�����Ϣ
	 */
	ErrorCode<String> batchUpdateRoomSeat(Long roomid, String tmpname, String seatbody, boolean add, User user);
	
	/**
	 * ͨ��Ӱ��ID��ѯӰ��ģ������
	 * @param roomid Ӱ��ID
	 * @return Ӱ��ģ�����ݼ���
	 */
	List<TempRoomSeat> getRoomSeatList(Long roomid);



}
