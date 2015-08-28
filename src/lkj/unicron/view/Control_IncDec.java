package lkj.unicron.view;

import lkj.nuicorn.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import lkj.unicron.util.Log;

public class Control_IncDec extends LinearLayout {

	//final ImageView mImgView;
	final TextView mTV;
	final TouchButton mImgBtnInc;
	final TextView mEditText;
	final TouchButton mImgBtnDec;
	
	public Control_IncDec(Context context, AttributeSet attrs) {
		super(context, attrs);
    	
        LayoutInflater inflate =
                (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.control_inc_des_edit, null);
        
        addView(v, new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        
    //    mImgView = (ImageView) v.findViewById(R.id.imageView1);
        mTV = (TextView) v.findViewById(R.id.textView1);
    	mImgBtnDec = (TouchButton) v.findViewById(R.id.imageButton1);
    	mEditText = (TextView) v.findViewById(R.id.editText1);
    	mEditText.setFocusable(false);
    	mImgBtnInc = (TouchButton) v.findViewById(R.id.imageButton2);
    	Log.d("control inc dec init");
	}
	
    public Control_IncDec addIncDecControl(int resId, String text, View.OnClickListener dec, View.OnClickListener inc ){
		//this.setImage(resId);
		this.setText(text);
		this.setBtnDec(dec);
		this.setBtnInc(inc);
		return this;
    }
	
	
	public void setText(String str){
		mTV.setText(str);		
	}
	
	public void setEditText(String str){
		mEditText.setText(str);		
	}
	
	public String getEditText(){
		return mEditText.getText().toString();		
	}
	
	public void setImage(int resid){
	//	mImgView.setBackgroundResource(resid);
	}
	
	public void setImage(Drawable bg){
	//	mImgView.setBackground(bg);
	}
	
	public void setBtnDec(View.OnClickListener l){
		mImgBtnDec.setOnClickListener(l);
	}
	
	public void setBtnInc(View.OnClickListener l){
		mImgBtnInc.setOnClickListener(l);
	}
	
	public void setBtnIncTag(String str){
		mImgBtnInc.setTag(str);
	}
	
	public void setBtnDecTag(String str){
		mImgBtnDec.setTag(str);
	}
}
