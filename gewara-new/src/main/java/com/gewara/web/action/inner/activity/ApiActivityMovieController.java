package com.gewara.web.action.inner.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.constant.ApiConstant;
import com.gewara.model.movie.Movie;
import com.gewara.web.action.api.BaseApiController;
@Controller
public class ApiActivityMovieController extends BaseApiController{
	/**
	 * ��ȡ��Ӱ�б�
	 * @param movieids
	 * @return
	 */
	@RequestMapping("/inner/activity/movie/getMovieByIds.xhtml")
	public String getMovieByIds(String movieids, ModelMap model){
		if(movieids == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "��������");
		List<String> movieidList = Arrays.asList(StringUtils.split(movieids, ","));
		List<Long> movieIds = new ArrayList<Long>();
		for (String string : movieidList) {
			movieIds.add(Long.parseLong(string));
		}
		List<Movie> movieList = daoService.getObjectList(Movie.class, movieIds);
		model.put("movieList", movieList);
		return getXmlView(model, "inner/activity/movieList.vm");
	}
	/**
	 * ��ȡ��Ӱ��Ϣ
	 * @param movieids
	 * @return
	 */
	@RequestMapping("/inner/activity/movie/getMovie.xhtml")
	public String getMovieByIds(Long movieid, ModelMap model){
		if(movieid == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "��������");
		Movie movie = daoService.getObject(Movie.class, movieid);
		if(movie == null) return getErrorXmlView(model,  ApiConstant.CODE_PARAM_ERROR, "���ݲ����ڣ�");
		model.put("movie", movie);
		return getXmlView(model, "inner/activity/movieDetail.vm");
	}
}
