package com.killoctal.andenginecontrols.detectors;

import java.util.ArrayList;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;

public class SlideDetector extends PointerDetector
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
	
	
	
	private float mMinimumDistance;
	private float mPressX, mPressY;
	private Direction mSlidingDirection;
	
	
	/**
	 * @name Listeners
	 * @{
	 */
	final public ArrayList<ISlideDetectorListener> mSlideListeners;
	/**
	 * @}
	 */
	
	public SlideDetector()
	{
		this(null);
	}
	
	public SlideDetector(ITouchArea pTouchArea)
	{
		super(pTouchArea);
		
		mSlideListeners = new ArrayList<SlideDetector.ISlideDetectorListener>();
		
		mPressX = 0;
		mPressY = 0;
		
		mSlidingDirection = Direction.NONE;
		
	}
	
	
	@Override
	protected void executeOnPressListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		mPressX = pTouchAreaLocalX;
		mPressY = pTouchAreaLocalY;
		
		super.executeOnPressListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
	
	@Override
	protected void executeOnMoveListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		float tmpOffsetX = mPressX - pTouchAreaLocalX;
		float tmpOffsetY = mPressY - pTouchAreaLocalY;
		
		
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
				for(ISlideDetectorListener iListener : mSlideListeners)
					iListener.onSlideStart(mSlidingDirection, tmpOffsetX, tmpOffsetY);
			}
		}
		
		super.executeOnMoveListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	
	
	@Override
	protected void executeOnReleaseListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		float tmpOffsetX = mPressX - pTouchAreaLocalX;
		float tmpOffsetY = mPressY - pTouchAreaLocalY;
		
		if (mSlidingDirection != Direction.NONE)
		{
			for(ISlideDetectorListener iListener : mSlideListeners)
				iListener.onSlideEnd(mSlidingDirection, tmpOffsetX, tmpOffsetY);
			
			// Force the offsets to zero
			mPressX = pTouchAreaLocalX;
			mPressY = pTouchAreaLocalY;
			
			mSlidingDirection = Direction.NONE;
		}
		
		super.executeOnReleaseListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	
	
}
