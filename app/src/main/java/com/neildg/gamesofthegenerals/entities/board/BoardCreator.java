/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.board;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.util.Constants;

import com.neildg.gamesofthegenerals.atlases.MilitarySymbolsAtlas;
import com.neildg.gamesofthegenerals.entities.comparison.PieceHierarchy;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameMode;
import com.neildg.gamesofthegenerals.entities.minimax.BoardState;
import com.neildg.gamesofthegenerals.entities.minimax.Position;
import com.neildg.gamesofthegenerals.entities.openings.OpeningMovesLibrary;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

import android.util.Log;

/**
 * Creates the board to be attached into the assigned scene
 * @author user
 *
 */
public class BoardCreator {

	private static String TAG = "BoardCreator";
	
	public static int BOARD_ROWS = 8;
	public static int BOARD_COLUMNS = 9;
	
	public static int STARTING_ROW_FOR_BOARD_PLACEMENT = 5;
	
	private Entity boardContainer;
	private Scene assignedScene;
	
	private BoardCell[][] board;	//represents the board
	
	private int rowDisplayLength;
	private int columnDisplayLength;
	
	public BoardCreator(Scene assignedScene) {
		this.assignedScene = assignedScene;
		this.boardContainer = new Entity();
		this.board = new BoardCell[BOARD_ROWS][BOARD_COLUMNS];
		
		BoardManager.getInstance().setBoardCreator(this);
		
		this.assignedScene.attachChild(this.boardContainer);
		
		OpeningMovesLibrary.getInstance().readLibraryFromCache();
	}
	
	public void destroy() {
		this.boardContainer.detachChildren();
		this.boardContainer.detachSelf();
	}
	
	public void hideBoardContainer() {
		
		this.boardContainer.setVisible(false);
		
		//unregister touch areas of board pieces
		Player playerOne = PlayerObserver.getInstance().getPlayerOne();
		for(int  i = 0; i < playerOne.getAlivePiecesCount(); i++) {
			BoardPiece boardPiece = playerOne.getAlivePieceAt(i);
			this.assignedScene.unregisterTouchArea(boardPiece);
		}
		
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		for(int i = 0; i < playerTwo.getAlivePiecesCount(); i++) {
			BoardPiece boardPiece = playerTwo.getAlivePieceAt(i);
			this.assignedScene.unregisterTouchArea(boardPiece);
		}
		
		
	}
	
	public void showBoardContainer() {
		this.boardContainer.setVisible(true);
		
		//register the touch areas
		Player playerOne = PlayerObserver.getInstance().getPlayerOne();
		for(int  i = 0; i < playerOne.getAlivePiecesCount(); i++) {
			BoardPiece boardPiece = playerOne.getAlivePieceAt(i);
			this.assignedScene.registerTouchArea(boardPiece);
		}
		
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		for(int i = 0; i < playerTwo.getAlivePiecesCount(); i++) {
			BoardPiece boardPiece = playerTwo.getAlivePieceAt(i);
			this.assignedScene.registerTouchArea(boardPiece);
		}
	}
	
