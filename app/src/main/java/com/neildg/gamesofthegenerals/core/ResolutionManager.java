/**
 * 
 */
package com.neildg.gamesofthegenerals.core;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.ZoomCamera;

import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Stores the set resolution
 * @author user
 *
 */
public final class ResolutionManager {

	private static ResolutionManager sharedInstance = null;
	
	public static int SCENE_WIDTH = 1280;
	public static int SCENE_HEIGHT = 720;
	
	private ZoomCamera camera;
	
	private ResolutionManager() {

	}
	
	public void setCamera(ZoomCamera camera) {
		this.camera = camera;
	}
	
	public ZoomCamera getCamera() {
		return this.camera;
	}
	
	//adjust accordingly to device metrics
	public void adjustToMetrics(WindowManager windowManager) {
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		SCENE_WIDTH = metrics.widthPixels;
		SCENE_HEIGHT = metrics.heightPixels;
	}
	
	
	public static ResolutionManager getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new ResolutionManager();
		}
		
		return sharedInstance;
	}
}
