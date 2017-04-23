package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.gewara.commons.sign.Sign;
import com.gewara.util.DateUtil;
import com.gewara.util.HttpResult;
import com.gewara.util.HttpUtils;
import com.gewara.util.JsonUtils;
import com.gewara.xmlbind.drama.gptbs.PlaceField;
import com.gewara.xmlbind.drama.gptbs.Program;
import com.gewara.xmlbind.drama.gptbs.Schedule;
import com.gewara.xmlbind.drama.gptbs.ScheduleArea;
import com.gewara.xmlbind.drama.gptbs.ScheduleSeat;
import com.gewara.xmlbind.drama.gptbs.Stadium;
public class GptbsApiTest {
	//private static final String URL = "http://gptbs.gewala.net/gptbs";
	public static final String appkey = "gewa_gptbs";
	public static final String secretcode = "deWZrs4ELxbeRBHLuRBM8fiBHCUh8Cxg";
	public static final String domain = "http://test.gewala.net/openapi/router/rest";
	public static final String username = "shunyi";
	public static final String password = "123456";
	public static void main(String[] args) {
		//testStadium();
		//fieldList();
		//fieldDetail();
		///programList();
	}
	//���Գ���
	public static void testStadium(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.stadium.stadiumDetail");
		params.put("id","1448");
		getSignMap(params);
		
		Stadium v = getObjFromUrl(params,Stadium.class);
		if(v!=null)System.out.println(v.getCnName());
	}
	//�����б�
	public static void testStadiumList(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.stadium.stadiumList");
		getSignMap(params);
		
		List<Stadium> programList = getListFromUrl(params,Stadium.class);
		for(Stadium sch : programList){
			System.out.println(sch.getId()+","+sch.getCnName());
		}
	}
	//��ȡ�����û����۳��εĳ���
	public static void testCanBookingStadiumList(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.ticket.stadiumList");
		getSignMap(params);
		
		List<Stadium> programList = getListFromUrl(params,Stadium.class);
		for(Stadium sch : programList){
			System.out.println(sch.getId()+","+sch.getCnName());
		}
	}
	
	//���ݳ����б�
	public static void fieldList(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.stadium.venueList");
		params.put("stadiumId","1448");
		getSignMap(params);
		
		List<PlaceField> fieldList = getListFromUrl(params,PlaceField.class);
		for(PlaceField field : fieldList){
			System.out.println(field.getId()+", "+field.getCnName()+", " + field.getBackground());
		}
	}
	
	//���ݳ�����ϸ
	public static void fieldDetail(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.stadium.venueDetail");
		params.put("id","622");
		getSignMap(params);
		
		PlaceField v = getObjFromUrl(params,PlaceField.class);
		if(v!=null)System.out.println(v.getId()+", "+v.getCnName());
	}

	//���ݳ���id��ȡ�����û����۳��ε���Ŀ
	public static void testProgramList(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.ticket.programList");
		params.put("venueId","622");
		getSignMap(params);
		
		List<Program> programList = getListFromUrl(params,Program.class);
		for(Program program : programList){
			System.out.println(program.getId() + ", " + program.getCnName()+","+program.getStartTime());
		}
	}
	
	
	//���ݳ���id��ȡ�����û����۳��ε���Ŀ
	public static void testScheduleList(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.ticket.scheduleList");
		params.put("programId","3199");
		getSignMap(params);
		
		List<Schedule> programList = getListFromUrl(params,Schedule.class);
		for(Schedule sch : programList){
			System.out.println(sch.getId()+","+sch.getCnName()+","+sch.getPlayTime());
		}
	}
	
