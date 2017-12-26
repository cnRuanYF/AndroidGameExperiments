package com.ruanyf.androidgame40;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;

/**
 * Created by Feng on 2017/12/21.
 */
public class Computer {

	// 棋盘格子状态常量
	public static final int N = GameCore.N; // 空位置
	public static final int B = GameCore.B; // 有黑色棋子（人的棋）
	public static final int W = GameCore.W; // 有白色棋子（电脑的棋）
	public static final int S = 3; // 需要下子的位置

	// 棋谱数组，数组中行数越高，表明该行棋谱中S位置越重要，电脑走最重要的位置
	private int chessManual[][] = {

			//一个棋子的情况
			{N, N, N, S, B}, {B, S, N, N, N}, {N, N, N, S, B}, {N, B, S, N, N}, {N, N, S, B, N}, {N, N, B, S, N},
			{N, N, N, S, W}, {W, S, N, N, N}, {N, N, N, S, W}, {N, W, S, N, N}, {N, N, S, W, N}, {N, N, W, S, N},

			//两个棋子的情况
			{B, B, S, N, N}, {N, N, S, B, B}, {B, S, B, N, N}, {N, N, B, S, B}, {N, B, S, B, N}, {N, B, B, S, N}, {N, S, B, B, N},
			{W, W, S, N, N}, {N, N, S, W, W}, {W, S, W, N, N}, {N, N, W, S, W}, {N, W, S, W, N}, {N, W, W, S, N}, {N, S, W, W, N},

			//三个棋子的情况
			{N, S, B, B, B}, {B, B, B, S, N}, {N, B, B, B, S}, {N, B, S, B, B}, {B, B, S, B, N},
			{N, S, W, W, W}, {W, W, W, S, N}, {N, W, W, W, S}, {N, W, S, W, W}, {W, W, S, W, N},

			//四个棋子的情况
			{S, B, B, B, B}, {B, S, B, B, B}, {B, B, S, B, B}, {B, B, B, S, B}, {B, B, B, B, S},
			{S, W, W, W, W}, {W, S, W, W, W}, {W, W, S, W, W}, {W, W, W, S, W}, {W, W, W, W, S}
	};

	private GameCore gameCore;

	public Computer(GameCore gameCore) {
		this.gameCore = gameCore;
	}

	/**
	 * 电脑根据棋谱下棋
	 *
	 * @return 棋盘格索引(列, 行)
	 */
	public CGPoint input() {
		int bestCol = -1, bestRow = -1; // 最佳位置
		int bestPriority = -1; // 最佳优先级
		int tmpCol = -1, tmpRow = -1; // 临时位置
		boolean isComform; // 是否符合棋谱

		// 遍历每一个棋盘格子
		for (int row = 0; row < 15; row++) {
			for (int col = 0; col < 15; col++) {
				// 遍历棋谱组合，优先级从低到高，需要倒序遍历
				for (int i = chessManual.length - 1; i >= 0; i--) {

					// 横向判断（→）
					if (col + 4 < 15) {// 防止越界
						// 初始化临时变量
						tmpRow = -1;
						tmpCol = -1;
						isComform = true;
						// 遍历排列中的5个棋子
						for (int j = 0; j < 5; j++) {
							// 若位置为空 & 对应棋谱位置为S
							if (gameCore.chessBoard[row][col + j] == N && chessManual[i][j] == S) {
								// 保存临时位置
								tmpCol = col + j;
								tmpRow = row;
							} else if (gameCore.chessBoard[row][col + j] != chessManual[i][j]) {
								// 否则标记为不匹配，结束本排列遍历
								isComform = false;
								break;
							}
						}
						// 若匹配棋谱，且优先级比上次找到的更高
						if (isComform && i > bestPriority) {
							// 保存最优属性
							bestPriority = i;
							bestCol = tmpCol;
							bestRow = tmpRow;
							break;
						}
					}

					// 纵向判断（↓）
					if (row + 4 < 15) {// 防止越界
						// 初始化临时变量
						tmpRow = -1;
						tmpCol = -1;
						isComform = true;
						// 遍历排列中的5个棋子
						for (int j = 0; j < 5; j++) {
							// 若位置为空 & 对应棋谱位置为S
							if (gameCore.chessBoard[row + j][col] == N && chessManual[i][j] == S) {
								// 保存临时位置
								tmpCol = col;
								tmpRow = row + j;
							} else if (gameCore.chessBoard[row + j][col] != chessManual[i][j]) {
								// 否则标记为不匹配，结束本排列遍历
								isComform = false;
								break;
							}
						}
						// 若匹配棋谱，且优先级比上次找到的更高
						if (isComform && i > bestPriority) {
							// 保存最优属性
							bestPriority = i;
							bestCol = tmpCol;
							bestRow = tmpRow;
							break;
						}
					}

					// 斜45度判断（↘）
					if (col + 4 < 15 && row + 4 < 15) {// 防止越界
						// 初始化临时变量
						tmpRow = -1;
						tmpCol = -1;
						isComform = true;
						// 遍历排列中的5个棋子
						for (int j = 0; j < 5; j++) {
							// 若位置为空 & 对应棋谱位置为S
							if (gameCore.chessBoard[row + j][col + j] == N && chessManual[i][j] == S) {
								// 保存临时位置
								tmpCol = col + j;
								tmpRow = row + j;
							} else if (gameCore.chessBoard[row + j][col + j] != chessManual[i][j]) {
								// 否则标记为不匹配，结束本排列遍历
								isComform = false;
								break;
							}
						}
						// 若匹配棋谱，且优先级比上次找到的更高
						if (isComform && i > bestPriority) {
							// 保存最优属性
							bestPriority = i;
							bestCol = tmpCol;
							bestRow = tmpRow;
							break;
						}
					}

					// 斜135度判断（↙）
					if (col - 4 >= 0 && row + 4 < 15) {// 防止越界
						// 初始化临时变量
						tmpRow = -1;
						tmpCol = -1;
						isComform = true;
						// 遍历排列中的5个棋子
						for (int j = 0; j < 5; j++) {
							// 若位置为空 & 对应棋谱位置为S
							if (gameCore.chessBoard[row + j][col - j] == N && chessManual[i][j] == S) {
								// 保存临时位置
								tmpCol = col - j;
								tmpRow = row + j;
							} else if (gameCore.chessBoard[row + j][col - j] != chessManual[i][j]) {
								// 否则标记为不匹配，结束本排列遍历
								isComform = false;
								break;
							}
						}
						// 若匹配棋谱，且优先级比上次找到的更高
						if (isComform && i > bestPriority) {
							// 保存最优属性
							bestPriority = i;
							bestCol = tmpCol;
							bestRow = tmpRow;
							break;
						}
					}

				}
			}
		}

		// 若匹配到了棋谱
		if (bestPriority != -1) {
			gameCore.chessBoard[bestRow][bestCol] = W; // TODO 电脑只能下白棋,后续可改进
			return CCNode.ccp(bestCol, bestRow);
		}

		// 没有匹配到棋谱则遍历所有格子找一个空位置
		for (int row = 0; row < 15; row++) {
			for (int col = 0; col < 15; col++) {
				// 若找到空位
				if (gameCore.chessBoard[row][col] == N) {
					gameCore.chessBoard[row][col] = W;// TODO 电脑只能下白棋,后续可改进
					return CCNode.ccp(col, row);
				}
			}
		}
		return null;
	}
}
