package com.example.jni;

public class ChessServerJNI {
	
	private static final ChessServerJNI INSTANCE = new ChessServerJNI();
	
	static{
		System.loadLibrary("ChessServer");
	}
	
	public native int send_packet_to_server(int GameMode,int GameType,int ChessCoord[]);
	
	public native int[] recv_packet_from_server();
	
	public native void disconnect();
	
	public static ChessServerJNI getInstance() {
		return INSTANCE;
	}
}
