package net.groovygrevling.persistence;



import net.groovygrevling.PubChessActivity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TournamentTable {
	public static final String DB_TABLE_TOURNAMENT = "tournament";
	public static final String KEY_ID = "id";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_PLAYEDDATE = "playeddate";
	public static final String KEY_DOUBLEROUNDROBIN = "doubleroundrobin";
	
	private static final String CREATE_TOURNAMENT_TABLE = "create table tournament"
			+ "(id text primary key not null,"
			+ "description text,"
			+ "playeddate text,"
			+ "doubleroundrobin integer);";
	
	public static void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_TOURNAMENT_TABLE);
		Log.i(PubChessActivity.TAG, "Creating tournament table");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(PubChessActivity.TAG,"Upgrading table tournament from version"+ oldVersion+ " to version " 
				+ newVersion);
		db.execSQL("DROP TABLE IF EXISTS tournament");
		onCreate(db);
	}
}
