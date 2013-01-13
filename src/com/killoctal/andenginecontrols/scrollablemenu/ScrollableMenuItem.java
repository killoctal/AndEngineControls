package com.killoctal.andenginecontrols.scrollablemenu;


import org.andengine.opengl.vbo.VertexBufferObjectManager;
import com.killoctal.andenginecontrols.buttons.PushButton;


public abstract class ScrollableMenuItem extends PushButton
{
	int mColumn = -1, mRow = -1;
	
	public ScrollableMenuItem(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pVertexBufferObjectManager);
	}
}

