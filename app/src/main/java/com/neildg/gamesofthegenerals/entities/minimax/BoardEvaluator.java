/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.minimax;

import android.util.Log;

import com.neildg.gamesofthegenerals.entities.board.BoardCreator;
import com.neildg.gamesofthegenerals.entities.comparison.PieceHierarchy;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

/**
 * 
 * Computes the score of the given boardstate
 * @author user
 *
 */
public class BoardEvaluator {

	private final static String TAG = "BoardEvaluator";
	private BoardState boardState;
	
	public static float DEFENSE_DEDUCTION = 2.0f; //score deduction constant multiplier for defensiveness. Higher means more stricter in positioning of piece. 
												  //Lower means that the computer will be more vulnerable to being eaten.
	public static float OPENNESS_VALUE = 5.0f;  //openness value constant to determine if the board state results into many more possible moves.
	public static float WIN_LOSS_VALUE = 99999.0f;
	
	public BoardEvaluator(BoardState boardState) {
		this.boardState = boardState;
	}
	
	//evaluates the board state and returns the corresponding score
	public float evaluate() {
		
		Player computer = PlayerObserver.getInstance().getPlayerTwo();
		Player human = PlayerObserver.getInstance().getPlayerOne();
		
		//compute computer's board score
		float computerScore = 0.0f;
		for(int  i = 0; i < this.boardState.getPositionSize(computer); i++) {
			Position position = this.boardState.getPositionAt(computer, i);
			float positionScore = /*this.computeOffensiveness(position, human) +*/ this.computeOpenness(position, human) - this.computeDefensiveness(position, human);
			computerScore += positionScore;
		}
		
		if(this.isFlagAtRisk(computer, human)) {
			computerScore = -WIN_LOSS_VALUE;
			Log.v(TAG, "Computer Flag at risk!");
			
		}
		//computerScore -= /*this.evaluateFlag(computer, human) + this.evaluatePossibleFlagForward(computer, human) + */this.evaluateFlagRisk(computer, human);
		
		//compute human's board score
		float humanScore = 0.0f;
//		for(int i = 0; i < this.boardState.getPositionSize(human); i++) {
//			Position position = this.boardState.getPositionAt(human, i);
//			float positionScore = /*this.computeOffensiveness(position, computer) +*/ this.computeOpenness(position, human) - this.computeDefensiveness(position, computer);
//			humanScore += positionScore;
//		}
//		
//		if(this.isFlagAtRisk(human, computer)) {
//			humanScore = -WIN_LOSS_VALUE;
//			Log.v(TAG, "Human Flag at risk!");
//		}
		//humanScore -= /*this.evaluateFlag(human, computer) + this.evaluatePossibleFlagForward(computer, human) +*/ this.evaluateFlagRisk(computer, human);
		
		return (computerScore - humanScore);
	}
	
	
	//returns the defensiveness score of the position based on the formula. The lower the number, then it means that the piece is prone to be eaten.
	private float computeDefensiveness(Position computingPos, Player opposingPlayer) {
		
		int numAdjacentPieces = 0;
		
		//check if bottom has an enemy piece
		Position positionAtBottom = this.boardState.getPositionAtPlace(computingPos.getRow() - 1, computingPos.getColumn());
		if(positionAtBottom != null && positionAtBottom.getOwnedPlayer() == opposingPlayer) {
			numAdjacentPieces++;
		}
		
		//check if top has an enemy piece
		Position positionAtTop = this.boardState.getPositionAtPlace(computingPos.getRow() + 1, computingPos.getColumn());
		if(positionAtTop != null && positionAtTop.getOwnedPlayer() == opposingPlayer) {
			numAdjacentPieces++;
		}
		
		//check if right has an enemy piece
		Position positionAtRight = this.boardState.getPositionAtPlace(computingPos.getRow(), computingPos.getColumn() + 1);
		if(positionAtRight != null && positionAtRight.getOwnedPlayer() == opposingPlayer) {
			numAdjacentPieces++;
		}
		
		//check if left has an enemy piece
		Position positionAtLeft = this.boardState.getPositionAtPlace(computingPos.getRow(), computingPos.getColumn() - 1);
		if(positionAtLeft != null && positionAtLeft.getOwnedPlayer() == opposingPlayer) {
			numAdjacentPieces++;
		}
		
		float defensiveScore = /*computingPos.getPieceValue() - */(DEFENSE_DEDUCTION * numAdjacentPieces);
		
		return defensiveScore;
	}
	
	private float computeOpenness(Position computingPos, Player opposingPlayer) {
		
		int emptyPlaces = 0;
		
		//check if bottom has an enemy piece
		Position positionAtBottom = this.boardState.getPositionAtPlace(computingPos.getRow() - 1, computingPos.getColumn());
		if(positionAtBottom == null) {
			emptyPlaces++;
		}
		
		//check if top has an enemy piece
		Position positionAtTop = this.boardState.getPositionAtPlace(computingPos.getRow() + 1, computingPos.getColumn());
		if(positionAtTop == null) {
			emptyPlaces++;
		}
		
		//check if right has an enemy piece
		Position positionAtRight = this.boardState.getPositionAtPlace(computingPos.getRow(), computingPos.getColumn() + 1);
		if(positionAtRight == null) {
			emptyPlaces++;
		}
		
		//check if left has an enemy piece
		Position positionAtLeft = this.boardState.getPositionAtPlace(computingPos.getRow(), computingPos.getColumn() - 1);
		if(positionAtLeft == null) {
			emptyPlaces++;
		}
		
		float opennessScore = emptyPlaces * OPENNESS_VALUE;
		return opennessScore;
	}
	
