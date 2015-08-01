package com.example.manager;

import com.example.thread.Communicate;

/**
 * 管理线程的类
 * @author 张国栋
 *
 */
public class CommunicateManager {
	
	private static Communicate communicate = null;
	
	/**
	 * 设置当前线程并运行
	 * 如果当前有线程在运行，返回false
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
	 * 给出当前线程
	 * @return
	 */
	public static Communicate getCommunicate(){
		if(communicate != null)
			return communicate;
		else
			return null;
	}
	
	/**
	 * 关闭线程
	 * 如果当前没有线程运行，返回false
	 * @return
	 */
	public static boolean delCommunicate(){
		if(communicate == null){
			return false;
		}else{
			//省略一个方法，应该调用线程类的销毁方法
			communicate = null;
			return true;
		}
	}
	
	/**
	 * 当前是否有线程在运行
	 * @return
	 */
	public static boolean ifCommunicate(){
		if(communicate != null)
			return true;
		else
			return false;
	}
}
