package com.example.chess;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.example.manager.ChessManager;
import com.example.manager.ResourcesManager;
import com.example.scene.GameScene;

public class ChessWhite extends Chess{
	
	
	public ChessWhite(int pXY[], int iD,GameScene game, ResourcesManager resourcesManager,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pXY[0], pXY[1],ChessManager.CHESS_WHITE, iD,game, resourcesManager.game_chess_white_region, pVertexBufferObjectManager);

	}
}

