package com.example.manager;

import com.example.chess.Chess;

public class ChessManager {
	
	public static final int YES_CHESS_UP = 1;
	
	public static final int CHESS_WHITE_WIN = 1;
	public static final int CHESS_BLACK_WIN = 2;
	public static final int FLAT = 0;
	
	public static final int UP = 1;
	public static final int DOWN = 0;
	public static final int CHESS_BLACK = 2;
	public static final int CHESS_WHITE = 1;
	public static final int CHESS_EMPTY = 0;
	public static final int CHESS_NO = -1;
	
	public static final int AI = 1;
	public static final int HUIQI = -1;
	public static final int USA = 0;
	public static final int SEND = -2;
	public static final int RECV = -3;

	
	public Stack<Integer> SoldChessId = new Stack<Integer>();
	public Stack<Integer> SnewChessId = new Stack<Integer>();
	public Stack<int []> SeatChess = new Stack<int []>();
	
	private Chess currentChess;
	private int NEXT_CHESS_COLOR = ChessManager.CHESS_BLACK;
	public int currentChessIsUp = ChessManager.DOWN;//ChessManager.NO_CHESS_UP;
	
	private static final ChessManager INSTANCE = new ChessManager();
	
	public Chess getCurrentChess() {
		return currentChess;
	}
	
	public void setoldnewChess(int oldi,int newi){
		SoldChessId.push(oldi);
		SnewChessId.push(newi);	}
	
	public void initializeStack(){
		SoldChessId.initialize();
		SnewChessId.initialize();
		SeatChess.initialize();
	}

	public void setCurrentChess(Chess currentChess) {
		this.currentChess = currentChess;
	}

	public static ChessManager getInstance() {
		return INSTANCE;
	}
	
	public int getNextCHESS_COLOR() {
		return NEXT_CHESS_COLOR;
	}
	
	public void setNextChess_color(){
		if(NEXT_CHESS_COLOR == ChessManager.CHESS_BLACK)
			NEXT_CHESS_COLOR = ChessManager.CHESS_WHITE;
		else
			NEXT_CHESS_COLOR = ChessManager.CHESS_BLACK;
	}
	
	public void currChess_color(){
		NEXT_CHESS_COLOR = CHESS_BLACK;
	}
	
}
