/**
 * 
 */
package com.neildg.gamesofthegenerals.core.extension;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * A touchable sprite that implements an interface callback for easier touch event handling
 * @author user
 *
 */
public class TouchableSprite extends Sprite {

	private IOnTouchListener touchListener;
	
	public TouchableSprite(float pX, float pY,
			ITextureRegion pTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager, IOnTouchListener touchListener) {
		super(pX, pY,pTextureRegion, vertexBufferObjectManager);
		this.touchListener = touchListener;
		
	}
	
	public void setTouchListener(IOnTouchListener touchListener) {
		this.touchListener = touchListener;
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
