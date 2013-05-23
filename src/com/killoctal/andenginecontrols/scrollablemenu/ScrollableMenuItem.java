package com.killoctal.andenginecontrols.scrollablemenu;


import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import com.killoctal.andenginecontrols.buttons.PushButton;


public abstract class ScrollableMenuItem extends PushButton
{
	
	int mColumn = -1, mRow = -1;
	public ScrollableMenuItem(Scene pScene, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pScene, pVertexBufferObjectManager);
		
		// The touch system is delegated to the scroll list controller
		pScene.unregisterTouchArea( getDetector() );
	}
}

