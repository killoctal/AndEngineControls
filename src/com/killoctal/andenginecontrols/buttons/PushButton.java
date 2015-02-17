package com.killoctal.andenginecontrols.buttons;


import java.util.HashSet;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.killoctal.andenginecontrols.detectors.ClickDetector;
import com.killoctal.andenginecontrols.detectors.PointerDetector;

/**
 * @brief A simple touchable push button
 * @author Gabriel Schlozer
 * 
 * The events are based on listeners. The button itself is a listener so you don't have to manually add listeners.
 * You can add listeners for extend the button possibilities.
 * 
 * @warning You MUST NOT register this touchArea to the scene because this is the internal detector
 *          that will be registered instead ! This structuration was made because the AndEngine
 *          touch management doesn't work properly with the enter/leave events so I coded a workaround.
 * 
 * @note You can overload "onAreaTouched" method if you want but the goal of using
 *       AndEngineControls is to do not have to use it anymore... but you still can.
 */
public class PushButton extends Rectangle
{
	private PointerDetector mDetector;
	private HashSet<PushButton> mChildrenToReEnable;
	final public Scene mScene;
	
	
	/**
	 * @brief Constructor
	 * 
	 * @param pScene
	 * @param pX
	 * @param pY
	 * @param pWidth
	 * @param pHeight
	 * @param pVertexBufferObjectManager
	 * @param pPointerDetector
	 * 
	 * @warning The button is automatically registered (in facts it is the detector instead the button itself)
	 */
	public PushButton(Scene pScene, float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		mScene = pScene;
		
		mDetector = instanciateDetector();
		mDetector.mTouchArea = this;
		
		// Automatic register (important to register the detector instead the button itself)
		mScene.registerTouchArea(mDetector);
		
		// Auto add listeners
		if ((Object)this instanceof ClickDetector.IClickListener)
		{
			mDetector.mPointer = (ClickDetector.IClickListener) (Object)this;
		}
	}
	
	
	/**
	 * @overload
	 */
	public PushButton(Scene pScene, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		this(pScene, 0, 0, 0, 0, pVertexBufferObjectManager);
	}
	
	
	/* virtuel */ protected PointerDetector instanciateDetector()
	{
		return new ClickDetector(this);
	}
	
	final public PointerDetector getDetector()
	{
		return mDetector;
	}
	
	
	
	/**
	 * @brief Enables or disable this control (and manage the sub elements too)
	 * @param pEnabled
	 */
	public void setEnabled(boolean pEnabled)
	{
		getDetector().setEnabled(pEnabled);
		
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
		return getDetector().isEnabled();
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
