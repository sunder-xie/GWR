package com.gewara.model.common;

import java.io.Serializable;

import com.gewara.model.BaseObject;

/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public class Indexarea extends BaseObject {
	//1=�ޣ�2=���ң�3=ʡ��7=�У�8=���أ�10=��Ȧ
	private static final long serialVersionUID = -886944461240705718L;
	private String indexareacode;
	private County county;
	private String indexareaname;
	public String getIndexareacode() {
		return indexareacode;
	}

	public void setIndexareacode(String indexareacode) {
		this.indexareacode = indexareacode;
	}

	public String getIndexareaname() {
		return indexareaname;
	}

	public void setIndexareaname(String indexareaname) {
		this.indexareaname = indexareaname;
	}

	public void setCounty(County county) {
		this.county = county;
	}

	public Indexarea() {
	}

	public Indexarea(String indexareacode) {
		this.indexareacode = indexareacode;
	}

	public Indexarea(String indexareacode,County county,   
			String indexareaname){
		this.indexareacode = indexareacode;
		this.county = county;
		this.indexareaname = indexareaname;
	}

	public County getCounty() {
		return county;
	}
	public String getId() {
		return indexareacode;
	}
	@Override
	public Serializable realId() {
		return indexareacode;
	}

	public String getCode(){
		return indexareacode;
	}

	public String getName(){
		return indexareaname;
	}
}
