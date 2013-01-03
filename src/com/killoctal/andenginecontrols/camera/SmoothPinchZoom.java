package com.killoctal.andenginecontrols.camera;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;



/**
 * @brief Creates a smoothed pinch zoom
 * @author Gabriel Schlozer © killoctal
 * @date 2013.01.03
 * @copyright GNU Lesser General Public License LGPLv3 http://www.gnu.org/licenses/lgpl.html
 */
public class SmoothPinchZoom implements IPinchZoomDetectorListener
{
	/**
	 * @name Editable properties
	 * @{
	 */
	/// Factor limit min (zoom back)
	public float FACTOR_MIN = 0;
	
	/// Factor limit max (zoom before)
	public float FACTOR_MAX = Float.MAX_VALUE;
	
	/// Smooth speed (more = reactive, less = slow)
	public float SPEED = 15;
	/**
	 * @}
	 */
	
	
	/// The camera
	final private ZoomCamera mCamera;
	
	/// Pinch detector
	final private PinchZoomDetector mPinch;
	
	/// Zoom factor target
	private float mFactorTarget;
	
	/// Factor at start pinch
	private float mFactorStart;
	
	/// If we are zooming or not
	private boolean mIsZooming;
	
	/// ID of the two pointers
	private int mPointerID_1, mPointerID_2;
	
	
	
	/**
	 * @brief Constructor
	 * @param pScene Scene (for register the update handler)
	 * @param pCam The camera that will uses zoom
	 */
	public SmoothPinchZoom(Scene pScene, ZoomCamera pCam)
	{
		mCamera = pCam;
		
		// Reset the pointers
		mPointerID_1 = mPointerID_2 = -1;
		
		// Creates the pinch detector
		mPinch = new PinchZoomDetector(this);
		
		// Init the target
		mFactorTarget = mCamera.getZoomFactor();
		
		// Smooth management is made with an update handler 
		pScene.registerUpdateHandler( new IUpdateHandler(){
			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				if (mFactorTarget != mCamera.getZoomFactor())
				{
					// Calculate the smooth factor
					float tmpCurrent = mCamera.getZoomFactor();
					float tmpNew = limit(tmpCurrent + (mFactorTarget - tmpCurrent) * pSecondsElapsed * SPEED);
					
					// Apply the zoom until 99.9% precision
					mCamera.setZoomFactor( (Math.abs(tmpNew / tmpCurrent) > 0.001f) ? tmpNew : mFactorTarget);
				}
			}
	
			@Override
			public void reset(){}
		});
	}
	
	
	
	/**
	 * @brief Return if user makes currently a zoom  
	 * @return TRUE if zoom is made now
	 * 
	 * @note Considered as sooming : since user uses two finders and moves it until
	 *       <b>these two fingers</b> are released
	 */
	public boolean isZooming()
	{
		return mIsZooming;
	}
	
	
	
	/**
	 * @brief Zoom now (with min/max gesture)
	 * @param pFactor Zoom factor
	 */
	public void zoomTo(float pFactor)
	{
		mFactorTarget = limit(pFactor);
	}
	
	
	
	/**
	 * @brief Apply limits to this value
	 * @return The value limited to defined limits
	 */
	private float limit(float pFactor)
	{
		return Math.max(FACTOR_MIN, Math.min(pFactor, FACTOR_MAX));
	}
	
	
	
	/**
	 * @brief Handle the touch event (main method)
	 * @param pSceneTouchEvent The event raised by
	 * @return TRUE if event handled, FALSE if not (same usage as "onTouchEvent")
	 */
	public boolean handle(TouchEvent pSceneTouchEvent)
	{
		// The two fingers memorization management
		if (pSceneTouchEvent.isActionDown()) // On push
		{
			if (mPointerID_1 == -1)
			{
				mPointerID_1 = pSceneTouchEvent.getPointerID();
			}
			else if (mPointerID_2 == -1)
			{
				mPointerID_2 = pSceneTouchEvent.getPointerID();
			}
		}
		else if (pSceneTouchEvent.isActionUp()) // On release
		{
			if (pSceneTouchEvent.getPointerID() == mPointerID_1)
			{
				mPointerID_1 = -1;
			}
			else if (pSceneTouchEvent.getPointerID() == mPointerID_2)
			{
				mPointerID_2 = -1;
			}
		}
		
		
		// If the two pointers are used, start pinch tracking
		if (mPointerID_1 != -1 && mPointerID_2 != -1)
		{
			// Transmit event to the pinch
			mPinch.onTouchEvent(pSceneTouchEvent);
			
			// If zoom just begin
			if (! mIsZooming && mPinch.isZooming())
			{
				mIsZooming = true;
			}
		}
		// If no pointer is used, stop the tracking
		else if (mPointerID_1 == -1 && mPointerID_2 == -1)
		{
			if (mIsZooming)
			{
				// Transmit event to the pinch
				mPinch.onTouchEvent(pSceneTouchEvent);
				mIsZooming = false;
			}
		}
		
		return mIsZooming;
	}

	
	
	@Override
	public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent)
	{
		// Inits the factors
		mFactorStart = mCamera.getZoomFactor();
		mFactorTarget = mFactorStart;
	}
	
	@Override
	public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor)
	{
		zoomTo(mFactorStart * pZoomFactor);
	}
	
	@Override
	public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor)
	{
		zoomTo(mFactorStart * pZoomFactor);
	}
	
}
