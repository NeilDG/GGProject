/**
 * 
 */
package com.neildg.gamesofthegenerals.layout.pieceselection;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.json.JSONException;

import android.graphics.Typeface;
import android.util.Log;

import com.neildg.gamesofthegenerals.atlases.ArrowsAtlas;
import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.FontNames;
import com.neildg.gamesofthegenerals.core.FontStorage;
import com.neildg.gamesofthegenerals.core.ResolutionManager;
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.core.extension.IOnTouchListener;
import com.neildg.gamesofthegenerals.core.extension.SimpleButton;
import com.neildg.gamesofthegenerals.core.extension.TouchableSprite;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.ClippingEntity;
import com.neildg.gamesofthegenerals.entities.ScrollEntity;
import com.neildg.gamesofthegenerals.entities.ScrollEntity.ScrollType;
import com.neildg.gamesofthegenerals.entities.board.BoardManager;
import com.neildg.gamesofthegenerals.entities.comparison.PieceHierarchy;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameState;
import com.neildg.gamesofthegenerals.entities.game.TurnManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameMode;
import com.neildg.gamesofthegenerals.entities.multiplayer.DataInterpreter;
import com.neildg.gamesofthegenerals.entities.multiplayer.SocketManager;
import com.neildg.gamesofthegenerals.entities.openings.OpeningMovesLibrary;
import com.neildg.gamesofthegenerals.entities.openings.OpeningMovesSaver;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;
import com.neildg.gamesofthegenerals.input.IPieceDragOutside;
import com.neildg.gamesofthegenerals.input.PiecePlacementInputHandler;
import com.neildg.gamesofthegenerals.layout.prompts.ChangeTurnUI;
import com.neildg.gamesofthegenerals.layout.prompts.ConfirmListener;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * Represents the bottom UI where the user can drag and drop pieces in the board for placement.
 * Still a placeholder.
 * @author user
 *
 */
public class PieceSelectionUI extends Entity implements IOnTouchListener, ConfirmListener{
	
	private final static String TAG = "PieceSelectionUI";
	
	public final static int UI_WIDTH = ResolutionManager.SCENE_WIDTH;
	public final static int UI_HEIGHT = 300;
	
	public final static int DRAGGABLE_AREA_WIDTH = UI_WIDTH - 345;
	public final static int DRAGGABLE_AREA_HEIGHT = UI_HEIGHT;
	
	public final static float DISTANCE_PER_PIECE_UI = PieceDisplayUI.UI_WIDTH + 50;
	
	private final static float SCROLL_SPEED = 40.0f;
	
	private static String arrowsAtlas = "arrows_atlas";
	
	private Rectangle background;
	private Entity scrollContent;
	private ScrollEntity draggableArea;
	
	private TouchableSprite leftArrowSprite;
	private TouchableSprite rightArrowSprite;
	
	private Rectangle doneButton;
	private SimpleButton saveButton;
	private OpeningMovesSaver openingSaver;
	
	private boolean actionDown = false;
	private TouchableSprite touchedSprite;
	
	private Scene assignedScene;
	
	public PieceSelectionUI() {
		super();
		
		PiecePlacementInputHandler.getInstance().setPieceSelectionUI(this);
		
		Engine engine = EngineCore.getInstance().getEngine();
		
		this.createUIContainer(engine);
		this.createTexts(engine);
		this.createDraggableArea(engine);
		this.createDraggablePieceUI(engine);
		this.createButtonSprites(engine);

	}
	
	
	private void createUIContainer(Engine engine) {
		this.background = new Rectangle(0, 0, UI_WIDTH, UI_HEIGHT, engine.getVertexBufferObjectManager());
		float[] normalizedRGB = RGBNormalizer.normalizeRGB(139, 119, 101);
		
		this.background.setColor(normalizedRGB[RGBNormalizer.RED], normalizedRGB[RGBNormalizer.GREEN], normalizedRGB[RGBNormalizer.BLUE]); //peach puff
		
		this.attachChild(this.background);
	}
	
	private void createButtonSprites(Engine engine) {
		//create left and right clickable arrows
		this.leftArrowSprite = new TouchableSprite(20, 50, AssetLoader.getInstance().getTextureFromAtlas(arrowsAtlas, ArrowsAtlas.LEFT_ARROW_ID), engine.getVertexBufferObjectManager(), this);
		this.rightArrowSprite = new TouchableSprite(UI_WIDTH - 150, 50, AssetLoader.getInstance().getTextureFromAtlas(arrowsAtlas, ArrowsAtlas.RIGHT_ARROW_ID), engine.getVertexBufferObjectManager(), this);
		 
		this.background.attachChild(leftArrowSprite);
		this.background.attachChild(rightArrowSprite);
	}
	
