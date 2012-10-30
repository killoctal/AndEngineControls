package com.killoctal.andenginecontrols.scrollablemenu;

import java.util.ArrayList;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.opengl.vbo.VertexBufferObjectManager;



public class ScrollableMenuControl extends Rectangle implements IScrollDetectorListener
{
	final private Scene mScene;
	
	//private ScrollableMenuItem mRemovingItem;
	
	final private ArrayList<ScrollableMenuItem> mItems;
	
	/// Thw current scroll (call updateScroll() if you change manually)
	public float mScrollX = 0, mScrollY = 0;
	
	public float mColumnWidth, mRowHeight;
	
	private int mColsCount = 0, mRowCount = 0;
	
	private boolean mScrollEventHandled;
	
	/// The scroll detector (for set minimal scroll distance)
	final public ScrollDetector mScrollDetector;


	/**
	 * @brief Constructor
	 * 
	 * @param pX
	 * @param pY
	 * @param pWidth
	 * @param pHeight
	 * @param pColumnWidth Width of each column
	 * @param pRowHeight Height of each row
	 * @param pVertexBufferObjectManager
	 * @param pScene
	 */
	public ScrollableMenuControl(float pX, float pY, float pWidth, float pHeight, float pColumnWidth, float pRowHeight,
			VertexBufferObjectManager pVertexBufferObjectManager, Scene pScene)
	{
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		mScene = pScene;
		mItems = new ArrayList<ScrollableMenuItem>();
		
		
		mColumnWidth = pColumnWidth;
		mRowHeight = pRowHeight;
		
		
		mScrollDetector = new ScrollDetector(10, this);
		
		mScene.registerTouchArea(this);
	}
	
	
	/**
	 * @brief Update the menu (buttons positions, sizes, etc)
	 */
	public void updateMenu()
	{
		for(ScrollableMenuItem iItem : mItems)
		{
			updatePosition(iItem);
			updateSize(iItem);
		}
	}
	
	
	/**
	 * @brief Update only positions
	 */
	public void updateScroll()
	{
		// Checking the applyed scroll
		float tmpMaxScrollX = mWidth - (float)mColsCount * mColumnWidth;
		float tmpMaxScrollY = mHeight - (float)mRowCount * mRowHeight;
		
		if (mScrollX > 0)
		{
			mScrollX = 0;
		}
		if (mScrollY > 0)
		{
			mScrollY = 0;
		}
		if (mScrollX < tmpMaxScrollX)
		{
			mScrollX = tmpMaxScrollX;
		}
		if (mScrollY < tmpMaxScrollY)
		{
			mScrollY = tmpMaxScrollY;
		}
		
		// Apply
		for(ScrollableMenuItem iItem : mItems)
		{
			updatePosition(iItem);
		}
	}
	
	
	/**
	 * @brief Update the size of an item
	 * @param pItem
	 */
	private void updateSize(ScrollableMenuItem pItem)
	{
		pItem.setSize(mColumnWidth, mRowHeight);
	}
	
	
	/**
	 * @brief Update the position of an item
	 * @param pItem
	 */
	private void updatePosition(ScrollableMenuItem pItem)
	{
		float sX = mScrollX + (float)pItem.mColumn * mColumnWidth;
		float sY = mScrollY + (float)pItem.mRow *mRowHeight;
	
		pItem.setPosition(sX, sY);
	}
	
	
	public void clearItems()
	{
		for(ScrollableMenuItem iItem : mItems)
		{
			removeItem(iItem);
		}
		
		mItems.clear();
		
		mColsCount = 0;
		mRowCount = 0;
	}
	
	
	private void removeItem(ScrollableMenuItem iItem)
	{
		if (iItem.getParent() == this)
		{
			mScene.unregisterTouchArea(iItem);
			iItem.detachSelf();
		}
	}
	
	
	
	public void addItemColumn(ScrollableMenuItem pItem)
	{
		addItem(mItems.size(), 0, pItem);
	}
	
	public void addItemRow(ScrollableMenuItem pItem)
	{
		addItem(0, mItems.size(), pItem);
	}
	
	
	
	public void addItem(int pColumn, int pRow, ScrollableMenuItem pItem)
	{
		pItem.mColumn = pColumn;
		pItem.mRow = pRow;
		
		addItem(pItem);
	}
	
	
	/**
	 * @brief Add an item, using inside's specified position
	 * @param pItem Item (with position specified inside)
	 */
	public void addItem(ScrollableMenuItem pItem)
	{
		if (pItem.mColumn+1 > mColsCount)
		{
			mColsCount = pItem.mColumn+1;
		}
		if (pItem.mRow+1 > mRowCount)
		{
			mRowCount = pItem.mRow+1;
		}
		
		
		mItems.add(pItem);
		
		//
		updatePosition(pItem);
		updateSize(pItem);
		
		//
		attachChild(pItem);
		mScene.registerTouchArea(pItem);
	}
	
	
	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		onScroll(pScollDetector, pPointerID, pDistanceX, pDistanceY);
		
		/*if (pDistanceY == 0 && pDistanceX != 0)
		{
			pScollDetector.
			//mRemovingItem = ;
		}*/
	}
	
	
	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		onScroll(pScollDetector, pPointerID, pDistanceX, pDistanceY);
	}
	
	
	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		mScrollEventHandled = true;
		
		mScrollX += pDistanceX;
		mScrollY += pDistanceY;
		
		updateScroll();
	}
	
	
	
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		mScrollEventHandled = false;
		
		// Transmit the event
		mScrollDetector.onTouchEvent(pSceneTouchEvent);
		
		// If scroll handled, we consume the event
		if (mScrollEventHandled)
		{
			return true;
		}
		
		return false;
	}
	
}

