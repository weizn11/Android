package com.example.scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.adt.align.HorizontalAlign;
import android.opengl.GLES20;
import android.os.Message;
import com.example.base.BaseScene;
import com.example.chess.Chess;
import com.example.manager.ChessManager;
import com.example.manager.CommunicateManager;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

public abstract class GameScene extends BaseScene implements
		IOnMenuItemClickListener {

	public static boolean moveChess = true;
	public static int myChess = ChessManager.CHESS_BLACK;
	public static boolean exitChess = false;

	private final int GAME_Xinju = 0;
	private final int GAME_Huiqi = 1;
	private final int GAME_Fanhui = 2;
	private final int GAME_Tuichu = 3;
	private final int GAME_Ti = 4;

	private HUD gameHUD;
	public Text scoreText;
	public AnimatedSprite nowMove;

	protected boolean show = false;
	protected int result = ChessManager.FLAT;
	private MenuScene gameChildMenuScene;
	private boolean littel = true;

	public GameScene() {
		super();
	}

	public void createChildScene() {
		createCheckerboard();
		createChess();
	};

	// 抽象方法，创建棋盘,初始化棋子,新局
	public abstract void createCheckerboard();

	public abstract void createChess();

	public abstract int getGameType();

	public abstract void cerrChessBorad();

	public abstract void nowjustis(int currentXY, int id, int state);

	public abstract Chess getChess(int id, int color);

	public abstract void movechess(int id, int currentXY, int p);

	public abstract void showNextChess(int id);

	public abstract void notShowNextChess();

	@Override
	public void createScene() {
		createBackground();
		createChildScene(); // 创建子场景
		createMenuChildScene();
		createHUD();
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadLevelMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT / 2);
	}

	private void createBackground() {
		setBackground(new SpriteBackground(new Sprite(
				activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT / 2,
				resourcesManager.game_gameBackground_region, vbom)));
		sky();
	}

	public void sky() {
		CircleOutlineParticleEmitter particleEmitter = new CircleOutlineParticleEmitter(
				activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT / 2, 80);
		SpriteParticleSystem particleSystem = new SpriteParticleSystem(
				particleEmitter, 10, 30, 50,
				ResourcesManager.getInstance().game_point_region,
				ResourcesManager.getInstance().vbom);

		particleSystem
				.addParticleInitializer(new ColorParticleInitializer<Sprite>(1,
						0, 1));
		particleSystem
				.addParticleInitializer(new AlphaParticleInitializer<Sprite>(0));// 透明度
		particleSystem
				.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(
						GLES20.GL_SRC_ALPHA, GLES20.GL_ONE));
		particleSystem
				.addParticleInitializer(new VelocityParticleInitializer<Sprite>(
						-250, 260, -500, 500));
		particleSystem
				.addParticleInitializer(new RotationParticleInitializer<Sprite>(
						0.0f, 360.0f));
		particleSystem
				.addParticleInitializer(new ExpireParticleInitializer<Sprite>(6));// 销毁粒子

		particleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0,
				1, 0.5f, 0.5f));// 调节粗细
		particleSystem.addParticleModifier(new ColorParticleModifier<Sprite>(0,
				3, 1, 1, 0, 1, 1, 1));
		particleSystem.addParticleModifier(new ColorParticleModifier<Sprite>(3,
				6, 1, 0, 1, 0, 1, 1));
		particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0,
				2, 0, 1));
		attachChild(particleSystem);

	}

	private void createHUD() {
		gameHUD = new HUD();
		scoreText = new Text(0,
				activity.CAMERA_HEIGHT - activity.setPointY(60),
				resourcesManager.font, "NOW:", new TextOptions(
						HorizontalAlign.LEFT), vbom);
		nowMove = new AnimatedSprite(0 + scoreText.getWidth()
				+ activity.setPointX(10), activity.CAMERA_HEIGHT - 30,
				ResourcesManager.getInstance().game_nowMove_region, vbom);
		gameHUD.attachChild(nowMove);
		scoreText.setAnchorCenter(0, 0);
		scoreText.setText(setText());
		gameHUD.attachChild(scoreText);

		camera.setHUD(gameHUD);
	}

	public String setText() {
		return "NOW：";
	}

	private void createMenuChildScene() {
		gameChildMenuScene = new MenuScene(camera);
		gameChildMenuScene.setPosition(0, 0);

		final IMenuItem xinjuMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(GAME_Xinju,
						resourcesManager.game_xinju_region, vbom), 1.2f, 1);
		final IMenuItem huiqiMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(GAME_Huiqi,
						resourcesManager.game_huiqi_region, vbom), 1.2f, 1);
		final IMenuItem fanhuiMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(GAME_Fanhui,
						resourcesManager.game_fanhui_region, vbom), 1.2f, 1);
		final IMenuItem tuichuMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(GAME_Tuichu,
						resourcesManager.game_tuichu_region, vbom), 1.2f, 1);
		final IMenuItem tiMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(GAME_Ti, resourcesManager.game_ti_region,
						vbom), 1.2f, 1);

		gameChildMenuScene.addMenuItem(xinjuMenultem);
		gameChildMenuScene.addMenuItem(huiqiMenultem);
		gameChildMenuScene.addMenuItem(fanhuiMenultem);
		gameChildMenuScene.addMenuItem(tuichuMenultem);
		if (getGameType() == SceneManager.NINE)
			gameChildMenuScene.addMenuItem(tiMenultem);

		gameChildMenuScene.buildAnimations();
		gameChildMenuScene.setBackgroundEnabled(false);

		if (getGameType() != SceneManager.NINE) {
			xinjuMenultem.setPosition(xinjuMenultem.getX() - 195,
					xinjuMenultem.getY() - (activity.CAMERA_HEIGHT / 2) + 80);
			huiqiMenultem.setPosition(huiqiMenultem.getX() - 65,
					xinjuMenultem.getY());
			fanhuiMenultem.setPosition(fanhuiMenultem.getX() + 65,
					xinjuMenultem.getY());
			tuichuMenultem.setPosition(tuichuMenultem.getX() + 195,
					xinjuMenultem.getY());
		} else {

			xinjuMenultem.setPosition(xinjuMenultem.getX() - 200,
					xinjuMenultem.getY() - (activity.CAMERA_HEIGHT / 2) + 80);
			huiqiMenultem.setPosition(huiqiMenultem.getX() - 100,
					xinjuMenultem.getY());
			fanhuiMenultem.setPosition(fanhuiMenultem.getX(),
					xinjuMenultem.getY());
			tuichuMenultem.setPosition(tuichuMenultem.getX() + 100,
					xinjuMenultem.getY());
			tiMenultem.setPosition(tiMenultem.getX() + 200,
					xinjuMenultem.getY());
		}
		gameChildMenuScene.setOnMenuItemClickListener(this);

		setChildScene(gameChildMenuScene);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		resourcesManager.button_sound.play();
		switch (pMenuItem.getID()) {
		case GAME_Xinju:
			if (SceneManager.GameType != SceneManager.NET) {
				this.cerrChessBorad();
			} else {
				Message message = new Message();
				message.what = 0x8282;
				message.obj = "请返回游戏重新匹配！";
				activity.mHandler.sendMessage(message);
			}
			return true;
		case GAME_Huiqi:
			if (SceneManager.GameType != SceneManager.NET) {
				this.downChess();
				this.moveOldNew();
			} else {
				Message message = new Message();
				message.what = 0x8282;
				message.obj = "不能悔棋！";
				activity.mHandler.sendMessage(message);
			}
			return true;
		case GAME_Fanhui:
			if (SceneManager.GameType == SceneManager.NET) {
				CommunicateManager.getCommunicate().send(-100, -100);
				GameScene.exitChess = true;
			}
			this.onBackKeyPressed();
			return true;
		case GAME_Tuichu:
			if (SceneManager.GameType == SceneManager.NET) {
				CommunicateManager.getCommunicate().send(-200, -200);
			}
			Message msg = new Message();
			msg.what = 0x1133;
			activity.mHandler.sendMessage(msg);
			return true;

		case GAME_Ti:
			activity.camera.setBoundsEnabled(true);
			if (littel) {
				activity.camera.setCenter(activity.camera.getCenterX() + 220,
						activity.camera.getCenterY());
				littel = false;
			} else {
				activity.camera.setCenter(activity.CAMERA_WIDTH / 2,
						activity.camera.getCenterY());
				littel = true;
			}
			activity.camera.setBoundsEnabled(false);
			return true;
		default:
			return false;
		}
	}

	public void upText() {
		String s = "NOW：";
		switch (ChessManager.getInstance().getNextCHESS_COLOR()) {
		case ChessManager.CHESS_BLACK:
			nowMove.animate(new long[] { 100, 100 }, 2, 3, false);
			break;
		case ChessManager.CHESS_WHITE:
			nowMove.animate(new long[] { 100, 100 }, 0, 1, false);
			break;
		}
		scoreText.setText(s);
	}

	private void downChess() {
		Chess chess = ChessManager.getInstance().getCurrentChess();
		if (chess != null && chess.currentState == ChessManager.UP) {
			chess.animate(new long[] { 100, 100 }, 2, 3, false);
			chess.setCurrentState();
			ChessManager.getInstance().currentChessIsUp = chess.currentState;
		}
	}

	public void moveOldNew() {
		if (SceneManager.GameType != SceneManager.ONLINE) {
			if (ChessManager.getInstance().SnewChessId.length > 0) {
				ChessManager.getInstance()
						.setCurrentChess(
								getChess(ChessManager.getInstance().SnewChessId
										.GetTop(), -1));
				this.movechess(ChessManager.getInstance().SnewChessId.pop(),
						ChessManager.getInstance().SoldChessId.pop(),
						ChessManager.HUIQI);
			} else {
				Message msg = new Message();
				msg.what = 0x1144;
				msg.obj = "无法悔棋！";
				activity.mHandler.sendMessage(msg);
			}
		} else {
			if (ChessManager.getInstance().SnewChessId.length > 1) {
				ChessManager.getInstance()
						.setCurrentChess(
								getChess(ChessManager.getInstance().SnewChessId
										.GetTop(), -1));
				this.movechess(ChessManager.getInstance().SnewChessId.pop(),
						ChessManager.getInstance().SoldChessId.pop(),
						ChessManager.HUIQI);
				ChessManager.getInstance()
						.setCurrentChess(
								getChess(ChessManager.getInstance().SnewChessId
										.GetTop(), -1));
				this.movechess(ChessManager.getInstance().SnewChessId.pop(),
						ChessManager.getInstance().SoldChessId.pop(),
						ChessManager.HUIQI);
			} else {
				Message msg = new Message();
				msg.what = 0x1144;
				msg.obj = "无法悔棋！";
				activity.mHandler.sendMessage(msg);
			}

		}
	}
}
