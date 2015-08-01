package com.example.scene;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TickerText;
import org.andengine.entity.text.TickerText.TickerTextOptions;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.ease.EaseElasticOut;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import android.opengl.GLES20;

import com.example.base.BaseScene;
import com.example.manager.SceneManager.SceneType;

public class SplashScene extends BaseScene {

	private Sprite splash;
	private Text text;

	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
		splash = new Sprite(4, -20, resourcesManager.splash_region, vbom) {

			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		splash.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(2f), new MoveModifier(4, -20,
						activity.CAMERA_HEIGHT * 2 / 3,
						activity.CAMERA_WIDTH / 2 - 12,
						activity.CAMERA_HEIGHT * 2 / 3, EaseElasticOut
								.getInstance())));

		splash.setScale(2f);
		splash.setPosition(-64, -64);
		attachChild(splash);

		final Text text = new TickerText(activity.CAMERA_WIDTH / 2 - 24,
				activity.CAMERA_HEIGHT * 5 / 12, resourcesManager.font,
				" 12\n\n 齐鲁软件设计大赛", new TickerTextOptions(
						HorizontalAlign.CENTER, 10), vbom);
		text.registerEntityModifier(new SequenceEntityModifier(
				new ParallelEntityModifier(new AlphaModifier(4, 0.0f, 1.0f),
						new ScaleModifier(4, 0.5f, 1.0f))));
		text.setBlendFunction(GLES20.GL_SRC_ALPHA,
				GLES20.GL_ONE_MINUS_SRC_ALPHA);

		attachChild(text);

		this.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {

			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				resourcesManager.spash_sound.play();
			}
		}));
	}

	@Override
	public void onBackKeyPressed() {

	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		text.detachSelf();
		text.dispose();
		this.detachSelf();
		this.dispose();
	}

	@Override
	public void dialoging(int n) {

	}

}
