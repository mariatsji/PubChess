package net.groovygrevling.persistence;

import net.groovygrevling.PubChessActivity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PlayerTable {
	public static final String DB_TABLE_PLAYER = "player";
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_ELO = "elo";
	public static final String KEY_NROFMATCHES = "matches";
	public static final String KEY_CREATED = "created";
	private static final String CREATE_TABLE_PLAYER = "create table player"
			+ "(id text primary key not null,"
			+ "name text not null,"
			+ "description text not null,"
			+ "elo real not null,"
			+ "matches integer default 0,"
			+ "created text," 
			+ "luck integer);";
	
	public static void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE_PLAYER);
		Log.i(PubChessActivity.TAG, "Creating player table");
	}
	
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(PubChessActivity.TAG,"Upgrading table player from version"+ oldVersion+ " to version " 
			+ newVersion);
//		db.execSQL("DROP TABLE IF EXISTS player");
		onCreate(db);
	}
	
}
