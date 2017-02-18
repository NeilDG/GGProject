/**
 * 
 */
package com.neildg.gamesofthegenerals.layout.killedpieces;

import org.andengine.engine.Engine;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.util.modifier.IModifier;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.ResolutionManager;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.NotificationListener;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * Display panel for all killed pieces
 * @author NeilDG
 *
 */
public class KilledPieceDisplay extends Rectangle implements NotificationListener{
	
	public final static int UI_WIDTH = 150;
	public final static int UI_HEIGHT = ResolutionManager.SCENE_HEIGHT;
	
	private Rectangle background;
	private BoardPiece lastPieceKilled;
	
	private float currentX = 0.0f;
	private float currentY = 5.0f;
	
	
	private final float yIncrement = BoardPiece.PIECE_HEIGHT + currentY;
	
	public KilledPieceDisplay() {
		super(0, 0, UI_WIDTH, UI_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		
		Engine engine = EngineCore.getInstance().getEngine();
				
		this.setRectColor(engine);
		
	}
	
	//sets the specific notification string to listen to
	public void setNotificationString(String notifString) {
		NotificationCenter.getInstance().addObserver(notifString, this);
	}
	
	private void setRectColor(Engine engine) {
		float[] normalizedRGB = RGBNormalizer.normalizeRGB(139, 119, 101);
		this.setColor(normalizedRGB[RGBNormalizer.RED], normalizedRGB[RGBNormalizer.GREEN], normalizedRGB[RGBNormalizer.BLUE]); //peach puff
		
	}

	
	//sender will be the killed piece to be sent into the display
	@Override
	public void onNotify(String notificationString, Object sender) {
		// TODO Auto-generated method stub
		this.lastPieceKilled = (BoardPiece) sender;
		
		//resize last piece
		this.lastPieceKilled.setScale(0.75f);
		
		float oldX = this.lastPieceKilled.getX();
		float oldY = this.lastPieceKilled.getY();
		
		// TODO Auto-generated method stub
		Engine engine = EngineCore.getInstance().getEngine();
		
		engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	KilledPieceDisplay.this.lastPieceKilled.detachSelf();
            	KilledPieceDisplay.this.attachChild(KilledPieceDisplay.this.lastPieceKilled);

            }
        });
		
		if(notificationString == Notifications.ON_KILLED_PLAYER_ONE_PIECE) {
			this.lastPieceKilled.registerEntityModifier(new MoveModifier(1.0f,oldX, this.getX() + this.currentX, 
					oldY, this.currentY));
		}
		else {
			this.lastPieceKilled.registerEntityModifier(new MoveModifier(1.0f,oldX - this.getX(), this.currentX, 
					oldY, this.currentY));
		}
		
		this.currentY += this.yIncrement;
		
		if(this.currentY >= UI_HEIGHT) {
			this.currentX += BoardPiece.PIECE_WIDTH - 10.0f;
			this.currentY =  5.0f;
		}
	}

}
