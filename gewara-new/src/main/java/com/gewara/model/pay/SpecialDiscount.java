package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.Status;
import com.gewara.model.BaseObject;
import com.gewara.util.VmBaseUtil;

/**
 * �����Żݷ���
 * @author acerge(acerge@163.com)
 * @since 2:29:44 PM Dec 8, 2010
 */
public class SpecialDiscount extends BaseObject{
	private static final long serialVersionUID = 1238746L;
	public static final String DISCOUNT_TYPE_PERORDER = "order";			//ÿ�ʶ����ۿ�
	public static final String DISCOUNT_TYPE_PERTICKET = "uprice";			//ÿ����Ʒ�ۿ�
	public static final String DISCOUNT_TYPE_PERCENT = "percent";			//�����ܶ�ٷֱ�
	public static final String DISCOUNT_TYPE_BUYONE_GIVEONE = "one2one";	//��1��1
	public static final String DISCOUNT_TYPE_FIXPRICE = "fprice";			//fix priceͳһ���ۣ����۹̶���
	public static final String DISCOUNT_TYPE_EXPRESSION = "exp";				//���ʽ

	public static final String DISCOUNT_PERIOD_A = "A";				//�Զ�������
	public static final String DISCOUNT_PERIOD_D = "D";				//��Ȼ��
	public static final String DISCOUNT_PERIOD_W = "W";				//��Ȼ������(һ��)
	public static final String DISCOUNT_PERIOD_DW = "DW";			//��Ȼ������(����)
	public static final String DISCOUNT_PERIOD_M = "M";				//��Ȼ������(һ����)
	
	public static final String REBATES_CASH = "Y";		//�ֽ�
	public static final String REBATES_CARDA = "A";		//A��
	public static final String REBATES_CARDC = "C";		//C��
	public static final String REBATES_CARDD = "D";		//D��
	public static final String REBATES_POINT = "P";		//����
	
	public static final String OPENTYPE_GEWA = "G"; 	//�Զ�Gewa���ο��� 
	public static final String OPENTYPE_PARTNER = "P";	//�̼��������ο���
	public static final String OPENTYPE_WAP = "W";		//WAP����
	public static final String OPENTYPE_SPECIAL = "S";	//�ر����õĲſ���
	public static final String ENCODE_KEY = "KE3a&h@";
	
	//����֧����ʽ
	public static final String VERIFYTYPE_FIXED = "fixed";		//�̶���
	public static final String VERIFYTYPE_ONLYONE = "onlyone";	//ֻ��һ��
	
	private Long id;
	private String flag;				//�����ʶ
	private String bankname;			//��������
	private String description;			//��Ҫ˵��
	private Timestamp timefrom;			//�Żݿ�ʼʱ��
	private Timestamp timeto;			//�Żݽ���ʱ��
	private Integer limitnum;			//�����޹�����
	private Integer limitperiod;		//�����޹����ڣ����ӣ�
	private String periodtype;			//�޹���������
	private Long spcounterid;			//ʹ��������������ID
	private Integer sellcount;			//��������
	private String opentype;			//��������   

	private String tag;					//��������
	private String addtime1;			//�µ�ʱ���޶�1��0000
	private String addtime2;			//�µ�ʱ���޶�2��2400
	private String addweek;				//�µ��ܼ�
	private String time1;				//��ӳʱ���޶�1��0000
	private String time2;				//��ӳʱ���޶�2��2400
	private String weektype;			//�����ܼ�
	private Integer pricegap;			//gewa������ɱ��� ��۷�Χ
	private Integer price1;				//���۷�Χ1
	private Integer price2;				//���۷�Χ2
	private Integer costprice1;			//�ɱ��۷�Χ1
	private Integer costprice2;			//�ɱ��۷�Χ2
	private String specialrule;			//���������ʽ
	
	private String citycode;			//�����б�
	private String relatedid;			//������б� 
	private String categoryid;			//С�����б�
	private String itemid;				//��ϸ�����б�
	private String fieldid;				//���ط����б�
	private String edition;				//�汾����
	
	private Integer rebates;			//ÿ�ʶ�������
	private Integer rebatesmax;			//ǰ����������
	private String rebatestype;			//�������ͣ���ֵ���Ϳ�
	private Long bindgoods;				//�����ײ�
	private Integer bindnum;			//�ײ͹�Ʊ����
	private String logo;				//����Logo
	
