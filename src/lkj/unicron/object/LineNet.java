package lkj.unicron.object;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import lkj.unicron.util.Log;

import org.join.ogles.lib.GLColor;
import org.join.ogles.lib.Ray;
import org.join.ogles.lib.Vector3f;
import org.join.ogles.lib.Vector4f;


public class LineNet {

	
	private final Object mLock = new Object();
	
	private float mLineWidth = 0.3f;
	private Boolean mVisable = true;
    int horizontalLines = 20;
    int verticalLines = 20;
    
	ArrayList<Vector3f> verticesList = new ArrayList<Vector3f>();
	public LineNet() {
		
	}
	public void setLineNet(float lengthBox, float widthBox, float heightBox, GLColor color) {
		
	        int r=0, g=65535, b = 0;
	       
	        
	        r = color.r;
	        g = color.g;
	        b = color.b;
	        


	        float vertices[] = new float[(horizontalLines + verticalLines) * 2 * 3 ];
	        float horizontalStep = (lengthBox)/horizontalLines;
	        float verticalStep = (widthBox)/verticalLines;
	        
	        int vertices_i=0;
	        for(int i=0; i < horizontalLines; i++){
	        	vertices[vertices_i] = lengthBox/2;
	        	vertices[vertices_i+1] = 0;
	        	vertices[vertices_i+2] = lengthBox/2 - horizontalStep * i;
	        	
	        	vertices[vertices_i+3] = -lengthBox/2;
	        	vertices[vertices_i+4] = 0;
	        	vertices[vertices_i+5] = lengthBox/2 - horizontalStep * i;
	        	vertices_i +=6;
	        }
	        
	        for(int i=0; i < verticalLines; i++){
	        	vertices[vertices_i] = widthBox/2 - verticalStep * i;
	        	vertices[vertices_i+1] = 0;
	        	vertices[vertices_i+2] = widthBox/2; 
	        	
	        	vertices[vertices_i+3] = widthBox/2 - verticalStep * i;
	        	vertices[vertices_i+4] = 0;
	        	vertices[vertices_i+5] = -widthBox/2; 
	        	vertices_i +=6;
	        }
	        
	        int colors[] = new int[vertices.length * 4];
	        for(int i=0, c=0; i < vertices.length; i++){
	        	colors[c] = r;
	        	colors[c+1] = g;
	        	colors[c+2] = b;
	        	colors[c+3] = 0;
	        	c +=4;
	        }

	        short indices[] = new short[vertices.length];
	        for(int i=0; i < vertices.length; i++){
	        	indices[i] = (short) i;
	        }


	        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());
	        mVertexBuffer = vbb.asFloatBuffer();
	        mVertexBuffer.put(vertices);
	        mVertexBuffer.position(0);

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
	        synchronized (mLock) {
	        	verticesList.clear();
	        	for(int i=0; i< vertices.length; i+=3){
		        	Vector3f v = new Vector3f(vertices[i],vertices[i+1],vertices[i+2]);
		        	verticesList.add(v);
		        }
	        }
	    }
	
    public void draw(GL10 gl)
    {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    	
      //  gl.glFrontFace(GL10.GL_CW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
		gl.glLineWidth(mLineWidth);
		gl.glDrawElements(GL10.GL_LINES, (horizontalLines + verticalLines) * 2, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
	//	gl.glDrawElements(GL10.GL_LINES, 26, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
    
    public void setLineWidth(float w){
    	mLineWidth = w;
    }
    public void setVisable(Boolean v){
    	mVisable = v;
    }

    private FloatBuffer   mVertexBuffer;
    private IntBuffer   mColorBuffer;
    private ShortBuffer  mIndexBuffer;
}

