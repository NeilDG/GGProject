/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.board;

/**
 * Represents the generic board cell functions to be used by BoardPiece
 * @author user
 *
 */
public interface IBoardCell {

	public static float CELL_WIDTH = 100;
	public static float CELL_HEIGHT = 75;
	
	public static float BB_WIDTH = 15;
	public static float BB_HEIGHT = 15;
	
	public abstract void snapPieceToCell();
	public abstract void assignBoardPiece(BoardPiece boardPiece);
	public abstract void removeAssignedBoardPiece();
	public abstract BoardPiece getAssignedBoardPiece();
	
	public abstract int getRow();
	public abstract int getColumn();
}