	private String paymethod;			//����֧����ʽ
	private Integer discount;			//�Żݽ��
	private Integer extdiscount;		//�ⲿ�Żݽ��
	private String expression;			//������ʽ
	private String distype;				//�Żݷ�ʽ
	private String remark;				//������ʱ˵��
	private String enableRemark;		//����ʱ��˵��
	private String recommendRemark;		//�Ƽ�ʱ��˵��
	private Timestamp createtime;		//����ʱ��
	private Timestamp updatetime;		//��������ʱ��
	private String banner;				//����bannerͼƬ
	private String adcontent;			//�������
	private String otherinfo;			//������Ϣ
	private Integer minbuy;				//ÿ��������������
	private Integer buynum;				//ÿ��������������
	private String validateUrl;			//��֤��תURL
	private String validBackUrl;		//֧��֪ͨURL��֤
	private Integer sortnum;			//��������
	private String uniqueby;			//ʹ��ʲô����Ψһ��
	private String ptnids;				//֧������id����
	private String channel;				//���ͣ���Ż�/���������Ż�
	private String bindmobile;			//���ֻ�
	private Long drawactivity;  		//�û�����Ʊ�󷵻�ȯ���ó齱��ʽ�����û�
	private Integer drawperiod;			//��Ʊ�ɹ���ȡ��ȯ����
	private Integer bindDrawCardNum;	//��������ȡ��������
	private Integer ipLimitedOrderCount;//��ip�����µ�����
	private String cardUkey;			//����֤Ψһ��ʶ
	private String cardNumUnique;  		//�Ƿ����п�������֤
	private Integer cardNumPeriodIntvel; //���п�ÿ��ʹ���޹�����
	private Integer cardNumPeriodSpan;	//���п��������
	private Integer cardNumLimitnum; 	//���п��޹�����
	private String cardbinUkey;  	 	//������֤��ʶ
	private String loginfrom;			//��¼��Դ
	private String extraInfo;			//��չ��Ϣ

	//���ĳɱ��ۣ���ȯ���߼�һ��
	private String costtype;		//�ɱ������ͣ�ӰƬ��׼ۼ�xԪ������ۼ�xԪ
	private Integer costnum;		//�ɱ�������
	
	private String verifyType;		//��̬����֤���ͣ��ޣ����ظ�ʹ�ã�������ʹ�ã��������ظ�ʹ��
	
	public SpecialDiscount(){
		
	}

	public Integer getBindDrawCardNum() {
		return bindDrawCardNum;
	}
	public void setBindDrawCardNum(Integer bindDrawCardNum) {
		this.bindDrawCardNum = bindDrawCardNum;
	}
	
	public Long getDrawactivity() {
		return drawactivity;
	}
	public void setDrawactivity(Long drawactivity) {
		this.drawactivity = drawactivity;
	}
	public SpecialDiscount(String flag, String tag){
		this.tag = tag;
		this.flag = flag;
		this.createtime = new Timestamp(System.currentTimeMillis());
		this.updatetime = createtime;
		this.sellcount = 0;
		this.extraInfo = Status.N;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public Timestamp getTimefrom() {
		return timefrom;
	}
	public void setTimefrom(Timestamp timefrom) {
		this.timefrom = timefrom;
	}
	public Timestamp getTimeto() {
		return timeto;
	}
	public void setTimeto(Timestamp timeto) {
		this.timeto = timeto;
	}
	public String getPaymethod() {
		return paymethod;
	}
	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}
	public boolean isValidPaymethod(String spaymethod, String paybank){
		if(StringUtils.isBlank(this.paymethod)) return true;
		String[] pmList = StringUtils.split(this.paymethod, ",");
		for(String pm: pmList){
			if(StringUtils.equals(pm, spaymethod)) return true;
			String[] pair = StringUtils.split(pm, ":");
			if(StringUtils.equals(pair[0], spaymethod)){
				if(pair.length==1 || pair.length>1 && StringUtils.equals(pair[1], paybank)) return true;
			}
		}
		return false;
	}
	public Integer getDiscount() {
		return discount;
	}
	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
	
	public Integer getExtdiscount() {
		return extdiscount;
	}

	public void setExtdiscount(Integer extdiscount) {
		this.extdiscount = extdiscount;
	}

