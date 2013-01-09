package com.killoctal.andenginecontrols.buttons;

import java.util.List;

import org.andengine.input.touch.TouchEvent;

import com.killoctal.andenginecontrols.buttons.PushButton.State;


/**
 * @brief Transmits events to the sublisteners 
 * @author Gabriel Schlozer
 */
public class PushButtonTransmitter implements IPushButton
{
	final private List<IPushButton> mListeners;
	public PushButtonTransmitter(List<IPushButton> pListeners)
	{
		mListeners = pListeners;
	}
	
	@Override
	public void onStateChanged(State pPreviousState)
	{
		for(IPushButton iListener : mListeners)
		{
			iListener.onStateChanged(pPreviousState);
		}
	}
	
	@Override
	public void onRelease(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IPushButton iListener : mListeners)
		{
			iListener.onRelease(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
	@Override
	public void onPress(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IPushButton iListener : mListeners)
		{
			iListener.onPress(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
	@Override
	public void onMove(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IPushButton iListener : mListeners)
		{
			iListener.onMove(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
	@Override
	public void onLeave(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IPushButton iListener : mListeners)
		{
			iListener.onLeave(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
	@Override
	public void onEnter(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IPushButton iListener : mListeners)
		{
			iListener.onEnter(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
	
	@Override
	public void onClick(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		for(IPushButton iListener : mListeners)
		{
			iListener.onClick(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		}
	}
}
