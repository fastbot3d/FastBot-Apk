package lkj.unicron.activity;

import java.io.File;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.join.ogles.lib.GLColor;
import org.join.ogles.lib.Matrix4f;
import org.join.ogles.lib.Vector3f;
import org.join.raypick.AppConfig;
import org.join.raypick.PickFactory;

import lkj.nuicorn.R;
import lkj.unicron.activity.FileListDialog.OnFileListDialogListener;
import lkj.unicron.cura.Engine;
import lkj.unicron.cura.Profile;
import lkj.unicron.object.D3Object;
import lkj.unicron.renderer.STLRenderer;
import lkj.unicron.util.Config;
import lkj.unicron.util.IOUtils;
import lkj.unicron.util.Log;
import lkj.unicron.util.M4;
import lkj.unicron.util.MeshLoader;
import lkj.unicron.view.Control_IncDec;
import lkj.unicron.view.LeftPannel;
import lkj.unicron.view.STLView;
import lkj.unicron.view.SceneView;
import lkj.unicron.view.ThreeStateButton;
import lkj.unicron.view.TouchButton;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Process;

/**
 * TODO "Reset view" → Button
 * 
 * 
 */
public class STLViewActivity extends Activity implements FileListDialog.OnFileListDialogListener {
	private STLView stlView;
	private SceneView mSceneView;
	private Config mConfig;

	private Profile mProfile, mProfileBackup;
	private Engine engine;
	private Context mContext;
	private Handler engineHandler = null;
	private volatile Looper mServiceLooper;
	private volatile ServiceHandler mServiceHandler;
	
	D3Object mObj;
	
	ToggleButton toggleButton = null; 	
	Button moveButton;
	Button mRotateBtn, mScaleBtn, mMoveBtn, mBackOrgBtn;
	Button mLeftBtn, mRightBtn, mFrontBtn, mBackBtn, mTopBtn;

	Button loadButton, gccodeButton, saveButton, preferencesButton;
	TouchButton smallScaleButton, largeScaleButton;
	
	HashMap<Integer,ThreeStateButton> mPannel = new HashMap<Integer, ThreeStateButton>();

	int mCur_Sel_Panel = IOUtils.LOOKPANNEL_ID;
	int mlast_Sel_Panel = 0;
	int mClick_num =0;
	
	Dialog mSaveDialog;
	
	public interface OnViewModeListener {
		public int onViewModeChane();
	}
	OnViewModeListener mViewModeListener = null;
	
	public void registerViewModeChange(OnViewModeListener l) {
		mViewModeListener = l;
	}
	
	 private final class ServiceHandler extends Handler {
		    ProgressDialog dialog = null;
		    
	        public ServiceHandler(Looper looper) {
	            super(looper);
	        }

