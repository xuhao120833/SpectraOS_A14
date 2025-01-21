package com.htc.spectraos.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyGridViewTextView extends TextView {

	public MyGridViewTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyGridViewTextView(Context context, AttributeSet attrs,
                              int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public MyGridViewTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isFocused() {
		// TODO Auto-generated method stub
		 return true;  
	}

}
