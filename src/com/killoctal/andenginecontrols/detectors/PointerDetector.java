package com.killoctal.andenginecontrols.detectors;

import java.util.ArrayList;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import com.killoctal.andenginecontrols.buttons.IControlListener;
import com.killoctal.andenginecontrols.buttons.IControlListener.State;
import com.killoctal.andenginecontrols.buttons.ControlListenerTransmitter;

public class PointerDetector
{
	boolean mIsLeaved;
	boolean mIsPressed;
	
	public ITouchArea mTouchArea;
	private int mPointerID;
	
	final private ControlListenerTransmitter mControlListener;
	final private ArrayList<IControlListener> mListeners;
	
	private State mState;
	
	private boolean mIsEnabled;

	private boolean mIsModal;
	private boolean mIsModalThisTime;
	private boolean mIsModalTotal;
	
	
	public PointerDetector()
	{
		this(null);
	}
	
	public PointerDetector(ITouchArea pTouchArea)
	{
		mTouchArea = pTouchArea;
		
		// Creates the listeners list
		mListeners = new ArrayList<IControlListener>();
		
		// Creates the listener transmitter
		mControlListener = new ControlListenerTransmitter(mListeners);
		
		reset();
	}

	
	public void reset()
	{
		mIsEnabled = true;
		mPointerID = -1;
		mIsLeaved = false;
		mIsPressed = false;
		mState = State.NORMAL;
		mIsModalThisTime = false;
		
		setModal(true);
	}
	
	

	public void setEnabled(boolean pEnabled)
	{
		mIsEnabled = pEnabled;
	}
	
	public boolean isEnabled()
	{
		return mIsEnabled;
	}
	
	
	final public State getState()
	{
		return mState;
	}
	
	final public boolean isPressed()
	{
		return mIsPressed;
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
	}

	
	
	public void addListener(IControlListener iListener)
	{
		mListeners.add(iListener);
	}
	
	
	
	private void changeState(State pState)
	{
		if (pState != mState)
		{
			State tmpPrevState = mState;
			mState = pState;
			mControlListener.onStateChanged(tmpPrevState);
		}
	}
	
	
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		boolean tmpContains = mTouchArea == null || mTouchArea.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
		if (mPointerID == -1 || mPointerID == pSceneTouchEvent.getPointerID())
		{
			if (mPointerID == -1)
			{
				mPointerID = pSceneTouchEvent.getPointerID();
			}
			
			if (pSceneTouchEvent.isActionMove())
			{
				if (tmpContains)
				{
					if (! mIsLeaved && mState == State.PRESSED)
					{
						mControlListener.onMove(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
					else
					{
						mIsLeaved = false;
						mControlListener.onEnter(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
					
					changeState(State.PRESSED);
				}
				else
				{
					if (! mIsLeaved)
					{
						mIsLeaved = true;
						mControlListener.onLeave(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
					else
					{
						mControlListener.onMove(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
					
					changeState(State.NORMAL);
				}
			}
			else
			{
				if (pSceneTouchEvent.isActionDown())
				{
					mIsPressed = true;
					mIsLeaved = false;
					
					mControlListener.onPress(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					
					changeState(State.PRESSED);
				}
				else if (pSceneTouchEvent.isActionUp())
				{
					// If no checked, bindings cause problems
					if (tmpContains)
					{
						mControlListener.onRelease(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
						
						if (mIsPressed)
						{
							mIsPressed = false;
							mIsLeaved = false;
							
							mControlListener.onClick(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
						}
					}
					
					changeState(State.NORMAL);
					
					mPointerID = -1;
				}
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