	//returns the offensiveness score of the position based on the formula
	private float computeOffensiveness(Position computingPos, Player opposingPlayer) {
		
		float numPiecesEliminate = PieceHierarchy.getNumPiecesCanEliminate(computingPos.getPieceType()) * 1.0f;
		float remainingPieces = opposingPlayer.getAlivePiecesCount() * 1.0f;
		
		
		
		int rowValue = 0;
		if(computingPos.getOwnedPlayer() == PlayerObserver.getInstance().getPlayerTwo()) {
			rowValue = computingPos.getRow();
		}
		else {
			rowValue = BoardCreator.BOARD_ROWS - computingPos.getRow(); //inverse for human.
		}
		
		//if the position is closer to the other side of the player's board, then it is a more favorable position
		float threatMultiplier = rowValue * 0.5f;
		
		float offensiveScore = /*computingPos.getPieceValue() +*/ threatMultiplier;
		
		return offensiveScore;
	}
	
	private boolean isFlagAtRisk(Player player, Player opposingPlayer) {
		Position flagPos = this.boardState.getPositionOfPieceType(PieceHierarchy.FLAG, player);
		
		if(flagPos == null) {
			return true;
		}
		
		float initialDefenseScore = this.computeDefensiveness(flagPos, opposingPlayer);
		
		if(initialDefenseScore > 0.0f) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//evaluates if a player's flag is prone to being eaten
	private float evaluateFlagRisk(Player player, Player opposingPlayer) {
		Position flagPos = this.boardState.getPositionOfPieceType(PieceHierarchy.FLAG, player);
		
		if(flagPos == null) {
			return -WIN_LOSS_VALUE;
		}
		
		float initialDefenseScore = this.computeDefensiveness(flagPos, opposingPlayer);
		initialDefenseScore = (initialDefenseScore * WIN_LOSS_VALUE);
		Log.v(TAG, "Flag defense score:" +initialDefenseScore);
		return initialDefenseScore;
	}
	
	//evalutes the effectiveness if a flag can be moved forward without putting it in danger
	private float evaluatePossibleFlagForward(Player player, Player opposingPlayer) {
		float resultScore = 0.0f;
		
		Position flagPos = this.boardState.getPositionOfPieceType(PieceHierarchy.FLAG, player);
		
		if(flagPos != null) {
			//reference only
			Position positionAhead = new Position(flagPos.getPieceID(), PieceHierarchy.FLAG, flagPos.getRow() + 2, flagPos.getColumn(), player);
			float positionAheadDefenseScore = this.computeDefensiveness(positionAhead, opposingPlayer);
			
			resultScore = WIN_LOSS_VALUE - (WIN_LOSS_VALUE * positionAheadDefenseScore);
		}
		
		return resultScore;
	}
	
	//evaluates if the flag can result in a win
	private float evaluateFlag(Player player, Player opposingPlayer) {
		Position flagPos = this.boardState.getPositionOfPieceType(PieceHierarchy.FLAG, player);
		
		//if flag is missing, then this board state has resulted in a loss, return -99999
		if(flagPos == null) {
			return -WIN_LOSS_VALUE;
		}
		//if flag has not yet reached the end of row, we return 0.
		if(flagPos.getRow() != BoardCreator.BOARD_ROWS - 1) {
			return 0;
		}
		
		//if flag is already at the end of the row
		boolean hasEnemyPiece = false;
		
		//check if bottom has an enemy piece
		Position positionAtBottom = this.boardState.getPositionAtPlace(flagPos.getRow() - 1, flagPos.getColumn());
		if(positionAtBottom != null && positionAtBottom.getOwnedPlayer() == opposingPlayer) {
			hasEnemyPiece = true;
		}
		
		//check if top has an enemy piece
		Position positionAtTop = this.boardState.getPositionAtPlace(flagPos.getRow() + 1, flagPos.getColumn());
		if(positionAtTop != null && positionAtTop.getOwnedPlayer() == opposingPlayer) {
			hasEnemyPiece = true;
		}
		
		//check if right has an enemy piece
		Position positionAtRight = this.boardState.getPositionAtPlace(flagPos.getRow(), flagPos.getColumn() + 1);
		if(positionAtRight != null && positionAtRight.getOwnedPlayer() == opposingPlayer) {
			hasEnemyPiece = true;
		}
		
		//check if left has an enemy piece
		Position positionAtLeft = this.boardState.getPositionAtPlace(flagPos.getRow(), flagPos.getColumn() - 1);
		if(positionAtLeft != null && positionAtLeft.getOwnedPlayer() == opposingPlayer) {
			hasEnemyPiece = true;
		}
		
		if(hasEnemyPiece) {
			return -WIN_LOSS_VALUE;
		}
		else {
			return WIN_LOSS_VALUE;
		}
	}
}
