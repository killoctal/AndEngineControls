package com.killoctal.andenginecontrols.buttons;

import org.andengine.input.touch.TouchEvent;
import com.killoctal.andenginecontrols.buttons.PushButton.State;

public interface IPushButton
{
	void onMove(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
	
	/**
	 * @brief Executed when pressed and released
	 */
	void onClick(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
	
	/**
	 * @brief Executed when pressed (~ACTION_DOWN)
	 */
	void onPress(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
	
	/**
	 * @brief Executed when released inside (~ACTION_UP)
	 */
	void onRelease(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
	
	/**
	 * @brief Executed when dragging inside
	 * @note Verify button state with isFocused()
	 */
	void onEnter(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
	
	/**
	 * @brief Executed when dragging out
	 * @note Verify button state with isFocused()
	 */
	void onLeave(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
	
	
	void onStateChanged(State pPreviousState);
}
