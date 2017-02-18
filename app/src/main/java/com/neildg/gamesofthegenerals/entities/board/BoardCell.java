/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.board;

import org.andengine.engine.Engine;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.primitive.vbo.IRectangleVertexBufferObject;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.Constants;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseSineInOut;

import android.util.Log;

import com.neildg.gamesofthegenerals.atlases.MilitarySymbolsAtlas;
import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.NotificationListener;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameMode;
import com.neildg.gamesofthegenerals.entities.game.GameStateManager.GameState;

/**
 * Represents a board cell
 * @author user
 *
 */
public class BoardCell extends Sprite implements IBoardCell, NotificationListener {
	
	private static String TAG = "BoardCell";
	
	private BoardPiece assignedBoardPiece = null;
	
	private Rectangle boundingBox;
	
	private int row;
	private int column;
	
	private float oldX;
	private float oldY;
	
	private BoardPiece movedBoardPiece = null;

	public enum BoardCellType {
		BLACK_CELL,
		WHITE_CELL,
	}
	
	public BoardCell(float pX, float pY, BoardCellType boardCellType) {
		super(pX, pY, getTextureBasedFromType(boardCellType), EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		this.setSize(CELL_WIDTH, CELL_HEIGHT);
		
		//create bounding box
		float[] localCenter =	this.convertSceneToLocalCoordinates(this.getSceneCenterCoordinates());
		
		float bbX = localCenter[Constants.VERTEX_INDEX_X] - (BB_WIDTH * 0.5f);
		float bbY = localCenter[Constants.VERTEX_INDEX_Y] - (BB_HEIGHT * 0.5f);
		
		this.boundingBox = new Rectangle(bbX, bbY, BB_WIDTH, BB_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		this.boundingBox.setVisible(false);
		this.attachChild(this.boundingBox);
	}
	
	public void setRowAndColumn(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	
	private static ITextureRegion getTextureBasedFromType(BoardCellType boardCellType) {
		ITextureRegion cellTexture = null;
		if(boardCellType == BoardCellType.BLACK_CELL) {
			cellTexture = AssetLoader.getInstance().getTextureFromAtlas("military_symbols_atlas", MilitarySymbolsAtlas.BLACK_CELL_ID);
		}
		else if(boardCellType == BoardCellType.WHITE_CELL) {
			cellTexture = AssetLoader.getInstance().getTextureFromAtlas("military_symbols_atlas", MilitarySymbolsAtlas.WHITE_CELL_ID);
		}
		else {
			Log.e(TAG, "Invalid Cell type!");
		}
		
		return cellTexture;
	}
	
	public Rectangle getBoundingBox() {
		return this.boundingBox;
	}
	
	@Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
			ITextureRegion cellTexture;	
			if(pSceneTouchEvent.isActionDown()) {
				cellTexture = getTextureBasedFromType(BoardCell.BoardCellType.BLACK_CELL);
			}
			else {
				cellTexture = getTextureBasedFromType(BoardCell.BoardCellType.WHITE_CELL);
			}

			this.setTextureRegion((TextureRegion) cellTexture);
			
            return true;
    }
	
	@Override
	public void assignBoardPiece(BoardPiece boardPiece) {
		this.assignedBoardPiece = boardPiece;
		
	}
	
	@Override
	public void removeAssignedBoardPiece() {
		this.assignedBoardPiece = null;
		
	}
	
	public void addColorIndicator() {
		if(this.assignedBoardPiece != null) {
			this.setTextureRegion((TextureRegion)getTextureBasedFromType(BoardCellType.BLACK_CELL));
		}
		else {
			this.setTextureRegion((TextureRegion)getTextureBasedFromType(BoardCellType.WHITE_CELL));
		}
	}
	
	@Override
	public BoardPiece getAssignedBoardPiece() {
		return this.assignedBoardPiece;
	}
	
	@Override
	public void snapPieceToCell() {
		float pieceCenterX = this.assignedBoardPiece.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_X];
		float pieceCenterY = this.assignedBoardPiece.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_Y];
		
		float boardCenterX = this.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_X];
		float boardCenterY = this.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_Y];
		
		this.oldX = pieceCenterX;
		this.oldY = pieceCenterY;
		
		
		float deltaX = this.assignedBoardPiece.getX() + (boardCenterX - pieceCenterX);
		float deltaY = this.assignedBoardPiece.getY() + (boardCenterY - pieceCenterY);
		
		this.setAlpha(1.0f);
		if(GameStateManager.getInstance().getCurrentState() == GameState.MAIN_GAME) {
			
			/*if(GameStateManager.getInstance().getGameMode() == GameMode.VERSUS_HUMAN_LOCAL) {
				this.movedBoardPiece = this.assignedBoardPiece;
				NotificationCenter.getInstance().removeObserver(Notifications.ON_BOARD_PIECE_SNAPPED, this);
				NotificationCenter.getInstance().addObserver(Notifications.ON_BOARD_PIECE_SNAPPED, this); //only one listener at a time
			}*/
			
			this.assignedBoardPiece.registerEntityModifier(new MoveByModifier(0.5f, boardCenterX - pieceCenterX, boardCenterY - pieceCenterY, null));
		}
		else if(GameStateManager.getInstance().getCurrentState() == GameState.PIECE_PLACEMENT) {
			this.assignedBoardPiece.setPosition(deltaX, deltaY);
		}
	}

	@Override
	public int getRow() {
		return this.row;
	}

	@Override
	public int getColumn() {
		return this.column;
	}

	//replays the move modifier when notified.
	@Override
	public void onNotify(String notificationString, Object sender) {
		
		if(this.movedBoardPiece == null) {
			return;
		}
		
		//reset position
		//this.movedBoardPiece.setPosition(this.oldX, this.oldY);
		
		float pieceCenterX = this.movedBoardPiece.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_X];
		float pieceCenterY = this.movedBoardPiece.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_Y];
		
		float boardCenterX = this.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_X];
		float boardCenterY = this.getSceneCenterCoordinates()[Constants.VERTEX_INDEX_Y];
		
		
		//reanimate
		this.movedBoardPiece.registerEntityModifier(new MoveByModifier(0.5f, boardCenterX - pieceCenterX, boardCenterY - pieceCenterY, null));
		this.movedBoardPiece = null;
	}
	
	

}
