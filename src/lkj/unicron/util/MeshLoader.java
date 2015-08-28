package lkj.unicron.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import lkj.unicron.object.D3Object;
import lkj.unicron.object.STL;

public class MeshLoader {

	public MeshLoader() {
		// TODO Auto-generated constructor stub
	}
	
	public static D3Object LoadFile(String pathname, Context context, Handler h) {
		D3Object obj = null;
		
		byte[] stlBytes = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(pathname.trim());
			stlBytes = IOUtils.toByteArray(in);
		} catch (IOException e) {
			Log.e("IOException in MeshLoader e=" + e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		if((pathname != null) && pathname.toLowerCase().endsWith(".stl") ){
			STL stl = new STL(pathname, stlBytes);
			obj = stl.loadfile(h);
			//obj.setAABB(stl.maxX,stl.maxY, stl.maxZ, stl.minX, stl.minY, stl.minZ);
		}
		return obj;
	}
	
	public static int SaveFile(String pathname,  D3Object obj,  Handler h) {
		STL stl = new STL(pathname);
		stl.savefile(obj, h);
		return 0;
	}
}
