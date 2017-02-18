/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.board;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.comparison.Arbiter;
import com.neildg.gamesofthegenerals.entities.comparison.PieceHierarchy;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameMode;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameState;
import com.neildg.gamesofthegenerals.entities.game.TurnManager;
import com.neildg.gamesofthegenerals.entities.minimax.AlphaBetaSearch;
import com.neildg.gamesofthegenerals.entities.minimax.BoardState;
import com.neildg.gamesofthegenerals.entities.minimax.GameTreeGenerator;
import com.neildg.gamesofthegenerals.entities.minimax.MonteCarloSearch;
import com.neildg.gamesofthegenerals.entities.minimax.Position;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;
import com.neildg.gamesofthegenerals.input.PiecePlacementInputHandler;

import android.app.Activity;
import android.util.Log;

/**
 * Manages the board and how the piece is being placed.
 * Tracks the pieces currently on the board and pieces that have been eliminated.
 * @author user
 *
 */
public class BoardManager {

	private final static String TAG = "BoardManager";
	private static BoardManager sharedInstance = null;
	
	private BoardCreator boardCreator;
	
	private BoardPiece lastBoardPieceMoved; //reference for the last piece moved. We use this to refresh replay on local versus.
	
	private BoardManager() {
		
	}
	
	public void setBoardCreator(BoardCreator boardCreator) {
		this.boardCreator = boardCreator;
	}
	
	public BoardCreator getBoardCreator() {
		return this.boardCreator;
	}
	
	
	
	//called while the piece is being dragged
	public void highlightCollidedCell(BoardPiece boardPiece) {
		
		for(int row = BoardCreator.BOARD_ROWS - 1; row >= 0; row--){
			for(int column =  BoardCreator.BOARD_COLUMNS - 1; column >= 0; column--) {
				BoardCell boardCell = this.boardCreator.getBoardAt(row, column);
				if(boardCell != null) {
					if(boardCell.getBoundingBox().collidesWith(boardPiece)) {
						boardCell.setAlpha(0.4f);
					}
					else {
						boardCell.setAlpha(1.0f);
					}
				}
			}
		}
	}
	
	//applies the board state. This represents the move to be done by the computer
	public void applyBoardState(BoardState boardState) {
		Player computer = PlayerObserver.getInstance().getPlayerTwo();
		//for security, we check active player. This is to make computer vs computer possible
		//Player computer = PlayerObserver.getInstance().getActivePlayer();
		
		for(int i = 0; i < boardState.getPositionSize(computer); i++) {
			Position position = boardState.getPositionAt(computer, i);
			BoardCell boardCell = this.boardCreator.getBoardAt(position.getRow(), position.getColumn());
			BoardPiece boardPiece = computer.getAlivePieceByID(position.getPieceID());
			
			
			this.processPiecePlacement(boardPiece, boardCell);
		}
		/*Position position = boardState.getMovedPosition();
		BoardCell boardCell = this.boardCreator.getBoardAt(position.getRow(), position.getColumn());
		BoardPiece boardPiece = computer.getAlivePieceByID(position.getPieceID());
		
		this.processPiecePlacement(boardPiece, boardCell);*/
		//TurnManager.getInstance().reportSuccessfulTurn();
	}
	
	public void updatePiecePosition(BoardPiece boardPiece) {
		
		for(int row = BoardCreator.BOARD_ROWS - 1; row >= 0; row--){
			for(int column =  BoardCreator.BOARD_COLUMNS - 1; column >= 0; column--) {
				BoardCell boardCell = this.boardCreator.getBoardAt(row, column);
				if(boardCell != null && boardCell.getBoundingBox().collidesWith(boardPiece)) {
					if(GameStateManager.getInstance().getCurrentState() == GameState.PIECE_PLACEMENT) {
						this.processPiecePlacement(boardPiece, boardCell);	
					}
					else if(this.validateGamePosition(boardPiece, boardCell)) {
						this.processPiecePlacement(boardPiece, boardCell);
						//boardPiece.placePieceToCell(boardCell);
					}
					else {
						//return to old position
						boardCell.setAlpha(1.0f);
						boardPiece.placePieceToCell(boardPiece.getBoardCell());
					}
					return;
				}
			}
		}
		
		//if scene is piece placement, delete the piece if no valid cell is found
		if(GameStateManager.getInstance().getCurrentState() == GameState.PIECE_PLACEMENT && boardPiece != null) {
			PiecePlacementInputHandler.getInstance().reportFailedPlacement(boardPiece);
			IBoardCell oldBoardCell = boardPiece.getBoardCell();
			if(oldBoardCell != null) {
				oldBoardCell.removeAssignedBoardPiece();
			}
			boardPiece.destroy();
			boardPiece = null;
		}
		else if(GameStateManager.getInstance().getCurrentState() == GameState.MAIN_GAME) {
			//if no colliding cell was found, snap back to place
			boardPiece.placePieceToCell(boardPiece.getBoardCell());
		}
		
	}
	
