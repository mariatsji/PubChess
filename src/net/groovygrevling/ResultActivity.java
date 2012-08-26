package net.groovygrevling;

import java.util.ArrayList;

import net.groovygrevling.model.Match;
import net.groovygrevling.model.Player;
import net.groovygrevling.model.Result;
import net.groovygrevling.model.Tournament;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ResultActivity extends Activity {

	public static final String TAG = ResultActivity.class.getCanonicalName();
	private Tournament runningTournament = null;
	private ArrayList<Player> players = null;
	private static final int RESULT_ID_OFFSET = 2300;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		drawResult();
	}

	public void resultHomeButton(View view) {
		Intent intent = new Intent(this, PubChessActivity.class);
		startActivity(intent);
		setContentView(R.layout.main);
	}

	public void drawResult() {
		runningTournament = ListAllTournamentsActivity.getRunningTournament();
		players = runningTournament.getPlayers();
		ArrayList<Match> matches = runningTournament.getMatches();

		for (Match m : matches) {
			if (m.getResult() == Result.WHITE_WIN.getValue()) {
				m.getWhite().increasePointsFromWin();
				Log.d(TAG, "Giving pint to white : " + m.getWhite());
			} else if (m.getResult() == Result.REMIS.getValue()){
				m.getWhite().incrasePointsFromRemis();
				Log.d(TAG, "Giving half a point to white : " + m.getWhite());
				m.getBlack().incrasePointsFromRemis();
				Log.d(TAG, "Giving half a point to black : " + m.getBlack());
			} else if (m.getResult() == Result.BLACK_WIN.getValue()){
				m.getBlack().increasePointsFromWin();
				Log.d(TAG, "Giving point to black : " + m.getBlack());
			} else {
				Log.w(TAG, "Unable to find correct result for match : " + m.getId() + " where result gives : " + m.getResult());
			}
		}
		
		View scrollView = findViewById(R.id.resultsInnerPane);
		if(scrollView !=null && scrollView instanceof ScrollView){
			ScrollView innerPane = (ScrollView) scrollView;
			RelativeLayout relativeLayout = new RelativeLayout(this);
			innerPane.addView(relativeLayout);
			RelativeLayout.LayoutParams resultLayout = new RelativeLayout.LayoutParams(
	                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			resultLayout.addRule(RelativeLayout.BELOW, R.id.resultTitle);
			for(int i = 0 ; i < players.size() ; i ++){
				resultLayout = new RelativeLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				TextView textView = new TextView(this);
				textView.setId(RESULT_ID_OFFSET + i);
				Player p = players.get(i);
				textView.setText(p.getName() + " score [" + p.getTournamentPoints() + "] elo [" + (int) p.getCurrentElo()+"]");
				if(i!=0)
					resultLayout.addRule(RelativeLayout.BELOW, RESULT_ID_OFFSET + i -1);
				relativeLayout.addView(textView, resultLayout);
			}
		}
	}
	
	public String[][] updateScore(String[][] tmpResult, String playerId, float score){
		String[][] retVal = new String[tmpResult.length][2];
		for(int i = 0 ; i < tmpResult.length; i++){
			Log.d(TAG, "playerid : " + tmpResult[i][0]);
		}
		for(int i = 0 ; i < retVal.length ; i ++){
			if(tmpResult[i][0].equals(playerId)){
				float oldRes = Float.valueOf(tmpResult[i][1]);
				tmpResult[i][1] = String.valueOf(oldRes + score);
			}
		}
		return retVal;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	Log.d("PubChessActivity", "Menu button pressed");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pubchess_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exitMenu:
            	Intent intent = new Intent(Intent.ACTION_MAIN);
            	intent.addCategory(Intent.CATEGORY_HOME);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
}
