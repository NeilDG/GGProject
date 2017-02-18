/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.game;

/**
 * Responsible for managing the game state.
 * @author user
 *
 */
public class GameStateManager {

	private static GameStateManager sharedInstance = null;
	
	public enum GameState {
		PIECE_PLACEMENT,
		AWAITING_MAIN_GAME,
		MAIN_GAME,
		RESULTS
	}
	
	public enum GameMode {
		VERSUS_COMPUTER,
		VERSUS_HUMAN_LOCAL,
		VERSUS_HUMAN_ADHOC,
		VERSUS_HUMAN_ONLINE
	}
	
	private GameState gameState;
	private GameMode gameMode;
	
	private GameStateManager() {
		this.gameState = GameState.PIECE_PLACEMENT;
	}
	
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}
	
	public GameMode getGameMode() {
		return this.gameMode;
	}
	
	public void setCurrentState(GameState gameState) {
		this.gameState = gameState;
	}
	
	public void reportNewGame() {
		this.gameState = GameState.PIECE_PLACEMENT;
	}
	
	//transition to the main game state
	public void reportPiecePlacementDone() {
		this.gameState = GameState.MAIN_GAME;
	}
	
	//reports game over and proceeds to the results game state
	public void reportGameOver() {
		this.gameState = GameState.RESULTS;
		
	}
	
	public GameState getCurrentState() {
		return this.gameState;
	}
	
	public static GameStateManager getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new GameStateManager();
		}
		
		return sharedInstance;
	}
	
}
