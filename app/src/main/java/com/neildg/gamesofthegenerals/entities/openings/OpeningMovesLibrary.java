/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.openings;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.minimax.BoardState;
import com.neildg.gamesofthegenerals.entities.minimax.Position;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;
import com.neildg.gamesofthegenerals.utils.JSONParser;

/**
 * Repository of opening moves to be used by the AI
 * @author NeilDG
 *
 */
public class OpeningMovesLibrary {

	private static final String TAG = "OpeningMovesLibrary";
	
	private static OpeningMovesLibrary sharedInstance = null;
	
	private final String OPENING_MOVES_DIR = "opening_moves";
	
	private ArrayList<BoardState> openingBoardStates;
	
	private BoardState adHocBoardState; //this is only used for adhoc mode.
	
	private Context appContext;
	
	private final static int MINIMUM_WIN_COUNT = 10;
	
	private int numFilesInLibrary = 0;
	
	private String lastWrittenFilePath = "";
	
	private OpeningMovesLibrary() {
		this.openingBoardStates = new ArrayList<BoardState>();
		this.appContext = EngineCore.getInstance().getContext();
	}
	
	//reads the library from the app's asset folder
	public void readLibraryFromCache() {
		try {
			String[] filePaths = this.getFilesInExternalCache();
			
			//if there are no opening moves stored in external cache, we refer to default moves in assets instead
			if(filePaths.length == 0) {
				filePaths = this.appContext.getAssets().list(OPENING_MOVES_DIR);
				
				for(String path : filePaths) {
					JSONObject jsonObj = new JSONObject(JSONParser.loadJSONFromAsset(this.appContext, OPENING_MOVES_DIR + "/" +path));
					this.parsePositions(jsonObj);
				}
			}
			else {
				for(String path : filePaths) {
					Log.v(TAG, "Path: " +path);
					JSONObject jsonObj = new JSONObject(JSONParser.loadJSONFromExternalCache(this.appContext, path));
					this.parsePositions(jsonObj);
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//returns the jsonobject read from a certain path in the external drive
	public JSONObject readJSONFromPath(String path) {
		JSONObject jsonObj = null;
		
		try {
			jsonObj = new JSONObject(JSONParser.loadJSONFromAbsolutePath(this.appContext, path));
			
		}
		catch(JSONException e) {
			e.printStackTrace();
		}
		
		return jsonObj;
	}
	
	//returns the list of opening moves in the external dir
	private String[] getFilesInExternalCache() {
		FilenameFilter jsonFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".json")) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		String[] filePaths = this.appContext.getExternalCacheDir().list(jsonFilter);
		this.numFilesInLibrary = filePaths.length;
		
		return filePaths;
	}
	
	public int getNumFilesInLibrary() {
		return this.numFilesInLibrary;
	}
	
	private void parsePositions(JSONObject jsonObj) throws JSONException{
		BoardState boardState = new BoardState();
		boardState.setWinCount(jsonObj.getInt("win_count"));
		
		Player computer = PlayerObserver.getInstance().getPlayerTwo();
		
		JSONArray jsonBoardPositions = jsonObj.getJSONArray("board_positions");
		
		for(int i = 0; i < jsonBoardPositions.length(); i++) {
			JSONObject jsonPos = jsonBoardPositions.getJSONObject(i);
			int pieceID = jsonPos.getInt("pieceID");
			int pieceType = jsonPos.getInt("pieceType");
			int row = jsonPos.getInt("row");
			int column = jsonPos.getInt("column");
			
			Position position = new Position(pieceID, pieceType, row, column, computer);
			boardState.addPosition(position, computer);
		}
		
		this.openingBoardStates.add(boardState);
		
		Log.v(TAG, "Saving new board state:  " +jsonObj.toString(2));
		
	}
	
	//parses the json object into a board state representation
	public void parseJSONResponse(JSONObject jsonObj) throws JSONException {
		this.adHocBoardState = new BoardState();
		this.adHocBoardState.setWinCount(jsonObj.getInt("win_count"));
		
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		
		JSONArray jsonBoardPositions = jsonObj.getJSONArray("board_positions");
		
		for(int i = 0; i < jsonBoardPositions.length(); i++) {
			JSONObject jsonPos = jsonBoardPositions.getJSONObject(i);
			int pieceID = jsonPos.getInt("pieceID");
			int pieceType = jsonPos.getInt("pieceType");
			int row = jsonPos.getInt("row");
			int column = jsonPos.getInt("column");
			
			Position position = new Position(pieceID, pieceType, row, column, playerTwo);
			this.adHocBoardState.addPosition(position, playerTwo);
		}
		Log.v(TAG, "Saving new board state:  " +jsonObj.toString(2));
	}
	
	//returns the board state setup from a remote device. Will return null if there's none
	public BoardState getAdHocBoardState() {
		return this.adHocBoardState;
	}
	
	public void clearAdHocBoardState() {
		this.adHocBoardState = null;
	}
	
	//finds the best board state from the list. TODO: create a heuristic for this
	public BoardState findBestBoardState() {
		
		BoardState bestBoardState = null;
		
		ArrayList<BoardState> filteredStateList = this.getBoardStatesWithSufficientWins();
		
		if(filteredStateList.size() == 0) {
			//pick a random board state
			bestBoardState = this.selectRandomBoardState(this.openingBoardStates);
			return bestBoardState;
		}
		else {
			//pick the best
			for(BoardState boardState : filteredStateList) {
				if(bestBoardState == null || boardState.getWinCount() > bestBoardState.getWinCount()) {
					bestBoardState = boardState;
				}
			}
			
			return bestBoardState;
		}
	}
	
	//we assign the last written file path that is stored in the library list. Should the computer wins, this path will be deleted. To filter noisy data
	public String getLastWrittenFilePath() {
		return this.lastWrittenFilePath;
	}
	
	public void setLastWrittenPath(String path) {
		this.lastWrittenFilePath = path;
	}
	
	public void deleteLastWrittenFile() {
		File file = new File(this.lastWrittenFilePath);
		
		if(!file.delete()) {
			throw new RuntimeException("Delete unsuccessful! " +this.lastWrittenFilePath+ " not found!");
		}
	}
	
	
	private BoardState selectRandomBoardState(ArrayList<BoardState> boardStateList) {
		Random rand = new Random();
		return boardStateList.get(rand.nextInt(boardStateList.size()));
	}
	
	public ArrayList<BoardState> getBoardStatesWithSufficientWins() {
		ArrayList<BoardState> boardStateList = new ArrayList<BoardState>();
		
		for(BoardState boardState : this.openingBoardStates) {
			if(boardState.getWinCount() >= MINIMUM_WIN_COUNT) {
				boardStateList.add(boardState);
			}
		}
		
		return boardStateList;
	}
	
	public static OpeningMovesLibrary getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new OpeningMovesLibrary();
		}
		
		return sharedInstance;
	}
}
