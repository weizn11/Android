package com.example.thread;

import android.os.Message;
import com.example.activity.GameActivity;
import com.example.jni.ChessServerJNI;
import com.example.manager.ChessManager;
import com.example.manager.CommunicateManager;
import com.example.manager.SceneManager;
import com.example.scene.GameScene;

/**
 * ͨѶ�߳� �����˽�������
 * 
 * @author �Ź���
 * 
 */
public class Communicate extends Thread {

	private int GameMode; // 1������ƥ�䣬2�����ƥ��
	private int GameType; // 2 4 5����

	private GameActivity activity;
	private int n;
	private int m[] = null;
	private Message msg = null;

	public boolean bb = true;
	private boolean re = true;
	private GameScene gameScene;

	/**
	 * @param gameMode
	 *            :1 ����ƥ�䡣2���ƥ��
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

		n = this.linkServer();// ���ӷ�����
		if (0 == n) { // ��ʾ������
			Message msg = new Message();
			msg.what = 0x8080;
			activity.mHandler.sendMessage(msg);
			if (matching() && bb) {// ƥ��ɹ�����δ��ʱ
				SceneManager.getInstance().SHOWLEVEL = this.GameType;// ���ý���ĳ�
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
								message2.obj = "�����˳���";
								activity.mHandler.sendMessage(message2);
								gameScene.onBackKeyPressed();
							} else {
								// ����Ϊ��ʼֵ
								GameScene.exitChess = false;
							}
							//this.send(-100, -100);
							re = false;
							break;
						case -200:
							Message message2 = new Message();
							message2.what = 0x1144;
							message2.obj = "�����˳���";
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
						message.obj = "����˼����ʱ��";
						activity.mHandler.sendMessage(message);
						break;
					default:
						Message message1 = new Message();
						message1.what = 0x1122;
						message1.obj = "��������Ͽ����ӣ�";
						activity.mHandler.sendMessage(message1);
						break;
					}
					// -1������������ӶϿ�
					// -2�����ǵ�һ�ε���recv�ͳ�����˵��ƥ�䳬ʱ������Ϸ�����г��ֱ�������˼����ʱ
				}

			} else {
				// ƥ��ʧ�ܣ�
				ChessServerJNI.getInstance().disconnect();
				try {
					Thread.sleep(500);// �ӳ�5���˳�
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
		// ����ֵ��-1������ʧ�� -2��������������ж�
	}

	public int linkServer() {
		Message message = new Message();
		message.what = 0x8282;
		int n = ChessServerJNI.getInstance().send_packet_to_server(GameMode,
				GameType, new int[] { 0, 0, 0, 0 });
		if (0 == n) {
			message.arg1 = 1;
			message.obj = "���ӳɹ���";
		} else {
			message.arg1 = 0;
			message.obj = "����ʧ�ܣ�";
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
			msg.obj = "ƥ��ɹ����Է����ߡ�";
			GameScene.moveChess = false;
			GameScene.myChess = ChessManager.CHESS_WHITE;
			b = true;
			break;
		case 1:
			msg.obj = "ƥ��ɹ��������ߡ�";
			b = true;
			break;
		case -1:
			msg.obj = "ƥ��ʧ�ܣ�";
			break;
		case -2:
			msg.obj = "ƥ��ʧ�ܣ�";
			break;
		default:
			msg.obj = "ƥ��ʧ�ܣ�";
			break;
		}
		return b;
	}

	public Message getM() {
		return msg;
	}

}
