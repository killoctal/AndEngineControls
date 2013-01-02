package com.killoctal.andenginecontrols.camera;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;

import com.killoctal.andenginecontrols.utils.Maths;


/**
 * @author Gabriel Schlozer
 */
public class SmoothPinchZoom implements IPinchZoomDetectorListener
{
	
	/// Cible du zoom (pour le smooth)
	private float mZoomTarget = -1;
	
	/// Le facteur au départ du zoom
	private float mFactorStart;
	
	/// Zoom factors limits
	final private float FACTOR_MIN, FACTOR_MAX;
	
	/// Pinch detector
	final private PinchZoomDetector mPinch;
	
	/// If we are zooming or not
	private boolean mIsZooming;
	
	/// La zoom camera
	final private ZoomCamera mCamera;
	
	
	private int mPointerID_1, mPointerID_2;

	public SmoothPinchZoom(Scene pScene, ZoomCamera pCam, float pFactorMin, float pFactorMax)
	{
		mCamera = pCam;
		FACTOR_MIN = pFactorMin;
		FACTOR_MAX = pFactorMax;
		
		mPointerID_1 = mPointerID_2 = -1;
		
		mPinch = new PinchZoomDetector(this);
		
		// La gestion du smooth est faite par un update handler 
		pScene.registerUpdateHandler( new IUpdateHandler(){
			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				if (mZoomTarget != mCamera.getZoomFactor())
				{
					if (mZoomTarget == -1)
					{
						mZoomTarget = mCamera.getZoomFactor();
					}
					
					mCamera.setZoomFactor( Maths.curveValue(mZoomTarget, mCamera.getZoomFactor(), 10, 4) );
				}
			}
	
			@Override
			public void reset(){}
		});
	}

	
	
	/**
	 * @brief Vérifier si on est en train de zoomer
	 * @return
	 */
	public boolean isZooming()
	{
		return mIsZooming;
	}
	
	
	
	/**
	 * @brief Faire un zoom
	 * @param pFactor Facteur de zoom
	 */
	public void zoomTo(float pFactor)
	{
		mZoomTarget = Math.max(FACTOR_MIN, Math.min(pFactor, FACTOR_MAX));
	}
	
	
	
	@Override
	public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent)
	{
		mFactorStart = mCamera.getZoomFactor();
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
	
	
	
	public boolean handle(TouchEvent pSceneTouchEvent)
	{
		if (pSceneTouchEvent.isActionDown())
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
		else if (pSceneTouchEvent.isActionUp())
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
		
		if (mPointerID_1 != -1 && mPointerID_2 != -1)
		{
			// On transmet l'event
			mPinch.onTouchEvent(pSceneTouchEvent);
			
			// If zoom just begin
			if (! mIsZooming && mPinch.isZooming())
			{
				mIsZooming = true;
			}
		}
		else if (mPointerID_1 == -1 && mPointerID_2 == -1)
		{
			mIsZooming = false;
		}
		
		// Si on est en train de zoomer ou qu'on vient de terminer, on intercepte l'event
		return mIsZooming;
	}
	
}
