/**
 * 
 */
package com.neildg.gamesofthegenerals.core.extension;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.primitive.vbo.IRectangleVertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

/**
 * A rectangle class with an anchor property
 * @author user
 *
 */
public class AnchoredRectangle extends Rectangle {

	private static final String TAG = "AnchoredRectangle";
	
	public enum AnchorType
	{
		TOP_LEFT,
		BOTTOM_LEFT,
		CENTER,
		TOP_RIGHT,
		BOTTOM_RIGHT
	}
	
	private float anchorX = 0.0f;
	private float anchorY = 0.0f;
	
	private AnchorType anchorPoint = AnchorType.TOP_LEFT;
	
	public AnchoredRectangle(float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, vertexBufferObjectManager);
		// TODO Auto-generated constructor stub
	}
	
	public void setAnchorPoint(AnchorType anchor) {
		this.anchorPoint = anchor;
		switch(this.anchorPoint) {
			case CENTER: this.anchorX = this.getWidth() * 0.5f;
						 this.anchorY = this.getHeight() * 0.5f;
						 break;
						 
			case TOP_LEFT: this.anchorX = 0.0f;
						   this.anchorY = 0.0f;
						   break;
						 
			default: Log.e(TAG, this.anchorPoint + "is not yet implemented!");
		}
		
		this.setPosition(this.getX(), this.getY());
	}
	
	@Override
	public void setPosition(float pX, float pY) {
		float offsetX = pX - this.anchorX;
		float offsetY = pY - this.anchorY;
		
		super.setPosition(offsetX, offsetY);
	}

}
