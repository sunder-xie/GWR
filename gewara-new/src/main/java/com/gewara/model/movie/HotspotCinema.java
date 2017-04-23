package com.gewara.model.movie;

import java.io.Serializable;

import com.gewara.model.BaseObject;

/**
 * ����ӰԺͳ������
 * @author user
 *
 */
public class HotspotCinema extends BaseObject{
	private static final long serialVersionUID = 5784408916913526370L;
	private Long cinemaid;			//ӰԺid
	private Integer buyQuantity;	//��������
	
	public HotspotCinema(){
	}
	
	public HotspotCinema(Long cinemaId, Integer buyQuantity){
		this.cinemaid = cinemaId;
		this.buyQuantity = buyQuantity;
	}

	public Integer getBuyQuantity() {
		return buyQuantity;
	}

	public void setBuyQuantity(Integer buyQuantity) {
		this.buyQuantity = buyQuantity;
	}

	@Override
	public Serializable realId() {
		return this.cinemaid;
	}

	public Long getCinemaid() {
		return cinemaid;
	}

	public void setCinemaid(Long cinemaid) {
		this.cinemaid = cinemaid;
	}
	

}
