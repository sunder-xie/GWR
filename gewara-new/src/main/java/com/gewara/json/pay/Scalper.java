package com.gewara.json.pay;


/**
 * ��ţʵ����
 * <p>һ����ţ��һ���绰�嵥�����˺Ű󶨵��ֻ��ţ�
 * @author user
 *
 */
public class Scalper {
	private Long id;
	private String name;				//��ţ����
	private String description;	//����
	private String mobiles;
	
	public String getMobiles() {
		return mobiles;
	}

	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
