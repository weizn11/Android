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

public class NineGameScene extends GameScene implements IOnSceneTouchListener {

	private final int HALF_LENGTH_NINE = 110;
	private final int LENGTH_NINE = 220;
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
	private int x5;
	private int x6;
	private int x7;

	private int y5;
	private int y4;
	private int y3;
	private int y2;
	private int y1;

	private Line[] line_x;
	private Line[] line_y;
	private AnimatedSprite[] sprite;
	private int i_sprite;

	private int[][] chess_xy;

	public NineGameScene() {
		super();
		activity.camera.setBounds(0, activity.camera.getCenterX() + 750, 0,
				activity.camera.getCenterY());

		setOnSceneTouchListener(this);
	}

	// 创建棋盘
	@Override
	public void createCheckerboard() {

		x1 = activity.CAMERA_WIDTH / 2 - LENGTH_NINE;
		x2 = activity.CAMERA_WIDTH / 2 - HALF_LENGTH_NINE;
		x3 = activity.CAMERA_WIDTH / 2;
		x4 = activity.CAMERA_WIDTH / 2 + HALF_LENGTH_NINE;
		x5 = activity.CAMERA_WIDTH / 2 + LENGTH_NINE;
		x6 = activity.CAMERA_WIDTH / 2 + LENGTH_NINE + HALF_LENGTH_NINE;
		x7 = activity.CAMERA_WIDTH / 2 + LENGTH_NINE + HALF_LENGTH_NINE * 2;

		y5 = activity.CAMERA_HEIGHT / 2 - LENGTH_NINE;
		y4 = activity.CAMERA_HEIGHT / 2 - HALF_LENGTH_NINE;
		y3 = activity.CAMERA_HEIGHT / 2;
		y2 = activity.CAMERA_HEIGHT / 2 + HALF_LENGTH_NINE;
		y1 = activity.CAMERA_HEIGHT / 2 + LENGTH_NINE;

		// 虚拟棋盘
		chess_xy = new int[][] { { x1, y1, 0, 0 },// id=0
				{ x2, y1, 0, 1 },// id=1
				{ x3, y1, 0, 2 },// id=2
				{ x4, y1, 0, 3 },// id=3
				{ x5, y1, 0, 4 },// id=4

				{ x1, y2, 1, 0 },// id=5
				{ x2, y2, 1, 1 },// id=6
				{ x3, y2, 1, 2 },// id=7
				{ x4, y2, 1, 3 },// id=8
				{ x5, y2, 1, 4 },// id=9

				{ x1, y3, 2, 0 },// id=10
				{ x2, y3, 2, 1 },// id=11
				{ x3, y3, 2, 2 },// id=12
				{ x4, y3, 2, 3 },// id=13
				{ x5, y3, 2, 4 },// id=14

				{ x1, y4, 3, 0 },// id=15
				{ x2, y4, 3, 1 },// id=16
				{ x3, y4, 3, 2 },// id=17
				{ x4, y4, 3, 3 },// id=18
				{ x5, y4, 3, 4 },// id=19

				{ x1, y5, 4, 0 },// id=20
				{ x2, y5, 4, 1 },// id=21
				{ x3, y5, 4, 2 },// id=22
				{ x4, y5, 4, 3 },// id=23
				{ x5, y5, 4, 4 },// id=24

				{ x6, y2, 1, 5 },// id=25
				{ x6, y3, 2, 5 },// id=26
				{ x6, y4, 3, 5 },// id=27
				{ x7, y3, 2, 6 },// id=28

		};
		line_x = new Line[] {
				new Line(chess_xy[0][0], chess_xy[0][1], chess_xy[4][0],
						chess_xy[4][1], LINE_WIDTH, vbom),
				new Line(chess_xy[5][0], chess_xy[5][1], chess_xy[9][0],
						chess_xy[9][1], LINE_WIDTH, vbom),
				new Line(chess_xy[10][0], chess_xy[10][1], chess_xy[28][0],
						chess_xy[28][1], LINE_WIDTH, vbom),
				new Line(chess_xy[15][0], chess_xy[15][1], chess_xy[19][0],
						chess_xy[19][1], LINE_WIDTH, vbom),
				new Line(chess_xy[20][0], chess_xy[20][1], chess_xy[24][0],
						chess_xy[24][1], LINE_WIDTH, vbom),

				new Line(chess_xy[0][0], chess_xy[0][1], chess_xy[24][0],
						chess_xy[24][1], LINE_WIDTH, vbom),
				new Line(chess_xy[4][0], chess_xy[4][1], chess_xy[20][0],
						chess_xy[20][1], LINE_WIDTH, vbom),
				new Line(chess_xy[2][0], chess_xy[2][1], chess_xy[10][0],
						chess_xy[10][1], LINE_WIDTH, vbom),
				new Line(chess_xy[2][0], chess_xy[2][1], chess_xy[14][0],
						chess_xy[14][1], LINE_WIDTH, vbom),
				new Line(chess_xy[22][0], chess_xy[22][1], chess_xy[10][0],
						chess_xy[10][1], LINE_WIDTH, vbom),
				new Line(chess_xy[22][0], chess_xy[22][1], chess_xy[14][0],
						chess_xy[14][1], LINE_WIDTH, vbom), };

		line_y = new Line[] {
				new Line(chess_xy[0][0], chess_xy[0][1], chess_xy[20][0],
						chess_xy[20][1], LINE_WIDTH, vbom),
				new Line(chess_xy[1][0], chess_xy[1][1], chess_xy[21][0],
						chess_xy[21][1], LINE_WIDTH, vbom),
				new Line(chess_xy[2][0], chess_xy[2][1], chess_xy[22][0],
						chess_xy[22][1], LINE_WIDTH, vbom),
				new Line(chess_xy[3][0], chess_xy[3][1], chess_xy[23][0],
						chess_xy[23][1], LINE_WIDTH, vbom),
				new Line(chess_xy[4][0], chess_xy[4][1], chess_xy[24][0],
						chess_xy[24][1], LINE_WIDTH, vbom),

				new Line(chess_xy[25][0], chess_xy[25][1], chess_xy[27][0],
						chess_xy[27][1], LINE_WIDTH, vbom),
				new Line(chess_xy[25][0], chess_xy[25][1], chess_xy[14][0],
						chess_xy[14][1], LINE_WIDTH, vbom),
				new Line(chess_xy[25][0], chess_xy[25][1], chess_xy[28][0],
						chess_xy[28][1], LINE_WIDTH, vbom),
				new Line(chess_xy[27][0], chess_xy[27][1], chess_xy[14][0],
						chess_xy[14][1], LINE_WIDTH, vbom),
				new Line(chess_xy[27][0], chess_xy[27][1], chess_xy[28][0],
						chess_xy[28][1], LINE_WIDTH, vbom), };

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
		int index[] = new int[] { -50, -50 };

		// 创建白棋
		chess_white = new ChessWhite[] {
				new ChessWhite(chess_xy[0], 0, this, resourcesManager, vbom),
				new ChessWhite(chess_xy[1], 1, this, resourcesManager, vbom),
				new ChessWhite(chess_xy[2], 2, this, resourcesManager, vbom),
				new ChessWhite(chess_xy[3], 3, this, resourcesManager, vbom),
				new ChessWhite(chess_xy[4], 4, this, resourcesManager, vbom),
				new ChessWhite(index, -1, this, resourcesManager, vbom),
				new ChessWhite(index, -1, this, resourcesManager, vbom),
				new ChessWhite(index, -1, this, resourcesManager, vbom),
				new ChessWhite(index, -1, this, resourcesManager, vbom), };
		// 创建黑棋
		chess_black = new ChessBlack[] {
				new ChessBlack(chess_xy[20], 20, this, resourcesManager, vbom),
				new ChessBlack(chess_xy[21], 21, this, resourcesManager, vbom),
				new ChessBlack(chess_xy[22], 22, this, resourcesManager, vbom),
				new ChessBlack(chess_xy[23], 23, this, resourcesManager, vbom),
				new ChessBlack(chess_xy[24], 24, this, resourcesManager, vbom),
				new ChessBlack(index, -1, this, resourcesManager, vbom),
				new ChessBlack(index, -1, this, resourcesManager, vbom),
				new ChessBlack(index, -1, this, resourcesManager, vbom),
				new ChessBlack(index, -1, this, resourcesManager, vbom), };

		for (int i = 0; i < chess_black.length; i++) {
			attachChild(chess_black[i]);
			registerTouchArea(chess_black[i]);
		}
		for (int i = 0; i < chess_white.length; i++) {
			attachChild(chess_white[i]);
			registerTouchArea(chess_white[i]);
		}

		// 二维数组模拟TWO棋盘
		chessboard_two = new int[5][7];
		cerrChessboard_two();

		// 创建chessShow
		sprite = new AnimatedSprite[18];
		for (int i = 0; i < 18; i++) {
			sprite[i] = new AnimatedSprite(-100, -100,
					ResourcesManager.getInstance().game_chessShow_region, vbom);
		}
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
		chessboard_two[0][4] = ChessManager.CHESS_WHITE;
		chessboard_two[0][5] = ChessManager.CHESS_NO;
		chessboard_two[0][6] = ChessManager.CHESS_NO;

		chessboard_two[1][0] = ChessManager.CHESS_EMPTY;
		chessboard_two[1][1] = ChessManager.CHESS_EMPTY;
		chessboard_two[1][2] = ChessManager.CHESS_EMPTY;
		chessboard_two[1][3] = ChessManager.CHESS_EMPTY;
		chessboard_two[1][4] = ChessManager.CHESS_EMPTY;
		chessboard_two[1][5] = ChessManager.CHESS_EMPTY;
		chessboard_two[1][6] = ChessManager.CHESS_NO;

		chessboard_two[2][0] = ChessManager.CHESS_EMPTY;
		chessboard_two[2][1] = ChessManager.CHESS_EMPTY;
		chessboard_two[2][2] = ChessManager.CHESS_EMPTY;
		chessboard_two[2][3] = ChessManager.CHESS_EMPTY;
		chessboard_two[2][4] = ChessManager.CHESS_EMPTY;
		chessboard_two[2][5] = ChessManager.CHESS_EMPTY;
		chessboard_two[2][6] = ChessManager.CHESS_EMPTY;

		chessboard_two[3][0] = ChessManager.CHESS_EMPTY;
		chessboard_two[3][1] = ChessManager.CHESS_EMPTY;
		chessboard_two[3][2] = ChessManager.CHESS_EMPTY;
		chessboard_two[3][3] = ChessManager.CHESS_EMPTY;
		chessboard_two[3][4] = ChessManager.CHESS_EMPTY;
		chessboard_two[3][5] = ChessManager.CHESS_EMPTY;
		chessboard_two[3][6] = ChessManager.CHESS_NO;

		chessboard_two[4][0] = ChessManager.CHESS_BLACK;
		chessboard_two[4][1] = ChessManager.CHESS_BLACK;
		chessboard_two[4][2] = ChessManager.CHESS_BLACK;
		chessboard_two[4][3] = ChessManager.CHESS_BLACK;
		chessboard_two[4][4] = ChessManager.CHESS_BLACK;
		chessboard_two[4][5] = ChessManager.CHESS_NO;
		chessboard_two[4][6] = ChessManager.CHESS_NO;
	}