	        @Override
	        public void handleMessage(Message msg) {
	        	String v_tag;
	        	Bundle bundle;
	        	
	        	//Log.v("handleMessage " + msg);
	            switch (msg.what) {
                case IOUtils.MESSAGE_PROGRESS_NEW:
                	bundle = msg.getData();
                    String title = bundle.getString("title");
                    String message = bundle.getString("message");
                    dialog = new ProgressDialog(mContext);
                    dialog.setTitle(title);
                    dialog.setMessage(message);
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.setIndeterminate(false);
                    dialog.setCancelable(false);
                    break;
                case IOUtils.MESSAGE_PROGRESS_MAX:
                    int max = msg.arg1;
    	        	//Log.v("handleMessage max=" + max);
                    dialog.setMax(max);	
                    break;
                    
                case IOUtils.MESSAGE_PROGRESS_SHOW:
                	dialog.setProgress(0);
                	dialog.show();	
                	break;
                    
                case IOUtils.MESSAGE_PROGRESS_DISMISS:
                	try {
                		//dialog.cancel();
                		dialog.dismiss();
                	} catch (Exception e){
                		Log.e("handleMessage dismiss Exception=" + e);
                	} finally {
                    	dialog = null;
                	}
                	break;
                	
                case IOUtils.MESSAGE_PROGRESS_SETVALUE:
                	//int v = msg.arg1;
    	        	//Log.v("handleMessage v=" + v);
                	dialog.setProgress(msg.arg1);
                	break;
                	
                case IOUtils.MESSAGE_SAVE_VERTEX:
                	mObj.saveVertexTmp();
    	        	//Log.v("handleMessage v=" + v);

                	break;
                case IOUtils.MESSAGE_RESTORE_VERTEX:
    	        	//Log.v("handleMessage v=" + v);
                	mObj.restoreVertexTmp();
                	break;
                case IOUtils.MESSAGE_3D_MOVE:
                	v_tag = (String) msg.obj;
                	if(v_tag.equals("move_left_right_inc")){
        				IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(1, 0, 0));
        			} else if (v_tag.equals("move_left_right_dec")){
        				IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(-1, 0, 0));
        			} else if (v_tag.equals("move_up_down_inc")){
        				IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0, 0, 1));
        			} else if (v_tag.equals("move_up_down_dec")){
        				IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0, 0, -1));
        			} else if (v_tag.equals("move_front_back_inc")){
        				IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0, 1, 0));
        			} else if (v_tag.equals("move_front_back_dec")){
        				IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0, -1, 0));
        			}
                	
    	        	//Log.v("handleMessage v_tag=" + v_tag);

                	break;
                case IOUtils.MESSAGE_3D_ROTATE:
                	bundle = msg.getData();
                    v_tag = bundle.getString("tag");
                    float x = bundle.getFloat("x");
                    float y = bundle.getFloat("y");
                    float z = bundle.getFloat("z");
                    
                	if(v_tag.equals("rotate_x_inc")){
        				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * 1, x, y, z, IOUtils.AxisX));
        			} else if (v_tag.equals("rotate_x_dec")){
        				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * (-1), x, y, z, IOUtils.AxisX));
        			} else if (v_tag.equals("rotate_y_inc")){
        				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * 1, x, y, z, IOUtils.AxisY));
        			} else if (v_tag.equals("rotate_y_dec")){
        				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * (-1), x, y, z, IOUtils.AxisY));
        			} else if (v_tag.equals("rotate_z_inc")){
        				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * (-1), x, y, z, IOUtils.AxisZ));
        			} else if (v_tag.equals("rotate_z_dec")){
        				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * 1, x, y, z, IOUtils.AxisZ));
        			}
                	break;
                case IOUtils.MESSAGE_3D_SCALE:
                	bundle = msg.getData();
                    v_tag = bundle.getString("tag");
                    Boolean isUniform = bundle.getBoolean("isUniform");
                    Boolean isPercentScale = bundle.getBoolean("isPercentScale");
                    float scale_offset = bundle.getFloat("scale_offset");
                    
                    if(isPercentScale){
        				IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(scale_offset, scale_offset, scale_offset));
        				break;
        			} 
                    
        			if(v_tag.equals("scale_length_inc")){
        				if(!isUniform){
        					IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(scale_offset, 1.0f, 1.0f));
        				}			
        			} else if (v_tag.equals("scale_length_dec")){
        				if(!isUniform){
        					IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(scale_offset, 1.0f, 1.0f));
        				}			
        			} else if (v_tag.equals("scale_width_inc")){
        				if(!isUniform){
        					IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(1.0f, scale_offset, 1.0f));
        				} 		
        			} else if (v_tag.equals("scale_width_dec")){
        				if(!isUniform){
        					IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(1.0f, scale_offset, 1.0f));
        				} 
        			} else if (v_tag.equals("scale_height_inc")){	
        				if(!isUniform){
        					IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(1.0f, 1.0f, scale_offset));
        				} 
        			} else if (v_tag.equals("scale_height_dec")){
        				if(!isUniform){
        					IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(1.0f, 1.0f, scale_offset));
        				} 
        			} 
        			
        			if (!v_tag.equals("scale_reset") && (!v_tag.equals("uniform_scale")) && isUniform ){
        				IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(scale_offset, scale_offset, scale_offset));
        			}       						
        			
                	break;                	
	        }
	      }
	  };
	  
		private String floatToStr(float f){		
			String str = Float.toString(f);
			int index = str.indexOf('.');
			return (String) str.subSequence(0, index+2);
		}
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		PackageManager manager = getPackageManager();
		ApplicationInfo appInfo = null;
		try {
			appInfo = manager.getApplicationInfo(getPackageName(), 0);
			Log.setDebug((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE);
		} catch (NameNotFoundException e) {
			Log.d(e);
		}

		Intent intent = getIntent();
		Uri uri = null;
		if (intent.getData() != null) {
			uri = getIntent().getData();
			Log.i("Uri:" + uri);
		}
		
	//	Log.i("onCreate floatToStr=" + floatToStr(1.333f));
		

/*		Log.i("onCreate, sin pi/3f = " + Math.sin(Math.PI/3));
		Log.i("onCreate, sin pi/6f = " + Math.sin(Math.PI/180 * 30));
		Log.i("onCreate, sin pi/3f = " + Math.sin(Math.PI/180 * 60));*/
		
		//setUpViews(uri);
		
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        
        
        HandlerThread engineThread = new HandlerThread("Engine threadloop",
                Process.THREAD_PRIORITY_URGENT_AUDIO);
        engineThread.start();
        engineHandler = new Handler(engineThread.getLooper());
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
		mProfile = new Profile(mContext,"full_config");
		mProfileBackup = new Profile(mContext,"full_config_backup");
		
		initLayout(uri);
		File file = new File("/sdcard/STL_Unicron/");
        if(!file.exists()){
        	file.mkdirs();
        }
	    
		mSceneView = new SceneView(mContext);
		
		mConfig = new Config(mContext);
		Config.save_basic_config_first(mContext);
		Config.save_basic_config_backup_first(mContext);
		Config.save_machine_config_first(mContext);
		Config.save_advance_print_option_other_first(mContext);
		
		FrameLayout relativeLayout = (FrameLayout) findViewById(R.id.stlFrameLayout);
		relativeLayout.removeView(mSceneView);
		relativeLayout.addView(mSceneView);
		// /sdcard/testModel.stl
		// /sdcard/ultimaker_platform.stl
		///sdcard/OpenSTL/Fox.stl

		
	//	SceneView glSurfaceView =
      //          (SceneView) findViewById(R.id.glsurfaceview);
      //  glSurfaceView.AddD3Object(mContext, mObj);
	//	Button btn = new Button(mContext);
	//	btn.setText("button1");
	//	relativeLayout.addView(btn);
		
	//	engine = new Engine(this);
	//	engine.getBinaryResFile("CuraEngine");
	//	engine.runEngine("abcd");
		//engine.test2(this,"ping");
		//engine.test3();
   
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mSceneView != null) {
			Log.i("onResume");
			mSceneView.onResume();
		//lkj	STLRenderer.requestRedraw();
			//lkj	stlView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mSceneView != null) {
			Log.i("onPause");
			mSceneView.onPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("onDestroy");
		if(engine != null)
			engine.engineExit();
		if(stlView != null)
			stlView.setVerticiesNull();
		stlView = null;
		Log.d("onDestroy 2");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (stlView != null) {
			Log.i("onRestoreInstanceState");
			Parcelable stlFileName = savedInstanceState.getParcelable("STLFileName");
			if (stlFileName != null) {
				//setUpViews((Uri) stlFileName);
			}
			boolean isRotate = savedInstanceState.getBoolean("isRotate");
			/*ToggleButton toggleButton = (ToggleButton) findViewById(R.id.rotateOrMoveToggleButton);
			if(toggleButton != null){
				toggleButton.setChecked(isRotate);
			}*/
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (stlView != null) {
			Log.i("onSaveInstanceState");
			outState.putParcelable("STLFileName", stlView.getUri());
			outState.putBoolean("isRotate", stlView.isRotate());
		}
	}
	
	final int PreferencesColorActivityResult = 0x100000;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch(requestCode) {
            case PreferencesColorActivityResult:
            	GLColor color = new GLColor();
            	color.r = data.getIntExtra("red", 0);
            	color.g = data.getIntExtra("green", 0);
            	color.b = data.getIntExtra("blue", 0);
            	color.a = data.getIntExtra("alpha", 0);
            	
            	boolean displayBox = data.getBooleanExtra("displayBox", true);
            	if(mObj != null){
            		mObj.setColor(color);
            		mObj.setLineBoxVisble(displayBox);
            	}
            	//Log.i("get activity result data=" + data +  "color=" + color);
                break;
        }

	}

	@Override
	public void onClickFileList(File file) {
		if (file == null) {
			return;
		}

		SharedPreferences config = getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		SharedPreferences.Editor configEditor = config.edit();
		configEditor.putString("lastPath", file.getParent());
		configEditor.commit();

		IOUtils.cur3DFileName = file.getPath().substring(file.getPath().lastIndexOf("/") + 1);
		IOUtils.curaEnginePath = "/data/data/" + mContext.getPackageName() + "/CuraEngine";
		

		mObj =null;
		mObj = MeshLoader.LoadFile(file.getPath().trim(), mContext, mServiceHandler);
		mObj.setColor(mConfig.getColor());
		mObj.setLineBoxVisble(mConfig.getLineBoxVisble(this));
		mObj.generateData();
		mSceneView.AddD3Object(mObj);
		//mSceneView.setObseverDirection(IOUtils.DIRECTION_RIGHT45);

		if(engine != null ){
			engine.setNewViewData(mSceneView, IOUtils.cur3DFileName);
		}
		if(toggleButton != null){
			toggleButton.setVisibility(View.VISIBLE);
		}
		saveButton.setVisibility(View.VISIBLE);
		gccodeButton.setVisibility(View.VISIBLE);
		mSceneView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (preferencesButton.getVisibility() == View.INVISIBLE) {
					;
				}
			}
		});
		
		old_x_size =mObj.GetLengthWidthHeight();
		Log.d("model length=" + old_x_size[0] + ",width=" + old_x_size[1] + ",height=" + old_x_size[2]);
		Vector3f v = mObj.GetCenterPointer();
		Log.d("model centerPointer=" +  v  + ",mMinY=" + mObj.mMinY  + ",mMaxY=" + mObj.mMaxY);
		IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0, -mObj.mMinY,0));
	}
	
	private void initLookPannel(){
		iv_front =  (Button) findViewById(R.id.homeBtn_viewPanel);
		iv_back =  (Button) findViewById(R.id.backBtn_viewPanel);
		iv_left =  (Button) findViewById(R.id.leftBtn_viewPanel);
		iv_right =  (Button) findViewById(R.id.rightBtn_viewPanel);
		iv_top =  (Button) findViewById(R.id.topBtn_viewPanel);
		
		
		iv_front.setText("HOME");
		iv_back.setText("Back");
		iv_left.setText("Left");
		iv_right.setText("Right");
		iv_top.setText("Top");

		//look pannel
		iv_front.setClickable(true);
		iv_front.setTag("listen_look_front");

		iv_back.setClickable(true);
		iv_back.setTag("listen_look_back");
		
		iv_left.setClickable(true);
		iv_left.setTag("listen_look_left");

		iv_right.setClickable(true);
		iv_right.setTag("listen_look_right");
		
		iv_top.setClickable(true);
		iv_top.setTag("listen_look_top");

		iv_front.setOnClickListener(listen_look);
		iv_back.setOnClickListener(listen_look);
		iv_left.setOnClickListener(listen_look);
		iv_right.setOnClickListener(listen_look);
		iv_top.setOnClickListener(listen_look);
	}
	
	private void initMovePannel(){
		move_left_right =  (Control_IncDec) findViewById(R.id.leftRight_movePanel);
		move_front_back =  (Control_IncDec) findViewById(R.id.frontBack_movePanel);
		move_up_down =  (Control_IncDec) findViewById(R.id.upDown_movePanel);
		
		move_reset = (Button) findViewById(R.id.resetBtn_movePanel);
		move_onPlatform =(Button) findViewById(R.id.onPlatformBtn_movePanel);
		move_center = (Button) findViewById(R.id.centerBtn_movePanel);
		
		move_left_right.setBtnIncTag("move_left_right_inc");
		move_left_right.setBtnDecTag("move_left_right_dec");
		move_left_right.addIncDecControl(R.drawable.ic_menu_archive,  "X", c_Dec_move_listen, c_Dec_move_listen);
		
		move_front_back.setBtnIncTag("move_front_back_inc");
		move_front_back.setBtnDecTag("move_front_back_dec");
		move_front_back.addIncDecControl(R.drawable.ic_menu_archive,  "Y", c_Dec_move_listen, c_Dec_move_listen);
		
		move_up_down.setBtnIncTag("move_up_down_inc");
		move_up_down.setBtnDecTag("move_up_down_dec");
		move_up_down.addIncDecControl(R.drawable.ic_menu_archive,  "Z", c_Dec_move_listen, c_Dec_move_listen);

		move_reset.setTag("move_reset");
		move_reset.setOnClickListener(c_Dec_move_listen);
		
		move_onPlatform.setTag("move_onPlatform");
		move_onPlatform.setOnClickListener(c_Dec_move_listen);

		move_center.setTag("move_center");
		move_center.setOnClickListener(c_Dec_move_listen);
	}
	
	private void initRotatePannel(){
		x_rotate =  (Control_IncDec) findViewById(R.id.x_rotatePanel);
		y_rotate =  (Control_IncDec) findViewById(R.id.y_rotatePanel);
		z_rotate =  (Control_IncDec) findViewById(R.id.z_rotatePanel);
		
		rotate_reset = (Button) findViewById(R.id.resetBtn_rotatePanel);
		
		x_dec90_rotate = (Button) findViewById(R.id.x_dec_90_rotatePanel);
		x_inc90_rotate	 = (Button) findViewById(R.id.x_inc_90_rotatePanel);
		
		y_dec90_rotate = (Button) findViewById(R.id.y_dec_90_rotatePanel);
		y_inc90_rotate	 = (Button) findViewById(R.id.y_inc_90_rotatePanel);
		
		z_dec90_rotate = (Button) findViewById(R.id.z_dec_90_rotatePanel);
		z_inc90_rotate	 = (Button) findViewById(R.id.z_inc_90_rotatePanel);
		
		 x_rotate.setBtnIncTag("rotate_x_inc");
		 x_rotate.setBtnDecTag("rotate_x_dec");

		 y_rotate.setBtnIncTag("rotate_y_inc");
		 y_rotate.setBtnDecTag("rotate_y_dec");
		 
		 z_rotate.setBtnIncTag("rotate_z_inc");
		 z_rotate.setBtnDecTag("rotate_z_dec");
		 
	
		x_rotate.addIncDecControl(R.drawable.ic_menu_archive, "X",
				c_Dec_rotate_listen, c_Dec_rotate_listen);
		y_rotate.addIncDecControl(R.drawable.ic_menu_archive, "Y",
				c_Dec_rotate_listen, c_Dec_rotate_listen);
		z_rotate.addIncDecControl(R.drawable.ic_menu_archive, "Z",
				c_Dec_rotate_listen, c_Dec_rotate_listen);

		rotate_reset.setTag("rotate_reset");
		rotate_reset.setOnClickListener(c_Dec_rotate_listen);
		
		x_dec90_rotate.setTag("x_dec90_rotate");
		x_dec90_rotate.setOnClickListener(c_Dec_rotate_listen);
		
		x_inc90_rotate.setTag("x_inc90_rotate");
		x_inc90_rotate.setOnClickListener(c_Dec_rotate_listen);
		
		y_dec90_rotate.setTag("y_dec90_rotate");
		y_dec90_rotate.setOnClickListener(c_Dec_rotate_listen);
		
		y_inc90_rotate.setTag("y_inc90_rotate");
		y_inc90_rotate.setOnClickListener(c_Dec_rotate_listen);
		
		z_dec90_rotate.setTag("z_dec90_rotate");
		z_dec90_rotate.setOnClickListener(c_Dec_rotate_listen);
		
		z_inc90_rotate.setTag("z_inc90_rotate");
		z_inc90_rotate.setOnClickListener(c_Dec_rotate_listen);
	}

	private void initScalePannel(){
		length_scale =  (Control_IncDec) findViewById(R.id.length_scalePanel);
		width_scale =  (Control_IncDec) findViewById(R.id.width_scalePanel);
		height_scale =  (Control_IncDec) findViewById(R.id.height_scalePanel);
		percent_scale =  (Control_IncDec) findViewById(R.id.percent_scalePanel);
		
		uniform_scale_CheckBox = (CheckBox) findViewById(R.id.uniformBtn_scalePanel);
		max_scale = (Button) findViewById(R.id.MaxBtn_scalePanel);
		scale_reset = (Button) findViewById(R.id.resetBtn_scalePanel);
		
		length_scale.addIncDecControl(R.drawable.ic_menu_archive,  "X", c_Dec_scale_listen, c_Dec_scale_listen);
		width_scale.addIncDecControl(R.drawable.ic_menu_archive,  "Y", c_Dec_scale_listen, c_Dec_scale_listen);
		height_scale.addIncDecControl(R.drawable.ic_menu_archive,  "Z", c_Dec_scale_listen, c_Dec_scale_listen);
		percent_scale.addIncDecControl(R.drawable.ic_menu_archive,  "%", c_Dec_scale_listen, c_Dec_scale_listen);
		
		length_scale.setBtnIncTag("scale_length_inc");
		length_scale.setBtnDecTag("scale_length_dec");

		width_scale.setBtnIncTag("scale_width_inc");
		width_scale.setBtnDecTag("scale_width_dec");
		
		height_scale.setBtnIncTag("scale_height_inc");
		height_scale.setBtnDecTag("scale_height_dec");
		
		percent_scale.setBtnIncTag("scale_percent_inc");
		percent_scale.setBtnDecTag("scale_percent_dec");
		percent_scale.setEditText("100%");
		
		uniform_scale_CheckBox.setTag("uniform_scale");
		uniform_scale_CheckBox.setOnClickListener(c_Dec_scale_listen);
		uniform_scale_CheckBox.setChecked(true);
		mUniform_scale = true;
		
		max_scale.setTag("max_scale");
		max_scale.setOnClickListener(c_Dec_scale_listen);
		
		scale_reset.setTag("scale_reset");
		scale_reset.setOnClickListener(c_Dec_scale_listen);
	}
	
	private void initLeftPannels() {
		
		 mLookPannel = (LinearLayout) findViewById(R.id.viewPanel);
		 mMovePannel = (LinearLayout) findViewById(R.id.movePanel);
		 mRotatePannel = (LinearLayout) findViewById(R.id.rotatePanel);
		 mScalePannel = (LinearLayout) findViewById(R.id.scalePanel);
		 
		 look_dispBtn =  (ThreeStateButton) findViewById(R.id.lookBtn);
		 move_dispBtn =  (ThreeStateButton) findViewById(R.id.moveBtn);
		 rotate_dispBtn =  (ThreeStateButton) findViewById(R.id.rotateBtn);
		 scale_dispBtn =  (ThreeStateButton) findViewById(R.id.scaleBtn);
				
		initLookPannel();
		initMovePannel();
		initRotatePannel();
		initScalePannel();
		

		mPannel.put(IOUtils.LOOKPANNEL_ID, look_dispBtn);
		mPannel.put(IOUtils.MOVEPANNEL_ID, move_dispBtn);
		mPannel.put(IOUtils.ROTATEPANNEL_ID, rotate_dispBtn);
		mPannel.put(IOUtils.SCALEPANNEL_ID, scale_dispBtn);
		
		look_dispBtn.setTag(IOUtils.LOOKPANNEL_ID);
		move_dispBtn.setTag(IOUtils.MOVEPANNEL_ID);
		rotate_dispBtn.setTag(IOUtils.ROTATEPANNEL_ID);
		scale_dispBtn.setTag(IOUtils.SCALEPANNEL_ID);
		
		look_dispBtn.setOnClickListener(pannel_listen);
		move_dispBtn.setOnClickListener(pannel_listen);
		rotate_dispBtn.setOnClickListener(pannel_listen);
		scale_dispBtn.setOnClickListener(pannel_listen);
		
		look_dispBtn.setButtonPanel(mLookPannel);
		move_dispBtn.setButtonPanel(mMovePannel);
		rotate_dispBtn.setButtonPanel(mRotatePannel);
		scale_dispBtn.setButtonPanel(mScalePannel);
		
		//look pannel is selected first; 
		mPannel.get(IOUtils.LOOKPANNEL_ID).setButtonState(LeftPannel.ONE_CLICK);
		mlast_Sel_Panel = mCur_Sel_Panel = IOUtils.LOOKPANNEL_ID;
		mClick_num = 1;
		
	}
	

	LinearLayout mSettingPannelLayer1;
	LinearLayout mSettingPannelLayer2;
	
	RadioGroup.OnCheckedChangeListener mPrintRadioGroupListener;
	int currentSelectRadio =0;
	
	private void initLayout(Uri uri) {
		setContentView(R.layout.stl);
		
		loadButton = (Button) findViewById(R.id.loadButton);
		loadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FileListDialog fileListDialog = new FileListDialog(STLViewActivity.this, false, "Choose STL file...", ".stl");
				fileListDialog.setOnFileListDialogListener(STLViewActivity.this);

				SharedPreferences config = getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
				
				String path = config.getString("lastPath", "/mnt/sdcard/");
				fileListDialog.show(new File(path).exists() ? path : "/mnt/sdcard/" );
			}
		});
		
		
		mSaveDialog = new Dialog(this);
		mSaveDialog.setContentView(R.layout.save_file_layout);
		mSaveDialog.setTitle("Save file...");
		mSaveDialog.setCancelable(false);
        
        Button cancel = (Button)mSaveDialog.findViewById(R.id.input_cancel_b);
        Button save = (Button)mSaveDialog.findViewById(R.id.input_create_b);
        save.setOnClickListener(new OnClickListener() {
                public void onClick (View v) { 
                	EditText rename_input = (EditText)mSaveDialog.findViewById(R.id.input_inputText);
    		        String name = "/sdcard/STL_Unicron/" +  rename_input.getText().toString();
    		        MeshLoader.SaveFile(name, mObj, mServiceHandler);
                    mSaveDialog.dismiss();
                    name.length()
                }
        });
        cancel.setOnClickListener(new OnClickListener() {
                public void onClick (View v) {  mSaveDialog.dismiss(); } 
        });
		
		
		saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mObj == null){
					return;
				}
			    EditText rename_input = (EditText)mSaveDialog.findViewById(R.id.input_inputText);
		        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd-hhmmss");
		       /* File file = new File("/sdcard/STL_Unicron/");
		        if(!file.exists()){
		        	file.mkdirs();
		        }*/
		        String name = format2.format(new Date()) + "-" + IOUtils.cur3DFileName ;
		        rename_input.setText(name);
		        Log.d("save file name=" + name);
		        mSaveDialog.show();
			}
		});
		
