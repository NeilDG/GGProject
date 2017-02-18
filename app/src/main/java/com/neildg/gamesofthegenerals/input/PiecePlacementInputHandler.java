/**
 * 
 */
package com.neildg.gamesofthegenerals.input;

import java.util.ArrayList;

import com.neildg.gamesofthegenerals.entities.board.BoardCreator;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;
import com.neildg.gamesofthegenerals.layout.pieceselection.PieceSelectionUI;

/**
 * Handles the behavior of input related to piece dragging during the piece placement scenario.
 * @author user
 *
 */
public class PiecePlacementInputHandler {

	private final static String TAG = "PiecePlacementInputHandler";
	
	private static PiecePlacementInputHandler sharedInstance = null;
	
	private BoardPiece selectedPiece;
	private boolean success = false;
	
	private ArrayList<IPieceDragOutside> pieceDragOutsideList;
	
	private PieceSelectionUI pieceSelectionUI;
	
	private PiecePlacementInputHandler() {
		this.pieceDragOutsideList = new ArrayList<IPieceDragOutside>();
	}
	
	public void reset() {
		this.clearPieceDragOutsideListeners();
		this.pieceSelectionUI = null;
	}
	
	public void setPieceSelectionUI(PieceSelectionUI pieceSelectionUI) {
		this.pieceSelectionUI = pieceSelectionUI;
	}
	
	public void addPieceDragOutsideListener(IPieceDragOutside pieceDragOutsideListener) {
		this.pieceDragOutsideList.add(pieceDragOutsideListener);
	}
	
	public void clearPieceDragOutsideListeners() {
		this.pieceDragOutsideList.clear();
	}
	
	public void reportSuccessfulPlacement(BoardPiece boardPiece) {
		this.success = true;
		
		Player activePlayer = PlayerObserver.getInstance().getActivePlayer();
		activePlayer.addAlivePiece(boardPiece);
		this.pieceSelectionUI.verifyIfAllPiecesPlaced();
	}
	
	public void reportFailedPlacement(BoardPiece boardPiece) {
		
		for(IPieceDragOutside listener : this.pieceDragOutsideList) {
			listener.onPieceDragOutside(boardPiece.getPieceType());
		}
		
		this.success = false;
		Player activePlayer = PlayerObserver.getInstance().getActivePlayer();
		activePlayer.removeAlivePiece(boardPiece);
		this.pieceSelectionUI.verifyIfAllPiecesPlaced();
	}
	
	public boolean isPlacementSuccessful() {
		return this.success;
	}
	
	//player holds the piece.
	public void holdPiece(BoardPiece boardPiece) {
		this.selectedPiece = boardPiece;
	}
	
	//player releases the piece
	public void releasePiece() {
		this.selectedPiece = null;
	}
	
	public BoardPiece getSelectedPiece() {
		return this.selectedPiece;
	}
	
	public static PiecePlacementInputHandler getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new PiecePlacementInputHandler();
		}
		
		return sharedInstance;
	}
}
