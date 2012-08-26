package net.groovygrevling.model;

import java.util.Date;

import android.util.Log;

public class Match {

	public static final String TAG = Match.class.getName();
	
	private String id;
	private Player white;
	private Player black;
	private Date playedDate;
	private float whiteEloWhenPlayed;
	private float blackEloWhenPlayed;
	private int result;
	private transient double randomWeight = 0;

	public Match(String id, Player white, Player black, Date played) {
		super();
		this.id = id;
		this.white = white;
		this.black = black;
		this.playedDate = played;
		this.result = ELOCalculation.UNPLAYED;
		this.randomWeight = Math.random();
	}
	
	
	
	public Match(String id, Player white, Player black, Date playedDate,
			float whiteEloWhenPlayed, float blackEloWhenPlayed,
			Tournament tournament, int result) {
		super();
		this.id = id;
		this.white = white;
		this.black = black;
		this.playedDate = playedDate;
		this.whiteEloWhenPlayed = whiteEloWhenPlayed;
		this.blackEloWhenPlayed = blackEloWhenPlayed;
		this.result = result;
	}



	public double getRandomWeight(){
		return randomWeight;
	}
	
	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id = id;
	}
	
	public Player getWhite() {
		return white;
	}

	public void setWhite(Player white) {
		this.white = white;
	}

	public Player getBlack() {
		return black;
	}

	public void setBlack(Player black) {
		this.black = black;
	}

	public Date getPlayed() {
		return playedDate;
	}

	public void setPlayed(Date played) {
		this.playedDate = played;
	}

	public float getWhiteEloWhenPlayed() {
		return whiteEloWhenPlayed;
	}

	public void setWhiteEloWhenPlayed(float whiteEloWhenPlayed) {
		this.whiteEloWhenPlayed = whiteEloWhenPlayed;
	}

	public float getBlackEloWhenPlayed() {
		return blackEloWhenPlayed;
	}

	public void setBlackEloWhenPlayed(float blackEloWhenPlayed) {
		this.blackEloWhenPlayed = blackEloWhenPlayed;
	}

	public int getResult() {
		return result;
	}

	public void adjustElo(int result) {
		// set new elo based on result
		double[] elos = new double[2];
		//get white nr of matches
		
		elos = ELOCalculation.calcElo(result, getWhite(), getBlack());
		Log.d(TAG, "Setting new ELO for player " + getWhite() + " : " + elos[0] + " and for " + getBlack() + " : " + elos[1]);
		getWhite().setCurrentElo(elos[0]);
		getBlack().setCurrentElo(elos[1]);

	}

	public void setResult(int result) {
		this.result = result;
		adjustElo(result);
	}

	@Override
	public String toString() {
		return "Match [white=" + white + ", black=" + black + ", playedDate="
				+ playedDate + ", whiteEloWhenPlayed=" + whiteEloWhenPlayed
				+ ", blackEloWhenPlayed=" + blackEloWhenPlayed
				+ ", result=" + result + "]";
	}

}
