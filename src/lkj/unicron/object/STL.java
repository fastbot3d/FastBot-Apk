package lkj.unicron.object;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.join.ogles.lib.Vector3f;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import lkj.unicron.util.IOUtils;
import lkj.unicron.util.Log;

public class STL extends ModelBase {
	
	String mPathname = null;
	Handler mProgressHandler = null;
	byte[] mStlBytes = null;
	ArrayList<Vector3f> mVertexList = new ArrayList<Vector3f>();
	ArrayList<Vector3f> mNormalList = new ArrayList<Vector3f>();
	//ArrayList<Integer> mIndex = new ArrayList<Integer>();

	
	public STL(String str, byte[] stlBytes) {
		mPathname = str;
		mStlBytes = stlBytes;
	}
	
	public STL(String str) {
		mPathname = str;
	}
	
		
	private boolean isText(byte[] bytes) {
		byte[] b5 = new byte[5];
		b5[0] = bytes[0];
		b5[1] = bytes[1];
		b5[2] = bytes[2];
		b5[3] = bytes[3];
		b5[4] = bytes[4];
		
		String str = new String(b5);
		Log.i("stl head text =" + str);
		if(str.equalsIgnoreCase("solid")){
			return true;
		} else {
			return false;
		}
		/*
		for (byte b : bytes) {
			if (b == 0x0a || b == 0x0d || b == 0x09) {
				// white spaces
				continue;
			}
			if (b < 0x20 || (0xff & b) >= 0x80) {
				// control codes
				return false;
			}
		}
		return true;
		*/
	}
	
	public D3Object loadfile(Handler h){
		mProgressHandler = h;
		Bundle bdle= new Bundle();
		bdle.putString("tile", "3D LOADING");
		bdle.putString("message", "loading......");		
		Message msg =mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_NEW);
		msg.setData(bdle);
		msg.sendToTarget();
		
		boolean text_fail = true;
		
		try {
			if (isText(mStlBytes)) {
				Log.i("trying text...");
				processText(new String(mStlBytes));
				if((mNormalList.size() >0) || (mVertexList.size() >0)){
					text_fail = false;
				}
			} 
			
			if (text_fail) {
				Log.i("trying binary...");
				processBinary(mStlBytes);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("loadfile Exception." + e);
		}   finally {
			mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_DISMISS).sendToTarget();
			mProgressHandler = null;
		}	
		
		Log.i("loadfile mVertexList=" + mVertexList.size() + ", mNormalList=" + mNormalList.size());
		
		if((mNormalList.size()>0)  && (mVertexList.size()>0)){
			D3Object obj = new D3Object(mVertexList, mNormalList);
			return obj;
		}

