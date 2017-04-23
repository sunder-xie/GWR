package com.gewara.service.gewapay;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

import com.gewara.helper.discount.SpecialDiscountHelper;
import com.gewara.model.pay.AccountRecord;
import com.gewara.model.pay.Adjustment;
import com.gewara.model.pay.Charge;
import com.gewara.model.pay.CheckRecord;
import com.gewara.model.pay.Discount;
import com.gewara.model.pay.GewaOrder;
import com.gewara.model.pay.MemberAccount;
import com.gewara.model.pay.OrderRefund;
import com.gewara.model.pay.PayBank;
import com.gewara.model.pay.Spcounter;
import com.gewara.model.pay.SpecialDiscount;
import com.gewara.model.user.Member;
import com.gewara.pay.PayValidHelper;
import com.gewara.service.OrderException;
import com.gewara.support.ErrorCode;

public interface PaymentService {
	String getOrderPayUrl2(GewaOrder order);

	String getChargePayUrl2(Charge charge);

	Map<String, String> getNetPayParams(GewaOrder order, String clientIp, String checkvalue);

	String getChargePayUrl(Charge charge, String clientIp);

	List<String> getPayserverMethodList();

	List<Charge> getChargeList(Long memberid, Timestamp from, Timestamp to, String type);

	List<Charge> getChargeListByMemberId(Long memberId, Timestamp startTime, Timestamp endTime, int from, int maxnum);

	List<Charge> getChargeListByMemberId(Long memberId, boolean ischarge, Timestamp startTime, Timestamp endTime,
			int from, int maxnum);

	List<Adjustment> getAdjustmentList(Long memberid, Timestamp timefrom, Timestamp timeto, String status);

	Integer getChargeCountByMemberId(Long memberId, boolean ischarge, Timestamp startTime, Timestamp endTime);

	List<GewaOrder> getGewaOrderListByMemberId(Long memberid);

	/**
	 * ���ϳ�ֵ�����¡�ͬ���û����˻������涩���������û�����
	 * 
	 * @param tradeNo
	 * @param payseqno
	 *           �ⲿ���׺�
	 * @param fee
	 *           ֧�����
	 * @param paymethod
	 * @param paybank
	 * @return
	 */
	ErrorCode<Charge> bankPayCharge(String tradeNo, boolean bankcharge, String payseqno, int fee, String paymethod,
			String paybank,String gatewayCode,String merchantCode);

	/**
	 * �������ϸ����һ��������ɹ�
	 * 
	 * @param tradeNo
	 * @param payseqno
	 *           �ⲿ���׺�
	 * @param fee
	 * @param paymethod
	 * @param paybank
	 * @return
	 */
	ErrorCode<GewaOrder> netPayOrder(String tradeNo, String payseqno, int fee, String paymethod, String paybank,
			String from);

	ErrorCode<GewaOrder> netPayOrder(String tradeNo, String payseqno, int fee, String paymethod, String paybank,
			String from, Timestamp paidtime,String gatewayCode,String merchantCode);

	ErrorCode<GewaOrder> netPayOrder(String tradeNo, String payseqno, int fee, String paymethod, String paybank,
			String from, Timestamp paidtime, Map<String, String> otherMap,String gatewayCode,String merchantCode);

	void gewaPayOrder(GewaOrder order, Long memberId) throws OrderException;

	/**
	 * �ҵ��ϴν��˼�¼
	 * 
	 * @return
	 */
	CheckRecord getLastCheckRecord();

	/**
	 * ���н��˵�
	 * 
	 * @return
	 */
	List<CheckRecord> getCheckRecordList(int from, int maxnum);

	/**
	 * ���˵�1�����������仯�û�
	 */
	ErrorCode<CheckRecord> closeAccountStep1(Long userid);
	/**
	 * �����˻���2���������ֵBillRecord, ����BillRecord���û��������ƽ��
	 * 
	 * @param record
	 */
	void closeAccountStep2(CheckRecord record);

