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
import com.example.chess.Chess;
import com.example.chess.ChessBlack;
import com.example.chess.ChessWhite;
import com.example.jni.ChessJNI;
import com.example.manager.ChessManager;
import com.example.manager.CommunicateManager;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import com.example.scene.GameScene;

public class FourGameScene extends GameScene implements IOnSceneTouchListener {

	private final int HALF_LENGTH_FOUR = 70;
	private final int LENGTH_FOUR = 210;
	private final int LINE_WIDTH = 5;

	private ChessBlack[] chess_black;
	private ChessWhite[] chess_white;
	private int[][] chessboard_two;

	@SuppressWarnings("unused")
	private int step;

	private int x1;
	private int x2;
	private int x3;
	private int x4;

	private int y4;
	private int y3;
	private int y2;
	private int y1;

	private Line[] line_x;
	private Line[] line_y;

	private int[][] chess_xy;
	private AnimatedSprite[] sprite;
	private int i_sprite;

	public FourGameScene() {
		super();
		setOnSceneTouchListener(this);
	}

	// 创建棋盘
	@Override
	public void createCheckerboard() {

		x1 = activity.CAMERA_WIDTH / 2 - LENGTH_FOUR;
		x2 = activity.CAMERA_WIDTH / 2 - HALF_LENGTH_FOUR;
		x3 = activity.CAMERA_WIDTH / 2 + HALF_LENGTH_FOUR;
		x4 = activity.CAMERA_WIDTH / 2 + LENGTH_FOUR;

		y4 = activity.CAMERA_HEIGHT / 2 - LENGTH_FOUR;
		y3 = activity.CAMERA_HEIGHT / 2 - HALF_LENGTH_FOUR;
		y2 = activity.CAMERA_HEIGHT / 2 + HALF_LENGTH_FOUR;
		y1 = activity.CAMERA_HEIGHT / 2 + LENGTH_FOUR;

		// 虚拟棋盘
		chess_xy = new int[][] { { x1, y1, 0, 0 },// id=0
				{ x2, y1, 0, 1 },// id=1
				{ x3, y1, 0, 2 },// id=2
				{ x4, y1, 0, 3 },// id=3

				{ x1, y2, 1, 0 },// id=4
				{ x2, y2, 1, 1 },// id=5
				{ x3, y2, 1, 2 },// id=6
				{ x4, y2, 1, 3 },// id=7

				{ x1, y3, 2, 0 },// id=8
				{ x2, y3, 2, 1 },// id=9
				{ x3, y3, 2, 2 },// id=10
				{ x4, y3, 2, 3 },// id=11

				{ x1, y4, 3, 0 },// id=12
				{ x2, y4, 3, 1 },// id=13
				{ x3, y4, 3, 2 },// id=14
				{ x4, y4, 3, 3 },// id=15

		};
		line_x = new Line[] {
				new Line(chess_xy[0][0], chess_xy[0][1], chess_xy[3][0],
						chess_xy[3][1], LINE_WIDTH, vbom),
				new Line(chess_xy[4][0], chess_xy[4][1], chess_xy[7][0],
						chess_xy[7][1], LINE_WIDTH, vbom),
				new Line(chess_xy[8][0], chess_xy[8][1], chess_xy[11][0],
						chess_xy[11][1], LINE_WIDTH, vbom),
				new Line(chess_xy[12][0], chess_xy[12][1], chess_xy[15][0],
						chess_xy[15][1], LINE_WIDTH, vbom), };

		line_y = new Line[] {
				new Line(chess_xy[0][0], chess_xy[0][1], chess_xy[12][0],
						chess_xy[12][1], LINE_WIDTH, vbom),
				new Line(chess_xy[1][0], chess_xy[1][1], chess_xy[13][0],
						chess_xy[13][1], LINE_WIDTH, vbom),
				new Line(chess_xy[2][0], chess_xy[2][1], chess_xy[14][0],
						chess_xy[14][1], LINE_WIDTH, vbom),
				new Line(chess_xy[3][0], chess_xy[3][1], chess_xy[15][0],
						chess_xy[15][1], LINE_WIDTH, vbom), };

		for (int i = 0; i < line_x.length; i++) {
			line_x[i].setColor(Color.BLACK);
			attachChild(line_x[i]);
		}
		for (int i = 0; i < line_y.length; i++) {
			line_y[i].setColor(Color.BLACK);
			attachChild(line_y[i]);
		}

	}

