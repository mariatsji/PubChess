package net.groovygrevling.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import android.util.Log;

import net.groovygrevling.PubChessActivity;

public class Tournament {

	public static final String TAG = Tournament.class.getCanonicalName();
	private String id;
	private String description;
	private Date playedDate;
	private ArrayList<Player> players;
	private ArrayList<Match> matches;
	private boolean doubleRoundRobin;
	
	public Tournament(String description, boolean doubleRoundRobin) {
		super();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		this.id = description.trim().replaceAll("\\s", "").toLowerCase() +
				sdf.format(Calendar.getInstance().getTime());
		this.description = description;
		this.players = new ArrayList<Player>();
		this.matches = new ArrayList<Match>();
		this.playedDate = Calendar.getInstance().getTime();
		this.setDoubleRoundRobin(doubleRoundRobin);
	}
	
	/**
	 * Constructor for dbimport
	 * @param id
	 * @param description
	 * @param playedDate
	 * @param players
	 * @param matches
	 * @param standing
	 * @param doubleRoundRobin
	 */
	public Tournament(String id, String description, Date playedDate,
			ArrayList<Player> players, ArrayList<Match> matches, boolean doubleRoundRobin) {
		this.id = id;
		this.description = description;
		this.playedDate = playedDate;
		this.players = players;
		this.matches = matches;
		this.doubleRoundRobin = doubleRoundRobin;
	}
	
	public void addPlayer(Player p){
		
		players.add(p);
		Log.d(PubChessActivity.TAG, "Adding player " + p.getId() + " to tournament. list size is now : " + players.size());
	}
	
	public Player getPlayerFromId(String id){
		id = id.trim();
		Player retVal = null;
		for(Player p : players){
			if(p.getId().equals(id))
				retVal = p;
		}
		return retVal;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getPlayedDate() {
		return playedDate;
	}

	public void setPlayedDate(Date playedDate) {
		this.playedDate = playedDate;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public ArrayList<Match> getMatches() {
		return matches;
	}

	public void setMatches(ArrayList<Match> matches) {
		this.matches = matches;
	}
	
	private ArrayList<Match> randomize(ArrayList<Match> matches){
		Collections.shuffle(matches);
		return matches;
	}
	
	public void drawSingleRoundRobin() {
		ArrayList<Match> sortedMatch = new ArrayList<Match>();
		Collections.shuffle(players);
		int maxSpacing = (int)Math.floor((double)players.size() / 2);
		for(int x = 1 ; x <= maxSpacing; x ++){
			//if x and players.size() are relatively prime
			//and x > Math.floor(players.size()/2 
			if(gcd(x,players.size())!=1 && x>=maxSpacing){
				for(int i = 0 ; i < maxSpacing; i ++){
					Log.d(TAG, "x and i players.size() are NOT relatively prime : " + x + "," + players.size());
					//reverse every other loop
					Player first = null;
					Player second = null;
					if(x%2==0){
						first = players.get(i % players.size());
						second = players.get((i + x) % players.size());	
					} else {
						second = players.get(i % players.size());
						first = players.get((i + x) % players.size());
					}
					String matchId = first.getId() + second.getId() + getId();
					Match match = new Match(matchId, first, second, Calendar.getInstance().getTime());
					sortedMatch.add(match);
				}
			} else {
				for(int i = 0 ; i < players.size(); i ++){
					Log.d(TAG, "x and i players.size() are relatively prime : " + x + "," + players.size());
					//reverse every other loop
					Player first = null;
					Player second = null;
					if(x%2==0){
						first = players.get(i % players.size());
						second = players.get((i + x) % players.size());	
					} else {
						second = players.get(i % players.size());
						first = players.get((i + x) % players.size());
					}
					String matchId = first.getId() + second.getId() + getId();
					Match match = new Match(matchId, first, second, Calendar.getInstance().getTime());
					sortedMatch.add(match);
				}
			}
		}
		setMatches(sortedMatch);
		
	}
	
	public void drawDoubleRoundRobin(){
		ArrayList<Match> sortedMatch = new ArrayList<Match>();
		//create sorted double round robin list
		for(Player p : players){
			Log.d(PubChessActivity.TAG, "Player in tournament : " + p.getId());
		}
		for(int i = 0 ; i < players.size() ; i ++){
			Player first = players.get(i);
			for(int j = 0 ; j < players.size() ; j++){
				Player second = players.get(j);
				if(!first.getId().equals(second.getId())){
					Log.d(PubChessActivity.TAG, "Creating match between " + first.getId() + " and " + second.getId());
					String matchId = first.getId() + second.getId() + getId();
					sortedMatch.add(new Match(matchId, first, second, Calendar.getInstance().getTime()));
				}
			}
		}
		Log.d(PubChessActivity.TAG, "Sorted match size " + sortedMatch.size());
		setMatches(randomize(sortedMatch));
	}

	public boolean isDoubleRoundRobin() {
		return doubleRoundRobin;
	}

	public void setDoubleRoundRobin(boolean doubleRoundRobin) {
		this.doubleRoundRobin = doubleRoundRobin;
	}

	@Override
	public String toString() {
		return "Tournament [id=" + id + ", description=" + description
				+ ", playedDate=" + playedDate + ", players=" + players.size()
				+ ", matches=" + matches.size() + ", doubleRoundRobin=" + doubleRoundRobin + "]";
	}
	
	private int gcd(int a, int b){
		int small = a<b?a:b;
		int big   = b>a?b:a;
		int retVal = 1;
		if(small>0){
			double ratio = (double) big / small;
			if(Math.rint(ratio)==ratio){
				retVal = small;
			} else {
				retVal = gcd(small - 1, big);
			}
		}
		return retVal;
	}
	
}