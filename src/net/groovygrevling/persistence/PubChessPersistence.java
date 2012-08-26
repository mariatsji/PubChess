package net.groovygrevling.persistence;

import java.util.List;

import net.groovygrevling.model.Match;
import net.groovygrevling.model.Player;
import net.groovygrevling.model.Tournament;

/**
 * The interface all persistence classes must implement
 * @author sjur
 *
 */
public interface PubChessPersistence {

	/**
	 * Method to persist a player object into storage
	 * @param p
	 */
	public void persist(Player p);
	
	/**
	 * Method to persist a tournament object into storage
	 * @param t
	 */
	public void persist(Tournament t);
	
	/**
	 * Method to persist a match
	 * @param m
	 */
	public void persist(Match m, String tournamentId);
	
	/**
	 * Method to retreive all players
	 * @return
	 */
	public List<Player> getAllPlayers();
	
	/**
	 * Method to retreive all tournaments
	 * @return
	 */
	public List<Tournament> getAllTournaments();
	
	/**
	 * Method to get all matches from a given tournamentcd 
	 * @param t
	 * @return
	 */
	public List<Match> getAllMatchesInTournament(Tournament t);
	
	/**
	 * Method to get all matches for a given player (from every tournament where player participated)
	 * @param p
	 * @return
	 */
	public List<Match> getAllMatchesForPlayer(Player p);
	
	/**
	 * Get a String[] representation of all player names
	 * @param players
	 * @return
	 */
	public List<String> getPlayerNames();
	
	/**
	 * Get a String[] representation of all player descriptions
	 */
	public List<String> getPlayerDescription();
	
	/**
	 * Get a String[] representation of all player ids
	 */
	public List<String> getPlayerIds();
	
	/**
	 * Get a String[] representation of all tournament ids
	 */
	
	public List<String> getAllTournamentIds();
	
	/**
	 * Get a Tournament object from a tournament String id
	 */
	
	public Tournament getTournamentFromId(String id);
	
	/**
	 * Get a Match object from a match String id
	 */
	
	public Match getMatchFromId(String id);
	
}