/*		smallScaleButton = (TouchButton) findViewById(R.id.smallScaleBtn);
		smallScaleButton.setOnLongTouchListener(2000);
		smallScaleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mObj != null && mSceneView != null) {
					mSceneView.setObseverDistance(false);
				}
			}
		});
		
	    largeScaleButton = (TouchButton) findViewById(R.id.largeScaleBtn);
	    largeScaleButton.setOnLongTouchListener(2000);
		largeScaleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mObj != null && mSceneView != null) {
					mSceneView.setObseverDistance(true);
				}
			}
		});		*/
		
		gccodeButton = (Button) findViewById(R.id.gccodeButton);
		gccodeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mObj == null){
					return;
				}
				Toast.makeText(mContext, "we are developing...",	Toast.LENGTH_LONG).show();
				/*if(engine == null ){
					engine = new Engine(mContext, mSceneView, mServiceHandler, IOUtils.cur3DFileName);
					engine.getBinaryResFile("CuraEngine");
				}
				
				engineHandler.post(new Runnable(){
					public void run(){
						engine.runEngine("abcd");
					}
				});*/
			}
		});
		
		
		//setting Pannel 
		mSettingPannelLayer1 = (LinearLayout) findViewById(R.id.settingPannelLayer1);
		mSettingPannelLayer2 = (LinearLayout) findViewById(R.id.settingPannelLayer2);

		final RadioGroup materialRadioGroup_Btn = (RadioGroup) mSettingPannelLayer1.findViewById(R.id.materialRadioGroup);
		if(Config.getMaterialSetting(this).equals("ABS")){
			materialRadioGroup_Btn.check(R.id.absMaterialRadio);
			mProfile.setItem("print_bed_temperature", Float.toString(100));
			mProfile.setItem("print_temperature", Float.toString(220));
		} else {
			materialRadioGroup_Btn.check(R.id.plaMaterialRadio);
			mProfile.setItem("print_bed_temperature", Float.toString(70));
        	mProfile.setItem("print_temperature", Float.toString(190));
		}
		materialRadioGroup_Btn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					switch (checkedId) {
						case  R.id.plaMaterialRadio: {	
							mProfile.setItem("print_bed_temperature", Float.toString(70));
        	            	mProfile.setItem("print_temperature", Float.toString(190));
        	            	Config.setMaterialSetting(mContext, "PLA");
							break;
							}	
						case  R.id.absMaterialRadio: {
							mProfile.setItem("print_bed_temperature", Float.toString(100));
        	            	mProfile.setItem("print_temperature", Float.toString(220));
        	            	Config.setMaterialSetting(mContext, "ABS");
							break;
							}
					}		
					Config.save_full_config(mContext, mProfile);
                    Log.d("materialRadioGroup_Btn");
				}
        });
		
		
	    CheckBox displayBox_checkBox  = (CheckBox) mSettingPannelLayer1.findViewById(R.id.displayBox_checkBox);
	    displayBox_checkBox.setChecked(Config.getLineBoxVisble(this));
	    displayBox_checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				Config.setLineBoxVisble(mContext, isChecked);
				if(mObj != null){
					mObj.setLineBoxVisble(isChecked);
				}
			}
	    });
	    
	    
	    final RadioGroup printRadioGroup_Btn = (RadioGroup) mSettingPannelLayer1.findViewById(R.id.printSettingRadioGroup);
	    if(Config.getQuickSetting(this).equals("Fast low quality print")){
	    	printRadioGroup_Btn.check(R.id.lowQuaPrintSettingRadio);
	    } else if(Config.getQuickSetting(this).equals("Normal quality print")){
	    	printRadioGroup_Btn.check(R.id.normalQuaPrintSettingRadio);
	    } else if(Config.getQuickSetting(this).equals("Hight quality print")){
	    	printRadioGroup_Btn.check(R.id.hightQuaPrintSettingRadio);
		} else if(Config.getQuickSetting(this).equals("option")){
			printRadioGroup_Btn.check(R.id.optionQuaPrintSettingRadio);
		}
	    
	    currentSelectRadio = printRadioGroup_Btn.getCheckedRadioButtonId();
	    
	 
	    mPrintRadioGroupListener = new RadioGroup.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					
            	    float nozzle_size =  Float.parseFloat( (String) mProfile.getItem("nozzle_size"));            	    
					// TODO Auto-generated method stub
					switch (checkedId) {
						case  R.id.lowQuaPrintSettingRadio: {
							Config.setQuickSetting(mContext, Config.indexToPrintType(2));
							mProfile.setItem("wall_thickness", Float.toString(nozzle_size * 2.5f));
        	            	mProfile.setItem("layer_height", Float.toString(0.2f));
        	            	mProfile.setItem("fill_density", Float.toString(10));
        	            	mProfile.setItem("print_speed", Float.toString(60));
        	            	mProfile.setItem("cool_min_layer_time", Float.toString(3));
        	            	mProfile.setItem("bottom_layer_speed", Float.toString(30));
        	            	Config.save_full_config(mContext, mProfile);
        	            	currentSelectRadio = R.id.lowQuaPrintSettingRadio;
							break;
							}	
						case  R.id.normalQuaPrintSettingRadio: {
							Config.setQuickSetting(mContext, Config.indexToPrintType(1));
							mProfile.setItem("wall_thickness", Float.toString(nozzle_size * 2.0f));
        	            	mProfile.setItem("layer_height", Float.toString(0.10f));
        	            	mProfile.setItem("fill_density", Float.toString(20));
        	            	mProfile.setItem("print_speed", Float.toString(50));
        	            	mProfile.setItem("cool_min_layer_time", Float.toString(5));
        	            	mProfile.setItem("bottom_layer_speed", Float.toString(15));
        	            	Config.save_full_config(mContext, mProfile);
        	            	currentSelectRadio = R.id.normalQuaPrintSettingRadio;
							break;
							}
						case  R.id.hightQuaPrintSettingRadio: {
							Config.setQuickSetting(mContext, Config.indexToPrintType(0));
							mProfile.setItem("wall_thickness", Float.toString(nozzle_size * 2.0f));
        	            	mProfile.setItem("layer_height",Float.toString(0.06f));
        	            	mProfile.setItem("fill_density", Float.toString(20));
        	            	mProfile.setItem("print_speed", Float.toString(50));
        	            	mProfile.setItem("cool_min_layer_time", Float.toString(5));
        	            	mProfile.setItem("bottom_layer_speed", Float.toString(15));
        	            	Config.save_full_config(mContext, mProfile);
        	            	currentSelectRadio = R.id.hightQuaPrintSettingRadio;
							break;
							}
						case  R.id.optionQuaPrintSettingRadio: {
							
							mSettingPannelLayer1.setVisibility(View.GONE );
							mSettingPannelLayer2.setVisibility(View.VISIBLE);
							
							LinearLayout tagLinearLayout = (LinearLayout) mSettingPannelLayer2.findViewById(R.id.linearLayoutTagBasic);
							int childNum= tagLinearLayout.getChildCount();
							for(int i=0; i<childNum; i++){
								View child = tagLinearLayout.getChildAt(i);
								String tag = (String) child.getTag();
								if(tag != null){
									if(tag.equals("wall_thickness") || tag.equals("layer_height") || tag.equals("fill_density")
											|| tag.equals("print_speed")){
										((EditText) child).setText(Config.get_advance_print_option_other(mContext, tag));
										continue;
									} 
									
									if (child instanceof EditText){
										((EditText) child).setText((String) mProfile.getItem(tag));
										Log.d("EditText val=" + mProfile.getItem(tag));
									} else if (child instanceof CheckBox){
										Boolean b=false;
										Log.d("CheckBox val=" + mProfile.getItem(tag));
										if(mProfile.getItem(tag).toString().trim().equals("1"))
											b = true;
										((CheckBox) child).setChecked(b);
									} else if (child instanceof Spinner){
										int pos = ((Spinner) child).getSelectedItemPosition();
										((Spinner) child).setSelection(Config.TypeToSupportindex((String) mProfile.getItem(tag)));
										Log.d("Spinner pos=" + Config.TypeToSupportindex((String) mProfile.getItem(tag))); 
									}
								}
							}
							tagLinearLayout = (LinearLayout) mSettingPannelLayer2.findViewById(R.id.linearLayoutTagAdvance);
							childNum= tagLinearLayout.getChildCount();
							for(int i=0; i<childNum; i++){
								View child = tagLinearLayout.getChildAt(i);
								String tag = (String) child.getTag();
								if(tag != null){
									if(tag.equals("cool_min_layer_time") || tag.equals("bottom_layer_speed")){
										((EditText) child).setText(Config.get_advance_print_option_other(mContext, tag));
										continue;
									} 
									
									if (child instanceof EditText){
										((EditText) child).setText((String) mProfile.getItem(tag));
										Log.d("EditText val=" + mProfile.getItem(tag));
									} else if (child instanceof CheckBox){
										Boolean b=false;
										Log.d("CheckBox val=" + mProfile.getItem(tag));
										if(mProfile.getItem(tag).toString().trim().equals("1"))
											b = true;
										((CheckBox) child).setChecked(b);
									} else if (child instanceof Spinner){
										int pos = ((Spinner) child).getSelectedItemPosition();
										((Spinner) child).setSelection(Config.TypeToSupportindex((String) mProfile.getItem(tag)));
										Log.d("Spinner pos=" + Config.TypeToSupportindex((String) mProfile.getItem(tag))); 
									}
								}
							}
							
							break;
							}
					}		
					
                    Log.d("printRadioGroup_Btn");
				}
        };
        printRadioGroup_Btn.setOnCheckedChangeListener(mPrintRadioGroupListener);
		
