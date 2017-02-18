/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.multiplayer;

import org.json.JSONException;
import org.json.JSONObject;

import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.board.BoardCell;
import com.neildg.gamesofthegenerals.entities.board.BoardManager;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.board.IBoardCell;
import com.neildg.gamesofthegenerals.entities.game.TurnManager;
import com.neildg.gamesofthegenerals.entities.openings.OpeningMovesLibrary;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

import android.util.Log;

/**
 * All bluetooth messages will go here to be interpreted by the system,
 * what type of message it is and how it will be interpreted in the game.
 * @author NeilDG
 *
 */
public class DataInterpreter {

	public final static String PIECE_PLACEMENT_READING = "PIECE_PLACEMENT";
	public final static String MOVE_PIECE_READING = "MOVE_PIECE";
	
	private final static String TAG = "DataInterpreter";
	
	private static DataInterpreter sharedInstance = null;
	
	
	private enum InterpretMode {
		PIECE_PLACEMENT,
		MOVE_PIECE,
		NOT_SET
	}
	
	private InterpretMode interpretMode;
	
	private DataInterpreter() {
		this.interpretMode = InterpretMode.NOT_SET;
	}
	
	public void interpretMessage(String message) {
		
		//string qualifiers to determine interpret mode on succeeding messages
		if(message.contains("PIECE_PLACE")) {
			this.interpretMode = InterpretMode.PIECE_PLACEMENT;
			return;
		}
		else if(message.contains(MOVE_PIECE_READING)) {
			this.interpretMode = InterpretMode.MOVE_PIECE;
			//return;
		}
		
		else if(message == Notifications.ON_BLUETOOTH_DISCONNECT) {
			NotificationCenter.getInstance().postNotification(Notifications.ON_BLUETOOTH_DISCONNECT, this);
			Log.v(TAG, "Remote device has disconnected. Broadcasting " +Notifications.ON_BLUETOOTH_DISCONNECT);
			return;
		}

		if(this.interpretMode == InterpretMode.PIECE_PLACEMENT) {
			this.parsePositions(message);
		}
		
		else if(this.interpretMode == InterpretMode.MOVE_PIECE) {
			this.processMovedPosition(message);
		}
	}
	
	//parse piece positions from received message
	private void parsePositions(String message) {
		
		//workaround. add } to end of message. dunno why it's missing
		String newMessage = message /*+ "}"*/;
		
		Log.v(TAG, "Received piece placement message");
		Log.v(TAG, newMessage);
		
		try {
			OpeningMovesLibrary.getInstance().parseJSONResponse(new JSONObject(newMessage));
			NotificationCenter.getInstance().postNotification(Notifications.ON_FINISHED_PLAYER_TURN_REMOTE, this);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void processMovedPosition(String message) {
		
		//workaround. add } to end of message. dunno why it's missing
		String newMessage = message /*+ "}"*/;
		
		Log.v(TAG, "Received move placement message");
		Log.v(TAG, newMessage);
				
		try {
			JSONObject jsonObj = new JSONObject(newMessage);
			int pieceID = jsonObj.getInt("pieceID");
			int newRow = jsonObj.getInt("row");
			int newCol = jsonObj.getInt("column");
			
			Player player = PlayerObserver.getInstance().getPlayerTwo();
			BoardPiece boardPiece = player.getAlivePieceByID(pieceID);
			BoardCell targetBoardCell = BoardManager.getInstance().getBoardCreator().getBoardAt(newRow, newCol);
			
			BoardManager.getInstance().processPiecePlacement(boardPiece, targetBoardCell);
			NotificationCenter.getInstance().postNotification(Notifications.ON_FINISHED_PLAYER_TURN_REMOTE, this);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void reset() {
		this.interpretMode = InterpretMode.NOT_SET;
	}
	
	public static DataInterpreter getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new DataInterpreter();
		}
		
		return sharedInstance;
	}
}
