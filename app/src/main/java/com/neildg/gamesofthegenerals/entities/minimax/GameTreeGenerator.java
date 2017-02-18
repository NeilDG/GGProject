/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.minimax;

import java.util.ArrayList;

import android.util.Log;

import com.neildg.gamesofthegenerals.entities.board.BoardCell;
import com.neildg.gamesofthegenerals.entities.board.BoardCreator;
import com.neildg.gamesofthegenerals.entities.board.BoardManager;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.board.IBoardCell;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

/**
 * Singleton instance of the game tree generator. Has the current game tree after the player move
 * @author user
 *
 */
public class GameTreeGenerator {

	private static String TAG = "GameTreeGenerator";
	private static GameTreeGenerator sharedInstance = null;
	
	public static int PLY_DEPTH = 3;	//we use a fixed ply depth for basic minimax
	
	private final int MIN = 0;
	private final int MAX = 1;
	
	private int currentPly;
	private BoardPiece lastMovedPiece;
	
	private GameTree currentGameTree;
	
	
	public void setLastMovedPiece(BoardPiece lastMovedPiece) {
		this.lastMovedPiece = lastMovedPiece;
	}
	
	//generates the game tree that requires the last moved piece by the player
	public void generateRootNode() {
		
		this.currentGameTree = null;
		this.currentPly = 0;
		
		BoardState rootState = this.createInitialBoardState();
		//Position rootPosition = new Position(this.lastMovedPiece, PlayerObserver.getInstance().getPlayerOne());
		this.currentGameTree = new GameTree(rootState);
		
	}
	
	//generates the branches starting from the root node
	public void generateBranches(BoardState currentState) {
		
		if(currentState.getDepth() == PLY_DEPTH) {
			Log.v(TAG, "Finished generating brances at specified ply depth");
			return;
		}
		else {
			
			Player player = this.getCorrespondingPlayer(currentState.getDepth());
			
			this.expandBoardState(currentState, player);
			
			for(int i = 0; i < currentState.getChildCount(); i++) {
				BoardState expandedState = (BoardState) currentState.getChild(i);
				this.generateBranches(expandedState);
			}
		}
	}
	
	
	public BoardState alphaBeta(BoardState boardState, int depth, float alpha, float beta) {
		if(depth == GameTreeGenerator.PLY_DEPTH) {
			//return evaluation of board and best move
			//Log.v(TAG, "Finished generating brances at specified ply depth " +depth);
			boardState.computeHeuristic();
			return boardState;
		}
		else {
			BoardState bestState = null;
			float bestScore = Float.NEGATIVE_INFINITY;
			Player player = this.getCorrespondingPlayer(depth);
			
			for(BoardState possibleStates : this.exploreNextMoves(boardState, player)) {
				//Log.v(TAG, "Possible states: " +possibleStates.getWhoseTurnToMove());
				BoardState expandedState = this.alphaBeta(possibleStates, depth + 1, -beta, -returnMax(alpha, bestScore));
				float score = expandedState.getHeuristic();
				score = -score;
				Log.v(TAG, "Expanded State score: " +score+ " depth: " +depth+ " alpha: " +alpha+ " beta: " +beta);
				if(score > bestScore) {
					bestScore = score;
					bestState = expandedState;
					
					if(bestScore >= beta) {
						//prune
						Log.v(TAG, "Pruning at depth " +depth);
						bestState.setHeuristicScore(bestScore);
						return bestState;
					}
				}
			}
			bestState.setHeuristicScore(bestScore);
			return bestState;
		}
		
			
	}
	
	public float alphaBetaFloat(BoardState boardState, int depth, float alpha, float beta) {
		if(depth == GameTreeGenerator.PLY_DEPTH) {
			return boardState.getHeuristic();
		}
		
		else {
			float bestScore = Float.NEGATIVE_INFINITY;
			Player player = this.getCorrespondingPlayer(depth);
			ArrayList<BoardState> possibleStates = this.exploreNextMoves(boardState, player);
			
			for(BoardState expandedState: possibleStates) {
				float value = -this.alphaBetaFloat(expandedState, depth + 1, -beta, -alpha);
				bestScore = this.returnMax(bestScore, value);
				Log.v(TAG,"Value: " +value);
				float newAlpha = this.returnMax(alpha, value);
				if(newAlpha >= beta) {
					break;
				}
			}
			
			return bestScore;
		}
	}
	
	public float returnMax(float alpha, float bestScore) {
		if(alpha >= bestScore) {
			return alpha;
		}
		else {
			return bestScore;
		}
	}

