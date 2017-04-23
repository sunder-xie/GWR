package com.gewara.service.movie.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gewara.Config;
import com.gewara.constant.sys.CacheConstant;
import com.gewara.constant.ticket.OpiConstant;
import com.gewara.model.movie.Cinema;
import com.gewara.model.movie.CinemaRoom;
import com.gewara.model.movie.Movie;
import com.gewara.model.movie.MoviePlayItem;
import com.gewara.service.PlaceService;
import com.gewara.service.impl.BaseServiceImpl;
import com.gewara.service.movie.MCPService;
import com.gewara.service.movie.PlayItemImporter;
import com.gewara.untrans.CacheService;
import com.gewara.util.DateUtil;
import com.gewara.util.GewaLogger;
import com.gewara.util.LoggerUtils;

/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
@Service("playItemImporter")
public class XLSPlayItemImporter extends BaseServiceImpl implements PlayItemImporter{
	private transient final GewaLogger log = LoggerUtils.getLogger(getClass(), Config.getServerIp(), Config.SYSTEMID);
	@Autowired@Qualifier("cacheService")
	private CacheService cacheService;
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	@Autowired@Qualifier("placeService")
	private PlaceService placeService;
	public void setPlaceService(PlaceService placeService) {
		this.placeService = placeService;
	}
	@Autowired@Qualifier("mcpService")
	private MCPService mcpService;
	public void setMcpService(MCPService mcpService) {
		this.mcpService = mcpService;
	}

	@Override
	public void importPlayTime(String fileName, List<String> errorMessages, String tag) {
		importPlayTime(new File(fileName), errorMessages, tag);
	}

	@Override
	public void importPlayTime(File file, List<String> errorMessages, String tag){
		try {
			importHSSFPlayTime(new BufferedInputStream(new FileInputStream(file)), errorMessages, tag);
		} catch (Exception e) {
			try{
				importXSSFPlayTime(new BufferedInputStream(new FileInputStream(file)), errorMessages, tag);
			}catch (Exception e1) {
				throw new IllegalArgumentException(e1);
			}
		}
	}
	
	@Override
	public void importXSSFPlayTime(InputStream inputStream, List<String> errorMessages, String tag) {
		try {
			Workbook workbook = new XSSFWorkbook(inputStream);
			workbookImport(workbook, errorMessages, tag);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}finally{
			try {
				inputStream.close();
			} catch (IOException e) {
				log.warn("", e);
			}
		}
	}
	
	@Override
	public void importHSSFPlayTime(InputStream inputStream, List<String> errorMessages, String tag) {
		try {
			POIFSFileSystem fs = new POIFSFileSystem(inputStream);
			Workbook workbook = new HSSFWorkbook(fs);
			workbookImport(workbook, errorMessages, tag);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}finally{
			try {
				inputStream.close();
			} catch (IOException e) {
				log.warn("", e);
			}
		}
	
	}
	
