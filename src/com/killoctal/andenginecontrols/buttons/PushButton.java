package com.killoctal.andenginecontrols.buttons;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class PushButton extends Rectangle
{

	public PushButton(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(0, 0, 0, 0, pVertexBufferObjectManager);
	}

	
	
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP)
		{
			onClick();
			return true;
		}
		
		return false;
	}
	
	
	
	public abstract void onClick();
}
