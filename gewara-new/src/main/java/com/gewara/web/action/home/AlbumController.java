/**
 * 
 */
package com.gewara.web.action.home;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.Config;
import com.gewara.constant.Status;
import com.gewara.constant.TagConstant;
import com.gewara.json.MemberStats;
import com.gewara.model.acl.GewaraUser;
import com.gewara.model.bbs.commu.Commu;
import com.gewara.model.bbs.commu.CommuMember;
import com.gewara.model.content.Picture;
import com.gewara.model.user.Album;
import com.gewara.model.user.AlbumComment;
import com.gewara.model.user.Member;
import com.gewara.model.user.MemberPicture;
import com.gewara.service.bbs.AlbumService;
import com.gewara.service.bbs.CommuService;
import com.gewara.service.member.FriendService;
import com.gewara.support.ErrorCode;
import com.gewara.support.ServiceHelper;
import com.gewara.untrans.CommentService;
import com.gewara.untrans.GewaPicService;
import com.gewara.untrans.ShareService;
import com.gewara.util.BeanUtil;
import com.gewara.util.BindUtils;
import com.gewara.util.JsonUtils;
import com.gewara.util.PictureUtil;
import com.gewara.util.StringUtil;
import com.gewara.util.WebUtils;
import com.gewara.util.XSSFilter;
import com.gewara.web.action.BaseHomeController;
import com.gewara.web.util.PageUtil;
import com.gewara.xmlbind.bbs.Comment;

/**
 * @author hxs(ncng_2006@hotmail.com)
 * @since Jan 27, 2010 10:09:44 AM
 */
