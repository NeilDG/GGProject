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
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

import com.neildg.gamesofthegenerals.atlases.MilitarySymbolsAtlas;
import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.FontNames;
import com.neildg.gamesofthegenerals.core.FontStorage;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.entities.board.BoardManager;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.comparison.PieceHierarchy;
import com.neildg.gamesofthegenerals.input.IPieceDragOutside;
import com.neildg.gamesofthegenerals.input.PiecePlacementInputHandler;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * Represents a single piece display. Contains the piece symbol and the number.
 * Clickable.
 * @author user
 *
 */
public class PieceDisplayUI extends Rectangle implements IPieceDragOutside{

	
	private final static String TAG = "PieceDisplayUI";
	
	public final static int UI_WIDTH = 200;
	public final static int UI_HEIGHT = 200;
	
	//private Rectangle container;
	private int pieceType;
	private int pieceNumber;
	private Sprite pieceIcon;
	private Text pieceLabel;
	private Text pieceNumberLabel;
	
	
	private Font assignedFont;
	
	private boolean actionDown = false;
	
	public PieceDisplayUI(Font font, int pieceType) {
		super(0, 0, UI_WIDTH, UI_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		
		this.pieceType = pieceType;
		PiecePlacementInputHandler.getInstance().addPieceDragOutsideListener(this);
		Engine engine = EngineCore.getInstance().getEngine();
		AssetLoader assetLoader = AssetLoader.getInstance();
		
		this.assignedFont = font; //the font parameter should already be loaded.
		this.createUIContainer(engine, assetLoader);
	}
	
	private void createUIContainer(Engine engine, AssetLoader assetLoader) {
		/*this.container = new Rectangle(0, 0, UI_WIDTH, UI_HEIGHT, engine.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				Log.v(TAG, "Piece touch!");
				return true;
			}
		};*/
		this.setColor(Color.BLACK);
		this.setAlpha(0.5f);
		
		//create placeholders
		ITextureRegion pieceTexture = PieceHierarchy.getCorrespondingPieceTexture(this.pieceType);
		this.pieceIcon = new Sprite(0, 0, pieceTexture, engine.getVertexBufferObjectManager());
		float iconPosX = (UI_WIDTH * 0.5f) - (this.pieceIcon.getWidth() * 0.5f);
		float iconPosY = 10;
		this.pieceIcon.setPosition(iconPosX, iconPosY);
		
		//create labels
	    this.pieceLabel = new Text(0, 0, this.assignedFont, PieceHierarchy.getPieceLabel(this.pieceType), engine.getVertexBufferObjectManager());
	    this.pieceLabel.setHorizontalAlign(HorizontalAlign.CENTER);
	    this.pieceLabel.setAutoWrapWidth(UI_WIDTH);
	    this.pieceLabel.setAutoWrap(AutoWrap.WORDS);
	    
	    //float[] normalizedRGB = RGBNormalizer.normalizeRGB(64, 64, 64);
	    //this.pieceLabel.setColor(normalizedRGB[RGBNormalizer.RED], normalizedRGB[RGBNormalizer.GREEN], normalizedRGB[RGBNormalizer.BLUE]);
	    
	    this.pieceLabel.setPosition((UI_WIDTH * 0.5f) - (this.pieceLabel.getWidth() * 0.5f), this.pieceIcon.getY() + this.pieceIcon.getHeight() + 5);
	    
		this.pieceNumberLabel = new Text(0, 0, this.assignedFont, "x6", engine.getVertexBufferObjectManager());
		this.pieceNumberLabel.setHorizontalAlign(HorizontalAlign.CENTER);
		this.pieceNumberLabel.setPosition((UI_WIDTH * 0.5f) - (this.pieceNumberLabel.getWidth() * 0.5f), this.pieceLabel.getY() + this.pieceLabel.getHeight() + 5);
		this.pieceNumberLabel.setColor(0.0f, 1.0f, 0.0f);
		this.setPieceNumber(PieceHierarchy.getCorrespondingNumberOfPieces(this.pieceType));
	    
		this.attachChild(this.pieceIcon);
		this.attachChild(this.pieceLabel);
		this.attachChild(this.pieceNumberLabel);
	}
	
	private void setPieceNumber(int number) {
		this.pieceNumber = number;
		this.pieceNumberLabel.setText("x" + this.pieceNumber);
		this.pieceNumberLabel.setColor(0.0f, 1.0f, 0.0f);
	}
	
	public void deductPieceNumber() {
		this.pieceNumber--;
		this.pieceNumberLabel.setText("x" + this.pieceNumber);
		
		if(this.pieceNumber == 0) {
			this.pieceNumberLabel.setColor(1.0f, 0.0f, 0.0f);
			
		}
	}
	
	public void addPiecePieceNumber() {
		if(this.pieceNumber < PieceHierarchy.getMaxPiecesOfType(this.pieceType)) {
			this.pieceNumber++;
			this.pieceNumberLabel.setText("x" + this.pieceNumber);
			this.pieceNumberLabel.setColor(0.0f, 1.0f, 0.0f);
		}
	}
	
	public int getCurrentPieceNumber() {
		return this.pieceNumber;
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

		if(pSceneTouchEvent.isActionDown() && this.pieceNumber > 0) {
			//initialize a clickable board piece
			this.actionDown = true;
			BoardPiece boardPiece = new BoardPiece(0, 0, this.pieceType, SceneManager.getInstance().getCurrentScene());
			SceneManager.getInstance().getCurrentScene().registerTouchArea(boardPiece);
			boardPiece.addToBoard();
			PiecePlacementInputHandler.getInstance().holdPiece(boardPiece);
			
			this.deductPieceNumber();
		}
		
		if(pSceneTouchEvent.isActionMove() && this.actionDown) {
			BoardPiece selectedPiece = PiecePlacementInputHandler.getInstance().getSelectedPiece();
			selectedPiece.setPosition(pSceneTouchEvent.getX() - BoardPiece.FINGER_OFFSET_X, pSceneTouchEvent.getY() - BoardPiece.FINGER_OFFSET_Y);
			BoardManager.getInstance().highlightCollidedCell(selectedPiece);
		}
		
		if(pSceneTouchEvent.isActionUp()) {
			this.actionDown = false;
			BoardPiece selectedPiece = PiecePlacementInputHandler.getInstance().getSelectedPiece();
			BoardManager.getInstance().updatePiecePosition(selectedPiece);
			
			if(PiecePlacementInputHandler.getInstance().isPlacementSuccessful()) {
				PiecePlacementInputHandler.getInstance().releasePiece();
			}
			
		}
		return true;
	}
	
	@Override
	//add +1 to piece if the piece already placed in the board is dragged outside
	public void onPieceDragOutside(int pieceType) {
		if(this.pieceType == pieceType) {
			this.addPiecePieceNumber();
		}
	}
}