	private BoardState createInitialBoardState() {
		BoardState initialBoardState = new BoardState();
		
		Player playerOne = PlayerObserver.getInstance().getPlayerOne();
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		
		//populate current positions of player one pieces
		for(int i = 0; i < playerOne.getAlivePiecesCount(); i++) {
			BoardPiece boardPiece = playerOne.getAlivePieceAt(i);
			Position playerOnePos = this.translateBoardPieceToPosition(boardPiece, playerOne);
			initialBoardState.addPosition(playerOnePos, playerOne);
		}
		
		//player two pieces
		for(int i = 0; i < playerTwo.getAlivePiecesCount(); i++) {
			BoardPiece boardPiece = playerTwo.getAlivePieceAt(i);
			Position playerTwoPos = this.translateBoardPieceToPosition(boardPiece, playerTwo);
			initialBoardState.addPosition(playerTwoPos, playerTwo);
		}
		
		return initialBoardState;
	}
	
	//translates a board piece instance into a position type
	private Position translateBoardPieceToPosition(BoardPiece boardPiece, Player owningPlayer) {
		Position position = new Position(boardPiece, owningPlayer);
		return position;
	}
	
	private Player getCorrespondingPlayer(int depth) {
		Player player;
		//min
		if(depth % 2 != 0) {
			//Log.v(TAG, "MIN depth " +depth);
			player = PlayerObserver.getInstance().getPlayerOne();
		}
		//max
		else {
			//Log.v(TAG, "MAX depth " +depth);
			player = PlayerObserver.getInstance().getPlayerTwo();
		}
		
		return player;
	}
	
	public void debugDepthFirstSearch(BoardState currentState) {
		currentState.markAsDiscovered();
		
		for(int i = 0; i < currentState.getChildCount(); i++) {
			BoardState expandedState = (BoardState) currentState.getChild(i);
			
			if(!expandedState.isDiscovered()) {
				expandedState.printDebugValues();
				this.debugDepthFirstSearch(expandedState);
			}
		}
	}
	
	//expands the possible positions for the assigned player
	/*public void expandPosition(Position position, Player player) {
		
		for(int i = 0; i < player.getAlivePiecesCount(); i++) {
			BoardPiece alivePiece = player.getAlivePieceAt(i);
			IBoardCell boardCell = alivePiece.getBoardCell();
			
			//check the following moves
			int pieceRow = boardCell.getRow();
			int pieceColumn = boardCell.getColumn();
			
			if(checkMoves(pieceRow + 1, pieceColumn)) {
				Position expandedPos = new Position(alivePiece.getPieceType(), pieceRow + 1, pieceColumn, player);
				position.addChild(expandedPos);
			}
			
			if(checkMoves(pieceRow - 1, pieceColumn)) {
				Position expandedPos = new Position(alivePiece.getPieceType(), pieceRow - 1, pieceColumn, player);
				position.addChild(expandedPos);
			}
			
			if(checkMoves(pieceRow, pieceColumn + 1)) {
				Position expandedPos = new Position(alivePiece.getPieceType(), pieceRow, pieceColumn + 1, player);
				position.addChild(expandedPos);
			}
			
			if(checkMoves(pieceRow, pieceColumn - 1)) {
				Position expandedPos = new Position(alivePiece.getPieceType(), pieceRow, pieceColumn - 1, player);
				position.addChild(expandedPos);
			}
			
		}
	}*/
	
	public void expandBoardState(BoardState boardState, Player player) {
		
		//find possible moves for the current player's turn
		for(int i = 0; i < boardState.getPositionSize(player); i++) {
			Position position = boardState.getPositionAt(player, i);
			
			int row = position.getRow();
			int column = position.getColumn();
			BoardState expandedBoardState;
			if(checkMoves(row + 1, column, player)) {
				expandedBoardState = this.generateNewChildStateForMove(boardState, position, i, position.getPieceID(), position.getPieceType(), row + 1, column, player);
				boardState.addChild(expandedBoardState);
			}
			
			if(checkMoves(row - 1, column, player)) {
				expandedBoardState = this.generateNewChildStateForMove(boardState, position, i, position.getPieceID(), position.getPieceType(), row - 1, column, player);
				boardState.addChild(expandedBoardState);
			}
			
			if(checkMoves(row, column + 1, player)) {
				expandedBoardState = this.generateNewChildStateForMove(boardState, position, i, position.getPieceID(), position.getPieceType(), row, column + 1, player);
				boardState.addChild(expandedBoardState);
			}
			
			if(checkMoves(row, column - 1, player)) {
				expandedBoardState = this.generateNewChildStateForMove(boardState, position, i, position.getPieceID(), position.getPieceType(), row, column - 1, player);
				boardState.addChild(expandedBoardState);
			}
			
		}
	}
	
