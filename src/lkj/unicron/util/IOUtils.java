package lkj.unicron.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;





import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import lkj.unicron.object.D3Object;

import org.join.ogles.lib.GLColor;
import org.join.ogles.lib.Matrix4f;
import org.join.ogles.lib.Vector3f;
import org.join.ogles.lib.Vector4f;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLUtils;

public class IOUtils {
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
	public final static int MESSAGE_PROGRESS_MAX = 1000;
	public final static int MESSAGE_PROGRESS_SETVALUE = 1001;
	public final static int MESSAGE_PROGRESS_SHOW = 1002;
	public final static int MESSAGE_PROGRESS_DISMISS = 1003;
	public final static int MESSAGE_PROGRESS_NEW = 1004;
	
	public final static int MESSAGE_SAVE_VERTEX = 1100;
	public final static int MESSAGE_RESTORE_VERTEX = 1101;
	
	public final static int MESSAGE_3D_MOVE = 1102;
	public final static int MESSAGE_3D_ROTATE = 1103;
	public final static int MESSAGE_3D_SCALE = 1104;
	
	public final static int DIRECTION_LEFT = 1<<0;
	public final static int DIRECTION_RIGHT = 1<<1;
	public final static int DIRECTION_FRONT = 1<<2;
	public final static int DIRECTION_BACK = 1<<3;
	public final static int DIRECTION_TOP =   1<<4;
	public final static int DIRECTION_BOTTOM =  1<<5;
	public final static int DIRECTION_RIGHT45 = 1<<6;
	
	//mm
	public static float MACHINE_LENGTH = 200f;
	public static float MACHINE_WIDTH = 200f;
	public static float MACHINE_HEIGHT = 200f;
	
	public static float EYE_X_DISTANCE = 0f;
	public static float EYE_Y_DISTANCE = 110f;
	public static float EYE_Z_DISTANCE = 370f;
	
	public static GLColor WARNING_COLOR= new GLColor(30000, 0, 0,65535);
	
	static public final int AxisX = 0;
	static public final int AxisY = 1;
	static public final int AxisZ = 2;	
	
	public static String cur3DFileName = null;
	public static String curaEnginePath = null;
	
	static public final int LOOKPANNEL_ID = 1111;
	static public final int MOVEPANNEL_ID = 1112;
	static public final int ROTATEPANNEL_ID = 1113;
	static public final int SCALEPANNEL_ID = 1114;
	
	static public final int VIEW_MODE_SUB_TOUCH= 1;
	static public final int VIEW_MODE_SUB_SHOW_PANNEL= 2;
	
	static public final int SKY_RADIUS = 1000;
	
	
	public static Matrix4f getTranslateTransform(float x, float y, float z) {
		Matrix4f transform = new Matrix4f();
		transform.glTranslatef(x, y, z);
		return transform;
	}
	
	public static Matrix4f getScaleTransform(float x, float y, float z) {
		Matrix4f transform = new Matrix4f();
		transform.glScalef(x, y, z);
		return transform;
	}
	
	//待优化， 直接写成矩阵，不用相乘。 任意角度rot() 矩阵。
	public static Matrix4f getRotateTransform_Old(float angle, float x, float y, float z, int axis) {
		Matrix4f transform = new Matrix4f();
		Matrix4f m_m0 = new Matrix4f();	
		Matrix4f m_rotate = new Matrix4f();	
		Matrix4f m_pos = new Matrix4f();	
		
		float a=0.0f;
		
		m_m0.glTranslatef(-x, -y, -z);
		m_pos.glTranslatef(x, y, z);

		switch (axis){
			case AxisX:
				a = 1.0f;
				m_rotate.rotX(angle);
				break;
			case AxisY:
				a = 1.0f;
				m_rotate.rotY(angle);
				break;
			case AxisZ:
				a = 1.0f;
				m_rotate.rotZ(angle);
				break;			
		}
		
		m_pos.mul(m_rotate);
		m_pos.mul(m_m0);
		
		//transform.rotY(angle);
	//	Vector3f axis = new Vector3f(x,y,z);
	//	transform.glRotateAxis(axis,angle);
		
		//transform.glRotatLkj(angle, x,  y, z, axis);
		//transform.glRotatef(angle, x,  y, z);			
		return m_pos;
	}
	
