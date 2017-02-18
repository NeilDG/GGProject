/**
 * 
 */
package com.neildg.gamesofthegenerals.core.extension;

import org.andengine.entity.Entity;
import org.andengine.input.touch.TouchEvent;

/**
 * Attach this to the class that will listen to the callback
 * @author user
 *
 */
public interface IOnTouchListener {

	public abstract boolean onTouch(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY, final Entity touchedEntity);
}
