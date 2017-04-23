package com.gewara.model.ticket;
import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.gewara.constant.ticket.OpiConstant;
import com.gewara.model.BaseObject;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.util.DateUtil;
import com.gewara.util.StringUtil;

/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public class OpenPlayItem extends BaseObject {
	private static final long serialVersionUID = -4016785855588367848L;
	private Long id;
	private Long mpid;
	private Long cinemaid;
	private String citycode;
	private Long movieid;
	private Long roomid;
	private String moviename;
	private String cinemaname;
	private String roomname;
	private Timestamp playtime;
	private String edition;
	private Integer price;			//ӰԺ��
	private String language;
	private Integer costprice; 		//�ɱ��ۣ�Ʊ��ۣ�
	private Integer gewaprice;
	private Integer lowest;			//���Ʊ��
	private Integer fee;			//�����
	private String remark;
	private String status;			//״̬����Ԥ��������Ԥ����
	private String partner;			//������鿪��״̬��Y���⿪��,N�����⿪��
	private Timestamp opentime;		//���Ź�Ʊʱ��
	private Timestamp closetime;	//�رչ�Ʊʱ��
	
	private String seqNo;			//����
	private String opentype; 		//�������ͣ�GEWA��HFH
	private String elecard;			//1)���õĵ���ȯ����ABC��2)���û��֣�P 3)M��ʾ�����̼������Żݻ

	private Integer minpoint;		//ʹ�û�������
	private Integer maxpoint;		//ʹ�û�������
	private String spflag;			//�ؼۻ��ʶ
	
	private String buylimit;		//�����������ƣ�1,2,3,4,5
	private Long topicid;			//ȡƱ����
	private String dayotime;		//day open time ÿ�տ���ʱ��
	private String dayctime;		//day close time ÿ�չر�ʱ��
	private Integer seatnum;		//��λ����
	private Integer asellnum;		//allow ����������
	private Integer gsellnum;		//Gewa������
	private Integer csellnum;		//ӰԺ����
	private Integer locknum;		//Gewa������
	private Timestamp updatetime;	//����ʱ��
	private Integer givepoint;		//�����֣�����ʾ���ӻ��֣�����ʾ������
	private String otherinfo;		//������Ϣ
	private String expressid;		//��ݷ�ʽ
	public String getExpressid() {
		return expressid;
	}
	public void setExpressid(String expressid) {
		this.expressid = expressid;
	}
	public String getOtherinfo() {
		return otherinfo;
	}
	public void setOtherinfo(String otherinfo) {
		this.otherinfo = otherinfo;
	}
	public Integer getSeatnum() {
		return seatnum;
	}
	public void setSeatnum(Integer seatnum) {
		this.seatnum = seatnum;
	}
	public Integer getRemainnum(){
		return this.seatnum - gsellnum - csellnum - locknum;
	}
	public String getDayctime() {
		return dayctime;
	}
	public void setDayctime(String dayctime) {
		this.dayctime = dayctime;
	}
	public OpenPlayItem(){}
	public OpenPlayItem(MoviePlayItem mpi, String opentype, String dayotime, String dayctime, int cmin){
		this.opentype = opentype;
		this.mpid = mpi.getId();
		//this.remark = mpi.getRemark();
		this.status = OpiConstant.STATUS_NOBOOK;
		this.partner = OpiConstant.PARTNER_OPEN;
		this.elecard = "BADM"; //M��ʾ�����̼������Żݻ
		copyFrom(mpi);
		this.opentime = DateUtil.addMinute(new Timestamp(System.currentTimeMillis()), 30);
		this.closetime = DateUtil.addMinute(playtime, - cmin);
		this.minpoint = 500;
		this.maxpoint = 10000;
		this.dayotime = dayotime;
		this.dayctime = dayctime;
	}
	public void copyFrom(MoviePlayItem mpi){
		//������language,edition,price
		this.cinemaid = mpi.getCinemaid();
		this.movieid = mpi.getMovieid();
		this.roomid = mpi.getRoomid();
		this.roomname = mpi.getPlayroom();
		this.playtime = Timestamp.valueOf(mpi.getFullPlaytime());
		this.gewaprice = mpi.getGewaprice();
		this.lowest = mpi.getLowest();
		this.price = mpi.getPrice();
		this.language = mpi.getLanguage();
		this.edition = mpi.getEdition();
		this.citycode = mpi.getCitycode();
		if(!OpiConstant.OPEN_GEWARA.equals(opentype)){
			this.seqNo = mpi.getSeqNo();
		}
	}
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public Serializable realId() {
		return id;
	}
	public Integer getGewaprice() {
		return gewaprice;
	}
	public void setGewaprice(Integer gewaprice) {
		this.gewaprice = gewaprice;
	}
	public Timestamp getOpentime() {
		return opentime;
	}
	public void setOpentime(Timestamp opentime) {
		this.opentime = opentime;
	}
	
	public boolean isOrder(){
		Timestamp curtime = new Timestamp(System.currentTimeMillis());
		String time = DateUtil.format(curtime, "HHmm");
		boolean open = playtime.after(curtime) && opentime.before(curtime) 
			&& closetime.after(curtime) && status.equals(OpiConstant.STATUS_BOOK) 
			&& StringUtil.between(time, dayotime, dayctime)
			&& gsellnum < asellnum;
		
		return open;
	}
	//����ķ���ֻ�ں�̨��
	public boolean isOpen(){
		return opentime!=null && opentime.before(new Timestamp(System.currentTimeMillis()));
	}
	
	public boolean isBooking(){
		return StringUtils.equals(status, OpiConstant.STATUS_BOOK) && !isClosed();
	}
	public boolean isUnOpenToGewa(){
		return StringUtils.contains(otherinfo, OpiConstant.UNOPENGEWA);
	}
	public boolean isUnShowToGewa(){
		return StringUtils.contains(otherinfo, OpiConstant.UNSHOWGEWA) || StringUtils.contains(otherinfo, OpiConstant.UNOPENGEWA);
	}
	public boolean isExpired(){
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		return playtime!=null && playtime.before(cur) || StringUtils.equals(status, OpiConstant.STATUS_PAST);
	}
	public boolean isClosed(){
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		return closetime==null || cur.after(closetime);
	}
	public String getMoviename() {
		return moviename;
	}
	public void setMoviename(String moviename) {
		this.moviename = moviename;
	}
	public String getCinemaname() {
		return cinemaname;
	}
	public void setCinemaname(String cinemaname) {
		this.cinemaname = cinemaname;
	}
	public String getRoomname() {
		return roomname;
	}
	public void setRoomname(String roomname) {
		this.roomname = roomname;
	}
	public Timestamp getPlaytime() {
		return playtime;
	}
	public void setPlaytime(Timestamp playtime) {
		this.playtime = playtime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getRoomid() {
		return roomid;
	}
	public void setRoomid(Long roomid) {
		this.roomid = roomid;
	}
	public Long getMpid() {
		return mpid;
	}
	public void setMpid(Long mpid) {
		this.mpid = mpid;
	}
	public Long getCinemaid() {
		return cinemaid;
	}
	public void setCinemaid(Long cinemaid) {
		this.cinemaid = cinemaid;
	}
	public Long getMovieid() {
		return movieid;
	}
	public void setMovieid(Long movieid) {
		this.movieid = movieid;
	}
	public Timestamp getClosetime() {
		return closetime;
	}
	public void setClosetime(Timestamp closetime) {
		this.closetime = closetime;
	}
	public String getOpentype() {
		return opentype;
	}
	public void setOpentype(String opentype) {
		this.opentype = opentype;
	}
	public Integer getCostprice() {
		return costprice;
	}
	public void setCostprice(Integer costprice) {
		this.costprice = costprice;
	}
	public Integer getLowest() {
		return lowest;
	}
	public void setLowest(Integer lowest) {
		this.lowest = lowest;
	}
	
	public boolean hasGewara(){
		return StringUtils.equals(opentype, OpiConstant.OPEN_GEWARA);
	}
	
	public boolean hasOpentype(String openType){
		return StringUtils.equals(this.opentype, openType);
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getTimeStr(){
		return DateUtil.format(playtime,"HH:mm");
	}
	public String getElecard() {
		return elecard;
	}
	public void setElecard(String elecard) {
		this.elecard = elecard;
	}
	public String getEdition() {
		return edition;
	}
	public void setEdition(String edition) {
		this.edition = edition;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Long getTopicid() {
		return topicid;
	}
	public void setTopicid(Long topicid) {
		this.topicid = topicid;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public boolean isOpenToPartner(){
		return "Y".equals(partner);
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
	public boolean isMustUsePoint() {
		return StringUtils.contains(this.elecard, 'P');
	}
	public boolean isOpenPointPay(){
		return maxpoint > 0;
	}
	public boolean isOpenCardPay(){
		return StringUtils.containsAny(this.elecard, "ABD");
	}
	public boolean isDisCountPay(){
		return StringUtils.contains(this.elecard, "M");
	}
	public String getDayotime() {
		return dayotime;
	}
	public void setDayotime(String dayotime) {
		this.dayotime = dayotime;
	}
	public Timestamp getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	public Integer getGsellnum() {
		return gsellnum;
	}
	public void setGsellnum(Integer gsellnum) {
		this.gsellnum = gsellnum;
	}
	public Integer getCsellnum() {
		return csellnum;
	}
	public void setCsellnum(Integer csellnum) {
		this.csellnum = csellnum;
	}
	public Integer getLocknum() {
		return locknum;
	}
	public void setLocknum(Integer locknum) {
		this.locknum = locknum;
	}
	public String getBuylimit() {
		return buylimit;
	}
	public void setBuylimit(String buylimit) {
		this.buylimit = buylimit;
	}
	// ��Ʊ��ť��� ��λ״̬ 
	public String getSeatStatus(){
		Integer remain = this.seatnum - this.gsellnum - this.csellnum - this.locknum;
		if(remain == 0) return "�����ˡ�";
		if(0 < remain && remain < 10) {
			return "��λ����";
		}
		return "ѡ����Ʊ";
	}
	// �ֻ�����ʾ��Ʊ״̬��0ûƱ��1����Ʊ��2�ܶ�Ʊ
	public String seatAmountStatus(){
		Integer remain = this.seatnum - this.gsellnum - this.csellnum - this.locknum;
		if(remain == 0) return "0";
		if(0 < remain && remain < 10) {
			return "1";
		}
		return "2";
	}
	
	public String getCitycode() {
		return citycode;
	}
	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
	public String getSpflag() {
		return spflag;
	}
	public void setSpflag(String spflag) {
		this.spflag = spflag;
	}
	public Integer getGivepoint() {
		return givepoint;
	}
	public void setGivepoint(Integer givepoint) {
		this.givepoint = givepoint;
	}
	public Integer getAsellnum() {
		return asellnum;
	}
	public void setAsellnum(Integer asellnum) {
		this.asellnum = asellnum;
	}
	public Integer getFee() {
		return fee;
	}
	public void setFee(Integer fee) {
		this.fee = fee;
	}
	
	public int gainLockMinute(){
		if(hasOpentype(OpiConstant.OPEN_PNX)){
			return OpiConstant.MAX_MINUTS_TICKETS_PNX;
		}
		return OpiConstant.MAX_MINUTS_TICKETS;
	}
	
	public int gainLockSeat(){
		if(hasOpentype(OpiConstant.OPEN_PNX) || hasOpentype(OpiConstant.OPEN_MTX)){
			return OpiConstant.MAXSEAT_PER_ORDER_PNX;
		}
		return OpiConstant.MAXSEAT_PER_ORDER;
	}
}
