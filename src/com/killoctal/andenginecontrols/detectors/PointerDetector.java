package com.killoctal.andenginecontrols.detectors;

import java.util.ArrayList;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;

public class PointerDetector
{
	
	public static interface IClickListener {
		/// Executed when pressed and released
		void onClick(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
		
		/// Executed when pressed (~ACTION_DOWN)
		void onPress(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
		
		/// Executed when released inside (~ACTION_UP)
		void onRelease(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
	}
	
	
	public static interface IMoveListener {
		/// Executed when pointer is moving
		void onMove(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
		
		/// Executed when dragging inside (you should verify button state with isPressed()
		void onEnter(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
		
		/// Executed when dragging out (you should verify button state with isPressed()
		void onLeave(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
	}
	
	
	
	
	boolean mIsPressed;
	boolean mIsPointerInside;
	
	public ITouchArea mTouchArea;
	private int mPointerID;
	
	
	
	private boolean mIsEnabled;

	private boolean mIsModal;
	private boolean mIsModalThisTime;
	private boolean mIsModalTotal;
	
	/**
	 * @name Listeners
	 * @{
	 */
	final public ArrayList<IClickListener> mClickListeners;
	final public ArrayList<IMoveListener> mMoveListeners;
	/**
	 * @}
	 */
	
	
	public PointerDetector()
	{
		this(null);
	}
	
	public PointerDetector(ITouchArea pTouchArea)
	{
		mTouchArea = pTouchArea;
		
		// Creates the listeners lists
		mClickListeners = new ArrayList<PointerDetector.IClickListener>();
		mMoveListeners = new ArrayList<PointerDetector.IMoveListener>();
		
		reset();
	}
	
	
	public void reset()
	{
		mIsEnabled = true;
		mPointerID = TouchEvent.INVALID_POINTER_ID;
		
		mIsModalThisTime = false;
		
		mIsPressed = false;
		mIsPointerInside = false;
		
		setModal(true);
	}
	
	
	/*public void catchPointer(int pPointerID)
	{
		mPointerID = pPointerID;
	}*/

	public void setEnabled(boolean pEnabled)
	{
		mIsEnabled = pEnabled;
	}
	
	public boolean isEnabled()
	{
		return mIsEnabled;
	}
	
	
	final public boolean isPressed()
	{
		return mIsPressed;
	}
	/*
	final public boolean isLeft()
	{
		return mIsLeft;
	}*/
	

	final public void setModal(boolean pModal, boolean pTotal)
	{
		mIsModal = pModal;
		mIsModalTotal = pTotal;
	}

	final public void setModal(boolean pModal)
	{
		setModal(pModal, true);
	}
	
	
	
	final public boolean isModal()
	{
		return mIsModal;
	}
	
	final public void setModalThisTime()
	{
		mIsModalThisTime = true;
	}
	
	
	/**
	 * 
	 * @param pPressed
	 * @note This does not execute any "on...()" method
	 */
	public void setPressed(boolean pPressed)
	{
		mIsPressed = pPressed;
	}

	
	
	
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		boolean tmpContains = mTouchArea == null || mTouchArea.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
		if (mPointerID == TouchEvent.INVALID_POINTER_ID)
		{
			mPointerID = pSceneTouchEvent.getPointerID();
		}
		
		
		if (mPointerID == pSceneTouchEvent.getPointerID())
		{
			if (pSceneTouchEvent.isActionMove())
			{
				if (tmpContains)
				{
					if (! mIsPointerInside)
					{
						mIsPointerInside = true;
						
						// Execute the listners
						for(IMoveListener iListener : mMoveListeners)
							iListener.onEnter(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
					else
					{
						// Execute the listners
						for(IMoveListener iListener : mMoveListeners)
							iListener.onMove(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
				}
				else
				{
					if (mIsPointerInside)
					{
						mIsPointerInside = false;

						// Execute the listners
						for(IMoveListener iListener : mMoveListeners)
							iListener.onLeave(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
					else
					{
						// Execute the listners
						for(IMoveListener iListener : mMoveListeners)
							iListener.onMove(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
				}
			}
			else if (pSceneTouchEvent.isActionDown())
			{
				mIsPressed = true;
				mIsPointerInside = true;
				
				// Execute the listners
				for(IClickListener iListener : mClickListeners)
					iListener.onPress(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
			else if (pSceneTouchEvent.isActionUp())
			{
				// If no checked, bindings cause problems
				if (tmpContains)
				{
					// Execute the listners
					for(IClickListener iListener : mClickListeners)
						iListener.onRelease(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					
					if (mIsPressed)
					{
						mIsPressed = false;
						mIsPointerInside = false;
						
						// Execute the listners
						for(IClickListener iListener : mClickListeners)
							iListener.onClick(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
				}
				
				// Free the catched pointer
				mPointerID = TouchEvent.INVALID_POINTER_ID;
			}
		}
		
		
		if (mIsModalThisTime)
		{
			mIsModalThisTime = false;
			return true;
		}
		
		if (mIsModal)
		{
			if (mIsModalTotal)
			{
				return true;
			}
			return tmpContains;
		}
		
		return false;
	}

}
