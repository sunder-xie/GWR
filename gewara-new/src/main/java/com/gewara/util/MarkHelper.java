package com.gewara.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.gewara.json.bbs.MarkCountData;
import com.gewara.model.bbs.MarkCount;

public class MarkHelper {
	private static final Map<String, String[]> markMap = new HashMap<String, String[]>();
	static {
		// ��Ӱ
		String moviePoor = "�ܲ���Բ�Ҫȥ��";
		String movieGeneral = "һ�㣬�ɿ��ɲ���";
		String movieGood = "���У��Ƽ����ȥ��";
		String movieBest = "���ݴ����һ����ȥ��";
		// ӰԺ
		String cinemaPoor = "һ�㣬����ѡ��ɿ���";
		String cinemaGeneral = "����ż����ȥ";
		String cinemaGood = "�ܰ����Ƽ����ȥ";
		String cinemaBest = "��ѣ�������ѡ";
		// ����
		String coachPoor = "һ��";
		String coachGeneral = "����";
		String coachGood = "�ܰ�";
		String coachBest = "���";
		// ������
		String gymBad = "���Ƽ���������";
		String gymPoor = "�պϣ�����ѡ��ɿ���";
		String gymGeneral = "����ֵ�ÿ���";
		String gymGood = "�ܰ����Ƽ����ȥ";
		String gymBest = "��ѣ�������ѡ";
		// ͨ��
		String commonBad = "���Ƽ���������";
		String commonPoor = "�պϣ�����ѡ��ɿ���";
		String commonGeneral = "����ֵ�ÿ���";
		String commonGood = "�ܰ����Ƽ����ȥ";
		String commonBest = "��ѣ�������ѡ";

		// ��Ժ
		String theatrePoor = "һ�㣬����ѡ��ɿ���";
		String theatreGeneral = "����ż����ȥ";
		String theatreGood = "�ܰ����Ƽ����ȥ";
		String theatreBest = "��ѣ�������ѡ";

		markMap.put("movie", new String[] { moviePoor, movieGeneral, movieGood, movieBest });
		markMap.put("cinema", new String[] { cinemaPoor, cinemaGeneral, cinemaGood, cinemaBest });
		markMap.put("gymcoach", new String[] { coachPoor, coachGeneral, coachGood, coachBest });
		markMap.put("barsinger", new String[] { coachPoor, coachGeneral, coachGood, coachBest });
		markMap.put("gym", new String[] { gymBad, gymPoor, gymGeneral, gymGood, gymBest });
		markMap.put("common", new String[] { commonBad, commonPoor, commonGeneral, commonGood, commonBest });
		markMap.put("theatre", new String[] { theatrePoor, theatreGeneral, theatreGood, theatreBest });

		// [1,4)[4,7),[7,9),[9,10]
		markMap.put("cinema_general", new String[] { cinemaPoor, cinemaPoor, cinemaPoor, cinemaPoor, cinemaGeneral, cinemaGeneral, cinemaGeneral, commonGeneral, cinemaGood, cinemaGood, cinemaBest });
		markMap.put("movie_general", new String[] { moviePoor, moviePoor, moviePoor, moviePoor, movieGeneral, movieGeneral, movieGeneral, movieGood, movieGood, movieBest, movieBest });
		markMap.put("gym_general", new String[] { gymBad, gymBad, gymBad, gymBad, gymPoor, gymPoor, gymGeneral, gymGeneral, gymGood, gymGood, gymBest });
		markMap.put("coach_general", new String[] { coachPoor, coachPoor, coachPoor, coachPoor, coachGeneral, coachGeneral, coachGeneral, coachGood, coachGood, coachBest, coachBest });
		markMap.put("gymcoach_general", new String[] { coachPoor, coachPoor, coachPoor, coachPoor, coachGeneral, coachGeneral, coachGeneral, coachGood, coachGood, coachBest, coachBest });
		markMap.put("barsinger_general", new String[] { coachPoor, coachPoor, coachPoor, coachPoor, coachGeneral, coachGeneral, coachGeneral, coachGood, coachGood, coachBest, coachBest });
		markMap.put("theatre_general", new String[] { theatrePoor, theatrePoor, theatrePoor, theatrePoor, theatreGeneral, theatreGeneral, theatreGeneral, commonGeneral, theatreGood, theatreGood, theatreBest });

		markMap.put("common", new String[] { commonBad, commonBad, commonBad, commonBad, commonPoor, commonPoor, commonGeneral, commonGeneral, commonGood, commonGood, commonBest, commonBest });
		markMap.put("price", new String[] { "��ֵ", "��ֵ", "��ֵ", "һ��", "һ��", "����", "����", "ֵ��", "ֵ��", "��ֵ", "��ֵ", "��ֵ" });
		markMap.put("space", new String[] { "ӵ��", "ӵ��", "ӵ��", "һ��", "һ��", "����", "����", "��", "��", "�ܰ�", "�ܰ�", "�ܰ�" });
	}
	
