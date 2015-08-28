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

package lkj.unicron.renderer;




import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.join.ogles.lib.GLColor;
import org.join.ogles.lib.IBufferFactory;
import org.join.ogles.lib.Matrix4f;
import org.join.ogles.lib.Ray;
import org.join.ogles.lib.Vector3f;
import org.join.raypick.AppConfig;
import org.join.raypick.PickFactory;

import lkj.nuicorn.R;
import lkj.unicron.object.ArgCircle;
import lkj.unicron.object.Cube;
import lkj.unicron.object.D3Object;
import lkj.unicron.object.Floor;
import lkj.unicron.object.FloorCircle;
import lkj.unicron.object.FloorXYZ;
import lkj.unicron.object.LineBox;
import lkj.unicron.object.LineNet;
import lkj.unicron.object.NumericSprite;
import lkj.unicron.object.Sky_Ball;
import lkj.unicron.object.Text3DMaker;
import lkj.unicron.object.TwoLine;
import lkj.unicron.util.IOUtils;
import lkj.unicron.util.Log;


/**
 * Example of how to use OpenGL|ES in a custom view
 *
 */
public class KubeRenderer implements GLSurfaceView.Renderer {

    private D3Object mObj;
    private Cube cube;
    Floor mBottomBox;
    FloorXYZ mXYZ;
    private LineBox mBigBox = null;
    private LineNet mLineNet = null;
    
    public TwoLine mTwoLine = null;
    public ArgCircle mArgCircle = null; 
    
    
    Text3DMaker mText3DMaker = null;
  	 Paint mXLabelPaint = new Paint();
  	 
  	 
    float mWidth, mHeight;
    
    FloorCircle mGroundCircle = null;
    Sky_Ball mSky = null;
    
    private float mBoxLength = IOUtils.MACHINE_LENGTH;
    private float mBoxWidth = IOUtils.MACHINE_WIDTH;
    private float mBoxHeight = IOUtils.MACHINE_HEIGHT;
    
    Boolean mIsFirstLoad = false;
    private float mAngle;
	Context mContext = null;

    public KubeRenderer(Context context) {
    	RendererInit(context);
    }
    
    public void RendererInit(Context context) {
    	mObj = null;
    	cube = null;
    	mBigBox = null;
    	mContext = null;
    	
    	mContext = context;
    	cube = new Cube();
    	mBigBox = new LineBox();
    	mBigBox.setLineWidth(2.1f);
    	mBigBox.setAABB(mBoxLength/2, mBoxWidth, mBoxHeight/2, -mBoxLength/2.0f, 0, -mBoxHeight/2.0f, new GLColor(65535,65535,65535));
    	
    	mLineNet = new LineNet();
    	mLineNet.setLineWidth(0.5f);
    	mLineNet.setLineNet(mBoxLength, mBoxWidth, mBoxHeight,  new GLColor(38020,38500,38500));

    	mTwoLine = new TwoLine();
    	mTwoLine.setVisable(false);
    	
    	mArgCircle = new ArgCircle();
    	mArgCircle.setVisable(false);
    	 
    	mText3DMaker = new Text3DMaker();

	     mXLabelPaint.setTextSize(32);
	     mXLabelPaint.setAntiAlias(true);
	     mXLabelPaint.setARGB(0xff, 0xff, 0x0, 0x0); // foreground is white, alpa is transparent 

    	mIsFirstLoad = true;
    }
    
    public void addD3Object(D3Object obj) {
    	mObj = obj;
    }
    
    
/*	// 观察者、中心和上方
	public Vector3f mvEye = new Vector3f(0, 0f, 380f), 
					 mvCenter = new Vector3f(0,0, 0), 
					 mvUp = new Vector3f(0, 1, 0);*/
	// 观察者、中心和上方
	public Vector3f mvEye = new Vector3f(IOUtils.EYE_X_DISTANCE, IOUtils.EYE_Y_DISTANCE, IOUtils.EYE_Z_DISTANCE), 
					 mvCenter = new Vector3f(0,100, 0), 
					 mvUp = new Vector3f(0, 1, 0);
	
