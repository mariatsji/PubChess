package net.groovygrevling;

import java.util.ArrayList;

import net.groovygrevling.model.Player;
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
import android.widget.ProgressBar;

public class ListAllPlayersActivity extends ListActivity {

	public static final String TAG = ListAllPlayersActivity.class.getName();
	private ArrayAdapter<String> adapter = null;
	private ArrayList<Player> players = null;
	private ArrayList<String> playerNames = null;
	private DbAdapter db = null;
    private static ProgressBar mProgress;
	
    public static ProgressBar getProgressBar(){
    	return mProgress;
    }
    
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		db = new DbAdapter(this);
		players = (ArrayList<Player>) db.getAllPlayers();
		playerNames = new ArrayList<String>();
		for(int i = 0 ; i < players.size() ; i ++){
			playerNames.add(i, players.get(i).getName());
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playerNames);
		setListAdapter(adapter);
		setContentView(R.layout.players);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		mProgress = (ProgressBar) findViewById(R.id.progressBarPlayer);
		mProgress.setVisibility(View.VISIBLE);
		mProgress.setProgress(0);
		String item = (String) getListAdapter().getItem(position);
		Log.v(TAG, "Clicked on : " + item);
		int indexOfItem = playerNames.indexOf(item);
		Log.v(TAG, "found index of this player : " + indexOfItem);
		Player p = players.get(indexOfItem);
		Intent intent = new Intent(this, PlayerDetailsActivity.class);
		intent.putExtra("playerId", p.getId());
		startActivity(intent);
	}

	public void homeButton(View view) {
		Intent intent = new Intent(this, PubChessActivity.class);
    	startActivity(intent);
		setContentView(R.layout.main);
	}

	public void newPlayerButton(View view) {
		setContentView(R.layout.editplayer);
	}
	
    public void submitPlayerButton(View view){
    	View enterName = this.findViewById(R.id.enterName);
    	String name = "";
    	if(enterName!=null && (enterName instanceof EditText)){
    		name = ((EditText)enterName).getText().toString();
    	}
    	View enterDescription = this.findViewById(R.id.enterDescription);
    	String description = "";
    	if(enterDescription!=null && (enterDescription instanceof EditText)){
    		description = ((EditText)enterDescription).getText().toString();
    	}
    	Player p = null;
    	if(name!=null && name.length()>0){
    		if(description!=null && description.length()>0)
    			p = new Player(name, description);
    		else
    			p = new Player(name);
    	}
    	Log.d(PubChessActivity.TAG, "Created player " + p.toString());
    	db.persist(p);
    	adapter.add(p.getName());
    	setContentView(R.layout.players);
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
