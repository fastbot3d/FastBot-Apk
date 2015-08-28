package lkj.unicron.object;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.join.ogles.lib.GLColor;

public class Floor {

	private FloatBuffer   mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private IntBuffer mColorBuffer;
    
    int vCount=0;
    int texId;
    
    public void TextureRect(float width,float height,float z, float offsetHeight, int texId,float sRange,float tRange, GLColor color)
    {
    	this.texId=texId;
        int r=0, g=65535, b = 0, a=0;
	       
        r = color.r;
        g = color.g;
        b = color.b;
        a = color.a;
        
    	
        vCount=6;
        final float UNIT_SIZE=1.0f;

        float vertices[]=new float[]
        {
/*        	10f, 20f, 0,
        	-10f,20f, 0,
        	-10f, -20f, 0,
        	
        	-10f,-20f,0,
        	10f,-20f,0,
        	10f,20f,0,*/
        		
        		-width*UNIT_SIZE, -z, -height*UNIT_SIZE +  offsetHeight,
            	-width*UNIT_SIZE, -z, height*UNIT_SIZE + offsetHeight,
            	width*UNIT_SIZE,  -z,height*UNIT_SIZE + offsetHeight,
            	     
            	width*UNIT_SIZE,  -z, height*UNIT_SIZE  + offsetHeight,
            	width*UNIT_SIZE,  -z, -height*UNIT_SIZE +  offsetHeight, 
            	-width*UNIT_SIZE, -z, -height*UNIT_SIZE +  offsetHeight,
        		
        		
/*        	width*UNIT_SIZE,height*UNIT_SIZE,0,
        	-width*UNIT_SIZE,height*UNIT_SIZE,0,
        	-width*UNIT_SIZE,-height*UNIT_SIZE,0,
        	     
        	-width*UNIT_SIZE,-height*UNIT_SIZE,0,
        	width*UNIT_SIZE,-height*UNIT_SIZE,0,  
        	width*UNIT_SIZE,height*UNIT_SIZE,0, */
        };
		
        int colors[] = {
                r,   g,    b, a,
                r,   g,    b, a,
                r,   g,    b, a,
          
                r,   g,    b, 0,
                r,   g,    b, 0,
                r,   g,    b, 0,
        };
        
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asIntBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
     
        float[] texST=
        {
            	sRange,0,
            	0,0,
            	0,tRange,
            	0,tRange,
            	sRange,tRange,
            	sRange,0
        };
        ByteBuffer tbb = ByteBuffer.allocateDirect(texST.length*4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = tbb.asFloatBuffer();
        mTextureBuffer.put(texST);
        mTextureBuffer.position(0);   
        
    }

    public void draw(GL10 gl)
    {    
    	gl.glEnable(GL10.GL_BLEND);
    	gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    	
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
     //   gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        
        gl.glVertexPointer(	3,GL10.GL_FLOAT, 0, mVertexBuffer);
      //  gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
        
        gl.glEnable(GL10.GL_TEXTURE_2D);   
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vCount);
        
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D); 
        
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
   //     gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisable(GL10.GL_BLEND);
    }
}



