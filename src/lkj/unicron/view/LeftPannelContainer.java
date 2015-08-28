package lkj.unicron.view;

import lkj.unicron.util.Log;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;

public class LeftPannelContainer extends ViewGroup  { //ViewGroup LinearLayout

	public LeftPannelContainer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public LeftPannelContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LeftPannelContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		

		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

	//	Log.i("lkj", "onMeasure widthSize:" + widthSize + ",widthMode=" + widthMode);
	//	Log.i("lkj", "onMeasure heightSize:" + heightSize + ",heightMode=" + heightMode);
		
	     
		
        int childCount = getChildCount();  
        for (int i = 0; i < childCount; i++) {  
            View childView = getChildAt(i);  
           // Log.i( "container i=" + i +  "onMeasure layout view:" + childView);
            ViewParent vg = childView.getParent();
            
            
            // 获取在onMeasure中计算的视图尺寸  
            int measuredWidth = childView.getMeasuredWidth(); 
            int measureHeight = childView.getMeasuredHeight();  
 
          // childView.measure(60, 60);
            childView.measure(measuredWidth, measureHeight);
            
            Log.i( "container Measure i=" + i + ",id=" + childView.getId() +  "onMeasure layout view:" + childView);
            Log.i( "container Measure width=" + childView.getWidth() +  ",height=:" + childView.getHeight());
            Log.i( "container Measure measuredWidth=" + measuredWidth +  ",measureHeight=:" + measureHeight);
           
        }  
        //setMeasuredDimension(60, 60);
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	final int BUTTONMARGIN = 50;
	final int CONTROLMARGIN = 20;
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int totalLen = 0;
		int topLen = 20;// CONTROLMARGIN + 40;
		int lastViewHeight = 0;
		
        // 遍历所有子视图  
        int childCount = getChildCount();  
        for (int i = 0; i < childCount; i++) {  
            View childView = getChildAt(i);  
            // 获取在onMeasure中计算的视图尺寸  
            int measureHeight = childView.getMeasuredHeight(); 
            int measuredWidth =  childView.getMeasuredWidth();  //childView.getWidth();
            	   
            
            totalLen += measureHeight; 
           // childView.layout(l + topLen, t  , r  + topLen , b );
            childView.layout(l , t + topLen , r, b );
          //  childView.layout(b - BUTTONMARGIN  , t + topLen , r   , b  );
            
            Log.d( "container onLayout view :" + childView + ",measureHeight=" + measureHeight + ",measuredWidth=" + measuredWidth);
            Log.d( "container onLayout view i=" + i + ",l=" + l + ",t=" + t + ",r=" + r + ",b=" + b + ",topLen=" + topLen);
            
           // topLen +=  CONTROLMARGIN + BUTTONMARGIN;
            topLen +=  BUTTONMARGIN;
            lastViewHeight = childView.getHeight();
 		
          
        }  
	}

	/*
	final int BUTTONMARGIN = 45;
	final int CONTROLMARGIN = 20;
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int totalLen = 0;
		int topLen = CONTROLMARGIN;
		int lastViewHeight = 0;
		
        // 遍历所有子视图  
        int childCount = getChildCount();  
        for (int i = 0; i < childCount; i++) {  
            View childView = getChildAt(i);  
            // 获取在onMeasure中计算的视图尺寸  
            int measureHeight = childView.getMeasuredHeight(); 
            int measuredWidth =  childView.getMeasuredWidth();  //childView.getWidth();
            	   
            
            totalLen += measureHeight; 
            childView.layout(l, t + topLen , r, b + topLen);
            
            topLen +=  CONTROLMARGIN + BUTTONMARGIN;
            lastViewHeight = childView.getHeight();
 		
           // Log.i( "container onLayout view :" + childView + ",measureHeight=" + measureHeight + ",measuredWidth=" + measuredWidth);
            //Log.i( "container onLayout view i=" + i + ",l=" + l + ",t=" + t + ",r=" + r + ",b=" + b + "topLen=" + topLen);
        }  
	}*/

	
}
