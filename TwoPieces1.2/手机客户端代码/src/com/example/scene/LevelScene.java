package com.example.scene;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import android.os.Message;
import com.example.base.BaseScene;
import com.example.manager.CommunicateManager;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;
import com.example.thread.Communicate;

public class LevelScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene levelMenuChildScene;
	private final int LEVEL_TWO = 0;
	private final int LEVEL_FOUR = 1;
	private final int LEVEL_NINE = 2;
	private final int LEVEL_BACK = 3;

	private float WI_1;
	private float WI_2;

	@Override
	public void createScene() {
		createLevelBackground();
		createLevelMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LEVEL;
	}

	@Override
	public void disposeScene() {
		camera.setCenter(activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT / 2);
	}

	private void createLevelBackground() {
		setBackground(new SpriteBackground(new Sprite(
				activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT / 2,
				resourcesManager.level_background_region, vbom)));

	}

	private void createLevelMenuChildScene() {
		levelMenuChildScene = new MenuScene(camera);
		levelMenuChildScene.setPosition(0, 0);

		final IMenuItem twoMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(LEVEL_TWO,
						resourcesManager.level_two_region, vbom), 1.2f, 1);
		final IMenuItem fourMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(LEVEL_FOUR,
						resourcesManager.level_four_region, vbom), 1.2f, 1);
		final IMenuItem nineMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(LEVEL_NINE,
						resourcesManager.level_nine_region, vbom), 1.2f, 1);
		final IMenuItem backMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(LEVEL_BACK,
						resourcesManager.level_back_region, vbom), 1.2f, 1);

		levelMenuChildScene.addMenuItem(twoMenultem);
		levelMenuChildScene.addMenuItem(fourMenultem);
		levelMenuChildScene.addMenuItem(nineMenultem);
		levelMenuChildScene.addMenuItem(backMenultem);

		levelMenuChildScene.buildAnimations();
		levelMenuChildScene.setBackgroundEnabled(false);

		WI_1 = activity.setPointY(150);
		WI_2 = activity.setPointY(50);

		twoMenultem.setPosition(twoMenultem.getX(), twoMenultem.getY() + WI_1);
		fourMenultem.setPosition(fourMenultem.getX(), fourMenultem.getY()
				+ WI_2);
		nineMenultem.setPosition(nineMenultem.getX(), nineMenultem.getY()
				- WI_2);
		backMenultem.setPosition(nineMenultem.getX(), nineMenultem.getY()
				- WI_1);

		levelMenuChildScene.setOnMenuItemClickListener(this);
		setChildScene(levelMenuChildScene);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		resourcesManager.button_sound.play();
		switch (pMenuItem.getID()) {
		case LEVEL_TWO:
			if (SceneManager.GameType != SceneManager.NET)
				SceneManager.getInstance().loadGameScene(LEVEL_TWO, engine);
			else {
				CommunicateManager.setCommunicate(new Communicate(2, 2,
						ResourcesManager.getInstance().activity));
			}
			return true;
		case LEVEL_FOUR:
			if (SceneManager.GameType != SceneManager.NET)
				SceneManager.getInstance().loadGameScene(LEVEL_FOUR, engine);
			else {
				CommunicateManager.setCommunicate(new Communicate(2, 4,
						ResourcesManager.getInstance().activity));
			}
			return true;
		case LEVEL_NINE:
			if (SceneManager.GameType == SceneManager.PLAYER) {
				SceneManager.getInstance().loadGameScene(LEVEL_NINE, engine);
			} else if (SceneManager.GameType == SceneManager.ONLINE) {
				Message msg = new Message();
				msg.what = 0x1122;
				msg.obj = "很抱歉亲，九块子儿的AI算法难度太大，我们的程序猿正在努力中！！！";
				activity.mHandler.sendMessage(msg);
			} else if (SceneManager.GameType == SceneManager.NET) {
				CommunicateManager.setCommunicate(new Communicate(2, 5,
						ResourcesManager.getInstance().activity));
			}
			return true;
		case LEVEL_BACK:
			onBackKeyPressed();
			return true;
		default:
			return false;
		}
	}

	@Override
	public void dialoging(int n) {
		switch (n) {
		case 2:
			SceneManager.getInstance().loadGameScene(LEVEL_TWO, engine);
			break;
		case 4:
			SceneManager.getInstance().loadGameScene(LEVEL_FOUR, engine);
			break;
		case 5:
			SceneManager.getInstance().loadGameScene(LEVEL_NINE, engine);
			break;
		default:
			break;
		}
	}

}