	public void cerrChessBorad() {
		int index[] = new int[] { -50, -50 };
		for (int i = 0; i < chess_white.length - 4; i++) {
			chess_white[i].setPosition(chess_xy[i][0], chess_xy[i][1]);
			chess_white[i].ID = i;
		}
		for (int i = chess_white.length - 4; i < chess_white.length; i++) {
			chess_white[i].setPosition(index[0], index[1]);
			chess_white[i].ID = -1;
		}
		for (int i = 0; i < chess_black.length - 4; i++) {
			chess_black[i]
					.setPosition(chess_xy[i + 20][0], chess_xy[i + 20][1]);
			chess_black[i].ID = i + 20;
		}
		for (int i = chess_black.length - 4; i < chess_black.length; i++) {
			chess_black[i].setPosition(index[0], index[1]);
			chess_black[i].ID = -1;
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
		} else if (pX > x5 - 15 && pX < x5 + 15) {
			x_one = x5;
		} else if (pX > x6 - 15 && pX < x6 + 15) {
			x_one = x6;
		} else if (pX > x7 - 15 && pX < x7 + 15) {
			x_one = x7;
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
		if (pY > y5 - 15 && pY < y5 + 15) {
			y_one = y5;
		}

		for (int i = 0; i < chess_xy.length; i++) {
			if (x_one == chess_xy[i][0] && y_one == chess_xy[i][1])
				return i;
		}
		return -1;
	}

	private boolean judge(int currentXY, int id) {
		if ((chess_xy[currentXY][2] == chess_xy[id][2])
				|| (chess_xy[currentXY][3] == chess_xy[id][3])
				|| (id % 2 == 0 && (Math
						.abs((chess_xy[id][2] - chess_xy[currentXY][2])) == Math
						.abs((chess_xy[id][3] - chess_xy[currentXY][3]))))) {
			if (id == 25 || id == 27) {
				if (currentXY != 14)
					return false;
			}
			if (currentXY == 25 || currentXY == 27) {
				if (id != 14)
					return false;
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {

			int currentXY = isPosition(pSceneTouchEvent.getX(),// 得到点击的棋盘的位置
					pSceneTouchEvent.getY());

			int id = 0;// 棋子原来的位置
			if (ChessManager.getInstance().currentChessIsUp == ChessManager.YES_CHESS_UP)
				id = ChessManager.getInstance().getCurrentChess().ID;

			if (moveChess
					&& result == ChessManager.FLAT// 如果结果是平局，有棋子点起来，点击的位置在棋盘上，点击的位置可以放置棋子,移动的位置差1
					&& ChessManager.getInstance().currentChessIsUp == ChessManager.YES_CHESS_UP
					&& currentXY != -1
					&& chessboard_two[chess_xy[currentXY][2]][chess_xy[currentXY][3]] == ChessManager.CHESS_EMPTY
					&& judge(currentXY, id)) {

				this.nowjustis(currentXY, id, ChessManager.USA);

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
		this.movechess(id, currentXY, ChessManager.USA);
		if (SceneManager.GameType == SceneManager.NET
				&& state == ChessManager.USA)
			CommunicateManager.getCommunicate().send(currentXY, id);
		int eatChess[] = ChessJNI.getInstance().nalter_nchess(chessboard_two,
				chess_xy[currentXY][2], chess_xy[currentXY][3]);

		ChessManager.getInstance().SeatChess.push(eatChess);// 记录被吃掉的棋子
		for (int i = 0; eatChess[i] != -1; i += 2) {
			int eatTwoChess[] = new int[] { eatChess[i], eatChess[i + 1] };
			this.getResult(eatTwoChess, ChessManager.USA);
		}

		this.resultNine(ChessManager.USA);

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

			int[] eat = null;
			if (ChessManager.getInstance().SeatChess.length > 0)
				eat = ChessManager.getInstance().SeatChess.pop();
			if (eat != null && eat[0] != -1) {
				for (int i = 0; eat[i] != -1; i += 2) {
					int a[] = new int[] { eat[i], eat[i + 1] };

					this.getResult(a, ChessManager.USA);
				}
			}
		}
		this.upText();
	}

	private void getResult(int eatChess[], int pUsa) {

		// 棋子被吃掉
		if (eatChess[0] != -1) {
			Chess chess = null;
			// 在虚拟棋盘上拿掉
			int color = chessboard_two[eatChess[0]][eatChess[1]];
			chessboard_two[eatChess[0]][eatChess[1]] = ChessManager.CHESS_EMPTY;
			final int eatId = getID(eatChess[0], eatChess[1]);

			chess = this.getChess(eatId, color);
			if (chess != null) {
				chess.dieChess(pUsa);
				chess = null;

				if (color == ChessManager.CHESS_WHITE) {
					for (int i = 0; i < chess_black.length; i++) {
						if (chess_black[i].ID == -1)
							chess = chess_black[i];
					}
					chessboard_two[eatChess[0]][eatChess[1]] = ChessManager.CHESS_BLACK;
				} else if (color == ChessManager.CHESS_BLACK) {
					for (int i = 0; i < chess_white.length; i++) {
						if (chess_white[i].ID == -1)
							chess = chess_white[i];
					}
					chessboard_two[eatChess[0]][eatChess[1]] = ChessManager.CHESS_WHITE;
				}
				if (chess != null) {
					chess.ID = eatId;
					final Chess chesss = chess;
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
									chesss.setPosition(chess_xy[eatId][0],
											chess_xy[eatId][1]);
								}
							}));
				}

			}

		}
	}

