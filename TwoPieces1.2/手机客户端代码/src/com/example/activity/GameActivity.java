package com.example.activity;

import java.io.IOException;
import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import com.example.base.BaseScene;
import com.example.manager.CommunicateManager;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class GameActivity extends BaseGameActivity {

	public static final int ABOUT = 1;
	public static final int HELP = 2;
	public static final int ABOUTANDHELP = 3;
	private static final String SHAREDPREFERENCES_NAME = null;

	public SmoothCamera camera;
	public int CAMERA_WIDTH = 540;
	public int CAMERA_HEIGHT = 960;
	public Builder builder;
	private BaseScene baseScen;
	private int sleeptime = 0;
	private int progressStatus = 0;
	private ProgressDialog pd;
	private int add;

	@SuppressWarnings("unused")
	private ResourcesManager resourcesManager;
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x1122:
				showDialog(msg);
				break;
			case 0x1133:
				baseScen = SceneManager.getInstance().getCurrentScene();
				if (baseScen != null) {
					baseScen.showExitDialog();
				}
				break;
			case 0x1144:
				showDialogNull((String) msg.obj);
				break;
			case 0x8080:
				onCreateSleepTime(msg);
				showSleepTime(msg);
				break;
			case 0x8181:
				pd.setProgress(progressStatus);
				break;
			case 0x8282:
				showToasTest((String) msg.obj);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {

		camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 1000,
				1000, 1000);
		// 全屏，屏幕方法(竖屏)，分辨率大小
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws IOException {

		// 初始化ResourcesManager
		ResourcesManager.prepareManager(mEngine, this, camera,
				getVertexBufferObjectManager());
		resourcesManager = ResourcesManager.getInstance();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws IOException {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback)
			throws IOException {
		mEngine.registerUpdateHandler(new TimerHandler(5f,
				new ITimerCallback() {

					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						SceneManager.getInstance().createMeneScene();
						showFirst();
						setShowFirst();
					}
				}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();

	}

	public void showFirst() {
		// 读取SharedPreFerences中需要的数据,使用SharedPreFerences来记录程序启动的使用次数
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		// 取得相应的值,如果没有该值,说明还未写入,用true作为默认值
		boolean isFirstIn = preferences.getBoolean("isFirstIn", true);
		// 判断程序第几次启动
		if (isFirstIn) {
			Intent intent = new Intent(this, MockDialog.class);
			intent.putExtra("Type", GameActivity.ABOUTANDHELP);
			this.startActivity(intent);
		}
	}

	public void setShowFirst() {
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isFirstIn", false);
		// 提交修改
		editor.commit();
	}

	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {

		if (pKeyCode == KeyEvent.KEYCODE_BACK
				&& pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			baseScen = SceneManager.getInstance().getCurrentScene();
			if (baseScen != null)
				baseScen.onBackKeyPressed();
			else {
				Log.e("tag", "当前没有跟踪到场景!");
				System.exit(0);
			}
			return true;
		}

		return super.onKeyDown(pKeyCode, pEvent);
	}

	public void showDialog(final Message msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage((String) msg.obj).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						SceneManager
								.getInstance()
								.getCurrentScene()
								.dialoging(SceneManager.getInstance().SHOWLEVEL);
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	public void showDialogNull(String s) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(s).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	// 0x8080之耗时操作
	public int sleepTime() {
		sleeptime += add;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return sleeptime;
	}

	// 0x8080之进度条窗口初始化
	public void onCreateSleepTime(final Message msg) {
		sleeptime = 0;
		progressStatus = 0;
		// add = msg.arg2;
		add = 1;
		pd = new ProgressDialog(this);
		pd.setMax(100);
		pd.setTitle((String) msg.obj);
		pd.setMessage((String) msg.obj);
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setIndeterminate(true);
		pd.show();
	}

	// 0x8080之进度条更新
	public void showSleepTime(final Message mssg) {
		pd.incrementSecondaryProgressBy(-pd.getProgress());
		new Thread() {
			public void run() {
				while (progressStatus < 100
						&& CommunicateManager.getCommunicate().getM() == null) {
					progressStatus = sleepTime();
					Message m = new Message();
					m.what = 0x8181;
					mHandler.sendMessage(m);
				}
				pd.dismiss();
				if (progressStatus >= 100) {
					Message m = new Message();
					m.what = 0x1122;
					m.obj = "匹配失败！";
					mHandler.sendMessage(m);
					CommunicateManager.getCommunicate().bb = false;
				} else {
					mHandler.sendMessage(CommunicateManager.getCommunicate()
							.getM());
				}
			}
		}.start();
	}

	public void showToasTest(String s) {
		Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
		toast.show();
	}

	public float setPointX(int x) {
		return (float) (this.CAMERA_WIDTH * 1.0 * (x * 1.0 / 540.0));
	}

	public float setPointY(int y) {
		return (float) (this.CAMERA_HEIGHT * 1.0 * (y * 1.0 / 960.0));
	}

}
