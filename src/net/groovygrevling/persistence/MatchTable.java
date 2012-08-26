package net.groovygrevling.persistence;

import net.groovygrevling.PubChessActivity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MatchTable {
	public static final String DB_TABLE_MATCH = "match";
	public static final String KEY_ID = "id";
	public static final String KEY_WHITEPLAYER = "whiteplayer";
	public static final String KEY_BLACKPLAYER = "blackplayer";
	public static final String KEY_PLAYEDDATE = "playeddate";
	public static final String KEY_WHITEELOWHENPLAYED = "whiteelowhenplayed";
	public static final String KEY_BLACKELOWHENPLAYED = "blackelowhenplayed";
	public static final String KEY_TOURNAMENT = "tournament";
	public static final String KEY_RESULT = "result";
	private static final String CREATE_TABLE_MATCH = "create table match"
			+ "(id text primary key not null,"
			+ "whiteplayer text,"
			+ "blackplayer text,"
			+ "playeddate text,"
			+ "whiteelowhenplayed real,"
			+ "blackelowhenplayed real,"
			+ "tournament text,"
			+ "result integer,"
			+ "FOREIGN KEY (whiteplayer) REFERENCES player(id)," 
			+ "FOREIGN KEY (blackplayer) REFERENCES player(id),"
			+ "FOREIGN KEY (tournament) REFERENCES tournament(id));";
	
	public static void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE_MATCH);
		Log.i(PubChessActivity.TAG, "Creating match table");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(PubChessActivity.TAG,"Upgrading table tournament from version" + oldVersion + " to version " 
				+ newVersion);
		db.execSQL("DROP TABLE IF EXISTS match");
		onCreate(db);
	}
}
