package lkj.unicron.object;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.join.ogles.lib.GLColor;
import org.join.ogles.lib.Matrix4f;
import org.join.ogles.lib.Vector3f;

import lkj.unicron.util.Log;
import lkj.unicron.util.M4;


public class D3Object {
	
	public  float mMaxX = -Float.MAX_VALUE;
	public  float mMaxY = -Float.MAX_VALUE;
	public  float mMaxZ = -Float.MAX_VALUE;
	public  float mMinX = Float.MAX_VALUE;
	public  float mMinY = Float.MAX_VALUE;
	public  float mMinZ = Float.MAX_VALUE;
	
	private ArrayList<Vector3f> mVertexList = null;  //new ArrayList<Vector3f>();
	private ArrayList<Vector3f> mVertexListBak = new ArrayList<Vector3f>();
	private ArrayList<Vector3f> mVertexListTmp = new ArrayList<Vector3f>();
	private ArrayList<Vector3f> mNormalList = null;  //new ArrayList<Vector3f>();
	ArrayList<Integer> mIndexList = new ArrayList<Integer>();
	private float[] tmp_float_list = null;
	public GLColor mSettingColor;
	public GLColor mColor;  //current color
	
	public  LineBox mLineBox = null;
	FloatBuffer mTriangleBuffer  = null;
	FloatBuffer mNormalBuffer  = null;
	IntBuffer mColorBuffer  = null;
	ShortBuffer mIndexBuffer  = null;
	
	
	public D3Object(ArrayList<Vector3f> vertexs, ArrayList<Vector3f> normals) {
		// TODO Auto-generated constructor stub
		D3Object(vertexs,normals);
	}
	
	public D3Object(ArrayList<Vector3f> vertexs, ArrayList<Vector3f> normals, ArrayList<Integer> indexs) {
		D3Object(vertexs,normals);
		mIndexList = indexs;
	}
	

	private void D3Object(ArrayList<Vector3f> vertexs,
			ArrayList<Vector3f> normals) {
		mVertexList = vertexs;
		mVertexListBak = new ArrayList<Vector3f>(vertexs.size());
		mNormalList = normals;
		mColor = new GLColor(65535, 65535, 65535);
		mSettingColor = new GLColor(0, 0, 30000);
		mLineBox = new LineBox();
		tmp_float_list = new float[mVertexList.size() * 3];
		calculateAABB();
		//generateNormalsData();
		for(int i=0; i<mVertexList.size(); i++){
			Vector3f v = new Vector3f();
			mVertexListBak.add(v);
			mVertexListTmp.add(v);
		}
	}

	public D3Object(ArrayList<Vector3f> vertexs, ArrayList<Vector3f> normals, GLColor color) {
		// TODO Auto-generated constructor stub
		mVertexList = vertexs;
		mNormalList = normals;
		mColor = color;
		mSettingColor = mColor;
		mLineBox = new LineBox();
		tmp_float_list = new float[mVertexList.size() * 3];
		calculateAABB();
		generateNormalsData();
		for(int i=0; i<mVertexList.size(); i++){
			Vector3f v = new Vector3f();
			mVertexListBak.add(v);
			mVertexListTmp.add(v);
		}
	}
	
	public void setWarningColor(GLColor color){
		mColor.r = color.r;
		mColor.g = color.g;
		mColor.b = color.b;
		mColor.a = color.a;
		generateColorData();
	}
	
	public void setColor(GLColor color){
		mColor.r = color.r;
		mColor.g = color.g;
		mColor.b = color.b;
		mColor.a = color.a;
		
		mSettingColor.r = color.r;
		mSettingColor.g = color.g;
		mSettingColor.b = color.b;
		mSettingColor.a = color.a;
		generateColorData();
	}
	
	public void setLineBoxVisble(Boolean v){
		mLineBox.setVisable(v);
	}
	
