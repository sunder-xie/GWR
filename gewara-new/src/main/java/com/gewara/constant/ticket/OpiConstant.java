/**
 * 
 */
package com.gewara.constant.ticket;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.gewara.model.movie.MoviePlayItem;
import com.gewara.model.movie.MoviePrice;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.support.ErrorCode;
import com.gewara.util.DateUtil;
import com.gewara.util.StringUtil;
public abstract class OpiConstant {
	public static final String STATUS_BOOK = "Y";			//����Ԥ��
	public static final String STATUS_NOBOOK = "N";			//������Ԥ��
	public static final String STATUS_RECOVER = "R";		//��ɾ��״̬�ָ�����Ҫ����
	public static final String STATUS_DISCARD = "D";		//����
	public static final String STATUS_CLOSE = "C";			//����״̬	
	public static final String STATUS_PAST = "P";			//���ι���

	
	public static final String PARTNER_OPEN = "Y";			//���⿪��
	public static final String PARTNER_CLOSE = "N";			//�����⿪��
	public static final String OPEN_GEWARA = "GEWA";		//�������ͣ�������˶Խ�
	public static final String OPEN_HFH = "HFH";			//�������ͣ�����˶Խ�
	public static final String OPEN_MTX = "MTX";			//�������ͣ��������ǶԽ�
	public static final String OPEN_DX = "DX";				//�������ͣ��붦�¶Խ�
	public static final String OPEN_WD = "WD";				//�������ͣ������Խ�
	public static final String OPEN_VISTA = "VISTA";		//�������ͣ���Vista�Խ�
	public static final String OPEN_PNX = "PNX";			//�������ͣ��붫Ʊ�Խ�
	public static final String OPEN_JY = "JY";				//�������ͣ���Vista�Խ�
	public static final String OPEN_DADI = "DADI";			//�������Ժ��
	
	public static final List<String> OPEN_LOWEST_IS_COST = Arrays.asList(
			OpiConstant.OPEN_DADI, OpiConstant.OPEN_WD, OpiConstant.OPEN_VISTA,OpiConstant.OPEN_JY);//��ͼۼ�����۵�����

	
	public static final String OPERATION_DISCARD = "discard";//��������

	public static final String PAYOPTION = "payoption";						//֧��ѡ��
	public static final String PAYCMETHODLIST = "paymethodlist";			//֧������
	public static final String CARDOPTION = "cardoption";					//ȯѡ��
	public static final String BATCHIDLIST = "batchidlist";					//����id����
	public static final String DEFAULTPAYMETHOD = "defaultpaymethod";		//Ĭ��֧����ʽ
	public static final String MEALOPTION = "mealoption";					//�ײ���
	public static final String ISREFUND = "isRefund";						//�Ƿ������Ʊ
	public static final String AUTO_OPEN_INFO = "autoOpen";					//�Զ�����
	public static final String AUTO_OPEN_INFO_STATUS = "autoOpenStatus";	//�Զ���������״̬���ֶ������Զ���
	public static final String SMPNO = "smpno";								//�ض��ĳ��α��
	public static final String LYMOVIEIDS = "lymovieids";					//��ӳ���γ���ids
	
	public static final String FROM_SPID = "fromSpid";						//��ĳ�������µĶ���
	public static final String ADDRESS = "address";							//��ַ����
	public static final String UNOPENGEWA = "unopengewa";					//���β��Ը���������
	public static final String UNSHOWGEWA = "unshowgewa";					//���β��Ը�������ʾ
	public static final String STATISTICS = "statistics";					//��λ��ͳ��
	public static final String SEATYPE = "seattype";						//��λ�Ƿ��е����۸�
	
	public static final String MPI_OPENSTATUS_INIT = "init";
	public static final String MPI_OPENSTATUS_OPEN = "open";
	public static final String MPI_OPENSTATUS_CLOSE = "close";
	public static final String MPI_OPENSTATUS_DISABLED = "disabled";		//
	public static final String MPI_OPENSTATUS_PAST = "past";				//����

	//��λͼˢ��Ƶ��
	public static final int SECONDS_SHOW_SEAT = 900;		//��ʾ��λͼ��20����
	public static final int SECONDS_ADDORDER = 300;		//�µ���5����
	public static final int SECONDS_UPDATE_SEAT = 60;		//���£�1����
	public static final int SECONDS_FORCEUPDATE_SEAT = 10;		//���£�10��
	
