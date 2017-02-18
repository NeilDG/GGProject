package com.neildg.gamesofthegenerals.entities;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.util.Constants;

import android.util.Log;
 
/*
 * Scroll entity used for scrollable entities with clipping.
 * Customized by snapping the content entity to its valid position if overscrolled.
 * 
 * created BY: Neil DG
 */
public class ScrollEntity extends Entity implements IOnSceneTouchListener,
                IScrollDetectorListener {
	
		private static String TAG = "ScrollEntity";
		
		private final float MIN_DISPLACEMENT = 30;
		
        private SurfaceScrollDetector scrollDetector;
        private IEntity contentEntity;
        
        private float startX;
        private float startY;
        
        public enum ScrollType {
        	HORIZONTAL,
        	VERTICAL
        }
        
        private ScrollType scrollType;
 
        public ScrollEntity(float startX, float startY, int width, int height, final IEntity content, ScrollType scrollType) {
                super(startX, startY);
               
                this.startX = startX;
                this.startY = startY;
                
                
                this.scrollDetector = new SurfaceScrollDetector(this);
                this.scrollType = scrollType;
               
                setContent(content);
        }
 
        public void setContent(IEntity content) {
                if(contentEntity != null && contentEntity.hasParent()) {
                        contentEntity.detachSelf();
                        contentEntity.dispose();
                }
                contentEntity = content;
                attachChild(contentEntity);
        }
       
        @Override
        public boolean onSceneTouchEvent(Scene scene, TouchEvent event) {
                if(event.isActionDown()) {
                        scrollDetector.setEnabled(true);
                }
                scrollDetector.onTouchEvent(event);
                return true;
        }
 
        @Override
        public void onScrollStarted(ScrollDetector detector, int pointerID, float distanceX, float distanceY) {
        }
        
        private boolean isWithinMinBounds(float distanceX, float distanceY) {
        	boolean result = true;
        	
        	if(this.scrollType == ScrollType.HORIZONTAL && this.contentEntity.getX() - MIN_DISPLACEMENT >= 0 && distanceX >= 0) {
        		result = false;
        	}
        	else if(this.scrollType == ScrollType.VERTICAL && this.contentEntity.getY() - MIN_DISPLACEMENT >= 0 && distanceY >= 0) {
        		result = false;
        	}
        	
        	return result;
        }
        
        private boolean isWithinMaxBounds(float distanceX, float distanceY) {
        	boolean result = true;
        	
        	IEntity lastChild = this.contentEntity.getLastChild();
        	
        	if(lastChild == null) {
        		return true;
        	}
        	
        	float[] lastChildPos = this.contentEntity.convertLocalToSceneCoordinates(lastChild.getX(), lastChild.getY());
        	lastChildPos[Constants.VERTEX_INDEX_X] -= this.startX;
        	Log.v(TAG, "Last Child X: " +lastChildPos[Constants.VERTEX_INDEX_X]+ "Content Entity X: " +this.contentEntity.getX());
        	
        	if(this.scrollType == ScrollType.HORIZONTAL && lastChildPos[Constants.VERTEX_INDEX_X] - MIN_DISPLACEMENT <= this.startX && distanceX <= 0) {
        		result = false;
        	}
        	else if(this.scrollType == ScrollType.VERTICAL && lastChildPos[Constants.VERTEX_INDEX_Y] - MIN_DISPLACEMENT <= this.startX && distanceY <= 0) {
        		result = false;
        	}
        	
        	return result;
        }
 
        @Override
        public void onScroll(ScrollDetector detector, int pointerID, float distanceX, float distanceY) {
                if(contentEntity != null) {
                		//vertical scroll
                		if(this.scrollType == ScrollType.VERTICAL) {
                			float y = contentEntity.getY() + distanceY;
                            contentEntity.setPosition(contentEntity.getX(), y);
                		}
                		//horizontal scroll
                		else if(this.scrollType == ScrollType.HORIZONTAL) {
                			float x = contentEntity.getX() + distanceX;
                			contentEntity.setPosition(x, contentEntity.getY());
                		}	
                }
        }
 
        @Override
        public void onScrollFinished(ScrollDetector detector, int pointerID, float distanceX, float distanceY) {

        	//snap to place if exceeded
        	if(contentEntity != null) {
        		//vertical scroll
        		if(this.scrollType == ScrollType.VERTICAL) {
        			//snap to original position
        			if(!isWithinMinBounds(distanceX, distanceY)) {
        				this.contentEntity.setPosition(this.contentEntity.getX(), this.startY);
        			}
        			if(!isWithinMaxBounds(distanceX, distanceY)) {
        				this.contentEntity.setPosition(this.contentEntity.getX(), this.startY);
        			}
        			
        		}
        		//horizontal scroll
        		else if(this.scrollType == ScrollType.HORIZONTAL) {
        			//snap to original position
        			if(!isWithinMinBounds(distanceX, distanceY)) {
        				this.contentEntity.setPosition(this.startX, this.contentEntity.getY());
        			}
        			if(!isWithinMaxBounds(distanceX, distanceY)) {
        				this.contentEntity.setPosition(this.startX, this.contentEntity.getY());
        			}
        		}
        		
        		
        }
        }
}