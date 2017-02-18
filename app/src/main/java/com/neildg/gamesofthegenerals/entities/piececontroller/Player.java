/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.piececontroller;

import java.util.ArrayList;

import android.util.Log;

import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.comparison.Arbiter;
import com.neildg.gamesofthegenerals.entities.comparison.PieceHierarchy;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;

/**
 * Represents a player
 * @author user
 *
 */
public class Player {

	private final static String TAG = "Player";
	
	public final static int PLAYER_ONE_ID = 1;
	public final static int PLAYER_TWO_ID = 2;
	
	private int playerID;
	private String playerName;
	private int currentLevel;
	
	private ArrayList<BoardPiece> alivePieces;
	private ArrayList<BoardPiece> deadPieces;
	
	public Player(String playerName, int playerID) {
		//placeholder values
		this.playerName = playerName;
		this.playerID = playerID;
		this.currentLevel = 1;
		
		this.alivePieces = new ArrayList<BoardPiece>();
		this.deadPieces = new ArrayList<BoardPiece>();
	}
	
	public int getPlayerID() {
		return this.playerID;
	}
	
	public void addAlivePiece(BoardPiece boardPiece) {
		if(!this.alivePieces.contains(boardPiece)) {
			this.alivePieces.add(boardPiece);
			Log.v(TAG, "Added alive piece " +boardPiece.getPieceType());
		}
		else {
			Log.e(TAG, "Board piece already added " +boardPiece.getPieceID()+ " owned by " +boardPiece.getPlayerOwner().getPlayerName());
		}
		
	}
	
	public String getPlayerName() {
		return this.playerName;
	}
	
	public void setPlayerName(String newPlayerName) {
		this.playerName = newPlayerName;
	}
	
	public void addPieceIDs() {
		
		int privateUniqueID = 990; //we use this for privates
		int spyUniqueID = 880; //we use this for spies
		
		for(BoardPiece alivePiece : this.alivePieces) {
			if(alivePiece.getPieceType() == PieceHierarchy.PRIVATE) {
				alivePiece.setPieceID(privateUniqueID);
				privateUniqueID++;
			}
			else if(alivePiece.getPieceType() == PieceHierarchy.SPY) {
				alivePiece.setPieceID(spyUniqueID);
				spyUniqueID++;
			}
			else {
				alivePiece.setPieceID(alivePiece.getPieceType());
			}
		}
	}
	
	public BoardPiece getAlivePieceByID(int pieceID) {
		for(BoardPiece alivePiece : this.alivePieces) {
			if(alivePiece.getPieceID() == pieceID) {
				return alivePiece;
			}
		}
		
		Log.e(TAG, "Board piece of " +pieceID+ " ID not found!");
		return null;
	}
	
	public void removeAlivePiece(BoardPiece boardPiece) {
		this.alivePieces.remove(boardPiece);
		Log.v(TAG, "Removed alive piece from  " +this.playerName);
		
	}
	
	//returns true if the piece is owned by the player
	public boolean isPieceOwned(BoardPiece boardPiece) {
		if(this.alivePieces.contains(boardPiece)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public BoardPiece getAlivePieceAt(int index) {
		return this.alivePieces.get(index);
	}
	
	public int getAlivePiecesCount() {
		return this.alivePieces.size();
	}
	
	//marks all pieces as unknown
	public void markAllPiecesAsUnknown() {
		for(BoardPiece boardPiece : this.alivePieces) {
			boardPiece.markAsUnknown();
		}
		
		for(BoardPiece boardPiece : this.deadPieces) {
			boardPiece.markAsUnknown();
		}
	}
	
	public void revealAllPieces() {
		for(BoardPiece boardPiece : this.alivePieces) {
			boardPiece.revealPiece();
		}
		
		for(BoardPiece boardPiece : this.deadPieces) {
			boardPiece.revealPiece();
		}
	}
	
	public void clearAllPieces() {
		this.alivePieces.clear();
		this.deadPieces.clear();
	}
	
	//kills a piece if it has been eaten, putting it into the dead pieces list
	public void killPiece(BoardPiece boardPiece) {
		this.removeAlivePiece(boardPiece);
		this.deadPieces.add(boardPiece);
		
		//if the killed piece is a flag, we end the game
		if(boardPiece.getPieceType() == PieceHierarchy.FLAG) {
			
			Player playerOne = PlayerObserver.getInstance().getPlayerOne();
			Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
			
			if(playerOne == this) {
				//means player two is winner
				PlayerObserver.getInstance().setWinner(playerTwo);
				Log.v(TAG, "Notifying winner!");
				NotificationCenter.getInstance().postNotification(Notifications.ON_CAPTURED_FLAG, playerTwo);
			}
			else {
				//means player one is winner
				PlayerObserver.getInstance().setWinner(playerOne);
				Log.v(TAG, "Notifying winner!");
				NotificationCenter.getInstance().postNotification(Notifications.ON_CAPTURED_FLAG, playerOne);
			}
			
			
			GameStateManager.getInstance().reportGameOver();
		}
		
		//killed piece goes to player one display
		if(this.playerID == PLAYER_ONE_ID) {
			NotificationCenter.getInstance().postNotification(Notifications.ON_KILLED_PLAYER_ONE_PIECE, boardPiece);
		}
		//killed piece goes to player two display
		else if(this.playerID == PLAYER_TWO_ID) {
			//boardPiece.setVisible(false);
			NotificationCenter.getInstance().postNotification(Notifications.ON_KILLED_PLAYER_TWO_PIECE, boardPiece);
		}
		
		
	}
}