	public static final int MAX_MINUTS_TICKETS = 15;		//��ӰƱ���������ʱ�䣨���ӣ�
	public static final int MAX_MINUTS_TICKETS_MTX = 10; 	//��������λ����ʱ��
	public static final int MAX_MINUTS_TICKETS_PNX = 5;		//Ʊ��ϵͳ��λ����ʱ��
	
	public static final int MAXSEAT_PER_ORDER = 5;			//���������
	public static final int MAXSEAT_PER_ORDER_PNX = 4;		//�������������Ʊ�������ǣ�
	
	
	public static final List<String> EDITIONS = Arrays.asList("2D","3D","IMAX2D","IMAX3D","˫��3D","��Ļ2D","��Ļ3D", "4D");
	public static final List<String> EDITIONS_3D = Arrays.asList("3D", "IMAX3D", "˫��3D","��Ļ3D", "4D");
	public static final List<String> LANGUAGES = Arrays.asList(
			"����","Ӣ��","����","����","����","���¿���","��������","����","����","����",
			"̩��","�������","ӡ����","��������","ϣ����","��˹��","������","������",
			"������","��������","������","��������","ӡ����","�ڿ�����","��������","������",
			"Խ����","������","������","���ϻ�","������","ԭ��"/*TODO: remove*/);
	public static boolean isValidEdition(String edition){
		return StringUtils.isNotBlank(edition) && EDITIONS.contains(edition);
	}
	
	public static final Map<String, String> partnerTextMap;
	public static final Map<String, String> partnerFlagMap;
	public static final Map<String, String> takemethodMap;
	static{
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put(OPEN_HFH, "����");
		tmp.put(OPEN_MTX, "������");
		tmp.put(OPEN_DX, "����");
		tmp.put(OPEN_WD, "���");
		tmp.put(OPEN_VISTA, "Vista");
		tmp.put(OPEN_GEWARA, "������");
		tmp.put(OPEN_PNX, "��Ʊ");
		tmp.put(OPEN_JY, "����");
		tmp.put(OPEN_DADI, "���");
		partnerTextMap = MapUtils.unmodifiableMap(tmp);
		Map<String, String> tmpFlag = new HashMap<String, String>();
		tmpFlag.put(OPEN_HFH, "H");
		tmpFlag.put(OPEN_MTX, "M");
		tmpFlag.put(OPEN_DX, "DX");
		tmpFlag.put(OPEN_WD, "W");
		tmpFlag.put(OPEN_VISTA, "V");
		tmpFlag.put(OPEN_GEWARA, "G");
		tmpFlag.put(OPEN_PNX, "P");
		tmpFlag.put(OPEN_JY, "J");
		tmpFlag.put(OPEN_DADI, "DD");
		partnerFlagMap = MapUtils.unmodifiableMap(tmpFlag);
		Map<String, String> tmpTakemethod = new LinkedHashMap<String, String>();
		tmpTakemethod.put("P", "�ֳ�����");
		tmpTakemethod.put("W", "ӰԺ��Ʊ����");
		tmpTakemethod.put("A", "������ȡƱ��");
		tmpTakemethod.put("U", "����Ժ������ȡƱ��");
		tmpTakemethod.put("L", "¬�װ�ӰԺ����ȡƱ��");
		tmpTakemethod.put("D", "���Ժ������ȡƱ��");
		tmpTakemethod.put("J", "����Ժ������ȡƱ��");
		tmpTakemethod.put("M", "ӰԺ��Ա����ȡƱ��");
		takemethodMap = MapUtils.unmodifiableMap(tmpTakemethod);
	}
	
	public static boolean hasPartner(String opentype){
		if(StringUtils.equals(OPEN_GEWARA, opentype)) return false;
		if(StringUtils.isBlank(partnerTextMap.get(opentype))) return false;
		return true;
	}
	
