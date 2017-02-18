/**
 * 
 */
package com.neildg.gamesofthegenerals.core;

import java.util.HashMap;

import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;
import org.andengine.util.texturepack.exception.TexturePackParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.neildg.gamesofthegenerals.utils.JSONParser;

/**
 * Class that handles resource loading
 * @author user
 *
 */
public class AssetLoader {

	public static String ATLASLIST_FILENAME = "atlaslist.json";
	
	//atlas list tags
	private static String ATLASLIST_ID_TAG = "id";
	private static String ATLASLIST_PATH_TAG = "path";
	
	private static String TAG = "AssetLoader";
	private static AssetLoader sharedInstance = null;
	
	private Context context;
	
	private HashMap<String,TexturePackTextureRegionLibrary> atlasMap;
	
	private AssetLoader() {
		this.context = EngineCore.getInstance().getContext();
		this.atlasMap = new HashMap<String, TexturePackTextureRegionLibrary>();
	}
	
	public void loadGraphics() {
		//read atlastlist file
		String jsonString = JSONParser.loadJSONFromAsset(context, ATLASLIST_FILENAME);
		
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.v(TAG, "Load ID Value: " +jsonObject.getString(ATLASLIST_ID_TAG));
				Log.v(TAG, "Load Path Value: " +jsonObject.getString(ATLASLIST_PATH_TAG));
				this.loadAtlas(jsonObject.getString(ATLASLIST_ID_TAG), jsonObject.getString(ATLASLIST_PATH_TAG));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void loadAtlas(String id, String path) {
		AssetManager assetManager = EngineCore.getInstance().getAssetManager();
		TextureManager textureManager = EngineCore.getInstance().getEngine().getTextureManager();
		
		String xmlPath = path + id + ".xml";
		String pngFolderPath = path;
		try {
			TexturePack texturePack = new TexturePackLoader(assetManager, textureManager).loadFromAsset(xmlPath, pngFolderPath);
			texturePack.loadTexture();
			
			atlasMap.put(id, texturePack.getTexturePackTextureRegionLibrary());
		} catch (TexturePackParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ITextureRegion getTextureFromAtlas(String atlasId, int textureId) {
		TexturePackTextureRegionLibrary textureRegionLibrary = this.atlasMap.get(atlasId);
		return textureRegionLibrary.get(textureId);
	}

	
	public static AssetLoader getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new AssetLoader();
		}
		
		return sharedInstance;
	}
}
