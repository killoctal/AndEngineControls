package com.killoctal.andenginecontrols.detectors;


import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;


public class ScrollDetector extends PointerDetector
{
	public static interface IScrollDetectorListener extends IPointerListener
	{
		public void onScrollStart(TouchEvent pSceneTouchEvent, float pScrollX, float pScrollY);
		public void onScroll(TouchEvent pSceneTouchEvent, float pScrollX, float pScrollY);
		public void onScrollFinish(TouchEvent pSceneTouchEvent, float pScrollX, float pScrollY);
	}
	
	
	public static float DEFAULT_MIN_DISTANCE = 10;
	
	public boolean mNoClickWhenScrolling = true;
	public float mMinimumDistance = DEFAULT_MIN_DISTANCE;
	
	private boolean mIsScrolling;
	private boolean mCanScroll;
	protected float mCurrentX, mCurrentY;
	
	
	public ScrollDetector(ITouchArea touchArea, IPointerListener pointer)
	{
		this(touchArea, DEFAULT_MIN_DISTANCE, pointer);
	}
	
	
	public ScrollDetector(ITouchArea pTouchArea, float pMinimumDistance, IPointerListener pointer)
	{
		super(pTouchArea, pointer);
		mMinimumDistance = pMinimumDistance;
		mPressX = 0;
		mPressY = 0;
		mCanScroll = false;
	}
	
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		// Stop event propagation if it is scrolling
		boolean wasScrolling = mIsScrolling;
		if (super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY))
		{
			if (wasScrolling)
			{
				return true;
			}
		}
		return false;
	}
	
	
	public boolean isScrolling()
	{
		return mIsScrolling;
	}
	
	
	
	@Override
	protected void executeOnPressListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		mCanScroll = true;
		
		mCurrentX = pTouchAreaLocalX;
		mCurrentY = pTouchAreaLocalY;
		
		super.executeOnPressListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
	
	
	@Override
	protected void executeOnMoveListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		if (mCanScroll)
		{
			mCurrentX = pTouchAreaLocalX;
			mCurrentY = pTouchAreaLocalY;
			
			float tmpOffsetX = mCurrentX - mPressX;
			float tmpOffsetY = mCurrentY - mPressY;
			
			// If slider already started
			if (mIsScrolling)
			{
				executeOnScrollListeners(pSceneTouchEvent, tmpOffsetX, tmpOffsetY);
			}
			// Check if slide has began in a direction
			else if (Math.abs(tmpOffsetX) >= mMinimumDistance || Math.abs(tmpOffsetY) >= mMinimumDistance)
			{
				mIsScrolling = true;
				
				// Executes listners (with new offsets)
				executeOnScrollStartListeners(pSceneTouchEvent, tmpOffsetX, tmpOffsetY);
			}
		}
		
		super.executeOnMoveListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	
	
	
	@Override
	protected void executeOnReleaseListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		if (mCanScroll)
		{
			mCanScroll = false;
			
			mCurrentX = pTouchAreaLocalX;
			mCurrentY = pTouchAreaLocalY;
			
			float tmpOffsetX = mCurrentX - mPressX;
			float tmpOffsetY = mCurrentY - mPressY;
			
			executeOnScrollFinishListeners(pSceneTouchEvent, tmpOffsetX, tmpOffsetY);
		}
		
		if (mIsScrolling)
		{
			mIsScrolling = false;
			if (mNoClickWhenScrolling)
			{
				setPressed(false);
			}
		}
		
		super.executeOnReleaseListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	
	
	
	protected void executeOnScrollStartListeners(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY)
	{
		if (mListener instanceof IScrollDetectorListener)
			((IScrollDetectorListener)mListener).onScrollStart(pSceneTouchEvent, pOffsetX, pOffsetY);
	}
	
	protected void executeOnScrollListeners(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY)
	{
		if (mListener instanceof IScrollDetectorListener)
			((IScrollDetectorListener)mListener).onScroll(pSceneTouchEvent, pOffsetX, pOffsetY);
	}
	
	protected void executeOnScrollFinishListeners(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY)
	{
		if (mListener instanceof IScrollDetectorListener)
			((IScrollDetectorListener)mListener).onScrollFinish(pSceneTouchEvent, pOffsetX, pOffsetY);
	}
	
	/*
	@Override
	protected void executeOnClickListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, long pPressDuration)
	{
		if (mIsScrolling && mNoClick)
		{
			// Intercept clicking
		}
		else
		{
			super.executeOnClickListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pPressDuration);
		}
	}*/
}

