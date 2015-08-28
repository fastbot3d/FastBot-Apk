package lkj.unicron.object;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import lkj.unicron.util.Log;

import org.join.ogles.lib.GLColor;
import org.join.ogles.lib.Vector3f;

import android.graphics.Paint;

public class ArgCircle {
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;

	private final Object mLock = new Object();
	
	int vCount = 0;
	
	private float mLineWidth = 3.0f;
	private Boolean mVisable = true;
	private float mStartDegree = 0.0f;
	private float mEndDegree = 0.0f;
    private IntBuffer   mColorBuffer;
    
    public NumericSprite mNumericXSprite;
    private Paint mXLabelPaint;
    float mNumericX_x = 0;
    float mNumericX_z = 0;
    
	public ArgCircle() {
		// TODO Auto-generated constructor stub
		
		 mNumericXSprite = new NumericSprite();
    	 mXLabelPaint = new Paint();
         mXLabelPaint.setTextSize(32);
         mXLabelPaint.setAntiAlias(true);
         mXLabelPaint.setARGB(0xff, 0x2f, 0x3f, 0x5f); // foreground is white, alpa is transparent 

         
	}
	
	float mWidth, mHeight;
	public void setTextSprite(GL10 gl, float width, float height){
    	mNumericXSprite.initialize(gl, width, height, mXLabelPaint);
        mWidth = width; 
        mHeight = height;
    }
	
	public void set2DOrignPoint(float x, float y){
		
	    mNumericX_x = x + 10;
	    mNumericX_z = mHeight - y - 10; 
	    
		Log.d("mNumericX_x=" + mNumericX_x + ",mNumericX_z=" + mNumericX_z);
    }
	
	public void setLabelDegree(float degree){
		synchronized (mLock) {
			mNumericXSprite.setValue(Math.abs((int) degree));
			mNumericXSprite.setVisable(true);
		}
    }
	
	public void setEndArgCircle() {
			mStartDegree = 0.0f;
			mEndDegree = 0.0f;
	}
	
	public void setStartArgCircle() {
			mStartDegree = mEndDegree;
			Log.d("set start degree =" + mStartDegree);
	}
	
	
	public void setArgCircle(Vector3f centerPoint, float radius, float degree) {
		synchronized (mLock) {
		float splitDegree = 1;
		float y = 1.0f;
		
		boolean isCloseWise = degree > 0 ? true : false; 
		
		
		mEndDegree = mStartDegree + degree;
		vCount = (int) ((Math.abs(mEndDegree-mStartDegree))/splitDegree + 2);
		if(vCount == 0){
			return ;
		}
		
		float vertices[] = new float[vCount  * 3 ];
		float i = 0;
		int  j= 0,  index=0;

		j = 1;
		vertices[0] = centerPoint.x;
		vertices[1] = y;
		vertices[2] = centerPoint.z;
		
		radius = 120.0f;
		

		
		if(mStartDegree < mEndDegree){
			for(i= mStartDegree; i<=mEndDegree; i+=splitDegree){
				index = j * 3 ;				
					vertices[index] =   (float) (radius * Math.sin(Math.PI/180 * splitDegree * i)) + centerPoint.x;
					vertices[index + 2] = (float) (radius * Math.cos(Math.PI/180 * splitDegree * i)) + centerPoint.z;
				
				vertices[index + 1] = y; 
				j ++ ;
			}
		} else {
			for(i= mStartDegree; i>=mEndDegree; i-=splitDegree){
				index = j * 3 ;
				
					vertices[index] =   (float) (radius * Math.sin(Math.PI/180 * splitDegree * i)) + centerPoint.x;
					vertices[index + 2] = (float) (radius * Math.cos(Math.PI/180 * splitDegree * i)) + centerPoint.z;
				
				vertices[index + 1] = y; 
				j ++ ;
			}
		}
		Log.d("mStartDegree=" + mStartDegree  + ",end degree="  + mEndDegree);
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		
		
	    int colors[] = new int[vCount * 4];
	    int c;
	    for(i=0, c=0; i < vCount; i++){
	        	colors[c] = 65535;
	        	colors[c+1] = 0;
	        	colors[c+2] = 65535;
	        	colors[c+3] = 0;
	    	c +=4;
	    }
	    
	    ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
	    cbb.order(ByteOrder.nativeOrder());
	    mColorBuffer = cbb.asIntBuffer();
	    mColorBuffer.put(colors);
	    mColorBuffer.position(0);
		}
	}

    public void setVisable(Boolean v){
    	mVisable = v;
    }
    
	public void draw(GL10 gl) {
		synchronized (mLock) {
			if(!mVisable){
				return;
			}
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
			gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
			gl.glLineWidth(mLineWidth);
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, vCount); //GL_TRIANGLE_FAN, GL_POINTS, 
			
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			
			mNumericXSprite.draw(gl, mNumericX_x, mNumericX_z);
		}
	}
}
