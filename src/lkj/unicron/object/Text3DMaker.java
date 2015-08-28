/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lkj.unicron.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.opengl.GLUtils;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;


public class Text3DMaker {

	GL10 gl;
	float mViewWidth;
	float mViewHeight;
	
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int mTextureID;
	private int mStrikeHeight =200;
	private int mStrikeWidth = 200;
	
    public Text3DMaker() {
    	
    }


    public void initialize(GL10 gl, float width, float height) {

    	mViewWidth = width;
    	mViewHeight = height;
    	
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        mTextureID = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);

        // Use Nearest for performance.
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);

        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_REPLACE);
    }
    
    int u=0, v=0;
    int textHeight =0 , textWidth=0;
    int mUV[] = new int[4]; 
    
    public void setText(GL10 gl, String text, Paint textPaint) {

        Bitmap.Config config = Bitmap.Config.ARGB_4444 ;

		mBitmap = Bitmap.createBitmap(mStrikeWidth, mStrikeHeight, config);
        mCanvas = new Canvas(mBitmap);
        mBitmap.eraseColor(0);
    	
        int ascent = 0;
        int descent = 0;
        int measuredTextWidth = 0;
        // Paint.ascent is negative, so negate it.
        ascent = (int) Math.ceil(-textPaint.ascent());
        descent = (int) Math.ceil(textPaint.descent());
        measuredTextWidth = (int) Math.ceil(textPaint.measureText(text));
            
        textHeight = ascent + descent;
        textWidth = Math.min(mStrikeWidth,measuredTextWidth);
        u=0;
        v=0;
        
        mUV[0]= u;
        mUV[1]= v + textHeight;
        mUV[2]= textWidth;
        mUV[3]= -textHeight;
        
    	mCanvas.drawText(text,0, 0, textPaint);
    	   
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
        // Reclaim storage used by bitmap and canvas.
        mBitmap.recycle();
        mBitmap = null;
        mCanvas = null;
    }
    
    public void draw(GL10 gl, float x, float y) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrthof(0.0f, mViewWidth, 0.0f, mViewHeight, 0.0f, 1.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        // Magic offsets to promote consistent rasterization.
        gl.glTranslatef(0.375f, 0.375f, 0.0f);
    	
        gl.glEnable(GL10.GL_TEXTURE_2D);
        ((GL11)gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
                GL11Ext.GL_TEXTURE_CROP_RECT_OES, mUV, 0);
        ((GL11Ext)gl).glDrawTexiOES((int) x, (int) y, 0, textWidth, textHeight);
        
        gl.glDisable(GL10.GL_BLEND);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPopMatrix();
        
        gl.glDisable(GL10.GL_TEXTURE_2D);//lkj add
    }

}
