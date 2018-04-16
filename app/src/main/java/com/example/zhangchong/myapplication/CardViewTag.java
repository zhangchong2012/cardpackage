package com.example.zhangchong.myapplication;

/**
 * Created by zhangchong on 2018/4/10.
 */

public class CardViewTag {
    private String mViewId;

    //最初的高度
    private int originalHeight;

    private int originalPaddingBottom = -1;
    private int originalPaddingRight = -1;
    private int originalPaddingLeft = -1;
    private int originalPaddingTop = -1;

    private int mRemoveIndex;

    public CardViewTag() {
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public void setOriginalHeight(int originalHeight) {
        this.originalHeight = originalHeight;
    }


    public int getOriginalPaddingBottom() {
        return originalPaddingBottom;
    }

    public int getOriginalPaddingTop() {
        return originalPaddingTop;
    }

    public int getOriginalPaddingRight() {
        return originalPaddingRight;
    }

    public int getOriginalPaddingLeft() {
        return originalPaddingLeft;
    }

    public void setOriginalPaddingBottom(int left, int top, int right, int bottom) {
        this.originalPaddingTop = top;
        this.originalPaddingLeft = left;
        this.originalPaddingRight = right;
        this.originalPaddingBottom = bottom;
    }

    public String getViewId() {
        return mViewId;
    }

    public void setViewId(String viewId) {
        this.mViewId = viewId;
    }

    public int getRemoveIndex() {
        return mRemoveIndex;
    }

    public void setRemoveIndex(int removeIndex) {
        this.mRemoveIndex = removeIndex;
    }
}