@Controller
public class AlbumController extends BaseHomeController {
	@Autowired@Qualifier("commuService")
	private CommuService commuService;
	public void setCommuService(CommuService commuService) {
		this.commuService = commuService;
	}
	@Autowired@Qualifier("shareService")
	private ShareService shareService;
	public void setShareService(ShareService shareService) {
		this.shareService = shareService;
	}
	@Autowired@Qualifier("commentService")
	private CommentService commentService;
	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}
	@Autowired@Qualifier("friendService")
	private FriendService friendService;
	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}
	@Autowired@Qualifier("config")
	private Config config;
	public void setConfig(Config config) {
		this.config = config;
	}
	@Autowired@Qualifier("gewaPicService")
	private GewaPicService gewaPicService;
	public void setGewaPicService(GewaPicService gewaPicService) {
		this.gewaPicService = gewaPicService;
	}
	@Autowired@Qualifier("albumService")
	private AlbumService albumService;
	public void setAlbumService(AlbumService albumService) {
		this.albumService = albumService;
	}
	private static final List<String> specialTagList = new ArrayList<String>();
	static{
		specialTagList.add("member");
		specialTagList.add("commu");
	}
	//�û����
	@RequestMapping("/home/album/albumList.xhtml")
	public String position(ModelMap model, Integer pageNo,String type,Long memberid){
		Member mymember = getLogonMember();
		if(memberid==null){//�Լ�
			memberid = mymember.getId();
		}
		//�жϷ���Ȩ��
		if(memberid!=null&&!memberid.equals(mymember.getId())){
			model.putAll(friendService.isPrivate(memberid));
		}
		model.putAll(controllerService.getCommonData(model, mymember, memberid));
		Member member = (Member) model.get("member");
		if(member == null) return show404(model, "��ˢ�����ԣ�");
		if(pageNo==null) pageNo=0;
		int rowsPerPage=12;
		int start = pageNo * rowsPerPage;
		int count=0;
		List<Album> albumList = null;
		Map<Long,Integer> imageNum = new HashMap<Long, Integer>();
		//�������
		if("friend".equals(type)){
			albumList = albumService.getFriendAlbumListByMemberId(member.getId(), start, rowsPerPage);
			count = albumService.getFriendAlbumCountByMemberId(member.getId());
		}else{
			//�ҵ����
			albumList = albumService.getAlbumListByMemberId(member.getId(), start, rowsPerPage);
			count=albumService.getAlbumListCountByMemberId(member.getId());
		}
		for(Album album:albumList){
			Integer num = albumService.getPictureountByAlbumId(album.getId());
			imageNum.put(album.getId(), num);
		}
		PageUtil pageUtil=new PageUtil(count,rowsPerPage,pageNo,"/home/album/albumList.xhtml", true, true);
		Map params = new HashMap(); 
		params.put("memberid", new String[]{memberid+""});
		params.put("type",type);
		pageUtil.initPageInfo(params);
		model.put("albumMap", albumService.getAlbumListByMemberId(member.getId()));
		model.put("albumList", albumList);
		model.put("imageNum", imageNum);
		model.put("count", count);
		model.put("pageUtil",pageUtil);
		return "home/album/albumList.vm";
	}

	//���ͼƬ
	@RequestMapping("/home/album/albumImageList.xhtml")
	public String albumImageList(Long albumid, Integer pageNo,ModelMap model){
		Album album = daoService.getObject(Album.class, albumid);
		if(album == null) return showError(model, "�����ʵ�ҳ�治���ڣ�");
		if(album.getCommuid()>0) return showError(model, "����ʵ���᲻���ڣ�");
		Member member = getLogonMember();
		Member albumMember = null;
		if(!album.getMemberid().equals(member.getId())){
			model.put("isMyFriend", friendService.isFriend(member.getId(), album.getMemberid()));
			albumMember = daoService.getObject(Member.class, album.getMemberid());
			if(albumMember == null) return showError(model, "���û������ڣ�");
			model.putAll(friendService.isPrivate(album.getMemberid()));		//�жϷ���Ȩ��
		}else albumMember = member;
		model.putAll(controllerService.getCommonData(model, member, albumMember.getId()));
		isShowAlbumImage(albumMember, getLogonMember(), model, album);
		if(pageNo==null) pageNo=0;
		int rowsPerPage=20;
		int start = pageNo * rowsPerPage;
		int count=0;
		List<Picture> albumImageList = albumService.getPictureByAlbumId(albumid, start, rowsPerPage);
		Map<Long,Long> imageNum = new HashMap<Long, Long>();
		count = albumService.getPictureountByAlbumId(albumid);
		PageUtil pageUtil = new PageUtil(count, rowsPerPage, pageNo, "/home/album/albumImageList.xhtml", true, true);
		Map params = new HashMap(); 
		params.put("albumid", albumid);
		pageUtil.initPageInfo(params);
		model.put("albumMap", albumService.getAlbumListByMemberId(albumMember.getId()));
		model.put("imageNum",imageNum);
		model.put("pageUtil",pageUtil);
		model.put("albumid", albumid);
		model.put("albumImageList",albumImageList);
		model.put("album",album);
		return "home/album/albumImageList.vm";
	}
	
	
	/**
	 * ����������Ȩ��
	 * @param model
	 * @param albumid
	 * @param commuid
	 * @return
	 */
	private String isShowAlbumImage(Member mymember,Member loginMember,ModelMap model,Album album){
		if(mymember == loginMember){//�жϵ�ǰ���ʵ��Ƿ����Լ������
			model.put("isShowCommuAlbum", true);
		}else{
			model.put("isShowCommuAlbum", false);
		}
		boolean isFriend = Boolean.valueOf(model.get("isMyFriend")+"");
		if(!mymember.getId().equals(loginMember.getId())){
			if(TagConstant.ALBUM_PRIVATE.equals(album.getRights())){
				model.put("isCanShow", false); 
			}else if(TagConstant.ALBUM_FRIEND.equals(album.getRights()) && isFriend){
				model.put("isCanShow", true);
			}else if(TagConstant.ALBUM_PUBLIC.equals(album.getRights()) || album.getMemberid().equals(loginMember.getId())){
				model.put("isCanShow", true);
			}
		}else{
			model.put("isCanShow", true);
		}
		return null;
	}
	
	//�޸����
	@RequestMapping("/home/album/modAlbum.xhtml")
	public String addAlbum(ModelMap model, Long albumid, Long commuid){
		Member member = getLogonMember();
		Album album = daoService.getObject(Album.class, albumid);
		if(commuid != null){
			Commu commu = daoService.getObject(Commu.class, commuid);
			if(commu == null){
				return showJsonError_DATAERROR(model);
			}
			if(!commu.hasStatus(Status.Y)) return showJsonError(model, "������Ȧ���Ѿ���ɾ����");
			boolean b = commuService.isCommuMember(commuid, member.getId());
			if(!b) return showJsonError(model, "����Ȩ���˲�����");
			if(!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())&&!member.getId().equals(album.getMemberid())) return showJsonError(model, "ֻ�й���Ա����Ȩ�޲�����");
		}else{
			if(!member.getId().equals(album.getMemberid())) return showJsonError(model, "��û��Ȩ�ޣ�");
		}
		Map map = BeanUtil.getBeanMapWithKey(album, "id", "subject","commuid", "rights", "description");
		map.put("albumid", album.getId());
		map.put("albumupdate", true);
		return showJsonSuccess(model, map);
	}
	//�������
	@RequestMapping("/home/album/createAlbum.xhtml")
	public String createAlbum(Long albumid, Long commuid, String captchaId, String captcha, HttpServletRequest request, ModelMap model){
		boolean isValidCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!isValidCaptcha) return showJsonError(model, "��֤�����");
		Member member = getLogonMember();
		if(member == null) return showJsonError_NOT_LOGIN(model);
		Album album = null;
		if(albumid != null){
			album = daoService.getObject(Album.class, albumid);
		}else{
			album = new Album(member.getId());
		}
		if(commuid != null){
			CommuMember commuMember = commuService.getCommuMemberByMemberidAndCommuid(member.getId(), commuid);
			if(CommuMember.FLAG_BLACK.equals(commuMember.getFlag())){
				return showJsonError(model, "�㱻����С���ݣ���ʱ���ܴ�����ᣡ");
			}
			
			Commu commu = daoService.getObject(Commu.class, commuid);
			if(commu == null){
				return showJsonError_DATAERROR(model);
			}
			if(!commu.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
			boolean b = commuService.isCommuMember(commuid, member.getId());
			if(!b) return showError(model, "����Ȩ���˲�����");
			if(albumid != null&&!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())&&!member.getId().equals(album.getMemberid())) return showJsonError(model, "ֻ�й���Ա�봴���߲���Ȩ�޲�����");
		}else if(albumid != null){
			if(!member.getId().equals(album.getMemberid())) return showJsonError(model, "��û��Ȩ�ޣ�");
		}
		BindUtils.bindData(album, request.getParameterMap());
		album.setMemberid(member.getId());
		if(commuid!=null){album.setCommuid(commuid);}
		album = XSSFilter.filterObjAttrs(album, "subject","description");
		daoService.saveObject(album);
		Map map = new HashMap();
		map.put("album",BeanUtil.getBeanMap(album));
		return showJsonSuccess(model,map);
	}
	//ɾ�����
	@RequestMapping("/home/album/delAlbum.xhtml")
	public String delAlbum(Long albumid, Long commuid, ModelMap model){
		Member member = getLogonMember();
		Album album = daoService.getObject(Album.class, albumid);
		if(album == null) return showJsonError_DATAERROR(model);
		if(commuid != null){
			Commu commu = daoService.getObject(Commu.class, commuid);
			if(commu == null){
				return showJsonError_DATAERROR(model);
			}
			if(!commu.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
			if(!album.getMemberid().equals(member.getId())&&!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())) return showJsonError(model, "ֻ�й���Ա�봴���߲���Ȩ�޲�����");
			album.setCommuid(0l);
			daoService.updateObject(album);
		}else{
			if(!member.getId().equals(album.getMemberid())) return showJsonError(model, "��û��Ȩ��ɾ����ᣡ");
			daoService.removeObject(album);
			daoService.removeObjectList(albumService.getPictureByAlbumId(album.getId(), -1, -1));
		}
		return showJsonSuccess(model);
	}
	//ɾ����Ƭ
	@RequestMapping("/home/album/delAlbumImage.xhtml")
	public String delAlbumImage(Long imageid, Long commuid, ModelMap model){
		Member member = getLogonMember();
		Picture albumImage = daoService.getObject(Picture.class, imageid);
		Album album = daoService.getObject(Album.class, albumImage.getRelatedid());
		if(commuid != null){
			Commu commu = daoService.getObject(Commu.class, commuid);
			if(commu == null){
				return showJsonError_DATAERROR(model);
			}
			if(!commu.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
			if(!album.getMemberid().equals(member.getId())&&!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())) return showJsonError(model, "ֻ�й���Ա�봴���߲���Ȩ�޲�����");
		}else{
			if(!album.getMemberid().equals(member.getId())) return showJsonError(model, "��û��Ȩ��ɾ��ͼƬ��");
		}
		daoService.removeObject(albumImage); //֮ǰ�ķ���
		try {
			boolean isSuc = gewaPicService.removePicture(albumImage.getPicturename());
			if(isSuc) return showJsonSuccess(model);	// �����ķ���
		} catch (IOException e) {
			dbLogger.error(StringUtil.getExceptionTrace(e));
			return showJsonError_DATAERROR(model);
		}
		return "";
	}
	//�޸���Ƭ
	@RequestMapping("/home/album/updateAlbumImage.xhtml")
	public String updateAlbumImage(Long imageid, Long commuid, String name, ModelMap model){
		Member member = getLogonMember();
		if(name == null) return showJsonError(model, "ͼƬ���Ʋ���Ϊ�գ�");
		Picture albumImage = daoService.getObject(Picture.class, imageid);
		Album album = daoService.getObject(Album.class, albumImage.getRelatedid());
		if(commuid != null){
			Commu commu = daoService.getObject(Commu.class, commuid);
			if(commu == null){
				return showJsonError_DATAERROR(model);
			}
			if(!commu.hasStatus(Status.Y)) return showJsonError(model, "��Ȧ���Ѿ���ɾ����");
			boolean b = commuService.isCommuMember(commuid, member.getId());
			if(!b) return showError(model, "����Ȩ���˲�����");
			if(!album.getMemberid().equals(member.getId())&&!member.getId().equals(commu.getAdminid())&&!member.getId().equals(commu.getSubadminid())) return showJsonError(model, "ֻ�й���Ա�봴���߲���Ȩ�޲�����");
		}else{
			if(!member.getId().equals(album.getMemberid())) return showJsonError(model, "��û��Ȩ��ɾ��ͼƬ��");
		}
		albumImage.setName(name);
		daoService.saveObject(albumImage);
		return showJsonSuccess(model);
	}
	@RequestMapping("/home/album/uploadAlbumImages.xhtml")
	public String uploadAlbumImages(String realName, String successFile, HttpServletRequest request,
			String tag,Long relatedid,String redirecturl, Long albumid, ModelMap model) throws Exception {
		Member member = getLogonMember();
		Object obj = relateService.getRelatedObject(tag, relatedid);
		if(obj == null) return goBack(model, "��������");
		if("commu".equals(tag)&&relatedid != null){
			CommuMember commuMember=commuService.getCommuMemberByMemberidAndCommuid(member.getId(), relatedid);
			if(CommuMember.FLAG_BLACK.equals(commuMember.getFlag())){
				return goBack(model, "�㱻����С���ݣ���ʱ�����ϴ�ͼƬ��");
			}
		}
		
		String[] fileNames = StringUtils.split(successFile, "@@");
		String[] fileFields = StringUtils.split(realName, "@@");
		List<Picture> albumImageList = new ArrayList<Picture>();//�û���Ȧ���ϴ�ͼƬ
		List<Picture> pictureList = new ArrayList<Picture>();//��ȥȦ�ӣ��û�����ϴ�ͼƬ
		List<MemberPicture> mpList = new ArrayList<MemberPicture>();
		int index = 0;
		String path = PictureUtil.getAlbumPicpath();
		Album album = new Album();
		List<Long> pictureIdList = new ArrayList<Long>();
		for (String fileName : fileNames) {
			if(TagConstant.TAGList.contains(tag)) {
				gewaPicService.moveRemoteTempTo(member.getId(), tag, relatedid, path, fileName);
				MemberPicture mp = new MemberPicture(tag, relatedid, member.getId(), member.getNickname(), path + fileName, TagConstant.FLAG_PIC);
				mp.setName(fileFields[index]);
				mp.setDescription(fileFields[index]);
				daoService.saveObject(mp);
				mpList.add(mp);
			}else if(specialTagList.contains(tag)){ //Ȧ�������û����
				gewaPicService.moveRemoteTempTo(member.getId(), "album", albumid, path, fileName);
				Picture ai = new Picture("album", albumid, member.getId(), path + fileName);
				ai.setMemberType(GewaraUser.USER_TYPE_MEMBER);
				ai.setName(fileFields[index]);
				ai.setDescription(fileFields[index]);
				daoService.saveObject(ai);
				album = daoService.getObject(Album.class, albumid);
				
				if(StringUtils.isBlank(album.getLogo())){
					album.setLogo(ai.getLogo());
					daoService.saveObject(album);
				}
				albumImageList.add(ai);
				pictureIdList.add(ai.getId());
			}else if("activity".equals(tag)){ //���������
				gewaPicService.moveRemoteTempTo(member.getId(), tag, relatedid, path, fileName);
				Picture picture=new Picture(tag);
				picture.setRelatedid(relatedid);
				picture.setMemberid(member.getId());
				picture.setMemberType(GewaraUser.USER_TYPE_MEMBER);
				picture.setName(fileFields[index]);
				picture.setDescription(fileFields[index]);
				picture.setPicturename(path + fileName);
				daoService.saveObject(picture);
				pictureList.add(picture);
			}
			index++;
		}
		model.put("redirecturl", redirecturl);
		model.put("relatedtag",tag);
		if(!albumImageList.isEmpty()){
			model.put("albumImageList", albumImageList);
			List<Album> albumList = daoService.getObjectList(Album.class,BeanUtil.getBeanPropertyList(albumImageList,Long.class,"albumid" , true));
			model.put("albumsMap",BeanUtil.beanListToMap(albumList, "id"));
		}
		if(!pictureList.isEmpty()){
			model.put("pictureList", pictureList);
		}else if(!mpList.isEmpty()){
			model.put("pictureList", mpList);
		}
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		//��������
		//��������+1
		memberCountService.updateMemberCount(member.getId(), MemberStats.FIELD_COMMENTCOUNT, 1, true);
		//��������+1
		String link = config.getBasePath() + "home/album/albumImageList.xhtml?albumid=" + albumid;
		String linkStr = "�ϴ���" +fileNames.length+"������Ƭ��" +"<a href=\""+link+"\" target=\"_blank\">"+album.getSubject()+"</a>";
		Map otherinfoMap = new HashMap();
		otherinfoMap.put("albumid", albumid);
		otherinfoMap.put("imagecount", fileFields.length);
		if(fileNames.length>0){
			otherinfoMap.put("image1", path+fileNames[0]);
			otherinfoMap.put("imageid1", pictureIdList.get(0));
		}
		if(fileNames.length>1){
			otherinfoMap.put("image2", path+fileNames[1]);
			otherinfoMap.put("imageid2", pictureIdList.get(1));
		}
		if(fileNames.length>2){
			otherinfoMap.put("image3", path+fileNames[2]);
			otherinfoMap.put("imageid3", pictureIdList.get(2));
		}
		if(fileNames.length>3){
			otherinfoMap.put("image4", path+fileNames[3]);
			otherinfoMap.put("imageid4", pictureIdList.get(3));
		}
		String otherinfo = JsonUtils.writeObjectToJson(otherinfoMap);
		String ip = WebUtils.getRemoteIp(request);
		ErrorCode<Comment> ec = null;
		if(TagConstant.TAGList.contains(tag)) {
			ec = commentService.addMicroComment(member, TagConstant.TAG_MEMBERPICTURE_MEMBER, null, linkStr,null , null, null, false, null, otherinfo,null,null, WebUtils.getIpAndPort(ip, request), null);
		}else{
			ec = commentService.addMicroComment(member, TagConstant.TAG_PICTURE_MEMBER, null, linkStr,null , null, null, false, null, otherinfo,null,null, WebUtils.getIpAndPort(ip, request), null);
		}
		if(ec!=null){
			if(ec.isSuccess()){
				shareService.sendShareInfo("wala",ec.getRetval().getId(), ec.getRetval().getMemberid(), null);
			}
		}
		return "home/album/editUpload.vm";
	}
	
	

	//������Ƭ����
	@RequestMapping("/home/album/saveAlbumComment.xhtml")
	public String saveAlbumComment(Long imageid,String body, ModelMap model){
		Member member = getLogonMember();
		if(member == null) return showError(model, "���ȵ�¼!");
		Picture albumImage = daoService.getObject(Picture.class, imageid);
		if(albumImage == null) return alertMessage(model, "ͼƬ��Ϣ����ȷ��");
		Album album = daoService.getObject(Album.class, albumImage.getRelatedid());
		if(album == null) return alertMessage(model, "�����Ϣ����ȷ��");
		if(body.length() > 200) return alertMessage(model, "����̫����");
		AlbumComment albumComment = new AlbumComment(member.getId());
		albumComment.setAlbumid(album.getId());
		albumComment.setImageid(imageid);
		albumComment.setBody(body);
		daoService.saveObject(albumComment);
		model.put("comment", albumComment);
	    addCacheMember(model, albumComment.getMemberid());
		 
		if(album.getMemberid().equals(member.getId()) || albumComment.getMemberid().equals(member.getId())) {
			model.put("isShowCommuAlbum",true);
		}
		Member commentMember = daoService.getObject(Member.class, albumComment.getMemberid());
		model.put("commentMember", commentMember);
		return "home/album/loadAlbumComment.vm";
	}
	//ɾ��ͼƬ�ظ�
	@RequestMapping("/home/album/deleteAlbumComment.xhtml")
	public String deleteAlbumComment(Long commentid, ModelMap model){
		AlbumComment albumComment = daoService.getObject(AlbumComment.class, commentid);
		if(albumComment == null) return showJsonError(model, "��������");
		daoService.removeObject(albumComment);
		return showJsonSuccess(model);
	}
	
	/**
	 * �ϴ�ͼƬ
	 */
	@RequestMapping("/home/uploadPicture.xhtml")
	public String uploadPicture(String tag, Long relatedid, Long albumid,ModelMap model){
		Member member = getLogonMember();
		model.putAll(controllerService.getCommonData(model, member, member.getId()));
		model.put("url", "home/album/albumList.xhtml?memberid=" + member.getId());
		model.put("relatedtag", tag);
		model.put("relatedid", relatedid);
		model.put("albumid", albumid);
		if(specialTagList.contains(tag)){
			if("commu".equals(tag)){//�ϴ�Ȧ�����
				model.put("commu",daoService.getObject(Commu.class, relatedid));
				model.put("albumMap", BeanUtil.beanListToMap(commuService.getCommuAlbumById(relatedid, 0, 1000), "id", "subject", false));
			}else{//�ϴ��û����
				model.put("albumMap", BeanUtil.beanListToMap(albumService.getAlbumListByMemberId(relatedid, 0, 1000), "id", "subject", false));
			}
		}
		return "home/album/imgUpload.vm";
	}
	
	/**
	 * �����޸���Ƭ����
	 */
	@RequestMapping("/home/updatePictureInfo.xhtml")
	public String updatePictureInfo(ModelMap model,HttpServletRequest request){
		String tag = request.getParameter("tag");
		String redirectUrl = request.getParameter("redirectUrl");
		model.put("redirectUrl", redirectUrl);
		String isalbumcover = request.getParameter("isalbumcover");
		String[] ids = request.getParameterValues("id");
		if(ids == null) return show404(model, "���Ժ�ˢ��ҳ������!");
		String[] names = request.getParameterValues("name");
		String[] descriptions = request.getParameterValues("description");
		if(specialTagList.contains(tag)){
			for(int i = 0;i<ids.length;i++){
				Picture ai = daoService.getObject(Picture.class,Long.valueOf(ids[i]+""));
				if(ai!=null){
					if((ai.getId()+"").equals(isalbumcover)){
						Album album = daoService.getObject(Album.class, ai.getRelatedid());
						album.setLogo(ai.getLogo());
						daoService.updateObject(album);
					}
					ai.setDescription(descriptions[i]);
					ai.setName(names[i]);
					daoService.updateObject(ai);
				}
			}
		}else{
			for(int i = 0;i<ids.length;i++){
				Picture picture = daoService.getObject(Picture.class, Long.valueOf(ids[i]+""));
				if(picture !=null){
					picture.setName(names[i]);
					picture.setDescription(descriptions[i]);
					daoService.updateObject(picture);
				}
			}
		}
		return "tempRedirect.vm";
	}
	
	/**
	 * �û����ͼƬ��ϸ
	 */
	@RequestMapping("/home/album/imageDetailList.xhtml")
	public String imageDetailList(ModelMap model,Long albumid, Long curAlbumPicId){
		Album album = daoService.getObject(Album.class, albumid);
		if(album == null) return showError(model, "����ʵ�ҳ�治���ڣ�");
		if(album.getCommuid()>0) return showError(model, "����ʵ���᲻���ڣ�");
		Member member = getLogonMember();
		Member albumMember = null;
		if(!member.getId().equals(album.getMemberid())){
			model.put("isMyFriend", friendService.isFriend(member.getId(), album.getMemberid()));
			albumMember = daoService.getObject(Member.class,album.getMemberid());
			model.putAll(friendService.isPrivate(album.getMemberid()));		//�жϷ���Ȩ��
		}else albumMember = member;
		model.putAll(controllerService.getCommonData(model, member, album.getMemberid()));
		isShowAlbumImage(albumMember, getLogonMember(), model, album);
		Map<Long,String[]> albumImageMap = new LinkedHashMap<Long, String[]>();
		List<Picture> albumImageList = albumService.getPictureByAlbumId(albumid, 0, 500);
		for (Picture ai : albumImageList) {
			String[] albumimages = new String[3];
			albumimages[0] = ai.getLogo();
			albumimages[1] = ai.getDescription()==null?"��һ������ʲô��û���£�":ai.getDescription();
			albumimages[2] = ai.getName()==null?"��һ������ʲô��û���£�":ai.getName();
			albumImageMap.put(ai.getId(), albumimages);
		}
		model.put("curAlbumMember", daoService.getObject(Member.class, album.getMemberid()));
		model.put("albumImageMap", albumImageMap);
		model.put("curAlbumImage", daoService.getObject(Picture.class, curAlbumPicId));
		model.put("albumid", albumid);
		model.put("curAlbum", album);
		
		List<AlbumComment> imageCommentList = albumService.getPictureComment(curAlbumPicId, 0,30);
		if(imageCommentList.size()>0){
			Map<Long, Member> memberMap = new HashMap<Long, Member>();
			List<Long> commentMemberIdList = ServiceHelper.getMemberIdListFromBeanList(imageCommentList);
			List<Member> memberList = daoService.getObjectList(Member.class, commentMemberIdList);
			memberMap = BeanUtil.beanListToMap(memberList, "id");
			model.put("memberMap",memberMap);
		}
		model.put("imageCommentList", imageCommentList);
		model.put("logonMember", member);
		return "home/album/imageDetail.vm";
	}
	
	/**
	 * �޸�ͼƬ���ƣ�����
	 */
	@RequestMapping("/image/updateImageInfo.xhtml")
	public String updateImageNameOrDescript(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, HttpServletRequest request, ModelMap model,String name,String description,Long albumimageid){
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		Picture ai = daoService.getObject(Picture.class, albumimageid);
		if(ai == null) return showJsonError(model, "��������");
		Album album = daoService.getObject(Album.class, ai.getRelatedid());
		if(album == null) return showJsonError(model, "��᲻���ڣ�");
		if(!member.getId().equals(album.getMemberid())){
			return showJsonError(model, "����Ȩ�����˲�����");
		}
		String value = "";
		if(StringUtils.isNotBlank(name)){
			ai.setName(name);
			value = name;
		}
		if(StringUtils.isNotBlank(description)){
			ai.setDescription(description);
			value = description;
		}
		daoService.updateObject(ai);
		if(StringUtils.isBlank(value)){
			value = "��һ������ʲô��û���£�";
		}
		return showJsonSuccess(model,value);
	}
	
	/**
	 * ����������
	 */
	@RequestMapping("/home/album/setAlbumCover.xhtml")
	public String setAlbumCover(Long albumId,String imageUrl,ModelMap model){
		Member member = getLogonMember();
		Album album = daoService.getObject(Album.class, albumId);
		if(member.getId().equals(album.getMemberid())){
			album.setLogo(imageUrl);
			daoService.updateObject(album);
			return showJsonSuccess(model);
		}else{
			return showJsonError(model, "����Ȩ�����˲�����");
		}
	}
	
	
	//����ͼƬ�ظ�
	@RequestMapping("/quan/album/albumCommentList.xhtml")
	public String albumCommentList(@CookieValue(value=LOGIN_COOKIE_NAME, required=false)String sessid, HttpServletRequest request, Long imageid,ModelMap model){
		Member member = loginService.getLogonMemberBySessid(WebUtils.getRemoteIp(request), sessid);
		Picture albumImage = daoService.getObject(Picture.class, imageid);
		if(albumImage == null) return alertMessage(model, "����ͼƬid��");
		Album album = daoService.getObject(Album.class, albumImage.getRelatedid());
		List<AlbumComment> imageCommentList = albumService.getPictureComment(imageid, 0,30);
		if(imageCommentList.size()>0){
			List<Long> commentMemberIdList = ServiceHelper.getMemberIdListFromBeanList(imageCommentList);
			addCacheMember(model, commentMemberIdList);
		}
		if(member!=null){
			if(album.getMemberid().equals(member.getId())) {
				model.put("isShowCommuAlbum",true);
			}
		}
		model.put("albumid",albumImage.getId());
		model.put("imageid",imageid);
		model.put("logonMember", member);
		model.put("imageCommentList", imageCommentList);
		return "home/album/albumImageComment.vm";
	}
}
