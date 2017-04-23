package com.gewara.service.api;

import java.util.List;

import com.gewara.model.sport.OpenTimeItem;
import com.gewara.model.sport.SportField;
import com.gewara.support.ErrorCode;
import com.gewara.xmlbind.sport.GstOtt;
import com.gewara.xmlbind.sport.GstSportField;


public interface ApiSportService {	
	/**
	 * ͬ�����ػ�����Ϣ
	 * @param sfList
	 */
	void addSportField(List<GstSportField> gstSportFieldList);

	/**
	 * ͬ������
	 * @param gott
	 */
	ErrorCode<List<OpenTimeItem>> saveSportTimeTable(GstOtt gott);
	/**
	 * �޸ĳ���
	 * @param rott
	 */
	void modSportTimeTable(GstOtt rott);
	SportField getSportField(Long sportid, Long itemid, String fieldname);
}
	
