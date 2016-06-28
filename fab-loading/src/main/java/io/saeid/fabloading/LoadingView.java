package io.saeid.fabloading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.saeid.supportanimator.Animator;
import io.saeid.supportanimator.animator.SupportAnimator;
import io.saeid.supportanimator.listener.SupportAnimatorListener;

/**
 * A loading animation based on FAB concept in Material Design.
 * <p>
 * Inspired by: http://www.materialup.com/posts/marvel-avengers-loading-animation
 * </p>
 *
 * @author Saeed Masoumi
 */
public class LoadingView extends FloatingActionButton implements SupportAnimatorListener {

    // Animation effects
    public static final int FROM_LEFT = 0;
    public static final int FROM_TOP = 1;
    public static final int FROM_RIGHT = 2;
    public static final int FROM_BOTTOM = 3;
    private static final boolean PRE_LOLLIPOP = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    //settable by the xml
    private boolean mLoadingOnClick = false;
    private int mDuration = 0; //default is 500 millis
    private int mRepeats = 1; // default is 1
    //stores all loading items
    private SparseArray<LoadingItem> mItems = new SparseArray<>();
    private boolean mRunning = false;
    private boolean mPaused = false;
    private SupportAnimator mAnimator;
    //Counter for mRepeats
    private int mRepeatCounter = 1;
    // Storing position of current animation.
    private int mCurrentAnimationPos = 1;
    // Current running animation item
    private LoadingItem mCurrentLoadingItem;
    // Current running circle
    private Circle mCircle = new Circle();
    // The value of position change in each frame
    private float mStep;
    // A path for FloatingActionButton
    private Path mPath = new Path();
    // A path for drawable of current animation
    private Path mDrawablePath;
    private Paint mPaint = new Paint();
    // Offset between FAB and real layout bounds (non-zero in pre-lollipop)
    private float mOffset = 0;
    // Storing bounds for mDrawable
    private Rect mBounds;
    // Callback listener
    private LoadingListener mListener;
    private Handler mHandler = new Handler();

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, 0, 0);
        if (attr != null) {
            try {
                mLoadingOnClick = attr.getBoolean(R.styleable.LoadingView_mfl_onclickLoading, false);
                mDuration = attr.getInt(R.styleable.LoadingView_mfl_duration, 500);
                mRepeats = attr.getInt(R.styleable.LoadingView_mfl_repeat, 1);
                mRepeatCounter = mRepeats;
            } finally {
                attr.recycle();
            }
        }
        mPaint.setAntiAlias(true);//make circle smoother
        if (mLoadingOnClick) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAnimation();
                }
            });
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    /**
     * If animation was not running then add an animation to animations' queue.
     *
     * @param color    Background color for loading animation.
     * @param drawable Drawable for loading animation.
     * @param type     Type of animation.
     * @return this
     */
    public LoadingView addAnimation(int color, int drawable, @AnimationMode int type) {
        if (!mRunning) {
            //set first item as default item
            if (mItems.size() == 0) {
                if (drawable != 0) setImageDrawable(getResources().getDrawable(drawable));
                setColorNormal(color);
                setColorPressed(color);
                setColorRipple(Color.TRANSPARENT);
            }
            mItems.put(mItems.size(), new LoadingItem(color, drawable, type));
        }
        return this;
    }

    /**
     * Removes an animation item from animations' queue.
     *
     * @param position given position.
     * @return this.
     */
    public LoadingView removeAnimation(int position) {
        if (!mRunning) {
            mItems.remove(position);
        }
        return this;
    }

    /**
     * Starts a loading animation if there is no running animation.
     * <p>
     * If animation is paused {@link #resumeAnimation()} should be called before {@link
     * #startAnimation()}
     */
    public void startAnimation() {
        if (!mRunning && mItems.size() > 1 && !mPaused) {
            //if (mAnimator == null || mAnimator.get() == null)
            mAnimator = Animator.with(this, mDuration);
            mAnimator.start();
        }
    }

    public void resumeAnimation() {
        mPaused = false;
    }

    public void pauseAnimation() {
        mPaused = true;
    }

    /**
     * Adds a listener for getting callback from start/end of animation.
     *
     * @param listener given listener.
     */
    public void addListener(LoadingListener listener) {
        mListener = listener;
    }

    @Deprecated
    @Override
    public void onSupportAnimationAttach() {

    }

    @Override
    public void onSupportAnimationStart() {
        mRunning = true;
        mCurrentLoadingItem = mItems.get(mCurrentAnimationPos);
        mCircle.restore();
        obtainCircle();
        mStep = (mCircle.radius * 2) / (mDuration / 1000f);
        if (mCurrentLoadingItem.drawableRes != 0) {
            mBounds = new Rect(0, 0, getWidth(), getHeight());

            if (mCurrentLoadingItem.drawable == null) {
                mCurrentLoadingItem.drawable =
                        getResources().getDrawable(mCurrentLoadingItem.drawableRes);//cache drawable
            }
            if (mCurrentLoadingItem.drawable != null) {
                mCurrentLoadingItem.drawable.setBounds(mBounds);
            }
        }
        if (mListener != null) mListener.onAnimationStart(mCurrentAnimationPos);
    }

    private void obtainCircle() {
        mOffset = 0;

        //There is a bounding problem in pre-lollipop
        if (PRE_LOLLIPOP) {
            ViewGroup.MarginLayoutParams params = ((ViewGroup.MarginLayoutParams) getLayoutParams());
            mOffset = Math.abs(params.leftMargin + params.rightMargin) * 0.5f;
        }
        float halfWidth = getWidth() * 0.5f;
        float halfHeight = getHeight() * 0.5f;

        switch (mCurrentLoadingItem.type) {
            case FROM_LEFT:
                mCircle.x = -halfWidth + mOffset;
                mCircle.y = halfHeight;
                break;
            case FROM_TOP:
                mCircle.x = halfWidth;
                mCircle.y = -halfHeight + mOffset;
                break;
            case FROM_RIGHT:
                mCircle.x = halfWidth + getWidth() - mOffset;
                mCircle.y = halfHeight;
                break;
            case FROM_BOTTOM:
                mCircle.x = halfWidth;
                mCircle.y = halfHeight + getHeight() - mOffset;
                break;
        }
        mCircle.radius = Math.max(halfWidth, halfHeight) - mOffset + 8;
        mPath.addCircle(getWidth() * 0.5f, getHeight() * 0.5f, mCircle.radius - 7, Path.Direction.CW);
        mPaint.setColor(mCurrentLoadingItem.color);
    }

    @Override
    public void onSupportAnimationEnd() {
        mRunning = false;
        mCurrentAnimationPos = (mCurrentAnimationPos + 1) % mItems.size();
        setColorNormal(mCurrentLoadingItem.color);
        setColorPressed(mCurrentLoadingItem.color);
        if (mCurrentLoadingItem.drawable != null) {
            setImageDrawable(mCurrentLoadingItem.drawable);
        }
        if (mRepeatCounter > 1) {
            mRepeatCounter--;
            if (mListener != null) mListener.onAnimationRepeat(mCurrentAnimationPos);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnimation();
                }
            }, 50);//wait for some frames then call startAnimation //TODO better implementation
        } else if (mRepeatCounter == 1 && mRepeats > 1) {
            mRepeatCounter = mRepeats;
            if (mListener != null) mListener.onAnimationRepeat(mCurrentAnimationPos);
        } else if (mListener != null) mListener.onAnimationEnd(mCurrentAnimationPos);
    }

    @Override
    public void onSupportAnimationCancel() {
        onSupportAnimationEnd();
    }

    @Override
    public void onSupportAnimationUpdate() {
        moveCircle();
        attachDrawablePath();
        invalidate();// causes draw method to be called.
    }

    /**
     * Moves {@link #mCircle}.
     */
    private void moveCircle() {
        float step = mStep * 0.01666f; //0.01666 equals to 60 fps
        switch (mCurrentLoadingItem.type) {
            case FROM_LEFT:
                if (mCircle.x < getWidth() * 0.5f) mCircle.x += step;
                break;
            case FROM_TOP:
                if (mCircle.y < getHeight() * 0.5f) mCircle.y += step;
                break;
            case FROM_RIGHT:
                if (mCircle.x > getWidth() * 0.5f) mCircle.x -= step;
                break;
            case FROM_BOTTOM:
                if (mCircle.y > getHeight() * 0.5f) mCircle.y -= step;
                break;
        }
    }

    /**
     * If drawable is available for current loading animation then add a circular path for clipping
     * with canvas.
     */
    private void attachDrawablePath() {
        if (mCurrentLoadingItem.drawableRes != 0) {
            mDrawablePath = new Path();
            mDrawablePath.addCircle(mCircle.x, mCircle.y, mCircle.radius, Path.Direction.CW);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mRunning) {

            canvas.clipPath(mPath);
            int count = canvas.getSaveCount();
            canvas.drawCircle(mCircle.x, mCircle.y, mCircle.radius, mPaint);
            canvas.save();

            if (mCurrentLoadingItem.drawable != null) {
                canvas.clipPath(mDrawablePath);
                mCurrentLoadingItem.drawable.draw(canvas);
            }

            canvas.restoreToCount(count);
        }
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
        mAnimator = Animator.with(this, duration);
    }

    public int getRepeat() {
        return mRepeats;
    }

    public void setRepeat(int repeat) {
        mRepeatCounter = repeat;
        mRepeats = repeat;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FROM_LEFT, FROM_TOP, FROM_RIGHT, FROM_BOTTOM})
    public @interface AnimationMode {
    }

    /**
     * Callback listener for animations while they're starting/ending/repeating
     */
    public interface LoadingListener {
        void onAnimationStart(int currentItemPosition);

        void onAnimationRepeat(int nextItemPosition);

        void onAnimationEnd(int nextItemPosition);
    }

    public static class LoadingItem {
        public int color;
        public int drawableRes;
        public Drawable drawable = null;
        public
        @AnimationMode
        int type;

        public LoadingItem(int color, int drawableRes, @AnimationMode int type) {
            this.color = color;
            this.drawableRes = drawableRes;
            this.type = type;
        }
    }
}