	public static String markByIndex(String tag, int idx) {
		return markByNameAndIndex(tag, "general", idx);
	}

	public static String markByNameAndIndex(String tag, String markname, int idx) {
		String[] marksList = markMap.get(tag + "_" + markname);
		if (marksList == null)
			marksList = markMap.get(markname);
		if (marksList == null)
			marksList = markMap.get("common");
		return marksList[idx];
	}

	private static List<String> markList = Arrays.asList("generalmark");
	public static List<String> getMarkList() {
		return markList;
	}
	/**
	 * return 10-100 : ʮ�ǣ��Ǽ�����
	 * 
	 * @param markItems
	 *           �ö��ŷָ�
	 * @return
	 */
	public static Map<String, Integer> getStar(Object bean, String markItemStr) {
		Map result = new HashMap<String, Object>();
		if (StringUtils.isBlank(markItemStr))
			return new HashMap<String, Integer>();
		String[] markItems = markItemStr.split(",");
		Integer sum, times;
		Integer mark;
		for (int i = 0; i < markItems.length; i++) {
			try {
				sum = new Integer(BeanUtils.getProperty(bean, markItems[i] + "mark"));
				times = new Integer(BeanUtils.getProperty(bean, markItems[i] + "markedtimes"));
				if (times == 0) times = 1;
				mark = sum * 10 / times;
				result.put(markItems[i] + "mark", mark);//
			} catch (NumberFormatException e) {// ignore
			} catch (IllegalAccessException e) {// ignore
			} catch (InvocationTargetException e) {// ignore
			} catch (NoSuchMethodException e) {// ignore
			} catch (Exception e) {// ignore
			}
		}
		return result;
	}

	public static void updateMark(Object bean, String markName, Integer markValue, boolean isUpdate) {
		try {
			String oldvalue = BeanUtils.getProperty(bean, markName);
			Integer newValue = new Integer(oldvalue) + markValue;
			BeanUtils.setProperty(bean, markName, newValue);
			try {
				if (isUpdate) {
					String oldclick = BeanUtils.getProperty(bean, markName + "edtimes");
					Long newclick = new Long(oldclick) + 1;
					BeanUtils.setProperty(bean, markName + "edtimes", newclick);
				}
			} catch (Exception e) {/* ignore */
			}
		} catch (IllegalAccessException e) {
			// ignore
		} catch (InvocationTargetException e) {
			// ignore
		} catch (NoSuchMethodException e) {
			// ignore
		}
	}

	public static Integer getSingleMarkStar(Object bean, String markname) {
		try {
			int sum = new Integer(BeanUtils.getProperty(bean, markname + "mark"));
			int times = new Integer(BeanUtils.getProperty(bean, markname + "markedtimes"));
			if (times == 0)
				times = 1;
			int mark = sum * 10 / times;
			return mark > 100 ? 100 : mark;
		} catch (NumberFormatException e) {// ignore
		} catch (IllegalAccessException e) {// ignore
		} catch (InvocationTargetException e) {// ignore
		} catch (NoSuchMethodException e) {// ignore
		} catch (Exception e) {// ignore
		}
		return null;
	}
	public static Integer getSingleMarkStar(Object bean, String markname, MarkCount markCount, String avgMarktimes) {
		try {
			if (markCount == null || avgMarktimes == null)
				return getSingleMarkStar(bean, markname);
			Integer param = markCount.getBookingtimes() + Integer.valueOf(avgMarktimes);
			int sum = new Integer(BeanUtils.getProperty(bean, markname + "mark"));
			int times = new Integer(BeanUtils.getProperty(bean, markname + "markedtimes"));
			if (times == 0) times = 1;
			if(times < 20) return getSingleMarkStar(bean, markname);
			int mark = (sum *10 + markCount.getAvgbookingmarks() * param) / (times + param);
			return Integer.parseInt(mark + "");
		} catch (NumberFormatException e) {// ignore
		} catch (IllegalAccessException e) {// ignore
		} catch (InvocationTargetException e) {// ignore
		} catch (NoSuchMethodException e) {// ignore
		} catch (Exception e) {// ignore
		}
		return getSingleMarkStar(bean, markname);
	}
	
