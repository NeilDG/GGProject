/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.minimax;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.neildg.gamesofthegenerals.entities.board.BoardCell;
import com.neildg.gamesofthegenerals.entities.board.BoardCreator;
import com.neildg.gamesofthegenerals.entities.comparison.Arbiter;
import com.neildg.gamesofthegenerals.entities.comparison.PieceHierarchy;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;
import com.neildg.gamesofthegenerals.entities.stat.Statistics;

/**
 * This represents the node to be used in the game tree. Contains the list of positions of the pieces in the board.
 * @author user
 *
 */
public class BoardState extends TreeNode {

	private final static String TAG = "BoardState";
	
	private ArrayList<Position> playerOnePositions;
	private ArrayList<Position> playerTwoPositions;
	
	private Position movedPosition;
	private Player whoseTurnToMove;
	
	private ArrayList<Double> evalScoreList = new ArrayList<Double>();
	
	private float monteCarloScore = 0.0f;
	private int winCount = 0;
	
	public BoardState() {
		super();
		
		this.playerOnePositions = new ArrayList<Position>();
		this.playerTwoPositions = new ArrayList<Position>();
	}
	
	public void setWinCount(int count) {
		this.winCount = count;
	}
	
	public int getWinCount() {
		return this.winCount;
	}
	
	public void addMonteCarloScore(double score) {
		this.evalScoreList.add(score);
		//Log.v(TAG, "Eval score: " +score);
		//Log.v(TAG, "Added monte carlo score. Size of evalscore: " +this.evalScoreList.size());
	}
	
	private double[] listToArray(List<Double> arr){   
	    double[] result = new double[arr.size()];
	    int i = 0;
	    for(Double d : arr) {
	        result[i++] = d.doubleValue();
	    }
	    return result;
	}
	
	public double getMedianMonteCarlo() {
		Statistics stat = new Statistics(this.listToArray(this.evalScoreList));
		return stat.median();
	}
	
	public double getMeanMonteCarlo() {
		Statistics stat = new Statistics(this.listToArray(this.evalScoreList));
		return stat.getMean();
	}
	
	//assigns the corresponding player who should make the move on this board state
	public void assignWhoseTurnToMove(Player player) {
		this.whoseTurnToMove = player;
	}
	
	public void setMovedPosition(Position position) {
		if(movedPosition != null) {
			Log.v(TAG, "OH NOES! Adding a duplicate moved position! Position conflict: " +position.getPieceID());
		}
		this.movedPosition = position;
	}
	
	public Position getMovedPosition() {
		return this.movedPosition;
	}
	
	//duplicates the positions. This is used for copying of states to a new state
	public void duplicatePositions(BoardState state) {
		
		Player playerOne = PlayerObserver.getInstance().getPlayerOne();
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		
		//duplicate player one positions
		for(int i = 0; i < state.getPositionSize(playerOne); i++) {
			Position copyingPos = state.getPositionAt(playerOne, i);
			Position newPos = new Position(copyingPos.getPieceID(), copyingPos.getPieceType(), copyingPos.getRow(), copyingPos.getColumn(), playerOne);
			this.addPosition(newPos, playerOne);
		}
		
		//duplicate player two positions
		for(int i = 0; i < state.getPositionSize(playerTwo); i++) {
			Position copyingPos = state.getPositionAt(playerTwo, i);
			Position newPos = new Position(copyingPos.getPieceID(), copyingPos.getPieceType(), copyingPos.getRow(), copyingPos.getColumn(), playerTwo);
			this.addPosition(newPos, playerTwo);
		}
	}
	public Player getWhoseTurnToMove() {
		return this.whoseTurnToMove;
	}
	
	public void addPosition(Position position, Player player) {
		this.getPositionListForPlayer(player).add(position);
	}
	
	//replaces a position found in the list.
	public void replacePosition(Position oldPos, int oldPosIndex, Position newPos, Player player) {
		if(!this.getPositionListForPlayer(player).remove(oldPos)) {
			//throw new RuntimeException("Remove unsuccessful! " +oldPos.getPieceID()+ " is not found in position list of " +player.getPlayerName());
		}
		//this.getPositionListForPlayer(player).remove(oldPosIndex);
		this.getPositionListForPlayer(player).add(newPos);
		
		/*if(newPos.getPieceType() == PieceHierarchy.SPY) {
			Log.v(TAG, "Moving SPY to newPos of: " +newPos.getRow() + " " +newPos.getColumn());
		}*/
		
		this.simulateArbiter(newPos, player);
		//this.setMovedPosition(newPos);		
	}
	
