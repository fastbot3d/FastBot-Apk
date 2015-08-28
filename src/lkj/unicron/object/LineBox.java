package lkj.unicron.object;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import lkj.unicron.util.Log;

import org.join.ogles.lib.GLColor;
import org.join.ogles.lib.Ray;
import org.join.ogles.lib.Vector3f;
import org.join.ogles.lib.Vector4f;


public class LineBox {
	public  float mMaxX = -Float.MAX_VALUE;
	public  float mMaxY = -Float.MAX_VALUE;
	public  float mMaxZ = -Float.MAX_VALUE;
	public  float mMinX = Float.MAX_VALUE;
	public  float mMinY = Float.MAX_VALUE;
	public  float mMinZ = Float.MAX_VALUE;
	
	private final Object mLock = new Object();
	
	private GLColor mColor = new GLColor(65535,0,0);
	private float mLineWidth = 1.0f;
	private Boolean mVisable = true;
	 
	ArrayList<Vector3f> verticesList = new ArrayList<Vector3f>();
	public LineBox() {
		
	}
	public void setAABB(float maxX, float maxY, float maxZ, float minX, float minY, float minZ, GLColor color) {
		
	        int r=0, g=65535, b = 0;
	       
	        mColor.r = color.r;
	        mColor.g = color.g;
	        mColor.b = color.b;
	        mColor.a = color.a;
	        
	        r = mColor.r;
	        g = mColor.g;
	        b = mColor.b;
	        
	        mMaxX = maxX;
	    	mMaxY = maxY;
	    	mMaxZ = maxZ;
	    	mMinX = minX;
	    	mMinY = minY;
	    	mMinZ = minZ;
	        
	        float vertices[] = {
	        		maxX, minY, maxZ, 
	        		maxX, minY, minZ,    //
	        		minX,  minY, minZ,    //2
	        		minX, minY, maxZ,
	                
	        		maxX, maxY, maxZ,   //4
	        		maxX, maxY, minZ,
	        		minX,  maxY, minZ,
	        		minX, maxY, maxZ,
	        		
	        	//	maxX-(maxX-minX)/2, maxY, maxZ-(maxZ-minZ)/2, //8
	        	//	maxX-(maxX-minX)/2, minY, maxZ-(maxZ-minZ)/2,
	        };

	        int colors[] = {
	             /* r,   g,    b, 0,
	                65535,   0,    0, 0,       //right
	                0,   0,    0, 0,   //2  center
	                0,   0,    65535, 0,    //3  left
	                
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                0,   65535,    0, 0,   //6   top
	                r,   g,    b, 0, */
	        		               
	        		r,   g,    b, 0,
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                
	           //     r,   65536,    b, 0,
	            //    r,   65536,    b, 0,
	                
	/*                
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                r,   g,    b, 0,
	                r,   g,    b, 0,*/
	        };

	        byte indices[] = {
	                0,1,   1,2,   2,3,  3,0,
	                4,5,   5,6,   6,7,  7,4,
	                0,4,   1,5,   2,6,  3,7,
	           //     8, 9,
	        };

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

	        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
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

	public void setColor(GLColor color){
        int r=0, g=65535, b = 0;
        
		mColor.r = color.r;
		mColor.g = color.g;
		mColor.b = color.b;
		mColor.a = color.a;
		
        r = mColor.r;
        g = mColor.g;
        b = mColor.b;
        
        int colors[] = {
                r,   g,    b, 0,
                r,   g,    b, 0,
                r,   g,    b, 0,
                r,   g,    b, 0,
                
                r,   g,    b, 0,
                r,   g,    b, 0,
                r,   g,    b, 0,
                r,   g,    b, 0,
                
          //      r,   65536,    b, 0,
           //     r,   65536,    b, 0,
        };
        
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asIntBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
	}
	
    public void draw(GL10 gl)
    {
    	if(!mVisable){
    		return;
    	}
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    	
      //  gl.glFrontFace(GL10.GL_CW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
		gl.glLineWidth(mLineWidth);
		gl.glDrawElements(GL10.GL_LINES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
	//	gl.glDrawElements(GL10.GL_LINES, 26, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
		
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
    
    public void setLineWidth(float w){
    	mLineWidth = w;
    }
    public void setVisable(Boolean v){
    	mVisable = v;
    }

    public float[] getSize(){
    	if (mMaxX != Float.MIN_VALUE){
    		float[] size = new float[3];
    		size[0] = mMaxX - mMinX;
    		size[1] = mMaxY - mMinY;
    		size[2] = mMaxZ - mMinZ;
    		return size;
    	}
    	return null;
    }		
    
	// 返回立方体外切圆的中心点
	public Vector3f getSphereCenter() {
		return new Vector3f(mMaxX - (mMaxX - mMinX)/2, mMaxY - (mMaxY - mMinY)/2, mMaxZ - (mMaxZ - mMinZ)/2);
	}

	// 返回立方体外切圆的半径（√3）
	public float getSphereRadius() {
		float r = 0.0f;
		Vector3f v2, v4;
        synchronized (mLock) {
			v2 = verticesList.get(2);
			v4 = verticesList.get(4);
			r = (float) Math.sqrt((v2.x - v4.x) * (v2.x - v4.x) + (v2.y - v4.y) * (v2.y - v4.y) +  (v2.z - v4.z) * (v2.z - v4.z));
	     }
		return r;
	}

    int TrangleIndexs[] = {
            0,1,4,   1,5,4,
            1,2,5,   2,6,5,
            2,7,6,   7,2,3,
            0,4,7,   7,3,0,
            4,5,6,   6,7,4,
            0,1,2,   2,3,0
    };
	private static Vector4f location = new Vector4f();
	/**
	 * 射线与模型的精确碰撞检测
	 * 
	 * @param ray
	 *            - 转换到模型空间中的射线
	 * @param trianglePosOut
	 *            - 返回的拾取后的三角形顶点位置
	 * @return 如果相交，返回true
	 */
	//public boolean intersect(Ray ray, Vector3f[] trianglePosOut) {
	public boolean intersect(Ray ray) {
		boolean bFound = false;
		// 存储着射线原点与三角形相交点的距离
		// 我们最后仅仅保留距离最近的那一个
		float closeDis = 0.0f;

		Vector3f v0, v1, v2;
		Vector3f v3, v4, v5;

		// 立方体6个面
		for (int i = 0; i < 6 * 6; i+=6) {
			synchronized (mLock) {
				v0  = verticesList.get(TrangleIndexs[i]);
				v1  = verticesList.get(TrangleIndexs[i+1]);
				v2  = verticesList.get(TrangleIndexs[i+2]);
	
				v3  = verticesList.get(TrangleIndexs[i+3]);
				v4  = verticesList.get(TrangleIndexs[i+4]);
				v5  = verticesList.get(TrangleIndexs[i+5]);
			}
			if (ray.intersectTriangle(v0, v1, v2, location)) {
				bFound = true;
			//	trianglePosOut[0].set(v0);
			//	trianglePosOut[1].set(v1);
			//	trianglePosOut[2].set(v2);
			} else if(ray.intersectTriangle(v3, v4, v5, location)){
				bFound = true;
			//	trianglePosOut[0].set(v0);
			//	trianglePosOut[1].set(v1);
			//	trianglePosOut[2].set(v2);
			}
		}
		return bFound;
	}

	    
		@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[ ");
		builder.append("mMaxX=" + mMaxX + ",mMaxY=" + mMaxY + ",mMaxZ=" + mMaxZ );
		builder.append("mMinX=" + mMinX + ",mMinY=" + mMinY + ",mMinZ=" + mMinZ );
		builder.append(" ]");
		return builder.toString();
	}

    private FloatBuffer   mVertexBuffer;
    private IntBuffer   mColorBuffer;
    private ByteBuffer  mIndexBuffer;
}

