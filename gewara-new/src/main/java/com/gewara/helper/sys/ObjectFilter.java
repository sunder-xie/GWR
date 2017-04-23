package com.gewara.helper.sys;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectFilter<T>{
	/**
	 * ���˵�itemList�в����õĶ��󣬷�����˱����˵��Ķ���
	 * @param removeItemList
	 */
	public void applyFilter(List<T> itemList){
		if(!hasFilter()) return;
		List<T> removeList = new ArrayList<T>();
		for(T item:itemList){
			if(excludeOpi(item)) removeList.add(item);
		}
		itemList.removeAll(removeList);
	}
	public abstract boolean hasFilter();
	public abstract boolean excludeOpi(T item);
}
