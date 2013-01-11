package com.killoctal.andenginecontrols.scrollablemenu;


import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import com.killoctal.andenginecontrols.buttons.PushButton;


public abstract class ScrollableMenuItem extends PushButton
{
	
	int mColumn = -1, mRow = -1;
	ScrollableMenuControl mScrollControl;
	
	
	public ScrollableMenuItem(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pVertexBufferObjectManager);
	}
	
	
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		if (mScrollControl.isScrolling() && getPointerDetector().isPressed())
		{
			getPointerDetector().setPressed(false);
		}
		
		if (super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY))
		{
			return mScrollControl.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
		
		return false;
	}
	
}

