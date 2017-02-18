/**
 * 
 */
package com.neildg.gamesofthegenerals.layout.prompts;

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
import com.neildg.gamesofthegenerals.core.extension.SimpleButton;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle.AnchorType;
import com.neildg.gamesofthegenerals.core.extension.IOnTouchListener;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.NotificationListener;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * Generic prompt to appear that has a positive and negative button.
 * @author NeilDG
 *
 */
public class GenericPrompt extends AnchoredRectangle implements IOnTouchListener, NotificationListener {
	
	public final static int UI_WIDTH = 700;
	public final static int UI_HEIGHT = 600;
	
	private final static String TAG = "GenericPrompt";
	
	private Scene assignedScene;
	private Engine engine;
	
	private Text displayedText;
	private SimpleButton positiveBtn;
	private SimpleButton negativeBtn;
	
	private ConfirmListener positiveBtnListener;
	private ConfirmListener negativeBtnListener;
	
	
	public GenericPrompt(Scene assignedScene) {
		super(0, 0, UI_WIDTH, UI_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		
		this.assignedScene = assignedScene;
		this.engine = EngineCore.getInstance().getEngine();
		
		this.setAnchorPoint(AnchorType.CENTER);
		this.createUI(this.engine);
		//this.setZIndex(Z_INDEX);
		
	}
	
	public void setConfirmListener(ConfirmListener positiveListener, ConfirmListener negativeListener) {
		this.positiveBtnListener = positiveListener;
		this.negativeBtnListener = negativeListener;
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
		 
		this.displayedText = new Text(0, 0, font, "GENERIC PROMPT HERE", 300, engine.getVertexBufferObjectManager());
		this.displayedText.setHorizontalAlign(HorizontalAlign.CENTER);
		this.displayedText.setAutoWrap(AutoWrap.WORDS);
		this.displayedText.setAutoWrapWidth(UI_WIDTH - 75.0f);
		this.displayedText.setPosition((UI_WIDTH * 0.5f) - (this.displayedText.getWidth() * 0.5f), 45);
	     
	    this.attachChild(this.displayedText);
	}
	
	private void createButtons() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 45, false);
		font.load();
		
		float buttonWidth = 300; float buttonHeight = 90;
		
		this.negativeBtn = new SimpleButton(UI_WIDTH * 0.5f, UI_HEIGHT - buttonHeight - 10.0f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.negativeBtn.setAnchorPoint(AnchorType.CENTER);
		this.negativeBtn.addText("CANCEL", font);
		
		this.positiveBtn = new SimpleButton(UI_WIDTH * 0.5f, this.negativeBtn.getY() - (buttonHeight * 0.5f) - 15.0f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.positiveBtn.setAnchorPoint(AnchorType.CENTER);
		this.positiveBtn.addText("CONFIRM", font);
		
		this.attachChild(this.positiveBtn);
		this.attachChild(this.negativeBtn);
	}
	
	public void setDisplayText(String text) {
		this.displayedText.setText(text);
		this.displayedText.setPosition((UI_WIDTH * 0.5f) - (this.displayedText.getWidth() * 0.5f), 100);
	}
	
	public void setPositiveButtonText(String text) {
		this.positiveBtn.setText(text);
	}
	
	public void setNegativeButtonText(String text) {
		this.negativeBtn.setText(text);
	}
	
	//call this function if the prompt only has one button.
	public void hideNegativeButton() {
		this.negativeBtn.setVisible(false);
		this.assignedScene.unregisterTouchArea(this.negativeBtn);
	}
	
	public void showNegativeButton() {
		this.negativeBtn.setVisible(true);
		this.assignedScene.registerTouchArea(this.negativeBtn);
	}

	@Override
	public boolean onTouch(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
			float pTouchAreaLocalY, Entity touchedEntity) {
		
		Log.v(TAG, pSceneTouchEvent.toString());
		
		if(pSceneTouchEvent.isActionUp()) {
			if(this.isVisible()) {
				
				if((SimpleButton) touchedEntity == this.positiveBtn) {
					if(this.positiveBtnListener == null) {
						Log.e(TAG,"POSITIVE Button Listener not set! Please add a confirm listener!");
					}
					else {
						this.setVisible(false);
						this.assignedScene.unregisterTouchArea(this.positiveBtn);
						this.assignedScene.unregisterTouchArea(this.negativeBtn);
						this.positiveBtnListener.onConfirm();
						
					}
				}
				
				else if((SimpleButton) touchedEntity == this.negativeBtn) {
					if(this.negativeBtnListener == null) {
						Log.e(TAG,"NEGATIVE Button Listener not set! Please add a confirm listener!");
					}
					else {
						this.setVisible(false);
						this.assignedScene.unregisterTouchArea(this.positiveBtn);
						this.assignedScene.unregisterTouchArea(this.negativeBtn);
						this.negativeBtnListener.onConfirm();	
					}
				}
				
			}
		}
		
		return true;
	}

	@Override
	public void onNotify(String notificationString, Object sender) {
		this.setZIndex(assignedScene.getChildCount());
		this.assignedScene.sortChildren();
		this.setVisible(true);
		this.assignedScene.registerTouchArea(this.positiveBtn);
		this.assignedScene.registerTouchArea(this.negativeBtn);
	}

}