	/**
	 * ����֧��ʱ���ѯ֧�����Ķ���
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	List<GewaOrder> getPaidOrderList(Long memberid, Timestamp timefrom, Timestamp timeto);

	List<GewaOrder> getPaidOrderList(Timestamp timefrom, Timestamp timeto, int from, int maxnum);

	List<AccountRecord> getAccountRecordList(Long checkid);
	/**
	 * ĳ�û�δ����Ķ���
	 * 
	 * @param memberid
	 * @return
	 */
	<T extends GewaOrder> List<T> getUnpaidOrderList(Class<T> clazz, Long memberid,String ukey);

	/**
	 * ����״̬��ȡ��������Ŀ
	 * 
	 * @param statusNew
	 * @return
	 */
	List<Adjustment> getAdjustmentList(String status, int from, int maxrow);

	Integer getAdjustmentCount(String status);

	List<Adjustment> getAdjustmentListByMemberId(Long memberid, String status);

	/**
	 * ȷ��������
	 * 
	 * @param adjustment
	 * @param user
	 * @return
	 */
	ErrorCode approveAdjustment(Adjustment adjustment, Long userid);

	ErrorCode approveAdjustment(Adjustment adjustment, MemberAccount account, Long userid);

	/**
	 * ����һ���˺�
	 * 
	 * @param member
	 * @return
	 */
	MemberAccount createNewAccount(Member member);

	/**
	 * ����tagȡgoods����
	 * 
	 * @param tag
	 * @return
	 */
	Integer getGoodsCountByTag(String tag);

	Member getMemberByMobile(String mobile);

	/**
	 * ���ݽ����֤�˻�
	 * 
	 * @param memberid
	 * @param amount
	 * @return
	 */
	public ErrorCode validateAccount(Long memberid, Integer amount);

	public ErrorCode validateAccount(Long memberid, String password);

	public ErrorCode validateAccount(Long memberid, Integer amount, String password);

	/**
	 * �ж��Ƿ��֧����ʽ
	 * 
	 * @param order
	 * @return
	 */
	ErrorCode isAllowChangePaymethod(GewaOrder order, String newpaymethod, String newpaybank);

	AccountRecord getAccountRecord(Long checkid, Long memberid);

	/**
	 * ��ѯ��ǰ���е��Ż���Ϣ
	 * 
	 * @param tag
	 * @param gewaprice
	 * @param time
	 * @param pricegap
	 * @return
	 */
	List<SpecialDiscount> getSpecialDiscountList(String tag, String opentype);

	List<SpecialDiscount> getPartnerSpecialDiscountList(String tag, Long partnerid);
	List<SpecialDiscount> getMobileSpecialDiscountList(String tag, Long partnerid);

	int getPaidOrderCount(Timestamp starttime, Timestamp endtime);

	ErrorCode addSpdiscountCharge(GewaOrder order, SpecialDiscount sd, Long userid, boolean isSupplement);

	/**
	 * �ؼۻ���ڴ�������Dȯ����
	 * 
	 * @return
	 */
	ErrorCode addSpdiscountCard(GewaOrder order, SpecialDiscount sd, Long userid, boolean isSupplement);

	ErrorCode removeSpdiscountCharge(GewaOrder order, SpecialDiscount sd, Long userid);

	/**
	 * ��֤�Ƿ��������ѵ�
	 * @param order
	 * @return
	 */
	ErrorCode validUse(GewaOrder order);

	/**
	 * �ֻ���̬���޸�֧������
	 * 
	 * @param member
	 *           �û�
	 * @param password
	 *           ֧������
	 * @param repassword
	 *           ȷ��֧������
	 * @param checkpass
	 *           ��̬��
	 * @param checkcount
	 *           ��֤����
	 * @return map
	 */
	ErrorCode<Map> mobileResetAccountPass(Member member, String password, String repassword, String checkpass);

	/**
	 * @param order
	 * @param clientIp
	 * @return
	 */
	ErrorCode<Map<String, String>> getNetPayParamsV2(GewaOrder order, String clientIp, String version);

	Map<String, String> getNetChargeParamsV2(Charge charge, String clientIp, String version);

	/**
	 * ��ʼ�����Ƶ�֧����ʽ
	 * 
	 * @return
	 */
	List<String> getLimitPayList();

