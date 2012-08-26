/**
 * 
 */
package net.groovygrevling.persistence;

import net.groovygrevling.PubChessActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author skvate
 *
 */
public class PubChessDbHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "applicationdata";
	private static final int DATABASE_VERSION = 1;
	
	public PubChessDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	} 

	@Override
	public void onCreate(SQLiteDatabase db) {
		PlayerTable.onCreate(db);
		MatchTable.onCreate(db);
		TournamentTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PubChessActivity.TAG, "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
//		PlayerTable.onUpgrade(db, oldVersion, newVersion);
//		MatchTable.onUpgrade(db, oldVersion, newVersion);
//		TournamentTable.onUpgrade(db, oldVersion, newVersion);
	}	
}