	//���ݳ���id���س�����ϸ
	public static void testScheduleDetail(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.schedule.scheduleDetail");
		params.put("id","2639");
		getSignMap(params);
		
		Schedule v = getObjFromUrl(params,Schedule.class);
		if(v!=null)System.out.println(v.getId()+", "+v.getCnName()+", " + v.getVenueId());
	}
	
	
	//���ݳ���id�ͳ���id��ȡ���������б�
	public static void testScheduleAreaList(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.schedule.scheduleAreaList");
		params.put("scheduleId","2639");
		params.put("venueId","622");
		getSignMap(params);
		
		List<ScheduleArea> areaList = getListFromUrl(params,ScheduleArea.class);
		for(ScheduleArea area : areaList){
			System.out.println(area.getId()+","+area.getCnName()+","+area.getGridHeight()+","+area.getGridWidth());
		}
	}
	//����id��ȡ��������
	public static void testScheduleAreaDetail(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.schedule.scheduleAreaDetail");
		params.put("id","2639");
		getSignMap(params);
		
		ScheduleArea v = getObjFromUrl(params,ScheduleArea.class);
		if(v!=null)System.out.println(v.getId()+", "+v.getCnName()+", " + v.getScheduleId());
	}
	
	//���ݳ���id,��������id������λ�б�
	public static void testAreaSeatList(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.schedule.areaSeatList");
		params.put("scheduleId","2639");
		params.put("scheduleVenueAreaId","2640");
		getSignMap(params);
		
		List<ScheduleSeat> seatList = getListFromUrl(params,ScheduleSeat.class);
		for(ScheduleSeat seat : seatList){
			System.out.println(seat.getId()+","+seat.getLineno()+","+seat.getRankno()+","+seat.getX()+","+seat.getY());
		}
	}
	
	//������Ʊ
	public static void testlockSeats(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.ticket.lockSeats");
		params.put("scheduleId","2639");
		params.put("seatIds","1186080");
		getSignMap(params);
		
		String v = getObjFromUrl(params,String.class);
		System.out.println(v);
	}
	//����������λ
	public static void testunlockSeats(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.ticket.unlock");
		params.put("scheduleId","2639");
		params.put("seatIds","1186080");
		getSignMap(params);
		
		String v = getObjFromUrl(params,String.class);
		System.out.println(v);
	}
	//Ԥ����λ
	public static void testpreOrder(){
		TreeMap<String,String> params = getCommonMap("com.gewara.gptbs.ticket.preOrder");
		params.put("scheduleId","2639");
		params.put("seatIds","1186080");
		params.put("discountJson","{}");
		params.put("ticketType","{}");
		params.put("showPrice","{}");
		params.put("orderNo","201305021100");
		getSignMap(params);
		
		String v = getObjFromUrl(params,String.class);
		System.out.println(v);
	}
	private static TreeMap<String, String> getCommonMap(String method){
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("method", method);
		params.put("appkey", appkey);
		params.put("username", username);
		params.put("password", password);
		params.put("callMethod","api");
		params.put("timestamp", DateUtil.getCurFullTimestampStr());
		params.put("format", "xml");
		params.put("v", "1.0");
		return params;
	}
	
	private static void getSignMap(TreeMap<String, String> params){
		String sign = Sign.signMD5(params, secretcode);
		params.put("signmethod", "MD5");
		params.put("sign", sign);
	}
	private static <T> T getObjFromUrl(Map<String,String> params,Class<T> clazz){
		T obj = null;
		HttpResult hr  = HttpUtils.getUrlAsString(domain, params);
		if (hr.isSuccess()) {
			String result = hr.getResponse();
			String code = JsonUtils.getJsonValueByKey(result, "code");
			String data = JsonUtils.writeObjectToJson(JsonUtils.readJsonToMap(result).get("data"));
			if("0000".equals(code)) {
				obj = JsonUtils.readJsonToObject(clazz,data);
			} else {
				System.out.println(result);
			}
		}
		return obj;
	}
	
	private static <T> List<T> getListFromUrl(Map<String,String> params,Class<T> clazz){
		List<T> rl = new ArrayList<T>();
		HttpResult hr  = HttpUtils.getUrlAsString(domain, params);
		if (hr.isSuccess()) {
			String result = hr.getResponse();
			String code = JsonUtils.getJsonValueByKey(result, "code");
			String data = JsonUtils.writeObjectToJson(JsonUtils.readJsonToMap(result).get("data"));
			if("0000".equals(code)){
				rl = JsonUtils.readJsonToObjectList(clazz,data);
			} else {
				System.out.println(result);
			}
		}
		return rl;
	}
	
}
