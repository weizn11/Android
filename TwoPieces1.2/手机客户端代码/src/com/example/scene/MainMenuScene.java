package com.example.scene;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.color.Color;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import com.example.activity.GameActivity;
import com.example.activity.MockDialog;
import com.example.base.BaseScene;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements
		IOnMenuItemClickListener {

	private MenuScene menuChildScene;
	private final int MENU_PLAYER = 0;
	private final int MENU_ON_LINE = 1;
	private final int MENU_NET = 2;
	private final int MENU_SOUND = 3;
	private final int MENU_WEB = 4;
	private final int MENU_HELP = 5;
	private final int MENU_ABOUT = 6;
	private final int MENU_EXIT = 7;
	private int mType;

	private IMenuItem soundMenultem;
	@SuppressWarnings("unused")
	private RepeatingSpriteBackground menu_background_region;

	private Line line;
	private float x1;
	private float x2;
	private float y1;
	private float y2;
	private float WI;
	private float WI_MENU;
	private boolean sound = true;
	@SuppressWarnings("unused")
	private Intent intent;

	@Override
	public void createScene() {
		createMenuBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		showExitDialog();
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {

	}

	private void createMenuBackground() {
		setBackground(new SpriteBackground(new Sprite(
				activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT / 2,
				resourcesManager.menu_background_region, vbom)));
	}

	private void createMenuChildScene() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);

		final IMenuItem playerMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_PLAYER,
						resourcesManager.menu_player_region, vbom), 1.2f, 1);
		final IMenuItem onLineMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_ON_LINE,
						resourcesManager.menu_players_region, vbom), 1.2f, 1);
		final IMenuItem playNetMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_NET,
						resourcesManager.menu_play_net_region, vbom), 1.2f, 1);
		final IMenuItem exitMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_EXIT,
						resourcesManager.menu_exit_region, vbom), 1.2f, 1);

		soundMenultem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_SOUND, resourcesManager.menu_sound_region, vbom), 1.2f, 1);
		final IMenuItem webMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_WEB, resourcesManager.menu_web_region,
						vbom), 1.2f, 1);
		final IMenuItem helpMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_HELP,
						resourcesManager.menu_help_region, vbom), 1.2f, 1);
		final IMenuItem aboutMenultem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_ABOUT,
						resourcesManager.menu_about_region, vbom), 1.2f, 1);

		menuChildScene.addMenuItem(playerMenultem);
		menuChildScene.addMenuItem(onLineMenultem);
		menuChildScene.addMenuItem(playNetMenultem);
		menuChildScene.addMenuItem(exitMenultem);
		menuChildScene.addMenuItem(soundMenultem);
		menuChildScene.addMenuItem(webMenultem);
		menuChildScene.addMenuItem(helpMenultem);
		menuChildScene.addMenuItem(aboutMenultem);

		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		playerMenultem.setPosition(playerMenultem.getX(), playerMenultem.getY()
				+ activity.setPointY(120));
		onLineMenultem.setPosition(onLineMenultem.getX(), onLineMenultem.getY()
				+ activity.setPointY(20));
		playNetMenultem.setPosition(playNetMenultem.getX(),
				playNetMenultem.getY() - activity.setPointY(80));
		exitMenultem.setPosition(exitMenultem.getX(), exitMenultem.getY()
				- activity.setPointY(180));

		WI_MENU = activity.setPointY(150);
		soundMenultem.setPosition(
				soundMenultem.getX() - activity.setPointX(150),
				exitMenultem.getY() - WI_MENU);
		webMenultem.setPosition(webMenultem.getX() - activity.setPointX(50),
				exitMenultem.getY() - WI_MENU);
		helpMenultem.setPosition(helpMenultem.getX() + activity.setPointX(50),
				exitMenultem.getY() - WI_MENU);
		aboutMenultem.setPosition(
				aboutMenultem.getX() + activity.setPointX(150),
				exitMenultem.getY() - WI_MENU);

		menuChildScene.setOnMenuItemClickListener(this);

		setChildScene(menuChildScene);

		WI = 8;
		x1 = 0 + WI;
		x2 = soundMenultem.getWidth() - WI;
		y1 = soundMenultem.getHeight() - WI;
		y2 = 0 + WI;
	}

	private void setNo() {
		line = new Line(x1, y1, x2, y2, 5, vbom);
		line.setColor(Color.WHITE);
		soundMenultem.attachChild(line);
	}

	private void disNo() {
		line.detachSelf();
		line.dispose();
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		resourcesManager.button_sound.play();
		switch (pMenuItem.getID()) {
		case MENU_PLAYER:
			SceneManager.GameType = SceneManager.PLAYER;
			SceneManager.getInstance().loadLevelScene(engine);
			return true;
		case MENU_ON_LINE:
			SceneManager.GameType = SceneManager.ONLINE;
			SceneManager.getInstance().loadLevelScene(engine);
			return true;
		case MENU_NET:
			SceneManager.GameType = SceneManager.NET;
			SceneManager.getInstance().loadLevelScene(engine);
			return true;
		case MENU_WEB:
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri
					.parse("http://www.softqilu.com/new/index.asp");
			intent.setData(content_url);
			activity.startActivity(intent);
			return true;
		case MENU_ABOUT:
			mType = GameActivity.ABOUT;
			intent = new Intent(activity, MockDialog.class);
			intent.putExtra("Type", mType);
			activity.startActivity(intent);
			return true;
		case MENU_HELP:
			mType = GameActivity.HELP;
			intent = new Intent(activity, MockDialog.class);
			intent.putExtra("Type", mType);
			activity.startActivity(intent);
			return true;
		case MENU_SOUND:
			if (sound) {
				sound = false;
				resourcesManager.button_sound.setVolume(0f);
				resourcesManager.spash_sound.setVolume(0f);
				this.setNo();
			} else {
				sound = true;
				resourcesManager.button_sound.setVolume(1f);
				resourcesManager.spash_sound.setVolume(1f);
				this.disNo();
			}
			return true;
		case MENU_EXIT:
			Message msg = new Message();
			msg.what = 0x1133;
			activity.mHandler.sendMessage(msg);
			return true;
		default:
			return false;
		}
	}

	@Override
	public void dialoging(int n) {

	}

}
