/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.board;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.Constants;
import org.andengine.util.modifier.ease.EaseSineInOut;

import android.util.Log;

import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.entities.comparison.PieceHierarchy;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameMode;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameState;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

/**
 * Represents a board piece to be added to the board cell
 * @author user
 *
 */
public class BoardPiece extends Sprite {

	public static int PIECE_WIDTH = 80;
	public static int PIECE_HEIGHT = 60;
	
	//we use this finger offsets for dragging so the user can see the piece under their fingers
	public static float FINGER_OFFSET_X = 90;
	public static float FINGER_OFFSET_Y = 70;
	
	private static String TAG = "BoardPiece";
	private static String militarySymbolsAtlasName = "military_symbols_atlas";
	
	private Entity boardContainer;
	
	private IBoardCell assignedBoardCell; //the current cell that the piece is placed.
	private int pieceType;
	private int pieceID; //represents the piece ID to be use in the board state for easier finding
	
	private boolean markedUnknown = false;
	
	private boolean mouseDown = false;
	
	public BoardPiece(float x, float y, int pieceType, Entity boardContainer) {
		super(x, y, AssetLoader.getInstance().getTextureFromAtlas(militarySymbolsAtlasName, PieceHierarchy.getCorrespondingPieceAtlasID(pieceType)), 
				EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		this.pieceType = pieceType;
		this.setSize(PIECE_WIDTH, PIECE_HEIGHT);
		this.boardContainer = boardContainer;
	}
	
	public void markAsUnknown() {
		this.markedUnknown = true;
		TextureRegion unknownTexture = (TextureRegion) AssetLoader.getInstance().getTextureFromAtlas(militarySymbolsAtlasName, PieceHierarchy.getCorrespondingPieceAtlasID(PieceHierarchy.UNKNOWN));
		this.setTextureRegion(unknownTexture);
	}
	
	public boolean isPieceUnknown() {
		return this.markedUnknown;
	}
	
	public void revealPiece() {
		this.markedUnknown = false;
		TextureRegion texture = (TextureRegion) AssetLoader.getInstance().getTextureFromAtlas(militarySymbolsAtlasName, PieceHierarchy.getCorrespondingPieceAtlasID(pieceType));
		this.setTextureRegion(texture);
	}
	
	public void setPieceID(int pieceID) {
		Log.v(TAG, "Setting piece IDs: " +pieceID);
		this.pieceID = pieceID;
	}
	
	public int getPieceID() {
		return this.pieceID;
	}
	
	public int getPieceType() {
		return this.pieceType;
	}
	
	public void addToBoard() {
		this.boardContainer.attachChild(this);
	}
	
	public void setBoardContainer(Entity boardContainer) {
		this.boardContainer = boardContainer;
	}
	
	public void placePieceToCell(IBoardCell boardCell) {
		
		float oldX = this.getX();
		float oldY = this.getY();
		
		//remove piece from old cell
		if(this.assignedBoardCell != null) {
			this.assignedBoardCell.removeAssignedBoardPiece();
		}
		
		this.assignedBoardCell = boardCell;
		this.assignedBoardCell.assignBoardPiece(this);
		this.assignedBoardCell.snapPieceToCell();
		
		
	}
	
	public IBoardCell getBoardCell() {
		return this.assignedBoardCell;
	}
	
	@Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		
		//do not listen to any events if AWAITING MAIN GAME
		if(GameStateManager.getInstance().getCurrentState() == GameState.AWAITING_MAIN_GAME) {
			return true;
		}
		
		
		Player activePlayer = PlayerObserver.getInstance().getActivePlayer();
		
		//restrict some conditions for touch
		if(pSceneTouchEvent.isActionDown() && activePlayer.isPieceOwned(this)) {
			
			//if local play, only the active player gains the touch privilege
			if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_LOCAL && activePlayer.isPieceOwned(this)) {
				this.mouseDown = true;
			}
			//if versus computer, you are only allowed to move your own piece
			else if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_COMPUTER && activePlayer == PlayerObserver.getInstance().getPlayerOne()
					&& activePlayer.isPieceOwned(this)) {
				this.mouseDown = true;
			}
			//if versus adhoc, you are only allowed to move your own piece during your turn.
			else if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_ADHOC && activePlayer == PlayerObserver.getInstance().getPlayerOne()
					&& activePlayer.isPieceOwned(this)) {
				this.mouseDown = true;
			}
			
			Log.v(TAG, "Board piece " + this.getPieceType()+ " down!");
		}
		
		
		if(this.mouseDown) {
			float deltaX = pSceneTouchEvent.getX() - this.boardContainer.getX();
			float deltaY = pSceneTouchEvent.getY() - this.boardContainer.getY();
			
			this.setPosition(deltaX - FINGER_OFFSET_X, deltaY -FINGER_OFFSET_Y);
			BoardManager.getInstance().highlightCollidedCell(this);
		}
		
		//snap piece to corresponding board cell
		if(pSceneTouchEvent.isActionUp()) {
			this.mouseDown = false;
			BoardManager.getInstance().updatePiecePosition(this);
		}
		 
         return true;
    }
	
	public Player getPlayerOwner() {
		Player playerOne = PlayerObserver.getInstance().getPlayerOne();
		Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
		
		if(playerOne.isPieceOwned(this)) {
			return playerOne;
		}
		if(playerTwo.isPieceOwned(this)) {
			return playerTwo;
		}
		else {
			throw new RuntimeException("No one owns piece type " +this.pieceType+ " ! Please check.");
		}
	}
	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed)
	{
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	public void destroy() {
		SceneManager.getInstance().getCurrentScene().unregisterTouchArea(this);
		IBoardCell assignedBoardCell = this.getBoardCell();
		if(assignedBoardCell != null) {
			assignedBoardCell.removeAssignedBoardPiece();
		}
		
		if(GameStateManager.getInstance().getCurrentState() == GameState.PIECE_PLACEMENT) {
			//this.setVisible(false);
			this.detachSelf();
		}
		
	}
	
}
