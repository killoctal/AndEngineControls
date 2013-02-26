package com.killoctal.andenginecontrols.detectors;

import java.util.ArrayList;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;

/**
 * @brief A pointer detector class for fix ACTION_UP and ACTION_MOVE problems
 * 
 * @note The "leaving" event requires the scene has setTouchAreaBindingOnActionDownEnabled(true) 
 */
public class PointerDetector
{
	
	public static interface IClickListener {
		/// Executed when pressed and released inside
		void onClick(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, long pPressDuration);
		
		/// Executed when pressed (~ACTION_DOWN)
		void onPress(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
		
		/// Executed when released inside (~ACTION_UP) or leaved
		void onRelease(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside);
	}
	
	
	public static interface IMoveListener {
		/// Executed when pointer is moving
		void onMove(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside);
		
		/// Executed when dragging inside
		void onEnter(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pIsPressed);
		
		/// Executed when dragging out
		void onLeave(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pIsPressed);
	}
	
	
	
	
	boolean mIsPressed;
	boolean mIsPointerInside;
	
	public ITouchArea mTouchArea;
	private int mPointerID;
	
	
	
	private boolean mIsEnabled;

	private boolean mIsModal;
	private boolean mIsModalThisTime;
	private boolean mIsModalTotal;
	
	private long mPressTime;
	protected float mPressX, mPressY;
	
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
		
		mIsEnabled = true;
		mPointerID = TouchEvent.INVALID_POINTER_ID;
		
		mIsModalThisTime = false;
		
		mIsPressed = false;
		mIsPointerInside = false;
		
		setModal(true);
	}
	
	
	
	/**
	 * @brief Handle the touch event
	 * 
	 * The onTouchArea of the object must execute this method
	 */
	public boolean handleTouchEvent(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		boolean tmpInside = mTouchArea == null || mTouchArea.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
		if (mPointerID == TouchEvent.INVALID_POINTER_ID)
		{
			mPointerID = pSceneTouchEvent.getPointerID();
		}
		
		
		if (mPointerID == pSceneTouchEvent.getPointerID())
		{
			if (pSceneTouchEvent.isActionDown())
			{
				// Useless check but for the principe
				if (tmpInside)
				{
					mPressTime = System.currentTimeMillis();
					setPressed(true);
					
					// Pointer press position
					mPressX = pTouchAreaLocalX;
					mPressY = pTouchAreaLocalY;
					
					// Execute the listners
					executeOnPressListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			}
			else if (pSceneTouchEvent.isActionUp())
			{
				// Execute the listners
				executeOnReleaseListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, tmpInside);
				
				if (tmpInside)
				{
					// If pressed => click
					if (mIsPressed)
					{
						setPressed(false);
						
						// Execute the listners
						executeOnClickListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, System.currentTimeMillis() - mPressTime);
					}
				}
				
				// Free the catched pointer
				freePointer();
			}
			else if (pSceneTouchEvent.isActionMove())
			{
				if (tmpInside)
				{
					if (! mIsPointerInside)
					{
						mIsPointerInside = true;
						
						// Execute the listners
						executeOnEnterListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, mIsPressed);
					}
				}
				else
				{
					if (mIsPointerInside)
					{
						mIsPointerInside = false;
						
						// Execute the listners
						executeOnLeaveListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, mIsPressed);
						
						// If pressed, release the button
						if (mIsPressed)
						{
							setPressed(false);
							
							// Execute the listners
							executeOnReleaseListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, false);
						}
					}
				}
				
				// Execute the listners
				executeOnMoveListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, tmpInside);
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
			return tmpInside;
		}
		
		return false;
	}
	
	
	
	
	
	
	
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
	
	
	
	final public void freePointer()
	{
		mPointerID = TouchEvent.INVALID_POINTER_ID;
	}
	
	
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
		mIsPointerInside = pPressed;
	}
	
	
	public float getPressX()
	{
		return mPressX;
	}
	public float getPressY()
	{
		return mPressY;
	}
	
	
	
	
	
	protected void executeOnClickListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, long pPressDuration)
	{
		for(IClickListener iListener : mClickListeners)
			iListener.onClick(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pPressDuration);
	}
	
	protected void executeOnPressListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IClickListener iListener : mClickListeners)
			iListener.onPress(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
	protected void executeOnReleaseListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		for(IClickListener iListener : mClickListeners)
			iListener.onRelease(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	
	protected void executeOnMoveListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		for(IMoveListener iListener : mMoveListeners)
			iListener.onMove(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	
	protected void executeOnEnterListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pIsPressed)
	{
		for(IMoveListener iListener : mMoveListeners)
			iListener.onEnter(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pIsPressed);
	}
	
	protected void executeOnLeaveListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pIsPressed)
	{
		for(IMoveListener iListener : mMoveListeners)
			iListener.onLeave(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pIsPressed);
	}
	
	
	
	
	
	

}
