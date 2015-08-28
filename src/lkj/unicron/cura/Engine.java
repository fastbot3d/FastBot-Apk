package lkj.unicron.cura;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.join.ogles.lib.Matrix4f;
import org.join.ogles.lib.Vector3f;

import lkj.unicron.util.Config;
import lkj.unicron.util.IOUtils;
import lkj.unicron.view.STLView;
import lkj.unicron.view.SceneView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Engine {
	final static String TAG = "Engine";
	String gccodeFileName = null;
	Context mContext;
	SceneView stlView;
	int mServerPort=0xc20d;
	ServerSocket mServerSocket;  //mServerSocket.close();
	Socket mClientSocket;
	Thread mListenThread = null;
	Thread mWorkThread = null;
	LogThread mLogThread = null;
	
	InputStream errInStream = null;
	
	final private int GUI_CMD_REQUEST_MESH = 0x1;	
	final private int GUI_CMD_SEND_POLYGONS = 0x2;	
	final private int GUI_CMD_FINISH_OBJECT = 0x03;	
	
	private Handler ProgressDialogHandler;
		
	public Engine(Context context, SceneView stlView, Handler handler, String fileName) {
		mContext = context;
		this.stlView = stlView;
		ProgressDialogHandler = handler;
		gccodeFileName ="/sdcard/STL_Unicron/"  + fileName + ".gcode";
/**/
		createThreadVar();
        Log.v(TAG, "1 :");
		while (true){
			if(mServerPort >=0xffff){
				Log.e(TAG, "server port >= 0XFFFF");
				break;
			}	
	        Log.v(TAG, "2 :");
			try {
				if (mServerSocket == null){
					mServerSocket = new ServerSocket();
				}
				mServerSocket.bind(new InetSocketAddress("127.0.0.1", mServerPort));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	            Log.e(TAG, "server bind IOException ", e);
	            mServerPort += 1;
	            continue;
			} 		
	        Log.v(TAG, "3 :");
			mListenThread.start();
			
			break;
		}
		Log.v(TAG, "4 :");
	}
	public void setNewViewData(SceneView stlView, String fileName){
		this.stlView = stlView;
		gccodeFileName ="/sdcard/STL_Unicron/"  + fileName + ".gcode";
	}
	
	private void createThreadVar(){
		mListenThread = new Thread("socket listen thread"){
			public void run() {
	            Log.v(TAG, "server listen on port:" + mServerPort);
	            
	           while(true){
					try {		
						/*try {
							if(mClientSocket != null){
								Log.v(TAG, "mClientSocket.close in mListenThread");
								mClientSocket.close();
							}
							mWorkThread.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.e(TAG, "mWorkThread.join", e);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							Log.e(TAG, "client socket close", e1);
						}
						*/
						Log.v(TAG, "5.0 :");
						mClientSocket = mServerSocket.accept();
						Log.v(TAG, "5.1 :");
						mWorkThread = new WorkThread();
						mWorkThread.start();
					} catch (IOException e) {
						e.printStackTrace();
			            Log.e(TAG, "server accept IOException on port:" + mServerPort);
			            break;
					} catch (Exception e) {
						e.printStackTrace();
			            Log.e(TAG, "server accept Exception on port:" + mServerPort);
			            break;
					}
				}
	            Log.v(TAG, "Listen Thread  exit");
			}
		};
		
		/*
		mWorkThread = new Thread("socket work thread"){
			public void run() {
				
			}
		};
		*/
		
		/*
		mLogThread = new Thread("socket log thread"){
			public void run() {
				
			}
		};
		*/
	}
	
	
	private class WorkThread extends Thread {
		
		public void run() {
            Log.v(TAG, "sock work thread ok");
    		byte[] buffer = new byte[1024];
    		int ret; 
    		int cmd=0;
    		
    		InputStream is = null;
    		OutputStream os = null;
			try {
				is = mClientSocket.getInputStream();
				os = mClientSocket.getOutputStream();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				Log.e(TAG, "mWorkThread, IOException " + e2);
			}
    		
    		
            while(true){
				Log.v(TAG, "mWorkThread, while");
            	try {	            		
					ret = is.read(buffer, 0, 4);
					if(ret < 0) {
						Log.e(TAG, "clinet buffer < 0");
						break;
					}
					cmd = (buffer[3] << 24) | (buffer[2] << 16) | (buffer[1] <<8) | buffer[0];
					Log.v(TAG, "clinet buffer:" + buffer + "cmd=" + cmd);
					switch(cmd){
						case GUI_CMD_REQUEST_MESH:
							float[] vertices = stlView.getVerticies();
							ByteBuffer packet = ByteBuffer.allocate(3 * (Float.SIZE / 8));
							packet.order(ByteOrder.LITTLE_ENDIAN); //LITTLE_ENDIAN
							int vCount = vertices.length / 3;
							byte dataLength[] = new byte[4];
							
							dataLength[0] =  (byte)((vCount) & 0xff);
							dataLength[1] =   (byte)((vCount >> 8) & 0xff);
							dataLength[2] = (byte)((vCount >> 16) & 0xff);
							dataLength[3] = (byte)((vCount >> 24) & 0xff);
							
							//send vertex count.
							//send vertex data.
							os.write(dataLength);
							Log.v(TAG, "vertex =" + vertices.length / 3);
							for(int i=0; i<vertices.length; i+=3){
								//Log.v(TAG, "i=" + i +  ",vertices[i] =" + vertices[i] + ",vertices[i+1] =" + vertices[i+1] +  ",vertices[i+2] =" + vertices[i+2]);
								packet.putFloat(vertices[i]);
								packet.putFloat(vertices[i+1]);
								packet.putFloat(vertices[i+2]);
								os.write(packet.array());
								
								packet.clear();
							}

							
							Log.v(TAG, "do with GUI_CMD_REQUEST_MESH");
							break;
						case GUI_CMD_SEND_POLYGONS:
							/*
							int polygons_size = 0;
							int layerNr = 0;
							int z = 0;
							int name_length = 0;
							String polygons_name  = null;
							
							ret = is.read(buffer, 0, 4);
							
							cmd = (buffer[3] << 24) | (buffer[2] << 16) | (buffer[1] <<8) | buffer[0];
							*/
							Log.v(TAG, "do with GUI_CMD_SEND_POLYGONS");
							break;
						case GUI_CMD_FINISH_OBJECT:
							Log.v(TAG, "do with GUI_CMD_FINISH_OBJECT");
							break;
						default:
							Log.e(TAG, "unkonwn cmd:" + cmd);
							break;
					};
            	} catch (SocketException e) {
					Log.e(TAG, "client socket getInputStream SocketException", e);
					break;
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "client socket getInputStream IOException", e);
					break;
				}
            }
            
            try {
				if(mClientSocket != null){
					Log.v(TAG, "mClientSocket.close in mWorkThread");
					mClientSocket.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Log.e(TAG, "client socket close", e1);
			}
            Log.v(TAG, "work thread exit");
		}
	  
	}
	
	private class LogThread extends Thread {
		
        @Override
        public void run() {
        	String str = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(errInStream));
			
            Log.v(TAG, "start socket log thread");
            
           while(true){
				try {
					str =in.readLine();
					if (str == null){
						Log.e(TAG, "read log err");
						break;
					}
					if(str.contains("Layer count:")){
						String[] parts = str.split(":");
						ProgressDialogHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_MAX, 3 * Integer.parseInt(parts[1].trim()), 0).sendToTarget();
						ProgressDialogHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SHOW).sendToTarget();
						ProgressDialogHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SETVALUE, 1, 0).sendToTarget();
						//Log.v(TAG, "layer count: " + parts[1]);
					} else if(str.contains("Progress:inset:")){
						String[] parts = str.split(":");
						ProgressDialogHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SETVALUE, Integer.parseInt(parts[2].trim()), 0).sendToTarget();
					} else if(str.contains("Progress:skin:")){
						String[] parts = str.split(":");
						ProgressDialogHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SETVALUE, Integer.parseInt(parts[3].trim()) + Integer.parseInt(parts[2].trim()), 0).sendToTarget();
					} else if (str.contains("Progress:export")){
						String[] parts = str.split(":");
						ProgressDialogHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_SETVALUE, 2 * Integer.parseInt(parts[3].trim()) + Integer.parseInt(parts[2].trim()), 0).sendToTarget();
						//Log.v(TAG, "progress : " + parts[2]);
					}
				} catch (IOException e) {
					e.printStackTrace();
		            Log.e(TAG, "socket log IOException:" + e);
		            break;
				} finally{
				}
	            Log.v(TAG,"-->:" + str );
			}
            ProgressDialogHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_DISMISS).sendToTarget();
            
  		    	try {
  		    		if (in != null)
  		    			in.close();
  		    		
                    if (errInStream != null){
                  	  errInStream.close();
                    }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            Log.v(TAG, "socket log thread exit");
		}
	}

	public void engineExit() {
		mContext = null;
		try {
			if (mServerSocket != null){
				mServerSocket.close();
			}			
			if(mClientSocket != null){
				Log.v(TAG, "mClientSocket.close in engineExit");
				mClientSocket.close();
			}				
			
			mListenThread.interrupt();
			mListenThread.join();
			mListenThread = null;
			
			if(mWorkThread!=null){
				mWorkThread.interrupt();
				mWorkThread.join();
			}
			mWorkThread= null;

			if(mLogThread!=null){
				mLogThread.interrupt();
				mLogThread.join();
			}
			mLogThread= null;
			
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	            Log.e(TAG, "engineExit IOException ", e);
		}  catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            Log.e(TAG, "engineExit InterruptedException ", e);
		}
	}
	
	//args extruderCount
	//      settings
	// posx, posy
	public void runEngine(String str){
	//	String commandList = null;
		HashMap<String, Integer> setting = null;
		if(IOUtils.curaEnginePath != null){
			
			setting = getSetting();
			String[] args = new String[setting.size() * 2 + 14];
			int i=0;
			
			args[0] = IOUtils.curaEnginePath;
			args[1] = "-vpg";
			args[2] = Integer.toString(mServerPort);
			
			i=3;
			for(Entry<String, Integer> set: setting.entrySet()){
				args[i] = "-s";
				args[i+1] = set.getKey() + "=" + set.getValue().toString();
				i += 2;
			}
			args[i] = "-s";
			args[++i] = "startCode=" + Profile.getAlterationFileContents(mContext, "start.gcode");
			args[++i] = "-s";
			args[++i] = "endCode=" + Profile.getAlterationFileContents(mContext, "end.gcode");
			

			Vector3f v1 = this.stlView.mObj.GetCenterPointer();
			Vector3f v = new Vector3f();
			v.set(v1);
			v.scale(1000);
	
			args[++i] = "-s";	
			args[++i] = "posx=" + (int)(v.x + 100 *1000);
			args[++i] = "-s";
			args[++i] = "posy=" + (int)(v.z + 100 *1000);
			
			Matrix4f rotate = new Matrix4f();	
			rotate.rotX((float)Math.PI/180 * (-90));
			args[++i] = "-m";
			args[++i] =  rotate.m00 + "," + rotate.m01 + "," +rotate.m02 + 
					"," +rotate.m10 + "," +rotate.m11 + "," +rotate.m12 + 
					"," +rotate.m20 + "," +rotate.m21 + "," +rotate.m22 ;
			
			args[++i] = "$"; // a '$' sign  stand for a 3D model.
		//	args[1] = "-v";
		//	args[2] = "-p";
		//	args[3] = "-g49677";
		//	args[4] = "$";
			/*		add	setttings 
			for(){
				commandList +=;
				//args2[2] = ;
			}*/
			Log.i(TAG, "runEngine args args[0]=" + args[0]);
			Log.i(TAG, "runEngine args args[1]=" + args[1]);
			Log.i(TAG, "runEngine args args[2]=" + args[2]);
			Log.i(TAG, "runEngine args i=" + i);
			for(String str1 : args) {
				Log.i(TAG, "str=" + str1);
			}
			
			Bundle bdle= new Bundle();
			bdle.putString("tile", "Generate Gcode");
		//	bdle.putString("message", gccodeFileName + "\n\nis generating, please wait....." );
			bdle.putString("message", "Printing....." );
			Message msg =ProgressDialogHandler.obtainMessage(IOUtils.MESSAGE_PROGRESS_NEW);
			msg.setData(bdle);
			msg.sendToTarget();
			Log.i(TAG, "runEngine 3 ret=" + exec(args));
		}
		
	}

	public void test3() {
		String[] args2 = new String[1];

		if(IOUtils.curaEnginePath != null){
			args2[0] = IOUtils.curaEnginePath;
			//args2[1] = "power";
			
			Log.i(TAG, "test 3 ret=" + exec(args2));
		}
		Log.i(TAG, "test 3 return");
		//String[] args2 = { "/system/bin/mkdir",  "/data/data/jp.kshoji.stlviewer/abc-3"};
        //Log.i(TAG, "test 4 ret=" + exec(args2));
         
 		//String args = "/system/bin/mkdir /data/data/jp.kshoji.stlviewer/abc \n"; 		 
        //   Log.i(TAG, "test 3 ret=" + execRootCmdSilent(args));
	}
	
	/*	*/
	public int getBinaryResFile(String assetName) {
		        // Copy the given asset out into a file so that it can be installed.
		        // Returns the path to the file.
		        byte[] buffer = new byte[8192];
		        InputStream is = null;
		        FileOutputStream fout = null;
		        
		        if(new File(IOUtils.curaEnginePath).exists()){
		        	Log.d(TAG, IOUtils.curaEnginePath + "is already exist.");
		        	return 1;
		        }
		        
		        try {
		            is = mContext.getAssets().open(assetName);
		            fout = new FileOutputStream(IOUtils.curaEnginePath);
		            int n = 0;
		            while ((n=is.read(buffer)) >= 0) {
		            	fout.write(buffer, 0, n);
		            }
		            
		            Log.d(TAG, "write save_path= " + IOUtils.curaEnginePath);
		            
		        } catch (IOException e) {
		            Log.e(TAG, "read write IOException ", e);
		        } catch (Exception e) {
		            Log.e(TAG, "read write Exception ", e);
		        } finally {
		        	try { 
		                if (is != null) {
		                	is.close();
		                }
		        	} catch (IOException e) {
			            Log.e(TAG, "is.close IOException ", e);
			        }
		        	try { 
		                if (fout != null) {
		                	fout.flush();		               
		                    fout.close();
		                }
		            } catch (IOException e) {
				            Log.e(TAG, "fout.close IOException ", e);
				    }
		        	
		        	try { 
				        File f = new File(IOUtils.curaEnginePath);
				        f.setExecutable(true);
		            } catch (Exception e) {
				            Log.e(TAG, "setExecutable Exception ", e);
				    }

		        }

		        
		         Log.i(TAG, "test2 return ");
		        return 2;		
      }

	  //执行linux命令但不关注结果输出
	public static int execRootCmdSilent(String paramString)
	  {
	    try {
	      Process localProcess = Runtime.getRuntime().exec("su");
	      OutputStream localObject = localProcess.getOutputStream();
	    //  DataOutputStream localDataOutputStream = new DataOutputStream( (OutputStream)localObject );
	      DataOutputStream localDataOutputStream = new DataOutputStream(localObject);
	      String str = String.valueOf(paramString);
	      localDataOutputStream.writeBytes(str);
	      localDataOutputStream.flush();
	      localDataOutputStream.writeBytes("exit\n");
	      localDataOutputStream.flush();
	      localProcess.waitFor();
	      int a = localProcess.exitValue();
	      return a;
	    } catch (Exception localException) {
	      localException.printStackTrace();
	    }
	      
	    return 1;
	  }
	 
	 //判断机器Android是否已经root，即是否获取root权限
	public static boolean haveRoot()
	  {
	    int i = execRootCmdSilent("echo test"); //通过执行测试命令来检测
	    if (i != -1)  return true;
	    return false;
	  }
	 

	
	  public String exec(String[] args) { 
          String result = ""; 
          ProcessBuilder processBuilder = new ProcessBuilder(args); 
          Process process = null; 
          FileOutputStream fout = null;
          InputStream errIs = null; 
          InputStream inIs = null; 
          byte buffer[] = new byte[1024];
          
          try { 
             // ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
              int read = -1; 

              
              try {
                  process = processBuilder.start(); 
                  } catch (Exception e) { 
                      Log.i(TAG, "Exception2 ret=" + e);
                      e.printStackTrace(); 
               } 
              
              errInStream = process.getErrorStream();
              
              try {
            	  if(mLogThread == null){ 
            		  mLogThread = new LogThread();
            		  mLogThread.start();
            	  } else {
                      Log.i(TAG, "mLogThread is alive !!!!!!!!!!!!!");
            	  }
              } catch (Exception e) { 
                  Log.i(TAG, "Exception3 ret=" + e);
                  e.printStackTrace(); 
              } 
              
              fout = new FileOutputStream(gccodeFileName);

              inIs = process.getInputStream(); 
              while ((read = inIs.read(buffer)) != -1) { 
               //   baos.write(read); 
                  fout.write(buffer, 0, read);
              } 
              

             // byte[] data = baos.toByteArray(); 
             // result = new String(data); 
          } catch (IOException e) { 
              Log.i(TAG, "exec IOException ret=" + e);
              e.printStackTrace(); 
          } catch (Exception e) { 
              Log.i(TAG, "exec Exception ret=" + e);
              e.printStackTrace(); 
          } finally { 
              try { 
                  if (errIs != null) { 
                      errIs.close(); 
                      Log.i(TAG, "exec close errIs ");
                  } 
                  if (inIs != null) { 
                      inIs.close(); 
                  } 
                  if (fout != null) {
                      fout.flush();
                	  fout.close(); 
                  } 
                  if (errInStream != null){
                	  errInStream.close();
                	  Log.i(TAG, "exec close errInStream ");
                  }
              } catch (IOException e) { 
                  e.printStackTrace(); 
              } 
              if (process != null) { 
                  process.destroy(); 
              } 
              try {
      			if(mLogThread!=null){
    				mLogThread.join();
    				mLogThread.interrupt();
				}
				mLogThread = null;
				
				if(mWorkThread!=null){
					mWorkThread.join();
					mWorkThread.interrupt();
				}
				mWorkThread= null;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          } 
          return result; 
      } 
	  
	  public HashMap<String, Integer> getSetting(){
		  int extruderCount = 1;
		  
		  HashMap<String, Integer> setting   = new HashMap<String,Integer>();
		  
		  /*for(String key : Profile.curaSettingItemName){
			  setting.put(key, 0);
		  }*/
		  
		  SharedPreferences config = mContext.getSharedPreferences("full_config", Activity.MODE_PRIVATE);
		  SharedPreferences machine_config = mContext.getSharedPreferences("machine_config", Activity.MODE_PRIVATE);

		  setting.put("layerThickness", (int) (Float.parseFloat(config.getString("layer_height", "0"))*1000));
		  setting.put("initialLayerThickness", (int) (Float.parseFloat(config.getString("bottom_thickness", "0")) > 0 
				  	? Float.parseFloat(config.getString("bottom_thickness", "0"))*1000 :  Float.parseFloat(config.getString("layer_height", "0"))*1000));
		  setting.put("filamentDiameter", (int) (Float.parseFloat(config.getString("filament_diameter", "0"))*1000));
		  setting.put("filamentFlow", (int) Float.parseFloat(config.getString("filament_flow", "0")));
		  
		  setting.put("extrusionWidth", (int)(Profile.calculateEdgeWidth(mContext) * 1000));
		  setting.put("insetCount",     (int)(Profile.calculateLineCount(mContext)));
		  setting.put("downSkinCount",  Float.parseFloat(config.getString("solid_bottom", "0")) > 0 
				  	? (int)(Profile.calculateSolidLayerCount(mContext)) :  0);
		  setting.put("upSkinCount",  Float.parseFloat(config.getString("solid_top", "0")) > 0 
				  	? (int)(Profile.calculateSolidLayerCount(mContext)) :  0);
		  setting.put("infillOverlap", (int) Float.parseFloat(config.getString("fill_overlap", "0")));
		  setting.put("initialSpeedupLayers", 4);
		  setting.put("initialLayerSpeed", (int) Float.parseFloat(config.getString("bottom_layer_speed", "0")));
		  setting.put("printSpeed", (int) Float.parseFloat(config.getString("print_speed", "0")));
		  
		  setting.put("infillSpeed",  (int) (Float.parseFloat(config.getString("infill_speed", "0")) > 0 
					? Float.parseFloat(config.getString("infill_speed", "0")) :  Float.parseFloat(config.getString("print_speed", "0"))));
		  setting.put("inset0Speed",  (int) (Float.parseFloat(config.getString("inset0_speed", "0")) > 0 
					? Float.parseFloat(config.getString("inset0_speed", "0")) :  Float.parseFloat(config.getString("print_speed", "0"))));
		  setting.put("insetXSpeed",  (int) (Float.parseFloat(config.getString("insetx_speed", "0")) > 0 
					? Float.parseFloat(config.getString("insetx_speed", "0")) :  Float.parseFloat(config.getString("print_speed", "0"))));
		  setting.put("moveSpeed", (int) Float.parseFloat(config.getString("travel_speed", "0")));
		  setting.put("fanSpeedMin", (int) (Float.parseFloat(config.getString("fan_enabled", "0")) > 0 
					? Float.parseFloat(config.getString("fan_speed", "0")) :  0));
		  setting.put("fanSpeedMax", (int) (Float.parseFloat(config.getString("fan_enabled", "0")) > 0 
					? Float.parseFloat(config.getString("fan_speed_max", "0")) :  0));
		  
		  setting.put("supportAngle", (int) (config.getString("support", "0").equals("None")
					? -1 : Float.parseFloat(config.getString("support_angle", "0"))) );
		  setting.put("supportEverywhere",  config.getString("support", "0").equals("Everywhere")
					? 1 : 0 );
		  setting.put("supportLineDistance",  (int) (Float.parseFloat(config.getString("support_fill_rate", "0")) > 0 
					? 100 * Profile.calculateEdgeWidth(mContext) * 1000 / Float.parseFloat(config.getString("support_fill_rate", "0")):  -1));
		  setting.put("supportXYDistance", (int) (1000 * Float.parseFloat(config.getString("support_xy_distance", "0"))));
		  setting.put("supportZDistance", (int) (1000 *Float.parseFloat( config.getString("support_z_distance", "0"))));
		  
		  setting.put("supportExtruder", config.getString("support_dual_extrusion", "0").equals("First extruder")
					? 0 : config.getString("support_dual_extrusion", "0").equals("Second extruder") && Profile.minimalExtruderCount(mContext) > 1 ? 1 : -1);
		  setting.put("retractionAmount",  (int) (Float.parseFloat(config.getString("retraction_enable", "0")) > 0 
					? Float.parseFloat(config.getString("retraction_amount", "0"))  * 1000 :  0));
		  
		  setting.put("retractionSpeed", (int) Float.parseFloat( config.getString("retraction_speed", "0")));
		  setting.put("retractionMinimalDistance", (int)(1000 *Float.parseFloat( config.getString("retraction_min_travel", "0"))));
		  setting.put("retractionAmountExtruderSwitch",  (int) (1000 *Float.parseFloat( config.getString("retraction_dual_amount", "0"))));
		  setting.put("retractionZHop",  (int) (1000 *Float.parseFloat( config.getString("retraction_hop", "0"))));
		  setting.put("minimalExtrusionBeforeRetraction", (int) (1000 *Float.parseFloat( config.getString("retraction_minimal_extrusion", "0"))));
		  setting.put("enableCombing",  Float.parseFloat(config.getString("retraction_combing", "0")) > 0 
					? 1 :  0);		  
		  setting.put("multiVolumeOverlap", (int) (1000 *Float.parseFloat( config.getString("overlap_dual", "0"))));
		  setting.put("objectSink", (int) (1000 *Float.parseFloat( config.getString("object_sink", "0"))));
		  setting.put("minimalLayerTime", (int) Float.parseFloat( config.getString("cool_min_layer_time", "0")));
		  setting.put("minimalFeedrate", (int) Float.parseFloat( config.getString("cool_min_feedrate", "0")));
		  setting.put("coolHeadLift",  Float.parseFloat(config.getString("cool_head_lift", "0")) > 0 
					? 1 :  0);
		  
	//	  setting.put("startCode", Integer.valueOf( config.getString("start.gcode", "0")));
	//	  setting.put("endCode", Integer.valueOf( config.getString("end.gcode", "0")));
		  setting.put("extruderOffset[1].X", (int) Float.parseFloat( machine_config.getString("extruder_offset_x1", "0")) * 1000);
		  setting.put("extruderOffset[1].Y", (int) Float.parseFloat( machine_config.getString("extruder_offset_y1", "0")) * 1000);
		  setting.put("extruderOffset[2].X", (int) Float.parseFloat( machine_config.getString("extruder_offset_x2", "0")) * 1000);
		  setting.put("extruderOffset[2].Y", (int) Float.parseFloat( machine_config.getString("extruder_offset_y2", "0")) * 1000);
		  setting.put("extruderOffset[3].X", (int) Float.parseFloat( machine_config.getString("extruder_offset_x3", "0")) * 1000);
		  setting.put("extruderOffset[3].Y", (int) Float.parseFloat( machine_config.getString("extruder_offset_y3", "0")) * 1000);
		  setting.put("fixHorrible", 0);
		  
		  int fanFullHeight =  (int) (Float.parseFloat(config.getString("fan_full_height", "0")) * 1000);
		  fanFullHeight = (fanFullHeight - setting.get("initialLayerThickness") - 1) / setting.get("layerThickness") + 1;
		  if(fanFullHeight<0)
			  fanFullHeight = 0;
		  setting.put("fanFullOnLayerNr", fanFullHeight);
		  
		  if(config.getString("support_type", "0").equals("Lines")){
			  setting.put("support_type", 1);
		  }
		  
		  if(config.getString("fill_density", "0").equals("0")){
			  setting.put("sparseInfillLineDistance", -1);
		  } else if(config.getString("support_type", "0").equals("100")){
			  setting.put("sparseInfillLineDistance", setting.get("extrusionWidth"));
			  setting.put("downSkinCount", 10000);
			  setting.put("upSkinCount", 10000);
		  } else {
			  int tmp = (int) (100 * Profile.calculateEdgeWidth(mContext) * 1000 / Float.parseFloat(config.getString("fill_density", "0")));
			  setting.put("sparseInfillLineDistance", tmp);
		  }
		  
		  if(config.getString("platform_adhesion", "0").equals("Brim")){
			  setting.put("skirtDistance", 0);
			  setting.put("skirtLineCount", setting.get("brim_line_count"));
		  } else if(config.getString("platform_adhesion", "0").equals("Raft")){
			  setting.put("skirtDistance", 0);
			  setting.put("skirtLineCount", 0);
			  
			  setting.put("raftMargin", setting.get("raft_margin") * 1000);
			  setting.put("raftLineSpacing", setting.get("raft_line_spacing") * 1000);
			  setting.put("raftBaseThickness", setting.get("raft_base_thickness") * 1000);
			  setting.put("raftBaseLinewidth", setting.get("raft_base_linewidth") * 1000);
			  setting.put("raftInterfaceThickness", setting.get("raft_interface_thickness") * 1000);
			  setting.put("raftInterfaceLinewidth", setting.get("raft_interface_linewidth") * 1000);
		  } else {
			  setting.put("skirtDistance",  (int) (Float.parseFloat( config.getString("skirt_gap", "0") )  * 1000) );
			  setting.put("skirtLineCount", (int) (Float.parseFloat( config.getString("skirt_line_count", "0") )));
			  setting.put("skirtMinLength", (int) (Float.parseFloat( config.getString("skirt_minimal_length", "0") )  * 1000));
		  }
		  int tmp =0;
		  if( Float.parseFloat(config.getString("fix_horrible_union_all_type_a", "0")) > 0){
			  tmp |=0x01;
		  }
		  if( Float.parseFloat(config.getString("fix_horrible_union_all_type_b", "0")) > 0){
			  tmp |=0x02;
		  }
		  if( Float.parseFloat(config.getString("fix_horrible_use_open_bits", "0")) > 0){
			  tmp |=0x10;
		  }
		  if( Float.parseFloat(config.getString("fix_horrible_extensive_stitching", "0")) > 0){
			  tmp |=0x04;
		  }
		  setting.put("fixHorrible", tmp);
		  
		  if(setting.get("layerThickness") <= 0){
			  setting.put("layerThickness", 1000);
		  }
		  if(machine_config.getString("gcode_flavor","0").equals("UltiGCode")){
			  setting.put("gcodeFlavor", 1);
		  } else if(machine_config.getString("gcode_flavor","0").equals("MakerBot")){
			  setting.put("gcodeFlavor", 2);
		  }
		  
		  if( Float.parseFloat(config.getString("spiralize", "0")) > 0){
			  setting.put("spiralizeMode", 1);
		  }
		  
		  if( Float.parseFloat(config.getString("wipe_tower", "0")) > 0 && (extruderCount > 1)){
			  tmp = (int) Math.sqrt(Float.parseFloat(config.getString("wipe_tower_volume", "0")) * 1000 * 1000 * 1000 / setting.get("layerThickness")) ;
			  setting.put("wipeTowerSize", tmp);
		  }
		  
		  if( Float.parseFloat(config.getString("ooze_shield", "0")) > 0){
			  setting.put("enableOozeShield", 1);
		  }
		  
		  return setting;
			/*	  = {
					
					'coolHeadLift': 1 if profile.getProfileSetting('cool_head_lift') == 'True' else 0,
					'startCode': profile.getAlterationFileContents('start.gcode', extruderCount),
					'endCode': profile.getAlterationFileContents('end.gcode', extruderCount),

				if profile.getProfileSetting('spiralize') == 'True':
					settings['spiralizeMode'] = 1
				if profile.getProfileSetting('wipe_tower') == 'True' and extruderCount > 1:
					settings['wipeTowerSize'] = int(math.sqrt(profile.getProfileSettingFloat('wipe_tower_volume') * 1000 * 1000 * 1000 / settings['layerThickness']))
				if profile.getProfileSetting('ooze_shield') == 'True':
					settings['enableOozeShield'] = 1
				return settings
		  */
	  }
		
		
}
