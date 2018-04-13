package com.example.zhangchong.myapplication;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchong on 2018/4/10.
 */

public class DialogAnimationLayout extends FrameLayout {
    public static final int MAX_CARD_VIEW = 3;
    private int mAppearHeight = 600;
    private LayoutTransition mTransition;
    private List<View> mPackageViews = new ArrayList<>();
    private DialogLayoutListener mListener;
    private ValueAnimator mZoomAnimator;

    public interface DialogLayoutListener {
        void onAnimationEnd();
    }

    public DialogAnimationLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public DialogAnimationLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DialogAnimationLayout(@NonNull Context context, @Nullable AttributeSet attrs,
        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initLayoutTransition();
    }

    private void resetLayoutTransition() {
        clearLayoutTransition();
        mTransition = null;
        initLayoutTransition();
    }

    private void clearLayoutTransition() {
        this.setLayoutTransition(null);
    }

    public void clear() {
        clearLayoutTransition();
        mPackageViews.clear();
        removeAllViews();
    }

    private void initLayoutTransition() {
        if (mTransition != null) {
            this.setLayoutTransition(mTransition);
            return;
        }
        mTransition = new LayoutTransition();
        // 子View添加到mContainer时的动画
        mTransition.setStartDelay(LayoutTransition.APPEARING, 0);
        mTransition.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        mTransition.setAnimator(LayoutTransition.APPEARING, getAppearAnimation());

        // 子Veiw从mContainer中移除时的动画
        mTransition.setAnimator(LayoutTransition.DISAPPEARING, getDisAppearAnimation());

        mTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view,
                int transitionType) {
                stopZoomAnimation();
            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container,
                final View view, final int transitionType) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        //最后一个view截图
                        //captureLastView();

                        stopZoomAnimation();
                        mZoomAnimator = continueAnimation(view, transitionType);
                    }
                });
                if(mListener != null){
                    mListener.onAnimationEnd();
                }
            }
        });

        this.setLayoutTransition(mTransition);
    }

    private void stopZoomAnimation() {
        if (mZoomAnimator != null && mZoomAnimator.isRunning()) {
            mZoomAnimator.end();
            mZoomAnimator = null;
        }
    }

    private ValueAnimator continueAnimation(View view, int transitionType) {
        ValueAnimator animator = null;
        switch (transitionType) {
            case LayoutTransition.APPEARING:
                int viewHeight = view.getHeight();
                CardViewTag tag = new CardViewTag();
                tag.setOriginalHeight(viewHeight);
                view.setTag(tag);
                animator = CardPackageHelper.shrinkCards(DialogAnimationLayout.this, viewHeight,
                    new CardControllerListener(CardControllerListener.TYPE_ADD));
                break;
            case LayoutTransition.DISAPPEARING:
                addToFirstChild();
                if(hashCache()) {
                    animator = CardPackageHelper.magnifyCards(DialogAnimationLayout.this, new CardControllerListener(CardControllerListener.TYPE_DEL));
                }
                break;
            default:
                break;
        }
        return animator;
    }

    private Animator getAppearAnimation() {
        return ObjectAnimator.ofFloat(null, "translationY", -mAppearHeight, 0)
            .setDuration(CardPackageHelper.DURATION_LAYOUT_DISPLAY);
    }

    private Animator getDisAppearAnimation() {
        return ObjectAnimator.ofFloat(null, "translationY", 0, -mAppearHeight)
            .setDuration(CardPackageHelper.DURATION_LAYOUT_DISMISS);
    }

    public List<View> getPackageViews() {
        return mPackageViews;
    }

    public void setPackageViews(List<View> packageViews) {
        this.mPackageViews = packageViews;
    }

    public int getAppearHeight() {
        return mAppearHeight;
    }

    public void setAppearHeight(int mAppearHeight) {
        this.mAppearHeight = mAppearHeight;
    }

    public void addNewView(View view) {
        initLayoutTransition();
        int index = getChildCount();
        int paddingTop = CardPackageHelper.PADDING_STEP * index;
        int height = (int) (Math.random() * 200) + 400;
        FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        view.setPadding(0, paddingTop, 0, 0);

        //showLastViewCapture();

        //添加截图view
        //DialogCaptureLayout layout = prepareCaptureView(view);

        mPackageViews.add(view);
        addView(view, params);
    }

    private DialogCaptureLayout prepareCaptureView(View view){
        DialogCaptureLayout layout = new DialogCaptureLayout(getContext());
        layout.setContentView(view);
        return layout;
    }

    //private void showLastViewCapture(){
    //    if(mPackageViews.size() == 0)
    //        return;
    //    DialogCaptureLayout layout = mPackageViews.get(mPackageViews.size() - 1);
    //    layout.showCaptureView();
    //}
    //
    //private void captureLastView(){
    //    if(mPackageViews.size() == 0)
    //        return;
    //    DialogCaptureLayout layout = mPackageViews.get(mPackageViews.size() - 1);
    //    layout.captureChacheBitmap();
    //}

    public boolean hashCache(){
        return mPackageViews.size() > 0;
    }

    public void setListener(DialogLayoutListener listener){
        mListener = listener;
    }

    public void removeNewChild() {
        initLayoutTransition();
        if (getChildCount() > 0) {
            stopZoomAnimation();
            removeViewAt(getChildCount() - 1);
        }
        if (mPackageViews.size() > 0) {
            mPackageViews.remove(mPackageViews.size() - 1);
        }
    }

    public void resume(){

    }

    public void addToFirstChild() {
        if (mPackageViews.size() - 1 - getChildCount() < 0) {
            return;
        }
        //卡片夹有多余的卡片

        clearLayoutTransition();
        View view = mPackageViews.get(mPackageViews.size() - 1 - getChildCount());
        int left = MAX_CARD_VIEW * CardPackageHelper.PADDING_STEP;
        int right = MAX_CARD_VIEW * CardPackageHelper.PADDING_STEP;
        int bottom = 0;
        CardViewTag tag = (CardViewTag) view.getTag();
        if (tag != null) {
            bottom = tag.getOriginalPaddingBottom();
        }
        view.setPadding(left, 0, right, bottom);

        FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(view.getWidth(), view.getHeight());
        addView(view, 0, params);
    }

    public void removeFirstChild() {
        clearLayoutTransition();
        if (getChildCount() > 0) {
            stopZoomAnimation();
            removeViewAt(0);
        }
    }

    private class CardControllerListener implements CardPackageHelper.CardDisplayAnimationListener {
        //0控制添加图片.1控制去掉图片
        public static final int TYPE_ADD = 0x00;
        public static final int TYPE_DEL = 0x01;
        private int mType = TYPE_ADD;

        public CardControllerListener(int type) {
            mType = type;
        }

        @Override
        public void onAnimationEnd() {
            switch (mType) {
                case TYPE_DEL:
                    showFirstChild();
                    break;
                default:
                    removeFirstChild();
                    break;
            }
        }

        private void showFirstChild() {

        }

        private void removeFirstChild() {
            if (getChildCount() > MAX_CARD_VIEW) {
                DialogAnimationLayout.this.removeFirstChild();
            }
        }
    }
}
