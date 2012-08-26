package net.groovygrevling.model;

public enum Result {

	WHITE_WIN(1),
	BLACK_WIN(2),
	REMIS(0),
	UNPLAYED(-1);
	
	private int value;
	
	Result(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}
