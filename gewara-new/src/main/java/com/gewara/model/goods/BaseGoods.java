package com.gewara.model.goods;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.GoodsConstant;
import com.gewara.constant.Status;
import com.gewara.constant.sys.ConfigConstant;
import com.gewara.model.BaseObject;
import com.gewara.util.DateUtil;

public abstract class BaseGoods extends BaseObject {
	private static final long serialVersionUID = 4914995483381697551L;
	
	protected Long id;						//ID
	protected String tag;					//����
	protected Long relatedid; 				//����ID, �ID
	protected String itemtype;
	protected Long itemid;
	protected String goodsname;				//��Ʒ����
	
	
	protected Integer oriprice;				//ԭ��	
	protected Integer unitprice;			//����
	protected Integer costprice;			//�ɱ���
	
	protected Integer maxprice;				//���۸�
	protected Integer minprice;				//��С�۸�
	
	protected Integer limitnum;				//֧���޹�����
	protected Integer allowaddnum;			//�µ��������ƣ���ֹ��ͬ���µ��������࣬�����¿�治��
	
	protected Integer quantity;				//ԭʼ�Ŀ�����������涩�������Ӷ�����
	protected Integer maxbuy;				//ÿ����๺�������
	protected Integer sales;				//��������
	
	protected Integer minpoint;				//ʹ�û�������
	protected Integer maxpoint;				//ʹ�û�������
	
	protected Integer goodssort;    		//��Ʒ����
	protected String deliver;				//�Ƿ���Ҫ��ַ//N,Y		
	protected String spflag;				//�ؼۻ��ʶ
	
	protected String partners;				//����Ʒ�����ں����̣������ǵ�android��ihphone��wap
	
	protected Long clerkid;					//������			
	protected String manager;				//��������� 
	
	
	protected String shortname;				//��Ʒ���[��Ʊʹ��]
	protected String printcontent;			//Ʊֽ��ӡ����	
	protected String ordermsg;				//��������				
	protected String notifymsg;				//3Сʱ���Ѷ�Ϣ		
	
	protected String status;				//״̬
	protected String otherinfo;				//�������� json��ʽ�� ���֧��������Ϣ��
	protected String citycode;				//���д���
	protected String summary;				//����ժҪ
	protected String description;			//��Ʒ����
	protected String logo;					//ͼƬ	
	protected String biglogo;				//��ͼ
	
	protected Timestamp releasetime;		//չʾʱ��
	protected Timestamp fromtime;   		//����ʱ��
	protected Timestamp totime;     		//����ʱ��
	protected Timestamp addtime;			//����ʱ��
	protected Timestamp fromvalidtime;		//ͨƱ�볡ʱ��
	protected Timestamp tovalidtime;		//ͨƱ�볡����ʱ��
	
	protected String elecard;		
	protected String expressid;				//���ͷ�ʽid
	protected String period;				//�Ƿ���ʱ��
	protected Integer msgMinute;		//������ǰ����ʱ��(����)
	
	protected String barcode;				//������
	protected String feetype;				//ҵ��ģʽ
	protected String servicetype;			//������
	protected String seotitle; 				//SEO�ؼ���
	protected String seodescription;		//SEO����
	protected Integer clickedtimes;
	
	public String getExpressid() {
		return expressid;
	}

	public void setExpressid(String expressid) {
		this.expressid = expressid;
	}

	@Override
	public final Serializable realId() {
		return id;
	}
	
	public Timestamp getFromvalidtime() {
		return fromvalidtime;
	}
	public void setFromvalidtime(Timestamp fromvalidtime) {
		this.fromvalidtime = fromvalidtime;
	}
	public Timestamp getTovalidtime() {
		return tovalidtime;
	}
	public void setTovalidtime(Timestamp tovalidtime) {
		this.tovalidtime = tovalidtime;
	}
	public String getCitycode() {
		return citycode;
	}
	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getPrintcontent() {
		return printcontent;
	}
	public void setPrintcontent(String printcontent) {
		this.printcontent = printcontent;
	}
	public Integer getOriprice() {
		return oriprice;
	}
	public void setOriprice(Integer oriprice) {
		this.oriprice = oriprice;
	}
	
	public String getBiglogo() {
		return biglogo;
	}
	public void setBiglogo(String biglogo) {
		this.biglogo = biglogo;
	}
	
	public abstract String getGoodstype();
	public abstract boolean hasBooking();

	public String getOrdermsg() {
		return ordermsg;
	}
	public Timestamp getReleasetime() {
		return releasetime;
	}
	public void setReleasetime(Timestamp releasetime) {
		this.releasetime = releasetime;
	}
	public void setOrdermsg(String ordermsg) {
		this.ordermsg = ordermsg;
	}
	public String getNotifymsg() {
		return notifymsg;
	}
	public void setNotifymsg(String notifymsg) {
		this.notifymsg = notifymsg;
	}
	public String getGoodsname() {
		return goodsname;
	}
	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Long getRelatedid() {
		return relatedid;
	}
	public void setRelatedid(Long relatedid) {
		this.relatedid = relatedid;
	}
	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public Long getItemid() {
		return itemid;
	}

