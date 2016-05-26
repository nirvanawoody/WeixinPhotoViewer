package com.nirvanawoody.weixinphotoviewer;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by nirvanawoody on 2016/5/17.
 */
public class SmoothImageView extends PhotoView {

	public enum Status {
		STATE_NOMAL,
		STATE_IN,
		STATE_OUT
	}

	private Status mStatus = Status.STATE_NOMAL;
	private Paint mPaint;
	private int mBgColor = 0xFF000000;
	private Matrix matrix;
	private boolean transformEnabled = true;
	private Bitmap mBitmap;

	private Transform startTransform;
	private Transform endTransform;
	private Transform animTransform;
	private Rect thumbRect;
	private boolean transformStart;

	private class Transform implements Cloneable {
		float left, top, width, height;
		int alpha;
		float scale;

		public Transform clone() {
			Transform obj = null;
			try {
				obj = (Transform) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return obj;
		}
	}

	public void setTransformEnabled(boolean transformEnabled) {
		this.transformEnabled = transformEnabled;
	}

	public SmoothImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSmoothImageView();
	}

	public SmoothImageView(Context context) {
		super(context);
		initSmoothImageView();
	}

	private void initSmoothImageView() {
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(mBgColor);
		matrix = new Matrix();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getDrawable() == null) {
			return;
		}

		if (mStatus == Status.STATE_OUT || mStatus == Status.STATE_IN) {
			if (startTransform == null || endTransform == null || animTransform == null) {
				initTransform();
			}

			if (animTransform == null) {
				super.onDraw(canvas);
				return;
			}

			mPaint.setAlpha(animTransform.alpha);
			canvas.drawPaint(mPaint);
			int saveCount = canvas.getSaveCount();
			matrix.setScale(animTransform.scale, animTransform.scale);
			float translateX = -(mBitmap.getWidth() * animTransform.scale - animTransform.width) / 2;
			float translateY = -(mBitmap.getHeight() * animTransform.scale - animTransform.height) / 2;
			matrix.postTranslate(translateX, translateY);

			canvas.translate(animTransform.left, animTransform.top);
			canvas.clipRect(0, 0, animTransform.width, animTransform.height);
			canvas.concat(matrix);
			getDrawable().draw(canvas);
			canvas.restoreToCount(saveCount);

			if (transformStart) {
				startTransform();
			}
		} else {
			mPaint.setAlpha(255);
			canvas.drawPaint(mPaint);
			super.onDraw(canvas);
		}
	}

	private void startTransform() {
		transformStart = false;
		if (animTransform == null) {
			return;
		}

		ValueAnimator animator = new ValueAnimator();
		animator.setDuration(300);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		if (mStatus == Status.STATE_IN) {
			PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("animScale", startTransform.scale, endTransform.scale);
			PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("animAlpha", startTransform.alpha, endTransform.alpha);
			PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("animLeft", startTransform.left, endTransform.left);
			PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("animTop", startTransform.top, endTransform.top);
			PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("animWidth", startTransform.width, endTransform.width);
			PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("animHeight", startTransform.height, endTransform.height);
			animator.setValues(scaleHolder, alphaHolder, leftHolder, topHolder, widthHolder, heightHolder);
		} else if (mStatus == Status.STATE_OUT) {
			PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("animScale", endTransform.scale, startTransform.scale);
			PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("animAlpha", endTransform.alpha, startTransform.alpha);
			PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("animLeft", endTransform.left, startTransform.left);
			PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("animTop", endTransform.top, startTransform.top);
			PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("animWidth", endTransform.width, startTransform.width);
			PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("animHeight", endTransform.height, startTransform.height);
			animator.setValues(scaleHolder, alphaHolder, leftHolder, topHolder, widthHolder, heightHolder);
		}
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				animTransform.alpha = (Integer) animation.getAnimatedValue("animAlpha");
				animTransform.scale = (float) animation.getAnimatedValue("animScale");
				animTransform.left = (float) animation.getAnimatedValue("animLeft");
				animTransform.top = (float) animation.getAnimatedValue("animTop");
				animTransform.width = (float) animation.getAnimatedValue("animWidth");
				animTransform.height = (float) animation.getAnimatedValue("animHeight");
				invalidate();
			}
		});
		animator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
/*
				 * 如果是进入的话，当然是希望最后停留在center_crop的区域。但是如果是out的话，就不应该是center_crop的位置了
				 * ， 而应该是最后变化的位置，因为当out的时候结束时，不回复视图是Normal，要不然会有一个突然闪动回去的bug
				 */
				if (onTransformListener != null) {
					onTransformListener.onTransformCompleted(mStatus);
				}
				if (mStatus == Status.STATE_IN) {
					mStatus = Status.STATE_NOMAL;
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		animator.start();

	}

	public void transformIn(Rect thunmbRect, onTransformListener listener) {
		this.thumbRect = thunmbRect;
		setOnTransformListener(listener);
		transformStart = true;
		mStatus = Status.STATE_IN;
		invalidate();
	}

	public void transformOut(Rect thumbRect, onTransformListener listener) {
		this.thumbRect = thumbRect;
		setOnTransformListener(listener);
		transformStart = true;
		mStatus = Status.STATE_OUT;
		invalidate();
	}

	private void initTransform() {
		if (getDrawable() == null) {
			return;
		}
		if (startTransform != null && endTransform != null && animTransform != null) {
			return;
		}
		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		if (mBitmap == null) {
			mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		}

		startTransform = new Transform();
		startTransform.alpha = 0;
		startTransform.left = thumbRect.left;
		startTransform.top = thumbRect.top - getStatusBarHeight(getContext());
		startTransform.width = thumbRect.width();
		startTransform.height = thumbRect.height();

		int bitmapWidth = mBitmap.getWidth();
		int bitmapHeight = mBitmap.getHeight();

		//开始时以CenterCrop方式显示，缩放图片使图片的一边等于起始区域的一边，另一边大于起始区域
		float startScaleX = (float) thumbRect.width() / bitmapWidth;
		float startScaleY = (float) thumbRect.height() / bitmapHeight;
		startTransform.scale = startScaleX > startScaleY ? startScaleX : startScaleY;
		//结束时以fitCenter方式显示，缩放图片使图片的一边等于View的一边，另一边大于View
		float endScaleX = (float) getWidth() / bitmapWidth;
		float endScaleY = (float) getHeight() / bitmapHeight;

		endTransform = new Transform();
		endTransform.scale = endScaleX < endScaleY ? endScaleX : endScaleY;
		endTransform.alpha = 255;
		int endBitmapWidth = (int) (endTransform.scale * bitmapWidth);
		int endBitmapHeight = (int) (endTransform.scale * bitmapHeight);
		endTransform.left = (getWidth() - endBitmapWidth) / 2;
		endTransform.top = (getHeight() - endBitmapHeight) / 2;
		endTransform.width = endBitmapWidth;
		endTransform.height = endBitmapHeight;

		if (mStatus == Status.STATE_IN) {
			animTransform = startTransform.clone();
		} else if (mStatus == Status.STATE_OUT) {
			animTransform = endTransform.clone();
		}
	}

	public interface onTransformListener {
		public void onTransformCompleted(Status status);

	}

	private onTransformListener onTransformListener;


	public void setOnTransformListener(SmoothImageView.onTransformListener onTransformListener) {
		this.onTransformListener = onTransformListener;
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = context.getResources().getDimensionPixelSize(R.dimen.default_status_bar_height);
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}


}
