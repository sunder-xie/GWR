package com.gewara.web.action.admin.gewapay;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.UnmodifiableMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.util.HttpResult;
import com.gewara.util.HttpUtils;
import com.gewara.util.StringUtil;
import com.gewara.util.VmUtils;
import com.gewara.web.action.admin.BaseAdminController;
@Controller
public class QryLogController extends BaseAdminController{
	public static final transient Map<String, String> codeMap;
	static{
		Map<String, String> tmpMap = new HashMap<String, String>();
		tmpMap.put("pnr600", "���׳ɹ�");
		tmpMap.put("pnr601", "�汾�Ŵ���");
		tmpMap.put("pnr602", "�̻��Ÿ�ʽ����");
		tmpMap.put("pnr603", "�̻����ڸ�ʽ����");
		tmpMap.put("pnr604", "�����Ÿ�ʽ����");
		tmpMap.put("pnr605", "���׽���ʽ����");
		tmpMap.put("pnr606", "���غŸ�ʽ����");
		tmpMap.put("pnr607", "ǩ����Ϣ��ʽ����");
		tmpMap.put("pnr608", "���غ��ں�����");
		tmpMap.put("pnr609", "���ز��������б���");
		tmpMap.put("pnr610", "������С���޶�");
		tmpMap.put("pnr613", "ϵͳ����");
		tmpMap.put("pnr614", "�Ƿ��̻���");
		tmpMap.put("pnr615", "�̻����ѹر�");
		tmpMap.put("pnr616", "�Ƿ����غ�");
		tmpMap.put("pnr617", "���غ��ѹر�");
		tmpMap.put("pnr619", "�޶�Ӧԭʼ���׼�¼");
		tmpMap.put("pnr620", "ԭ����ʧ��");
		tmpMap.put("pnr621", "���׽�����");
		tmpMap.put("pnr628", "���ݲ�������");
		tmpMap.put("pnr629", "����״̬����");
		tmpMap.put("pnr631", "����Ϣ����");
		tmpMap.put("pnr632", "�ص���ַ����Ϊ��");
		tmpMap.put("pnr633", "��BIN����");
		tmpMap.put("pnr634", "��Ч�ڸ�ʽ����");
		tmpMap.put("pnr635", "CVV2��ʽ����");
		tmpMap.put("pnr636", "֤�����͸�ʽ����");
		tmpMap.put("pnr637", "֤�����Ͳ���Ϊ��");
		tmpMap.put("pnr638", "֤�������ʽ����");
		tmpMap.put("pnr639", "�������ȳ�������");
		tmpMap.put("pnr640", "��������Ϊ��");
		tmpMap.put("pnr641", "��ǩ��ʧ��");
		tmpMap.put("pnr642", "���ͻ���ս�������ʧ��");
		tmpMap.put("pnr643", "ԭʼ�����̻����ڸ�ʽ����");
		tmpMap.put("pnr644", "ԭʼ���׶����Ÿ�ʽ����");
		tmpMap.put("pnr645", "�������ʹ���");
		tmpMap.put("pnr646", "��������쳣");
		tmpMap.put("pnr647", "�ظ��˿�");
		tmpMap.put("pnr648", "ǩ��ʧ��");
		tmpMap.put("pnr649", "����Ϣ����ʧ��");
		tmpMap.put("pnr001", "֧����Ϣ����");
		tmpMap.put("pnr002", "��Ч���Ż��˻�");
		tmpMap.put("pnr003", "�������ò���");
		tmpMap.put("pnr004", "����Ч�ڴ���");
		tmpMap.put("pnr005", "����ȡ��");
		tmpMap.put("pnr006", "���ݽ��մ���");
		tmpMap.put("pnr007", "���׳�ʱ");
		tmpMap.put("pnr008", "���޶�");
		tmpMap.put("pnr009", "�Ǳ��п�");
		tmpMap.put("pnr010", "�绰��Ȩæ��");
		tmpMap.put("pnr011", "��Ȩ������ʹ�ô�������");
		tmpMap.put("pnr012", "����ʧ��");
		tmpMap.put("pnr013", "֤�����벻��");
		tmpMap.put("pnr014", "��������");
		tmpMap.put("pnr015", "�鷢����");
		tmpMap.put("pnr016", "��ЧCVV2");
		tmpMap.put("pnr017", "��Ч�̻�");
		tmpMap.put("pnr018", "���ڵĿ�");
		tmpMap.put("pnr019", "�ظ�����");
		tmpMap.put("pnr020", "��ʧ��");
		tmpMap.put("pnr021", "���Կ�");
		tmpMap.put("pnr022", "�ÿ�δ����");
		tmpMap.put("pnr023", "�ٿ�");
		tmpMap.put("pnr098", "ԭʼ����������ȱ����Ч�ڻ�ȱ�ٿ���");
		tmpMap.put("pnr099", "����");
		tmpMap.put("pnr688", "���н���ʧ��");
		tmpMap.put("pnr689", "�������᲻������");
		tmpMap.put("pnr6AO", "û�տ�");
		tmpMap.put("pnr6A1", "����ж�");
		tmpMap.put("pnr6A2", "��Ч����");
		tmpMap.put("pnr6A3", "��Ч���");
		tmpMap.put("pnr6A4", "�޴˷�����");
		tmpMap.put("pnr6A5", "�������ݻ��ʽ����");
		tmpMap.put("pnr6A6", "�����ƵĿ�");
		tmpMap.put("pnr6A7", "�޴˿����޴��˻�");
		tmpMap.put("pnr6A8", "�������򽻻����Ĳ��ܲ���");
		tmpMap.put("pnr6A9", "���ɽ���");
		tmpMap.put("pnr6AA", "����������");
		tmpMap.put("pnr6AB", "�������ڴ�����");
		tmpMap.put("pnr6AC", "������֧�ֵ�����");
		tmpMap.put("pnr6AD", "���������PIN������");
		tmpMap.put("pnr6AE", "����ȷ��PIN");
		tmpMap.put("pnr6AF", "����ȡ���������");
		tmpMap.put("pnr6AG", "���ڻ������м�������ʩ�Ҳ������޷��ﵽ");
		tmpMap.put("pnr6AH", "���������ղ���������Ӧ��");
		tmpMap.put("pnr6AI", "MACУ���");
		tmpMap.put("pnr6AJ", "������Ϣ���Ϸ�");
		tmpMap.put("pnr6AK", "֤�����벻��Ϊ��");
		tmpMap.put("pnr6AL", "���ղ��ܽ��в������ѳ���");
		tmpMap.put("pnr6AM", "���ղ��ܽ������ѳ���");
		tmpMap.put("pnr6AN", "�����˻���Ч��,�����˻�");
		tmpMap.put("pnr6AP", "ͬһ�ղ��ܽ��и����˻�����");
		tmpMap.put("pnr6AQ", "���벻��ȷ");
		tmpMap.put("pnr6AR", "��ַ��ʽ����");
		tmpMap.put("pnr6AS", "�������ڲ���ȷ");
		tmpMap.put("pnr6AT", "�ۼ��˿����ԭ���׽��");
		tmpMap.put("pnr6AU", "�˻���״̬������");
		tmpMap.put("pnr6AV", "ֹ����");
		tmpMap.put("pnr6AW", "�Ѿ�����,���������");
		tmpMap.put("pnr6AX", "���������뽻��");
		tmpMap.put("pnr6AY", "����ϵ�յ����ֹ��˻�");
		tmpMap.put("pnr6AZ", "��֧�ָÿ���");
		codeMap = UnmodifiableMap.decorate(tmpMap);
	}
	//TODO:�����־д��HBase
	@RequestMapping("/admin/common/qryLog.xhtml")
	public String qryLog(String tradeno, ModelMap model){
		String url1 = "http://180.153.146.139:8080/log/domain2.log";
		String url2 = "http://180.153.146.140:8080/log/domain2.log";
		List<String> resList = new ArrayList<String>();
		resList.addAll(qryLogList(tradeno, url1));
		resList.addAll(qryLogList(tradeno, url2));
		return forwardMessage(model, resList);
	}
	private List<String> qryLogList(String tradeno, String url){
		HttpResult code =  HttpUtils.postUrlAsString(url, null);
		List<String> resList = new ArrayList<String>();
		if(code.isSuccess()){
			String result = code.getResponse();
			String reg = "\"OrdId\":\""+tradeno+"\",\"Pid\":\"\",\"RespCode\":\"pnr(.*?)\"";
			List<String> strList = StringUtil.findByRegex(result, reg, false);
			for(String str : strList){
				String json = "{" + str + "}";
				Map<String, String> tmp = VmUtils.readJsonToMap(json);
				String response = "�����ţ�"+tmp.get("OrdId");
				response = response + ", ʧ��ԭ��" + codeMap.get(tmp.get("RespCode"));
				resList.add(response);
			}
		}
		return resList;
	}
}