	public GLColor getSettingColor(){
		return mSettingColor;
	}
	
	public void calculateAABB(){
		if(mVertexList.size()>0){
			resetAABB();
			
			Vector3f v = null;
			for(int i=0; i<mVertexList.size(); i++){
				v = mVertexList.get(i);
				GetAABB(v.x, v.y, v.z);
			}
			mLineBox.setAABB(mMaxX, mMaxY, mMaxZ, mMinX, mMinY, mMinZ, new GLColor(65535, 0, 0));
			//Log.d("LineBox =" + mLineBox.toString());
		}
	}
	
	private void resetAABB(){
		mMaxX = -Float.MAX_VALUE;
		mMaxY = -Float.MAX_VALUE;
		mMaxZ = -Float.MAX_VALUE;
		mMinX = Float.MAX_VALUE;
		mMinY = Float.MAX_VALUE;
		mMinZ = Float.MAX_VALUE;
	}
	
	public float[] GetLengthWidthHeight() {
		float val[] = new float[3];
		val[0] = Math.abs(mMaxX - mMinX);
		val[1] = Math.abs(mMaxY - mMinY);
		val[2] = Math.abs(mMaxZ - mMinZ);
		return val;	
	}
	
	private void GetAABB(float x, float y, float z) {
		if (x > mMaxX) {
			mMaxX = x;
		}
		if (y > mMaxY) {
			mMaxY = y;
		}		
		if (z > mMaxZ) {
			mMaxZ = z;
		}
		if (x < mMinX) {
			mMinX = x;
		}
		if (y < mMinY) {
			mMinY = y;
		}
		if (z < mMinZ) {
			mMinZ = z;
		}
	}
	
	public Vector3f GetCenterPointer(){
		Vector3f v = new Vector3f();
		v.x = mMaxX - (mMaxX - mMinX)/2;
		v.y = mMaxY - (mMaxY - mMinY)/2;;
		v.z = mMaxZ - (mMaxZ - mMinZ)/2;
		return v;
	}
	
	public ArrayList<Vector3f> getVertexs(){
		if(mVertexList.size() > 0){
			return mVertexList;
		}
		return null;
	}
	
	public float[] getVertexArray(){
		return listToFloatArray(mVertexList, false);
	}

	public ArrayList<Vector3f> getNormals(){
		if(mNormalList.size() > 0){
			return mNormalList;
		}
		return null;
	}
	
	public void saveVertexTmp(){		
		for (int i = 0; i < mVertexList.size() ; i++) {
			mVertexListBak.get(i).set(mVertexList.get(i));
		}		
	}
	
	public void restoreVertexTmp(){
		synchronized (this) {
			for (int i = 0; i < mVertexListBak.size() ; i++) {
				mVertexList.get(i).set(mVertexListBak.get(i));
			}
			generateVertexData();
			calculateAABB();
		}
	}

	public void setVertexAndNormal(ArrayList<Vector3f> vertexs, ArrayList<Vector3f> normals){
		if(vertexs.size() > 0){
			mVertexList = vertexs;
		}
		
		if(normals.size() > 0){
			mNormalList = normals;
		}
		calculateAABB();
		generateVertexData();
	}
	
	private short[] listToShortArray(ArrayList<Integer> list) {
		short[] result = new short[list.size()];
		for (int i = 0; i < list.size() ; i++) {
			int a = list.get(i);
			result[i] = (short)(a);
		}
		return result;
	}
	
	private float[] listToFloatArray(ArrayList<Vector3f> list, boolean isCalculateAABB) {
		//float[] result = new float[list.size() * 3];
		int j = 0;
		float x,y,z;
		
		if(isCalculateAABB)
			resetAABB();
		
		for (int i = 0; i < list.size() ; i++) {
			x = list.get(i).x;
			y = list.get(i).y;
			z = list.get(i).z;
			tmp_float_list[j] = x;
			tmp_float_list[j+1] = y;
			tmp_float_list[j+2] = z;
			if(isCalculateAABB)
				GetAABB(x, y, z);
			j += 3;
		}
		
		if(isCalculateAABB)
			mLineBox.setAABB(mMaxX, mMaxY, mMaxZ, mMinX, mMinY, mMinZ, new GLColor(65535, 0, 0));
		
		return tmp_float_list;
	}
	
