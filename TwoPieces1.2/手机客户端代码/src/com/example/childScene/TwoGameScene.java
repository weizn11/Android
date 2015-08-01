package com.example.childScene;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;

import android.os.Message;
import android.util.Log;

import com.example.chess.Chess;
import com.example.chess.ChessBlack;
import com.example.chess.ChessWhite;
import com.example.jni.ChessJNI;
import com.example.manager.ChessManager;
import com.example.manager.CommunicateManager;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import com.example.scene.GameScene;

public class TwoGameScene extends GameScene implements IOnSceneTouchListener {

	private int[][] chess_xy;
	private ChessBlack[] chess_black;
	private ChessWhite[] chess_white;

	private int[][] chessboard_two;

	private int step;

	private final int HALF_LENGTH = 166;
	private final int LINE_WIDTH = 5;

	private int x1;
	private int x2;
	private int x3;

	private int y3;
	private int y2;
	private int y1;

	private Line[] line_x;
	private Line[] line_y;

	private AnimatedSprite sprite;

	public TwoGameScene() {
		super();
		setOnSceneTouchListener(this);
	}

	// 创建TWO棋盘
	public void createCheckerboard() {

		x1 = activity.CAMERA_WIDTH / 2 - HALF_LENGTH;
		x2 = activity.CAMERA_WIDTH / 2;
		x3 = activity.CAMERA_WIDTH / 2 + HALF_LENGTH;

		y3 = activity.CAMERA_HEIGHT / 2 - HALF_LENGTH;
		y2 = activity.CAMERA_HEIGHT / 2;
		y1 = activity.CAMERA_HEIGHT / 2 + HALF_LENGTH;

		chess_xy = new int[][] { { x1, y1, 0, 0 }, { x3, y1, 0, 2 },
				{ x2, y2, 1, 1 }, { x1, y3, 2, 0 }, { x3, y3, 2, 2 }, };

		line_x = new Line[] {
				new Line(chess_xy[0][0], chess_xy[0][1], chess_xy[1][0],
						chess_xy[1][1], LINE_WIDTH, vbom),
				new Line(chess_xy[3][0], chess_xy[3][1], chess_xy[4][0],
						chess_xy[4][1], LINE_WIDTH, vbom), };

		line_y = new Line[] {
				new Line(chess_xy[0][0], chess_xy[0][1], chess_xy[3][0],
						chess_xy[3][1], LINE_WIDTH, vbom),
				new Line(chess_xy[0][0], chess_xy[0][1], chess_xy[4][0],
						chess_xy[4][1], LINE_WIDTH, vbom),
				new Line(chess_xy[1][0], chess_xy[1][1], chess_xy[3][0],
						chess_xy[3][1], LINE_WIDTH, vbom), };

		for (int i = 0; i < line_x.length; i++) {
			line_x[i].setColor(Color.BLACK);
			attachChild(line_x[i]);
		}
		for (int i = 0; i < line_y.length; i++) {
			line_y[i].setColor(Color.BLACK);
			attachChild(line_y[i]);
		}

	}

	// 初始化TWO棋子
	public void createChess() {

		// 初始化下一步颜色
		ChessManager.getInstance().currChess_color();
		this.step = 0;
		this.result = ChessManager.FLAT;

		// 创建白棋
		chess_white = new ChessWhite[] {
				new ChessWhite(chess_xy[0], 0, this, resourcesManager, vbom),
				new ChessWhite(chess_xy[1], 1, this, resourcesManager, vbom), };
		// 创建黑棋
		chess_black = new ChessBlack[] {
				new ChessBlack(chess_xy[3], 3, this, resourcesManager, vbom),
				new ChessBlack(chess_xy[4], 4, this, resourcesManager, vbom), };

		// 二维数组模拟TWO棋盘
		chessboard_two = new int[3][3];
		cerrChessboard_two();

		for (int i = 0; i < chess_black.length; i++) {
			attachChild(chess_black[i]);
			registerTouchArea(chess_black[i]);
		}
		for (int i = 0; i < chess_white.length; i++) {
			attachChild(chess_white[i]);
			registerTouchArea(chess_white[i]);
		}

		sprite = new AnimatedSprite(-100, -100,
				ResourcesManager.getInstance().game_chessShow_region, vbom);
		sprite.animate(50);
		attachChild(sprite);

		ChessManager.getInstance().initializeStack();

	}

	public void cerrChessboard_two() {
		chessboard_two[0][1] = ChessManager.CHESS_NO;
		chessboard_two[1][0] = ChessManager.CHESS_NO;
		chessboard_two[1][1] = ChessManager.CHESS_EMPTY;
		chessboard_two[2][1] = ChessManager.CHESS_NO;
		chessboard_two[1][2] = ChessManager.CHESS_NO;
		chessboard_two[0][0] = ChessManager.CHESS_WHITE;
		chessboard_two[0][2] = ChessManager.CHESS_WHITE;
		chessboard_two[2][0] = ChessManager.CHESS_BLACK;
		chessboard_two[2][2] = ChessManager.CHESS_BLACK;
	}

