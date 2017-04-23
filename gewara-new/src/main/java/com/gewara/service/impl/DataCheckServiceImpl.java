package com.gewara.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gewara.service.DataCheckService;


@Service("dataCheckService")
public class DataCheckServiceImpl extends BaseServiceImpl implements DataCheckService{
	/**
	 * @param checkitem:Ҫ������Ŀ����moviename��cinemaname��name��
	 * @param name����Ŀ�����ƣ���������Ӱ�ǵ�
	 * @return
	 */
	private static Map<String,String> itemsMap = new HashMap<String,String>();
	static{
		itemsMap.put("moviename", "Movie");
		itemsMap.put("cinemaname", "Cinema");
		itemsMap.put("ktvname", "Ktv");
		itemsMap.put("barname", "Bar");
		itemsMap.put("gymname", "Gym");
		itemsMap.put("sportname", "Sport");
	}
	public boolean checkname(String checkItem, String name){
		String query = "from " + itemsMap.get(checkItem) + " t where t." + checkItem + " = ?";
		List result = this.hibernateTemplate.find(query, name);
		if(result!=null && result.size() > 0 ) return true;
		return false;
	}
}