	public static Matrix4f getRotateTransform(float angle, float x, float y, float z, int axis) {
		Matrix4f m_return = new Matrix4f();
		float sinAngle, cosAngle;

		sinAngle = (float) Math.sin((double) angle);
		cosAngle = (float) Math.cos((double) angle);
		
		switch (axis){
			case AxisX:
				m_return.m00 = 1.0f;
				m_return.m01 = 0.0f;
				m_return.m02 = 0.0f;
				m_return.m03 = 0.0f;
				
				m_return.m10 = 0.0f;
				m_return.m11 = cosAngle;
				m_return.m12 = -sinAngle;
				m_return.m13 = -y * cosAngle + z * sinAngle + y;
				
				m_return.m20 = 0.0f;
				m_return.m21 = sinAngle;
				m_return.m22 = cosAngle;
				m_return.m23 = -y * sinAngle - z*cosAngle + z;
				
				m_return.m30 = 0.0f;
				m_return.m31 = 0.0f;
				m_return.m32 = 0.0f;
				m_return.m33 = 1.0f;
				break;
			case AxisY:
				m_return.m00 = cosAngle;
				m_return.m01 = 0.0f;
				m_return.m02 = sinAngle;
				m_return.m03 = -x * cosAngle - z*sinAngle + x;
				
				m_return.m10 = 0.0f;
				m_return.m11 = 1;
				m_return.m12 = 0f;
				m_return.m13 = 0f;
				
				m_return.m20 = -sinAngle;
				m_return.m21 = 0;
				m_return.m22 = cosAngle;
				m_return.m23 =x * sinAngle - z*cosAngle + z;
				
				m_return.m30 = 0.0f;
				m_return.m31 = 0.0f;
				m_return.m32 = 0.0f;
				m_return.m33 = 1.0f;
				break;
			case AxisZ:
				m_return.m00 = cosAngle;
				m_return.m01 = -sinAngle;
				m_return.m02 = 0.0f;
				m_return.m03 = -x * cosAngle + y*sinAngle + x;
				
				m_return.m10 = sinAngle;
				m_return.m11 = cosAngle;
				m_return.m12 = 0f;
				m_return.m13 = -x * sinAngle - y * cosAngle + y;
				
				m_return.m20 = 0;
				m_return.m21 = 0;
				m_return.m22 = 1.0f;
				m_return.m23 = 0;
				
				m_return.m30 = 0.0f;
				m_return.m31 = 0.0f;
				m_return.m32 = 0.0f;
				m_return.m33 = 1.0f;
				break;
		}
		return m_return;
	}
	
	public static Matrix4f glRotateAxis(Vector3f v1, Vector3f v2, float angle)
	{
		Matrix4f transform = new Matrix4f();	
		//Matrix4f transform2 = new Matrix4f();
		//Matrix4f transform3 = new Matrix4f();
		Matrix4f t1, t2, t3;
		
	/*	transform.glTranslatef(-v1.x, 0, -v1.z);	
		Log.d("transform = " + transform.toString());
		transform2.glRotatef(angle, 0.0f,  1.0f, 0.0f);
		Log.d("transform2 = " + transform2.toString());
		transform3.glTranslatef(v1.x, 0, v1.z);
		Log.d("transform3 = " + transform3.toString());
		
		transform.mul(transform2);
		Log.d("transform 4 = " + transform.toString());
		transform.mul(transform3);
		Log.d("transform n = " + transform.toString());
		*/
		//Vector3f axis = new Vector3f() ;
		//, Vector3f v2
		//axis.x = v1.x - v2.x;
		//axis.y = v1.y - v2.y;
		//axis.z = v1.z - v2.z;
			
		transform.RotateArbitraryLine(v1, v2, angle);			
		return transform;
	}
	
	public static void ApplyMat(D3Object obj, Matrix4f m4) {
		
		ArrayList<Vector3f> vertexList = obj.getVertexs();
		for(int i=0; i<vertexList.size(); i++){
			Vector3f v = vertexList.get(i);
			//if (i<10)
				//Log.d("Vector3f = " + v.toString());
			m4.transform(v, v);
			//if (i<10)
				//Log.d("---Vector3f = " + v.toString());
		}
		//Log.d("M4 = " + m4.toString());
		
		//obj.calculateAABB();
		obj.generateVertexData();
		m4 = null;
	}
	
	/**
	 * Convert <code>input</code> stream into byte[].
	 * 
	 * @param input
	 * @return Array of Byte
	 * @throws IOException
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);		
		return output.toByteArray();
	}
	
	/**
	 * Copy <code>length</code> size of <code>input</code> stream to <code>output</code> stream.
	 * This method will NOT close input and output stream.
	 * 
	 * @param input
	 * @param output
	 * @return long copied length
	 * @throws IOException
	 */
	private static long copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while ((n = input.read(buffer)) != -1) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
	