	/**
	 * 设置相机矩阵
	 * 
	 * @param gl
	 */
	private void setUpCamera(GL10 gl) {
		// 设置模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		// GLU.gluLookAt(gl, mfEyeX, mfEyeY, mfEyeZ, mfCenterX, mfCenterY,
		// mfCenterZ, 0, 1, 0);//系统提供
		Matrix4f.gluLookAt(mvEye, mvCenter, mvUp, AppConfig.gMatView);
		gl.glLoadMatrixf(AppConfig.gMatView.asFloatBuffer());
	}

	private Matrix4f matRot = new Matrix4f();
	Matrix4f tmpMat = new Matrix4f();
	private Vector3f point;
	public float mfAngleX = 0.0f;
	public float mfAngleY = 0.0f;
	public float mLookRotateDistance = 0.0f;
	public float mLookScaleDistance = 100.0f;
	
	/**
	 * 渲染模型
	 */
	private void drawModel(GL10 gl) {
		

/*		if (mLookScaleDistance != 100.0f) {
			try {
				matRot.setIdentity();
				matRot.glTranslatef(0, 0, 100 - mLookScaleDistance);
				//	matRot.glTranslatef(-mLookScaleDistance, -mLookScaleDistance, -mLookScaleDistance);
				//matRot.glScalef(mLookScaleDistance, mLookScaleDistance, mLookScaleDistance);
				AppConfig.gMatModel.mul(matRot);
			} catch (Exception e) {
				Log.i( "Exception matrix e=" + e);
			}
			Log.i( "mLookScaleDistance =" + mLookScaleDistance);
			//mLookScaleDistance = 100;
			gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());
		}*/
		

		
		if (mLookRotateDistance != 0) {
			/* 以下方式完全按照手势方向旋转 */
			matRot.setIdentity();
			// 世界坐标系的向量点
			point = new Vector3f(mfAngleX, mfAngleY, 0);

			try {
				// 转换到模型内部的点，先要求逆
				matInvertModel.set(AppConfig.gMatModel);
				matInvertModel.invert();
				matInvertModel.transform(point, point);
				
				float d = Vector3f.distance(new Vector3f(), point);
			//	Log.d( "point =" + point + ",d=" + d);
			//	Log.d( "point.x/d =" + point.x/d + ",point.y/d=" + point.y/d + ",point.z/d" + point.z/d);
				// 再减少误差
				//if (Math.abs(d - mLookRotateDistance) <= 1E-4) {
					
					// 绕这个单位向量旋转（由于误差可能会产生缩放而使得模型消失不见）
						matRot.glRotatef((float) (mLookRotateDistance * Math.PI / 180),
								point.x / d, point.y / d, point.z / d);
						
						// 旋转后在原基础上再转
						if (0 != mLookRotateDistance) {
							AppConfig.gMatModel.mul(matRot);
						}	
						//Log.d("" + AppConfig.gMatModel);
					
				//}
			} catch (Exception e) {
				// 由于四舍五入求逆矩阵失败
				Log.i( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!Exception matrix e=" + e);
			}
			//Log.d( "mLookRotateDistance =" + mLookRotateDistance);
			mLookRotateDistance = 0;
		}
		
	//	gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());

 		//gl.glTranslatef(positionX, -positionY, 0);

 		// rotation and apply Z-axis
/* 		if (mObj != null) {
 			//gl.glTranslatef(0, 0, -360);
 		} else {
 			gl.glTranslatef(0, 0, -0);
 		}*/
		gl.glPushMatrix();
		tmpMat.set(AppConfig.gMatModel);
		//tmpMat.setIdentity();
 		matRot.setIdentity();
 		matRot.glTranslatef(0, -60, 0);
 		tmpMat.mul(matRot);
 		gl.glMultMatrixf(tmpMat.asFloatBuffer());
 	    mSky.drawSelf(gl);
 	    gl.glPopMatrix();
 	    

		gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());

 	//	gl.glRotatef(angle, x, y, z)
 		
		  
	 	mBigBox.draw(gl);
        mLineNet.draw(gl);
        
        mTwoLine.draw(gl);
        mArgCircle.draw(gl);
        
        mGroundCircle.draw(gl);

        mText3DMaker.draw(gl, 100, 100);
	//	gl.glTranslatef(0, 0 - mBoxHeight/2, 0);
 		//gl.glScalex(5, 5, 5);
 		//gl.glRotatef(145.0f, 0, 1, 0);
 		//gl.glRotatef(angleY, 1, 0, 0);

 	   if(mObj != null){
 	        mObj.draw(gl);;
 	 	 }
 
       mBottomBox.draw(gl);
       mXYZ.draw(gl);
       
    
 // gl.glTranslatef(-15, 0, -30);
 // cube.draw(gl);
 // mWorld.draw(gl);
        
	}
	
    
    public void onDrawFrame(GL10 gl) {
  		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

  		setUpCamera(gl);
  		
/* 		 gl.glPushMatrix();
         gl.glMatrixMode(GL10.GL_MODELVIEW);
         gl.glLoadIdentity();  
        // gl.glTranslatef(0, 0, 0);
         //   gl.glRotatef((float)-(direction*180/Math.PI), 0, 1, 0);
         mFloorCircle.draw(gl);
         gl.glPopMatrix();
	
  		 gl.glPushMatrix();
         gl.glMatrixMode(GL10.GL_MODELVIEW);
         gl.glLoadIdentity();  
        // gl.glEnable(GL10.GL_FOG);
        // initFog(gl);
         gl.glTranslatef(0, -150, 0);
      //   gl.glRotatef((float)-(direction*180/Math.PI), 0, 1, 0);

         mSky.drawSelf(gl);
       //  gl.glDisable(GL10.GL_FOG);
         gl.glPopMatrix();*/
         
  		
		gl.glPushMatrix();
		{
			// 渲染物体
			drawModel(gl);
		}
		gl.glPopMatrix();

		 if(mObj != null){
			 updatePick();
			 UpdateObjColor();
		 }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
		// 设置视口
		gl.glViewport(0, 0, width, height);
		AppConfig.gpViewport[0] = 0;
		AppConfig.gpViewport[1] = 0;
		AppConfig.gpViewport[2] = width;
		AppConfig.gpViewport[3] = height;
		
		mWidth = width; 
		mHeight = height;
		mTwoLine.setTextSprite(gl, mWidth, mHeight);
		mArgCircle.setTextSprite(gl, mWidth, mHeight);
		//mText3DMaker.initialize(gl, mWidth, mHeight);
		//mText3DMaker.setText(gl, "hello abc", mXLabelPaint);
		
		Log.i("onSurfaceChanged ...");
        
		// 设置投影矩阵
		float ratio = (float) width / height;// 屏幕宽高比
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		// GLU.gluPerspective(gl, 45.0f, ratio, 1, 5000);系统提供
		Matrix4f.gluPersective(45.0f, ratio, 1, 50000, AppConfig.gMatProject);
		gl.glLoadMatrixf(AppConfig.gMatProject.asFloatBuffer());
		AppConfig.gMatProject.fillFloatArray(AppConfig.gpMatrixProjectArray);
		// 每次修改完GL_PROJECTION后，最好将当前矩阵模型设置回GL_MODELVIEW
		gl.glMatrixMode(GL10.GL_MODELVIEW);
    }


    
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// 全局性设置
		gl.glEnable(GL10.GL_DITHER);
		// gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_FASTEST);
		//gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    	
		// 设置清屏背景颜色
		// gl.glClearColor(0.0f, 0, 0, 0.0f);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1);  //this code produce background for NumericSprite
		// 设置着色模型为平滑着色
		gl.glShadeModel(GL10.GL_SMOOTH);

