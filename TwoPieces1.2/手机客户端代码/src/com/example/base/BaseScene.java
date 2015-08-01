package com.example.base;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.activity.GameActivity;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager.SceneType;

/**
 * ��Ϸ�д����ĳ����ĳ�����࣬����������Ϸ�߼�
 * 
 * @author �Ź���
 * 
 */
public abstract class BaseScene extends Scene {

	protected Engine engine;
	protected GameActivity activity;
	protected ResourcesManager resourcesManager;
	protected VertexBufferObjectManager vbom;
	protected Camera camera;

	public BaseScene() {
		this.resourcesManager = ResourcesManager.getInstance();
		this.engine = resourcesManager.engine;
		this.activity = resourcesManager.activity;
		this.vbom = resourcesManager.vbom;
		this.camera = resourcesManager.camera;

		createScene();
	}

	public abstract void createScene();

	public abstract void onBackKeyPressed();

	public abstract SceneType getSceneType();

	public abstract void disposeScene();

	public abstract void dialoging(int n);

	public void showExitDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("��ȷ��Ҫ�˳���Ϸ��").setCancelable(false).setTitle("�˳���Ϸ")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						System.exit(0);
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

}
