package net.groovygrevling.model;

public class ELOCalculation {

	public static final int UNPLAYED = -1;
	public static final int REMIS = 0;
	public static final int WHITE_WIN = 1;
	public static final int BLACK_WIN = 2;

	private static Player whitePlayer;
	private static Player blackPlayer;
	
	// Returns an array of two doubles: [0] is the new rating for player A, [1]
	// for player B
	//
	// Parameters:
	// status: 0 = draw, 1 = player A won, 2 = player B won
	// oldRatingWhite, oldRatingBlack: the players points before the game
	public static double[] calcElo(int gameResult, Player white, Player black) {
		whitePlayer = white;
		blackPlayer = black;
		
		double qa = Math.pow(10, white.getCurrentElo() / 400);
		double qb = Math.pow(10, black.getCurrentElo() / 400);

		double ea = qa / (qa + qb);
		double eb = qb / (qa + qb);

		double sa = 0.5; // draw
		double sb = 0.5; // draw
		if (gameResult == WHITE_WIN) {
			sa = 1;
			sb = 0;
		} else if (gameResult == BLACK_WIN) {
			sa = 0;
			sb = 1;
		}

		double[] newElos = new double[2];
		newElos[0] = white.getCurrentElo() + (getWhiteK() * (sa - ea));
		newElos[1] = black.getCurrentElo() + (getBlackK() * (sb - eb));
		return newElos;
	}

	/** 
	 * FIDE rules:
	 * K = 30 (was 25) for a player new to the rating list until s/he has completed events with a total of at least 30 games.[15]
	 * K = 15 as long as a player's rating remains under 2400.
	 * K = 10 once a player's published rating has reached 2400, and s/he has also completed events with a total of at least 30 games. Thereafter it remains permanently at 10.
	 * @return
	 */
	public static double getWhiteK(){
		double retVal = 30;
		if(whitePlayer.getNrOfMatches()<30){
			retVal = 30;
		} else {
			if(whitePlayer.getCurrentElo()<2400){
				retVal = 15;
			} else {
				retVal = 10;
			}
		}
		return retVal;
	}
	
	/** 
	 * FIDE rules:
	 * K = 30 (was 25) for a player new to the rating list until s/he has completed events with a total of at least 30 games.[15]
	 * K = 15 as long as a player's rating remains under 2400.
	 * K = 10 once a player's published rating has reached 2400, and s/he has also completed events with a total of at least 30 games. Thereafter it remains permanently at 10.
	 * @return
	 */
	public static double getBlackK(){
		double retVal = 30;
		if(blackPlayer.getNrOfMatches()<30){
			retVal = 30;
		} else {
			if(blackPlayer.getCurrentElo()<2400){
				retVal = 15;
			} else {
				retVal = 10;
			}
		}
		return retVal;
	}

}
