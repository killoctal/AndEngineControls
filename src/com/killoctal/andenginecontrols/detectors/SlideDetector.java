package com.killoctal.andenginecontrols.detectors;

import org.andengine.entity.scene.ITouchArea;

public class SlideDetector extends ScrollDetector
{
	public enum Direction
	{
		LEFT,
		TOP,
		RIGHT,
		BOTTOM,
		NONE
	}
	
	public static interface ISlideDetectorListener
	{
		public void onSlideStart(Direction pDirection, float pOffsetX, float pOffsetY);
		public void onSlide(Direction pDirection, float pOffsetX, float pOffsetY);
		public void onSlideEnd(Direction pDirection, float pOffsetX, float pOffsetY);
	}
	
	
	
	private Direction mSlidingDirection;
	
	
	/**
	 * @name Listeners
	 * @{
	 */
	public ISlideDetectorListener mSlideListener;
	/**
	 * @}
	 */
	
	public SlideDetector()
	{
		this(DEFAULT_MIN_DISTANCE);
	}
	
	public SlideDetector(float pMinimumDistance)
	{
		this(pMinimumDistance, null);
	}
	
	public SlideDetector(float pMinimumDistance, ITouchArea pTouchArea)
	{
		super(pMinimumDistance, pTouchArea);
		
		mSlideListener = null;
		
		mSlidingDirection = Direction.NONE;
		
	}
	
	
	public Direction getDirection()
	{
		return mSlidingDirection;
	}
	
	
	@Override
	protected void executeOnScrollStartListeners(float pOffsetX, float pOffsetY)
	{
		// Check if slide has began in a direction
		if (pOffsetX <= - mMinimumDistance)
		{
			mSlidingDirection = Direction.LEFT;
		}
		else if (pOffsetX >= mMinimumDistance)
		{
			mSlidingDirection = Direction.RIGHT;
		}
		else if (pOffsetY <= - mMinimumDistance)
		{
			mSlidingDirection = Direction.TOP;
		}
		else if (pOffsetY >= mMinimumDistance)
		{
			mSlidingDirection = Direction.BOTTOM;
		}
		
		// If directiopn changed => slide start
		if (mSlidingDirection != Direction.NONE)
		{
			if (mSlideListener != null)
				mSlideListener.onSlideStart(mSlidingDirection, pOffsetX, pOffsetY);
		}
	}
	
	
	@Override
	protected void executeOnScrollListeners(float pOffsetX, float pOffsetY)
	{
		super.executeOnScrollListeners(pOffsetX, pOffsetY);
		
		if (mSlideListener != null)
			mSlideListener.onSlide(mSlidingDirection, pOffsetX, pOffsetY);
	}
	
	
	@Override
	protected void executeOnScrollFinishListeners(float pOffsetX, float pOffsetY)
	{
		super.executeOnScrollFinishListeners(pOffsetX, pOffsetY);
		
		if (mSlidingDirection != Direction.NONE)
		{
			if (mSlideListener != null)
				mSlideListener.onSlideEnd(mSlidingDirection, pOffsetX, pOffsetY);
			
			mSlidingDirection = Direction.NONE;
		}
	}
	
	
	/*
	@Override
	protected void executeOnMoveListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		if (mCanSlide)
		{
			mCurrentX = pTouchAreaLocalX;
			mCurrentY = pTouchAreaLocalY;
			
			float tmpOffsetX = mCurrentX - mPressX;
			float tmpOffsetY = mCurrentY - mPressY;
			
			// If slider already started
			if (mSlidingDirection != Direction.NONE)
			{
				for(ISlideDetectorListener iListener : mSlideListeners)
					iListener.onSlide(mSlidingDirection, tmpOffsetX, tmpOffsetY);
			}
			else
			{
				// Check if slide has began in a direction
				if (tmpOffsetX < - mMinimumDistance)
				{
					mSlidingDirection = Direction.LEFT;
				}
				else if (tmpOffsetX > mMinimumDistance)
				{
					mSlidingDirection = Direction.RIGHT;
				}
				else if (tmpOffsetY < - mMinimumDistance)
				{
					mSlidingDirection = Direction.TOP;
				}
				else if (tmpOffsetY > mMinimumDistance)
				{
					mSlidingDirection = Direction.BOTTOM;
				}
				
				// If directiopn changed => slide start
				if (mSlidingDirection != Direction.NONE)
				{
					// Replace the press position by the slide start position
					mPressX = pTouchAreaLocalX;
					mPressY = pTouchAreaLocalY;
					
					for(ISlideDetectorListener iListener : mSlideListeners)
						iListener.onSlideStart(mSlidingDirection, mCurrentX - mPressX, mCurrentY - mPressY);
				}
			}
		}
		
		super.executeOnMoveListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	*/
	/*
	@Override
	protected void executeOnReleaseListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		if (mSlidingDirection != Direction.NONE)
		{
			float tmpOffsetX = mPressX - mCurrentX;
			float tmpOffsetY = mPressY - mCurrentY;
			
			for(ISlideDetectorListener iListener : mSlideListeners)
				iListener.onSlideEnd(mSlidingDirection, tmpOffsetX, tmpOffsetY);
			
			mSlidingDirection = Direction.NONE;
		}
		
		super.executeOnReleaseListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	
	*/
	
}
