package com.gewara.json.order;

/**
 * 360CPS�����Ķ���������ο� http://union.360.cn/help/apidocnew#cxjk
 * 
 */
public class OutOriginOrder {
	private String outOrigin;
	// ��������
	private Long id;
	// ������վ���
	private String bid;
	// 360�û�ID
	private String qid;
	// 360ҵ����
	private String qihoo_id;
	// ��չ�ֶΣ�
	private String ext;
	// ������
	private String order_id;
	// �µ�ʱ��
	private String order_time;
	// ����������ʱ��
	private String order_updtime;
	// ��Ӷ��
	private String total_comm;
	// Ӷ����ϸ
	private String commission;
	// ������Ʒ����ϸ��Ϣ
	private String p_info;
	// �������
	private String server_price;
	// ����Ӧ���ܶ�
	private Integer total_price;
	// ��Ʒ�ŻݵĽ��
	private String coupon;
	// ����������״̬
	private String status;
	// ӰƬID
	private Long movieid;
	
	private String description2;
	private String quantity;
	private String unitprice;
	private Integer amount;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public String getQihoo_id() {
		return qihoo_id;
	}

	public void setQihoo_id(String qihoo_id) {
		this.qihoo_id = qihoo_id;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getOrder_time() {
		return order_time;
	}

	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}

	public String getOrder_updtime() {
		return order_updtime;
	}

	public void setOrder_updtime(String order_updtime) {
		this.order_updtime = order_updtime;
	}

	public String getTotal_comm() {
		return total_comm;
	}

	public void setTotal_comm(String total_comm) {
		this.total_comm = total_comm;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public String getP_info() {
		return p_info;
	}

	public void setP_info(String p_info) {
		this.p_info = p_info;
	}

	public String getServer_price() {
		return server_price;
	}

	public void setServer_price(String server_price) {
		this.server_price = server_price;
	}

	public Integer getTotal_price() {
		return total_price;
	}

	public void setTotal_price(Integer total_price) {
		this.total_price = total_price;
	}

	public String getCoupon() {
		return coupon;
	}

	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getMovieid() {
		return movieid;
	}

	public void setMovieid(Long movieid) {
		this.movieid = movieid;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getDescription2() {
		return description2;
	}

	public void setDescription2(String description2) {
		this.description2 = description2;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getUnitprice() {
		return unitprice;
	}

	public void setUnitprice(String unitprice) {
		this.unitprice = unitprice;
	}

	public String getOutOrigin() {
		return outOrigin;
	}

	public void setOutOrigin(String outOrigin) {
		this.outOrigin = outOrigin;
	}
}