	private FloatBuffer getFloatBufferFromList(List<Float> vertexList) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexList.size() * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer triangleBuffer = vbb.asFloatBuffer();
		float[] array = new float[vertexList.size()];
		for (int i = 0; i < vertexList.size(); i++) {
			array[i] = vertexList.get(i);
		}
		triangleBuffer.put(array);
		triangleBuffer.position(0);
		return triangleBuffer;
	}
	
	public void generateData(){
		generateVertexData();
		generateColorData();
	}
	
	public void generateVertexData(){
		float[] vertexArray = listToFloatArray(mVertexList, true);
		if(mTriangleBuffer!=null){
			mTriangleBuffer.rewind();	
			mTriangleBuffer.clear();
		} else {
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			mTriangleBuffer = vbb.asFloatBuffer();
		}
		mTriangleBuffer.put(vertexArray);
		mTriangleBuffer.position(0);
		Log.d("in generateVertexData, call generateNormalsData");	
		generateNormalsData();
	//	generateIndexData();
	}
	
	private Vector3f calNormalData(Vector3f v1, Vector3f v2, Vector3f v3){
		Vector3f edge1 = new Vector3f();
		Vector3f edge2 =  new Vector3f();
		Vector3f norm =  new Vector3f();
		
		edge1.sub(v3, v1);
		edge2.sub(v2, v1);
		norm.cross(edge1, edge2);
		norm.inv_normalize();
		//norm.normalize();
		
		return norm;		
	}
	
	private void generateNormalsData(){
		Vector3f v1 = null, v2 = null, v3 = null;
		Vector3f norm =null;
		
		mNormalList.clear();
		
		for(int i=0; i<mVertexList.size(); i+=3){
			v1 =  mVertexList.get(i);
			v2 =  mVertexList.get(i+1);
			v3 =  mVertexList.get(i+2);
			norm = calNormalData(v1, v2, v3);
			mNormalList.add(norm);
			mNormalList.add(norm);
			mNormalList.add(norm);
		}
		Log.d("in generateNormalsData");
		float[] normalArray = listToFloatArray(mNormalList, false);
		if(mNormalBuffer!=null){
			Log.d("in generateNormalsData 0");
			mNormalBuffer.rewind();
			mNormalBuffer.clear();
			//mNormalBuffer.reset();	
		} else {
			Log.d("in generateNormalsData 1");
		ByteBuffer vbb = ByteBuffer.allocateDirect(normalArray.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mNormalBuffer = vbb.asFloatBuffer();
		}
		mNormalBuffer.put(normalArray);
		mNormalBuffer.position(0);
	}
	
	private void generateIndexData(){
		short[] indexArray =  listToShortArray(mIndexList);
		if(mIndexBuffer!=null){
			mIndexBuffer.rewind();	
		} else {
			ByteBuffer vbb2 = ByteBuffer.allocateDirect(indexArray.length * 2);
			vbb2.order(ByteOrder.nativeOrder());
			mIndexBuffer = vbb2.asShortBuffer();
		}
		mIndexBuffer.put(indexArray);
		mIndexBuffer.position(0);
	}
	
	private void generateColorData(){		
		/*
		int [] colorArray = new int[mVertexList.size() * 4];
		for (int i = 0; i < colorArray.length; ) {
			colorArray[i] = mColor.r;
			colorArray[i+1] = mColor.g;
			colorArray[i+2] = mColor.b;
			colorArray[i+3] = mColor.a;
			i += 4;
		}
		if(mColorBuffer!=null){
			mColorBuffer.rewind();	
		} else {
			ByteBuffer vbb2 = ByteBuffer.allocateDirect(colorArray.length * 4);
			vbb2.order(ByteOrder.nativeOrder());
			mColorBuffer = vbb2.asIntBuffer();
		}
		mColorBuffer.put(colorArray);
		mColorBuffer.position(0);
		*/
	}
	
	/*
	public void transform(Matrix4f m) {
		for(int i=0; i<mVertexList.size(); i++){
			Vector3f v = mVertexList.get(i);
			if (i<10)
				Log.d("Vector3f = " + v.toString());
			// v.transform(m);
			m.transform(v, v);
			if (i<10)
				Log.d("---Vector3f = " + v.toString());
		}
		Log.d("M4 = " + m.toString());
		
		CalculateAABB();
		generateVertexData();
	}
	
	public void move(int direction, float x, float y, float z) {
		for(int i=0; i<mVertexList.size(); i++){
			Vector3f v = mVertexList.get(i);
		//	v.move(direction, x, y, z);
		}
		
		CalculateAABB();
		generateVertexData();
	}
	*/
	
	
	
	public void draw(GL10 gl) {
		if (mTriangleBuffer == null) {
			return;
		}
		turnonLight(gl);
		
	    gl.glFrontFace(GL10.GL_CCW); // OpenGL docs
	      // Enable face culling.
	      gl.glEnable(GL10.GL_CULL_FACE); // OpenGL docs
	      // What faces to remove with the face culling.
	      gl.glCullFace(GL10.GL_BACK); // OpenGL docs
	      
	      
	     gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	  //   gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	     gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
	//	Log.i("draw 1");
     //  gl.glFrontFace(GL10.GL_CCW);
		//gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, new float[] { 0.15f, 0.15f, 0.15f, 0.85f }, 0);
		//gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, new float[] { mColor.r, mColor.g, mColor.b, mColor.a }, 0);
		
		
		//gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, new float[] { 0.8f,0.0f,0.0f,1.0f }, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, new float[] { mColor.r / 65535.0f, mColor.g / 65535.0f, mColor.b / 65535.0f, mColor.a / 65535.0f  }, 0);
		gl.glLightfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, new float[] {1.0f,1.0f,1.0f,1.0f}, 0);
		
		synchronized (this) {
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mTriangleBuffer);
		//	gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
		}
