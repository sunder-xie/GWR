package com.gewara.helper.api;

import java.util.ArrayList;
import java.util.List;

public class FindSeatUtilHelper {
	private int[][] map; // ��ͼ����0��ʾ��ͨ����1��ʾ����ͨ��
	private int map_w; // ��ͼ���
	private int map_h; // ��ͼ�߶�
	private int start_x; // �������X
	private int start_y; // �������Y
	private int goal_x; // �յ�����X
	private int goal_y; // �յ�����Y

	private boolean closeList[][]; // �ر��б�
	public int openList[][][]; // ���б�
	private int openListLength;

	private static final int EXIST = 1;
	private static final int NOT_EXIST = 0;

	private static final int ISEXIST = 0;
	private static final int EXPENSE = 1; // ����Ĵ���
	private static final int DISTANCE = 2; // ����Ĵ���
	private static final int COST = 3; // ���ĵ��ܴ���
	private static final int FATHER_DIR = 4; // ���ڵ�ķ���

	public static final int DIR_NULL = 0;
	public static final int DIR_DOWN = 1; // ������
	public static final int DIR_UP = 2; // ������
	public static final int DIR_LEFT = 3; // ������
	public static final int DIR_RIGHT = 4; // ������
	public static final int DIR_UP_LEFT = 5;
    public static final int DIR_UP_RIGHT = 6;
    public static final int DIR_DOWN_LEFT = 7;
    public static final int DIR_DOWN_RIGHT = 8;

	private int counter; // �㷨Ƕ�����
	private boolean isFound; // �Ƿ��ҵ�·��
	public FindSeatUtilHelper(int[][] mx, int sx, int sy, int gx, int gy) {
		start_x = sx;
		start_y = sy;
		goal_x = gx;
		goal_y = gy;
		map = mx;
		map_w = mx.length;
		map_h = mx[0].length;
		counter = 2000;
		initCloseList();
		initOpenList(goal_x, goal_y);
	}
	// �õ���ͼ����һ�������ֵ
	private int getMapExpense() {
		return 1;
	}
	// �õ����������ֵ
	private int getDistance(int x, int y, int ex, int ey) {
		return (Math.abs(x - ex) + Math.abs(y - ey));
	}
	// �õ�����������Ӵ�ʱ��������ֵ
	private int getCost(int x, int y) {
		return openList[x][y][COST];
	}
	public void searchPath() {
		addOpenList(start_x, start_y);
		find(start_x, start_y);
	}
	private void find(int x, int y) {
		// �����㷨���
		for (int t = 0; t < counter; t++) {
			if (((x == goal_x) && (y == goal_y))) {
				isFound = true;
				return;
			} else if ((openListLength == 0)) {
				isFound = false;
				return;
			}
			removeOpenList(x, y);
			addCloseList(x, y);
			// �õ���Χ�ܹ����ߵĵ�
			addNewOpenList(x, y, x, y + 1, DIR_UP);
			addNewOpenList(x, y, x, y - 1, DIR_DOWN);
			addNewOpenList(x, y, x - 1, y, DIR_RIGHT);
			addNewOpenList(x, y, x + 1, y, DIR_LEFT);
			addNewOpenList(x, y, x + 1, y + 1, DIR_UP_LEFT);
            addNewOpenList(x, y, x - 1, y + 1, DIR_UP_RIGHT);
            addNewOpenList(x, y, x + 1, y - 1, DIR_DOWN_LEFT);
            addNewOpenList(x, y, x - 1, y - 1, DIR_DOWN_RIGHT);
			// �ҵ���ֵ��С�ĵ㣬������һ���㷨
			int cost = 0x7fffffff;
			for (int i = 0; i < map_w; i++) {
				for (int j = 0; j < map_h; j++) {
					if (openList[i][j][ISEXIST] == EXIST) {
						if (cost > getCost(i, j)) {
							cost = getCost(i, j);
							x = i;
							y = j;
						}
					}
				}
			}
		}
		// �㷨����
		isFound = false;
		return;
	}
	// ���һ���µĽڵ�
	private void addNewOpenList(int x, int y, int newX, int newY, int dir) {
		if (isCanPass(newX, newY)) {
			if (openList[newX][newY][ISEXIST] == EXIST) {
				if (openList[x][y][EXPENSE] + getMapExpense() < openList[newX][newY][EXPENSE]) {
					setFatherDir(newX, newY, dir);
					setCost(newX, newY, x, y);
				}
			} else {
				addOpenList(newX, newY);
				setFatherDir(newX, newY, dir);
				setCost(newX, newY, x, y);
			}
		}
	}