	public static String getParnterText(String opentype){
		if(StringUtils.isBlank(opentype)) return "";
		String tmpText = partnerTextMap.get(opentype);
		if(StringUtils.isNotBlank(tmpText)) return tmpText;
		return "δ֪";
	}
	public static String getStatusStr(OpenPlayItem opi){
		Timestamp curtime = new Timestamp(System.currentTimeMillis());
		if(opi.getPlaytime().before(curtime)) return "�������Ѿ���ʱ";

		String time = DateUtil.format(curtime, "HHmm");
		boolean open = opi.getOpentime().before(curtime) && opi.getClosetime().after(curtime) 
				&& opi.getStatus().equals(OpiConstant.STATUS_BOOK) 
				&& StringUtil.between(time, opi.getDayotime(), opi.getDayctime())
				&& opi.getGsellnum() < opi.getAsellnum();
		if(open) return "��Ʊ��";
		if(!opi.getStatus().equals(OpiConstant.STATUS_BOOK)) return "�������ݲ����Ŷ�Ʊ";
		if(opi.getOpentime().after(curtime)) return "������" + DateUtil.formatTimestamp(opi.getOpentime()) + "���Ŷ�Ʊ";
		if(opi.getClosetime().before(curtime)) return "�������ѹرն�Ʊ";
		if(opi.getGsellnum() >= opi.getAsellnum()) return "��������λ������";
		if(!StringUtil.between(time, opi.getDayotime(), opi.getDayctime())) 
			return "������ֻ��ÿ��" + opi.getDayotime().substring(0,2) + ":" + opi.getDayotime().substring(2,4) + 
				"~" + opi.getDayctime().substring(0,2) + ":" + opi.getDayctime().substring(2,4) + "����";
		return "δ֪";
	}
	public static String getUnbookingReason(OpenPlayItem opi){
		if(opi == null) return "���ų��β����ڣ�";
		if(opi.isOrder()) return "";
		Timestamp curtime = new Timestamp(System.currentTimeMillis());
		if(opi.getPlaytime().before(curtime)) return "�������Ѿ���ʱ";
		String time = DateUtil.format(curtime, "HHmm");
		if(!opi.getStatus().equals(OpiConstant.STATUS_BOOK)) return "�������ݲ����Ŷ�Ʊ";
		if(opi.getOpentime().after(curtime)) return "������" + DateUtil.formatTimestamp(opi.getOpentime()) + "���Ŷ�Ʊ";
		if(opi.getClosetime().before(curtime)) return "�������ѹرն�Ʊ";
		if(opi.getGsellnum() >= opi.getAsellnum()) return "��������λ������";
		if(!StringUtil.between(time, opi.getDayotime(), opi.getDayctime())) 
			return "������ֻ��ÿ��" + opi.getDayotime().substring(0,2) + ":" + opi.getDayotime().substring(2,4) + 
				"~" + opi.getDayctime().substring(0,2) + ":" + opi.getDayctime().substring(2,4) + "����";
		return "δ֪";
	}
	/**
	 * ����Ƭ�Ƚϣ� �����Ƿ��в���
	 * @param mpi
	 * @return
	 */
	public static String getDifferent(OpenPlayItem opi, MoviePlayItem mpi){
		String msg = "";
		if(!opi.getMovieid().equals(mpi.getMovieid())){
			msg += ",movie:" + opi.getMovieid() + "--->" + mpi.getMovieid(); 
		}
		if(!opi.getRoomid().equals(mpi.getRoomid())){
			msg += ",room:" + opi.getRoomid() + "--->" + mpi.getRoomid(); 
		}
		if(!StringUtils.equals(opi.getRoomname(), mpi.getPlayroom())){
			msg += ",roomname:" + opi.getRoomname() + "--->" + mpi.getPlayroom(); 
		}
		String oplaydate = DateUtil.formatDate(opi.getPlaytime());
		String mplaydate = DateUtil.formatDate(mpi.getPlaydate());
		if(!oplaydate.equals(mplaydate)){
			msg += ",date:" + oplaydate + "--->" + mplaydate; 
		}
		if(!opi.getTimeStr().equals(mpi.getPlaytime())){
			msg += ",time:" + opi.getTimeStr() + "--->" + mpi.getPlaytime(); 
		}
		if(!StringUtils.equals(opi.getLanguage(), mpi.getLanguage())){
			msg += ",language:" + opi.getLanguage() + "--->" + mpi.getLanguage(); 
		}
		if(!StringUtils.equals(opi.getEdition(), mpi.getEdition())){
			msg += ",edition:" + opi.getEdition() + "--->" + mpi.getEdition(); 
		}
		if(opi.getPrice() == null || mpi.getPrice()==null){
			msg += ",price��" + opi.getPrice() + "--->" + mpi.getPrice();
		}else if(!opi.getPrice().equals(mpi.getPrice())){
			msg += ",price��" + opi.getPrice() + "--->" + mpi.getPrice();
		}
		if(opi.getLowest() != null && mpi.getLowest()!=null && !opi.getLowest().equals(mpi.getLowest())){
			msg += ",lowest��" + opi.getLowest() + "--->" + mpi.getLowest();
		}

		if(!opi.hasGewara()){
			if(!StringUtils.equals(opi.getSeqNo(), mpi.getSeqNo())){
				msg += "seqNo��" + opi.getSeqNo() + "--->" + mpi.getSeqNo();
			}
		}
		if(msg.length()>0) msg = msg.substring(1);
		return msg;
	}
	public static String getFullDesc(OpenPlayItem opi){
		return opi.getCinemaname() + " " + opi.getRoomname() + " " + opi.getMoviename() + DateUtil.format(opi.getPlaytime(), "MM��dd�� HH:mm") + " " + opi.getGewaprice();
	}
	/**
	 * ��֤��ӳ�汾��Ӱ�����Ƿ�ƥ��
	 * @param roomPlaytype
	 * @param opiEdition
	 * @return
	 */
	public static String validateRoomPlaytype(String roomPlaytype, String opiEdition){
		if(StringUtils.isBlank(roomPlaytype)) return "";
		if(opiEdition.equals("3D")){
			if(roomPlaytype.equals("3D")) return "";
		}else if(opiEdition.equals("IMAX")){
			if(roomPlaytype.equals("IMAX")) return "";
		}else{//2D
			if(roomPlaytype.equals("2D")) return "";
		}
		return "���κ�Ӱ����ӳ�汾��ƥ�䣺" + roomPlaytype + "<---->" + opiEdition;
	}
	/**
	 * ���ص���Ƭ���Ӱ���ĳ����Զ�ת��Ϊ��Ӧ������
	 * ����дΪ˫��3D��ֻҪ���ص��ĳ���Ϊ3D���Զ�ת��Ϊ˫��3D�����ص�λ2D���β�ת����
	 * �磺��Ļ2D����Ļ3D�����ص���2D�����Զ�ת��Ϊ��Ļ2D��3D�����Զ�ת��Ϊ��Ļ3D��
	 * @param synchEdition
	 * @param roomDefaultEdition
	 * @return
	 */
	public static String getDefaultEdition(String synchEdition,String roomDefaultEdition){
		if(StringUtils.isBlank(synchEdition) || StringUtils.isBlank(roomDefaultEdition)){
			return synchEdition;
		}
		String defaults[] = StringUtils.split(roomDefaultEdition, ",");
		for(String defaultEdition : defaults){
			if(defaultEdition.contains(synchEdition)){
				return defaultEdition;
			}
		}
		return synchEdition;
	}
	