	//generates a new board state for the possible move towards a new position
	private BoardState generateNewChildStateForMove(BoardState parentState, Position oldPos, int posIndex, int pieceID, int pieceTypeToMove, int newRow, int newColumn, Player player) {
		
		//create expanded board state copy
		BoardState expandedBoardState = new BoardState();
		expandedBoardState.assignWhoseTurnToMove(player);
		expandedBoardState.duplicatePositions(parentState);
		
		//Position newPos = expandedBoardState.getPositionAtPlace(oldPos.getRow(), oldPos.getColumn());
		//newPos.setNewPosition(newRow, newColumn);
		
		Position newPos = new Position(pieceID, pieceTypeToMove, newRow, newColumn, player);
		expandedBoardState.replacePosition(oldPos, posIndex, newPos, player);
		//expandedBoardState.computeHeuristic();
		
		return expandedBoardState;
		//add this new state as child
		//parentState.addChild(expandedBoardState);
	}
	
	public ArrayList<BoardState> exploreNextMoves(BoardState boardState, Player player) {
		ArrayList<BoardState> possibleStates = new ArrayList<BoardState>();
		
		//find possible moves for the current player's turn
		for(int i = 0; i < boardState.getPositionSize(player); i++) {
			Position position = boardState.getPositionAt(player, i);
			
			int row = position.getRow();
			int column = position.getColumn();
			
			BoardState expandedBoardState;
			
			if(checkMoves(row + 1, column, player)) {
				expandedBoardState = this.generateNewChildStateForMove(boardState, position, i, position.getPieceID(), position.getPieceType(), row + 1, column, player);
				possibleStates.add(expandedBoardState);
			}
			
			if(checkMoves(row - 1, column, player)) {
				expandedBoardState = this.generateNewChildStateForMove(boardState, position, i, position.getPieceID(), position.getPieceType(), row - 1, column, player);
				possibleStates.add(expandedBoardState);
			}
			
			if(checkMoves(row, column + 1, player)) {
				expandedBoardState = this.generateNewChildStateForMove(boardState, position, i, position.getPieceID(), position.getPieceType(), row, column + 1, player);
				possibleStates.add(expandedBoardState);
			}
			
			if(checkMoves(row, column - 1, player)) {
				expandedBoardState = this.generateNewChildStateForMove(boardState, position, i, position.getPieceID(), position.getPieceType(), row, column - 1, player);
				possibleStates.add(expandedBoardState);
			}
		}
		
		return possibleStates;
	}
	
	
	private boolean checkMoves(int row, int column, Player player) {
		boolean result = false;
		
		if((row >= BoardCreator.BOARD_ROWS || column >= BoardCreator.BOARD_COLUMNS) || (row < 0 || column < 0)) {
			return result;
		}
		
		PlayerObserver playerObs = PlayerObserver.getInstance();
		Player computer = playerObs.getPlayerTwo();
		BoardCreator boardCreator = BoardManager.getInstance().getBoardCreator();
		
		IBoardCell targetBoardCell = boardCreator.getBoardAt(row, column);
		BoardPiece targetBoardPiece = targetBoardCell.getAssignedBoardPiece();
		
		boolean computersTurn = false;
		
		//determine if it's computer's turn
		if(player == computer) {
			computersTurn = true;
		}
		
		if(targetBoardPiece != null) {
			//check if the piece belongs to the enemy player
			if(computersTurn && PlayerObserver.getInstance().getPlayerOwner(targetBoardPiece) != computer) {
				//valid move, computer can possibly eat enemy piece
				result = true;
			}
			//if its player's turn, check if board piece is owned by computer
			else if(!computersTurn && PlayerObserver.getInstance().getPlayerOwner(targetBoardPiece) != player) {
				result = true;
			}
		}
		else {
			//empty cell, computer piece can move to specified row and column
			result = true;
		}
		
		return result;
	}
	
	
	public GameTree getCurrentGameTree() {
		return this.currentGameTree;
	}
	
	/*public static GameTreeGenerator getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new GameTreeGenerator();
		}
		
		return sharedInstance;
	}*/
}
