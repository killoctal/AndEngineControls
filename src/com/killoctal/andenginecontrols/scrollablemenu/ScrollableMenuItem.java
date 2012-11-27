package com.killoctal.andenginecontrols.scrollablemenu;


import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import com.killoctal.andenginecontrols.buttons.PushButton;


public abstract class ScrollableMenuItem extends PushButton
{
	public int mColumn = -1, mRow = -1;
	
	protected ScrollableMenuControl mScrollControl;
	
	public ScrollableMenuItem(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pVertexBufferObjectManager);
	}

	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		if (mScrollControl.isScrolling())
		{
			return false;
		}
		
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
}

