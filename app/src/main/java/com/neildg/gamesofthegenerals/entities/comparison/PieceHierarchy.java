/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.comparison;

import org.andengine.opengl.texture.region.ITextureRegion;

import android.util.Log;

import com.neildg.gamesofthegenerals.atlases.MilitarySymbolsAtlas;
import com.neildg.gamesofthegenerals.core.AssetLoader;

/**
 * References of the piece hieararchy.
 * @author user
 *
 */
public class PieceHierarchy {

	public final static int FLAG = 0,
			PRIVATE = 1,
			SEARGENT = 2,
			SECOND_LIEUTENANT = 3,
			FIRST_LIEUTENANT = 4,
			CAPTAIN = 5,
			MAJOR = 6,
			LT_COLONEL = 7,
			COLONEL = 8,
			ONE_STAR_GENERAL = 9,
			TWO_STAR_GENERAL = 10,
			THREE_STAR_GENERAL = 11,
			FOUR_STAR_GENERAL = 12,
			FIVE_STAR_GENERAL = 13,
			SPY = 14,
			UNKNOWN = 15;
	
	public final static String FLAG_STRING = "Flag",
				PRIVATE_STRING = "Private",
				SEARGENT_STRING = "Seargent",
				SECOND_LIEUTENANT_STRING = "2nd Lieutenant",
				FIRST_LIEUTENANT_STRING = "1st Lieutenant",
				CAPTAIN_STRING = "Captain",
				MAJOR_STRING = "Major",
				LT_COLONEL_STRING = "Lt. Colonel",
				COLONEL_STRING = "Colonel",
				ONE_STAR_GENERAL_STRING = "One Star General",
				TWO_STAR_GENERAL_STRING = "Two Star General",
				THREE_STAR_GENERAL_STRING = "Three Star General",
				FOUR_STAR_GENERAL_STRING = "Four Star General",
				FIVE_STAR_GENERAL_STRING = "Five Star General",
				SPY_STRING = "Spy";
				
				
	
	public final static int KIND_OF_PIECES = 15;
	
	private final static String TAG = "PieceHieararchy";	
	
