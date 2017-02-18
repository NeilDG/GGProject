/**
 * 
 */
package com.neildg.gamesofthegenerals.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.util.GLState;

import android.annotation.SuppressLint;
import android.opengl.GLES20;

import com.neildg.gamesofthegenerals.core.ResolutionManager;
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.board.BoardCreator;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameState;
import com.neildg.gamesofthegenerals.input.PiecePlacementInputHandler;
import com.neildg.gamesofthegenerals.layout.pieceselection.PieceSelectionUI;
import com.neildg.gamesofthegenerals.layout.prompts.ChangeTurnUI;
import com.neildg.gamesofthegenerals.layout.prompts.ConfirmListener;
import com.neildg.gamesofthegenerals.layout.prompts.GenericPrompt;
import com.neildg.gamesofthegenerals.layout.prompts.WaitingTurnUI;

/**
 * Placement of pieces is done in this scene
 * @author user
 *
 */
public class PiecePlacementScene extends AbstractScene {
	
	private BoardCreator boardCreator;
	
	private PieceSelectionUI pieceSelectionUI;
	private WaitingTurnUI waitTurnUI;
	private GenericPrompt disconnectPrompt;
	
	public PiecePlacementScene() {
		super();
		
	}
	
	

	/* (non-Javadoc)
	 * @see com.neildg.gamesofthegenerals.scenes.AbstractScene#loadSceneAssets()
	 */
	@Override
	public void loadSceneAssets() {
		GameStateManager.getInstance().setCurrentState(GameState.PIECE_PLACEMENT);
		
		this.boardCreator = new BoardCreator(this);
		this.boardCreator.populateBoardForPlacement();
		this.boardCreator.setBoardPosition(ResolutionManager.SCENE_WIDTH * 0.14f, ResolutionManager.SCENE_HEIGHT * 0.14f);
		
		this.createFPSDisplay();
		this.loadUI();
		
	}
	
	private void loadUI() {
		this.pieceSelectionUI = new PieceSelectionUI();
		this.pieceSelectionUI.setPosition(0, ResolutionManager.SCENE_HEIGHT - PieceSelectionUI.UI_HEIGHT);
		this.attachChild(this.pieceSelectionUI);
		this.pieceSelectionUI.registerTouches(this);

		//waiting turn UI
		this.waitTurnUI = new WaitingTurnUI(this);
		this.waitTurnUI.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.35f);
		this.attachChild(this.waitTurnUI);
		
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
		// TODO Auto-generated method stub
		
		this.boardCreator.destroy();
		this.detachChildren();
		this.detachSelf();
		NotificationCenter.getInstance().clearObservers();
	}
	

}