	public void setItemid(Long itemid) {
		this.itemid = itemid;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getUnitprice() {
		return unitprice;
	}
	public void setUnitprice(Integer unitprice) {
		this.unitprice = unitprice;
	}
	public Long getClerkid() {
		return clerkid;
	}
	public void setClerkid(Long clerkid) {
		this.clerkid = clerkid;
	}
	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public Timestamp getAddtime() {
		return addtime;
	}
	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	public String getLogo() {
		return logo;
	}
	public String getLimg() {
		if(StringUtils.isBlank(logo)) return "img/default_head.png";
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getMinpoint() {
		return minpoint;
	}
	public void setMinpoint(Integer minpoint) {
		this.minpoint = minpoint;
	}
	public Integer getMaxpoint() {
		return maxpoint;
	}
	public void setMaxpoint(Integer maxpoint) {
		this.maxpoint = maxpoint;
	}
	
	public Timestamp getFromtime() {
		return fromtime;
	}
	public void setFromtime(Timestamp fromtime) {
		this.fromtime = fromtime;
	}
	public Timestamp getTotime() {
		return totime;
	}
	public void setTotime(Timestamp totime) {
		this.totime = totime;
	}
	public Integer getGoodssort() {
		return goodssort;
	}
	public void setGoodssort(Integer goodssort) {
		this.goodssort = goodssort;
	}
	public Integer getLimitnum() {
		return limitnum;
	}
	public void setLimitnum(Integer limitnum) {
		this.limitnum = limitnum;
	}
		
	public boolean isStart(){
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		return cur.before(fromtime);
	}
	
	public boolean isEnd(){
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		return cur.after(totime);
	}
	public boolean isNeedDeliver() {
		return GoodsConstant.DELIVER_ENTITY.equals(deliver);
	}
	public Integer getRealpoint() {
		return this.unitprice * ConfigConstant.POINT_RATIO;
	}
	public String getReallogo(){
		if(StringUtils.isBlank(logo)) return "img/default_head.png";
		return logo;
	}
	public String getDeliver() {
		return deliver;
	}
	public void setDeliver(String deliver) {
		this.deliver = deliver;
	}
	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public Integer getCostprice() {
		return costprice;
	}
	public void setCostprice(Integer costprice) {
		this.costprice = costprice;
	}
	public boolean isPointType(){
		return GoodsConstant.GOODS_TAG_POINT.equals(tag);
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
	public String getSpflag() {
		return spflag;
	}
	public void setSpflag(String spflag) {
		this.spflag = spflag;
	}
	public String getTipMsg(){
		String result = "";
		if(StringUtils.isNotBlank(ordermsg)) {
			result = ordermsg.replaceAll("quantity", "X").replaceAll("password", "******");
		}
		return result;
	}
	public List getSummaryList(){
		if(StringUtils.isNotBlank(this.summary)) {
			return Arrays.asList(StringUtils.split(this.summary, ","));
		}
		return null;
	}
	public boolean isOpenPointPay(){
		return maxpoint !=null && maxpoint > 0;
	}
	
	public boolean isOpenCardPay(){
		return StringUtils.containsAny(elecard, "ABD");
	}
	public String getElecard() {
		return elecard;
	}

	public void setElecard(String elecard) {
		this.elecard = elecard;
	}
	public Integer getAllowaddnum() {
		return allowaddnum;
	}
	public void setAllowaddnum(Integer allowaddnum) {
		this.allowaddnum = allowaddnum;
	}
	public Integer getMaxbuy() {
		return maxbuy;
	}
	public void setMaxbuy(Integer maxbuy) {
		this.maxbuy = maxbuy;
	}

	public String getPartners() {
		return partners;
	}

	public void setPartners(String partners) {
		this.partners = partners;
	}

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		this.sales = sales;
	}
	
	public boolean hasStatus(String stats){
		return StringUtils.equals(this.status, stats);
	}
	
	public boolean hasExpired(){
		return tovalidtime.before(DateUtil.getCurFullTimestamp());
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	
	public boolean hasPeriod(){
		if(StringUtils.isBlank(this.period)) return false;
		return StringUtils.equals(this.period, GoodsConstant.PERIOD_Y);
	}
	public Integer getMsgMinute() {
		return msgMinute;
	}

	public void setMsgMinute(Integer msgMinute) {
		this.msgMinute = msgMinute;
	}
	
	public boolean isOpenBarcode(){
		return StringUtils.equals(barcode, Status.Y);
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String gainBriefname(){
		return StringUtils.isNotBlank(this.shortname)?this.shortname:this.goodsname;
	}

	public String getSeotitle() {
		return seotitle;
	}

	public void setSeotitle(String seotitle) {
		this.seotitle = seotitle;
	}

	public String getSeodescription() {
		return seodescription;
	}

	public void setSeodescription(String seodescription) {
		this.seodescription = seodescription;
	}

	public Integer getMaxprice() {
		return maxprice;
	}

	public void setMaxprice(Integer maxprice) {
		this.maxprice = maxprice;
	}

	public Integer getMinprice() {
		return minprice;
	}

	public void setMinprice(Integer minprice) {
		this.minprice = minprice;
	}
	public String getFeetype() {
		return feetype;
	}

	public void setFeetype(String feetype) {
		this.feetype = feetype;
	}
	public String getName(){
		return this.goodsname;
	}
	public Integer getClickedtimes() {
		return clickedtimes;
	}
	public void setClickedtimes(Integer clickedtimes) {
		this.clickedtimes = clickedtimes;
	}

	public String getServicetype() {
		return servicetype;
	}

	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}
}