	/**
	 * Copy <code>length</code> size of <code>input</code> stream to <code>output</code> stream.
	 * 
	 * @param input
	 * @param output
	 * @return long copied length
	 * @throws IOException
	 */
	public static long copy(InputStream input, OutputStream output, int length) throws IOException {
		byte[] buffer = new byte[length];
		int count = 0;
		int n = 0;
		int max = length;
		while ((n = input.read(buffer, 0, max)) != -1) {
			output.write(buffer, 0, n);
			count += n;
			if (count > length) {
				break;
			}
			
			max -= n;
			if (max <= 0) {
				break;
			}
		}
		return count;
	}
	
	/**
	 * Close <code>closeable</code> quietly.
	 * 
	 * @param closeable
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		
		try {
			closeable.close();
		} catch (Throwable e) {
			// do nothing
		}		
	}
	
	public static String getEngineFilename()
	{
		return "CuraEngine";
	}
	
	
	public static Bitmap createBitmap(String text, int width, int height)
	{
	    Bitmap bitmap;
	    Canvas canvas;
	    Paint textPaint;
	    
	    textPaint = new Paint();
	    textPaint.setTextSize(32);
	    textPaint.setAntiAlias(true);
	    textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
	    
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);
        canvas.drawText(text, 0, 0, textPaint);

        canvas = null;
        
        return bitmap;
        
/*        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
        bitmap.recycle();
        bitmap = null;*/
	}
	
	public static ProgressDialog newProgressDialog(Context context, String title, String message) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(title);
		progressDialog.setMax(0);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(false);
		
		progressDialog.show();
		
		return progressDialog;
	}
	
	public static  Dialog onCreateDialog(Context context, String title, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        return dialog;
    }   

	
	
	//
	
	public static Matrix4f Calculation(double[] vectorBefore, double[] vectorAfter)
	{
	    double[] rotationAxis;
	    double rotationAngle;
	    double[][] rotationMatrix;
	    rotationAxis = CrossProduct(vectorBefore, vectorAfter);
	    rotationAngle = Math.acos(DotProduct(vectorBefore, vectorAfter) / Normalize(vectorBefore) / Normalize(vectorAfter));
	    rotationMatrix = RotationMatrix(rotationAngle, rotationAxis);
	    
	    Matrix4f m4 = new Matrix4f();
	    m4.m00 = (float) rotationMatrix[0][0];
	    m4.m01 = (float) rotationMatrix[0][1];
	    m4.m02 = (float) rotationMatrix[0][2];

	    m4.m10 = (float) rotationMatrix[1][0];
	    m4.m11 = (float) rotationMatrix[1][1];
	    m4.m12 = (float) rotationMatrix[1][2];
	    
	    m4.m20 = (float) rotationMatrix[2][0];
	    m4.m21 = (float) rotationMatrix[2][1];
	    m4.m22 = (float) rotationMatrix[2][2];
	    
	    return m4;
	}

	static double[] CrossProduct(double[] a, double[] b)
	{
	    double[] c = new double[3];

	    c[0] = a[1] * b[2] - a[2] * b[1];
	    c[1] = a[2] * b[0] - a[0] * b[2];
	    c[2] = a[0] * b[1] - a[1] * b[0];

	    return c;
	}

	static double DotProduct(double[] a, double[] b)
	{
	    double result;
	    result = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];

	    return result;
	}

	static double Normalize(double[] v)
	{
	    double result;

	    result = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);

	    return result;
	}

	static double[][] RotationMatrix(double angle, double[] u)
	{
	    double norm = Normalize(u);
	    double[][] rotatinMatrix = new double[3][3];
	    
	    u[0] = u[0] / norm;
	    u[1] = u[1] / norm;
	    u[2] = u[2] / norm;

	    rotatinMatrix[0][0] = Math.cos(angle) + u[0] * u[0] * (1 - Math.cos(angle));
	    rotatinMatrix[0][1] = u[0] * u[1] * (1 - Math.cos(angle) - u[2] * Math.sin(angle));
	    rotatinMatrix[0][2] = u[1] * Math.sin(angle) + u[0] * u[2] * (1 - Math.cos(angle));

	    rotatinMatrix[1][0] = u[2] * Math.sin(angle) + u[0] * u[1] * (1 - Math.cos(angle));
	    rotatinMatrix[1][1] = Math.cos(angle) + u[1] * u[1] * (1 - Math.cos(angle));
	    rotatinMatrix[1][2] = -u[0] * Math.sin(angle) + u[1] * u[2] * (1 - Math.cos(angle));
	      
	    rotatinMatrix[2][0] = -u[1] * Math.sin(angle) + u[0] * u[2] * (1 - Math.cos(angle));
	    rotatinMatrix[2][1] = u[0] * Math.sin(angle) + u[1] * u[2] * (1 - Math.cos(angle));
	    rotatinMatrix[2][2] = Math.cos(angle) + u[2] * u[2] * (1 - Math.cos(angle));

	    return rotatinMatrix;
	}
}