	// ��������ֵ
	private void setCost(int x, int y, int ex, int ey) {
		openList[x][y][EXPENSE] = openList[ex][ey][EXPENSE] + getMapExpense();
		openList[x][y][DISTANCE] = getDistance(x, y, ex, ey);
		openList[x][y][COST] = openList[x][y][EXPENSE] + openList[x][y][DISTANCE];
	}

	// ���ø��ڵ㷽��
	private void setFatherDir(int x, int y, int dir) {
		openList[x][y][FATHER_DIR] = dir;
	}

	// �ж�һ�����Ƿ����ͨ��
	private boolean isCanPass(int x, int y) {
		// �����߽�
		if (x < 0 || x >= map_w || y < 0 || y >= map_h) {
			return false;
		}
		// ��ͼ��ͨ
		if (map[x][y] != 0) {
			return false;
		}
		// �ڹر��б���
		if (isInCloseList(x, y)) {
			return false;
		}
		return true;
	}

	// �Ƴ����б��һ��Ԫ��
	private void removeOpenList(int x, int y) {
		if (openList[x][y][ISEXIST] == EXIST) {
			openList[x][y][ISEXIST] = NOT_EXIST;
			openListLength--;
		}
	}

	// �ж�һ���Ƿ��ڹر��б���
	private boolean isInCloseList(int x, int y) {
		return closeList[x][y];
	}

	// ��ӹر��б�
	private void addCloseList(int x, int y) {
		closeList[x][y] = true;
	}

	// ��Ӵ��б�
	private void addOpenList(int x, int y) {
		if (openList[x][y][ISEXIST] == NOT_EXIST) {
			openList[x][y][ISEXIST] = EXIST;
			openListLength++;
		}
	}
	// ��ʼ���ر��б�
	private void initCloseList() {
		closeList = new boolean[map_w][map_h];
		for (int i = 0; i < map_w; i++) {
			for (int j = 0; j < map_h; j++) {
				closeList[i][j] = false;
			}
		}
	}
	// ��ʼ�����б�
	private void initOpenList(int ex, int ey) {
		openList = new int[map_w][map_h][5];
		for (int i = 0; i < map_w; i++) {
			for (int j = 0; j < map_h; j++) {
				openList[i][j][ISEXIST] = NOT_EXIST;
				openList[i][j][EXPENSE] = getMapExpense();
				openList[i][j][DISTANCE] = getDistance(i, j, ex, ey);
				openList[i][j][COST] = openList[i][j][EXPENSE] + openList[i][j][DISTANCE];
				openList[i][j][FATHER_DIR] = DIR_NULL;
			}
		}
		openListLength = 0;
	}
	// ��ý��
	public FindSeatPt[] getResult() {
		FindSeatPt[] result;
		ArrayList<FindSeatPt> route;
		searchPath();
		if (!isFound) {
			return null;
		}
		route = new ArrayList<FindSeatPt>();
		// openList�Ǵ�Ŀ�������ʼ�㵹�Ƶġ�
		int iX = goal_x;
		int iY = goal_y;
		while ((iX != start_x || iY != start_y)) {
			route.add(new FindSeatPt(iX, iY));
			switch (openList[iX][iY][FATHER_DIR]) {
			case DIR_DOWN:			iY++;break;
			case DIR_UP:			iY--;break;
			case DIR_LEFT:			iX--;break;
			case DIR_RIGHT:			iX++; break;
			case DIR_UP_LEFT:       iX--;   iY--;    break;
            case DIR_UP_RIGHT:      iX++;   iY--;    break;
            case DIR_DOWN_LEFT:     iX--;   iY++;    break;
            case DIR_DOWN_RIGHT:    iX++;   iY++;    break;
			}
		}
		int size = route.size();
		result = new FindSeatPt[size];
		for (int i = 0; i < size; i++) {
			result[i] = new FindSeatPt(route.get(i));
		}
		return result;
	}
	public static String getPathDirection(int startX, int startY, List<String> pathList){
		if(pathList==null || pathList.size()==0){
			return null;
		}
		int tmpx = startX;
		int tmpy = startY;
		String result = "";
		String paths = pathList.get(0);
		String[] path = paths.split(":");
		int x = Integer.valueOf(path[0]);
		int y = Integer.valueOf(path[1]);
		if(tmpx==x){
			if(y>tmpy){
				result = "0";
			}else {
				result = "180";
			}
		}else{
			if(tmpy==y){
				if(x>tmpx){
					result = "90";
				}else {
					result = "270";
				}
			}else {
				if((tmpx+1)==x){
					if(tmpy+1==y){
						result = "45";
					}else if((tmpy-1)==y){
						result = "135";
					}
				}else if(tmpx-1==x){
					if(tmpy+1==y){
						result = "315";
					}else if((tmpy-1)==y){
						result = "225";
					}
				}
			}
		}
		return result;
	}
}
