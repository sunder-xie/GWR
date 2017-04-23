package com.gewara.service.movie;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author <a href="mailto:acerge@163.com">gebiao(acerge)</a>
 * @since 2007-9-28����02:05:17
 */
public interface PlayItemImporter {
	/**
	 * @param fileName
	 * @param tag 
	 * @return Map(cinemaParamList,movieParamList,movieIndexParam) for static page gen
	 * @throws ImportPlayTimeException
	 */
	void importPlayTime(String fileName, List<String> errorMessages, String tag);
	/**
	 * @param file
	 * @return Map(cinemaParamList,movieParamList,movieIndexParam) for static page gen
	 * @throws ImportPlayTimeException
	 */
	void importPlayTime(File file, List<String> errorMessages, String tag);
	
	void importXSSFPlayTime(InputStream inputStream, List<String> errorMessages, String tag);
	/**
	 * @param inputStream xlsFileInputStream
	 * @return Map(cinemaParamList,movieParamList,movieIndexParam) for static page gen
	 * @throws ImportPlayTimeException
	 */
	void importHSSFPlayTime(InputStream inputStream, List<String> errorMessages, String tag);
}
