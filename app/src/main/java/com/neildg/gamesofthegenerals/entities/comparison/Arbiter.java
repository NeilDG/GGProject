/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.comparison;

import android.util.Log;

import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.minimax.Position;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

/**
 * Represents the arbiter that handles the comparison of pieces
 * @author user
 *
 */
public class Arbiter {

	private final static String TAG = "Arbiter";
	private static Arbiter sharedInstance = null;
	
	public static int WINNING_PIECE_INDEX = 0;
	public static int DEFEATED_PIECE_INDEX = 1;
	
	private Arbiter() {
		
	}
	
	//evaluates the pieces. Returns the surviving boardPiece and the dead piece
	public BoardPiece[] evaluatePieces(BoardPiece attackingPiece, BoardPiece defendingPiece) {
		int attackingPieceType = attackingPiece.getPieceType();
		int defendingPieceType = defendingPiece.getPieceType();
		
		BoardPiece[] result = new BoardPiece[2];
		
		Log.d(TAG, "Arbiter evaluate! Attacking: " +attackingPieceType+ " Defending: " +defendingPieceType);
		
		if(attackingPieceType == defendingPieceType) {
			if(attackingPieceType == PieceHierarchy.FLAG && defendingPieceType == PieceHierarchy.FLAG) {
				//attacking piece wins the game
				result[WINNING_PIECE_INDEX] = attackingPiece;
				result[DEFEATED_PIECE_INDEX] = defendingPiece;

				this.eliminatePiece(defendingPiece);
				return result;
			}
			else {
				//both pieces are eliminated
				this.eliminatePiece(attackingPiece);
				this.eliminatePiece(defendingPiece);
				return null;
			}
			
		}
		//spies case
		else if(attackingPieceType == PieceHierarchy.SPY) {
			if(defendingPieceType == PieceHierarchy.PRIVATE) {
				//attacking piece defeated
				this.eliminatePiece(attackingPiece);
				result[WINNING_PIECE_INDEX] = defendingPiece;
				result[DEFEATED_PIECE_INDEX] = attackingPiece;
				return result;
			}
			else {
				//defending piece is defeated
				this.eliminatePiece(defendingPiece);
				result[WINNING_PIECE_INDEX] = attackingPiece;
				result[DEFEATED_PIECE_INDEX] = defendingPiece;
				return result;

			}
		}
		else if(defendingPieceType == PieceHierarchy.SPY) {
			if(attackingPieceType == PieceHierarchy.PRIVATE) {
				//defending piece is defeated
				this.eliminatePiece(defendingPiece);
				result[WINNING_PIECE_INDEX] = attackingPiece;
				result[DEFEATED_PIECE_INDEX] = defendingPiece;
				return result;
			}
			else {
				//attacking piece is defeated
				this.eliminatePiece(attackingPiece);
				result[WINNING_PIECE_INDEX] = defendingPiece;
				result[DEFEATED_PIECE_INDEX] = attackingPiece;
				return result;
			}
		}
		//any other cases
		else {
			if(attackingPieceType > defendingPieceType) {
				//defending piece type is defeated
				this.eliminatePiece(defendingPiece);
				result[WINNING_PIECE_INDEX] = attackingPiece;
				result[DEFEATED_PIECE_INDEX] = defendingPiece;
				return result;
			}
			else {
				//attacking piece type is defeated
				this.eliminatePiece(attackingPiece);
				result[WINNING_PIECE_INDEX] = defendingPiece;
				result[DEFEATED_PIECE_INDEX] = attackingPiece;
				return result;
			}
		}
	}
	
	//same algo but this one is used for simulation TODO: refactor this soon to share one method only. NOTE: Returns the DEFEATED position.
	public Position evaluatePosition(Position attackingPos, Position defendingPos) {
		int attackingPieceType = attackingPos.getPieceType();
		int defendingPieceType = defendingPos.getPieceType();
		
		//Log.d(TAG, "Arbiter simulation! Attacking: " +attackingPieceType+ " Defending: " +defendingPieceType);
		
		if(attackingPieceType == defendingPieceType) {
			if(attackingPieceType == PieceHierarchy.FLAG && defendingPieceType == PieceHierarchy.FLAG) {
				//attacking piece wins the game
				return defendingPos;
			}
			else {
				//both pieces are eliminated
				return null;
			}
			
		}
		//spies case
		else if(attackingPieceType == PieceHierarchy.SPY) {
			if(defendingPieceType == PieceHierarchy.PRIVATE) {
				//attacking piece defeated
				return attackingPos;
			}
			else {
				//defending piece is defeated
				return defendingPos;
			}
		}
		else if(defendingPieceType == PieceHierarchy.SPY) {
			if(attackingPieceType == PieceHierarchy.PRIVATE) {
				//defending piece is defeated
				return defendingPos;
			}
			else {
				//attacking piece is defeated
				return attackingPos;
			}
		}
		//any other cases
		else {
			if(attackingPieceType > defendingPieceType) {
				//defending piece type is defeated
				return defendingPos;
			}
			else {
				//attacking piece type is defeated
				return attackingPos;
			}
		}
	}
	
	private void eliminatePiece(BoardPiece boardPiece) {
		//determine who owns this piece
		Player playerOwner = PlayerObserver.getInstance().getPlayerOwner(boardPiece);
		playerOwner.killPiece(boardPiece);
		boardPiece.destroy();
	}
	
	public static Arbiter getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new Arbiter();
		}
		
		return sharedInstance;
	}
}
