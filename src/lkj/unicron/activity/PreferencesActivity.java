package lkj.unicron.activity;

import org.join.ogles.lib.GLColor;

import lkj.nuicorn.R;
import lkj.unicron.cura.Profile;
import lkj.unicron.util.Config;
import lkj.unicron.util.Log;
import lkj.unicron.util.MeshLoader;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PreferencesActivity extends Activity {
	private SeekBar redSeekBar;
	private SeekBar greenSeekBar;
	private SeekBar blueSeekBar;
	private SeekBar alphaSeekBar;
	private ToggleButton mToggleBox;
	private ToggleButton gridsToggleButton;
	private TextView objectColorView;
	private Profile mProfile ;
	
	private Context mContext ;
	
	GLColor mColor;
	  float mRed;
	  float mGreen;
	  float mBlue;
	  float mAlpha;
	  boolean mdisplayBox = true;
	  boolean mdisplayGrids = true;

	private void applyColor() {
		int r=0, g=0, b=0, a=0, color=0;
		int gl_r=0, gl_g=0, gl_b=0, gl_a=0;
		
		r = redSeekBar.getProgress() * 255 / redSeekBar.getMax();
		g = greenSeekBar.getProgress() * 255 / greenSeekBar.getMax();
		b = blueSeekBar.getProgress() * 255 / blueSeekBar.getMax();
		a = alphaSeekBar.getProgress() * 255 / alphaSeekBar.getMax();
		
		gl_r = redSeekBar.getProgress() * 65535 / redSeekBar.getMax();
		gl_g = greenSeekBar.getProgress() * 65535 / greenSeekBar.getMax();
		gl_b = blueSeekBar.getProgress() * 65535 / blueSeekBar.getMax();
		gl_a = alphaSeekBar.getProgress() * 65535 / alphaSeekBar.getMax();
		
		color = r << 16;
		color |= g << 8;
		color |= b;
		color |= a << 24;
		objectColorView.setBackgroundColor(color);
		

		mColor.r = gl_r ;
		mColor.g = gl_g ;
		mColor.b = gl_b ;
		mColor.a = gl_a ;
	}

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
		setContentView(R.layout.preferences);
		
		mContext = this;
		
		mColor = new GLColor();
		mProfile = new Profile(mContext);
		
		redSeekBar = (SeekBar) findViewById(R.id.redSeekBar);
		greenSeekBar = (SeekBar) findViewById(R.id.greenSeekBar);
		blueSeekBar = (SeekBar) findViewById(R.id.blueSeekBar);
		alphaSeekBar = (SeekBar) findViewById(R.id.alphaSeekBar);
		mToggleBox = (ToggleButton) findViewById(R.id.toggleBox);
	/*	gridsToggleButton = (ToggleButton) findViewById(R.id.gridToggleButton);*/

		objectColorView = (TextView) findViewById(R.id.objectColorView);
		OnTouchListener onTouchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				applyColor();
				return false;
			}
		};

		redSeekBar.setOnTouchListener(onTouchListener);
		greenSeekBar.setOnTouchListener(onTouchListener);
		blueSeekBar.setOnTouchListener(onTouchListener);
		alphaSeekBar.setOnTouchListener(onTouchListener);

		Button resetButton = (Button) findViewById(R.id.resetButton);
		resetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRed = Config.DEFAULT_RED;
				mGreen = Config.DEFAULT_GREEN;
				mBlue = Config.DEFAULT_BLUE;
				mAlpha = Config.DEFAULT_ALPHA;
				mdisplayBox = false;
				mdisplayGrids = true;

				redSeekBar.setProgress((int) (redSeekBar.getMax() * mRed));
				greenSeekBar.setProgress((int) (greenSeekBar.getMax() * mGreen));
				blueSeekBar.setProgress((int) (blueSeekBar.getMax() * mBlue));
				alphaSeekBar.setProgress((int) (alphaSeekBar.getMax() * mAlpha));
				mToggleBox.setChecked(mdisplayBox);
			//	gridsToggleButton.setChecked(mdisplayGrids);
				
				applyColor();
			}
		});

		Button closeButton = (Button) findViewById(R.id.closeButton);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				Intent intent = new Intent();
				intent.putExtra("red", mColor.r);
				intent.putExtra("green", mColor.g);
				intent.putExtra("blue", mColor.b);
				intent.putExtra("alpha", mColor.a);
				intent.putExtra("displayBox", mToggleBox.isChecked());
				
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		// simple print
		final Dialog quickPrintQuality;
		quickPrintQuality = new Dialog(this);
		quickPrintQuality.setContentView(R.layout.simple_print_setting);
		quickPrintQuality.setTitle("Simple print setting");
		quickPrintQuality.setCancelable(false);
		
		RadioGroup quickPrintQuality_radioGroup = (RadioGroup)quickPrintQuality.findViewById(R.id.quickPrintQuality);        
		quickPrintQuality_radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					if(checkedId == R.id.lowQuality){
						Log.d("low quality");
					} else if (checkedId == R.id.normalQuality){
						Log.d("normal quality");
					} else if (checkedId == R.id.hightQuality){
						Log.d("hight quality");
					}
					quickPrintQuality.hide();
				}
        });
		quickPrintQuality_radioGroup.check(R.id.lowQuality);
		
		// simple print
		final Dialog fullPrintQuality;
		fullPrintQuality = new Dialog(this);
		fullPrintQuality.setContentView(R.layout.full_print_setting);
		fullPrintQuality.setTitle("full print setting");
		fullPrintQuality.setCancelable(false);
		
		RadioGroup printQuality_radioGroup = (RadioGroup)this.findViewById(R.id.printQuality);        
		printQuality_radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					SharedPreferences pathSettingConfig = getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
					Editor editor = pathSettingConfig.edit();
					editor.putInt("printSettingSelect", checkedId);
					editor.commit();
					
					if(checkedId == R.id.quickPrintBtn){
						quickPrintQuality.show();
						Log.d("quickPrintBtn quality");
					} else if (checkedId == R.id.fullPrintBtn){
						LinearLayout tagLinearLayout = (LinearLayout) fullPrintQuality.findViewById(R.id.linearLayoutTag);
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
						fullPrintQuality.show();
						Log.d("fullPrintBtn quality");
					} else if (checkedId == R.id.expertPrintBtn){
						Log.d("expertPrintBtn quality");
					}
				}
        });
		SharedPreferences pathSettingConfig = getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		int select_id =  pathSettingConfig.getInt("printSettingSelect", R.id.quickPrintBtn);
		printQuality_radioGroup.check(select_id);
		
		Button ok_full_print_Btn = (Button) fullPrintQuality.findViewById(R.id.ok_full_print_Btn);
		ok_full_print_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinearLayout tagLinearLayout = (LinearLayout) fullPrintQuality.findViewById(R.id.linearLayoutTag);
				int childNum= tagLinearLayout.getChildCount();
				for(int i=0; i<childNum; i++){
					View child = tagLinearLayout.getChildAt(i);
					String tag = (String) child.getTag();
					if(tag != null){
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
				Config.save_full_config(mContext, mProfile);
				fullPrintQuality.hide();
				//Config.get_full_config(mContext);
			}
		});
		
		Spinner s1 = (Spinner) fullPrintQuality.findViewById(R.id.spinner_SupportType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.support_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter);
        s1.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        Log.d("Spinner1: position=" + position + " id=" + id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    	Log.d("Spinner1: unselected");
                    }
                });
        
		Spinner s2 = (Spinner) fullPrintQuality.findViewById(R.id.spinner_AdhesionType);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.platform_adhesion_type, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(adapter2);
        s2.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        Log.d("Spinner2: position=" + position + " id=" + id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    	Log.d("Spinner2: unselected");
                    }
                });
	};

	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences colorConfig = getSharedPreferences("other_configs", Activity.MODE_PRIVATE);
		Editor editor = colorConfig.edit();

		mRed = (float) redSeekBar.getProgress() / (float) redSeekBar.getMax();
		mGreen = (float) greenSeekBar.getProgress() / (float) greenSeekBar.getMax();
		mBlue = (float) blueSeekBar.getProgress() / (float) blueSeekBar.getMax();
		mAlpha = (float) alphaSeekBar.getProgress() / (float) alphaSeekBar.getMax();
		mdisplayBox = mToggleBox.isChecked();
	//	mdisplayGrids = gridsToggleButton.isChecked();

		editor.putFloat("red", mRed);
		editor.putFloat("green", mGreen);
		editor.putFloat("blue", mBlue);
		editor.putFloat("alpha", mAlpha);
		editor.putBoolean("displayBox", mdisplayBox);
	//	editor.putBoolean("displayGrids", mdisplayGrids);

		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences colorConfig = getSharedPreferences("other_configs", Activity.MODE_PRIVATE);

		float red = colorConfig.getFloat("red", Config.DEFAULT_RED);
		float green = colorConfig.getFloat("green", Config.DEFAULT_GREEN);
		float blue = colorConfig.getFloat("blue", Config.DEFAULT_BLUE);
		float alpha = colorConfig.getFloat("alpha", Config.DEFAULT_ALPHA);
		boolean displayBox = colorConfig.getBoolean("displayBox", true);
		boolean displayGrids = colorConfig.getBoolean("displayGrids", true);

		redSeekBar.setProgress((int) (redSeekBar.getMax() * red));
		greenSeekBar.setProgress((int) (greenSeekBar.getMax() * green));
		blueSeekBar.setProgress((int) (blueSeekBar.getMax() * blue));
		alphaSeekBar.setProgress((int) (alphaSeekBar.getMax() * alpha));
		mToggleBox.setChecked(displayBox);
	//	gridsToggleButton.setChecked(displayGrids);

		applyColor();
	}
	
 
}
