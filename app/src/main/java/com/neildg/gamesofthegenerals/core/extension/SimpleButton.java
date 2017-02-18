/**
 * 
 */
package com.neildg.gamesofthegenerals.core.extension;

import org.andengine.engine.Engine;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.neildg.gamesofthegenerals.core.EngineCore;

/**
 * A simple button class
 * @author user
 *
 */
public class SimpleButton extends AnchoredRectangle {

	private IOnTouchListener touchListener;
	private int buttonID = 0;
	
	private Text buttonText;
	
	public SimpleButton(float pX, float pY, float pWidth, float pHeight, IOnTouchListener touchListener,
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, vertexBufferObjectManager);
		this.touchListener = touchListener;
		// TODO Auto-generated constructor stub
	}
	
	public void setButtonId(int id) {
		this.buttonID = id;
	}
	
	public int getButtonId() {
		return this.buttonID;
	}
	
	public void setTouchListener(IOnTouchListener touchListener) {
		this.touchListener = touchListener;
	}
	
	public void addText(String text, Font font) {
		 Engine engine = EngineCore.getInstance().getEngine();
		 this.buttonText = new Text(0,0, font, text, 300, engine.getVertexBufferObjectManager());
		 this.buttonText.setPosition((this.getWidth() * 0.5f) - (buttonText.getWidth() * 0.5f), (this.getHeight() * 0.5f) - (buttonText.getHeight() * 0.5f));
		 this.attachChild(this.buttonText);
	}
	
	public void setText(String text) {
		this.buttonText.setText(text);
		this.buttonText.setPosition((this.getWidth() * 0.5f) - (buttonText.getWidth() * 0.5f), (this.getHeight() * 0.5f) - (buttonText.getHeight() * 0.5f));
	}
	
	
	@Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown() && this.isVisible()) {
			this.setScale(1.2f);
		}
		else if(pSceneTouchEvent.isActionUp() && this.isVisible()) {
			this.setScale(1.0f);
		}
		return this.touchListener.onTouch(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, this);
	}
}