	public static ErrorCode<Integer> getLowerPrice(String edition/*opi edition*/, MoviePrice mp,Timestamp playTime){
		if(StringUtils.isBlank(edition)){
			return ErrorCode.getFailure(mp.getMovieid() + "ӰƬδ�������Ʊ��");
		}
		if(mp.getStartTime() != null && mp.getEndTime() != null && mp.getStartTime().before(playTime) && mp.getEndTime().after(playTime)){
			if(EDITIONS_3D.contains(edition)){
				if(mp.getRangeEdition3D() == null) return ErrorCode.getFailure("��������3D�۸���Ϊ�գ�");
				return ErrorCode.getSuccessReturn(mp.getRangeEdition3D());
			}else if(StringUtils.indexOf(edition, "��Ļ") != -1){
				if(mp.getRangeEditionJumu() == null) return ErrorCode.getFailure("�������ݾ�Ļ�۸���Ϊ�գ�");
				return ErrorCode.getSuccessReturn(mp.getRangeEditionJumu());
			}else if(StringUtils.equals(edition, "IMAX")){
				if(mp.getRangeEditionIMAX() == null) return ErrorCode.getFailure("��������IMAX�۸���Ϊ�գ�");
				return ErrorCode.getSuccessReturn(mp.getRangeEditionIMAX());
			}
			return ErrorCode.getSuccessReturn(mp.getRangePrice());
		}else{
			if(EDITIONS_3D.contains(edition)){
				if(mp.getEdition3D() == null) return ErrorCode.getFailure("��������3D�۸���Ϊ�գ�");
				return ErrorCode.getSuccessReturn(mp.getEdition3D());
			}else if(StringUtils.indexOf(edition, "��Ļ") != -1){
				if(mp.getEditionJumu() == null) return ErrorCode.getFailure("�������ݾ�Ļ�۸���Ϊ�գ�");
				return ErrorCode.getSuccessReturn(mp.getEditionJumu());
			}else if(StringUtils.equals(edition, "IMAX")){
				if(mp.getEditionIMAX() == null) return ErrorCode.getFailure("��������IMAX�۸���Ϊ�գ�");
				return ErrorCode.getSuccessReturn(mp.getEditionIMAX());
			}
			return ErrorCode.getSuccessReturn(mp.getPrice());
		}
	}
	//������¼���һ�θ���
	public static String getLastChangeKey(Long mpid) {
		return "LastChange" + mpid;
	}
}
