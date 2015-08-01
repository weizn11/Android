package com.example.chess;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.example.manager.ChessManager;
import com.example.scene.GameScene;

public class Chess extends AnimatedSprite {

	public int ID = -1;
	public int currentState = ChessManager.DOWN;
	public SpriteParticleSystem particleSystem;
	public CircleOutlineParticleEmitter particleEmitter;

	private int chess_color = ChessManager.CHESS_NO;
	private GameScene gameScene;

	public Chess(float pX, float pY, int pChess_color, int iD, GameScene game,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		this.chess_color = pChess_color;
		this.ID = iD;
		this.gameScene = game;
	}

	public void currCurrentState() {
		currentState = ChessManager.DOWN;
	}

	public void setCurrentState() {
		if (currentState == ChessManager.DOWN)
			currentState = ChessManager.UP;
		else
			currentState = ChessManager.DOWN;
	}

	public void upChess() {
		this.animate(new long[] { 100, 100 }, 0, 1, false);
		gameScene.showNextChess(ID);
	}

	public void downChess() {
		this.animate(new long[] { 100, 100 }, 2, 3, false);
		gameScene.notShowNextChess();
	}

	// 棋子被吃掉
	public void dieChess(int pUsa) {
		ID = -1;
		currentState = ChessManager.DOWN;
		float mf = 0;
		if (pUsa == ChessManager.AI)
			mf = 1f;
		else
			mf = 0.2f;
		this.registerUpdateHandler(new TimerHandler(mf, new ITimerCallback() {

			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				setPosition(-50, -50);
			}
		}));
	}

	public int getChess_color() {
		return chess_color;
	}

	public void setChess_color(int chess_color) {
		this.chess_color = chess_color;
	}

	public void pathChess(int sx, int sy, int px, int py, int pUsa) {
		Path path = new Path(2).to(sx, sy).to(px, py);
		if (pUsa == ChessManager.AI)
			this.registerEntityModifier(new SequenceEntityModifier(
					new DelayModifier(0.8f), new PathModifier(0.2f, path)));
		else
			this.registerEntityModifier(new PathModifier(0.2f, path));
		downChess();
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		if (gameScene.moveChess
				&& this.getChess_color() == ChessManager.getInstance()
						.getNextCHESS_COLOR()) {// &&
												// ChessManager.getInstance().currentChessIsUp
												// !=
												// ChessManager.YES_CHESS_UP){
			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
				if (ChessManager.getInstance().getCurrentChess() != null
						&& ChessManager.getInstance().currentChessIsUp == ChessManager.YES_CHESS_UP
						&& currentState == ChessManager.DOWN) {

					if (ChessManager.getInstance().getCurrentChess().currentState == ChessManager.UP) {// 管理器中的棋子落下
						ChessManager.getInstance().getCurrentChess()
								.downChess();
					}
					ChessManager.getInstance().getCurrentChess()// 管理器中的棋子状态落下
							.setCurrentState();
					ChessManager.getInstance().setCurrentChess(this);// 管理器中的棋子改为当前棋子
					setCurrentState();// 改变当前棋子的状态
					ChessManager.getInstance().currentChessIsUp = currentState;// ChessManager.YES_CHESS_UP;//
																				// 管理器中记录棋子的状态
					if (currentState == ChessManager.UP) {// 升起
						this.upChess();

					} else {
						this.downChess();
					}
				} else {
					ChessManager.getInstance().setCurrentChess(this);
					setCurrentState();// 改变当前棋子的状态
					ChessManager.getInstance().currentChessIsUp = currentState;// ChessManager.YES_CHESS_UP;//
																				// 管理器中记录棋子的状态
					if (currentState == ChessManager.UP) {
						this.upChess();
					} else {
						this.downChess();
					}

				}
			}
		}
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
				pTouchAreaLocalY);
	}

}
