package com.example.jni;

public class ChessJNI {
	
	private static final ChessJNI INSTANCE = new ChessJNI();
	
	static{
		System.loadLibrary("TwoAI");
	}
	
	//Two
	public native int tjudge_tvictory(int chessboard[][]);
	
	public native int[] tAI(int chessboard[][], int player, int top_flag);
	
	//Four
	public native int[] feat_fchess(int chessboard[][], int i, int j);
	
	public native int fjudge_fvictory_ffour(int chessboard[][]);
	
	public native int[] fFour_fAI(int chessboard[][], int player);
	
	//Nine
	public native int njudge_fvictory_nine(int chessboard[][]);
	
	public native int[] nalter_nchess(int chessboard[][], int i, int j);
	
	public static ChessJNI getInstance() {
		return INSTANCE;
	}
}
