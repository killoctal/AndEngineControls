package com.killoctal.andenginecontrols.scrollablemenu;

import android.util.SparseArray;
import java.util.ArrayList;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


/**
 * 
 * @author Gabriel Schlozer
 * 
 * @warning If buttons must have special touch events, create them before the menu
 *          control, or use a custom registerTouchArea for this control.
 */
public class ScrollableMenuControl extends Rectangle implements IScrollDetectorListener
{
	final private Scene mScene;
	
	//private ScrollableMenuItem mRemovingItem;
	
	final private ArrayList<ScrollableMenuItem> mItems;
	
	/// The current scroll (call updateScroll() if you change manually)
	public float mScrollX = 0, mScrollY = 0;
	
	public float mDefaultColumnWidth, mDefaultRowHeight;
	
	/// The scroll detector (for set minimal scroll distance)
	final public ScrollDetector mScrollDetector;
	
	
	private final SparseArray<Float> mRowsHeights, mColsWidths;
	private final SparseArray<Float> mColsPos, mRowsPos;
	
	private boolean mScrolling;
	
	
	/**
	 * @brief Constructor
	 * 
	 * @param pX
	 * @param pY
	 * @param pWidth
	 * @param pHeight
	 * @param pDefaultColumnWidth Width of each column
	 * @param pDefaultRowHeight Height of each row
	 * @param pVertexBufferObjectManager
	 * @param pScene
	 */
	public ScrollableMenuControl(float pX, float pY, float pWidth, float pHeight, float pDefaultColumnWidth, float pDefaultRowHeight,
			VertexBufferObjectManager pVertexBufferObjectManager, Scene pScene)
	{
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		mScene = pScene;
		mItems = new ArrayList<ScrollableMenuItem>();
		
		
		mDefaultColumnWidth = pDefaultColumnWidth;
		mDefaultRowHeight = pDefaultRowHeight;
		
		mScrolling = false;
		
		mRowsHeights = new SparseArray<Float>();
		mColsWidths = new SparseArray<Float>();
		
		mColsPos = new SparseArray<Float>();
		mRowsPos = new SparseArray<Float>();
		
		mScrollDetector = new ScrollDetector(10, this);
		
		mScene.registerTouchArea(this);
	}
	
	
	final public boolean isScrolling()
	{
		return mScrolling;
	}
	
	
	public int rowCount()
	{
		return mRowsHeights.size();
	}
	
	public int columnCount()
	{
		return mColsWidths.size();
	}
	
	public int itemsCount()
	{
		return mItems.size();
	}
	
	/**
	 * @brief Update the menu (buttons positions, sizes, etc)
	 */
	public void updateMenu()
	{
		mColsPos.clear();
		float tmpTotalX = 0;
		for(int i=0 ; i<mColsWidths.size() ; i++)
		{
			mColsPos.put(mColsWidths.keyAt(i), tmpTotalX);
			tmpTotalX += mColsWidths.valueAt(i);
		}
		
		mRowsPos.clear();
		float tmpTotalY = 0;
		for(int i=0 ; i<mRowsHeights.size() ; i++)
		{
			mRowsPos.put(mRowsHeights.keyAt(i), tmpTotalY);
			tmpTotalY += mRowsHeights.valueAt(i);
		}
		
		updateScroll();
	}
	
	
	/**
	 * @brief Update only positions
	 */
	public void updateScroll()
	{
		// Checking the applyed scroll
		float tmpMaxScrollX = mWidth -  (mColsPos.get(mColsPos.size()-1, 0f) + mColsWidths.get(mColsWidths.size()-1, 0f));
		float tmpMaxScrollY = mHeight - (mRowsPos.get(mRowsPos.size()-1, 0f) + mRowsHeights.get(mRowsHeights.size()-1, 0f));
		
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
			iItem.setPosition(
					mScrollX + mColsPos.get(iItem.mColumn, 0f),
					mScrollY + mRowsPos.get(iItem.mRow, 0f)
					);
		}
		
	}
	
	
	
	
	
	public void clearItems()
	{
		for(ScrollableMenuItem iItem : mItems)
		{
			removeItem(iItem);
		}
		
		mItems.clear();
		
		mScrollX = mScrollY = 0;
		
		mRowsHeights.clear();
		mColsWidths.clear();
		mColsPos.clear();
		mRowsPos.clear();
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
		addItem(pItem, 0, mItems.size());
	}
	
	public void addItemRow(ScrollableMenuItem pItem)
	{
		addItem(pItem, mItems.size(), 0);
	}
	
	public void addItem(ScrollableMenuItem pItem, int pRow, int pColumn)
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
		//
		if (pItem.getWidth() == 0)
		{
			pItem.setWidth(mDefaultColumnWidth);
		}
		if (pItem.getHeight() == 0)
		{
			pItem.setHeight(mDefaultRowHeight);
		}
		
		
		
		if (mColsWidths.get(pItem.mColumn, 0f) < pItem.getWidth())
		{
			mColsWidths.put(pItem.mColumn, pItem.getWidth());
		}
		
		if (mRowsHeights.get(pItem.mRow, 0f) < pItem.getHeight())
		{
			mRowsHeights.put(pItem.mRow, pItem.getHeight());
		}
		

		mItems.add(pItem);
		pItem.mScrollControl = this;
		attachChild(pItem);
		
		updateMenu();
		
		// Placing the button touch area just before the control
		int tmpIndex = mScene.getTouchAreas().indexOf(this);
		if (tmpIndex != -1)
		{
			mScene.getTouchAreas().add(tmpIndex, pItem);
		}
	}
	
	
	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		mScrolling = true;
		
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
		
		mScrolling = false;
	}
	
	
	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY)
	{
		mScrollX += pDistanceX;
		mScrollY += pDistanceY;
		
		updateScroll();
	}
	
	
	
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		// Transmit the event
		mScrollDetector.onTouchEvent(pSceneTouchEvent);
		
		// If touched, the event is always stopped
		return true;
	}
	
}

