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
import com.neildg.gamesofthegenerals.core.extension.IOnTouchListener;
import com.neildg.gamesofthegenerals.core.extension.SimpleButton;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle.AnchorType;
import com.neildg.gamesofthegenerals.layout.prompts.ConfirmListener;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * Prompt showing choices between hosting a game or joining one.
 * @author NeilDG
 *
 */
public class BluetoothSetupPrompt extends AnchoredRectangle implements IOnTouchListener {

	public final static int UI_WIDTH = 800;
	public final static int UI_HEIGHT = 400;
	
	private final static String TAG = "BluetoothSetupPrompt";
	
	private Engine engine;
	private AssetLoader assetLoader;
	private Scene assignedScene;
	
	private SimpleButton hostGameBtn;
	private SimpleButton joinGameBtn;
	
	private ConfirmListener hostGameConfirmListener;
	private ConfirmListener joinGameConfirmListener;
	
	public BluetoothSetupPrompt(Scene assignedScene) {
		super(0, 0, UI_WIDTH, UI_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		
		this.assignedScene = assignedScene;
		this.engine = EngineCore.getInstance().getEngine();
		this.assetLoader = AssetLoader.getInstance();
		
		this.setAnchorPoint(AnchorType.CENTER);
		this.createUI(engine);
	}
	
	public void setConfirmListener(ConfirmListener hostGameConfirmListener, ConfirmListener joinGameConfirmListener) {
		this.hostGameConfirmListener = hostGameConfirmListener;
		this.joinGameConfirmListener = joinGameConfirmListener;
	}
	
	private void createUI(Engine engine) {
		float[] normalizedRGB = RGBNormalizer.normalizeRGB(139, 119, 101);
		
		this.setColor(normalizedRGB[RGBNormalizer.RED], normalizedRGB[RGBNormalizer.GREEN], normalizedRGB[RGBNormalizer.BLUE]); //peach puff
		
		this.createButtons();
		this.setVisible(false);
		
	}
	
	
	private void createButtons() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 45, false);
		font.load();
		
		float buttonWidth = 600; float buttonHeight = 90;
		
		this.hostGameBtn = new SimpleButton(UI_WIDTH * 0.5f, UI_HEIGHT * 0.35f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.hostGameBtn.setAnchorPoint(AnchorType.CENTER);
		this.hostGameBtn.addText("Host A Game", font);
		
		this.joinGameBtn = new SimpleButton(UI_WIDTH * 0.5f, this.hostGameBtn.getY() + this.hostGameBtn.getHeight() + 100.0f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.joinGameBtn.setAnchorPoint(AnchorType.CENTER);
		this.joinGameBtn.addText("Join A Game", font);
		
		this.attachChild(this.hostGameBtn);
		this.attachChild(this.joinGameBtn);
	}
	
	public void show() {
		this.setVisible(true);
		this.assignedScene.registerTouchArea(this.hostGameBtn);
		this.assignedScene.registerTouchArea(this.joinGameBtn);
	}
	
	public void hide() {
		this.setVisible(false);
		this.assignedScene.unregisterTouchArea(this.hostGameBtn);
		this.assignedScene.unregisterTouchArea(this.joinGameBtn);
	}

	@Override
	public boolean onTouch(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
			float pTouchAreaLocalY, Entity touchedEntity) {
		if(pSceneTouchEvent.isActionUp()) {
			if(this.isVisible()) {
				if(this.hostGameBtn == (SimpleButton) touchedEntity) {
					if(this.hostGameConfirmListener == null) {
						Log.e(TAG,"ON CONFIRM Listener not set! Please add a confirm listener!");
					}
					else {
						this.hostGameConfirmListener.onConfirm();
					}
					
					this.hide();
				}
				else if(this.joinGameBtn == (SimpleButton) touchedEntity) {
					if(this.joinGameConfirmListener == null) {
						Log.e(TAG,"ON CONFIRM Listener not set! Please add a confirm listener!");
					}
					else {
						this.joinGameConfirmListener.onConfirm();
					}
					
					this.hide();
				}
			}
		}
		
		return true;
	}
}
