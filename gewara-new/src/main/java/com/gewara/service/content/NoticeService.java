package com.gewara.service.content;

import java.util.List;

import com.gewara.model.content.Notice;


public interface NoticeService {

	//���ݹ���id��tag��ѯ������Ϣ�б�
	List<Notice> getNoticeListByCommuid(Long relateid,String tag,int from,int maxnum);
	//���ݹ���id��tag��ѯ������Ϣ����
	Integer getNoticeCountByCount(Long relatedid,String tag);


}
