/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.minimax;

/**
 * Represents the game tree to be used by the minimax algorithm. Contains the root node
 * @author user
 *
 */
public class GameTree {
	
	private final static String TAG = "GameTree";
	
	private BoardState root; //this would be the last position made by the player.
	
	public GameTree(BoardState root) {
		this.root = root;
	}
	
	public void addChildToRoot(BoardState boardState) {
		this.root.addChild(boardState);
		boardState.setParent(this.root);
	}
	
	
	public BoardState getRoot() {
		return this.root;
	}
}
