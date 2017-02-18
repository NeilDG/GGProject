/**
 * 
 */
package com.neildg.gamesofthegenerals.layout.prompts;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.util.HorizontalAlign;

import android.graphics.Typeface;

import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.NotificationListener;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.game.TurnManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameState;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * The waiting turn UI to show while waiting for the player's move.
 * @author NeilDG
 *
 */
public class WaitingTurnUI extends AnchoredRectangle implements NotificationListener{

	public static int UI_WIDTH = 400;
	public static int UI_HEIGHT = 85;
	
	private final static String TAG = "WaitingTurnUI";
	
	private Scene assignedScene;
	private Engine engine;
	private AssetLoader assetLoader;
	
	public WaitingTurnUI(Scene assignedScene) {
		super(0, 0, UI_WIDTH, UI_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		
		this.assignedScene = assignedScene;
		this.engine = EngineCore.getInstance().getEngine();
		this.assetLoader = AssetLoader.getInstance();
		
		this.setAnchorPoint(AnchorType.CENTER);
		this.createUI(engine);
		this.setVisible(false);
		
		NotificationCenter.getInstance().addObserver(Notifications.ON_WAITING_PLAYER_TURN, this);
		NotificationCenter.getInstance().addObserver(Notifications.ON_FINISHED_PLAYER_TURN_COMPUTER, this);
		NotificationCenter.getInstance().addObserver(Notifications.ON_FINISHED_PLAYER_TURN_REMOTE, this);
	}
	
	private void createUI(Engine engine) {
		float[] normalizedRGB = RGBNormalizer.normalizeRGB(139, 119, 101);
		
		this.setColor(normalizedRGB[RGBNormalizer.RED], normalizedRGB[RGBNormalizer.GREEN], normalizedRGB[RGBNormalizer.BLUE]); //peach puff
		
		this.createTexts();
	}
	
	private void createTexts() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, false);
		font.load();
		 
		Text waitingText = new Text(0, 0, font, "Waiting for your opponent...", engine.getVertexBufferObjectManager());
		waitingText.setHorizontalAlign(HorizontalAlign.CENTER);
		waitingText.setAutoWrap(AutoWrap.WORDS);
		waitingText.setAutoWrapWidth(UI_WIDTH - 75.0f);
		waitingText.setPosition((UI_WIDTH * 0.5f) - (waitingText.getWidth() * 0.5f), 10);
		
		this.attachChild(waitingText);
	}
	
	

	@Override
	public void onNotify(String notificationString, Object sender) {
		if(notificationString == Notifications.ON_WAITING_PLAYER_TURN) {
			this.setZIndex(assignedScene.getChildCount());
			this.assignedScene.sortChildren();
			this.setVisible(true);
		}
		else if(notificationString == Notifications.ON_FINISHED_PLAYER_TURN_COMPUTER || notificationString == Notifications.ON_BLUETOOTH_DISCONNECT) {
			this.setVisible(false);
		}
		
		else if(notificationString == Notifications.ON_FINISHED_PLAYER_TURN_REMOTE) {
			
			//if this is visible, then it means the player is waiting for the other player to finish.
			if(this.isVisible() && GameStateManager.getInstance().getCurrentState() == GameState.AWAITING_MAIN_GAME) {
				this.setVisible(false);
				GameStateManager.getInstance().reportPiecePlacementDone();
				SceneManager.getInstance().loadScene(SceneList.GAME_SCENE);
			}
			
			//moves processing
			else if(this.isVisible() && GameStateManager.getInstance().getCurrentState() == GameState.MAIN_GAME) {
				this.setVisible(false);
			}
			
		}
		
		
	}
}