	//we simulate the arbiter function. check if there are any overlapping positions and let the arbiter evaluate.
	public void simulateArbiter(Position newPos, Player player) {
		Player playerOne = PlayerObserver.getInstance().getPlayerOne();
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		
		Player defendingPlayer = null;
		if(playerTwo == player) {
			defendingPlayer = playerOne;
		}
		else {
			defendingPlayer = playerTwo;
		}
		
		ArrayList<Position> positionsToCheck = this.getPositionListForPlayer(defendingPlayer);
		Position defeatedPos = null;
		for(Position position : positionsToCheck) {
			//if there's a collision in row and column, then the newPos means it is attacking this position
			if(newPos.getColumn() == position.getColumn() && newPos.getRow() == position.getRow()) {
				Arbiter simulatedArbiter = Arbiter.getInstance();
				defeatedPos = simulatedArbiter.evaluatePosition(newPos, position);
			}
		}
		
		if(defeatedPos != null) {
			this.removePosition(defeatedPos);
		}
	}
	
	
	private ArrayList<Position> getPositionListForPlayer(Player player) {
		if(player == PlayerObserver.getInstance().getPlayerOne()) {
			return this.playerOnePositions;
		}
		else {
			return this.playerTwoPositions;
		}
	}
	
	
	//for now, heuristic is computed by adding the total piece values of player two VS player one.
	//Basic concept is that the higher piece value, means its more favorable for the computer.
	//Lower means favorable for player.
	public void computeHeuristic() {
		/*float playerTwoScore = 0.0f;
		float playerOneScore = 0.0f;
		
		for(Position playerTwoPos : playerTwoPositions) {
			playerTwoScore += playerTwoPos.getPieceValue();
		}
		
		for(Position playerOnePos: playerOnePositions) {
			playerOneScore += playerOnePos.getPieceValue();
		}
		
		this.heuristicScore = playerTwoScore - playerOneScore;*/
		
		BoardEvaluator boardEvaluator = new BoardEvaluator(this);
		this.heuristicScore = boardEvaluator.evaluate();
	}
	
	public int getPositionSize(Player player) {
		return this.getPositionListForPlayer(player).size();
	}
	
	public Position getPositionAt(Player player, int index) {
		return this.getPositionListForPlayer(player).get(index);
	}
	
	private void removePosition(Position position) {
		ArrayList<Position> playerOnePositions = this.getPositionListForPlayer(PlayerObserver.getInstance().getPlayerOne());
		ArrayList<Position> playerTwoPositions = this.getPositionListForPlayer(PlayerObserver.getInstance().getPlayerTwo());
		
		if(playerOnePositions.contains(position)) {
			if(playerOnePositions.remove(position)) {
				//Log.v(TAG, "Removal of piece " +position.getPieceType()+" from player one successful");
			}
			else {
				throw new RuntimeException("Removal of position " +position.getPieceType()+ " is unsuccessful! Not in list!");
			}
		}
		else if(playerTwoPositions.contains(position)) {
			if(playerTwoPositions.remove(position)) {
				//Log.v(TAG, "Removal of position " +position.getPieceType()+" from player two successful");
			}
			else {
				throw new RuntimeException("Removal of position " +position.getPieceType()+ " is unsuccessful! Not in list!");
			}
		}
		else {
			throw new RuntimeException("There is no position " +position.getPieceType()+ " found in any of the player lists!");
		}
		
	}
	
	//returns the position specified by row and column, null otherwise
	public Position getPositionAtPlace(int row, int column) {
		
		if(!BoardCreator.isWithinBounds(row, column)) {
			return null;
		}
		
		for(Position playerTwoPos : this.playerTwoPositions) {
			if(playerTwoPos.getRow() == row && playerTwoPos.getColumn() == column) {
				return playerTwoPos;
			}
		}
		
		for(Position playerOnePos : this.playerOnePositions) {
			if(playerOnePos.getRow() == row && playerOnePos.getColumn() == column) {
				return playerOnePos;
			}
		}
		
		return null;
	}
	
	//returns the position specified by piece type
	public Position getPositionOfPieceType(int pieceType, Player player) {
		for(Position position : this.getPositionListForPlayer(player)) {
			if(position.getPieceType() == pieceType) {
				return position;
			}
		}
		
		Player playerOne = PlayerObserver.getInstance().getPlayerOne();
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		

		Log.e(TAG, "Piece not found owned by " +player.getPlayerName()+ "! Piece type " +pieceType+ " might have been eaten already?");
		return null;
	}
	
	public void printDebugValues() {
		Log.d(TAG, "Depth: " +this.getDepth() + " Heuristic score: " +this.heuristicScore);
		
		for(Position playerOnePos : this.playerOnePositions) {
			playerOnePos.printDebugValues();
		}
		
		for(Position playerTwoPos : this.playerTwoPositions) {
			playerTwoPos.printDebugValues();
		}
	}
	
}
