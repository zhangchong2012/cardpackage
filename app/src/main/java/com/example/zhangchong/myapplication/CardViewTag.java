package com.example.zhangchong.myapplication;

/**
 * Created by zhangchong on 2018/4/10.
 */

public class CardViewTag {
    private String viewId;

    //最初的高度
    private int originalHeight;

    private int originalPaddingBottom = -1;
    private int originalPaddingRight = -1;
    private int originalPaddingLeft = -1;
    private int originalPaddingTop = -1;

    public CardViewTag(String viewId) {
        this.viewId = viewId;
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

    public void setOriginalPadding(int left, int top, int right, int bottom) {
        this.originalPaddingTop = top;
        this.originalPaddingLeft = left;
        this.originalPaddingRight = right;
        this.originalPaddingBottom = bottom;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }
}
