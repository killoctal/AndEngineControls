package com.killoctal.andenginecontrols.buttons;


import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * 
 * @author Gabriel Schlozer
 * 
 * @note The "leaving" event requires the scene has setTouchAreaBindingOnActionDownEnabled(true) 
 */
public class PushButton extends Rectangle
{
	public enum State
	{
		NORMAL,
		PRESSED
	};
	
	private boolean mIsLeaved;
	
	
	private boolean mIsFocused;
	private boolean mIsModal;
	private boolean mIsModalThisTime;
	private boolean mIsModalTotal;
	
	private boolean mEnabled;
	private State mState;
	
	public PushButton(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		this(0, 0, 0, 0, pVertexBufferObjectManager);
	}
	
	
	public PushButton(float pX, float pY, float pWidth, float pHeight,VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		mEnabled = true;
		
		mIsLeaved = false;
		
		mIsFocused= false;
		
		mIsModalThisTime = false;
		
		mState = State.NORMAL;
		
		setModal(true);
	}
	
	
	final public State getState()
	{
		return mState;
	}
	
	final public boolean isFocused()
	{
		return mIsFocused;
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
			onStateChanged(tmpPrevState);
		}
	}
	
	
	/**
	 * @note Don't override this method, it is best to override the virtual methods onClick, onMove, etc
	 */
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		boolean tmpContains = contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
		
		if (pSceneTouchEvent.isActionMove())
		{
			if (! tmpContains)
			{
				if (! mIsLeaved)
				{
					mIsLeaved = true;
					onLeave(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				else
				{
					onMove(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				
				changeState(State.NORMAL);
			}
			else
			{
				if (mIsLeaved)
				{
					mIsLeaved = false;
					onEnter(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				else
				{
					onMove(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				
				changeState(State.PRESSED);
			}
		}
		else
		{
			if (pSceneTouchEvent.isActionDown())
			{
				mIsFocused = true;
				mIsLeaved = false;
				
				onPress(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				
				changeState(State.PRESSED);
			}
			else if (pSceneTouchEvent.isActionUp())
			{
				// If no checked, bindings cause problems
				if (tmpContains)
				{
					onRelease(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					
					if (mIsFocused)
					{
						mIsFocused = false;
						mIsLeaved = false;
						
						onClick(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
				}
				
				changeState(State.NORMAL);
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
	
	
	
	protected void onMove(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }
	
	/**
	 * @brief Executed when pressed and released
	 */
	protected void onClick(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }
	
	/**
	 * @brief Executed when pressed (~ACTION_DOWN)
	 */
	protected void onPress(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }
	
	/**
	 * @brief Executed when released inside (~ACTION_UP)
	 */
	protected void onRelease(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }
	
	/**
	 * @brief Executed when dragging inside
	 * @note Verify button state with isFocused()
	 */
	protected void onEnter(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }
	
	/**
	 * @brief Executed when dragging out
	 * @note Verify button state with isFocused()
	 */
	protected void onLeave(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }
	
	protected void onStateChanged(State pPreviousState) { /* nothing */ }
}
