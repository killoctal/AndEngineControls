package com.killoctal.andenginecontrols.buttons;


import java.util.ArrayList;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * @brief A simple push button
 * @author Gabriel Schlozer
 * 
 * The events are based on listeners. The button itself is a listener so you don't have to manually add listeners.
 * You can add listeners for extend the button possibilities.
 * 
 * @note The "leaving" event requires the scene has setTouchAreaBindingOnActionDownEnabled(true) 
 */
public class PushButton extends Rectangle implements IPushButton
{
	public enum State
	{
		NORMAL,
		PRESSED
	};
	
	private boolean mIsLeaved;
	
	
	private boolean mIsPressed;
	private boolean mIsModal;
	private boolean mIsModalThisTime;
	private boolean mIsModalTotal;
	
	private boolean mEnabled;
	private State mState;
	private int mPointerID;
	
	final private PushButtonTransmitter mControlListener;
	final private ArrayList<IPushButton> mListeners;
	
	public PushButton(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		this(0, 0, 0, 0, pVertexBufferObjectManager);
	}
	
	
	public PushButton(float pX, float pY, float pWidth, float pHeight,VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		mEnabled = true;
		
		mIsLeaved = false;
		
		mIsPressed = false;
		
		mIsModalThisTime = false;
		
		mState = State.NORMAL;
		
		mPointerID = -1;
		
		setModal(true);
		
		// Creates the listeners list
		mListeners = new ArrayList<IPushButton>();
		addButtonListener(this);
		
		// Creates the listener transmitter
		mControlListener = new PushButtonTransmitter(mListeners);
	}
	
	public void addButtonListener(IPushButton iListener)
	{
		mListeners.add(iListener);
	}
	
	final public State getState()
	{
		return mState;
	}
	
	final public boolean isPressed()
	{
		return mIsPressed;
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

	final public void setModal(boolean pModal)
	{
		setModal(pModal, true);
	}
	
	final public void setModal(boolean pModal, boolean pTotal)
	{
		mIsModal = pModal;
		mIsModalTotal = pTotal;
	}
	
	final public boolean isModal()
	{
		return mIsModal;
	}
	
	final public void setModalThisTime()
	{
		mIsModalThisTime = true;
	}
	
	
	
	public void setEnabled(boolean pEnabled)
	{
		mEnabled = pEnabled;
	}
	
	public boolean isEnabled()
	{
		return mEnabled;
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
	
	
	/**
	 * @note Don't override this method, it is best to override the virtual methods onClick, onMove, etc
	 */
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		boolean tmpContains = contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
		if (mPointerID == -1 || mPointerID == pSceneTouchEvent.getPointerID())
		{
			if (mPointerID == -1)
			{
				mPointerID = pSceneTouchEvent.getPointerID();
			}
			
			if (pSceneTouchEvent.isActionMove())
			{
				if (! tmpContains)
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
				else
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
	
	@Override
	public boolean contains(float pX, float pY)
	{
		if (! mEnabled || ! isVisible())
		{
			return false;
		}
		
		return super.contains(pX, pY);
	}
	
	
	
	@Override
	public void onMove(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }


	@Override
	public void onClick(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }


	@Override
	public void onPress(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }


	@Override
	public void onRelease(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }


	@Override
	public void onEnter(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }


	@Override
	public void onLeave(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }


	@Override
	public void onStateChanged(State pPreviousState) { /* nothing */ }
	
	

}