	public String getDistype() {
		return distype;
	}
	public void setDistype(String distype) {
		this.distype = distype;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getLimitnum() {
		return limitnum;
	}
	public void setLimitnum(Integer limitnum) {
		this.limitnum = limitnum;
	}
	public Integer getLimitperiod() {
		return limitperiod;
	}
	public void setLimitperiod(Integer limitperiod) {
		this.limitperiod = limitperiod;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	public Integer getSellcount() {
		return sellcount;
	}
	public void setSellcount(Integer sellcount) {
		this.sellcount = sellcount;
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
	public String getTime1() {
		return time1;
	}
	public void setTime1(String time1) {
		this.time1 = time1;
	}
	public String getTime2() {
		return time2;
	}
	public void setTime2(String time2) {
		this.time2 = time2;
	}
	public Integer getPricegap() {
		return pricegap;
	}
	public void setPricegap(Integer pricegap) {
		this.pricegap = pricegap;
	}
	public Integer getPrice1() {
		return price1;
	}
	public void setPrice1(Integer price1) {
		this.price1 = price1;
	}
	public Integer getPrice2() {
		return price2;
	}
	public void setPrice2(Integer price2) {
		this.price2 = price2;
	}
	public String getCitycode() {
		return citycode;
	}
	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
	public String getRelatedid() {
		return relatedid;
	}
	public void setRelatedid(String relatedid) {
		this.relatedid = relatedid;
	}
	public String getCategoryid() {
		return categoryid;
	}
	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getFieldid() {
		return fieldid;
	}
	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getOpentype() {
		return opentype;
	}
	public void setOpentype(String opentype) {
		this.opentype = opentype;
	}
	public String getWeektype() {
		return weektype;
	}
	public void setWeektype(String weektype) {
		this.weektype = weektype;
	}
	public String getAddtime1() {
		return addtime1;
	}
	public void setAddtime1(String addtime1) {
		this.addtime1 = addtime1;
	}
	public String getAddtime2() {
		return addtime2;
	}
	public void setAddtime2(String addtime2) {
		this.addtime2 = addtime2;
	}
	public String getAddweek() {
		return addweek;
	}
	public void setAddweek(String addweek) {
		this.addweek = addweek;
	}
	public String getBanner() {
		return banner;
	}
	public void setBanner(String banner) {
		this.banner = banner;
	}
	public String getAdcontent() {
		return adcontent;
	}
	public void setAdcontent(String adcontent) {
		this.adcontent = adcontent;
	}
	public Integer getBuynum() {
		return buynum;
	}
	public void setBuynum(Integer buynum) {
		this.buynum = buynum;
	}
	public Integer getRebates() {
		return rebates;
	}
	public void setRebates(Integer rebates) {
		this.rebates = rebates;
	}
	public Integer getRebatesmax() {
		return rebatesmax;
	}
	public void setRebatesmax(Integer rebatesmax) {
		this.rebatesmax = rebatesmax;
	}
	public String getRebatestype() {
		return rebatestype;
	}
	public void setRebatestype(String rebatestype) {
		this.rebatestype = rebatestype;
	}
	public String getEnableRemark() {
		return enableRemark;
	}
	public void setEnableRemark(String enableRemark) {
		this.enableRemark = enableRemark;
	}
	public String getRecommendRemark() {
		return recommendRemark;
	}
	public void setRecommendRemark(String recommendRemark) {
		this.recommendRemark = recommendRemark;
	}
	public String getFullEnableRemark(Integer amount, Integer sdiscount){
		int tmpDiscount = sdiscount == null ? 0: sdiscount.intValue();
		return StringUtils.replace(StringUtils.replace(enableRemark, "amount", amount - tmpDiscount+".00"), "discount", tmpDiscount + ".00");
	}
	public String getFullRecommendRemark(Integer amount, Integer sdiscount){
		int tmpDiscount = sdiscount == null ? 0: sdiscount.intValue();
		return StringUtils.replace(StringUtils.replace(recommendRemark, "amount", amount - tmpDiscount+".00"), "discount", tmpDiscount + ".00");
	}
	public Long getBindgoods() {
		return bindgoods;
	}
	public void setBindgoods(Long bindgoods) {
		this.bindgoods = bindgoods;
	}
	public Integer getBindnum() {
		return bindnum;
	}
	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public void setBindnum(Integer bindnum) {
		this.bindnum = bindnum;
	}
	public Integer getMinbuy() {
		return minbuy;
	}
	public void setMinbuy(Integer minbuy) {
		this.minbuy = minbuy;
	}
	public String getValidateUrl() {
		return validateUrl;
	}
	public void setValidateUrl(String validateUrl) {
		this.validateUrl = validateUrl;
	}
	public Integer getSortnum() {
		return sortnum;
	}
	public void setSortnum(Integer sortnum) {
		this.sortnum = sortnum;
	}
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getUniqueby() {
		return uniqueby;
	}
	public void setUniqueby(String uniqueby) {
		this.uniqueby = uniqueby;
	}
	public String getValidBackUrl() {
		return validBackUrl;
	}
	public void setValidBackUrl(String validBackUrl) {
		this.validBackUrl = validBackUrl;
	}
	public String getLimitperiodStr() {
		int hour = limitperiod/60;
		int min = limitperiod%60;
		int day = 0;
		if(hour > 24){
			day = hour/24;
			hour = hour % 24;
		}
		String result = (day > 0?day+"��":"") + (hour>0? hour+"Сʱ":"") + (min>0?min+"��":"");
		return result;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public String getLimg(){
		return this.logo;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public Timestamp getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}
	public String getBindmobile() {
		return bindmobile;
	}
	public void setBindmobile(String bindmobile) {
		this.bindmobile = bindmobile;
	}
	public Long getSpcounterid() {
		return spcounterid;
	}
	public void setSpcounterid(Long spcounterid) {
		this.spcounterid = spcounterid;
	}
	public Integer getCostprice1() {
		return costprice1;
	}
	public void setCostprice1(Integer costprice1) {
		this.costprice1 = costprice1;
	}
	public Integer getCostprice2() {
		return costprice2;
	}
	public void setCostprice2(Integer costprice2) {
		this.costprice2 = costprice2;
	}

	public Integer getDrawperiod() {
		return drawperiod;
	}

	public void setDrawperiod(Integer drawperiod) {
		this.drawperiod = drawperiod;
	}
	
	public boolean hasDistype(String type){
		return StringUtils.equals(this.distype, type);
	}
	
	public boolean hasRebatestype(String retype){
		return StringUtils.equals(this.rebatestype, retype);
	}
	
	public String gainDiscountType(){
		String tmp = "";
		if(hasDistype(DISCOUNT_TYPE_PERORDER)){
			tmp = "����";
		}else if(hasDistype(DISCOUNT_TYPE_PERTICKET)){
			tmp = "����";
		}else if(hasDistype(DISCOUNT_TYPE_PERCENT)){
			tmp = "�ۿ�";
		}else if(hasDistype(DISCOUNT_TYPE_BUYONE_GIVEONE)){
			tmp = tmp + "��һ��һ";
		}else if(hasDistype(DISCOUNT_TYPE_FIXPRICE)){
			tmp = "�̶��۸�";
		}else if(hasDistype(DISCOUNT_TYPE_EXPRESSION)){
			tmp = "�";
		}
		if(StringUtils.isBlank(tmp)){
			if(this.rebates>0){
				tmp = "����";
			}else{
				tmp = "�Ż�";
			}
		}
		return tmp;
	}
	
	public String getDiscountText(String bankText, String discountText, String rebatesText){
		String tmp = "bankname";
		if(hasDistype(DISCOUNT_TYPE_PERORDER)){
			if(this.discount != null && this.discount > 0){
				tmp = tmp + "ÿ�ʶ�������discountԪ";
			}
		}else if(hasDistype(DISCOUNT_TYPE_PERTICKET)){
			if(this.discount != null && this.discount > 0){
				tmp = tmp + "ÿ��Ʊ����discountԪ";
			}
		}else if(hasDistype(DISCOUNT_TYPE_PERCENT)){
			tmp = tmp + "ÿ�ʶ���discount���Ż�";
		}else if(hasDistype(DISCOUNT_TYPE_BUYONE_GIVEONE)){
			tmp = tmp + "��������һ��һ";
		}else if(hasDistype(DISCOUNT_TYPE_FIXPRICE)){
			tmp = tmp + "ÿ��Ʊ����discountԪ";
		}else if(hasDistype(DISCOUNT_TYPE_EXPRESSION)){
			//TODO:�����ʾ
			tmp = tmp + this.description;
		}
		tmp = StringUtils.replace(tmp, "bankname", bankText);
		tmp = StringUtils.replace(tmp, "discount", discountText);
		if(this.rebates>0){
			if((hasDistype(DISCOUNT_TYPE_PERTICKET) || hasDistype(DISCOUNT_TYPE_PERORDER)) && !(this.discount != null && this.discount > 0)){
				tmp += "����rebates";
			}else{
				tmp += ", ����rebates";
			}
			if(hasRebatestype("Y")) tmp += "Ԫ";
			if(hasRebatestype("P")) tmp += "����";
			if(hasRebatestype("A") || hasRebatestype("D")) tmp += "Ԫȯ";
			tmp = StringUtils.replace(tmp, "rebates", rebatesText);
		}
		return tmp;
	}
	
	public String gainDiscount(String noRebates){
		int tmp = this.discount;
		if(hasExtdicount()){
			tmp = this.extdiscount;
		}
		String tmpDiscount = String.valueOf(tmp);
		if(Boolean.parseBoolean(noRebates) && hasRebatestype("Y")){
			tmpDiscount = String.valueOf(tmp + this.rebates);
		}
		if(hasDistype(DISCOUNT_TYPE_PERCENT)){
			String tmpPattern = "0";
			if(tmp !=0 && 100%tmp>0) tmpPattern = "0.0";
			tmpDiscount = VmBaseUtil.formatPercent(100-tmp, 10, tmpPattern);
		}else if(hasDistype(DISCOUNT_TYPE_BUYONE_GIVEONE)){
			tmpDiscount = "X";
		}else if(hasDistype(DISCOUNT_TYPE_EXPRESSION)){
			tmpDiscount = "XԪ";
		}
		return tmpDiscount;
	}
	
	public boolean hasExtdicount(){
		return this.extdiscount != null && this.discount == 0 && this.extdiscount > 0;
	}

	public Integer getIpLimitedOrderCount() {
		return ipLimitedOrderCount;
	}

	public void setIpLimitedOrderCount(Integer ipLimitedOrderCount) {
		this.ipLimitedOrderCount = ipLimitedOrderCount;
	}

	public String getCardUkey() {
		return cardUkey;
	}

	public void setCardUkey(String cardUkey) {
		this.cardUkey = cardUkey;
	}

	public String getCardNumUnique() {
		return cardNumUnique;
	}

	public void setCardNumUnique(String cardNumUnique) {
		this.cardNumUnique = cardNumUnique;
	}

	public Integer getCardNumLimitnum() {
		return cardNumLimitnum;
	}

	public void setCardNumLimitnum(Integer cardNumLimitnum) {
		this.cardNumLimitnum = cardNumLimitnum;
	}

	public String getCardbinUkey() {
		return cardbinUkey;
	}

	public void setCardbinUkey(String cardbinUkey) {
		this.cardbinUkey = cardbinUkey;
	}

	public Integer getCardNumPeriodIntvel() {
		return cardNumPeriodIntvel;
	}

	public void setCardNumPeriodIntvel(Integer cardNumPeriodIntvel) {
		this.cardNumPeriodIntvel = cardNumPeriodIntvel;
	}

	public Integer getCardNumPeriodSpan() {
		return cardNumPeriodSpan;
	}

	public void setCardNumPeriodSpan(Integer cardNumPeriodSpan) {
		this.cardNumPeriodSpan = cardNumPeriodSpan;
	}
	
	public String getLoginfrom() {
		return loginfrom;
	}

	public void setLoginfrom(String loginfrom) {
		this.loginfrom = loginfrom;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getSpecialrule() {
		return specialrule;
	}

	public void setSpecialrule(String specialrule) {
		this.specialrule = specialrule;
	}

	public String getPeriodtype() {
		return periodtype;
	}

	public void setPeriodtype(String periodtype) {
		this.periodtype = periodtype;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public String getPtnids() {
		return ptnids;
	}

	public void setPtnids(String ptnids) {
		this.ptnids = ptnids;
	}

	public String getCosttype() {
		return costtype;
	}

	public void setCosttype(String costtype) {
		this.costtype = costtype;
	}

	public Integer getCostnum() {
		return costnum;
	}

	public void setCostnum(Integer costnum) {
		this.costnum = costnum;
	}

	public String getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(String verifyType) {
		this.verifyType = verifyType;
	}
}