	private void createDraggableArea(Engine engine) {
		float posX = 80;
		float posY = 60;
		
		this.scrollContent = new Entity(posX, posY);

	}
	
	private void createTexts(Engine engine) {
		
		 //create corresponding JSON writer
		 this.openingSaver = new OpeningMovesSaver();
		 
		 Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 45, false);
		 font.load();

	     Text instructionsText = new Text(0, 0, font, "Place Your Pieces", engine.getVertexBufferObjectManager());
	     instructionsText.setHorizontalAlign(HorizontalAlign.CENTER);
	     instructionsText.setPosition((UI_WIDTH * 0.5f) - (instructionsText.getWidth() * 0.5f), 0);

	     //create done button
	     float doneButtonWidth = 150; float doneButtonHeight = 70;
		 this.doneButton = new Rectangle(UI_WIDTH - 170, -90, doneButtonWidth, doneButtonHeight, engine.getVertexBufferObjectManager()) {
			 @Override
			    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					if(pSceneTouchEvent.isActionDown() && this.isVisible()) {
						this.setScale(1.2f);
					}
					else if(pSceneTouchEvent.isActionUp() && this.isVisible()) {
						this.setScale(1.0f);
						PieceSelectionUI.this.processPiecePlacement();
					}
					return true;
				}
		 };
		 this.doneButton.setColor(Color.GREEN);
		 Text doneText = new Text(0,0, font, "DONE", engine.getVertexBufferObjectManager());
		 doneText.setPosition((doneButtonWidth * 0.5f) - (doneText.getWidth() * 0.5f), (doneButtonHeight * 0.5f) - (doneText.getHeight() * 0.5f));
		 this.doneButton.attachChild(doneText);
		 this.doneButton.setVisible(false);
		 
		 //create save button
		 this.saveButton = new SimpleButton(UI_WIDTH - 170, this.doneButton.getY() - 90, doneButtonWidth, doneButtonHeight, this, engine.getVertexBufferObjectManager());
		 this.saveButton.setColor(Color.RED);
		 this.saveButton.addText("SAVE", font);
		 this.saveButton.setVisible(false);
		
		
		 
	     this.attachChild(instructionsText);
	     this.attachChild(this.doneButton);
	     this.attachChild(this.saveButton);
	}
	
	//processes the piece placement. A new piece selection UI is made if the game mode is local versus
	public void processPiecePlacement() {
		
		
		if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_COMPUTER) {
			this.openingSaver.saveBoardLayoutToExternal();
			GameStateManager.getInstance().reportPiecePlacementDone();
			SceneManager.getInstance().loadScene(SceneList.GAME_SCENE);
		}
		else if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_LOCAL) {
			this.setVisible(false);
			this.unregisterTouches();
			
			//verify if both players already has pieces placed
			Player playerOne = PlayerObserver.getInstance().getPlayerOne();
			Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
			
			if(playerOne.getAlivePiecesCount() > 0 && playerTwo.getAlivePiecesCount() > 0) {
				GameStateManager.getInstance().reportPiecePlacementDone();
				TurnManager.getInstance().reportSuccessfulTurn();
				SceneManager.getInstance().loadScene(SceneList.GAME_SCENE);
			}
			else {
				TurnManager.getInstance().reportSuccessfulTurn();
				//create change turn UI
				ChangeTurnUI changeTurnUI = new ChangeTurnUI(this.assignedScene);
				changeTurnUI.setConfirmListener(this);
				changeTurnUI.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.5f);
				this.assignedScene.attachChild(changeTurnUI);
				
				//notifies the change turn UI
				NotificationCenter.getInstance().postNotification(Notifications.ON_FINISHED_PLAYER_TURN_LOCAL, this);
			}
			
		}
		
		else if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_ADHOC) {
			//this.setVisible(false);
			this.unregisterTouches();
			this.doneButton.setVisible(false);
			GameStateManager.getInstance().setCurrentState(GameState.AWAITING_MAIN_GAME);
			NotificationCenter.getInstance().postNotification(Notifications.ON_WAITING_PLAYER_TURN, this);
			SocketManager.getInstance().sendMessage(DataInterpreter.PIECE_PLACEMENT_READING);
			
			//add a pause so the message won't be cut on the receiving client
			/*try {
				Thread.sleep(1000);
			} catch (final Throwable t) {
				Log.e(TAG,t.toString());
			}*/
			
			try {
				SocketManager.getInstance().sendMessage(this.openingSaver.convertPiecePlacementToJSON().toString(1));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//check if the remote device has already setup a board state
			if(OpeningMovesLibrary.getInstance().getAdHocBoardState() != null) {
				NotificationCenter.getInstance().postNotification(Notifications.ON_FINISHED_PLAYER_TURN_REMOTE, this);
			}
			
		}
		
	}
	
	//on confirm change turn UI
	@Override
	public void onConfirm() {
		//reload piece placement scene
		SceneManager.getInstance().loadScene(SceneList.PIECE_PLACEMENT_SCENE);
	}
	
	private void createDraggablePieceUI(Engine engine) {
		
		float startX = 10;
		
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, Color.WHITE_ABGR_PACKED_INT);
		font.load();
	     
		for(int i = 0; i < PieceHierarchy.KIND_OF_PIECES; i++) {
			PieceDisplayUI pieceUI = new PieceDisplayUI(font, i);
			pieceUI.setPosition(startX, 20);
			startX += DISTANCE_PER_PIECE_UI;
			this.scrollContent.attachChild(pieceUI);
		}
		
		this.draggableArea = new ScrollEntity(this.scrollContent.getX(), 0, DRAGGABLE_AREA_WIDTH, DRAGGABLE_AREA_HEIGHT, this.scrollContent, ScrollType.HORIZONTAL);
		this.background.attachChild(this.draggableArea);
	}
	
	//register touchable areas in scene
	public void registerTouches(Scene assignedScene) {
			this.assignedScene = assignedScene;
			this.assignedScene.registerTouchArea(this.leftArrowSprite);
			this.assignedScene.registerTouchArea(this.rightArrowSprite);
			this.assignedScene.setOnSceneTouchListener(this.draggableArea);
			this.assignedScene.registerTouchArea(this.doneButton);
			this.assignedScene.registerTouchArea(this.saveButton);
			for(int  i = 0; i < this.scrollContent.getChildCount(); i++) {
				PieceDisplayUI pieceUI = (PieceDisplayUI) this.scrollContent.getChildByIndex(i);
				this.assignedScene.registerTouchArea(pieceUI);
			}
	}
	
	private void unregisterTouches() {
		this.assignedScene.unregisterTouchArea(this.leftArrowSprite);
		this.assignedScene.unregisterTouchArea(this.rightArrowSprite);
		this.assignedScene.setOnSceneTouchListener(null);
		this.assignedScene.unregisterTouchArea(this.doneButton);
		this.assignedScene.unregisterTouchArea(this.saveButton);
		for(int  i = 0; i < this.scrollContent.getChildCount(); i++) {
			PieceDisplayUI pieceUI = (PieceDisplayUI) this.scrollContent.getChildByIndex(i);
			this.assignedScene.unregisterTouchArea(pieceUI);
		}
	}


	@Override
	public boolean onTouch(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
			float pTouchAreaLocalY, Entity touchedEntity) {
		if(pSceneTouchEvent.isActionDown()) {
			this.actionDown = true;
			
			try {
			this.touchedSprite = (TouchableSprite) touchedEntity;
			} catch(ClassCastException e) {
				Log.v(TAG, "Cannot convert. Skipping");
			}
		}

		else if(pSceneTouchEvent.isActionUp()) {
			this.actionDown = false;
			
			if(touchedEntity == this.leftArrowSprite) {
				this.draggableArea.onScrollFinished(null, 0, SCROLL_SPEED, 0.0f);
			}
			else if(touchedEntity == this.rightArrowSprite) {
				this.draggableArea.onScrollFinished(null, 0, -SCROLL_SPEED, 0.0f);
			}
			else if(touchedEntity == this.saveButton) {
				this.openingSaver.saveBoardLayoutToExternal();
			}
			
		}
		
		return true;
	}
	
	
	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		
		if(this.actionDown) {
			if(this.touchedSprite == this.leftArrowSprite) {
				this.draggableArea.onScroll(null, 0, SCROLL_SPEED, 0.0f);
			}
			else if(this.touchedSprite == this.rightArrowSprite) {
				this.draggableArea.onScroll(null, 0,-SCROLL_SPEED, 0.0f);
			}
		}
	}

	public void verifyIfAllPiecesPlaced() {
		for(int  i = 0; i < this.scrollContent.getChildCount(); i++) {
			PieceDisplayUI pieceUI = (PieceDisplayUI) this.scrollContent.getChildByIndex(i);
			if (pieceUI.getCurrentPieceNumber() != 0) {
				this.doneButton.setVisible(false);
				this.saveButton.setVisible(false);
				return;
			}
		}
		
		//if all pieces are placed, show done button
		this.doneButton.setVisible(true);
	}



		
}