	//	gl.glEnable(GL10.GL_BLEND);
	//	gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		// 启用背面剪裁
		//gl.glEnable(GL10.GL_CULL_FACE);
		//gl.glCullFace(GL10.GL_BACK);
		// 启用深度测试
		gl.glEnable(GL10.GL_DEPTH_TEST);
		

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);

		int floorId = loadTexture(gl, R.drawable.ppan); //bottom
		
		//mFoor = new Floor(-(int)mBoxLength/2, (int)mBoxLength/2, -(int)mBoxHeight/2, (int)mBoxHeight/2, mTextureID);
		mBottomBox = new Floor();
		//mFloor.TextureRect(1.5f,1.5f,testTexId,10,10);
		//mFloor.TextureRect(mBoxLength/2,mBoxHeight/2, 0, floorId, 10,10, new GLColor(65535,65535,65535,1000));
		mBottomBox.TextureRect(108 ,114, 0, 6, floorId, 1,1, new GLColor(65535,65535,65535,1000));
		
		mXYZ = new FloorXYZ();
		int xyzId = loadTexture(gl, R.drawable.xyz_c);
		mXYZ.TextureRect(-100, 60, 60, xyzId,1,1);
		
		int  skyId= loadTexture(gl, R.drawable.sky_ui); //skyball, sky
		mSky = new Sky_Ball(IOUtils.SKY_RADIUS, skyId);
		
		int  floorCircleId= loadTexture(gl, R.drawable.ground_ui); //, grass
	    mGroundCircle = new FloorCircle();
		mGroundCircle.TextureRect(900, -2, floorCircleId, 1,1);
		
		Log.i("lkj floor floorId=" + floorId + ",skyId=" + skyId);
		AppConfig.gMatModel.setIdentity();
		mLookScaleDistance = 100.0f;
		

    }
    
    public void initFog(GL10 gl)
	{
		float[] fogColor={0.96470f,0.96862f,0.91372f,0};
		gl.glFogfv(GL10.GL_FOG_COLOR, fogColor, 0);
		gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_EXP2);
		gl.glFogf(GL10.GL_FOG_DENSITY, 0.006f);
		gl.glFogf(GL10.GL_FOG_START, IOUtils.SKY_RADIUS-IOUtils.SKY_RADIUS /5);
		gl.glFogf(GL10.GL_FOG_END, IOUtils.SKY_RADIUS);
	}
    
	private void turnonLight(GL10 gl) {
  		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, new float[]{1.0f,1.0f,1.0f,1.0f}, 0);
		//gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, new float[]{0.2f,0.2f,0.2f,1.0f}, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, new float[]{0.8f,0.8f,0.8f,1.0f}, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, new float[] {1.0f,1.0f,1.0f,1.0f}, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, new float[] { 0f, 0f, 1400f, 0f }, 0); 
		
		/*gl.glEnable(GL10.GL_LIGHT1);		
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, new float[]{1.0f,1.0f,1.0f,1.0f}, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, new float[]{0.8f,0.8f,0.8f,1.0f}, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, new float[] {1.0f,1.0f,1.0f,1.0f}, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, new float[] { 0f, 0f, 1400f, 1f }, 0); */
	}
	
	private void turnoffLight(GL10 gl) {
  		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_LIGHT0); 
	//	gl.glDisable(GL10.GL_LIGHT1); 
	}

	private int loadTexture(GL10 gl, int drawableId) {
		int id = 0;
		// 启用纹理映射
		gl.glClearDepthf(1.0f);
		// 允许2D贴图,纹理
		gl.glEnable(GL10.GL_TEXTURE_2D);
		try {
			int[] textures = new int[1];
			// 创建纹理
			gl.glGenTextures(1, textures, 0);
			id = textures[0];
			// 设置要使用的纹理
			gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
			
			
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,  GL10.GL_NEAREST);
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,  GL10.GL_LINEAR);

	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
	        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

	      //  gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
	      //          GL10.GL_REPLACE);

			// 打开二进制流
            InputStream is = mContext.getResources().openRawResource(drawableId);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(is);
            } finally {
                try {
                    is.close();
                } catch(IOException e) {
                    // Ignore.
                }
            }
            
			Log.d("bitmap width | height =" + bitmap.getWidth() + "|" + bitmap.getHeight());

            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
		}  finally {                    
        }
		
		return id;
	}
	
    
	private Boolean checkObjectInBox() {
		Boolean isInBox = false;
		if(mBigBox.mMaxX >= mObj.mMaxX && 
				mBigBox.mMinX <= mObj.mMinX &&	
				mBigBox.mMaxY >= mObj.mMaxY && 
				mBigBox.mMinY <= mObj.mMinY && 
				mBigBox.mMaxZ >= mObj.mMaxZ && 
				mBigBox.mMinZ <= mObj.mMinZ 
				){
			isInBox = true;
		}
		return isInBox;
	}
    
	static Boolean IsInBox_last = false;
	private void UpdateObjColor(){
		Boolean isInBox = checkObjectInBox();
		if(isInBox != IsInBox_last){
			if (isInBox)
				mObj.setColor(mObj.getSettingColor());
			else
				mObj.setWarningColor(IOUtils.WARNING_COLOR);
			IsInBox_last = isInBox;
		}
	}
    
	private Vector3f transformedSphereCenter = new Vector3f();
	private Ray transformedRay = new Ray();
	private Matrix4f matInvertModel = new Matrix4f();
	private Vector3f[] mpTriangle = { new Vector3f(), new Vector3f(),
			new Vector3f() };
	float [] mObjectCenter2DPoint = new float[2];
	
	public float[] getObjectCenter2DPoint() {
		return mObjectCenter2DPoint;
	}
	
	public Vector3f getCurTouch3DPoint() {
		return PickFactory.getPickRay().mvOrigin;
	}
	
	/**
	 * 更新拾取事件
	 */
	private void updatePick() {
		if (!AppConfig.gbNeedPick) {
			return;
		}
		
		Vector3f centerPoint = mObj.GetCenterPointer();
		Vector3f v = PickFactory.D3to2DPoint(centerPoint.x, 0.0f, centerPoint.z);
		//Log.d("to2D =" + v);
		mObjectCenter2DPoint[0] = v.x;
		mObjectCenter2DPoint[1] = v.y;
		
		AppConfig.gbNeedPick = false;
		// 更新最新的拾取射线
		PickFactory.update(AppConfig.gScreenX, AppConfig.gScreenY);
		// 获得最新的拾取射线
		Ray ray = PickFactory.getPickRay();

		// 首先把模型的绑定球通过模型矩阵，由模型局部空间变换到世界空间
		AppConfig.gMatModel.transform(mObj.mLineBox.getSphereCenter(),
				transformedSphereCenter);

		// 触碰的立方体面的标记为无
		//cube.surface = -1;

		// 首先检测拾取射线是否与模型绑定球发生相交
		// 这个检测很快，可以快速排除不必要的精确相交检测
		if (ray.intersectSphere(transformedSphereCenter, mObj.mLineBox.getSphereRadius())) {
			// 如果射线与绑定球发生相交，那么就需要进行精确的三角面级别的相交检测
			// 由于我们的模型渲染数据，均是在模型局部坐标系中
			// 而拾取射线是在世界坐标系中
			// 因此需要把射线转换到模型坐标系中
			// 这里首先计算模型矩阵的逆矩阵
			matInvertModel.set(AppConfig.gMatModel);
			matInvertModel.invert();
			// 把射线变换到模型坐标系中，把结果存储到transformedRay中
			ray.transform(matInvertModel, transformedRay);
			
			//Log.i( " found Sphere !!!!!!!!!! ");
			
			// 将射线与模型做精确相交检测
			if (mObj.mLineBox.intersect(transformedRay)) {
				
				
				// 如果找到了相交的最近的三角形
				AppConfig.gbTrianglePicked = true;
				
			//	Log.i( " found obj !!!!!!!!!! ");
				// 触碰了哪一个面
				/*
				Log.i("触碰的立方体面", "=标记=" + cube.surface);
				// 回调
				if (null != onSurfacePickedListener) {
					onSurfacePickedListener.onSurfacePicked(cube.surface);
				}
				// 填充数据到被选取三角形的渲染缓存中
				mBufPickedTriangle.clear();
				for (int i = 0; i < 3; i++) {
					IBufferFactory
							.fillBuffer(mBufPickedTriangle, mpTriangle[i]);
					// Log.i("点" + i, mpTriangle[i].x + "\t" + mpTriangle[i].y
					// + "\t" + mpTriangle[i].z);
				}
				mBufPickedTriangle.position(0);
				*/
			}
		} else {
			AppConfig.gbTrianglePicked = false;
		}
	}

	
    public void setAngle(float angle) {
        mAngle = angle;
    }

    public float getAngle() {
        return mAngle;
    }
}


