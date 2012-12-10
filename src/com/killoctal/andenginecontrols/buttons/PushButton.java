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
public abstract class PushButton extends Rectangle
{
	private boolean mIsPressed;
	private boolean mIsLeaved;
	
	private boolean mInterceptEvent;
	 
	
	public PushButton(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		this(0, 0, 0, 0, pVertexBufferObjectManager);
	}
	
	
	public PushButton(float pX, float pY, float pWidth, float pHeight,VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		mIsPressed = false;
		mIsLeaved = false;
		
		mInterceptEvent = true;
	}
	
	
	
	final public boolean isPressed()
	{
		return mIsPressed;
	}
	
	
	final public void setInterceptEvent(boolean pInterceptEvent)
	{
		mInterceptEvent = pInterceptEvent;
	}
	
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		if (pSceneTouchEvent.isActionMove())
		{
			if (mIsLeaved)
			{
				if (contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY()))
				{
					if (! mIsPressed)
					{
						mIsPressed = true;
						mIsLeaved = false;
						
						onEnter(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
				}
			}
			else
			{
				if (! contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY()))
				{
					mIsPressed = false;
					mIsLeaved = true;
					
					onLeave(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			}
		}
		else
		{
			if (pSceneTouchEvent.isActionDown())
			{
				mIsPressed = true;
				mIsLeaved = false;
				
				onPress(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
			else if (pSceneTouchEvent.isActionUp())
			{
				onRelease(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				
				if (mIsPressed)
				{
					mIsPressed = false;
					mIsLeaved = false;
					
					return onClick(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			}
		}
		
		
		return mInterceptEvent;
	}
	
	
	
	protected boolean onClick(TouchEvent pceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { return mInterceptEvent; }
	protected void onPress(TouchEvent pceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }
	protected void onRelease(TouchEvent pceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }
	protected void onEnter(TouchEvent pceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }
	protected void onLeave(TouchEvent pceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) { /* nothing */ }
}
