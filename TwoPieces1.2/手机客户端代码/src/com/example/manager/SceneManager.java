package com.example.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.example.base.BaseScene;
import com.example.childScene.FourGameScene;
import com.example.childScene.NineGameScene;
import com.example.childScene.TwoGameScene;
import com.example.scene.GameScene;
import com.example.scene.LevelScene;
import com.example.scene.MainMenuScene;
import com.example.scene.SplashScene;

/**
 * 提供管理场景的方法，切换，跟踪当前场景 包含了一类枚举，对场景类型的描述
 * 
 * @author 张国栋
 * 
 */

public class SceneManager {

	public static final int PLAYER = 0;
	public static final int ONLINE = 1;
	public static final int NET = 2;

	public static final int TWO = 0;
	public static final int FOUR = 1;
	public static final int NINE = 2;

	public int SHOWLEVEL = 0;

	public static int GameType;

	private BaseScene splashScene;
	private BaseScene menuScene;
	private BaseScene gameScene;
	private BaseScene levelScene;
	private GameScene childGame;

	private static final SceneManager INSTANCE = new SceneManager();

	private SceneType currentSceneType = SceneType.SCENE_SPLASH;

	private BaseScene currentScene;
	private GameScene currentGameScene;

	private Engine engine = ResourcesManager.getInstance().engine;

	public enum SceneType {
		SCENE_SPLASH, SCENE_MENU, SCENE_GAME, SCENE_LEVEL,
	}

	public void setScene(BaseScene scene) {
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}

	public void setGameScene(GameScene gameScene) {
		this.currentGameScene = gameScene;
	}

	public GameScene getGameScene() {
		return this.currentGameScene;
	}

	public void delGameScene() {
		this.currentGameScene = null;
	}

	public void setScene(SceneType sceneType) {
		switch (sceneType) {
		case SCENE_MENU:
			setScene(menuScene);
			break;
		case SCENE_GAME:
			setScene(gameScene);
			break;
		case SCENE_SPLASH:
			setScene(splashScene);
			break;
		case SCENE_LEVEL:
			setScene(levelScene);
			break;
		default:
			break;
		}
	}

	public static SceneManager getInstance() {
		return INSTANCE;
	}

	public SceneType getCurrentSceneType() {
		return currentSceneType;
	}

	public BaseScene getCurrentScene() {
		return currentScene;
	}

	// 创建SPLASH场景
	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		ResourcesManager.getInstance().loadSplashScreen();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}

	// 销毁splash场景
	private void disposeSplashScene() {
		ResourcesManager.getInstance().unloadSplashScreen();
		splashScene.dispose();
		splashScene = null;
		currentScene = null;
	}

	// 创建菜单场景
	public void createMeneScene() {
		ResourcesManager.getInstance().loadMenuResoures();
		menuScene = new MainMenuScene();
		disposeSplashScene();
		SceneManager.getInstance().setScene(menuScene);
	}

	// 卸载菜单资源 加载关卡资源
	public void loadLevelScene(final Engine mEngine) {
		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {

					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						ResourcesManager.getInstance().loadLevelResources();
						levelScene = new LevelScene();
						ResourcesManager.getInstance().unloadMenuTextures();
						SceneManager.getInstance().setScene(levelScene);
					}
				}));
	}

	// 卸载关卡资源 加载菜单资源
	public void loadMenuScene(final Engine mEngine) {
		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {

					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						ResourcesManager.getInstance().loadMenuTextures();
						levelScene.disposeScene();
						ResourcesManager.getInstance()
								.unloadLevelMenuTextures();
						SceneManager.getInstance().setScene(menuScene);
					}
				}));
	}

	// 卸载关卡资源 加载游戏资源
	public void loadGameScene(final int level, final Engine mEngine) {
		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {

					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						ResourcesManager.getInstance().loadGameResources();
						switch (level) {
						case 0:
							childGame = new TwoGameScene();
							break;
						case 1:
							childGame = new FourGameScene();
							break;
						case 2:
							childGame = new NineGameScene();
							break;
						default:
							break;
						}
						gameScene = childGame;
						ResourcesManager.getInstance()
								.unloadLevelMenuTextures();
						SceneManager.getInstance().setScene(gameScene);
						SceneManager.getInstance().setGameScene(childGame);

					}
				}));
	}

	// 卸载游戏资源 加载关卡资源
	public void loadLevelMenuScene(final Engine mEngine) {
		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {

					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						ResourcesManager.getInstance().loadLevelMenuTextures();
						gameScene.disposeScene();
						ResourcesManager.getInstance().unloadGameTextures();
						SceneManager.getInstance().setScene(levelScene);
						SceneManager.getInstance().delGameScene();
					}
				}));
	}

}
