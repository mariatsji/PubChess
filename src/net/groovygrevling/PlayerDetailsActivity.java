package net.groovygrevling;

import java.util.List;

import net.groovygrevling.model.Match;
import net.groovygrevling.model.Player;
import net.groovygrevling.model.Result;
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

public class PlayerDetailsActivity extends Activity {

	private Player player = null;
	private DbAdapter db = null;
	private static final int VIEW_OFFSET = 1380;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.playerdetails);
		db = new DbAdapter(this);
		Bundle extras = getIntent().getExtras();
		String playerId = extras.getString("playerId");
		player = db.getPlayer(playerId);
		ListAllPlayersActivity.getProgressBar().setProgress(1);
		drawInfoOnPlayer();
	}
	
	private void drawInfoOnPlayer(){
		View base = findViewById(R.id.playerDetailsInnerPane);
		if(base!=null){
			if(base instanceof ScrollView){
				ScrollView scrollView = (ScrollView)base;
				RelativeLayout relativeLayout = new RelativeLayout(this);
				scrollView.addView(relativeLayout);
				//name
				TextView playerName = new TextView(this);
				playerName.setId(VIEW_OFFSET);
				playerName.setText("Player : " + player.getName());
				playerName.setTextAppearance(this, android.R.attr.textAppearanceLarge);
				RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layout.addRule(RelativeLayout.BELOW, R.id.playerDetailsTitle);
				layout.addRule(RelativeLayout.ALIGN_LEFT);
				relativeLayout.addView(playerName, layout);
				//desc
				TextView playerDesc = new TextView(this);
				playerDesc.setId(VIEW_OFFSET + 1);
				playerDesc.setText(player.getDescription());
				playerDesc.setTextAppearance(this, android.R.attr.textAppearanceLarge);
				RelativeLayout.LayoutParams layoutDesc = new RelativeLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutDesc.addRule(RelativeLayout.BELOW, VIEW_OFFSET);
				layoutDesc.addRule(RelativeLayout.ALIGN_LEFT);
				relativeLayout.addView(playerDesc, layoutDesc);
				//statistics in a single view
				TextView playerStats = new TextView(this);
				playerStats.setId(VIEW_OFFSET + 2);
				playerStats.setText(getStatisticsString());
				playerStats.setTextAppearance(this, android.R.attr.textAppearanceLarge);
				RelativeLayout.LayoutParams layoutStats = new RelativeLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutStats.addRule(RelativeLayout.BELOW, VIEW_OFFSET + 1);
				layoutStats.addRule(RelativeLayout.ALIGN_LEFT);
				relativeLayout.addView(playerStats, layoutStats);
			}
		}
		ListAllPlayersActivity.getProgressBar().setProgress(2);
	}
	
	public void playersBackButton(View view){
		Intent intent = new Intent(this, ListAllPlayersActivity.class);
		startActivity(intent);
	}
	
	private String getStatisticsString(){
		String retVal = "";
		retVal += "Elo : " + (int) player.getCurrentElo() + "\n";
		//matches
		List<Match> matches = db.getAllMatchesForPlayer(player);
		ListAllPlayersActivity.getProgressBar().setMax(matches.size() + 2);
		retVal +=  "Matches : " + matches.size() + "\n";
		int whitewins = 0;
		int blackwins = 0;
		int remis = 0;
		int whiteloss = 0;
		int blackloss = 0;
		int i = 0;
		for(Match m : matches){
			if(m.getWhite().getId().equals(player.getId())){
				if(m.getResult()==Result.WHITE_WIN.getValue())
					whitewins++;
				else if(m.getResult()==Result.REMIS.getValue())
					remis++;
				else if(m.getResult()==Result.BLACK_WIN.getValue())
					whiteloss++;
			} else if(m.getBlack().getId().equals(player.getId())){
				if(m.getResult()==Result.WHITE_WIN.getValue())
					blackloss ++;
				else if(m.getResult()==Result.REMIS.getValue())
					remis++;
				else if(m.getResult()==Result.BLACK_WIN.getValue())
					blackwins++;
			}
			i++;
			ListAllPlayersActivity.getProgressBar().setProgress(i);
		}
		retVal+= "Wins : " + (whitewins + blackwins) + " (" + whitewins + " as white) \n";
		retVal+= "Remis: " + remis +"\n";
		retVal+= "Loss : " + (whiteloss + blackloss) + " (" + whiteloss + " as white) \n";
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
