package lkj.unicron.view;

import lkj.nuicorn.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ThreeStateButton extends Button {

    private Drawable mNormalBackground;
    private Drawable mGreenBackground;
    private Drawable mRedBackground;
    
	public ThreeStateButton(Context context) {
		this(context, null, 0);
		// TODO Auto-generated constructor stub
	}

	public ThreeStateButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
		//this.setBackgroundColor(Color.BLUE);
        this.setBackground(mNormalBackground);
	}

	public ThreeStateButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
        setWillNotDraw(false);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ThreeBackground, defStyle, 0);

        mNormalBackground = a.getDrawable(R.styleable.ThreeBackground_normalBackground);
        mGreenBackground = a.getDrawable(R.styleable.ThreeBackground_greenBackground);
        mRedBackground = a.getDrawable(R.styleable.ThreeBackground_redBackground);

        a.recycle();

	}
	
	public static final int ZERO_CLICK = 0;
	public static final int ONE_CLICK = 1;
	public static final int TWO_CLICK = 2;
	
	private int mClick_state = ZERO_CLICK;
	private int mClick_num =0;
	
	LinearLayout mLinearPanel;
	
    public void setButtonState(int state){
    	mClick_state = state;
    	if (mClick_state == ONE_CLICK){
    		//this.setBackgroundColor(Color.GREEN);
    		this.setBackground(mGreenBackground);
    		mLinearPanel.setVisibility(View.GONE);
		} else if(mClick_state == TWO_CLICK){
			//this.setBackgroundColor(Color.RED);
			this.setBackground(mRedBackground);
			mLinearPanel.setVisibility(View.VISIBLE);
		} else if (mClick_state == ZERO_CLICK ){
			//this.setBackgroundColor(Color.BLUE);
			this.setBackground(mNormalBackground);
			mLinearPanel.setVisibility(View.GONE);
		}
    }
    
    public void setButtonPanel(LinearLayout panel){
    	mLinearPanel = panel;
    	
    }
}
