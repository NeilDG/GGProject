/**
 * 
 */
package com.neildg.gamesofthegenerals.layout.prompts;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.util.HorizontalAlign;

import android.graphics.Typeface;
import android.util.Log;

import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle;
import com.neildg.gamesofthegenerals.core.extension.IOnTouchListener;
import com.neildg.gamesofthegenerals.core.extension.SimpleButton;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle.AnchorType;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.NotificationListener;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * Prompt to appear when player has made a move, instructing the player to lend the device to his opponent.
 * @author NeilDG
 *
 */
public class ChangeTurnUI extends AnchoredRectangle implements IOnTouchListener, NotificationListener{

	public final static int UI_WIDTH = 900;
	public final static int UI_HEIGHT = 690;
	
	private final static String TAG = "ChangeTurnUI";
	
	private Engine engine;
	private AssetLoader assetLoader; 
	private Scene assignedScene;
	
	private ConfirmListener confirmListener;
	
	private Text confirmText;
	private SimpleButton confirmBtn;
	
	public ChangeTurnUI(Scene assignedScene) {
		super(0, 0, UI_WIDTH, UI_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		
		this.assignedScene = assignedScene;
		this.engine = EngineCore.getInstance().getEngine();
		this.assetLoader = AssetLoader.getInstance();
		
		this.setAnchorPoint(AnchorType.CENTER);
		this.createUI(this.engine);
		//this.setZIndex(Z_INDEX);
		
		NotificationCenter.getInstance().addObserver(Notifications.ON_FINISHED_PLAYER_TURN_LOCAL, this);
	}
	
	public void setConfirmListener(ConfirmListener confirmListener) {
		this.confirmListener = confirmListener;
	}
	
	private void createUI(Engine engine) {
		float[] normalizedRGB = RGBNormalizer.normalizeRGB(139, 119, 101);
		
		this.setColor(normalizedRGB[RGBNormalizer.RED], normalizedRGB[RGBNormalizer.GREEN], normalizedRGB[RGBNormalizer.BLUE]); //peach puff
		
		this.createTexts();
		this.createButtons();
		this.setVisible(false);
		
	}
	
	private void createTexts() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 55, false);
		font.load();
		 
		Text playerTurnOverText = new Text(0, 0, font, "Your turn is over. Lend the device to your opponent", engine.getVertexBufferObjectManager());
		playerTurnOverText.setHorizontalAlign(HorizontalAlign.CENTER);
		playerTurnOverText.setAutoWrap(AutoWrap.WORDS);
		playerTurnOverText.setAutoWrapWidth(UI_WIDTH - 75.0f);
		playerTurnOverText.setPosition((UI_WIDTH * 0.5f) - (playerTurnOverText.getWidth() * 0.5f), 100);
		
		this.confirmText = new Text(0, 0, font, "<Player Name>, press confirm for your turn.", engine.getVertexBufferObjectManager());
		this.confirmText.setAutoWrap(AutoWrap.WORDS);
		this.confirmText.setAutoWrapWidth(UI_WIDTH - 75.0f);
		this.confirmText.setHorizontalAlign(HorizontalAlign.CENTER);
		this.confirmText.setPosition((UI_WIDTH * 0.5f) - (this.confirmText.getWidth() * 0.5f), playerTurnOverText.getY() + playerTurnOverText.getHeight() + 40); 
	     
	    this.attachChild(playerTurnOverText);
	    this.attachChild(this.confirmText);
	}
	
	private void createButtons() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 45, false);
		font.load();
		
		float buttonWidth = 300; float buttonHeight = 90;
		
		this.confirmBtn = new SimpleButton(UI_WIDTH * 0.5f, this.confirmText.getY() + this.confirmText.getHeight() + 100.0f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.confirmBtn.setAnchorPoint(AnchorType.CENTER);
		this.confirmBtn.addText("CONFIRM", font);
		
		this.attachChild(this.confirmBtn);
	}

	@Override
	public boolean onTouch(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
			float pTouchAreaLocalY, Entity touchedEntity) {
		
		Log.v(TAG, pSceneTouchEvent.toString());
		
		if(pSceneTouchEvent.isActionUp()) {
			Log.v(TAG, "Change TURN TOUCH " +touchedEntity.toString()+ " vs " +this.confirmBtn.toString());
			if(this.isVisible()) {
				Log.v(TAG, "Confirm button!");
				if(this.confirmListener == null) {
					Log.e(TAG,"ON CONFIRM Listener not set! Please add a confirm listener!");
				}
				else {
					this.confirmListener.onConfirm();
					this.setVisible(false);
					this.assignedScene.unregisterTouchArea(this.confirmBtn);
				}
			}
		}
		
		return true;
	}

	@Override
	public void onNotify(String notificationString, Object sender) {
		Log.v(TAG, "Show change turn UI");
		this.setVisible(true);
		this.setZIndex(assignedScene.getChildCount());
		this.assignedScene.sortChildren();
		this.assignedScene.registerTouchArea(this.confirmBtn);
		
		//hide all pieces for security
		Player playerOne = PlayerObserver.getInstance().getPlayerOne();
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		playerOne.markAllPiecesAsUnknown();
		playerTwo.markAllPiecesAsUnknown();
		
		//set confirm text
		this.confirmText.setText(PlayerObserver.getInstance().getActivePlayer().getPlayerName() + ", press confirm for your turn.");
		
	}
}
