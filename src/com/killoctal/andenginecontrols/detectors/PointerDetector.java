package com.killoctal.andenginecontrols.detectors;


import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.Constants;

/**
 * @brief A pointer detector class for fix ACTION_UP and ACTION_MOVE problems
 * 
 * @note The "leaving" event requires the scene has setTouchAreaBindingOnActionDownEnabled(true)
 * @warning If you use the touchAreaBindingOnActionMove the effects could be unexpected !
 */
public class PointerDetector implements ITouchArea
{
	private static float[] POS = new float[2];
	
	public static interface IClickListener {
		/// Executed when pressed and released inside
		void onClick(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, long pPressDuration);
		
		/// Executed when pressed (~ACTION_DOWN)
		void onPress(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
		
		/// Executed when released inside (~ACTION_UP) or leaved
		void onRelease(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside);
	}
	
	public static interface ILongClick {
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
	
	
	private int mPointerID;
	
	private boolean mIsEnabled;
	
	public boolean mAcceptsIncomingEvent = false;
	public boolean mClickOnIncomingEvent = true;
	public boolean mIsModal = true;
	public boolean mIsModalTotal = false;
	
	private boolean mIsModalThisTime;
	
	private long mPressTime;
	protected float mPressX, mPressY;
	
	
	public ITouchArea mTouchArea;
	
	/**
	 * @name Listeners
	 * @{
	 */
	public IClickListener mClickListener;
	public IMoveListener mMoveListener;
	/**
	 * @}
	 */
	
	
	
	/**
	 * @brief Constructor
	 * @param pTouchArea A touchArea to link with
	 */
	public PointerDetector(ITouchArea pTouchArea)
	{
		mTouchArea = pTouchArea;
		
		// Creates the listeners lists
		mClickListener = null;
		mMoveListener = null;
		
		mIsEnabled = true;
		mPointerID = TouchEvent.INVALID_POINTER_ID;
		
		mIsModalThisTime = false;
		
		mIsPressed = false;
		mIsPointerInside = false;
	}
	
	
	
	/**
	 * @overload
	 */
	public PointerDetector()
	{
		this(null);
	}
	
	
	
	/**
	 * @brief Handle the touch event
	 * 
	 * The onTouchArea of the object must execute this method
	 */
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		boolean tmpInside = mTouchArea == null || mTouchArea.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
		// Only do this if enabled
		if (mIsEnabled)
		{
			int tmpActionEvent = pSceneTouchEvent.getAction();
			
			// Catch the pointer ID
			if (mPointerID == TouchEvent.INVALID_POINTER_ID)
			{
				// If item concerned, catch the ID and continue
				if (tmpInside && (pSceneTouchEvent.isActionDown() || mAcceptsIncomingEvent))
				{
					mPointerID = pSceneTouchEvent.getPointerID();
				}
			}
			
			// If the pointer is the right one and not an other
			if (mPointerID == pSceneTouchEvent.getPointerID())
			{
				if (mTouchArea != null)
				{
					// Transmit the event to the toucharea (probably useless but might be usefull)
					mTouchArea.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				
				if (tmpActionEvent == TouchEvent.ACTION_MOVE)
				{
					if (tmpInside)
					{
						if (! mIsPointerInside)
						{
							mIsPointerInside = true;
	
							// Execute the listners
							executeOnEnterListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, mIsPressed);
							
							// Simulates the down action if accepts incomingEvent and a click preparation is wanted
							if (mAcceptsIncomingEvent && mClickOnIncomingEvent)
							{
								tmpActionEvent = TouchEvent.ACTION_DOWN;
							}
						}
					}
					else
					{
						if (mIsPointerInside)
						{
							mIsPointerInside = false;
							
							// Execute the listner
							executeOnLeaveListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, mIsPressed);
							
							// If pressed, release the button
							if (mIsPressed)
							{
								setPressed(false);
								
								// Execute the listner
								executeOnReleaseListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, false);
							}
						}
					}
	
					// Execute the listner
					executeOnMoveListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, tmpInside);
				}
				
				if (tmpActionEvent == TouchEvent.ACTION_DOWN)
				{
					// Useless check but for the principe
					if (tmpInside)
					{
						mPressTime = System.currentTimeMillis();
						setPressed(true);
						
						// Pointer press position
						mPressX = pTouchAreaLocalX;
						mPressY = pTouchAreaLocalY;
	
						// Execute the listner
						executeOnPressListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
				}
				
				if (tmpActionEvent == TouchEvent.ACTION_UP)
				{
					// Execute the listner
					executeOnReleaseListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, tmpInside);
					if (tmpInside)
					{
						// If pressed => click
						if (mIsPressed)
						{
							setPressed(false);
							
							// Execute the listner
							executeOnClickListeners(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, System.currentTimeMillis() - mPressTime);
						}
					}
					
					// Free the catched pointer
					freePointer();
				}
			}
		}
		
		// If modal once
		if (mIsModalThisTime)
		{
			mIsModalThisTime = false;
			return true;
		}
		
		
		// If always modal
		if (mIsModal)
		{
			if (mIsModalTotal)
			{
				return true;
			}
			return tmpInside;
		}
		
		// Not handled
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
	

	@Override
	public boolean contains(float pX, float pY)
	{
		// Really important for abuse the AndEngine primitive touch system (sorry Nicolas :p)
		return true;
	}



	@Override
	public float[] convertSceneToLocalCoordinates(float pX, float pY)
	{
		if (mTouchArea != null)
		{
			return mTouchArea.convertSceneToLocalCoordinates(pX, pY);
		}
		
		POS[Constants.VERTEX_INDEX_X] = pX;
		POS[Constants.VERTEX_INDEX_Y] = pY;
		
		return POS;
	}



	@Override
	public float[] convertLocalToSceneCoordinates(float pX, float pY)
	{
		if (mTouchArea != null)
		{
			return mTouchArea.convertLocalToSceneCoordinates(pX, pY);
		}
		
		POS[Constants.VERTEX_INDEX_X] = pX;
		POS[Constants.VERTEX_INDEX_Y] = pY;
		
		return POS;
	}


	
	
	protected void executeOnClickListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, long pPressDuration)
	{
		if (mClickListener != null)
			mClickListener.onClick(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pPressDuration);
	}
	
	
	protected void executeOnPressListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		if (mClickListener != null)
			mClickListener.onPress(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
	
	protected void executeOnReleaseListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		if (mClickListener != null)
			mClickListener.onRelease(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	
	
	protected void executeOnMoveListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pInside)
	{
		if (mMoveListener != null)
			mMoveListener.onMove(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pInside);
	}
	
	
	protected void executeOnEnterListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pIsPressed)
	{
		if (mMoveListener != null)
			mMoveListener.onEnter(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pIsPressed);
	}
	
	
	protected void executeOnLeaveListeners(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, boolean pIsPressed)
	{
		if (mMoveListener != null)
			mMoveListener.onLeave(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY, pIsPressed);
	}
	
	
	

}
