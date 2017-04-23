package com.gewara.model.pay;

import java.io.Serializable;
import java.sql.Timestamp;

import com.gewara.constant.order.ElecCardConstant;
import com.gewara.model.BaseObject;

/**
 * ����ȯ������Ϣ
 * @since Dec 30, 2011, 5:09:25 PM
 * @author acerge(gebiao)
 * @function 
 */
public class ElecCardExtra extends BaseObject {
	private static final long serialVersionUID = 6287072047189731464L;
	private Long batchid;			//���κ�
	private Long pid;				//������ID
	private Timestamp addtime;		//����ʱ��
	private Long issuerid;			//������ID	
	private String category1;		//���1:����
	private String category2;		//���2:С��
	private String applycity;		//��������
	private String applydept;		//���벿��
	private String applytype;		//��������
	private Long adduserid;			//������
	private Timestamp soldtime;		//����ʱ��
	private Long sellerid; 			//������
	private String sellremark;		//����˵��
	private Integer sellprice;		//����
	private String channel;			//��������
	private Integer cardcount;		//������
	private Integer usedcount;		//ʹ������
	private Integer delcount;		//��������
	private Integer newcount;		//�¿�����
	private Integer soldcount;		//�۳�����
	private Integer lockcount;		//��������
	private Integer issuecount;		//�û����õ�����
	private String mincardno;		//��С����
	private String maxcardno;		//��󿨺�
	private Timestamp statstime;	//ͳ��ʱ��
	private Long merchantid;		//�����̼ң�ֻΪӰԺ���˶�ר��ר�ã�Gewara��0
	private String status;			//����״̬����ʷ������
	public ElecCardExtra(){}
	public ElecCardExtra(Long batchid){
		this.batchid = batchid;
		this.addtime = new Timestamp(System.currentTimeMillis());
		this.status = ElecCardConstant.DATA_NOW;
		this.cardcount = 0;
		this.usedcount = 0;
		this.delcount = 0;
		this.newcount = 0;
		this.lockcount = 0;
		this.merchantid = 0l;
	}
	@Override
	public Serializable realId() {
		return batchid;
	}
	public void copyFrom(ElecCardExtra parentExtra) {
		category1 = parentExtra.category1;		//���1:����
		category2 = parentExtra.category2;		//���2:С��
		applycity = parentExtra.applycity;		//��������
		applydept = parentExtra.applydept;		//���벿��
		applytype = parentExtra.applytype;		//��������
		sellprice = parentExtra.sellprice;		//����
		merchantid = parentExtra.merchantid;
	}

	public Long getBatchid() {
		return batchid;
	}

	public void setBatchid(Long batchid) {
		this.batchid = batchid;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public boolean hasParent(){
		return pid != null;
	}

	public Long getIssuerid() {
		return issuerid;
	}

	public void setIssuerid(Long issuerid) {
		this.issuerid = issuerid;
	}

	public String getCategory1() {
		return category1;
	}

	public void setCategory1(String category1) {
		this.category1 = category1;
	}

	public String getCategory2() {
		return category2;
	}

	public void setCategory2(String category2) {
		this.category2 = category2;
	}

	public String getApplycity() {
		return applycity;
	}

	public void setApplycity(String applycity) {
		this.applycity = applycity;
	}

	public String getApplydept() {
		return applydept;
	}

	public void setApplydept(String applydept) {
		this.applydept = applydept;
	}

	public String getApplytype() {
		return applytype;
	}

	public void setApplytype(String applytype) {
		this.applytype = applytype;
	}

	public Long getAdduserid() {
		return adduserid;
	}

	public void setAdduserid(Long adduserid) {
		this.adduserid = adduserid;
	}

	public Timestamp getSoldtime() {
		return soldtime;
	}

	public void setSoldtime(Timestamp soldtime) {
		this.soldtime = soldtime;
	}

	public Long getSellerid() {
		return sellerid;
	}

	public void setSellerid(Long sellerid) {
		this.sellerid = sellerid;
	}

	public String getSellremark() {
		return sellremark;
	}

	public void setSellremark(String sellremark) {
		this.sellremark = sellremark;
	}

	public Integer getSellprice() {
		return sellprice;
	}

	public void setSellprice(Integer sellprice) {
		this.sellprice = sellprice;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
	public Timestamp getAddtime() {
		return addtime;
	}
	public void setAddtime(Timestamp addtime) {
		this.addtime = addtime;
	}
	public Integer getUsedcount() {
		return usedcount;
	}
	public void setUsedcount(Integer usedcount) {
		this.usedcount = usedcount;
	}
	public Timestamp getStatstime() {
		return statstime;
	}
	public void setStatstime(Timestamp statstime) {
		this.statstime = statstime;
	}
	public Integer getDelcount() {
		return delcount;
	}
	public void setDelcount(Integer delcount) {
		this.delcount = delcount;
	}
	public Integer getNewcount() {
		return newcount;
	}
	public void setNewcount(Integer newcount) {
		this.newcount = newcount;
	}
	public Integer getSoldcount() {
		return soldcount;
	}
	public void setSoldcount(Integer soldcount) {
		this.soldcount = soldcount;
	}
	public String getMincardno() {
		return mincardno;
	}
	public void setMincardno(String mincardno) {
		this.mincardno = mincardno;
	}
	public String getMaxcardno() {
		return maxcardno;
	}
	public void setMaxcardno(String maxcardno) {
		this.maxcardno = maxcardno;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getCardcount() {
		return cardcount;
	}
	public void setCardcount(Integer cardcount) {
		this.cardcount = cardcount;
	}
	public Long getMerchantid() {
		return merchantid;
	}
	public void setMerchantid(Long merchantid) {
		this.merchantid = merchantid;
	}
	public Integer getLockcount() {
		return lockcount;
	}
	public void setLockcount(Integer lockcount) {
		this.lockcount = lockcount;
	}
	public Integer getIssuecount() {
		return issuecount;
	}
	public void setIssuecount(Integer issuecount) {
		this.issuecount = issuecount;
	}
}
