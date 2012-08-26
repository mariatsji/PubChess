package net.groovygrevling;

import java.text.SimpleDateFormat;
import java.util.List;

import net.groovygrevling.model.Match;
import net.groovygrevling.model.Result;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class TournamentDetailsActivity extends Activity {

	public static final String TAG = TournamentDetailsActivity.class.getName();
	private Tournament tournament = null;
	private DbAdapter db = null;
	private static final int VIEW_OFFSET = 1740;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.tournamentdetails);
		db = new DbAdapter(this);
		Bundle extras = getIntent().getExtras();
		String tournamentId = extras.getString("tournamentId");
		tournament = db.getTournamentFromId(tournamentId);
		drawTournamentDetails();
	}
	
	public void tournamentDetailsBackButton(View view){
		Intent intent = new Intent(this, ListAllTournamentsActivity.class);
		startActivity(intent);
	}
	
	public void drawTournamentDetails(){
		Log.v(TAG, "Drawing info on tournament : " + tournament);
		View base = findViewById(R.id.tournamentDetailsInnerPane);
		RelativeLayout relativeLayout = null;
		if(base!=null){
			if(base instanceof ScrollView){
				ScrollView scrollView = (ScrollView)base;
				relativeLayout = new RelativeLayout(this);
				scrollView.addView(relativeLayout);
				//name
				TextView tournamentDesc = new TextView(this);
				tournamentDesc.setId(VIEW_OFFSET);
				tournamentDesc.setText(tournament.getDescription());
				tournamentDesc.setTextAppearance(this, android.R.attr.textAppearanceLarge);
				RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layout.addRule(RelativeLayout.BELOW, R.id.tournamentDetailsTitle);
				layout.addRule(RelativeLayout.ALIGN_LEFT);
				relativeLayout.addView(tournamentDesc, layout);
				//desc
				TextView pltournamentDate = new TextView(this);
				pltournamentDate.setId(VIEW_OFFSET + 1);
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
				pltournamentDate.setText(sdf.format(tournament.getPlayedDate()));
				pltournamentDate.setTextAppearance(this, android.R.attr.textAppearanceLarge);
				RelativeLayout.LayoutParams layoutDesc = new RelativeLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutDesc.addRule(RelativeLayout.BELOW, VIEW_OFFSET);
				layoutDesc.addRule(RelativeLayout.ALIGN_LEFT);
				relativeLayout.addView(pltournamentDate, layoutDesc);
				//statistics in a single view
				TextView tournamentStats = new TextView(this);
				tournamentStats.setId(VIEW_OFFSET + 2);
				tournamentStats.setText(getStatisticsString());
				tournamentStats.setTextAppearance(this, android.R.attr.textAppearanceLarge);
				RelativeLayout.LayoutParams layoutStats = new RelativeLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutStats.addRule(RelativeLayout.BELOW, VIEW_OFFSET + 1);
				layoutStats.addRule(RelativeLayout.ALIGN_LEFT);
				relativeLayout.addView(tournamentStats, layoutStats);
			}
		}
		drawAllMatchesWithResult(relativeLayout);
	}
	
	private String getStatisticsString(){
		return "-----";
	}
	
	private void drawAllMatchesWithResult(View  base){
		if(base!=null && base instanceof RelativeLayout){
			RelativeLayout relativeLayout = (RelativeLayout) base;
			List<Match> matches = tournament.getMatches();
			Log.d(TAG, "Found matches : " + tournament.getMatches().size());
			for(int i = 0 ; i < matches.size() ; i++){
				Match m = matches.get(i);
				TextView textView = new TextView(this);
				textView.setId(VIEW_OFFSET + 3 + i);
				String matchText = m.getWhite().getName() + " - " + m.getBlack().getName();
				if(m.getResult()==Result.WHITE_WIN.getValue())
					matchText += " 1 - 0";
				else if (m.getResult()==Result.REMIS.getValue())
					matchText += " \u00BD - \u00BD";
				else if (m.getResult()==Result.BLACK_WIN.getValue())
					matchText += " 0 - 1";
				textView.setText(matchText);
				RelativeLayout.LayoutParams layoutStats = new RelativeLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutStats.addRule(RelativeLayout.BELOW, VIEW_OFFSET + 3 + i - 1);
				layoutStats.addRule(RelativeLayout.ALIGN_LEFT);
				relativeLayout.addView(textView, layoutStats);
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
