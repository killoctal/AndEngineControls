package com.killoctal.andenginecontrols.buttons;


import java.util.HashSet;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import com.killoctal.andenginecontrols.detectors.PointerDetector;
import com.killoctal.andenginecontrols.detectors.PointerDetector.IClickListener;
import com.killoctal.andenginecontrols.detectors.PointerDetector.IMoveListener;

/**
 * @brief A simple push button
 * @author Gabriel Schlozer
 * 
 * The events are based on listeners. The button itself is a listener so you don't have to manually add listeners.
 * You can add listeners for extend the button possibilities.
 * 
 * @note The "leaving" event requires the scene has setTouchAreaBindingOnActionDownEnabled(true) 
 */
public class PushButton extends Rectangle
{
	private PointerDetector mDetector;
	private HashSet<PushButton> mChildrenToReEnable;
	
	public PushButton(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		this(0, 0, 0, 0, pVertexBufferObjectManager);
	}
	
	
	public PushButton(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		this(pX, pY, pWidth, pHeight, pVertexBufferObjectManager, null);
	}
	
	
	public PushButton(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager pVertexBufferObjectManager, PointerDetector pPointerDetector)
	{
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		// Creates the default detector
		setClickDetector(pPointerDetector);
		
		// Auto add listeners
		if (this instanceof PointerDetector.IClickListener)
		{
			mDetector.mClickListener = (IClickListener) this;
		}
		
		if (this instanceof PointerDetector.IMoveListener)
		{
			mDetector.mMoveListener = (IMoveListener) this;
		}
	}
	
	
	
	final public PointerDetector getDetector()
	{
		return mDetector;
	}
	
	
	
	public void setClickDetector(PointerDetector pDetector)
	{
		if (pDetector != null)
		{
			mDetector = pDetector;
		}
		else
		{
			mDetector = new PointerDetector(this);
		}
		mDetector.mTouchArea = this;
	}
	
	
	
	/**
	 * @brief Enables or disable this control (and manage the sub elements too)
	 * @param pEnabled
	 */
	public void setEnabled(boolean pEnabled)
	{
		mDetector.setEnabled(pEnabled);
		
		if (mChildren != null)
		{
			if (! pEnabled)
			{
				for(IEntity iEntity : mChildren)
				{
					t(iEntity);
				}
			}
			else
			{
				if (mChildrenToReEnable != null)
				{
					for(PushButton iBtn : mChildrenToReEnable)
					{
						iBtn.setEnabled(true);
					}
					mChildrenToReEnable.clear();
				}
			}
		}
	}
	
	
	final public boolean isEnabled()
	{
		return mDetector.isEnabled();
	}
	
	
	/**
	 * @brief Automatically disables the children if this button is disabled
	 */
	@Override
	public void attachChild(IEntity pEntity) throws IllegalStateException
	{
		super.attachChild(pEntity);
		
		if (! isEnabled())
		{
			t(pEntity);
		}
	}
	
	
	private void t(IEntity pEntity)
	{
		if (pEntity instanceof PushButton)
		{
			PushButton tmpBtn = (PushButton) pEntity;
			if (tmpBtn.isEnabled())
			{
				if (mChildrenToReEnable == null)
				{
					mChildrenToReEnable = new HashSet<PushButton>();
				}
				mChildrenToReEnable.add(tmpBtn);
				tmpBtn.setEnabled(false);
			}
		}
	}
	
	
	
	/**
	 * @note Don't override this method, it is best to override the virtual methods onClick, onMove, etc
	 */
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		return mDetector.handleTouchEvent(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
	
	
	@Override
	public boolean contains(float pX, float pY)
	{
		if (! isEnabled() || ! isVisible())
		{
			return false;
		}
		
		return super.contains(pX, pY);
	}
	


}
