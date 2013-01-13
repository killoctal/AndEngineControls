package com.killoctal.andenginecontrols.camera;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import com.killoctal.andenginecontrols.detectors.ScrollDetector;
import com.killoctal.andenginecontrols.utils.ACMaths;

public class SmoothCameraScroll implements ScrollDetector.IScrollDetectorListener
{
	/**
	 * @name Editable properties
	 * @{
	 */
	/// Factor limit min (zoom back)
	//public float FACTOR_MIN = 0;
	
	/// Factor limit max (zoom before)
	//public float FACTOR_MAX = Float.MAX_VALUE;
	
	/// Smooth speed (more = reactive, less = slow)
	public float SPEED = 15;
	/**
	 * @}
	 */
	private ScrollDetector mScrollDetector;
	
	
	private float mOffsetX, mOffsetY;
	
	/// The camera
	final private ZoomCamera mCamera;
	
	public SmoothCameraScroll(Scene pScene, ZoomCamera pCam)
	{
		mCamera = pCam;
		
		// Creates the pinch detector
		mScrollDetector = new ScrollDetector();
		mScrollDetector.mScrollListeners.add(this);
		
		// Init the target
		mOffsetX = mOffsetY = 0;
		
		// Smooth management is made with an update handler 
		pScene.registerUpdateHandler( new IUpdateHandler(){
			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				if (mOffsetX != 0 || mOffsetY != 0)
				{
					mCamera.offsetCenter(mOffsetX, mOffsetY);
					
					mOffsetX = ACMaths.smoothValue(0, mOffsetX, SPEED, 4);
					mOffsetY = ACMaths.smoothValue(0, mOffsetY, SPEED, 4);
				}
			}
			@Override
			public void reset(){}
		});
		
		
	}
	
	
	final public ScrollDetector getDetector()
	{
		return mScrollDetector;
	}
	
	
	@Override
	public void onScrollStart(float pDistanceX, float pDistanceY)
	{
		onScroll(pDistanceX, pDistanceY);
	}
	
	
	
	@Override
	public void onScroll(float pDistanceX, float pDistanceY)
	{
		final float zoomFactor = mCamera.getZoomFactor();
		
		mOffsetX = -zoomFactor * pDistanceX;
		mOffsetY = -zoomFactor * pDistanceY;
	}
	
	
	
	@Override
	public void onScrollFinish(float pDistanceX, float pDistanceY)
	{
		onScroll(pDistanceX, pDistanceY);
	}
}