	public static ITextureRegion getCorrespondingPieceTexture(int pieceType) {
		
		switch(pieceType) {
			case FLAG : return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.FLAG_ID);
			case PRIVATE: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.PRIVATE_ID);
			case SEARGENT: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.SERGEANT_ID);
			case SECOND_LIEUTENANT: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.SECOND_LIEUTENANT_ID);
			case FIRST_LIEUTENANT: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.LIEUTENANT_ID);
			case CAPTAIN: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.CAPTAIN_ID);
			case MAJOR: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.MAJOR_ID);
			case LT_COLONEL: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.LT_COLONEL_ID);
			case COLONEL: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.COLONEL_ID);
			case ONE_STAR_GENERAL: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.ONE_STAR_GENERAL_ID);
			case TWO_STAR_GENERAL: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.TWO_STAR_GENERAL_ID);
			case THREE_STAR_GENERAL: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.THREE_STAR_GENERAL_ID);
			case FOUR_STAR_GENERAL: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.FOUR_STAR_GENERAL_ID);
			case FIVE_STAR_GENERAL: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.FIVE_STAR_GENERAL_ID);
			case SPY: return AssetLoader.getInstance().getTextureFromAtlas(MilitarySymbolsAtlas.ATLAS_NAME, MilitarySymbolsAtlas.SPY_ID);
			
			default: Log.e(TAG, "Piece type of ID " +pieceType+ " not found!"); return null;
		}
	}
	
	public static int getCorrespondingPieceAtlasID(int pieceType) {
		
		switch(pieceType) {
			case FLAG : return MilitarySymbolsAtlas.FLAG_ID;
			case PRIVATE:  return MilitarySymbolsAtlas.PRIVATE_ID;
			case SEARGENT: return MilitarySymbolsAtlas.SERGEANT_ID;
			case SECOND_LIEUTENANT: return MilitarySymbolsAtlas.SECOND_LIEUTENANT_ID;
			case FIRST_LIEUTENANT: return MilitarySymbolsAtlas.LIEUTENANT_ID;
			case CAPTAIN: return MilitarySymbolsAtlas.CAPTAIN_ID;
			case MAJOR: return MilitarySymbolsAtlas.MAJOR_ID;
			case LT_COLONEL: return MilitarySymbolsAtlas.LT_COLONEL_ID;
			case COLONEL: return MilitarySymbolsAtlas.COLONEL_ID;
			case ONE_STAR_GENERAL: return MilitarySymbolsAtlas.ONE_STAR_GENERAL_ID;
			case TWO_STAR_GENERAL: return MilitarySymbolsAtlas.TWO_STAR_GENERAL_ID;
			case THREE_STAR_GENERAL: return MilitarySymbolsAtlas.THREE_STAR_GENERAL_ID;
			case FOUR_STAR_GENERAL: return MilitarySymbolsAtlas.FOUR_STAR_GENERAL_ID;
			case FIVE_STAR_GENERAL: return MilitarySymbolsAtlas.FIVE_STAR_GENERAL_ID;
			case SPY: return MilitarySymbolsAtlas.SPY_ID;
			case UNKNOWN: return MilitarySymbolsAtlas.UNKNOWN_ID;
			default: Log.e(TAG, "Piece type of ID " +pieceType+ " not found!"); return 99;
		}
	}
	
	public static int getCorrespondingNumberOfPieces(int pieceType) {
		switch(pieceType) {
			case FLAG : return 1;
			case PRIVATE: return 6;
			case SEARGENT: return 1;
			case SECOND_LIEUTENANT: return 1;
			case FIRST_LIEUTENANT: return 1;
			case CAPTAIN: return 1;
			case MAJOR: return 1;
			case LT_COLONEL: return 1;
			case COLONEL: return 1;
			case ONE_STAR_GENERAL: return 1;
			case TWO_STAR_GENERAL: return 1;
			case THREE_STAR_GENERAL: return 1;
			case FOUR_STAR_GENERAL: return 1;
			case FIVE_STAR_GENERAL: return 1;
			case SPY: return 2;
			
			default: Log.e(TAG, "Piece type of ID " +pieceType+ " not found!"); return 0;
		}
	}
	
	public static String getPieceLabel(int pieceType) {
		switch(pieceType) {
			case FLAG : return FLAG_STRING;
			case PRIVATE: return PRIVATE_STRING;
			case SEARGENT: return SEARGENT_STRING;
			case SECOND_LIEUTENANT: return SECOND_LIEUTENANT_STRING;
			case FIRST_LIEUTENANT: return FIRST_LIEUTENANT_STRING;
			case CAPTAIN: return CAPTAIN_STRING;
			case MAJOR: return MAJOR_STRING;
			case LT_COLONEL: return LT_COLONEL_STRING;
			case COLONEL: return COLONEL_STRING;
			case ONE_STAR_GENERAL: return ONE_STAR_GENERAL_STRING;
			case TWO_STAR_GENERAL: return TWO_STAR_GENERAL_STRING;
			case THREE_STAR_GENERAL: return THREE_STAR_GENERAL_STRING;
			case FOUR_STAR_GENERAL: return FOUR_STAR_GENERAL_STRING;
			case FIVE_STAR_GENERAL: return FIVE_STAR_GENERAL_STRING;
			case SPY: return SPY_STRING;
			
			default: Log.e(TAG, "Piece type of ID " +pieceType+ " not found!"); return "";
		}
	}
	
	public static int getMaxPiecesOfType(int pieceType) {
		if(pieceType == PRIVATE) {
			return 6;
		}
		else if(pieceType == SPY) {
			return 2;
		}
		else 
			return 1;
	}
	
	//the initial value will be the number of pieces a piece can eliminate. The actual value returned is the computed value based on the number of pieces
	//it can eliminate versus the number of pieces it meets. See Generals Game manual
	public static float getInitialHeuristic(int pieceType) {
		switch(pieceType) {
			case FIVE_STAR_GENERAL: return 7.80f;
			case FOUR_STAR_GENERAL: return 6.95f;
			case THREE_STAR_GENERAL: return 6.15f;
			case TWO_STAR_GENERAL: return 5.40f;
			case ONE_STAR_GENERAL: return 4.70f;
			case COLONEL: return 4.05f;
			case LT_COLONEL: return 3.45f;
			case MAJOR: return 2.90f;
			case CAPTAIN: return 2.40f;
			case FIRST_LIEUTENANT: return 1.95f;
			case SECOND_LIEUTENANT: return 1.55f;
			case SEARGENT: return 1.20f;
			case PRIVATE: return 1.37f;
			case SPY: return 7.50f;
			case FLAG: return 0.0f;
			default: Log.e(TAG, "Piece ID " +pieceType+ " not found!"); return 0.0f;
		}
	}
	
	//returns the number of pieces it can eliminate as seen in GG manual
	public static float getNumPiecesCanEliminate(int pieceType) {
		switch(pieceType) {
			case FIVE_STAR_GENERAL: return 18;
			case FOUR_STAR_GENERAL: return 17;
			case THREE_STAR_GENERAL: return 16;
			case TWO_STAR_GENERAL: return 15;
			case ONE_STAR_GENERAL: return 14;
			case COLONEL: return 13;
			case LT_COLONEL: return 12;
			case MAJOR: return 11;
			case CAPTAIN: return 10;
			case FIRST_LIEUTENANT: return 9;
			case SECOND_LIEUTENANT: return 8;
			case SEARGENT: return 7;
			case PRIVATE: return 3;
			case SPY: return 10;
			case FLAG: return 0;
			default: Log.e(TAG, "Piece ID " +pieceType+ " not found!"); return 0.0f;
		
		}
	}
	
}
