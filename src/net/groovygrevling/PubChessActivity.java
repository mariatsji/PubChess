package net.groovygrevling;

import net.groovygrevling.persistence.DbAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * 
 * @author sjur
 *
 */
public class PubChessActivity extends Activity {
	
	public static final String TAG = PubChessActivity.class.getName();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting PubChessActivity application");	
        setContentView(R.layout.main);
    }
    
    public void playerButton(View view){
    	Intent intent = new Intent(PubChessActivity.this, ListAllPlayersActivity.class);
    	startActivity(intent);
    	setContentView(R.layout.players);
    }
    
    public void tournamentButton(View view){
    	Intent intent = new Intent(PubChessActivity.this, ListAllTournamentsActivity.class);
    	startActivity(intent);
    	setContentView(R.layout.tournaments);
    }
    
    private void sendEmail(String content){
    	final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent.setType("plain/text");
    	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "sjur.millidahl@gmail.com"});
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Pub Chess DB Content");
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
    	this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	Log.d("PubChessActivity", "Menu button pressed");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pubchess_menu, menu);
        return true;
    }
    
    private void dumpTables(){
    	DbAdapter db = new DbAdapter(this);
    	String sql = db.dumpDatabases();
    	sendEmail(sql);
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
            case R.id.dumpTables:
            	dumpTables();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}