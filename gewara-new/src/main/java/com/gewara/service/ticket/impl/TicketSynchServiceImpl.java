package com.gewara.service.ticket.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gewara.constant.Status;
import com.gewara.constant.content.FilmFestConstant;
import com.gewara.constant.sys.CacheConstant;
import com.gewara.constant.sys.JsonDataKey;
import com.gewara.constant.sys.LogTypeConstant;
import com.gewara.constant.ticket.OpiConstant;
import com.gewara.helper.ticket.UpdateMpiContainer;
import com.gewara.model.common.JsonData;
import com.gewara.model.movie.Cinema;
import com.gewara.model.movie.CinemaRoom;
import com.gewara.model.movie.Movie;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.model.movie.RoomSeat;
import com.gewara.model.movie.SpecialActivity;
import com.gewara.model.ticket.OpenPlayItem;
import com.gewara.service.impl.BaseServiceImpl;
import com.gewara.service.movie.MCPService;
import com.gewara.service.ticket.OpenPlayService;
import com.gewara.service.ticket.OpiManageService;
import com.gewara.service.ticket.TicketSynchService;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CacheService;
import com.gewara.untrans.monitor.MonitorService;
import com.gewara.untrans.monitor.RoleTag;
import com.gewara.untrans.ticket.RemoteTicketService;
import com.gewara.util.BeanUtil;
import com.gewara.util.ChangeEntry;
import com.gewara.util.DateUtil;
import com.gewara.util.JsonUtils;
import com.gewara.util.VmUtils;
import com.gewara.web.action.inner.util.TicketRemoteUtil;
import com.gewara.xmlbind.ticket.SynchPlayItem;
import com.gewara.xmlbind.ticket.TicketRoom;
import com.gewara.xmlbind.ticket.TicketRoomSeat;
import com.gewara.xmlbind.ticket.TicketRoomSeatList;

@Service("ticketSynchService")
public class TicketSynchServiceImpl extends BaseServiceImpl implements	TicketSynchService, InitializingBean {
	@Autowired@Qualifier("remoteTicketService")
	private RemoteTicketService remoteTicketService;
	
	@Autowired@Qualifier("monitorService")
	private MonitorService monitorService;
	
	@Autowired@Qualifier("mcpService")
	private MCPService mcpService;
	
	@Autowired@Qualifier("openPlayService")
	private OpenPlayService openPlayService;
	
	@Autowired@Qualifier("opiManageService")
	private OpiManageService opiManageService;
	
	@Autowired@Qualifier("cacheService")
	private CacheService cacheService;
	