	//checks if the specified row and column is within the bounds of this board size
	public static boolean isWithinBounds(int row, int column) {
		if(row >= 0 && row < BOARD_ROWS && column >= 0 && column < BOARD_COLUMNS) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void populateBoard() {
		float x = 0;
		float y = 0;
		
		this.rowDisplayLength = BOARD_ROWS;
		this.columnDisplayLength = BOARD_COLUMNS;
		
		this.assignedScene.setTouchAreaBindingOnActionDownEnabled(true);
		for(int row = 0; row < BOARD_ROWS; row++) {
			for(int column = 0; column < BOARD_COLUMNS; column++) {
				BoardCell boardCell = new BoardCell(x, y, BoardCell.BoardCellType.WHITE_CELL);
				this.board[row][column] = boardCell;
				boardCell.setRowAndColumn(row, column);
				this.boardContainer.attachChild(boardCell);
				x += BoardCell.CELL_WIDTH;
				
			}
			
			x = 0;
			y += BoardCell.CELL_HEIGHT;
		}
	}
	
	//only creates the bottom rows. Called by the board placement scene
	public void populateBoardForPlacement() {
		float x = 0;
		float y = 0;
		
		this.rowDisplayLength = BOARD_ROWS - STARTING_ROW_FOR_BOARD_PLACEMENT;
		this.columnDisplayLength = BOARD_COLUMNS;
		
		for(int row = STARTING_ROW_FOR_BOARD_PLACEMENT; row < BOARD_ROWS; row++) {
			for(int column = 0; column < BOARD_COLUMNS; column++) {
				BoardCell boardCell = new BoardCell(x, y, BoardCell.BoardCellType.WHITE_CELL);
				this.board[row][column] = boardCell;
				boardCell.setRowAndColumn(row, column);
				this.assignedScene.setTouchAreaBindingOnActionDownEnabled(true);
				this.boardContainer.attachChild(boardCell);
				x += BoardCell.CELL_WIDTH;
				
			}
			
			x = 0;
			y += BoardCell.CELL_HEIGHT;
		}
	}
	
	public int getRowDisplayLength() {
		return this.rowDisplayLength;
	}
	
	public int getColumnDisplayLength() {
		return this.columnDisplayLength;
	}
	
	public void populatePieces(Player player) {
		player.addPieceIDs(); //add piece IDs for player
		
		
		for(int  i = 0; i < player.getAlivePiecesCount(); i++) {
			BoardPiece alivePiece = player.getAlivePieceAt(i);
			
			
			int assignedRow = alivePiece.getBoardCell().getRow();
			int assignedColumn = alivePiece.getBoardCell().getColumn();
			
			//inverts row number if player two
			if(player == PlayerObserver.getInstance().getPlayerTwo()) {
				assignedRow = (BoardCreator.BOARD_ROWS - 1) - alivePiece.getBoardCell().getRow(); 
			}
			
			Log.v(TAG, "PieceID: " +alivePiece.getPieceID()+ " Row: " +assignedRow+ " Column: " +assignedColumn);
			
			this.assignedScene.registerTouchArea(alivePiece);
			alivePiece.setBoardContainer(this.boardContainer);
			alivePiece.addToBoard();
			
			
			
			alivePiece.placePieceToCell(this.getBoardAt(assignedRow, assignedColumn));
			
		}
		
		//PlayerObserver.getInstance().getPlayerTwo().markAllPiecesAsUnknown(); //for player two, hide pieces by default
	}
	
	//call this for ad hoc mode or bluetooth
	public void populateAdHocPieces() {
		BoardState adHocState = OpeningMovesLibrary.getInstance().getAdHocBoardState();
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		
		for(int i = 0; i < adHocState.getPositionSize(playerTwo); i++) {
			Position position = adHocState.getPositionAt(playerTwo, i);
			BoardPiece boardPiece = new BoardPiece(0, 0, position.getPieceType(), this.boardContainer);
			this.assignedScene.registerTouchArea(boardPiece);
			boardPiece.addToBoard();
			boardPiece.placePieceToCell(this.getBoardAt(position.getRow(), position.getColumn()));
			playerTwo.addAlivePiece(boardPiece);
			
		}
		
		PlayerObserver.getInstance().getPlayerTwo().addPieceIDs();
		
		//PlayerObserver.getInstance().getPlayerTwo().markAllPiecesAsUnknown(); //QQQQQ placeholder
	}
	
	
	public void populateEnemyPieces() {
		
		BoardState openingState = OpeningMovesLibrary.getInstance().findBestBoardState();
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		
		
		
		for(int i = 0; i < openingState.getPositionSize(playerTwo); i++) {
			Position position = openingState.getPositionAt(playerTwo, i);
			BoardPiece boardPiece = new BoardPiece(0, 0, position.getPieceType(), this.boardContainer);
			this.assignedScene.registerTouchArea(boardPiece);
			boardPiece.addToBoard();
			boardPiece.placePieceToCell(this.getBoardAt(position.getRow(), position.getColumn()));
			playerTwo.addAlivePiece(boardPiece);
			
		}
		
		//PlayerObserver.getInstance().getPlayerTwo().markAllPiecesAsUnknown(); //QQQQQ placeholder
		
		/*int row = 0;
		int column = 0;
		for(int pieceType = PieceHierarchy.KIND_OF_PIECES - 1; pieceType >= 0; pieceType--) {
			BoardPiece boardPiece = new BoardPiece(0,0, pieceType, this.boardContainer);
			this.assignedScene.registerTouchArea(boardPiece);
			boardPiece.addToBoard();
			boardPiece.placePieceToCell(this.getBoardAt(row, column));
			playerTwo.addAlivePiece(boardPiece);
			column++;
			
			if(column == BOARD_COLUMNS) {
				column = 0;
				row++;
			}
			
		}*/
		
		PlayerObserver.getInstance().getPlayerTwo().addPieceIDs();
	}
	
	public void setBoardPosition(float x, float y) {
		this.boardContainer.setPosition(x, y);
	}
	
	public BoardCell getBoardAt(int row, int column) {
		return this.board[row][column];
	}
	
	
	public void snapToBoardCell(BoardPiece boardPiece, int row, int column) {
		BoardCell boardCell = this.getBoardAt(row, column);
		
		float pieceCenterX = boardPiece.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_X];
		float pieceCenterY = boardPiece.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_Y];
		
		float boardCenterX = boardCell.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_X];
		float boardCenterY = boardCell.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_Y];
		
		float deltaX = boardPiece.getX() + (boardCenterX - pieceCenterX);
		float deltaY = boardPiece.getY() + (boardCenterY - pieceCenterY);
		
		boardPiece.setPosition(deltaX, deltaY);
		Log.v(TAG, "Board Piece X: " +boardPiece.getX()+ " Y: " +boardPiece.getY());
	}
}
