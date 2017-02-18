/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.minimax;

import java.util.ArrayList;
import java.util.Random;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.board.BoardManager;
import com.neildg.gamesofthegenerals.entities.board.BoardPiece;
import com.neildg.gamesofthegenerals.entities.piececontroller.Player;
import com.neildg.gamesofthegenerals.entities.piececontroller.PlayerObserver;
import com.neildg.gamesofthegenerals.entities.stat.Statistics;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * The async task to perform the monte carlo search method
 * @author user
 *
 */
public class MonteCarloSearch extends AsyncTask<Object, Integer, Position> {

	private static final String TAG = "MonteCarloSearch";
	
	final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
	
	private BoardState bestState;
	private GameTreeGenerator gameTreeGenerator;
	private GameTree currentGameTree;
	
	private int searchTimes = 1000; //this can be tweaked in the future. may depend on AI level
	
	public MonteCarloSearch() {
		this.gameTreeGenerator = new GameTreeGenerator();
		
	}
	
	//assigns the last moved piece by the player, which will create the root node.
		public void assignLastMovedPiece(BoardPiece lastMovedPiece) {
			this.gameTreeGenerator.setLastMovedPiece(lastMovedPiece);
		}
	
	public void performMonteCarlo() {
		
		if(PlayerObserver.getInstance().getActivePlayer() != PlayerObserver.getInstance().getPlayerTwo()) {
			Log.e(TAG, "Player's move detected. Skipping tree search");
			return;
		}
		else{
			this.gameTreeGenerator.generateRootNode();
			this.currentGameTree = this.gameTreeGenerator.getCurrentGameTree();
			
			Player playerOne = PlayerObserver.getInstance().getPlayerOne();
			Player playerTwo = PlayerObserver.getInstance().getPlayerTwo();
			
			ArrayList<BoardState> possibleComputerStates = this.gameTreeGenerator.exploreNextMoves(this.currentGameTree.getRoot(), playerTwo);
			ArrayList<BoardState> selectedComputerStates = new ArrayList<BoardState>();
			
			for(int i = 0; i < this.searchTimes; i++) {
				//find a random computer move CM
				BoardState computerState = this.selectRandomBoardState(possibleComputerStates);
				selectedComputerStates.add(computerState);
				
				computerState.computeHeuristic();
				float evalScore = computerState.getHeuristic();
				computerState.addMonteCarloScore(evalScore);
				
				//find a random human move against CM
				/*ArrayList<BoardState> possibleHumanMoves = this.gameTreeGenerator.exploreNextMoves(computerState, playerOne);
				if(possibleHumanMoves.size() > 0) {
					BoardState randomHumanMove = this.selectRandomBoardState(possibleHumanMoves);
					
					//store resulting board score after performing human moves against computer move
					randomHumanMove.computeHeuristic();
					float evalScore = randomHumanMove.getHeuristic();		
					computerState.addMonteCarloScore(evalScore);
				}
				else {
					//human has no pieces left, or flag is captured. we add 999.0f to score
					computerState.addMonteCarloScore(BoardEvaluator.WIN_LOSS_VALUE);
				}*/
					
				/*//replace best state if a higher monte carlo score is found
				if(this.bestState == null || this.bestState.getMonteCarloScore() < computerState.getMonteCarloScore()) {
					this.bestState = computerState;
				}*/
			}
			
			//we find the best median score among selected computer states
			for(BoardState generatedStates : selectedComputerStates) {
				if(this.bestState == null || this.bestState.getMeanMonteCarlo() < generatedStates.getMeanMonteCarlo()) {
					this.bestState = generatedStates;
				}
			}
		}
		
	}
	
	private BoardState selectRandomBoardState(ArrayList<BoardState> boardStateList) {
		Random rand = new Random();
		return boardStateList.get(rand.nextInt(boardStateList.size()));
	}
	@Override
	protected Position doInBackground(Object... params) {
		Log.v(TAG , "Executing monte carlo");
		this.publishProgress(30);
		
		this.performMonteCarlo();
		return null;
	}
	
	@Override
	protected void onProgressUpdate(final Integer... progress) {
		NotificationCenter.getInstance().postNotification(Notifications.ON_WAITING_PLAYER_TURN, this);
		//Toast.makeText(runningActivity, "Move searching... ", Toast.LENGTH_LONG).show();
    }
	
	@Override
	protected void onPostExecute(Position bestPosition) {
		
		if(PlayerObserver.getInstance().getActivePlayer() != PlayerObserver.getInstance().getPlayerTwo()) {
			return;
		}
		
	   // execution of result of Long time consuming operation
	   BoardManager.getInstance().applyBoardState(this.bestState);
	   Log.v(TAG, "Finished monte-carlo execution");
	   
	   
	   NotificationCenter.getInstance().postNotification(Notifications.ON_FINISHED_PLAYER_TURN_COMPUTER, this);
	  }

}
