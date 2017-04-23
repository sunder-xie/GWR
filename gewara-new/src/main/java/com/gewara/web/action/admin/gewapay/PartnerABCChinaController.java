/**
 * 
 */
package com.gewara.web.action.admin.gewapay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.security.Key;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import com.gewara.constant.AdminCityContant;
import com.gewara.constant.ChargeConstant;
import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.SmsConstant;
import com.gewara.constant.sys.MongoData;
import com.gewara.model.pay.Charge;
import com.gewara.model.pay.MemberAccount;
import com.gewara.model.pay.SMSRecord;
import com.gewara.model.user.Member;
import com.gewara.mongo.MongoService;
import com.gewara.pay.PayUtil;
import com.gewara.service.gewapay.PaymentService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.UntransService;
import com.gewara.util.DateUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.action.admin.BaseAdminController;

@Controller
public class PartnerABCChinaController extends BaseAdminController{

	@Autowired@Qualifier("gewaMultipartResolver")
	private MultipartResolver gewaMultipartResolver;
	@Autowired@Qualifier("mongoService")
	private MongoService mongoService;
	@Autowired@Qualifier("untransService")
	private UntransService untransService;
	@Autowired@Qualifier("paymentService")
	private PaymentService paymentService;
	private String filename;
	
	@RequestMapping("/admin/gewapay/abc/userList.xhtml")
	public String abcUserList(){
		return "admin/gewapay/abc/abcUserList.vm";
	}
	
	@RequestMapping("/admin/gewapay/abc/decryptUserList.xhtml")
	public String decryptUserList(HttpServletRequest request,ModelMap model){
		String remoteip = WebUtils.getRemoteIp(request);
		dbLogger.warn("ũҵ�����ļ�����>>ip:"+remoteip+",userid:"+this.getLogonUser().getId());
		try {
			MultipartHttpServletRequest multipartRequest = gewaMultipartResolver.resolveMultipart(request);
			MultipartFile multipartFile = multipartRequest.getFileMap().get("abc_file");
			filename = multipartFile.getOriginalFilename().replace(".DSQ", "");
			String source = DESUtils.decrypt(multipartFile.getInputStream());
			source = source.replaceAll("\\|!", ",").replaceAll("\r\n", ";");
			String[] memberList = source.split(";");
			List<Map> userList = new ArrayList<Map>();
			int totalAmount = 0;
			Map member = null;
			for (int i =1; i < memberList.length ; i++) {
				member = this.getABCMember(memberList[i]);
				userList.add(this.getABCMember(memberList[i]));
				totalAmount += Integer.valueOf(member.get("amount")+"");
			}
			member = this.getABCMember(memberList[0]);
			int amount = Integer.valueOf(member.get("amount")+"");
			if(memberList.length -1 != Integer.valueOf(member.get("mobile")+""))model.put("msg", "��ֵ��¼����������ȣ�����ũҵ���к˶ԣ�");
			if(totalAmount != amount)model.put("msg", "�ܽ����ڳ�ֵ������ũҵ���к˶ԣ�");
			model.put("member", this.getABCMember(memberList[0]));
			model.put("userList", userList);
			model.put("source", source);
		} catch (IOException e) {
			dbLogger.warn("ABC DECRYPT ERROR",e);
		}
		return "admin/gewapay/abc/abcUserList.vm";
	}
	
	private Map getABCMember(String abcMenberstr){
		String[] abcMenber = abcMenberstr.split(",");
		Map member = new HashMap();
		member.put("id", abcMenber[0]);
		member.put("mobile", abcMenber[1]);
		float amount = Float.valueOf(abcMenber[2]);
		member.put("amount", (int)amount);
		return member;
	}
	