	// 初始化棋子
	@Override
	public void createChess() {
		// 初始化下一步颜色
		ChessManager.getInstance().currChess_color();
		this.step = 0;
		this.result = ChessManager.FLAT;

		// 创建白棋
		chess_white = new ChessWhite[] {
				new ChessWhite(chess_xy[0], 0, this, resourcesManager, vbom),
				new ChessWhite(chess_xy[1], 1, this, resourcesManager, vbom),
				new ChessWhite(chess_xy[2], 2, this, resourcesManager, vbom),
				new ChessWhite(chess_xy[3], 3, this, resourcesManager, vbom), };
		// 创建黑棋
		chess_black = new ChessBlack[] {
				new ChessBlack(chess_xy[12], 12, this, resourcesManager, vbom),
				new ChessBlack(chess_xy[13], 13, this, resourcesManager, vbom),
				new ChessBlack(chess_xy[14], 14, this, resourcesManager, vbom),
				new ChessBlack(chess_xy[15], 15, this, resourcesManager, vbom), };

		for (int i = 0; i < chess_black.length; i++) {
			attachChild(chess_black[i]);
			registerTouchArea(chess_black[i]);
		}
		for (int i = 0; i < chess_white.length; i++) {
			attachChild(chess_white[i]);
			registerTouchArea(chess_white[i]);
		}

		// 二维数组模拟TWO棋盘
		chessboard_two = new int[4][4];
		cerrChessboard_two();

		// 创建chessShow
		sprite = new AnimatedSprite[] {
				new AnimatedSprite(-100, -100,
						ResourcesManager.getInstance().game_chessShow_region,
						vbom),
				new AnimatedSprite(-100, -100,
						ResourcesManager.getInstance().game_chessShow_region,
						vbom),
				new AnimatedSprite(-100, -100,
						ResourcesManager.getInstance().game_chessShow_region,
						vbom),
				new AnimatedSprite(-100, -100,
						ResourcesManager.getInstance().game_chessShow_region,
						vbom), };
		for (int i = 0; i < sprite.length; i++) {
			sprite[i].animate(50);
			attachChild(sprite[i]);

		}

		ChessManager.getInstance().initializeStack();
	}

	public void cerrChessboard_two() {
		chessboard_two[0][0] = ChessManager.CHESS_WHITE;
		chessboard_two[0][1] = ChessManager.CHESS_WHITE;
		chessboard_two[0][2] = ChessManager.CHESS_WHITE;
		chessboard_two[0][3] = ChessManager.CHESS_WHITE;

		chessboard_two[1][0] = ChessManager.CHESS_EMPTY;
		chessboard_two[1][1] = ChessManager.CHESS_EMPTY;
		chessboard_two[1][2] = ChessManager.CHESS_EMPTY;
		chessboard_two[1][3] = ChessManager.CHESS_EMPTY;

		chessboard_two[2][0] = ChessManager.CHESS_EMPTY;
		chessboard_two[2][1] = ChessManager.CHESS_EMPTY;
		chessboard_two[2][2] = ChessManager.CHESS_EMPTY;
		chessboard_two[2][3] = ChessManager.CHESS_EMPTY;

		chessboard_two[3][0] = ChessManager.CHESS_BLACK;
		chessboard_two[3][1] = ChessManager.CHESS_BLACK;
		chessboard_two[3][2] = ChessManager.CHESS_BLACK;
		chessboard_two[3][3] = ChessManager.CHESS_BLACK;
	}