	public void cerrChessBorad() {
		for (int i = 0; i < chess_white.length; i++) {
			chess_white[i].setPosition(chess_xy[i][0], chess_xy[i][1]);
			chess_white[i].ID = i;
		}
		for (int i = 0; i < chess_black.length; i++) {
			chess_black[i].setPosition(chess_xy[i + 3][0], chess_xy[i + 3][1]);
			chess_black[i].ID = i + 3;
		}
		cerrChessboard_two();
		// 初始化下一步颜色
		ChessManager.getInstance().currChess_color();
		this.step = 0;
		this.result = ChessManager.FLAT;
		ChessManager.getInstance().initializeStack();
	}

	// 判断是否落在棋盘上
	private int isPosition(float pX, float pY) {

		int x_one = -1;
		int y_one = -1;

		if (pX > x1 - 15 && pX < x1 + 15) {
			x_one = x1;
		} else if (pX > x2 - 15 && pX < x2 + 15) {
			x_one = x2;
		} else if (pX > x3 - 15 && pX < x3 + 15) {
			x_one = x3;
		}

		if (pY > y1 - 15 && pY < y1 + 15) {
			y_one = y1;
		} else if (pY > y2 - 15 && pY < y2 + 15) {
			y_one = y2;
		} else if (pY > y3 - 15 && pY < y3 + 15) {
			y_one = y3;
		}

		for (int i = 0; i < chess_xy.length; i++) {
			if (x_one == chess_xy[i][0] && y_one == chess_xy[i][1])
				return i;
		}
		return -1;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {

			int currentXY = isPosition(pSceneTouchEvent.getX(),// 得到点击的棋盘的位置
					pSceneTouchEvent.getY());

			int id = 0; // 棋子原来的位置
			if (ChessManager.getInstance().currentChessIsUp == ChessManager.YES_CHESS_UP)
				id = ChessManager.getInstance().getCurrentChess().ID;

			if (moveChess
					&& result == ChessManager.FLAT // 如果结果是平局，有棋子点起来，点击的位置在棋盘上，点击的位置可以放置棋子
					&& ChessManager.getInstance().currentChessIsUp == ChessManager.YES_CHESS_UP
					&& currentXY != -1
					&& chessboard_two[chess_xy[currentXY][2]][chess_xy[currentXY][3]] == ChessManager.CHESS_EMPTY
					&& (currentXY - id < 4)
					&& !((id == 4 && currentXY == 1) || (id == 1 && currentXY == 4))) {

				this.nowjustis(currentXY, id, ChessManager.USA);

				if (result == ChessManager.FLAT // 调用AI
						&& SceneManager.GameType == SceneManager.ONLINE) {
					int a[] = ChessJNI.getInstance().tAI(chessboard_two,
							ChessManager.getInstance().getNextCHESS_COLOR(), 1);

					int iid = getID(a[1], a[2]);
					int cXY = getID(a[3], a[4]);

					ChessManager.getInstance().setCurrentChess(
							this.getChess(iid, ChessManager.getInstance()
									.getNextCHESS_COLOR()));

					this.movechess(iid, cXY, ChessManager.AI);

					result = ChessJNI.getInstance().tjudge_tvictory( // 判断胜负
							chessboard_two);
					this.getResult(result, ChessManager.AI);
				}

			}
		}
		return false;
	}

	@Override
	public void nowjustis(int currentXY, int id, int state) {
		if (SceneManager.GameType == SceneManager.NET
				&& state == ChessManager.RECV) {
			ChessManager.getInstance().setCurrentChess(this.getChess(id, -1));
		}
		this.movechess(id, currentXY, ChessManager.USA); // 移动棋子
		if (SceneManager.GameType == SceneManager.NET
				&& state == ChessManager.USA)
			CommunicateManager.getCommunicate().send(currentXY, id);

		result = ChessJNI.getInstance().tjudge_tvictory(chessboard_two);// 判断胜负
		this.getResult(result, ChessManager.USA); // 显示胜负的窗口

	}