	private void workbookImport(Workbook workbook, List<String> errorMessages, String tag){
		Sheet dataSheet = workbook.getSheetAt(0);
		String cityCode = "310000";// Ĭ�����Ϻ�
		Row cityRow = dataSheet.getRow(1);
		if(cityRow != null){
			Cell cityCell = cityRow.getCell(2);
			if(cityCell != null){
				cityCode = cityCell.getRichStringCellValue().getString();
				if(StringUtils.isBlank(cityCode)) cityCode = "310000";
			}
		}
		//moviename=B,name,playroom,playdate,playtime,price,discountprice,studentprice,language,remark
		int lastRowNum = dataSheet.getLastRowNum();
		List<Integer> rowNumberList = new ArrayList<Integer>();
		errorMessages.add("��ǰ���������������Ϊ" + cityCode + "�ķ�ӳ��Ϣ��");
		List<String> movieNameList = new ArrayList<String>();
		List<String> cinemaNameList = new ArrayList<String>();
		List<MoviePlayItem> playItemList = new ArrayList<MoviePlayItem>();
		Long batch = System.currentTimeMillis(); //����
		Timestamp createtime = new Timestamp(batch);
		Date cur = DateUtil.getCurDate();
		if(StringUtils.isNotBlank(tag) && StringUtils.isNumeric(tag)) batch = new Long(tag);
		for(int rowIndex = 4; rowIndex <= lastRowNum; rowIndex ++){
			Row row = dataSheet.getRow(rowIndex);
			if(row == null) continue;//��������
			Cell movienameCell = row.getCell(1);//B�п�ʼ 
			Cell cinemanameCell = row.getCell(2); 
			Cell playroomCell = row.getCell(3); 
			Cell playdateCell = row.getCell(4); 
			Cell playtimeCell = row.getCell(5); 
			Cell priceCell = row.getCell(6);
			Cell pricemarkCell = row.getCell(7); 
			Cell languageCell = row.getCell(8);
			Cell editionCell = row.getCell(9);
			Cell remarkCell = row.getCell(10);
			String moviename = null,cinemaname = null,playroom = null, playtime = null;
			String pricemark = null, language = null, edition = null, remark = null;
			Date playdate = null;
			double d_price;
			Integer price = null;
			boolean error = false;
			if(movienameCell==null || cinemanameCell==null || playdateCell==null 
					|| playtimeCell==null){
				if(movienameCell==null && cinemanameCell==null)   continue; //��Ϊ�ǿ��У�������
				if(movienameCell != null)
					moviename = movienameCell.getRichStringCellValue().getString();
				if(cinemanameCell != null)
					cinemaname = cinemanameCell.getRichStringCellValue().getString();
				if(StringUtils.isBlank(moviename) && StringUtils.isBlank(cinemaname)){//��Ϊ�ǿ��У�������
					continue;
				}
				errorMessages.add("��"+(rowIndex+1)+"�У�moviename,cinemaname,playdate,playtime, priceCell��Ϊ�����");
				error = true;
			}else{
				try{
					moviename = movienameCell.getRichStringCellValue().getString();
				}catch(Exception e){
					errorMessages.add("��"+(rowIndex+1)+"��moviename��Ӱ���Ƹ�ʽ����ȷ��������Text���ͣ�" + e.getMessage());
					error = true;
				}
				try{
					cinemaname = cinemanameCell.getRichStringCellValue().getString();
				}catch(Exception e){
					errorMessages.add("��"+(rowIndex+1)+"��cinemaname��ӰԺ���Ƹ�ʽ����ȷ��������Text���ͣ�" + e.getMessage());
					error = true;
				}
				try{
					playdate = playdateCell.getDateCellValue();
					if(playdate.before(cur)){
						errorMessages.add("��"+(rowIndex+1)+"��playdate�����Ѿ����ڣ�");
						error = true;
					}
				}catch(Exception e){
					errorMessages.add("��"+(rowIndex+1)+"��playdate���ڸ�ʽ����ȷ��" + e.getMessage());
					error = true;
				}
				//playdateCell.getNumericCellValue();
				try{
					playtime = DateUtil.formatTime(playtimeCell.getRichStringCellValue().getString());
				}catch(Exception e){
					errorMessages.add("��"+(rowIndex+1)+"��playtimeʱ���ʽ����ȷ��" + e.getMessage());
					error = true;
				}
				if(StringUtils.isBlank(moviename) || StringUtils.isBlank(cinemaname) || playdate==null){
					errorMessages.add("��"+(rowIndex+1)+"�У�moviename,cinemaname,playdate,playtime��Ϊ�����");
					error = true;
				}else{
					moviename = StringUtils.trim(moviename);
					cinemaname = StringUtils.trim(cinemaname);
				}
			}
			if(playroomCell != null){
				try{
					playroom = playroomCell.getRichStringCellValue().getString();
				}catch(Exception e){
					playroom = "" + (int)playroomCell.getNumericCellValue();
				}
			}
			if(pricemarkCell!=null){
				try{
					pricemark = pricemarkCell.getRichStringCellValue().getString();
				}catch(Exception e){
					try{
						pricemark = "" + pricemarkCell.getNumericCellValue();
					}catch(Exception e1){
						errorMessages.add("��"+(rowIndex+1)+"��pricemark�����۸��ʽ����ȷ��������Text��Numeric���ͣ�" + e.getMessage() + e1.getMessage());
						error = true;
					}
				}
			}
			if(!error){
				if(priceCell!=null){
					try{
						d_price = priceCell.getNumericCellValue();
						price =  (int)d_price;
					}catch(NumberFormatException e){price = 0;}
				}
				if(languageCell != null) language = languageCell.getRichStringCellValue().getString().trim();
				if(remarkCell != null) remark = remarkCell.getRichStringCellValue().getString();
				if(editionCell != null) edition = editionCell.getRichStringCellValue().getString();
				MoviePlayItem mpi = new MoviePlayItem(createtime);
				mpi.setPlayroom(StringUtils.trim(playroom));
				mpi.setPlaydate(playdate);
				mpi.setPlaytime(StringUtils.trim(playtime));
				mpi.setPrice(price == null ? null: (price>0?price:null));
				mpi.setPricemark(pricemark);
				mpi.setLanguage(StringUtils.trim(language));
				mpi.setEdition(StringUtils.trim(edition));
				mpi.setRemark(StringUtils.trim(remark));
				mpi.setBatch(batch);
				mpi.setOpentype(OpiConstant.OPEN_GEWARA);
				movieNameList.add(StringUtils.trim(moviename));
				cinemaNameList.add(StringUtils.trim(cinemaname));
				playItemList.add(mpi);
				rowNumberList.add(rowIndex+1);
			}
		}
		
		//���������ֿ���Ϊ��saveRecord�ύ�����ݿ�������ɾ�̬ҳ��
		saveRecord(cityCode, movieNameList,cinemaNameList, playItemList, rowNumberList, errorMessages);
	}
	