	@RequestMapping("/admin/gewapay/abc/confirmUserList.xhtml")
	public String confirmUserList(String source,ModelMap model, HttpServletRequest request){
		String remoteip = WebUtils.getRemoteIp(request);
		dbLogger.warn("ũҵ����������ֵ��ʼ>>ip:"+remoteip+",userid:"+this.getLogonUser().getId());
		String[] memberList = source.split(";");
		int totalAmount = 0;
		String msg = null;
		List<Map> userList = new ArrayList<Map>();
		StringBuilder sb = new StringBuilder();
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		String date = DateUtil.format(new Date(), "yyyy��MM��dd��");
		for (int i =1; i < memberList.length ; i++) {
			Map abcMember = this.getABCMember(memberList[i]);
			abcMember.put("flag", "1");
			int amount = Integer.valueOf(abcMember.get("amount")+"");
			if(!isAllowCharge(memberList[i])){
				dbLogger.warn("ũҵ���д�����Ѿ���ֵ����"+memberList[i]);
				userList.add(abcMember);
				totalAmount += amount;
				abcMember.put("flag", "0");
				sb.append(abcMember.get("id")+"|!"+abcMember.get("amount")+"|!"+abcMember.get("flag")+"\r\n");
				//+"|!������Ѿ���ֵ��,�ظ�����\r\n"
				continue;
			}
			userList.add(abcMember);
			String mobile = abcMember.get("mobile")+"";
			totalAmount += amount;
			Member member = this.daoService.getObjectByUkey(Member.class, "mobile", mobile, false);
			if(member == null){
				//�û������ڣ��������û�
				String mbPassword = StringUtils.rightPad("" + new Random().nextInt(99999999), 8, '0');
				ErrorCode<Member> result = memberService.regMemberWithMobile(mobile, mobile, mbPassword, null, "in", "ABCBANK", AdminCityContant.CITYCODE_SH, remoteip);
				if(result.isSuccess()){
					member = result.getRetval();
					paymentService.createNewAccount(member);
					//��ȡ��������
					msg = getMsgContent(mobile, date, amount, null, mbPassword);
					dbLogger.warn("ũҵ�����û������ɹ���"+memberList[i]);
				}else{
					dbLogger.warn("ũҵ�����û�����ʧ�ܣ�"+memberList[i]);
					sb.append(abcMember.get("id")+"|!"+abcMember.get("amount")+"|!"+abcMember.get("flag")+"|!"+result.getMsg()+"\r\n");
					continue;
				}
			}else{
				msg = getMsgContent(mobile, date, amount, null, null);
				dbLogger.warn(msg);
			}
			//�û����ڣ����û���ֵ
			if(member != null){
				Charge charge = new Charge(PayUtil.getChargeTradeNo(), ChargeConstant.WABIPAY);
				charge.setMemberid(member.getId());
				charge.setMembername(member.getNickname());
				charge.setPaymethod(PaymethodConstant.PAYMETHOD_ABCBANKPAY);
				charge.setTotalfee(amount);
				charge.setValidtime(DateUtil.addHour(charge.getAddtime(), 4));
				daoService.saveObject(charge);
				String payseqno = abcMember.get("id")+"";
				MemberAccount account = daoService.getObjectByUkey(MemberAccount.class, "memberid", charge.getMemberid(), false);
				if(account == null) paymentService.createNewAccount(member);
				ErrorCode<Charge> result = paymentService.bankPayCharge(charge.getTradeNo(), false, payseqno, amount, PaymethodConstant.PAYMETHOD_ABCBANKPAY, "xx", PaymethodConstant.PAYMETHOD_ABCBANKPAY,null);

				if(result.isSuccess()){
					dbLogger.warn("ũҵ���г�ֵ�ɹ���"+memberList[i]);
					this.saveABCCharge(memberList[i]);
					SMSRecord sms = new SMSRecord(null,"ABC"+DateUtil.format(cur, "yyyyMMddHHmmss"), abcMember.get("mobile")+"", msg,
							cur, DateUtil.addDay(cur, 2), SmsConstant.SMSTYPE_NOW);
					sms.setTradeNo(charge.getTradeNo());
					untransService.addMessage(sms);
				}else{
					dbLogger.warn("ũҵ���г�ֵʧ�ܣ�"+memberList[i]);
					sb.append(abcMember.get("id")+"|!"+abcMember.get("amount")+"|!"+abcMember.get("flag")+"|!"+result.getMsg()+"\r\n");
					continue;
				}
			}else{
				dbLogger.warn("ũҵ���г�ֵʧ�ܣ�"+memberList[i]);
				sb.append(abcMember.get("id")+"|!"+abcMember.get("amount")+"|!"+abcMember.get("flag")+"|!�޷������û�\r\n");
				continue;
			}
			//���Ͷ���
			abcMember.put("flag", "0");
			sb.append(abcMember.get("id")+"|!"+abcMember.get("amount")+"|!"+abcMember.get("flag")+"\r\n");
		}
		Map total =  this.getABCMember(memberList[0]);
		String result = total.get("id")+"|!"+userList.size()+"|!"+totalAmount+"\r\n";
		model.put("member", total);
		model.put("userList", userList);
		model.put("result", result+sb.toString());
		model.put("totalAmount", totalAmount);
		dbLogger.warn("ũҵ����������ֵ����>>ip:"+remoteip+",userid:"+this.getLogonUser().getId());
		return "admin/gewapay/abc/abcUserList.vm";
	}
	