	public static int markMethod = 1;
	public static Integer getLastMarkStar(Object bean, String markname, MarkCountData markCount, Map markData) {
		try {
			if (markMethod==2) return getMarkStart2(bean, markname, markCount, markData);
			return getMarkStar3(bean, markname, markCount, markData);
		} catch (NumberFormatException e) {// ignore
		} catch (Exception e) {// ignore
		}
		return getSingleMarkStar(bean, markname);
	}
	public static Integer getMarkStart2(Object bean, String markname, MarkCountData markCount, Map markData) {
		try {
			Integer avgMarktimes = Integer.valueOf(markData.get("avgmarktimes").toString());
			if (markCount == null || avgMarktimes == null)
				return getSingleMarkStar(bean, markname);
			Integer param = markCount.getBookingtimes() + avgMarktimes;
			int sum = new Integer(BeanUtils.getProperty(bean, markname + "mark"));
			int times = new Integer(BeanUtils.getProperty(bean, markname + "markedtimes"));
			if (times == 0) times = 1;
			if(times < 20) return getSingleMarkStar(bean, markname);
			int mark = (sum *10 + markCount.getAvgbookingmarks() * param) / (times + param);
			return Integer.parseInt(mark + "");
		} catch (NumberFormatException e) {// ignore
		} catch (IllegalAccessException e) {// ignore
		} catch (InvocationTargetException e) {// ignore
		} catch (NoSuchMethodException e) {// ignore
		} catch (Exception e) {// ignore
		}
		return getSingleMarkStar(bean, markname);
	}
	/**
	 * 
	 * @param a		�ǹ�Ʊ�û�ƽ������(�ٷ���)
	 * @param b		�ǹ�Ʊ�û����ִ���
	 * @param c		��Ʊ�û����ִ���
	 * @param d		��Ʊ�û�ƽ������(�ٷ���)
	 * @param e		�����㷨����
	 * @param n		һ�����ڵ���ӰƬ��������ִ���
	 * @return
	 */
	public static int markValue(int a, int b, int c, int d, int e, int n){
		int result =((a*(b/(b+c+e*(b+c+n)))+d))/(1+(b/(b+c+e*(b+c+n))));
		return result;
	}
	
	public static Integer getMarkStar3(Object bean, String markname, MarkCountData markCount, Map markData){
		Integer maxMarktimes = Integer.valueOf(markData.get("maxtimes").toString());
		Integer markConstant = Integer.valueOf(markData.get("markConstant").toString());
		if (markCount == null || maxMarktimes == null || markConstant == null)
			return getSingleMarkStar(bean, markname);
		Integer marks = markCount.getUnavgbookingmarks() * markCount.getUnbookingtimes();
		Integer times = markCount.getUnbookingtimes() + markCount.getBookingtimes();
		if (times == 0) times = 1;
		if(markCount.getBookingtimes() < 20) return 70;
		Integer commonX = (times+markConstant)*(times+Integer.valueOf(maxMarktimes));
		Integer top = marks / commonX+markCount.getAvgbookingmarks();
		Integer bottom = 1+(markCount.getUnbookingtimes()/commonX);
		return top/bottom > 100 ? 100 :top/bottom;
	}
	public static Integer getNewMarkStar(Object bean, String markname, MarkCountData markCount, String avgMarktimes) {
		return getSingleMarkStar(bean, markname, markCount, avgMarktimes);
		//return getSingleMarkStar(bean, markname);
	}
}