	/**
	 * @param cityCode
	 * @param movieNameList
	 * @param cinemaNameList
	 * @param playItemList
	 * @param rowNumberList
	 * @param errorMessages
	 * @return Map(cinemaParamList,movieParamList,movieIndexParam) for static page gen
	 */
	public void saveRecord(String citycode, List<String> movieNameList, List<String> cinemaNameList, 
			List<MoviePlayItem> playItemList, List<Integer> rowNumberList, List<String> errorMessages) {
		Set<String> movieNameSet =  new HashSet<String>(movieNameList);
		Set<String> cinemaNameSet =  new HashSet<String>(cinemaNameList);
		Map<String,Movie> movieMap = new HashMap<String, Movie>();
		Map<String,Cinema> cinemaMap = new HashMap<String, Cinema>();
		for(String movieName:movieNameSet){
			Movie movie = mcpService.getMovieByName(movieName);
			if(movie != null) movieMap.put(movieName, movie); 
		}
		for(String cinemaName:cinemaNameSet){
			Cinema cinema = placeService.getPlaceByName(citycode, Cinema.class, cinemaName);
			if(cinema != null) cinemaMap.put(cinemaName, cinema);
		}
		for(int i = 0,count=playItemList.size(); i < count; i++){
			Movie movie = movieMap.get(movieNameList.get(i));
			Cinema cinema = cinemaMap.get(cinemaNameList.get(i));
			if(movie == null){
				errorMessages.add("��" + (rowNumberList.get(i)+1) + "�У���Ӱ��" + movieNameList.get(i) + "�������ݿ��в�����,����ϸ�˶����ƣ�");
			}
			if(cinema == null){
				errorMessages.add("��" + (rowNumberList.get(i)+1) + "�У���ӰԺ��" + cinemaNameList.get(i) + "�������ݿ��в�����,����ϸ�˶����ƣ�");
			}
			MoviePlayItem mpi = playItemList.get(i);
			if(movie!=null && cinema!=null){//����
				if(StringUtils.isNotBlank(mpi.getPlayroom())){
					CinemaRoom room = mcpService.getRoomByRoomname(cinema.getId(), mpi.getPlayroom());
					if(room == null){
						errorMessages.add("��" + (rowNumberList.get(i)+1) + "�У�" + cinemaNameList.get(i) + "��Ӱ������" + mpi.getPlayroom() + "������,����ϸ�˶����ƣ�");
						continue;
					}else{
						mpi.setRoomid(room.getId());
						mpi.setRoomnum(room.getNum());
					}
				}
				mpi.setCinemaid(cinema.getId());
				mpi.setCitycode(cinema.getCitycode());
				mpi.setMovieid(movie.getId());
				MoviePlayItem oldMpi = null;
				if(mpi.getRoomid()!=null) {
					oldMpi = mcpService.getUniqueMpi(OpiConstant.OPEN_GEWARA, mpi.getCinemaid(), mpi.getRoomid(), mpi.getPlaydate(), mpi.getPlaytime());
				}else {
					oldMpi = mcpService.getUniqueMpi2(mpi.getCinemaid(), mpi.getMovieid(), mpi.getPlaydate(), mpi.getPlaytime());
				}
				if(oldMpi == null) {
					mpi.setOpentype(OpiConstant.OPEN_GEWARA);
					baseDao.saveObject(mpi);
				}else{
					errorMessages.add("��" + (rowNumberList.get(i)+1) + "�У��޸���ԭ������Ƭ����˶ԣ���" + movieNameList.get(i));
					oldMpi.copyFrom(mpi);
					baseDao.saveObject(oldMpi);
				}
			}
		}
		//���MCPService�л������Ƭ
		String key = "playMovieIdList" + citycode;
		cacheService.remove(CacheConstant.REGION_HALFHOUR, key);
	}
}
