package com.gewara.model.movie;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;

public class MovieTierPrice extends BaseObject {
	private static final long serialVersionUID = 5693818573292260241L;
	private Long id;
	private Long movieid;
	private String type;
	private Timestamp addtime;
	private Integer edition3D;
	private Integer price;
	private Integer editionJumu;
	private Integer editionIMAX;
	
	private Timestamp startTime; //��ʼʱ��
	private Timestamp endTime; //����ʱ��
	private Integer rangeEdition3D; //ʱ����ڼ۸�
	private Integer rangePrice; //ʱ����ڼ۸�
	private Integer rangeEditionJumu;//ʱ����ڼ۸�
	private Integer rangeEditionIMAX;//ʱ����ڼ۸�
	
	public MovieTierPrice(){}
	@Override
	public Serializable realId() {
		return id;
	}
	
	public MovieTierPrice(Long movieid, String type, Integer price){
		this.movieid = movieid;
		this.type = type;
		this.price = price;
		this.addtime = DateUtil.getCurFullTimestamp();
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMovieid() {
		return movieid;
	}

	public void setMovieid(Long movieid) {
		this.movieid = movieid;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Timestamp getAddtime() {
		return addtime;
	}

	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}

	public Integer getEdition3D() {
		return edition3D;
	}

	public void setEdition3D(Integer edition3D) {
		this.edition3D = edition3D;
	}
	public Integer getEditionJumu() {
		return editionJumu;
	}
	public void setEditionJumu(Integer editionJumu) {
		this.editionJumu = editionJumu;
	}
	public Integer getEditionIMAX() {
		return editionIMAX;
	}
	public void setEditionIMAX(Integer editionIMAX) {
		this.editionIMAX = editionIMAX;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public Integer getRangeEdition3D() {
		return rangeEdition3D;
	}
	public void setRangeEdition3D(Integer rangeEdition3D) {
		this.rangeEdition3D = rangeEdition3D;
	}
	public Integer getRangePrice() {
		return rangePrice;
	}
	public void setRangePrice(Integer rangePrice) {
		this.rangePrice = rangePrice;
	}
	public Integer getRangeEditionJumu() {
		return rangeEditionJumu;
	}
	public void setRangeEditionJumu(Integer rangeEditionJumu) {
		this.rangeEditionJumu = rangeEditionJumu;
	}
	public Integer getRangeEditionIMAX() {
		return rangeEditionIMAX;
	}
	public void setRangeEditionIMAX(Integer rangeEditionIMAX) {
		this.rangeEditionIMAX = rangeEditionIMAX;
	}
}