	private Long sixteenFilmFest = null;
	@Override
	public void updateSynchPlayItem(UpdateMpiContainer container, SynchPlayItem synchPlayItem, MoviePlayItem mpi, Cinema cinema, CinemaRoom room, List<String> msgList){
		if(StringUtils.equals(synchPlayItem.getStatus(), Status.N)){//ɾ��
			if(mpi != null){
				String msg = "ɾ����Ƭ: cinemaid:" + synchPlayItem.getCinemaid() + ", mpi:" + JsonUtils.writeObjectToJson(mpi);
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_API, msg);
				opiManageService.saveMpiToHisData(mpi);
				container.addDelete(mpi.getId());
				baseDao.removeObject(mpi);
			}
		}else{//�޸ġ�����
			boolean isAdd = false;
			if(mpi == null) {
				mpi = mcpService.getUniqueMpi(synchPlayItem.getPartner(), room.getCinemaid(), room.getId(), synchPlayItem.gainPlaydate(), synchPlayItem.gainPlaytime());
				if(mpi!=null && StringUtils.isNotBlank(mpi.getSeqNo())){
					//֮ǰͬһʱ���������Ƭ����������˳��Σ�����Ҫɾ������
					opiManageService.saveMpiToHisData(mpi);
					container.addDelete(mpi.getId());
					baseDao.removeObject(mpi);
					hibernateTemplate.flush();//ɾ��ʱ��ǰ�ύ���
					mpi = null;
				}
			}
			if(mpi != null){//��ѯ�Ƿ����Լ�������Ƭ
				OpenPlayItem opi = baseDao.getObjectByUkey(OpenPlayItem.class, "mpid", mpi.getId(), true);
				//��ѯ�Ƿ��ǶԽ�ǰ������Ƭ������ǣ��򲻸���
				if(opi!=null && opi.hasGewara()) {
					//��ֹ��Ϣ�ظ�����
					Object first = cacheService.get(CacheConstant.REGION_HALFDAY, "ignoreShow" + synchPlayItem.getMpiseq());
					if(first==null){
						cacheService.set(CacheConstant.REGION_HALFDAY, "ignoreShow" + synchPlayItem.getMpiseq(), "1");
						msgList.add("δ�Խ�ǰ���ŵĳ��Σ�ϵͳ���Ը��£�" + OpiConstant.getFullDesc(opi));
					}else{
						msgList.add("IGNORE:δ�Խ�ǰ���ŵĳ��Σ�ϵͳ���Ը��£�" + OpiConstant.getFullDesc(opi));
					}
					return;
				}
			}else{
				boolean restore = opiManageService.restoreMpiFromHisData(synchPlayItem.getMpiseq());
				if(restore){
					mpi = baseDao.getObjectByUkey(MoviePlayItem.class, "seqNo", synchPlayItem.getMpiseq());
					opiManageService.changeMpiStatus(mpi.getId(), OpiConstant.STATUS_RECOVER, 0L);
				}else{
					mpi = new MoviePlayItem(new Timestamp(System.currentTimeMillis()));
				}
				isAdd = true;
			}
			ChangeEntry changeEntry = new ChangeEntry(mpi);
			Movie movie = baseDao.getObject(Movie.class, synchPlayItem.getMovieid());
			ErrorCode result = updateMpi(synchPlayItem, mpi, cinema, room, movie);
			baseDao.saveObject(mpi);
			if(isAdd){
				container.addInsert(mpi);
			}else if(StringUtils.isNotBlank(result.getMsg())) {
				container.addUpdate(mpi);
				msgList.add(result.getMsg());
			}
			monitorService.saveSysChangeLog(MoviePlayItem.class, mpi.getId(), changeEntry.getChangeMap(mpi));
		}
	}
	@Override
	public void updateSpiderPlayItem(SynchPlayItem synchPlayItem, MoviePlayItem mpi, Cinema cinema, List<String> msgList){
		if(StringUtils.equals(cinema.getBooking(), Cinema.BOOKING_OPEN)) {
			return;
		}
		if(StringUtils.equals(synchPlayItem.getStatus(), Status.N)){//ɾ��
			if(mpi != null){
				String msg = "ɾ����Ƭ: cinemaid:" + synchPlayItem.getCinemaid() + ", mpi:" + JsonUtils.writeObjectToJson(mpi);
				dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_API, msg);
				opiManageService.saveMpiToHisData(mpi);
				baseDao.removeObject(mpi);
			}
		}else{//�޸ġ�����
			boolean isupdate = true;
			if(mpi == null) {
				mpi = mcpService.getUniqueMpi2(cinema.getId(), synchPlayItem.getMovieid(), synchPlayItem.gainPlaydate(), synchPlayItem.gainPlaytime());
				if(mpi!=null && StringUtils.isNotBlank(mpi.getSeqNo())){
					//֮ǰͬһʱ���������Ƭ����������˳��Σ�����Ҫɾ������
					opiManageService.saveMpiToHisData(mpi);
					baseDao.removeObject(mpi);
					hibernateTemplate.flush();//ɾ��ʱ��ǰ�ύ���
					mpi = null;
				}
			}
			if(mpi == null){//��ѯ�Ƿ����Լ�������Ƭ
				mpi = new MoviePlayItem(new Timestamp(System.currentTimeMillis()));
				isupdate = false;
			}
			ChangeEntry changeEntry = new ChangeEntry(mpi);
			Movie movie = baseDao.getObject(Movie.class, synchPlayItem.getMovieid());
			ErrorCode result = updateMpi(synchPlayItem, mpi, cinema, null, movie);
			baseDao.saveObject(mpi);
			if(isupdate){
				msgList.add(result.getMsg());
			}
			monitorService.saveSysChangeLog(MoviePlayItem.class, mpi.getId(), changeEntry.getChangeMap(mpi));
		}
	}
	private ErrorCode updateMpi(SynchPlayItem synchPlayItem, MoviePlayItem mpi, Cinema cinema, CinemaRoom room, Movie movie){
		String msg = "";
		mpi.setCitycode(cinema.getCitycode());
		mpi.setCinemaid(cinema.getId());
		mpi.setMovieid(synchPlayItem.getMovieid());
		String edition = synchPlayItem.getEdition();
		if(room!=null){
			mpi.setRoomid(room.getId());
			mpi.setRoomnum(room.getNum());
			mpi.setPlayroom(room.getRoomname());
			edition = OpiConstant.getDefaultEdition(synchPlayItem.getEdition(), room.getDefaultEdition());
		}else{
			mpi.setPlayroom(synchPlayItem.getRoomname());
		}
		mpi.setPlaydate(synchPlayItem.gainPlaydate());
		mpi.setPlaytime(synchPlayItem.gainPlaytime());
		mpi.setSeqNo(synchPlayItem.getMpiseq());
		mpi.setLowest(synchPlayItem.getLowest());
		mpi.setOpentype(synchPlayItem.getPartner());
		//TODO:��Ӱ������ʹ��
		if(StringUtils.equals(synchPlayItem.getPartner(), OpiConstant.OPEN_PNX)){
			mpi.setRemark(synchPlayItem.getOtherinfo());
			mpi.setBatch(sixteenFilmFest);
		}
		
		String language = synchPlayItem.getLanguage();
		if(StringUtils.equals(synchPlayItem.getStatus(), OpiConstant.STATUS_CLOSE)){
			mpi.setOpenStatus(OpiConstant.MPI_OPENSTATUS_DISABLED);
		}else if(StringUtils.equals(synchPlayItem.getStatus(), OpiConstant.STATUS_PAST)){
			mpi.setOpenStatus(OpiConstant.MPI_OPENSTATUS_PAST);
		}
		if(mpi.getId()==null){
			mpi.setLanguage(language);
			mpi.setEdition(edition);
			mpi.setPrice(synchPlayItem.getPrice());
		}else{
			if(!StringUtils.equals(mpi.getLanguage(), language) || 
					!StringUtils.equals(mpi.getEdition(), edition) || 
					mpi.getPrice()==null || !synchPlayItem.getPrice().equals(mpi.getPrice())){
				JsonData jd = baseDao.getObject(JsonData.class, JsonDataKey.KEY_EDITION_LANGUAGE + mpi.getId());
				if(jd ==null){
					mpi.setLanguage(language);
					mpi.setEdition(edition);
					mpi.setPrice(synchPlayItem.getPrice());
				}else{
					Map<String, String> oldMap = VmUtils.readJsonToMap(jd.getData());
					boolean updateJd = false;

					if(StringUtils.isBlank(oldMap.get("language"))){
						msg += ",���Ը���:" + mpi.getLanguage() + "-->" + synchPlayItem.getLanguage(); 
						mpi.setLanguage(synchPlayItem.getLanguage());
					}else if(!StringUtils.equals(mpi.getLanguage(), synchPlayItem.getLanguage())){
						String oldLanguage = oldMap.get("language");
						String newLanguage = synchPlayItem.getLanguage();
						if(oldLanguage.equals(newLanguage)){//ӰԺδ�ģ������ֹ����ģ�����
							msg += ",�������Ը��ģ�" + mpi.getLanguage() + "-->" + synchPlayItem.getLanguage();
						}else {//ӰԺ�и���
							if(oldLanguage.equals(mpi.getLanguage())){//�ֹ�δ�ģ�ӰԺ�ı�
								mpi.setLanguage(newLanguage);
								oldMap.put("language", newLanguage);
								updateJd = true;
							}else{
								msg += ",�ֹ���ӰԺ�����������ԣ�" + oldMap.get("language") + "-->" + mpi.getLanguage() + "-->" + synchPlayItem.getLanguage();
							}
						}
					}

					if(StringUtils.isBlank(oldMap.get("edition"))){
						msg += ",�汾����:" + mpi.getEdition() + "-->" + edition; 
						mpi.setEdition(edition);
					}else if(!StringUtils.equals(mpi.getEdition(), edition)){
						String oldEdition = oldMap.get("edition");
						String newEdition = edition;
						if(oldEdition.equals(newEdition)){//ӰԺδ�ģ�ֻ���ֹ����ģ�����
							msg += ",���԰汾���ģ�" + mpi.getEdition() + "-->" + newEdition;
						}else {//ӰԺ�и���
							if(oldEdition.equals(mpi.getEdition())){//�ֹ�δ�ģ�ӰԺ�ı�
								mpi.setEdition(newEdition);
								oldMap.put("edition", newEdition);
								updateJd = true;
							}else{
								msg += ",�ֹ���ӰԺ�������˰汾��" + oldMap.get("edition") + "-->" + mpi.getEdition() + "-->" + newEdition;
							}
						}
					}
					
					if(mpi.getPrice()==null || StringUtils.isBlank(oldMap.get("price"))){
						msg += ",�۸�:" + mpi.getPrice() + "-->" + synchPlayItem.getPrice();
						mpi.setPrice(synchPlayItem.getPrice());
					}else if(!mpi.getPrice().equals(synchPlayItem.getPrice())){//�۸��б��
						Integer oldPrice = Integer.valueOf(oldMap.get("price"));
						Integer newPrice = synchPlayItem.getPrice();
						if(oldPrice.equals(newPrice)){//ӰԺδ�ģ������ֹ����ģ�����
							msg += ",���Լ۸���ģ�" + mpi.getPrice() + "-->" + newPrice;
						}else {//ӰԺ�и���
							if(oldPrice.equals(mpi.getPrice())){//�ֹ�δ�ģ�ӰԺ�ı�
								mpi.setPrice(newPrice);
								oldMap.put("price", ""+newPrice);
								updateJd = true;
							}else{
								msg += ",�ֹ���ӰԺ�������˼۸�" + oldMap.get("price") + "-->" + mpi.getPrice() + "-->" + synchPlayItem.getPrice();
							}
						}
					}
					if(updateJd){
						jd.setData(JsonUtils.writeMapToJson(oldMap));
						baseDao.saveObject(jd);
					}
				}
			}
		}
		if(StringUtils.isNotBlank(msg)){
			msg = cinema.getName() + "," + movie.getMoviename() + ", mpid=" + mpi.getId() + ", " + msg;
		}
		return ErrorCode.getSuccess(msg);
	}
	@Override
	public ErrorCode updateCinemaRoom(Long cinemaid, Long userid){
		Cinema cinema = baseDao.getObject(Cinema.class, cinemaid);
		ErrorCode<List<TicketRoom>> code = remoteTicketService.getRemoteRoomList(cinema);
		if(!code.isSuccess()){
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_API, "����Ӱ����cinemaid:" + cinemaid + ",msg" + code.getMsg());
			return ErrorCode.getFailure(code.getErrcode(), code.getMsg());
		}
		List<CinemaRoom> cinemaRoomList = baseDao.getObjectListByField(CinemaRoom.class, "cinemaid", cinema.getId());
		Map<String, CinemaRoom> roomMap = BeanUtil.beanListToMap(cinemaRoomList, "num");
		List<TicketRoom> ticketRoomList = code.getRetval();
		for (TicketRoom ticketRoom : ticketRoomList) {
			CinemaRoom room = roomMap.remove(ticketRoom.getRoomnum());
			if(room == null){
				room = new CinemaRoom(cinema.getId(), ticketRoom.getRoomtype());
				room.setCinemaid(cinema.getId());
				room.setNum(ticketRoom.getRoomnum());
				room.setRoomname(ticketRoom.getName());
			}
			ChangeEntry changeEntry = new ChangeEntry(room);
			TicketRemoteUtil.copyCinemaRoom(room, ticketRoom);
			baseDao.saveObject(room);
			monitorService.saveChangeLog(userid, CinemaRoom.class, room.getId(), changeEntry.getChangeMap(room));
		}
		return ErrorCode.SUCCESS;
	}
	
	@Override
	public List<String> updateRoomSeatList(CinemaRoom room, boolean forceUpdate){
		List<String> msgList = new ArrayList<String>();
		ErrorCode<TicketRoomSeatList> code = remoteTicketService.getRemoteRoomSeatList(room);
		if(!code.isSuccess()){
			String msg = "����Ӱ����λ��cinemaid:" + room.getCinemaid() +",roomnum:"+ room.getNum() +",msg:" + code.getMsg();
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_API, msg);
			msgList.add(msg);
			return msgList;
		}
		TicketRoomSeatList seatList = code.getRetval();
		
		Map<String, RoomSeat> srMap = getRoomSeatMapByRoomid(room.getId());
		Date effactiveDate = seatList.getUpdatetime();
		List<TicketRoomSeat> ticketRoomSeatList = seatList.getTicketRoomSeatList();
		Date effectivedate = room.getEffectivedate();
		if(!forceUpdate && effectivedate!=null && DateUtil.getLastTimeOfDay(effectivedate).after(effactiveDate)){
			msgList.add("û��Ҫ���µ����ݣ�");
			return msgList;
		}else{ //������λͼ
			int maxRow=0, maxRank=0; int count=0;
			String loveflag = "N";
			List<RoomSeat> removeList = new ArrayList<RoomSeat>();
			List<RoomSeat> addList = new ArrayList<RoomSeat>();
			int updated = 0;
			for(TicketRoomSeat seat : ticketRoomSeatList){
				count ++;
				if("1".equals(seat.getLoveInd())) loveflag = "Y";
				maxRow = Math.max(maxRow, seat.getLineno());
				maxRank = Math.max(maxRank, seat.getRankno());
				RoomSeat rs = srMap.remove(seat.getLineno()+","+seat.getRankno());
				String msg="";
				boolean addnew = false;
				if(rs!=null){
					if(!StringUtils.equals(rs.getSeatline(), seat.getSeatline())){
						msg += "�б��" + rs.getSeatline() + "->" + seat.getSeatline();
					}
					if(!StringUtils.equals(rs.getSeatrank(), seat.getSeatrank())){
						msg += "�б��" + rs.getSeatrank() +"->" + seat.getSeatrank();
					}
					String msg2="";
					//��ֹԶ�̷����������Ǳ������õ�������  StringUtils.equals(rs.getLoveInd(), "0")
					if(!StringUtils.equals(rs.getLoveInd(), seat.getLoveInd())){
						if(StringUtils.equals(rs.getLoveInd(), "0")){
							msg += "������" + rs.getLoveInd() + "->" + seat.getLoveInd();
						}else{
							msg2 = "�������������ģ�" + rs.getLoveInd() + "->" + seat.getLoveInd();
						}
					}
					if(StringUtils.isNotBlank(msg)){
						msgList.add("�޸���λ��" + msg + "\n");
						removeList.add(rs);
						addnew = true;
						updated ++;
					}
					if(StringUtils.isNotBlank(msg2)){
						msgList.add(msg2 + "\n");
					}
				}else{
					addnew =true;
				}

				if(addnew) {
					rs = new RoomSeat(room.getId(), seat.getLineno(), seat.getRankno());
					rs.setLoveInd(seat.getLoveInd());
					rs.setSeatline(seat.getSeatline());
					rs.setSeatrank(seat.getSeatrank());
					msgList.add("������λ" + seat.getLineno() + "," + seat.getRankno() +"\n");
					addList.add(rs);
				}
			}
			for(Map.Entry<String, RoomSeat> map:srMap.entrySet()){
				removeList.add(map.getValue());
				msgList.add("ɾ����λ" + map.getValue().getSeatLabel() + "\n");
			}
			room.setRanknum(maxRank);
			room.setLinenum(maxRow);
			room.setEffectivedate(DateUtil.currentTime());
			room.setLoveflag(loveflag);
			if(updated !=0 || removeList.size()!=0 || addList.size()!=0){//�и���
				room.setUpdatetime(new Timestamp(System.currentTimeMillis()));
			}
			baseDao.saveObject(room);
			baseDao.removeObjectList(removeList);
			hibernateTemplate.flush();
			baseDao.addObjectList(addList);
			hibernateTemplate.flush();
			msgList.add("��λ����" + room.getSeatnum() + "[" + count + "]��λ������" + maxRow + "����λ������" + maxRank);
			return msgList;
		}
	}
	
	private Map<String,RoomSeat> getRoomSeatMapByRoomid(Long roomid){
		List<RoomSeat> roomSeatList = baseDao.getObjectListByField(RoomSeat.class, "roomid", roomid);
		Map<String,RoomSeat> seatMap = new HashMap<String,RoomSeat>();
		for(RoomSeat rs : roomSeatList){
			seatMap.put(rs.getLineno()+","+rs.getRankno(), rs);
		}
		return seatMap;
	}
	@Override
	public void updateOpenPlayItem(List<String> msgList){
		updateOpenPlayItem(null, msgList);
	}
	@Override
	public void updateOpenPlayItem(Long cinemaid, List<String> msgList){
		List<Long/*opid*/> unsynchList = openPlayService.getUnsynchPlayItem(cinemaid);
		if(!unsynchList.isEmpty()){
			dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_JOB, "��ͬ����Ƭ������" + unsynchList.size());
			for(Long opid: unsynchList){
				ErrorCode<OpenPlayItem> code = opiManageService.updateOpenPlayItem(opid);
				if(StringUtils.isNotBlank(code.getMsg())){
					msgList.add(code.getMsg());
					if(code.getRetval()!=null){
						cacheService.set(CacheConstant.REGION_HALFDAY, OpiConstant.getLastChangeKey(code.getRetval().getMpid()), code.getMsg());
					}
					dbLogger.warnWithType(LogTypeConstant.LOG_TYPE_JOB, "ͬ����Ƭ��" + code.getMsg());
				}
			}
			Iterator<String> it = msgList.iterator();
			for(;it.hasNext();){
				if(StringUtils.startsWith(it.next(), "IGNORE")) it.remove();
			}
			if(msgList.size()>0){
				Map model = new HashMap();
				model.put("jobtime", new Timestamp(System.currentTimeMillis()));
				model.put("msgList", new LinkedHashSet<String>(msgList));
				monitorService.saveSysTemplateWarn("���Ź�Ʊ�����д���", "warn/msgmail.vm", model, RoleTag.dingpiao);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try{
			SpecialActivity result = baseDao.getObjectByUkey(SpecialActivity.class, "tag", FilmFestConstant.TAG_FILMFEST_16, true);
			sixteenFilmFest = result.getId();
		}catch(Exception e){
		}
	}
}