/*		final Spinner quickPrintSettingTypeSpinner = (Spinner) mSettingPannelLayer1.findViewById(R.id.quickPrintSettingTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.print_quality_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quickPrintSettingTypeSpinner.setAdapter(adapter);
     //   quickPrintSettingTypeSpinner.setSelection(Config.TypeToSupportindex(Config.getQuickSetting(mContext)));
        quickPrintSettingTypeSpinner.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                    	    
                    	    switch (position) {
	            	            case 0:  //"Hight quality print";
	            	            	
	            	            	break;
	            	            	
	            	            case 1: //"Normal quality print";
	            	            	
	            	            	break;
	            	            	
	            	            case 2: //"Fast low quality print";
	            	            	
	            	            	break;
            	            }
                            Log.d("quickPrintSettingTypeSpinner: position=" + position + " id=" + id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    	Log.d("quickPrintSettingTypeSpinner: unselected");
                    }
                });

        
		final RadioGroup printSetting_radioGroup = (RadioGroup)mSettingPannelLayer1.findViewById(R.id.printSettingRadioGroup);
		if(Config.getIsQuickSetting(this))
			printSetting_radioGroup.check(R.id.quickPrintSettingRadio);
		else {
			printSetting_radioGroup.check(R.id.fullPrintSettingRadio);
			quickPrintSettingTypeSpinner.setClickable(false);
		}
		*/
		
		
		final Button fullPrintSettingRadio_Btn = (Button) mSettingPannelLayer1.findViewById(R.id.fullPrintSettingRadio_Btn);
