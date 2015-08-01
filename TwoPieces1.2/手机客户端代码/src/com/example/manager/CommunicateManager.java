package com.example.manager;

import com.example.thread.Communicate;

/**
 * �����̵߳���
 * @author �Ź���
 *
 */
public class CommunicateManager {
	
	private static Communicate communicate = null;
	
	/**
	 * ���õ�ǰ�̲߳�����
	 * �����ǰ���߳������У�����false
	 * @param c
	 * @return
	 */
	public static boolean setCommunicate(Communicate c){
		if(communicate == null){
			communicate = c;
			communicate.start();
			return true;
		}else
			return false;
	}
	
	/**
	 * ������ǰ�߳�
	 * @return
	 */
	public static Communicate getCommunicate(){
		if(communicate != null)
			return communicate;
		else
			return null;
	}
	
	/**
	 * �ر��߳�
	 * �����ǰû���߳����У�����false
	 * @return
	 */
	public static boolean delCommunicate(){
		if(communicate == null){
			return false;
		}else{
			//ʡ��һ��������Ӧ�õ����߳�������ٷ���
			communicate = null;
			return true;
		}
	}
	
	/**
	 * ��ǰ�Ƿ����߳�������
	 * @return
	 */
	public static boolean ifCommunicate(){
		if(communicate != null)
			return true;
		else
			return false;
	}
}