	private void getResult(int result, int pUsa) {
		if (result != ChessManager.FLAT) {

			final Message message = new Message();
			message.what = 0x1144;
			if (this.step == 1) {
				message.obj = "第一步不允许这样走！";
			} else if (result == ChessManager.CHESS_WHITE_WIN) {
				switch (SceneManager.GameType) {
				case SceneManager.PLAYER:
					message.obj = "丫丫胜！";
					break;
				case SceneManager.ONLINE:
					message.obj = "您输了！";
					break;
				case SceneManager.NET:
					if (myChess == ChessManager.CHESS_WHITE)
						message.obj = "您胜了！";
					else
						message.obj = "您输了！";
					break;
				default:
					message.obj = "丫丫胜！";
					return;
				}

			} else {
				switch (SceneManager.GameType) {
				case SceneManager.PLAYER:
					message.obj = "星星胜！";
					break;
				case SceneManager.ONLINE:
					message.obj = "您胜了！";
					break;
				case SceneManager.NET:
					if (myChess == ChessManager.CHESS_WHITE)
						message.obj = "您输了！";
					else
						message.obj = "您胜了！";
					break;
				default:
					message.obj = "星星胜！";
					return;
				}

			}
			float mf = 0;
			if (pUsa == ChessManager.AI)
				mf = 1f;
			else
				mf = 0.2f;
			this.registerUpdateHandler(new TimerHandler(mf,
					new ITimerCallback() {

						@Override
						public void onTimePassed(
								final TimerHandler pTimerHandler) {
							activity.mHandler.sendMessage(message);
							if (SceneManager.GameType == SceneManager.NET) {
								CommunicateManager.getCommunicate().bb = false;
								onBackKeyPressed();
							}
						}
					}));
		}
	}

	public void movechess(int id, int currentXY, int pUsa) {

		chessboard_two[chess_xy[id][2]][chess_xy[id][3]] = ChessManager.CHESS_EMPTY; // 虚拟棋盘的移动
		chessboard_two[chess_xy[currentXY][2]][chess_xy[currentXY][3]] = ChessManager
				.getInstance().getCurrentChess().getChess_color();
		ChessManager.getInstance().getCurrentChess().ID = currentXY;

		ChessManager
				// 棋盘的移动
				.getInstance()
				.getCurrentChess()
				.pathChess(chess_xy[id][0], chess_xy[id][1],
						chess_xy[currentXY][0], chess_xy[currentXY][1], pUsa);

		if (pUsa == ChessManager.USA) { // 改变棋子的状态和管理器中棋子的状态
			ChessManager.getInstance().getCurrentChess().setCurrentState();
		}
		ChessManager.getInstance().currentChessIsUp = ChessManager
				.getInstance().getCurrentChess().currentState;
		ChessManager.getInstance().setNextChess_color(); // 设置可下一步可以移动的棋子的类别
		if (pUsa != ChessManager.HUIQI) {
			ChessManager.getInstance().setoldnewChess(id, currentXY); // 设置新旧棋子
		}
		this.upText();
		if (pUsa != ChessManager.HUIQI)
			this.step++;
		else
			this.step--;
	}

	private int getID(int i, int j) {
		switch (i) {
		case 0:
			if (j == 0)
				return 0;
			else
				return 1;
		case 1:
			return 2;
		case 2:
			if (j == 0)
				return 3;
			else
				return 4;
		default:
			return -1;
		}
	}

	@Override
	public int getGameType() {
		return SceneManager.TWO;
	}

	@Override
	public void dialoging(int n) {
		this.cerrChessBorad();
	}

	@Override
	public Chess getChess(int id, int color) {
		Chess chess = null;
		switch (color) {
		case ChessManager.CHESS_BLACK:
			for (int i = 0; i < chess_black.length; i++) {
				if (id == chess_black[i].ID) {
					chess = chess_black[i];
					break;
				}
			}
			break;
		case ChessManager.CHESS_WHITE:
			for (int i = 0; i < chess_white.length; i++) {
				if (id == chess_white[i].ID) {
					chess = chess_white[i];
					break;
				}
			}
			break;
		case -1:
			for (int i = 0; i < chess_black.length; i++) {
				if (id == chess_black[i].ID) {
					Log.e("++", chess_black[i].ID + "");
					chess = chess_black[i];
					break;
				}
			}
			if (chess == null) {
				for (int i = 0; i < chess_white.length; i++) {
					if (id == chess_white[i].ID) {
						Log.e("++", chess_white[i].ID + "");
						chess = chess_white[i];
						break;
					}
				}
			}
			break;
		default:
			break;
		}
		return chess;
	}

	@Override
	public void showNextChess(int id) {
		int Id = 0;
		for (int i = 0; i < chessboard_two.length; i++) {
			for (int j = 0; j < chessboard_two[i].length; j++) {
				if (chessboard_two[i][j] == ChessManager.CHESS_EMPTY) {
					Id = this.getID(i, j);
					switch (Id) {
					case 1:
						if (id == 0 || id == 2) {
							sprite.setPosition(chess_xy[Id][0], chess_xy[Id][1]);
							//sprite.animate(100);
						}
						break;
					case 4:
						if (id == 3 || id == 2) {
							sprite.setPosition(chess_xy[Id][0], chess_xy[Id][1]);
							//sprite.animate(100);
						}
						break;
					default:
						sprite.setPosition(chess_xy[Id][0], chess_xy[Id][1]);
						break;
					}

				}
			}
		}
	}

	@Override
	public void notShowNextChess() {
		sprite.setPosition(-100, 100);
	}

}
