package lkj.unicron.view;

import lkj.nuicorn.R;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import lkj.unicron.util.IOUtils;
import lkj.unicron.util.Log;

//public class LeftPannel extends LinearLayout { 
		public class LeftPannel extends ViewGroup {  //ViewGroup
	
	public static final int ZERO_CLICK = 0;
	public static final int ONE_CLICK = 1;
	public static final int TWO_CLICK = 2;
	
	private int mClick_state = ZERO_CLICK;
	private int mClick_num =0;
	
	Button mBtn1;
	final LinearLayout mLinear0;
	final LinearLayout mLinear1;
	
    public LeftPannel(Context context, AttributeSet attrs) {
    	super(context, attrs);
        LayoutInflater inflate =
                (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.left_pannel, null);
        
        addView(v, new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        
        // LayoutInflater.from(context).inflate(R.layout.three_button, this, true);
     //lkj  mBtn1 = (Button)this.findViewById(R.id.button1);
        mLinear0 = (LinearLayout)findViewById(R.id.liner0);
        mLinear1 = (LinearLayout)findViewById(R.id.liner1);
        

  /*      mBtn1.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.i("btn1 is clicked");

					if (mClick_num == 0){
						setButtonState(ONE_CLICK);	
					} else if(mClick_num == 1){
						setButtonState(TWO_CLICK);	
					} else if(mClick_num == 2){
						setButtonState(ZERO_CLICK);	
						mClick_num = -1;
					}
					mClick_num ++;
				}
		});*/
        
      //  setButtonState(ZERO_CLICK);	
		Log.i("LeftPannel init");
		// TODO Auto-generated constructor stub
    }
    
	public void setBtnOnClick(View.OnClickListener l){
		mBtn1.setOnClickListener(l);
	}
	
    public void setButtonState(int state){
    	mClick_state = state;
    	if (mClick_state == ONE_CLICK){
    		mBtn1.setBackgroundColor(Color.RED);
			mLinear1.setVisibility(INVISIBLE);
		} else if(mClick_state == TWO_CLICK){
			mBtn1.setBackgroundColor(Color.GREEN);
			mLinear1.setVisibility(VISIBLE);
		} else if (mClick_state == ZERO_CLICK ){
			mBtn1.setBackgroundColor(Color.BLUE);
			mLinear1.setVisibility(INVISIBLE);
		}
    }
    
	public void setButtonText(String str){
		mBtn1.setText(str);		
	}
	
	public void setButtonTag(int tagId){
		mBtn1.setTag(tagId);	
	}
	
	public int getButtonTag(){
		return (Integer) mBtn1.getTag();	
	}
	
	public int getButtonState(String str){
		return mClick_state;
	}
    
    public void addOtherView(View v){
    	mLinear1.addView(v);	
    }
    
    public void addRightButtonView(Button btn){
    	mLinear0.addView(btn);	
    	mBtn1 = btn;
    	setButtonState(ZERO_CLICK);	
    }
    
    /*
     * 
     * */
    public Control_IncDec addIncDecControl(Context context, int resId, String text, View.OnClickListener dec, View.OnClickListener inc ){
		Control_IncDec c_IncDec = new Control_IncDec(context, null);
		c_IncDec.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		c_IncDec.setImage(resId);
		c_IncDec.setText(text);
		c_IncDec.setBtnDec(dec);
		c_IncDec.setBtnInc(inc);
    	
		addOtherView(c_IncDec);	
		return c_IncDec;
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
			int totalLen = 0;
			
			int view_h ;
            int view_padtop ;
            int view_padmbottom ;
            
            int new_t, new_b;
            
	        // 遍历所有子视图  
	        int childCount = getChildCount();  
	        for (int i = 0; i < childCount; i++) {  
	            View childView = getChildAt(i);  
	            ViewGroup vg = (ViewGroup) childView;
	            // 获取在onMeasure中计算的视图尺寸  
	            int measureHeight = childView.getMeasuredHeight();  
	            int measuredWidth =  childView.getMeasuredWidth();  //childView.getWidth();
	            	            
	            totalLen += measureHeight; 
	            
	            view_h = measureHeight;
	            view_padtop = childView.getPaddingTop();
	            view_padmbottom = childView.getPaddingBottom();
	            
	            new_t = t;
	            if(t>720)
	            	new_t = t - 720;
	            
	            new_b = b;
	            if(b>720)
	            	new_b = b - 720;
	            
/*	            if((t + view_padtop + view_h) > (measureHeight - view_padmbottom + 10)){
	            	new_t = measureHeight - view_h - view_padtop - view_padmbottom;
	            }*/ //=0,t=20,r=1020,b=720new_t=20
	            
	        //    childView.layout(0, 20, 300, 200);
	            childView.layout(l, t, r, b);
	            Log.i( "2 LeftPannel onLayout view :" + childView + ",measuredWidth=" +measuredWidth  + ",measureHeight=" + measureHeight);
	            Log.i( "LeftPannel onLayout view" + ",view_h=" + childView.getHeight() + ",view_width=" + childView.getWidth());
	            
	            Log.i( "LeftPannel onLayout view i=" + i + ",l=" + l + ",t=" + t + ",r=" + r + ",b=" + b + ",new_t=" + new_t);

	           // Log.i( "LeftPannel ,view_padtop=" + view_padtop + ",view_padmbottom=" + view_padmbottom );
            	Log.i( "LeftPannel getChildCount=" + vg.getChildCount());
	            	View v_linearlayout1 = vg.getChildAt(0);
	            	View v_btn = vg.getChildAt(1);
	            	Log.i( "LeftPannel LinearLayout childView=" + v_btn + ",left=" + v_btn.getLeft() + ",top=" + v_btn.getTop() + ",right=" + v_btn.getRight() + ",bottom=" + v_btn.getBottom()
	            			 	+ ",width=" + vg.getMeasuredWidth() + ",height=" + vg.getMeasuredHeight());
	            	
	            	int btn_l, btn_t, btn_r, btn_b;
	            	int linear_l, linear_t, linear_r, linear_b;
	            	btn_l = v_btn.getLeft();
	            	btn_t = v_btn.getTop();
	            	btn_r = v_btn.getRight();
	            	btn_b = v_btn.getBottom();
	            	int btn_width = btn_r - btn_l;
	            	int btn_height = btn_b - btn_t;
	            	
	            	linear_l = v_linearlayout1.getLeft();
	            	linear_t = v_linearlayout1.getTop();
	            	linear_r = v_linearlayout1.getRight();
	            	linear_b = v_linearlayout1.getBottom();
	            	int linearlayout_width = linear_r - linear_l;
	            	int linearlayout_height = linear_b - linear_t;
	   
	            	
	            	int layout_width = r - l ;
	            	int layout_height = b - t ;
	            	
	            	
	            	v_btn.setLeft(layout_width - btn_width);
	            	v_btn.setRight(layout_width + btn_width);
	            	
	            	
	            	v_linearlayout1.setLeft(layout_width - linearlayout_width - btn_width);
	            	v_linearlayout1.setRight(layout_width + linearlayout_width + btn_width);
	            	if( (t + linearlayout_height)> layout_height){
	            		Log.i( "LeftPannel out bound!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	            		//v_linearlayout1.setTop(t - (t + linearlayout_height- layout_height));
	            	//	v_linearlayout1.setTop(layout_height -linearlayout_height);
	            		//v_linearlayout1.setBottom(b );
	            	}
	            		Log.i( "LeftPannel Button btn_l=" + btn_l + ",btn_t=" + btn_t + ",btn_r=" + btn_r + ",btn_b=" + btn_b
	            			 	+ ",btn_width=" + btn_width + ",btn_height=" + btn_height);
	            		Log.i( "LeftPannel Button linear_l=" + linear_l + ",linear_t=" + linear_t + ",linear_r=" + linear_r + ",linear_b=" + linear_b
	            			 	+ ",linearlayout_width=" + linearlayout_width + ",linearlayout_height=" + linearlayout_height);
	            		Log.i( "LeftPannel r=" + r + ",l=" + l + ",b=" + b + ",t=" + t);
	            		Log.i( "LeftPannel btn=" + v_btn);
	            		Log.i( "LeftPannel v_linearlayout1=" + v_linearlayout1);
	            		Log.i( "LeftPannel v_btn.getX()=" + v_btn.getX() + ",v_btn.getY()" + v_btn.getY());
	            		//Log.i( "LeftPannel left_offset=" + left_offset);
	        }  
	        
		//	super.onLayout(changed, l, t, r, b);   
	        
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		

		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		Log.i("LeftPannel  onMeasure widthSize:" + widthSize + ",widthMode=" + widthMode);
		Log.i("LeftPannel  onMeasure heightSize:" + heightSize + ",heightMode=" + heightMode);
		//Log.i("AT_MOST=" + MeasureSpec.AT_MOST);
	    
		
		int measuredWidth = widthMeasureSpec;
		int measureHeight = heightMeasureSpec;
        int childCount = getChildCount();  
        for (int i = 0; i < childCount; i++) {  
            View childView = getChildAt(i);  
            
            // 获取在onMeasure中计算的视图尺寸  
            measuredWidth =  childView.getMeasuredWidth();  
            measureHeight = childView.getMeasuredHeight();  
         //   if (measureHeight>720)
         //   	measureHeight = 200;
            childView.measure(measuredWidth, measureHeight);
           // childView.measure(60, 60);
            
            Log.i( "LeftPannel  onMeasure  i=" + i + ",id=" + childView.getId() +  "onMeasure layout view:" + childView);
            Log.i( "LeftPannel  onMeasure  width=" + childView.getWidth() +  ",height=:" + childView.getHeight());
            Log.i( "LeftPannel  onMeasure  measuredWidth=" + measuredWidth +  ",measureHeight=:" + measureHeight);
           
        }  
        // setMeasuredDimension(measuredWidth, measureHeight);
		// TODO Auto-generated method stub
		super.onMeasure(measuredWidth, measureHeight);
	}
	/* */
    
/*	public ThreeButton(Context context) {
		super(context);
	}


	public ThreeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}*/

}
