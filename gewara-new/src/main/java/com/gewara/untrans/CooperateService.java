/**
 * 
 */
package com.gewara.untrans;

import com.gewara.model.pay.GewaOrder;
import com.gewara.support.ErrorCode;

/**
 * ������
 * @author Administrator
 *
 */
public interface CooperateService {
	/**
	 * �ؼۻ���п���֧����¼
	 * @param tradeno
	 * @param otherinfo
	 * @param spid
	 * @return
	 */
	boolean addCardnumOperation(String tradeno,String otherinfo,Long spid);
	/**
	 * ͨ�ÿ�bin��֤�����п�ʹ�ô�����֤
	 * @param order
	 * @param spid
	 * @param cardNumber
	 * @return
	 */
	ErrorCode<String> checkCommonCardbinOrCardNumLimit(GewaOrder order,Long spid,String cardNumber);
	/**
	 * �Ϻ�����������ӰƱ�
	 * @param preCardno
	 * @return
	 */
	ErrorCode<String> checkShbankCode(Long orderid, String preCardno, String endCardno);

	/**
	 * �Ϻ����з�����֤
	 * @param otherinfo
	 * @return
	 */
	ErrorCode checkShbankBack(Long orderid, String otherinfo, String isSave);
	
	/**
	 * ��ҵ����������ӰƱ�
	 * @param orderid
	 * @param preCardno
	 * @param endCardno
	 * @return
	 */
	ErrorCode<String> checkXybankCode(Long orderid, String preCardno, String endCardno);
	/**
	 * �������з�����֤
	 * @param orderid
	 * @param preCardno
	 * @param endCardno
	 * @return
	 */
	ErrorCode<String> checkHxbankCode(Long orderid, String preCardno, String endCardno);
	/**
	 * �Ϻ����з�����֤
	 * @param otherinfo
	 * @return
	 */
	ErrorCode checkXybankBack(Long orderid, String otherinfo, String isSave);
	/**
	 * ����2.0��ݽӿ�֧����binУ��
	 * @param orderid
	 * @param cardNumber
	 * @return
	 */
	ErrorCode checkUnionPayFastCode(GewaOrder order,String payBank,String cardNumber,Long spid);
	
	/**
	 * 
	 * �������й����У�����2.0��ݽӿ�֧����binУ�飬��һ�ſ�һ������ֻ��ʹ��һ��
	 * 
	 * @param order
	 * @param paybank
	 * @param cardNumber
	 * @param spid
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 7, 2013 5:23:37 PM
	 */
	public ErrorCode<String> checkUnionPayFastCodeForSZ(GewaOrder order,String paybank,String cardNumber,Long spid);
	
	public ErrorCode<String> checkUnionPayFastCodeForNyyh(GewaOrder order,String paybank, String cardNumber, Long spid);
	
	/**
	 * ����ũ���л����bin��֤�� ÿ��ÿ��ֻ�ܲμ�һ�λ
	 * 
	 * @param order
	 * @param paybank
	 * @param cardNumber
	 * @param spid
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 28, 2013 5:58:27 PM
	 */
	public ErrorCode<String> checkUnionPayFastCodeForCqnsyh(GewaOrder order,String paybank, String cardNumber, Long spid);
	
	/**
	 * �������ѽڻ ,֧���������еĿ�����������Ϊ62����ÿ��ֻ�ܲμ�һ�λ
	 * 
	 * @param order
	 * @param paybank
	 * @param cardNumber
	 * @param spid
	 * @return
	 *
	 * @author leo.li
	 * Modify Time May 28, 2013 5:45:59 PM
	 */
	public ErrorCode<String> checkUnionPayFastCodeForYouJie(GewaOrder order,String paybank, String cardNumber, Long spid);
	
	/**
	 * �����������ÿ����������Ϻ����̻��ţ���ÿ�������Ϊÿ��ÿ����ʹ��һ�Ρ�
	 * 
	 * @param paybank
	 * @param cardNumber
	 * @param spid
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Jun 9, 2013 11:55:12 AM
	 */
	public ErrorCode<String> checkUnionPayFastCodeForWzcb(GewaOrder order,String paybank,String cardNumber, Long spid);
	
	/**
	 * �������У���֤��bin
	 * 
	 * @param order
	 * @param paybank
	 * @param cardNumber
	 * @param spid
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Jul 2, 2013 5:36:38 PM
	 */
	public ErrorCode<String> checkUnionPayFastCodeForZdcb(GewaOrder order,String paybank,String cardNumber,Long spid);
	
	/**
	 * ��������2.0��ݽӿ�֧����binУ�飬����ʹ�ô���
	 * 
	 * @param order
	 * @param cardNumber
	 * @return
	 *
	 * @author leo.li
	 * Modify Time Mar 19, 2013 7:40:07 PM
	 */
	public ErrorCode<String> checkUnionPayFastAJS(GewaOrder order,String cardNumber,Long spid);
	
	/**
	 * ��������2.0���binУ�飬����ʹ�ô���
	 * @param order
	 * @param cardNumber
	 * @param spid
	 * @return
	 */
	public ErrorCode<String> checkUnionPayFastBJ(GewaOrder order,String cardNumber,Long spid);

	ErrorCode<String> checkUnionPayFastShenZhenCodeForPingAn(GewaOrder order, String paybank, String cardNumber, Long spid);

	ErrorCode<String> checkUnionPayFastGuangzhouCodeForBocByWeekone(GewaOrder order, String paybank, String cardNumber, Long spid);
	
	ErrorCode<String> checkUnionPayFastGuangzhouCodeForBocByMonthTwo(GewaOrder order, String paybank, String cardNumber, Long spid);
	
	

	

	
}
