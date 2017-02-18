/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.openings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.JSONObject;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.entities.board.BoardCreator;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.Toast;

/**
 * Class that can be used to save the opening moves placed.
 * Use this when you want to populate the opening moves library.
 * TODO: Will be used for saving player's opening moves if it has won against the computer
 * @author NeilDG
 *
 */
public class OpeningMovesSaver {

	private static final String TAG = "OpeningMovesSaver";
	
	public static String OPENING_SAVE_PATH;
	
	private Activity activity;
	private Context appContext;
	private JsonWriter jsonWriter;
	private File file;
	
	private int writeNumberCode = 1;
	
	
	
	public OpeningMovesSaver() {
		this.appContext = EngineCore.getInstance().getContext();
		this.activity = (Activity) this.appContext;
		
		OPENING_SAVE_PATH = this.appContext.getExternalCacheDir().getAbsolutePath();
		this.writeNumberCode = OpeningMovesLibrary.getInstance().getNumFilesInLibrary() + 1;
	}
	
	//call this function through a button that will save the layout made by the player on an external directory.
	//Copy paste the JSON file to the assets folder of the application.
	public void saveBoardLayoutToExternal() {
		//add piece IDs for computer
		PlayerObserver.getInstance().getPlayerTwo().addPieceIDs();

		this.writeJSONStream();	
	}
	
	private void writeJSONStream() {
		try {
			this.initializeJSONWriter();
			this.jsonWriter.setIndent("    ");
			this.jsonWriter.beginObject();
			this.writeWinCount();
			this.writePieces();
			this.jsonWriter.endObject();
			this.jsonWriter.close();
			
			this.activity.runOnUiThread(new Runnable(){
			    public void run(){
			    	//Toast.makeText(OpeningMovesSaver.this.appContext, "Board layout saved to "+ OpeningMovesSaver.this.file.getAbsolutePath(), Toast.LENGTH_LONG).show();
			    }
			});
			
			this.writeNumberCode++;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeWinCount() throws IOException {
		this.jsonWriter.name("win_count").value(0);
	}
	
	private void writePieces() throws IOException {
		
		Player playerOne = PlayerObserver.getInstance().getPlayerOne();
	
		this.jsonWriter.name("board_positions");
		this.jsonWriter.beginArray();
		for(int  i = 0; i < playerOne.getAlivePiecesCount(); i++) {
			BoardPiece alivePiece = playerOne.getAlivePieceAt(i);
			
			int assignedRow = (BoardCreator.BOARD_ROWS - 1) - alivePiece.getBoardCell().getRow(); //invert row for computer use
			int assignedColumn = alivePiece.getBoardCell().getColumn();
			
			this.jsonWriter.beginObject();
			this.jsonWriter.name("pieceID").value(alivePiece.getPieceID());
			this.jsonWriter.name("pieceType").value(alivePiece.getPieceType());
			this.jsonWriter.name("row").value(assignedRow);
			this.jsonWriter.name("column").value(assignedColumn);
			this.jsonWriter.endObject();
			Log.v(TAG, "WRITING Piece Row: " +assignedRow+ " Column: " +assignedColumn);
			
		}
		this.jsonWriter.endArray();
	}
	
	public JSONObject convertPiecePlacementToJSON() {
		this.writeJSONStream();
		
		String path = OpeningMovesLibrary.getInstance().getLastWrittenFilePath();
		JSONObject jsonObj = OpeningMovesLibrary.getInstance().readJSONFromPath(path);
		
		OpeningMovesLibrary.getInstance().deleteLastWrittenFile();
		
		return jsonObj;
	}
	
	
	
	private void initializeJSONWriter() throws IOException {
		OpeningMovesLibrary.getInstance().setLastWrittenPath(OPENING_SAVE_PATH + "/player_opening_"+this.writeNumberCode+".JSON");
		
		this.file = new File(OpeningMovesLibrary.getInstance().getLastWrittenFilePath());
		FileWriter fw = new FileWriter(this.file);
		
		this.jsonWriter = new JsonWriter(fw);
	}
	
}
