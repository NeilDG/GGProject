/**
 * 
 */
package com.neildg.gamesofthegenerals.layout.bluetooth;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
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
import com.neildg.gamesofthegenerals.layout.prompts.ConfirmListener;
import com.neildg.gamesofthegenerals.scenes.BluetoothSetupScene;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * Prompt to show if the host has established a game
 * @author NeilDG
 *
 */
public class BluetoothHostEstablishPrompt extends AnchoredRectangle implements IOnTouchListener, NotificationListener {

	public final static int UI_WIDTH = 900;
	public final static int UI_HEIGHT = 600;
	
	private final static String TAG = "BluetoothHostEstablishPrompt";
	
	private Engine engine;
	private AssetLoader assetLoader;
	private Scene assignedScene;
	
	private SimpleButton proceedToGameBtn;
	
	private ConfirmListener confirmListener;
	
	private Text serverAddressText;
	
	public BluetoothHostEstablishPrompt(Scene assignedScene) {
		super(0, 0, UI_WIDTH, UI_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		
		this.assignedScene = assignedScene;
		this.engine = EngineCore.getInstance().getEngine();
		this.assetLoader = AssetLoader.getInstance();
		
		this.setAnchorPoint(AnchorType.CENTER);
		this.createUI(this.engine);
		//this.setZIndex(Z_INDEX);
		
		NotificationCenter.getInstance().addObserver(Notifications.ON_CLIENT_SUCCESSFULLY_CONNECTED, this);
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
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 35, false);
		font.load();
		 
		Text serverAddressTitle = new Text(0, 0, font, "Your server address is: ", engine.getVertexBufferObjectManager());
		serverAddressTitle.setHorizontalAlign(HorizontalAlign.CENTER);
		serverAddressTitle.setAutoWrap(AutoWrap.WORDS);
		serverAddressTitle.setAutoWrapWidth(UI_WIDTH - 75.0f);
		serverAddressTitle.setPosition((UI_WIDTH * 0.5f) - (serverAddressTitle.getWidth() * 0.5f), 25);
		
		Font serverFont = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 45, false);
		serverFont.load();
		
		this.serverAddressText = new Text(0, 0, serverFont, "<SERVER ADDRESS>", 800, engine.getVertexBufferObjectManager());
		this.serverAddressText.setAutoWrap(AutoWrap.WORDS);
		this.serverAddressText.setAutoWrapWidth(UI_WIDTH - 75.0f);
		this.serverAddressText.setHorizontalAlign(HorizontalAlign.CENTER);
		this.serverAddressText.setPosition((UI_WIDTH * 0.5f) - (this.serverAddressText.getWidth() * 0.5f), serverAddressTitle.getY() + serverAddressTitle.getHeight() + 40); 
		
		Text detailsText = new Text(0, 0, font, "Let your opponent connect to your device. The game button will appear once your opponent has established a successful connection.", engine.getVertexBufferObjectManager());
		detailsText.setHorizontalAlign(HorizontalAlign.CENTER);
		detailsText.setAutoWrap(AutoWrap.WORDS);
		detailsText.setAutoWrapWidth(UI_WIDTH - 75.0f);
		detailsText.setPosition((UI_WIDTH * 0.5f) - (serverAddressTitle.getWidth() * 0.5f), this.serverAddressText.getY() + this.serverAddressText.getHeight() + 40);
		
	    this.attachChild(serverAddressTitle);
	    this.attachChild(this.serverAddressText);
	    this.attachChild(detailsText);
	}
	
	private void createButtons() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 45, false);
		font.load();
		
		float buttonWidth = 600; float buttonHeight = 90;
		
		this.proceedToGameBtn = new SimpleButton(UI_WIDTH * 0.5f, UI_HEIGHT * 0.8f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.proceedToGameBtn.setAnchorPoint(AnchorType.CENTER);
		this.proceedToGameBtn.addText("Proceed To Game", font);
		
		this.proceedToGameBtn.setVisible(false);
		
		this.attachChild(this.proceedToGameBtn);
	}
	
	public void assignServerAddress(final String address) {
		this.serverAddressText.setText(address);
		
	}
	
	public void show() {
		this.setVisible(true);
	}
	
	public void hide() {
		this.setVisible(false);
		this.assignedScene.unregisterTouchArea(this.proceedToGameBtn);
	}

	@Override
	public boolean onTouch(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
			float pTouchAreaLocalY, Entity touchedEntity) {
		
		if(pSceneTouchEvent.isActionUp()) {
			if(this.isVisible()) {
				if(this.proceedToGameBtn == (SimpleButton) touchedEntity) {
					if(this.confirmListener == null) {
						Log.e(TAG,"ON CONFIRM Listener not set! Please add a confirm listener!");
					}
					else {
						this.confirmListener.onConfirm();
					}
					
					this.hide();
				}
			}
		}
		
		return true;
	}

	//shows the proceed to game button once notified that a client has connected
	@Override
	public void onNotify(String notificationString, Object sender) {
		this.proceedToGameBtn.setVisible(true);
		this.assignedScene.registerTouchArea(this.proceedToGameBtn);
	}
}
