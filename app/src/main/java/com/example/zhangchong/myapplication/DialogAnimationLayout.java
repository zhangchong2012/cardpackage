package com.example.zhangchong.myapplication;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
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
            public void endTransition(final LayoutTransition transition, ViewGroup container,
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
                if (mListener != null) {
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
        CardViewTag tag = (CardViewTag) view.getTag();

        switch (transitionType) {
            case LayoutTransition.APPEARING:
                int viewHeight = view.getHeight();
                if (tag == null) {
                    tag = new CardViewTag();
                }
                tag.setOriginalHeight(viewHeight);
                view.setTag(tag);
                animator = CardPackageHelper.shrinkCards(DialogAnimationLayout.this, viewHeight,
                    new CardControllerListener(CardControllerListener.TYPE_ADD));
                break;
            case LayoutTransition.DISAPPEARING:
                int removeIndex = tag.getRemoveIndex();
                if(tag  == null
                    //条件1:多余3张卡片的时候删除.
                    || (removeIndex == MAX_CARD_VIEW - 1 && mPackageViews.size() >= MAX_CARD_VIEW)
                    //条件2:不足3张卡片的时候删除
                    || removeIndex == mPackageViews.size()){
                    animator = continueRemoveNewView();
                }else{
                    animator = continueRemoveOtherView(removeIndex);
                }
                break;
            default:
                break;
        }
        return animator;
    }


    private ValueAnimator continueRemoveOtherView(int max){
        addToFirstChild();
        ValueAnimator animator = null;
        if (hashCache()) {
            animator = CardPackageHelper.magnifyCards(DialogAnimationLayout.this, max,
                new CardControllerListener(CardControllerListener.TYPE_DEL));
        }
        return animator;
    }

    //删除最新的view,添加最后一个view
    private ValueAnimator continueRemoveNewView(){
        addToFirstChild();
        ValueAnimator animator = null;
        if (hashCache()) {
            animator = CardPackageHelper.magnifyCards(DialogAnimationLayout.this, getChildCount(),
                new CardControllerListener(CardControllerListener.TYPE_DEL));
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
        //最大的top就是(卡片数量-1)*step
        index = index < MAX_CARD_VIEW - 1? index : MAX_CARD_VIEW -1;
        int paddingTop = CardPackageHelper.PADDING_STEP * index;
        int height = (int) (Math.random() * 200) + 400;
        FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        //       DialogCaptureLayout layout = prepareCaptureView(view);
        view.setPadding(0, paddingTop, 0, 0);

        //将当前最后一个页面的截图显示
        //       captureLastView();
        //       showLastViewCapture();

        //添加截图view
        mPackageViews.add(view);
        addView(view, params);
    }

    private boolean isCurrentInDisplay(int index) {
        if (index < 0){
            return false;
        }
        //如果当前展示不足三张卡片
        if (mPackageViews.size() <= MAX_CARD_VIEW && index < mPackageViews.size()) {
            return true;
        } else if (index > mPackageViews.size() - 1 - MAX_CARD_VIEW) {
            return true;
        }
        return false;
    }

    public void removeChildAtIndex(int index) {
        //如果正在展示
        if (isCurrentInDisplay(index)) {
            // 最后一张卡片
            if (index == mPackageViews.size() - 1) {
                removeNewChild();
            } else {
                initLayoutTransition();
                if (getChildCount() > 0) {
                    stopZoomAnimation();
                    if (index >= 0 && index < mPackageViews.size()) {
                        int displayIndex = mPackageViews.size() - index - 1;
                        View view = mPackageViews.remove(index);
                        removeViewWithTag(view, displayIndex);
                    }
                }
            }
        } else if (index >= 0 && index < mPackageViews.size()) {
            //如果不在展示
            mPackageViews.remove(index);
        }
    }

    private DialogCaptureLayout prepareCaptureView(View view){
        DialogCaptureLayout layout = new DialogCaptureLayout(getContext());
        layout.setContentView(view);
        return layout;
    }

    //插入新卡片之前,截图最前面的view.
    private void showLastViewCapture() {
        if (mPackageViews.size() == 0) {
            return;
        }
        //DialogCaptureLayout layout = mPackageViews.get(mPackageViews.size() - 1);
        //layout.showCaptureView();
    }

    private void captureLastView() {
        if (mPackageViews.size() == 0){
            return;
        }
        //DialogCaptureLayout layout = mPackageViews.get(mPackageViews.size() - 1);
        //layout.captureChacheBitmap();
    }

    public boolean hashCache() {
        return mPackageViews.size() > 0;
    }

    public void setListener(DialogLayoutListener listener) {
        mListener = listener;
    }

    public void removeNewChild() {
        initLayoutTransition();
        if (getChildCount() > 0) {
            stopZoomAnimation();
            if (mPackageViews.size() > 0) {
                View view = mPackageViews.remove(mPackageViews.size() - 1);
                removeViewWithTag(view, getChildCount() - 1);
            }
        }
    }

    /**
     * 删除view
     * @param view
     * @param displayIndex: view在parent的位置.
     */
    private void removeViewWithTag(View view, int displayIndex) {
        if (view == null){
            return;
        }

        CardViewTag tag = (CardViewTag)view.getTag();
        if(tag == null){
            tag = new CardViewTag();
        }
        tag.setRemoveIndex(displayIndex);
        removeView(view);
    }

    private void showLastViewContent() {
        if (mPackageViews.size() == 0) {
            return;
        }

        //DialogCaptureLayout layout = mPackageViews.get(mPackageViews.size() - 1);
        //layout.showContentView();
    }

    public void resume() {

    }

    //从卡片包中取出一个view添加到展示页面
    public void addToFirstChild() {
        //不足不添加
        if (mPackageViews.size() < MAX_CARD_VIEW) return;

        int index = mPackageViews.size() - 1 - getChildCount();
        Log.e("msg", "get view inex:"
            + index
            + ", total view: "
            + mPackageViews.size()
            + ", last View Is :"
            + getChildCount());
        if (index < 0) {
            return;
        }
        //卡片夹有多余的卡片

        clearLayoutTransition();
        View view = mPackageViews.get(index);
        int left = MAX_CARD_VIEW * CardPackageHelper.PADDING_STEP;
        int right = MAX_CARD_VIEW * CardPackageHelper.PADDING_STEP;
        view.setPadding(left, 0, right, 0);

        FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(view.getWidth(), view.getHeight());
        addView(view, 0, params);
    }

    public void removeFirstChild() {
        clearLayoutTransition();
        if (getChildCount() > 0) {
            stopZoomAnimation();
            removeViewWithTag(getChildAt(0), 0);
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
                    onRemoveAnimationEnd();
                    break;
                default:
                    onAddAnimationEnd();
                    break;
            }
        }

        private void onRemoveAnimationEnd() {
            //showLastViewContent();
        }

        private void onAddAnimationEnd() {
            if (getChildCount() > MAX_CARD_VIEW) {
                DialogAnimationLayout.this.removeFirstChild();
            }
        }
    }
}
