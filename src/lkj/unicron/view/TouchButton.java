package lkj.unicron.view;

import java.util.List;

import lkj.unicron.util.Log;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class TouchButton extends Button {

	OnClickListener mListener;
	View v = null;
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		// TODO Auto-generated method stub
		mListener = l;
		v = this;
		super.setOnClickListener(l);
	}

	 private boolean clickdown = false;
	 private boolean mLongClcik = false;
	 private int mtime =2000; //default
	 
	 final Runnable runLongClick = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
		
			/*while(mLongClcik)
            {
            	sleep(100);
				 mListener.onClick(getRootView());
				 Log.d("call onclick");
            }*/
		}
	 };

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		/*if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            clickdown = true;
            new LongTouchTask().execute();
          //  Log.d("按下");
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            clickdown = false;
          //  Log.d("弹起");
        }*/
		return super.onTouchEvent(event);
	}


	public TouchButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TouchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	 class  LongTouchTask extends AsyncTask<byte[], Integer, List<Float>>{
	        protected void onPostExecute(Void result) {
	 
	        }
	        protected void onProgressUpdate(Integer... values) {
	        	if(clickdown)
	        		mListener.onClick(v);
	        }
			@Override
			protected List<Float> doInBackground(byte[]... arg0) {

            	sleep(mtime);

	            while(clickdown)
	            {
	            	
	            	sleep(100);
	                publishProgress(0);
	            }
	            return null;
			}
	    }
	 
	 private void sleep(int time) {
	        try {
	            Thread.sleep(time);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }   

	    public void setOnLongTouchListener(int time) {
	        mtime = time;
	    }
	     
}
