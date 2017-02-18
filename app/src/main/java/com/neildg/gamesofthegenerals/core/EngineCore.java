/**
 * 
 */
package com.neildg.gamesofthegenerals.core;

import org.andengine.engine.Engine;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * Holds the instance of the engine and other necessary engine data
 * @author user
 *
 */
public class EngineCore {
	private static EngineCore sharedInstance = null;

	private Engine engine;
	private Context context;
	
	private EngineCore() {
		
	}
	
	public static void init(Engine engine, Context context) {
		EngineCore engineInstance = EngineCore.getInstance();
		engineInstance.engine = engine;
		engineInstance.context = context;
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public AssetManager getAssetManager() {
		return this.context.getAssets();
	}
	
	public Engine getEngine() {
		return this.engine;
	}
	
	public static EngineCore getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new EngineCore();
		}
		
		return sharedInstance;
	}
	
	
}
