package com.killoctal.andenginecontrols.detectors;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;

public class ClickDetector extends PointerDetector
{
	private long mPressTime;
	
	public static interface IClickListener extends IPointerListener {
		/// Executed when pressed and released inside
		void onClick(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, long pPressDuration);
	}
	
	
	public ClickDetector(ITouchArea touchArea, IPointerListener listener)
	{
		super(touchArea, listener);
	}
	
	
	@Override
	public void setPressed(boolean pPressed)
	{
		super.setPressed(pPressed);
		if (pPressed)
		{
			mPressTime = System.currentTimeMillis();
		}
	}
	
	
	@Override
	protected void executeOnReleaseListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		super.executeOnReleaseListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
		
		// Execute click listener
		if (mIsPressed)
		{
			executeOnClickListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, System.currentTimeMillis() - mPressTime);
		}
	}
	
	protected void executeOnClickListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, long pPressDuration)
	{
		if (mListener instanceof IClickListener)
			((IClickListener)mListener).onClick(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pPressDuration);
	}
}
