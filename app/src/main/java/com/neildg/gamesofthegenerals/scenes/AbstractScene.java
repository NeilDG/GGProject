/**
 * 
 */
package com.neildg.gamesofthegenerals.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSCounter;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.util.color.Color;

import android.graphics.Typeface;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.SceneManager;

/**
 * Represents own abstract custom scene that must implement loadSceneAssets and inherits fps display counter
 * @author user
 *
 */
public abstract class AbstractScene extends Scene implements IBaseScene {

	private Text fpsText;
	private FPSCounter fpsCounter;
	
	public AbstractScene() {
		super();
	}

	public void createFPSDisplay() {
		
		//fps display
		Engine engine = EngineCore.getInstance().getEngine();
		BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		Font font = new Font(engine.getFontManager(), fontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50, true, Color.WHITE);
		SceneManager.getInstance().loadFont(font);
        SceneManager.getInstance().loadFontTexture(fontTexture);
		
		this.fpsCounter = new FPSCounter();
		engine.registerUpdateHandler(fpsCounter);
        
        this.fpsText = new Text(200, 0, font, "FPS:", "FPS: XXXXX".length(), engine.getVertexBufferObjectManager());
        this.attachChild(fpsText);
        
        this.registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                   fpsText.setText("FPS: " + String.format("%.2f", fpsCounter.getFPS()));
            }
        }));
	}
}
