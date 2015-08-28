package lkj.unicron.object;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.join.ogles.lib.Vector3f;
import org.join.raypick.AppConfig;
import org.join.raypick.PickFactory;

import android.graphics.Paint;
import lkj.unicron.util.Log;

public class TwoLine {
		
		private final Object mLock = new Object();
		
		private Vector3f mStart_X_Vector3f = new Vector3f();
		private Vector3f mStart_Z_Vector3f = new Vector3f();
		private Vector3f mEnd_X_Vector3f = new Vector3f();
		private Vector3f mEnd_Z_Vector3f = new Vector3f();
		
		private float mLineWidth = 3f;
		private Boolean mVisable = true;

	    private FloatBuffer   mVertexBuffer;
	    private IntBuffer   mColorBuffer;
	    private ShortBuffer  mIndexBuffer;
	    
	    public NumericSprite mNumericXSprite;
	    public NumericSprite mNumericZSprite;
	    
	    private Paint mXLabelPaint;
	    private Paint mZLabelPaint;
	    
	    float mNumericX_x = 0;
	    float mNumericX_z = 0;
	    float mNumericZ_x = 0;
	    float mNumericZ_z = 0;
	    
		public TwoLine() {
			// TODO Auto-generated constructor stub
			int vertices_count = 4;
			int r_x = 65534;
			int g_x = 0;
			int b_x = 0;
			
			int r_z = 12032;
			int g_z = 16065;
			int b_z = 24255;

			
	        int colors[] = new int[vertices_count * 4];
	        for(int i=0, c=0; i < vertices_count; i++){
	        	if(i==1){
		        	colors[c] = r_x;
		        	colors[c+1] = g_x;
		        	colors[c+2] = b_x;
		        	colors[c+3] = 0;
	        	} else {
	        		colors[c] = r_z;
		        	colors[c+1] = g_z;
		        	colors[c+2] = b_z;
		        	colors[c+3] = 0;
	        	}
	        	c +=4;
	        }

	        short indices[] = new short[4];
	        indices[0] = 0;
	        indices[1] = 1;
	        indices[2] = 2;
	        indices[3] = 3;
	        
	        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
	        cbb.order(ByteOrder.nativeOrder());
	        mColorBuffer = cbb.asIntBuffer();
	        mColorBuffer.put(colors);
	        mColorBuffer.position(0);

	        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length*2);
	        ibb.order(ByteOrder.nativeOrder());
	        mIndexBuffer = ibb.asShortBuffer();
	        mIndexBuffer.put(indices);
	        mIndexBuffer.position(0);	
	        
	        mNumericXSprite = new NumericSprite();
    	 mXLabelPaint = new Paint();
         mXLabelPaint.setTextSize(32);
         mXLabelPaint.setAntiAlias(true);
         mXLabelPaint.setARGB(0xff, 0xff, 0x0, 0x0); // foreground is white, alpa is transparent 

         mNumericZSprite = new NumericSprite();
    	 mZLabelPaint = new Paint();
         mZLabelPaint.setTextSize(32);
         mZLabelPaint.setAntiAlias(true);
         mZLabelPaint.setARGB(0xff, 0x2f, 0x3f, 0x5f); // foreground is white, alpa is transparent
		}
		
		public void generateData() {
				float vertices[] = new float[12];
				int i=0;
				vertices[i++] = mStart_X_Vector3f.x;
				vertices[i++] = mStart_X_Vector3f.y;
				vertices[i++] = mStart_X_Vector3f.z;
				
				vertices[i++] = mEnd_X_Vector3f.x;
				vertices[i++] = mEnd_X_Vector3f.y;
				vertices[i++] = mEnd_X_Vector3f.z;

				vertices[i++] = mStart_Z_Vector3f.x;
				vertices[i++] = mStart_Z_Vector3f.y;
				vertices[i++] = mStart_Z_Vector3f.z;
				
				vertices[i++] = mEnd_Z_Vector3f.x;
				vertices[i++] = mEnd_Z_Vector3f.y;
				vertices[i++] = mEnd_Z_Vector3f.z;
				
		        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
		        vbb.order(ByteOrder.nativeOrder());
		        mVertexBuffer = vbb.asFloatBuffer();
		        mVertexBuffer.put(vertices);
		        mVertexBuffer.position(0);

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
				gl.glDrawElements(GL10.GL_LINES, 4, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
	
				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
				
		        mNumericXSprite.draw(gl, mNumericX_x, mNumericX_z);
		        mNumericZSprite.draw(gl, mNumericZ_x, mNumericZ_z);
			}
		}
		

	    float mWidth, mHeight;
	    public void setLineWidth(float w){
	    	mLineWidth = w;
	    }
	     
	    public void setTextSprite(GL10 gl, float width, float height){
	    	mNumericXSprite.initialize(gl, width, height, mXLabelPaint);
	    	mNumericZSprite.initialize(gl, width, height, mZLabelPaint);
	        mWidth = width; 
	        mHeight = height;
	    }
	    
	    public void setVisable(Boolean v){
	    	mVisable = v;
	    }
	    
	    public void set2DOrignPoint(float x, float y){
			
		    mNumericX_x = x + 10;
		    mNumericX_z = mHeight - y; 
		    
		    mNumericZ_x = x;
		    mNumericZ_z = mHeight- y + 40 ;
		    
			 //   Log.d("mNumericX_x=" + mNumericX_x + ",mNumericX_z=" + mNumericX_z);
	    }
	    
	    public void setStartPoint(Vector3f v){
	    	mStart_X_Vector3f.set(v.x, 0, v.z);
	    	mStart_Z_Vector3f.set(v.x-0.5f, 0, v.z-0.5f);
	    }
	    
	    public void setEndPoint(Vector3f end){
	    	mEnd_X_Vector3f.set(end.x, 0, mStart_X_Vector3f.z);
	    	mEnd_Z_Vector3f.set(mStart_Z_Vector3f.x, 0, end.z);
			synchronized (mLock) {
				float x_distance =  Math.abs((mEnd_X_Vector3f.x - mStart_X_Vector3f.x));
				float z_distance =  Math.abs((mEnd_Z_Vector3f.z - mStart_Z_Vector3f.z));

				mNumericXSprite.setValue((int) x_distance);
				mNumericXSprite.setVisable(true);
				
				mNumericZSprite.setValue((int) z_distance);
				mNumericZSprite.setVisable(true);
				
				generateData();
				mVisable = true;
			}
	    }

	}

