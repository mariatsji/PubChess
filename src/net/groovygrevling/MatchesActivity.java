package net.groovygrevling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.groovygrevling.model.Match;
import net.groovygrevling.model.Player;
import net.groovygrevling.model.Tournament;
import net.groovygrevling.persistence.DbAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MatchesActivity extends Activity {

	public static final String TAG = MatchesActivity.class.getName();
	private Tournament runningTournament = null;
	private ArrayList<Match> matches = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	private static final int MATCHES_VIEW_INT_ID = 12000;
	private static final int RADIO_BUTTONS_OFFSET = 900;
	private static final int RADIOGROUP_OFFSET = 200;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		runningTournament = ListAllTournamentsActivity.getRunningTournament();
		Log.d(TAG, "Matches activity");
		setContentView(R.layout.matches);
		View view = this.findViewById(R.id.matchesTitle);
		TextView title = null;
		if(view != null && view instanceof TextView){
			title = (TextView) view;
			Log.d(TAG, "Title " + title + " sdf " + sdf.toString() + " tourn: " + runningTournament.getDescription());
			title.setText(runningTournament.getDescription() + " " + sdf.format(runningTournament.getPlayedDate()));
		}
		drawMatches();
	}
	
	public void drawMatches(){
		matches = runningTournament.getMatches();
		View layout = findViewById(R.id.matchesInnerPane);
		if(layout != null && layout instanceof ScrollView){
			ScrollView scrollableLayout = (ScrollView) layout;
			RelativeLayout matchesLayout = new RelativeLayout(this);
			scrollableLayout.addView(matchesLayout);
			for(int i = 0 ; i < matches.size() ; i ++){
				Match match = matches.get(i);
				Log.d(PubChessActivity.TAG, "Adding match TextView " + match.toString());
				RelativeLayout.LayoutParams textMatchLayout = new RelativeLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				if(i==0)
					textMatchLayout.addRule(RelativeLayout.BELOW, R.id.matchesTitle);
				else{
					textMatchLayout.addRule(RelativeLayout.BELOW, MATCHES_VIEW_INT_ID + RADIO_BUTTONS_OFFSET + RADIOGROUP_OFFSET + i- 1);
				}
				textMatchLayout.setMargins(1, 20, 15, 1);
				TextView textView = new TextView(this);
				textView.setId(MATCHES_VIEW_INT_ID + i);
				textView.setText(match.getWhite().getName() + " (" + (int)match.getWhite().getCurrentElo() + ") - " + match.getBlack().getName() + " (" + (int)match.getBlack().getCurrentElo() +")");
				matchesLayout.addView(textView, textMatchLayout);
				
				//Draw RadioGroup (result)
				RadioGroup radGroup = new RadioGroup(this);
				radGroup.setOrientation(RadioGroup.HORIZONTAL);
				RadioButton whiteWinButton = new RadioButton(this);
				whiteWinButton.setText("");
				whiteWinButton.setId(MATCHES_VIEW_INT_ID + RADIO_BUTTONS_OFFSET + 1 + (i*3));
				RadioButton remisButton = new RadioButton(this);
				remisButton.setText("");
				remisButton.setId(MATCHES_VIEW_INT_ID + RADIO_BUTTONS_OFFSET + 0 + (i*3));
				RadioButton blackWinButton = new RadioButton(this);
				blackWinButton.setText("");
				blackWinButton.setId(MATCHES_VIEW_INT_ID + RADIO_BUTTONS_OFFSET + 2 + (i*3));
				RelativeLayout.LayoutParams leftmostButton = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				leftmostButton.addRule(RelativeLayout.ALIGN_LEFT);
				RelativeLayout.LayoutParams midButton = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				midButton.addRule(RelativeLayout.RIGHT_OF, MATCHES_VIEW_INT_ID + RADIO_BUTTONS_OFFSET + 0 + i);
				RelativeLayout.LayoutParams rightmostButton = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				rightmostButton.addRule(RelativeLayout.RIGHT_OF, MATCHES_VIEW_INT_ID + RADIO_BUTTONS_OFFSET + 1 + i);
				radGroup.addView(whiteWinButton, leftmostButton);
				radGroup.addView(remisButton, midButton);
				radGroup.addView(blackWinButton, rightmostButton);
				radGroup.setId(MATCHES_VIEW_INT_ID + RADIO_BUTTONS_OFFSET + RADIOGROUP_OFFSET + i);
				RelativeLayout.LayoutParams radGroupLayoutParams = new RelativeLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				if(i==0)
					radGroupLayoutParams.addRule(RelativeLayout.BELOW, R.id.matchesTitle);
				else
					radGroupLayoutParams.addRule(RelativeLayout.BELOW, MATCHES_VIEW_INT_ID + RADIO_BUTTONS_OFFSET + RADIOGROUP_OFFSET + i - 1);
				radGroupLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				matchesLayout.addView(radGroup, radGroupLayoutParams);
			}	
		}
	}
	
	public void finishTournamentButton(View view){
		//collect matches and results
		int foundResults = 0;
		for(int i = 0 ; i < matches.size() ; i ++){
			View v = findViewById(MATCHES_VIEW_INT_ID + RADIO_BUTTONS_OFFSET + RADIOGROUP_OFFSET + i);
			if(v!=null && v instanceof RadioGroup){
				RadioGroup radGroup = (RadioGroup) v;
				int resultButton = radGroup.getCheckedRadioButtonId();
				View b = findViewById(resultButton);
				if(b!=null && b instanceof Button){
					foundResults++;
					Match thisMatch = matches.get(i);
					int result = resultButton - MATCHES_VIEW_INT_ID - RADIO_BUTTONS_OFFSET - (i*3);
					Log.i(TAG, "Setting result : " + result);
					thisMatch.setResult(result);
				} else {
					Log.w(TAG, "Error finding button view from ID " + resultButton);
				}
			} else {
				Log.w(TAG, "error finding matches radio group view by ID " + MATCHES_VIEW_INT_ID + RADIO_BUTTONS_OFFSET + RADIOGROUP_OFFSET + i);
			}
		}
		if(foundResults==matches.size()){
			//update players match-stats
			for(Match m : matches){
				int whiteNr = m.getWhite().getNrOfMatches();
				m.getWhite().setNrOfMatches(whiteNr + 1);
				int blackNr = m.getBlack().getNrOfMatches();
				m.getBlack().setNrOfMatches(blackNr + 1);
			}
			//persist and update
			DbAdapter db = new DbAdapter(this);
			persistTournament(db);
			persistMatches(db);
			persistPlayers(db);
			Intent intent = new Intent(this, ResultActivity.class);
			startActivity(intent);
		}
	}

	public void cancelTournamentButton(View view){
		//delete tournament
		Intent intent = new Intent(this, PubChessActivity.class);
    	startActivity(intent);
		setContentView(R.layout.main);
	}
	
	private void persistTournament(DbAdapter db){
		if(runningTournament != null){
			db.persist(runningTournament);
		}
			
	}
	
	private void persistMatches(DbAdapter db){
		for(Match m : matches){
			if(m!=null){
				db.persist(m, runningTournament.getId());
			}
		}
	}
	
	private void persistPlayers(DbAdapter db){
		for(Player p : runningTournament.getPlayers()){
			if(p!=null){
				db.persist(p);
			}
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
