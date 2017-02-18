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
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle;
import com.neildg.gamesofthegenerals.core.extension.SimpleButton;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle.AnchorType;
import com.neildg.gamesofthegenerals.core.extension.IOnTouchListener;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.layout.prompts.ConfirmListener;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * Prompt to show to confirm if devices has been paired
 * @author NeilDG
 *
 */
public class BluetoothWarningPrompt extends AnchoredRectangle implements IOnTouchListener{
	
	public final static int UI_WIDTH = 900;
	public final static int UI_HEIGHT = 690;
	
	private final static String TAG = "BluetoothWarningPrompt";
	
	private Engine engine;
	private AssetLoader assetLoader;
	private Scene assignedScene;
	
	private SimpleButton continueBtn;
	private SimpleButton returnBtn;
	
	private ConfirmListener confirmListener;
	
	public BluetoothWarningPrompt(Scene assignedScene) {
		super(0, 0, UI_WIDTH, UI_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		
		this.assignedScene = assignedScene;
		this.engine = EngineCore.getInstance().getEngine();
		this.assetLoader = AssetLoader.getInstance();
		
		this.setAnchorPoint(AnchorType.CENTER);
		this.createUI(this.engine);
		//this.setZIndex(Z_INDEX);
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
		 
		Text warningText = new Text(0, 0, font, "Make sure that the HOST is visible to all devices. A bluetooth request will pop up if bluetooth is disabled.", engine.getVertexBufferObjectManager());
		warningText.setHorizontalAlign(HorizontalAlign.CENTER);
		warningText.setAutoWrap(AutoWrap.WORDS);
		warningText.setAutoWrapWidth(UI_WIDTH - 75.0f);
		warningText.setPosition((UI_WIDTH * 0.5f) - (warningText.getWidth() * 0.5f), 100);
		
	    this.attachChild(warningText);
	}
	
	private void createButtons() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 45, false);
		font.load();
		
		float buttonWidth = 600; float buttonHeight = 90;
		
		this.continueBtn = new SimpleButton(UI_WIDTH * 0.5f, 400.0f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.continueBtn.setAnchorPoint(AnchorType.CENTER);
		this.continueBtn.addText("CONTINUE", font);
		
		this.returnBtn = new SimpleButton(UI_WIDTH * 0.5f, this.continueBtn.getY() + this.continueBtn.getHeight() + 100.0f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.returnBtn.setAnchorPoint(AnchorType.CENTER);
		this.returnBtn.addText("RETURN TO MAIN MENU", font);
		
		this.attachChild(this.continueBtn);
		this.attachChild(this.returnBtn);
	}
	
	public void show() {
		this.setVisible(true);
		this.assignedScene.registerTouchArea(this.continueBtn);
		this.assignedScene.registerTouchArea(this.returnBtn);
	}
	
	public void hide() {
		this.setVisible(false);
		this.assignedScene.unregisterTouchArea(this.continueBtn);
		this.assignedScene.unregisterTouchArea(this.returnBtn);
	}

	@Override
	public boolean onTouch(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
			float pTouchAreaLocalY, Entity touchedEntity) {
		
		if(pSceneTouchEvent.isActionUp()) {
			if(this.isVisible()) {
				if(this.continueBtn == (SimpleButton) touchedEntity) {
					if(this.confirmListener == null) {
						Log.e(TAG,"ON CONFIRM Listener not set! Please add a confirm listener!");
					}
					else {
						this.confirmListener.onConfirm();
					}
					
					this.hide();
				}
				else if(this.returnBtn == (SimpleButton) touchedEntity) {
					this.hide();
					SceneManager.getInstance().loadScene(SceneList.MAIN_MENU_SCENE);
				}
			}
		}
		
		return true;
	}
}