	public void cerrChessBorad() {
		for (int i = 0; i < chess_white.length; i++) {
			chess_white[i].setPosition(chess_xy[i][0], chess_xy[i][1]);
			chess_white[i].ID = i;
		}
		for (int i = 0; i < chess_black.length; i++) {
			chess_black[i]
					.setPosition(chess_xy[i + 12][0], chess_xy[i + 12][1]);
			chess_black[i].ID = i + 12;
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
		} else if (pX > x4 - 15 && pX < x4 + 15) {
			x_one = x4;
		}

		if (pY > y1 - 15 && pY < y1 + 15) {
			y_one = y1;
		} else if (pY > y2 - 15 && pY < y2 + 15) {
			y_one = y2;
		} else if (pY > y3 - 15 && pY < y3 + 15) {
			y_one = y3;
		} else if (pY > y4 - 15 && pY < y4 + 15) {
			y_one = y4;
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

			int currentXY = isPosition(pSceneTouchEvent.getX(), // 得到点击的棋盘的位置
					pSceneTouchEvent.getY());

			int id = 0; // 棋子原来的位置
			if (ChessManager.getInstance().currentChessIsUp == ChessManager.YES_CHESS_UP)
				id = ChessManager.getInstance().getCurrentChess().ID;

			if (moveChess
					&& result == ChessManager.FLAT // 如果结果是平局，有棋子点起来，点击的位置在棋盘上，点击的位置可以放置棋子,移动的位置差1
					&& ChessManager.getInstance().currentChessIsUp == ChessManager.YES_CHESS_UP
					&& currentXY != -1
					&& chessboard_two[chess_xy[currentXY][2]][chess_xy[currentXY][3]] == ChessManager.CHESS_EMPTY
					&& Math.abs((chess_xy[id][2] + chess_xy[id][3])
							- (chess_xy[currentXY][2] + chess_xy[currentXY][3])) == 1) {

				this.nowjustis(currentXY, id, ChessManager.USA);

				if (result == ChessManager.FLAT
						&& SceneManager.GameType == SceneManager.ONLINE) {

					// 调用AI,返回六元素数组，前两个是移动之前的坐标，中间两个是移动后的坐标，
					// 最后两个是对方被吃掉的棋子的坐标，若值为-1则表示没有。
					int fourai[] = ChessJNI.getInstance().fFour_fAI(
							chessboard_two,
							ChessManager.getInstance().getNextCHESS_COLOR());
					int ai_id = getID(fourai[0], fourai[1]);
					int ai_currentXY = getID(fourai[2], fourai[3]);
					int ai_eatChess[] = new int[] { fourai[4], fourai[5] };

					ChessManager.getInstance().SeatChess.push(ai_eatChess);// 记录被吃掉的棋子

					int aicolor = chessboard_two[chess_xy[ai_id][2]][chess_xy[ai_id][3]];

					ChessManager.getInstance().setCurrentChess(
							this.getChess(ai_id, aicolor));
					this.movechess(ai_id, ai_currentXY, ChessManager.AI);
					this.getResult(ai_eatChess, ChessManager.AI);
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

		int eatChess[] = ChessJNI.getInstance().feat_fchess(
		// 落下的棋子的坐标
				chessboard_two, chess_xy[currentXY][2], chess_xy[currentXY][3]);

		ChessManager.getInstance().SeatChess.push(eatChess); // 记录被吃掉的棋子
		this.getResult(eatChess, ChessManager.USA);
	}

	public void movechess(int id, int currentXY, int pUsa) {

		chessboard_two[chess_xy[id][2]][chess_xy[id][3]] = ChessManager.CHESS_EMPTY;// 虚拟棋盘的移动
		chessboard_two[chess_xy[currentXY][2]][chess_xy[currentXY][3]] = ChessManager
				.getInstance().getCurrentChess().getChess_color();
		ChessManager.getInstance().getCurrentChess().ID = currentXY;

		ChessManager
				// 棋盘的移动
				.getInstance()
				.getCurrentChess()
				.pathChess(chess_xy[id][0], chess_xy[id][1],
						chess_xy[currentXY][0], chess_xy[currentXY][1], pUsa);

		if (pUsa == ChessManager.USA) {

			ChessManager.getInstance().getCurrentChess().setCurrentState();// 改变棋子的状态和管理器中棋子的状态
		}
		ChessManager.getInstance().currentChessIsUp = ChessManager
				.getInstance().getCurrentChess().currentState;

		ChessManager.getInstance().setNextChess_color();// 设置可下一步可以移动的棋子的类别
		if (pUsa != ChessManager.HUIQI) {

			ChessManager.getInstance().setoldnewChess(id, currentXY);// 设置新旧棋子
			this.step++;
		} else {
			this.step--;
			Chess chess = null;
			int color = ChessManager.getInstance().getNextCHESS_COLOR();
			int[] eat = null;
			if (ChessManager.getInstance().SeatChess.length > 0)
				eat = ChessManager.getInstance().SeatChess.pop();
			if (eat != null && eat[0] != -1) {
				if (color == ChessManager.CHESS_WHITE) {
					for (int i = 0; i < chess_black.length; i++) {
						if (chess_black[i].ID == -1)
							chess = chess_black[i];
					}
					chessboard_two[eat[0]][eat[1]] = ChessManager.CHESS_BLACK;
				} else if (color == ChessManager.CHESS_BLACK) {
					for (int i = 0; i < chess_white.length; i++) {
						if (chess_white[i].ID == -1)
							chess = chess_white[i];
					}
					chessboard_two[eat[0]][eat[1]] = ChessManager.CHESS_WHITE;
				}
				int eatid = getID(eat[0], eat[1]);
				if (chess != null) {
					chess.setPosition(chess_xy[eatid][0], chess_xy[eatid][1]);
					chess.ID = eatid;
				}
			}
		}
		this.upText();
	}

	private void getResult(int eatChess[], int pUsa) {

		if (eatChess[0] != -1) { // 棋子被吃掉
			Chess chess = null;

			int color = chessboard_two[eatChess[0]][eatChess[1]];// 在虚拟棋盘上拿掉
			chessboard_two[eatChess[0]][eatChess[1]] = ChessManager.CHESS_EMPTY;
			int eatId = getID(eatChess[0], eatChess[1]);

			chess = this.getChess(eatId, color);
			if (chess != null) {
				chess.dieChess(pUsa);
			}

			result = ChessJNI.getInstance().fjudge_fvictory_ffour(// 吃掉后判断胜负
					chessboard_two);
			if (result != ChessManager.FLAT) {

				final Message message = new Message();
				message.what = 0x1144;
				if (result == ChessManager.CHESS_WHITE_WIN) {
					switch (SceneManager.GameType) {
					case SceneManager.PLAYER:
						message.obj = "丫丫胜";
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
						message.obj = "丫丫胜";
						return;
					}
				} else {
					switch (SceneManager.GameType) {
					case SceneManager.PLAYER:
						message.obj = "星星胜";
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
						message.obj = "星星胜";
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
	}

	@Override
	public void dialoging(int n) {
		this.cerrChessBorad();
	}

	private int getID(int x, int y) {
		for (int i = 0; i < chess_xy.length; i++) {
			if (x == chess_xy[i][2] && y == chess_xy[i][3])
				return i;
		}
		return -1;
	}

	@Override
	public int getGameType() {
		return SceneManager.FOUR;
	}

	@Override
	public Chess getChess(int id, int color) {

		switch (color) {
		case ChessManager.CHESS_BLACK:
			for (int i = 0; i < chess_black.length; i++) {
				if (id == chess_black[i].ID) {
					return chess_black[i];
				}
			}
			break;
		case ChessManager.CHESS_WHITE:
			for (int i = 0; i < chess_white.length; i++) {
				if (id == chess_white[i].ID) {
					return chess_white[i];
				}
			}
			break;
		case -1:
			for (int i = 0; i < chess_black.length; i++) {
				if (id == chess_black[i].ID) {
					return chess_black[i];
				}
			}
			for (int i = 0; i < chess_white.length; i++) {
				if (id == chess_white[i].ID) {
					return chess_white[i];
				}
			}
			break;
		}
		return null;
	}

	@Override
	public void showNextChess(int id) {
		i_sprite = 0;
		if ((id + 1) % 4 != 0
				&& chessboard_two[chess_xy[id + 1][2]][chess_xy[id + 1][3]] == ChessManager.CHESS_EMPTY) {
			sprite[i_sprite].setPosition(chess_xy[id + 1][0],
					chess_xy[id + 1][1]);
			i_sprite++;
		}
		if (id % 4 != 0
				&& chessboard_two[chess_xy[id - 1][2]][chess_xy[id - 1][3]] == ChessManager.CHESS_EMPTY) {
			sprite[i_sprite].setPosition(chess_xy[id - 1][0],
					chess_xy[id - 1][1]);
			i_sprite++;
		}
		if (id - 4 > 0
				&& chessboard_two[chess_xy[id - 4][2]][chess_xy[id - 4][3]] == ChessManager.CHESS_EMPTY) {
			sprite[i_sprite].setPosition(chess_xy[id - 4][0],
					chess_xy[id - 4][1]);
			i_sprite++;
		}
		if (id + 4 < 16
				&& chessboard_two[chess_xy[id + 4][2]][chess_xy[id + 4][3]] == ChessManager.CHESS_EMPTY) {
			sprite[i_sprite].setPosition(chess_xy[id + 4][0],
					chess_xy[id + 4][1]);
			i_sprite++;
		}

	}

	@Override
	public void notShowNextChess() {
		for (int i = 0; i < i_sprite; i++) {
			sprite[i].setPosition(-100, -100);
		}

	}

}