/*		printSetting_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					switch (checkedId) {
						case  R.id.quickPrintSettingRadio: {
								//quickPrintSettingTypeSpinner.setActivated(true);
								quickPrintSettingTypeSpinner.setClickable(true);
								fullPrintSettingRadio_Btn.setEnabled(false);
								//Config.setIsQuickSetting(mContext, true);
								
								//quickPrintSettingTypeSpinner.setSelection(Config.TypeToSupportindex(Config.getQuickSetting(mContext)));
								
								Log.d("low quality");
								break;
							}
						case  R.id.fullPrintSettingRadio:  {
							fullPrintSettingRadio_Btn.setEnabled(true);
							fullPrintSettingRadio_Btn.callOnClick();
							    quickPrintSettingTypeSpinner.setClickable(false);
							    Config.setQuickSetting(mContext, false);
								mSettingPannelLayer1.setVisibility(mSettingPannelLayer1.isShown() ? View.GONE : View.VISIBLE);
								mSettingPannelLayer2.setVisibility(mSettingPannelLayer2.isShown() ? View.GONE : View.VISIBLE);
								Log.d("full quality");
								
								
								LinearLayout tagLinearLayout = (LinearLayout) mSettingPannelLayer2.findViewById(R.id.linearLayoutTagBasic);
								int childNum= tagLinearLayout.getChildCount();
								for(int i=0; i<childNum; i++){
									View child = tagLinearLayout.getChildAt(i);
									String tag = (String) child.getTag();
									if(tag != null){
										if (child instanceof EditText){
											((EditText) child).setText((String) mProfile.getItem(tag));
											Log.d("EditText val=" + mProfile.getItem(tag));
										} else if (child instanceof CheckBox){
											Boolean b=false;
											Log.d("CheckBox val=" + mProfile.getItem(tag));
											if(mProfile.getItem(tag).toString().trim().equals("1"))
												b = true;
											((CheckBox) child).setChecked(b);
										} else if (child instanceof Spinner){
											int pos = ((Spinner) child).getSelectedItemPosition();
											((Spinner) child).setSelection(Config.TypeToSupportindex((String) mProfile.getItem(tag)));
											Log.d("Spinner pos=" + Config.TypeToSupportindex((String) mProfile.getItem(tag))); 
										}
									}
								}
								tagLinearLayout = (LinearLayout) mSettingPannelLayer2.findViewById(R.id.linearLayoutTagAdvance);
								childNum= tagLinearLayout.getChildCount();
								for(int i=0; i<childNum; i++){
									View child = tagLinearLayout.getChildAt(i);
									String tag = (String) child.getTag();
									if(tag != null){
										if (child instanceof EditText){
											((EditText) child).setText((String) mProfile.getItem(tag));
											Log.d("EditText val=" + mProfile.getItem(tag));
										} else if (child instanceof CheckBox){
											Boolean b=false;
											Log.d("CheckBox val=" + mProfile.getItem(tag));
											if(mProfile.getItem(tag).toString().trim().equals("1"))
												b = true;
											((CheckBox) child).setChecked(b);
										} else if (child instanceof Spinner){
											int pos = ((Spinner) child).getSelectedItemPosition();
											((Spinner) child).setSelection(Config.TypeToSupportindex((String) mProfile.getItem(tag)));
											Log.d("Spinner pos=" + Config.TypeToSupportindex((String) mProfile.getItem(tag))); 
										}
									}
								}
								break;
							}
					}
				}
        });

		*/
        
		Spinner s1 = (Spinner) mSettingPannelLayer2.findViewById(R.id.spinner_SupportType);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this, R.array.support_type, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter1);
        s1.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        Log.d("support_type Spinner1: position=" + position + " id=" + id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    	Log.d("support_type Spinner1: unselected");
                    }
                });

		Spinner s2 = (Spinner) mSettingPannelLayer2.findViewById(R.id.spinner_AdhesionType);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.platform_adhesion_type, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(adapter2);
        s2.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        Log.d("platform_adhesion_type Spinner2: position=" + position + " id=" + id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    	Log.d("platform_adhesion_type Spinner2: unselected");
                    }
                });	

		//mProfileBackup
		Button restore_full_print_Btn = (Button) mSettingPannelLayer2.findViewById(R.id.restore_setting_Btn);
		restore_full_print_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinearLayout tagLinearLayout = (LinearLayout) mSettingPannelLayer2.findViewById(R.id.linearLayoutTagBasic);
				int childNum= tagLinearLayout.getChildCount();
				for(int i=0; i<childNum; i++){
					View child = tagLinearLayout.getChildAt(i);
					String tag = (String) child.getTag();
					if(tag != null){
						if (child instanceof EditText){
							((EditText) child).setText((String) mProfileBackup.getItem(tag));
							Log.d("EditText val=" + mProfileBackup.getItem(tag));
						} else if (child instanceof CheckBox){
							Boolean b=false;
							Log.d("CheckBox val=" + mProfileBackup.getItem(tag));
							if(mProfileBackup.getItem(tag).toString().trim().equals("1"))
								b = true;
							((CheckBox) child).setChecked(b);
						} else if (child instanceof Spinner){
							int pos = ((Spinner) child).getSelectedItemPosition();
							((Spinner) child).setSelection(Config.TypeToSupportindex((String) mProfileBackup.getItem(tag)));
							Log.d("Spinner pos=" + Config.TypeToSupportindex((String) mProfileBackup.getItem(tag))); 
						}
					}
				}
				tagLinearLayout = (LinearLayout) mSettingPannelLayer2.findViewById(R.id.linearLayoutTagAdvance);
				childNum= tagLinearLayout.getChildCount();
				for(int i=0; i<childNum; i++){
					View child = tagLinearLayout.getChildAt(i);
					String tag = (String) child.getTag();
					if(tag != null){
						if (child instanceof EditText){
							((EditText) child).setText((String) mProfileBackup.getItem(tag));
							Log.d("EditText val=" + mProfileBackup.getItem(tag));
						} else if (child instanceof CheckBox){
							Boolean b=false;
							Log.d("CheckBox val=" + mProfileBackup.getItem(tag));
							if(mProfileBackup.getItem(tag).toString().trim().equals("1"))
								b = true;
							((CheckBox) child).setChecked(b);
						} else if (child instanceof Spinner){
							int pos = ((Spinner) child).getSelectedItemPosition();
							((Spinner) child).setSelection(Config.TypeToSupportindex((String) mProfileBackup.getItem(tag)));
							Log.d("Spinner pos=" + Config.TypeToSupportindex((String) mProfileBackup.getItem(tag))); 
						}
					}
				}
			}
		});
		
		Button cancel_full_print_Btn = (Button) mSettingPannelLayer2.findViewById(R.id.cancel_setting_Btn);
		cancel_full_print_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSettingPannelLayer1.setVisibility(View.VISIBLE);
				mSettingPannelLayer2.setVisibility(View.GONE);
				printRadioGroup_Btn.setOnCheckedChangeListener(null);
				printRadioGroup_Btn.check(currentSelectRadio);
				printRadioGroup_Btn.setOnCheckedChangeListener(mPrintRadioGroupListener);
				
			}
		});
		
		final Button more_print_Btn = (Button) mSettingPannelLayer2.findViewById(R.id.more_setting_Btn);
		more_print_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				mSettingPannelLayer1.setVisibility(View.GONE);
				mSettingPannelLayer2.setVisibility(View.VISIBLE);
				LinearLayout print_basic_setting = (LinearLayout) mSettingPannelLayer2.findViewById(R.id.basic_printSetting);
				LinearLayout print_more_setting = (LinearLayout) mSettingPannelLayer2.findViewById(R.id.more_printSetting);
				
				if(more_print_Btn.getText().equals("More >>")){			
					more_print_Btn.setText("Basic >>");
					print_basic_setting.setVisibility(View.GONE);
					print_more_setting.setVisibility(View.VISIBLE);
				} else {				
					more_print_Btn.setText("More >>");
					print_basic_setting.setVisibility(View.VISIBLE);
					print_more_setting.setVisibility(View.GONE);					
				}				
			}
		});
		
		Button ok_full_print_Btn = (Button) mSettingPannelLayer2.findViewById(R.id.ok_setting_Btn);
		ok_full_print_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinearLayout tagLinearLayoutBasic = (LinearLayout) mSettingPannelLayer2.findViewById(R.id.linearLayoutTagBasic);
				int childNum= tagLinearLayoutBasic.getChildCount();
				for(int i=0; i<childNum; i++){
					View child = tagLinearLayoutBasic.getChildAt(i);
					String tag = (String) child.getTag();
					if(tag != null){
						if(tag.equals("wall_thickness") || tag.equals("layer_height") 
								|| tag.equals("fill_density") || tag.equals("print_speed")){
							Config.save_advance_print_option_other(mContext, tag, ((EditText) child).getText().toString());
						}
						
						if (child instanceof EditText){
							String val = ((EditText) child).getText().toString();
							mProfile.setItem(tag, val);
							Log.d("tag=" + tag + ",val=" + val); 
						} else if (child instanceof CheckBox){
							Boolean val = ((CheckBox) child).isChecked();
							mProfile.setItem(tag, val ? 1 : 0);
							Log.d("tag=" + tag + ",val=" + val); 
						} else if (child instanceof Spinner){
							int pos = ((Spinner) child).getSelectedItemPosition();
							mProfile.setItem(tag, Config.indexToSupportType(pos));
							Log.d("tag=" + tag + ",val=" + Config.indexToSupportType(pos)); 
						}
					}
				}
				LinearLayout tagLinearLayoutAdvance = (LinearLayout) mSettingPannelLayer2.findViewById(R.id.linearLayoutTagAdvance);
				childNum= tagLinearLayoutAdvance.getChildCount();
				for(int i=0; i<childNum; i++){
					View child = tagLinearLayoutAdvance.getChildAt(i);
					String tag = (String) child.getTag();
					if(tag != null){
						if(tag.equals("cool_min_layer_time") || tag.equals("bottom_layer_speed")){
							Config.save_advance_print_option_other(mContext, tag, ((EditText) child).getText().toString());
						}
						
						if (child instanceof EditText){
							String val = ((EditText) child).getText().toString();
							mProfile.setItem(tag, val);
							Log.d("tag=" + tag + ",val=" + val); 
						} else if (child instanceof CheckBox){
							Boolean val = ((CheckBox) child).isChecked();
							mProfile.setItem(tag, val ? 1 : 0);
							Log.d("tag=" + tag + ",val=" + val); 
						} else if (child instanceof Spinner){
							int pos = ((Spinner) child).getSelectedItemPosition();
							mProfile.setItem(tag, Config.indexToSupportType(pos));
							Log.d("tag=" + tag + ",val=" + Config.indexToSupportType(pos)); 
						}
					}
				}
				Config.setQuickSetting(mContext, "option");
				
				printRadioGroup_Btn.setOnCheckedChangeListener(null);
				printRadioGroup_Btn.check(R.id.optionQuaPrintSettingRadio);
				printRadioGroup_Btn.setOnCheckedChangeListener(mPrintRadioGroupListener);
				
				Config.save_full_config(mContext, mProfile);
				mSettingPannelLayer1.setVisibility(View.VISIBLE);
				mSettingPannelLayer2.setVisibility(View.GONE);
			}
		});
		
		preferencesButton = (Button) findViewById(R.id.preferncesButton);
		preferencesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
