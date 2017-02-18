/**
 * 
 */
package com.neildg.gamesofthegenerals.input;

/**
 * Behavior to implement if a piece was dragged outside the board during piece placement
 * @author user
 *
 */
public interface IPieceDragOutside {

	public abstract void onPieceDragOutside(int pieceType);
}
