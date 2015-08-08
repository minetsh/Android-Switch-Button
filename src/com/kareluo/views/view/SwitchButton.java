package com.kareluo.views.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.kareluo.views.R;

public class SwitchButton extends View implements OnClickListener {

	private Bitmap mSlide, mState, mFrame, mDisabledState, mDisabledSlide;
	private Paint mPaint;
	private float mWidth, mSlideRadius;
	private Rect mStateLeftSrc, mStateLeftDst, mStateRightSrc, mStateRightDst;
	private float mSlideOffset = 0;
	private boolean mChecked = false;
	private int mStateWidth, mStateHeight;
	private float mSlideRange;
	private OnCheckedChangeListener mCheckedListener;
	private float mSlideDownX = 0f;
	private boolean mIsClickEvent;
	private static final float MIN = 2;
	private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initialize(attrs);
	}

	public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(attrs);
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(attrs);
	}

	public SwitchButton(Context context) {
		super(context);
	}

	private void initialize(AttributeSet attrs) {
		mSlide = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_slider_normal);
		boolean checked = attrs.getAttributeBooleanValue(NAMESPACE, "checked",
				false);
		

		mChecked = checked;

		mState = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_state_normal);
		mFrame = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_frame);
		mPaint = new Paint();
		mStateLeftSrc = new Rect();
		mStateLeftDst = new Rect();
		mStateRightSrc = new Rect();
		mStateRightDst = new Rect();

		mWidth = mFrame.getWidth();
		mSlideRadius = mWidth - mState.getWidth() / 2;
		mStateWidth = mState.getWidth();
		mStateHeight = mState.getHeight();
		mSlideRange = mWidth - 2 * mSlideRadius;
		if (mChecked) {
			mSlideOffset = -mSlideRange;
		}
		setOnClickListener(this);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mFrame.getWidth(), mFrame.getHeight());
		calc();
	}

	private void calc() {
		int midx = (int) (mSlideOffset + mWidth - mSlideRadius);
		mStateLeftSrc.set(0, 0, midx, mStateHeight);
		mStateLeftDst.set(0, 0, midx, mStateHeight);

		mStateRightSrc.set((int) (mStateWidth - mWidth + midx), 0, mStateWidth,
				mStateHeight);
		mStateRightDst.set(midx, 0, (int) mWidth, mStateHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mFrame, 0, 0, mPaint);
		if (isEnabled()) {
			Log.i("asdasd","Enable");
			canvas.drawBitmap(mState, mStateLeftSrc, mStateLeftDst, mPaint);
			canvas.drawBitmap(mState, mStateRightSrc, mStateRightDst, mPaint);
			canvas.drawBitmap(mSlide, mSlideOffset, 0, mPaint);
		} else {
			Log.i("asdasd","Disable");
			canvas.drawBitmap(getDisabledState(), mStateLeftSrc, mStateLeftDst,
					mPaint);
			canvas.drawBitmap(getDisabledState(), mStateRightSrc,
					mStateRightDst, mPaint);
			canvas.drawBitmap(getDisabledSlide(), mSlideOffset, 0, mPaint);
		}
	}

	private Bitmap getDisabledSlide() {
		if (mDisabledSlide == null) {
			mDisabledSlide = BitmapFactory.decodeResource(getResources(),
					R.drawable.switch_slider_disable);
		}
		return mDisabledSlide;
	}

	private Bitmap getDisabledState() {
		if (mDisabledState == null) {
			mDisabledState = BitmapFactory.decodeResource(getResources(),
					R.drawable.switch_state_disable);
		}
		return mDisabledState;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled())
			return true;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsClickEvent = true;
			mSlideDownX = mSlideOffset - event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			float x = event.getX();
			if (Math.abs(mSlideOffset - mSlideDownX - x) <= MIN) {
				break;
			}
			mIsClickEvent = false;
			mSlideOffset = x + mSlideDownX;
			if (mSlideOffset < -mSlideRange) {
				mSlideOffset = -mSlideRange;
			}
			if (mSlideOffset > 0) {
				mSlideOffset = 0;
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			if (mIsClickEvent) {
				performClick();
			} else {
				loosenAnimation();
			}
			break;
		}
		invalidate();
		return true;
	}

	@Override
	public void draw(Canvas canvas) {
		calc();
		super.draw(canvas);
	}

	private void loosenAnimation() {
		if (mSlideOffset >= -mSlideRange / 2) {
			if (mChecked) {
				mChecked = false;
				onStateChange();
			}
			performAnim(mSlideOffset, 0);
			mChecked = false;
		} else {
			if (!mChecked) {
				mChecked = true;
				onStateChange();
			}
			performAnim(mSlideOffset, -mSlideRange);
			mChecked = true;
		}
	}

	private void onStateChange() {
		if (mCheckedListener != null) {
			mCheckedListener.onCheckedChange(this, mChecked);
		}
	}

	private void performAnim(float from, float to) {
		ValueAnimator animator = ValueAnimator.ofFloat(from, to);
		animator.setDuration(400);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mSlideOffset = (Float) animation.getAnimatedValue();
				invalidate();
			}

		});
		animator.start();
	}

	@Override
	public void onClick(View v) {
		mChecked = !mChecked;
		onStateChange();
		performAnim(mChecked ? 0 : -mSlideRange, mChecked ? -mSlideRange : 0);
	}

	public void setOnCheckedListener(OnCheckedChangeListener l) {
		mCheckedListener = l;
	}

	public static interface OnCheckedChangeListener {
		public void onCheckedChange(SwitchButton view, boolean isChecked);
	}
}
