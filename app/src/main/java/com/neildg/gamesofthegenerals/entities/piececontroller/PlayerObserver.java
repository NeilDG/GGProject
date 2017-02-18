/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.piececontroller;

import java.util.Random;

import com.neildg.gamesofthegenerals.entities.board.BoardPiece;

/**
 * Holds reference to both players for the game.
 * @author user
 *
 */
public class PlayerObserver {

	private static PlayerObserver sharedInstance = null;
	
	private Player activePlayer;
	private Player computerPlayer;
	
	private Player playerOne;
	private Player playerTwo;
	
	private Player winner;
	
	private PlayerObserver() {
		this.playerOne = new Player("Player", Player.PLAYER_ONE_ID);
		this.playerTwo = new Player("Computer", Player.PLAYER_TWO_ID);
		
		this.setActivePlayer(this.playerOne);
	}
	
	
	public void setWinner(Player player) {
		this.winner = player;
	}
	
	public Player getWinner() {
		return this.winner;
	}
	
	public Player getPlayerOne() {
		return this.playerOne;
	}
	
	public Player getPlayerTwo() {
		return this.playerTwo;
	}
	
	//returns the corresponding owner of the board piece
	public Player getPlayerOwner(BoardPiece boardPiece) {
		if(this.playerOne.isPieceOwned(boardPiece)) {
			return this.playerOne;
		}
		else {
			return this.playerTwo;
		}
	}
	
	public void setActivePlayer(Player player) {
		this.activePlayer = player;
		
	}
	
	public Player getActivePlayer() {
		return this.activePlayer;
	}
	
	public Player getInactivePlayer() {
		if(this.activePlayer != this.playerOne) {
			return playerOne;
		}
		else {
			return playerTwo;
		}
	}
	
	public void reset() {
		this.setActivePlayer(playerOne); //player one always goes first
		this.playerOne.clearAllPieces();
		this.playerTwo.clearAllPieces();
		this.winner = null;
	}
	
	public static PlayerObserver getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new PlayerObserver();
		}
		return sharedInstance;
	}
}
