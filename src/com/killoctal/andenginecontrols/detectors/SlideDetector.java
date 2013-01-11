package com.killoctal.andenginecontrols.detectors;

import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.BaseDetector;

public class SlideDetector extends BaseDetector
{
	private float mMinimumDistance;
	
	public SlideDetector()
	{
		
	}
	
	@Override
	public void reset(){
		// TODO Stub de la méthode généré automatiquement
	}

	@Override
	protected boolean onManagedTouchEvent(TouchEvent pSceneTouchEvent)
	{
		return false;
	}
	
	
	
	public static interface ISlideDetectorListener
	{
		//public void onSlide(float)
	}
}
