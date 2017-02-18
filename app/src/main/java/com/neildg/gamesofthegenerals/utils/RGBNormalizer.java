/**
 * 
 */
package com.neildg.gamesofthegenerals.utils;

/**
 * Normalizes the given RGB
 * @author user
 *
 */
public class RGBNormalizer {

	public static float MAX_RGB = 255.0f;
	
	public static int RED = 0;
	public static int GREEN = 1;
	public static int BLUE = 2;
	
	public static float[] normalizeRGB(int red, int green, int blue) {
		float[] normalizedRGB = new float[3];
		
		normalizedRGB[0] = (red * 1.0f) / MAX_RGB;
		normalizedRGB[1] = (green * 1.0f) / MAX_RGB;
		normalizedRGB[2] = (blue * 1.0f) / MAX_RGB;
		
		return normalizedRGB;
	}
}
