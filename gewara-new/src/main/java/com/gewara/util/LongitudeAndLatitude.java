package com.gewara.util;

public class LongitudeAndLatitude {
	private static final double R = 6371229;              //����İ뾶 ��
	
	
	/**
	 * ��������
	 * @param longt1 ����
	 * @param lat1 γ��
	 * @param longt2
	 * @param lat2
	 * @return
	 */
	public static double getDistance(double x1, double y1, double x2, double y2){
		 double x,y,distance;
		 x=(x2-x1)* Math.PI*R*Math.cos( ((y1+y2)/2) * Math.PI/180)/180;
		 y=(y2-y1)*Math.PI*R/180;
		 distance=Math.hypot(x,y);
		 return distance;
	}

	/**
	 * ���ݾ����ȡ����
	 * @param x1 ����
	 * @param y1 γ��
	 * @param distance
	 * @return
	 */
	public static double getLongitude(double x1, double y1, double distance){
		double x2 = x1 + distance*180/(Math.PI*R*Math.cos(y1 * Math.PI/180));
		return x2;
	}
	
	
	/**
	 * ���ݾ����ȡγ��
	 * @param latitude
	 * @param distance
	 * @return
	 */
	public static double getLatitude(double y1, double distance){
		double y2 = y1 + distance*180/Math.PI/R;
		return y2;
	}
}
