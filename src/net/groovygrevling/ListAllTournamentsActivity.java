package net.groovygrevling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.groovygrevling.model.Tournament;
import net.groovygrevling.persistence.DbAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

public class ListAllTournamentsActivity extends ListActivity {
	
	public static final String TAG = ListAllTournamentsActivity.class.getName();
	private static Tournament runningTournament = null;
	private DbAdapter db = null;
	private List<Tournament> tournaments = null;
	private List<String> tournamentNames = null;
//	private ProgressBar mProgress;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.tournaments);
//		mProgress = (ProgressBar) findViewById(R.id.progressBarTournament);
//		mProgress.setVisibility(View.VISIBLE);
//		mProgress.setProgress(0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		tournamentNames = new ArrayList<String>(); 
//		mProgress.setMax(tournamentNames.size());
		db = new DbAdapter(this);
		tournaments = db.getAllTournaments();
		
		for(int i = 0 ; i < tournaments.size(); i ++){
			tournamentNames.add(i, tournaments.get(i).getDescription() + " (" + sdf.format(tournaments.get(i).getPlayedDate()) + ")");
//			mProgress.setProgress(i);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, tournamentNames);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Log.v(TAG, "Clicked : " + item);
		int indexOfItem = tournamentNames.indexOf(item);
		Log.v(TAG, "found index of this tournament : " + indexOfItem);
		Tournament t = tournaments.get(indexOfItem);
		Intent intent = new Intent(this, TournamentDetailsActivity.class);
		intent.putExtra("tournamentId", t.getId());
		startActivity(intent);
	}

	public void homeButton(View view) {
		Intent intent = new Intent(ListAllTournamentsActivity.this, PubChessActivity.class);
    	startActivity(intent);
		setContentView(R.layout.main);
	}

	public void newTournamentButton(View view) {
		setContentView(R.layout.edittournament);
	}
	
	public void submitTournamentButton(View view){
		View enterName = this.findViewById(R.id.enterTournamentName);
    	String tournamentName = "";
    	if(enterName!=null && (enterName instanceof EditText)){
    		tournamentName = ((EditText)enterName).getText().toString();
    	}
    	View doubleRoundRobinView = this.findViewById(R.id.toggleRoundRobinButton);
    	boolean doubleRoundRobin = false;
    	if(doubleRoundRobinView!=null && (doubleRoundRobinView instanceof ToggleButton))
    			doubleRoundRobin = ((ToggleButton) doubleRoundRobinView).isChecked();
    	runningTournament = new Tournament(tournamentName, doubleRoundRobin);
    	Log.d(PubChessActivity.TAG, "Created new Tournament : " + runningTournament.getDescription());
//    	db.open();
//    	db.persist(runningTournament);
//    	db.close();
    	//add players to tournament
    	Intent intent = new Intent(ListAllTournamentsActivity.this, ParticipantsActivity.class);
    	startActivity(intent);
	}

	public static Tournament getRunningTournament(){
		return runningTournament;
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
            	db.close();
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
