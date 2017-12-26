package com.ruanyf.androidgame40;

/**
 * 五子棋游戏核心
 * 将通用逻辑独立，方便后续跨平台扩展
 * Created by Feng on 2017/12/21.
 */
public class GameCore {

	public static final int N = 0; // 空位置
	public static final int B = 1; // 黑棋
	public static final int W = 2; // 白棋

	public int[][] chessBoard = new int[15][15]; // 15x15的棋盘信息

	/**
	 * 以一个棋子为中心判断是否连成5子
	 *
	 * @param col 棋子所在列 (0-14)
	 * @param row 棋子所在行 (0-14)
	 * @return 5个同色棋子相连则返回true
	 */
	public boolean isFive(int col, int row) {
		// 判断落点是否为空以及4个方向是否连成5子
		if (!isEmpty(col, row)
				&& (isFiveHorizontal(col, row) || isFiveVertical(col, row)
				|| isFiveLT(col, row) || isFiveRT(col, row))) {
			return true;
		}
		return false;
	}

	/**
	 * 判断指定位置是否为空
	 */
	private boolean isEmpty(int col, int row) {
		if (chessBoard[row][col] == N) {
			return true;
		}
		return false;
	}

	/**
	 * 判断横向是否连成5子
	 */
	private boolean isFiveHorizontal(int col, int row) {
		int type = chessBoard[row][col]; // 获取落子颜色
		int lineCount = 0; // 连续同色的计数

		// 遍历直线上每个棋子颜色(方向→)
		for (int i = col - 4; i <= col + 4; i++) {
			if (i < 0) {
				continue; // 左侧越界则直接进行下一轮判断
			} else if (i >= 15) {
				break; // 右侧越界则无需继续判断
			}
			if (chessBoard[row][i] != type) {
				lineCount = 0; // 棋子颜色不同则重新计数
			} else {
				lineCount++;
				if (lineCount >= 5) {
					return true; // 连续同色计数达到5返回true
				}
			}
		}

		// 没有5个以上同色棋子相连
		return false;
	}

	/**
	 * 判断纵向是否连成5子
	 */
	private boolean isFiveVertical(int col, int row) {
		int type = chessBoard[row][col]; // 获取落子颜色
		int lineCount = 0; // 连续同色的计数

		// 遍历直线上每个棋子颜色(方向↓)
		for (int i = row - 4; i <= row + 4; i++) {
			if (i < 0) {
				continue; // 上方越界则直接进行下一轮判断
			} else if (i >= 15) {
				break; // 下方越界则无需继续判断
			}
			if (chessBoard[i][col] != type) {
				lineCount = 0; // 棋子颜色不同则重新计数
			} else {
				lineCount++;
				if (lineCount >= 5) {
					return true; // 连续同色计数达到5返回true
				}
			}
		}

		// 没有5个以上同色棋子相连
		return false;
	}

	/**
	 * 判断左斜方向(45度↘)是否连成5子
	 */
	private boolean isFiveLT(int col, int row) {
		int type = chessBoard[row][col]; // 获取落子颜色
		int lineCount = 0; // 连续同色的计数

		// 遍历直线上每个棋子颜色(方向↘)
		for (int i = -4; i <= 4; i++) {
			int x = col + i;
			int y = row + i;
			if (x < 0 || y < 0) {
				continue; // 左侧或上方越界则直接进行下一轮判断
			} else if (x >= 15 || y >= 15) {
				break; // 右侧或下方越界则无需继续判断
			}
			if (chessBoard[y][x] != type) {
				lineCount = 0; // 棋子颜色不同则重新计数
			} else {
				lineCount++;
				if (lineCount >= 5) {
					return true; // 连续同色计数达到5返回true
				}
			}
		}

		// 没有5个以上同色棋子相连
		return false;
	}

	/**
	 * 判断右斜方向(135度↙)是否连成5子
	 */
	private boolean isFiveRT(int col, int row) {
		int type = chessBoard[row][col]; // 获取落子颜色
		int lineCount = 0; // 连续同色的计数

		// 遍历直线上每个棋子颜色(方向↙)
		for (int i = -4; i <= 4; i++) {
			int x = col - i;
			int y = row + i;
			if (x >= 15 || y < 0) {
				continue; // 右侧或上方越界则直接进行下一轮判断
			} else if (x < 0 || y >= 15) {
				break; // 左侧或下方越界则无需继续判断
			}
			if (chessBoard[y][x] != type) {
				lineCount = 0; // 棋子颜色不同则重新计数
			} else {
				lineCount++;
				if (lineCount >= 5) {
					return true; // 连续同色计数达到5返回true
				}
			}
		}

		// 没有5个以上同色棋子相连
		return false;
	}

}