/*				Intent intent = new Intent(STLViewActivity.this, PreferencesActivity.class);
				// startActivity(intent);
				startActivityForResult(intent, PreferencesColorActivityResult);*/
				
				if(mLookPannel.isShown() || mMovePannel.isShown() || mRotatePannel.isShown() || mScalePannel.isShown()){
					return;
				}
				
				mSettingPannelLayer1.setVisibility(mSettingPannelLayer1.isShown() ? View.GONE : View.VISIBLE);

				if(mSettingPannelLayer2.isShown()){
					mSettingPannelLayer2.setVisibility(View.GONE);
				}
			}
		});
		
		initLeftPannels();
		
	}

	LinearLayout mLookPannel;
	LinearLayout mMovePannel;
	LinearLayout mRotatePannel;
	LinearLayout mScalePannel;
	
	ThreeStateButton look_dispBtn, move_dispBtn, rotate_dispBtn, scale_dispBtn;
	 
	Button iv_front, iv_back, iv_left, iv_top, iv_right45, iv_right;
	Button iv_rightPannel_look_btn, iv_rightPannel_move_btn, iv_rightPannel_rotate_btn, iv_rightPannel_scale_btn;
	
	Control_IncDec move_left_right, move_front_back, move_up_down;
	Button move_reset,move_onPlatform, move_center;

	Control_IncDec x_rotate, y_rotate, z_rotate;
	Button x_inc90_rotate, x_dec90_rotate, y_inc90_rotate, y_dec90_rotate, z_inc90_rotate, z_dec90_rotate;
	Button rotate_reset;

	Control_IncDec length_scale, width_scale, height_scale, percent_scale;
	CheckBox uniform_scale_CheckBox;
	Button scale_reset, max_scale;
	Boolean mUniform_scale = false;
		
	//look pannel
	View.OnClickListener listen_look = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String v_tag = (String) v.getTag();
			/*if(v_tag.equals(iv_right45.getTag())){
				mSceneView.setObseverDirection(IOUtils.DIRECTION_RIGHT45);
			} else*/
				
			if(v_tag.equals(iv_front.getTag())){
				mSceneView.setObseverDirection(IOUtils.DIRECTION_FRONT);
			} else if (v_tag.equals(iv_back.getTag())){
				mSceneView.setObseverDirection(IOUtils.DIRECTION_BACK);
			} else if (v_tag.equals(iv_left.getTag())){
				mSceneView.setObseverDirection(IOUtils.DIRECTION_LEFT);
			} else if (v_tag.equals(iv_right.getTag())){
				mSceneView.setObseverDirection(IOUtils.DIRECTION_RIGHT);
			} else if (v_tag.equals(iv_top.getTag())){
				mSceneView.setObseverDirection(IOUtils.DIRECTION_TOP);	
			}
			Log.i("click v tag ="  + v_tag);
		}
	};
	
	int move_left_right_offset=0, move_front_back_offset=0, move_up_down_offset=0;
	//move pannel 
	View.OnClickListener c_Dec_move_listen = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String v_tag = (String) v.getTag();
			mServiceHandler.obtainMessage(IOUtils.MESSAGE_3D_MOVE, v_tag).sendToTarget();
			if(v_tag.equals("move_left_right_inc")){
				move_left_right_offset += 1;
				//IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(1, 0, 0));
			} else if (v_tag.equals("move_left_right_dec")){
				move_left_right_offset -= 1;
				//IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(-1, 0, 0));
			} else if (v_tag.equals("move_front_back_inc")){
				move_front_back_offset +=1;
				//IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0, 0, 1));
			} else if (v_tag.equals("move_front_back_dec")){
				move_front_back_offset -=1;
				//IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0, 0, -1));
			} else if (v_tag.equals("move_up_down_inc")){
				move_up_down_offset += 1;
				//IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0, 1, 0));
			} else if (v_tag.equals("move_up_down_dec")){
				move_up_down_offset -= 1;
				//IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0, -1, 0));
			} else if (v_tag.equals("move_reset")){
				mServiceHandler.obtainMessage(IOUtils.MESSAGE_RESTORE_VERTEX).sendToTarget();
				//mObj.restoreVertexTmp();
				//IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(-move_left_right_offset, -move_up_down_offset, -move_front_back_offset));
				move_left_right_offset = 0;
				move_front_back_offset = 0;
				move_up_down_offset = 0;
			} else if (v_tag.equals("move_onPlatform")){
				Log.d("model move_onPlatform mMinY=" + mObj.mMinY);
				IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0,  -mObj.mMinY,0));
				move_up_down_offset = 0;
			} else if (v_tag.equals("move_center")){
				Vector3f v_c = mObj.GetCenterPointer();
				Log.d("model centerPointer=" +  v_c);
				//Log.d("0 model move_onPlatform mMinY=" + mObj.mMinY);
				//IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(-v_c.x, -v_c.y,-v_c.z));
				//Log.d("1 model move_onPlatform mMinY=" + mObj.mMinY);
				
				float new_minY= Math.abs(v_c.y - mObj.mMinY);
				//IOUtils.ApplyMat(mObj, IOUtils.getTranslateTransform(0, -100.0f + Math.abs(mObj.mMinY),0));
				Matrix4f transform_center = IOUtils.getTranslateTransform(-v_c.x, -v_c.y,-v_c.z);
				Matrix4f transform_onPlatform = IOUtils.getTranslateTransform(0, new_minY ,0);
				transform_center.mul(transform_onPlatform);
				IOUtils.ApplyMat(mObj, transform_center);
				move_left_right_offset = 0;
				move_front_back_offset = 0;
				move_up_down_offset = 0;
			}
			
			move_left_right.setEditText(Integer.toString(move_left_right_offset) + " mm");
			move_front_back.setEditText(Integer.toString(move_front_back_offset) + " mm");
			move_up_down.setEditText(Integer.toString(move_up_down_offset) + " mm");
			Log.i("v_tag=" + v_tag);
			Log.i("move v=" + v + "  is clicked" + ",id=" + v.getId());
		}
	};

	int rotate_x_offset=0, rotate_y_offset=0, rotate_z_offset=0;
	// rotate pannel
	View.OnClickListener c_Dec_rotate_listen = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String v_tag = (String) v.getTag();
			Vector3f point = mObj.GetCenterPointer();
			Bundle bdle= new Bundle();
			bdle.putString("tag", v_tag);
			bdle.putFloat("x", point.x);
			bdle.putFloat("y", point.y);
			bdle.putFloat("z", point.z);
			Message msg = mServiceHandler.obtainMessage(IOUtils.MESSAGE_3D_ROTATE);
			msg.setData(bdle);
			msg.sendToTarget();
			
			if(v_tag.equals("rotate_x_inc")){
				rotate_x_offset += 1;
				//IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.sin(Math.PI/180 * 1), point.x, point.y, point.z, IOUtils.AxisX));
			} else if (v_tag.equals("rotate_x_dec")){
				rotate_x_offset -= 1;
				//IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.sin(Math.PI/180 * (-1)), point.x, point.y, point.z, IOUtils.AxisX));
			} else if (v_tag.equals("rotate_y_inc")){
				rotate_y_offset +=1;
				//IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.sin(Math.PI/180 * 1), point.x, point.y, point.z, IOUtils.AxisY));
			} else if (v_tag.equals("rotate_y_dec")){
				rotate_y_offset -=1;
				//IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.sin(Math.PI/180 * (-1)), point.x, point.y, point.z, IOUtils.AxisY));
			} else if (v_tag.equals("rotate_z_inc")){
				rotate_z_offset += 1;
				//IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.sin(Math.PI/180 * 1), point.x, point.y, point.z, IOUtils.AxisZ));
			} else if (v_tag.equals("rotate_z_dec")){
				rotate_z_offset -= 1;
			//	IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.sin(Math.PI/180 * (-1)), point.x, point.y, point.z, IOUtils.AxisZ));
			} else if (v_tag.equals("x_dec90_rotate")){
				rotate_x_offset -= 90;
				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * (-90), point.x, point.y, point.z, IOUtils.AxisX));
			} else if (v_tag.equals("x_inc90_rotate")){
				rotate_x_offset += 90;
				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * (90), point.x, point.y, point.z, IOUtils.AxisX));
			} else if (v_tag.equals("y_dec90_rotate")){
				rotate_y_offset -= 90;
				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * (-90), point.x, point.y, point.z, IOUtils.AxisY));
			} else if (v_tag.equals("y_inc90_rotate")){
				rotate_y_offset += 90;
				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * (90), point.x, point.y, point.z, IOUtils.AxisY));
			} else if (v_tag.equals("z_dec90_rotate")){
				rotate_z_offset -= 90;
				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * (90), point.x, point.y, point.z, IOUtils.AxisZ));
			} else if (v_tag.equals("z_inc90_rotate")){
				rotate_z_offset += 90;
				IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.PI/180 * (-90), point.x, point.y, point.z, IOUtils.AxisZ));
			} else if (v_tag.equals("rotate_reset")){
				Log.d("restore vertex!!!!!!!");
				mServiceHandler.obtainMessage(IOUtils.MESSAGE_RESTORE_VERTEX).sendToTarget();
				//mObj.restoreVertexTmp();
				//IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.sin(Math.PI/180 * (-rotate_x_offset)), point.x, point.y, point.z, IOUtils.AxisX));
				//IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.sin(Math.PI/180 * (-rotate_y_offset)), point.x, point.y, point.z, IOUtils.AxisY));
				//IOUtils.ApplyMat(mObj, IOUtils.getRotateTransform((float)Math.sin(Math.PI/180 * (-rotate_z_offset)), point.x, point.y, point.z, IOUtils.AxisZ));
				rotate_x_offset = 0;
				rotate_y_offset = 0;
				rotate_z_offset = 0;
			}
			if(Math.abs(rotate_x_offset) != 360){
				rotate_x_offset = ((int) (Math.signum(rotate_x_offset)) * Math.abs(rotate_x_offset)%360) ;
			}
			if(Math.abs(rotate_y_offset) != 360){
				rotate_y_offset = ((int) (Math.signum(rotate_y_offset)) * Math.abs(rotate_y_offset)%360) ;
			}
			if(Math.abs(rotate_z_offset) != 360){
				rotate_z_offset = ((int) (Math.signum(rotate_z_offset)) * Math.abs(rotate_z_offset)%360) ;
			}
			
			x_rotate.setEditText(Integer.toString(rotate_x_offset));
			y_rotate.setEditText(Integer.toString(rotate_y_offset));
			z_rotate.setEditText(Integer.toString(rotate_z_offset));
			Log.i("v_tag=" + v_tag);
			Log.i("rotate v=" + v + "  is clicked");
		}
	};

	int scale_x_offset=0, scale_y_offset=0, scale_z_offset=0;
	float old_x_size[]; float last_size[] = new float[3];
	Boolean is_percent_scale = false;
	float percent_scale_offset = 1.0f;
	//scale pannel 
	View.OnClickListener c_Dec_scale_listen = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String v_tag = (String) v.getTag();
			float a[] =mObj.GetLengthWidthHeight();
			float scale_offset = 0;
			
			Bundle bdle= new Bundle();
			bdle.putString("tag", v_tag);
		
			if(v_tag.equals("uniform_scale")){
				mUniform_scale = uniform_scale_CheckBox.isChecked();
			} else if(v_tag.equals("scale_length_inc")){
				scale_x_offset = 1;
				scale_offset = (a[0] + scale_x_offset) / a[0];
				length_scale.setEditText(floatToStr(scale_x_offset + a[0]) + " mm");
				if(!mUniform_scale){
					//IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(scale_offset, 1.0f, 1.0f));
				} else {
					width_scale.setEditText(floatToStr(a[1] * scale_offset) + " mm");
					height_scale.setEditText(floatToStr(a[2] * scale_offset) + " mm");
				}				
			} else if (v_tag.equals("scale_length_dec")){
				scale_x_offset = -1;
				scale_offset = (a[0] + scale_x_offset) / a[0];
				length_scale.setEditText(floatToStr(scale_x_offset + a[0]) + " mm");
				if(!mUniform_scale){
					//IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(scale_offset, 1.0f, 1.0f));
				} else {
					width_scale.setEditText(floatToStr(a[1] * scale_offset) + " mm");
					height_scale.setEditText(floatToStr(a[2] * scale_offset) + " mm");
				}					
			} else if (v_tag.equals("scale_width_inc")){
				scale_y_offset =1;
				scale_offset = (a[1] + scale_y_offset) / a[1];
				width_scale.setEditText(floatToStr(scale_y_offset + a[1]) + " mm");
				if(!mUniform_scale){
					//IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(1.0f, scale_offset, 1.0f));
				} else {
					length_scale.setEditText(floatToStr(a[0] * scale_offset) + " mm");
					height_scale.setEditText(floatToStr(a[2] * scale_offset) + " mm");
				}				
			} else if (v_tag.equals("scale_width_dec")){
				scale_y_offset =-1;
				scale_offset = (a[1] + scale_y_offset) / a[1];
				width_scale.setEditText(floatToStr(scale_y_offset + a[1]) + " mm");
				if(!mUniform_scale){
					//IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(1.0f, scale_offset, 1.0f));
				} else {
					length_scale.setEditText(floatToStr(a[0] * scale_offset) + " mm");
					height_scale.setEditText(floatToStr(a[2] * scale_offset) + " mm");
				}	
			} else if (v_tag.equals("scale_height_inc")){
				scale_z_offset = 1;
				scale_offset = (a[2] + scale_z_offset) / a[2];
				height_scale.setEditText(floatToStr(scale_z_offset + a[2]) + " mm");
				if(!mUniform_scale){
					//IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(1.0f, 1.0f, scale_offset));
				} else {
					length_scale.setEditText(floatToStr(a[0] * scale_offset) + " mm");
					width_scale.setEditText(floatToStr(a[1] * scale_offset  ) + " mm");
				}	
			} else if (v_tag.equals("scale_height_dec")){
				scale_z_offset = -1;
				scale_offset = (a[2] + scale_z_offset) / a[2];
				height_scale.setEditText(floatToStr(scale_z_offset + a[2]) + " mm");
				if(!mUniform_scale){
					//IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(1.0f, 1.0f, scale_offset));
				} else {
					length_scale.setEditText(floatToStr(a[0] * scale_offset) + " mm");
					width_scale.setEditText(floatToStr(a[1] * scale_offset  ) + " mm");
				}	
			} else if (v_tag.equals("scale_percent_inc")){
				is_percent_scale = true;
				
				if("100%".equalsIgnoreCase(percent_scale.getEditText())){
					percent_scale_offset = 1.0f;
					percent_scale_offset += 0.1;
				} else {
					percent_scale_offset += 0.1f;
				}
				
				scale_offset = percent_scale_offset;
				if(scale_offset <= 1.0f){
					scale_offset = 1.0f + 0.1f;
				}
				
				scale_x_offset = (int) (last_size[0] * scale_offset);
				scale_y_offset = (int) (last_size[1] * scale_offset);
				scale_z_offset = (int) (last_size[2] * scale_offset);
				length_scale.setEditText(Integer.toString(scale_x_offset));
				width_scale.setEditText(Integer.toString(scale_y_offset));
				height_scale.setEditText(Integer.toString(scale_z_offset));
				
				NumberFormat nt = NumberFormat.getPercentInstance();
				nt.setMinimumFractionDigits(2);
				percent_scale.setEditText(nt.format(percent_scale_offset));
			} else if (v_tag.equals("scale_percent_dec")){
				is_percent_scale = true;
				if("100%".equalsIgnoreCase(percent_scale.getEditText())){
					percent_scale_offset = 1.0f;
					percent_scale_offset -= 0.1;
				} else {
					percent_scale_offset -= 0.1f;
				}
				scale_offset = percent_scale_offset;
				if(scale_offset >= 1.0f){
					scale_offset = 1.0f - 0.1f;
				}
				
				scale_x_offset = (int) (last_size[0] * scale_offset);
				scale_y_offset = (int) (last_size[1] * scale_offset);
				scale_z_offset = (int) (last_size[2] * scale_offset);
				length_scale.setEditText(Integer.toString(scale_x_offset));
				width_scale.setEditText(Integer.toString(scale_y_offset));
				height_scale.setEditText(Integer.toString(scale_z_offset));
				
				NumberFormat nt = NumberFormat.getPercentInstance();
				nt.setMinimumFractionDigits(2);
				percent_scale.setEditText(nt.format(percent_scale_offset));
			} else if (v_tag.equals("max_scale")){
				float machine_max = IOUtils.MACHINE_LENGTH;
				float min_edge = Math.min(a[0], a[1]);
				float max_edge = Math.max(a[0], a[1]);
				
				max_edge = Math.max(max_edge, a[2]);
				min_edge =Math.min(min_edge, a[2]);
				
				if(max_edge >= machine_max) {
					scale_offset = machine_max / max_edge;
				} else if(max_edge <= machine_max) { 
					scale_offset = machine_max / max_edge;
				}				
			} else if (v_tag.equals("scale_reset")){
				//scale_offset = (old_x_size[0]) / a[0];
				//length_scale.setEditText(Float.toString(old_x_size[0]));
				//width_scale.setEditText(Float.toString(a[1] * scale_offset));
				//height_scale.setEditText(Float.toString(a[2] * scale_offset));
				mServiceHandler.obtainMessage(IOUtils.MESSAGE_RESTORE_VERTEX).sendToTarget();
				//mObj.restoreVertexTmp();
				scale_x_offset = 0;
				scale_y_offset = 0;
				scale_z_offset = 0;
				percent_scale.setEditText("100%");
			}
			
			bdle.putBoolean("isUniform", mUniform_scale);
			bdle.putBoolean("isPercentScale", is_percent_scale);
			bdle.putFloat("scale_offset", scale_offset);
			Message msg = mServiceHandler.obtainMessage(IOUtils.MESSAGE_3D_SCALE);
			msg.setData(bdle);
			msg.sendToTarget();
			
			if(is_percent_scale){
				//IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(scale_offset, scale_offset, scale_offset));
				is_percent_scale = false;
			} else if (!v_tag.equals("scale_reset") && (!v_tag.equals("uniform_scale")) && mUniform_scale ){
				//IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(scale_offset, scale_offset, scale_offset));
			} else if (v_tag.equals("max_scale")){
				IOUtils.ApplyMat(mObj, IOUtils.getScaleTransform(scale_offset, scale_offset, scale_offset));
			} 
						
			Log.i("scale_offset=" + scale_offset);
			Log.i("old val a[0]=" + a[0] + ",a[1]=" + a[1]  + ",a[2]=" + a[2]);
			
			a =mObj.GetLengthWidthHeight();
			length_scale.setEditText(floatToStr(a[0]) + " mm");
			width_scale.setEditText(floatToStr(a[1]) + " mm");
			height_scale.setEditText(floatToStr(a[2]) + " mm");

			Log.i("new val a[0]=" + a[0] + ",a[1]=" + a[1]  + ",a[2]=" + a[2]);
			
			Log.i("v_tag=" + v_tag);
			Log.i("scale v=" + v + "  is clicked" + ",mUniform_scale=" + mUniform_scale);
		}
	};
	
	View.OnClickListener pannel_listen = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			if (mObj == null || mSettingPannelLayer1.isShown() || mSettingPannelLayer2.isShown()){
				return;
			}
			switch((Integer)v.getTag()){
				case IOUtils.LOOKPANNEL_ID:
					mCur_Sel_Panel = IOUtils.LOOKPANNEL_ID;
					break;
				case IOUtils.MOVEPANNEL_ID:
					mCur_Sel_Panel = IOUtils.MOVEPANNEL_ID;
					break;
				case IOUtils.ROTATEPANNEL_ID:
					mCur_Sel_Panel = IOUtils.ROTATEPANNEL_ID;
					break;
				case IOUtils.SCALEPANNEL_ID:
					mCur_Sel_Panel = IOUtils.SCALEPANNEL_ID;
					break;
			}
			
			if((Integer)v.getTag() != IOUtils.ROTATEPANNEL_ID){
				mSceneView.mRenderer.mArgCircle.setEndArgCircle();
			}
			mSceneView.mRenderer.mTwoLine.setVisable(false);
			mSceneView.mRenderer.mArgCircle.setVisable(false);
			
			

			if (mlast_Sel_Panel != mCur_Sel_Panel){
				mClick_num = 1;
			} else {
				mClick_num++;
				mClick_num = mClick_num % (LeftPannel.TWO_CLICK +1);
			}
			mSceneView.mViewMode_sub = mClick_num;
			mSceneView.mViewMode = mCur_Sel_Panel;
			
			int state =0;
			if (mClick_num == 0){
				state = LeftPannel.ZERO_CLICK;
			} else if(mClick_num == 1){
				state = LeftPannel.ONE_CLICK;
			} else if(mClick_num == 2){
				state = LeftPannel.TWO_CLICK;
			}
			
			if( mCur_Sel_Panel != IOUtils.LOOKPANNEL_ID ){
				mSceneView.setObseverDirection(IOUtils.DIRECTION_FRONT);
			}
			
			mPannel.get(mCur_Sel_Panel).setButtonState(state);
			for(int i=IOUtils.LOOKPANNEL_ID; i <= IOUtils.SCALEPANNEL_ID; i++ ){
				if(i != mCur_Sel_Panel)
					mPannel.get(i).setButtonState(LeftPannel.ZERO_CLICK);
					if(state == LeftPannel.TWO_CLICK){
						float a[] =mObj.GetLengthWidthHeight();
						last_size[0] = a[0]; last_size[1] = a[1]; last_size[2] = a[2];						
						//mObj.saveVertexTmp();
						mServiceHandler.obtainMessage(IOUtils.MESSAGE_SAVE_VERTEX).sendToTarget();
						Log.d("save vertex!!!!!!!");
					}
			}
			mlast_Sel_Panel = mCur_Sel_Panel;
			
			if( state == LeftPannel.ZERO_CLICK ){// mCur_Sel_Panel != LOOKPANNEL_ID &&
				mPannel.get(IOUtils.LOOKPANNEL_ID).setButtonState(LeftPannel.ONE_CLICK);
				mlast_Sel_Panel = mCur_Sel_Panel = IOUtils.LOOKPANNEL_ID;
				mSceneView.mViewMode = IOUtils.LOOKPANNEL_ID;
				mClick_num = 1;
			}
			
			switch((Integer)v.getTag()){
			case IOUtils.LOOKPANNEL_ID:
				break;
			case IOUtils.MOVEPANNEL_ID:
				move_left_right.setEditText(Integer.toString(move_left_right_offset));
				move_front_back.setEditText(Integer.toString(move_front_back_offset));
				move_up_down.setEditText(Integer.toString(move_up_down_offset));
				break;
			case IOUtils.ROTATEPANNEL_ID:
				x_rotate.setEditText(Integer.toString(rotate_x_offset));
				y_rotate.setEditText(Integer.toString(rotate_y_offset));
				z_rotate.setEditText(Integer.toString(rotate_z_offset));
				break;
			case IOUtils.SCALEPANNEL_ID:
				float a[] =mObj.GetLengthWidthHeight();
				
				length_scale.setEditText(floatToStr(a[0]) + " mm");
				width_scale.setEditText(floatToStr(a[1]) + " mm");
				height_scale.setEditText(floatToStr(a[2]) + " mm");
				break;
		}
		}
	};	
	

	
}
