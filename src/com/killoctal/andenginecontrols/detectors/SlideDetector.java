package com.killoctal.andenginecontrols.detectors;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;

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
		public void onSlideStart(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY, Direction pDirection);
		public void onSlide(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY, Direction pDirection);
		public void onSlideEnd(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY, Direction pDirection);
	}
	
	
	
	private Direction mSlidingDirection;
	private float mFixedPos;
	
	
	/**
	 * @name Listeners
	 * @{
	 */
	public ISlideDetectorListener mSlideListener;
	/**
	 * @}
	 */
	
	public SlideDetector(ITouchArea touchArea, IPointerListener listener)
	{
		this(touchArea, DEFAULT_MIN_DISTANCE, listener);
	}
	
	public SlideDetector(ITouchArea pTouchArea, float pMinimumDistance, IPointerListener listener)
	{
		super(pTouchArea, pMinimumDistance, listener);
		
		mSlideListener = null;
		
		mFixedPos = 0;
		mSlidingDirection = Direction.NONE;
	}
	
	
	public Direction getDirection()
	{
		return mSlidingDirection;
	}
	
	
	@Override
	protected void executeOnScrollStartListeners(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY)
	{
		// Check if slide has began in a direction
		if (pOffsetX <= - mMinimumDistance)
		{
			mSlidingDirection = Direction.LEFT;
			mFixedPos = pOffsetY;
		}
		else if (pOffsetX >= mMinimumDistance)
		{
			mSlidingDirection = Direction.RIGHT;
			mFixedPos = pOffsetY;
		}
		else if (pOffsetY <= - mMinimumDistance)
		{
			mSlidingDirection = Direction.TOP;
			mFixedPos = pOffsetX;
		}
		else if (pOffsetY >= mMinimumDistance)
		{
			mSlidingDirection = Direction.BOTTOM;
			mFixedPos = pOffsetX;
		}
		
		// If directiopn changed => slide start
		if (mSlidingDirection != Direction.NONE)
		{
			if (mSlideListener != null)
				mSlideListener.onSlideStart(pSceneTouchEvent, pOffsetX, pOffsetY, mSlidingDirection);
		}
	}
	
	
	
	@Override
	protected void executeOnScrollListeners(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY)
	{
		super.executeOnScrollListeners(pSceneTouchEvent, pOffsetX, pOffsetY);
		
		if (mSlideListener != null)
		{
			mSlideListener.onSlide(pSceneTouchEvent,
					(mSlidingDirection != Direction.TOP && mSlidingDirection != Direction.BOTTOM) ? pOffsetX : mFixedPos,
					(mSlidingDirection != Direction.LEFT && mSlidingDirection != Direction.RIGHT) ? pOffsetY : mFixedPos,
					mSlidingDirection);
		}
	}
	
	
	@Override
	protected void executeOnScrollFinishListeners(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY)
	{
		super.executeOnScrollFinishListeners(pSceneTouchEvent, pOffsetX, pOffsetY);
		
		if (mSlidingDirection != Direction.NONE)
		{
			if (mSlideListener != null)
				mSlideListener.onSlideEnd(pSceneTouchEvent, pOffsetX, pOffsetY, mSlidingDirection);
			
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
