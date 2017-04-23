package com.gewara.command;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class CommentCommand {
	public static final String TYPE_MODERATOR = "moderator";
	private Integer pageNumber;	//��ҳҳ��
	private Integer maxCount;		//����
	public String tag;			//��������
	public Long relatedid;		//����ID
	public String micrbody;		// ����
	public String bodypic;		//ͼƬ��ַ
	public String video;		//��Ƶ
	public String link;			//����
	public Integer generalmark;	//��������
	public String marks;		// ������
	private String isLongWala;	//�Ƿ��ǳ�����
	public String pointxy;		//��γ��
	public String order;   // ��������flowernum ��������
	private String flag;  //ֵΪticket��ѯ��Ʊ�û�����������
	
	//��ѯ��
	private String hasMarks;	//�Ƿ�������
	private String pages;		//�Ƿ��ҳ
	private String isPic;		//�Ƿ���ͼƬ
	private String isVideo;		//�Ƿ�������
	private String isFloor;		//�Ƿ���Ҫ¥��
	private String isCount;		//�Ƿ���Ҫ����
	public String title;		//����
	private String issue;		//�Ƿ��з����
	private String isJson;		//�Ƿ��Ƿ���json
	private String isRight;		//�Ƿ����ұߵ�����
	private String isWide;		//�Ƿ����
	private String isTicket;    //�Ƿ���Ҫ��ʾ��Ʊ������ѯ
	private String startTime;		//��ѯ������ʱ��
	private String endTime;
	
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
	public String getMicrbody() {
		return micrbody;
	}
	public void setMicrbody(String micrbody) {
		this.micrbody = micrbody;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Integer getGeneralmark() {
		return generalmark;
	}
	public void setGeneralmark(Integer generalmark) {
		this.generalmark = generalmark;
	}
	public String getBodypic() {
		return bodypic;
	}
	public void setBodypic(String bodypic) {
		this.bodypic = bodypic;
	}
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	public String getMarks() {
		return marks;
	}
	public void setMarks(String marks) {
		this.marks = marks;
	}
	
	public Integer getPageNumber() {
		if(pageNumber == null || pageNumber<0) pageNumber = 0;
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getMaxCount() {
		if(maxCount == null || maxCount <1) maxCount = 6;
		return maxCount;
	}
	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}
	public String getIsLongWala() {
		return isLongWala;
	}
	public void setIsLongWala(String isLongWala) {
		this.isLongWala = isLongWala;
	}
	
	public boolean hasLongWala(){
		return Boolean.parseBoolean(isLongWala);
	}
	
	public String getPointxy() {
		return pointxy;
	}
	public void setPointxy(String pointxy) {
		this.pointxy = pointxy;
	}
	public String getHasMarks() {
		return hasMarks;
	}
	public void setHasMarks(String hasMarks) {
		this.hasMarks = hasMarks;
	}
	
	public boolean hasMarks(){
		return Boolean.parseBoolean(hasMarks);
	}
	
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	
	public boolean hasPages(){
		return Boolean.parseBoolean(pages);
	}
	
	public String getIsPic() {
		return isPic;
	}
	
	public void setIsPic(String isPic) {
		this.isPic = isPic;
	}
	
	public boolean hasPic(){
		return Boolean.parseBoolean(isPic);
	}
	
	public String getIsVideo() {
		return isVideo;
	}
	
	public void setIsVideo(String isVideo) {
		this.isVideo = isVideo;
	}
	
	public boolean hasVideo(){
		return Boolean.parseBoolean(isVideo);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	
	public boolean hasIssue(){
		return Boolean.parseBoolean(issue);
	}
	
	public static String getTypeModerator() {
		return TYPE_MODERATOR;
	}
	public String getIsJson() {
		return isJson;
	}
	public void setIsJson(String isJson) {
		this.isJson = isJson;
	}
	public String getIsRight() {
		return isRight;
	}
	public void setIsRight(String isRight) {
		this.isRight = isRight;
	}
	public boolean hasTag(String... tags){
		if(StringUtils.isBlank(tag) || ArrayUtils.isEmpty(tags)) return false;
		for (String str : tags) {
			if(StringUtils.equals(tag, str)) return true;
		}
		return false;
	}
	public String getIsFloor() {
		return isFloor;
	}
	public void setIsFloor(String isFloor) {
		this.isFloor = isFloor;
	}
	
	public boolean hasFloor(){
		return Boolean.parseBoolean(isFloor);
	}
	
	public String getIsCount() {
		return isCount;
	}
	public void setIsCount(String isCount) {
		this.isCount = isCount;
	}
	public boolean hasJson(){
		return Boolean.parseBoolean(isJson);
	}
	public boolean hasRight(){
		return Boolean.parseBoolean(isRight);
	}
	
	public boolean hasCount(){
		return Boolean.parseBoolean(isCount);
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getIsWide() {
		return isWide;
	}
	public void setIsWide(String isWide) {
		this.isWide = isWide;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getIsTicket() {
		return isTicket;
	}
	public void setIsTicket(String isTicket) {
		this.isTicket = isTicket;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
}
