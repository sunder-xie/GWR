/**
 * 
 */
package com.gewara.service.bbs;

import java.util.List;
import java.util.Map;

import com.gewara.model.content.Picture;
import com.gewara.model.user.Album;
import com.gewara.model.user.AlbumComment;


/**
 * @author chenhao(sky_stefanie@hotmail.com)
 */
public interface AlbumService {

	/**ͨ���û�id��ѯ����б�
	 * @param id
	 * @return
	 */
	List<Album> getAlbumListByMemberId(Long id, int from, int maxnum);
	/**ͨ���û�id��ѯ�������
	 * @param id
	 * @return
	 */
	int getAlbumListCountByMemberId(Long id);
	/**
	 * ͨ���û�id��ѯ����б�(id, subject)
	 * @param memberid
	 * @return
	 */
	List<Map> getAlbumListByMemberId(Long memberid);

	/**
	 * ����albumid��ѯ����ͼƬ + ����
	 */
	List<Picture> getPictureByAlbumId(Long albumid, int from, int maxnum);
	Integer getPictureountByAlbumId(Long albumid);
	
	/**
	 * ����ͼƬid��ѯͼƬ�ظ��б�
	 * @param imageid
	 * @return
	 */
	List<AlbumComment> getPictureComment(Long imageid, int from, int maxnum);
	/**
	 * ����Ȧ��id��ѯ����Ȧ�ӵ���Ƭ��Ϣ
	 */
	List<Picture> getPicturesByCommuidList(Long commuid,int from,int maxnum);
	
	/**
	 * ͨ���û�ID��ѯ���û����ѵ������Ϣ
	 */
	List<Album> getFriendAlbumListByMemberId(Long memberid,int from,int maxnum);
	Integer getFriendAlbumCountByMemberId(Long memberid);
	
	List<Long> getMemberIdListByAlbumComment(Long imageid, int from, int maxnum);
	//�����û�ID��Ȧ��ID�õ�����б�
	List<Album> getAlbumListByMemberIdOrCommuId(Long memberid, Long commuid, String searchKey, int from, int maxnum);
	//�����û�ID��Ȧ��ID�õ��������
	Integer getAlbumCountByMemberIdOrCommuId(Long memberid, Long commuid, String searchKey);
}