	public void processPiecePlacement(final BoardPiece boardPiece, BoardCell boardCell) {
		Player activePlayer = PlayerObserver.getInstance().getActivePlayer();
		
		boardCell.setAlpha(0.76f);
		
		//check if new position is empty
		if(!this.isNewPositionOccupied(boardCell)) {
			boardPiece.placePieceToCell(boardCell);
			PiecePlacementInputHandler.getInstance().reportSuccessfulPlacement(boardPiece);
			

			//report a successful turn during actual game
			if(GameStateManager.getInstance().getCurrentState() == GameState.MAIN_GAME) {
				TurnManager.getInstance().reportSuccessfulTurn();
				TurnManager.getInstance().processTurnOver(boardPiece, boardCell);	
			}
		}
		else {
			
			BoardPiece boardPieceOnCell = boardCell.getAssignedBoardPiece();
			
			Log.v(TAG, "Board Piece on cell: " +boardPieceOnCell.getPieceType()+ " is Owned? " +activePlayer.isPieceOwned(boardPieceOnCell));
			
			/*if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_ADHOC) {
				activePlayer = PlayerObserver.getInstance().getPlayerOne(); //since adhoc will always consider the remote as player two
			}*/
			
			//check if piece is owned
			if(activePlayer.isPieceOwned(boardPieceOnCell)) {
				IBoardCell oldBoardCell = boardPiece.getBoardCell();
				//return to original position if there is such
				if(oldBoardCell != null) {
					boardPiece.placePieceToCell(oldBoardCell);
					
				}
				//delete board piece if not
				else {
					PiecePlacementInputHandler.getInstance().reportFailedPlacement(boardPiece);
					boardPiece.destroy();
				}
			}
			else {
				//commence an evaluation of the arbiter
				BoardPiece[] result = Arbiter.getInstance().evaluatePieces(boardPiece, boardPieceOnCell);
				if(result != null && result[Arbiter.WINNING_PIECE_INDEX] != null) {
					result[Arbiter.WINNING_PIECE_INDEX].placePieceToCell(boardCell);
				}
				
				//report a successful turn during actual game
				if(GameStateManager.getInstance().getCurrentState() == GameState.MAIN_GAME || GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_ADHOC) {
					TurnManager.getInstance().reportSuccessfulTurn();
					TurnManager.getInstance().processTurnOver(boardPiece, boardCell);
				}
				
			}
			
		}
		
	}
	
	
	
	public boolean isNewPositionOccupied(BoardCell targetCell) {
		if(targetCell.getAssignedBoardPiece() == null) {
			return false;
		}
		else {
			return true;
		}
		
	}
	
	public boolean validateGamePosition(BoardPiece boardPiece, BoardCell targetCell) {
		boolean flag = false;
		
		IBoardCell oldBoardCell = boardPiece.getBoardCell();
		
		int oldRow = oldBoardCell.getRow();
		int oldColumn = oldBoardCell.getColumn();
		
			//check valid placements
			if(targetCell.getRow() == oldRow) {
				if(targetCell.getColumn() == oldColumn - 1 || targetCell.getColumn() == oldColumn + 1) {
					flag = true;
				}
			}
			else if(targetCell.getColumn() == oldColumn) {
				if(targetCell.getRow() == oldRow - 1 || targetCell.getRow() == oldRow + 1) {
					flag = true;
				}
			}
		
		
		return flag;
	}
	
	public static BoardManager getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new BoardManager();
		}
		
		return sharedInstance;
	}
}
