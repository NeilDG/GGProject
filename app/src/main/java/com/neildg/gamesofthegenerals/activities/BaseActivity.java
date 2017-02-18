package com.neildg.gamesofthegenerals.activities;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;

import com.neildg.gamesofthegenerals.R;
import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.ResolutionManager;
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.entities.multiplayer.SocketManager;

public class BaseActivity extends SimpleBaseGameActivity {
	
	private final static String TAG = "BaseActivity";
	
	private static int ENGINE_MAX_FPS = 30;
	
	@Override
	public Engine onCreateEngine(final EngineOptions pEngineOptions) {
	    Engine e = new LimitedFPSEngine(pEngineOptions, ENGINE_MAX_FPS);
	 
	    EngineCore.init(e, this);
	    // or - depending on the behaviour you're after
	    // Engine e = new FixedStepEngine(pEngineOptions, TARGET_FPS);
	    return e;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_base);
		
		
	}
	
	@Override
	protected void onPause() {
		/*if(this.isGameLoaded()) {
			SceneManager.getInstance().unloadFontResources();
		}*/
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if(this.isGameLoaded()) {
			SceneManager.getInstance().unloadFontResources();
			SceneManager.getInstance().reloadFontResources();
		}
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		//close any existing bluetooth connections
		SocketManager.getInstance().closeConnections();
		
		super.onStop();
	}
	
	@Override
	public void onBackPressed() {
		
		//if it is in the game session, show prompt before exiting
		if(SceneManager.getInstance().getCurrentSceneIndex() != SceneList.MAIN_MENU_SCENE) {
			new AlertDialog.Builder(this)
		    .setTitle("Abandon Game Session")
		    .setMessage("Are you sure you want to abandon your game session?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            SocketManager.getInstance().closeConnections();
		            SceneManager.getInstance().goToPreviousScene();
		            dialog.cancel();
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	dialog.cancel();
		        }
		     }).setCancelable(false)
		     .show();
		}
		
		//if it is in the main menu, show exit prompt then use native back pressed
		else if(SceneManager.getInstance().getCurrentSceneIndex() == SceneList.MAIN_MENU_SCENE) {
			new AlertDialog.Builder(this)
		    .setTitle("Exit Game")
		    .setMessage("Are you sure you want to exit the game?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	BaseActivity.super.onBackPressed();
					System.exit(0);
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	dialog.cancel();
		        }
		     }).setCancelable(false)
		     .show();
		}
		
		/*//if there is a previous scene, go to that scene
		else if(SceneManager.getInstance().goToPreviousScene()) {
			//close any existing bluetooth connections
			SocketManager.getInstance().closeConnections();
			Log.v(TAG, "Going to previous scene!");
		}
		//else, minimize app by using native onBackPressed
		else {
			super.onBackPressed();
			System.exit(0);
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		//ResolutionManager.getInstance().adjustToMetrics(getWindowManager());
		
		final ZoomCamera camera = new ZoomCamera(0, 0, ResolutionManager.SCENE_WIDTH, ResolutionManager.SCENE_HEIGHT);
		//RatioResolutionPolicy resoPolicy = new RatioResolutionPolicy(ResolutionManager.SCENE_WIDTH, ResolutionManager.SCENE_HEIGHT);
		FillResolutionPolicy resoPolicy = new FillResolutionPolicy();
		
		ResolutionManager.getInstance().setCamera(camera);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, resoPolicy, camera);
	}

	@Override
	protected void onCreateResources() {
		// TODO Auto-generated method stub
		AssetLoader.getInstance().loadGraphics();
	}

	@Override
	protected Scene onCreateScene() {
		
		SceneManager.getInstance().loadScene(SceneList.MAIN_MENU_SCENE);
		Scene mainMenu = SceneManager.getInstance().getCurrentScene();
		
		return mainMenu;
	}

}
