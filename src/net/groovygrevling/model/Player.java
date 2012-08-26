package net.groovygrevling.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Player {

	private String id = null;
	private String name = null;
	private String description = "Good on opening theory after 13 beers!";
	private double currentElo = 1000;
	private int nrOfMatches = 0;
	private Date created = null;
	private SimpleDateFormat sdf = null;
	//tournamentPoints is transient - meaning it should never be stored, and skipped
	//in any (future) serialization
	private transient double tournamentPoints = 0;
	
	public Player(String name, String description){
		sdf = new SimpleDateFormat("yyyyMMddHHmm");
		this.id = name.trim().replaceAll("\\s", "").toLowerCase() +
				sdf.format(Calendar.getInstance().getTime());
		this.name = name;
		this.description = description;
		init();
	}
	
	public Player(String name){
		sdf = new SimpleDateFormat("yyyyMMddHHmm");
		this.id = name.trim().replaceAll("\\s", "").toLowerCase() +
				sdf.format(Calendar.getInstance().getTime());
		this.name = name;
		init();
	}
	
	public void init(){
		this.created = Calendar.getInstance().getTime();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getCurrentElo() {
		return currentElo;
	}

	public void setCurrentElo(double currentElo) {
		this.currentElo = currentElo;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public double getTournamentPoints() {
		return tournamentPoints;
	}
	
	public void increasePointsFromWin() {
		tournamentPoints += 1;
	}
	
	public void incrasePointsFromRemis() {
		tournamentPoints += 0.5;
	}
	
	@Override
	public String toString() {
		return "Player [id=" + id + ", name=" + name + ", description="
				+ description + ", currentElo=" + currentElo + "]";
	}

	public int getNrOfMatches() {
		return nrOfMatches;
	}

	public void setNrOfMatches(int nrOfMatches) {
		this.nrOfMatches = nrOfMatches;
	}

}
