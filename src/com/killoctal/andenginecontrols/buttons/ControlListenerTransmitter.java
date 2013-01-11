package com.killoctal.andenginecontrols.buttons;

import java.util.List;
import org.andengine.input.touch.TouchEvent;


/**
 * @brief Transmits events to the sublisteners 
 * @author Gabriel Schlozer
 */
public class ControlListenerTransmitter implements IControlListener
{
	final private List<IControlListener> mListeners;
	
	
	public ControlListenerTransmitter(List<IControlListener> pListeners)
	{
		mListeners = pListeners;
	}
	
	
	@Override
	public void onStateChanged(State pPreviousState)
	{
		for(IControlListener iListener : mListeners)
		{
			iListener.onStateChanged(pPreviousState);
		}
	}
	
	@Override
	public void onRelease(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IControlListener iListener : mListeners)
		{
			iListener.onRelease(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
	@Override
	public void onPress(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IControlListener iListener : mListeners)
		{
			iListener.onPress(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
	@Override
	public void onMove(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IControlListener iListener : mListeners)
		{
			iListener.onMove(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
	@Override
	public void onLeave(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IControlListener iListener : mListeners)
		{
			iListener.onLeave(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
	@Override
	public void onEnter(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IControlListener iListener : mListeners)
		{
			iListener.onEnter(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
	@Override
	public void onClick(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IControlListener iListener : mListeners)
		{
			iListener.onClick(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
}
