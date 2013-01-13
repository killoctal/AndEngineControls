package com.killoctal.andenginecontrols.utils;




public class ACMaths
{
	/**
	 * @brief Smooth a value
	 * @param dest Value target
	 * @param current Current value (that will be is modified at each loop)
	 * @param speed Smooth speed (more = reactive, less = slow)
	 * @param tfact Hum. Don't know.
	 * @note Precision 99.99% of target value 
	 */
	public static float smoothValue(float dest, float current, float speed, float tfact)
	{
		float diff = dest - current;
		
		float value = (Math.abs(diff / dest) > 0.0001f)
				? (float) (current + (diff * ((Math.pow(speed, tfact) - Math.pow(speed-1f, tfact)) / Math.pow(speed , tfact))))
				: dest;
				
		return value;
	}
	
	
}
