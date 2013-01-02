package com.killoctal.andenginecontrols.utils;




public class Maths
{
	/**
	 * @note Precision 99.99% of target value 
	 */
	public static float curveValue(float dest, float current, float speed, float tfact)
	{
		float diff = dest - current;
		
		float value = (Math.abs(diff / dest) > 0.0001f)
				? (float) (current + (diff * ((Math.pow(speed, tfact) - Math.pow(speed-1f, tfact)) / Math.pow(speed , tfact))))
				: dest;
				
		return value;
	}
	
}