	private void saveABCCharge(String abcMember){
		Map saveMember = new HashMap();
		saveMember.put(MongoData.SYSTEM_ID, MongoData.buildId());
		saveMember.put("mkey", abcMember);
		saveMember.put("addtime", DateUtil.formatTimestamp(System.currentTimeMillis()));
		mongoService.saveOrUpdateMap(saveMember, MongoData.SYSTEM_ID, MongoData.NS_ABC_CHARGE);
	}
	
	private boolean isAllowCharge(String abcMember){
		Map params = new HashMap();
		params.put("mkey", abcMember);
		int count = mongoService.getCount(MongoData.NS_ABC_CHARGE, params);
		return count == 0;
	}
	
	private String getMsgContent(String mobile, String date, int amount, String maPassword, String mbPassword){
		String msg = null;
		if(StringUtils.isNotBlank(mbPassword)){
			msg = "�𾴵�ũ���û������ɹ�ʹ��ũ�л��ֶһ��˸�����������"+amount+"���߱ң�" +
				"����Ϊ�������˸������ʺţ��û���Ϊ"+mobile+"���ֻ��ţ���" +
				"����Ϊ"+mbPassword+"��������ɣ��뾡���޸ģ�������������������www.gewara.com��ѯ��";
		}else if(StringUtils.isNotBlank(maPassword)){
			msg = "�𾴵��û������û���Ϊ"+mobile+"�ĸ������˻���" +date
					+"�յ���ֵ"+amount+"�߱ң���֧������Ϊ"+maPassword+"������������������www.gewara.com��ѯ��";
		}else{
			msg = "�𾴵�ũ���û������ɹ�ʹ��ũ�л��ֶһ��˸�����������"+amount+"���߱ң�" +
			"������Ϊ����ֵ���ʺ�"+mobile+"�С�����������������www.gewara.com��ѯ��";
		}
		return msg;
	}
	
	@RequestMapping("/admin/gewapay/abc/encryptResult.xhtml")
	public void encryptResult(String result,HttpServletResponse response, HttpServletRequest request){
		String remoteip = WebUtils.getRemoteIp(request);
		dbLogger.warn("ũҵ���г�ֵ�������>>ip:"+remoteip+",userid:"+this.getLogonUser().getId());
		try {
			response.setContentType("application/binary");
			response.setHeader("Content-Disposition", "attachment;"+"filename="+filename+".DJG");
			DESUtils.encrypt(result, response.getOutputStream());
		} catch (IOException e) {
			dbLogger.warn("ABC ENCRYPT ERROR",e);
		}
	}
	
}

class DESUtils {

	public static String decrypt(InputStream is) {
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, getDesKeyFromFile());
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			CipherOutputStream cos = new CipherOutputStream(os, cipher);
			byte[] buffer = new byte[1024];
			int i;
			while ((i = is.read(buffer)) > 0) {
				cos.write(buffer, 0, i);
			}
			cos.close();
			is.close();
			os.close();
			IOUtils.copyBytes(is, cos, 4096, false);
			return new String(os.toByteArray());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static void encrypt(String source, OutputStream os) {
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, getDesKeyFromFile());
			ByteArrayInputStream is = new ByteArrayInputStream(source.getBytes("UTF-8"));
			CipherInputStream cis = new CipherInputStream(is, cipher);
			byte[] buffer = new byte[1024];
			int i;
			while ((i = cis.read(buffer)) > 0) {
				os.write(buffer, 0, i);
			}
			cis.close();
			is.close();
			os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static Key getDesKeyFromFile() {
		Key key = null;
		try {
			InputStream is = DESUtils.class.getClassLoader().getResourceAsStream("com/gewara/pay/abchina_des.key");
			ObjectInputStream ois = new ObjectInputStream(is);
			key = (Key) ois.readObject();
			ois.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return key;
	}

	
}
