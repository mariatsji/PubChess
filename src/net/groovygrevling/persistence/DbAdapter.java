package net.groovygrevling.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.groovygrevling.model.Match;
import net.groovygrevling.model.Player;
import net.groovygrevling.model.Tournament;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbAdapter implements PubChessPersistence {

	public static final String TAG = DbAdapter.class.getName();
	private SQLiteDatabase db;
	private PubChessDbHelper dbHelper;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	
	public DbAdapter(Context context) {
		dbHelper = new PubChessDbHelper(context);
	}
	
	public DbAdapter open() throws SQLException {
		if(dbHelper!=null)
			if(db==null){
				db = dbHelper.getWritableDatabase();
			} else {
				if(!db.isOpen())
					db = dbHelper.getWritableDatabase();
			}
		return this;
	}

	public void close() {
		if(dbHelper!=null)
			dbHelper.close();
		if(db!=null){
			if(db.isOpen())
				db.close();
		}
	}

	public void persist(Player p) {
		open();
		Cursor cursor = db.rawQuery("select * from player where id=?", new String[] {p.getId()});
		if(cursor.getCount() > 0){
			updatePlayer(p);
		}else{
			addPlayer(p);
		}
		if(cursor!=null)
			cursor.close();
		close();
	}
	
	public Player getPlayerFromId(String playerId){
		return getPlayer(playerId);
	}

	public void persist(Tournament t) {
		open();
		Cursor cursor = db.rawQuery("select * from tournament where id=?", new String[] {t.getId()});
		if(cursor.moveToFirst()){
			updateTournament(t);
		}else{
			addTournament(t);
		}if(cursor!=null)
			cursor.close();
		close();
	}

	public void persist(Match m, String tournamentId) {
		open();
		Cursor cursor = db.rawQuery("select * from match where id=?", new String[] {m.getId()});
		if(cursor.getCount() > 0){
			updateMatch(m, tournamentId);
		}else{
			addMatch(m, tournamentId);
		}
		if(cursor!=null)
			cursor.close();
		close();
	}

	public List<Player> getAllPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		ArrayList<String> playerIDs = (ArrayList<String>) getPlayerIds();
		if(playerIDs!=null && playerIDs.size()>0){
			for (String id : playerIDs) {
				players.add(getPlayer(id));
			}
		}
		return players;
	}

	public List<Tournament> getAllTournaments() {
		open();
		List<Tournament> tournamentList = new ArrayList<Tournament>();
		Cursor cursor = db.query(TournamentTable.DB_TABLE_TOURNAMENT, new String[] {TournamentTable.KEY_ID}, null, null, null, null, null);
		while(cursor.moveToNext()){
			tournamentList.add(getTournamentFromId(cursor.getString(cursor.getColumnIndex(TournamentTable.KEY_ID))));
		}
		close();
		if(cursor!=null)
			cursor.close();
		return tournamentList;
	}

	public List<Match> getAllMatchesInTournament(Tournament t) {
		ArrayList<Match> matches = new ArrayList<Match>();
		open();
		Cursor cursor = db.query(MatchTable.DB_TABLE_MATCH, new String[] {
				MatchTable.KEY_ID
				}, MatchTable.KEY_TOURNAMENT+ " = '" + t.getId() + "';", null, null, null, null);
		Log.v(TAG, "Found matches in db where TOURNAMENT = " + t.getId() + " : " + cursor.getCount());
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				String matchId = cursor.getString(0);
				Match m = getMatchFromId(matchId);
				Log.v(TAG, "Using matchid " + matchId + " inflated match " + m);
				matches.add(m);
			}
		}
		if(cursor!=null)
			cursor.close();
		close();
		return matches;
	}

	public List<Match> getAllMatchesForPlayer(Player p) {
		ArrayList<Match> retVal = new ArrayList<Match>();
		for(Tournament t : getAllTournaments()){
			for(Match m : getAllMatchesInTournament(t)){
				if(m.getWhite().getId().equals(p.getId())){
					retVal.add(m);
				}else if (m.getBlack().getId().equals(p.getId())){
					retVal.add(m);
				}
			}
		}
		return retVal;
	}


	public List<String> getPlayerDescription() {
		return null;
	}


	public void addPlayer(Player p) {
		open();
		ContentValues values = createPlayerContentValues(p);
		db.insert(PlayerTable.DB_TABLE_PLAYER, null, values);
		Log.i(TAG, "Inserted new player id " + p.getId());
		close();
	}
	
	/**
	 * Creates a player-object based on a playerID
	 * @param playerId
	 * @return A Player-object if ID exists in DB
	 */
	public Player getPlayer(String playerId) {
		open();
		Player retVal = null;
		Cursor cursor = db.query(PlayerTable.DB_TABLE_PLAYER, null, PlayerTable.KEY_ID + "='" + playerId + "';",
				null, null, null, null);
		if(cursor.getCount()>0){
			Log.i(TAG, "Found player in database with id [" + playerId +"]");
			cursor.moveToFirst();
			int idIndex = cursor.getColumnIndex(PlayerTable.KEY_ID);
			String playerDBId = cursor.getString(idIndex);
			int nameIndex = cursor.getColumnIndex(PlayerTable.KEY_NAME);
			String playerDBName = cursor.getString(nameIndex);
			int descriptionIndex = cursor.getColumnIndex(PlayerTable.KEY_DESCRIPTION);
			String playerDBDescription = cursor.getString(descriptionIndex);
			int eloIndex = cursor.getColumnIndex(PlayerTable.KEY_ELO);
			Double playerDBElo = cursor.getDouble(eloIndex);
			int matchesIndex = cursor.getColumnIndex(PlayerTable.KEY_NROFMATCHES);
			int matchesPlayed = cursor.getInt(matchesIndex);
			int createdIndex = cursor.getColumnIndex(PlayerTable.KEY_CREATED);
			String playerCreated = cursor.getString(createdIndex);
			Date created = null;
			if(playerCreated!=null){
				try {
					created = sdf.parse(playerCreated);
				} catch (ParseException e) {
					Log.w(TAG, "Error parsing date from DB : ", e);
				}
			}
			//create a Player-object that has all the properties fetched from DB
			retVal = new Player(playerDBName, playerDBDescription);
			retVal.setId(playerDBId);
			retVal.setCurrentElo(playerDBElo);
			retVal.setNrOfMatches(matchesPlayed);
			retVal.setCreated(created);
			Log.d(TAG, "Objectified player : " + retVal.getId());
		}
		close();
		if(cursor!=null)
			cursor.close();
		return retVal;
	}

	public void updatePlayer(Player p) {
		open();
		ContentValues values = createPlayerContentValues(p);
		db.update(PlayerTable.DB_TABLE_PLAYER, values, PlayerTable.KEY_ID + "=?",
				new String[] { p.getId() });
		Log.i(TAG, "Updated player id " + p.getId());
		close();
	}

	public List<String> getPlayerNames() {
		open();
		Cursor cursor = db.query(PlayerTable.DB_TABLE_PLAYER, new String[] { PlayerTable.KEY_NAME }, null,
				null, null, null, null);
		List<String> players = new ArrayList<String>();
		Log.i(TAG, cursor.getCount() + " rows in cursor");
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				players.add(cursor.getString(cursor.getColumnIndex(PlayerTable.KEY_NAME)));
			}
		} else {
//			players.add(new String("No players"));
		}
		if(cursor!=null)
			cursor.close();
		return players;
	}

	public List<String> getPlayerIds() {
		open();
		Cursor cursor = db.query(PlayerTable.DB_TABLE_PLAYER, new String[] {PlayerTable.KEY_ID }, null, null,
				null, null, null);
		
		List<String> playerIds = new ArrayList<String>();
		Log.i(TAG, cursor.getCount() + " rows in cursor");
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				playerIds.add(cursor.getString(cursor.getColumnIndex(PlayerTable.KEY_ID)));
			}
		} else {
			playerIds = null;
		}
		if(cursor!=null)
			cursor.close();
		close();
		return playerIds;
	}

	
	public void updateTournament(Tournament t) {
		open();
		ContentValues values = createTournamentContentValues(t);
		db.update(TournamentTable.DB_TABLE_TOURNAMENT, values, TournamentTable.KEY_ID + "=?",
				new String[] { t.getId() });
		Log.i(TAG, "Updated tournament id " + t.getId());
		close();
	}

	public void addTournament(Tournament t) {
		open();
		ContentValues values = createTournamentContentValues(t);
		db.insert(TournamentTable.DB_TABLE_TOURNAMENT, null, values);
		Log.i(TAG, "Inserted new tournament id " + t.getId());
		close();
	}
	

	public Tournament getTournamentFromId(String id) {
		Log.i(TAG, "Searching for tournament with id " + id);
		Tournament t = null;
		open();
		Cursor cursor_tournament = db.query(TournamentTable.DB_TABLE_TOURNAMENT, null, TournamentTable.KEY_ID + " = ?", 
				new String[] {id}, null, null, null);	
		cursor_tournament.moveToFirst();
		String description = cursor_tournament.getString(cursor_tournament.getColumnIndex(TournamentTable.KEY_DESCRIPTION));
		Date playedDate;
		try {
			playedDate = sdf.parse(cursor_tournament.getString(cursor_tournament.getColumnIndex(TournamentTable.KEY_PLAYEDDATE)));
		} catch (ParseException e) {
			Log.w(TAG,"Failed to set playeddate on tournament id "+id+" setting to null");
			e.printStackTrace();
			playedDate = null;
		}
		boolean doubleRoundRobin = cursor_tournament.getInt(cursor_tournament.getColumnIndex(TournamentTable.KEY_DOUBLEROUNDROBIN))>0;
		if(cursor_tournament!=null)
			cursor_tournament.close();
		Log.d(TAG, "Found tournament variables " + id + "," + description + "," + playedDate + "," + doubleRoundRobin);
		Cursor cursor_match = db.query(MatchTable.DB_TABLE_MATCH,new String[]{MatchTable.KEY_ID}, MatchTable.KEY_TOURNAMENT + " = ?", 
			new String[] {id}, null, null, null);
		ArrayList<Match> matches = new ArrayList<Match>();
		while(cursor_match.moveToNext()){
			matches.add(getMatchFromId(cursor_match.getString(cursor_match.getColumnIndex(MatchTable.KEY_ID))));
		}
		cursor_match.close();
		open();
		Log.d(TAG, "Found matches to put in tournament : " + matches.size());
		ArrayList<Player> players = new ArrayList<Player>();
		Log.d(TAG, "Have db object : " + db);
		
		Cursor cursor_player = db.rawQuery("SELECT p.id from player as p, match as m "
				+"WHERE (p.id = m.whiteplayer OR p.id = m.blackplayer) "
				+" AND m.tournament = ? ;", new String[]{id});
		while(cursor_player.moveToNext()){
			players.add(getPlayer(cursor_player.getString(cursor_player.getColumnIndex(PlayerTable.KEY_ID))));
		}
		if(cursor_player!=null)
			cursor_player.close();
		open();
		Log.d(TAG, "Found players to put in tournament (and matches) : " + matches.size());
		t = new Tournament(id, description, playedDate, players, matches, doubleRoundRobin);
		Log.d(TAG, "Tournament build-up complete : " + t);
		return t;	
	}

	public List<String> getAllTournamentIds() {
		open();
		ArrayList<String> tournamentIds = new ArrayList<String>();
		Cursor cursor = db.query(TournamentTable.DB_TABLE_TOURNAMENT, new String[] {TournamentTable.KEY_ID} , null, null, null, null, null);
		while(cursor.moveToNext()){
			tournamentIds.add(cursor.getString(cursor.getColumnIndex(TournamentTable.KEY_ID)));
		}
		if(cursor!=null)
			cursor.close();
		close();
		return tournamentIds;
	}
	
	public List<String> getAllTournamentNames() {
		open();
		ArrayList<String> tournamentDescs = new ArrayList<String>();
		Cursor cursor = db.query(TournamentTable.DB_TABLE_TOURNAMENT, new String[] {TournamentTable.KEY_DESCRIPTION} , null, null, null, null, null);
		while(cursor.moveToNext()){
			tournamentDescs.add(cursor.getString(cursor.getColumnIndex(TournamentTable.KEY_DESCRIPTION)));
		}
		if(cursor!=null)
			cursor.close();
		close();
		return tournamentDescs;
	}
	
	public void updateMatch(Match m, String tournamentId) {
		open();
		ContentValues values = createMatchContentValues(m, tournamentId);
		db.update(MatchTable.DB_TABLE_MATCH, values, MatchTable.KEY_ID + "=?",
				new String[] { m.getId() });
		Log.i(TAG, "Updated match id " + m.getId());
		close();
	}

	public void addMatch(Match m, String tournamentId) {
		open();
		ContentValues values = createMatchContentValues(m, tournamentId);
		db.insert(MatchTable.DB_TABLE_MATCH, null, values);
		Log.i(TAG, "Inserted new tournament id " +m.getId());
		close();
	}
	
	public Match getMatchFromId(String id){
		open();
		Cursor cursor = db.query(
				MatchTable.DB_TABLE_MATCH,
				null, 
				MatchTable.KEY_ID + " = '" + id +"';", null, null, null, null);
		if(cursor!=null && cursor.getCount()>0)
			cursor.moveToFirst();
		Player white = getPlayer(cursor.getString(cursor.getColumnIndex(MatchTable.KEY_WHITEPLAYER)));
		Player black = getPlayer(cursor.getString(cursor.getColumnIndex(MatchTable.KEY_BLACKPLAYER)));
//		Tournament tournament = getTournamentFromId(cursor.getString(cursor.getColumnIndex(MatchTable.KEY_TOURNAMENT)));
		Date playedDate;
		try {
			playedDate = sdf.parse(cursor.getString(cursor.getColumnIndex(MatchTable.KEY_PLAYEDDATE)));
		} catch (ParseException e) {
			Log.w(TAG,"Failed to set date on match i "+id+" . Making null");
			playedDate = null;
			e.printStackTrace();
		}
		float whiteEloWhenPlayed = cursor.getFloat(cursor.getColumnIndex(MatchTable.KEY_WHITEELOWHENPLAYED));
		float blackEloWhenPlayed = cursor.getFloat(cursor.getColumnIndex(MatchTable.KEY_BLACKELOWHENPLAYED));
		int result = cursor.getInt(cursor.getColumnIndex(MatchTable.KEY_RESULT));
		//cant have reference to tournament, creates loop
		Match m = new Match(id, white, black, playedDate, whiteEloWhenPlayed, blackEloWhenPlayed, null, result);
		if(cursor!=null)
			cursor.close();
		close();
		return m;
	}
	
	public ContentValues createMatchContentValues(Match m, String tournamentId) {
		ContentValues values = new ContentValues();
		values.put(MatchTable.KEY_ID, m.getId());
		values.put(MatchTable.KEY_WHITEPLAYER, m.getWhite().getId());
		values.put(MatchTable.KEY_BLACKPLAYER, m.getBlack().getId());
		values.put(MatchTable.KEY_PLAYEDDATE, sdf.format(m.getPlayed()));
		values.put(MatchTable.KEY_WHITEELOWHENPLAYED, m.getWhiteEloWhenPlayed());
		values.put(MatchTable.KEY_BLACKELOWHENPLAYED, m.getBlackEloWhenPlayed());
		values.put(MatchTable.KEY_TOURNAMENT, tournamentId);
		values.put(MatchTable.KEY_RESULT, m.getResult());
		return values;
	}
	
	public ContentValues createPlayerContentValues(Player p) {
		ContentValues values = new ContentValues();
		values.put(PlayerTable.KEY_ID, p.getId());
		values.put(PlayerTable.KEY_NAME, p.getName());
		values.put(PlayerTable.KEY_DESCRIPTION, p.getDescription());
		values.put(PlayerTable.KEY_ELO, p.getCurrentElo());
		values.put(PlayerTable.KEY_NROFMATCHES, p.getNrOfMatches());
		values.put(PlayerTable.KEY_CREATED, sdf.format(p.getCreated()));
		return values;
	}
	
	public ContentValues createTournamentContentValues(Tournament t) {
		ContentValues values = new ContentValues();
		values.put(TournamentTable.KEY_ID, t.getId());
		values.put(TournamentTable.KEY_DESCRIPTION, t.getDescription());
		values.put(TournamentTable.KEY_PLAYEDDATE, sdf.format(t.getPlayedDate()));
		values.put(TournamentTable.KEY_DOUBLEROUNDROBIN, t.isDoubleRoundRobin()?1:0);
		return values;
	}
	
	public String dumpDatabases(){
		StringBuilder sb = new StringBuilder();
		open();
		Cursor players = db.rawQuery("SELECT * FROM " + PlayerTable.DB_TABLE_PLAYER + ";", null);
		if(players.getCount()>0){
			while(players.moveToNext()){
				sb.append("INSERT INTO " + PlayerTable.DB_TABLE_PLAYER + " values ('");
				sb.append(players.getString(0));
				sb.append("','");
				sb.append(players.getString(1));
				sb.append("','");
				sb.append(players.getString(2));
				sb.append("','");
				sb.append(players.getString(3));
				sb.append("',");
				sb.append(players.getString(4));
				sb.append(",'");
				sb.append(players.getString(5));
				sb.append("',");
				sb.append(players.getString(6));
				sb.append(");");
				sb.append("\n");
			}
		}
		
		Cursor matches = db.rawQuery("SELECT * FROM " + MatchTable.DB_TABLE_MATCH + ";", null);
		if(matches.getCount()>0){
			while(matches.moveToNext()){
				sb.append("INSERT INTO " + MatchTable.DB_TABLE_MATCH + " values ('");
				sb.append(matches.getString(0));
				sb.append("','");
				sb.append(matches.getString(1));
				sb.append("','");
				sb.append(matches.getString(2));
				sb.append("','");
				sb.append(matches.getString(3));
				sb.append("',");
				sb.append(matches.getString(4));
				sb.append(",");
				sb.append(matches.getString(5));
				sb.append(",'");
				sb.append(matches.getString(6));
				sb.append("',");
				sb.append(matches.getString(7));
				sb.append(");");
				sb.append("\n");
			}
		}
		
		Cursor tournaments = db.rawQuery("SELECT * FROM " + TournamentTable.DB_TABLE_TOURNAMENT + ";", null);
		if(tournaments.getCount()>0){
			while(tournaments.moveToNext()){
				sb.append("INSERT INTO " + TournamentTable.DB_TABLE_TOURNAMENT + " + values ('");
				sb.append(tournaments.getString(0));
				sb.append("','");
				sb.append(tournaments.getString(1));
				sb.append("','");
				sb.append(tournaments.getString(2));
				sb.append("',");
				sb.append(tournaments.getString(3));
				sb.append(");");
				sb.append("\n");
			}
		}
		close();
		return sb.toString();
		
	}
	
//	public List<String> getTournamentNames() {
//		open();
//		ArrayList<String> tournamentnames = new ArrayList<String>();
//		Cursor cursor = db.query(TournamentTable.DB_TABLE_TOURNAMENT, new String[] {TournamentTable.KEY_DESCRIPTION}, null, null, null, null, null);
//		if(cursor.getCount() > 0){
//			while(cursor.moveToNext()){
//			tournamentnames.add(cursor.getString(cursor.getColumnIndex(TournamentTable.KEY_DESCRIPTION)));
//		}
//		}else{
////			tournamentnames.add(new String("No tournaments"));
//		}
//		if(cursor!=null)
//			cursor.close();
//		close();
//		return tournamentnames;
//	}

}