		return null;
	} 

	
	public int savefile(D3Object obj, Handler h){
		mProgressHandler = h;
		ArrayList<Vector3f> vertexList = obj.getVertexs();
		ArrayList<Vector3f> normalList = obj.getNormals();
				
		Bundle bdle= new Bundle();
		bdle.putString("tile", "3D SAVING");
		bdle.putString("message", "saving......");
		Message msg =mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_NEW);
		msg.setData(bdle);
		msg.sendToTarget();
		
		int vertexSize = vertexList.size();
		
		mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_MAX, vertexSize, 0).sendToTarget();
		mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SHOW).sendToTarget();
		
		Vector3f v = null;
		FileOutputStream out = null;
		try {
			int i = 0, j=0;
			byte buf[] = new byte[84];
			
			ByteBuffer packet = ByteBuffer.allocate(12 * (Float.SIZE / 8));
			packet.order(ByteOrder.LITTLE_ENDIAN); //LITTLE_ENDIAN
			

			out = new FileOutputStream(mPathname);
			
			byte buf1[] = null;
			buf1 = "lkj BINARY STL EXPORT.".getBytes();
			
			for(i=0; i<buf1.length; i++){
				buf[i] = buf1[i];
			}
			
			buf[83] = (byte)(((vertexSize/3) >>24) & 0XFF);
			buf[82] = (byte)(((vertexSize/3) >>16) & 0XFF);
			buf[81] = (byte)(((vertexSize/3) >>8) & 0XFF);
			buf[80] = (byte)((vertexSize /3) & 0XFF);
						
			out.write(buf, 0, 84);	
						
			for(i=0,j=0; i< vertexSize ; i+=3, j+=3){
				v=  normalList.get(j);
				packet.putFloat(v.x);
				packet.putFloat(v.y);
				packet.putFloat(v.z);
				
				v = vertexList.get(i);
				packet.putFloat(v.x);
				packet.putFloat(v.y);
				packet.putFloat(v.z);
				//out.write(packet.array());
				//packet.clear();
				
				v = vertexList.get(i+1);
				packet.putFloat(v.x);
				packet.putFloat(v.y);
				packet.putFloat(v.z);
				//out.write(packet.array());
				//packet.clear();
				
				v = vertexList.get(i+2);
				packet.putFloat(v.x);
				packet.putFloat(v.y);
				packet.putFloat(v.z);
				
				out.write(packet.array());
				packet.clear();
				
				out.write((byte)0);
				out.write((byte)0);
				
				mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SETVALUE, i, 0).sendToTarget();
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("savefile Exception." + e);
		}   finally {
				try {
					if(out!=null){
						out.flush();
						out.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("savefile close Exception." + e);
				}
			mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_DISMISS).sendToTarget();
			mProgressHandler = null;
		}		

		return 1;
	}
	

	
	private void processText(String stlText) throws Exception {
		mNormalList.clear();
		mVertexList.clear();
	//	mIndex.clear();
		String[] stlLines = stlText.split("\n");
		
		mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_MAX, stlLines.length, 0).sendToTarget();
		mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SHOW).sendToTarget();
		Log.d("processText");
		for (int i = 0; i < stlLines.length; i++) {
			String string = stlLines[i].trim();
			if (string.startsWith("facet normal ")) {
				string = string.replaceFirst("facet normal ", "");
				String[] normalValue = string.split(" ");
				float x = Float.parseFloat(normalValue[0]);
				float y = Float.parseFloat(normalValue[1]);
				float z = Float.parseFloat(normalValue[2]);
				Vector3f v = new Vector3f(x, y, z, i);
				mNormalList.add(v);
				//Log.d("normal add");
			}
			if (string.startsWith("vertex ")) {
				string = string.replaceFirst("vertex ", "");
				String[] vertexValue = string.split(" ");
				float x = Float.parseFloat(vertexValue[0]);
				float y = Float.parseFloat(vertexValue[1]);
				float z = Float.parseFloat(vertexValue[2]);
				Vector3f v = new Vector3f(x, y, z, i);
				mVertexList.add(v);
				//mIndex.add(i);
				GetAABB(x, y, z);
				//Log.d("vertex add");
			}
			
			if (i % (stlLines.length / 50) == 0) {
				mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SETVALUE, i, 0).sendToTarget();
			}
		}
	}
	
	
	private int getIntWithLittleEndian(byte[] bytes, int offset) {
		return (0xff & bytes[offset]) | ((0xff & bytes[offset + 1]) << 8) | ((0xff & bytes[offset + 2]) << 16) | ((0xff & bytes[offset + 3]) << 24);
	}
	
	private void processBinary(byte[] stlBytes) throws Exception {
		mNormalList.clear();
		mVertexList.clear();
		//mIndex.clear();
		int index = 0;
		int faceSize = getIntWithLittleEndian(stlBytes, 80);
		Log.i("faceSize:" + faceSize);
		
		Vector3f edge1 = new Vector3f();
		Vector3f edge2 =  new Vector3f();
		
		mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_MAX, faceSize, 0).sendToTarget();
		mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SHOW).sendToTarget();
		for (int i = 0; i < faceSize; i++) {
		//	float normal_x = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50));
		//	float normal_y = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 4));
		//	float normal_z = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 8));
		//	Vector3f normal = new Vector3f(normal_x, normal_y, normal_z, i);
			/*mNormalList.add(normal);
			mNormalList.add(normal);			
			mNormalList.add(normal);*/
			
			float x = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 12));
			float y = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 16));
			float z = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 20));
			GetAABB(x, y, z);
			Vector3f v1 = new Vector3f(x, y, z, index);
			mVertexList.add(v1);
		//	mIndex.add(index);
			
			x = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 24));
			y = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 28));
			z = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 32));
			GetAABB(x, y, z);
			Vector3f v2 = new Vector3f(x, y, z, index+1);
			mVertexList.add(v2);
		//	mIndex.add(index+1);
			
			x = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 36));
			y = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 40));
			z = Float.intBitsToFloat(getIntWithLittleEndian(stlBytes, 84 + i * 50 + 44));
			GetAABB(x, y, z);
			Vector3f v3 = new Vector3f(x, y, z, index+2);
			mVertexList.add(v3);
		//	mIndex.add(index+2);
			

			Vector3f norm =  new Vector3f();
			edge1.sub(v3, v1);
			edge2.sub(v2, v1);
			norm.cross(edge1, edge2);
			norm.inv_normalize();
			mNormalList.add(norm);
			mNormalList.add(norm);
			mNormalList.add(norm);
			
			index += 3;
			if (i % (faceSize / 50) == 0) {
				mProgressHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SETVALUE, i, 0).sendToTarget();
			}
		}
	}
	
	
}
