package com.ruanyf.androidgame39;

import android.view.MotionEvent;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.transitions.CCMoveInLTransition;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.util.CGPointUtil;

/**
 * 对局图层
 * Created by Feng on 2017/12/21.
 */
public class ChessLayer extends CCLayer {

	// 屏幕适配相关
	private static final int chessBoardBorderWidth = 21;
	private static final float chessBoardGridWidth = 31.25f;
	private int chessBoardOffsetY;
	CGSize winSize;

	private boolean isWhiteTurn;
	private CCLabel blackText, whiteText;
	private GameCore gameCore;

	public ChessLayer() {
		super();

		// 屏幕适配
		winSize = CCDirector.sharedDirector().getWinSize();
		chessBoardOffsetY = (int) ((winSize.height - 480) / 2 + chessBoardBorderWidth);

		// 棋盘背景
		CCSprite chessBoard = CCSprite.sprite("chessBoard.jpg");
		chessBoard.setPosition(winSize.width / 2, winSize.height / 2);
		addChild(chessBoard);

		// 玩家头像
		CCSprite blackIcon = CCSprite.sprite("blackIcon.jpg");
		CCSprite whiteIcon = CCSprite.sprite("whiteIcon.jpg");
		blackIcon.setPosition(75, 90);
		whiteIcon.setPosition(winSize.width - 75, winSize.height - 90);
		addChild(blackIcon);
		addChild(whiteIcon);

		// 玩家名字文本
		CCLabel blackName = CCLabel.makeLabel("Player 1", "", 24);
		CCLabel whiteName = CCLabel.makeLabel("Player 2", "", 24);
		blackName.setPosition(200, 90);
		whiteName.setPosition(winSize.width - 200, winSize.height - 90);
		addChild(blackName);
		addChild(whiteName);

		// 玩家状态文本
		blackText = CCLabel.makeLabel("Black's turn", "", 24);
		whiteText = CCLabel.makeLabel("White's turn", "", 24);
		blackText.setPosition(360, 90);
		whiteText.setPosition(winSize.width - 360, winSize.height - 90);
		whiteText.setVisible(false);
		addChild(blackText);
		addChild(whiteText);

		gameCore = new GameCore();
		setIsTouchEnabled(true);
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint touchPoint = convertTouchToNodeSpace(event);

		// 根据触摸点计算所在棋盘格行列索引
		int col = (int) ((touchPoint.x - chessBoardBorderWidth) / chessBoardGridWidth);
		int row = (int) ((touchPoint.y - chessBoardOffsetY) / chessBoardGridWidth);

		// 有可能触摸点没有超过落点十字的x/y坐标，则会被计算为上一行/列，需要2层循环，分别多遍历一次上一行/列的判断
		for (int y = row; y <= row + 1; y++) {
			for (int x = col; x <= col + 1; x++) {
				// 防止越界 & 格子为空才能落子 & 以格子宽度为直径的圆形判定
				if (x >= 0 && x < 15 && y >= 0 && y < 15 // 防止越界
						&& gameCore.chessBoard[y][x] == GameCore.N // 格子为空才能落子
						&& CGPointUtil.distance(getGridPosition(x, y), touchPoint) < chessBoardGridWidth / 2) {
//					if (isWhiteTurn) {
//						CCSprite whiteChess = new CCSprite("whiteChess.png");
//						whiteChess.setPosition(getGridPosition(x, y));
//						addChild(whiteChess);
//						gameCore.chessBoard[y][x] = GameCore.W;
//						if (gameCore.isFive(x, y)) {
//							whiteText.setColor(ccColor3B.ccRED);
//							whiteText.setString("White is WINNER!");
//							end();
//						} else {
//							blackText.setVisible(true);
//							whiteText.setVisible(false);
//							isWhiteTurn = !isWhiteTurn;
//						}
//					} else {
//						CCSprite blackChess = new CCSprite("blackChess.png");
//						blackChess.setPosition(getGridPosition(x, y));
//						addChild(blackChess);
//						gameCore.chessBoard[y][x] = GameCore.B;
//						if (gameCore.isFive(x, y)) {
//							blackText.setColor(ccColor3B.ccRED);
//							blackText.setString("Black is WINNER!");
//							end();
//						} else {
//							blackText.setVisible(false);
//							whiteText.setVisible(true);
//							isWhiteTurn = !isWhiteTurn;
//						}
//					}
					// 尝试精简分支语句代码
					CCSprite chess = new CCSprite(isWhiteTurn ? "whiteChess.png" : "blackChess.png");
					chess.setPosition(getGridPosition(x, y));
					addChild(chess);
					gameCore.chessBoard[y][x] = isWhiteTurn ? GameCore.W : GameCore.B;
					if (gameCore.isFive(x, y)) {
						whiteText.setColor(ccColor3B.ccRED);
						whiteText.setString("White is WINNER!");
						blackText.setColor(ccColor3B.ccRED);
						blackText.setString("Black is WINNER!");
						end();
					} else {
						blackText.setVisible(isWhiteTurn);
						whiteText.setVisible(!isWhiteTurn);
						isWhiteTurn = !isWhiteTurn;
					}
				}
			}
		}
		return super.ccTouchesBegan(event);
	}

	/**
	 * 对局结束的处理
	 */
	private void end() {
		setIsTouchEnabled(false);

		// 2秒后执行重新开局
		CCDelayTime ccDelayTime = CCDelayTime.action(2);
		CCCallFunc ccCallFunc = CCCallFunc.action(this, "back");
		CCSequence ccSequence = CCSequence.actions(ccDelayTime, ccCallFunc);
		runAction(ccSequence);
	}

	/**
	 * 返回主菜单
	 */
	public void back() {
		CCScene ccScene = CCScene.node();
		ccScene.addChild(new MenuLayer());
		CCMoveInLTransition ccMoveInLTransition = CCMoveInLTransition.transition(2, ccScene);
		CCDirector.sharedDirector().replaceScene(ccMoveInLTransition);
	}

	/**
	 * 获取棋盘对应行列的坐标点
	 *
	 * @param col 列索引
	 * @param row 行索引
	 * @return 棋盘对应行列的坐标点
	 */
	public CGPoint getGridPosition(int col, int row) {
		return ccp(chessBoardGridWidth * col + chessBoardBorderWidth,
				chessBoardGridWidth * row + chessBoardOffsetY);
	}
}
