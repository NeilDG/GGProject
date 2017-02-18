/**
 * 
 */
package com.neildg.gamesofthegenerals.core;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.Texture;

import com.neildg.gamesofthegenerals.scenes.AbstractScene;
import com.neildg.gamesofthegenerals.scenes.BluetoothSetupScene;
import com.neildg.gamesofthegenerals.scenes.GameScene;
import com.neildg.gamesofthegenerals.scenes.IBaseScene;
import com.neildg.gamesofthegenerals.scenes.MainMenuScene;
import com.neildg.gamesofthegenerals.scenes.PiecePlacementScene;

/**
 * Manages the transition of scenes
 * @author user
 *
 */
public class SceneManager {

	private Engine engineInstance;
	
	private ITexture currentFontTexture;
	private Font currentFont;
	
	private static SceneManager sharedInstance = null;
	
	private ArrayList<IBaseScene> sceneList;
	private IBaseScene currentScene = null;
	private int currentSceneIndex = 0;
	
	private SceneManager() {
		this.engineInstance = EngineCore.getInstance().getEngine();
		
		this.sceneList = new ArrayList<IBaseScene>();
		
		this.createSceneList();
	}
	
	private void createSceneList() {
		this.sceneList.add(new MainMenuScene(ResolutionManager.getInstance().getCamera()));
		this.sceneList.add(new PiecePlacementScene());
		this.sceneList.add(new GameScene());
		this.sceneList.add(new BluetoothSetupScene());
	}
	
	public void loadScene(final int index) {
		
		
		Engine engine = EngineCore.getInstance().getEngine();
		
		engine.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				SceneManager.this.unloadFontResources();
				SceneManager.this.destroyCurrentScene();
				
				IBaseScene sceneToLoad = SceneManager.this.sceneList.get(index);
				sceneToLoad.loadSceneAssets();
				
				SceneManager.this.engineInstance.setScene((Scene) sceneToLoad);
				SceneManager.this.currentScene = sceneToLoad;
				SceneManager.this.currentSceneIndex = index;
			}
		});
		
	}
	
	//destroys the current scene and frees up resources
	private void destroyCurrentScene() {
		if(this.currentScene != null) {
			Scene scene = (Scene) this.currentScene;

			((IBaseScene) scene).destroyScene();
			scene.clearEntityModifiers();
			scene.clearTouchAreas();
			scene.clearUpdateHandlers();
			scene.clearChildScene();
			System.gc();
			
			this.currentScene = null;
		}
	}
	
	//returns to the previous scene. Called by onBackPressed
	//Returns true if successful
	public boolean goToPreviousScene() {
		boolean result = false;
		
		if(this.currentSceneIndex == SceneList.BLUETOOTH_SETUP_SCENE || this.currentSceneIndex == SceneList.GAME_SCENE) {
			this.currentSceneIndex = SceneList.MAIN_MENU_SCENE;
			this.loadScene(this.currentSceneIndex);
			result = true;
		}
		else if(this.currentSceneIndex > 0) {
			
			
			this.currentSceneIndex--;
			this.loadScene(this.currentSceneIndex);
			result = true;
		}
		
		return result;
	}
	
	public Scene getCurrentScene() {
		return (Scene) this.currentScene;
	}
	
	public int getCurrentSceneIndex() {
		return this.currentSceneIndex;
	}
	
	public void loadFontTexture(ITexture texture) {
		this.engineInstance.getTextureManager().loadTexture(texture);
		this.currentFontTexture = texture;
	}
	
	public void loadFont(Font font) {
		this.engineInstance.getFontManager().loadFont(font);
		this.currentFont = font;
	}
	
	public void unloadFontResources() {
		if(this.currentFontTexture != null)
			this.engineInstance.getTextureManager().unloadTexture(this.currentFontTexture);
		if(this.currentFont != null)
			this.engineInstance.getFontManager().unloadFont(this.currentFont);
		
		//this.currentFontTexture = null;
		//this.currentFont = null;
	}
	
	public void reloadFontResources() {
		if(this.currentFontTexture != null)
			this.loadFontTexture(this.currentFontTexture);
		if(this.currentFont != null)
			this.loadFont(this.currentFont);
	}
	
	public static SceneManager getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new SceneManager();
		}
		
		return sharedInstance;
	}
	
	
}
