/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.game;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.board.BoardCell;
import com.neildg.gamesofthegenerals.entities.board.BoardCreator;
import com.neildg.gamesofthegenerals.entities.board.BoardManager;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameMode;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameState;
import com.neildg.gamesofthegenerals.entities.minimax.AlphaBetaSearch;
import com.neildg.gamesofthegenerals.entities.minimax.MonteCarloSearch;
import com.neildg.gamesofthegenerals.entities.multiplayer.DataInterpreter;
import com.neildg.gamesofthegenerals.entities.multiplayer.SocketManager;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

/**
 * Manages the exchanging of turns. Also serves as a basis for reversing moves
 * @author user
 *
 */
public class TurnManager {

	private static TurnManager sharedInstance = null; 
	
	private final static String TAG = "TurnManager";
	
	private int maxMoves;
	private int moveCount;
	
	private boolean isHost = true; //only used for ad hoc mode to determine who goes first. host goes first
	
	private TurnManager() {
		this.reset();
		this.setMaxMoves(99);
	}
	
	public void reset() {
		this.moveCount = 0;
	}
	
	public void MarkAsHost() {
		this.isHost = true;
	}
	
	public void MarkAsClient() {
		this.isHost = false;
	}
	
	//sets the max moves for the game before it is called a draw
	public void setMaxMoves(int moves) {
		this.maxMoves = moves;
	}
	
	public int getMaxMoves() {
		return this.maxMoves;
	}
	
	public void setFirstPlayerForBluetooth() {
		if(this.isHost) {
			PlayerObserver.getInstance().setActivePlayer(PlayerObserver.getInstance().getPlayerOne());
		}
		else {
			PlayerObserver.getInstance().setActivePlayer(PlayerObserver.getInstance().getPlayerTwo());
			this.moveCount++;
			
			NotificationCenter.getInstance().postNotification(Notifications.ON_WAITING_PLAYER_TURN, this);
			
		}
	}
	
	//reports a successful turn, changing the active player
	public void reportSuccessfulTurn() {
		this.moveCount++;
		
		//player one as even. TODO: create a method to choose who makes the first move
		if(this.moveCount % 2 == 0) {
			PlayerObserver.getInstance().setActivePlayer(PlayerObserver.getInstance().getPlayerOne());
		}
		else {
			PlayerObserver.getInstance().setActivePlayer(PlayerObserver.getInstance().getPlayerTwo());
		}
	}
	
	//hides the opponent pieces. Call this function for local playing
	public void hideOpponentPieces() {
		PlayerObserver.getInstance().getActivePlayer().revealAllPieces();
		PlayerObserver.getInstance().getInactivePlayer().markAllPiecesAsUnknown();
	}
	
	//processes proper turn over. If computer, execute search. If player, transfer controls.
	public void processTurnOver(final BoardPiece boardPiece, final BoardCell targetBoardCell) {
		
		if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_LOCAL) {
			BoardManager.getInstance().getBoardCreator().hideBoardContainer();
			NotificationCenter.getInstance().postNotification(Notifications.ON_FINISHED_PLAYER_TURN_LOCAL, this);
			Log.v(TAG, "Showing finished turn UI!");
		}
		
		else if(PlayerObserver.getInstance().getActivePlayer() == PlayerObserver.getInstance().getPlayerTwo()) {
			
			
			if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_COMPUTER) {
				Log.e(TAG, "Computer's move! Thinking");
				//find the best move by executing alpha-beta search
				/*AlphaBetaSearch aBTask = new AlphaBetaSearch();
				aBTask.assignLastMovedPiece(boardPiece);
				aBTask.execute();*/
				
				//monte carlo search
				Activity activity = (Activity) EngineCore.getInstance().getContext();
				activity.runOnUiThread(new Runnable() {
				    @Override
				    public void run() {
				    	MonteCarloSearch mcTask = new MonteCarloSearch();
						mcTask.assignLastMovedPiece(boardPiece);
						mcTask.execute();
				    }
				});
				
				//alpha beta search
				/*Activity activity = (Activity) EngineCore.getInstance().getContext();
				activity.runOnUiThread(new Runnable() {
				    @Override
				    public void run() {
				    	AlphaBetaSearch aBTask = new AlphaBetaSearch();
				    	aBTask.assignLastMovedPiece(boardPiece);
				    	aBTask.execute();
				    }
				});*/
			}
			
			else if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_ADHOC) {
				
				if(GameStateManager.getInstance().getCurrentState() != GameState.RESULTS)
					NotificationCenter.getInstance().postNotification(Notifications.ON_WAITING_PLAYER_TURN, this);
				
				//interpret in simple JSON objects
				try {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put(DataInterpreter.MOVE_PIECE_READING, 0);
					jsonObj.put("pieceID", boardPiece.getPieceID());
					jsonObj.put("row",   (BoardCreator.BOARD_ROWS - 1) - targetBoardCell.getRow()); //invert row
					jsonObj.put("column", targetBoardCell.getColumn());
					
					//SocketManager.getInstance().sendMessage(DataInterpreter.MOVE_PIECE_READING);
					
					//add a pause so the message won't be cut on the receiving client
					/*try {
						Thread.sleep(1000);
					} catch (final Throwable t) {
						Log.e(TAG,t.toString());
					}*/
					SocketManager.getInstance().sendMessage(jsonObj.toString(1));
				}
				catch(JSONException e) {
					e.printStackTrace();
				}
				
				
			}
			
		}
	}
	
	public static TurnManager getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new TurnManager();
		}
		
		return sharedInstance;
	}
}
