/**
 * 
 */
package com.neildg.gamesofthegenerals.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;
import org.andengine.util.texturepack.exception.TexturePackParseException;

import com.neildg.gamesofthegenerals.atlases.AvatarIconsAtlas;
import com.neildg.gamesofthegenerals.atlases.MilitarySymbolsAtlas;
import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.FontNames;
import com.neildg.gamesofthegenerals.core.FontStorage;
import com.neildg.gamesofthegenerals.core.ResolutionManager;
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameMode;
import com.neildg.gamesofthegenerals.entities.multiplayer.SocketManager;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.widget.Toast;

/**
 * Represents the Main menu scene
 * @author user
 *
 */
public class MainMenuScene extends MenuScene implements IBaseScene, IOnMenuItemClickListener {
	
	private Text text;
	
	//test load atlases
	private TexturePackTextureRegionLibrary texturePackLibrary;
	private TexturePack texturePack;
	private ITextureRegion privateIconTexture;

	public MainMenuScene(Camera camera) {
		super(camera);
	}
	
	@Override
	public void loadSceneAssets() {
		
		//close any bluetooth connections
		SocketManager.getInstance().closeConnections();
		
		this.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
        
		Engine engine = EngineCore.getInstance().getEngine();
		
        /*BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        FontStorage.getInstance().registerNewBitmapFont(fontTexture, FontNames.BASIC_FONT);
        Font font = new Font(engine.getFontManager(), fontTexture, Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD), 50, false, Color.BLACK);
       
        SceneManager.getInstance().loadFont(font);
        SceneManager.getInstance().loadFontTexture(font.getTexture());*/
		
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50, true);
		font.load();
       
		IMenuItem playBluetoothItem = new ScaleMenuItemDecorator(new TextMenuItem(0, font, "Bluetooth Battle", engine.getVertexBufferObjectManager()), 1.3f, 1.0f);
        this.addMenuItem(playBluetoothItem);
        
		IMenuItem playLocalItem = new ScaleMenuItemDecorator(new TextMenuItem(1, font, "Same Device Battle", engine.getVertexBufferObjectManager()), 1.3f, 1.0f);
        this.addMenuItem(playLocalItem);
        
        IMenuItem playWithBotsItem = new ScaleMenuItemDecorator(new TextMenuItem(2, font, "Play Against Computer", engine.getVertexBufferObjectManager()), 1.3f, 1.0f);
        this.addMenuItem(playWithBotsItem);

		IMenuItem watchComputerItem = new ScaleMenuItemDecorator(new TextMenuItem(3, font, "Computer Against Computer", engine.getVertexBufferObjectManager()), 1.3f, 1.0f);
		this.addMenuItem(watchComputerItem);
        
        IMenuItem quitItem = new ScaleMenuItemDecorator(new TextMenuItem(4, font, "Quit", engine.getVertexBufferObjectManager()), 1.3f, 1.0f);
        this.addMenuItem(quitItem);
        
        this.buildAnimations();
        
        this.setOnMenuItemClickListener(this);
        
        this.createVersionNumber();
	}
	
	public void createVersionNumber() {
		Engine engine = EngineCore.getInstance().getEngine();
		
		Activity activity = (Activity) EngineCore.getInstance().getContext();
		
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, true);
		font.load();
		
		PackageInfo pInfo;
		try {
			pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			Text versionText = new Text(0, 0, font, pInfo.versionName, engine.getVertexBufferObjectManager());
			versionText.setHorizontalAlign(HorizontalAlign.LEFT);
			versionText.setAutoWrap(AutoWrap.WORDS);
			versionText.setAutoWrapWidth(ResolutionManager.SCENE_WIDTH - 75.0f);
			versionText.setPosition(20, ResolutionManager.SCENE_HEIGHT - 45.0f);
			
			Text creditsText = new Text(0, 0, font, "Created By NeilDG. App Not For Sale.", engine.getVertexBufferObjectManager());
			creditsText.setHorizontalAlign(HorizontalAlign.LEFT);
			creditsText.setAutoWrap(AutoWrap.WORDS);
			creditsText.setAutoWrapWidth(ResolutionManager.SCENE_WIDTH - 75.0f);
			creditsText.setPosition(ResolutionManager.SCENE_WIDTH - 505.0f, ResolutionManager.SCENE_HEIGHT - 45.0f);
			
			this.attachChild(versionText);
			this.attachChild(creditsText);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void detachChildren() {
		this.clearMenuItems();
		super.detachChildren();
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
	    
		final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
	    switch(pMenuItem.getID())
	    {

	    	case 0: 
	    		//play via bluetooth
	    		GameStateManager.getInstance().setGameMode(GameMode.VERSUS_HUMAN_ADHOC);
	    		SceneManager.getInstance().loadScene(SceneList.BLUETOOTH_SETUP_SCENE);
	    		//set placeholder names
	    		PlayerObserver.getInstance().getPlayerOne().setPlayerName("PlayerOne");
	    		PlayerObserver.getInstance().getPlayerTwo().setPlayerName("PlayerTwo");
	    		return true;
	    	case 1: 
	    		//play with fellow player using same device
	    		GameStateManager.getInstance().setGameMode(GameMode.VERSUS_HUMAN_LOCAL);
	    		SceneManager.getInstance().loadScene(SceneList.PIECE_PLACEMENT_SCENE);
	    		//set placeholder names
	    		PlayerObserver.getInstance().getPlayerOne().setPlayerName("PlayerOne");
	    		PlayerObserver.getInstance().getPlayerTwo().setPlayerName("PlayerTwo");
	    		return true;
	        case 2:
	            //play with bots scene
	        	GameStateManager.getInstance().setGameMode(GameMode.VERSUS_COMPUTER);
	        	SceneManager.getInstance().loadScene(SceneList.PIECE_PLACEMENT_SCENE);
	            return true;
			case 3:
				//computer vs computer
				GameStateManager.getInstance().setGameMode(GameMode.COMPUTER_VERSUS_COMPUTER);
				SceneManager.getInstance().loadScene(SceneList.GAME_SCENE);
				return true;
	        case 4:
	        	//quit application
	        	runningActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						runningActivity.onBackPressed();
					}
				});
	            return true;
	        default:
	            return false;
	    }
	}

	@Override
	public void destroyScene() {
		// TODO Auto-generated method stub
		this.detachChildren();
		this.detachSelf();
		
	}
}
