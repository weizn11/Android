package com.example.thread;

import android.os.Message;
import com.example.activity.GameActivity;
import com.example.jni.ChessServerJNI;
import com.example.manager.ChessManager;
import com.example.manager.CommunicateManager;
import com.example.manager.SceneManager;
import com.example.scene.GameScene;

/**
 * 通讯线程 与服务端建立链接
 * 
 * @author 张国栋
 * 
 */
public class Communicate extends Thread {

	private int GameMode; // 1，好友匹配，2，随机匹配
	private int GameType; // 2 4 5块子

	private GameActivity activity;
	private int n;
	private int m[] = null;
	private Message msg = null;

	public boolean bb = true;
	private boolean re = true;
	private GameScene gameScene;

	/**
	 * @param gameMode
	 *            :1 好友匹配。2随机匹配
	 * @param gameType
	 *            :2 4 5
	 */
	public Communicate(int gameMode, int gameType, GameActivity gameActivity) {
		this.GameMode = gameMode;
		this.GameType = gameType;
		this.activity = gameActivity;
	}

	@Override
	public void run() {

		n = this.linkServer();// 链接服务器
		if (0 == n) { // 显示进度条
			Message msg = new Message();
			msg.what = 0x8080;
			activity.mHandler.sendMessage(msg);
			if (matching() && bb) {// 匹配成功并且未超时
				SceneManager.getInstance().SHOWLEVEL = this.GameType;// 设置进入的场
				while (re) {
					re = false;
					int k[] = ChessServerJNI.getInstance()
							.recv_packet_from_server();
					switch (k[0]) {
					case 0:
					case 1:
						re = true;
						gameScene = SceneManager.getInstance().getGameScene();
						
						switch(k[1]){
						case -100:
							if (!GameScene.exitChess) {
								Message message2 = new Message();
								message2.what = 0x1144;
								message2.obj = "对手退出！";
								activity.mHandler.sendMessage(message2);
								gameScene.onBackKeyPressed();
							} else {
								// 设置为初始值
								GameScene.exitChess = false;
							}
							//this.send(-100, -100);
							re = false;
							break;
						case -200:
							Message message2 = new Message();
							message2.what = 0x1144;
							message2.obj = "对手退出！";
							activity.mHandler.sendMessage(message2);
							gameScene.onBackKeyPressed();
							re = false;
							break;
						default:
							gameScene.nowjustis(k[2], k[1], ChessManager.RECV);
							GameScene.moveChess = true;
								break;
						}
						break;
					case -2:
						Message message = new Message();
						message.what = 0x1122;
						message.obj = "对手思考超时！";
						activity.mHandler.sendMessage(message);
						break;
					default:
						Message message1 = new Message();
						message1.what = 0x1122;
						message1.obj = "与服务器断开连接！";
						activity.mHandler.sendMessage(message1);
						break;
					}
					// -1：与服务器连接断开
					// -2：若是第一次调用recv就出现则说明匹配超时，在游戏过程中出现表明对手思考超时
				}

			} else {
				// 匹配失败！
				ChessServerJNI.getInstance().disconnect();
				try {
					Thread.sleep(500);// 延迟5秒退出
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		ChessServerJNI.getInstance().disconnect();
		CommunicateManager.delCommunicate();
	}

	public void send(int currentXY, int id) {
		int m[] = new int[4];
		m[0] = id;
		m[1] = currentXY;
		@SuppressWarnings("unused")
		int n = ChessServerJNI.getInstance().send_packet_to_server(0, 0, m);
		GameScene.moveChess = false;
		// 返回值：-1、连接失败 -2、与服务器连接中断
	}

	public int linkServer() {
		Message message = new Message();
		message.what = 0x8282;
		int n = ChessServerJNI.getInstance().send_packet_to_server(GameMode,
				GameType, new int[] { 0, 0, 0, 0 });
		if (0 == n) {
			message.arg1 = 1;
			message.obj = "链接成功！";
		} else {
			message.arg1 = 0;
			message.obj = "链接失败！";
		}
		activity.mHandler.sendMessage(message);
		return n;
	}

	public boolean matching() {
		boolean b = false;
		m = ChessServerJNI.getInstance().recv_packet_from_server();
		msg = new Message();
		msg.what = 0x1122;
		switch (m[0]) {
		case 0:
			msg.obj = "匹配成功，对方先走。";
			GameScene.moveChess = false;
			GameScene.myChess = ChessManager.CHESS_WHITE;
			b = true;
			break;
		case 1:
			msg.obj = "匹配成功，您先走。";
			b = true;
			break;
		case -1:
			msg.obj = "匹配失败！";
			break;
		case -2:
			msg.obj = "匹配失败！";
			break;
		default:
			msg.obj = "匹配失败！";
			break;
		}
		return b;
	}

	public Message getM() {
		return msg;
	}

}
