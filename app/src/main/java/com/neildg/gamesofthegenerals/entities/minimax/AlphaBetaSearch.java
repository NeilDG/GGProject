/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.minimax;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.board.BoardManager;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * This singleton class performs the Alpha-Beta pruning search in the game tree.
 * @author user
 *
 */
public class AlphaBetaSearch extends AsyncTask<Object, Integer, Position>{

	private static AlphaBetaSearch sharedInstance = null;
	private static String TAG = "AlphaBetaSearch";
	
	private GameTreeGenerator gameTreeGenerator;
	private GameTree currentGameTree;
	
	private float alpha;
	private float beta;
	
	private BoardState bestState;
	
	final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
	
	public AlphaBetaSearch() {
		this.gameTreeGenerator = new GameTreeGenerator();

		this.alpha = Float.NEGATIVE_INFINITY;
		this.beta = Float.POSITIVE_INFINITY;
	}
	
	//called by async task to search for the best move in the game tree.
	//generates the root node and its branches according to ply depth
	public void searchGameTree() {
		
		if(PlayerObserver.getInstance().getActivePlayer() != PlayerObserver.getInstance().getPlayerTwo()) {
			Log.e(TAG, "Player's move detected. Skipping tree search");
			return;
		}
		else {
			this.gameTreeGenerator.generateRootNode();
			this.currentGameTree = this.gameTreeGenerator.getCurrentGameTree();
			
			this.bestState = this.gameTreeGenerator.alphaBeta(this.currentGameTree.getRoot(), 1, alpha, beta);
			Log.v(TAG, "Best state ALPHA BETA: " +this.bestState.getHeuristic());
		}
		
	}

	@Override
	protected Position doInBackground(Object... arg0) {
		Position bestPosition = null;
		
		this.publishProgress(30);
		Log.v(TAG, "Executing");
		this.searchGameTree();
		
		Log.v(TAG, "Alpha: " +this.alpha+ " Beta: " +this.beta);
		
		
		return bestPosition;
	}
	
	@Override
	protected void onProgressUpdate(final Integer... progress) {
		NotificationCenter.getInstance().postNotification(Notifications.ON_WAITING_PLAYER_TURN, this);
    }
	
	@Override
	protected void onPostExecute(Position bestPosition) {
		
		if(PlayerObserver.getInstance().getActivePlayer() != PlayerObserver.getInstance().getPlayerTwo()) {
			return;
		}
		
	   // execution of result of Long time consuming operation
	   BoardManager.getInstance().applyBoardState(this.bestState);
	   NotificationCenter.getInstance().postNotification(Notifications.ON_FINISHED_PLAYER_TURN_COMPUTER, this);
	   Log.v(TAG, "Finished alpha-beta execution");
	  }
	
}
