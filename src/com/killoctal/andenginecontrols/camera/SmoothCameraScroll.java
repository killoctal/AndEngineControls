package com.killoctal.andenginecontrols.camera;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
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
	public float SPEED = 15, TFACT = 8;
	/**
	 * @}
	 */
	final private ScrollDetector mScrollDetector;
	
	
	protected float mOffsetX, mOffsetY;
	
	/// Camera borders
	protected float
		mMinX = Float.MIN_VALUE,
		mMaxX = Float.MAX_VALUE,
		mMinY = Float.MIN_VALUE,
		mMaxY = Float.MAX_VALUE;
	
	/// The camera
	final private ZoomCamera mCamera;
	
	public SmoothCameraScroll(Scene pScene, ZoomCamera pCam)
	{
		mCamera = pCam;
		
		// Creates the pinch detector
		mScrollDetector = new ScrollDetector(null, SmoothCameraScroll.this) {
			/**
			 * @brief Prevents camera shaking on scroll start
			 */
			@Override
			protected void executeOnScrollStartListeners(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY)
			{
				mPressX = mCurrentX;
				mPressY = mCurrentY;
				super.executeOnScrollStartListeners(pSceneTouchEvent, 0, 0);
			}
		};
		
		// Init the target
		mOffsetX = mOffsetY = 0;
		
		// Smooth management is made with an update handler 
		pScene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				if ((int)mOffsetX != 0 || (int)mOffsetY != 0)
				{
					if (checkBorders())
					{
					
						mCamera.offsetCenter(mOffsetX, mOffsetY);
						
						mOffsetX = ACMaths.smoothValue(0, mOffsetX, SPEED, 8);
						mOffsetY = ACMaths.smoothValue(0, mOffsetY, SPEED, 8);
					}
				}
			}
			@Override
			public void reset(){}
		});
	}
	
	
	public void setBorders(float pMinX, float pMaxX, float pMinY, float pMaxY)
	{
		mMinX = pMinX;
		mMaxX = pMaxX;
		mMinY = pMinY;
		mMaxY = pMaxY;
	}
	
	protected boolean checkBorders()
	{
		/* ## disabled because does not works properly ##
		float correctX = 0, correctY = 0;
		
		if (mCamera.getXMax() > mMaxX)
		{
			correctX = mMaxX - mCamera.getXMax();
		}
		else if (mCamera.getXMin() < mMinX)
		{
			correctX = mMinX - mCamera.getXMin();
		}
		
		if (mCamera.getYMax() > mMaxY)
		{
			correctY = mMaxY - mCamera.getYMax();
		}
		else if (mCamera.getYMin() < mMinY)
		{
			correctY = mMinY - mCamera.getYMin();
		}
		
		
		if (correctX != 0)
		{
			mOffsetX = 0;
			mCamera.offsetCenter(correctX, 0);
		}
		
		if (correctY != 0)
		{
			mOffsetY = 0;
			mCamera.offsetCenter(0, correctY);
		}
		*/
		return true;
	}
	
	final public ScrollDetector getDetector()
	{
		return mScrollDetector;
	}
	
	
	@Override
	public void onScrollStart(TouchEvent pSceneTouchEvent, float pDistanceX, float pDistanceY) {}
	
	
	
	@Override
	public void onScroll(TouchEvent pSceneTouchEvent, float pDistanceX, float pDistanceY)
	{
		final float zoomFactor = mCamera.getZoomFactor();
		
		mOffsetX = -zoomFactor * pDistanceX;
		mOffsetY = -zoomFactor * pDistanceY;
	}
	
	
	@Override
	public void onScrollFinish(TouchEvent pSceneTouchEvent, float pDistanceX, float pDistanceY) {}
	

}
