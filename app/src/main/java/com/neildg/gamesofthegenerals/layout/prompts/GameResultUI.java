/**
 * 
 */
package com.neildg.gamesofthegenerals.layout.prompts;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.primitive.vbo.IRectangleVertexBufferObject;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.util.HorizontalAlign;

import android.graphics.Typeface;
import android.util.Log;

import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.ResolutionManager;
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle;
import com.neildg.gamesofthegenerals.core.extension.IOnTouchListener;
import com.neildg.gamesofthegenerals.core.extension.SimpleButton;
import com.neildg.gamesofthegenerals.core.extension.TouchableSprite;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.NotificationListener;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameMode;
import com.neildg.gamesofthegenerals.entities.openings.OpeningMovesLibrary;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;
import com.neildg.gamesofthegenerals.input.PiecePlacementInputHandler;
import com.neildg.gamesofthegenerals.layout.pieceselection.PieceDisplayUI;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * Represents the game result UI after the game ends
 * @author user
 *
 */
public class GameResultUI extends AnchoredRectangle implements IOnTouchListener, NotificationListener{

	private final static String TAG = "GameResultUI";
	
	public final static int UI_WIDTH = 700;
	public final static int UI_HEIGHT = 600;
	
	private Rectangle background;
	private Engine engine;
	
	private Scene assignedScene;
	
	
	private Text winText;
	private SimpleButton playAgainBtn;
	private SimpleButton mainMenuBtn;
	
	public GameResultUI(Scene assignedScene) {
		super(0, 0, UI_WIDTH, UI_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		
		this.engine = EngineCore.getInstance().getEngine();
		this.assignedScene = assignedScene;
		
		this.setAnchorPoint(AnchorType.CENTER);
		this.createUI(this.engine);
		
		NotificationCenter.getInstance().addObserver(Notifications.ON_CAPTURED_FLAG, this);

	}
	
	public void createUI(Engine engine) {
		this.background = new Rectangle(0, 0, UI_WIDTH, UI_HEIGHT, engine.getVertexBufferObjectManager());
		float[] normalizedRGB = RGBNormalizer.normalizeRGB(139, 119, 101);
		
		this.background.setColor(normalizedRGB[RGBNormalizer.RED], normalizedRGB[RGBNormalizer.GREEN], normalizedRGB[RGBNormalizer.BLUE]); //peach puff
		
		this.attachChild(this.background);
		this.createTexts();
		this.createButtons();
		
		this.setVisible(false);
	}
	
	public void showResults(Player winningPlayer) {
		this.setVisible(true);
		
		String winString;
		//there's a winning player
		if(winningPlayer != null) {
			winString = winningPlayer.getPlayerName() + " wins!";
		}
		else {
			winString = "It's a draw!";
		}
		
		this.winText.setText(winString);
		
		//disable play again if ad hoc mode
		if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_ADHOC) {
			this.playAgainBtn.setVisible(false);
			this.assignedScene.unregisterTouchArea(this.playAgainBtn);
		}
		else {
			this.playAgainBtn.setVisible(true);
			this.assignedScene.registerTouchArea(this.playAgainBtn);
		}
		
		//determine if the computer wins. If it wins, delete the existing player opening save data to avoid using it in future games.
		if(winningPlayer == PlayerObserver.getInstance().getPlayerTwo() &&
				GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_COMPUTER) {
			OpeningMovesLibrary.getInstance().deleteLastWrittenFile();
		}
	}
	
	private void createTexts() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 55, false);
		font.load();
		 
		Text ggText = new Text(0, 0, font, "Good Game! Well Played!", engine.getVertexBufferObjectManager());
	    ggText.setHorizontalAlign(HorizontalAlign.CENTER);
	    ggText.setPosition((UI_WIDTH * 0.5f) - (ggText.getWidth() * 0.5f), 100);
	     
	    this.winText = new Text(0, 0, font, "Whoever wins text", engine.getVertexBufferObjectManager());
	    this.winText.setHorizontalAlign(HorizontalAlign.CENTER);
	    this.winText.setPosition((UI_WIDTH * 0.5f) - (this.winText.getWidth() * 0.5f), ggText.getY() + ggText.getHeight() + 40);
	     
	    this.attachChild(ggText);
	    this.attachChild(this.winText);
	}
	
	private void createButtons() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 45, false);
		font.load();
		
		float buttonWidth = 300; float buttonHeight = 90;
		
		this.playAgainBtn = new SimpleButton(UI_WIDTH * 0.5f, 350.0f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.playAgainBtn.setAnchorPoint(AnchorType.CENTER);
		this.playAgainBtn.addText("Play Again", font);
		this.mainMenuBtn = new SimpleButton(UI_WIDTH * 0.5f, this.playAgainBtn.getY() + this.playAgainBtn.getHeight() + 60.0f, buttonWidth, buttonHeight, this, engine.getVertexBufferObjectManager());
		this.mainMenuBtn.setAnchorPoint(AnchorType.CENTER);
		this.mainMenuBtn.addText("Main Menu", font);
		
		this.attachChild(this.playAgainBtn);
		this.attachChild(this.mainMenuBtn);
	}
	
	//register touchable areas in scene
	private void registerTouches() {
		this.assignedScene.registerTouchArea(this.playAgainBtn);
		this.assignedScene.registerTouchArea(this.mainMenuBtn);
	}
	
	private void unregisterTouches() {
		this.assignedScene.unregisterTouchArea(this.playAgainBtn);
		this.assignedScene.unregisterTouchArea(this.mainMenuBtn);
	}

	@Override
	public boolean onTouch(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
			float pTouchAreaLocalY, Entity touchedEntity) {

		if(pSceneTouchEvent.isActionUp()) {
			if(touchedEntity == this.playAgainBtn && this.isVisible()) {
				Log.v(TAG, "Play again!");
				this.setVisible(false);
				this.unregisterTouches();
				PlayerObserver.getInstance().reset(); //reset game
				PiecePlacementInputHandler.getInstance().reset(); //reset piece placement input handler
				GameStateManager.getInstance().reportNewGame();
				SceneManager.getInstance().loadScene(SceneList.PIECE_PLACEMENT_SCENE);
				
			}
			else if(touchedEntity == this.mainMenuBtn && this.isVisible()) {
				Log.v(TAG, "Main menu!!");
				this.setVisible(false);
				this.unregisterTouches();
				PlayerObserver.getInstance().reset(); //reset game
				PiecePlacementInputHandler.getInstance().reset(); //reset piece placement input handler
				GameStateManager.getInstance().reportNewGame();
				SceneManager.getInstance().loadScene(SceneList.MAIN_MENU_SCENE);
			}
		}
		
		return true;
	}

	@Override
	public void onNotify(String notificationString, Object sender) {
		Player winner = (Player) sender;
		this.registerTouches();
		this.showResults(winner);
		
		Log.v(TAG, "NOTIFIED RESULT UI!");
	}

}