	public void resultNine(int pUsa) {

		// 吃掉后判断胜负
		result = ChessJNI.getInstance().njudge_fvictory_nine(chessboard_two);
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
						message.obj = "您输了！";
					else
						message.obj = "您胜了！";
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
						message.obj = "您胜了！";
					else
						message.obj = "您输了！";
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

	private int getID(int x, int y) {

		for (int i = 0; i < chess_xy.length; i++) {
			if (x == chess_xy[i][2] && y == chess_xy[i][3])
				return i;
		}

		return -1;
	}

	@Override
	public int getGameType() {
		return SceneManager.NINE;
	}

	@Override
	public void dialoging(int n) {
		this.cerrChessBorad();

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
		i_sprite = 0;// sprite计数

		int chess_x = chess_xy[id][2];
		int chess_y = chess_xy[id][3];

		int i_x_right = chess_y + 1;
		int bianjie = 0;
		if (chess_x == 2) {
			bianjie = 7;
		} else {
			bianjie = 5;
		}

		while (i_x_right < bianjie) {
			if (chessboard_two[chess_x][i_x_right] != ChessManager.CHESS_EMPTY)
				break;
			int linid = this.getID(chess_x, i_x_right);
			sprite[i_sprite]
					.setPosition(chess_xy[linid][0], chess_xy[linid][1]);
			i_x_right++;
			i_sprite++;
		}
		int i_x_left = chess_y - 1;
		while (i_x_left >= 0) {
			if (chessboard_two[chess_x][i_x_left] != ChessManager.CHESS_EMPTY)
				break;
			int linid = this.getID(chess_x, i_x_left);
			sprite[i_sprite]
					.setPosition(chess_xy[linid][0], chess_xy[linid][1]);
			i_x_left--;
			i_sprite++;
		}
		int i_y_up = chess_x - 1;
		while (i_y_up >= 0) {
			if (chessboard_two[i_y_up][chess_y] != ChessManager.CHESS_EMPTY)
				break;
			int linid = this.getID(i_y_up, chess_y);
			sprite[i_sprite]
					.setPosition(chess_xy[linid][0], chess_xy[linid][1]);
			i_y_up--;
			i_sprite++;
		}
		int i_y_down = chess_x + 1;
		while (i_y_down < 5) {
			if (chessboard_two[i_y_down][chess_y] != ChessManager.CHESS_EMPTY)
				break;
			int linid = this.getID(i_y_down, chess_y);
			sprite[i_sprite]
					.setPosition(chess_xy[linid][0], chess_xy[linid][1]);
			i_y_down++;
			i_sprite++;
		}
		if ((chess_x + chess_y) % 2 == 0) {
			i_x_left = chess_y - 1;
			i_y_down = chess_x + 1;
			while (i_x_left >= 0 && i_y_down < 5) {
				if (chessboard_two[i_y_down][i_x_left] != ChessManager.CHESS_EMPTY)
					break;
				int linid = this.getID(i_y_down, i_x_left);
				sprite[i_sprite].setPosition(chess_xy[linid][0],
						chess_xy[linid][1]);
				i_y_down++;
				i_x_left--;
				i_sprite++;
			}

			i_x_left = chess_y - 1;
			i_y_up = chess_x - 1;
			while (i_x_left >= 0 && i_y_up >= 0) {
				if (chessboard_two[i_y_up][i_x_left] != ChessManager.CHESS_EMPTY)
					break;
				int linid = this.getID(i_y_up, i_x_left);
				sprite[i_sprite].setPosition(chess_xy[linid][0],
						chess_xy[linid][1]);
				i_y_up--;
				i_x_left--;
				i_sprite++;
			}

			if (id == 4 || id == 24) {
				bianjie = 5;
			} else {
				bianjie = 7;
			}

			i_x_right = chess_y + 1;
			i_y_up = chess_x - 1;
			while (i_x_right < bianjie && i_y_up >= 0) {
				if (chessboard_two[i_y_up][i_x_right] != ChessManager.CHESS_EMPTY)
					break;
				int linid = this.getID(i_y_up, i_x_right);
				sprite[i_sprite].setPosition(chess_xy[linid][0],
						chess_xy[linid][1]);
				i_y_up--;
				i_x_right++;
				i_sprite++;
			}

			i_x_right = chess_y + 1;
			i_y_down = chess_x + 1;
			while (i_x_right < bianjie && i_y_down < 5) {
				if (chessboard_two[i_y_down][i_x_right] != ChessManager.CHESS_EMPTY)
					break;
				int linid = this.getID(i_y_down, i_x_right);
				sprite[i_sprite].setPosition(chess_xy[linid][0],
						chess_xy[linid][1]);
				i_y_down++;
				i_x_right++;
				i_sprite++;
			}
		}

	}

	@Override
	public void notShowNextChess() {
		for (int i = 0; i < i_sprite; i++) {
			sprite[i].setPosition(-100, -100);
		}

	}

}
