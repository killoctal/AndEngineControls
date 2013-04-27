package com.killoctal.andenginecontrols.scrollablemenu;

import android.util.SparseArray;
import java.util.ArrayList;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import com.killoctal.andenginecontrols.detectors.SlideDetector;
import com.killoctal.andenginecontrols.detectors.SlideDetector.Direction;


/**
 * 
 * @author Gabriel Schlozer
 * 
 * @warning If buttons must have special touch events, create them before the menu
 *          control, or use a custom registerTouchArea for this control.
 */
public class ScrollableMenuControl extends Rectangle implements SlideDetector.ISlideDetectorListener
{
	//private ScrollableMenuItem mRemovingItem;
	
	final private ArrayList<ScrollableMenuItem> mItems;
	
	/// The current scroll (call updateScroll() if you change manually)
	public float mScrollX = 0, mScrollY = 0;
	private float mScrollStartX, mScrollStartY;
	
	public float mDefaultColumnWidth, mDefaultRowHeight;
	
	/// The scroll detector (for set minimal scroll distance)
	final private SlideDetector mSlideDetector;
	
	
	private final SparseArray<Float> mRowsHeights, mColsWidths;
	private final SparseArray<Float> mColsPos, mRowsPos;
	
	private boolean mIsScrolling;
	
	float tmpMaxScrollX;
	float tmpMaxScrollY;
	
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
	public ScrollableMenuControl(float pX, float pY, float pWidth, float pHeight,
			float pDefaultColumnWidth, float pDefaultRowHeight,
			VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		mItems = new ArrayList<ScrollableMenuItem>();
		
		mDefaultColumnWidth = pDefaultColumnWidth;
		mDefaultRowHeight = pDefaultRowHeight;
		
		mRowsHeights = new SparseArray<Float>();
		mColsWidths = new SparseArray<Float>();
		
		mColsPos = new SparseArray<Float>();
		mRowsPos = new SparseArray<Float>();
		
		mSlideDetector = new SlideDetector(10, this);
		mSlideDetector.mSlideListener = this;
		
		tmpMaxScrollX = tmpMaxScrollY = 0;
		
		mScrollStartX = mScrollStartY = 0;
		
		mIsScrolling = false;
	}
	
	
	final public boolean isScrolling()
	{
		return mIsScrolling;
	}
	
	final public SlideDetector getDetector()
	{
		return mSlideDetector;
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
		attachChild(pItem);
		
		updateMenu();
	}
	
	

	/**
	 * @brief Update only positions
	 */
	public void updateScroll()
	{
		// Checking the applyed scroll
		tmpMaxScrollX = mWidth -  (mColsPos.get(mColsPos.size()-1, 0f) + mColsWidths.get(mColsWidths.size()-1, 0f));
		tmpMaxScrollY = mHeight - (mRowsPos.get(mRowsPos.size()-1, 0f) + mRowsHeights.get(mRowsHeights.size()-1, 0f));
		
		if (mScrollX > 0f)
		{
			mScrollX = 0;
		}
		if (mScrollY > 0f)
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
	
	
	/*
	public void scrollTo(float pScrollX, float pScrollY)
	{
		mScrollX = pScrollX;
		mScrollY = pScrollY;
		
		updateScroll();
		
		mScrollStartX = mScrollX;
		mScrollStartY = mScrollY;
	}
	*/
	
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(ScrollableMenuItem iItem : mItems)
		{
			if (isScrolling())
			{
				if (iItem.getDetector().isPressed())
				{
					iItem.getDetector().setPressed(false);
				}
			}
			
			float[] tmpNewLocalCoords = iItem.convertSceneToLocalCoordinates(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			iItem.onAreaTouched(pSceneTouchEvent, tmpNewLocalCoords[0], tmpNewLocalCoords[1]);
		}
		
		// Transmit the event
		return mSlideDetector.handleTouchEvent(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}


	@Override
	public void onSlideStart(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY, Direction pDirection)
	{
		mIsScrolling = true;
		switch(pDirection)
		{
			case TOP:
			case BOTTOM:
				if (tmpMaxScrollY == 0)
				{
					mIsScrolling = false;
				}
				break;
				
			case LEFT:
			case RIGHT:
				if (tmpMaxScrollX == 0)
				{
					mIsScrolling = false;
				}
				break;
	
			case NONE: break;
		}
		
		onSlide(pSceneTouchEvent, pOffsetX, pOffsetY, pDirection);
	}


	@Override
	public void onSlide(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY, Direction pDirection)
	{
		mScrollX = mScrollStartX + pOffsetX;
		mScrollY = mScrollStartY + pOffsetY;
		
		updateScroll();
	}


	@Override
	public void onSlideEnd(TouchEvent pSceneTouchEvent, float pOffsetX, float pOffsetY, Direction pDirection)
	{
		onSlide(pSceneTouchEvent, pOffsetX, pOffsetY, pDirection);
		
		mIsScrolling = false;
		
		mScrollStartX = mScrollX;
		mScrollStartY = mScrollY;
	}


	
}

