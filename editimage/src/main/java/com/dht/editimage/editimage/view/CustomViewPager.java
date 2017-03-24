package com.dht.editimage.editimage.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * 禁用ViewPager滑动事件
 */
public class CustomViewPager extends ViewPager {
    private boolean isCanScroll = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        isCanScroll = true;
        super.setCurrentItem(item, smoothScroll);
        isCanScroll = false;
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (isCanScroll) {
            super.scrollTo(x, y);
        }
    }
}