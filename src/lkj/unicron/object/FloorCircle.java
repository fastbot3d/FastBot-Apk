package lkj.unicron.object;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.join.ogles.lib.GLColor;

public class FloorCircle {
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;

	int vCount = 0;
	int texId;

	final float UNIT_SIZE = 1.0f;
	
	public FloorCircle() {
		// TODO Auto-generated constructor stub
	}

	public void TextureRect(float radius,  float z, int texId, float sRange, float tRange) {
		this.texId = texId;

		int degree = 2;

		vCount = 360/degree + 1;
		
		float vertices[] = new float[vCount  * 3 ];
		float texST[] = new float[vCount  * 2 ];
		int i = 0, j= 0,  index=0;

		j = 1;
		vertices[0] = vertices[2] = 0.0f;
		vertices[1] = z;
		
		texST[0] = 0.5f;
		texST[1] = 0.5f;
		
		for(i=1; i<=360; i+=degree){
			index = j * 3 ;
			vertices[index] = (float) (radius * Math.cos(Math.PI/180 * degree * i)); 
			vertices[index + 1] = z; 
			vertices[index + 2] = (float) (radius * Math.sin(Math.PI/180 * degree * i));
			
			index = j * 2;
			if(i>=90 && i<=270){
				texST[index] = (float) (0.5f * Math.cos(Math.PI/180 * degree * i));
			} else {
				texST[index] = (float) (0.5 + 0.5f * Math.cos(Math.PI/180 * degree * i));
			}
			
			if(i>=0 && i<=180){
				texST[index + 1] = (float) (0.5 - 0.5f * Math.sin(Math.PI/180 * degree * i));	
			} else {
				texST[index + 1] = (float) (0.5 + 0.5f * Math.sin(Math.PI/180 * degree * i));
			}
			
			
			j ++ ;
		}
		

		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

		ByteBuffer tbb = ByteBuffer.allocateDirect(texST.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		mTextureBuffer = tbb.asFloatBuffer();
		mTextureBuffer.put(texST);
		mTextureBuffer.position(0);
	}

	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vCount);

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
