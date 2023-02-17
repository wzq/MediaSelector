package com.wzq.media.selector.basic;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * copy from photo view
 */
public class HackyViewPager extends ViewPager {
	
    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException e) {
			return false;
		}
    }
}
