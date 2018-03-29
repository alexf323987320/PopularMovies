package com.example.alex.popularmovies;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

//https://gist.github.com/egslava/589b82a6add9c816a007
//view pager with the ability to wrap content

public class ViewPagerExt extends ViewPager {

    public ViewPagerExt(Context context) {
        super(context);
//        setOffscreenPageLimit(100);
    }

    public ViewPagerExt(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setOffscreenPageLimit(100);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//        int height = 0;
//        for(int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            int h = child.getMeasuredHeight();
//            if(h > height) height = h;
//        }

//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            //we stored the adapter position in the tag of TextView in the ReviewFragment
            if ((int)child.getTag() == getCurrentItem()) {
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int height = child.getMeasuredHeight();
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                break;
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
