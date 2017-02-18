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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;

import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle;
import com.neildg.gamesofthegenerals.core.extension.IOnTouchListener;
import com.neildg.gamesofthegenerals.core.extension.SimpleButton;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle.AnchorType;
import com.neildg.gamesofthegenerals.layout.prompts.ConfirmListener;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * Error prompt to show if bluetooth connection returns an error
 * @author NeilDG
 *
 */
public class BluetoothErrorPrompt extends AnchoredRectangle implements IOnTouchListener {

	public final static int UI_WIDTH = 800;
	public final static int UI_HEIGHT = 400;
	
	private final static String TAG = "BluetoothSetupPrompt";
	
	private Engine engine;
	private AssetLoader assetLoader;
	private Scene assignedScene;
	
	private SimpleButton retryBtn;
	
	private ConfirmListener confirmListener;
	
	public BluetoothErrorPrompt(Scene assignedScene) {
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
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 45, false);
		font.load();
		 
		Text warningText = new Text(0, 0, font, "Error setting up bluetooth! Either bluetooth is not supported by your device or it is not enabled!", engine.getVertexBufferObjectManager());
		warningText.setHorizontalAlign(HorizontalAlign.CENTER);
		warningText.setAutoWrap(AutoWrap.WORDS);
		warningText.setAutoWrapWidth(UI_WIDTH - 75.0f);
		warningText.setPosition((UI_WIDTH * 0.5f) - (warningText.getWidth() * 0.5f), 25);
		
	    this.attachChild(warningText);
	}
	
	private void createButtons() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 35, false);
		font.load();
		
		float buttonWidth = 600; float buttonHeight = 90;
		
		this.retryBtn = new SimpleButton(UI_WIDTH * 0.5f, 330.0f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.retryBtn.setAnchorPoint(AnchorType.CENTER);
		this.retryBtn.addText("RETRY", font);
		
		this.attachChild(this.retryBtn);
	}
	
	public void show() {
		this.setVisible(true);
		this.assignedScene.registerTouchArea(this.retryBtn);
		
		//we request for bluetooth intent
		Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
        runningActivity.startActivity(i);
	}
	
	public void hide() {
		this.setVisible(false);
		this.assignedScene.unregisterTouchArea(this.retryBtn);
	}

	@Override
	public boolean onTouch(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
			float pTouchAreaLocalY, Entity touchedEntity) {
		
		if(pSceneTouchEvent.isActionUp()) {
			if(this.isVisible()) {
				if(this.retryBtn == (SimpleButton) touchedEntity) {
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
}
