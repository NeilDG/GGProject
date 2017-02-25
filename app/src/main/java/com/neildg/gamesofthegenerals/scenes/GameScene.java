/**
 * 
 */
package com.neildg.gamesofthegenerals.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSCounter;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.Color;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;

import com.neildg.gamesofthegenerals.atlases.MilitarySymbolsAtlas;
import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.ResolutionManager;
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.board.BoardCreator;
import com.neildg.gamesofthegenerals.entities.board.BoardManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.game.TurnManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameMode;
import com.neildg.gamesofthegenerals.entities.minimax.BoardState;
import com.neildg.gamesofthegenerals.entities.minimax.MonteCarloSearch;
import com.neildg.gamesofthegenerals.entities.multiplayer.SocketManager;
import com.neildg.gamesofthegenerals.entities.openings.OpeningMovesLibrary;
import com.neildg.gamesofthegenerals.entities.openings.OpeningMovesSaver;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;
import com.neildg.gamesofthegenerals.layout.killedpieces.KilledPieceDisplay;
import com.neildg.gamesofthegenerals.layout.prompts.ChangeTurnUI;
import com.neildg.gamesofthegenerals.layout.prompts.ConfirmListener;
import com.neildg.gamesofthegenerals.layout.prompts.GameResultUI;
import com.neildg.gamesofthegenerals.layout.prompts.GenericPrompt;
import com.neildg.gamesofthegenerals.layout.prompts.WaitingTurnUI;

/**
 * Refers to the actual game scene
 * @author user
 *
 */
public class GameScene extends AbstractScene implements ConfirmListener  {

	private BoardCreator boardCreator;
	
	private KilledPieceDisplay playerOneKilledPieceDisplay;
	private KilledPieceDisplay playerTwoKilledPieceDisplay;
	
	private GameResultUI gameResults;
	private ChangeTurnUI changeTurnUI;
	private WaitingTurnUI waitTurnUI;
	
	private GenericPrompt disconnectPrompt;
	
	public GameScene() {
		super();
	}
	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed)
	{
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	
	@Override
	public void loadSceneAssets() {
		
		this.boardCreator = new BoardCreator(this);
		this.boardCreator.populateBoard();
		this.boardCreator.populatePieces(PlayerObserver.getInstance().getPlayerOne());
		
		if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_LOCAL) {
			this.boardCreator.populatePieces(PlayerObserver.getInstance().getPlayerTwo());
			TurnManager.getInstance().hideOpponentPieces();
		}
		else if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_ADHOC) {
			this.boardCreator.populateAdHocPieces();
			TurnManager.getInstance().hideOpponentPieces();
		}
		else if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_COMPUTER){
			this.boardCreator.populateEnemyPieces();
			TurnManager.getInstance().hideOpponentPieces();
		}
		else if(GameStateManager.getInstance().getGameMode() == GameMode.COMPUTER_VERSUS_COMPUTER) {

			//QQQQQ just load a random board state
			BoardState boardState = OpeningMovesLibrary.getInstance().getRandomBoardState();
			this.boardCreator.populatePlayerOnePiece(boardState);
			OpeningMovesSaver openingMovesSaver = new OpeningMovesSaver();
			openingMovesSaver.saveBoardLayoutToExternal();
			//end qqqq

			this.boardCreator.populateEnemyPieces();
			TurnManager.getInstance().hideOpponentPieces();

			//monte carlo search
			Activity activity = (Activity) EngineCore.getInstance().getContext();
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MonteCarloSearch mcTask = new MonteCarloSearch();
					mcTask.setAssignedPlayer(PlayerObserver.getInstance().getActivePlayer());
					mcTask.execute();
				}
			});
		}
		
		this.boardCreator.setBoardPosition(ResolutionManager.SCENE_WIDTH * 0.148f, ResolutionManager.SCENE_HEIGHT * 0.14f);
		
		
		this.createFPSDisplay();
		
		//test change turn UI
		this.changeTurnUI = new ChangeTurnUI(this);
		this.changeTurnUI.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.5f);
		this.changeTurnUI.setConfirmListener(this);
		this.attachChild(this.changeTurnUI);
		
		//create killed piece UI for player's side
		this.playerOneKilledPieceDisplay = new KilledPieceDisplay();
		this.playerOneKilledPieceDisplay.setPosition(0, 0);
		this.playerOneKilledPieceDisplay.setNotificationString(Notifications.ON_KILLED_PLAYER_ONE_PIECE);
		
		this.playerTwoKilledPieceDisplay = new KilledPieceDisplay();
		this.playerTwoKilledPieceDisplay.setPosition(ResolutionManager.SCENE_WIDTH - KilledPieceDisplay.UI_WIDTH, 0);
		this.playerTwoKilledPieceDisplay.setNotificationString(Notifications.ON_KILLED_PLAYER_TWO_PIECE);
		
		this.attachChild(this.playerOneKilledPieceDisplay);
		this.attachChild(this.playerTwoKilledPieceDisplay);
		
		//waiting turn UI
		this.waitTurnUI = new WaitingTurnUI(this);
		this.waitTurnUI.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.07f);
		this.attachChild(this.waitTurnUI);
		
		TurnManager.getInstance().setFirstPlayerForBluetooth();
		
		//test of results screen
		this.gameResults = new GameResultUI(this);
		this.gameResults.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.5f);
		this.attachChild(this.gameResults);
		
		//disconnect prompt that will show if the remote device has disconnected
		this.disconnectPrompt = new GenericPrompt(this);
		this.disconnectPrompt.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.5f);
		this.disconnectPrompt.setDisplayText("Your opponent has disconnected from the game. Please return to the main menu to play again.");
		this.disconnectPrompt.setPositiveButtonText("Main Menu");
		this.disconnectPrompt.hideNegativeButton();
		
		this.attachChild(this.disconnectPrompt);
		
		//disconnect confirm listener
		ConfirmListener confirmListener = new ConfirmListener() {
			@Override
			public void onConfirm() {
				SceneManager.getInstance().loadScene(SceneList.MAIN_MENU_SCENE);
			}
		};
		
		this.disconnectPrompt.setConfirmListener(confirmListener, confirmListener);
		NotificationCenter.getInstance().addObserver(Notifications.ON_BLUETOOTH_DISCONNECT, this.disconnectPrompt);
			
	}

	@Override
	public void destroyScene() {
		Engine engine = EngineCore.getInstance().getEngine();
		
		engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	GameScene.this.boardCreator.destroy();
            	GameScene.this.detachChildren();
            	GameScene.this.detachSelf();
            }
        });
		
		//reset game
		PlayerObserver.getInstance().reset();
		TurnManager.getInstance().reset();
		NotificationCenter.getInstance().clearObservers();
	}

	//turn over display confirm
	@Override
	public void onConfirm() {
		BoardManager.getInstance().getBoardCreator().showBoardContainer();
		TurnManager.getInstance().hideOpponentPieces();
		NotificationCenter.getInstance().postNotification(Notifications.ON_BOARD_PIECE_SNAPPED, this);
	}
	
}
