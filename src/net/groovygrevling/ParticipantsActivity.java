package net.groovygrevling;

import java.util.ArrayList;
import java.util.List;

import net.groovygrevling.model.Match;
import net.groovygrevling.model.Player;
import net.groovygrevling.model.Tournament;
import net.groovygrevling.persistence.DbAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class ParticipantsActivity extends Activity {

	public static final String TAG = ParticipantsActivity.class.getName();
	protected DbAdapter dbAdapter = null;
	protected String[] playerIds;
	protected String[] playerNames; 
	protected boolean[] selections;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbAdapter = new DbAdapter(this);
		
		Object[] tmpPlayerNames = dbAdapter.getPlayerNames().toArray();
		playerNames = new String[tmpPlayerNames.length];
		for(int i = 0 ; i < tmpPlayerNames.length ; i ++){
			Log.i(TAG, "tmpPlayerNames : " + tmpPlayerNames[i]);
			playerNames[i] = (String)tmpPlayerNames[i];
		}
		
		List<String> dbPlayerIds = dbAdapter.getPlayerIds();
		Object[] tmpPlayerIds = new Object[dbPlayerIds.size()];
		if(dbPlayerIds != null){
			tmpPlayerIds = dbPlayerIds.toArray();
		}
		playerIds = new String[tmpPlayerNames.length];
		for(int i = 0 ; i < tmpPlayerIds.length ; i ++){
			Log.i(TAG, "playerIds : " + tmpPlayerIds[i]);
			playerIds[i] = (String)tmpPlayerIds[i];
		}
		selections = new boolean[playerIds.length];
		Log.d(PubChessActivity.TAG, "Creating Participants activity");
		setContentView(R.layout.participants);
		showDialog(0);
	}
	
	public void homeButton(View view){
		Intent intent = new Intent(ParticipantsActivity.this, PubChessActivity.class);
    	startActivity(intent);
		setContentView(R.layout.main);
	}
	
	public void startTournamentButton(View view){
		Intent intent = new Intent(ParticipantsActivity.this, MatchesActivity.class);
		startActivity(intent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
				.setTitle("Players")
				.setMultiChoiceItems(playerNames, selections,
						new DialogSelectionClickHandler())
				.setPositiveButton("OK", new DialogButtonClickHandler())
				.create();
	}

	public class DialogSelectionClickHandler implements
			DialogInterface.OnMultiChoiceClickListener {
		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			Log.i("ME", playerNames[clicked] + " selected: " + selected);
		}
	}

	public class DialogButtonClickHandler implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int clicked) {
			switch (clicked) {
			case DialogInterface.BUTTON_POSITIVE:
				addSelectedPlayersToRunningTournament();
				break;
			}
		}
	}

	protected void addSelectedPlayersToRunningTournament() {
		Tournament runningTournament = ListAllTournamentsActivity.getRunningTournament();
		for(int i = 0 ; i < playerIds.length; i++){
			Log.i(PubChessActivity.TAG, "playerId loopthrough : " + playerIds[i]);
		}
		for (int i = 0; i < playerIds.length; i++) {
			if(selections[i] && playerIds[i]!=null && !playerIds[i].equals("")){
				Log.i("ME", playerNames[i] + " selected: " + playerNames[i]);
				Player p = dbAdapter.getPlayerFromId(playerIds[i]);
				Log.d(PubChessActivity.TAG, "Adding player to tournament : " + p.getId());
				runningTournament.addPlayer(p);
			}
		}
		Log.d(PubChessActivity.TAG, "After adding all players, tournament now has " + runningTournament.getPlayers().size() + " players");
		if(runningTournament.isDoubleRoundRobin()){
			Log.d(PubChessActivity.TAG, "Drawring double round robin");
			runningTournament.drawDoubleRoundRobin();
		} else {
			Log.d(PubChessActivity.TAG, "Drawring single round robin");
			runningTournament.drawSingleRoundRobin();
		}
		Log.d(PubChessActivity.TAG, "After drawing matches, tournament now has " + runningTournament.getPlayers().size() + " players and " + runningTournament.getMatches().size() + " matches");
		ArrayList<Match> matches = runningTournament.getMatches();
		for(Match m : matches){
			Log.i(PubChessActivity.TAG, "Drew match between : " + m.getWhite().getName() + " (" + m.getWhite().getCurrentElo() + ") - " + m.getBlack().getName() + " (" + m.getBlack().getCurrentElo() + ")");
		}
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