	void reInitLimitPayList();

	/**
	 * �󶨵�֧����ʽ
	 * 
	 * @param discountList
	 * @param orderOtherinfo
	 * @param order
	 * @return
	 */
	String getBindPay(List<Discount> discountList, Map<String, String> orderOtherinfo, GewaOrder order);

	ErrorCode<Integer> getSpdiscountAmount(SpecialDiscountHelper sdh, GewaOrder order, SpecialDiscount sd,
			Spcounter spcounter, PayValidHelper pvh);

	Spcounter getSpdiscountCounter(SpecialDiscount sd);

	Spcounter updateSpdiscountMemberCount(SpecialDiscount sd, GewaOrder order) throws OrderException;

	void updateSpdiscountPaidCount(SpecialDiscount sd, GewaOrder order);
	/**
	 * �µ���������
	 * @param sd
	 * @param spcounter
	 * @param order
	 */
	void updateSpdiscountAddCount(SpecialDiscount sd, Spcounter spcounter, GewaOrder order);
	ErrorCode restoreSdCounterBySpcounter(Long spcounterid);

	/**
	 * �齱�Ϊ�н���Ա���߱�
	 * 
	 * @param drawActivityId
	 *           �齱�id
	 * @param member
	 *           ��Ա
	 * @param totalfee
	 *           �����߱ҽ��
	 * @return
	 */
	ErrorCode<?> addWaiBiByDrawActivity(String drawActivityId, Member member, Integer totalfee);

	/**
	 * ��֤�߱Һ��˻����֧��
	 * 
	 * @param member
	 * @param account
	 * @param payPass
	 * @param wbpay
	 * @return
	 */
	ErrorCode validateWbPay(Member member, MemberAccount account, String payPass, String wbpay);

	/**
	 * ���㣬���г�ֵ
	 * 
	 * @param member
	 * @param account
	 * @param order
	 * @param chargeMethod
	 * @return
	 */
	Charge addChargeByOrder(Member member, MemberAccount account, GewaOrder order, String chargeMethod);

	/**
	 * ���ڼ�������λ
	 * @param spcounter
	 * @param userid
	 */
	void resetSpcounter(Spcounter spcounter, Long userid);

	ErrorCode usePayServer(String paymethod, Map<String, String> params, String clientIp, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws Exception;

	ErrorCode usePayServer(GewaOrder order, String clientIp, Map paramsData, String version, HttpServletRequest request, ModelMap model);
	ErrorCode usePayServer(Charge charge, String clientIp, Map paramsData, String version, HttpServletRequest request, ModelMap model);
	/**
	 * ���������˿�
	 * 
	 * @param refund
	 * @param order
	 * @param userid
	 * @return
	 */
	ErrorCode refundSupplementOrder(OrderRefund refund, GewaOrder order, Long userid);

	/**
	 * �˻����ת��Ϊ�߱�
	 * 
	 * @param account
	 * @param bank
	 * @return ���ͻ���
	 */
	ErrorCode<Integer> bankToWaBi(MemberAccount account, Integer bank);

	/**
	 * ��ȡ�����б�
	 * 
	 * @param type
	 * @return
	 */
	List<PayBank> getPayBankList(String type);

	ErrorCode<Double> checkAddpoint(Charge charge);
	String getDecryptIdcard(String encryptIdCard);
	String getEncryptIdcard(String idCard);

	List<Charge> getChargeList(Long memberid, String status, String chargeto);
	
	List<MemberAccount> encryAccounts();
	
	
	Integer anlyEncryAccounts();
	
	Long encryIDCard(Long maxid);
	
	/**
	 * �����ؼۻID��ѯ������
	 * @param spids
	 * @return
	 */
	List<Spcounter> getSpcounterBySpids(List<Long> spids);

	List<Discount> getOrderDiscountList(GewaOrder order);

	String getGewaPayPrikey();

	ErrorCode<MemberAccount> createOrUpdateAccount(Member member, String realname, String password, String confirmPassword, String idcard);

	ErrorCode<MemberAccount> updateAccountPassword(Member member, String oldPassword, String password, String confirmPassword);
}