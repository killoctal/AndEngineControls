package com.killoctal.andenginecontrols.detectors;

import java.util.ArrayList;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;


public class ScrollDetector extends PointerDetector
{
	public static interface IScrollDetectorListener
	{
		public void onScrollStart(float pDistanceX, float pDistanceY);
		public void onScroll(float pDistanceX, float pDistanceY);
		public void onScrollFinish(float pDistanceX, float pDistanceY);
	}
	

	/**
	 * @name Listeners
	 * @{
	 */
	final public ArrayList<IScrollDetectorListener> mScrollListeners;
	/**
	 * @}
	 */
	
	public static float DEFAULT_MIN_DISTANCE = 10;
	
	private boolean mIsScrolling;
	private boolean mNoClick;
	
	protected float mMinimumDistance;
	private boolean mCanScroll;
	protected float mCurrentX, mCurrentY;
	
	
	public ScrollDetector()
	{
		this(DEFAULT_MIN_DISTANCE);
	}
	
	public ScrollDetector(float pMinimumDistance)
	{
		this(pMinimumDistance, null);
	}
	
	
	public ScrollDetector(float pMinimumDistance, ITouchArea pTouchArea)
	{
		super(pTouchArea);
		mMinimumDistance = pMinimumDistance;
		
		mScrollListeners = new ArrayList<ScrollDetector.IScrollDetectorListener>();
		mPressX = 0;
		mPressY = 0;
		mCanScroll = false;
		mNoClick = true;
	}
	
	
	
	public boolean isScrolling()
	{
		return mIsScrolling;
	}
	
	
	
	public void setNoClickWhenScrolling(boolean pNoClick)
	{
		mNoClick = pNoClick;
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
				executeOnScrollListeners(tmpOffsetX, tmpOffsetY);
			}
			else
			{
				// Check if slide has began in a direction
				if (Math.abs(tmpOffsetX) >= mMinimumDistance || Math.abs(tmpOffsetY) >= mMinimumDistance)
				{
					mIsScrolling = true;
					
					// Executes listners (with new offsets)
					executeOnScrollStartListeners(tmpOffsetX, tmpOffsetY);
				}
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
			
			executeOnScrollFinishListeners(tmpOffsetX, tmpOffsetY);
		}
		
		if (mIsScrolling)
		{
			mIsScrolling = false;
			if (mNoClick)
			{
				setPressed(false);
			}
		}
		super.executeOnReleaseListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	
	
	
	protected void executeOnScrollStartListeners(float pOffsetX, float pOffsetY)
	{
		for(IScrollDetectorListener iListener : mScrollListeners)
			iListener.onScrollStart(pOffsetX, pOffsetY);
	}
	
	protected void executeOnScrollListeners(float pOffsetX, float pOffsetY)
	{
		for(IScrollDetectorListener iListener : mScrollListeners)
			iListener.onScroll(pOffsetX, pOffsetY);
	}
	
	protected void executeOnScrollFinishListeners(float pOffsetX, float pOffsetY)
	{
		for(IScrollDetectorListener iListener : mScrollListeners)
			iListener.onScrollFinish(pOffsetX, pOffsetY);
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

