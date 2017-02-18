/**
 * 
 */
package com.neildg.gamesofthegenerals.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;


/**
 * JSON Parser Util
 * @author user
 *
 */
public class JSONParser {

	public static String loadJSONFromAsset(Context context, String jsonPath) {
		 String json = null;
	        try {

	            InputStream is = context.getAssets().open(jsonPath);

	            int size = is.available();

	            byte[] buffer = new byte[size];

	            is.read(buffer);

	            is.close();

	            json = new String(buffer, "UTF-8");


	        } catch (IOException ex) {
	            ex.printStackTrace();
	            return null;
	        }
	        return json;
	}
	
	public static String loadJSONFromExternalCache(Context context, String jsonPath) {
		String json = null;
        try {

            File file = new File(context.getExternalCacheDir().getAbsolutePath() + "/" + jsonPath);
            FileInputStream is = new FileInputStream(file);
            
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
	}
	
	public static String loadJSONFromAbsolutePath(Context context, String jsonPath) {
		String json = null;
        try {

            File file = new File(jsonPath);
            FileInputStream is = new FileInputStream(file);
            
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
	}
}
