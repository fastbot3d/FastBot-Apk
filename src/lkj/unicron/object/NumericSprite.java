/*
 * Copyright (C) 2008 The Android Open Source Project
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

import javax.microedition.khronos.opengles.GL10;

import lkj.unicron.util.Log;

import android.graphics.Paint;

public class NumericSprite {
	
    float mWidth, mHeight;
	private Boolean mVisable = false;
	private final Object mLock = new Object();
	
    public NumericSprite() {
        mText = "";
        mLabelMaker = null;
    }
    
    public void initialize(GL10 gl, float viewWidth, float viewHeight, Paint paint) {
        mWidth = viewWidth;
        mHeight = viewHeight;
        
        int height = roundUpPower2((int) paint.getFontSpacing());
        final float interDigitGaps = 9 * 1.0f;
        int width = roundUpPower2((int) (interDigitGaps + paint.measureText(sStrike)));
        mLabelMaker = new LabelMaker(true, width, height);
        mLabelMaker.initialize(gl);
        mLabelMaker.beginAdding(gl);
        for (int i = 0; i < 10; i++) {
            String digit = sStrike.substring(i, i+1);
            mLabelId[i] = mLabelMaker.add(gl, digit, paint);
            mDigitWidth[i] = (int) Math.ceil(mLabelMaker.getWidth(i));
        }
        mLabelMaker.endAdding(gl);
    }

    public void shutdown(GL10 gl) {
        mLabelMaker.shutdown(gl);
        mLabelMaker = null;
    }

    /**
     * Find the smallest power of two >= the input value.
     * (Doesn't work for negative numbers.)
     */
    private int roundUpPower2(int x) {
        x = x - 1;
        x = x | (x >> 1);
        x = x | (x >> 2);
        x = x | (x >> 4);
        x = x | (x >> 8);
        x = x | (x >>16);
        return x + 1;
    }

    public void setValue(int value) {
		synchronized (mLock) {
	        mText = format(value);
        }
    }
    
    public void setVisable(Boolean v){
		synchronized (mLock) {
			mVisable = v;
		}
    }
    
    public void draw(GL10 gl, float x, float y) {
		synchronized (mLock) {
		if(!mVisable){
			return;
		}
        int length = mText.length();
        mLabelMaker.beginDrawing(gl, mWidth, mHeight);
        for(int i = 0; i < length; i++) {
            char c = mText.charAt(i);
            
            int digit = c - '0';
            //Log.d("char c=" + c + ", digit=" + digit);
            mLabelMaker.draw(gl, x, y, mLabelId[digit]);
            x += mDigitWidth[digit];
        }
        mLabelMaker.endDrawing(gl);
		}
    }

    public float width() {
        float width = 0.0f;
        int length = mText.length();
        for(int i = 0; i < length; i++) {
            char c = mText.charAt(i);
            width += mDigitWidth[c - '0'];
        }
        return width;
    }

    private String format(int value) {
        return Integer.toString(value);
    }

    private LabelMaker mLabelMaker;
    private String mText;
    private int[] mDigitWidth = new int[10];
    private int[] mLabelId = new int[10];
    private final static String sStrike = "0123456789";
}
