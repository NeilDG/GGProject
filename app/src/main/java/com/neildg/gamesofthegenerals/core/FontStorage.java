/**
 * 
 */
package com.neildg.gamesofthegenerals.core;

import java.util.HashMap;

import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

/**
 * Stores the font created for reuse
 * @author user
 *
 */
public class FontStorage {

	private static FontStorage sharedInstance = null;
	
	private HashMap<String, ITexture> fontTextureMapping;
	
	private FontStorage() {
		this.fontTextureMapping = new HashMap<String, ITexture>();
	}
	
	public void registerNewBitmapFont(BitmapTextureAtlas fontTexture, String fontName) {
		this.fontTextureMapping.put(fontName, fontTexture);
	}
	
	public ITexture getFontTexture(String fontName) {
		return this.fontTextureMapping.get(fontName);
	}
	
	public static FontStorage getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new FontStorage();
		}
		
		return sharedInstance;
	}
}