/*		for (int i = 0; i < mVertexList.size() / 3; i++) {
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i * 3, 3);
		}*/
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mVertexList.size());
   	//	gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, mIndexList.size(), GL10.GL_UNSIGNED_SHORT,	mIndexBuffer);
		
		
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
      //  gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        
        gl.glDisable(GL10.GL_CULL_FACE); // OpenGL docs
        
        turnoffLight(gl);
        
        
		mLineBox.draw(gl);
	}
	
	
	private void turnonLight(GL10 gl) {
  		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, new float[]{1.0f,1.0f,1.0f,1.0f}, 0);
		//gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, new float[]{0.2f,0.2f,0.2f,1.0f}, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, new float[]{0.8f,0.8f,0.8f,1.0f}, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, new float[] {1.0f,1.0f,1.0f,1.0f}, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, new float[] { 0f, 1000, 1000f, 0f }, 0); 
		
		//gl.glEnable(GL10.GL_LIGHT1);		
		//gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, new float[]{1.0f,1.0f,1.0f,1.0f}, 0);
		//gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, new float[]{0.8f,0.8f,0.8f,1.0f}, 0);
		//gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, new float[] {1.0f,1.0f,1.0f,1.0f}, 0);
		//gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, new float[] { 0f, -1400f, 0f, 0f }, 0); 
	}
	
	private void turnoffLight(GL10 gl) {
  		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_LIGHT0); 
		//gl.glDisable(GL10.GL_LIGHT1); 
	}
}
