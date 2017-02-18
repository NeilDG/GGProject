/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.minimax;

import java.util.ArrayList;

import android.util.Log;

import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.comparison.PieceHierarchy;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

/**
 * Represents a valid position used by the board state
 * @author user
 *
 */
public class Position {

	private static String TAG = "Position";
	
	private int pieceID;
	private byte pieceType;
	private int row;
	private int column;
	
	private boolean willDuel = false; //marks this piece for dueling, meaning that its position has another piece (which is also marked for dueling)
	private float pieceValue = 0.0f;
	
	private int playerIndex;		//the player who should make the move possible for this position to be reached. 0 for player one. 1 for player two
	
	public Position(BoardPiece boardPiece, Player ownedPlayer) {
		super();
		
		this.pieceType = (byte) boardPiece.getPieceType();
		this.pieceID = boardPiece.getPieceID();
		
		this.row = boardPiece.getBoardCell().getRow();
		this.column = boardPiece.getBoardCell().getColumn();
		
		this.playerIndex = this.determinePlayerIndex(ownedPlayer);
		
		//this.heuristicScore = PieceHierarchy.getInitialHeuristic(pieceType);
		this.pieceValue = PieceHierarchy.getInitialHeuristic(pieceType);
		
	}
	
	public Position(int pieceID, int pieceType, int row, int column, Player ownedPlayer) {
		super();
		
		this.pieceID = pieceID;
		this.pieceType = (byte) pieceType;
		this.row = row;
		this.column = column;
		this.playerIndex = this.determinePlayerIndex(ownedPlayer);
		
		//this.heuristicScore = PieceHierarchy.getInitialHeuristic(pieceType);
		this.pieceValue = PieceHierarchy.getInitialHeuristic(pieceType);
		
	}
	
	public int getPieceID() {
		return this.pieceID;
	}
	
	public int determinePlayerIndex(Player player) {
		if(player == PlayerObserver.getInstance().getPlayerOne()) {
			return 0;
		}
		else {
			return 1;
		}
	}
	public void markForDuel() {
		this.willDuel = true;
	}
	
	public boolean isPositionForDuel() {
		return this.willDuel;
	}
	
	public float getPieceValue() {
		return this.pieceValue;
	}
	
	public int getPieceType() {
		return this.pieceType;
	}

	
	public int getRow() {
		return this.row;
	}
	
	public int getColumn() {
		return this.column;
	}
	
	public void setNewPosition(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	public Player getOwnedPlayer() {
		if(this.playerIndex == 0) {
			return PlayerObserver.getInstance().getPlayerOne();
		}
		else {
			return PlayerObserver.getInstance().getPlayerTwo();
		}
	}
	
	public void printDebugValues() {
		Log.d(TAG, " Position - Piece Type: " +this.pieceType+ " | Row: " +this.row+ " Column: " +this.column);
	}
}
