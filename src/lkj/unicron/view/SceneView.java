package lkj.unicron.view;

import org.join.ogles.lib.Vector3f;
import org.join.raypick.AppConfig;

import lkj.unicron.object.D3Object;
import lkj.unicron.renderer.KubeRenderer;
import lkj.unicron.renderer.STLRenderer;
import lkj.unicron.util.IOUtils;
import lkj.unicron.util.Log;
import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SceneView extends GLSurfaceView {
	        
	public KubeRenderer mRenderer;
	public D3Object mObj;

	public final int VIEW_MODE_LOOK=IOUtils.LOOKPANNEL_ID;
	public final int VIEW_MODE_MOVE=IOUtils.MOVEPANNEL_ID;
	public final int VIEW_MODE_ROTATE=IOUtils.ROTATEPANNEL_ID;
	public final int VIEW_MODE_SCALE=IOUtils.SCALEPANNEL_ID;
	
	public final int VIEW_MODE_SUB_TOUCH= IOUtils.VIEW_MODE_SUB_TOUCH;
	public final int VIEW_MODE_SUB_SHOW_PANNEL= IOUtils.VIEW_MODE_SUB_SHOW_PANNEL;
	
	public int mViewMode = VIEW_MODE_LOOK;
	public int mViewMode_sub = VIEW_MODE_SUB_TOUCH;
	
	public SceneView(Context context) {
		super(context);
		if (mRenderer == null){
			mRenderer = new KubeRenderer(context);
			setRenderer(mRenderer);
		}
		//this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	public void AddD3Object(D3Object obj) {
		mObj = null;
		mObj = obj;
		mRenderer.addD3Object(obj);
		//this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		//this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	private int mCurObserverDirection = IOUtils.DIRECTION_FRONT;
	
	public void setObseverDirection(int direction) {
			AppConfig.gMatModel.setIdentity();
		switch (direction){
			case IOUtils.DIRECTION_RIGHT45:
				mRenderer.mvEye.x  = IOUtils.EYE_Z_DISTANCE/2; 
				mRenderer.mvEye.y  = 0; 
				mRenderer.mvEye.z  = IOUtils.EYE_Z_DISTANCE/2; 
				break;
			case IOUtils.DIRECTION_LEFT:
				mRenderer.mvEye.x  = -IOUtils.EYE_Z_DISTANCE; 
				mRenderer.mvEye.y  =  IOUtils.EYE_Y_DISTANCE; 
				mRenderer.mvEye.z  = 0; 
				break;
			case IOUtils.DIRECTION_RIGHT:
				mRenderer.mvEye.x  =  IOUtils.EYE_Z_DISTANCE; 
				mRenderer.mvEye.y  =  IOUtils.EYE_Y_DISTANCE; 
				mRenderer.mvEye.z  = 0; 
				break;
			case IOUtils.DIRECTION_FRONT:
				mRenderer.mvEye.x  = IOUtils.EYE_X_DISTANCE; 
				mRenderer.mvEye.y  = IOUtils.EYE_Y_DISTANCE; 
				mRenderer.mvEye.z  = IOUtils.EYE_Z_DISTANCE; 
				break;
			case IOUtils.DIRECTION_BACK:
				mRenderer.mvEye.x  = 0; 
				mRenderer.mvEye.y  = IOUtils.EYE_Y_DISTANCE; 
				mRenderer.mvEye.z  = -IOUtils.EYE_Z_DISTANCE; 
				break;
			case IOUtils.DIRECTION_TOP:
				mRenderer.mvEye.x  = 0; 
				mRenderer.mvEye.y = IOUtils.EYE_Z_DISTANCE; 
				mRenderer.mvEye.z = 0 ;
				break;
		}
		
		mCurObserverDirection = direction;
		
		//mRenderer.mvCenter.x  = 0; 
		//mRenderer.mvCenter.y  = 0f; 
		//mRenderer.mvCenter.z  = 0f; 
		
		mRenderer.mvUp.x  = 0; 
		if (direction == IOUtils.DIRECTION_TOP){
			mRenderer.mvUp.y  = 0f; 
			mRenderer.mvUp.z  = 1f; 
		} else {
			mRenderer.mvUp.y  = 1f; 
			mRenderer.mvUp.z  = 0f; 
		}
		
		Log.i("eyex=" + mRenderer.mvEye.x + ",mRenderer.mvEye.y=" + mRenderer.mvEye.y + ",mRenderer.mvEye.z=" + mRenderer.mvEye.z);
		
	}
	
	public void setObseverDistance(Boolean near){
		switch (mCurObserverDirection){
		case IOUtils.DIRECTION_LEFT:
			if(near){
				 mRenderer.mvEye.x++; 
			} else {
				mRenderer.mvEye.x--;
			}
			break;
		case IOUtils.DIRECTION_RIGHT:
			if(near){
				 mRenderer.mvEye.x--; 
			} else {
				mRenderer.mvEye.x++;
			}
			break;
		case IOUtils.DIRECTION_FRONT:
			if(near){
				 mRenderer.mvEye.z--; 
			} else {
				mRenderer.mvEye.z++;
			}
			break;
		case IOUtils.DIRECTION_BACK:
			if(near){
				 mRenderer.mvEye.z++; 
			} else {
				mRenderer.mvEye.z--;
			}
			break;
		case IOUtils.DIRECTION_TOP:
			mRenderer.mvEye.x  = 0; 
			mRenderer.mvEye.y  =  500f;//IOUtils.EYE_DISTANCE; 
			mRenderer.mvEye.z  = 300f; 
			mCurObserverDirection = IOUtils.DIRECTION_TOP;
			break;
	}
	}
	

	
	public float[] getVerticies() {
		return 	mObj.getVertexArray();
	}
	
	private float getPinchDistance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return android.util.FloatMath.sqrt(x * x + y * y);
	}
	
	private void getPinchCenterPoint(MotionEvent event, PointF pt) {
		pt.x = (event.getX(0) + event.getX(1)) * 0.5f;
		pt.y = (event.getY(0) + event.getY(1)) * 0.5f;
	}
	
	private void changeDistance(float distance) {
		Log.i("distance:" + distance);
		//lkj stlRenderer.distanceZ = distance;
		STLRenderer.requestRedraw();
		requestRender();
	}

	private boolean isRotate = true;
	
	public boolean isRotate() {
		return isRotate;
	}
	
	public void setRotate(boolean isRotate) {
		this.isRotate = isRotate;
	}
	
	private final float TOUCH_SCALE_FACTOR = 180.0f / 380;
	private float previousX;
	private float previousY;
	
	// zoom rate (larger > 1.0f > smaller)
	private float lastPinchScale = 100.0f;
	private float pinchScale = 1.0f;

	private PointF pinchStartPoint = new PointF();
	private float pinchStartZ = 0.0f;
	private float pinchStartDistance = 0.0f;
	private float pinchMoveX = 0.0f;
	private float pinchMoveY = 0.0f;

	// for touch event handling
	private static final int TOUCH_NONE = 0;
	private static final int TOUCH_DRAG = 1;
	private static final int TOUCH_ZOOM = 2;
	private int touchMode = TOUCH_NONE;
	
	private boolean isFirstMove = false;
	private float totalRotateDegree = 0.0f;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		AppConfig.setTouchPosition(x, y);
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// starts pinch
		case MotionEvent.ACTION_POINTER_DOWN:
			if (event.getPointerCount() >= 2) {
				pinchStartDistance = getPinchDistance(event);
				pinchStartZ = mRenderer.mLookScaleDistance;
				if (pinchStartDistance > 50f) {
					getPinchCenterPoint(event, pinchStartPoint);
					previousX = pinchStartPoint.x;
					previousY = pinchStartPoint.y;
					touchMode = TOUCH_ZOOM;
				}
			}
			break;
		
		case MotionEvent.ACTION_MOVE:
			if (touchMode == TOUCH_ZOOM && pinchStartDistance > 0) {
				// on pinch
				PointF pt = new PointF();
				
				getPinchCenterPoint(event, pt);
				pinchMoveX = pt.x - previousX;
				pinchMoveY = pt.y - previousY;
				float dx = pinchMoveX;
				float dy = pinchMoveY;
				previousX = pt.x;
				previousY = pt.y;
	
				pinchScale = getPinchDistance(event) / pinchStartDistance ;

				if (mViewMode == VIEW_MODE_LOOK){
					mRenderer.mLookScaleDistance = pinchStartZ/ pinchScale;
				} else if (mViewMode == VIEW_MODE_SCALE && (mViewMode_sub == VIEW_MODE_SUB_TOUCH)){
					if(pinchScale >= 1.0f){
						pinchScale = 1.006f;
					} else {
						pinchScale = 0.994f;
					}
					IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(pinchScale, pinchScale, pinchScale));
				}
				//Log.d("scale = " + pinchScale);
			}
			break;
		
		// end pinch
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if (touchMode == TOUCH_ZOOM) {
				touchMode = TOUCH_NONE;
				pinchMoveX = 0.0f;
				pinchMoveY = 0.0f;
				pinchScale = 1.0f;
				pinchStartPoint.x = 0.0f;
				pinchStartPoint.y = 0.0f;
				mRenderer.mLookScaleDistance = 100;
			}
			break;
	}

	switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// start drag
		case MotionEvent.ACTION_DOWN:
			AppConfig.gbNeedPick = false;
			if (event.getPointerCount() == 1 && 
					(mViewMode == VIEW_MODE_LOOK || mViewMode == VIEW_MODE_MOVE || mViewMode == VIEW_MODE_ROTATE)
					) {
				touchMode = TOUCH_DRAG;
				if(mObj != null){
					isFirstMove = false;
					mRenderer.mTwoLine.setStartPoint(mObj.GetCenterPointer());
					mRenderer.mArgCircle.setStartArgCircle();
					
					totalRotateDegree = 0f;
				}
				//previousX = event.getX();
				//previousY = event.getY();
			}
			break;
		
		case MotionEvent.ACTION_MOVE:
			//Log.d("mViewMode_sub =" + mViewMode_sub);
			if(touchMode != TOUCH_DRAG)
				break;
			float dx = x - previousX;
			float dy = y - previousY;

			//if (mViewMode == VIEW_MODE_LOOK && (mCurObserverDirection == IOUtils.DIRECTION_FRONT) ){
			if (mViewMode == VIEW_MODE_LOOK){
				AppConfig.gbNeedPick = false;
				dy = x - previousX;
				dx = y - previousY;
				float d = (float) (Math.sqrt(dx * dx + dy * dy));
				// 旋转轴单位向量的x,y值（z=0）
				mRenderer.mfAngleX = dx;
				mRenderer.mfAngleY = dy;
				// 手势距离
				mRenderer.mLookRotateDistance = d;
			}
			
			if((mViewMode_sub != VIEW_MODE_SUB_TOUCH))
				break;
			
			if(mViewMode == VIEW_MODE_MOVE || mViewMode == VIEW_MODE_ROTATE){
				AppConfig.gbNeedPick = true;
			}

				
			if (mViewMode == VIEW_MODE_ROTATE) {
				angleX = dx * TOUCH_SCALE_FACTOR;
				angleY = dy * TOUCH_SCALE_FACTOR;

				if (AppConfig.gbTrianglePicked  && (AppConfig.gMatModel.getIsIdentity()) ) {
					Vector3f point = mObj.GetCenterPointer();
					if(dx>1.0f || dx <-1.0f){
						float lengthWidthHeight[] = mObj.GetLengthWidthHeight();
						float maxEdge  = Math.max(lengthWidthHeight[0], lengthWidthHeight[1]);
						if(isFirstMove == false){
							float [] objectCenter2DPoint = mRenderer.getObjectCenter2DPoint();
							mRenderer.mArgCircle.set2DOrignPoint(objectCenter2DPoint[0], objectCenter2DPoint[1]);
							isFirstMove = true;
						}
						totalRotateDegree += angleX;
						mRenderer.mArgCircle.setLabelDegree(totalRotateDegree);
				    	mRenderer.mArgCircle.setArgCircle(point, maxEdge, totalRotateDegree);
				    	mRenderer.mArgCircle.setVisable(true);
				    	
						IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform(
								(float) Math.sin(Math.PI / 180 * angleX), point.x,
								point.y, point.z, IOUtils.AxisY));
					} /*else if(dy>1.0f || dy <-1.0f){
						IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform(
								(float) Math.sin(Math.PI / 180 * angleY), point.x,
								point.y, point.z, IOUtils.AxisX));
					}*/
				}
				
				//Log.d("angleX=" + angleX + ",dx=" + dx + ",dy=" + dy);
			} else if (mViewMode == VIEW_MODE_MOVE) {
				if (AppConfig.gbTrianglePicked && (AppConfig.gMatModel.getIsIdentity()) ){

					switch (mCurObserverDirection){
					case IOUtils.DIRECTION_FRONT:
						if(isFirstMove == false){
							float [] objectCenter2DPoint = mRenderer.getObjectCenter2DPoint();
							mRenderer.mTwoLine.set2DOrignPoint(objectCenter2DPoint[0], objectCenter2DPoint[1]);
							isFirstMove = true;
						}
						mRenderer.mTwoLine.setEndPoint(mObj.GetCenterPointer());
						
						IOUtils.ApplyMat(mObj,IOUtils.getTranslateTransform(dx, 0.0f, dy));
						break;
/*					case IOUtils.DIRECTION_LEFT:
						IOUtils.ApplyMat(mObj,IOUtils.getTranslateTransform(-dy, 0.0f, dx));
						break;
					case IOUtils.DIRECTION_RIGHT:
						IOUtils.ApplyMat(mObj,IOUtils.getTranslateTransform(dy, 0.0f, -dx));
						break;

					case IOUtils.DIRECTION_BACK:
						IOUtils.ApplyMat(mObj,IOUtils.getTranslateTransform(-dx, 0.0f, -dy));
						break;
					case IOUtils.DIRECTION_TOP:
						IOUtils.ApplyMat(mObj,IOUtils.getTranslateTransform(-dx, 0.0f, -dy));
						break;*/
					}
				}
			}
			//Log.d("is picked = " + AppConfig.gbTrianglePicked);
			//Log.d("dx=" + dx + ",dy=" + dy);
			break;
		
		// end drag
		case MotionEvent.ACTION_UP:
			if (touchMode == TOUCH_DRAG)
				touchMode = TOUCH_NONE;
			AppConfig.gbNeedPick = false;
			break;			

	}
	previousX = x;
	previousY = y;
	return true;
	}
	float angleX =0.0f;
	float angleY =0.0f;
}
