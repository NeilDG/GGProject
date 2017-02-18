package com.neildg.gamesofthegenerals.entities;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.opengl.util.GLState;
 
import android.opengl.GLES10;

/*
 * Represents a clipping entity
 */
public class ClippingEntity extends Entity {
        private int width;
        private int height;
       
        public ClippingEntity(float x, float y, int width, int height) {
                super(x, y);
                this.setWidth(width);
                this.setHeight(height);
        }
       
        @Override
    protected void onManagedDraw(final GLState glState, final Camera camera) {
        float[] coords = convertLocalToSceneCoordinates(getX(), getY());
        
        super.onManagedDraw(glState, camera);
        
        glState.pushProjectionGLMatrix();
        glState.enableScissorTest();
         //invert Y coordinate as needed by OpenGL
        final int y = camera.getSurfaceHeight() - height - (int)coords[1];
        GLES10.glScissor((int)coords[0], y,
                        getWidth(), getHeight());
        super.onManagedDraw(glState, camera);
        glState.disableScissorTest();
        glState.popProjectionGLMatrix();
         
        }
       
        private int getWidth() {
                return width;
        }
 
        private void setWidth(int width) {
                this.width = width;
        }
 
        private int getHeight() {
                return height;
        }
 
        private void setHeight(int height) {
                this.height = height;
        }
}