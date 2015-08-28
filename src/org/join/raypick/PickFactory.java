package org.join.raypick;

import lkj.unicron.util.Log;

import org.join.ogles.lib.Projector;
import org.join.ogles.lib.Ray;
import org.join.ogles.lib.Vector3f;

public class PickFactory {

	private static Ray gPickRay = new Ray();

	public static Ray getPickRay() {
		return gPickRay;
	}

	private static Projector gProjector = new Projector();

	private static float[] gpObjPosArray = new float[4];

	/**
	 * 更新拾取射线
	 * 
	 * @param screenX
	 *            - 屏幕坐标X
	 * @param screenY
	 *            - 屏幕坐标Y
	 */
	synchronized	public static void update(float screenX, float screenY) {
		AppConfig.gMatView.fillFloatArray(AppConfig.gpMatrixViewArray);

		// 由于OpenGL坐标系原点为左下角，而窗口坐标系原点为左上角
		// 因此，在OpenGl中的Y应该需要用当前视口高度，减去窗口坐标Y
		float openglY = AppConfig.gpViewport[3] - screenY;
		// z = 0 , 得到P0
		gProjector.gluUnProject(screenX, openglY, 0.0f,
				AppConfig.gpMatrixViewArray, 0, AppConfig.gpMatrixProjectArray,
				0, AppConfig.gpViewport, 0, gpObjPosArray, 0);
		// 填充射线原点P0
		gPickRay.mvOrigin.set(gpObjPosArray[0], gpObjPosArray[1],
				gpObjPosArray[2]);

	//	Log.d("screenX=" + screenX + ",screenY=" + screenY);
	//	Log.d("gpObjPosArray[0]=" + gpObjPosArray[0] + ",gpObjPosArray[1]=" + gpObjPosArray[1] + ",gpObjPosArray[2]" + gpObjPosArray[2]);
		// z = 1 ，得到P1
		gProjector.gluUnProject(screenX, openglY, 1.0f,
				AppConfig.gpMatrixViewArray, 0, AppConfig.gpMatrixProjectArray,
				0, AppConfig.gpViewport, 0, gpObjPosArray, 0);
		// 计算射线的方向，P1 - P0
		gPickRay.mvDirection.set(gpObjPosArray[0], gpObjPosArray[1],
				gpObjPosArray[2]);
		gPickRay.mvDirection.sub(gPickRay.mvOrigin);
		// 向量归一化
		gPickRay.mvDirection.normalize();
	}

	private final static Object mLock = new Object();
	
	synchronized public static Vector3f to3DPoint(float screenX, float screenY) {
		Vector3f v = new Vector3f();
		synchronized (mLock) {
		AppConfig.gMatView.fillFloatArray(AppConfig.gpMatrixViewArray);
		
		// 由于OpenGL坐标系原点为左下角，而窗口坐标系原点为左上角
		// 因此，在OpenGl中的Y应该需要用当前视口高度，减去窗口坐标Y
		float openglY = AppConfig.gpViewport[3] - screenY;
		// z = 0 , 得到P0
		gProjector.gluUnProject(screenX, openglY, 0.0f,
				AppConfig.gpMatrixViewArray, 0, AppConfig.gpMatrixProjectArray,
				0, AppConfig.gpViewport, 0, gpObjPosArray, 0);
	}
		// 填充射线原点P0
		v.set(gpObjPosArray[0], gpObjPosArray[1],
				gpObjPosArray[2]);
		return v;
	}
	
	//lkj Vector3f v
	public static Vector3f D3to2DPoint(float x, float y, float z) {		
		Vector3f v_ret = new Vector3f();
		//synchronized (mLock) {
		AppConfig.gMatView.fillFloatArray(AppConfig.gpMatrixViewArray);

		gProjector.gluProject(x, y, z,
				AppConfig.gpMatrixViewArray, 0, AppConfig.gpMatrixProjectArray,
				0, AppConfig.gpViewport, 0, gpObjPosArray, 0);
		//}
		// 
		v_ret.set(gpObjPosArray[0], AppConfig.gpViewport[3] - gpObjPosArray[1],
				gpObjPosArray[2]);
		return v_ret;
	}
}
